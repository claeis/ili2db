package ch.ehi.ili2fgdb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ch.ehi.fgdb4j.jni.ByteArray;
import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.FieldDefs;
import ch.ehi.fgdb4j.jni.FieldInfo;
import ch.ehi.fgdb4j.jni.FieldType;
import ch.ehi.fgdb4j.jni.MultiPartShapeBuffer;
import ch.ehi.fgdb4j.jni.Row;
import ch.ehi.fgdb4j.jni.ShapeBuffer;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.ce_time;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValue;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueField;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueNull;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueString;
import ch.ehi.ili2fgdb.jdbc.sql.SqlQname;

public class FgdbResultSet extends AbstractResultSet implements ResultSet {

	public static final int MAGIC_HOUR_DATEONLY = 0;
	public static final int MAGIC_YEAR_TIMEONLY = -1;
	public static final int MAGIC_MON_TIMEONLY=11;
	public static final int MAGIC_MDAY_TIMEONLY=30;
	public static final int MAGIC_ISDST_TIMEONLY=-1;
	public static final int MAGIC_WDAY_TIMEONLY=6;
	public static final int MAGIC_YDAY_TIMEONLY=363;
	
	private EnumRows rowIterator=null;
	private Row fgdbCurrentRow=null;
	FieldInfo fgdbFieldInfo=null;
	private int fgdbFieldCount=0;
	private boolean lastGetWasNull=false;
	private List<SelectValue> selectValues=null;
	private Table fgdbTable=null;
	private FgdbConnection conn=null;
	
	public FgdbResultSet(FgdbConnection conn,Table table,EnumRows rows, List<SelectValue> selectvalues) throws SQLException {
		this.conn=conn;
		this.rowIterator=rows;
		this.fgdbTable=table;
		fgdbFieldInfo=new FieldInfo();
		int err=rowIterator.GetFieldInformation(fgdbFieldInfo);
		if(err!=0){
			StringBuffer errDesc=new StringBuffer();
			fgbd4j.GetErrorDescription(err, errDesc);
			throw new SQLException(errDesc.toString());
		}
		int[] fieldCounto=new int[1];
		err=fgdbFieldInfo.GetFieldCount(fieldCounto);
		if(err!=0){
			StringBuffer errDesc=new StringBuffer();
			fgbd4j.GetErrorDescription(err, errDesc);
			throw new SQLException(errDesc.toString());
		}
		fgdbFieldCount=fieldCounto[0];
		selectValues=selectvalues;
	}	

	@Override
	public void close() throws SQLException {
		if(fgdbCurrentRow!=null){
			fgdbCurrentRow.delete();
			fgdbCurrentRow=null;
		}
		if(rowIterator!=null){
			rowIterator.Close();
			rowIterator.delete();
			rowIterator=null;
		}
		if(fgdbFieldInfo!=null){
			fgdbFieldInfo.delete();
			fgdbFieldInfo=null;
		}
		if(fgdbTable!=null){
			conn.getGeodatabase().CloseTable(fgdbTable);
			fgdbTable.delete();
			fgdbTable=null;
		}
	}

	@Override
	public int findColumn(String colName) throws SQLException {
		if(selectValues!=null){
			int colIdx=0;
			for(SelectValue sv:selectValues){
				String fieldName=null;
				fieldName=sv.getColumnName();
				if(fieldName.equals(colName)){
					return colIdx+1;
				}
				colIdx++;
			}
			
		}else{
			return findFgdbColumn(colName)+1;
		}
		throw new SQLException("unknown column "+colName);
	}
	private int findFgdbColumn(String colName) throws SQLException {
		for(int colIdx=0;colIdx<fgdbFieldCount;colIdx++){
			StringBuffer fieldName=new StringBuffer();
			fgdbFieldInfo.GetFieldName(colIdx, fieldName);
			if(fieldName.toString().equals(colName)){
				return colIdx;
			}
		}
		throw new SQLException("unknown column "+colName);
	}

	@Override
	public Object getObject(int colIdx) throws SQLException {
		colIdx--;
		if(selectValues!=null){
			SelectValue sv=selectValues.get(colIdx);
			if(sv instanceof SelectValueString){
				lastGetWasNull=false;
				return ((SelectValueString) sv).getLiteralValue();
			}else if(sv instanceof SelectValueNull){
				lastGetWasNull=true;
				return null;
			}
			colIdx=findFgdbColumn(sv.getColumnName());
		}
		boolean isNull[]=new boolean[1];
		int err=fgdbCurrentRow.IsNull(colIdx, isNull);
	    if(err!=0){
			throw new SQLException(getErrorDescription(err));
	    }
		if(isNull[0]){
			lastGetWasNull=true;
			return null;
		}
		lastGetWasNull=false;
		Object valueo=null;
		int[] fieldType=new int[1];
		err=fgdbFieldInfo.GetFieldType(colIdx, fieldType);
	    if(err!=0){
			throw new SQLException(getErrorDescription(err));
	    }
		if(fieldType[0]==FieldType.fieldTypeOID.swigValue()){
			int value[]=new int[1];
		    err=fgdbCurrentRow.GetOID(value);
		    if(err==0){
		    	valueo=value[0];
		    }
		}else if(fieldType[0]==FieldType.fieldTypeInteger.swigValue()){
			int value[]=new int[1];
		    err=fgdbCurrentRow.GetInteger(colIdx, value);
		    if(err==0){
		    	valueo=value[0];
		    }
		}else if(fieldType[0]==FieldType.fieldTypeSmallInteger.swigValue()){
			short[] value=new short[1];
		    err=fgdbCurrentRow.GetShort(colIdx, value);
		    if(err==0){
		    	valueo=value[0];
		    }
		}else if(fieldType[0]==FieldType.fieldTypeDouble.swigValue()){
			double value[]=new double[1];
		    err=fgdbCurrentRow.GetDouble(colIdx, value);
		    if(err==0){
		    	valueo=value[0];
		    }
		}else if(fieldType[0]==FieldType.fieldTypeString.swigValue()){
			StringBuffer value=new StringBuffer();;
		    err=fgdbCurrentRow.GetString(colIdx, value);
		    if(err==0){
		    	valueo=value.toString();
		    }
		}else if(fieldType[0]==FieldType.fieldTypeGeometry.swigValue()){
			//MultiPartShapeBuffer value=new MultiPartShapeBuffer();
			ShapeBuffer value=null;
			try {
				value=new ShapeBuffer();
				err=fgdbCurrentRow.GetGeometry(value);
				if(err==0){
				    byte[] bytebuf=value.getBuffer();
				    valueo=bytebuf;
				    // NullShape?
				    if(bytebuf!=null && bytebuf.length>=4){
				    	if(bytebuf[0]==0 && bytebuf[1]==0 && bytebuf[2]==0 && bytebuf[3]==0){
							lastGetWasNull=true;
				    		valueo=null;
				    	}
				    }
				//}else if(err==-2147467259){ // Row::GetGeometry() will fail with -2147467259 for NULL geometries
				//	lastGetWasNull=true;
		    	//	valueo=null;
		    	//	err=0; // ignore error
				}
			}finally {
				if(value!=null){
					value.delete();
					value=null;
				}
			}
		}else if(fieldType[0]==FieldType.fieldTypeBlob.swigValue()){
			ByteArray value=null;
			try{
				value=new ByteArray();
			    err=fgdbCurrentRow.GetBinary(colIdx, value);
			    if(err==0){
			    	valueo=value.getBuffer();
			    }
				
			}finally{
				if(value!=null){
			    	value.delete();
			    	value=null;
				}
			}
		}else if(fieldType[0]==FieldType.fieldTypeDate.swigValue()){
			ce_time value=null;
			try{
				value=new ce_time();
			    err=fgdbCurrentRow.getDateTime(colIdx, value);
			    if(err==0){
					GregorianCalendar time=new GregorianCalendar();
					int year=value.getTm_year();
					time.set(GregorianCalendar.YEAR,year+1900);
					int month = value.getTm_mon();
					time.set(GregorianCalendar.MONTH, month);
					int day = value.getTm_mday();
					time.set(GregorianCalendar.DAY_OF_MONTH,day);
					int hour = value.getTm_hour();
					time.set(GregorianCalendar.HOUR_OF_DAY,hour);
					int min = value.getTm_min();
					time.set(GregorianCalendar.MINUTE,min);
					int sec = value.getTm_sec();
					time.set(GregorianCalendar.SECOND,sec);
					time.set(GregorianCalendar.MILLISECOND, 0);
					int isdst=value.getTm_isdst();
					int wday=value.getTm_wday();
					int yday=value.getTm_yday();
					if(year==MAGIC_YEAR_TIMEONLY || year==1){
				    	valueo=new java.sql.Time(time.getTimeInMillis());
					}else{
						if(hour==MAGIC_HOUR_DATEONLY){
					    	valueo=new java.sql.Date(time.getTimeInMillis());
						}else{
					    	valueo=new java.sql.Timestamp(time.getTimeInMillis());
						}
					}
			    }
			}finally{
				if(value!=null){
					value.delete();
					value=null;
				}
			}
		}else{
			throw new SQLException("unexpected field type "+fieldType[0]);
		}
	    if(err!=0){
			throw new SQLException(getErrorDescription(err));
	    }
		return valueo;
	}

	private String getErrorDescription(int err) {
		StringBuffer errDesc=new StringBuffer();
		fgbd4j.GetErrorDescription(err, errDesc);
		return errDesc.toString();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return rowIterator==null;
	}

	@Override
	public boolean next() throws SQLException {
		if(fgdbCurrentRow!=null){
			fgdbCurrentRow.delete();
			fgdbCurrentRow=null;
		}
		fgdbCurrentRow=new Row();
		return rowIterator.Next(fgdbCurrentRow) == 0;
	}

	@Override
	public boolean wasNull() throws SQLException {
		return lastGetWasNull;
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		javax.sql.rowset.RowSetMetaDataImpl ret=new javax.sql.rowset.RowSetMetaDataImpl();
		if(selectValues!=null){
			ret.setColumnCount(selectValues.size());
		}else{
			ret.setColumnCount(fgdbFieldCount);
		}
		return ret;
	}

}
