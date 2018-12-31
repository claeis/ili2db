package ch.ehi.ili2db.fromxtf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.types.OutParam;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.IliNames;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.ArrayMapping;
import ch.ehi.ili2db.mapping.ColumnWrapper;
import ch.ehi.ili2db.mapping.MultiLineMapping;
import ch.ehi.ili2db.mapping.MultiPointMapping;
import ch.ehi.ili2db.mapping.MultiSurfaceMapping;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.TrafoConfigNames;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AreaType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BlackboxType;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.NumericalType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class FromXtfRecordConverter extends AbstractRecordConverter {
	private SqlColumnConverter geomConv=null;
	private java.sql.Timestamp today=null;
	private int defaultSrsid=0;
	private Connection conn=null;
	private String dbusr=null;
	private String datasetName=null;
	private boolean isItfReader;
	private XtfidPool oidPool=null;
	private HashMap tag2class=null;
	private Integer defaultEpsgCode=null;
    private Map<AttributeDef,EnumValueMap> enumCache=new HashMap<AttributeDef,EnumValueMap>();
    private String dbSchema;
	
	public FromXtfRecordConverter(TransferDescription td1, NameMapping ili2sqlName,HashMap tag2class1,
			Config config,
			DbIdGen idGen1,SqlColumnConverter geomConv1,Connection conn1,String dbusr1,boolean isItfReader1,
			XtfidPool oidPool1,TrafoConfig trafoConfig,	
			Viewable2TableMapping class2wrapper1,String datasetName,String dbSchema) {
		super(td1, ili2sqlName, config, idGen1,trafoConfig,class2wrapper1);
		conn=conn1;
		tag2class=tag2class1;
		dbusr=dbusr1;
		oidPool=oidPool1;
		this.geomConv=geomConv1;
		isItfReader=isItfReader1;
		this.datasetName=datasetName;
		this.dbSchema=dbSchema;
		today=new java.sql.Timestamp(System.currentTimeMillis());
		try{
			Integer srsid=geomConv.getSrsid(defaultCrsAuthority,defaultCrsCode,conn);
			if(srsid==null){
				defaultSrsid=-1;
			} else {
				defaultSrsid = srsid;
			}
		}catch(UnsupportedOperationException ex){
			EhiLogger.logAdaption("no CRS support by converter; use -1 as default srsid");
			defaultSrsid=-1;
		}catch(ConverterException ex){
			throw new IllegalArgumentException("failed to get srsid for "+defaultCrsAuthority+":"+defaultCrsCode+", "+ex.getLocalizedMessage());
		}
		defaultEpsgCode=TransferFromIli.parseEpsgCode(defaultCrsAuthority+":"+defaultCrsCode);
		
	}
	public void writeRecord(long basketSqlId, java.util.Map<String,String> genericDomains,IomObject iomObj,Viewable iomClass,
			StructWrapper structEle, ViewableWrapper aclass, String sqlType,
			long sqlId, boolean updateObj, PreparedStatement ps,ArrayList structQueue,Viewable originalClass)
			throws SQLException, ConverterException {
		int valuei=1;
		
		if(updateObj){
			// if update, t_id is last param
			//ps.setInt(valuei, sqlId);
			//valuei++;
		}else{
			ps.setLong(valuei, sqlId);
			valuei++;
		}
		
		if(createBasketCol){
			ps.setLong(valuei, basketSqlId);
			valuei++;
		}
		if(createDatasetCol){
			ps.setString(valuei, datasetName);
			valuei++;
		}
		
		if(!aclass.isSecondaryTable()){
			if(aclass.getExtending()==null){
				if(createTypeDiscriminator || aclass.includesMultipleTypes()){
					ps.setString(valuei, sqlType);
					valuei++;
				}
				// if class
				if(structEle==null){
					if(!updateObj){
						if(!aclass.isStructure()){
							if(createIliTidCol || aclass.getOid()!=null){
								// import TID from transfer file
								if(AbstractRecordConverter.isUuidOid(td, aclass.getOid())){
									 Object toInsertUUID = geomConv.fromIomUuid(iomObj.getobjectoid());
									 ps.setObject(valuei, toInsertUUID);
								}else{
									ps.setString(valuei, iomObj.getobjectoid());
								}
								valuei++;
							}
						}
					}
				}
				// if struct, add ref to parent
				if(structEle!=null){
					ps.setLong(valuei, structEle.getParentSqlId());
					valuei++;
					if(createGenericStructRef){
						ps.setString(valuei, structEle.getParentSqlType());
						valuei++;
						// T_ParentAttr
						ps.setString(valuei, structEle.getParentSqlAttr());
						valuei++;
					}
					// T_Seq
					ps.setInt(valuei, structEle.getStructi());
					valuei++;
				}
			}
		}
 
		HashMap attrs=getIomObjectAttrs(iomClass);
		Iterator<ColumnWrapper> iter = aclass.getAttrIterator();
		while (iter.hasNext()) {
		    ColumnWrapper columnWrapper=iter.next();
			ViewableTransferElement obj = columnWrapper.getViewableTransferElement();
			if (obj.obj instanceof AttributeDef) {
				AttributeDef attr = (AttributeDef) obj.obj;
			   AttributeDef rootAttr=Ili2cUtility.getRootBaseAttr(attr);
			   if(!attrs.containsKey(rootAttr)){
			       ; // skip attrs that are not attrs of class of current object 
			   }else {
                if(!attr.isTransient()){
                    Type proxyType=attr.getDomain();
                    if(proxyType!=null && (proxyType instanceof ObjectType)){
                        // skip implicit particles (base-viewables) of views
                    }else{
                        valuei = addAttrValue(iomObj, sqlType, sqlId, aclass.getSqlTablename(),ps,
                                valuei, attr,(AttributeDef)attrs.get(rootAttr),columnWrapper.getEpsgCode(),structQueue,genericDomains,originalClass);
                    }
                }
			   }
			}
			if(obj.obj instanceof RoleDef){
				RoleDef role = (RoleDef) obj.obj;
				if(true) { // role.getExtending()==null){
					if(!attrs.containsKey(role)){
					    ; // role is not a property of class of current object
					}else {						
						 String refoid=null;
						String roleName=role.getName();
						// a role of an embedded association?
						if(obj.embedded){
							AssociationDef roleOwner = (AssociationDef) role.getContainer();
							if(roleOwner.getDerivedFrom()==null){
								// not just a link?
								 IomObject structvalue=iomObj.getattrobj(roleName,0);
								if (roleOwner.getAttributes().hasNext()
									|| roleOwner.getLightweightAssociations().iterator().hasNext()) {
									 // TODO handle attributes of link
								}
								if(structvalue!=null){
									refoid=structvalue.getobjectrefoid();
									long orderPos=structvalue.getobjectreforderpos();
									if(orderPos!=0){
									   // refoid,orderPos
									   //ret.setStringAttribute(roleName, refoid);
									   //ret.setStringAttribute(roleName+".orderPos", Long.toString(orderPos));
									}else{
									   // refoid
									   //ret.setStringAttribute(roleName, refoid);
									}
								}else{
									refoid=null;
								}
							}
						 }else{
							 IomObject structvalue=iomObj.getattrobj(roleName,0);
							 refoid=structvalue.getobjectrefoid();
							 long orderPos=structvalue.getobjectreforderpos();
							 if(orderPos!=0){
								// refoid,orderPos
								//ret.setStringAttribute(roleName, refoid);
								//ret.setStringAttribute(roleName+".orderPos", Long.toString(orderPos));
							 }else{
								// refoid
								//ret.setStringAttribute(roleName, refoid);
							 }
						 }
						OutParam<Integer> valueiRef=new OutParam<Integer>(valuei);
						setReferenceColumn(ps,role.getDestination(),refoid,valueiRef);
						valuei=valueiRef.value;
					}
				}
			 }
		}
		if(createStdCols){
			// T_LastChange
			ps.setTimestamp(valuei, today);
			valuei++;
			// T_CreateDate
			if(!updateObj){
				ps.setTimestamp(valuei, today);
				valuei++;
			}
			// T_User
			ps.setString(valuei, dbusr);
			valuei++;
		}
		if(updateObj){
			// if update, t_id is last param
			ps.setLong(valuei, sqlId);
			valuei++;
		}else{
			// if insert, t_id is first param
			//ps.setInt(valuei, sqlId);
			//valuei++;
		}
	}
	private void setReferenceColumn(PreparedStatement ps,
			AbstractClassDef destination, String refoid, OutParam<Integer> valuei) throws SQLException {
	  	String targetRootClassName=Ili2cUtility.getRootViewable(destination).getScopedName(null);
	  	ViewableWrapper targetObjTable=null;
		ArrayList<ViewableWrapper> targetTables = getTargetTables(destination);
	  	if(refoid!=null){
		  	String targetObjClass=oidPool.getObjecttag(targetRootClassName,refoid);
		  	if(targetObjClass==null){
		  		// unknown object
		  		refoid=null; // handle reference as if it is null
		  	}else{
			  	targetObjTable=getViewableWrapper(getSqlType((Viewable) tag2class.get(targetObjClass)).getName());
			  	while(!targetTables.contains(targetObjTable)){
			  		targetObjTable=targetObjTable.getExtending();
			  	}
			  	if(targetObjTable==null){
			  		throw new IllegalStateException("targetObjTable==null");
			  	}
		  	}
	  	}
		  for(ViewableWrapper targetTable : targetTables){
			  	if(refoid!=null && targetTable==targetObjTable){
				   long refsqlId=oidPool.getObjSqlId(targetRootClassName,refoid);
				   ps.setLong(valuei.value, refsqlId);
				}else{
					ps.setNull(valuei.value, Types.BIGINT);
				}
				valuei.value++;
		  }
	}
	/** creates an insert statement for a given viewable.
	 * @param sqlTableName table name of viewable
	 * @param aclass viewable
	 * @return insert statement
	 */
	public String createInsertStmt(boolean isUpdate,Viewable iomClass,DbTableName sqlTableName,ViewableWrapper aclass,StructWrapper structEle){
		StringBuffer ret = new StringBuffer();
		StringBuffer values = new StringBuffer();
		//INSERT INTO table_name (column1,column2,column3,...)
		//VALUES (value1,value2,value3,...);
		
		//UPDATE table_name
		//SET column1=value1,column2=value2,...
		//WHERE some_column=some_value;
		if(isUpdate){
			ret.append("UPDATE ");
		}else{
			ret.append("INSERT INTO ");
		}
		ret.append(sqlTableName.getQName());
		String sep=null;
		if(isUpdate){
			sep=" SET ";
		}else{
			sep=" (";
		}
		
		// add T_Id
		if(!isUpdate){
			ret.append(sep);
			ret.append(colT_ID);
			values.append("?");
			sep=",";
		}
		
		// add T_basket
		if(createBasketCol){
			ret.append(sep);
			ret.append(DbNames.T_BASKET_COL);
			if(isUpdate){
				ret.append("=?");
			}else{
				values.append(",?");
			}
			sep=",";
		}
		if(createDatasetCol){
			ret.append(sep);
			ret.append(DbNames.T_DATASET_COL);
			if(isUpdate){
				ret.append("=?");
			}else{
				values.append(",?");
			}
			sep=",";
		}
		
		if(!aclass.isSecondaryTable()){
			// if root, add type
			if(aclass.getExtending()==null){
				if(createTypeDiscriminator || aclass.includesMultipleTypes()){
					ret.append(sep);
					ret.append(DbNames.T_TYPE_COL);
					if(isUpdate){
						ret.append("=?");
					}else{
						values.append(",?");
					}
					sep=",";
				}
				// if Class
				if(!aclass.isStructure()){
					if(!isUpdate){
						if(createIliTidCol || aclass.getOid()!=null){
							ret.append(sep);
							ret.append(DbNames.T_ILI_TID_COL);
							values.append(",?");
							sep=",";
						}
					}
				}
				// if STRUCTURE, add ref to parent
				if(aclass.isStructure()){
					if(structEle==null){
						// struct is extended by a class and current object is an instance of the class
					}else{
						// current object is an instance of the structure
						if(createGenericStructRef){
							ret.append(sep);
							ret.append(DbNames.T_PARENT_ID_COL);
							if(isUpdate){
								ret.append("=?");
							}else{
								values.append(",?");
							}
							sep=",";
							ret.append(sep);
							ret.append(DbNames.T_PARENT_TYPE_COL);
							if(isUpdate){
								ret.append("=?");
							}else{
								values.append(",?");
							}
							sep=",";
							// attribute name in parent class
							ret.append(sep);
							ret.append(DbNames.T_PARENT_ATTR_COL);
							if(isUpdate){
								ret.append("=?");
							}else{
								values.append(",?");
							}
							sep=",";
						}else{
							ret.append(sep);
							Viewable parentViewable=getViewable(structEle.getParentSqlType());
							ViewableWrapper parentTable=getViewableWrapperOfAbstractClass((Viewable)structEle.getParentAttr().getContainer(),parentViewable);
							ret.append(ili2sqlName.mapIliAttributeDefReverse(structEle.getParentAttr(),sqlTableName.getName(),parentTable.getSqlTablename()));
							if(isUpdate){
								ret.append("=?");
							}else{
								values.append(",?");
							}
							sep=",";
						}
						// seqeunce (not null if LIST)
						ret.append(sep);
						ret.append(DbNames.T_SEQ_COL);
						if(isUpdate){
							ret.append("=?");
						}else{
							values.append(",?");
						}
						sep=",";
					}
				}
			}
		}
		
		HashMap attrs=getIomObjectAttrs(iomClass);
		Iterator<ColumnWrapper> iter = aclass.getAttrIterator();
		while (iter.hasNext()) {
		    ColumnWrapper columnWrapper=iter.next();
		   ViewableTransferElement obj = columnWrapper.getViewableTransferElement();
		   if (obj.obj instanceof AttributeDef) {
			   AttributeDef attr = (AttributeDef) obj.obj;
			   AttributeDef rootAttr=Ili2cUtility.getRootBaseAttr(attr);
			   if(!attrs.containsKey(rootAttr)){
			       ; // skip attrs that are not attrs of class of current object 
			   }else {
					if(!attr.isTransient()){
						Type proxyType=attr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							   sep = addAttrToInsertStmt(isUpdate,ret, values, sep, attr,(AttributeDef)attrs.get(rootAttr),columnWrapper.getEpsgCode(),sqlTableName.getName());
						}
					}
			   }
		   }
		   if(obj.obj instanceof RoleDef){
			   RoleDef role = (RoleDef) obj.obj;
			   if(true) { //role.getExtending()==null){
			       RoleDef rootRole=Ili2cUtility.getRootBaseRole(role);
				   if(!attrs.containsKey(rootRole)){
				       ; // skip roles, that are not part of class of current object
				   }else {
						ArrayList<ViewableWrapper> targetTables = getTargetTables(role.getDestination());
						  for(ViewableWrapper targetTable : targetTables){
								String roleName=ili2sqlName.mapIliRoleDef(role,sqlTableName.getName(),targetTable.getSqlTablename(),targetTables.size()>1);
								// a role of an embedded association?
								if(obj.embedded){
									AssociationDef roleOwner = (AssociationDef) role.getContainer();
									if(roleOwner.getDerivedFrom()==null){
										 // TODO if(orderPos!=0){
										 ret.append(sep);
										 ret.append(roleName);
											if(isUpdate){
												ret.append("=?");
											}else{
												values.append(",?");
											}
											sep=",";
									}
								 }else{
									 // TODO if(orderPos!=0){
									 ret.append(sep);
									 ret.append(roleName);
										if(isUpdate){
											ret.append("=?");
										}else{
											values.append(",?");
										}
										sep=",";
								 }
						  }
					   
				   }
				}
			}
		}
		// stdcols
		if(createStdCols){
			ret.append(sep);
			ret.append(DbNames.T_LAST_CHANGE_COL);
			if(isUpdate){
				ret.append("=?");
			}else{
				values.append(",?");
			}
			sep=",";
			
			if(!isUpdate){
				ret.append(sep);
				ret.append(DbNames.T_CREATE_DATE_COL);
				values.append(",?");
				sep=",";
			}
			
			ret.append(sep);
			ret.append(DbNames.T_USER_COL);
			if(isUpdate){
				ret.append("=?");
			}else{
				values.append(",?");
			}
			sep=",";
		}

		if(isUpdate){
			//WHERE some_column=some_value;
			// add T_Id
			ret.append(" WHERE ");
			ret.append(colT_ID);
			ret.append("=?");
		}else{
			ret.append(") VALUES (");
			ret.append(values);
			ret.append(")");
		}
		
		return ret.toString();
	}
	private ViewableWrapper getViewableWrapperOfAbstractClass(
			Viewable abstractClass, Viewable concreteClass) {
		if(abstractClass==concreteClass){
			return class2wrapper.get(concreteClass);
		}
		ArrayList<ViewableWrapper> rets=new ArrayList<ViewableWrapper>();
		rets.add(class2wrapper.get(concreteClass));
		Viewable superClass=(Viewable) concreteClass.getExtending();
		while(superClass!=null){
			ViewableWrapper ret2=class2wrapper.get(superClass);
			if(ret2!=null){
				if(!rets.contains(ret2)){
					rets.add(0,ret2);
				}
			}
			if(superClass==abstractClass){
				break;
			}
			superClass=(Viewable) superClass.getExtending();
		}
		for(int i=0;i<rets.size();i++){
			ViewableWrapper ret=rets.get(i);
			if(i==rets.size()-1){
				return ret;
			}
			if(!TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(trafoConfig.getViewableConfig(ret.getViewable(), TrafoConfigNames.INHERITANCE_TRAFO))){
				return ret;
			}
		}
		return null;
	}
	public ViewableWrapper getViewableWrapper(String sqlType){
		Viewable aclass = getViewable(sqlType);
		return class2wrapper.get(aclass);
	}
	private Viewable getViewable(String sqlType) {
		String iliQname=ili2sqlName.mapSqlTableName(sqlType);
		Viewable aclass=(Viewable) tag2class.get(iliQname);
		return aclass;
	}
	public String addAttrToInsertStmt(boolean isUpdate,
			StringBuffer ret, StringBuffer values, String sep, AttributeDef attr,AttributeDef classAttr,Integer epsgCode,String sqlTableName) {
		if(true) { // attr.getExtending()==null){
			Type type = attr.getDomainResolvingAliases();
			String attrSqlName=ili2sqlName.mapIliAttributeDef(attr,epsgCode,sqlTableName,null);
			if (attr.isDomainBoolean()) {
					ret.append(sep);
					ret.append(attrSqlName);
					if(isUpdate){
						ret.append("=?");
					}else{
						values.append(",?");
					}
					sep=",";
			}else if (type instanceof CompositionType){
				if(TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.CATALOGUE_REF_TRAFO))){
	                ArrayList<ViewableWrapper> targetTables = getTargetTables(getCatalogueRefTarget(type));
	                for(ViewableWrapper targetTable:targetTables)
	                {
	                    attrSqlName=ili2sqlName.mapIliAttributeDef(attr,sqlTableName,targetTable.getSqlTablename(),targetTables.size()>1);
	                    ret.append(sep);
	                    ret.append(attrSqlName);
	                    if(isUpdate){
	                        ret.append("=?");
	                    }else{
	                        values.append(",?");
	                    }
	                    sep=",";
	                }
				}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO))){
					 ret.append(sep);
					 ret.append(attrSqlName);
						multiSurfaceAttrs.addMultiSurfaceAttr(attr);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperMultiSurface("?",epsgCode));
						}else{
							values.append(","+geomConv.getInsertValueWrapperMultiSurface("?",epsgCode));
						}
						sep=",";
				}else if(TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTILINE_TRAFO))){
					 ret.append(sep);
					 ret.append(attrSqlName);
						multiLineAttrs.addMultiLineAttr(attr);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperMultiPolyline("?",epsgCode));
						}else{
							values.append(","+geomConv.getInsertValueWrapperMultiPolyline("?",epsgCode));
						}
						sep=",";
				}else if(TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTIPOINT_TRAFO))){
					 ret.append(sep);
					 ret.append(attrSqlName);
						multiPointAttrs.addMultiPointAttr(attr);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperMultiCoord("?",epsgCode));
						}else{
							values.append(","+geomConv.getInsertValueWrapperMultiCoord("?",epsgCode));
						}
						sep=",";
				}else if(TrafoConfigNames.ARRAY_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.ARRAY_TRAFO))){
					 ret.append(sep);
					 ret.append(attrSqlName);
						arrayAttrs.addArrayAttr(attr);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperArray("?"));
						}else{
							values.append(","+geomConv.getInsertValueWrapperArray("?"));
						}
						sep=",";
                }else if(TrafoConfigNames.JSON_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.JSON_TRAFO))){
                    ret.append(sep);
                    ret.append(attrSqlName);
                       if(isUpdate){
                           ret.append("="+geomConv.getInsertValueWrapperJson("?"));
                       }else{
                           values.append(","+geomConv.getInsertValueWrapperJson("?"));
                       }
                       sep=",";
				}else if(TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTILINGUAL_TRAFO))){
					for(String sfx:DbNames.MULTILINGUAL_TXT_COL_SUFFIXS){
						ret.append(sep);
						ret.append(attrSqlName+sfx);
						if(isUpdate){
							ret.append("=?");
						}else{
							values.append(",?");
						}
						sep=",";
					}
				}
			}else if (type instanceof PolylineType){
				 ret.append(sep);
				 ret.append(attrSqlName);
					if(isUpdate){
						ret.append("="+geomConv.getInsertValueWrapperPolyline("?",epsgCode));
					}else{
						values.append(","+geomConv.getInsertValueWrapperPolyline("?",epsgCode));
					}
					sep=",";
			 }else if(type instanceof SurfaceOrAreaType){
				 if(createItfLineTables){
				 }else{
					 ret.append(sep);
					 ret.append(attrSqlName);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperSurface("?",epsgCode));
						}else{
							values.append(","+geomConv.getInsertValueWrapperSurface("?",epsgCode));
						}
						sep=",";
				 }
				 if(createItfAreaRef){
					 if(type instanceof AreaType){
						 ret.append(sep);
						 ret.append(attrSqlName+DbNames.ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX);
							if(isUpdate){
								ret.append("="+geomConv.getInsertValueWrapperCoord("?",epsgCode));
							}else{
								 values.append(","+geomConv.getInsertValueWrapperCoord("?",epsgCode));
							}
							sep=",";
					 }
				 }
			 }else if(type instanceof CoordType){
				 ret.append(sep);
				 ret.append(attrSqlName);
					if(isUpdate){
						ret.append("="+geomConv.getInsertValueWrapperCoord("?",epsgCode));
					}else{
						values.append(","+geomConv.getInsertValueWrapperCoord("?",epsgCode));
					}
					sep=",";
			}else if(type instanceof EnumerationType){
				ret.append(sep);
				ret.append(attrSqlName);
				if(isUpdate){
					ret.append("=?");
				}else{
					values.append(",?");
				}
				sep=",";
				if(createEnumTxtCol){
					ret.append(sep);
					ret.append(attrSqlName+DbNames.ENUM_TXT_COL_SUFFIX);
					if(isUpdate){
						ret.append("=?");
					}else{
						values.append(",?");
					}
					sep=",";
				}
			}else if(type instanceof ReferenceType){
				ArrayList<ViewableWrapper> targetTables = getTargetTables(((ReferenceType)type).getReferred());
				for(ViewableWrapper targetTable:targetTables)
				{
					attrSqlName=ili2sqlName.mapIliAttributeDef(attr,sqlTableName,targetTable.getSqlTablename(),targetTables.size()>1);
					ret.append(sep);
					ret.append(attrSqlName);
					if(isUpdate){
						ret.append("=?");
					}else{
						values.append(",?");
					}
					sep=",";
				}
			}else{
				ret.append(sep);
				ret.append(attrSqlName);
				if(isUpdate){
					ret.append("=?");
				}else{
					values.append(",?");
				}
				sep=",";
			}
		   }
		return sep;
	}
	public int addAttrValue(IomObject iomObj, String sqlType, long sqlId,
			String sqlTableName,PreparedStatement ps, int valuei, AttributeDef tableAttr,AttributeDef classAttr,Integer epsgCode,ArrayList structQueue,Map<String,String> genericDomains,Viewable originalClass)
			throws SQLException, ConverterException {
		if(true) { // attr.getExtending()==null){
			 String attrName=tableAttr.getName();
			if( tableAttr.isDomainBoolean()) {
					String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
					if(value!=null){
						if(value.equals("true")){
							geomConv.setBoolean(ps,valuei,true);
						}else{
							geomConv.setBoolean(ps,valuei,false);
						}
					}else{
						ps.setNull(valuei,Types.BIT);
					}
					valuei++;
			}else if(tableAttr.isDomainIliUuid()){
				String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
				if(value==null){
					 geomConv.setUuidNull(ps, valuei);
				}else{
					 Object toInsertUUID = geomConv.fromIomUuid(value);
					 ps.setObject(valuei, toInsertUUID);
				}
				valuei++;
			}else if( tableAttr.isDomainIli1Date()) {
				String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
				if(value!=null){
					GregorianCalendar gdate=new GregorianCalendar(Integer.parseInt(value.substring(0,4)),Integer.parseInt(value.substring(4,6))-1,Integer.parseInt(value.substring(6,8)));
					java.sql.Date date=new java.sql.Date(gdate.getTimeInMillis());
					geomConv.setDate(ps,valuei,date);
				}else{
					ps.setNull(valuei,Types.DATE);
				}
				valuei++;
			}else if( tableAttr.isDomainIli2Date()) {
				String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Date date=new java.sql.Date(xmldate.toGregorianCalendar().getTimeInMillis());
					geomConv.setDate(ps,valuei,date);
				}else{
					ps.setNull(valuei,Types.DATE);
				}
				valuei++;
			}else if( tableAttr.isDomainIli2Time()) {
				String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Time time=new java.sql.Time(xmldate.toGregorianCalendar().getTimeInMillis());
					geomConv.setTime(ps,valuei,time);
				}else{
					ps.setNull(valuei,Types.TIME);
				}
				valuei++;
			}else if( tableAttr.isDomainIli2DateTime()) {
				String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Timestamp datetime=new java.sql.Timestamp(xmldate.toGregorianCalendar().getTimeInMillis());
					geomConv.setTimestamp(ps,valuei,datetime);
				}else{
					ps.setNull(valuei,Types.TIMESTAMP);
				}
				valuei++;
			}else{
				Type type = tableAttr.getDomainResolvingAliases();
			 
				if (type instanceof CompositionType){
					 int structc= classAttr==null ? 0 : iomObj.getattrvaluecount(attrName);
					if(TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.CATALOGUE_REF_TRAFO))){
                        IomObject catref= classAttr==null ? null : iomObj.getattrobj(attrName,0);
                        String refoid=null;
                        if(catref!=null){
                            IomObject structvalue=catref.getattrobj(IliNames.CHBASE1_CATALOGUEREFERENCE_REFERENCE,0);
                            if(structvalue!=null){
                                refoid=structvalue.getobjectrefoid();
                            }
                        }
	                    OutParam<Integer> valueiRef=new OutParam<Integer>(valuei);
	                    setReferenceColumn(ps,getCatalogueRefTarget(type),refoid,valueiRef);
	                    valuei=valueiRef.value;
					}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.MULTISURFACE_TRAFO))){
						 IomObject iomValue= classAttr==null ? null : iomObj.getattrobj(attrName,0);
						 IomObject iomMultisurface=null;
						 MultiSurfaceMapping attrMapping=null;
						 if(iomValue!=null){
							 attrMapping=multiSurfaceAttrs.getMapping(tableAttr);
							 int surfacec=iomValue.getattrvaluecount(attrMapping.getBagOfSurfacesAttrName());
							 for(int surfacei=0;surfacei<surfacec;surfacei++){
								 IomObject iomSurfaceStructure=iomValue.getattrobj(attrMapping.getBagOfSurfacesAttrName(), surfacei);
								 IomObject iomPoly=iomSurfaceStructure.getattrobj(attrMapping.getSurfaceAttrName(), 0);
								 if(iomPoly!=null) {
	                                 IomObject iomSurface=iomPoly.getattrobj("surface", 0);
	                                 if(iomMultisurface==null){
	                                     iomMultisurface=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
	                                 }
	                                 iomMultisurface.addattrobj("surface", iomSurface);
								 }
							 }
						 }
						 if(iomMultisurface!=null){
								AttributeDef surfaceAttr = getMultiSurfaceAttrDef(type, attrMapping);
								SurfaceType surface=((SurfaceType) surfaceAttr.getDomainResolvingAliases());
								CoordType coord=(CoordType)surface.getControlPointDomain().getType();
							 boolean is3D=coord.getDimensions().length==3;
							 Object geomObj = geomConv.fromIomMultiSurface(iomMultisurface,epsgCode,surface.getLineAttributeStructure()!=null,is3D,getP(surface));
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setSurfaceNull(ps,valuei);
						 }
						 valuei++;
					}else if(TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.MULTILINE_TRAFO))){
						 IomObject iomValue= classAttr==null ? null : iomObj.getattrobj(attrName,0);
						 IomObject iomMultiline=null;
						 MultiLineMapping attrMapping=null;
						 if(iomValue!=null){
							 attrMapping=multiLineAttrs.getMapping(tableAttr);
							 int polylinec=iomValue.getattrvaluecount(attrMapping.getBagOfLinesAttrName());
							 for(int polylinei=0;polylinei<polylinec;polylinei++){
								 IomObject iomPolylineStructure=iomValue.getattrobj(attrMapping.getBagOfLinesAttrName(), polylinei);
								 IomObject iomPoly=iomPolylineStructure.getattrobj(attrMapping.getLineAttrName(), 0);
								 if(iomMultiline==null){
									 iomMultiline=new ch.interlis.iom_j.Iom_jObject(Wkb2iox.OBJ_MULTIPOLYLINE,null);
								 }
								 iomMultiline.addattrobj(Wkb2iox.ATTR_POLYLINE, iomPoly);
							 }
						 }
						 if(iomMultiline!=null){
								AttributeDef polylineAttr = getMultiLineAttrDef(type, attrMapping);
								PolylineType line=((PolylineType) polylineAttr.getDomainResolvingAliases());
								CoordType coord=(CoordType)line.getControlPointDomain().getType();
							 boolean is3D=coord.getDimensions().length==3;
							 Object geomObj = geomConv.fromIomMultiPolyline(iomMultiline,epsgCode,is3D,getP(line));
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setPolylineNull(ps,valuei);
						 }
						 valuei++;
					}else if(TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.MULTIPOINT_TRAFO))){
						 IomObject iomValue= classAttr==null ? null : iomObj.getattrobj(attrName,0);
						 IomObject iomMultipoint=null;
						 MultiPointMapping attrMapping=null;
						 if(iomValue!=null){
							 attrMapping=multiPointAttrs.getMapping(tableAttr);
							 int pointc=iomValue.getattrvaluecount(attrMapping.getBagOfPointsAttrName());
							 for(int pointi=0;pointi<pointc;pointi++){
								 IomObject iomPointStructure=iomValue.getattrobj(attrMapping.getBagOfPointsAttrName(), pointi);
								 IomObject iomPoint=iomPointStructure.getattrobj(attrMapping.getPointAttrName(), 0);
								 if(iomMultipoint==null){
									 iomMultipoint=new ch.interlis.iom_j.Iom_jObject(Wkb2iox.OBJ_MULTIPOINT,null);
								 }
								 iomMultipoint.addattrobj(Wkb2iox.ATTR_COORD, iomPoint);
							 }
						 }
						 if(iomMultipoint!=null){
								AttributeDef coordAttr = getMultiPointAttrDef(type, attrMapping);
								CoordType coord=((CoordType) coordAttr.getDomainResolvingAliases());
							 boolean is3D=coord.getDimensions().length==3;
							 Object geomObj = geomConv.fromIomMultiCoord(iomMultipoint,epsgCode,is3D);
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setCoordNull(ps,valuei);
						 }
						 valuei++;
					}else if(TrafoConfigNames.ARRAY_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.ARRAY_TRAFO))){
						 int valuec= classAttr==null ? 0 : iomObj.getattrvaluecount(attrName);
						 String iomArray[]=new String[valuec];
						 ArrayMapping attrMapping=arrayAttrs.getMapping(tableAttr);
						 for(int elei=0;elei<valuec;elei++) {
							 IomObject iomValue=iomObj.getattrobj(attrName,elei);
							 String value=iomValue.getattrvalue(attrMapping.getValueAttr().getName());
							 iomArray[elei]=value;
						 }
						 if(iomArray.length>0){
							 Object geomObj = geomConv.fromIomArray(attrMapping.getValueAttr(),iomArray,enumTypes);
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setArrayNull(ps,valuei);
						 }
						 valuei++;
                    }else if(TrafoConfigNames.JSON_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.JSON_TRAFO))){
                        int valuec= classAttr==null ? 0 : iomObj.getattrvaluecount(attrName);
                        IomObject iomValue[]=new IomObject[valuec];
                        if(iomValue.length>0){
                            for(int i=0;i<valuec;i++) {
                                iomValue[i]=iomObj.getattrobj(attrName, i);
                            }
                            Object geomObj = geomConv.fromIomStructureToJson(tableAttr,iomValue);
                           ps.setObject(valuei,geomObj);
                        }else{
                           geomConv.setJsonNull(ps,valuei);
                        }
                        valuei++;
					}else if(TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(tableAttr, TrafoConfigNames.MULTILINGUAL_TRAFO))){
						 IomObject iomMulti= classAttr==null ? null : iomObj.getattrobj(attrName,0);
						for(String sfx:DbNames.MULTILINGUAL_TXT_COL_SUFFIXS){
							 if(iomMulti!=null){
								 	String value=getMultilingualText(iomMulti,sfx);
									if(value!=null){
										ps.setString(valuei, value);
									}else{
										ps.setNull(valuei,Types.VARCHAR);
									}
							 }else{
									ps.setNull(valuei,Types.VARCHAR);
							 }
							valuei++;
						}
					}else{
						 // enqueue struct values
						 for(int structi=0;structi<structc;structi++){
						 	IomObject struct=iomObj.getattrobj(attrName,structi);
						 	String sqlAttrName=ili2sqlName.mapIliAttributeDef(tableAttr,null,sqlTableName,null);
						 	enqueStructValue(structQueue,sqlId,sqlType,sqlAttrName,struct,structi,tableAttr);
						 }
					}
				}else if (type instanceof PolylineType){
					 IomObject value= classAttr==null ? null : iomObj.getattrobj(attrName,0);
					 if(value!=null){
						boolean is3D=((CoordType)((PolylineType)type).getControlPointDomain().getType()).getDimensions().length==3;
						ps.setObject(valuei,geomConv.fromIomPolyline(value,epsgCode,is3D,getP((PolylineType)type)));
					 }else{
						geomConv.setPolylineNull(ps,valuei);
					 }
					 valuei++;
				 }else if(type instanceof SurfaceOrAreaType){
					 if(createItfLineTables){
					 }else{
						 IomObject value= classAttr==null ? null : iomObj.getattrobj(attrName,0);
						 if(value!=null){
								boolean is3D=((CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType()).getDimensions().length==3;
							 Object geomObj = geomConv.fromIomSurface(value,epsgCode,((SurfaceOrAreaType)type).getLineAttributeStructure()!=null,is3D,getP((SurfaceOrAreaType)type));
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setSurfaceNull(ps,valuei);
						 }
						 valuei++;
					 }
					 if(createItfAreaRef){
						 if(type instanceof AreaType){
							 IomObject value=null;
							 if(isItfReader){
								 value= classAttr==null ? null : iomObj.getattrobj(attrName,0);
							 }else{
								 value= classAttr==null ? null : iomObj.getattrobj(ItfReader2.SAVED_GEOREF_PREFIX+attrName,0);
							 }
							 if(value!=null){
								boolean is3D=((CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType()).getDimensions().length==3;
								ps.setObject(valuei,geomConv.fromIomCoord(value,epsgCode,is3D));
							 }else{
								geomConv.setCoordNull(ps,valuei);
							 }
							 valuei++;
						 }
					 }
				 }else if(type instanceof CoordType){
					 IomObject value= classAttr==null ? null : iomObj.getattrobj(attrName,0);
					 if(value!=null){
					    int actualEpsgCode=TransferFromIli.getEpsgCode(originalClass,tableAttr, genericDomains, defaultEpsgCode);
					    if(actualEpsgCode==epsgCode) {
	                        boolean is3D=((CoordType)type).getDimensions().length==3;
	                        ps.setObject(valuei,geomConv.fromIomCoord(value,epsgCode,is3D));
					    }else {
	                        geomConv.setCoordNull(ps,valuei);
					    }
					 }else{
						geomConv.setCoordNull(ps,valuei);
					 }
					 valuei++;
				}else if(type instanceof NumericType){
					String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
					if(type.isAbstract()){
					}else{
						PrecisionDecimal min=((NumericType)type).getMinimum();
						PrecisionDecimal max=((NumericType)type).getMaximum();
						if(min.getAccuracy()>0){
							if(value!=null){
								try{
									ps.setDouble(valuei, Double.parseDouble(value));
								}catch(java.lang.NumberFormatException ex){
									EhiLogger.logError(ex);
								}
							}else{
								geomConv.setDecimalNull(ps,valuei);
							}
						}else{
							if(value!=null){
								try{
									int val=(int)Math.round(Double.parseDouble(value));
									ps.setInt(valuei, val);
								}catch(java.lang.NumberFormatException ex){
									EhiLogger.logError(ex);
								}
							}else{
								ps.setNull(valuei,Types.INTEGER);
							}
						}
						valuei++;
					}
				}else if(type instanceof EnumerationType){
					String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
					if(createEnumColAsItfCode){
						if(value!=null){
							int itfCode=mapXtfCode2ItfCode((EnumerationType)type, value);
							ps.setInt(valuei, itfCode);
						}else{
							ps.setNull(valuei,Types.INTEGER);
						}
					}else{
		                if(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID.equals(createEnumTable)) {
	                        if(value!=null){
	                            ps.setLong(valuei, mapEnumValue(classAttr,value));
	                        }else{
	                            ps.setNull(valuei,Types.BIGINT);
	                        }
		                }else {
	                        if(value!=null){
	                            ps.setString(valuei, value);
	                        }else{
	                            ps.setNull(valuei,Types.VARCHAR);
	                        }
		                }
					}
					valuei++;
					if(createEnumTxtCol){
						if(value!=null){
							ps.setString(valuei, beautifyEnumDispName(value));
						}else{
							ps.setNull(valuei,Types.VARCHAR);
						}
						valuei++;
					}
				}else if(type instanceof ReferenceType){
					 IomObject structvalue= classAttr==null ? null : iomObj.getattrobj(attrName,0);
					 String refoid=null;
					 if(structvalue!=null){
						 refoid=structvalue.getobjectrefoid();
					 }
					OutParam<Integer> valueiRef=new OutParam<Integer>(valuei);
					setReferenceColumn(ps,((ReferenceType) type).getReferred(),refoid,valueiRef);
					valuei=valueiRef.value;
				}else if(type instanceof BlackboxType){
					String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
					if(((BlackboxType)type).getKind()==BlackboxType.eXML){
						if(value==null){
							 geomConv.setXmlNull(ps, valuei);
						}else{
							 Object toInsertXml = geomConv.fromIomXml(value);
							 ps.setObject(valuei, toInsertXml);
						}
						valuei++;
					}else{
						if(value==null){
							 geomConv.setBlobNull(ps, valuei);
						}else{
							 Object toInsertBlob = geomConv.fromIomBlob(value);
							 ps.setObject(valuei, toInsertBlob);
						}
						valuei++;
					}
				}else{
					String value= classAttr==null ? null : iomObj.getattrvalue(attrName);
					if(value!=null){
						ps.setString(valuei, value);
					}else{
						ps.setNull(valuei,Types.VARCHAR);
					}
					valuei++;
				}
			}
		}
		return valuei;
	}
	private long mapEnumValue(AttributeDef attr, String xtfvalue) throws SQLException  {
	    EnumValueMap map=null;
	    if(enumCache.containsKey(attr)) {
	        map=enumCache.get(attr);
	    }else {
	        OutParam<String> qualifiedIliName=new OutParam<String>();
	        DbTableName sqlDbName=getEnumTargetTableName(attr, qualifiedIliName, dbSchema);
	        map=EnumValueMap.createEnumValueMap(conn, colT_ID, true, qualifiedIliName.value, sqlDbName);
            enumCache.put(attr,map);
	    }
        return map.mapXtfValue(xtfvalue);
    }
    protected AttributeDef getMultiPointAttrDef(Type type, MultiPointMapping attrMapping) {
		Table multiPointType = ((CompositionType) type).getComponentType();
		Table pointStructureType=((CompositionType) ((AttributeDef) multiPointType.getElement(AttributeDef.class, attrMapping.getBagOfPointsAttrName())).getDomain()).getComponentType();
		AttributeDef coordAttr = (AttributeDef) pointStructureType.getElement(AttributeDef.class,attrMapping.getPointAttrName());
		return coordAttr;
	}
	protected AttributeDef getMultiLineAttrDef(Type type, MultiLineMapping attrMapping) {
		Table multiLineType = ((CompositionType) type).getComponentType();
		Table lineStructureType=((CompositionType) ((AttributeDef) multiLineType.getElement(AttributeDef.class, attrMapping.getBagOfLinesAttrName())).getDomain()).getComponentType();
		AttributeDef polylineAttr = (AttributeDef) lineStructureType.getElement(AttributeDef.class,attrMapping.getLineAttrName());
		return polylineAttr;
	}
	protected AttributeDef getMultiSurfaceAttrDef(Type type, MultiSurfaceMapping attrMapping) {
		Table multiSurfaceType = ((CompositionType) type).getComponentType();
		Table surfaceStructureType=((CompositionType) ((AttributeDef) multiSurfaceType.getElement(AttributeDef.class, attrMapping.getBagOfSurfacesAttrName())).getDomain()).getComponentType();
		AttributeDef surfaceAttr = (AttributeDef) surfaceStructureType.getElement(AttributeDef.class,attrMapping.getSurfaceAttrName());
		return surfaceAttr;
	}
	final private int  LEN_LANG_PREFIX=DbNames.MULTILINGUAL_TXT_COL_PREFIX.length();
	private String getMultilingualText(IomObject iomMulti, String sfx) {
		if(sfx.length()>0){
			// remove leading '_'
			sfx=sfx.substring(LEN_LANG_PREFIX);
		}
	 	int txtc=iomMulti.getattrvaluecount(IliNames.CHBASE1_LOCALISEDTEXT);
	 	for(int txti=0;txti<txtc;txti++){
			IomObject iomTxt=iomMulti.getattrobj(IliNames.CHBASE1_LOCALISEDTEXT,txti);
			String lang=iomTxt.getattrvalue(IliNames.CHBASE1_LOCALISEDTEXT_LANGUAGE);
			if(lang==null)lang="";
			if(lang.equals(sfx)){
				return iomTxt.getattrvalue(IliNames.CHBASE1_LOCALISEDTEXT_TEXT);
			}
	 	}
		return null;
	}
	private HashMap<AttributeDef,Integer> srsCache=new HashMap<AttributeDef,Integer>();
	public int _getSrsid(AttributeDef attr){ // FIXME sollte nicht notwendig sein
		Integer srsid=srsCache.get(attr);
		if(srsid!=null) {
			return srsid;
		}
		ch.interlis.ili2c.metamodel.Element attrOrDomainDef=attr;
		ch.interlis.ili2c.metamodel.Type attrType=attr.getDomain();
		if(attrType instanceof ch.interlis.ili2c.metamodel.TypeAlias) {
			attrOrDomainDef=((ch.interlis.ili2c.metamodel.TypeAlias)attrType).getAliasing();
			attrType=((Domain) attrOrDomainDef).getType();
		}
		CoordType coord=null;
		if(attrType instanceof CoordType) {
			coord=(CoordType)attrType;
		}else if(attrType instanceof LineType) {
			Domain coordDomain=((LineType)attrType).getControlPointDomain();
			if(coordDomain!=null){
				attrOrDomainDef=coordDomain;
				coord=(CoordType)coordDomain.getType();
			}
		}
		if(coord!=null) {
			String crs=coord.getCrs(attrOrDomainDef);
			if(crs!=null) {
				String crsv[]=crs.split(":");
				String crsAuthority = crsv[0];
				String crsCode = crsv[1];
				try{
					srsid=geomConv.getSrsid(crsAuthority,crsCode,conn);
					if(srsid!=null){
						srsCache.put(attr, srsid);
						return srsid;
					}
				}catch(UnsupportedOperationException ex){
					EhiLogger.logAdaption("no CRS support by converter; use default "+defaultSrsid);
				}catch(ConverterException ex){
					throw new IllegalArgumentException("failed to get srsid for "+crsAuthority+":"+crsCode+", "+ex.getLocalizedMessage());
				}
				
			}
		}
		srsid=defaultSrsid;
		srsCache.put(attr, srsid);
		return srsid;
	}
	private HashMap<LineType,Double> typeCache=new HashMap<LineType,Double>();
	public double getP(LineType type)
	{
		if(typeCache.containsKey(type)){
			return ((Double)typeCache.get(type)).doubleValue();
		}
		double p;
		CoordType coordType=(CoordType)type.getControlPointDomain().getType();
		NumericalType dimv[]=coordType.getDimensions();
		int accuracy=((NumericType)dimv[0]).getMaximum().getAccuracy();
		if(accuracy==0){
			p=0.5;
		}else{
			p=Math.pow(10.0,-accuracy);
			//EhiLogger.debug("accuracy "+accuracy+", p "+p);
		}
		typeCache.put(type,new Double(p));
		return p;
	}
	private void enqueStructValue(ArrayList structQueue,long parentSqlId,String parentSqlType,String parentSqlAttr,IomObject struct,int structi,AttributeDef attr)
	{
		structQueue.add(new StructWrapper(parentSqlId,parentSqlType,parentSqlAttr,struct,structi,attr));
	}

}
