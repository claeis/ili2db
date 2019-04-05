package ch.ehi.ili2db.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Table;
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
				allTablev.addAll(secondaryTables);
				base=base.getExtending();
			}
		}
		return allTablev;
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
	public Domain getOid() {
		if(isSecondaryTable()){
			return null;
		}
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
		return incMultipleTypes;
	}
	public void setMultipleTypes(boolean multipleTypes) {
		incMultipleTypes=multipleTypes;
	}
	public ViewableWrapper getExtending() {
		return base;
	}
	public void setExtending(ViewableWrapper base1) {
		base=base1;
	}
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
	public boolean containsAttributes(Set<AttributeDef> iomObjectAttrs) {
		for(ColumnWrapper ele:attrv){
			if(ele.getViewableTransferElement().obj instanceof AttributeDef){
				if(iomObjectAttrs.contains(ele.getViewableTransferElement().obj)){
					return true;
				}
			}
		}
		return false;
	}
}
