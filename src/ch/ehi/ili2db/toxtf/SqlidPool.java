package ch.ehi.ili2db.toxtf;

import java.util.HashMap;

public class SqlidPool {
	private HashMap<Long,String> sqlid2xtfid=new HashMap<Long,String>();

	public boolean containsSqlid(long sqlid) {
		return sqlid2xtfid.containsKey(sqlid);
	}

	public String getXtfid(long sqlid) {
		return sqlid2xtfid.get(sqlid);
	}

	public void putSqlid2Xtfid(long sqlid, String xtfoid) {
		sqlid2xtfid.put(sqlid, xtfoid);
	}

}
