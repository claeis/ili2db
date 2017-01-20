package ch.ehi.ili2db.mapping;

import java.util.HashMap;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Viewable;

public class TrafoConfig {

	
	HashMap<String,HashMap<String,String>> config=null;
	public void readTrafoConfig(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		config=read(conn,schema);
	}
	private static HashMap<String,HashMap<String,String>> read(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashMap<String,HashMap<String,String>> settings=new HashMap<String,HashMap<String,String>>();
		String sqlName=DbNames.TRAFO_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(DbUtility.tableExists(conn,new DbTableName(schema,DbNames.TRAFO_TAB))){
			try{
				// select entries
				String insStmt="SELECT "+DbNames.TRAFO_TAB_ILINAME_COL+","+DbNames.TRAFO_TAB_TAG_COL+","+DbNames.TRAFO_TAB_SETTING_COL+" FROM "+sqlName;
				EhiLogger.traceBackendCmd(insStmt);
				java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
				try{
					java.sql.ResultSet rs=insPrepStmt.executeQuery();
					while(rs.next()){
						int valIdx=1;
						String iliname=rs.getString(valIdx++);
						String tag=rs.getString(valIdx++);
						String value=rs.getString(valIdx++);
						setSetting(settings, iliname, tag, value);
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to read "+sqlName,ex);
				}finally{
					insPrepStmt.close();
				}
			}catch(java.sql.SQLException ex){		
				throw new Ili2dbException("failed to read "+sqlName,ex);
			}
		}
		return settings;
	}
	public void updateTrafoConfig(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{

		HashMap<String, HashMap<String, String>> existingEntries = read(conn,schema);
		String sqlName=DbNames.TRAFO_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{

			// update entries
			{
				//UPDATE table_name
				//SET column1=value1,column2=value2,...
				//WHERE some_column=some_value;
				String updStmt="UPDATE "+sqlName+" SET "+DbNames.TRAFO_TAB_SETTING_COL+"=? WHERE "+DbNames.TRAFO_TAB_ILINAME_COL+"=? AND "+DbNames.TRAFO_TAB_TAG_COL+"=?";
				EhiLogger.traceBackendCmd(updStmt);
				java.sql.PreparedStatement updPrepStmt = conn.prepareStatement(updStmt);
				try{
					;
					for(String iliname:config.keySet()){
						HashMap<String, String> values=config.get(iliname);
						for(String tag:values.keySet()){
							if(containsSetting(existingEntries,iliname,tag)){
								// update
								String value=values.get(tag);
								updPrepStmt.clearParameters();
								updPrepStmt.setString(1, value);
								updPrepStmt.setString(2, iliname);
								updPrepStmt.setString(3, tag);
								updPrepStmt.executeUpdate();
							}
						}
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to update trafo",ex);
				}finally{
					updPrepStmt.close();
				}
				
			}
			// insert entries
			{
				String insStmt="INSERT INTO "+sqlName+" ("+DbNames.TRAFO_TAB_ILINAME_COL+","+DbNames.TRAFO_TAB_TAG_COL+","+DbNames.TRAFO_TAB_SETTING_COL+") VALUES (?,?,?)";
				EhiLogger.traceBackendCmd(insStmt);
				java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
				try{
					;
					for(String iliname:config.keySet()){
						HashMap<String, String> values=config.get(iliname);
						for(String tag:values.keySet()){
							if(!containsSetting(existingEntries,iliname,tag)){
								// insert
								String value=values.get(tag);
								insPrepStmt.clearParameters();
								insPrepStmt.setString(1, iliname);
								insPrepStmt.setString(2, tag);
								insPrepStmt.setString(3, value);
								insPrepStmt.executeUpdate();
							}
						}
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to insert trafo",ex);
				}finally{
					insPrepStmt.close();
				}
				
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update trafo-table "+sqlName,ex);
		}

	}
	private static boolean containsSetting(
			HashMap<String, HashMap<String, String>> settings,
			String iliname, String tag) {
		HashMap<String,String> values=null;
		if(settings.containsKey(iliname)){
			values=settings.get(iliname);
		}else{
			return false;
		}
		return values.containsKey(tag);
	}
	private static String getSetting(
			HashMap<String, HashMap<String, String>> settings,
			String iliname, String tag) {
		HashMap<String,String> values=null;
		if(settings.containsKey(iliname)){
			values=settings.get(iliname);
		}else{
			return null;
		}
		return values.get(tag);
	}
	private static void setSetting(
			HashMap<String, HashMap<String, String>> settings,
			String iliname, String tag,String value) {
		HashMap<String,String> values=null;
		if(settings.containsKey(iliname)){
			values=settings.get(iliname);
		}else{
			values=new HashMap<String,String>();
			settings.put(iliname, values);
		}
		values.put(tag, value);
	}
	private static String getIliQname(AttributeDef attr) {
		return attr.getContainer().getScopedName(null)+"."+attr.getName();
	}
	public String getAttrConfig(AttributeDef attr, String tag) {
		String iliname=getIliQname(attr);
		return getSetting(config, iliname, tag);
	}
	public String getViewableConfig(Viewable aclass, String tag) {
		String iliname=aclass.getScopedName(null);
		return getSetting(config, iliname, tag);
	}
	public void setAttrConfig(AttributeDef attr, String tag,String value) {
		String iliname=getIliQname(attr);
		setSetting(config, iliname, tag,value);
	}
	public void setViewableConfig(Viewable aclass, String tag,String value) {
		String iliname=aclass.getScopedName(null);
		setSetting(config, iliname, tag,value);
	}

}
