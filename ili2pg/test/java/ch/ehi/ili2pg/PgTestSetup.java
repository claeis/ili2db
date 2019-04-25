package ch.ehi.ili2pg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class PgTestSetup extends ch.ehi.ili2db.AbstractTestSetup {
    private String dburl;
    private String dbuser;
    private String dbpwd;
    private String dbschema;
    
    public PgTestSetup(String dburl, String dbuser, String dbpwd,String dbschema) {
        super();
        this.dburl = dburl;
        this.dbuser = dbuser;
        this.dbpwd = dbpwd;
        this.dbschema=dbschema;
    }

    @Override
    protected Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2pg.PgMain().initConfig(config);
        config.setDburl(dburl);
        config.setDbusr(dbuser);
        config.setDbpwd(dbpwd);
        if(dbschema!=null){
            config.setDbschema(dbschema);
        }
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
    protected void initConfig(Config config) {
        new ch.ehi.ili2pg.PgMain().initConfig(config);
        if(dbschema!=null) {
            config.setDbschema(dbschema);
        }
    }
    @Override
    protected String prefixName(String name) {
        if(dbschema==null) {
            return name;
        }
        return dbschema+"."+name;
    }

    @Override
    protected void resetDb() throws SQLException {
        if(dbschema!=null) {
            Connection jdbcConnection=createConnection();
            try {
                Statement stmt=jdbcConnection.createStatement();
                try {
                    stmt.execute("DROP SCHEMA IF EXISTS "+dbschema+" CASCADE");
                }finally {
                    stmt.close();
                    stmt=null;
                }
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
        
    }

    @Override
    protected Connection createConnection() throws SQLException {
        try {
            Class driverClass = Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        Connection jdbcConnection = DriverManager.getConnection(
                dburl, dbuser, dbpwd);
        return jdbcConnection;
    }
    @Override
    protected String getSchema() {
        return dbschema;
    }

}
