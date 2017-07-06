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
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.Row;
import ch.ehi.fgdb4j.jni.SWIGTYPE_p_tm;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.parser.SqlLexer;
import ch.ehi.ili2fgdb.jdbc.parser.SqlSyntax;
import ch.ehi.ili2fgdb.jdbc.sql.ColRef;
import ch.ehi.ili2fgdb.jdbc.sql.InsertStmt;
import ch.ehi.ili2fgdb.jdbc.sql.SelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.SqlStmt;
import ch.ehi.ili2fgdb.jdbc.sql.UpdateStmt;
import ch.ehi.ili2fgdb.jdbc.sql.Value;

public class FgdbStatement implements Statement {

	private FgdbConnection conn=null;
	
	protected FgdbStatement(FgdbConnection conn) {
		super();
		this.conn=conn;
	}

	@Override
	public void addBatch(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean execute(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet executeQuery(String stmtStr) throws SQLException {
		SqlStmt stmt=null;
		{
			SqlLexer lexer = new SqlLexer (new java.io.StringReader(stmtStr));
			SqlSyntax parser=new SqlSyntax(lexer);
			try {
				stmt=parser.statement();
			} catch (RecognitionException e) {
				throw new SQLException(e);
			} catch (TokenStreamException e) {
				throw new SQLException(e);
			}
		}
		  EnumRows rows=new EnumRows();
		  int err=0;
			if(stmt instanceof SelectStmt){
				SelectStmt ustmt=(SelectStmt)stmt;
				Table table=new Table();
				err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
				if(ustmt.getConditions()!=null && ustmt.getConditions().size()>0){
					throw new SQLException("where not yet implemented");
				}
				  StringBuffer where=new StringBuffer();
				  { // TODO
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
	public int executeUpdate(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
