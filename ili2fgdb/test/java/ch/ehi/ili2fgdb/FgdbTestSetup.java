package ch.ehi.ili2fgdb;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb;

public class FgdbTestSetup extends ch.ehi.ili2db.AbstractTestSetup {
    private String fgdbFilename;

    public FgdbTestSetup(String fgdbFilename) {
        super();
        this.fgdbFilename = fgdbFilename;
    }

    @Override
    public void setXYParams(Config config) {
        config.setValue(GeneratorFgdb.XY_RESOLUTION, "0.005");
        config.setValue(GeneratorFgdb.XY_TOLERANCE, "0.05");
    }
    
    @Override
    public Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2fgdb.FgdbMain().initConfig(config);
        config.setDbfile(fgdbFilename);
        config.setDburl(FgdbDriver.BASE_URL+fgdbFilename);
        if(logfile!=null){
            config.setLogfile(logfile);
        }
        config.setXtffile(xtfFilename);
        if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
            config.setItfTransferfile(true);
        }
        return config;
    }

    @Override
    public void initConfig(Config config) {
        new ch.ehi.ili2fgdb.FgdbMain().initConfig(config);
    }

    @Override
    public void resetDb() throws SQLException {
        File fgdbFile=new File(fgdbFilename);
        Fgdb4j.deleteFileGdb(fgdbFile);
    }

    @Override
    public Connection createConnection() throws SQLException {
        DriverManager.registerDriver(new FgdbDriver());
        
        Connection conn = DriverManager.getConnection(
                FgdbDriver.BASE_URL+fgdbFilename, null, null);
        return conn;
    }

}
