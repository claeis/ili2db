package ch.ehi.ili2db.toxtf;

import java.util.HashMap;

import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;

public class FixIomObjectRefs {

	private IomObject root=null;
	private HashMap<IomObject,Target> refs=new HashMap<IomObject,Target>();
	private class Target{
		public Target(int sqlid, Viewable aclass) {
			super();
			this.sqlid = sqlid;
			this.aclass = aclass;
		}
		int sqlid;
		Viewable aclass;
			
	}
	public FixIomObjectRefs(IomObject rootObj) {
		root=rootObj;
	}

	public IomObject getRoot(){
		return root;
	}
	public void addFix(IomObject refobj, int sqlid,Viewable targetClass) {
		refs.put(refobj, new Target(sqlid,targetClass));
	}

	public boolean needsFixing() {
		return !refs.isEmpty();
	}

	public java.util.Collection<IomObject> getRefs() {
		return refs.keySet();
	}

	public int getTargetSqlid(IomObject ref) {
		return refs.get(ref).sqlid;
	}
	public Viewable getTargetClass(IomObject ref) {
		return refs.get(ref).aclass;
	}

}
