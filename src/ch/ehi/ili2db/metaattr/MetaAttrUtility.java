package ch.ehi.ili2db.metaattr;

import java.util.HashMap;
import java.util.Iterator;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;


import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iox_j.inifile.IniFileReader;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.ili2c.metamodel.Cardinality;
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
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc.Stmt;
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
		attrvalueCol.setSize(DbNames.SETTING_COL_SIZE);
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
	    HashMap<String,HashMap<String,String>> entries=new HashMap<String,HashMap<String,String>>();
		Iterator transIter = td.iterator();
		while(transIter.hasNext()){
			Object transElem=transIter.next();
			if(transElem instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
				continue;
			}else if(transElem instanceof ch.interlis.ili2c.metamodel.DataModel){
				visitElement(entries,(Element)transElem);
			}
		}
		saveTableTab(gen,conn,schema,entries);
	}

	// Recursively iterate data model and write all found meta-attributes
	private static void visitElement(HashMap<String,HashMap<String,String>> entries, Element el)
	throws Ili2dbException
	{
		Settings metaValues = el.getMetaValues();
		if(metaValues.getValues().size() > 0){
			for(String attr:metaValues.getValues()){
	            HashMap<String,String> exstValues=getMetaValues(entries,el);
                exstValues.put(attr, metaValues.getValue(attr));
			}
		}
        if(el instanceof RoleDef){
            RoleDef role=(RoleDef)el;
            HashMap<String,String> exstValues=getMetaValues(entries,el);
            exstValues.put("ili.assocKind", mapRoleKind(role.getKind()));
            exstValues.put("ili.assocCardinalityMin", mapCardinality(role.getCardinality().getMinimum()));
            exstValues.put("ili.assocCardinalityMax", mapCardinality(role.getCardinality().getMaximum()));
        }
		if(el instanceof Container){
			Container e = (Container) el;
			Iterator it = e.iterator();
			while(it.hasNext()){
				visitElement(entries,(Element)it.next());
			}
		}
	}

	
	  private static String mapCardinality(long val) {
	      if (val == Cardinality.UNBOUND) {
	        return "*";
	      }
	        return Long.toString(val);
	    }
	
    private static String mapRoleKind(int kind) {
        if(kind==RoleDef.Kind.eAGGREGATE) {
            return "AGGREGATE";
        }else if(kind==RoleDef.Kind.eCOMPOSITE) {
            return "COMPOSITE";
        }
        return "ASSOCIATE";
    }

    private static HashMap<String, String> getMetaValues(HashMap<String,HashMap<String,String>> entries,Element el) {
        HashMap<String,String> exstValues=entries.get(el.getScopedName());
        if(exstValues==null){
            exstValues=new HashMap<String,String>(); 
            entries.put(el.getScopedName(), exstValues);
        }
        return exstValues;
    }

    private static void saveTableTab(GeneratorJdbc gen, Connection conn,String schemaName,HashMap<String,HashMap<String,String>> tabInfo)
    throws Ili2dbException
    {
        DbTableName tabName=new DbTableName(schemaName,DbNames.META_ATTRIBUTES_TAB);
        String sqlName=tabName.getQName();
        if(conn!=null) {
            HashMap<String,HashMap<String,String>> exstEntries=readTableTab(conn,sqlName);
            try{

                // insert entries
                String insStmt="INSERT INTO "+sqlName+" ("+DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL+") VALUES (?,?,?)";
                EhiLogger.traceBackendCmd(insStmt);
                java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
                try{
                    for(String table:tabInfo.keySet()){
                        HashMap<String,String> exstValues=exstEntries.get(table);
                        if(exstValues==null){
                            exstValues=new HashMap<String,String>(); 
                        }
                        HashMap<String,String> newValues=tabInfo.get(table);
                        for(String tag:newValues.keySet()){
                            if(!exstValues.containsKey(tag)){
                                String value=newValues.get(tag);
                                insPrepStmt.setString(1, table);
                                insPrepStmt.setString(2, tag);
                                insPrepStmt.setString(3, value);
                                insPrepStmt.executeUpdate();
                            }
                        }
                    }
                }catch(java.sql.SQLException ex){
                    throw new Ili2dbException("failed to insert meta info values to "+sqlName,ex);
                }finally{
                    insPrepStmt.close();
                }
            }catch(java.sql.SQLException ex){       
                throw new Ili2dbException("failed to update meta-info table "+sqlName,ex);
            }
        }
        if(gen!=null){
            for(String table:tabInfo.keySet()){
                HashMap<String,String> newValues=tabInfo.get(table);
                for(String tag:newValues.keySet()){
                    String value=newValues.get(tag);
                    String insStmt="INSERT INTO "+sqlName+" ("+DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL
                            +") VALUES ('"+table+"','"+tag+"','"+value+"')";
                    gen.addCreateLine(gen.new Stmt(insStmt));
                }
            }
        }
        
    }
    private static HashMap<String, HashMap<String, String>> readTableTab(
            Connection conn, String sqlName) throws Ili2dbException {
        HashMap<String,HashMap<String,String>> exstEntries=new HashMap<String,HashMap<String,String>>();
        try{

            // select entries
            String selStmt="SELECT "+DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL+","+DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL+" FROM "+sqlName;
            EhiLogger.traceBackendCmd(selStmt);
            java.sql.PreparedStatement selPrepStmt = conn.prepareStatement(selStmt);
            ResultSet rs = selPrepStmt.executeQuery();
            try{
                while(rs.next()){
                    String table=rs.getString(1);
                    String tag=rs.getString(2);
                    String value=rs.getString(3);
                    HashMap<String,String> exstValues=exstEntries.get(table);
                    if(exstValues==null){
                        exstValues=new HashMap<String,String>(); 
                        exstEntries.put(table, exstValues);
                    }
                    exstValues.put(tag, value);
                }
            }catch(java.sql.SQLException ex){
                throw new Ili2dbException("failed to read meta info values from "+sqlName,ex);
            }finally{
                rs.close();
                selPrepStmt.close();
            }
        }catch(java.sql.SQLException ex){       
            throw new Ili2dbException("failed to read meta-info table "+sqlName,ex);
        }
        
        return exstEntries;
    }
	
}
