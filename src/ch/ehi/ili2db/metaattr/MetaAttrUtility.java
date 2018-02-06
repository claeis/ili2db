package ch.ehi.ili2db.metaattr;

import java.util.Map;
import java.util.Iterator;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

import com.moandjiezana.toml.Toml;

import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.PredefinedModel;
import ch.interlis.ili2c.metamodel.DataModel;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.ehi.sqlgen.repository.DbColVarchar;

public class MetaAttrUtility{

	// Read meta-attributes from toml file and write them inte elements
	public static void importMetaAttrsFromToml(FileReader tomlFile, TransferDescription td)
	throws Ili2dbException
	{
		Toml toml = new Toml().read(tomlFile);
		for (Map.Entry<String, Object> entry : toml.entrySet()) {
			if(!toml.containsTable(entry.getKey())){
				continue;
			}
			Toml table = toml.getTable(entry.getKey());
			for (Map.Entry<String, Object> tableEntry : table.entrySet()) {
				
				//TODO verify if Toml4j can read directly quoted keys and why without quotes, the dotted keys doesn't work
				String ilielement = entry.getKey().replace("\"", "");
				
				// Add meta-attr to the Element
				Element element = td.getElement(entry.getKey().replace("\"", ""));
				element.setMetaValue(tableEntry.getKey(), (String)tableEntry.getValue());
			}
		}
	}

	// Update Elements meta-attributes from meta-attributes table
	public static void updateElementsMetaAttributes(Connection conn, String schema, TransferDescription td)
	throws Ili2dbException
	{
		String sqlName=DbNames.META_ATTRIBUTES_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String stmt="SELECT " +
				DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL + ", " + 
				DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL + ", " +
				DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL + " " +
				"FROM " + sqlName; 
				
			EhiLogger.traceBackendCmd(stmt);
			Statement dbstmt = conn.createStatement();
			ResultSet rs=dbstmt.executeQuery(stmt);

			while(rs.next()){
				String ilielement=rs.getString(DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL);
				String attrname=rs.getString(DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL);
				String attrvalue=rs.getString(DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL);
				
				// Add meta-attr to the Element
				Element element = td.getElement(ilielement);
				element.setMetaValue(attrname, attrvalue);
			}
			
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to read meta-attributes table", ex);
		}
	}
	
	// Create meta-attributes table
	public static void addMetaAttributesTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(), DbNames.META_ATTRIBUTES_TAB));

		DbColVarchar ilielementCol=new DbColVarchar();
		ilielementCol.setName(DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL);
		ilielementCol.setNotNull(true);
		ilielementCol.setSize(255);
		tab.addColumn(ilielementCol);
		DbColVarchar attrnameCol=new DbColVarchar();
		attrnameCol.setName(DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL);
		attrnameCol.setNotNull(true);
		attrnameCol.setSize(1024);
		tab.addColumn(attrnameCol);
		DbColVarchar attrvalueCol=new DbColVarchar();
		attrvalueCol.setName(DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL);
		attrvalueCol.setNotNull(true);
		attrvalueCol.setSize(1024);
		tab.addColumn(attrvalueCol);
		schema.addTable(tab);
	}

	// Read meta-attributes from elements and write them into table
	public static void updateMetaAttributesTable(java.sql.Connection conn, String schema, TransferDescription td) 
	throws Ili2dbException
	{
		Iterator transIter = td.iterator();
		while(transIter.hasNext()){
			Object transElem=transIter.next();
			if(transElem instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
				continue;
			}else if(transElem instanceof ch.interlis.ili2c.metamodel.DataModel){
				iterateDataModel(transElem, conn, schema);
			}
		}
	}

	// Recursively iterate data model and write all found meta-attributes
	private static void iterateDataModel(Object o, java.sql.Connection conn, String schema)
	throws Ili2dbException
	{
		Element el = (Element) o;
		Settings metaValues = el.getMetaValues();
		if(metaValues.getValues().size() > 0){
			for(String attr:metaValues.getValues()){
				insertMetaAttributeEntry(conn, schema, el.getScopedName(), attr, metaValues.getValue(attr));
			}
		}
		if(o instanceof Container){
			Container e = (Container) o;
			Iterator it = e.iterator();
			while(it.hasNext()){
				iterateDataModel(it.next(), conn, schema);
			}
		}
	}

	// Write meta-attribute into db
	private static void insertMetaAttributeEntry(Connection conn, String schema, String ilielement, String attrname, String attrvalue)
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
			PreparedStatement ps = conn.prepareStatement(stmt);
			ps.setString(1, ilielement);
			ps.setString(2, attrname);
			ps.setString(3, attrvalue);
			ps.executeUpdate();
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to insert meta-attribute", ex);
		}
	} 
}
