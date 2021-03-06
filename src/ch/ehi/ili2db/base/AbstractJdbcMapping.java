package ch.ehi.ili2db.base;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public abstract class AbstractJdbcMapping implements CustomMapping {
	@Override
	public Connection connect(String url, String dbusr, String dbpwd,
			Config config) throws SQLException {
	    java.util.Properties params=config.getDbProperties();
	    if(params!=null && !params.isEmpty()) {
	        if(dbusr!=null) {
	            params.setProperty("user", dbusr);
	        }
	        if(dbpwd!=null) {
	            params.setProperty("password", dbpwd);
	        }
	        return DriverManager.getConnection(url, params);
	    }
		return DriverManager.getConnection(url, dbusr, dbpwd);
	}

    @Override
    public void prePreScript(Connection conn, Config config)
    {
        
    }
    @Override
    public void postPostScript(Connection conn, Config config)
    {
        
    }
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
    public String shortenConnectUrl4IliCache(String url) {
        return url;
    }
    @Override
    public String shortenConnectUrl4Log(String url) {
        return url;
    }
    @Override
    public String getCreateSchemaStmt(String dbschema) {
        return "CREATE SCHEMA IF NOT EXISTS "+dbschema;
    }
    @Override
    public boolean tableExists(Connection conn,DbTableName tableName)
    {
        return DbUtility.tableExists(conn, tableName);
    }

}
