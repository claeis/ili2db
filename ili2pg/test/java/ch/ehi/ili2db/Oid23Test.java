package ch.ehi.ili2db;

import java.io.File;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Oid23Test {
	private static final String DBSCHEMA = "Oid23";
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
	public void importIli() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Oid23/Oid1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassA1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa1",rs.getString(2));
				}
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassA1b'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa1b",rs.getString(2));
				}
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassB1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classb1",rs.getString(2));
				}
				{
				    // t_ili2db_attrname
				    String [][] expectedValues=new String[][] {
				        {"Oid1.TestC.ac.a", "a", "classc1", "classa1"},
				    };
				    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
				    
				}
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid1.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestC.ac", "ch.ehi.ili2db.inheritance", "embedded"},
                        {"Oid1.TestC.ClassC1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
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
    public void importIliAssoc() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Oid23/Oid5.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(null);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        //{"Oid5.TestA.a2b.a", "a", "a2b", "classa1"},
                        //{"Oid5.TestA.a2b.b", "b", "a2b", "classb1"},
                        //{"Oid5.TestA.a2b.attrAB", "attrab", "a2b", null},
                        {"Oid5.TestA.ClassA1.attrA", "attra", "classa1", null},
                        {"Oid5.TestA.ClassB1.attrB", "attrb", "classb1", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid5.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid5.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        //{"Oid5.TestA.a2b",     "ch.ehi.ili2db.inheritance", "newClass"},
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
    public void importXtfAssoc() throws Exception
    {
        importIliAssoc();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                File data=new File("test/data/Oid23/Oid5a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName("Oid5");
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
    public void updateXtfAssoc() throws Exception
    {
        importXtfAssoc();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                File data=new File("test/data/Oid23/Oid5b.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_UPDATE);
                config.setDatasetName("Oid5");
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
    public void exportXtfAssoc() throws Exception
    {
        importXtfAssoc();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                File data=new File("test/data/Oid23/Oid5a-out.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setModels("Oid5");
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
    public void importIliMetaAttr() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Oid23/Oid3.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                //config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(null);
                config.setIliMetaAttrsFile("test/data/Oid23/Oid3.ini");
                config.setCreateMetaInfo(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassA1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassA1b'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1b",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassB1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classb1",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Oid3.TestC.ac.a", "a", "classc1", "classa1"},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid3.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid3.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid3.TestC.ac", "ch.ehi.ili2db.inheritance", "embedded"},
                        {"Oid3.TestC.ClassC1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid3.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid3.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
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
    public void importXtfMetaAttr() throws Exception
    {
        {
            importIliMetaAttr();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                {
                    File data=new File("test/data/Oid23/Oid3a.xtf");
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportBid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    File data=new File("test/data/Oid23/Oid3c.xtf");
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportBid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
            // import-test: Oid1a.xtf
            Integer a1_tid=null;
            {
                String stmtTxt="SELECT classa1.t_id, classa1.t_ili_tid FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
                a1_tid=rs.getInt(1);
            }
            {
                String stmtTxt="SELECT classb1.t_id, classb1.t_ili_tid FROM "+DBSCHEMA+".classb1 WHERE classb1.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
            }
            // import-test_ Oid1c.xtf
            {
                String stmtTxt="SELECT classc1.t_id, classc1.a FROM "+DBSCHEMA+".classc1";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals((int)a1_tid,rs.getInt(2));
                Assert.assertFalse(rs.next());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtfMetaAttr() throws Exception
    {
        {
            importXtfMetaAttr();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            File data=new File("test/data/Oid23/Oid1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setBaskets("Oid3.TestA");
            config.setModels("Oid3");
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
                 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid3.TestA.ClassB1b", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid3.TestA.ClassA1", obj0.getobjecttag());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
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
					File data=new File("test/data/Oid23/Oid1a.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		            config.setDoImplicitSchemaImport(true);
		    		config.setCreateFk(Config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Oid23/Oid1c.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
					config.setFunction(Config.FC_IMPORT);
					config.setCreateFk(Config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
			}
			// import-test: Oid1a.xtf
			Integer a1_tid=null;
			{
				String stmtTxt="SELECT classa1.t_id, classa1.t_ili_tid FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
				a1_tid=rs.getInt(1);
			}
			{
				String stmtTxt="SELECT classb1.t_id, classb1.t_ili_tid FROM "+DBSCHEMA+".classb1 WHERE classb1.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
			}
			// import-test_ Oid1c.xtf
			{
				String stmtTxt="SELECT classc1.t_id, classc1.a FROM "+DBSCHEMA+".classc1";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals((int)a1_tid,rs.getInt(2));
                Assert.assertFalse(rs.next());
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
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Oid23/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Oid23/InsertIntoTable.sql"));
	        File data=new File("test/data/Oid23/Oid1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setBaskets("Oid23.TestA");
			config.setModels("Oid1a");
			config.setFunction(Config.FC_EXPORT);
    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
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
				 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Oid23.TestA.ClassB1b", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Oid23.TestA.ClassA1", obj0.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importIliExtendedTopic() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Oid23/Oid2.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(null);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Oid2.TestD.a2b.a","a","classdb","classda"}                    
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Oid2.TestD.a2b","ch.ehi.ili2db.inheritance","embedded"},
                    {"Oid2.TestD.ClassDb","ch.ehi.ili2db.inheritance","newClass"},
                    {"Oid2.TestD.ClassDa","ch.ehi.ili2db.inheritance","newClass"},
                    {"Oid2.TestE.ClassDa","ch.ehi.ili2db.inheritance","superClass"},
                    {"Oid2.TestE.ClassDb","ch.ehi.ili2db.inheritance","superClass"}                  
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
    public void importXtfExtendedTopic() throws Exception
    {
        importIliExtendedTopic();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                File data=new File("test/data/Oid23/Oid2a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
                File data=new File("test/data/Oid23/Oid2b.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setValidation(false);
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
    @Ignore("enable when export by modelname is implemented  ili2db#287")
    public void exportXtfExtendedTopic() throws Exception
    {
        {
            importXtfExtendedTopic();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File("test/data/Oid23/Oid2-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setModels("Oid2");
            config.setFunction(Config.FC_EXPORT);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // read objects of db and write objectValue to HashMap
            HashMap<String,StartBasketEvent> baskets=new HashMap<String,StartBasketEvent>();
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                    baskets.put(((StartBasketEvent) event).getBid(),(StartBasketEvent) event);
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null){
                        objs.put(iomObj.getobjectoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertEquals(3,objs.size());
             {
                 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid2.TestE.ClassDa", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid2.TestE.ClassDa", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("a7284ca2-5c41-4479-ba69-66c05bff8fbd");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid2.TestE.ClassDb", obj0.getobjecttag());
                 IomObject refObj=obj0.getattrobj("a",0);
                 String ref=refObj.getobjectrefoid();
                 Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc", ref);
             }
             Assert.assertEquals(2,baskets.size());
             {
                 StartBasketEvent basket0 = baskets.get("1832a8d4-45be-4ede-ad85-c63940de272d");
                 Assert.assertNotNull(basket0);
                 Assert.assertEquals("Oid2.TestE", basket0.getType());
             }
             {
                 StartBasketEvent basket0 = baskets.get("f43a1da3-1afc-41e7-8a03-2225785f7ae9");
                 Assert.assertNotNull(basket0);
                 Assert.assertEquals("Oid2.TestE", basket0.getType());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
}