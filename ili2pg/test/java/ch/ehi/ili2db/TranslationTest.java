package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class TranslationTest {
	private static final String DBSCHEMA = "Translation";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
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
	
	@Test
	public void importIli23() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {       
				File data=new File("test/data/Translation/EnumOk.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setModels("EnumOkA;EnumOkB");
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setVer4_translation(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// class[a] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'EnumOkA.TopicA.ClassA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa",rs.getString(2));
				}
				// class[b] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'EnumOkB.TopicB.ClassB'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
		    }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importIli10() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/Translation/ModelBsimple10.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setIli1Translation("ModelBsimple10=ModelAsimple10");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// class[a] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa",rs.getString(2));
				}
				// class[b] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	        
	@Test
	public void importIli10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/Translation/ModelBsimple10.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setDoItfLineTables(true);
				config.setAreaRef(config.AREA_REF_KEEP);
				config.setIli1Translation("ModelBsimple10=ModelAsimple10");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// class[a2] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa2",rs.getString(2));
				}
				// class[b2] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
		    }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importXtf23() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File("test/data/Translation/EnumOka.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		config.setVer4_translation(true);
	    		config.setDatasetName("EnumOka");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	    		// tid's of class[a]
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 4"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("o1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 5"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("o2",rs.getString(2));
				}
				// tid's of class[b]
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 9"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("x1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 10"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("x2",rs.getString(2));
				}
				// bid's of classa and classb are created
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("EnumOkA.TopicA",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 8"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("EnumOkB.TopicB",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void exportXtf23() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/CreateTableXtf23.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/InsertIntoTableXtf23.sql"));
			
			EhiLogger.getInstance().setTraceFilter(false);
			File data=new File("test/data/Translation/EnumOka-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			config.setDatasetName("EnumOka");
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
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("o2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
				 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
			 }
			 {
				 IomObject obj0 = objs.get("x1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("x2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
				 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importItf10() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File("test/data/Translation/ModelAsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
	    		config.setDatasetName("ModelAsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
	        {
	        	File data=new File("test/data/Translation/ModelBsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
    		// tid's of class[a]
			Assert.assertTrue(stmt.execute("SELECT classa.attra FROM translation.classa"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("o10",rs.getString(1));
			}
			// bid's of classa and classb are created
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 14"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
        
	@Test
	public void importItf10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File("test/data/Translation/ModelAsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setDoItfLineTables(true);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		config.setAreaRef(config.AREA_REF_KEEP);
	    		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
	    		config.setDatasetName("ModelAsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
	        {
	        	File data=new File("test/data/Translation/ModelBsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
 			Assert.assertTrue(stmt.execute("SELECT classa2_geoma._geom FROM "+DBSCHEMA+".classa2_geoma"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140",rs.getString(1));
 			}
 			Assert.assertTrue(stmt.execute("SELECT classa3_geoma._geom FROM translation.classa3_geoma"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140",rs.getString(1));
 			}
 			// bid's of classa and classb are created
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
 			}
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 16"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
 			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    
	@Test
	public void exportItf10() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/CreateTableItf10.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/InsertIntoTableItf10.sql"));
	        
	        {
	    		File data=new File("test/data/Translation/ModelAsimple10a-out.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelAsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	    		
	    		data=new File("test/data/Translation/ModelBsimple10a-out.itf");
	    		config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	    		
	    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
	    		ItfReader reader=new ItfReader(data);
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
					 IomObject obj0 = objs.get("21");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
				 }
	    		 {
					 IomObject obj0 = objs.get("20");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("22");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("25");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("26");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2_geomB", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("27");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3_geomB", obj0.getobjecttag());
				 }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    
	//@Test
	public void exportItf10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			EhiLogger.getInstance().setTraceFilter(false);
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/CreateTableItf10LineTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Translation/InsertIntoTableItf10LineTable.sql"));
	        {
	        	File data=new File("test/data/Translation/ModelAsimple10a-out.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelAsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
	        {
	        	File data=new File("test/data/Translation/ModelBsimple10a-out.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        
				HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
				ItfReader reader=new ItfReader(data);
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
					 IomObject obj0 = objs.get("21");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
				 }
	    		 {
					 IomObject obj0 = objs.get("20");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("22");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("25");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("26");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2_geomB", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("27");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3_geomB", obj0.getobjecttag());
				 }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}