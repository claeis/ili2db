package ch.ehi.ili2duckdb;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbTableName;

public class DuckDBMapping extends AbstractJdbcMapping {
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
        if(isNewFile!=null) {
            Statement dbstmt = null;
            try{
                try{
                    String line="INSTALL spatial;";
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(line);
                    dbstmt.execute(line);
                    line="LOAD spatial;";
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
        return GeneratorDuckDB.tableExists(conn, tableName);
    }
    
}
