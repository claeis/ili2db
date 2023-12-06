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
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.ObjectEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public abstract class MultiCrs24Test {
    protected static final String TEST_OUT = "test/data/Crs/";
    protected static final String DBSCHEMA = "MultiCrs24";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    protected abstract void assertImportedData(Statement stmt) throws Exception;

    protected abstract void assertAttributeNameTable(Connection jdbcConnection) throws SQLException;

    protected abstract void assertTrafoTable(Connection jdbcConnection) throws SQLException;

    @Test
    public void importIli() throws Exception {
        setup.resetDb();

        File data = new File(TEST_OUT, "MultiCrs24.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setUseEpsgInNames(true);
        config.setValidation(false);
        config.setImportTid(true);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertAttributeNameTable(jdbcConnection);
            assertTrafoTable(jdbcConnection);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "MultiCrs24.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setDatasetName("Data");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setUseEpsgInNames(true);
        config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertImportedData(stmt);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void exportXtf() throws Exception {
        importXtf();

        File data = new File(TEST_OUT, "MultiCrs24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setDatasetName("Data");
        config.setFunction(Config.FC_EXPORT);
        config.setValidation(false);
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry("test/data/Crs/MultiCrs24.ili", FileEntryKind.ILIMODELFILE);
        ili2cConfig.addFileEntry(fileEntry);
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);

        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        IoxReader reader = Xtf24Reader.createReader(data);
        ((Xtf24Reader) reader).setModel(td);
        IoxEvent event;
        do {
            event = reader.read();
            if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            }
        } while (!(event instanceof EndTransferEvent));

        IomObject objectLv95 = objs.get("Test_LV95");
        Assert.assertNotNull(objectLv95);
        Assert.assertEquals("COORD {C1 2460001.000, C2 1045001.000}", objectLv95.getattrobj("attr2", 0).toString());
        Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.000, C2 1070000.000}, COORD {C1 2490000.000, C2 1080000.000}]}}", objectLv95.getattrobj("attr3", 0).toString());
        Assert.assertEquals("MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}]}", objectLv95.getattrobj("attr4", 0).toString());

        IomObject objectLv03 = objs.get("Test_LV03");
        Assert.assertNotNull(objectLv03);
        Assert.assertEquals("COORD {C1 460002.000, C2 45002.000}", objectLv03.getattrobj("attr2", 0).toString());
        Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.000, C2 70000.000}, COORD {C1 490000.000, C2 80000.000}]}}", objectLv03.getattrobj("attr3", 0).toString());
        Assert.assertEquals("MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.0, C2 70000.0}, COORD {C1 490000.0, C2 80000.0}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.0, C2 70000.0}, COORD {C1 490000.0, C2 80000.0}]}}]}", objectLv03.getattrobj("attr4", 0).toString());
    }

    @Test
    public void importIliOptionUseEpsgInNamesMissing() throws Exception {
        setup.resetDb();

        File data = new File(TEST_OUT, "MultiCrs24.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setValidation(false);
        config.setImportTid(true);

        // must be true if topic has deferred generics
        config.setUseEpsgInNames(false);

        try {
            Ili2db.run(config, null);
            fail("Expected exception not thrown");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(Ili2dbException.class));
            assertEquals("mapping of ili-classes to sql-tables failed", ex.getMessage());

            Exception cause = (Exception) ex.getCause();
            assertThat(cause, instanceOf(Ili2dbException.class));
            assertEquals("Mapping of Topic MultiCrs24.TestA requires the '--multiSrs' option because it declares deferred generics.", cause.getMessage());
        }
    }
}
