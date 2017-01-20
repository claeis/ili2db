package ch.ehi.ili2db.converter;

import java.util.ArrayList;
import java.util.HashMap;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.mapping.MultiSurfaceMappings;
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
import ch.interlis.ili2c.metamodel.AbstractLeafElement;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.ExtendableContainer;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.NumericalType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom_j.itf.EnumCodeMapper;

public class AbstractRecordConverter {
	private EnumCodeMapper enumTypes=new EnumCodeMapper();
	protected TransferDescription td=null;
	protected ch.ehi.ili2db.mapping.NameMapping ili2sqlName=null;
	private String schemaName=null;
	protected String defaultCrsAuthority=null;
	protected String defaultCrsCode=null;
	private String createEnumTable=null;
	protected boolean createStdCols=false;
	protected boolean createEnumTxtCol=false;
	protected boolean removeUnderscoreFromEnumDispName=false;
	protected boolean createEnumColAsItfCode=false;
	protected boolean createIliTidCol=false;
	protected boolean createTypeDiscriminator=false;
	protected boolean createGenericStructRef=false;
	protected boolean sqlEnableNull=true;
	protected boolean strokeArcs=true;
	protected boolean createBasketCol=false;
	protected boolean createItfLineTables=false;
	protected boolean createItfAreaRef=false;
	protected boolean createFk=false;
	protected boolean createFkIdx=false;
	protected boolean isIli1Model=false;
	protected boolean deleteExistingData=false;
	protected String colT_ID=null;
	private String uuid_default_value=null;
	private DbIdGen idGen=null;
	protected TrafoConfig trafoConfig=null;
	protected Viewable2TableMapping class2wrapper=null;
	protected MultiSurfaceMappings multiSurfaceAttrs=new MultiSurfaceMappings();

	public AbstractRecordConverter(TransferDescription td1,ch.ehi.ili2db.mapping.NameMapping ili2sqlName,ch.ehi.ili2db.gui.Config config,DbIdGen idGen1, TrafoConfig trafoConfig1,Viewable2TableMapping class2wrapper1){
		td=td1;
		this.defaultCrsAuthority=config.getDefaultSrsAuthority();
		this.defaultCrsCode=config.getDefaultSrsCode();
		this.ili2sqlName=ili2sqlName;
		trafoConfig=trafoConfig1;
		createEnumTable=config.getCreateEnumDefs();
		class2wrapper=class2wrapper1;
		createEnumColAsItfCode=config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getCreateEnumColAsItfCode());
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createEnumTxtCol=config.CREATE_ENUM_TXT_COL.equals(config.getCreateEnumCols());
		removeUnderscoreFromEnumDispName=config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE.equals(config.getBeautifyEnumDispName());
		createFk=config.CREATE_FK_YES.equals(config.getCreateFk());
		createFkIdx=config.CREATE_FKIDX_YES.equals(config.getCreateFkIdx());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		uuid_default_value=config.getUuidDefaultValue();
		this.idGen=idGen1;
		schemaName=config.getDbschema();

		deleteExistingData=config.DELETE_DATA.equals(config.getDeleteMode());
		
		createTypeDiscriminator=config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		createGenericStructRef=config.STRUCT_MAPPING_GENERICREF.equals(config.getStructMapping());
		sqlEnableNull=config.SQL_NULL_ENABLE.equals(config.getSqlNull());
		strokeArcs=config.STROKE_ARCS_ENABLE.equals(config.getStrokeArcs());
		createIliTidCol=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		
		createBasketCol=config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		
		isIli1Model=td1.getIli1Format()!=null;
		createItfLineTables=isIli1Model && config.getDoItfLineTables();
		createItfAreaRef=isIli1Model && config.AREA_REF_KEEP.equals(config.getAreaRef());
		
	}
	public String beautifyEnumDispName(String value) {
		if(removeUnderscoreFromEnumDispName){
			return value.replace('_', ' ');
		}
		return value;
	}
	public DbColGeometry generatePolylineType(LineType type, String attrName) {
		DbColGeometry ret=new DbColGeometry();
		boolean compoundCurve=false;
		if(!strokeArcs){
			compoundCurve=true;
		}
		ret.setType(compoundCurve ? DbColGeometry.COMPOUNDCURVE : DbColGeometry.LINESTRING);
		// TODO get crs from ili
		ret.setSrsAuth(defaultCrsAuthority);
		ret.setSrsId(defaultCrsCode);
		Domain coordDomain=type.getControlPointDomain();
		if(coordDomain!=null){
			CoordType coord=(CoordType)coordDomain.getType();
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attrName);
		}
		return ret;
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
		private HashMap<Viewable,ArrayList<ViewableWrapper>> targetTablesPool=new HashMap<Viewable,ArrayList<ViewableWrapper>>(); 
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
	protected String getSqlAttrName(AttributeDef def,String ownerSqlTableName,String targetSqlTableName){
		return ili2sqlName.mapIliAttributeDef(def,ownerSqlTableName,targetSqlTableName);
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
	protected void setBB(DbColGeometry ret, CoordType coord,String scopedAttrName) {
		NumericalType dimv[]=coord.getDimensions();
		if(!(dimv[0] instanceof NumericType) || !(dimv[1] instanceof NumericType)){
			EhiLogger.logError("Attribute "+scopedAttrName+": COORD type not supported ("+dimv[0].getClass().getName()+")");
			return;
		}
		if(((NumericType)dimv[0]).getMinimum()!=null){
			ret.setMin1(((NumericType)dimv[0]).getMinimum().doubleValue());
			ret.setMax1(((NumericType)dimv[0]).getMaximum().doubleValue());
			ret.setMin2(((NumericType)dimv[1]).getMinimum().doubleValue());
			ret.setMax2(((NumericType)dimv[1]).getMaximum().doubleValue());
			if(dimv.length==3){
				ret.setMin3(((NumericType)dimv[2]).getMinimum().doubleValue());
				ret.setMax3(((NumericType)dimv[2]).getMaximum().doubleValue());
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
	public ArrayList<ViewableWrapper> getStructWrappers(Table structClass) {
		ArrayList<ViewableWrapper> ret=new ArrayList<ViewableWrapper>();
		ViewableWrapper structWrapper=class2wrapper.get(structClass);
		if(structWrapper!=null){
			while(structWrapper.getExtending()!=null){
				structWrapper=structWrapper.getExtending();
			}
			ret.add(structWrapper);
			return ret;
		}
		ArrayList<ExtendableContainer<AbstractLeafElement>> exts=new ArrayList<ExtendableContainer<AbstractLeafElement>>();
		exts.addAll(structClass.getDirectExtensions());
		while(exts.size()>0){
			structClass=(Table)exts.remove(0);
			structWrapper=class2wrapper.get(structClass);
			if(structWrapper!=null){
				while(structWrapper.getExtending()!=null){
					structWrapper=structWrapper.getExtending();
				}
				ret.add(structWrapper);
			}else{
				exts.addAll(structClass.getDirectExtensions());
			}
		}
		return ret;
	}
	public boolean createTypeDiscriminator() {
		return createTypeDiscriminator;
	}
	
}
