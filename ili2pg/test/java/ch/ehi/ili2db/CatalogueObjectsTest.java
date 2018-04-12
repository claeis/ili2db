package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Ignore;
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

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CatalogueObjectsTest {
	private static final String DBSCHEMA = "CatalogueObjects1";
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
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

	@Test
	public void importIli() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/CatalogueObjects/CatalogueObjects1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(null);
                config.setCatalogueRefTrafo(null);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
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
    public void importIliSmart1CoalesceCatalogRef() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/CatalogueObjects/CatalogueObjects1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setDatasetName(DATASETNAME);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
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
    public void importIliSmart2CoalesceCatalogRef() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/CatalogueObjects/CatalogueObjects1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setDatasetName(DATASETNAME);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
    @Ignore("fails because of mixed TID type in base table")
	@Test
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/CatalogueObjects/CatalogueObjects1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				//config.setValidation(false);
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
    public void importXtfSmart1CoalesceCatalogRef() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/CatalogueObjects/CatalogueObjects1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setDatasetName(DATASETNAME);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                //config.setValidation(false);
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
    public void importXtfSmart2CoalesceCatalogRef() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/CatalogueObjects/CatalogueObjects1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setDatasetName(DATASETNAME);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                //config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
	
    @Ignore("see importXtf()")
	@Test
	public void exportXtf() throws Exception
	{
	    importXtf();
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/CreateTable.sql"));
	        //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/InsertIntoTable.sql"));
			File data=new File("test/data/CatalogueObjects/CatalogueObjects1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setDatasetName(DATASETNAME);
			config.setBaskets("CatalogueObjects1.TopicC.1");
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
				 IomObject obj0 = objs.get("9");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Katalog_OhneUuid", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("10");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Nutzung", obj1.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void exportXtfSmart1CoalesceCatalogRef() throws Exception
    {
        importXtfSmart1CoalesceCatalogRef();
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/CreateTable.sql"));
            //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/InsertIntoTable.sql"));
            File data=new File("test/data/CatalogueObjects/CatalogueObjects1a-smart1out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setDatasetName(DATASETNAME);
            config.setBaskets("CatalogueObjects1.TopicC.1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // read objects of db and write objectValue to HashMap
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
            IomObject nutzung=null;
            IomObject ohneUuid=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null){
                        objs.put(iomObj.getobjectoid(), iomObj);
                        if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicC.Nutzung")) {
                            nutzung=iomObj;
                        }else if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicB.OhneUuid")) {
                            ohneUuid=iomObj;
                        }
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertNotNull(ohneUuid);
             Assert.assertNotNull(nutzung);
             {
                 // TODO verify references Programm_n, OhneUuid_n, Programm_1, OhneUuid_1
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtfSmart2CoalesceCatalogRef() throws Exception
    {
        importXtfSmart2CoalesceCatalogRef();
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/CreateTable.sql"));
            //DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/InsertIntoTable.sql"));
            File data=new File("test/data/CatalogueObjects/CatalogueObjects1a-smart2out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setDatasetName(DATASETNAME);
            config.setBaskets("CatalogueObjects1.TopicC.1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // read objects of db and write objectValue to HashMap
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
            IomObject nutzung=null;
            IomObject ohneUuid=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null){
                        objs.put(iomObj.getobjectoid(), iomObj);
                        if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicC.Nutzung")) {
                            nutzung=iomObj;
                        }else if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicB.OhneUuid")) {
                            ohneUuid=iomObj;
                        }
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertNotNull(ohneUuid);
             Assert.assertNotNull(nutzung);
             {
                 // TODO verify references Programm_n, OhneUuid_n, Programm_1, OhneUuid_1
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
}