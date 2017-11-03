package ch.ehi.ili2mssql;

import java.sql.Connection;

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

}
