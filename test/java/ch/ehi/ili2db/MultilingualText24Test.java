package ch.ehi.ili2db;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.Main;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.ObjectEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static ch.ehi.ili2db.Ili2dbAssert.assertTableContainsValues;
import static org.junit.Assert.assertNotNull;

public abstract class MultilingualText24Test {

    protected static final String DBSCHEMA = "MultilingualText24";
    protected static final String TEST_OUT = "test/data/MultilingualText/";
    protected static final String[] ENTRY_A1_1 = {
            "a1.1-null", "a1.1-de", "a1.1-fr", "a1.1-it", "a1.1-rm", "a1.1-en",
            "a1.1-de", "de",
            "a1.1-null", "a1.1-de", "a1.1-fr", "a1.1-it", "a1.1-rm", "a1.1-en",
            "a1.1-de", "de",
    };
    protected static final String[] ENTRY_A1_2 = new String[ENTRY_A1_1.length];
    protected static final String[] ENTRY_B1_1 = {
            null, "b1.1-de", null, null, null, null,
            "b1.1-fr", "fr",
            null, "b1.1-de", null, null, null, null,
            "b1.1-fr", "fr",
    };
    protected static final String[] ENTRY_URI = {
            null, "http://localhost/uri1.1-de", "http://localhost/uri1.1-fr", null, null, null,
            "http://localhost/uri1.1-en", "en",
    };

    protected AbstractTestSetup setup = createTestSetup();
    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importIliSmartChbaseV2() throws Exception {
        setup.resetDb();

        File data = new File(TEST_OUT, "MultilingualText_V2.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
        config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        try {
            jdbcConnection = setup.createConnection();

            assertTrafoEntries(jdbcConnection);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    private void assertTrafoEntries(Connection connection) throws SQLException {
        assertTableContainsValues(
                connection,
                setup.prefixName(DbNames.TRAFO_TAB),
                new String[] {
                        DbNames.TRAFO_TAB_ILINAME_COL,
                        DbNames.TRAFO_TAB_TAG_COL,
                        DbNames.TRAFO_TAB_SETTING_COL,
                }, new String[][] {
                        {"MultilingualText_V2.TestA.ClassA1.atext", "ch.ehi.ili2db.multilingualTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassA1.btext", "ch.ehi.ili2db.localisedTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassA1.ctext", "ch.ehi.ili2db.multilingualTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassA1.dtext", "ch.ehi.ili2db.localisedTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassB1.atext", "ch.ehi.ili2db.multilingualTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassB1.btext", "ch.ehi.ili2db.localisedTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassB1.ctext", "ch.ehi.ili2db.multilingualTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassB1.dtext", "ch.ehi.ili2db.localisedTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassUri.localised", "ch.ehi.ili2db.localisedTrafo", "expand"},
                        {"MultilingualText_V2.TestA.ClassUri.multilingual", "ch.ehi.ili2db.multilingualTrafo", "expand"},
                },
                "tag = 'ch.ehi.ili2db.multilingualTrafo' OR tag = 'ch.ehi.ili2db.localisedTrafo'"
        );
    }

    @Test
    public void importXtfSmartChbaseV2() throws Exception {
        importIliSmartChbaseV2();

        File data = new File(TEST_OUT, "MultilingualText_V2.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Connection connection = null;
        Statement stmt = null;
        try {
            connection = setup.createConnection();
            stmt = connection.createStatement();

            assertTextRow(stmt, "classa1", "a1.1", ENTRY_A1_1);
            assertTextRow(stmt, "classa1", "a1.2", ENTRY_A1_2);
            assertTextRow(stmt, "classb1", "b1.1", ENTRY_B1_1);

            assertUriRow(stmt, "classuri", "uri1.1", ENTRY_URI);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void assertTextRow(Statement stmt, String tableName, String tid, String[] values) throws SQLException {
        Assert.assertTrue(stmt.execute("SELECT"
                + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                + ",btext,btext_lang"
                + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                + ",dtext,dtext_lang"
                + " FROM " + setup.prefixName(tableName) + " WHERE t_ili_tid = '" + tid + "'"));

        ResultSet rs = stmt.getResultSet();
        Assert.assertTrue(rs.next());

        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals(values[i], rs.getString(i + 1));
        }
    }

    private void assertUriRow(Statement stmt, String tableName, String tid, String[] values) throws SQLException {
        Assert.assertTrue(stmt.execute("SELECT"
                + " multilingual,multilingual_de,multilingual_fr,multilingual_it,multilingual_rm,multilingual_en"
                + ",localised,localised_lang"
                + " FROM " + setup.prefixName(tableName) + " WHERE t_ili_tid = '" + tid + "'"));

        ResultSet rs = stmt.getResultSet();
        Assert.assertTrue(rs.next());

        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals(values[i], rs.getString(i + 1));
        }
    }

    @Test
    public void exportXtfSmartChbaseV2() throws Exception {
        importXtfSmartChbaseV2();

        File data = new File(TEST_OUT, "MultilingualText_V2-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setModels("MultilingualText_V2;Localisation_V2;LocalisationCH_V2");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Configuration ili2cConfig = new Configuration();
        ili2cConfig.addFileEntry(new FileEntry(TEST_OUT + "CHBase_Part2_LOCALISATION_V2.ili", FileEntryKind.ILIMODELFILE));
        ili2cConfig.addFileEntry(new FileEntry(TEST_OUT + "MultilingualText_V2.ili", FileEntryKind.ILIMODELFILE));
        TransferDescription td = Main.runCompiler(ili2cConfig);
        assertNotNull(td);

        HashMap<String, IomObject> objects = new HashMap<String, IomObject>();
        IoxReader reader = Xtf24Reader.createReader(data);
        ((Xtf24Reader) reader).setModel(td);
        IoxEvent event;
        do {
            event = reader.read();
            if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objects.put(iomObj.getobjectoid(), iomObj);
                }
            }
        } while (!(event instanceof EndTransferEvent));

        IomObject objA11 = objects.get("a1.1");
        assertNotNull(objA11);
        Assert.assertEquals("MultilingualText_V2.TestA.ClassA1 oid a1.1 {atext LocalisationCH_V2.MultilingualText {LocalisedText [LocalisationCH_V2.LocalisedText {Text a1.1-null}, LocalisationCH_V2.LocalisedText {Language de, Text a1.1-de}, LocalisationCH_V2.LocalisedText {Language fr, Text a1.1-fr}, LocalisationCH_V2.LocalisedText {Language rm, Text a1.1-rm}, LocalisationCH_V2.LocalisedText {Language it, Text a1.1-it}, LocalisationCH_V2.LocalisedText {Language en, Text a1.1-en}]}, btext LocalisationCH_V2.LocalisedText {Language de, Text a1.1-de}, ctext LocalisationCH_V2.MultilingualMText {LocalisedText [LocalisationCH_V2.LocalisedMText {Text a1.1-null}, LocalisationCH_V2.LocalisedMText {Language de, Text a1.1-de}, LocalisationCH_V2.LocalisedMText {Language fr, Text a1.1-fr}, LocalisationCH_V2.LocalisedMText {Language rm, Text a1.1-rm}, LocalisationCH_V2.LocalisedMText {Language it, Text a1.1-it}, LocalisationCH_V2.LocalisedMText {Language en, Text a1.1-en}]}, dtext LocalisationCH_V2.LocalisedMText {Language de, Text a1.1-de}}"
                , objA11.toString());

        IomObject objA12 = objects.get("a1.2");
        assertNotNull(objA12);
        Assert.assertEquals("MultilingualText_V2.TestA.ClassA1 oid a1.2 {}"
                , objA12.toString());

        IomObject objB11 = objects.get("b1.1");
        assertNotNull(objB11);
        Assert.assertEquals("MultilingualText_V2.TestA.ClassB1 oid b1.1 {atext LocalisationCH_V2.MultilingualText {LocalisedText LocalisationCH_V2.LocalisedText {Language de, Text b1.1-de}}, btext LocalisationCH_V2.LocalisedText {Language fr, Text b1.1-fr}, ctext LocalisationCH_V2.MultilingualMText {LocalisedText LocalisationCH_V2.LocalisedMText {Language de, Text b1.1-de}}, dtext LocalisationCH_V2.LocalisedMText {Language fr, Text b1.1-fr}}"
                , objB11.toString());

        IomObject objUri = objects.get("uri1.1");
        assertNotNull(objUri);
        Assert.assertEquals("MultilingualText_V2.TestA.ClassUri oid uri1.1 {localised LocalisationCH_V2.LocalisedUri {Language en, Text http://localhost/uri1.1-en}, multilingual LocalisationCH_V2.MultilingualUri {LocalisedText [LocalisationCH_V2.LocalisedUri {Language de, Text http://localhost/uri1.1-de}, LocalisationCH_V2.LocalisedUri {Language fr, Text http://localhost/uri1.1-fr}]}}"
                , objUri.toString());
    }
}
