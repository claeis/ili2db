package ch.ehi.ili2gpkg;

import java.io.File;
import java.sql.Connection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.ili2db.LogCollector;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class ObjectStatisticsTest {
    String gpkgFileName = "test/data/Logging/Logging.gpkg";
    private static final String DATASETNAME_A = "Testset1";
    private static final String DATASETNAME_B = "Testset2";

    Connection jdbcConnection = null;

    @After
    public void endDb() throws Exception {
        if (jdbcConnection != null) {
            jdbcConnection.close();
        }
    }

    public Config initConfig(String xtfFilename, String dbschema, String logfile, String gpkgFile) {
        Config config = new Config();
        new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);

        config.setDbfile(gpkgFile);
        config.setDburl("jdbc:sqlite:" + gpkgFile);
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
                LogCollector logCollector = null;
                {
                    logCollector = new LogCollector();
                    EhiLogger.getInstance().addListener(logCollector);
                    File data = new File("test/data/Logging/Logging-a.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log", gpkgFileName);
                    Ili2db.setNoSmartMapping(config);
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(Config.CREATE_FK_YES);
                    config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                    config.setImportBid(true);
                    config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                    config.setCreateImportTabs(true);
                    Ili2db.readSettingsFromDb(config);
                    config.setValidation(false);
                    try {
                        Ili2db.run(config, null);
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    int counter = 0;
                    for (LogEvent event : logCollector.getWarn()) {
                        String msg = trim(event.getEventMsg());
                        if (msg.equals("Logging-a.xtf: Abl_22.TopicA BID=GdeA.TopicA")
                                || msg.equals("Logging-a.xtf: Abl_22.TopicB BID=GdeA.TopicB")
                                || msg.equals("1 objects in CLASS Abl_22.TopicA.Gebaeude")
                                || msg.equals("2 objects in CLASS Abl_22.TopicB.Grundstueck")) {
                            counter++;
                        }
                    }
                    Assert.assertEquals(4, counter);
                }
                {
                    logCollector = new LogCollector();
                    EhiLogger.getInstance().addListener(logCollector);
                    File data = new File("test/data/Logging/Logging-b.xtf");
                    Config config = initConfig(data.getPath(), null, data.getPath() + ".log", gpkgFileName);
                    config.setDatasetName(DATASETNAME_B);
                    config.setFunction(Config.FC_IMPORT);
                    config.setValidation(false);
                    config.setImportBid(true);
                    Ili2db.readSettingsFromDb(config);
                    try {
                        Ili2db.run(config, null);
                    } catch (Exception e) {
                        Assert.fail(e.getMessage());
                    }
                    int counter = 0;
                    for (LogEvent event : logCollector.getWarn()) {
                        if (trim(event.getEventMsg()).equals("Logging-b.xtf: Abl_22.TopicA BID=GdeB.TopicA")
                                || trim(event.getEventMsg()).equals("1 objects in CLASS Abl_22.TopicA.Gebaeude")) {
                            counter++;
                        }
                    }
                    Assert.assertEquals(2, counter);
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage()); 
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void exportMultipleDataset() throws Exception {

        importDataset();

        LogCollector logCollector = null;
        try {
            logCollector = new LogCollector();
            EhiLogger.getInstance().addListener(logCollector);
            File data = new File("test/data/Logging/Logging-out.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log", gpkgFileName);
            config.setDatasetName(DATASETNAME_A + ch.interlis.ili2c.Main.MODELS_SEPARATOR + DATASETNAME_B);
            config.setFunction(Config.FC_EXPORT);
            config.setModels("Abl_22");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            try {
                Ili2db.run(config, null);
            } catch (Exception e) {
                EhiLogger.logError(e);
                Assert.fail();
            }
            int counter = 0;
            for (LogEvent event : logCollector.getWarn()) {
                if (event.getEventMsg().equals("Logging-out.xtf: Abl_22.TopicA BID=GdeA.TopicA")
                        || event.getEventMsg().equals("Logging-out.xtf: Abl_22.TopicA BID=GdeB.TopicA")
                        || event.getEventMsg().equals("Logging-out.xtf: Abl_22.TopicB BID=GdeA.TopicB")) {
                    counter++;
                }
            }
            Assert.assertEquals(3, counter);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    private static String trim(String value)
    {
        if(value==null)return null;
        String ret=value.replace(Ili2db.NO_BREAK_SPACE,' ');
        return ret.trim();
        
    }
}