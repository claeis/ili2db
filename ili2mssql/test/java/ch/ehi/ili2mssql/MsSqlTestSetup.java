package ch.ehi.ili2mssql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2mssql.test_utils.TestUtils;

public class MsSqlTestSetup extends AbstractTestSetup {
    private String dburl;
    private String dbuser;
    private String dbpwd;
    private String dbschema;
    
    public MsSqlTestSetup(String dburl, String dbuser, String dbpwd,String dbschema) {
        super();
        this.dburl=dburl;
        this.dbuser=dbuser;
        this.dbpwd=dbpwd;
        this.dbschema=dbschema;
    }

    @Override
    public Config initConfig(String xtfFilename, String logfile) {
        Config config=new Config();
        new ch.ehi.ili2mssql.MsSqlMain().initConfig(config);
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
    public void initConfig(Config config) {
        new ch.ehi.ili2mssql.MsSqlMain().initConfig(config);
        if(dbschema!=null) {
            config.setDbschema(dbschema);
        }    
    }

    @Override
    public String prefixName(String name) {
        if(dbschema==null) {
            return name;
        }
        return dbschema+"."+name;
    }

    @Override
    public void resetDb() throws SQLException {
        if(dbschema!=null) {
            Connection jdbcConnection=createConnection();
            try {
                Statement stmt=jdbcConnection.createStatement();
                try {
                    String dropScript = TestUtils.getDropScript(dbschema);
                    stmt.execute(dropScript);
                } catch(IOException e) {
                    throw new SQLException("Could not load drop schema script file");
                }
                finally {
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
    public Connection createConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return DriverManager.getConnection(dburl, dbuser, dbpwd);
    }

    @Override
    public String getSchema() {
        return dbschema;
    }
    @Override
    public Connection createDbSchema() throws SQLException {
        if(dbschema!=null) {
            Connection jdbcConnection=createConnection();
            try {
                Statement stmt=jdbcConnection.createStatement();
                try {
                    stmt.execute("CREATE SCHEMA "+dbschema+";");
                }finally {
                    stmt.close();
                    stmt=null;
                }
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
        return createConnection();
    }
}
