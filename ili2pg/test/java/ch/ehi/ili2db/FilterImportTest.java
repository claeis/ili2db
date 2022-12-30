package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.Ili2db;
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
public class FilterImportTest {
	
	private static final String DBSCHEMA = "FilterImport";
	private static final String TEST_OUT = "test/data/FilterImport/";
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
	public void importXtfByBID() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"FilterImport1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		        Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setBaskets("TestA1");
				config.setImportBid(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);

				// check that only one of the two classa1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".classa1";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(1,rs.getInt(1));
				}
				// check that no classb1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".classb1";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(0,rs.getInt(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfByBID() throws Exception
	{
		{
			importXtfByBID();
		}
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				File data=new File(TEST_OUT,"FilterImport1a-out.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				config.setBaskets("TestA1");
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
					 IomObject obj1 = objs.get("a1");
					 Assert.assertNotNull(obj1);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj1.getobjecttag());
					 
					 IomObject obj2 = objs.get("a2");
					 Assert.assertNull(obj2);
					 
					 IomObject obj3 = objs.get("b1");
					 Assert.assertNull(obj3);
				 }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfByTopic() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"FilterImport1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setTopics("FilterImport.TestA");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
		
				// check that the classa1 objects from both baskets were imported
				{
					String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".classa1";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(1));
				}
				// check that no classb1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".classb1";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(0,rs.getInt(1));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfByTopic() throws Exception
	{
		{
			importXtfByTopic();
		}
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				File data=new File(TEST_OUT,"FilterImport1a-out.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				config.setTopics("FilterImport.TestA");
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
					 IomObject obj1 = objs.get("a1");
					 Assert.assertNotNull(obj1);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj1.getobjecttag());
					 
					 IomObject obj2 = objs.get("a2");
					 Assert.assertNotNull(obj2);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj2.getobjecttag());
					 
					 IomObject obj3 = objs.get("b1");
					 Assert.assertNull(obj3);
				 }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
