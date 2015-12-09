package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;

import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;

public class FixIomObjectExtRefs {

	private IomObject root=null;
	private HashMap<IomObject,Target> refs=new HashMap<IomObject,Target>();
	private class Target{
		public Target(Viewable aclass) {
			super();
			this.aclass = aclass;
		}
		Viewable aclass;
			
	}
	public FixIomObjectExtRefs(IomObject rootObj) {
		root=rootObj;
	}

	public IomObject getRoot(){
		return root;
	}
	public void addFix(IomObject refobj, Viewable targetClass) {
		refs.put(refobj, new Target(targetClass));
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

}
