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
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public abstract class GenericValueRanges24Test {

    protected static final String TEST_OUT = "test/data/GenericValueRanges24/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "GenericValueRanges24.xtf");
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
        config.setOneGeomPerTable(true);
        setup.setXYParams(config);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertAttrCoord(stmt);
            assertAttrMultiCoord(stmt);
            assertAttrLine(stmt);
            assertAttrMultiLine(stmt);
            assertAttrSurface(stmt);
            assertAttrMultiSurface(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    protected abstract void assertAttrCoord(Statement stmt) throws Exception;
    protected abstract void assertAttrMultiCoord(Statement stmt) throws Exception;
    protected abstract void assertAttrLine(Statement stmt) throws Exception;
    protected abstract void assertAttrMultiLine(Statement stmt) throws Exception;
    protected abstract void assertAttrSurface(Statement stmt) throws Exception;
    protected abstract void assertAttrMultiSurface(Statement stmt) throws Exception;

    @Test
    public void exportXtf() throws Exception {
        importXtf();

        // export xtf
        File data = new File(TEST_OUT, "GenericValueRanges24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("ModelA");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "/GenericValueRanges24.ili", FileEntryKind.ILIMODELFILE);
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
        assertEquals("ModelA.TopicA.ClassA", iomObj.getobjecttag());

        assertEquals(8, iomObj.getattrcount());
        assertEquals(1, iomObj.getattrvaluecount("AttrCoord"));

        IomObject attrCoord = iomObj.getattrobj("AttrCoord", 0);
        assertNotNull(attrCoord);
        assertEquals("COORD", attrCoord.getobjecttag());
        assertEquals("2530001.000", attrCoord.getattrvalue("C1"));
        assertEquals("1150002.000", attrCoord.getattrvalue("C2"));
        assertNull(attrCoord.getattrvalue("C3"));

        assertAttributeType(iomObj, "AttrMultiCoord", "MULTICOORD");
        assertAttributeType(iomObj, "AttrLine", "POLYLINE");
        assertAttributeType(iomObj, "AttrMultiLine", "MULTIPOLYLINE");
        assertAttributeType(iomObj, "AttrSurface", "MULTISURFACE");
        assertAttributeType(iomObj, "AttrMultiSurface", "MULTISURFACE");
        assertAttributeType(iomObj, "AttrArea", "MULTISURFACE");
        assertAttributeType(iomObj, "AttrMultiArea", "MULTISURFACE");

        assertTrue(reader.read() instanceof EndBasketEvent);
        assertTrue(reader.read() instanceof EndTransferEvent);
    }

    @Test
    public void importIli() throws SQLException, Ili2dbException {
        setup.resetDb();
        File data = new File(TEST_OUT, "GenericValueRanges24.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setCreateNumChecks(true);
        config.setCreateDateTimeChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setOneGeomPerTable(true);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String[][] expectedValues = new String[][]{
                        {"ModelA.TopicA.ClassA.AttrCoord", "attrcoord", "classa", null},
                        {"ModelA.TopicA.ClassA.AttrMultiCoord", "attrmulticoord", "classa_attrmulticoord", null},
                        {"ModelA.TopicA.ClassA.AttrMultiLine", "attrmultiline", "classa_attrmultiline", null},
                        {"ModelA.TopicA.ClassA.AttrArea", "attrarea", "classa_attrarea", null},
                        {"ModelA.TopicA.ClassA.AttrLine", "attrline", "classa_attrline", null},
                        {"ModelA.TopicA.ClassA.AttrMultiArea", "attrmultiarea", "classa_attrmultiarea", null},
                        {"ModelA.TopicA.ClassA.AttrSurface", "attrsurface", "classa_attrsurface", null},
                        {"ModelA.TopicA.ClassA.AttrMultiSurface", "attrmultisurface", "classa_attrmultisurface", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String[][] expectedValues = new String[][]{
                        {"ModelA.TopicA.ClassA.AttrLine:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrline"},
                        {"ModelA.TopicA.ClassA.AttrMultiSurface:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrmultisurface"},
                        {"ModelA.TopicA.ClassA.AttrMultiLine:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrmultiline"},
                        {"ModelA.TopicA.ClassA.AttrArea:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrarea"},
                        {"ModelA.TopicA.ClassA.AttrSurface:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrsurface"},
                        {"ModelA.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ModelA.TopicA.ClassA.AttrMultiCoord:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrmulticoord"},
                        {"ModelA.TopicA.ClassA.AttrMultiArea:2056(ModelA.TopicA.ClassA)", "ch.ehi.ili2db.secondaryTable", "classa_attrmultiarea"},
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

    private static void assertAttributeType(IomObject iomObject, String attributeName, String attributeType) {
        IomObject attr = iomObject.getattrobj(attributeName, 0);
        assertNotNull(attr);
        assertEquals(attributeType, attr.getobjecttag());
    }
}
