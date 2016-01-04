package ch.ehi.ili2db.mapping;

import ch.interlis.ili2c.metamodel.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** Wrapper around a Viewable to  
 * make it aware of all attributes of all specializations.
 */
public class ViewableWrapper {
	private ViewableWrapper base=null;
	public ViewableWrapper(Viewable viewable){
		this.viewable=viewable;
	}
	/** the viewable that this wrapper wraps.
	 */
	private Viewable viewable=null;
	/** the attributes and roles that this Record has.
	 * list<Viewable.TransferElement>
	 */
	private List<ViewableTransferElement> attrv=new java.util.ArrayList<ViewableTransferElement>();
	private ArrayList<ViewableWrapper> allTablev=null;
	/** the attributes and roles that this record has.
	 * @return list<ViewableTransferElement>
	 */
	public List<ViewableTransferElement> getAttrv() {
		return attrv;
	}
	public void setAttrv(List<ViewableTransferElement> list) {
		attrv = list;
	}
	public java.util.Iterator<ViewableTransferElement> getAttrIterator() {
		return attrv.iterator();
	}
	public ArrayList<ViewableWrapper> getWrappers() {
		if(allTablev==null){
			allTablev=new ArrayList<ViewableWrapper>(10);
			allTablev.add(this);
			ViewableWrapper base=this.getExtending();
			while(base!=null){
				allTablev.add(0,base);		
				base=base.getExtending();
			}
		}
		return allTablev;
	}


	/** gets the viewable that this wrapper wraps.
	 */
	public Viewable getViewable() {
		return viewable;
	}
	public boolean isStructure() {
		return (viewable instanceof Table) && !((Table)viewable).isIdentifiable();
	}
	public Domain getOid() {
		Viewable def=getViewable();
		if(!(def instanceof AbstractClassDef)){
			return null;
		}
		if((def instanceof Table) && !((Table)def).isIdentifiable()){
			return null;
		}
		AbstractClassDef aclass=(AbstractClassDef) def;
		if(aclass.getOid()!=null){
			return aclass.getOid();
		}
		for(Object exto : aclass.getExtensions()){
			AbstractClassDef ext=(AbstractClassDef) exto;
			if(ext.getOid()!=null){
				return ext.getOid();
			}
		}
		return null;
	}
	public boolean includesMultipleTypes() {
		return viewable.getExtensions().size()>1; // TODO or no record for base-viewable
	}
	public boolean isAssocLightweight() {
		return (viewable instanceof AssociationDef) && ((AssociationDef)viewable).isLightweight();
	}
	public ViewableWrapper getExtending() {
		return base;
	}
	public void setExtending(ViewableWrapper base1) {
		base=base1;
	}

}
