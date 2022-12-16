package ch.ehi.ili2db;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public abstract class UpdateTest {
    protected static final String TEST_OUT = "test/data/Update/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    protected void assertDatabaseContainsClassA1(Statement stmt, String oid, String attrA1) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attra1 FROM " + setup.prefixName("classa1") + " WHERE t_ili_tid='" + oid + "';");

        assertTrue(String.format("Object with oid <%s> does not exist.", oid), rs.next());
        assertEquals(attrA1, rs.getObject(1));
        assertFalse(rs.next());
    }

    protected long getClassA1Count(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + setup.prefixName("classa1") + ";");

        assertTrue(rs.next());
        return ((Number)rs.getObject(1)).longValue();
    }

    protected void assertDatabaseContainsClassA2(Statement stmt, String oid, String attrA2, String classA1Oid) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attra2, b.t_ili_tid FROM " + setup.prefixName("classa2") + " a JOIN " + setup.prefixName("classa1") + " b ON b.t_id=classa1 WHERE a.t_ili_tid='" + oid + "';");

        assertTrue(String.format("Object with oid <%s> does not exist.", oid), rs.next());
        assertEquals(attrA2, rs.getObject(1));
        assertEquals(classA1Oid, rs.getObject(2));
        assertFalse(rs.next());
    }

    protected long getClassA2Count(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + setup.prefixName("classa2") + ";");

        assertTrue(rs.next());
        return ((Number)rs.getObject(1)).longValue();
    }

    private void schemaimport() throws Exception {
        File data = new File(TEST_OUT, "Update.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setDefaultSrsCode("2056");

        setup.setXYParams(config);
        Ili2db.run(config, null);
    }

    private void runWithXtf(int function, String file) throws Exception {
        File data = new File(TEST_OUT, file);
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(function);
        config.setDatasetName("datasetA");
        config.setImportTid(true);
        config.setImportBid(true);

        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
    }

    @Test
    public void updateXtf() throws Exception {
        setup.resetDb();
        schemaimport();
        runWithXtf(Config.FC_IMPORT, "Init.xtf");
        runWithXtf(Config.FC_UPDATE, "Update.xtf");

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertDatabaseContainsClassA1(stmt, "oid_A1_1", "ADETANGL");
            assertDatabaseContainsClassA1(stmt, "oid_A1_4", "GUYEMASP");
            assertEquals(2, getClassA1Count(stmt));

            assertEquals(0, getClassA2Count(stmt));
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
    public void updateXtfWithRef() throws Exception {
        setup.resetDb();
        schemaimport();
        runWithXtf(Config.FC_IMPORT, "Init.xtf");
        runWithXtf(Config.FC_UPDATE, "UpdateWithRef.xtf");

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertDatabaseContainsClassA1(stmt, "oid_A1_1", "MACHARDU");
            assertDatabaseContainsClassA1(stmt, "oid_A1_4", "FIRAFORE");
            assertEquals(2, getClassA1Count(stmt));

            assertDatabaseContainsClassA2(stmt, "oid_A2_1", "MENSIDAN", "oid_A1_4");
            assertDatabaseContainsClassA2(stmt, "oid_A2_3", "CRECONGT", "oid_A1_1");
            assertEquals(2, getClassA2Count(stmt));
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
    public void updateXtfExternalRef() throws Exception {
        setup.resetDb();
        schemaimport();
        runWithXtf(Config.FC_IMPORT, "Init.xtf");
        runWithXtf(Config.FC_UPDATE, "InitExternalRef.xtf");
        runWithXtf(Config.FC_UPDATE, "UpdateExternalRef.xtf");

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertDatabaseContainsClassA1(stmt, "oid_A1_1", "OSLINATE");
            assertDatabaseContainsClassA1(stmt, "oid_A1_2", "VERADEST");
            assertDatabaseContainsClassA1(stmt, "oid_A1_3", "ERSIPTIN");
            assertEquals(3, getClassA1Count(stmt));

            assertDatabaseContainsClassA2(stmt, "oid_A2_1", "AULTIONY", "oid_A1_1");
            assertDatabaseContainsClassA2(stmt, "oid_A2_2", "ROMARBIL", "oid_A1_1");
            assertDatabaseContainsClassA2(stmt, "oid_A2_3", "SPRONGAS", "oid_A1_3");
            assertDatabaseContainsClassA2(stmt, "oid_A2_5", "NOUSERON", "oid_A1_3");
            assertEquals(4, getClassA2Count(stmt));
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
