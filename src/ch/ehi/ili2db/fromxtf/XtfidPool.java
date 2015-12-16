package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;

import ch.ehi.ili2db.base.DbIdGen;

public class XtfidPool {
	private HashMap<String,Integer> xtfId2sqlId=new HashMap<String,Integer>();
	private DbIdGen idGen=null;
	public XtfidPool(DbIdGen idGen1) {
		idGen=idGen1;
	}
	/** maps the xtfId to a sqlId.
	 */
	public int getObjSqlId(String xtfId){
		if(xtfId2sqlId.containsKey(xtfId)){
			return xtfId2sqlId.get(xtfId).intValue();
		}
		int ret=newObjSqlId();
		xtfId2sqlId.put(xtfId,new Integer(ret));
		return ret;
	}
	/** gets a new obj id.
	 */
	public int newObjSqlId(){
		return idGen.newObjSqlId();
	}
	public int getLastSqlId()
	{
		return idGen.getLastSqlId();
	}
	public boolean containsXtfid(String xtfid) {
		return xtfId2sqlId.containsKey(xtfid);
	}
	public void putXtfid2sqlid(String xtfid, Integer sqlid) {
		xtfId2sqlId.put(xtfid, sqlid);
	}

}
