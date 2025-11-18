package ch.ehi.ili2db.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AbstractCoordType;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BaseType;
import ch.interlis.ili2c.metamodel.Cardinality;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;

/** Wrapper around a Viewable to  
 * make it aware of all attributes of all specializations.
 */
public class ViewableWrapper {
	private DbTableName sqlTablename=null;
	private ViewableWrapper base=null;
	private ViewableWrapper mainTable=null;
	private ArrayList<ViewableWrapper> secondaryTables=new ArrayList<ViewableWrapper>();
	boolean incMultipleTypes=true;
	
	private ViewableWrapper(String sqlSchemaname1,String sqlTablename1){
		sqlTablename=new DbTableName(sqlSchemaname1,sqlTablename1);
	}
	public ViewableWrapper(String sqlSchemaname1,String sqlTablename1,Viewable viewable){
		this(sqlSchemaname1,sqlTablename1);
		this.viewable=viewable;
	}
	public ViewableWrapper createSecondaryTable(String sqlTablename1) {
		ViewableWrapper ret=new ViewableWrapper(sqlTablename.getSchema(),sqlTablename1);
		ret.mainTable=this;
		secondaryTables.add(ret);
		return ret;
	}
	public ViewableWrapper getSecondaryTable(String sqlTablename1) {
		for(ViewableWrapper sec:secondaryTables){
			if(sec.sqlTablename.equals(sqlTablename1)){
				return sec;
			}
		}
		return null;
	}
	/** the viewable that this wrapper wraps.
	 */
	private Viewable viewable=null;
	/** the attributes and roles that this Record has.
	 * list<Viewable.TransferElement>
	 */
	private List<ColumnWrapper> attrv=new java.util.ArrayList<ColumnWrapper>();
	private ArrayList<ViewableWrapper> allTablev=null;
	/** the attributes and roles that this record has.
	 * @return list<ViewableTransferElement>
	 */
	public List<ColumnWrapper> getAttrv() {
		return attrv;
	}
	public void setAttrv(List<ColumnWrapper> list) {
		attrv = list;
	}
	public java.util.Iterator<ColumnWrapper> getAttrIterator() {
		return attrv.iterator();
	}
	public ArrayList<ViewableWrapper> getSecondaryTables() {
		return secondaryTables;
	}
	public ArrayList<ViewableWrapper> getWrappers() {
		if(allTablev==null){
			allTablev=new ArrayList<ViewableWrapper>(10);
			ViewableWrapper base=this;
			while(base!=null){
				allTablev.add(0,base);	// root (table with column t_type) must be first (because of query stmt)

				// omit secondary attribute tables with primitive type and cardinality greater than 1
				for (ViewableWrapper secondary : secondaryTables) {
					if (secondary.getPrimitiveCollectionAttr() == null) {
						allTablev.add(secondary);
					}
				}
				base=base.getExtending();
			}
		}
		return allTablev;
	}

	public ArrayList<ViewableWrapper> getPrimitiveCollectionWrappers() {
		ArrayList<ViewableWrapper> ret = new ArrayList<ViewableWrapper>();
		for (ViewableWrapper secondary : secondaryTables) {
			if (secondary.getPrimitiveCollectionAttr() != null) {
				ret.add(secondary);
			}
		}
		return ret;
	}


	/** gets the viewable that this wrapper wraps.
	 */
	public Viewable getViewable() {
		  if(isSecondaryTable()){
			  return getMainTable().getViewable();
		  }
		return viewable;
	}
	public boolean isStructure() {
		return (viewable instanceof Table) && !((Table)viewable).isIdentifiable();
	}

	/**
	 * Get defining attribute definition for secondary collection table.
	 *
	 * @return Attribute definition of this secondary table, that has only a single attribute and a maximum cardinality
	 * greater than one and a type of that attribute is primitive.
	 * Null if this viewableWrapper does not represent a BAG / LIST OF primitive
	 */
	public AttributeDef getPrimitiveCollectionAttr() {
		if (isSecondaryTable() && attrv.size() == 1) {
			ColumnWrapper columnWrapper = attrv.get(0);
			if (columnWrapper.isIliAttr()) {
				AttributeDef attr = (AttributeDef) columnWrapper.getViewableTransferElement().obj;
				Cardinality cardinality = attr.getDomainOrDerivedDomain().getCardinality();
				Type type = attr.getDomainResolvingAll();
				if (cardinality.getMaximum() > 1) {
					return attr;
				}
			}
		}
		return null;
	}
    public boolean hasOid() {
        if(isSecondaryTable()){
            return false;
        }
        Viewable def=getViewable();
        if(!(def instanceof AbstractClassDef)){
            return false;
        }
        AbstractClassDef aclass=(AbstractClassDef) def;
        if(aclass.getOid()!=null){
            return true;
        }
        if(Ili2cUtility.getOidDomainFromMetaAttr(aclass)!=null) {
           return  true;
        }
        HashSet<Domain> ret=new HashSet<Domain>();
        for(Object exto : aclass.getExtensions()){
            AbstractClassDef ext=(AbstractClassDef) exto;
            if(ext.getOid()!=null){
                ret.add(ext.getOid());
            }
            if(Ili2cUtility.getOidDomainFromMetaAttr(ext)!=null) {
                ret.add(Ili2cUtility.getOidDomainFromMetaAttr(ext));
            }
        }
        if(ret.size()==0) {
            return false;
        }
        return true;
    }
	public Domain getOid() {
		if(isSecondaryTable()){
			return null;
		}
		Viewable def=getViewable();
		if(!(def instanceof AbstractClassDef)){
			return null;
		}
		AbstractClassDef aclass=(AbstractClassDef) def;
		if(aclass.getOid()!=null){
			return aclass.getOid();
		}
		if(Ili2cUtility.getOidDomainFromMetaAttr(aclass)!=null) {
		   return  Ili2cUtility.getOidDomainFromMetaAttr(aclass);
		}
        HashSet<Domain> ret=new HashSet<Domain>();
		for(Object exto : aclass.getExtensions()){
			AbstractClassDef ext=(AbstractClassDef) exto;
			if(ext.getOid()!=null){
				ret.add(ext.getOid());
			}else if(Ili2cUtility.getOidDomainFromMetaAttr(ext)!=null) {
	            ret.add(Ili2cUtility.getOidDomainFromMetaAttr(ext));
			}else if(ext.isAbstract()) {
			    // skip (no objects for abstract classes)
	        }else if(ext instanceof Table && ((Table) ext).isIdentifiable()) {
	            ret.add(null);
	        }
		}
		if(ret.size()!=1) {
		    return null;
		}
		return ret.iterator().next();
	}
	public boolean includesMultipleTypes() {
		return incMultipleTypes;
	}
	public void setMultipleTypes(boolean multipleTypes) {
		incMultipleTypes=multipleTypes;
	}
	public ViewableWrapper getExtending() {
		return base;
	}
    public ViewableWrapper getRoot() {
        ViewableWrapper root0=this;
        ViewableWrapper root1=root0.getExtending();
        while(root1!=null) {
            root0=root1;
            root1=root0.getExtending();
        }
        return root0;
    }
	public void setExtending(ViewableWrapper base1) {
		base=base1;
	}
	/** returns the unqualified db tablename.
	 */
	public String getSqlTablename() {
		return sqlTablename.getName();
	}
	public DbTableName getSqlTable() {
		return sqlTablename;
	}
	public String getSqlTableQName() {
		return sqlTablename.getQName();
	}
	public ViewableWrapper getMainTable() {
		return mainTable;
	}
	public boolean isSecondaryTable() {
		return mainTable!=null;
	}
	public boolean containsAttributes(Set<? extends ch.interlis.ili2c.metamodel.Element> iomObjectAttrs) {
		for(ColumnWrapper col:attrv){
			if(col.isIliElement()){
				if(iomObjectAttrs.contains(col.getViewableTransferElement().obj)){
					return true;
				}
			}
		}
		return false;
	}
}
