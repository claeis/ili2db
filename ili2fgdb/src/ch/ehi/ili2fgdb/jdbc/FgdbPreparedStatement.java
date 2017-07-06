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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.Row;
import ch.ehi.fgdb4j.jni.SWIGTYPE_p_tm;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.sql.ColRef;
import ch.ehi.ili2fgdb.jdbc.sql.InsertStmt;
import ch.ehi.ili2fgdb.jdbc.sql.SelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.SqlStmt;
import ch.ehi.ili2fgdb.jdbc.sql.UpdateStmt;
import ch.ehi.ili2fgdb.jdbc.sql.Value;

public class FgdbPreparedStatement extends FgdbStatement implements PreparedStatement {

	private SqlStmt stmt=null;
	private FgdbConnection conn=null;
	private String stmtStr=null;
	private java.util.ArrayList<Object> params=null;
	
	protected FgdbPreparedStatement(FgdbConnection conn,SqlStmt select, String stmtStr) {
		super(conn);
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
		  EnumRows rows=new EnumRows();
		  int err=0;
			if(stmt instanceof SelectStmt){
				SelectStmt ustmt=(SelectStmt)stmt;
				Table table=new Table();
				err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
				  StringBuffer where=new StringBuffer();
				  {
					  String sep="";
					  int paramIdx=0;
					  for(java.util.Map.Entry<Value,Value> set:ustmt.getConditions()){
						  ColRef colref=(ColRef)set.getKey();
						  where.append(sep);sep=" AND ";
						  where.append(colref.getName());
						  Object param=params.get(paramIdx++);
						  appendParam(where, paramIdx, param);
					  }
				  }
				  StringBuffer fields=new StringBuffer();
				  {
					  String sep="";
					  for(String colref:ustmt.getFields()){
						  fields.append(sep);sep=",";
						  fields.append(colref);
					  }
				  }
				  
				  err= table.Search(fields.toString(), where.toString(), true, rows);
			}else{
				err=conn.getGeodatabase().ExecuteSQL(stmtStr, true, rows);
			}
		if(err!=0){
			StringBuffer errDesc=new StringBuffer();
			fgbd4j.GetErrorDescription(err, errDesc);
			throw new SQLException(errDesc.toString());
		}
		FgdbResultSet ret=new FgdbResultSet(rows);
		
		return ret;
	}

	@Override
	public int executeUpdate() throws SQLException {
		if(stmt instanceof InsertStmt){
			Table table=new Table();
			conn.getGeodatabase().OpenTable(((InsertStmt) stmt).getTableName(), table);
			Row row=new Row();
			table.CreateRowObject(row);
			for(int i=0;i<((InsertStmt) stmt).getFields().size();i++){
				String colName=((InsertStmt) stmt).getFields().get(i);
				Object val=params.get(i);
				setRowVal(row, colName, val);
			}
			table.Insert(row);
			params=null;
		}else if(stmt instanceof UpdateStmt){
			UpdateStmt ustmt=(UpdateStmt)stmt;
			Table table=new Table();
			int err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
			  EnumRows rows=new EnumRows();
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
			  StringBuffer fields=new StringBuffer();
			  {
				  String sep="";
				  for(java.util.Map.Entry<Value,Value> set:ustmt.getSettings()){
					  ColRef colref=(ColRef)set.getKey();
					  fields.append(sep);sep=",";
					  fields.append(colref.getName());
				  }
			  }
			  
			  err= table.Search(fields.toString(), where.toString(), true, rows);
			
				Row row=new Row();
			  while (rows.Next(row) == 0)
			  {
				  int paramIdx=0;
				  for(java.util.Map.Entry<Value,Value> set:ustmt.getSettings()){
					  Object param=params.get(paramIdx++);
					  String colName=((ColRef) set.getKey()).getName();
					  setRowVal(row, colName, param);
				  }
					err=table.Update(row);
			  }
			  
			err=conn.getGeodatabase().CloseTable(table);
			params=null;
			
		}
		return 0;
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

	private void setRowVal(Row row, String i, Object val) {
		if(val==null){
			row.SetNull(i);
		}else{
			if(val instanceof String){
				row.SetString(i,(String)val );
			}else if(val instanceof Long){
				row.SetInteger(i,((Long)val).intValue() );
			}else if(val instanceof Integer){
				row.SetInteger(i,((Integer) val).intValue() );
			}else if(val instanceof java.sql.Timestamp){
				java.sql.Timestamp value= (java.sql.Timestamp)val;
				row.SetDate(i, value.getTime()/1000L); // use unix time
			}else{
				  throw new IllegalArgumentException("param "+i+" unexpected type "+val.getClass().getName());
			}
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

}
