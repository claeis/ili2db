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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

public abstract class ListOfBagOf24Test {
    protected static final String TEST_OUT = "test/data/ListOfBagOfPrimTypes24/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    protected abstract void assertTableContainsColumns(Connection jdbcConnection, String tableName, String... expectedColumns) throws SQLException;

    protected void assertTableContainsValues(Connection jdbcConnection, String table, String[] columns, String[][] expectedValues) throws SQLException {
        Ili2dbAssert.assertTableContainsValues(jdbcConnection, table, columns, expectedValues, null);
    }

    abstract protected void assertClassA1Attr8(Connection jdbcConnection) throws Exception;

    abstract protected void assertClassA1Attr9(Connection jdbcConnection) throws Exception;

    @Test
    public void importBagOfPrimitiveTypesIli() throws SQLException, Ili2dbException {
        setup.resetDb();

        File data = new File(TEST_OUT, "BagOfPrimTypes24.ili");
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
        try {
            jdbcConnection = setup.createConnection();

            {
                String[][] expectedValues = new String[][]{
                        {"BagOfPrimTypes24.TestA.StructA2.Attr1", "structa2_attr1", "structa2_attr1", "structa2"},
                        {"BagOfPrimTypes24.TestA.StructA2.Attr1", "attr1", "structa2_attr1", null},
                        {"BagOfPrimTypes24.StructA.Attr1", "attr1", "structa", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr1", "classa1_attr1", "classa1_attr1", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr2", "classa1_attr2", "classa1_attr2", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr3", "classa1_attr3", "classa1_attr3", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr4", "classa1_attr4", "classa1_attr4", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr5", "classa1_attr5", "classa1_attr5", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr7", "classa1_attr7", "classa1_attr7", "classa1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr1", "attr1", "classa1_attr1", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr2", "attr2", "classa1_attr2", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr3", "attr3", "classa1_attr3", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr4", "attr4", "classa1_attr4", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr5", "attr5", "classa1_attr5", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr7", "attr7", "classa1_attr7", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr6", "attr6", "classa1", null},
                        {"BagOfPrimTypes24.TestA.ClassA2.Attr1", "classa2_attr1", "structa2", "classa2"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr8", "attr8", "classa1", null},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr9", "attr9", "classa1_attr9", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                String[][] expectedValues = new String[][]{
                        {"BagOfPrimTypes24.TestA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr2", "ch.ehi.ili2db.secondaryTable", "classa1_attr2"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr3", "ch.ehi.ili2db.secondaryTable", "classa1_attr3"},
                        {"BagOfPrimTypes24.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"BagOfPrimTypes24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr1", "ch.ehi.ili2db.secondaryTable", "classa1_attr1"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr7", "ch.ehi.ili2db.secondaryTable", "classa1_attr7"},
                        {"BagOfPrimTypes24.TestA.StructA2.Attr1", "ch.ehi.ili2db.secondaryTable", "structa2_attr1"},
                        {"BagOfPrimTypes24.TestA.StructA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr4", "ch.ehi.ili2db.secondaryTable", "classa1_attr4"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr5", "ch.ehi.ili2db.secondaryTable", "classa1_attr5"},
                        {"BagOfPrimTypes24.TestA.ClassA1.Attr9:2056(BagOfPrimTypes24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr9"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues, setup.getSchema());
            }

            assertTableContainsColumns(jdbcConnection, "classa1", "T_Id", "T_basket", "T_Ili_Tid", "attr6", "attr8");
            assertTableContainsColumns(jdbcConnection, "structa", "T_Id", "T_basket", "T_Ili_Tid", "T_Seq", "attr1");
            assertTableContainsColumns(jdbcConnection, "classa1_attr1", "T_Id", "T_basket", "T_Seq", "classa1_attr1", "attr1");
            assertTableContainsColumns(jdbcConnection, "classa1_attr2", "T_Id", "T_basket", "T_Seq", "classa1_attr2", "attr2");
            assertTableContainsColumns(jdbcConnection, "classa1_attr3", "T_Id", "T_basket", "T_Seq", "classa1_attr3", "attr3");
            assertTableContainsColumns(jdbcConnection, "classa1_attr4", "T_Id", "T_basket", "T_Seq", "classa1_attr4", "attr4");
            assertTableContainsColumns(jdbcConnection, "classa1_attr5", "T_Id", "T_basket", "T_Seq", "classa1_attr5", "attr5");
            assertTableContainsColumns(jdbcConnection, "classa1_attr7", "T_Id", "T_basket", "T_Seq", "classa1_attr7", "attr7");
            assertTableContainsColumns(jdbcConnection, "classa1_attr9", "T_Id", "T_basket", "attr9");

            assertTableContainsColumns(jdbcConnection, "classa2", "T_Id", "T_basket", "T_Ili_Tid");
            assertTableContainsColumns(jdbcConnection, "structa2", "T_Id", "T_basket", "T_Ili_Tid", "T_Seq", "classa2_attr1");
            assertTableContainsColumns(jdbcConnection, "structa2_attr1", "T_Id", "T_basket", "T_Seq", "structa2_attr1", "attr1");
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void importListOfPrimitiveTypesIli() throws SQLException, Ili2dbException {
        setup.resetDb();

        File data = new File(TEST_OUT, "ListOfPrimTypes24.ili");
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
        try {
            jdbcConnection = setup.createConnection();

            {
                String[][] expectedValues = new String[][]{
                        {"ListOfPrimTypes24.TestA.StructA2.Attr1", "structa2_attr1", "structa2_attr1", "structa2"},
                        {"ListOfPrimTypes24.TestA.StructA2.Attr1", "attr1", "structa2_attr1", null},
                        {"ListOfPrimTypes24.StructA.Attr1", "attr1", "structa", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr1", "classa1_attr1", "classa1_attr1", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr2", "classa1_attr2", "classa1_attr2", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr3", "classa1_attr3", "classa1_attr3", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr4", "classa1_attr4", "classa1_attr4", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr5", "classa1_attr5", "classa1_attr5", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr7", "classa1_attr7", "classa1_attr7", "classa1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr1", "attr1", "classa1_attr1", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr2", "attr2", "classa1_attr2", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr3", "attr3", "classa1_attr3", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr4", "attr4", "classa1_attr4", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr5", "attr5", "classa1_attr5", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr7", "attr7", "classa1_attr7", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr6", "attr6", "classa1", null},
                        {"ListOfPrimTypes24.TestA.ClassA2.Attr1", "classa2_attr1", "structa2", "classa2"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr8", "attr8", "classa1", null},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr9", "attr9", "classa1_attr9", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                String[][] expectedValues = new String[][]{
                        {"ListOfPrimTypes24.TestA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr2", "ch.ehi.ili2db.secondaryTable", "classa1_attr2"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr3", "ch.ehi.ili2db.secondaryTable", "classa1_attr3"},
                        {"ListOfPrimTypes24.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ListOfPrimTypes24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr1", "ch.ehi.ili2db.secondaryTable", "classa1_attr1"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr7", "ch.ehi.ili2db.secondaryTable", "classa1_attr7"},
                        {"ListOfPrimTypes24.TestA.StructA2.Attr1", "ch.ehi.ili2db.secondaryTable", "structa2_attr1"},
                        {"ListOfPrimTypes24.TestA.StructA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr4", "ch.ehi.ili2db.secondaryTable", "classa1_attr4"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr5", "ch.ehi.ili2db.secondaryTable", "classa1_attr5"},
                        {"ListOfPrimTypes24.TestA.ClassA1.Attr9:2056(ListOfPrimTypes24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr9"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues, setup.getSchema());
            }

            assertTableContainsColumns(jdbcConnection, "classa1", "T_Id", "T_basket", "T_Ili_Tid", "attr6", "attr8");
            assertTableContainsColumns(jdbcConnection, "structa", "T_Id", "T_basket", "T_Ili_Tid", "T_Seq", "attr1");
            assertTableContainsColumns(jdbcConnection, "classa1_attr1", "T_Id", "T_basket", "T_Seq", "classa1_attr1", "attr1");
            assertTableContainsColumns(jdbcConnection, "classa1_attr2", "T_Id", "T_basket", "T_Seq", "classa1_attr2", "attr2");
            assertTableContainsColumns(jdbcConnection, "classa1_attr3", "T_Id", "T_basket", "T_Seq", "classa1_attr3", "attr3");
            assertTableContainsColumns(jdbcConnection, "classa1_attr4", "T_Id", "T_basket", "T_Seq", "classa1_attr4", "attr4");
            assertTableContainsColumns(jdbcConnection, "classa1_attr5", "T_Id", "T_basket", "T_Seq", "classa1_attr5", "attr5");
            assertTableContainsColumns(jdbcConnection, "classa1_attr7", "T_Id", "T_basket", "T_Seq", "classa1_attr7", "attr7");
            assertTableContainsColumns(jdbcConnection, "classa1_attr9", "T_Id", "T_basket", "attr9");

            assertTableContainsColumns(jdbcConnection, "classa2", "T_Id", "T_basket", "T_Ili_Tid");
            assertTableContainsColumns(jdbcConnection, "structa2", "T_Id", "T_basket", "T_Ili_Tid", "T_Seq", "classa2_attr1");
            assertTableContainsColumns(jdbcConnection, "structa2_attr1", "T_Id", "T_basket", "T_Seq", "structa2_attr1", "attr1");
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void importBagOfPrimitiveTypesXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "BagOfPrimTypes24.xtf");
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
        try {
            jdbcConnection = setup.createConnection();

            assertTableContainsValues(jdbcConnection, "classa1", new String[]{"attr6"}, new String[][]{{"ORYSIT"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr1", new String[]{"attr1"}, new String[][]{{"Blaa"}, {"Ftaa"}, {"Gluu"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr2", new String[]{"attr2"}, new String[][]{{"12"}, {"14"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr3", new String[]{"attr3"}, new String[][]{{"E2"}, {"E1"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr4", new String[]{"attr4"}, new String[][]{{"prefix-019-postfix"}, {"prefix-199-postfix"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr5", new String[]{"attr5"}, new String[][]{{"<MIDEPS xmlns=\"\"></MIDEPS>"}, {"<WOROLF xmlns=\"\"></WOROLF>"}});
            assertTableContainsValues(jdbcConnection, "classa1_attr7", new String[]{"attr7"}, new String[][]{{"1997-10-14"}, {"2008-01-29"}});
            assertTableContainsValues(jdbcConnection, "structa2_attr1", new String[]{"attr1"}, new String[][]{{"HERSEN"}, {"FLORIN"}});
            assertClassA1Attr8(jdbcConnection);
            assertClassA1Attr9(jdbcConnection);

        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void importListOfPrimitiveTypesXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "ListOfPrimTypes24.xtf");
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

        assertListOf24DbContent();
    }

    @Test
    public void importListOfPrimitiveTypesXtf_ColsAsText() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "ListOfPrimTypes24.xtf");
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

        config.setSqlColsAsText(Config.SQL_COLS_AS_TEXT_ENABLE);

        Ili2db.run(config, null);

        assertListOf24DbContent();
    }

    private void assertListOf24DbContent() throws Exception {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = setup.createConnection();

            assertTableContainsValues(jdbcConnection, "classa1", new String[]{
                    "attr6"
            }, new String[][]{
                    {"ORYSIT"}
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr1", new String[]{
                    "T_Seq", "attr1"
            }, new String[][]{
                    {"0", "Blaa"},
                    {"1", "Ftaa"},
                    {"2", "Gluu"},
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr2", new String[]{
                    "T_Seq", "attr2"
            }, new String[][]{
                    {"0", "12"},
                    {"1", "14"},
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr3", new String[]{
                    "T_Seq", "attr3"
            }, new String[][]{
                    {"0", "E2"},
                    {"1", "E1"},
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr4", new String[]{
                    "T_Seq", "attr4"
            }, new String[][]{
                    {"0", "prefix-019-postfix"},
                    {"1", "prefix-199-postfix"},
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr5", new String[]{
                    "T_Seq", "attr5"
            }, new String[][]{
                    {"0", "<MIDEPS xmlns=\"\"></MIDEPS>"},
                    {"1", "<WOROLF xmlns=\"\"></WOROLF>"},
            });

            assertTableContainsValues(jdbcConnection, "classa1_attr7", new String[]{
                    "T_Seq", "attr7"
            }, new String[][]{
                    {"0", "1997-10-14"},
                    {"1", "2008-01-29"},
            });

            assertClassA1Attr8(jdbcConnection);
            assertClassA1Attr9(jdbcConnection);

            assertTableContainsValues(jdbcConnection, "structa2_attr1", new String[]{
                    "T_Seq", "attr1"
            }, new String[][]{
                    {"0", "HERSEN"},
                    {"1", "FLORIN"},
            });

        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void exportBagOfPrimitiveTypesXtf() throws Exception {
        importBagOfPrimitiveTypesXtf();

        // export xtf
        File data = new File(TEST_OUT, "BagOfPrimTypes24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("BagOfPrimTypes24");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "BagOfPrimTypes24.ili", FileEntryKind.ILIMODELFILE);
        ili2cConfig.addFileEntry(fileEntry);
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);

        // assert xtf
        Xtf24Reader reader = new Xtf24Reader(data);
        reader.setModel(td);

        assertThat(reader.read(), instanceOf(StartTransferEvent.class));
        assertThat(reader.read(), instanceOf(StartBasketEvent.class));

        assertClassA1(reader.read());
        assertClassA2(reader.read());

        assertThat(reader.read(), instanceOf(EndBasketEvent.class));
        assertThat(reader.read(), instanceOf(EndTransferEvent.class));
    }

    @Test
    public void exportListOfPrimitiveTypesXtf() throws Exception {
        importListOfPrimitiveTypesXtf();

        // export xtf
        File data = new File(TEST_OUT, "ListOfPrimTypes24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("ListOfPrimTypes24");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // compile model
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "ListOfPrimTypes24.ili", FileEntryKind.ILIMODELFILE);
        ili2cConfig.addFileEntry(fileEntry);
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);

        // assert xtf
        Xtf24Reader reader = new Xtf24Reader(data);
        reader.setModel(td);

        assertThat(reader.read(), instanceOf(StartTransferEvent.class));
        assertThat(reader.read(), instanceOf(StartBasketEvent.class));

        assertClassA1(reader.read());
        assertClassA2(reader.read());

        assertThat(reader.read(), instanceOf(EndBasketEvent.class));
        assertThat(reader.read(), instanceOf(EndTransferEvent.class));
    }

    private void assertClassA1(IoxEvent event) {
        assertThat(event, instanceOf(ObjectEvent.class));
        IomObject iomObj = ((ObjectEvent) event).getIomObject();

        assertTrue(iomObj.getobjecttag().endsWith(".TestA.ClassA1"));
        assertEquals(9, iomObj.getattrcount());

        assertEquals(3, iomObj.getattrvaluecount("Attr1"));
        assertEquals("Blaa", iomObj.getattrprim("Attr1", 0));
        assertEquals("Ftaa", iomObj.getattrprim("Attr1", 1));
        assertEquals("Gluu", iomObj.getattrprim("Attr1", 2));

        assertEquals(2, iomObj.getattrvaluecount("Attr2"));
        assertEquals("12", iomObj.getattrprim("Attr2", 0));
        assertEquals("14", iomObj.getattrprim("Attr2", 1));

        assertEquals(2, iomObj.getattrvaluecount("Attr3"));
        assertEquals("E2", iomObj.getattrprim("Attr3", 0));
        assertEquals("E1", iomObj.getattrprim("Attr3", 1));

        assertEquals(2, iomObj.getattrvaluecount("Attr4"));
        assertEquals("prefix-019-postfix", iomObj.getattrprim("Attr4", 0));
        assertEquals("prefix-199-postfix", iomObj.getattrprim("Attr4", 1));

        assertEquals(2, iomObj.getattrvaluecount("Attr5"));
        assertEquals("<MIDEPS xmlns=\"\"></MIDEPS>", iomObj.getattrprim("Attr5", 0));
        assertEquals("<WOROLF xmlns=\"\"></WOROLF>", iomObj.getattrprim("Attr5", 1));

        assertEquals(1, iomObj.getattrvaluecount("Attr6"));
        assertEquals("ORYSIT", iomObj.getattrprim("Attr6", 0));

        assertEquals(2, iomObj.getattrvaluecount("Attr7"));
        assertEquals("1997-10-14", iomObj.getattrprim("Attr7", 0));
        assertEquals("2008-01-29", iomObj.getattrprim("Attr7", 1));

        assertEquals(1, iomObj.getattrvaluecount("Attr8"));
        assertEquals("480000.000", iomObj.getattrobj("Attr8", 0).getattrvalue("C1"));
        assertEquals("70000.000", iomObj.getattrobj("Attr8", 0).getattrvalue("C2"));

        assertEquals(1, iomObj.getattrvaluecount("Attr9"));
        assertEquals("500000.000", iomObj.getattrobj("Attr9", 0).getattrvalue("C1"));
        assertEquals("72000.000", iomObj.getattrobj("Attr9", 0).getattrvalue("C2"));
    }

    private void assertClassA2(IoxEvent event) {
        assertThat(event, instanceOf(ObjectEvent.class));
        IomObject iomObj = ((ObjectEvent) event).getIomObject();

        assertEquals(1, iomObj.getattrcount());
        assertTrue(iomObj.getobjecttag().endsWith(".TestA.ClassA2"));

        assertEquals(1, iomObj.getattrvaluecount("Attr1"));
        IomObject structAttr = iomObj.getattrobj("Attr1", 0);

        assertEquals(1, structAttr.getattrcount());
        assertTrue(structAttr.getobjecttag().endsWith(".TestA.StructA2"));

        assertEquals(2, structAttr.getattrvaluecount("Attr1"));
        assertEquals("HERSEN", structAttr.getattrprim("Attr1", 0));
        assertEquals("FLORIN", structAttr.getattrprim("Attr1", 1));
    }
}
