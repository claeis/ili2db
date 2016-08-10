package ch.ehi.ili2db.toxtf;

import java.util.HashMap;

import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;

public class FixIomObjectRefs {

	private IomObject root=null;
	private HashMap<IomObject,Target> refs=new HashMap<IomObject,Target>();
	private class Target{
		public Target(long sqlid, Viewable aclass) {
			super();
			this.sqlid = sqlid;
			this.aclass = aclass;
		}
		long sqlid;
		Viewable aclass;
			
	}

	public void setRoot(IomObject root1){
		if(root!=null){
			throw new IllegalStateException("root can only be set once");
		}
		root=root1;
	}
	public IomObject getRoot(){
		return root;
	}
	public void addFix(IomObject refobj, long sqlid,Viewable targetClass) {
		refs.put(refobj, new Target(sqlid,targetClass));
	}

	public boolean needsFixing() {
		return !refs.isEmpty();
	}

	public java.util.Collection<IomObject> getRefs() {
		return refs.keySet();
	}

	public long getTargetSqlid(IomObject ref) {
		return refs.get(ref).sqlid;
	}
	public Viewable getTargetClass(IomObject ref) {
		return refs.get(ref).aclass;
	}

}
