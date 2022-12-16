package ch.ehi.ili2db;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iox.*;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public abstract class MultiCoord24Test {

    protected static final String TEST_OUT = "test/data/MultiCoord24/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiCoord24.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsCode("2056");
        setup.setXYParams(config);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertMultiChoord24_classa1_geomattr1(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    protected abstract void assertMultiChoord24_classa1_geomattr1(Statement stmt) throws Exception;

    @Test
    public void exportXtf() throws Exception {
        importXtf();

        // export xtf
        File data = new File(TEST_OUT, "MultiCoord24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("MultiCoord24");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "/MultiCoord24.ili", FileEntryKind.ILIMODELFILE);
        ili2cConfig.addFileEntry(fileEntry);
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);

        // assert xtf
        Xtf24Reader reader = new Xtf24Reader(data);
        reader.setModel(td);

        assertTrue(reader.read() instanceof StartTransferEvent);
        assertTrue(reader.read() instanceof StartBasketEvent);

        IoxEvent event = reader.read();
        assertTrue(event instanceof ObjectEvent);
        IomObject iomObj = ((ObjectEvent) event).getIomObject();
        assertEquals(1, iomObj.getattrcount());
        assertEquals("MultiCoord24.TestA.ClassA1", iomObj.getobjecttag());

        assertEquals(1, iomObj.getattrcount());
        assertEquals(1, iomObj.getattrvaluecount("geomAttr1"));

        IomObject multiCoordAttr = iomObj.getattrobj("geomAttr1", 0);
        assertNotNull(multiCoordAttr);

        IomObject coord1 = multiCoordAttr.getattrobj("coord", 0);
        assertNotNull(coord1);
        assertEquals("2530001.0", coord1.getattrvalue("C1"));
        assertEquals("1150002.0", coord1.getattrvalue("C2"));
        assertNull(coord1.getattrvalue("C3"));

        IomObject coord2 = multiCoordAttr.getattrobj("coord", 1);
        assertNotNull(coord2);
        assertEquals("2740003.0", coord2.getattrvalue("C1"));
        assertEquals("1260004.0", coord2.getattrvalue("C2"));
        assertNull(coord2.getattrvalue("C3"));

        assertTrue(reader.read() instanceof EndBasketEvent);
        assertTrue(reader.read() instanceof EndTransferEvent);
    }

    @Test
    public void importIli() throws SQLException, Ili2dbException {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiCoord24.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setCreateNumChecks(true);
        config.setCreateDateTimeChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String[][] expectedValues = new String[][]{
                        {"MultiCoord24.TestA.ClassA1.geomAttr1", "geomattr1", "classa1", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String[][] expectedValues = new String[][]{
                        {"MultiCoord24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues, setup.getSchema());
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
}
