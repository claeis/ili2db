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

public abstract class MultiPolyline24Test {

    protected static final String TEST_OUT = "test/data/MultiPolyline24/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiPolyline24.xtf");
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

            assertMultiPolyline24_classa1_geomattr1(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    protected abstract void assertMultiPolyline24_classa1_geomattr1(Statement stmt) throws Exception;

    @Test
    public void exportXtf() throws Exception {
        importXtf();

        // export xtf
        File data = new File(TEST_OUT, "MultiPolyline24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("MultiPolyline24");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "/MultiPolyline24.ili", FileEntryKind.ILIMODELFILE);
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
        assertEquals("MultiPolyline24.TestA.ClassA1", iomObj.getobjecttag());

        assertEquals(1, iomObj.getattrcount());
        assertEquals(1, iomObj.getattrvaluecount("geomAttr1"));

        IomObject multiPolylineAttr = iomObj.getattrobj("geomAttr1", 0);
        assertNotNull(multiPolylineAttr);

        IomObject polyline1 = multiPolylineAttr.getattrobj("polyline", 0);
        assertNotNull(polyline1);

        IomObject sequence1 = polyline1.getattrobj("sequence", 0);
        assertNotNull(sequence1);

        IomObject coord1 = sequence1.getattrobj("segment", 0);
        assertNotNull(coord1);
        assertEquals("480000.111", coord1.getattrvalue("C1"));
        assertEquals("70000.111", coord1.getattrvalue("C2"));
        assertEquals("4000.111", coord1.getattrvalue("C3"));

        IomObject coord2 = sequence1.getattrobj("segment", 1);
        assertNotNull(coord2);
        assertEquals("480000.222", coord2.getattrvalue("C1"));
        assertEquals("70000.222", coord2.getattrvalue("C2"));
        assertEquals("4000.222", coord2.getattrvalue("C3"));

        IomObject coord3 = sequence1.getattrobj("segment", 2);
        assertNotNull(coord3);
        assertEquals("480000.333", coord3.getattrvalue("C1"));
        assertEquals("70000.333", coord3.getattrvalue("C2"));
        assertEquals("4000.333", coord3.getattrvalue("C3"));

        IomObject polyline2 = multiPolylineAttr.getattrobj("polyline", 1);
        assertNotNull(polyline2);

        IomObject sequence2 = polyline2.getattrobj("sequence", 0);
        assertNotNull(sequence2);

        IomObject coord4 = sequence2.getattrobj("segment", 0);
        assertNotNull(coord4);
        assertEquals("480000.444", coord4.getattrvalue("C1"));
        assertEquals("70000.444", coord4.getattrvalue("C2"));
        assertEquals("4000.444", coord4.getattrvalue("C3"));

        IomObject coord5 = sequence2.getattrobj("segment", 1);
        assertNotNull(coord5);
        assertEquals("480000.555", coord5.getattrvalue("C1"));
        assertEquals("70000.555", coord5.getattrvalue("C2"));
        assertEquals("4000.555", coord5.getattrvalue("C3"));

        IomObject coord6 = sequence2.getattrobj("segment", 2);
        assertNotNull(coord6);
        assertEquals("480000.666", coord6.getattrvalue("C1"));
        assertEquals("70000.666", coord6.getattrvalue("C2"));
        assertEquals("4000.666", coord6.getattrvalue("C3"));

        assertTrue(reader.read() instanceof EndBasketEvent);
        assertTrue(reader.read() instanceof EndTransferEvent);
    }

    @Test
    public void importIli() throws SQLException, Ili2dbException {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiPolyline24.ili");
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
                        {"MultiPolyline24.TestA.ClassA1.geomAttr1", "geomattr1", "classa1", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String[][] expectedValues = new String[][]{
                        {"MultiPolyline24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
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
