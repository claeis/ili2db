package ch.ehi.ili2geodb.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.esri.arcgis.geodatabase.IWorkspace;

public class GeodbConnection implements ch.ehi.ili2db.base.GeodbConnection, Connection {
	private IWorkspace wksp=null;
	private String url=null;
	protected GeodbConnection(IWorkspace wksp,String url1){
		this.wksp=wksp;
		url=url1;
	}
	public String getUrl(){
		return url;
	}
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void close() throws SQLException {
		wksp=null;
	}

	public void commit() throws SQLException {
		// TODO Auto-generated method stub

	}

	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Statement createStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Statement createStatement(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return new GeodbMetaData(this);
	}

	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isClosed() throws SQLException {
		return wksp==null;
	}

	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public String nativeSQL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public CallableStatement prepareCall(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void rollback() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void rollback(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setAutoCommit(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setCatalog(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	public void setHoldability(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setReadOnly(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Savepoint setSavepoint(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTransactionIsolation(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public IWorkspace getGeodbWorkspace(){
		return wksp;
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
	public void abort(Executor arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	public void setSchema(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
