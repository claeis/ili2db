package ch.ehi.ili2fgdb.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.parser.SqlLexer;
import ch.ehi.ili2fgdb.jdbc.parser.SqlSyntax;
import ch.ehi.ili2fgdb.jdbc.sql.AbstractSelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.ComplexSelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.FgdbSelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.IntConst;
import ch.ehi.ili2fgdb.jdbc.sql.IsNull;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValue;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueField;
import ch.ehi.ili2fgdb.jdbc.sql.SqlStmt;
import ch.ehi.ili2fgdb.jdbc.sql.StringConst;
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
		ResultSet ret=null;
		if(stmt instanceof ComplexSelectStmt){
			ret = executeComplexSelectStmt((ComplexSelectStmt) stmt);
		}else if(stmt instanceof FgdbSelectStmt){
			ret = executeFgdbSelectStmt((FgdbSelectStmt) stmt);
		} else {
			ret = executeStringQuery(stmtStr);
		}
		return ret;
	}

	private ResultSet executeSelectStmt(AbstractSelectStmt stmt) throws SQLException {
		ResultSet ret=null;
		if(stmt instanceof ComplexSelectStmt){
			ret = executeComplexSelectStmt((ComplexSelectStmt) stmt);
		}else if(stmt instanceof FgdbSelectStmt){
			ret = executeFgdbSelectStmt((FgdbSelectStmt) stmt);
		} else {
			throw new IllegalArgumentException("unexpceted type "+stmt.getClass().getName());
		}
		return ret;
	}
	private ResultSet executeComplexSelectStmt(ComplexSelectStmt stmt) throws SQLException {
		MemResultSet ret=new MemResultSet(executeSelectStmt(stmt.getSubSelect()),null,null);
		return ret;
	}

	private ResultSet executeStringQuery(String stmtStr) throws SQLException {
		ResultSet ret;
        EnumRows rows=null;
		try {
	        rows=new EnumRows();
	          int err=0;
	        err=conn.getGeodatabase().ExecuteSQL(stmtStr, true, rows);
	        if(err!=0){
	            StringBuffer errDesc=new StringBuffer();
	            fgbd4j.GetErrorDescription(err, errDesc);
	            throw new SQLException(errDesc.toString());
	        }
	        ret=new FgdbResultSet(conn,null,rows,null);
	        rows=null;
		}finally {
		    if(rows!=null) {
		        rows.Close();
		        rows.delete();
		        rows=null;
		    }
		}
		return ret;
	}

	private ResultSet executeFgdbSelectStmt(FgdbSelectStmt ustmt) throws SQLException {
		ResultSet ret;
		  List<SelectValue> selectvalues = null;
		  int err=0;
		Table table=null;
        EnumRows rows=null;
		try {
	        table=new Table();
	        err=conn.getGeodatabase().OpenTable(ustmt.getTableName(), table);
	        if(err!=0){
	            StringBuffer errDesc=new StringBuffer();
	            fgbd4j.GetErrorDescription(err, errDesc);
	            throw new SQLException(errDesc.toString(),"22000",err);
	        }
	          StringBuffer where=new StringBuffer();
	          { 
	              String sep="";
	                for(java.util.Map.Entry<Value,Value> cond : ustmt.getConditions()){
	                    where.append(((ch.ehi.ili2fgdb.jdbc.sql.ColRef)cond.getKey()).getName());
	                    Value rh = cond.getValue();
	                    if(rh instanceof IsNull) {
	                        where.append("IS NULL");
	                    }else {
	                        where.append("=");
	                        if(rh instanceof IntConst){
	                            where.append(Integer.toString(((IntConst)rh).getValue()));
	                        }else{
	                            where.append("'");
	                            where.append(((StringConst)rh).getValue());
	                            where.append("'");
	                        }
	                    }
	                    sep=" AND ";
	                }
	          }
	          StringBuffer fields=new StringBuffer();
	          {
	              String sep="";
	              for(SelectValue colref:ustmt.getFields()){
	                  if(colref instanceof SelectValueField){
	                      fields.append(sep);sep=",";
	                      fields.append(colref.getColumnName());
	                  }
	              }
	              if(fields.length()==0){
	                  fields.append("*");
	              }
	          }
	          
	          rows=new EnumRows();
	          err= table.Search(fields.toString(), where.toString(), false, rows);
	          selectvalues = ustmt.getFields();
	            if(err!=0){
	                StringBuffer errDesc=new StringBuffer();
	                fgbd4j.GetErrorDescription(err, errDesc);
	                throw new SQLException(errDesc.toString());
	            }
	            ret=new FgdbResultSet(conn,table,rows,selectvalues);
	            rows=null;
	            table=null;
	        return ret;
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
	}

	@Override
	public int executeUpdate(String stmtStr) throws SQLException {
        EnumRows rows=null;
          int err=0;
          try {
              rows=new EnumRows();
              err=conn.getGeodatabase().ExecuteSQL(stmtStr, true, rows);
              if(err!=0){
                  StringBuffer errDesc=new StringBuffer();
                  fgbd4j.GetErrorDescription(err, errDesc);
                  throw new SQLException(errDesc.toString());
              }
          }finally {
              if(rows!=null) {
                  rows.Close();
                  rows.delete();
                  rows=null;
              }
          }
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
