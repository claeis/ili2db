package ch.ehi.ili2duckdb;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class DuckDBTestSetup extends AbstractTestSetup {
    
    private String dbschema;
    private String duckdbFilename;
    private String dburl;
    
    public DuckDBTestSetup(String duckdbFilename, String dburl) {
        this(duckdbFilename,dburl,null);
    }
    
    public DuckDBTestSetup(String duckdbFilename, String dburl, String dbschema) {
        this.duckdbFilename = duckdbFilename;
        this.dburl = dburl;
        this.dbschema = dbschema;
    }
    
    @Override
    public Config initConfig(String xtfFilename, String logfile) {
        Config config = new Config();
        new DuckDBMain().initConfig(config);
        config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE);
        config.setDbfile(duckdbFilename);
        config.setDburl(dburl);
        if (dbschema != null) {
            config.setDbschema(dbschema);
        }
        if (logfile != null) {
            config.setLogfile(logfile);
        }
        config.setXtffile(xtfFilename);
        if (xtfFilename != null && Ili2db.isItfFilename(xtfFilename)) {
            config.setItfTransferfile(true);
        }
        return config;
    }
    
    @Override
    public String prefixName(String name) {
        if (dbschema == null) {
            return name;
        }
        return dbschema + "." + name;
    }

    @Override
    public String getSchema() {
        return dbschema;
    }
    
    @Override
    public void resetDb() throws SQLException {
//        if (dbschema != null) {
//            Connection jdbcConnection = createConnection();
//            try {
//                Statement stmt = jdbcConnection.createStatement();
//                try {
//                    stmt.execute("DROP SCHEMA IF EXISTS " + dbschema + " CASCADE");
//                } finally {
//                    stmt.close();
//                    stmt = null;
//                }
//            } finally {
//                jdbcConnection.close();
//                jdbcConnection = null;
//            }
//        }
        {
            // test.duckdb
            // test.duckdb.wal
            for (String ext : new String[] { "", ".wal" }) {
                File dbfile = new File(duckdbFilename + ext);
                if (dbfile.exists()) {
                    File file = new File(dbfile.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }
    
    @Override
    public Connection createDbSchema() throws SQLException {
        Config config=new Config();
        config.setDbfile(duckdbFilename);
        config.setDburl(dburl);
        DuckDBMapping duckdbMapping = new DuckDBMapping();
        duckdbMapping.preConnect(config.getDburl(), null, null, config);
        Connection jdbcConnection = createConnection();
        duckdbMapping.postConnect(jdbcConnection, config);
        return jdbcConnection;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        DriverManager.registerDriver(new org.duckdb.DuckDBDriver());
        return DriverManager.getConnection(dburl, null, null);
    }
    
    @Override
    public void initConfig(Config config) {
        new DuckDBMain().initConfig(config);
        config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE);
        if (dbschema != null) {
            config.setDbschema(dbschema);
        }
    }
    
    @Override
    public boolean supportsCompoundGeometry() {
        return false;
    }
}
