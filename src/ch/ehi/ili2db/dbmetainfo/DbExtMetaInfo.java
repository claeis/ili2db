package ch.ehi.ili2db.dbmetainfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.EnumerationType;

public class DbExtMetaInfo {
	public static final String TAG_COL_UNIT = Config.PREFIX+".unit";
	public static final String TAG_COL_TEXTKIND = Config.PREFIX+".textKind";
	public static final String TAG_COL_TEXTKIND_MTEXT = "MTEXT";
	public static final String TAG_TAB_TABLEKIND = Config.PREFIX+".tableKind";
	public static final String TAG_TAB_TABLEKIND_ENUM = "ENUM";
	public static final String TAG_TAB_TABLEKIND_SECONDARY = "SECONDARY";
	public static final String TAG_TAB_TABLEKIND_ASSOCIATION = "ASSOCIATION";
	public static final String TAG_TAB_TABLEKIND_STRUCTURE = "STRUCTURE";
	public static final String TAG_TAB_TABLEKIND_CATALOGUE = "CATALOGUE";
	HashMap<ColKey,HashMap<String,String>> colInfo=new HashMap<ColKey,HashMap<String,String>>();
	HashMap<String,HashMap<String,String>> tabInfo=new HashMap<String,HashMap<String,String>>();
	
	public void setColumnInfo(String table,String column, String tag,String value)
	{
		setColumnInfo(table, null, column, tag, value);
	}
	public void setColumnInfo(String table,String subtype,String column, String tag,String value)
	{
		ColKey key=new ColKey(table, subtype, column);
		HashMap<String,String> info=colInfo.get(key);
		if(info==null){
			info=new HashMap<String,String>(); 
			colInfo.put(key,info);
		}
		info.put(tag, value);
		
	}
	public void setTableInfo(String table,String tag,String value)
	{
		HashMap<String,String> info=tabInfo.get(table);
		if(info==null){
			info=new HashMap<String,String>(); 
			tabInfo.put(table,info);
		}
		info.put(tag, value);
	}
	public void updateMetaInfoTables(Connection conn,String schemaName)
	throws Ili2dbException
	{
		saveColumnTab(conn, schemaName);
		saveTableTab(conn, schemaName);
	}
	private void saveColumnTab(Connection conn,String schemaName)
	throws Ili2dbException
	{
		DbTableName tabName=new DbTableName(schemaName,DbNames.META_INFO_COLUMN_TAB);
		String sqlName=tabName.getQName();
		HashMap<ColKey,HashMap<String,String>> exstEntries=readColumnTab(conn,sqlName);
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+","+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+","+DbNames.META_INFO_COLUMN_TAB_TAG_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+") VALUES (?,?,?,?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			try{
				for(ColKey colKey:colInfo.keySet()){
					HashMap<String,String> exstValues=exstEntries.get(colKey);
					if(exstValues==null){
						exstValues=new HashMap<String,String>(); 
					}
					HashMap<String,String> newValues=colInfo.get(colKey);
					for(String tag:newValues.keySet()){
						if(!exstValues.containsKey(tag)){
							String value=newValues.get(tag);
							insPrepStmt.setString(1, colKey.getTable());
							insPrepStmt.setString(2, colKey.getSubtype());
							insPrepStmt.setString(3, colKey.getColumn());
							insPrepStmt.setString(4, tag);
							insPrepStmt.setString(5, value);
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
	private HashMap<ColKey, HashMap<String, String>> readColumnTab(
			Connection conn, String sqlName) throws Ili2dbException {
		HashMap<ColKey,HashMap<String,String>> exstEntries=new HashMap<ColKey,HashMap<String,String>>(); 
		try{

			// insert entries
			String selStmt="SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+","+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+","+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+","+DbNames.META_INFO_COLUMN_TAB_TAG_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(selStmt);
			java.sql.PreparedStatement selPrepStmt = conn.prepareStatement(selStmt);
			ResultSet rs = selPrepStmt.executeQuery();
			try{
				while(rs.next()){
					String table=rs.getString(1);
					String subtype=rs.getString(2);
					String col=rs.getString(3);
					String tag=rs.getString(4);
					String val=rs.getString(5);
					ColKey colKey=new ColKey(table,subtype,col);
					HashMap<String,String> exstValues=exstEntries.get(colKey);
					if(exstValues==null){
						exstValues=new HashMap<String,String>(); 
						exstEntries.put(colKey, exstValues);
					}
					exstValues.put(tag, val);
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read meta info values from "+sqlName,ex);
			}finally{
				selPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read meta-info table "+sqlName,ex);
		}
		return exstEntries;
	}
	private void saveTableTab(Connection conn,String schemaName)
	throws Ili2dbException
	{
		DbTableName tabName=new DbTableName(schemaName,DbNames.META_INFO_TABLE_TAB);
		String sqlName=tabName.getQName();
		HashMap<String,HashMap<String,String>> exstEntries=readTableTab(conn,sqlName);
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+DbNames.META_INFO_TABLE_TAB_TABLENAME_COL+","+DbNames.META_INFO_TABLE_TAB_TAG_COL+","+DbNames.META_INFO_TABLE_TAB_SETTING_COL+") VALUES (?,?,?)";
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
	private HashMap<String, HashMap<String, String>> readTableTab(
			Connection conn, String sqlName) throws Ili2dbException {
		HashMap<String,HashMap<String,String>> exstEntries=new HashMap<String,HashMap<String,String>>();
		try{

			// select entries
			String selStmt="SELECT "+DbNames.META_INFO_TABLE_TAB_TABLENAME_COL+","+DbNames.META_INFO_TABLE_TAB_TAG_COL+","+DbNames.META_INFO_TABLE_TAB_SETTING_COL+" FROM "+sqlName;
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
				selPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read meta-info table "+sqlName,ex);
		}
		
		return exstEntries;
	}
	static public void addMetaInfoTables(DbSchema schema)
	{
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.META_INFO_COLUMN_TAB));
			
			// qualified name of ili table name
			DbColVarchar tableName=new DbColVarchar();
			tableName.setName(DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL);
			tableName.setNotNull(true);
			tableName.setSize(255);
			tab.addColumn(tableName);
			
			// name of ili subtype
			DbColVarchar subType=new DbColVarchar();
			subType.setName(DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL);
			subType.setNotNull(false);
			subType.setSize(255);
			tab.addColumn(subType);
			
			// name of ili column name
			DbColVarchar columnName=new DbColVarchar();
			columnName.setName(DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL);
			columnName.setNotNull(true);
			columnName.setSize(255);
			tab.addColumn(columnName);
			
			// tag
			DbColVarchar tag=new DbColVarchar();
			tag.setName(DbNames.META_INFO_COLUMN_TAB_TAG_COL);
			tag.setNotNull(true);
			tag.setSize(1024);
			tab.addColumn(tag);
			
			// setting
			DbColVarchar setting=new DbColVarchar();
			setting.setName(DbNames.META_INFO_COLUMN_TAB_SETTING_COL);
			setting.setNotNull(true);
			setting.setSize(1024);
			tab.addColumn(setting);
			
			schema.addTable(tab);
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.META_INFO_TABLE_TAB));
			
			// qualified name of ili table name
			DbColVarchar tableName=new DbColVarchar();
			tableName.setName(DbNames.META_INFO_TABLE_TAB_TABLENAME_COL);
			tableName.setNotNull(true);
			tableName.setSize(255);
			tab.addColumn(tableName);
			
			// tag
			DbColVarchar tag=new DbColVarchar();
			tag.setName(DbNames.META_INFO_TABLE_TAB_TAG_COL);
			tag.setNotNull(true);
			tag.setSize(1024);
			tab.addColumn(tag);
			
			// setting
			DbColVarchar setting=new DbColVarchar();
			setting.setName(DbNames.META_INFO_TABLE_TAB_SETTING_COL);
			setting.setNotNull(true);
			setting.setSize(1024);
			tab.addColumn(setting);
			
			schema.addTable(tab);
		}
	}
}
