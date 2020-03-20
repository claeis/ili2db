package ch.ehi.ili2ora;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class OraTestSetup extends AbstractTestSetup {
    private String dburl;
    private String dbuser;
    private String dbpwd;
    private String dbschema;
    private String dbtablespace;
    
    public OraTestSetup(String dburl, String dbuser, String dbpwd, String dbschema, String dbschemaPrefix, String dbtablespace) {
        this.dburl=dburl;
        this.dbuser=dbuser;
        this.dbpwd=dbpwd;
        this.dbschema=dbschemaPrefix!=null?dbschemaPrefix+dbschema:dbschema;
        this.dbtablespace=dbtablespace;
    }
    @Override
    public void initConfig(Config config) {
        new ch.ehi.ili2ora.OraMain().initConfig(config);
        if(dbschema!=null) {
            config.setDbschema(dbschema);
        }
    }

    @Override
    public Config initConfig(String xtfFilename, String logfile) {
        Config config=new Config();
        new ch.ehi.ili2ora.OraMain().initConfig(config);
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
    public void resetDb() throws SQLException {
        if(dbschema!=null) {
            Connection jdbcConnection=createConnection();
            try {
                Statement stmt=jdbcConnection.createStatement();
                try { // drop schema (user)
                    stmt.execute("DROP USER "+dbschema+" CASCADE");
                }catch(SQLSyntaxErrorException e) {
                    // if the error is not "dbschema not exists" throw the exception else continue 
                    if(e.getErrorCode()!=1918) {
                        throw e;
                    }
                } finally {
                    stmt.close();
                    stmt=null;
                }
                Statement createSchemaStmt=jdbcConnection.createStatement();
                
                try { // create schema (user)
                    String createUser="CREATE USER "+dbschema+" IDENTIFIED BY 123456";
                    if(dbtablespace!=null) createUser+= " DEFAULT TABLESPACE "+dbtablespace+" QUOTA 20M on "+dbtablespace;
                    createSchemaStmt.execute(createUser);
                } finally {
                    createSchemaStmt.close();
                    createSchemaStmt=null;
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
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return DriverManager.getConnection(dburl, dbuser, dbpwd);
    }

    @Override
    public String prefixName(String name) {
        if(dbschema==null) {
            return name;
        }
        return dbschema+"."+name;
    }
    
    @Override
    public String getSchema() {
        return dbschema;
    }
}
