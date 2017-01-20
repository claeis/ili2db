package ch.ehi.ili2db.fromili;

import java.sql.Connection;

import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public interface CustomMapping {
	public void fromIliInit(ch.ehi.ili2db.gui.Config config);
	public void fromIliEnd(ch.ehi.ili2db.gui.Config config);
	public void fixupViewable(DbTable sqlTableDef,Viewable iliClassDef);
	public void fixupAttribute(DbTable sqlTableDef,DbColumn sqlColDef,AttributeDef iliAttrDef);
	public void fixupEmbeddedLink(DbTable dbTable,DbColumn dbColId,AssociationDef roleOwner,RoleDef role,DbTableName targetTable,String targetPk);
	public void preConnect(String url, String dbusr, String dbpwd, Config config);
	public void postConnect(Connection conn, Config config);

}
