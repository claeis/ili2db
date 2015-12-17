package ch.ehi.ili2db.fromili;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.sqlgen.repository.DbColBoolean;
import ch.ehi.sqlgen.repository.DbColDate;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColDecimal;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import ch.ehi.sqlgen.repository.DbColTime;
import ch.ehi.sqlgen.repository.DbColUuid;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AreaType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BasketType;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Evaluable;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.ObjectPath;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TextType;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom_j.itf.ModelUtilities;

public class FromIliRecordConverter extends AbstractRecordConverter {
	private DbSchema schema=null;
	private CustomMapping customMapping=null;
	private HashSet visitedEnums=null;
	private String nl=System.getProperty("line.separator");
	private ArrayList<AttributeDef> surfaceAttrs=null; 
	private boolean coalesceCatalogueRef=true;

	public FromIliRecordConverter(TransferDescription td1, NameMapping ili2sqlName,
			Config config, DbSchema schema1, CustomMapping customMapping1,
			DbIdGen idGen1, HashSet visitedEnums1, TrafoConfig trafoConfig) {
		super(td1, ili2sqlName, config, idGen1,trafoConfig);
		visitedEnums=visitedEnums1;
		customMapping=customMapping1;
		schema=schema1;
		coalesceCatalogueRef=Config.CATALOGUE_REF_TRAFO_COALESCE.equals(config.getCatalogueRefTrafo());
	}

	public void generateViewable(Viewable def)
	throws Ili2dbException
	{
		//EhiLogger.debug("viewable "+def);
		DbTableName sqlName=getSqlTableName(def);
		Viewable base=(Viewable)def.getExtending();
		DbTable dbTable=new DbTable();
		dbTable.setName(sqlName);
		dbTable.setIliName(def.getScopedName(null));
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(def.getDocumentation()!=null){
			cmt.append(cmtSep+def.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+def.getScopedName(null));
		cmtSep=nl;
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
		}
		
		if(deleteExistingData){
			dbTable.setDeleteDataIfTableExists(true);
		}
		if(base==null){
		  dbTable.setRequiresSequence(true);
		}
		String baseRef="";
		DbColId dbColId=addKeyCol(dbTable);
		if(base!=null){
		  dbColId.setScriptComment("REFERENCES "+base.getScopedName(null));
		  if(createFk){
			  dbColId.setReferencedTable(getSqlTableName(base));
		  }
		}
		  if(createBasketCol){
			  // add basketCol
				DbColId t_basket=new DbColId();
				t_basket.setName(DbNames.T_BASKET_COL);
				t_basket.setNotNull(true);
				t_basket.setScriptComment("REFERENCES "+DbNames.BASKETS_TAB);
				if(createFk){
					t_basket.setReferencedTable(new DbTableName(schema.getName(),DbNames.BASKETS_TAB));
				}
				if(createFkIdx){
					t_basket.setIndex(true);
				}
				dbTable.addColumn(t_basket);
		  }
		DbColumn dbCol;
		if(base==null){
			if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(def)){
				  dbCol=createSqlTypeCol(DbNames.T_TYPE_COL);
				  dbTable.addColumn(dbCol);
			}
			// if CLASS
			  if((def instanceof View) || (def instanceof Table) && ((Table)def).isIdentifiable()){
				  if(createIliTidCol || Ili2cUtility.isViewableWithOid(def)){
						addIliTidCol(dbTable,def);
				  }
			  }
		  // if STRUCTURE, add ref to parent
		  if((def instanceof Table) && !((Table)def).isIdentifiable()){
			  if(createGenericStructRef){
				  // add parentid
					DbColId dbParentId=new DbColId();
					dbParentId.setName(DbNames.T_PARENT_ID_COL);
					dbParentId.setNotNull(true);
					dbParentId.setPrimaryKey(false);
					dbTable.addColumn(dbParentId);
					  // add parent_type
					dbCol=createSqlTypeCol(DbNames.T_PARENT_TYPE_COL);
					dbTable.addColumn(dbCol);
					// add parent_attr
					dbCol=createSqlTypeCol(DbNames.T_PARENT_ATTR_COL);
					dbTable.addColumn(dbCol);
			  }else{
				  // add reference to parent for each structAttr when generating structAttr
			  }
			// add seqeunce attr
			DbColId dbSeq=new DbColId();
			dbSeq.setName(DbNames.T_SEQ_COL);
			dbSeq.setNotNull(true);
			dbSeq.setPrimaryKey(false);
			dbTable.addColumn(dbSeq);
		  }
		}

		// body
		surfaceAttrs=new ArrayList<AttributeDef>(); 
		Iterator iter=null;
		if(isIli1Model){
			iter=ModelUtilities.getIli1AttrList((AbstractClassDef)def).iterator();
		}else{
			iter=def.getDefinedAttributesAndRoles2();
		}
		  while (iter.hasNext()) {
			  ViewableTransferElement obj = (ViewableTransferElement)iter.next();
			  if (obj.obj instanceof AttributeDef) {
				  AttributeDef attr = (AttributeDef) obj.obj;
				  if(attr.getExtending()==null){
					try{
						if(createItfLineTables && attr.getDomainResolvingAll() instanceof SurfaceOrAreaType){
							surfaceAttrs.add(attr);
						}
						if(!attr.isTransient()){
							Type proxyType=attr.getDomain();
							if(proxyType!=null && (proxyType instanceof ObjectType)){
								// skip implicit particles (base-viewables) of views
							}else{
								generateAttr(dbTable,attr);
							}
						}
					}catch(Exception ex){
						throw new Ili2dbException(attr.getContainer().getScopedName(null)+"."+attr.getName(),ex);
					}
				  }else{
					  if(Ili2cUtility.isBoolean(td,attr)){
						  
					  }else if(createEnumColAsItfCode && attr.getDomainResolvingAll() instanceof EnumerationType){
						  throw new Ili2dbException("EXTENDED attributes with type enumeration not supported");
					  }
				  }
			  }
			  if(obj.obj instanceof RoleDef){
				  RoleDef role = (RoleDef) obj.obj;
					if(role.getExtending()==null){
						// not an embedded role and roledef not defined in a lightweight association?
						if (!obj.embedded && !((AssociationDef)def).isLightweight()){
						  dbColId=new DbColId();
						  dbColId.setName(getSqlRoleName(role));
						  dbColId.setNotNull(true);
						  dbColId.setPrimaryKey(false);
						  if(createFk){
							  dbColId.setReferencedTable(getSqlTableName(role.getDestination()));
						  }
							if(createFkIdx){
								dbColId.setIndex(true);
							}
						  dbTable.addColumn(dbColId);
						  // handle ordered
						  if(role.isOrdered()){
								// add seqeunce attr
								DbColId dbSeq=new DbColId();
								dbSeq.setName(getSqlRoleName(role)+"_"+DbNames.T_SEQ_COL);
								dbSeq.setNotNull(true);
								dbSeq.setPrimaryKey(false);
								dbTable.addColumn(dbSeq);
						  }
						}
						// a role of an embedded association?
						if(obj.embedded){
							AssociationDef roleOwner = (AssociationDef) role.getContainer();
							if(roleOwner.getDerivedFrom()==null){
								// role is oppend;
							  dbColId=new DbColId();
							  String fkName=getSqlRoleName(role);
							  dbColId.setName(fkName);
							  boolean notNull=false;
							  dbColId.setNotNull(notNull);
							  dbColId.setPrimaryKey(false);
							  if(createFk){
								  dbColId.setReferencedTable(getSqlTableName(role.getDestination()));
							  }
								if(createFkIdx){
									dbColId.setIndex(true);
								}
							  customMapping.fixupEmbeddedLink(dbTable,dbColId,roleOwner,role,getSqlTableName(role.getDestination()),colT_ID);
							  dbTable.addColumn(dbColId);
							  // handle ordered
							  if(role.getOppEnd().isOrdered()){
									// add seqeunce attr
									DbColId dbSeq=new DbColId();
									dbSeq.setName(getSqlRoleName(role)+"_"+DbNames.T_SEQ_COL);
									dbSeq.setNotNull(notNull);
									dbSeq.setPrimaryKey(false);
									dbTable.addColumn(dbSeq);
							  }
							}
						}
					}
			  }
		}
		  if(createStdCols){
				addStdCol(dbTable);
		  }
		  customMapping.fixupViewable(dbTable,def);
	  	schema.addTable(dbTable);
	  	
	}
	public void generateAttr(DbTable dbTable,AttributeDef attr)
	throws Ili2dbException
	{
		if(attr.getDomain() instanceof EnumerationType){
			visitedEnums.add(attr);
		}
		DbColumn dbCol=null;
		DbColumn dbCol_georef=null;
		Type type = attr.getDomainResolvingAll();
		if (Ili2cUtility.isBoolean(td,attr)) {
			dbCol= new DbColBoolean();
		}else if (Ili2cUtility.isIli1Date(td,attr)) {
			dbCol= new DbColDate();
		}else if (Ili2cUtility.isIliUuid(td,attr)) {
			dbCol= new DbColUuid();
		}else if (Ili2cUtility.isIli2Date(td,attr)) {
			dbCol= new DbColDate();
		}else if (Ili2cUtility.isIli2DateTime(td,attr)) {
			dbCol= new DbColDateTime();
		}else if (Ili2cUtility.isIli2Time(td,attr)) {
			dbCol= new DbColTime();
		}else if (type instanceof PolylineType){
			String attrName=attr.getContainer().getScopedName(null)+"."+attr.getName();
			DbColGeometry ret = generatePolylineType((PolylineType)type, attrName);
			dbCol=ret;
		}else if (type instanceof SurfaceOrAreaType){
			if(createItfLineTables){
				dbCol=null;
			}else{
				DbColGeometry ret=new DbColGeometry();
				boolean curvePolygon=false;
				if(!strokeArcs){
					curvePolygon=true;
				}
				ret.setType(curvePolygon ? DbColGeometry.CURVEPOLYGON : DbColGeometry.POLYGON);
				// TODO get crs from ili
				ret.setSrsAuth(defaultCrsAuthority);
				ret.setSrsId(defaultCrsCode);
				CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
				ret.setDimension(coord.getDimensions().length);
				setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
				dbCol=ret;
			}
			if(createItfAreaRef){
				if(type instanceof AreaType){
					DbColGeometry ret=new DbColGeometry();
					ret.setType(DbColGeometry.POINT);
					// TODO get crs from ili
					ret.setSrsAuth(defaultCrsAuthority);
					ret.setSrsId(defaultCrsCode);
					ret.setDimension(2); // always 2 (even if defined as 3d in ili)
					CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbCol_georef=ret;
				}
			}
		}else if (type instanceof CoordType){
			DbColGeometry ret=new DbColGeometry();
			ret.setType(DbColGeometry.POINT);
			// TODO get crs from ili
			ret.setSrsAuth(defaultCrsAuthority);
			ret.setSrsId(defaultCrsCode);
			CoordType coord=(CoordType)type;
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
			dbCol=ret;
		}else if (type instanceof CompositionType){
			// skip it
			if(!createGenericStructRef){
				if(isChbaseCatalogueRef(td, attr) && (coalesceCatalogueRef 
						|| Config.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,Config.CATALOGUE_REF_TRAFO)))){
					
					DbColId ret=new DbColId();
					ret.setNotNull(false);
					ret.setPrimaryKey(false);
					if(createFk){
						ret.setReferencedTable(getSqlTableName(((ReferenceType) ((AttributeDef)((CompositionType)type).getComponentType().getAttributes().next()).getDomain()).getReferred()));
					}
					if(createFkIdx){
						ret.setIndex(true);
					}
					trafoConfig.setAttrConfig(attr, Config.CATALOGUE_REF_TRAFO,Config.CATALOGUE_REF_TRAFO_COALESCE);
					dbCol=ret;
				}else{
					// add reference to struct table
					addParentRef(attr);
					dbCol=null;
				}
			}else{
				dbCol=null;
			}
		}else if (type instanceof ReferenceType){
			DbColId ret=new DbColId();
			ret.setNotNull(false);
			ret.setPrimaryKey(false);
			if(createFk){
				ret.setReferencedTable(getSqlTableName(((ReferenceType)type).getReferred()));
			}
			if(createFkIdx){
				ret.setIndex(true);
			}
			dbCol=ret;
		}else if (type instanceof BasketType){
			// skip it; type no longer exists in ili 2.3
			dbCol=null;
		}else if(type instanceof EnumerationType){
			if(createEnumColAsItfCode){
				DbColId ret=new DbColId();
				dbCol=ret;
			}else{
				DbColVarchar ret=new DbColVarchar();
				ret.setSize(255);
				dbCol=ret;				
			}
		}else if(type instanceof NumericType){
			if(type.isAbstract()){
			}else{
				PrecisionDecimal min=((NumericType)type).getMinimum();
				PrecisionDecimal max=((NumericType)type).getMaximum();
				if(min.getAccuracy()>0){
					DbColDecimal ret=new DbColDecimal();
					int size=Math.max(min.toString().length(),max.toString().length());
					int precision=min.getAccuracy();
					//EhiLogger.debug("attr "+ attr.getName()+", maxStr <"+maxStr+">, size "+Integer.toString(size)+", precision "+Integer.toString(precision));
					ret.setSize(size);
					ret.setPrecision(precision);
					dbCol=ret;
				}else{
					DbColNumber ret=new DbColNumber();
					int size=Math.max(min.toString().length(),max.toString().length());
					ret.setSize(size);
					dbCol=ret;
				}
			}
		}else if(type instanceof TextType){
			DbColVarchar ret=new DbColVarchar();
			if(((TextType)type).getMaxLength()>0){
				ret.setSize(((TextType)type).getMaxLength());
			}else{
				ret.setSize(255);
			}
			dbCol=ret;
		}else{
			DbColVarchar ret=new DbColVarchar();
			ret.setSize(255);
			dbCol=ret;
		}

		if (dbCol != null) {
			String sqlName=getSqlAttrName(attr);
			setAttrDbColProps(attr, dbCol, sqlName);
			customMapping.fixupAttribute(dbTable, dbCol, attr);
			dbTable.addColumn(dbCol);
			if (createEnumTxtCol && type instanceof EnumerationType) {
				DbColVarchar ret = new DbColVarchar();
				ret.setSize(255);
				dbCol=ret;
				dbCol.setName(sqlName+DbNames.ENUM_TXT_COL_SUFFIX);
				if (sqlEnableNull) {
					dbCol.setNotNull(false);
				} else {
					if (attr.getDomain().isMandatoryConsideringAliases()) {
						dbCol.setNotNull(true);
					}
				}
				customMapping.fixupAttribute(dbTable, dbCol, attr);
				dbTable.addColumn(dbCol);
			}
		} else {
			customMapping.fixupAttribute(dbTable, null, attr);
		}
		if (dbCol_georef != null) {
			String sqlName=getSqlAttrName(attr)+DbNames.ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX;
			setAttrDbColProps(attr, dbCol_georef, sqlName);
			//customMapping.fixupAttribute(dbTable, dbCol_georef, attr);
			dbTable.addColumn(dbCol_georef);
		}
	}
	private boolean isChbaseCatalogueRef(TransferDescription td,
			AttributeDef attr) {
		if(Ili2cUtility.isPureChbaseCatalogueRef(td, attr)){
			CompositionType type=(CompositionType)attr.getDomain();
			if(type.getCardinality().getMaximum()==1){
				return true;
			}
		}
		return false;
	}

	private void addParentRef(AttributeDef attr){
		CompositionType type = (CompositionType)attr.getDomainResolvingAll();
		Table structClass=type.getComponentType();
		Table root=(Table)structClass.getRootExtending();
		if(root!=null){
			structClass=root;
		}
		DbTableName structClassSqlName=getSqlTableName(structClass);
		
		// find struct table
		DbTable dbTable=schema.findTable(structClassSqlName);
		
		// add ref attr
		String refAttrSqlName=ili2sqlName.mapIliAttributeDefQualified(attr);
		DbColId dbParentId=new DbColId();
		dbParentId.setName(refAttrSqlName);
		dbParentId.setNotNull(false); // values of other struct attrs will have NULL
		dbParentId.setPrimaryKey(false);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+attr.getContainer().getScopedName(null)+"."+attr.getName());
		cmtSep=nl;
		if(cmt.length()>0){
			dbParentId.setComment(cmt.toString());
		}
		if(createFk){
			dbParentId.setReferencedTable(getSqlTableName((Viewable)attr.getContainer()));
		}
		if(createFkIdx){
			dbParentId.setIndex(true);
		}
		dbTable.addColumn(dbParentId);
	}
	protected void setAttrDbColProps(AttributeDef attr, DbColumn dbCol,
			String sqlName) {
		dbCol.setName(sqlName);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		if(sqlName!=attr.getName()){
			cmt.append(cmtSep+"@iliname "+attr.getName());
			cmtSep=nl;
		}
		if(cmt.length()>0){
			dbCol.setComment(cmt.toString());
		}
		if (sqlEnableNull) {
			dbCol.setNotNull(false);
		} else {
			Type type=attr.getDomain();
			if(type==null){
				Evaluable[] ev = (((LocalAttribute)attr).getBasePaths());
				type=((ObjectPath)ev[0]).getType();
			}
			if (type.isMandatoryConsideringAliases()) {
				dbCol.setNotNull(true);
			}
		}
	}
	private DbColumn createSqlTypeCol(String name){
		  DbColVarchar dbCol=new DbColVarchar();
		  dbCol.setName(name);
		  dbCol.setNotNull(true);
		  dbCol.setSize(ili2sqlName.getMaxSqlNameLength());
		  return dbCol;
		}
	public ArrayList<AttributeDef> getSurfaceAttrs() {
		return surfaceAttrs;
	}
}
