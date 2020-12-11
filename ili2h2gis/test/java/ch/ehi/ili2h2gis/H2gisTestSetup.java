package ch.ehi.ili2h2gis;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class H2gisTestSetup extends AbstractTestSetup {
    
    private String h2gisFilename;
    private String dburl;
    public H2gisTestSetup(String gpkgFilename,String dburl) {
        this.h2gisFilename=gpkgFilename;
        this.dburl=dburl;
    }
    @Override
    public Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2h2gis.H2gisMain().initConfig(config);
        config.setDbfile(h2gisFilename);
        config.setDburl(dburl);
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
    public void resetDb() {
        
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
        return DriverManager.getConnection(dburl, null, null);
    }
    @Override
    public void initConfig(Config config) {
        new ch.ehi.ili2h2gis.H2gisMain().initConfig(config);
    }

}
