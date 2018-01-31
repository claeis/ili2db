package ch.ehi.ili2db.metaattr;

import java.util.Map;
import java.io.FileReader;
import java.sql.Connection;
import com.moandjiezana.toml.Toml;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;

public class MetaAttrUtility{

	// Read meta-attributes from toml file and write them into db
	public static void importMetaAttrsFromToml(FileReader tomlFile, Connection conn, String schema)
	throws Ili2dbException
	{
		Toml toml = new Toml().read(tomlFile);
		for (Map.Entry<String, Object> entry : toml.entrySet()) {
			if(!toml.containsTable(entry.getKey())){
				continue;
			}
			Toml table = toml.getTable(entry.getKey());
			for (Map.Entry<String, Object> tableEntry : table.entrySet()) {
				insertMetaAttributeEntry(conn, schema, entry.getKey(), tableEntry.getKey(), (String)tableEntry.getValue());
			}
		}
	}

	// Write meta-attribute into db
	public static void insertMetaAttributeEntry(Connection conn, String schema, String ilielement, String attrname, String attrvalue)
	throws Ili2dbException
	{
		String sqlName=DbNames.META_ATTRIBUTES_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String stmt="INSERT INTO "+sqlName+" (" + 
				DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL + "," + 
				DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL + "," +
				DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL +
				") VALUES (?, ?, ?)";
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
			ps.setString(1, ilielement);
			ps.setString(2, attrname);
			ps.setString(3, attrvalue);
			ps.executeUpdate();
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to insert meta-attribute", ex);
		}
	} 

}
