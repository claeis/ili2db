package ch.ehi.ili2db;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class FetchTest {

    protected static final String TEST_OUT = "test/data/FetchSize/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void exportXtf() throws Exception {
        {
            importIli();
            fillRecords();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data = new File(TEST_OUT, "FetchSize-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("FetchSize");
        config.setExportTid(true);
        config.setFetchSize(100);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);
        XtfReader reader = new XtfReader(data);
        assertTrue(reader.read() instanceof StartTransferEvent);
        assertTrue(reader.read() instanceof StartBasketEvent);

        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        IoxEvent event = reader.read();
        while (event instanceof ObjectEvent) {
            IomObject iomObj = ((ObjectEvent) event).getIomObject();
            objs.put(iomObj.getobjectoid(), iomObj);
            event = reader.read();
        }
        assertEquals(1000, objs.size());
    }

    private void importIli() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "FetchSize.ili");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.NULL);
        Ili2db.run(config, null);
    }

    private void fillRecords() throws SQLException {
        Connection jdbcConnection = setup.createConnection();
        try {
            Statement stmt = jdbcConnection.createStatement();
            for (int i = 0; i < 1000; i++) {
                String t_ili_tid = "o" + i;
                String attr1 = "bla bla bla" + i;
                Integer attr2 = i;

                stmt.execute("INSERT INTO " + setup.prefixName("classa1")+"(t_ili_tid,attr1,attr2) VALUES ('" + t_ili_tid + "', '" + attr1 + "', '" + attr2 + "')");
            }
        }finally {
            jdbcConnection.close();
        }
    }

}
