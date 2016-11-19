package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;

import ch.ehi.ili2db.base.DbIdGen;

public class XtfidPool {
	private HashMap<String,HashMap<String,Long>> xtfIdByTag2sqlId=new HashMap<String,HashMap<String,Long>>();
	private HashMap<String,HashMap<String,String>> xtfIdByTag2tag=new HashMap<String,HashMap<String,String>>();
	private DbIdGen idGen=null;
	public XtfidPool(DbIdGen idGen1) {
		idGen=idGen1;
	}
	/** maps an xtfId to a sqlId.
	 * xtfIds are qualified with the qualified name of the ili-class, so that non-unique TIDs
	 * of ITF files can be handled. For Ili2 models the root class of the class inheritance is
	 * used.
	 */
	public long getObjSqlId(String tag,String xtfId){
		HashMap<String,Long> xtfId2sqlId=null;
		if(xtfIdByTag2sqlId.containsKey(tag)){
			xtfId2sqlId=xtfIdByTag2sqlId.get(tag);
		}else{
			xtfId2sqlId=new HashMap<String,Long>(); 
			xtfIdByTag2sqlId.put(tag,xtfId2sqlId);
		}
		if(xtfId2sqlId.containsKey(xtfId)){
			return xtfId2sqlId.get(xtfId).longValue();
		}
		throw new IllegalStateException("unkonwn xtfid "+tag+":"+xtfId);
	}
	public boolean containsXtfid(String tag,String xtfid) {
		HashMap<String,Long> xtfId2sqlId=null;
		if(xtfIdByTag2sqlId.containsKey(tag)){
			xtfId2sqlId=xtfIdByTag2sqlId.get(tag);
		}else{
			xtfId2sqlId=new HashMap<String,Long>(); 
			xtfIdByTag2sqlId.put(tag,xtfId2sqlId);
		}
		if(xtfId2sqlId.containsKey(xtfid)){
			return true;
		}
		return false;
	}
	/** gets a new obj id.
	 */
	public long newObjSqlId(){
		return idGen.newObjSqlId();
	}
	public long getLastSqlId()
	{
		return idGen.getLastSqlId();
	}
	public void putXtfid2sqlid(String rootTag,String tag,String xtfId, Long sqlid) {
		HashMap<String,Long> xtfId2sqlId=null;
		if(xtfIdByTag2sqlId.containsKey(rootTag)){
			xtfId2sqlId=xtfIdByTag2sqlId.get(rootTag);
		}else{
			xtfId2sqlId=new HashMap<String,Long>(); 
			xtfIdByTag2sqlId.put(rootTag,xtfId2sqlId);
		}
		xtfId2sqlId.put(xtfId, sqlid);
		//
		HashMap<String,String> xtfId2tag=null;
		if(xtfIdByTag2tag.containsKey(rootTag)){
			xtfId2tag=xtfIdByTag2tag.get(rootTag);
		}else{
			xtfId2tag=new HashMap<String,String>(); 
			xtfIdByTag2tag.put(rootTag,xtfId2tag);
		}
		xtfId2tag.put(xtfId, tag);
	}
	public String getObjecttag(String rootTag,String xtfId) {
		HashMap<String,String> xtfId2tag=null;
		if(xtfIdByTag2tag.containsKey(rootTag)){
			xtfId2tag=xtfIdByTag2tag.get(rootTag);
		}else{
			xtfId2tag=new HashMap<String,String>(); 
			xtfIdByTag2tag.put(rootTag,xtfId2tag);
		}
		return xtfId2tag.get(xtfId);
	}
	private HashMap<String,Long> bid2sqlid=new HashMap<String,Long>();
	public long getBasketSqlId(String bid) {
		if(bid2sqlid.containsKey(bid)){
			return bid2sqlid.get(bid).longValue();
		}
		long ret=newObjSqlId();
		bid2sqlid.put(bid,new Long(ret));
		return ret;
	}
	public long createObjSqlId(String rootTag, String tag, String xtfId) {
		HashMap<String,String> xtfId2tag=null;
		if(xtfIdByTag2tag.containsKey(rootTag)){
			xtfId2tag=xtfIdByTag2tag.get(rootTag);
		}else{
			xtfId2tag=new HashMap<String,String>(); 
			xtfIdByTag2tag.put(rootTag,xtfId2tag);
		}
		xtfId2tag.put(xtfId, tag);
		HashMap<String,Long> xtfId2sqlId=null;
		if(xtfIdByTag2sqlId.containsKey(rootTag)){
			xtfId2sqlId=xtfIdByTag2sqlId.get(rootTag);
		}else{
			xtfId2sqlId=new HashMap<String,Long>(); 
			xtfIdByTag2sqlId.put(rootTag,xtfId2sqlId);
		}
		if(xtfId2sqlId.containsKey(xtfId)){
			return xtfId2sqlId.get(xtfId).longValue();
		}
		long ret=newObjSqlId();
		xtfId2sqlId.put(xtfId,new Long(ret));
		return ret;
	}

}
