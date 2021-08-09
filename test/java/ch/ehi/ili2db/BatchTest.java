package ch.ehi.ili2db;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class BatchTest {

    protected static final String TEST_OUT="test/data/BatchSize/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;

    @Test
    public void importXtf() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"BatchSize.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setCreateNumChecks(true);
        config.setBatchSize(50);
        Ili2db.run(config,null);

        Connection jdbcConnection = setup.createConnection();
        try{
            Statement stmt=jdbcConnection.createStatement();
            Assert.assertTrue(stmt.execute("SELECT ST_X(lcoord),ST_Y(lcoord) FROM "+setup.prefixName("ClassKoord2")+" WHERE t_ili_tid = 'Coord2.1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(2460001.000,rs.getDouble(1),0.00005);
                Assert.assertEquals(1045001.000,rs.getDouble(2),0.00005);
            }
        }finally {
            jdbcConnection.close();
            jdbcConnection=null;
        }
    }

}
