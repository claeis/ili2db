package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;
import java.util.Map;

import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;

public class FixIomObjectExtRefs {

	private long basketSqlId;
	private String rootTid=null;
	private String rootTag=null;
	private HashMap<IomObject,Target> refs=new HashMap<IomObject,Target>();
    private Map<String, String> genericDomains=null;
	private class Target{
		public Target(Viewable aclass,boolean isExternal) {
			super();
			this.aclass = aclass;
			this.isExternal=isExternal;
		}
		Viewable aclass;
		boolean isExternal;
			
	}
	public FixIomObjectExtRefs(long basketSqlId,Map<String, String> genericDomains, String rootObjTag,String rootObjTid) {
		rootTid=rootObjTid;
		rootTag=rootObjTag;
		this.basketSqlId=basketSqlId;
		this.genericDomains=genericDomains;
	}

	public String getRootTid(){
		return rootTid;
	}
	public String getRootTag(){
		return rootTag;
	}
	public void addFix(IomObject refobj, Viewable targetClass,boolean isExternal) {
		refs.put(refobj, new Target(targetClass,isExternal));
	}

	public boolean needsFixing() {
		return !refs.isEmpty();
	}

	public java.util.Collection<IomObject> getRefs() {
		return refs.keySet();
	}

	public Viewable getTargetClass(IomObject ref) {
		return refs.get(ref).aclass;
	}

	public long getBasketSqlId() {
		return basketSqlId;
	}

    public boolean isExternalTarget(IomObject ref) {
        return refs.get(ref).isExternal;
    }

    public java.util.Map<String,String> getGenericDomains() {
        return genericDomains;
    }

}
