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

public abstract class MultiSurfaceArea24Test {

    protected static final String TEST_OUT = "test/data/MultiSurfaceArea24/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiSurfaceArea24.xtf");
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

            assertMultiSurfaceArea24_classa12_geomattr(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    protected abstract void assertMultiSurfaceArea24_classa12_geomattr(Statement stmt) throws Exception;

    @Test
    public void exportXtf() throws Exception {
        importXtf();

        // export xtf
        File data = new File(TEST_OUT, "MultiSurfaceArea24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("MultiSurfaceArea24");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "/MultiSurfaceArea24.ili", FileEntryKind.ILIMODELFILE);
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
        assertEquals("MultiSurfaceArea24.TestA.ClassA1", iomObj.getobjecttag());
        assertMultisurfaceOrAreaAttribute(iomObj);

        event = reader.read();
        assertTrue(event instanceof ObjectEvent);
        iomObj = ((ObjectEvent) event).getIomObject();
        assertEquals("MultiSurfaceArea24.TestA.ClassA2", iomObj.getobjecttag());
        assertMultisurfaceOrAreaAttribute(iomObj);

        assertTrue(reader.read() instanceof EndBasketEvent);
        assertTrue(reader.read() instanceof EndTransferEvent);
    }

    @Test
    public void importIli() throws SQLException, Ili2dbException {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiSurfaceArea24.ili");
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
                        {"MultiSurfaceArea24.TestA.ClassA1.geomAttr1", "geomattr1", "classa1", null},
                        {"MultiSurfaceArea24.TestA.ClassA2.geomAttr1", "geomattr1", "classa2", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String[][] expectedValues = new String[][]{
                        {"MultiSurfaceArea24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"MultiSurfaceArea24.TestA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
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

    private void assertMultisurfaceOrAreaAttribute(IomObject iomObj){
        assertEquals(1, iomObj.getattrcount());
        assertEquals(1, iomObj.getattrvaluecount("geomAttr1"));

        IomObject multiSurfaceAreaAttr = iomObj.getattrobj("geomAttr1", 0);
        assertNotNull(multiSurfaceAreaAttr);
        for (int i = 0; i < 2; i++) {
            String x=i==0?"480000":"490000";
            IomObject surface = multiSurfaceAreaAttr.getattrobj("surface", i);

            assertEquals(1, surface.getattrcount());
            assertEquals(2, surface.getattrvaluecount("boundary"));
            IomObject outerBoundary = surface.getattrobj("boundary",0);
            assertNotNull(outerBoundary);
            IomObject innerBoundary = surface.getattrobj("boundary",1);
            assertNotNull(innerBoundary);

            assertEquals(1, outerBoundary.getattrcount());
            assertEquals(1, outerBoundary.getattrvaluecount("polyline"));
            IomObject outerPolyline = outerBoundary.getattrobj("polyline",0);
            assertNotNull(outerPolyline);

            assertEquals(1, innerBoundary.getattrcount());
            assertEquals(1, innerBoundary.getattrvaluecount("polyline"));
            IomObject innerPolyline = innerBoundary.getattrobj("polyline",0);
            assertNotNull(innerPolyline);

            assertEquals(1, outerPolyline.getattrcount());
            assertEquals(1, outerPolyline.getattrvaluecount("sequence"));
            IomObject outerSequence = outerPolyline.getattrobj("sequence",0);
            assertNotNull(outerSequence);

            assertEquals(1, innerPolyline.getattrcount());
            assertEquals(1, innerPolyline.getattrvaluecount("sequence"));
            IomObject innerSequence = innerPolyline.getattrobj("sequence",0);
            assertNotNull(innerSequence);

            assertEquals(1, outerSequence.getattrcount());
            assertEquals(4, outerSequence.getattrvaluecount("segment"));
            {
                IomObject coord1 = outerSequence.getattrobj("segment", 0);
                assertNotNull(coord1);
                IomObject coord2 = outerSequence.getattrobj("segment", 1);
                assertNotNull(coord2);
                IomObject coord3 = outerSequence.getattrobj("segment", 2);
                assertNotNull(coord3);
                IomObject coord4 = outerSequence.getattrobj("segment", 3);
                assertNotNull(coord4);

                assertEquals(x+".111", coord1.getattrvalue("C1"));
                assertEquals("70000.111", coord1.getattrvalue("C2"));
                assertEquals("5000.111", coord1.getattrvalue("C3"));

                assertEquals(x+".999", coord2.getattrvalue("C1"));
                assertEquals("70000.111", coord2.getattrvalue("C2"));
                assertEquals("5000.111", coord2.getattrvalue("C3"));

                assertEquals(x+".999", coord3.getattrvalue("C1"));
                assertEquals("70000.999", coord3.getattrvalue("C2"));
                assertEquals("5000.111", coord3.getattrvalue("C3"));

                assertEquals(x+".111", coord4.getattrvalue("C1"));
                assertEquals("70000.111", coord4.getattrvalue("C2"));
                assertEquals("5000.111", coord4.getattrvalue("C3"));
                
            }
            
            assertEquals(1, innerSequence.getattrcount());
            assertEquals(4, innerSequence.getattrvaluecount("segment"));
            {
                IomObject coord4 = innerSequence.getattrobj("segment", 0);
                assertNotNull(coord4);
                IomObject coord5 = innerSequence.getattrobj("segment", 1);
                assertNotNull(coord5);
                IomObject coord6 = innerSequence.getattrobj("segment", 2);
                assertNotNull(coord6);
                IomObject coord7 = innerSequence.getattrobj("segment", 3);
                assertNotNull(coord7);

                assertEquals(x+".555", coord4.getattrvalue("C1"));
                assertEquals("70000.222", coord4.getattrvalue("C2"));
                assertEquals("5000.111", coord4.getattrvalue("C3"));

                assertEquals(x+".666", coord5.getattrvalue("C1"));
                assertEquals("70000.222", coord5.getattrvalue("C2"));
                assertEquals("5000.111", coord5.getattrvalue("C3"));

                assertEquals(x+".666", coord6.getattrvalue("C1"));
                assertEquals("70000.666", coord6.getattrvalue("C2"));
                assertEquals("5000.111", coord6.getattrvalue("C3"));

                assertEquals(x+".555", coord7.getattrvalue("C1"));
                assertEquals("70000.222", coord7.getattrvalue("C2"));
                assertEquals("5000.111", coord7.getattrvalue("C3"));
                
            }
        }
    }
}
