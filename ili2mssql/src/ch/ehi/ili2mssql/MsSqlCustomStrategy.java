package ch.ehi.ili2mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;


public class MsSqlCustomStrategy  extends AbstractJdbcMapping {
    
    @Override
    public void prePreScript(Connection conn, Config config) {
        String sql="EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\";";
        executeStatement(conn, sql, "Failed to disable foreign keys");
    }
    
    @Override
    public void postPostScript(Connection conn, Config config){
        String sql="EXEC sp_msforeachtable \"ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all\";";
        executeStatement(conn, sql, "Failed to enable foreign keys");
    }

    @Override
    public String shortenConnectUrl4IliCache(String url) {
        return getMainPartUrl(url);
    }
    @Override
    public String shortenConnectUrl4Log(String url) {
        return getMainPartUrl(url);
    }

    @Override
    public String getCreateSchemaStmt(String dbschema) {
        String stmt = "IF NOT EXISTS (SELECT  schema_name FROM information_schema.schemata WHERE schema_name = '"+dbschema+"')";
        stmt += "EXEC sp_executesql N'CREATE SCHEMA "+dbschema+"'";
        return stmt;
    }
    
    private void executeStatement(Connection conn, String cmt, String errorMessage) {
        Statement dbstmt = null;
        try{
            try{
                dbstmt = conn.createStatement();
                EhiLogger.traceBackendCmd(cmt);
                dbstmt.execute(cmt);
            }finally {
                if(dbstmt!=null) dbstmt.close();
            }
        } catch(SQLException e){
            EhiLogger.logError(e);
            throw new IllegalStateException(errorMessage, e);
        }
    }
    
    private String getMainPartUrl(String url) {
        String[] parts = url.split(";");
        return parts[0]; //the first part contains host, port and instance
    }
}