package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class PreAndPostScriptTest {

	private static final String DBSCHEMA = "PreAndPostScriptSchema";
	private static final String DATASETA = "DataSetA";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Statement stmt=null;
	
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
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}
	
	// tests if preValue is executed before postValue in a schemaImport-test.
	// options:
	// [Config.FC_SCHEMAIMPORT]
	// [setDbschema(DBSCHEMA)]
	@Test
	public void schemaImportTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				// preScript
				File preData=new File("test/data/PreAndPostScript/PreScriptSchemaImport.sql");
				// ili
				File data=new File("test/data/PreAndPostScript/ModelA.ili");
				// postScript
				File postData=new File("test/data/PreAndPostScript/PostScriptSchemaImport.sql");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setPreScript(preData.toString());
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setDbschema(DBSCHEMA);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				Long tid=null;
				String scriptName=null;
				ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Long> rows = new HashMap<String, Long>();
				while(rs.next()){
					String[] row = new String[columnCount];
					for(int i=1;i<=columnCount;i++){
						tid = Long.parseLong(rs.getString(1));
						scriptName = rs.getString(2);
					}
					rows.put(scriptName, tid);
				}
				Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	// tests if preValue is executed before postValue in a import-test.
	// options:
	// [Config.FC_IMPORT]
	// [setDbschema(DBSCHEMA)]
	@Test
	public void importXtfTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				// prescript
				File preData=new File("test/data/PreAndPostScript/PreScript.sql");
				// xtf
				File data=new File("test/data/PreAndPostScript/MainImport.xtf");
				// afterScript
				File postData=new File("test/data/PreAndPostScript/PostScript.sql");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setPreScript(preData.toString());
				config.setDbschema(DBSCHEMA);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				Long tid=null;
				String scriptName=null;
				ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Long> rows = new HashMap<String, Long>();
				while(rs.next()){
					String[] row = new String[columnCount];
					for(int i=1;i<=columnCount;i++){
						tid = Long.parseLong(rs.getString(1));
						scriptName = rs.getString(2);
					}
					rows.put(scriptName, tid);
				}
				Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	// tests if preValue is executed before postValue in a update-test.
	// options:
	// [config.BASKET_HANDLING_READWRITE]
	// [config.TID_HANDLING_PROPERTY]
	// [config.CREATE_DATASET_COL]
	// [config.CREATE_FK_YES]
	// [setDbschema(DBSCHEMA)]
	// [setDatasetName(DATASETA)]
	// [setFunction(Config.FC_UPDATE)]
	@Test
	public void updateTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/CreateTableUpdate.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/InsertIntoTableUpdate.sql"));
			{
				// prescript
				File preData=new File("test/data/PreAndPostScript/PreScriptUpdate.sql");
				// data to update
				File data=new File("test/data/PreAndPostScript/MainUpdate.xtf");
				// afterScript
				File postData=new File("test/data/PreAndPostScript/PostScriptUpdate.sql");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setTidHandling(config.TID_HANDLING_PROPERTY);
				config.setCreateDatasetCols(config.CREATE_DATASET_COL);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setDbschema(DBSCHEMA);
				config.setDatasetName(DATASETA);
				config.setPreScript(preData.toString());
				config.setFunction(Config.FC_UPDATE);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				Long tid=null;
				String scriptName=null;
				ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Long> rows = new HashMap<String, Long>();
				while(rs.next()){
					String[] row = new String[columnCount];
					for(int i=1;i<=columnCount;i++){
						tid = Long.parseLong(rs.getString(1));
						scriptName = rs.getString(2);
					}
					rows.put(scriptName, tid);
				}
				Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	// tests if preValue is executed before postValue in a replace-test.
	// options:
	// [Config.FC_REPLACE]
	// [setDatasetName(DATASETA)]
	@Test
	public void replaceTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/CreateTableReplace.sql"));
			DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/InsertIntoTableReplace.sql"));
			{
				// prescript
				File preData=new File("test/data/PreAndPostScript/PreScriptReplace.sql");
				// new data
				File data=new File("test/data/PreAndPostScript/MainReplace.xtf");
				// afterScript
				File postData=new File("test/data/PreAndPostScript/PostScriptReplace.sql");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setDbschema(DBSCHEMA);
				config.setDatasetName(DATASETA);
				config.setPreScript(preData.toString());
				config.setFunction(Config.FC_REPLACE);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				Long tid=null;
				String scriptName=null;
				ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Long> rows = new HashMap<String, Long>();
				while(rs.next()){
					String[] row = new String[columnCount];
					for(int i=1;i<=columnCount;i++){
						tid = Long.parseLong(rs.getString(1));
						scriptName = rs.getString(2);
					}
					rows.put(scriptName, tid);
				}
				Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	// tests if preValue is executed before postValue in a delete-test.
	// options:
	// [Config.FC_DELETE]
	// [setDatasetName(DATASETA)]
	// [config.BASKET_HANDLING_READWRITE]
	@Test
	public void deleteTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/CreateTableDelete.sql"));
			DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/InsertIntoTableDelete.sql"));
			{
				// prescript
				File preData=new File("test/data/PreAndPostScript/PreScriptDelete.sql");
				// afterScript
				File postData=new File("test/data/PreAndPostScript/PostScriptDelete.sql");
				Config config=initConfig(null,DBSCHEMA,"test/data/PreAndPostScript/MainImport-out.xtf"+".log");
				config.setDatasetName(DATASETA);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setPreScript(preData.toString());
				config.setFunction(Config.FC_DELETE);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				Long tid=null;
				String scriptName=null;
				ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Long> rows = new HashMap<String, Long>();
				while(rs.next()){
					String[] row = new String[columnCount];
					for(int i=1;i<=columnCount;i++){
						tid = Long.parseLong(rs.getString(1));
						scriptName = rs.getString(2);
					}
					rows.put(scriptName, tid);
				}
				Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	// tests if preValue is executed before postValue in a export-test.
	// options:
	// [Config.FC_EXPORT]
	// [setDatasetName(DATASETA)]
	// [config.BASKET_HANDLING_READWRITE]
	@Test
	public void exportTest() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
			jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
			stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/CreateTableExport.sql"));
				DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/PreAndPostScript/InsertIntoTableExport.sql"));
				// prescript
				File preData=new File("test/data/PreAndPostScript/PreScriptExport.sql");
				// export data
				File data=new File("test/data/PreAndPostScript/MainExport-out.xtf");
				// afterScript
				File postData=new File("test/data/PreAndPostScript/PostScriptExport.sql");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setDatasetName(DATASETA);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setPreScript(preData.toString());
				config.setFunction(Config.FC_EXPORT);
				config.setPostScript(postData.toString());
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					Long tid=null;
					String scriptName=null;
					ResultSet rs = stmt.executeQuery("SELECT helperclass.t_id, helperclass.attr1 FROM "+DBSCHEMA+".helperclass");
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					Map<String, Long> rows = new HashMap<String, Long>();
					while(rs.next()){
						String[] row = new String[columnCount];
						for(int i=1;i<=columnCount;i++){
							tid = Long.parseLong(rs.getString(1));
							scriptName = rs.getString(2);
						}
						rows.put(scriptName, tid);
					}
					Assert.assertTrue(rows.get("preValue")<rows.get("postValue"));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}