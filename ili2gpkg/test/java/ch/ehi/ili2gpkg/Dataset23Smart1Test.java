package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
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
public class Dataset23Smart1Test {
    String gpkgFileName = "test/data/Dataset23Smart1/Dataset23Smart1.gpkg";
    private static final String DATASETNAME_A = "Testset1";
    private static final String DATASETNAME_B = "Testset2";
    Connection jdbcConnection = null;

    @After
    public void endDb() throws Exception {
        if (jdbcConnection != null) {
            jdbcConnection.close();
        }
    }

    public Config initConfig(String xtfFilename, String dbschema, String logfile) {
        Config config = new Config();
        new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);

        config.setDbfile(gpkgFileName);
        config.setDburl("jdbc:sqlite:" + gpkgFileName);
        if (logfile != null) {
            config.setLogfile(logfile);
        }
        config.setXtffile(xtfFilename);
        if (xtfFilename != null && Ili2db.isItfFilename(xtfFilename)) {
            config.setItfTransferfile(true);
        }
        return config;
    }

    @Test
    public void importXtf() throws Exception {
        Connection jdbcConnection = null;
        try {
            File gpkgFile = new File(gpkgFileName);
            if (gpkgFile.exists()) {
                File file = new File(gpkgFile.getAbsolutePath());
                boolean fileDeleted = file.delete();
                Assert.assertTrue(fileDeleted);
            }

            {
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(config.CREATE_FK_YES);
                    config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset1b1.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    config.setDatasetName(DATASETNAME_B);
                    config.setFunction(Config.FC_IMPORT);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
            }
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importDatasetEmpty() throws Exception {
        Connection jdbcConnection = null;
        try {
            File gpkgFile = new File(gpkgFileName);
            if (gpkgFile.exists()) {
                File file = new File(gpkgFile.getAbsolutePath());
                boolean fileDeleted = file.delete();
                Assert.assertTrue(fileDeleted);
            }

            {
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset1c1.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(config.CREATE_FK_YES);
                    config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
            }
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void replaceDatasetEmpty() throws Exception {
        {
            importDatasetEmpty();
        }
        
        Connection jdbcConnection = null;
        try {

            {
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset1c2.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_REPLACE);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
            }
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void exportXtf() throws Exception {

        importXtf();

        try {

            File data = new File("test/data/Dataset23Smart1/Dataset1-export.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
            config.setDatasetName(DATASETNAME_A);
            config.setFunction(Config.FC_EXPORT);
            config.setModels("Dataset1");
            Ili2db.readSettingsFromDb(config);
            try {
                Ili2db.run(config, null);
            } catch (Exception e) {
                EhiLogger.logError(e);
                Assert.fail();
            }

            XtfReader reader = new XtfReader(data);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxEvent event = reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                objs.put(iomObj.getobjectoid(), iomObj);
            }
            event = reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                objs.put(iomObj.getobjectoid(), iomObj);

            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
            
            assertEquals(2,objs.size());
            for(IomObject iomObj:objs.values()) {
                assertTrue(iomObj.getattrvalue("attr1").equals("a1"));
            }

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void importMultipleDataset() throws Exception {
        Connection jdbcConnection = null;
        try {
            File gpkgFile = new File(gpkgFileName);
            if (gpkgFile.exists()) {
                File file = new File(gpkgFile.getAbsolutePath());
                boolean fileDeleted = file.delete();
                Assert.assertTrue(fileDeleted);
            }

            {
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset2a.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(config.CREATE_FK_YES);
                    config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
                {
                    File data = new File("test/data/Dataset23Smart1/Dataset2b.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
                    config.setDatasetName(DATASETNAME_B);
                    config.setFunction(Config.FC_IMPORT);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config, null);
                }
            }
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportMultipleDataset() throws Exception {

        importMultipleDataset();

        try {

            File data = new File("test/data/Dataset23Smart1/Dataset2-export.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
            config.setDatasetName(DATASETNAME_A + ch.interlis.ili2c.Main.MODELS_SEPARATOR + DATASETNAME_B);
            config.setFunction(Config.FC_EXPORT);
            config.setModels("Dataset2");
            Ili2db.readSettingsFromDb(config);
            try {
                Ili2db.run(config, null);
            } catch (Exception e) {
                EhiLogger.logError(e);
                Assert.fail();
            }
            
            HashMap<String,StartBasketEvent> baskets=new HashMap<String,StartBasketEvent>();
            XtfReader reader = new XtfReader(data);
            IoxEvent event = reader.read();
            while(!(event instanceof EndTransferEvent)) {
                if(event instanceof StartBasketEvent) {
                    baskets.put(((StartBasketEvent) event).getBid(),(StartBasketEvent) event);
                }
                
                event = reader.read();
            }
            assertEquals(4,baskets.size());
            assertEquals("Dataset2.TestA",baskets.get("c34c86ec-2a75-4a89-a194-f9ebc422f8bc").getType());
            assertEquals("Dataset2.TestA",baskets.get("46b78f2e-2402-4600-8a26-b220e0950800").getType());
            assertEquals("Dataset2.TestA",baskets.get("1aa57314-dca3-4add-a6e6-00f02ab165c1").getType());
            assertEquals("Dataset2.TestA",baskets.get("23b08e5a-07d8-4e85-9f83-1b449bdae961").getType());

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
