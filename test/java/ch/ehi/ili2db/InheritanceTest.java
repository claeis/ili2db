package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import ch.ehi.ili2db.base.DbNames;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class InheritanceTest {
    protected static final String TEST_DATA_DIR = "test/data/Inheritance";
    protected static final String DATASETNAME = "Testset1";
    protected static final String DATASETNAMEX = "Testset1x";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importIli_smart0() throws Exception {
        setup.resetDb();
        File data = new File(TEST_DATA_DIR, "Inheritance1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    @Test
    public void importIli_smart1() throws Exception {
        setup.resetDb();
        File data = new File(TEST_DATA_DIR, "Inheritance1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    // TODO ili2db#382 integrate Inheritance2.ili into Inheritance1.ili
    @Test
    public void importIli_smart2() throws Exception {
        setup.resetDb();
        File data = new File(TEST_DATA_DIR, "Inheritance2.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Ili2dbAssert.assertAttrNameTable(setup, new String[][]{
                {"Inheritance2.TestB.ClassA.attrA3", "attra3", "classa", null},
                {"Inheritance2.TestA.ClassA3b.attrA3b", "attra3b", "classa3b", null},
                {"Inheritance2.TestA.ClassB.attrB", "attrb", "classb", null},
                {"Inheritance2.TestA.ClassA3b.attrA3b", "attra3b", "classa3c", null},
                {"Inheritance2.TestA.ClassA3.attrA3", "attra3", "classa3c", null},
                {"Inheritance2.TestA.ClassA3.attrA3", "attra3", "classa3b", null},
                {"Inheritance2.TestA.aa2bb.bb", "bb", "aa2bb", "classb"},
                {"Inheritance2.TestA.ClassA3c.attrA3c", "attra3c", "classa3c", null},
                {"Inheritance2.TestA.aa2bb.aa", "aa_classa3c", "aa2bb", "classa3c"},
                {"Inheritance2.TestA.aa2bb.aa", "aa_classa3b", "aa2bb", "classa3b"},
                {"Inheritance2.TestA.a2b.a", "a_classa3b", "classb", "classa3b"},
                {"Inheritance2.TestA.a2b.a", "a_classa3c", "classb", "classa3c"},
                {"Inheritance2.TestB.ClassA2.attrA3", "attra3", "classa2", null},
        });

        Ili2dbAssert.assertTrafoTable(setup, new String[][]{
                {"Inheritance2.TestA.ClassA3", "ch.ehi.ili2db.inheritance", "subClass"},
                {"Inheritance2.TestB.ClassA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestA.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestA.ClassA3b", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestA.ClassA3c", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestA.aa2bb", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestB.ClassA2", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"Inheritance2.TestA.a2b", "ch.ehi.ili2db.inheritance", "embedded"},
        });
    }

    @Test
    public void importXtf_smart0() throws Exception {
        {
            importIli_smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data = new File(TEST_DATA_DIR, "Inheritance1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Assert.assertTrue(stmt.execute("SELECT classa3.attra3,a3b.attra3b FROM " + setup.prefixName("classa3") + ',' + setup.prefixName("classa3b") + " a3b WHERE classa3.t_ili_tid='7'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("a3", rs.getString(1));
                Assert.assertEquals("a3b", rs.getString(2));
            }

        }
    }

    @Test
    public void importXtf_smart2() throws Exception {
        {
            importIli_smart2();
        }
        File data = new File(TEST_DATA_DIR, "Inheritance2a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        config.setImportBid(true);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Ili2dbAssert.assertTableContainsValues(setup, "classa3c", new String[]{DbNames.T_ILI_TID_COL, "attra3", "attra3b", "attra3c"}, new String[][]{
                    {"2", "attra3-20", "attra3b-20", "attra3c-20"},
            }, null);

            Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM " + setup.prefixName("classb") + " WHERE t_ili_tid='4'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertNotNull(rs.getObject("a_classa3c"));
                Assert.assertNull(rs.getObject("a_classa3b"));
            }
        }
    }

    @Test
    public void updateXtfNew_smart2() throws Exception {
        {
            importIli_smart2();
        }
        File data = new File(TEST_DATA_DIR, "Inheritance2a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_UPDATE);
        config.setImportTid(true);
        config.setDatasetName(DATASETNAME);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Ili2dbAssert.assertTableContainsValues(setup, "classa3c", new String[]{DbNames.T_ILI_TID_COL, "attra3", "attra3b", "attra3c"}, new String[][]{
                    {"2", "attra3-20", "attra3b-20", "attra3c-20"},
            }, null);

            Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM " + setup.prefixName("classb") + " WHERE t_ili_tid='4'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertNotNull(rs.getObject("a_classa3c"));
                Assert.assertNull(rs.getObject("a_classa3b"));
            }
        }
    }

    @Test
    public void updateXtfExisting_smart2() throws Exception {
        {
            importXtf_smart2();
        }
        long old_t_id = -1;
        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Assert.assertTrue(stmt.execute("SELECT t_id,attra3,attra3b,attra3c FROM " + setup.prefixName("classa3c") + " WHERE t_ili_tid='2'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                old_t_id = rs.getLong(1);
            }
        }

        File data = new File(TEST_DATA_DIR, "Inheritance2aUpdate.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_UPDATE);
        config.setDatasetName(DATASETNAME);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Assert.assertTrue(stmt.execute("SELECT t_id,attra3,attra3b,attra3c FROM " + setup.prefixName("classa3c") + " WHERE t_ili_tid='2'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(old_t_id, rs.getLong(1));
                Assert.assertEquals("attra3-20u", rs.getString(2));
                Assert.assertEquals("attra3b-20u", rs.getString(3));
                Assert.assertEquals("attra3c-20u", rs.getString(4));
            }

            Assert.assertTrue(stmt.execute("SELECT a_classa3b,a_classa3c FROM " + setup.prefixName("classb") + " WHERE t_ili_tid='4'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertNotNull(rs.getObject("a_classa3c"));
                Assert.assertNull(rs.getObject("a_classa3b"));
            }
        }
    }

    @Test
    public void importXtfExtRef_smart2() throws Exception {
        {
            importXtf_smart2();
        }

        File data = new File(TEST_DATA_DIR, "Inheritance2b.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setImportTid(true);
        config.setDatasetName(DATASETNAMEX);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            Assert.assertTrue(stmt.execute("SELECT attrb,attra3,attra3b,attra3c FROM " + setup.prefixName("classb") + " INNER JOIN " + setup.prefixName("classa3c") + " ON (classa3c.t_id=classb.a_classa3c) WHERE classb.t_ili_tid='x2'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("attrb-x2", rs.getString(1));
                Assert.assertEquals("attra3-20", rs.getString(2));
                Assert.assertEquals("attra3b-20", rs.getString(3));
                Assert.assertEquals("attra3c-20", rs.getString(4));
            }
        }
    }

    @Test
    public void exportXtf_smart0() throws Exception {
        {
            importXtf_smart0();
        }

        File data = new File(TEST_DATA_DIR, "Inheritance1a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setModels("Inheritance1");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // read objects of db and write objectValue to HashMap
        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        {
            IomObject obj1 = objs.get("17");
            Assert.assertNotNull(obj1);
            Assert.assertEquals("Inheritance1.TestB.ClassB1", obj1.getobjecttag());
        }
        {
            IomObject obj1 = objs.get("17");
            Assert.assertEquals("x2", obj1.getattrobj("s3b", 0).getattrvalue("attrB3b"));
        }
        {
            IomObject obj1 = objs.get("17");
            Assert.assertEquals("b2", obj1.getattrobj("s2", 0).getattrvalue("attrB2b"));
        }
        {
            IomObject obj1 = objs.get("17");
            Assert.assertEquals("b3a", obj1.getattrobj("s3a", 0).getattrvalue("attrB3"));
        }
        {
            IomObject obj1 = objs.get("17");
            Assert.assertEquals("b1", obj1.getattrobj("s1", 0).getattrvalue("attrB1"));
        }
    }

    @Test
    public void exportXtf_smart2() throws Exception {
        {
            importXtf_smart2();
        }
        File data = new File(TEST_DATA_DIR, "Inheritance2a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setDatasetName(DATASETNAME);
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // read objects of db and write objectValue to HashMap
        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                } else {
                    objs.put(iomObj.getattrobj("aa", 0).getobjectrefoid() + iomObj.getattrobj("bb", 0).getobjectrefoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        Assert.assertEquals(8, objs.size());
        {
            IomObject obj1 = objs.get("1");
            Assert.assertEquals("Inheritance2.TestA.ClassA3b oid 1 {attrA3 attra3-10, attrA3b attra3b-10}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("2");
            Assert.assertEquals("Inheritance2.TestA.ClassA3c oid 2 {attrA3 attra3-20, attrA3b attra3b-20, attrA3c attra3c-20}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("3");
            Assert.assertEquals("Inheritance2.TestA.ClassB oid 3 {a -> 1 REF {}, attrB attrb-30}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("4");
            Assert.assertEquals("Inheritance2.TestA.ClassB oid 4 {a -> 2 REF {}, attrB attrb-40}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("5");
            Assert.assertEquals("Inheritance2.TestA.ClassB oid 5 {a -> 1 REF {}, attrB attrb-50}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("14");
            Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 4 REF {}}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("24");
            Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 2 REF {}, bb -> 4 REF {}}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("15");
            Assert.assertEquals("Inheritance2.TestA.aa2bb {aa -> 1 REF {}, bb -> 5 REF {}}", obj1.toString());
        }
    }

    @Test
    public void importIliStructAttrFK_smart0() throws Exception {
        setup.resetDb();

        File data = new File(TEST_DATA_DIR, "StructAttr1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // FIXME check that FK exists on reference from struct table to class table
        Ili2dbAssert.assertAttrNameTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_structa", null},
                {"StructAttr1.TopicA.StructAb.ab1", "ab1", "topica_structab", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_structa", "topica_classa"},
                {"StructAttr1.TopicA.ClassA.attr2", "attr2", "topica_classa", null},
                {"StructAttr1.TopicA.ClassB.attr3", "attr3", "topica_classb", null},
                {"StructAttr1.TopicA.ClassC.attr4", "attr4", "topica_classc", null},
                {"StructAttr1.TopicA.ClassD.d1", "topica_classd_d1", "topica_structa", "topica_classd"},
                {"StructAttr1.TopicA.ClassD.d2", "d2", "topica_classd", null},
                {"StructAttr1.TopicB.StructA.name", "aname", "topicb_structa", null},
                {"StructAttr1.TopicB.ClassA.attr1", "topicb_classa_attr1", "topicb_structa", "topicb_classa"},
                {"StructAttr1.TopicB.ClassA.attr2", "attr2", "topicb_classa", null},
                {"StructAttr1.TopicB.ClassB.attr3", "attr3", "topicb_classb", null},
                {"StructAttr1.TopicB.ClassB.b1", "topicb_classb_b1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.ClassC.attr4", "attr4", "topicb_classc", null},
                {"StructAttr1.TopicB.ClassC.c1", "topicb_classc_c1", "topicb_structa", "topicb_classc"},
        });

        Ili2dbAssert.assertTrafoTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.StructAb", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "newClass"},
        });
    }

    @Test
    public void importIliStructAttrFK_smart1() throws Exception {
        setup.resetDb();
        File data = new File(TEST_DATA_DIR, "StructAttr1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // FIXME check that FK exists on reference from struct table to class table
        // imported classes
        Ili2dbAssert.assertTableContainsValues(setup, DbNames.CLASSNAME_TAB, new String[]{DbNames.CLASSNAME_TAB_ILINAME_COL, DbNames.CLASSNAME_TAB_SQLNAME_COL}, new String[][]{
                {"StructAttr1.TopicA.StructA", "topica_structa"},
                {"StructAttr1.TopicB.StructA", "topicb_structa"},
        }, DbNames.CLASSNAME_TAB_ILINAME_COL + " LIKE 'StructAttr1.Topic_.StructA'");

        Ili2dbAssert.assertAttrNameTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_structa", null},
                {"StructAttr1.TopicA.StructAb.ab1", "ab1", "topica_structa", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_structa_attr1", "topica_structa", "topica_structa"},
                {"StructAttr1.TopicA.ClassA.attr2", "attr2", "topica_structa", null},
                {"StructAttr1.TopicA.ClassB.attr3", "attr3", "topica_structa", null},
                {"StructAttr1.TopicA.ClassC.attr4", "attr4", "topica_structa", null},
                {"StructAttr1.TopicA.ClassD.d1", "topica_structa_d1", "topica_structa", "topica_structa"},
                {"StructAttr1.TopicA.ClassD.d2", "d2", "topica_structa", null},
                {"StructAttr1.TopicB.StructA.name", "aname", "topicb_structa", null},
                {"StructAttr1.TopicB.ClassB.attr3", "attr3", "topicb_classb", null},
                {"StructAttr1.TopicB.ClassA.attr1", "topicb_classb_attr1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.ClassB.b1", "topicb_classb_b1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.ClassC.c1", "topicb_classb_c1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.ClassA.attr2", "attr2", "topicb_classb", null},
                {"StructAttr1.TopicB.ClassC.attr4", "attr4", "topicb_classb", null},
        });

        Ili2dbAssert.assertTrafoTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicA.StructAb", "ch.ehi.ili2db.inheritance", "superClass"},
                {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "superClass"},
                {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "superClass"},
                {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
                {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "superClass"},
                {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
        });
    }

    @Test
    public void importIliStructAttrFK_smart2() throws Exception {
        setup.resetDb();
        File data = new File(TEST_DATA_DIR, "StructAttr1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // FIXME check that FK exists on reference from struct table to class table
        Ili2dbAssert.assertAttrNameTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_structab", null},
                {"StructAttr1.TopicB.ClassB.attr3", "attr3", "topicb_classc", null},
                {"StructAttr1.TopicA.ClassC.attr4", "attr4", "topica_classc", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_classd", "topica_classc"},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_classd", "topica_classb"},
                {"StructAttr1.TopicB.ClassA.attr2", "attr2", "topicb_classc", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_classd", "topica_classa"},
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_classa", null},
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_classc", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_structa", "topica_classb"},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_structa", "topica_classa"},
                {"StructAttr1.TopicA.ClassB.attr3", "attr3", "topica_classb", null},
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_structa", null},
                {"StructAttr1.TopicA.ClassA.attr2", "attr2", "topica_classb", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_structa", "topica_classc"},
                {"StructAttr1.TopicB.ClassB.attr3", "attr3", "topicb_classb", null},
                {"StructAttr1.TopicA.StructAb.ab1", "ab1", "topica_structab", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_classa", "topica_classa"},
                {"StructAttr1.TopicB.ClassA.attr2", "attr2", "topicb_classb", null},
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_classb", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_classa", "topica_classc"},
                {"StructAttr1.TopicB.ClassB.b1", "topicb_classc_b1", "topicb_structa", "topicb_classc"},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_classa", "topica_classb"},
                {"StructAttr1.TopicA.ClassD.d1", "topica_classd_d1", "topica_structab", "topica_classd"},
                {"StructAttr1.TopicA.StructA.name", "aname", "topica_classd", null},
                {"StructAttr1.TopicB.ClassB.b1", "topicb_classb_b1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.StructA.name", "aname", "topicb_structa", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classc_attr1", "topica_structab", "topica_classc"},
                {"StructAttr1.TopicA.ClassD.d2", "d2", "topica_classd", null},
                {"StructAttr1.TopicB.ClassA.attr1", "topicb_classb_attr1", "topicb_structa", "topicb_classb"},
                {"StructAttr1.TopicB.ClassA.attr1", "topicb_classc_attr1", "topicb_structa", "topicb_classc"},
                {"StructAttr1.TopicA.ClassB.attr3", "attr3", "topica_classc", null},
                {"StructAttr1.TopicB.ClassC.c1", "topicb_classc_c1", "topicb_structa", "topicb_classc"},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classb_attr1", "topica_structab", "topica_classb"},
                {"StructAttr1.TopicA.ClassA.attr2", "attr2", "topica_classc", null},
                {"StructAttr1.TopicA.ClassA.attr1", "topica_classa_attr1", "topica_structab", "topica_classa"},
                {"StructAttr1.TopicA.ClassA.attr2", "attr2", "topica_classa", null},
                {"StructAttr1.TopicB.ClassC.attr4", "attr4", "topicb_classc", null}
        });

        Ili2dbAssert.assertTrafoTable(setup, new String[][]{
                {"StructAttr1.TopicA.StructA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicA.StructAb", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicA.ClassD", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
        });
    }

    @Test
    public void importXtfStructAttrFK_smart0() throws Exception {
        {
            importIliStructAttrFK_smart0();
        }

        File data = new File(TEST_DATA_DIR, "StructAttr1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    @Test
    public void importXtfStructAttrFK_smart1() throws Exception {
        {
            importIliStructAttrFK_smart1();
        }

        File data = new File(TEST_DATA_DIR, "StructAttr1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            // subtype value
            Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM " + setup.prefixName("topica_structa") + " WHERE topica_structa.aname = 'Anna'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertFalse(rs.next());
            }
            Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM " + setup.prefixName("topica_structa") + " WHERE topica_structa.aname = 'Berta'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertFalse(rs.next());
            }
            Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM " + setup.prefixName("topica_structa") + " WHERE topica_structa.aname = 'Claudia'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertFalse(rs.next());
            }
        }
    }

    @Test
    public void importXtfStructAttrFK_smart2() throws Exception {
        {
            importIliStructAttrFK_smart2();
        }

        File data = new File(TEST_DATA_DIR, "StructAttr1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    @Test
    public void exportXtfStructAttrFK_smart0() throws Exception {
        {
            importXtfStructAttrFK_smart0();
        }
        File data = new File(TEST_DATA_DIR, "StructAttr1a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setDatasetName(DATASETNAME);
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // read objects of db and write objectValue to HashMap
        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        Assert.assertEquals(6, objs.size());
        {
            IomObject obj1 = objs.get("a1");
            Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a2");
            Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a3");
            Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a4");
            Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b2");
            Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b3");
            Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
    }

    @Test
    public void exportXtfStructAttrFK_smart1() throws Exception {
        {
            importXtfStructAttrFK_smart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data = new File(TEST_DATA_DIR, "StructAttr1a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setDatasetName(DATASETNAME);
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // read objects of db and write objectValue to HashMap
        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        Assert.assertEquals(6, objs.size());
        {
            IomObject obj1 = objs.get("a1");
            Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a2");
            Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a3");
            Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a4");
            Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b2");
            Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b3");
            Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
    }

    @Test
    public void exportXtfStructAttrFK_smart2() throws Exception {
        {
            importXtfStructAttrFK_smart2();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data = new File(TEST_DATA_DIR, "StructAttr1a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setDatasetName(DATASETNAME);
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        // read objects of db and write objectValue to HashMap
        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        Assert.assertEquals(6, objs.size());
        {
            IomObject obj1 = objs.get("a1");
            Assert.assertEquals("StructAttr1.TopicA.ClassA oid a1 {attr1 StructAttr1.TopicA.StructA {name Anna}, attr2 text2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a2");
            Assert.assertEquals("StructAttr1.TopicA.ClassB oid a2 {attr1 StructAttr1.TopicA.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a3");
            Assert.assertEquals("StructAttr1.TopicA.ClassC oid a3 {attr1 StructAttr1.TopicA.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("a4");
            Assert.assertEquals("StructAttr1.TopicA.ClassD oid a4 {d1 StructAttr1.TopicA.StructAb {ab1 ab1, name Rolf}, d2 d2}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b2");
            Assert.assertEquals("StructAttr1.TopicB.ClassB oid b2 {attr1 StructAttr1.TopicB.StructA {name Berta}, attr2 text2, attr3 text3}", obj1.toString());
        }
        {
            IomObject obj1 = objs.get("b3");
            Assert.assertEquals("StructAttr1.TopicB.ClassC oid b3 {attr1 StructAttr1.TopicB.StructA {name Claudia}, attr2 text2, attr3 text3, attr4 text4}", obj1.toString());
        }
    }

    @Test
    public void importIliRefAttrFK_smart1() throws Exception {
        setup.resetDb();
        //EhiLogger.getInstance().setTraceFilter(false);
        File data = new File(TEST_DATA_DIR, "RefAttr1.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
        //config.setCreatescript(data.getPath()+".sql");
        //config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // inheritance Ref test.
        Ili2dbAssert.assertTableContainsValues(setup, DbNames.INHERIT_TAB, new String[]{DbNames.INHERIT_TAB_THIS_COL, DbNames.INHERIT_TAB_BASE_COL}, new String[][]{
                {"RefAttr1.TopicA.ClassA1", "RefAttr1.TopicA.ClassA0"},
                {"RefAttr1.TopicA.ClassA2", "RefAttr1.TopicA.ClassA0"},
                {"RefAttr1.TopicA.ClassA11", "RefAttr1.TopicA.ClassA1"},
                {"RefAttr1.TopicA.StructA1", "RefAttr1.TopicA.StructA0"},
                {"RefAttr1.TopicA.StructA2", "RefAttr1.TopicA.StructA0"},
                {"RefAttr1.TopicA.StructA11", "RefAttr1.TopicA.StructA1"},
        }, DbNames.INHERIT_TAB_BASE_COL + " IS NOT NULL");
    }

    @Test
    public void importXtfRefAttrFK_smart1() throws Exception {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importIliRefAttrFK_smart1();
        }
        File data = new File(TEST_DATA_DIR, "RefAttr1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        config.setImportTid(true);
        //config.setCreatescript(data.getPath()+".sql");
        //config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    @Test
    public void exportXtfRefAttrFK_smart1() throws Exception {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importXtfRefAttrFK_smart1();
        }
        File data = new File(TEST_DATA_DIR, "RefAttr1a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setDatasetName(DATASETNAME);
        //config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        HashMap<String, IomObject> objs = new HashMap<>();
        XtfReader reader = new XtfReader(data);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        {
            IomObject obj0 = objs.get("b.3");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
            Assert.assertEquals("RefAttr1.TopicA.StructA11 {ref -> a11.1 REF {}}", obj0.getattrobj("struct", 0).toString());
        }
        {
            IomObject obj0 = objs.get("b.1");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
            Assert.assertEquals("RefAttr1.TopicA.StructA1 {ref -> a1.1 REF {}}", obj0.getattrobj("struct", 0).toString());
        }
    }

    @Test
    public void importIliSubtypeFK_smart1() throws Exception {
        setup.resetDb();

        File data = new File(TEST_DATA_DIR, "SubtypeFK.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setDatasetName(DATASETNAME);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        // subtype class import
        Ili2dbAssert.assertTableContainsValues(setup, DbNames.CLASSNAME_TAB, new String[]{DbNames.CLASSNAME_TAB_ILINAME_COL, DbNames.CLASSNAME_TAB_SQLNAME_COL}, new String[][]{
                {"SubtypeFK23.Topic.bc1", "bc1"},
                {"SubtypeFK23.Topic.ClassA", "classa"},
                {"SubtypeFK23.Topic.ClassB", "classb"},
                {"SubtypeFK23.Topic.ClassC", "classc"},
        }, null);

        Ili2dbAssert.assertAttrNameTable(setup, new String[][]{
                {"SubtypeFK23.Topic.bc1.b1", "b1", "classa", "classa"},
        });

        Ili2dbAssert.assertTrafoTable(setup, new String[][]{
                {"SubtypeFK23.Topic.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                {"SubtypeFK23.Topic.ClassB", "ch.ehi.ili2db.inheritance", "superClass"},
                {"SubtypeFK23.Topic.bc1", "ch.ehi.ili2db.inheritance", "embedded"},
                {"SubtypeFK23.Topic.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
        });
    }

    @Test
    public void importXtfSubtypeFK_smart1() throws Exception {
        {
            importIliSubtypeFK_smart1();
        }
        File data = new File(TEST_DATA_DIR, "SubtypeFKa.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDatasetName(DATASETNAME);
        //config.setCreatescript(data.getPath()+".sql");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        try (Connection jdbcConnection = setup.createConnection();
             Statement stmt = jdbcConnection.createStatement()) {
            // subtype value
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.target, t_ili2db_attrname.sqlname FROM " + setup.prefixName("t_ili2db_attrname") + " WHERE t_ili2db_attrname.target = 'classa'"));
            {
                ResultSet rs = stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("b1", rs.getString(2));
            }
        }
    }
}
