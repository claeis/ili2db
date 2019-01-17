package ch.ehi.ili2mysql;

import java.sql.Connection;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public class MysqlCustomStrategy extends AbstractJdbcMapping {
    @Override
    public void prePreScript(Connection conn, Config config)
    {
        java.sql.Statement stmt=null;
        try {
            stmt=conn.createStatement();
            String sql=null;

            try {
                sql="SET  foreign_key_checks=0;";
                EhiLogger.traceBackendCmd(sql);
                stmt.execute(sql);
            } catch(SQLException e){
                throw new IllegalStateException("failed to disable foreign keys", e);
            }
            
        } catch (SQLException e) {
            EhiLogger.logError(e);
        }finally{
            if(stmt!=null){
                try {
                    stmt.close();
                    stmt=null;
                } catch (SQLException e) {
                    EhiLogger.logError(e);
                }
            }
        }
    }
    
    @Override
    public void postPostScript(Connection conn, Config config)
    {
        java.sql.Statement stmt=null;
        try {
            stmt=conn.createStatement();
            String sql=null;

            try {
                sql="SET  foreign_key_checks=1;";
                EhiLogger.traceBackendCmd(sql);
                stmt.execute(sql);
            } catch(SQLException e){
                throw new IllegalStateException("failed to enable foreign keys", e);
            }
            
        } catch (SQLException e) {
            EhiLogger.logError(e);
        }finally{
            if(stmt!=null){
                try {
                    stmt.close();
                    stmt=null;
                } catch (SQLException e) {
                    EhiLogger.logError(e);
                }
            }
        }
    }


}
