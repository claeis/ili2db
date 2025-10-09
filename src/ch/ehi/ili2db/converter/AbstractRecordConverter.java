package ch.ehi.ili2db.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.types.OutParam;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.ArrayMappings;
import ch.ehi.ili2db.mapping.MultiLineMappings;
import ch.ehi.ili2db.mapping.MultiPointMappings;
import ch.ehi.ili2db.mapping.MultiSurfaceMappings;
import ch.ehi.ili2db.mapping.StructAttrPath;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.TrafoConfigNames;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColUuid;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AbstractCoordType;
import ch.interlis.ili2c.metamodel.AbstractEnumerationType;
import ch.interlis.ili2c.metamodel.AbstractLeafElement;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BlackboxType;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.ExtendableContainer;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.NumericalType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom_j.itf.EnumCodeMapper;

public class AbstractRecordConverter {
	protected EnumCodeMapper enumTypes=new EnumCodeMapper();
	protected TransferDescription td=null;
	protected ch.ehi.ili2db.mapping.NameMapping ili2sqlName=null;
	private String schemaName=null;
	protected String defaultCrsAuthority=null;
	protected String defaultCrsCode=null;
	protected String createEnumTable=null;
	protected boolean createStdCols=false;
	protected boolean createEnumTxtCol=false;
	protected boolean createEnumColAsItfCode=false;
	protected boolean createTypeDiscriminator=false;
	protected boolean sqlEnableNull=true;
	protected boolean strokeArcs=true;
	protected boolean createBasketCol=false;
	protected boolean createDatasetCol=false;
	protected boolean createItfLineTables=false;
    protected boolean createXtfLineTables=false;
	protected boolean createItfAreaRef=false;
	protected boolean createFk=false;
	protected boolean createFkIdx=false;
    protected boolean createExtRef=false;
	protected boolean isIli1Model=false;
	protected String colT_ID=null;
	private String uuid_default_value=null;
	private DbIdGen idGen=null;
	protected TrafoConfig trafoConfig=null;
	protected Viewable2TableMapping class2wrapper=null;
	protected MultiSurfaceMappings multiSurfaceAttrs=new MultiSurfaceMappings();
	protected MultiLineMappings multiLineAttrs=new MultiLineMappings();
	protected MultiPointMappings multiPointAttrs=new MultiPointMappings();
	protected ArrayMappings arrayAttrs=new ArrayMappings();
	protected boolean sqlColsAsText = false;

	public AbstractRecordConverter(TransferDescription td1,ch.ehi.ili2db.mapping.NameMapping ili2sqlName,ch.ehi.ili2db.gui.Config config,DbIdGen idGen1, TrafoConfig trafoConfig1,Viewable2TableMapping class2wrapper1){
		td=td1;
		this.defaultCrsAuthority=config.getDefaultSrsAuthority();
		this.defaultCrsCode=config.getDefaultSrsCode();
		this.ili2sqlName=ili2sqlName;
		trafoConfig=trafoConfig1;
		createEnumTable=config.getCreateEnumDefs();
		class2wrapper=class2wrapper1;
		createEnumColAsItfCode=Config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getValue(Config.CREATE_ENUMCOL_AS_ITFCODE));
		createStdCols=Config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createEnumTxtCol=Config.CREATE_ENUM_TXT_COL.equals(config.getCreateEnumCols());
		createFk=Config.CREATE_FK_YES.equals(config.getCreateFk());
		createFkIdx=Config.CREATE_FKIDX_YES.equals(config.getCreateFkIdx());
		createExtRef=Config.SQL_EXTREF_ENABLE.equals(config.getSqlExtRefCols());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		uuid_default_value=config.getUuidDefaultValue();
		this.idGen=idGen1;
		schemaName=config.getDbschema();

		createTypeDiscriminator=Config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		sqlEnableNull=Config.SQL_NULL_ENABLE.equals(config.getSqlNull());
		strokeArcs=Config.STROKE_ARCS_ENABLE.equals(Config.getStrokeArcs(config));
		
		createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		createDatasetCol=Config.CREATE_DATASET_COL.equals(config.getCreateDatasetCols());
		
		isIli1Model=td1.getIli1Format()!=null;
		createItfLineTables=isIli1Model && config.getDoItfLineTables();
		createItfAreaRef=isIli1Model && Config.AREA_REF_KEEP.equals(config.getAreaRef());
        createXtfLineTables=!isIli1Model && config.getDoXtfLineTables();

		sqlColsAsText=Config.SQL_COLS_AS_TEXT_ENABLE.equals(config.getSqlColsAsText());

	}
	public DbColGeometry generatePolylineType(Model model, LineType type, String attrName, Integer epsgCode) {
		DbColGeometry ret=new DbColGeometry();
		boolean compoundCurve=false;
		if(!strokeArcs){
			compoundCurve=true;
		}
		ret.setType(compoundCurve ? DbColGeometry.COMPOUNDCURVE : DbColGeometry.LINESTRING);
		Domain coordDomain=type.getControlPointDomain();
		if(coordDomain!=null){
			CoordType coord=(CoordType)coordDomain.getType();
			if (coord.isGeneric()) {
				coord = (CoordType) Ili2cUtility.resolveGenericCoordDomain(model, coordDomain, epsgCode).getType();
			}
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attrName);
		}
		return ret;
	}

	public DbColGeometry generateMultiPolylineType(Model model, LineType type, String attrName, Integer epsgCode) {
		DbColGeometry ret=new DbColGeometry();
		boolean curvePolyline=false;
		if(!strokeArcs){
			curvePolyline=true;
		}
		ret.setType(curvePolyline ? DbColGeometry.MULTICURVE : DbColGeometry.MULTILINESTRING);
		Domain coordDomain=type.getControlPointDomain();
		if(coordDomain!=null){
			CoordType coord=(CoordType)coordDomain.getType();
			if (coord.isGeneric()) {
				coord = (CoordType) Ili2cUtility.resolveGenericCoordDomain(model, coordDomain, epsgCode).getType();
			}
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attrName);
		}
		return ret;
	}

    public void setCrs(DbColGeometry ret,int epsgCode) {
        ret.setSrsAuth("EPSG");
        ret.setSrsId(Integer.toString(epsgCode));
        
    }
		public DbColId addKeyCol(DbTable table) {
			  DbColId dbColId=new DbColId();
			  dbColId.setName(colT_ID);
			  dbColId.setNotNull(true);
			  dbColId.setPrimaryKey(true);
			  if(table.isRequiresSequence()){
				  dbColId.setDefaultValue(idGen.getDefaultValueSql());
			  }
			  table.addColumn(dbColId);
			  return dbColId;
		}
		public void addIliTidCol(DbTable dbTable,Domain oidDomain) {
			if(isUuidOid(td,oidDomain)){
				DbColUuid dbColIliTid= new DbColUuid();
				dbColIliTid.setName(DbNames.T_ILI_TID_COL);
				// CREATE EXTENSION "uuid-ossp";
				dbColIliTid.setDefaultValue(uuid_default_value);
				dbTable.addColumn(dbColIliTid);
			}else{
				DbColVarchar dbColIliTid=new DbColVarchar();
				dbColIliTid.setName(DbNames.T_ILI_TID_COL);
				dbColIliTid.setSize(200);
				dbTable.addColumn(dbColIliTid);
			}
		}
		private HashMap<ch.interlis.ili2c.metamodel.Element,ArrayList<ViewableWrapper>> targetTablesPool=new HashMap<ch.interlis.ili2c.metamodel.Element,ArrayList<ViewableWrapper>>(); 
        public ArrayList<ViewableWrapper> getTargetTables(RoleDef destination) {
            if(targetTablesPool.containsKey(destination)){
                return targetTablesPool.get(destination);
            }
            ArrayList<ViewableWrapper> ret=new ArrayList<ViewableWrapper>(); 
            Iterator<AbstractClassDef> destIt=destination.iteratorDestination();
            while(destIt.hasNext()) {
                AbstractClassDef dest=destIt.next();
                ArrayList<ViewableWrapper> im=getTargetTables(dest);
                for(ViewableWrapper imDest:im) {
                    if(!ret.contains(imDest)) {
                        ret.add(imDest);
                    }
                }
            }
            targetTablesPool.put(destination, ret);
            return ret;
        }
		public ArrayList<ViewableWrapper> getTargetTables(Viewable destination) {
			if(targetTablesPool.containsKey(destination)){
				return targetTablesPool.get(destination);
			}
			ArrayList<ViewableWrapper> ret=new ArrayList<ViewableWrapper>(); 
			ArrayList<Viewable> candids=new ArrayList<Viewable>();
			candids.add(destination);
			while(!candids.isEmpty()){
				Viewable candid=candids.remove(0);
				String inheritanceStrategy = trafoConfig.getViewableConfig(candid, TrafoConfigNames.INHERITANCE_TRAFO);
				if(TrafoConfigNames.INHERITANCE_TRAFO_SUPERCLASS.equals(inheritanceStrategy)){
					ret.add(class2wrapper.get(candid)); // ViewableWrapper of base
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(inheritanceStrategy)){
					// visit all sub classes
					candids.addAll(candid.getDirectExtensions());
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(inheritanceStrategy)){
					ViewableWrapper wrapper=class2wrapper.get(candid);
					// classes that have no defined Wrapper are loaded by the compiler, but are not used
					if(wrapper!=null){
						ret.add(wrapper);
					}
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(inheritanceStrategy)){
					ViewableWrapper wrapper=class2wrapper.get(candid);
					// classes that have no defined Wrapper are loaded by the compiler, but are not used
					if(wrapper!=null){
						ret.add(wrapper);
					}
					// visit all sub classes
					candids.addAll(candid.getDirectExtensions());
				}else{
					// skip it
					// classes that have no assigned inheritanceStrategy are loaded by the compiler, but are not used
					// example: 
					// CLASS CatalogueObjectTrees_V1.Catalogues.Item is loaded and an extension 
					// of CLASS CatalogueObjects_V1.Catalogues.Item.
					// It is loaded because it is defined in the same ili-file, but is normally not used.
				}
			}
			// buffer result, so that later calls return exactly the same ordering of targetTables
			// first call: build sql statement
			// second++ calls: set/get sql parameters
			targetTablesPool.put(destination, ret);
			return ret;
		}
	    protected static AbstractClassDef getCatalogueRefTarget(Type type) {
	        return ((ReferenceType) ((AttributeDef)((CompositionType)type).getComponentType().getAttributes().next()).getDomain()).getReferred();
	    }

	protected String getSqlAttrName(StructAttrPath def,Integer epsgCode,String ownerSqlTableName,String targetSqlTableName){
		return ili2sqlName.mapIliAttributeDef(def,epsgCode,ownerSqlTableName,targetSqlTableName);
	}
	/** maps a ili2c viewable to a sql name. 
	 * @param def class, structure, association to map
	 * @return table name or value of column t_type if this viewable is not mapped with a newClass strategy
	 */
	public DbTableName getSqlType(Viewable def){
		String sqlname=ili2sqlName.mapIliClassDef(def);
		return new DbTableName(schemaName,sqlname);
	}
	public static boolean isUuidOid(TransferDescription td,Domain oid) {
		if(oid!=null){
			if(oid==td.INTERLIS.UUIDOID){
				return true;
			}
		}
		return false;
	}
	public static boolean isUuidOid(TransferDescription td,Viewable aclass) {
		throw new IllegalArgumentException("to be removed");
	}
	public static void addStdCol(DbTable table) {
		DbColumn dbCol=new DbColDateTime();
		dbCol.setName(DbNames.T_LAST_CHANGE_COL);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		dbCol=new DbColDateTime();
		dbCol.setName(DbNames.T_CREATE_DATE_COL);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		DbColVarchar dbColUsr=new DbColVarchar();
		dbColUsr.setName(DbNames.T_USER_COL);
		dbColUsr.setNotNull(true);
		dbColUsr.setSize(40);
		table.addColumn(dbColUsr);
	}
	protected void setBB(DbColGeometry ret, AbstractCoordType coord, String scopedAttrName) {
		NumericalType dimv[]=coord.getDimensions();
		if(!(dimv[0] instanceof NumericType) || !(dimv[1] instanceof NumericType)){
			EhiLogger.logError("Attribute "+scopedAttrName+": COORD type not supported ("+dimv[0].getClass().getName()+")");
			return;
		}
		if(((NumericType)dimv[0]).getMinimum()!=null){
			ret.setMin1(((NumericType)dimv[0]).getMinimum().toString());
			ret.setMax1(((NumericType)dimv[0]).getMaximum().toString());
			ret.setMin2(((NumericType)dimv[1]).getMinimum().toString());
			ret.setMax2(((NumericType)dimv[1]).getMaximum().toString());
			if(dimv.length==3){
				ret.setMin3(((NumericType)dimv[2]).getMinimum().toString());
				ret.setMax3(((NumericType)dimv[2]).getMaximum().toString());
			}
		}
	}
	protected int mapXtfCode2ItfCode(EnumerationType type,String xtfCode)
	{
		return Integer.parseInt(enumTypes.mapXtfCode2ItfCode(type, xtfCode));
	}	
	protected String mapItfCode2XtfCode(EnumerationType type,int itfCode)
	{
		return enumTypes.mapItfCode2XtfCode(type, Integer.toString(itfCode));
	}	
	public ArrayList<ViewableWrapper> getStructWrappers(Viewable structClass) {
		ArrayList<ViewableWrapper> ret=new ArrayList<ViewableWrapper>();
		ViewableWrapper structWrapper=class2wrapper.get(structClass);
		if(structWrapper!=null){
			while(structWrapper.getExtending()!=null){
				structWrapper=structWrapper.getExtending();
			}
			ret.add(structWrapper);
		}
		ArrayList<ExtendableContainer<AbstractLeafElement>> exts=new ArrayList<ExtendableContainer<AbstractLeafElement>>();
		exts.addAll(structClass.getDirectExtensions());
		while(exts.size()>0){
			structClass=(Viewable)exts.remove(0);
			structWrapper=class2wrapper.get(structClass);
			if(structWrapper!=null){
				while(structWrapper.getExtending()!=null){
					structWrapper=structWrapper.getExtending();
				}
				if(!ret.contains(structWrapper)) {
	                ret.add(structWrapper);
				}
			}else{
				exts.addAll(structClass.getDirectExtensions());
			}
		}
		return ret;
	}
	public boolean createTypeDiscriminator() {
		return createTypeDiscriminator;
	}
    protected DbTableName getEnumTargetTableName(AttributeDef attr,OutParam<String> iliname,String schema) {
        ch.interlis.ili2c.metamodel.Type type=attr.getDomain();
        if(type instanceof ch.interlis.ili2c.metamodel.TypeAlias){
            Domain domain=((ch.interlis.ili2c.metamodel.TypeAlias) type).getAliasing();
            if(iliname!=null) {
                iliname.value=domain.getScopedName();
            }
            domain=Ili2cUtility.getRootBaseDomain(domain);
            String sqlname=ili2sqlName.mapIliDomainDef(domain);
            return new DbTableName(schema,sqlname);
        }
        if(iliname!=null) {
            iliname.value=attr.getScopedName();
        }
        attr=Ili2cUtility.getRootBaseAttr(attr);
        String sqlname=ili2sqlName.mapIliEnumAttributeDefAsTable(attr);
        return new DbTableName(schema,sqlname);
    }
    /** get mapping of root attribute definition to the 
     * specialized attribute as defined by the given class
     * 
     * @param aclass
     * @return
     */
    public java.util.Map<? extends ch.interlis.ili2c.metamodel.Element,? extends ch.interlis.ili2c.metamodel.Element> getIomObjectAttrs(Viewable aclass) {
    	java.util.Map<ch.interlis.ili2c.metamodel.Element,ch.interlis.ili2c.metamodel.Element> ret=new HashMap<ch.interlis.ili2c.metamodel.Element,ch.interlis.ili2c.metamodel.Element>();
    	Iterator iter = aclass.getAttributesAndRoles2();
    	while (iter.hasNext()) {
    	   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
    	   if (obj.obj instanceof AttributeDef) {
    		   AttributeDef attr = (AttributeDef) obj.obj;
    			if(!attr.isTransient()){
    				Type proxyType=attr.getDomain();
    				if(proxyType!=null && (proxyType instanceof ObjectType)){
    					// skip implicit particles (base-viewables) of views
    				}else{
    					AttributeDef root=Ili2cUtility.getRootBaseAttr(attr);
    					ret.put(root,attr);
    				}
    			}
    	   }
    	   if(obj.obj instanceof RoleDef){
    		   RoleDef role = (RoleDef) obj.obj;
    		   { // if(role.getExtending()==null)
    				// a role of an embedded association?
    				if(obj.embedded){
    					AssociationDef roleOwner = (AssociationDef) role.getContainer();
    					if(roleOwner.getDerivedFrom()==null){
    						RoleDef root=Ili2cUtility.getRootBaseRole(role);
    						ret.put(root,role);
    					}
    				 }else{
                         RoleDef root=Ili2cUtility.getRootBaseRole(role);
                         ret.put(root,role);
    				 }
    			}
    		}
    	}
    	return ret;
    }

	protected boolean mapAsTextCol(AttributeDef attributeDef) {
		if(!sqlColsAsText){
			return false;
		}
		return Ili2cUtility.isIomObjectPrimType(td,attributeDef);
	}
	
}
