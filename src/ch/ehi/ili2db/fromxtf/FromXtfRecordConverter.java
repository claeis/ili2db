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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.IliNames;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.gui.Config;
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
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
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

public class FromXtfRecordConverter extends AbstractRecordConverter {
	private SqlColumnConverter geomConv=null;
	private java.sql.Timestamp today=null;
	private int defaultSrsid=0;
	private Connection conn=null;
	private String dbusr=null;
	private boolean isItfReader;
	private XtfidPool oidPool=null;
	private HashMap tag2class=null;
	
	public FromXtfRecordConverter(TransferDescription td1, NameMapping ili2sqlName,HashMap tag2class1,
			Config config,
			DbIdGen idGen1,SqlColumnConverter geomConv1,Connection conn1,String dbusr1,boolean isItfReader1,
			XtfidPool oidPool1,TrafoConfig trafoConfig,	
			Viewable2TableMapping class2wrapper1) {
		super(td1, ili2sqlName, config, idGen1,trafoConfig,class2wrapper1);
		conn=conn1;
		tag2class=tag2class1;
		dbusr=dbusr1;
		oidPool=oidPool1;
		this.geomConv=geomConv1;
		isItfReader=isItfReader1;
		today=new java.sql.Timestamp(System.currentTimeMillis());
		try{
			defaultSrsid=geomConv.getSrsid(defaultCrsAuthority,defaultCrsCode,conn);
		}catch(UnsupportedOperationException ex){
			EhiLogger.logAdaption("no CRS support by converter; use -1 as default srsid");
			defaultSrsid=-1;
		}catch(ConverterException ex){
			throw new IllegalArgumentException("failed to get srsid for "+defaultCrsAuthority+":"+defaultCrsCode+", "+ex.getLocalizedMessage());
		}
		
	}
	public void writeRecord(long basketSqlId, IomObject iomObj,Viewable iomClass,
			StructWrapper structEle, ViewableWrapper aclass, String sqlType,
			long sqlId, boolean updateObj, PreparedStatement ps,ArrayList structQueue)
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
 
		HashSet attrs=getIomObjectAttrs(iomClass);
		Iterator iter = aclass.getAttrIterator();
		while (iter.hasNext()) {
			ViewableTransferElement obj = (ViewableTransferElement)iter.next();
			if (obj.obj instanceof AttributeDef) {
				AttributeDef attr = (AttributeDef) obj.obj;
				if(attrs.contains(attr)){
					if(!attr.isTransient()){
						Type proxyType=attr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							valuei = addAttrValue(iomObj, sqlType, sqlId, aclass.getSqlTablename(),ps,
									valuei, attr,structQueue);
						}
					}
				}
			}
			if(obj.obj instanceof RoleDef){
				RoleDef role = (RoleDef) obj.obj;
				if(role.getExtending()==null){
					if(attrs.contains(role)){
												
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
						Holder<Integer> valueiRef=new Holder<Integer>(valuei);
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
			AbstractClassDef destination, String refoid, Holder<Integer> valuei) throws SQLException {
	  	String targetRootClassName=Ili2cUtility.getRootViewable(destination).getScopedName(null);
	  	ViewableWrapper targetObjTable=null;
		ArrayList<ViewableWrapper> targetTables = getTargetTables(destination);
	  	if(refoid!=null){
		  	String targetObjClass=oidPool.getObjecttag(targetRootClassName,refoid);
		  	targetObjTable=getViewableWrapper(getSqlType((Viewable) tag2class.get(targetObjClass)).getName());
		  	while(!targetTables.contains(targetObjTable)){
		  		targetObjTable=targetObjTable.getExtending();
		  	}
		  	if(targetObjTable==null){
		  		throw new IllegalStateException("targetObjTable==null");
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
	public HashSet getIomObjectAttrs(Viewable aclass) {
		HashSet ret=new HashSet();
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
						AttributeDef base=(AttributeDef) attr.getExtending();
						while(base!=null){
							attr=base;
							base=(AttributeDef) attr.getExtending();
						}
						ret.add(attr);
					}
				}
		   }
		   if(obj.obj instanceof RoleDef){
			   RoleDef role = (RoleDef) obj.obj;
			   if(role.getExtending()==null){
					// a role of an embedded association?
					if(obj.embedded){
						AssociationDef roleOwner = (AssociationDef) role.getContainer();
						if(roleOwner.getDerivedFrom()==null){
							RoleDef base=(RoleDef) role.getExtending();
							while(base!=null){
								role=base;
								base=(RoleDef) role.getExtending();
							}
							ret.add(role);
						}
					 }else{
							RoleDef base=(RoleDef) role.getExtending();
							while(base!=null){
								role=base;
								base=(RoleDef) role.getExtending();
							}
							ret.add(role);
					 }
				}
			}
		}
		return ret;
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
		
		HashSet attrs=getIomObjectAttrs(iomClass);
		Iterator iter = aclass.getAttrIterator();
		while (iter.hasNext()) {
		   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
		   if (obj.obj instanceof AttributeDef) {
			   AttributeDef attr = (AttributeDef) obj.obj;
			   if(attrs.contains(attr)){
					if(!attr.isTransient()){
						Type proxyType=attr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							   sep = addAttrToInsertStmt(isUpdate,ret, values, sep, attr,sqlTableName.getName());
						}
					}
			   }
		   }
		   if(obj.obj instanceof RoleDef){
			   RoleDef role = (RoleDef) obj.obj;
			   if(role.getExtending()==null){
				   if(attrs.contains(role)){
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
		ViewableWrapper ret=class2wrapper.get(abstractClass);
		if(ret!=null){
			return ret;
		}
		ret=class2wrapper.get(concreteClass);
		concreteClass=(Viewable) concreteClass.getExtending();
		if(concreteClass!=null && concreteClass!=abstractClass){
			ViewableWrapper ret2=class2wrapper.get(concreteClass);
			if(ret2!=null){
				ret=ret2;
			}
			concreteClass=(Viewable) concreteClass.getExtending();
		}
		return ret;
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
			StringBuffer ret, StringBuffer values, String sep, AttributeDef attr,String sqlTableName) {
		if(attr.getExtending()==null){
			Type type = attr.getDomainResolvingAliases();
			String attrSqlName=ili2sqlName.mapIliAttributeDef(attr,sqlTableName,null);
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
					ret.append(sep);
					ret.append(attrSqlName);
					if(isUpdate){
						ret.append("=?");
					}else{
						values.append(",?");
					}
					sep=",";
				}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO))){
					 ret.append(sep);
					 ret.append(attrSqlName);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperMultiSurface("?",getSrsid(type)));
						}else{
							values.append(","+geomConv.getInsertValueWrapperMultiSurface("?",getSrsid(type)));
						}
						sep=",";
						multiSurfaceAttrs.addMultiSurfaceAttr(attr);
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
						ret.append("="+geomConv.getInsertValueWrapperPolyline("?",getSrsid(type)));
					}else{
						values.append(","+geomConv.getInsertValueWrapperPolyline("?",getSrsid(type)));
					}
					sep=",";
			 }else if(type instanceof SurfaceOrAreaType){
				 if(createItfLineTables){
				 }else{
					 ret.append(sep);
					 ret.append(attrSqlName);
						if(isUpdate){
							ret.append("="+geomConv.getInsertValueWrapperSurface("?",getSrsid(type)));
						}else{
							values.append(","+geomConv.getInsertValueWrapperSurface("?",getSrsid(type)));
						}
						sep=",";
				 }
				 if(createItfAreaRef){
					 if(type instanceof AreaType){
						 ret.append(sep);
						 ret.append(attrSqlName+DbNames.ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX);
							if(isUpdate){
								ret.append("="+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
							}else{
								 values.append(","+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
							}
							sep=",";
					 }
				 }
			 }else if(type instanceof CoordType){
				 ret.append(sep);
				 ret.append(attrSqlName);
					if(isUpdate){
						ret.append("="+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
					}else{
						values.append(","+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
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
			String sqlTableName,PreparedStatement ps, int valuei, AttributeDef attr,ArrayList structQueue)
			throws SQLException, ConverterException {
		if(attr.getExtending()==null){
			 String attrName=attr.getName();
			if( attr.isDomainBoolean()) {
					String value=iomObj.getattrvalue(attrName);
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
			}else if(attr.isDomainIliUuid()){
				String value=iomObj.getattrvalue(attrName);
				if(value==null){
					 geomConv.setUuidNull(ps, valuei);
				}else{
					 Object toInsertUUID = geomConv.fromIomUuid(value);
					 ps.setObject(valuei, toInsertUUID);
				}
				valuei++;
			}else if( attr.isDomainIli1Date()) {
				String value=iomObj.getattrvalue(attrName);
				if(value!=null){
					GregorianCalendar gdate=new GregorianCalendar(Integer.parseInt(value.substring(0,4)),Integer.parseInt(value.substring(4,6))-1,Integer.parseInt(value.substring(6,8)));
					java.sql.Date date=new java.sql.Date(gdate.getTimeInMillis());
					geomConv.setDate(ps,valuei,date);
				}else{
					ps.setNull(valuei,Types.DATE);
				}
				valuei++;
			}else if( attr.isDomainIli2Date()) {
				String value=iomObj.getattrvalue(attrName);
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
			}else if( attr.isDomainIli2Time()) {
				String value=iomObj.getattrvalue(attrName);
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
			}else if( attr.isDomainIli2DateTime()) {
				String value=iomObj.getattrvalue(attrName);
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
				Type type = attr.getDomainResolvingAliases();
			 
				if (type instanceof CompositionType){
					 int structc=iomObj.getattrvaluecount(attrName);
					if(TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.CATALOGUE_REF_TRAFO))){
						 IomObject catref=iomObj.getattrobj(attrName,0);
						 String refoid=null;
						 if(catref!=null){
							 IomObject structvalue=catref.getattrobj(IliNames.CHBASE1_CATALOGUEREFERENCE_REFERENCE,0);
							 if(structvalue!=null){
								 refoid=structvalue.getobjectrefoid();
							 }
						 }
						 if(refoid!=null){
							 	String targetClassName=IliNames.CHBASE1_CATALOGUEOBJECTS_CATALOGUES+"."+IliNames.CHBASE1_ITEM;
								long refsqlId=oidPool.getObjSqlId(targetClassName,refoid);
								ps.setLong(valuei, refsqlId);
						 }else{
								ps.setNull(valuei,Types.BIGINT);
						 }
						valuei++;
					}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO))){
						 IomObject iomValue=iomObj.getattrobj(attrName,0);
						 IomObject iomMultisurface=null;
						 MultiSurfaceMapping attrMapping=null;
						 if(iomValue!=null){
							 attrMapping=multiSurfaceAttrs.getMapping(attr);
							 int surfacec=iomValue.getattrvaluecount(attrMapping.getBagOfSurfacesAttrName());
							 for(int surfacei=0;surfacei<surfacec;surfacei++){
								 IomObject iomSurfaceStructure=iomValue.getattrobj(attrMapping.getBagOfSurfacesAttrName(), surfacei);
								 IomObject iomPoly=iomSurfaceStructure.getattrobj(attrMapping.getSurfaceAttrName(), 0);
								 IomObject iomSurface=iomPoly.getattrobj("surface", 0);
								 if(iomMultisurface==null){
									 iomMultisurface=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
								 }
								 iomMultisurface.addattrobj("surface", iomSurface);
							 }
						 }
						 if(iomMultisurface!=null){
								Table multiSurfaceType = ((CompositionType) type).getComponentType();
								Table surfaceStructureType=((CompositionType) ((AttributeDef) multiSurfaceType.getElement(AttributeDef.class, attrMapping.getBagOfSurfacesAttrName())).getDomain()).getComponentType();
								SurfaceType surface=((SurfaceType) ((AttributeDef) surfaceStructureType.getElement(AttributeDef.class,attrMapping.getSurfaceAttrName())).getDomainResolvingAliases());
								CoordType coord=(CoordType)surface.getControlPointDomain().getType();
							 boolean is3D=coord.getDimensions().length==3;
							 Object geomObj = geomConv.fromIomMultiSurface(iomMultisurface,getSrsid(type),surface.getLineAttributeStructure()!=null,is3D,getP(surface));
							ps.setObject(valuei,geomObj);
						 }else{
							geomConv.setSurfaceNull(ps,valuei);
						 }
						 valuei++;
					}else if(TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTILINGUAL_TRAFO))){
						 IomObject iomMulti=iomObj.getattrobj(attrName,0);
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
						 	String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr,sqlTableName,null);
						 	enqueStructValue(structQueue,sqlId,sqlType,sqlAttrName,struct,structi,attr);
						 }
					}
				}else if (type instanceof PolylineType){
					 IomObject value=iomObj.getattrobj(attrName,0);
					 if(value!=null){
						boolean is3D=((CoordType)((PolylineType)type).getControlPointDomain().getType()).getDimensions().length==3;
						ps.setObject(valuei,geomConv.fromIomPolyline(value,getSrsid(type),is3D,getP((PolylineType)type)));
					 }else{
						geomConv.setPolylineNull(ps,valuei);
					 }
					 valuei++;
				 }else if(type instanceof SurfaceOrAreaType){
					 if(createItfLineTables){
					 }else{
						 IomObject value=iomObj.getattrobj(attrName,0);
						 if(value!=null){
								boolean is3D=((CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType()).getDimensions().length==3;
							 Object geomObj = geomConv.fromIomSurface(value,getSrsid(type),((SurfaceOrAreaType)type).getLineAttributeStructure()!=null,is3D,getP((SurfaceOrAreaType)type));
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
								 value=iomObj.getattrobj(attrName,0);
							 }else{
								 value=iomObj.getattrobj(ItfReader2.SAVED_GEOREF_PREFIX+attrName,0);
							 }
							 if(value!=null){
								boolean is3D=((CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType()).getDimensions().length==3;
								ps.setObject(valuei,geomConv.fromIomCoord(value,getSrsid(type),is3D));
							 }else{
								geomConv.setCoordNull(ps,valuei);
							 }
							 valuei++;
						 }
					 }
				 }else if(type instanceof CoordType){
					 IomObject value=iomObj.getattrobj(attrName,0);
					 if(value!=null){
						boolean is3D=((CoordType)type).getDimensions().length==3;
						ps.setObject(valuei,geomConv.fromIomCoord(value,getSrsid(type),is3D));
					 }else{
						geomConv.setCoordNull(ps,valuei);
					 }
					 valuei++;
				}else if(type instanceof NumericType){
					String value=iomObj.getattrvalue(attrName);
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
					String value=iomObj.getattrvalue(attrName);
					if(createEnumColAsItfCode){
						if(value!=null){
							int itfCode=mapXtfCode2ItfCode((EnumerationType)type, value);
							ps.setInt(valuei, itfCode);
						}else{
							ps.setNull(valuei,Types.INTEGER);
						}
					}else{
						if(value!=null){
							ps.setString(valuei, value);
						}else{
							ps.setNull(valuei,Types.VARCHAR);
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
					 IomObject structvalue=iomObj.getattrobj(attrName,0);
					 String refoid=null;
					 if(structvalue!=null){
						 refoid=structvalue.getobjectrefoid();
					 }
					Holder<Integer> valueiRef=new Holder<Integer>(valuei);
					setReferenceColumn(ps,((ReferenceType) type).getReferred(),refoid,valueiRef);
					valuei=valueiRef.value;
				}else{
					String value=iomObj.getattrvalue(attrName);
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
	public int getSrsid(Type type){
		return defaultSrsid;
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
