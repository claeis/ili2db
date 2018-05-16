package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Assoc23Test {
	
	private static final String DBSCHEMA = "Assoc23";
	private static final String TEST_OUT="test/data/Assoc23/";
	Connection jdbcConnection=null;
	Statement stmt=null;
	
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
	
	@Test
	public void importXtfRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1a.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfRefForward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1b.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfRefUnkownFail() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1z.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		try{
		    		Ili2db.run(config,null);
		    		Assert.fail();
	    		}catch(Ili2dbException ex){
	    			
	    		}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfExtRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc2a.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfExtFileRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				{
					File data=new File(TEST_OUT,"Assoc2b1.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setCreateFk(config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					File data=new File(TEST_OUT,"Assoc2b2.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setCreateFk(config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfExtRefForward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc2c.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfRefBackward() throws Exception
	{
		{
			importXtfRefBackward();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=null;
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1a-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfRefForward() throws Exception
	{
		{
			importXtfRefForward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1b-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfRefUnkownFail() throws Exception
	{
		{
			importXtfRefUnkownFail();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1z-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		try{
		    		Ili2db.run(config,null);
	    		}catch(Ili2dbException ex){
	    			Assert.fail();
	    		}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				int obj0 = objs.size();
				assertEquals(0,obj0);
			 }
		}
	}
	
	@Test
	public void exportXtfExtRefBackward() throws Exception
	{
		{
			importXtfExtRefBackward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc2a-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc2");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfExtFileRefBackward() throws Exception
	{
		{
			importXtfExtFileRefBackward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				{
					data=new File(TEST_OUT,"Assoc2b1-out.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
		    		config.setModels("Assoc2");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					data=new File(TEST_OUT,"Assoc2b2-out.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
		    		config.setModels("Assoc2");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				 // assoc2b1
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
				 }
				 // assoc2b2
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfExtRefForward() throws Exception
	{
		{
			importXtfExtRefForward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc2c-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc2");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		{
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
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
			 }
		}
	}
}