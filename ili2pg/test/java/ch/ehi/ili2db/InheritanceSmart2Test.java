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
public class InheritanceSmart2Test {
	private static final String DBSCHEMA = "InheritanceSmart2";
	private static final String DATASETNAME = "Testset1";
	private static final String DATASETNAMEX = "Testset1x";
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

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
    public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File("test/data/InheritanceSmart2/Inheritance2a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
			config.setDatasetName(DATASETNAME);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			//config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			Assert.assertTrue(stmt.execute("SELECT attra3,attra3b,attra3c FROM "+DBSCHEMA+".classa3c WHERE t_ili_tid='2'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("attra3-20",rs.getString(1));
				Assert.assertEquals("attra3b-20",rs.getString(2));
				Assert.assertEquals("attra3c-20",rs.getString(3));
			}
			Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM "+DBSCHEMA+".classb WHERE t_ili_tid='4'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertNotNull(rs.getObject("a_classa3c"));
				Assert.assertNull(rs.getObject("a_classa3b"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}            
	}
	
	@Test
	public void updateXtfNew() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2Schema.sql"));
			File data=new File("test/data/InheritanceSmart2/Inheritance2a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_UPDATE);
            config.setDatasetName(DATASETNAME);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
	        
	        Statement stmt=jdbcConnection.createStatement();
			Assert.assertTrue(stmt.execute("SELECT attra3,attra3b,attra3c FROM "+DBSCHEMA+".classa3c WHERE t_ili_tid='2'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("attra3-20",rs.getString(1));
				Assert.assertEquals("attra3b-20",rs.getString(2));
				Assert.assertEquals("attra3c-20",rs.getString(3));
			}
			Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM "+DBSCHEMA+".classb WHERE t_ili_tid='4'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertNotNull(rs.getObject("a_classa3c"));
				Assert.assertNull(rs.getObject("a_classa3b"));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}          
	}
	
	@Test
	public void updateXtfExisting() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2Schema.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2a.sql"));
			File data=new File("test/data/InheritanceSmart2/Inheritance2aUpdate.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_UPDATE);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
			config.setDatasetName(DATASETNAME);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			//config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
	        
	        Statement stmt=jdbcConnection.createStatement();
			Assert.assertTrue(stmt.execute("SELECT t_id,attra3,attra3b,attra3c FROM "+DBSCHEMA+".classa3c WHERE t_ili_tid='2'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals(5,rs.getLong(1));
				Assert.assertEquals("attra3-20u",rs.getString(2));
				Assert.assertEquals("attra3b-20u",rs.getString(3));
				Assert.assertEquals("attra3c-20u",rs.getString(4));
			}

			Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM "+DBSCHEMA+".classb WHERE t_ili_tid='4'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertNotNull(rs.getObject("a_classa3c"));
				Assert.assertNull(rs.getObject("a_classa3b"));
			}		
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfSmart2ExtRef() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2Schema.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2a.sql"));	        
	        Statement stmt=jdbcConnection.createStatement();

			File data=new File("test/data/InheritanceSmart2/Inheritance2b.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setDatasetName(DATASETNAMEX);
			//config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);

			Assert.assertTrue(stmt.execute("SELECT attrb,attra3,attra3b,attra3c FROM "+DBSCHEMA+".classb INNER JOIN "+DBSCHEMA+".classa3c ON (classa3c.t_id=classb.a_classa3c) WHERE classb.t_ili_tid='x2'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("attrb-x2",rs.getString(1));
				Assert.assertEquals("attra3-20",rs.getString(2));
				Assert.assertEquals("attra3b-20",rs.getString(3));
				Assert.assertEquals("attra3c-20",rs.getString(4));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	        	        
	}
	
	@Test
	public void exportXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2Schema.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2a.sql"));		
			File data=new File("test/data/InheritanceSmart2/Inheritance2a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME);
			config.setFunction(Config.FC_EXPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
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
			File data=new File("test/data/InheritanceSmart2/StructAttr1.ili");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
			config.setDatasetName(DATASETNAME);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			//config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
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
			File data=new File("test/data/InheritanceSmart2/StructAttr1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
			config.setDatasetName(DATASETNAME);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			//config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
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
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2Schema.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart2/InitInheritanceSmart2a.sql"));
			File data=new File("test/data/InheritanceSmart2/StructAttr1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setDatasetName(DATASETNAME);
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
				 IomObject obj0 = objs.get("2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Inheritance2.TestA.ClassA3c", obj0.getobjecttag());
				 Assert.assertEquals("attra3c-20", obj0.getattrvalue("attrA3c"));
			 }
			 {
				 IomObject obj0 = objs.get("3");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Inheritance2.TestA.ClassB", obj0.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}