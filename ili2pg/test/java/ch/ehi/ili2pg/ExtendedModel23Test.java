package ch.ehi.ili2pg;

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
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class ExtendedModel23Test {
	private static final String DBSCHEMA = "ExtendedModel23";
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
	public void importXtf() throws Exception
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
					File data=new File("test/data/ExtendedModel/ExtendedModel1.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		            config.setDoImplicitSchemaImport(true);
					config.setModels("BaseModel;ExtendedModel");
		    		config.setCreateFk(config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
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
	public void exportXtfOriginal() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection,new java.io.FileReader("test/data/ExtendedModel/ExtendedModelCreateTable.sql"));
			DbUtility.executeSqlScript(jdbcConnection,new java.io.FileReader("test/data/ExtendedModel/ExtendedModelInsertIntoTable.sql"));
			File data=new File("test/data/ExtendedModel/ExtendedModel1-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("BaseModel;ExtendedModel");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
				 IomObject obj0 = objs.get("32");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("ExtendedModel.TestAp.ClassA2", obj0.getobjecttag());
				 Assert.assertEquals("rot.dunkel", obj0.getattrvalue("farbe"));
				 Assert.assertEquals("33", obj0.getattrobj("a3",0).getobjectrefoid());
				 Assert.assertEquals("1.1", obj0.getattrvalue("wert"));
				 Assert.assertEquals("34", obj0.getattrobj("ap1",0).getobjectrefoid());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfBase() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        
	        DbUtility.executeSqlScript(jdbcConnection,new java.io.FileReader("test/data/ExtendedModel/ExtendedModelCreateTable.sql"));
			DbUtility.executeSqlScript(jdbcConnection,new java.io.FileReader("test/data/ExtendedModel/ExtendedModelInsertIntoTable.sql"));
			File data=new File("test/data/ExtendedModel/ExtendedModel1-out.xtf");
			
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("BaseModel;ExtendedModel");
			config.setExportModels("BaseModel");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
				 IomObject obj0 = objs.get("32");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("BaseModel.TestA.ClassA2", obj0.getobjecttag());
				 Assert.assertEquals("rot", obj0.getattrvalue("farbe"));
				 Assert.assertEquals("33", obj0.getattrobj("a3",0).getobjectrefoid());
				 Assert.assertEquals(null, obj0.getattrvalue("wert"));
				 Assert.assertEquals(null, obj0.getattrobj("ap1",0));
			 }
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
}