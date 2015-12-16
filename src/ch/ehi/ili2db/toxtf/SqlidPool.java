package ch.ehi.ili2db.toxtf;

import java.util.HashMap;

public class SqlidPool {
	private HashMap<Integer,String> sqlid2xtfid=new HashMap<Integer,String>();

	public boolean containsSqlid(int sqlid) {
		return sqlid2xtfid.containsKey(sqlid);
	}

	public String getXtfid(int sqlid) {
		return sqlid2xtfid.get(sqlid);
	}

	public void putSqlid2Xtfid(int sqlid, String xtfoid) {
		sqlid2xtfid.put(sqlid, xtfoid);
	}

}
