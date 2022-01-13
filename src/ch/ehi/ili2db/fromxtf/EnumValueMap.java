package ch.ehi.ili2db.fromxtf;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.metaattr.IliMetaAttrNames;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class EnumValueMap {
    private HashMap<Long,String> id2xtf=new HashMap<Long,String>();
    private HashMap<String,Long> xtf2id=new HashMap<String,Long>();
    private HashMap<String,String> xtf2displayName=new HashMap<String,String>();
    public long mapXtfValue(String xtfvalue) {
        return xtf2id.get(xtfvalue);
    }
    public String mapIdValue(long value) {
        return id2xtf.get(value);
    }
    public String mapXtfValueToDisplayName(String xtfvalue) {
        return xtf2displayName.get(xtfvalue);
    }

    public static HashSet<String> readEnumTable(java.sql.Connection conn,String tidColumnName,boolean hasThisClassColumn,String qualifiedIliName,DbTableName sqlDbName)
    throws Ili2dbException
    {
        EnumValueMap map;
        try {
            map = createEnumValueMap(conn,tidColumnName,hasThisClassColumn,qualifiedIliName,sqlDbName);
        } catch (SQLException ex) {
            throw new Ili2dbException("failed to read enum-table "+sqlDbName,ex);
        }
        HashSet<String> ret=new HashSet<String>();
        ret.addAll(map.xtf2id.keySet());
        return ret;
    }
    public static EnumValueMap createEnumValueMap(java.sql.Connection conn,String tidColumnName,boolean hasThisClassColumn,String qualifiedIliName,DbTableName sqlDbName) throws SQLException
    {
        EnumValueMap ret=new EnumValueMap();
    	String sqlName=sqlDbName.getName();
    	if(sqlDbName.getSchema()!=null){
    		sqlName=sqlDbName.getSchema()+"."+sqlName;
    	}
    		String exstStmt=null;
    		if(!hasThisClassColumn){
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+","+DbNames.ENUM_TAB_DISPNAME_COL+" FROM "+sqlName;
    		}else{
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+","+DbNames.ENUM_TAB_DISPNAME_COL+" FROM "+sqlName+" WHERE "+DbNames.ENUM_TAB_THIS_COL+" = '"+qualifiedIliName+"'";
    		}
    		EhiLogger.traceBackendCmd(exstStmt);
    		java.sql.PreparedStatement exstPrepStmt = null;
            java.sql.ResultSet rs=null;
    		try{
                exstPrepStmt = conn.prepareStatement(exstStmt);
    			rs=exstPrepStmt.executeQuery();
                Long id=0L;
    			while(rs.next()){
    				String iliCode=rs.getString(1);
                    String displayName = null;
                    if (tidColumnName == null) {
                        displayName = rs.getString(2);
                    }
                    if(tidColumnName!=null) {
                        id=rs.getLong(2);
                    }else {
                        id++;
                    }
    				ret.addValue(id,iliCode,displayName);
    			}
    		}finally{
    		    if(rs!=null) {
    		        rs.close();
    		        rs=null;
    		    }
    		    if(exstPrepStmt!=null) {
                    exstPrepStmt.close();
                    exstPrepStmt=null;
    		    }
    		}
    	return ret;
    }

    void addValue(long id, String xtfCode, String displayName) {
        id2xtf.put(id,xtfCode);
        xtf2id.put(xtfCode,id);
        xtf2displayName.put(xtfCode, displayName);
    }
}
