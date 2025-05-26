package ch.ehi.ili2pg;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
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
public class Naming1smart1Test {
	private static final String DBSCHEMA = "Naming1smart1";
	private static final String DATASETNAME = "Testset1";
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
	public void importXtfDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        
			File data=new File("test/data/Naming1smart1/Naming1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setDatasetName(DATASETNAME);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
			config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			config.setTidHandling(config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			{
				Assert.assertTrue(stmt.execute("SELECT attra FROM "+DBSCHEMA+".naming1testclass_classa1 WHERE t_ili_tid='c2'"));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("attrA'",rs.getString(1));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportDataset_3x() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Naming1smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Naming1smart1/InsertIntoTable.sql"));
			File data=new File("test/data/Naming1smart1/Naming1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME);
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
			 
			IomObject a1 = objs.get("a1");
			Assert.assertNotNull(a1);
			Assert.assertEquals("Naming1.TestAttr.ClassA1", a1.getobjecttag());
			Assert.assertEquals("a1 first", a1.getattrvalue("attr1"));
			Assert.assertEquals("a1 second", a1.getattrvalue("Attr1"));

			IomObject a2 = objs.get("a2");
			Assert.assertNotNull(a2);
			Assert.assertEquals("Naming1.TestAttr.ClassA1a", a2.getobjecttag());
			Assert.assertEquals("a2 first", a2.getattrvalue("attr1"));
			Assert.assertEquals("a2 second", a2.getattrvalue("Attr1"));
			Assert.assertEquals("a2", a2.getattrvalue("attrA"));

			IomObject a3 = objs.get("a3");
			Assert.assertNotNull(a3);
			Assert.assertEquals("Naming1.TestAttr.ClassA1b", a3.getobjecttag());
			Assert.assertEquals("a3 first", a3.getattrvalue("attr1"));
			Assert.assertEquals("a3 second", a3.getattrvalue("Attr1"));
			Assert.assertEquals("a3", a3.getattrvalue("attrA"));

			IomObject c1 = objs.get("c1");
			Assert.assertNotNull(c1);
			Assert.assertEquals("Naming1.TestClass.ClassA1", c1.getobjecttag());
			Assert.assertEquals("attr1", c1.getattrvalue("attr1"));

			IomObject c2 = objs.get("c2");
			Assert.assertNotNull(c2);
			Assert.assertEquals("Naming1.TestClass.Classa1", c2.getobjecttag());
			Assert.assertEquals("attrA'", c2.getattrvalue("attrA"));
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void exportXtfDataset() throws Exception
    {
        importXtfDataset();
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File("test/data/Naming1smart1/Naming1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName(DATASETNAME);
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
             
            IomObject a1 = objs.get("a1");
            Assert.assertNotNull(a1);
            Assert.assertEquals("Naming1.TestAttr.ClassA1", a1.getobjecttag());
            Assert.assertEquals("a1 first", a1.getattrvalue("attr1"));
            Assert.assertEquals("a1 second", a1.getattrvalue("Attr1"));

            IomObject a2 = objs.get("a2");
            Assert.assertNotNull(a2);
            Assert.assertEquals("Naming1.TestAttr.ClassA1a", a2.getobjecttag());
            Assert.assertEquals("a2 first", a2.getattrvalue("attr1"));
            Assert.assertEquals("a2 second", a2.getattrvalue("Attr1"));
            Assert.assertEquals("a2", a2.getattrvalue("attrA"));

            IomObject a3 = objs.get("a3");
            Assert.assertNotNull(a3);
            Assert.assertEquals("Naming1.TestAttr.ClassA1b", a3.getobjecttag());
            Assert.assertEquals("a3 first", a3.getattrvalue("attr1"));
            Assert.assertEquals("a3 second", a3.getattrvalue("Attr1"));
            Assert.assertEquals("a3", a3.getattrvalue("attrA"));

            IomObject c1 = objs.get("c1");
            Assert.assertNotNull(c1);
            Assert.assertEquals("Naming1.TestClass.ClassA1", c1.getobjecttag());
            Assert.assertEquals("attr1", c1.getattrvalue("attr1"));

            IomObject c2 = objs.get("c2");
            Assert.assertNotNull(c2);
            Assert.assertEquals("Naming1.TestClass.Classa1", c2.getobjecttag());
            Assert.assertEquals("attrA'", c2.getattrvalue("attrA"));
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
}