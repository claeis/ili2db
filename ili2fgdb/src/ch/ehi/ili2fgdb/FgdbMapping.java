package ch.ehi.ili2fgdb;

import java.io.File;
import java.sql.Connection;

import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public class FgdbMapping extends AbstractJdbcMapping {

	private boolean isNewFile=false;
	private String defaultXyResolution=null;
	private String defaultXyTolerance=null;
	
	@Override
	public void fromIliInit(Config config) {
		defaultXyResolution=config.getValue(GeneratorFgdb.XY_RESOLUTION);
		defaultXyTolerance=config.getValue(GeneratorFgdb.XY_TOLERANCE);
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
		if(sqlColDef instanceof DbColGeometry){
			sqlColDef.setCustomValue(GeneratorFgdb.XY_RESOLUTION, defaultXyResolution);
			sqlColDef.setCustomValue(GeneratorFgdb.XY_TOLERANCE, defaultXyTolerance);
		}
	}

	@Override
	public void fixupEmbeddedLink(DbTable dbTable, DbColumn dbColId,
			AssociationDef roleOwner, RoleDef role, DbTableName targetTable,
			String targetPk) {
	}

	@Override
	public void preConnect(String url, String dbusr, String dbpwd, Config config) {
		String fileName=config.getDbfile();
		if(new File(fileName).exists()){
			isNewFile=false;
		}else{
			isNewFile=true;
		}
	}

	@Override
	public void postConnect(Connection conn, Config config) {
		if(isNewFile){
		}
	}

}
