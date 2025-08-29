package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public abstract class ExpandStruct24Test {
    private static final String NUMMER1 = "nummer1";
    private static final String NUMMER0 = "nummer0";
    private static final String FARBEN1_B = "farben1_b";
    private static final String FARBEN1_G = "farben1_g";
    private static final String FARBEN1_R = "farben1_r";
    private static final String FARBEN1_T_TYPE = "farben1_t_type";
    private static final String FARBEN0_B = "farben0_b";
    private static final String FARBEN0_G = "farben0_g";
    private static final String FARBEN0_R = "farben0_r";
    private static final String FARBEN0_T_TYPE = "farben0_t_type";
    protected static final String TEST_OUT="test/data/ExpandStruct/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
    @Test
    public void importIli() throws Exception
    {
        Connection jdbcConnection=null;
        try {
            setup.resetDb();
            
            File data=new File(TEST_OUT,"ExpandStruct24.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setStructTrafo(Config.STRUCT_TRAFO_EXPAND);
            Ili2db.run(config,null);
            
            jdbcConnection=setup.createConnection();
            // asserts
            {
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"ExpandStruct24.TestA.Farbe.r",          "r",        "farbe",null},
                        {"ExpandStruct24.TestA.Farbe.b",          "b",        "farbe",null},
                        {"ExpandStruct24.TestA.Farbe.g",          "g",        "farbe",null},
                        {"ExpandStruct24.TestA.Auto.Farben[0]._type", "farben0_t_type","auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[0].r", FARBEN0_R,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[0].g", FARBEN0_G,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[0].b", FARBEN0_B,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[1]._type", "farben1_t_type","auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[1].r", FARBEN1_R,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[1].g", FARBEN1_G,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Farben[1].b", FARBEN1_B,"auto",null},
                        {"ExpandStruct24.TestA.Auto.Nummer[0]",   NUMMER0,  "auto",null},
                        {"ExpandStruct24.TestA.Auto.Nummer[1]",   NUMMER1,  "auto",null}
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"ExpandStruct24.TestA.Farbe",       "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ExpandStruct24.TestA.Auto",        "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ExpandStruct24.TestA.Auto.Farben", "ch.ehi.ili2db.structTrafo", "expand"},
                        {"ExpandStruct24.TestA.Auto.Nummer", "ch.ehi.ili2db.arrayTrafo",  "expand"}
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
                }
                
            }
            
        }finally {
            if(jdbcConnection!=null) {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    
    
    @Test
    public void importXtf() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        
        File data=new File(TEST_OUT,"ExpandStruct24a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setImportBid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setStructTrafo(Config.STRUCT_TRAFO_EXPAND);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            Connection jdbcConnection=null;
            Statement stmt=null;
            try{
                jdbcConnection=setup.createConnection();
                stmt=jdbcConnection.createStatement();
                importXtf_doAsserts(stmt);
            }finally {
                if (stmt!=null) {
                    stmt.close();
                    stmt = null;
                }
                if(jdbcConnection!=null) {
                    jdbcConnection.close();
                    jdbcConnection=null;
                }
            }
        }
    }

    protected void importXtf_doAsserts(java.sql.Statement stmt) throws SQLException {
        java.sql.ResultSet rs=null;
        try {
            String s=FARBEN0_T_TYPE+","+FARBEN0_R+","+FARBEN0_G+","+FARBEN0_B+","+FARBEN1_T_TYPE+","+FARBEN1_R+","+FARBEN1_G+","+FARBEN1_B+","+NUMMER0+","+NUMMER1;
            rs=stmt.executeQuery("SELECT "+s+" FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='1'");
            assertTrue(rs.next());
            assertEquals("farbe",rs.getString(FARBEN0_T_TYPE));
            assertEquals(10,rs.getInt(FARBEN0_R));
            assertEquals(11,rs.getInt(FARBEN0_G));
            assertEquals(12,rs.getInt(FARBEN0_B));
            assertEquals("farbe",rs.getString(FARBEN1_T_TYPE));
            assertEquals(1,rs.getInt(FARBEN1_R));
            assertEquals(1,rs.getInt(FARBEN1_G));
            assertEquals(22,rs.getInt(FARBEN1_B));
            assertEquals(10,rs.getInt(NUMMER0));
            assertEquals(12,rs.getInt(NUMMER1));
            
            rs=stmt.executeQuery("SELECT "+s+" FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='2'");
            assertTrue(rs.next());
            rs.getString(FARBEN0_T_TYPE);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN0_R);
            assertEquals(true,rs.wasNull());
            rs.getString(FARBEN1_T_TYPE);
            assertEquals(true,rs.wasNull());

            rs=stmt.executeQuery("SELECT "+s+" FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='3'");
            assertTrue(rs.next());
            assertEquals("farbe",rs.getString(FARBEN0_T_TYPE));
            rs.getInt(FARBEN0_R);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN0_G);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN0_B);
            assertEquals(true,rs.wasNull());
            rs.getString(FARBEN1_T_TYPE);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN1_R);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN1_G);
            assertEquals(true,rs.wasNull());
            rs.getInt(FARBEN1_B);
            assertEquals(true,rs.wasNull());
            assertEquals(1,rs.getInt(NUMMER0));
            rs.getInt(NUMMER1);
            assertEquals(true,rs.wasNull());
            
        }finally {
            if(rs!=null) {
                rs.close();
                rs=null;
            }
        }
    }
    @Test
    public void exportXtf() throws Exception
    {
        {
            importXtf();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExpandStruct24a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("ExpandStruct24");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            Configuration ili2cConfig = new Configuration();
            FileEntry fileEntry = new FileEntry(TEST_OUT + "ExpandStruct24.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
            TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
            assertNotNull(td);
            
            Xtf24Reader reader=new Xtf24Reader(data);
            reader.setModel(td);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxEvent event=reader.read();
            while(event instanceof ObjectEvent) {
                IomObject iomObj=((ObjectEvent)event).getIomObject();
                objs.put(iomObj.getobjectoid(),iomObj);
                event=reader.read();
            }
            assertEquals(3,objs.size());
            assertEquals("ExpandStruct24.TestA.Auto oid 1 {Farben [ExpandStruct24.TestA.Farbe {b 12, g 11, r 10}, ExpandStruct24.TestA.Farbe {b 22, g 1, r 1}], Nummer [10, 12]}",objs.get("1").toString());
            assertEquals("ExpandStruct24.TestA.Auto oid 2 {}",objs.get("2").toString());
            assertEquals("ExpandStruct24.TestA.Auto oid 3 {Farben ExpandStruct24.TestA.Farbe {}, Nummer 1}",objs.get("3").toString());
            assertTrue(event instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
            reader.close();
        }
    }
	
}