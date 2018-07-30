package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;

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
    public void importDataset() throws Exception {
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
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setCreateFk(config.CREATE_FK_YES);
                    config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                    config.setCatalogueRefTrafo(null);
                    config.setMultiSurfaceTrafo(null);
                    config.setMultilingualTrafo(null);
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
    public void exportDataset() throws Exception {

        importDataset();

        try {

            File data = new File("test/data/Dataset23Smart1/Dataset1-export.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
            config.setDatasetName(DATASETNAME_A + ch.interlis.ili2c.Main.MODELS_SEPARATOR + DATASETNAME_B);
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
            IoxEvent event = reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                String attrtag = iomObj.getobjecttag();
                assertEquals("5", iomObj.getobjectoid());
                assertTrue(iomObj.getattrvalue("attr1").equals("a1"));
                assertEquals("Dataset1.TestA.ClassA1", attrtag);
            }
            event = reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                assertEquals("7", iomObj.getobjectoid());
                assertTrue(iomObj.getattrvalue("attr1").equals("a1"));

                assertTrue(reader.read() instanceof EndBasketEvent);
            }

            {
                System.out.println("Stop!");
                assertTrue(reader.read() instanceof StartBasketEvent);
                IoxEvent objectEvent = reader.read();
                {
                    assertTrue(objectEvent instanceof ObjectEvent);
                    IomObject iomObj = ((ObjectEvent) objectEvent).getIomObject();
                    String attrtag = iomObj.getobjecttag();
                    assertEquals("Dataset1.TestA.ClassA1", attrtag);
                    assertEquals("26", iomObj.getobjectoid());
                }
            }
            event = reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                assertEquals("28", iomObj.getobjectoid());
                assertTrue(iomObj.getattrvalue("attr1").equals("b1"));

                assertTrue(reader.read() instanceof EndBasketEvent);
                assertTrue(reader.read() instanceof EndTransferEvent);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
