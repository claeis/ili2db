package ch.ehi.ili2db;

import ch.ehi.basics.logging.EhiLogger;
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
import java.sql.Statement;

import static org.junit.Assert.*;

public abstract class ValidationTest {

    protected static final String TEST_OUT = "test/data/Validation/";
    protected AbstractTestSetup setup = createTestSetup();

    protected abstract AbstractTestSetup createTestSetup();

    @Test
    public void importXtf() throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "Simple23a.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsCode("2056");
        config.setModels("Simple23;Simple23Zusatz");
        setup.setXYParams(config);
        Ili2db.run(config, null);

        Connection jdbcConnection = null;
        Statement stmt = null;
        try {
            jdbcConnection = setup.createConnection();
            stmt = jdbcConnection.createStatement();

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
    public void validate() throws Exception {
        importXtf();

        // export xtf
        File data = new File(TEST_OUT, "Simple23a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_VALIDATE);
        config.setModels("Simple23");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

    }
    @Test
    public void validateAdditionalModel_Fail() throws Exception {
        importXtf();

        // export xtf
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        File data = new File(TEST_OUT, "Simple23a-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_VALIDATE);
        config.setValidConfigFile(new File(TEST_OUT, "Simple23Zusatz.ini").getPath());
        config.setModels("Simple23");
        Ili2db.readSettingsFromDb(config);
        try {
            Ili2db.run(config, null);
            fail();
        }catch(Exception ex) {
            
        }
        assertEquals("Mandatory Constraint Simple23Zusatz.Simple23Zusatz.ClassA1View.Constraint1 is not true.",logCollector.getErrs().get(0).getEventMsg());
    }

}
