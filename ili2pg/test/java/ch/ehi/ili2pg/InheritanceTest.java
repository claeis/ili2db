package ch.ehi.ili2pg;

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
import ch.ehi.ili2db.Ili2dbAssert;
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
public class InheritanceTest {
    private static final String TEST_DATA_DIR = "test/data/Inheritance";
	private static final String DBSCHEMA = "Inheritance";
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

	@Test
	public void importIli_smart0() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File(TEST_DATA_DIR,"Inheritance1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
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
    public void importIli_smart1() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"Inheritance1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    // TODO ili2db#382 integrate Inheritance2.ili into Inheritance1.ili
    @Test
    public void importIli_smart2() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            File data=new File(TEST_DATA_DIR,"Inheritance2.ili");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            //config.setCreatescript(data.getPath()+".sql");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // TODO verify content of t_ili2db_attrname
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Inheritance2.TestB.ClassA.attrA3",  "attra3",    "classa", null},
                    {"Inheritance2.TestA.ClassA3b.attrA3b", "attra3b", "classa3b", null},
                    {"Inheritance2.TestA.ClassB.attrB", "attrb", "classb", null},    
                    {"Inheritance2.TestA.ClassA3b.attrA3b", "attra3b", "classa3c", null},  
                    {"Inheritance2.TestA.ClassA3.attrA3", "attra3", "classa3c", null},
                    {"Inheritance2.TestA.ClassA3.attrA3", "attra3", "classa3b", null},  
                    {"Inheritance2.TestA.aa2bb.bb", "bb", "aa2bb", "classb"},
                    {"Inheritance2.TestA.ClassA3c.attrA3c", "attra3c", "classa3c", null},  
                    {"Inheritance2.TestA.aa2bb.aa", "aa_classa3c", "aa2bb", "classa3c"},
                    {"Inheritance2.TestA.aa2bb.aa", "aa_classa3b", "aa2bb", "classa3b"},
                    {"Inheritance2.TestA.a2b.a", "a_classa3b", "classb", "classa3b"},
                    {"Inheritance2.TestA.a2b.a",  "a_classa3c",    "classb",    "classa3c"},
                    {"Inheritance2.TestB.ClassA2.attrA3", "attra3", "classa2", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Inheritance2.TestA.ClassA3",    "ch.ehi.ili2db.inheritance", "subClass"},
                    {"Inheritance2.TestB.ClassA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestA.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestA.ClassA3b",   "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestA.ClassA3c",   "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestA.aa2bb",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestB.ClassA2",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Inheritance2.TestA.a2b",    "ch.ehi.ili2db.inheritance", "embedded"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }            
    }
    
    
    @Test
    public void importXtf_smart0() throws Exception
    {
        {
            importIli_smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);

        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            {
                File data=new File(TEST_DATA_DIR,"Inheritance1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName(DATASETNAME);
                config.setImportTid(true);
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
    public void importXtf_smart2() throws Exception
    {
        {
            importIli_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            File data=new File(TEST_DATA_DIR,"Inheritance2a.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setDatasetName(DATASETNAME);
            config.setImportTid(true);
            config.setImportBid(true);
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
    public void updateXtfNew_smart2() throws Exception
    {
        {
            importIli_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            File data=new File(TEST_DATA_DIR,"Inheritance2a.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_UPDATE);
            config.setImportTid(true);
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
    public void updateXtfExisting_smart2() throws Exception
    {
        {
            importXtf_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            long old_t_id=-1;
            Assert.assertTrue(stmt.execute("SELECT t_id,attra3,attra3b,attra3c FROM "+DBSCHEMA+".classa3c WHERE t_ili_tid='2'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                old_t_id=rs.getLong(1);
            }
            File data=new File(TEST_DATA_DIR,"Inheritance2aUpdate.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_UPDATE);
            config.setDatasetName(DATASETNAME);
            //config.setCreatescript(data.getPath()+".sql");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            Assert.assertTrue(stmt.execute("SELECT t_id,attra3,attra3b,attra3c FROM "+DBSCHEMA+".classa3c WHERE t_ili_tid='2'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(old_t_id,rs.getLong(1));
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
    public void importXtfExtRef_smart2() throws Exception
    {
        {
            importXtf_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();

            File data=new File(TEST_DATA_DIR,"Inheritance2b.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
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
	public void exportXtf_smart0() throws Exception
	{
	    {
	        importXtf_smart0();
	    }
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
			File data=new File(TEST_DATA_DIR,"Inheritance1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Inheritance1");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
    public void exportXtf_smart2() throws Exception
    {
        {
            importXtf_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            File data=new File(TEST_DATA_DIR,"Inheritance2a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName(DATASETNAME);
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
                    }else {
                        objs.put(iomObj.getattrobj("aa",0).getobjectrefoid()+iomObj.getattrobj("bb",0).getobjectrefoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertEquals(8, objs.size());
             {
                 IomObject obj1 = objs.get("1");
                 Assert.assertEquals("Inheritance2.TestA.ClassA3b oid 1 {attrA3 attra3-10, attrA3b attra3b-10}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("2");
                 Assert.assertEquals("Inheritance2.TestA.ClassA3c oid 2 {attrA3 attra3-20, attrA3b attra3b-20, attrA3c attra3c-20}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("3");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 3 {a -> 1 REF {}, attrB attrb-30}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("4");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 4 {a -> 2 REF {}, attrB attrb-40}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("5");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 5 {a -> 1 REF {}, attrB attrb-50}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("14");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 4 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("24");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 2 REF {}, bb -> 4 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("15");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 5 REF {}}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void exportXtf_IdPerTable_smart0() throws Exception
    {
        {
            importIli_smart0();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR,"IdPerTable_smart0.sql")));
            File data=new File(TEST_DATA_DIR,"Inheritance1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setTopics("Inheritance1.TestD");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
                    }else {
                        objs.put(iomObj.getattrobj("d2",0).getobjectrefoid()+iomObj.getattrobj("x2",0).getobjectrefoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertEquals(3, objs.size());
             {
                 IomObject obj1 = objs.get("20");
                 Assert.assertEquals("Inheritance1.TestD.ClassD1b oid 20 {attrD1 20_d1, attrD1b 20_d1b}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("21");
                 Assert.assertEquals("Inheritance1.TestD.ClassD1x oid 21 {attrD1x 21_d1x, d1 -> 20 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("2021");
                 Assert.assertEquals("Inheritance1.TestD.d2x {d2 -> 20 REF {}, x2 -> 21 REF {}}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtf_IdPerTable_smart1() throws Exception
    {
        {
            importIli_smart1();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR,"IdPerTable_smart1.sql")));
            File data=new File(TEST_DATA_DIR,"Inheritance1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setTopics("Inheritance1.TestD");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
                    }else {
                        objs.put(iomObj.getattrobj("d2",0).getobjectrefoid()+iomObj.getattrobj("x2",0).getobjectrefoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertEquals(3, objs.size());
             {
                 IomObject obj1 = objs.get("20");
                 Assert.assertEquals("Inheritance1.TestD.ClassD1b oid 20 {attrD1 20_d1, attrD1b 20_d1b}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("21");
                 Assert.assertEquals("Inheritance1.TestD.ClassD1x oid 21 {attrD1x 21_d1x, d1 -> 20 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("2021");
                 Assert.assertEquals("Inheritance1.TestD.d2x {d2 -> 20 REF {}, x2 -> 21 REF {}}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtf_IdPerTable_smart2() throws Exception
    {
        {
            importIli_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR,"IdPerTable_smart2.sql")));
            File data=new File(TEST_DATA_DIR,"Inheritance2a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName(DATASETNAME);
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
                    }else {
                        objs.put(iomObj.getattrobj("aa",0).getobjectrefoid()+iomObj.getattrobj("bb",0).getobjectrefoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertEquals(8, objs.size());
             {
                 IomObject obj1 = objs.get("1");
                 Assert.assertEquals("Inheritance2.TestA.ClassA3b oid 1 {attrA3 attra3-10, attrA3b attra3b-10}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("2");
                 Assert.assertEquals("Inheritance2.TestA.ClassA3c oid 2 {attrA3 attra3-20, attrA3b attra3b-20, attrA3c attra3c-20}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("3");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 3 {a -> 1 REF {}, attrB attrb-30}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("4");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 4 {a -> 2 REF {}, attrB attrb-40}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("5");
                 Assert.assertEquals("Inheritance2.TestA.ClassB oid 5 {a -> 1 REF {}, attrB attrb-50}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("14");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 4 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("24");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 2 REF {}, bb -> 4 REF {}}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("15");
                 Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 5 REF {}}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void importIliStructAttrFK_smart0() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_DATA_DIR,"StructAttr1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// FIXME check that FK exists on reference from struct table to class table
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA.name",   "aname", "topica_structa", null},
                        {"StructAttr1.TopicA.StructAb.ab1",   "ab1", "topica_structab", null},
                        {"StructAttr1.TopicA.ClassA.attr1",   "topica_classa_attr1",   "topica_structa",   "topica_classa"},
                        {"StructAttr1.TopicA.ClassA.attr2",   "attr2",   "topica_classa",   null},
                        {"StructAttr1.TopicA.ClassB.attr3",   "attr3", "topica_classb", null},
                        {"StructAttr1.TopicA.ClassC.attr4",   "attr4", "topica_classc", null},
                        {"StructAttr1.TopicA.ClassD.d1",   "topica_classd_d1",   "topica_structa",   "topica_classd"},
                        {"StructAttr1.TopicA.ClassD.d2",   "d2", "topica_classd", null},
                        {"StructAttr1.TopicB.StructA.name",   "aname", "topicb_structa", null},
                        {"StructAttr1.TopicB.ClassA.attr1",   "topicb_classa_attr1",   "topicb_structa",    "topicb_classa"},
                        {"StructAttr1.TopicB.ClassA.attr2",   "attr2", "topicb_classa", null},
                        {"StructAttr1.TopicB.ClassB.attr3",   "attr3", "topicb_classb", null},
                        {"StructAttr1.TopicB.ClassB.b1",   "topicb_classb_b1", "topicb_structa", "topicb_classb"},
                        {"StructAttr1.TopicB.ClassC.attr4",   "attr4", "topicb_classc", null}, 
                        {"StructAttr1.TopicB.ClassC.c1",   "topicb_classc_c1", "topicb_structa", "topicb_classc"},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.StructAb", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
    @Test
    public void importIliStructAttrFK_smart1() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"StructAttr1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                // FIXME check that FK exists on reference from struct table to class table
                // imported classes
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'StructAttr1.TopicA.StructA'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("topica_structa",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'StructAttr1.TopicB.StructA'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("topicb_structa",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA.name",   "aname", "topica_structa", null},
                        {"StructAttr1.TopicA.StructAb.ab1",   "ab1", "topica_structa", null},
                        {"StructAttr1.TopicA.ClassA.attr1",   "topica_structa_attr1",   "topica_structa", "topica_structa"},
                        {"StructAttr1.TopicA.ClassA.attr2",   "attr2", "topica_structa", null},
                        {"StructAttr1.TopicA.ClassB.attr3",   "attr3", "topica_structa", null},
                        {"StructAttr1.TopicA.ClassC.attr4",   "attr4", "topica_structa", null},
                        {"StructAttr1.TopicA.ClassD.d1",   "topica_structa_d1",   "topica_structa", "topica_structa"},
                        {"StructAttr1.TopicA.ClassD.d2",   "d2",   "topica_structa", null},
                        {"StructAttr1.TopicB.StructA.name",  "aname", "topicb_structa", null},    
                        {"StructAttr1.TopicB.ClassB.attr3",   "attr3", "topicb_classb", null},
                        {"StructAttr1.TopicB.ClassA.attr1",   "topicb_classb_attr1",   "topicb_structa", "topicb_classb"},
                        {"StructAttr1.TopicB.ClassB.b1",   "topicb_classb_b1",   "topicb_structa", "topicb_classb"},
                        {"StructAttr1.TopicB.ClassC.c1",   "topicb_classb_c1",   "topicb_structa", "topicb_classb"},
                        {"StructAttr1.TopicB.ClassA.attr2",   "attr2", "topicb_classb", null},
                        {"StructAttr1.TopicB.ClassC.attr4",   "attr4", "topicb_classb", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.StructAb",  "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
    @Test
    public void importIliStructAttrFK_smart2() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"StructAttr1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                // FIXME check that FK exists on reference from struct table to class table
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_structab", null},
                        {"StructAttr1.TopicB.ClassB.attr3", "attr3",   "topicb_classc",   null},
                        {"StructAttr1.TopicA.ClassC.attr4", "attr4",   "topica_classc",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_classd",   "topica_classc"},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_classd",   "topica_classb"},
                        {"StructAttr1.TopicB.ClassA.attr2", "attr2",   "topicb_classc",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_classd",   "topica_classa"},
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_classa",   null},
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_classc",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_structa",  "topica_classb"},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_structa",  "topica_classa"},
                        {"StructAttr1.TopicA.ClassB.attr3", "attr3",   "topica_classb",   null},
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_structa",  null},
                        {"StructAttr1.TopicA.ClassA.attr2", "attr2",   "topica_classb",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_structa",  "topica_classc"},
                        {"StructAttr1.TopicB.ClassB.attr3", "attr3",   "topicb_classb",   null},
                        {"StructAttr1.TopicA.StructAb.ab1", "ab1", "topica_structab", null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_classa",   "topica_classa"},
                        {"StructAttr1.TopicB.ClassA.attr2", "attr2",   "topicb_classb",   null},
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_classb",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_classa",   "topica_classc"},
                        {"StructAttr1.TopicB.ClassB.b1",    "topicb_classc_b1",    "topicb_structa",  "topicb_classc"},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_classa",   "topica_classb"},
                        {"StructAttr1.TopicA.ClassD.d1",    "topica_classd_d1",    "topica_structab", "topica_classd"},
                        {"StructAttr1.TopicA.StructA.name", "aname",   "topica_classd",   null},
                        {"StructAttr1.TopicB.ClassB.b1",    "topicb_classb_b1",    "topicb_structa",  "topicb_classb"},
                        {"StructAttr1.TopicB.StructA.name", "aname",   "topicb_structa",  null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_structab", "topica_classc"},
                        {"StructAttr1.TopicA.ClassD.d2",    "d2",  "topica_classd",   null},
                        {"StructAttr1.TopicB.ClassA.attr1", "topicb_classb_attr1", "topicb_structa",  "topicb_classb"},
                        {"StructAttr1.TopicB.ClassA.attr1", "topicb_classc_attr1", "topicb_structa",  "topicb_classc"},
                        {"StructAttr1.TopicA.ClassB.attr3", "attr3",   "topica_classc",   null},
                        {"StructAttr1.TopicB.ClassC.c1",    "topicb_classc_c1",    "topicb_structa",  "topicb_classc"},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_structab", "topica_classb"},
                        {"StructAttr1.TopicA.ClassA.attr2", "attr2",   "topica_classc",   null},
                        {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_structab", "topica_classa"},
                        {"StructAttr1.TopicA.ClassA.attr2", "attr2",   "topica_classa",   null},
                        {"StructAttr1.TopicB.ClassC.attr4", "attr4",   "topicb_classc",   null}
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String[][] expectedValues=new String[][] {
                        {"StructAttr1.TopicA.StructA",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicA.StructAb",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                        {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
	
	@Test
	public void importXtfStructAttrFK_smart0() throws Exception
	{
	    {
	        importIliStructAttrFK_smart0();
	    }
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        {
				File data=new File(TEST_DATA_DIR,"StructAttr1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setDatasetName(DATASETNAME);
				config.setImportTid(true);
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
    public void importXtfStructAttrFK_smart1() throws Exception
    {
        {
            importIliStructAttrFK_smart1();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            {
                File data=new File(TEST_DATA_DIR,"StructAttr1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName(DATASETNAME);
                config.setImportTid(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                // subtype value
                Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.aname = 'Anna'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                }
                Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.aname = 'Berta'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                }
                Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.aname = 'Claudia'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
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
    public void importXtfStructAttrFK_smart2() throws Exception
    {
        {
            importIliStructAttrFK_smart2();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            {
                File data=new File(TEST_DATA_DIR,"StructAttr1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName(DATASETNAME);
                config.setImportTid(true);
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
	public void exportXtfStructAttrFK_smart0() throws Exception
	{
	    {
	        importXtfStructAttrFK_smart0();
	    }
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        File data=new File(TEST_DATA_DIR,"StructAttr1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME);
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
             Assert.assertEquals(6, objs.size());
			 {
				 IomObject obj1 = objs.get("a1");
                 Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
			 }
			 {
				 IomObject obj1 = objs.get("a2");
                 Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
			 }
			 {
				 IomObject obj1 = objs.get("a3");
                 Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
			 }
             {
                 IomObject obj1 = objs.get("a4");
                 Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
             }
			 {
				 IomObject obj1 = objs.get("b2");
                 Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
			 }
			 {
				 IomObject obj1 = objs.get("b3");
				 Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void exportXtfStructAttrFK_smart1() throws Exception
    {
        {
            importXtfStructAttrFK_smart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File(TEST_DATA_DIR,"StructAttr1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName(DATASETNAME);
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
             Assert.assertEquals(6, objs.size());
             {
                 IomObject obj1 = objs.get("a1");
                 Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a2");
                 Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a3");
                 Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a4");
                 Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("b2");
                 Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("b3");
                 Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtfStructAttrFK_smart2() throws Exception
    {
        {
            importXtfStructAttrFK_smart2();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File(TEST_DATA_DIR,"StructAttr1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName(DATASETNAME);
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
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
             Assert.assertEquals(6, objs.size());
             {
                 IomObject obj1 = objs.get("a1");
                 Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a2");
                 Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a3");
                 Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("a4");
                 Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("b2");
                 Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
             }
             {
                 IomObject obj1 = objs.get("b3");
                 Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
    @Test
    public void importIliRefAttrFK_smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"RefAttr1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                //config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                
                // inheritance Ref test.
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.ClassA1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("RefAttr1.TopicA.ClassA0",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.ClassA11'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("RefAttr1.TopicA.ClassA1",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.StructA1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("RefAttr1.TopicA.StructA0",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.StructA11'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("RefAttr1.TopicA.StructA1",rs.getString(2));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfRefAttrFK_smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importIliRefAttrFK_smart1();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            {
                File data=new File(TEST_DATA_DIR,"RefAttr1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName(DATASETNAME);
                config.setImportTid(true);
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
    public void exportXtfRefAttrFK_smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importXtfRefAttrFK_smart1();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            
            File data=new File(TEST_DATA_DIR,"RefAttr1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName(DATASETNAME);
            //config.setValidation(false);
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
                 IomObject obj0 = objs.get("b.3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("RefAttr1.TopicA.StructA11 {ref -> a11.1 REF {}}", obj0.getattrobj("struct", 0).toString());
             }
             {
                 IomObject obj0 = objs.get("b.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("RefAttr1.TopicA.StructA1 {ref -> a1.1 REF {}}", obj0.getattrobj("struct", 0).toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
    @Test
    public void importIliSubtypeFK_smart1() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"SubtypeFK.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setDatasetName(DATASETNAME);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                
                // subtype class import
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.bc1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("bc1",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassA'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassB'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classb",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassC'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classc",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"SubtypeFK23.Topic.bc1.b1", "b1", "classa", "classa"},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"SubtypeFK23.Topic.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"SubtypeFK23.Topic.ClassB",  "ch.ehi.ili2db.inheritance", "superClass"},
                        {"SubtypeFK23.Topic.bc1", "ch.ehi.ili2db.inheritance", "embedded"},
                        {"SubtypeFK23.Topic.ClassC",  "ch.ehi.ili2db.inheritance", "superClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
    
    @Test
    public void importXtfSubtypeFK_smart1() throws Exception
    {
        {
            importIliSubtypeFK_smart1();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            {
                File data=new File(TEST_DATA_DIR,"SubtypeFKa.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName(DATASETNAME);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                
                // subtype value
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.target, t_ili2db_attrname.sqlname FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.target = 'classa'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("b1",rs.getString(2));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
	
}