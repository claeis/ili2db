package ch.ehi.ili2db;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class InheritanceNoSmartTest {
	private static final String DBSCHEMA = "InheritanceNoSmart";
	private static final String DATASETNAME = "Testset1";
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
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

	@Test
	public void importNoSmart() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceNoSmart/Inheritance1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(null);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT classa3.attra3,a3b.attra3b FROM "+DBSCHEMA+".classa3,"+DBSCHEMA+".classa3b a3b WHERE classa3.t_ili_tid='7'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("a3",rs.getString(1));
					Assert.assertEquals("a3b",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

	@Test
	public void exportNoSmart() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceNoSmart/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceNoSmart/InsertIntoTable.sql"));
			File data=new File("test/data/InheritanceNoSmart/Inheritance1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Inheritance1");
			config.setFunction(Config.FC_EXPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// read objects of db and write objectValue to HashMap
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
				 IomObject obj1 = objs.get("17");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Inheritance1.TestB.ClassB1", obj1.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("x2", obj1.getattrobj("s3b", 0).getattrvalue("attrB3b"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b2", obj1.getattrobj("s2", 0).getattrvalue("attrB2b"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b3a", obj1.getattrobj("s3a", 0).getattrvalue("attrB3"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b1", obj1.getattrobj("s1", 0).getattrvalue("attrB1"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}	
	
	@Test
	public void importIliStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/InheritanceNoSmart/StructAttr1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(null);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("StructAttr1.TopicA.ClassC",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_classc",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
	
	@Test
	public void importXtfStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/InheritanceNoSmart/StructAttr1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(null);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// column-name
				Assert.assertTrue(stmt.execute("SELECT topica_structa.aname FROM "+DBSCHEMA+".topica_structa"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Anna",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceNoSmart/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceNoSmart/InsertIntoTable.sql"));
	        File data=new File("test/data/InheritanceNoSmart/StructAttr1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Inheritance1");
			config.setDatasetName(DATASETNAME);
			config.setFunction(Config.FC_EXPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// read objects of db and write objectValue to HashMap
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
				 IomObject obj1 = objs.get("17");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Inheritance1.TestB.ClassB1", obj1.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("x2", obj1.getattrobj("s3b", 0).getattrvalue("attrB3b"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b2", obj1.getattrobj("s2", 0).getattrvalue("attrB2b"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b3a", obj1.getattrobj("s3a", 0).getattrvalue("attrB3"));
			 }
			 {
				 IomObject obj1 = objs.get("17");
				 Assert.assertEquals("b1", obj1.getattrobj("s1", 0).getattrvalue("attrB1"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}