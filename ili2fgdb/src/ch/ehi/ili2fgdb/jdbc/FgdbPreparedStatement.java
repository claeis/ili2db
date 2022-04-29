package ch.ehi.ili2fgdb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ch.ehi.fgdb4j.jni.ByteArray;
import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.FieldInfo;
import ch.ehi.fgdb4j.jni.FieldType;
import ch.ehi.fgdb4j.jni.MultiPartShapeBuffer;
import ch.ehi.fgdb4j.jni.Row;
import ch.ehi.fgdb4j.jni.ShapeBuffer;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.ce_time;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.sql.*;
import ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb;

public class FgdbPreparedStatement implements PreparedStatement {
	private SqlStmt stmt=null;
	private FgdbConnection conn=null;
	private String stmtStr=null;
	private java.util.ArrayList<Object> params=null;
	private int fieldCount=0;
	private String geometryColumn=null;
	private java.util.Map<String,Integer> fieldType=null;
	
	protected FgdbPreparedStatement(FgdbConnection conn,SqlStmt select, String stmtStr) {
		this.stmt = select;
		this.conn=conn;
		this.stmtStr=stmtStr;
	}


	@Override
	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearParameters() throws SQLException {
		params=null;
	}

	@Override
	public boolean execute() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		  ResultSet ret=executeSelectStmt((AbstractSelectStmt)stmt,null);
		  return ret;
	}

	private ResultSet executeSelectStmt(AbstractSelectStmt stmt,String keyCol) throws SQLException {
		  Table table=null;
          EnumRows rows=null;
		  java.util.List<SelectValue> selectCols=null;
		  int err=0;
		  ResultSet ret=null;
		  try {
		        if(stmt instanceof FgdbSelectStmt){
		            FgdbSelectStmt ustmt=(FgdbSelectStmt)stmt;
		            table=new Table();
		            err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
		            if(err!=0){
		                StringBuffer errDesc=new StringBuffer();
		                fgbd4j.GetErrorDescription(err, errDesc);
		                throw new SQLException(errDesc.toString());
		            }
		            setupFieldInfo(table);
		              StringBuffer where=new StringBuffer();
		              {
		                  String sep="";
		                  int paramIdx=0;
		                  for(java.util.Map.Entry<Value,Value> set:ustmt.getConditions()){
		                      ColRef colref=(ColRef)set.getKey();
		                      where.append(sep);sep=" AND ";
		                      where.append(colref.getName());
		                      Value rh=set.getValue();
		                        if(rh instanceof IntConst){
		                            where.append("=");
		                            where.append(Integer.toString(((IntConst)rh).getValue()));
		                        }else if(rh instanceof IsNull){
		                            where.append(" IS NULL");
		                        }else if(rh instanceof StringConst){
		                            where.append("='");
		                            where.append(((StringConst)rh).getValue());
		                            where.append("'");
		                        }else{
		                              Object param=params.get(paramIdx++);
		                              appendParam(where, paramIdx, param);
		                        }
		                  }
		              }
		              StringBuffer fields=new StringBuffer();
		              {
		                  String sep="";
		                  selectCols=ustmt.getFields();
		                  for(SelectValue colref:selectCols){
		                      if(colref instanceof SelectValueField){
		                          fields.append(sep);sep=",";
		                          String columnName = colref.getColumnName();
		                        fields.append(columnName);
		                        if(keyCol!=null && columnName.equals(keyCol)){
		                            keyCol=null;
		                        }
		                      }
		                  }
		                  if(keyCol!=null){
		                      fields.append(sep);sep=",";
		                      fields.append(keyCol);
		                      selectCols.add(new SelectValueField(new SqlQname(keyCol)));
		                  }
		              }
		              
		              rows=new EnumRows();
		              err= table.Search(fields.toString(), where.toString(), false, rows);
		                if(err!=0){
		                    StringBuffer errDesc=new StringBuffer();
		                    fgbd4j.GetErrorDescription(err, errDesc);
		                    throw new SQLException(errDesc.toString());
		                }
		                ret=new FgdbResultSet(conn,table,rows,selectCols);
		                rows=null;
		                table=null;
		        }else if(stmt instanceof JoinStmt){
		            JoinStmt jstmt=(JoinStmt)stmt;
		            ResultSet rsLeft=executeSelectStmt(jstmt.getLeftStmt(),jstmt.getLeftKeyCol());
		            ArrayList<ResultSet> rsRight=new ArrayList<ResultSet>();
		            int rightc=jstmt.getRightStmt().size();
		            for(int i=0;i<rightc;i++){
		                rsRight.add(executeSelectStmt(jstmt.getRightStmt().get(i),jstmt.getRightKeyCol().get(i)));  
		            }
		            ret=new JoinResultSet(rsLeft,rsRight,jstmt);
		        }else if(stmt instanceof ComplexSelectStmt){
		            ret=new MemResultSet(executeSelectStmt(((ComplexSelectStmt) stmt).getSubSelect(),null),stmt.getConditions(),params);
		        }else{
		            rows=new EnumRows();
		            err=conn.getGeodatabase().ExecuteSQL(stmtStr, true, rows);
		            if(err!=0){
		                StringBuffer errDesc=new StringBuffer();
		                fgbd4j.GetErrorDescription(err, errDesc);
		                throw new SQLException(errDesc.toString());
		            }
		            ret=new FgdbResultSet(conn,table,rows,selectCols);
		            rows=null;
		            table=null;
		        }
		  }finally {
		      if(rows!=null) {
		          rows.Close();
		          rows.delete();
		          rows=null;
		      }
		      if(table!=null) {
		          conn.getGeodatabase().CloseTable(table);
		          table.delete();
		          table=null;
		      }
		  }
	
	return ret;
	}


	@Override
	public int executeUpdate() throws SQLException {
		int err=0;
		if(stmt instanceof InsertStmt){
			Table table=null;
            Row row=null;
            ShapeBuffer shapeBuffer=null;
			try{
	            table=new Table();
	            err=conn.getGeodatabase().OpenTable(((InsertStmt) stmt).getTableName(), table);
	            if(err!=0){
	                StringBuffer errDesc=new StringBuffer();
	                fgbd4j.GetErrorDescription(err, errDesc);
	                throw new SQLException(errDesc.toString());
	            }
	            setupFieldInfo(table);
	            row=new Row();
	            err=table.CreateRowObject(row);
	            if(err!=0){
	                StringBuffer errDesc=new StringBuffer();
	                fgbd4j.GetErrorDescription(err, errDesc);
	                throw new SQLException(errDesc.toString());
	            }
	            for(int i=0;i<((InsertStmt) stmt).getFields().size();i++){
	                String colName=((InsertStmt) stmt).getFields().get(i);
	                Object val=params.get(i);
	                if(colName.equals(geometryColumn)){
	                    if(val==null){
	                        setRowVal(row, geometryColumn, null);
	                    }else{
                            shapeBuffer=new ShapeBuffer();
                            shapeBuffer.setBuffer((byte[])val);
                            row.SetGeometry(shapeBuffer);
	                    }
	                }else{
	                    setRowVal(row, colName, val);
	                }
	            }
	            if(shapeBuffer==null && geometryColumn!=null){
	                setRowVal(row, geometryColumn, null);
	            }
                if(shapeBuffer!=null){
                    shapeBuffer.delete();
                    shapeBuffer=null;
                }
	            err=table.Insert(row);
	            if(err!=0){
	                StringBuffer errDesc=new StringBuffer();
	                fgbd4j.GetErrorDescription(err, errDesc);
	                throw new SQLException(errDesc.toString());
	            }
	            params=null;
			}finally {
                if(shapeBuffer!=null){
                    shapeBuffer.delete();
                    shapeBuffer=null;
                }
			    if(row!=null) {
	                row.delete();
	                row=null;
			    }
			    if(table!=null) {
	                err=conn.getGeodatabase().CloseTable(table);
	                if(err!=0){
	                    StringBuffer errDesc=new StringBuffer();
	                    fgbd4j.GetErrorDescription(err, errDesc);
	                    throw new SQLException(errDesc.toString());
	                }
			        table.delete();
			        table=null;
			    }
			}
		}else if(stmt instanceof UpdateStmt){
			UpdateStmt ustmt=(UpdateStmt)stmt;
			Table table=null;
            Row row=null;
            EnumRows rows=null;
			try {
	            table=new Table();
	            err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
	            if(err!=0){
	                StringBuffer errDesc=new StringBuffer();
	                fgbd4j.GetErrorDescription(err, errDesc);
	                throw new SQLException(errDesc.toString());
	            }
	            setupFieldInfo(table);
	              rows=new EnumRows();
	              StringBuffer where=new StringBuffer();
	              {
	                  String sep="";
	                  int paramIdx=ustmt.getSettings().size();
	                  for(java.util.Map.Entry<Value,Value> set:ustmt.getConditions()){
	                      ColRef colref=(ColRef)set.getKey();
	                      where.append(sep);sep=" AND ";
	                      where.append(colref.getName());
	                      Object param=params.get(paramIdx++);
	                      appendParam(where, paramIdx, param);
	                  }
	              }
	              
	              // Make sure to disable recycling when intending to edit rows.
	              err= table.Search("*", where.toString(), false, rows);
	                if(err!=0){
	                    StringBuffer errDesc=new StringBuffer();
	                    fgbd4j.GetErrorDescription(err, errDesc);
	                    throw new SQLException(errDesc.toString());
	                }
	            
	              row=new Row();
	              while (rows.Next(row) == 0)
	              {
	                  int paramIdx=0;
	                  for(java.util.Map.Entry<Value,Value> set:ustmt.getSettings()){
	                      Object param=params.get(paramIdx++);
	                      String colName=((ColRef) set.getKey()).getName();
	                      setRowVal(row, colName, param);
	                  }
	                    err=table.Update(row);
	                    if(err!=0){
	                        StringBuffer errDesc=new StringBuffer();
	                        fgbd4j.GetErrorDescription(err, errDesc);
	                        throw new SQLException(errDesc.toString());
	                    }
	              }
	            params=null;
			}finally {
			    if(rows!=null) {
	                  rows.Close();
	                  rows.delete();
	                  rows=null;
			    }
			    if(row!=null) {
	                  row.delete();
	                  row=null;
			    }
			    if(table!=null) {
	                err=conn.getGeodatabase().CloseTable(table);
	                if(err!=0){
	                    StringBuffer errDesc=new StringBuffer();
	                    fgbd4j.GetErrorDescription(err, errDesc);
	                    throw new SQLException(errDesc.toString());
	                }
	                table.delete();
	                table=null;
			    }
			}
		}
		return 0;
	}


	private void setupFieldInfo(Table table) {
		if(fieldType==null){
			fieldType=new java.util.HashMap<String,Integer>();
			int err;
			FieldInfo fieldInfo=null;
			try {
	            fieldInfo=new FieldInfo();
	            err=table.GetFieldInformation(fieldInfo);
	            int[] fieldCounto=new int[1];
	            err=fieldInfo.GetFieldCount(fieldCounto);
	            fieldCount=fieldCounto[0];
	            int[] fieldType=new int[1];
	            for(int colIdx=0;colIdx<fieldCount;colIdx++){
	                StringBuffer fieldName=new StringBuffer();
	                fieldInfo.GetFieldName(colIdx, fieldName);
	                fieldInfo.GetFieldType(colIdx, fieldType);
	                this.fieldType.put(fieldName.toString(), fieldType[0]);
	                if(fieldType[0]==FieldType.fieldTypeGeometry.swigValue()){
	                    geometryColumn=fieldName.toString();
	                }
	            }
			}finally {
			    if(fieldInfo!=null) {
	                fieldInfo.delete();
	                fieldInfo=null;
			    }
			}
		}
	}


	private void appendParam(StringBuffer where, int paramIdx, Object param) {
		if(param==null){
			  where.append(" IS NULL");
		  }else{
			  where.append("=");
			  if(param instanceof String){
				  where.append("'");
				  where.append(param);
				  where.append("'");
			  }else if(param instanceof Long){
					  where.append(param);
			  }else{
				  throw new IllegalArgumentException("param "+paramIdx+" unexpected type "+param.getClass().getName());
			  }
		  }
	}

	private void setRowVal(Row row, String colName, Object val) throws SQLException {
		int err=0;
		if(val==null){
			err=row.SetNull(colName);
		}else{
			if(val instanceof String){
				if(fieldType.get(colName)==FieldType.fieldTypeXML.swigValue()){
					err=row.SetXML(colName,(String)val );
				}else{
					err=row.SetString(colName,(String)val );
				}
			}else if(val instanceof Long){
				err=row.SetInteger(colName,((Long)val).intValue() );
			}else if(val instanceof Integer){
				err=row.SetInteger(colName,((Integer) val).intValue() );
			}else if(val instanceof Double){
				err=row.SetDouble(colName,((Double) val).doubleValue() );
			}else if(val instanceof Boolean){
				err=row.SetShort(colName,(short) (((Boolean) val)?1:0) );
			}else if(val instanceof java.sql.Timestamp){
				GregorianCalendar value=new GregorianCalendar();
				value.setTimeInMillis(((java.sql.Timestamp) val).getTime());
                ce_time time=null;
				try {
	                time=new ce_time();
	                time.setTm_year(value.get(GregorianCalendar.YEAR)-1900);
	                time.setTm_mon(value.get(GregorianCalendar.MONTH));
	                time.setTm_mday(value.get(GregorianCalendar.DAY_OF_MONTH));
	                time.setTm_hour(value.get(GregorianCalendar.HOUR_OF_DAY));
	                time.setTm_min(value.get(GregorianCalendar.MINUTE));
	                time.setTm_sec(value.get(GregorianCalendar.SECOND));
	                time.setTm_isdst(0);
	                time.setTm_wday(0);
	                time.setTm_yday(0);
	                err=row.setDateTime(colName, time); 
				}finally {
				    if(time!=null) {
				        time.delete();
				    }
				}
			}else if(val instanceof java.sql.Date){
				GregorianCalendar value=new GregorianCalendar();
				value.setTimeInMillis(((java.sql.Date) val).getTime());
                ce_time time=null;
				try {
                    time=new ce_time();
                    time.setTm_year(value.get(GregorianCalendar.YEAR)-1900);
                    time.setTm_mon(value.get(GregorianCalendar.MONTH));
                    time.setTm_mday(value.get(GregorianCalendar.DAY_OF_MONTH));
                    time.setTm_hour(FgdbResultSet.MAGIC_HOUR_DATEONLY);
                    time.setTm_min(0);
                    time.setTm_sec(0);
                    time.setTm_isdst(0);
                    time.setTm_wday(0);
                    time.setTm_yday(0);
                    err=row.setDateTime(colName, time); 
				}finally {
                    if(time!=null) {
                        time.delete();
                    }
				}
			}else if(val instanceof java.sql.Time){
				GregorianCalendar value=new GregorianCalendar();
				value.setTimeInMillis(((java.sql.Time) val).getTime());
                ce_time time=null;
                try {
                    time=new ce_time();
                    time.setTm_year(FgdbResultSet.MAGIC_YEAR_TIMEONLY);
                    time.setTm_mon(FgdbResultSet.MAGIC_MON_TIMEONLY);
                    time.setTm_mday(FgdbResultSet.MAGIC_MDAY_TIMEONLY);
                    time.setTm_hour(value.get(GregorianCalendar.HOUR_OF_DAY));
                    time.setTm_min(value.get(GregorianCalendar.MINUTE));
                    time.setTm_sec(value.get(GregorianCalendar.SECOND));
                    time.setTm_isdst(FgdbResultSet.MAGIC_ISDST_TIMEONLY);
                    time.setTm_wday(FgdbResultSet.MAGIC_WDAY_TIMEONLY);
                    time.setTm_yday(FgdbResultSet.MAGIC_YDAY_TIMEONLY);
                    err=row.setDateTime(colName, time); 
                }finally {
                    if(time!=null) {
                        time.delete();
                    }
                }
			}else if(val instanceof byte[]){
				byte[] value= (byte[])val;
				ByteArray binaryBuf=new ByteArray();
				binaryBuf.setBuffer(value);
				row.SetBinary(colName, binaryBuf);
			}else{
				  throw new IllegalArgumentException("param "+colName+" unexpected type "+val.getClass().getName());
			}
		}
		if(err!=0){
			StringBuffer errDesc=new StringBuffer();
			fgbd4j.GetErrorDescription(err, errDesc);
			throw new SQLException(errDesc.toString());
		}
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArray(int arg0, Array arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlob(int arg0, Blob arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBoolean(int arg0, boolean arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setByte(int arg0, byte arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setBytes(int arg0, byte[] arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClob(int arg0, Clob arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDate(int arg0, Date arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setDouble(int arg0, double arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setFloat(int arg0, float arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setInt(int arg0, int arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setLong(int arg0, long arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNClob(int arg0, NClob arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNString(int arg0, String arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setNull(int idx, int sqlType) throws SQLException {
		setParam(idx,null);
	}

	@Override
	public void setNull(int idx, int arg1, String arg2) throws SQLException {
		setParam(idx,null);
	}

	@Override
	public void setObject(int idx, Object value) throws SQLException {
		setParam(idx, value);
	}

	@Override
	public void setObject(int idx, Object value, int arg2) throws SQLException {
		setParam(idx, value);
	}

	@Override
	public void setObject(int idx, Object value, int arg2, int arg3)
			throws SQLException {
		setParam(idx, value);
	}

	@Override
	public void setRef(int arg0, Ref arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setRowId(int arg0, RowId arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setShort(int arg0, short arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setString(int idx, String value) throws SQLException {
		setParam(idx,value);
	}

	private void setParam(int idx, Object value) {
		prepParamSet(idx);
		params.set(idx-1,value);
	}
	private void prepParamSet(int idx) {
		if(params==null){
			params=new java.util.ArrayList<Object>();
		}
		for(int i=params.size();i<idx;i++){
			params.add(null);
		}
	}

	@Override
	public void setTime(int arg0, Time arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setTimestamp(int idx, Timestamp value) throws SQLException {
		setParam(idx, value);
	}

	@Override
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
			throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setURL(int arg0, URL arg1) throws SQLException {
		setParam(arg0, arg1);
	}

	@Override
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}


	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int executeUpdate(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setMaxRows(int max) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setCursorName(String name) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean execute(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setFetchSize(int rows) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void addBatch(String sql) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean getMoreResults(int current) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	// since jre 1.7
	//@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	//@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
