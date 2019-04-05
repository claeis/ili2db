package ch.ehi.ili2mssql;

import java.sql.Connection;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public class MsSqlCustomStrategy  extends AbstractJdbcMapping {

	@Override
	public void fromIliInit(Config config) {
		
	}

	@Override
	public void fromIliEnd(Config config) {
		
	}

	@Override
	public void fixupViewable(DbTable sqlTableDef, Viewable iliClassDef) {
		
	}

	@Override
	public void fixupAttribute(DbTable sqlTableDef, DbColumn sqlColDef, AttributeDef iliAttrDef) {
		
	}

	@Override
	public void fixupEmbeddedLink(DbTable dbTable, DbColumn dbColId, AssociationDef roleOwner, RoleDef role,
			DbTableName targetTable, String targetPk) {
	}

	@Override
	public void preConnect(String url, String dbusr, String dbpwd, Config config) {
		
	}

	@Override
	public void postConnect(Connection conn, Config config) {
		
	}
	
	@Override
	public void prePreScript(Connection conn, Config config)
	{
		java.sql.Statement stmt=null;
		try {
			stmt=conn.createStatement();
			String sql=null;

			try {
				sql="EXEC sp_msforeachtable \"ALTER TABLE ? NOCHECK CONSTRAINT all\";";
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
				sql="EXEC sp_msforeachtable \"ALTER TABLE ? WITH CHECK CHECK CONSTRAINT all\";";
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
	
	@Override
	public String shortenConnectUrl4IliCache(String url) {
		String[] parts = url.split(";");
		return parts[0]; //the first part contains host, port and instance
	}
    @Override
    public String shortenConnectUrl4Log(String url) {
        String[] parts = url.split(";");
        return parts[0]; //the first part contains host, port and instance
    }
}
