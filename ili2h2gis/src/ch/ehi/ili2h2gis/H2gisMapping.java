package ch.ehi.ili2h2gis;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2gis.utilities.SFSUtilities;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbTableName;

public class H2gisMapping extends AbstractJdbcMapping {
    private Boolean isNewFile=null;
    
    @Override
    public void preConnect(String url, String dbusr, String dbpwd, Config config) {
        String fileName=config.getDbfile();
        if(fileName!=null) {
            if(new File(fileName).exists()){
                isNewFile=false;
            }else{
                isNewFile=true;
            }
        }
    }
    @Override
    public void postConnect(Connection conn, Config config) {
        if(isNewFile!=null && isNewFile){
            Statement dbstmt = null;
            try{
                try{
                    String line="CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR \"org.h2gis.functions.factory.H2GISFunctions.load\";";
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(line);
                    dbstmt.execute(line);
                    line="CREATE ALIAS IF NOT EXISTS H2GIS_UNLOAD FOR \"org.h2gis.functions.factory.H2GISFunctions.unRegisterH2GISFunctions\";";
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(line);
                    dbstmt.execute(line);
                    line="CALL H2GIS_SPATIAL();";
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(line);
                    dbstmt.execute(line);
                }finally{
                    if(dbstmt!=null) {
                        dbstmt.close();
                        dbstmt=null;
                    }
                }
            }catch(SQLException ex){
                throw new IllegalStateException(ex);
            }
        }
    }
    @Override
    public void postPostScript(Connection conn, Config config)
    {
        Statement dbstmt = null;
        try{
            try{
                //String line="CALL H2GIS_UNLOAD();";
                //dbstmt = conn.createStatement();
                //EhiLogger.traceBackendCmd(line);
                //dbstmt.execute(line);
            }finally{
                if(dbstmt!=null) {
                    dbstmt.close();
                    dbstmt=null;
                }
            }
        }catch(SQLException ex){
            throw new IllegalStateException(ex);
        }
        
    }
    
    @Override
    public boolean tableExists(Connection conn,DbTableName tableName)
    {
        return GeneratorH2gis.tableExists(conn, tableName);
    }
    
}
