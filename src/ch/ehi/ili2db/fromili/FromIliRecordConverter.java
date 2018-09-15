package ch.ehi.ili2db.fromili;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.Holder;

import ch.ehi.basics.types.OutParam;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.ArrayMapping;
import ch.ehi.ili2db.mapping.ColumnWrapper;
import ch.ehi.ili2db.mapping.IliMetaAttrNames;
import ch.ehi.ili2db.mapping.MultiLineMapping;
import ch.ehi.ili2db.mapping.MultiPointMapping;
import ch.ehi.ili2db.mapping.MultiSurfaceMapping;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.TrafoConfigNames;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.sqlgen.repository.DbColBlob;
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
import ch.ehi.sqlgen.repository.DbColXml;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbIndex;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AreaType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.AttributeRef;
import ch.interlis.ili2c.metamodel.BasketType;
import ch.interlis.ili2c.metamodel.BlackboxType;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Evaluable;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.ObjectPath;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PathEl;
import ch.interlis.ili2c.metamodel.PathElAssocRole;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TextType;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.UniqueEl;
import ch.interlis.ili2c.metamodel.UniquenessConstraint;
import ch.interlis.ili2c.metamodel.Unit;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class FromIliRecordConverter extends AbstractRecordConverter {
	private DbSchema schema=null;
	private CustomMapping customMapping=null;
	private HashSet visitedEnumsAttrs=null;
	private String nl=System.getProperty("line.separator");
	private boolean coalesceCatalogueRef=true;
	private boolean coalesceMultiSurface=true;
	private boolean coalesceMultiLine=true;
	private boolean coalesceMultiPoint=true;
	private boolean coalesceArray=true;
	private boolean expandMultilingual=true;
	private boolean createUnique=true;
	private boolean createNumCheck=false;
	private DbExtMetaInfo metaInfo=null;

	public FromIliRecordConverter(TransferDescription td1, NameMapping ili2sqlName,
			Config config, DbSchema schema1, CustomMapping customMapping1,
			DbIdGen idGen1, HashSet visitedEnumsAttrs1, TrafoConfig trafoConfig,	Viewable2TableMapping class2wrapper1
			,DbExtMetaInfo metaInfo
			) {
		super(td1, ili2sqlName, config, idGen1,trafoConfig,class2wrapper1);
		visitedEnumsAttrs=visitedEnumsAttrs1;
		customMapping=customMapping1;
		schema=schema1;
		coalesceCatalogueRef=Config.CATALOGUE_REF_TRAFO_COALESCE.equals(config.getCatalogueRefTrafo());
		coalesceMultiSurface=Config.MULTISURFACE_TRAFO_COALESCE.equals(config.getMultiSurfaceTrafo());
		coalesceMultiLine=Config.MULTILINE_TRAFO_COALESCE.equals(config.getMultiLineTrafo());
		coalesceMultiPoint=Config.MULTIPOINT_TRAFO_COALESCE.equals(config.getMultiPointTrafo());
		coalesceArray=Config.ARRAY_TRAFO_COALESCE.equals(config.getArrayTrafo());
		expandMultilingual=Config.MULTILINGUAL_TRAFO_EXPAND.equals(config.getMultilingualTrafo());
		createUnique=config.isCreateUniqueConstraints();
		createNumCheck=config.isCreateCreateNumChecks();
		this.metaInfo=metaInfo;
	}

	public void generateTable(ViewableWrapper def,int pass)
	throws Ili2dbException
	{
		//EhiLogger.debug("viewable "+def);
		if(pass==1){
			DbTableName sqlName=new DbTableName(schema.getName(),def.getSqlTablename());
			DbTable dbTable=new DbTable();
			dbTable.setName(sqlName);
		  	schema.addTable(dbTable);
			return;
		}
		// second pass; add columns
		DbTableName sqlName=new DbTableName(schema.getName(),def.getSqlTablename());
		DbTable dbTable=schema.findTable(sqlName);
		ViewableWrapper base=def.getExtending();
		{
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(!def.isSecondaryTable()){
			dbTable.setIliName(def.getViewable().getScopedName(null));
			if(def.getViewable().getDocumentation()!=null){
				cmt.append(cmtSep+def.getViewable().getDocumentation());
				cmtSep=nl;
			}
			cmt.append(cmtSep+"@iliname "+def.getViewable().getScopedName(null));
			cmtSep=nl;
		}
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
		}
		}
		
		if(deleteExistingData){
			dbTable.setDeleteDataIfTableExists(true);
		}
		if(base==null && !def.isSecondaryTable()){
		  dbTable.setRequiresSequence(true);
		}
		String baseRef="";
		DbColId dbColId=addKeyCol(dbTable);
		if(base!=null){
		  dbColId.setScriptComment("REFERENCES "+base.getViewable().getScopedName(null));
		  if(createFk){
			  dbColId.setReferencedTable(getSqlType(base.getViewable()));
		  }
                  metaInfo.setColumnInfo(dbTable.getName().getName(), null, dbColId.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, getSqlType(base.getViewable()).getName());
		}else if(def.isSecondaryTable()){
			  if(createFk){
				  dbColId.setReferencedTable(new DbTableName(schema.getName(),def.getMainTable().getSqlTablename()));
			  }
                          metaInfo.setColumnInfo(dbTable.getName().getName(), null, dbColId.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, def.getMainTable().getSqlTablename());
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
		  if(createDatasetCol){
				DbColVarchar t_dsName=new DbColVarchar();
				t_dsName.setName(DbNames.T_DATASET_COL);
				t_dsName.setSize(DbNames.DATASETNAME_COL_SIZE);
				t_dsName.setNotNull(true);
				t_dsName.setIndex(true);
				dbTable.addColumn(t_dsName);
		  }
		DbColumn dbCol;
		if(base==null && !def.isSecondaryTable()){
			if(createTypeDiscriminator || def.includesMultipleTypes()){
				  dbCol=createSqlTypeCol(DbNames.T_TYPE_COL);
				  dbTable.addColumn(dbCol);
			}
			// if CLASS
			  if(!def.isStructure()){
				  if(createIliTidCol || def.getOid()!=null){
						addIliTidCol(dbTable,def.getOid());
				  }
			  }
		  // if STRUCTURE, add ref to parent
		  if(def.isStructure()){
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
			// add sequence attr
			DbColId dbSeq=new DbColId();
			dbSeq.setName(DbNames.T_SEQ_COL);
			//dbSeq.setNotNull(true); // must be optional for cases where struct is exdended by a class
			dbSeq.setPrimaryKey(false);
			dbTable.addColumn(dbSeq);
		  }
		}

		// body
		Iterator<ColumnWrapper> iter=def.getAttrIterator();
		  while (iter.hasNext()) {
			  ColumnWrapper columnWrapper = iter.next();
			  if (columnWrapper.getViewableTransferElement().obj instanceof AttributeDef) {
				  AttributeDef attr = (AttributeDef) columnWrapper.getViewableTransferElement().obj;
                  try{
                      if(!attr.isTransient()){
                          Type proxyType=attr.getDomain();
                          if(proxyType!=null && (proxyType instanceof ObjectType)){
                              // skip implicit particles (base-viewables) of views
                          }else{
                              generateAttr(dbTable,def.getViewable(),attr,columnWrapper.getEpsgCode());
                          }
                      }
                  }catch(Exception ex){
                      throw new Ili2dbException(attr.getContainer().getScopedName(null)+"."+attr.getName(),ex);
                  }
			  }
			  if(columnWrapper.getViewableTransferElement().obj instanceof RoleDef){
				  RoleDef role = (RoleDef) columnWrapper.getViewableTransferElement().obj;
					if(role.getExtending()==null){
						// not an embedded role and roledef not defined in a lightweight association?
						if (!columnWrapper.getViewableTransferElement().embedded && !def.isAssocLightweight()){
							ArrayList<ViewableWrapper> targetTables = getTargetTables(role.getDestination());
						  for(ViewableWrapper targetTable : targetTables){
							  dbColId=new DbColId();
							  DbTableName targetSqlTableName=targetTable.getSqlTable();
							  String roleSqlName=ili2sqlName.mapIliRoleDef(role,sqlName.getName(),targetSqlTableName.getName(),targetTables.size()>1);
							  dbColId.setName(roleSqlName);
							  boolean notNull=false;
							  if(!sqlEnableNull){
								  if(targetTables.size()>1){
									  notNull=false; // multiple alternative FK columns
								  }else{
									  notNull=true;
								  }
							  }
							  dbColId.setNotNull(notNull);
							  dbColId.setPrimaryKey(false);
							  if(createFk){
								  dbColId.setReferencedTable(targetSqlTableName);
							  }
							  metaInfo.setColumnInfo(dbTable.getName().getName(), null, dbColId.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, targetSqlTableName.getName());                                                          
								if(createFkIdx){
									dbColId.setIndex(true);
								}
								String cmt=role.getDocumentation();
								if(cmt!=null && cmt.length()>0){
									dbColId.setComment(cmt);									
								}
							  dbTable.addColumn(dbColId);
							  // handle ordered
							  if(role.isOrdered()){
									// add seqeunce attr
									DbColId dbSeq=new DbColId();
									dbSeq.setName(roleSqlName+"_"+DbNames.T_SEQ_COL);
									dbSeq.setNotNull(notNull);
									dbSeq.setPrimaryKey(false);
									dbTable.addColumn(dbSeq);
							  }
						  }
						}
						// a role of an embedded association?
						if(columnWrapper.getViewableTransferElement().embedded){
							AssociationDef roleOwner = (AssociationDef) role.getContainer();
							if(roleOwner.getDerivedFrom()==null){
								// role is oppend;
								ArrayList<ViewableWrapper> targetTables = getTargetTables(role.getDestination());
							  for(ViewableWrapper targetTable:targetTables){
								  dbColId=new DbColId();
								  DbTableName targetSqlTableName=targetTable.getSqlTable();
								  String roleSqlName=ili2sqlName.mapIliRoleDef(role,sqlName.getName(),targetSqlTableName.getName(),targetTables.size()>1);
								  dbColId.setName(roleSqlName);
								  boolean notNull=false;
								  if(!sqlEnableNull){
									  if(targetTables.size()>1){
										  notNull=false; // multiple alternative FK columns
									  }else if(role.getOppEnd().getDestination()!=def.getViewable()){
										  notNull=false; // other subtypes in of def don't have this FK
									  }else{
										  if(role.getCardinality().getMinimum()==0){
											  notNull=false;
										  }else{
											  notNull=true;
										  }
									  }
								  }
								  dbColId.setNotNull(notNull);
								  dbColId.setPrimaryKey(false);
								  if(createFk){
									  dbColId.setReferencedTable(targetSqlTableName);
								  }
                                                                  metaInfo.setColumnInfo(dbTable.getName().getName(), null, dbColId.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, targetSqlTableName.getName());
									if(createFkIdx){
										dbColId.setIndex(true);
									}
									String cmt=role.getDocumentation();
									if(cmt!=null && cmt.length()>0){
										dbColId.setComment(cmt);									
									}
								  customMapping.fixupEmbeddedLink(dbTable,dbColId,roleOwner,role,targetSqlTableName,colT_ID);
								  dbTable.addColumn(dbColId);
								  // handle ordered
								  if(role.getOppEnd().isOrdered()){
										// add seqeunce attr
										DbColId dbSeq=new DbColId();
										dbSeq.setName(roleSqlName+"_"+DbNames.T_SEQ_COL);
										dbSeq.setNotNull(notNull);
										dbSeq.setPrimaryKey(false);
										dbTable.addColumn(dbSeq);
								  }
								  
							  }
							}
						}
					}
			  }
		}
		  if(createStdCols){
				addStdCol(dbTable);
		  }
		  if(createUnique && !def.isStructure()){
			  // check if UNIQUE mappable
			  Viewable aclass=def.getViewable();
			  Iterator it=aclass.iterator();
			  while(it.hasNext()){
				  Object cnstro=it.next();
				  if(cnstro instanceof UniquenessConstraint){
					  UniquenessConstraint cnstr=(UniquenessConstraint)cnstro;
					  for(int epsgCode:getEpsgCodes(def.getAttrv())) {
	                      HashSet attrs=getUniqueAttrs(cnstr,def.getAttrv(),epsgCode);
	                      // mappable?
	                      if(attrs!=null){
	                          DbIndex dbIndex=new DbIndex();
	                          dbIndex.setPrimary(false);
	                          dbIndex.setUnique(true);
	                          for(Object attro:attrs){
	                              String attrSqlName=null;
	                              if(attro instanceof AttributeDef){
	                                      Type attrType=((AttributeDef) attro).getDomainResolvingAliases();
	                                    if(attrType instanceof CoordType || attrType instanceof LineType) {
	                                        attrSqlName=ili2sqlName.mapIliAttributeDef((AttributeDef) attro,epsgCode,def.getSqlTablename(),null);
	                                    }else {
	                                        attrSqlName=ili2sqlName.mapIliAttributeDef((AttributeDef) attro,null,def.getSqlTablename(),null);
	                                    }
	                              }else if(attro instanceof RoleDef){
	                                  RoleDef role=(RoleDef) attro;
	                                  DbTableName targetSqlTableName=getSqlType(role.getDestination());
	                                  attrSqlName=ili2sqlName.mapIliRoleDef(role,def.getSqlTablename(),targetSqlTableName.getName());
	                              }else{
	                                  throw new IllegalStateException("unexpected attr "+attro);
	                              }
	                              DbColumn idxCol=dbTable.getColumn(attrSqlName);
	                              dbIndex.addAttr(idxCol);
	                          }
	                          dbTable.addIndex(dbIndex);
	                      }
					  }
				  }
			  }
			  
		  }
		  if(!def.isSecondaryTable()){
			  customMapping.fixupViewable(dbTable,def.getViewable());
		  }
	  	
	}

	private Integer[] getEpsgCodes(List<ColumnWrapper> attrv) {
	    HashSet<Integer> epsgCodes=new HashSet<Integer>();
	    for(ColumnWrapper col:attrv) {
	        if(col.getEpsgCode()!=null) {
	            epsgCodes.add(col.getEpsgCode());
	        }
	    }
        return epsgCodes.toArray(new Integer[epsgCodes.size()]);
    }

	private HashSet getUniqueAttrs(UniquenessConstraint cnstr, List<ColumnWrapper> colv,int epsgCode) {
		  if(cnstr.getLocal()){
			  return null;
		  }
		  HashSet wrapperCols=new HashSet();
		  {
		      for(ColumnWrapper col:colv) {
		          if(col.getEpsgCode()!=null) {
		              if(col.getEpsgCode()==epsgCode) {
	                      wrapperCols.add(col.getViewableTransferElement().obj);
		              }
		          }else {
		              wrapperCols.add(col.getViewableTransferElement().obj);
		          }
		      }
		  }
			HashSet ret=new HashSet();
		  UniqueEl attribs=cnstr.getElements();
        	Iterator attri=attribs.iteratorAttribute();
          for (; attri.hasNext();)
          {
         		ObjectPath path=(ObjectPath)attri.next();
         		PathEl pathEles[]=path.getPathElements();
         		if(pathEles.length!=1){
         			return null;
         		}
         		PathEl pathEle=pathEles[0];
         		if(pathEle instanceof AttributeRef){
         			AttributeDef attr=((AttributeRef) pathEle).getAttr();
         			while(attr.getExtending()!=null){
         				attr=(AttributeDef) attr.getExtending();
         			}
         			if(!wrapperCols.contains(attr)){
         				return null;
         			}
         			ret.add(attr);
         		}else if(pathEle instanceof PathElAssocRole){
         			RoleDef role=((PathElAssocRole) pathEle).getRole();
         			while(role.getExtending()!=null){
         				role=(RoleDef) role.getExtending();
         			}
         			if(!wrapperCols.contains(role)){
         				return null;
         			}
         			ret.add(role);
         		}else{
         			return null;
         		}
          }
          return ret;
	}

	public void generateAttr(DbTable dbTable,Viewable aclass,AttributeDef attr,Integer epsgCode)
	throws Ili2dbException
	{
		OutParam<DbColumn> dbCol=new OutParam<DbColumn>();dbCol.value=null;
		OutParam<Unit> unitDef=new OutParam<Unit>();unitDef.value=null;
		OutParam<Boolean> mText=new OutParam<Boolean>();mText.value=false;

		ArrayList<DbColumn> dbColExts=new ArrayList<DbColumn>();
		Type type = attr.getDomainResolvingAll();
		if(createSimpleDbCol(dbTable, aclass, attr, type, dbCol, unitDef, mText, dbColExts)) {
		}else if (type instanceof SurfaceOrAreaType){
			if(createItfLineTables){
				dbCol.value=null;
			}else{
				DbColGeometry ret=new DbColGeometry();
				boolean curvePolygon=false;
				if(!strokeArcs){
					curvePolygon=true;
				}
				ret.setType(curvePolygon ? DbColGeometry.CURVEPOLYGON : DbColGeometry.POLYGON);
				// get crs from ili
				setCrs(ret,epsgCode);
				CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
				ret.setDimension(coord.getDimensions().length);
				setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
				dbCol.value=ret;
			}
			if(createItfAreaRef){
				if(type instanceof AreaType){
					DbColGeometry ret=new DbColGeometry();
					String sqlName=getSqlAttrName(attr,epsgCode,dbTable.getName().getName(),null)+DbNames.ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX;
					ret.setName(sqlName);
					ret.setType(DbColGeometry.POINT);
					setNullable(aclass,attr, ret);
					// get crs from ili
					setCrs(ret,epsgCode);
					ret.setDimension(2); // always 2 (even if defined as 3d in ili)
					CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbColExts.add(ret);
				}
			}
		}else if (type instanceof PolylineType){
			String attrName=attr.getContainer().getScopedName(null)+"."+attr.getName();
			DbColGeometry ret = generatePolylineType((PolylineType)type, attrName);
			setCrs(ret,epsgCode);
			dbCol.value=ret;
		}else if (type instanceof CoordType){
			DbColGeometry ret=new DbColGeometry();
			ret.setType(DbColGeometry.POINT);
			setCrs(ret,epsgCode);
			CoordType coord=(CoordType)type;
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
			dbCol.value=ret;
		}else if (type instanceof CompositionType){
			// skip it
			if(!createGenericStructRef){
				if(isChbaseCatalogueRef(td, attr) && (coalesceCatalogueRef 
						|| TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.CATALOGUE_REF_TRAFO)))){
                    ArrayList<ViewableWrapper> targetTables = getTargetTables(getCatalogueRefTarget(type));
                    for(ViewableWrapper targetTable:targetTables)
                    {
                        DbColId ret=new DbColId();
                        ret.setName(ili2sqlName.mapIliAttributeDef(attr,dbTable.getName().getName(),targetTable.getSqlTablename(),targetTables.size()>1));
                        ret.setNotNull(false);
                        ret.setPrimaryKey(false);
                        if(createFk){
                            ret.setReferencedTable(targetTable.getSqlTable());
                        }
                        metaInfo.setColumnInfo(dbTable.getName().getName(), null, ret.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, targetTable.getSqlTablename());
                        if(createFkIdx){
                            ret.setIndex(true);
                        }
                        dbColExts.add(ret);
                    }
                    trafoConfig.setAttrConfig(attr, TrafoConfigNames.CATALOGUE_REF_TRAFO,TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE);
				}else if(Ili2cUtility.isMultiSurfaceAttr(td, attr) && (coalesceMultiSurface 
						|| TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTISURFACE_TRAFO)))){
					multiSurfaceAttrs.addMultiSurfaceAttr(attr);
					DbColGeometry ret=new DbColGeometry();
					boolean curvePolygon=false;
					if(!strokeArcs){
						curvePolygon=true;
					}
					ret.setType(curvePolygon ? DbColGeometry.MULTISURFACE : DbColGeometry.MULTIPOLYGON);
					// get crs from ili
					AttributeDef surfaceAttr = multiSurfaceAttrs.getSurfaceAttr(attr);
					setCrs(ret,epsgCode);
					SurfaceType surface=((SurfaceType) surfaceAttr.getDomainResolvingAliases());
					CoordType coord=(CoordType)surface.getControlPointDomain().getType();
					ret.setDimension(coord.getDimensions().length);
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbCol.value=ret;
					trafoConfig.setAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO,TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE);
				}else if(Ili2cUtility.isMultiLineAttr(td, attr) && (coalesceMultiLine 
						|| TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTILINE_TRAFO)))){
					multiLineAttrs.addMultiLineAttr(attr);
					DbColGeometry ret=new DbColGeometry();
					boolean curvePolyline=false;
					if(!strokeArcs){
						curvePolyline=true;
					}
					ret.setType(curvePolyline ? DbColGeometry.MULTICURVE : DbColGeometry.MULTILINESTRING);
					// get crs from ili
					AttributeDef polylineAttr = multiLineAttrs.getPolylineAttr(attr);
					setCrs(ret,epsgCode);
					PolylineType polylineType=((PolylineType) polylineAttr.getDomainResolvingAliases());
					CoordType coord=(CoordType)polylineType.getControlPointDomain().getType();
					ret.setDimension(coord.getDimensions().length);
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbCol.value=ret;
					trafoConfig.setAttrConfig(attr, TrafoConfigNames.MULTILINE_TRAFO,TrafoConfigNames.MULTILINE_TRAFO_COALESCE);
				}else if(Ili2cUtility.isMultiPointAttr(td, attr) && (coalesceMultiPoint 
						|| TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTIPOINT_TRAFO)))){
					multiPointAttrs.addMultiPointAttr(attr);
					DbColGeometry ret=new DbColGeometry();
					ret.setType(DbColGeometry.MULTIPOINT);
					// get crs from ili
					AttributeDef coordAttr = multiPointAttrs.getCoordAttr(attr);
					setCrs(ret,epsgCode);
					CoordType coord=(CoordType) ( coordAttr.getDomainResolvingAliases());
					ret.setDimension(coord.getDimensions().length);
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbCol.value=ret;
					trafoConfig.setAttrConfig(attr, TrafoConfigNames.MULTIPOINT_TRAFO,TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE);
				}else if(Ili2cUtility.isArrayAttr(td, attr) && (coalesceArray 
						|| TrafoConfigNames.ARRAY_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.ARRAY_TRAFO)))){
					arrayAttrs.addArrayAttr(attr);
					ArrayMapping attrMapping=arrayAttrs.getMapping(attr);
					AttributeDef localAttr=attrMapping.getValueAttr();
					Type localType = localAttr.getDomainResolvingAll();
					if(!createSimpleDbCol(dbTable, aclass, localAttr, localType, dbCol, unitDef, mText, dbColExts)) {
						  throw new IllegalStateException("unexpected attr type "+localAttr.getScopedName());
					}
					dbCol.value.setArraySize(DbColumn.UNLIMITED_ARRAY);		
					trafoConfig.setAttrConfig(attr, TrafoConfigNames.ARRAY_TRAFO,TrafoConfigNames.ARRAY_TRAFO_COALESCE);
				}else if(isChbaseMultilingual(td, attr) && (expandMultilingual 
							|| TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTILINGUAL_TRAFO)))){
					for(String sfx:DbNames.MULTILINGUAL_TXT_COL_SUFFIXS){
						DbColVarchar ret=new DbColVarchar();
						ret.setName(getSqlAttrName(attr,null,dbTable.getName().getName(),null)+sfx);
						ret.setSize(DbColVarchar.UNLIMITED);
						ret.setNotNull(false);
						ret.setPrimaryKey(false);
						dbColExts.add(ret);
					}
					trafoConfig.setAttrConfig(attr, TrafoConfigNames.MULTILINGUAL_TRAFO,TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND);
				}else{
					// add reference col from struct ele to parent obj to struct table
					addParentRef(aclass,attr);
					dbCol.value=null;
				}
			}else{
				dbCol.value=null;
			}
		}else if (type instanceof ReferenceType){
			ArrayList<ViewableWrapper> targetTables = getTargetTables(((ReferenceType)type).getReferred());
			for(ViewableWrapper targetTable:targetTables)
			{
				DbColId ret=new DbColId();
				ret.setName(ili2sqlName.mapIliAttributeDef(attr,dbTable.getName().getName(),targetTable.getSqlTablename(),targetTables.size()>1));
				ret.setNotNull(false);
				ret.setPrimaryKey(false);
				if(createFk){
					ret.setReferencedTable(targetTable.getSqlTable());
				}
                                metaInfo.setColumnInfo(dbTable.getName().getName(), null, ret.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, targetTable.getSqlTablename());
				if(createFkIdx){
					ret.setIndex(true);
				}
				dbColExts.add(ret);
			}
		}else{
			DbColVarchar ret=new DbColVarchar();
			ret.setSize(255);
			dbCol.value=ret;
		}
		if(type instanceof EnumerationType) {
			if (createEnumTxtCol) {
				DbColVarchar ret = new DbColVarchar();
				ret.setSize(255);
				ret.setName(getSqlAttrName(attr,null,dbTable.getName().getName(),null)+DbNames.ENUM_TXT_COL_SUFFIX);
				setNullable(aclass,attr, ret);
				dbColExts.add(ret);
			}
		}
		if (dbCol.value != null) {
			String sqlColName=getSqlAttrName(attr,epsgCode,dbTable.getName().getName(),null);
			setAttrDbColProps(aclass,attr, dbCol.value, sqlColName);
			String subType=null;
			Viewable attrClass=(Viewable)attr.getContainer();
			if(attrClass!=aclass && attrClass.isExtending(aclass)){
				subType=getSqlType(attrClass).getName();
			}
			if(unitDef.value!=null){
				String unitName=unitDef.value.getName();
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_UNIT, unitName);
			}
			if(mText.value){
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_TEXTKIND, DbExtMetaInfo.TAG_COL_TEXTKIND_MTEXT);
			}
			if(dbCol.value instanceof DbColGeometry) {
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C1_MIN, Double.toString(((DbColGeometry) dbCol.value).getMin1()));
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C1_MAX, Double.toString(((DbColGeometry) dbCol.value).getMax1()));
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C2_MIN, Double.toString(((DbColGeometry) dbCol.value).getMin2()));
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C2_MAX, Double.toString(((DbColGeometry) dbCol.value).getMax2()));
				if(((DbColGeometry) dbCol.value).getDimension()==3) {
					metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C3_MIN, Double.toString(((DbColGeometry) dbCol.value).getMin3()));
					metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_C3_MAX, Double.toString(((DbColGeometry) dbCol.value).getMax3()));
				}
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_GEOMTYPE, getIli2DbGeomType(((DbColGeometry) dbCol.value).getType()));
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_SRID, ((DbColGeometry) dbCol.value).getSrsId());
				metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_COORDDIMENSION, Integer.toString(((DbColGeometry) dbCol.value).getDimension()));
			}
			String dispName = attr.getMetaValues().getValue(IliMetaAttrNames.METAATTR_DISPNAME);
			if (dispName!=null){
			    metaInfo.setColumnInfo(dbTable.getName().getName(), subType, sqlColName, DbExtMetaInfo.TAG_COL_DISPNAME, dispName);
			}
			customMapping.fixupAttribute(dbTable, dbCol.value, attr);
			dbTable.addColumn(dbCol.value);
		}
		for(DbColumn dbColExt:dbColExts) {
			customMapping.fixupAttribute(dbTable, dbColExt, attr);
			dbTable.addColumn(dbColExt);
		}
		if(dbCol.value==null && dbColExts.size()==0){
			customMapping.fixupAttribute(dbTable, null, attr);
		}
	}
	
	static public String getIli2DbGeomType(int type)
	{
		 switch(type){
			case DbColGeometry.POINT:
				return "POINT";
			case DbColGeometry.LINESTRING:
				return "LINESTRING";
			case DbColGeometry.POLYGON:
				return "POLYGON";
			case DbColGeometry.MULTIPOINT:
				return "MULTIPOINT";
			case DbColGeometry.MULTILINESTRING:
				return "MULTILINESTRING";
			case DbColGeometry.MULTIPOLYGON:
				return "MULTIPOLYGON";
			case DbColGeometry.GEOMETRYCOLLECTION:
				return "GEOMETRYCOLLECTION";
			case DbColGeometry.CIRCULARSTRING:
				return "CIRCULARSTRING";
			case DbColGeometry.COMPOUNDCURVE:
				return "COMPOUNDCURVE";
			case DbColGeometry.CURVEPOLYGON:
				return "CURVEPOLYGON";
			case DbColGeometry.MULTICURVE:
				return "MULTICURVE";
			case DbColGeometry.MULTISURFACE:
				return "MULTISURFACE";
			case DbColGeometry.POLYHEDRALSURFACE:
				return "POLYHEDRALSURFACE";
			case DbColGeometry.TIN:
				return "TIN";
			case DbColGeometry.TRIANGLE:
				return "TRIANGLE";
			default:
				throw new IllegalArgumentException();
		 }
	}

	private boolean createSimpleDbCol(DbTable dbTable, Viewable aclass, AttributeDef attr, Type type,
			OutParam<DbColumn> dbCol, OutParam<Unit> unitDef, OutParam<Boolean> mText, ArrayList<DbColumn> dbColExts) {
		if (attr.isDomainBoolean()) {
			dbCol.value= new DbColBoolean();
		}else if (attr.isDomainIli1Date()) {
			dbCol.value= new DbColDate();
		}else if (attr.isDomainIliUuid()) {
			dbCol.value= new DbColUuid();
		}else if (attr.isDomainIli2Date()) {
			dbCol.value= new DbColDate();
		}else if (attr.isDomainIli2DateTime()) {
			dbCol.value= new DbColDateTime();
		}else if (attr.isDomainIli2Time()) {
			dbCol.value= new DbColTime();
		}else if (type instanceof BasketType){
			// skip it; type no longer exists in ili 2.3
			dbCol.value=null;
		}else if(type instanceof EnumerationType){
			visitedEnumsAttrs.add(attr);
			if(createEnumColAsItfCode){
				DbColId ret=new DbColId();
				dbCol.value=ret;
			}else{
				DbColVarchar ret=new DbColVarchar();
				ret.setSize(255);
				dbCol.value=ret;				
			}
		}else if(type instanceof NumericType){
			if(type.isAbstract()){
			}else{
				PrecisionDecimal min=((NumericType)type).getMinimum();
				PrecisionDecimal max=((NumericType)type).getMaximum();
				int minLen=min.toString().length();
				int maxLen=max.toString().length();
				if(min.toString().startsWith("-")){
					minLen-=1;
				}
				if(max.toString().startsWith("-")){
					maxLen-=1;
				}
				if(min.getAccuracy()>0){
					DbColDecimal ret=new DbColDecimal();
					int size=Math.max(minLen,maxLen)-1;
					int precision=min.getAccuracy();
					//EhiLogger.debug("attr "+ attr.getName()+", maxStr <"+maxStr+">, size "+Integer.toString(size)+", precision "+Integer.toString(precision));
					ret.setSize(size);
					ret.setPrecision(precision);
					if(createNumCheck){
						ret.setMinValue(min.doubleValue());
						ret.setMaxValue(max.doubleValue());
					}
					dbCol.value=ret;
				}else{
					DbColNumber ret=new DbColNumber();
					int size=Math.max(minLen,maxLen);
					ret.setSize(size);
					if(createNumCheck){
						ret.setMinValue((int)min.doubleValue());
						ret.setMaxValue((int)max.doubleValue());
					}
					dbCol.value=ret;
				}
				unitDef.value=((NumericType)type).getUnit();
			}
		}else if(type instanceof TextType){
			DbColVarchar ret=new DbColVarchar();
			if(((TextType)type).getMaxLength()>0){
				ret.setSize(((TextType)type).getMaxLength());
			}else{
				ret.setSize(DbColVarchar.UNLIMITED);
			}
			if(!((TextType)type).isNormalized()){
			    mText.value=true;
			}
			dbCol.value=ret;
		}else if(type instanceof BlackboxType){
			if(((BlackboxType)type).getKind()==BlackboxType.eXML){
				DbColXml ret=new DbColXml();
				dbCol.value=ret;
			}else{
				DbColBlob ret=new DbColBlob();
				dbCol.value=ret;
			}
		}else{
			return false;
		}
		return true;
	}
	private boolean isChbaseMultilingual(TransferDescription td,
			AttributeDef attr) {
		if(Ili2cUtility.isPureChbaseMultilingualText(td, attr) || Ili2cUtility.isPureChbaseMultilingualMText(td, attr)){
			CompositionType type=(CompositionType)attr.getDomain();
			if(type.getCardinality().getMaximum()==1){
				return true;
			}
		}
		return false;
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
	private void addParentRef(Viewable parentTable,AttributeDef attr){
		CompositionType type = (CompositionType)attr.getDomainResolvingAll();
		Table structClass=type.getComponentType();
		// if abstract struct, might have multiple tables!
		for(ViewableWrapper structWrapper : getStructWrappers(structClass)){
			DbTableName structClassSqlName=structWrapper.getSqlTable();
			
			// find struct table
			DbTable dbTable=schema.findTable(structClassSqlName);
			
			// add ref attr
			String refAttrSqlName=ili2sqlName.mapIliAttributeDefReverse(attr,structClassSqlName.getName(),class2wrapper.get(parentTable).getSqlTablename());
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
				dbParentId.setReferencedTable(class2wrapper.get(parentTable).getSqlTable());
			}
                        metaInfo.setColumnInfo(dbTable.getName().getName(), null, dbParentId.getName(), DbExtMetaInfo.TAG_COL_FOREIGNKEY, class2wrapper.get(parentTable).getSqlTablename());
			if(createFkIdx){
				dbParentId.setIndex(true);
			}
			dbTable.addColumn(dbParentId);
		}
	}

	protected void setAttrDbColProps(Viewable aclass,AttributeDef attr, DbColumn dbCol,
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
		setNullable(aclass,attr, dbCol);
	}

	public void setNullable(Viewable aclass,AttributeDef attr, DbColumn dbCol) {
		if (sqlEnableNull) {
			dbCol.setNotNull(false);
		} else {
			Type type=attr.getDomain();
			if(type==null){
				Evaluable[] ev = (((LocalAttribute)attr).getBasePaths());
				type=((ObjectPath)ev[0]).getType();
			}
			if (type.isMandatoryConsideringAliases()) {
				// attr not defined in sub-class
				if (attr.getContainer()==aclass || aclass.isExtending(attr.getContainer())) {
					dbCol.setNotNull(true);
				}
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
}
