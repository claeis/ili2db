package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class Oid23Test {
	protected static final String TEST_DATA_DIR="test/data/Oid23";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	@Test
	public void importIli_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		Statement stmt=null;
		try{
	        setup.resetDb();
			{
				File data=new File(TEST_DATA_DIR,"Oid1.ili");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
		        jdbcConnection = setup.createConnection();
	            stmt=jdbcConnection.createStatement();
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassA1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa1",rs.getString(2));
				}
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassA1b'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa1b",rs.getString(2));
				}
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid1.TestA.ClassB1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classb1",rs.getString(2));
				}
				{
				    // t_ili2db_attrname
				    String [][] expectedValues=new String[][] {
				        {"Oid1.TestC.ac.a", "a", "classc1", "classa1"},
                        {"Oid1.TestA.ab2.a", "a", "ab2", "classa2"},
                        {"Oid1.TestA.ab2.b", "b", "ab2", "classb2"},
				    };
				    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
				    
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
                        {"Oid1.TestA.ClassB2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid1.TestA.ab2", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(2, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("classa1"));
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("ab2"));
                    
                }
			}
		}finally{
            if(stmt!=null){
                stmt.close();
            }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importIliStructBaseTidCol_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid6.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1b'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1b",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassB1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classb1",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0.a0", "a0", "structa0", null},
                        {"Oid6.TestA.ClassA1.a1", "a1", "classa1", null},
                        {"Oid6.TestA.ClassA1b.a1b", "a1b", "classa1b", null},
                        {"Oid6.TestA.StructB0.b0", "b0", "structb0", null},
                        {"Oid6.TestA.ClassB1.b1", "b1", "classb1", null},
                        {"Oid6.TestA.ClassB1b.b1b", "b1b", "classb1b", null},
                        {"Oid6.TestA.StructC0.c0", "c0", "structc0", null},
                        {"Oid6.TestA.ClassC1.c1", "c1", "classc1", null},
                        {"Oid6.TestA.ClassC1b.c1b", "c1b", "classc1b", null},
                        {"Oid6.TestA.StructD0.d0", "d0", "structd0", null},
                        {"Oid6.TestA.ClassD1.d1", "d1", "classd1", null},
                        {"Oid6.TestA.ClassD1b.d1b", "d1b", "classd1b", null},
                        {"Oid6.TestA.StructE0.e0", "e0", "structe0", null},
                        {"Oid6.TestA.ClassE1.e1", "e1", "classe1", null},
                        {"Oid6.TestA.ClassE1b.e1b", "e1b", "classe1b", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructB0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructC0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassC1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassC1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructD0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassD1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassD1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructE0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassE1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassE1b", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(2, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structa0"));
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structc0"));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfStructBaseTidCol_Smart0() throws Exception
    {
        {
            importIliStructBaseTidCol_Smart0();
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Oid6a.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportTid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // import-test: Oid1a.xtf
            Integer a1_tid=null;
            {
                String stmtTxt="SELECT structa0.t_id, structa0.t_ili_tid FROM "+setup.prefixName("structa0")+" WHERE structa0.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
                a1_tid=rs.getInt(1);
            }
            {
                String stmtTxt="SELECT structb0.t_id, structb0.t_ili_tid FROM "+setup.prefixName("structb0")+" WHERE structb0.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtfStructBaseTidCol_Smart0() throws Exception
    {
        {
            importXtfStructBaseTidCol_Smart0();
        }
        try{
            File data=new File(TEST_DATA_DIR,"Oid6a-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setModels("Oid6");
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
                 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81887a1c-257e-45c2-8c8c-c05ddc6c3c38");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1b", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1b", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("9088acab-eaea-4b1e-80a6-80b7cb98005d");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassC1b", obj0.getobjecttag());
             }
        }finally{
        }
    }
    @Test
    public void importIliStructBaseNoTidCol_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid6.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1b'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1b",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassB1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classb1",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0.a0", "a0", "structa0", null},
                        {"Oid6.TestA.ClassA1.a1", "a1", "classa1", null},
                        {"Oid6.TestA.ClassA1b.a1b", "a1b", "classa1b", null},
                        {"Oid6.TestA.StructB0.b0", "b0", "structb0", null},
                        {"Oid6.TestA.ClassB1.b1", "b1", "classb1", null},
                        {"Oid6.TestA.ClassB1b.b1b", "b1b", "classb1b", null},
                        {"Oid6.TestA.StructC0.c0", "c0", "structc0", null},
                        {"Oid6.TestA.ClassC1.c1", "c1", "classc1", null},
                        {"Oid6.TestA.ClassC1b.c1b", "c1b", "classc1b", null},
                        {"Oid6.TestA.StructD0.d0", "d0", "structd0", null},
                        {"Oid6.TestA.ClassD1.d1", "d1", "classd1", null},
                        {"Oid6.TestA.ClassD1b.d1b", "d1b", "classd1b", null},
                        {"Oid6.TestA.StructE0.e0", "e0", "structe0", null},
                        {"Oid6.TestA.ClassE1.e1", "e1", "classe1", null},
                        {"Oid6.TestA.ClassE1b.e1b", "e1b", "classe1b", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructB0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructC0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassC1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassC1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructD0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassD1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassD1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructE0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassE1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassE1b", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(2, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structa0"));
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structc0"));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfStructBaseNoTidCol_Smart0() throws Exception
    {
        {
            importIliStructBaseNoTidCol_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Oid6a.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // import-test: Oid1a.xtf
            Integer a1_tid=null;
            {
                String stmtTxt="SELECT structa0.t_id, structa0.t_ili_tid FROM "+setup.prefixName("structa0")+" WHERE structa0.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
                a1_tid=rs.getInt(1);
            }
            {
                String stmtTxt="SELECT structb0.t_id, structb0.t_ili_tid FROM "+setup.prefixName("structb0")+" WHERE structb0.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtfStructBaseNoTidCol_Smart0() throws Exception
    {
        {
            importXtfStructBaseNoTidCol_Smart0();
        }
        try{
            File data=new File(TEST_DATA_DIR,"Oid6a-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setModels("Oid6");
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
                 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81887a1c-257e-45c2-8c8c-c05ddc6c3c38");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1b", obj0.getobjecttag());
             }
             if(false){
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1b", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("9088acab-eaea-4b1e-80a6-80b7cb98005d");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassC1b", obj0.getobjecttag());
             }
        }finally{
        }
    }
    @Test
    public void importIliStructBaseNoTidCol_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid6.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassA1b'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1b",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid6.TestA.ClassB1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classb1",rs.getString(2));
                }
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0.a0", "a0", "structa0", null},
                        {"Oid6.TestA.ClassA1.a1", "a1", "structa0", null},
                        {"Oid6.TestA.ClassA1b.a1b", "a1b", "structa0", null},
                        {"Oid6.TestA.StructB0.b0", "b0", "structb0", null},
                        {"Oid6.TestA.ClassB1.b1", "b1", "structb0", null},
                        {"Oid6.TestA.ClassB1b.b1b", "b1b", "structb0", null},
                        {"Oid6.TestA.StructC0.c0", "c0", "structc0", null},
                        {"Oid6.TestA.ClassC1.c1", "c1", "classc1b", null},
                        {"Oid6.TestA.ClassC1b.c1b", "c1b", "classc1b", null},
                        {"Oid6.TestA.StructD0.d0", "d0", "structd0", null},
                        {"Oid6.TestA.ClassD1.d1", "d1", "structd0", null},
                        {"Oid6.TestA.ClassD1b.d1b", "d1b", "structd0", null},
                        {"Oid6.TestA.StructE0.e0", "e0", "structe0", null},
                        {"Oid6.TestA.ClassE1.e1", "e1", "classe1b", null},
                        {"Oid6.TestA.ClassE1b.e1b", "e1b", "classe1b", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid6.TestA.StructA0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.StructB0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.StructC0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassC1", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"Oid6.TestA.ClassC1b", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.StructD0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassD1", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.ClassD1b", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"Oid6.TestA.StructE0", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid6.TestA.ClassE1", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"Oid6.TestA.ClassE1b", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(2, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structa0"));
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("structc0"));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfStructBaseNoTidCol_Smart1() throws Exception
    {
        {
            importIliStructBaseNoTidCol_Smart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Oid6a.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // import-test: Oid1a.xtf
            Integer a1_tid=null;
            {
                String stmtTxt="SELECT structa0.t_id, structa0.t_ili_tid FROM "+setup.prefixName("structa0")+" WHERE structa0.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
                a1_tid=rs.getInt(1);
            }
            {
                String stmtTxt="SELECT structb0.t_id, structb0.t_ili_tid FROM "+setup.prefixName("structb0")+" WHERE structb0.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtfStructBaseNoTidCol_Smart1() throws Exception
    {
        {
            importXtfStructBaseNoTidCol_Smart1();
        }
        try{
            File data=new File(TEST_DATA_DIR,"Oid6a-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setModels("Oid6");
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
                 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81887a1c-257e-45c2-8c8c-c05ddc6c3c38");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassA1b", obj0.getobjecttag());
             }
             if(false){
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("81fc3941-01ec-4c51-b1ba-46b6295d9b4e");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassB1b", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("9088acab-eaea-4b1e-80a6-80b7cb98005d");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid6.TestA.ClassC1b", obj0.getobjecttag());
             }
        }finally{
        }
    }
    
    @Test
    public void importIliwithoutBid_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid5.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Oid5.TestA.ClassA1.attrA", "attra", "classa1", null},
                        {"Oid5.TestA.ClassB1.attrB", "attrb", "classb1", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Oid5.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Oid5.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(2, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("classa1"));
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("classb1"));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfwithoutBid_Smart0() throws Exception
    {
        {
            importIliwithoutBid_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"Oid5a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName("Oid5");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void updateXtfwithoutBid_Smart0() throws Exception
    {
        {
            importXtfwithoutBid_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"Oid5b.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_UPDATE);
                config.setDatasetName("Oid5");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtfwithoutBid_Smart0() throws Exception
    {
        {
            importXtfwithoutBid_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            {
                File data=new File(TEST_DATA_DIR,"Oid5a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setModels("Oid5");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
        }
    }
    @Test
    public void importIliMetaAttr_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid3.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                //config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setIliMetaAttrsFile(new File(TEST_DATA_DIR,"Oid3.ini").getPath());
                config.setCreateMetaInfo(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassA1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassA1b'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("classa1b",rs.getString(2));
                }
                {
                    String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'Oid3.TestA.ClassB1'";
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
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
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
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                    HashMap<String,String> res=new HashMap<String,String>();
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    while(rs.next()) {
                        String tableName=rs.getString(1);
                        String oidDomain=rs.getString(2);
                        res.put(tableName,oidDomain);
                    }
                    Assert.assertEquals(1, res.size());
                    Assert.assertEquals("INTERLIS.UUIDOID",res.get("classa1"));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfMetaAttr_Smart0() throws Exception
    {
        {
            importIliMetaAttr_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Oid3a.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportBid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    File data=new File(TEST_DATA_DIR,"Oid3c.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportBid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // import-test: Oid1a.xtf
            Integer a1_tid=null;
            {
                String stmtTxt="SELECT classa1.t_id, classa1.t_ili_tid FROM "+setup.prefixName("classa1")+" WHERE classa1.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
                a1_tid=rs.getInt(1);
            }
            {
                String stmtTxt="SELECT classb1.t_id, classb1.t_ili_tid FROM "+setup.prefixName("classb1")+" WHERE classb1.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
            }
            // import-test_ Oid1c.xtf
            {
                String stmtTxt="SELECT classc1.t_id, classc1.a FROM "+setup.prefixName("classc1");
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals((int)a1_tid,rs.getInt(2));
                Assert.assertFalse(rs.next());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
    @Test
    public void exportXtfMetaAttr_Smart0() throws Exception
    {
        {
            importXtfMetaAttr_Smart0();
        }
        try{
            File data=new File(TEST_DATA_DIR,"Oid1a-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
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
        }
    }
	

    @Test
	public void importXtf_Smart0() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
		    setup.resetDb();
			{
				{
					File data=new File(TEST_DATA_DIR,"Oid1a.xtf");
		    		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	                Ili2db.setNoSmartMapping(config);
		    		config.setFunction(Config.FC_IMPORT);
		            config.setDoImplicitSchemaImport(true);
		    		config.setCreateFk(Config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		    		config.setImportBid(true);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					File data=new File(TEST_DATA_DIR,"Oid1c.xtf");
					Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	                Ili2db.setNoSmartMapping(config);
					config.setFunction(Config.FC_IMPORT);
					config.setCreateFk(Config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
			}
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
			// import-test: Oid1a.xtf
			Integer a1_tid=null;
			{
				String stmtTxt="SELECT classa1.t_id, classa1.t_ili_tid FROM "+setup.prefixName("classa1")+" WHERE classa1.t_ili_tid = 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc'";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("c34c86ec-2a75-4a89-a194-f9ebc422f8bc",rs.getString(2));
				a1_tid=rs.getInt(1);
			}
			{
				String stmtTxt="SELECT classb1.t_id, classb1.t_ili_tid FROM "+setup.prefixName("classb1")+" WHERE classb1.t_ili_tid = '81fc3941-01ec-4c51-b1ba-46b6295d9b4e'";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("81fc3941-01ec-4c51-b1ba-46b6295d9b4e",rs.getString(2));
			}
			// import-test_ Oid1c.xtf
			{
				String stmtTxt="SELECT classc1.t_id, classc1.a FROM "+setup.prefixName("classc1");
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals((int)a1_tid,rs.getInt(2));
                Assert.assertFalse(rs.next());
			}
		}finally{
            if(stmt!=null) {
                stmt.close();
            }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}	
	
	@Test
	public void exportXtf_Smart0() throws Exception
	{
	    {
	        importXtf_Smart0();
	    }
		try{
	        File data=new File(TEST_DATA_DIR,"Oid1a-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setBaskets("Oid1.TestA");
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
				 Assert.assertEquals("Oid1.TestA.ClassB1b", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Oid1.TestA.ClassA1", obj0.getobjecttag());
			 }
             {
                 IomObject obj0 = objs.get("b327aab8-1908-4dc0-aa96-ef1de78fffd6");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Oid1.TestA.ab2", obj0.getobjecttag());
             }
		}finally{
		}
	}
    @Test
    public void importIliExtendedTopic_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid2.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(null);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Oid2.TestD.a2b.a","a","classdb","classda"}                    
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                
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
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
            {
                String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_OIDDOMAIN+"' AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = '"+DbNames.T_ILI_TID_COL+"'";
                HashMap<String,String> res=new HashMap<String,String>();
                ResultSet rs=stmt.executeQuery(stmtTxt);
                while(rs.next()) {
                    String tableName=rs.getString(1);
                    String oidDomain=rs.getString(2);
                    res.put(tableName,oidDomain);
                }
                Assert.assertEquals(0, res.size());
                
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliExtendedTopicWoBasketCol_Smart1_fails() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Oid2.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(null);
                config.setBasketHandling(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                Assert.fail();
            }
        }catch(Ili2dbException ex) {
            Assert.assertEquals("Model Oid2 requires column T_basket",ex.getMessage());
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliStableBidWoBasketCol_Smart1_fails() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Bid5.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(null);
                config.setBasketHandling(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                Assert.fail();
            }
        }catch(Ili2dbException ex) {
            Assert.assertEquals("Model Bid5 requires column T_basket",ex.getMessage());
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfExtendedTopic_Smart1() throws Exception
    {
        {
            importIliExtendedTopic_Smart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"Oid2a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
                File data=new File(TEST_DATA_DIR,"Oid2b.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtfExtendedTopic_Smart1() throws Exception
    {
        {
            importXtfExtendedTopic_Smart1();
        }
        try{
            File data=new File(TEST_DATA_DIR,"Oid2-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
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
        }
    }
}