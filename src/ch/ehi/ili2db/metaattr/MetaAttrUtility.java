package ch.ehi.ili2db.metaattr;

import java.util.Iterator;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;


import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iox_j.inifile.IniFileReader;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.ili2c.metamodel.Container;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.repository.DbColVarchar;

public class MetaAttrUtility{

	/** Read meta-attributes from a toml file and add them to the ili2c metamodel.
	 * @param td ili-model as read by the ili compiler
	 * @param tomlFile
	 * @throws Ili2dbException
	 */
	public static void addMetaAttrsFromConfigFile(TransferDescription td, File configFile)
	throws java.io.IOException
	{
        ValidationConfig config=IniFileReader.readFile(configFile);
        for(String iliQName:config.getIliQnames()) {
            Element element = td.getElement(iliQName);
            // known element?
            if(element!=null) {
                for(String paramName:config.getConfigParams(iliQName)) {
                    String paramValue=config.getConfigValue(iliQName, paramName);
                    if(element.getMetaValue(paramName)==null) {
                        // define/set it
                        element.setMetaValue(paramName, paramValue);
                    }
                }
            }            
            
      }
	}

	/** Read meta-attributes from the db and add them to the ili2c metamodel.
	 * @param td
	 * @param conn
	 * @param schema
	 * @throws Ili2dbException
	 */
	public static void addMetaAttrsFromDb(TransferDescription td, Connection conn, String schema)
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
				// known element?
				if(element!=null) {
				    // meta-attr not yet set/defined?
				    if(element.getMetaValue(attrname)==null){
				        // set it to the read value
	                    element.setMetaValue(attrname, attrvalue);
				    }
				}
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

	/** Read meta-attributes from ili2c metamodel and write them into db table.
	 * @param gen 
	 * @param conn db connection
	 * @param schema
	 * @param td ili model as read by the interlis compiler
	 * @throws Ili2dbException
	 */
	public static void updateMetaAttributesTable(GeneratorJdbc gen, java.sql.Connection conn, String schema, TransferDescription td) 
	throws Ili2dbException
	{
	    // TODO insert/update instead of insert
		Iterator transIter = td.iterator();
		while(transIter.hasNext()){
			Object transElem=transIter.next();
			if(transElem instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
				continue;
			}else if(transElem instanceof ch.interlis.ili2c.metamodel.DataModel){
				visitElement(gen,(Element)transElem, conn, schema);
			}
		}
	}

	// Recursively iterate data model and write all found meta-attributes
	private static void visitElement(GeneratorJdbc gen, Element el, java.sql.Connection conn, String schema)
	throws Ili2dbException
	{
		Settings metaValues = el.getMetaValues();
		if(metaValues.getValues().size() > 0){
			for(String attr:metaValues.getValues()){
				insertMetaAttributeEntry(gen,conn, schema, el.getScopedName(), attr, metaValues.getValue(attr));
			}
		}
		if(el instanceof Container){
			Container e = (Container) el;
			Iterator it = e.iterator();
			while(it.hasNext()){
				visitElement(gen,(Element)it.next(), conn, schema);
			}
		}
	}

	// Write meta-attribute into db
	private static void insertMetaAttributeEntry(GeneratorJdbc gen, Connection conn, String schema, String ilielement, String attrname, String attrvalue)
	throws Ili2dbException
	{
		String sqlName=DbNames.META_ATTRIBUTES_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(conn!=null) {
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
		if(gen!=null){
            String stmt="INSERT INTO "+sqlName+" (" + 
                    DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL + "," + 
                    DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL + "," +
                    DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL +
                    ") VALUES ("+Ili2db.quoteSqlStringValue(ilielement)+","+ Ili2db.quoteSqlStringValue(attrname)+","+ Ili2db.quoteSqlStringValue(attrvalue)+")";
            gen.addCreateLine(gen.new Stmt(stmt));
		}
	} 
}
