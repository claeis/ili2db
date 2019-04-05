package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iox.IoxException;

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class DbUtilityExecuteSqlTest {
	private static final String TEST_IN = "test/data/DbUtilitySql/";
	private static final String DBSCHEMA = "test";
	private static final String ATTRNAME = "attr1";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 

	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);				
		config.setDburl(dburl);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;	
	}

	private void createSchema(ResultSet rs, Statement stmt) throws IoxException {
		try {
			stmt.execute("CREATE SCHEMA "+DBSCHEMA);
			rs=stmt.executeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+DBSCHEMA+"'");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			assertEquals(1, columnCount);
			while(rs.next()){
				for(int i=1;i<=columnCount;i++){
					assertEquals(DBSCHEMA, rs.getString(1));
				}
			}
			if(rs!=null) {
				rs.close();
				rs=null;
			}
		} catch (SQLException e) {
	    	throw new IoxException(e);
		}
	}

	private void dropSchema(ResultSet rs, Statement stmt) throws IoxException {
	    try {
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		    rs=stmt.executeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '"+DBSCHEMA+"'");
		    assertFalse(rs.next());
			if(rs!=null) {
				rs.close();
				rs=null;
			}
	    } catch (SQLException e) {
	    	throw new IoxException(e);
		}
	}
	
	// Es wird getestet ob das sql Skript fehlerfrei in die Datenbank importiert werden kann,
	// wenn CREATE SEQUENCE mit 2 Semikolon: "test.t_ili2db_seq;;" beendet wird.
	@Test
	public void dbUtilityExecuteSql_TwiceSemicolon_Ok() throws IoxException, SQLException{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		ResultSet rs=null;
		try {
			Class driverClass = Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			throw new IoxException(e1);
		}
		jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
		Statement stmt=jdbcConnection.createStatement();
		try{
			dropSchema(rs, stmt);
		    createSchema(rs, stmt);
		    
	        // execute sql script
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(TEST_IN+"ExecuteTwiceSemicolon.sql"));
	        // insert into table
	        stmt.execute("INSERT INTO "+DBSCHEMA+".class1 ("+ATTRNAME+") VALUES ('a')");
			rs = stmt.executeQuery("SELECT "+ATTRNAME+" FROM "+DBSCHEMA+".class1");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			assertEquals(1, columnCount);
			while(rs.next()){
				for(int i=1;i<=columnCount;i++){
					assertEquals("a", rs.getString(ATTRNAME));
				}
			}
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(stmt!=null){
				stmt.close();
			}
		}
	}
	
	// Es wird getestet ob das sql Skript fehlerfrei in die Datenbank importiert werden kann,
	// wenn CREATE SEQUENCE mit 1 Semikolon: "test.t_ili2db_seq;" beendet wird.
	@Test
	public void dbUtilityExecuteSql_SingleSemicolon_Ok() throws IoxException, SQLException{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		ResultSet rs=null;
		try {
			Class driverClass = Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			throw new IoxException(e1);
		}
		jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
		Statement stmt=jdbcConnection.createStatement();
		try{
			dropSchema(rs, stmt);
		    createSchema(rs, stmt);
		    
	        // execute sql script
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(TEST_IN+"ExecuteSingleSemicolon.sql"));
	        // insert into table
	        stmt.execute("INSERT INTO "+DBSCHEMA+".class1 ("+ATTRNAME+") VALUES ('a')");
			rs = stmt.executeQuery("SELECT "+ATTRNAME+" FROM "+DBSCHEMA+".class1");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			assertEquals(1, columnCount);
			while(rs.next()){
				for(int i=1;i<=columnCount;i++){
					assertEquals("a", rs.getString(ATTRNAME));
				}
			}
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
			if(rs!=null) {
				rs.close();
			}
			if(stmt!=null){
				stmt.close();
			}
		}
	}
}