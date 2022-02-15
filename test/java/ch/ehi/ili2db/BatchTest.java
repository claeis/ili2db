package ch.ehi.ili2db;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class BatchTest {

    protected static final String TEST_OUT = "test/data/BatchSize/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data = new File(TEST_OUT, "BatchSize.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setCreateNumChecks(true);
        config.setBatchSize(50);
        Ili2db.run(config, null);

        Connection jdbcConnection = setup.createConnection();
        try {
            Statement stmt = jdbcConnection.createStatement();
            Assert.assertTrue(stmt.execute("SELECT * FROM " + setup.prefixName("classa1")));
            {
                ResultSet rs = stmt.getResultSet();
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                Assert.assertEquals(1000, count);
            }
        } finally {
            jdbcConnection.close();
        }
    }

}
