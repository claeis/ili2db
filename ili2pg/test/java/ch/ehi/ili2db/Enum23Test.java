package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
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
public class Enum23Test {
	private static final String DBSCHEMA = "Enum23";
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
	public void importIliWithoutBeautify() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2_ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele_2",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
    @Test
    public void createScriptFromIliFkTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            File data=new File("test/data/Enum23/Enum23b.ili");
            File outfile=new File(data.getPath()+"-out.sql");
            Config config=new Config();
            new ch.ehi.ili2pg.PgMain().initConfig(config);
            config.setLogfile(data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_SCRIPT);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setDbschema(DBSCHEMA);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(null);
            config.setCreatescript(outfile.getPath());
            Ili2db.run(config,null);
            
            // verify generated script
            {
                Class driverClass = Class.forName("org.postgresql.Driver");
                jdbcConnection = DriverManager.getConnection(
                        dburl, dbuser, dbpwd);
                jdbcConnection.setAutoCommit(false);
                stmt=jdbcConnection.createStatement();          
                stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
                stmt.close();
                stmt=null;
                
                // execute generated script
                DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(outfile));

                jdbcConnection.commit();
                jdbcConnection.close();
                jdbcConnection=null;
                
                {
                    // rum import without schema generation
                    data=new File("test/data/Enum23/Enum23b.xtf");
                    config.setXtffile(data.getPath());
                    config.setDburl(dburl);
                    config.setDbusr(dbuser);
                    config.setDbpwd(dbpwd);
                    config.setDbschema(DBSCHEMA);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(false);
                    config.setCreatescript(null);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                    
                }
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
    @Test
    public void createScriptFromIliSingleTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            File data=new File("test/data/Enum23/Enum23b.ili");
            File outfile=new File(data.getPath()+"-out.sql");
            Config config=new Config();
            new ch.ehi.ili2pg.PgMain().initConfig(config);
            config.setLogfile(data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_SCRIPT);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setDbschema(DBSCHEMA);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(null);
            config.setCreatescript(outfile.getPath());
            Ili2db.run(config,null);
            
            // verify generated script
            {
                Class driverClass = Class.forName("org.postgresql.Driver");
                jdbcConnection = DriverManager.getConnection(
                        dburl, dbuser, dbpwd);
                jdbcConnection.setAutoCommit(false);
                stmt=jdbcConnection.createStatement();          
                stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
                stmt.close();
                stmt=null;
                
                // execute generated script
                DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(outfile));

                jdbcConnection.commit();
                jdbcConnection.close();
                jdbcConnection=null;
                
                {
                    // rum import without schema generation
                    data=new File("test/data/Enum23/Enum23b.xtf");
                    config.setXtffile(data.getPath());
                    config.setDburl(dburl);
                    config.setDbusr(dbuser);
                    config.setDbpwd(dbpwd);
                    config.setDbschema(DBSCHEMA);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(false);
                    config.setCreatescript(null);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                    
                }
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
    @Test
    public void createScriptFromIliMultiTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            File data=new File("test/data/Enum23/Enum23b.ili");
            File outfile=new File(data.getPath()+"-out.sql");
            Config config=new Config();
            new ch.ehi.ili2pg.PgMain().initConfig(config);
            config.setLogfile(data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_SCRIPT);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setDbschema(DBSCHEMA);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(null);
            config.setCreatescript(outfile.getPath());
            Ili2db.run(config,null);
            
            // verify generated script
            {
                Class driverClass = Class.forName("org.postgresql.Driver");
                jdbcConnection = DriverManager.getConnection(
                        dburl, dbuser, dbpwd);
                jdbcConnection.setAutoCommit(false);
                stmt=jdbcConnection.createStatement();          
                stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
                stmt.close();
                stmt=null;
                
                // execute generated script
                DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(outfile));

                jdbcConnection.commit();
                jdbcConnection.close();
                jdbcConnection=null;
                
                {
                    // rum import without schema generation
                    data=new File("test/data/Enum23/Enum23b.xtf");
                    config.setXtffile(data.getPath());
                    config.setDburl(dburl);
                    config.setDbusr(dbuser);
                    config.setDbpwd(dbpwd);
                    config.setDbschema(DBSCHEMA);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(false);
                    config.setCreatescript(null);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                    
                }
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
	
	@Test
	public void importIliWithBeautify() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
		
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2 ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele 2",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2 ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele 2",rs.getString(1));
				}
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"boolean")));
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"classa1_attr2")));
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"classa1_attr4")));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}		
	}
	
	@Test
	public void importIliExtendedMultiTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23b.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    Assert.assertFalse(stmt.getMoreResults());
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}		
	}
    @Test
    public void importIliExtendedFkTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23b.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                //config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23b.Enum1' AND "+DbNames.ENUM_TAB_BASE_COL+" IS NULL AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    Assert.assertFalse(stmt.getMoreResults());
                    stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23b.Enum1a' AND "+DbNames.ENUM_TAB_BASE_COL+"='Enum23b.Enum1' AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    Assert.assertFalse(stmt.getMoreResults());
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfExtendedFkTable() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23b.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23b.Enum1' AND "+DbNames.ENUM_TAB_BASE_COL+" IS NULL AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    Assert.assertFalse(stmt.getMoreResults());
                    stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23b.Enum1a' AND "+DbNames.ENUM_TAB_BASE_COL+"='Enum23b.Enum1' AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    Assert.assertFalse(stmt.getMoreResults());
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void exportXtfExtendedFkTable() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtfExtendedFkTable();
        
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            File data=new File("test/data/Enum23/Enum23b-out.xtf");
            {
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Enum23b");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
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
                     IomObject obj0 = objs.get("1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23b.TestA.ClassA1", obj0.getobjecttag());
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1"));
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1a"));
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1b"));
                 }
                 {
                     IomObject obj0 = objs.get("2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23b.TestA.ClassA2", obj0.getobjecttag());
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1"));
                     Assert.assertEquals("Test2.Test2a", obj0.getattrvalue("attr1a"));
                     Assert.assertEquals("Test2.Test2a", obj0.getattrvalue("attr1b"));
                 }
                
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
	
	@Test
	public void importIliSingleTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT "+DbNames.ENUM_TAB_DISPNAME_COL+" FROM "+DBSCHEMA+"."+DbNames.ENUM_TAB+" WHERE "+DbNames.ENUM_TAB_ILICODE_COL+" ='Test2_ele' AND "+DbNames.ENUM_TAB_THIS_COL+"='Enum23.Enum1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2_ele",rs.getString(1));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}