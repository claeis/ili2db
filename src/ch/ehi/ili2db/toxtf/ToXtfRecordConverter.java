package ch.ehi.ili2db.toxtf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.IliNames;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.TrafoConfigNames;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.interlis.ili2c.metamodel.AreaType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.itf.ItfWriter2;

public class ToXtfRecordConverter extends AbstractRecordConverter {
	private boolean isMsAccess=false;
	private Connection conn=null;
	private SqlColumnConverter geomConv=null;
	private SqlidPool sqlid2xtfid=null;
	public ToXtfRecordConverter(TransferDescription td1, NameMapping ili2sqlName,
			Config config, DbIdGen idGen1,SqlColumnConverter geomConv1,Connection conn1,SqlidPool sqlidPool,TrafoConfig trafoConfig,Viewable2TableMapping class2wrapper1) {
		super(td1, ili2sqlName, config, idGen1,trafoConfig,class2wrapper1);
		conn=conn1;
		geomConv=geomConv1;
		sqlid2xtfid=sqlidPool;
		try {
			if(conn.getMetaData().getURL().startsWith("jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)}")){
				isMsAccess=true;
			}
		} catch (SQLException e) {
			EhiLogger.logError(e);
		}
	}
	/** creates sql query statement for a class.
	 * @param aclass type of objects to build query for
	 * @param wrapper not null, if building query for struct values
	 * @return SQL-Query statement
	 */
	public String createQueryStmt(Viewable aclass1,Integer basketSqlId,StructWrapper structWrapper){
		ViewableWrapper aclass=class2wrapper.get(aclass1);
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		if(createTypeDiscriminator || aclass.includesMultipleTypes()){
			ret.append(", r0."+DbNames.T_TYPE_COL);
		}
		if(!aclass.isStructure()){
			if(createIliTidCol || aclass.getOid()!=null){
				ret.append(", r0."+DbNames.T_ILI_TID_COL);
			}
		}
		if(structWrapper!=null){
			if(createGenericStructRef){
				ret.append(", r0."+DbNames.T_PARENT_ID_COL);
				ret.append(", r0."+DbNames.T_PARENT_TYPE_COL);
				ret.append(", r0."+DbNames.T_PARENT_ATTR_COL);
			}else{
				ret.append(", r0."+ili2sqlName.mapIliAttributeDefReverse(structWrapper.getParentAttr(),getSqlType(aclass.getViewable()).getName(),getSqlType(structWrapper.getParentTable().getViewable()).getName()));
			}
			ret.append(", r0."+DbNames.T_SEQ_COL);
		}
		String sep=",";
		for(ViewableWrapper table:aclass.getWrappers()){
			String sqlTableName=getSqlType(table.getViewable()).getName();
			Iterator iter = table.getAttrIterator();
			while (iter.hasNext()) {
			   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
			   if (obj.obj instanceof AttributeDef) {
				   AttributeDef attr = (AttributeDef) obj.obj;
				   AttributeDef baseAttr=attr;
				   while(true){
					   AttributeDef baseAttr1=(AttributeDef)baseAttr.getExtending();
					   if(baseAttr1==null){
						   break;
					   }
					   baseAttr=baseAttr1;
				   }
					if(!baseAttr.isTransient()){
						Type proxyType=baseAttr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							 sep = addAttrToQueryStmt(ret, sep, baseAttr,sqlTableName);
						}
					}
			   }
			   if(obj.obj instanceof RoleDef){
				   RoleDef role = (RoleDef) obj.obj;
				   if(role.getExtending()==null){
					String roleSqlName=ili2sqlName.mapIliRoleDef(role,sqlTableName,getSqlType(role.getDestination()).getName());
					// a role of an embedded association?
					if(obj.embedded){
						AssociationDef roleOwner = (AssociationDef) role.getContainer();
						if(roleOwner.getDerivedFrom()==null){
							 // TODO if(orderPos!=0){
							 ret.append(sep);
							 sep=",";
							 ret.append(roleSqlName);
						}
					 }else{
						 // TODO if(orderPos!=0){
						 ret.append(sep);
						 sep=",";
						 ret.append(roleSqlName);
					 }
				   }
				}
			}
		}
		// stdcols
		if(createStdCols){
			ret.append(sep);
			sep=",";
			ret.append("r0."+DbNames.T_LAST_CHANGE_COL);
			ret.append(sep);
			sep=",";
			ret.append("r0."+DbNames.T_CREATE_DATE_COL);
			ret.append(sep);
			sep=",";
			ret.append("r0."+DbNames.T_USER_COL);
		}

		ret.append(" FROM ");
		ArrayList<ViewableWrapper> tablev=new ArrayList<ViewableWrapper>(10);
		tablev.add(aclass);
		ViewableWrapper base=aclass.getExtending();
		while(base!=null){
			tablev.add(base);		
			base=base.getExtending();
		}
		sep="";
		int tablec=tablev.size();
		if(isMsAccess){
			for(int i=0;i<tablec;i++){
				ret.append("(");
			}
		}
		for(int i=0;i<tablec;i++){
			ret.append(sep);
			ret.append(getSqlType(tablev.get(i).getViewable()));
			ret.append(" r"+Integer.toString(tablec-1-i));
			if(i>0){
				ret.append(" ON r"+Integer.toString(tablec-i)+"."+colT_ID+"=r"+Integer.toString(tablec-1-i)+"."+colT_ID);
			}
			if(isMsAccess){
				ret.append(")");
			}
			sep=" INNER JOIN ";
		}
		sep=" WHERE";
		if(createTypeDiscriminator || aclass.includesMultipleTypes()){
			ret.append(sep+" r0."+DbNames.T_TYPE_COL+"='"+getSqlType(aclass1).getName()+"'");
			sep=" AND";
		}
		if(structWrapper!=null){
			if(createGenericStructRef){
				ret.append(sep+" r0."+DbNames.T_PARENT_ID_COL+"=? AND r0."+DbNames.T_PARENT_ATTR_COL+"=?");
			}else{
				ret.append(sep+" r0."+ili2sqlName.mapIliAttributeDefReverse(structWrapper.getParentAttr(),getSqlType(aclass.getViewable()).getName(),getSqlType(structWrapper.getParentTable().getViewable()).getName())+"=?");
			}
			sep=" AND";
		}
		if(basketSqlId!=null){
			ret.append(sep+" r0."+DbNames.T_BASKET_COL+"=?");
		}
		if(structWrapper!=null){
			ret.append(" ORDER BY r0."+DbNames.T_SEQ_COL+" ASC");
		}
		return ret.toString();
	}
	public String addAttrToQueryStmt(StringBuffer ret, String sep, AttributeDef attr,String sqlTableName) {
		if(attr.getExtending()==null){
			Type type = attr.getDomainResolvingAliases();
			 String attrSqlName=ili2sqlName.mapIliAttributeDef(attr,sqlTableName,null);
			if( Ili2cUtility.isIli1Date(td,attr)) {
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperDate(attrSqlName));
			}else if( Ili2cUtility.isIli2Date(td,attr)) {
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperDate(attrSqlName));
			}else if( Ili2cUtility.isIli2Time(td,attr)) {
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperTime(attrSqlName));
			}else if( Ili2cUtility.isIli2DateTime(td,attr)) {
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperDateTime(attrSqlName));
			}else if (type instanceof CompositionType){
				if(TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.CATALOGUE_REF_TRAFO))){
					 ret.append(sep);
					 sep=",";
					 ret.append(attrSqlName);
				}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO))){
					 ret.append(sep);
					 sep=",";
					 ret.append(geomConv.getSelectValueWrapperMultiSurface(attrSqlName));
				}else if(TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTILINGUAL_TRAFO))){
					for(String sfx:DbNames.MULTILINGUAL_TXT_COL_SUFFIXS){
						 ret.append(sep);
						 sep=",";
						 ret.append(attrSqlName+sfx);
					}
				}
			}else if (type instanceof PolylineType){
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperPolyline(attrSqlName));
			 }else if(type instanceof SurfaceOrAreaType){
				 if(createItfLineTables){
				 }else{
					 ret.append(sep);
					 sep=",";
					 ret.append(geomConv.getSelectValueWrapperSurface(attrSqlName));
				 }
				 if(createItfAreaRef){
					 if(type instanceof AreaType){
						 ret.append(sep);
						 sep=",";
						 ret.append(geomConv.getSelectValueWrapperCoord(attrSqlName+DbNames.ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX));
					 }
				 }
			 }else if(type instanceof CoordType){
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperCoord(attrSqlName));
			}else{
				 ret.append(sep);
				 sep=",";
				 ret.append(attrSqlName);
			}
		   }
		return sep;
	}
	public void setStmtParams(java.sql.PreparedStatement dbstmt,
			Integer basketSqlId, FixIomObjectRefs fixref,
			StructWrapper structWrapper) throws SQLException {
		dbstmt.clearParameters();
		int paramIdx=1;
		if(structWrapper!=null){
			dbstmt.setInt(paramIdx++,structWrapper.getParentSqlId());
			if(createGenericStructRef){
				dbstmt.setString(paramIdx++,ili2sqlName.mapIliAttributeDef(structWrapper.getParentAttr(),getSqlType(structWrapper.getParentTable().getViewable()).getName(),null));
			}
		}else{
			if(fixref!=null){
				throw new IllegalArgumentException("fixref!=null");
			}
		}
		if(basketSqlId!=null){
			dbstmt.setInt(paramIdx++,basketSqlId);
		}
	}
	public int getT_ID(java.sql.ResultSet rs) throws SQLException {
		int sqlid=rs.getInt(1);
		return sqlid;
	}
	public Iom_jObject convertRecord(java.sql.ResultSet rs, Viewable aclass1,
			FixIomObjectRefs fixref, StructWrapper structWrapper,
			HashMap structelev, ArrayList<StructWrapper> structQueue, int sqlid)
			throws SQLException {
		ViewableWrapper aclass=class2wrapper.get(aclass1);
		Iom_jObject iomObj;
		int valuei=1;
		valuei++;
		if(createTypeDiscriminator || aclass.includesMultipleTypes()){
			//String t_type=rs.getString(valuei);
			valuei++;
		}
		String sqlIliTid=null;
		if(structWrapper==null){
			if(!aclass.isStructure()){
				if(createIliTidCol || aclass.getOid()!=null){
					sqlIliTid=rs.getString(valuei);
					sqlid2xtfid.putSqlid2Xtfid(sqlid, sqlIliTid);
					valuei++;
				}else{
					sqlIliTid=Integer.toString(sqlid);
					sqlid2xtfid.putSqlid2Xtfid(sqlid, sqlIliTid);
				}
			}
		}
		if(structWrapper==null){
			if(!aclass.isStructure()){
				iomObj=new Iom_jObject(aclass1.getScopedName(null),sqlIliTid);
			}else{
				iomObj=new Iom_jObject(aclass1.getScopedName(null),null);
			}
			iomObj.setattrvalue(ItfWriter2.INTERNAL_T_ID, Integer.toString(sqlid));
			fixref.setRoot(iomObj);
		}else{
			iomObj=(Iom_jObject)structelev.get(Integer.toString(sqlid));
			if(createGenericStructRef){
				valuei+=4;
			}else{
				valuei+=2;
			}
		}
		
		for(ViewableWrapper table:aclass.getWrappers()){
			Iterator iter = table.getAttrIterator();
			while (iter.hasNext()) {
			   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
			   if (obj.obj instanceof AttributeDef) {
				   AttributeDef attr = (AttributeDef) obj.obj;
				   AttributeDef baseAttr=attr;
				   while(true){
					   AttributeDef baseAttr1=(AttributeDef)baseAttr.getExtending();
					   if(baseAttr1==null){
						   break;
					   }
					   baseAttr=baseAttr1;
				   }
					if(!baseAttr.isTransient()){
						Type proxyType=baseAttr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							   valuei = addAttrValue(rs, valuei, sqlid, iomObj, baseAttr,structQueue,table,fixref);
						}
					}
			   }
			   if(obj.obj instanceof RoleDef){
				   RoleDef role = (RoleDef) obj.obj;
				   if(role.getExtending()==null){
					 String roleName=role.getName();
					 String sqlRoleName=ili2sqlName.mapIliRoleDef(role,getSqlType(table.getViewable()).getName(),getSqlType(role.getDestination()).getName());
					 // a role of an embedded association?
					 if(obj.embedded){
						AssociationDef roleOwner = (AssociationDef) role.getContainer();
						if(roleOwner.getDerivedFrom()==null){
							 // TODO if(orderPos!=0){
							int value=rs.getInt(valuei);
							valuei++;
							if(!rs.wasNull()){
								IomObject ref=iomObj.addattrobj(roleName,roleOwner.getScopedName(null));
								mapSqlid2Xtfid(fixref,value,ref,role.getDestination());
							}
						}
					 }else{
						 // TODO if(orderPos!=0){
						IomObject ref=iomObj.addattrobj(roleName,"REF");
						mapSqlid2Xtfid(fixref,rs.getInt(valuei),ref,role.getDestination());
						valuei++;
					 }
				   }
				}
			}
			
		}

		return iomObj;
	}
	
	final private int  LEN_LANG_PREFIX=DbNames.MULTILINGUAL_TXT_COL_PREFIX.length();
	public int addAttrValue(java.sql.ResultSet rs, int valuei, int sqlid,
			Iom_jObject iomObj, AttributeDef attr,ArrayList<StructWrapper> structQueue,ViewableWrapper table,FixIomObjectRefs fixref) throws SQLException {
		if(attr.getExtending()==null){
			String attrName=attr.getName();
			String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr,getSqlType(table.getViewable()).getName(),null);
			if( Ili2cUtility.isBoolean(td,attr)) {
					boolean value=rs.getBoolean(valuei);
					valuei++;
					if(!rs.wasNull()){
						if(value){
							iomObj.setattrvalue(attrName,"true");
						}else{
							iomObj.setattrvalue(attrName,"false");
						}
					}
			}else if( Ili2cUtility.isIli1Date(td,attr)) {
				java.sql.Date value=rs.getDate(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyyMMdd");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( Ili2cUtility.isIli2Date(td,attr)) {
				java.sql.Date value=rs.getDate(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( Ili2cUtility.isIli2Time(td,attr)) {
				java.sql.Time value=rs.getTime(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("HH:mm:ss.SSS");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( Ili2cUtility.isIli2DateTime(td,attr)) {
				java.sql.Timestamp value=rs.getTimestamp(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // with timezone: yyyy-MM-dd'T'HH:mm:ss.SSSZ 
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else{
				Type type = attr.getDomainResolvingAliases();
				if (type instanceof CompositionType){
					if(TrafoConfigNames.CATALOGUE_REF_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.CATALOGUE_REF_TRAFO))){
						Table catalogueReferenceTyp = ((CompositionType) type).getComponentType();
						int value=rs.getInt(valuei);
						valuei++;
						if(!rs.wasNull()){
							IomObject catref=iomObj.addattrobj(attrName,catalogueReferenceTyp.getScopedName(null));
							IomObject ref=catref.addattrobj(IliNames.CHBASE1_CATALOGUEREFERENCE_REFERENCE,"REF");
							mapSqlid2Xtfid(fixref,value,ref,((ReferenceType) ((AttributeDef)catalogueReferenceTyp.getAttributes().next()).getDomain()).getReferred());
						}
					}else if(TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTISURFACE_TRAFO))){
						Table multiSurfaceType = ((CompositionType) type).getComponentType();
						Table surfaceStructureType=((CompositionType) ((AttributeDef) multiSurfaceType.getElement(AttributeDef.class, IliNames.CHBASE1_GEOMETRY_MULTISURFACE_SURFACES)).getDomain()).getComponentType();
						String multiSurfaceQname=multiSurfaceType.getScopedName(null);
						String surfaceStructureQname=surfaceStructureType.getScopedName(null);
						SurfaceType surface=((SurfaceType) ((AttributeDef) surfaceStructureType.getElement(AttributeDef.class,IliNames.CHBASE1_GEOMETRY_SURFACESTRUCTURE_SURFACE)).getDomainResolvingAliases());
						CoordType coord=(CoordType)surface.getControlPointDomain().getType();
						boolean is3D=coord.getDimensions().length==3;
						Object geomobj=rs.getObject(valuei);
						valuei++;
						if(!rs.wasNull()){
							try{
								IomObject iomMultiSurface=geomConv.toIomMultiSurface(geomobj,sqlAttrName,is3D);
								IomObject iomChbaseMultiSurface=new Iom_jObject(multiSurfaceQname,null); 
								int surfacec=iomMultiSurface.getattrvaluecount("surface");
								for(int surfacei=0;surfacei<surfacec;surfacei++){
									IomObject iomSurface=iomMultiSurface.getattrobj("surface",surfacei);
									IomObject iomChbaseSurfaceStructure=iomChbaseMultiSurface.addattrobj(IliNames.CHBASE1_GEOMETRY_MULTISURFACE_SURFACES, surfaceStructureQname);
									IomObject iomSurfaceClone=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
									iomSurfaceClone.addattrobj("surface",iomSurface);
									iomChbaseSurfaceStructure.addattrobj(IliNames.CHBASE1_GEOMETRY_SURFACESTRUCTURE_SURFACE, iomSurfaceClone);
								}
								iomObj.addattrobj(attrName,iomChbaseMultiSurface);
							}catch(ConverterException ex){
								EhiLogger.logError("Object "+sqlid+": failed to convert surface/area",ex);
							}	
						}
					}else if(TrafoConfigNames.MULTILINGUAL_TRAFO_EXPAND.equals(trafoConfig.getAttrConfig(attr, TrafoConfigNames.MULTILINGUAL_TRAFO))){
						IomObject iomMulti=null;
						Table multilingualTextType = ((CompositionType) type).getComponentType();
						String multilingualTextQname=multilingualTextType.getScopedName(null);
						String localizedTextQname=((CompositionType) ((AttributeDef) multilingualTextType.getAttributes().next()).getDomain()).getComponentType().getScopedName(null);
						for(String sfx:DbNames.MULTILINGUAL_TXT_COL_SUFFIXS){
							String value=rs.getString(valuei);
							valuei++;
							if(!rs.wasNull()){
								if(iomMulti==null){
									iomMulti=new Iom_jObject(multilingualTextQname, null);
								}
								IomObject iomTxt=iomMulti.addattrobj(IliNames.CHBASE1_LOCALISEDTEXT,localizedTextQname);
								
								iomTxt.setattrvalue(IliNames.CHBASE1_LOCALISEDTEXT_LANGUAGE,sfx.length()==0?null:sfx.substring(LEN_LANG_PREFIX));
								iomTxt.setattrvalue(IliNames.CHBASE1_LOCALISEDTEXT_TEXT,value);
							}
						}
						if(iomMulti!=null){
							iomObj.addattrobj(attrName, iomMulti);
						}
					}else{
						// enque iomObj as parent
						structQueue.add(new StructWrapper(sqlid,attr,iomObj,table));
					}
				}else if (type instanceof PolylineType){
					Object geomobj=rs.getObject(valuei);
					valuei++;
					if(!rs.wasNull()){
						try{
						boolean is3D=((CoordType)((PolylineType)type).getControlPointDomain().getType()).getDimensions().length==3;
						IomObject polyline=geomConv.toIomPolyline(geomobj,sqlAttrName,is3D);
						iomObj.addattrobj(attrName,polyline);
						}catch(ConverterException ex){
							EhiLogger.logError("Object "+sqlid+": failed to convert polyline",ex);
						}	
					}
				 }else if(type instanceof SurfaceOrAreaType){
					 if(createItfLineTables){
					 }else{
							Object geomobj=rs.getObject(valuei);
							valuei++;
							if(!rs.wasNull()){
								try{
									boolean is3D=((CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType()).getDimensions().length==3;
									IomObject surface=geomConv.toIomSurface(geomobj,sqlAttrName,is3D);
									iomObj.addattrobj(attrName,surface);
								}catch(ConverterException ex){
									EhiLogger.logError("Object "+sqlid+": failed to convert surface/area",ex);
								}	
							}
					 }
					 if(createItfAreaRef){
						 if(type instanceof AreaType){
								Object geomobj=rs.getObject(valuei);
								valuei++;
								if(!rs.wasNull()){
									try{
										boolean is3D=false;
										IomObject coord=geomConv.toIomCoord(geomobj,sqlAttrName,is3D);
										iomObj.addattrobj(attrName,coord);
									}catch(ConverterException ex){
										EhiLogger.logError("Object "+sqlid+": failed to convert coord",ex);
									}
								}
						 }
					 }
				 }else if(type instanceof CoordType){
					Object geomobj=rs.getObject(valuei);
					valuei++;
					if(!rs.wasNull()){
						try{
							boolean is3D=((CoordType)type).getDimensions().length==3;
							IomObject coord=geomConv.toIomCoord(geomobj,sqlAttrName,is3D);
							iomObj.addattrobj(attrName,coord);
						}catch(ConverterException ex){
							EhiLogger.logError("Object "+sqlid+": failed to convert coord",ex);
						}
					}
				}else if(type instanceof EnumerationType){
					if(createEnumColAsItfCode){
						int value=rs.getInt(valuei);
						valuei++;
						if(!rs.wasNull()){
							iomObj.setattrvalue(attrName,mapItfCode2XtfCode((EnumerationType)type, value));
						}
					}else{
						String value=rs.getString(valuei);
						valuei++;
						if(!rs.wasNull()){
							iomObj.setattrvalue(attrName,value);
						}
						
					}
				}else if(type instanceof ReferenceType){
					int value=rs.getInt(valuei);
					valuei++;
					if(!rs.wasNull()){
						IomObject ref=iomObj.addattrobj(attrName,"REF");
						mapSqlid2Xtfid(fixref,value,ref,((ReferenceType)type).getReferred());
					}
				}else{
					String value=rs.getString(valuei);
					valuei++;
					if(!rs.wasNull()){
						iomObj.setattrvalue(attrName,value);
					}
				}
			   }
			}
		return valuei;
	}
	private String mapSqlid2Xtfid(FixIomObjectRefs fixref, int sqlid,IomObject refobj,Viewable targetClass) {
		if(sqlid2xtfid.containsSqlid(sqlid)){
			refobj.setobjectrefoid(sqlid2xtfid.getXtfid(sqlid));
		}else{
			fixref.addFix(refobj,sqlid,targetClass);
		}
		return null;
	}

}
