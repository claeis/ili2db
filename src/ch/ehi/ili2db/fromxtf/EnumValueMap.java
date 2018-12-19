package ch.ehi.ili2db.fromxtf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.sqlgen.repository.DbTableName;

public class EnumValueMap {
    private HashMap<Long,String> id2xtf=new HashMap<Long,String>();
    private HashMap<String,Long> xtf2id=new HashMap<String,Long>();
    
    public long mapXtfValue(String xtfvalue) {
        return xtf2id.get(xtfvalue);
    }
    public String mapIdValue(long value) {
        return id2xtf.get(value);
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
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+" FROM "+sqlName;
    		}else{
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+" FROM "+sqlName+" WHERE "+DbNames.ENUM_TAB_THIS_COL+" = '"+qualifiedIliName+"'";
    		}
    		EhiLogger.traceBackendCmd(exstStmt);
    		java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
    		try{
    			java.sql.ResultSet rs=exstPrepStmt.executeQuery();
    			while(rs.next()){
    				String iliCode=rs.getString(1);
                    Long id=null;
                    if(tidColumnName!=null) {
                        id=rs.getLong(2);
                    }
    				ret.addValue(id,iliCode);
    			}
    		}finally{
    			exstPrepStmt.close();
    		}
    	return ret;
    }

    private void addValue(long id, String xtfCode) {
        id2xtf.put(id,xtfCode);
        xtf2id.put(xtfCode,id);
    }


}
