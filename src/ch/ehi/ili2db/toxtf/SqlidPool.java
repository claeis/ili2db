package ch.ehi.ili2db.toxtf;

import java.util.HashMap;

public class SqlidPool {
	private HashMap<String,HashMap<Long,String>> sqlid2xtfid=new HashMap<String,HashMap<Long,String>>();

	public boolean containsSqlid(String sqlTablename,long sqlid) {
		return getIdMap(sqlTablename).containsKey(sqlid);
	}

	public String getXtfid(String sqlTablename,long sqlid) {
		return getIdMap(sqlTablename).get(sqlid);
	}

	public void putSqlid2Xtfid(String sqlTablename,long sqlid, String xtfoid) {
		getIdMap(sqlTablename).put(sqlid, xtfoid);
	}
	private HashMap<Long,String> getIdMap(String sqlTablename){
	    HashMap<Long,String> map=sqlid2xtfid.get(sqlTablename);
	    if(map==null) {
	        map=new HashMap<Long,String>(); 
	        sqlid2xtfid.put(sqlTablename,map);
	    }
	    return map;
	}

}
