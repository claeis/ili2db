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
public abstract class Json24Test {
    protected static final String TEST_OUT="test/data/Json/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
    @Test
    public void importIli() throws Exception
    {
        Connection jdbcConnection=null;
        try {
            setup.resetDb();
            
            File data=new File(TEST_OUT,"Json24.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
            Ili2db.run(config,null);
            
            jdbcConnection=setup.createConnection();
            // asserts
            {
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Json24.TestA.Farbe.r",  "r", "farbe" ,null },
                        {"Json24.TestA.Auto.Farben",  "farben",    "auto",null },  
                        {"Json24.TestA.Auto.Farbe",  "farbe",    "auto",null },  
                        {"Json24.TestA.Auto.Nummer",  "nummer",    "auto",null },  
                        {"Json24.TestA.Farbe.active", "aactive",    "farbe" ,null },
                        {"Json24.TestA.Farbe.g",  "g", "farbe" ,null },
                        {"Json24.TestA.Farbe.name",   "aname", "farbe" ,null },
                        {"Json24.TestA.Farbe.b",  "b", "farbe"                 ,null },        
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Json24.TestA.Farbe",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Json24.TestA.Auto.Farben",  "ch.ehi.ili2db.jsonTrafo",   "coalesce"},
                        {"Json24.TestA.Auto.Farbe",  "ch.ehi.ili2db.jsonTrafo",   "coalesce"},
                        {"Json24.TestA.Auto.Nummer",  "ch.ehi.ili2db.jsonTrafo",   "coalesce"},
                        {"Json24.TestA.Auto", "ch.ehi.ili2db.inheritance", "newClass"}
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
        
        File data=new File(TEST_OUT,"Json24a.xtf");
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
        config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
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
        
    }
    @Test
    public void exportXtf() throws Exception
    {
        {
            importXtf();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"Json24a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("Json24");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            Configuration ili2cConfig = new Configuration();
            FileEntry fileEntry = new FileEntry(TEST_OUT + "Json24.ili", FileEntryKind.ILIMODELFILE);
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
            assertEquals("Json24.TestA.Auto oid 1 {Farben [Json24.TestA.Farbe {active false, b 12, g 11, name f1, r 10}, Json24.TestA.Farbe {active false, b 22, g 21, name f2, r 20}], Nummer [10, 12]}",objs.get("1").toString());
            assertEquals("Json24.TestA.Auto oid 2 {}",objs.get("2").toString());
            assertEquals("Json24.TestA.Auto oid 3 {Farbe Json24.TestA.Farbe {active false, b 22, g 21, name f2, r 20}, Farben Json24.TestA.Farbe {active false, b 12, g 11, name f1, r 10}, Nummer 1}",objs.get("3").toString());
            assertTrue(event instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
            reader.close();
        }
    }
	
}