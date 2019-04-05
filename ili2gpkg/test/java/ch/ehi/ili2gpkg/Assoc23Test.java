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

public class Assoc23Test {

    private String gpkgFileName = "test/data/Assoc23/Assoc3.gpkg";
    private static final String DATASETNAME_A = "Testset1";
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
    public void importXtf_fail() throws Exception {
        Connection jdbcConnection = null;
        try {
            File gpkgFile = new File(gpkgFileName);
            if (gpkgFile.exists()) {
                File file = new File(gpkgFile.getAbsolutePath());
                boolean fileDeleted = file.delete();
                Assert.assertTrue(fileDeleted);
            }
            
            LogCollector logCollector = new LogCollector();
            EhiLogger.getInstance().addListener(logCollector);
            File data = new File("test/data/Assoc23/Assoc3a.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
            config.setDatasetName(DATASETNAME_A);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setValidation(false);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
            Ili2db.readSettingsFromDb(config);
            try {
                Ili2db.run(config, null);
                Assert.fail();
            } catch (Exception e) {
                final String errorTxt1 = "unknown referenced object Assoc3.Test.ClassA1 TID a1_fail referenced from Assoc3.Test.ClassB1 TID b1";
                final String errorTxt2 = "unknown referenced object Assoc3.Test.ClassA1 TID a1_fail referenced from Assoc3.Test.assocab2 TID a1_fail:b1";
                final String errorTxt3 = "failed to transfer data from file to db";
            int counter = 0;
                for (LogEvent event : logCollector.getErrs()) {
                    if (event.getEventMsg().equals(errorTxt1)     || 
                            event.getEventMsg().equals(errorTxt2) || 
                            event.getEventMsg().equals(errorTxt3)) {
                        counter++;
                    }
                }
                Assert.assertEquals(3, counter);
            }
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfSkipReferenceErrors() throws Exception {
        Connection jdbcConnection = null;
        try {
            File gpkgFile = new File(gpkgFileName);
            if (gpkgFile.exists()) {
                File file = new File(gpkgFile.getAbsolutePath());
                boolean fileDeleted = file.delete();
                Assert.assertTrue(fileDeleted);
            }
            
            LogCollector logCollector = new LogCollector();
            EhiLogger.getInstance().addListener(logCollector);
            File data = new File("test/data/Assoc23/Assoc3a.xtf");
            Config config = initConfig(data.getPath(), null, data.getPath() + ".log");
            config.setDatasetName(DATASETNAME_A);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setValidation(false);
            config.setSkipReferenceErrors(true);
            config.setSqlNull(Config.SQL_NULL_ENABLE);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            config.setCatalogueRefTrafo(null);
            config.setMultiSurfaceTrafo(null);
            config.setMultilingualTrafo(null);
            config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

}