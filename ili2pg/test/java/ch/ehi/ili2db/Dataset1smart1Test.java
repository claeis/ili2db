package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Dataset1smart1Test {
	private static final String DBSCHEMA = "Dataset1smart1";
	private static final String DATASETNAME_A = "Testset1";
	private static final String DATASETNAME_B = "Testset2";
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
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				{
					File data=new File("test/data/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
					config.setDatasetName(DATASETNAME_A);
					config.setFunction(Config.FC_IMPORT);
					config.setCreateFk(config.CREATE_FK_YES);
					config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
					config.setCatalogueRefTrafo(null);
					config.setMultiSurfaceTrafo(null);
					config.setMultilingualTrafo(null);
					config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Dataset1b1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
					config.setDatasetName(DATASETNAME_B);
					config.setFunction(Config.FC_IMPORT);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 1"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Testset1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 11"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Testset2",rs.getString(2));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void deleteSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/InsertIntoTable.sql"));		
			Config config=initConfig(null,DBSCHEMA,"test/data/Dataset1Smart1/Dataset1b1-out.xtf"+".log");
			config.setDatasetName(DATASETNAME_B);
			config.setFunction(Config.FC_DELETE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 1"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("Testset1",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 11"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertFalse(rs.next());
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void replaceSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/InsertIntoTable.sql"));
			File data=new File("test/data/Dataset1a2.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_REPLACE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			Assert.assertTrue(stmt.execute("SELECT classa1.attr1, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_id = 14"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("b1",rs.getString(1));
			}
			Assert.assertTrue(stmt.execute("SELECT classa1.attr1, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_id = 16"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("b1",rs.getString(1));
			}
			Assert.assertTrue(stmt.execute("SELECT classa1.attr1, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_id = 1002"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("a2",rs.getString(1));
			}
			Assert.assertTrue(stmt.execute("SELECT classa1.attr1, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_id = 1003"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("a2",rs.getString(1));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset1Smart1/InsertIntoTable.sql"));		
			
			File data=new File("test/data/Dataset1Smart1/Dataset1-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_EXPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			XtfReader reader=new XtfReader(data);
			IoxEvent event=null;
			 do{
		        event=reader.read();
		        if(event instanceof StartTransferEvent){
		        }else if(event instanceof StartBasketEvent){
		        }else if(event instanceof ObjectEvent){
		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
		        	if(iomObj.getobjectoid()!=null){
			        	objs.put(iomObj.getobjectoid(), iomObj);
		        	}
		        }else if(event instanceof EndBasketEvent){
		        }else if(event instanceof EndTransferEvent){
		        }
			 }while(!(event instanceof EndTransferEvent));
			 {
				 IomObject obj0 = objs.get("4");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Dataset1.TestA.ClassA1", obj0.getobjecttag());
				 Assert.assertEquals("a1", obj0.getattrvalue("attr1"));
			 }
			 {
				 IomObject obj0 = objs.get("6");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Dataset1.TestA.ClassA1b", obj0.getobjecttag());
				 Assert.assertEquals("a1", obj0.getattrvalue("attr1"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
