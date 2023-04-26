package ch.ehi.ili2gpkg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class GpkgTestSetup extends AbstractTestSetup {
    
    private String gpkgFilename;
    private String dburl;
    private boolean doMultiGeomPerTable;
    public GpkgTestSetup(String gpkgFilename,String dburl) {
        this(gpkgFilename,dburl,false);
    }
    public GpkgTestSetup(String gpkgFilename,String dburl,boolean doMultiGeomPerTable) {
        this.gpkgFilename=gpkgFilename;
        this.dburl=dburl;
        this.doMultiGeomPerTable=doMultiGeomPerTable;
    }
    @Override
    public Config initConfig(String xtfFilename,String logfile) {
        Config config=new Config();
        new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);
        config.setOneGeomPerTable(!doMultiGeomPerTable);
        config.setDbfile(gpkgFilename);
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
        File gpkgFile=new File(gpkgFilename);
        if(gpkgFile.exists()){ 
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
    }
    @Override
    public Connection createDbSchema() throws SQLException {
        Config config=new Config();
        config.setDbfile(gpkgFilename);
        config.setDburl(dburl);
        GpkgMapping gpkgMapping=new GpkgMapping();
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
        new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);
    }

}
