package ch.ehi.ili2pg;

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

public class PgCustomStrategy extends AbstractJdbcMapping {

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
	public void fixupAttribute(DbTable sqlTableDef, DbColumn sqlColDef,
			AttributeDef iliAttrDef) {
	}

	@Override
	public void fixupEmbeddedLink(DbTable dbTable, DbColumn dbColId,
			AssociationDef roleOwner, RoleDef role, DbTableName targetTable,
			String targetPk) {
	}

	private final static String[] pgExtensions = new String[]{
			"postgis", "\"uuid-ossp\""
	};

	@Override
	public void postConnect(Connection conn, Config config) {
		// setupPgExt
		if(config.isSetupPgExt()){
			java.sql.Statement stmt=null;
			try {
				stmt=conn.createStatement();
				String sql=null;
				for(String pgExtension : pgExtensions){
					try {
						sql="CREATE EXTENSION IF NOT EXISTS "+pgExtension+";";
						EhiLogger.traceBackendCmd(sql);
						stmt.execute(sql);
					} catch(SQLException e){
						throw new IllegalStateException("failed to create extension "+pgExtension, e);
					}
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
}
