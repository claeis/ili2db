package ch.ehi.ili2mssql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class MsSqlTestSetup extends AbstractTestSetup {
    private String dburl;
    private String dbuser;
    private String dbpwd;
    private String dbschema;
    
    private String dropSchema;
    
    public MsSqlTestSetup(String dburl, String dbuser, String dbpwd,String dbschema) {
        super();
        this.dburl=dburl;
        this.dbuser=dbuser;
        this.dbpwd=dbpwd;
        this.dbschema=dbschema;
        try {
            dropSchema = this.getScript();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Config initConfig(String xtfFilename, String logfile) {
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
    protected void initConfig(Config config) {
        new ch.ehi.ili2mssql.MsSqlMain().initConfig(config);
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
                    String strStmt = dropSchema.replace("{{{schema}}}", dbschema);
                    stmt.execute(strStmt);
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
            Class driverClass = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
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

    private String getScript() throws java.io.IOException {
        File file = new File("test/data/MssqlBase/dropSchema.sql");
        java.io.InputStream is = new java.io.FileInputStream(file.getPath());
        java.io.BufferedReader buf = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
                
        while(line != null){
           sb.append(line).append("\n");
           line = buf.readLine();
        }
        return sb.toString();
    }
}
