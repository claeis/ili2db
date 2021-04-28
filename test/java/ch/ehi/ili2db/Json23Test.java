package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
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
public abstract class Json23Test {
    protected static final String TEST_OUT="test/data/Json/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
    @Test
    public void importIli() throws Exception
    {
        Class driverClass = Class.forName("org.postgresql.Driver");
        Connection jdbcConnection=null;
        try {
            setup.resetDb();
            jdbcConnection=setup.createConnection();
            
            File data=new File(TEST_OUT,"Json23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(null);
            config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
            Ili2db.run(config,null);
            
            // asserts
            {
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Json23.TestA.Farbe.r",  "r", "farbe" ,null },
                        {"Json23.TestA.Auto.Farben",  "farben",    "auto",null },  
                        {"Json23.TestA.Farbe.active", "aactive",    "farbe" ,null },
                        {"Json23.TestA.Farbe.g",  "g", "farbe" ,null },
                        {"Json23.TestA.Farbe.name",   "aname", "farbe" ,null },
                        {"Json23.TestA.Farbe.b",  "b", "farbe"                 ,null },        
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Json23.TestA.Farbe",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Json23.TestA.Auto.Farben",  "ch.ehi.ili2db.jsonTrafo",   "coalesce"},
                        {"Json23.TestA.Auto", "ch.ehi.ili2db.inheritance", "newClass"}
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
        //EhiLogger.getInstance().setTraceFilter(false);
        Class driverClass = Class.forName("org.postgresql.Driver");
        setup.resetDb();
        
        File data=new File(TEST_OUT,"Json23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setImportBid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setCatalogueRefTrafo(null);
        config.setMultiSurfaceTrafo(null);
        config.setMultilingualTrafo(null);
        config.setInheritanceTrafo(null);
        config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            Connection jdbcConnection=null;
            try{
                jdbcConnection=setup.createConnection();
                java.sql.Statement stmt=jdbcConnection.createStatement();
                importXtf_doAsserts(stmt);
            }finally {
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
            rs=stmt.executeQuery("SELECT farben->0->>'@type',farben->0->'r' FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='1'");
            assertTrue(rs.next());
            assertEquals("Json23.TestA.Farbe",rs.getString(1));
            assertEquals(10,rs.getInt(2));
            rs=stmt.executeQuery("SELECT cast(farben as text) FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='2'");
            assertTrue(rs.next());
            rs.getString(1);
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
        File data=new File(TEST_OUT,"Json23a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("Json23");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            XtfReader reader=new XtfReader(data);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxEvent event=reader.read();
            while(event instanceof ObjectEvent) {
                IomObject iomObj=((ObjectEvent)event).getIomObject();
                objs.put(iomObj.getobjectoid(),iomObj);
                event=reader.read();
            }
            assertEquals(2,objs.size());
            assertEquals("Json23.TestA.Auto oid 1 {Farben [Json23.TestA.Farbe {active false, b 12, g 11, name f1, r 10}, Json23.TestA.Farbe {active false, b 22, g 21, name f2, r 20}]}",objs.get("1").toString());
            assertEquals("Json23.TestA.Auto oid 2 {}",objs.get("2").toString());
            assertTrue(event instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
            reader.close();
        }
    }
	
}