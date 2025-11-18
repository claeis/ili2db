package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;

public class FixIomObjectExtRefs {

	private long basketSqlId;
	private String rootTid=null;
	private String rootTag=null;
	private HashMap<IomObject,Target> refs=new HashMap<IomObject,Target>();
    private Map<String, String> genericDomains=null;
	private class Target{
		public Target(boolean isExternal) {
			super();
			this.targetClass = new java.util.ArrayList<Viewable>();
			this.isExternal=isExternal;
		}
		public void addTargetClass(Viewable target) {
		    targetClass.add(target);
		}
		java.util.List<Viewable> targetClass;
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
        Target target=new Target(isExternal);
        refs.put(refobj, target);
	    target.addTargetClass(targetClass);
	}
    public void addFix(IomObject refobj, RoleDef role,boolean isExternal) {
        Target target=new Target(isExternal);
        refs.put(refobj, target);
        for(Iterator<AbstractClassDef> targetClassIt=role.iteratorDestination();targetClassIt.hasNext();) {
            AbstractClassDef targetClass=targetClassIt.next();
            target.addTargetClass(targetClass);
        }
    }

	public boolean needsFixing() {
		return !refs.isEmpty();
	}

	public java.util.Collection<IomObject> getRefs() {
		return refs.keySet();
	}

	public Iterable<Viewable> getTargetClass(IomObject ref) {
		return refs.get(ref).targetClass;
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
