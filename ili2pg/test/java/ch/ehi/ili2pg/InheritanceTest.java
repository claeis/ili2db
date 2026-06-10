package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

public class InheritanceTest extends ch.ehi.ili2db.InheritanceTest {
    private static final String DBSCHEMA = "Inheritance";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Test
    public void exportXtf_IdPerTable_smart0() throws Exception {
        {
            importIli_smart0();
        }
        try (Connection jdbcConnection = setup.createConnection()) {
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR, "IdPerTable_smart0.sql")));
            File data = new File(TEST_DATA_DIR, "Inheritance1a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setTopics("Inheritance1.TestD");
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
                        objs.put(iomObj.getattrobj("d2", 0).getobjectrefoid() + iomObj.getattrobj("x2", 0).getobjectrefoid(), iomObj);
                    }
                } else if (event instanceof EndBasketEvent) {
                } else if (event instanceof EndTransferEvent) {
                }
            } while (!(event instanceof EndTransferEvent));
            Assert.assertEquals(3, objs.size());
            {
                IomObject obj1 = objs.get("20");
                Assert.assertEquals("Inheritance1.TestD.ClassD1b oid 20 {attrD1 20_d1, attrD1b 20_d1b}", obj1.toString());
            }
            {
                IomObject obj1 = objs.get("21");
                Assert.assertEquals("Inheritance1.TestD.ClassD1x oid 21 {attrD1x 21_d1x, d1 -> 20 REF {}}", obj1.toString());
            }
            {
                IomObject obj1 = objs.get("2021");
                Assert.assertEquals("Inheritance1.TestD.d2x {d2 -> 20 REF {}, x2 -> 21 REF {}}", obj1.toString());
            }
        }
    }

    @Test
    public void exportXtf_IdPerTable_smart1() throws Exception {
        {
            importIli_smart1();
        }
        try (Connection jdbcConnection = setup.createConnection()) {
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR, "IdPerTable_smart1.sql")));
            File data = new File(TEST_DATA_DIR, "Inheritance1a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setTopics("Inheritance1.TestD");
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
                        objs.put(iomObj.getattrobj("d2", 0).getobjectrefoid() + iomObj.getattrobj("x2", 0).getobjectrefoid(), iomObj);
                    }
                } else if (event instanceof EndBasketEvent) {
                } else if (event instanceof EndTransferEvent) {
                }
            } while (!(event instanceof EndTransferEvent));
            Assert.assertEquals(3, objs.size());
            {
                IomObject obj1 = objs.get("20");
                Assert.assertEquals("Inheritance1.TestD.ClassD1b oid 20 {attrD1 20_d1, attrD1b 20_d1b}", obj1.toString());
            }
            {
                IomObject obj1 = objs.get("21");
                Assert.assertEquals("Inheritance1.TestD.ClassD1x oid 21 {attrD1x 21_d1x, d1 -> 20 REF {}}", obj1.toString());
            }
            {
                IomObject obj1 = objs.get("2021");
                Assert.assertEquals("Inheritance1.TestD.d2x {d2 -> 20 REF {}, x2 -> 21 REF {}}", obj1.toString());
            }
        }
    }

    @Test
    public void exportXtf_IdPerTable_smart2() throws Exception {
        {
            importIli_smart2();
        }
        try (Connection jdbcConnection = setup.createConnection()) {
            DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(new File(TEST_DATA_DIR, "IdPerTable_smart2.sql")));
            File data = new File(TEST_DATA_DIR, "Inheritance2a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setDatasetName(DATASETNAME);
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // read objects of db and write objectValue to HashMap
            HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
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
    }
}
