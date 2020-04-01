package ch.ehi.ili2mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class MysqlTestSetup extends ch.ehi.ili2db.AbstractTestSetup {
    private String dbuser;
    private String dbpwd;
    private String dburl;
    public MysqlTestSetup(String dburl, String dbuser, String dbpwd) {
        super();
        this.dburl = dburl;
        this.dbuser = dbuser;
        this.dbpwd = dbpwd;
    }
    
    @Override
    public Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2mysql.MysqlMain().initConfig(config);
        config.setDburl(dburl);
        config.setDbusr(dbuser);
        config.setDbpwd(dbpwd);
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
        new ch.ehi.ili2mysql.MysqlMain().initConfig(config);
    }
    @Override
    public void resetDb() throws SQLException {
        Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql:///", dbuser, dbpwd);
        Statement stmt=jdbcConnection.createStatement();
        stmt.execute("DROP DATABASE IF EXISTS ili2db");
        stmt.execute("CREATE DATABASE IF NOT EXISTS ili2db");
        stmt.close();
        jdbcConnection.close();
        jdbcConnection=null;
    }
    @Override
    public Connection createConnection() throws SQLException {
        Connection jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        return jdbcConnection;
    }

}
