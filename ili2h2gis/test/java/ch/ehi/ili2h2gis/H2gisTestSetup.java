package ch.ehi.ili2h2gis;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class H2gisTestSetup extends AbstractTestSetup {
    
    private String dbschema;
    private String h2gisFilename;
    private String dburl;
    
    public H2gisTestSetup(String gpkgFilename,String dburl) {
        this(gpkgFilename,dburl,null);
    }
    public H2gisTestSetup(String gpkgFilename,String dburl,String dbschema) {
        this.h2gisFilename=gpkgFilename;
        this.dburl=dburl;
        this.dbschema=dbschema;
    }
    @Override
    public Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2h2gis.H2gisMain().initConfig(config);
        config.setDbfile(h2gisFilename);
        config.setDburl(dburl);
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
    
    @Override
    public void resetDb() throws SQLException {

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
        {
            // test.mv.db
            // test.newFile
            // test.tempFile
            // test.h2.db
            // test.lock.db 
            // test.trace.db 
            for(String ext:new String[] {".mv.db",".newFile",".tempFile",".h2.db",".lock.db",".trace.db"}) {
                File dbfile=new File(h2gisFilename+ext);
                if(dbfile.exists()){ 
                    File file = new File(dbfile.getAbsolutePath());
                    file.delete();
                }
            }
        }
        
    }
    @Override
    public Connection createDbSchema() throws SQLException {
        Config config=new Config();
        config.setDbfile(h2gisFilename);
        config.setDburl(dburl);
        H2gisMapping gpkgMapping=new H2gisMapping();
        gpkgMapping.preConnect(config.getDburl(),null,null,config);
        Connection jdbcConnection=createConnection();
        gpkgMapping.postConnect(jdbcConnection, config);
        return jdbcConnection;
    }
    @Override
    public Connection createConnection() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());
        return DriverManager.getConnection(dburl, null, null);
    }
    @Override
    public void initConfig(Config config) {
        new ch.ehi.ili2h2gis.H2gisMain().initConfig(config);
        if(dbschema!=null) {
            config.setDbschema(dbschema);
        }
    }
    @Override
    public boolean supportsCompoundGeometry(){
        return false;
    }

}
