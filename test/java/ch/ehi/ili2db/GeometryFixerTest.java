package ch.ehi.ili2db;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;


public abstract class GeometryFixerTest {

    protected static final String TEST_OUT = "test/data/GeometryFixer/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    private Config getBaseConfig() {
        File data = new File(TEST_OUT, "GeometryFixer1a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setCatalogueRefTrafo(null);
        config.setMultiSurfaceTrafo(null);
        config.setMultilingualTrafo(null);
        config.setInheritanceTrafo(null);
        config.setDefaultSrsCode("2056");
        setup.setXYParams(config);
        return config;
    }

    @Test
    public void importXtfWithStrokeArcs() throws Exception {
        setup.resetDb();
        Config config = getBaseConfig();
        Config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE);

        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertStrokeArcsGeometryValid(stmt);
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
    public void importXtfWithoutStrokeArcs() throws Exception {
        setup.resetDb();
        Config config = getBaseConfig();
        Config.setStrokeArcs(config, null);

        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

            assertGeometryWithoutStrokeArcs(stmt);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }

    protected abstract void assertStrokeArcsGeometryValid(Statement stmt) throws Exception;

    protected abstract void assertGeometryWithoutStrokeArcs(Statement stmt) throws Exception;
}
