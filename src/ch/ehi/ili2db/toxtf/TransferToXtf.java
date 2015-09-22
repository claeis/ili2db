/* This file is part of the ili2ora project.
 * For more information, please see <http://www.interlis.ch>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.ehi.ili2db.toxtf;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.converter.*;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.fromxtf.BasketStat;
import ch.ehi.ili2db.fromxtf.ClassStat;
import ch.ehi.ili2db.mapping.Mapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.*;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iox.IoxWriter;
import ch.interlis.iox_j.*;
import ch.interlis.iom_j.itf.EnumCodeMapper;
import ch.interlis.iom_j.itf.ItfWriter;
import ch.interlis.iom_j.itf.ItfWriter2;
import ch.interlis.iom_j.itf.ModelUtilities;
import ch.interlis.iom_j.xtf.XtfWriter;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 06.05.2005 $
 */
public class TransferToXtf {
	private Mapping ili2sqlName=null;
	private TransferDescription td=null;
	private Connection conn=null;
	private String schema=null; // name of db schema or null
	private boolean createStdCols=false;
	private String colT_ID=null;
	private boolean createTypeDiscriminator=false;
	private boolean createGenericStructRef=false;
	private boolean writeIliTid=false;
	private boolean doItfLineTables=false;
	private boolean createItfLineTables=false;
	private boolean createItfAreaRef=false;
	private boolean createEnumColAsItfCode=false;
	private EnumCodeMapper enumMapper=new EnumCodeMapper();
	private SqlGeometryConverter geomConv=null;
	private boolean isMsAccess=false;

	/** list of not yet processed struct attrs
	 */
	private ArrayList structQueue=null;
	/** map of xml-elementnames to interlis classdefs.
	 *  Used to map typenames read from the T_TYPE column to the classdef.
	 */
	private HashMap tag2class=null; // map<String tag, Viewable classDef>
	private ch.interlis.ili2c.generator.IndentPrintWriter expgen=null;
	public TransferToXtf(Mapping ili2sqlName1,TransferDescription td1,Connection conn1,SqlGeometryConverter geomConv,Config config){
		ili2sqlName=ili2sqlName1;
		td=td1;
		tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
		conn=conn1;
		schema=config.getDbschema();
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=TransferFromIli.T_ID;
		}
		createTypeDiscriminator=config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		createGenericStructRef=config.STRUCT_MAPPING_GENERICREF.equals(config.getStructMapping());
		writeIliTid=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		doItfLineTables=config.isItfTransferfile();
		createItfLineTables=doItfLineTables && config.getDoItfLineTables();
		createItfAreaRef=doItfLineTables &&  config.AREA_REF_KEEP.equals(config.getAreaRef());
		createEnumColAsItfCode=config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getCreateEnumColAsItfCode());
		this.geomConv=geomConv;
		try {
			if(conn.getMetaData().getURL().startsWith("jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)}")){
				isMsAccess=true;
			}
		} catch (SQLException e) {
			EhiLogger.logError(e);
		}
	}
	public void doit(String filename,IoxWriter iomFile,String sender,HashSet<BasketStat> stat)
	throws ch.interlis.iox.IoxException
	{
		this.basketStat=stat;
		StartTransferEvent startEvent=new StartTransferEvent();
		startEvent.setSender(sender);
		iomFile.write(startEvent);
		structQueue=new ArrayList();
		// for all MODELs
		Iterator modeli = td.iterator ();
		while (modeli.hasNext ())
		{
		  Object mObj = modeli.next ();
		  if ((mObj instanceof Model) && !(suppressModel((Model)mObj)))
		  {
			Model model=(Model)mObj;
			// for all TOPICs
			Iterator topici=model.iterator();
			while(topici.hasNext()){
				Object tObj=topici.next();
				if (tObj instanceof Topic && !(suppressTopic((Topic)tObj))){
						Topic topic=(Topic)tObj;
						StartBasketEvent iomBasket=null;
						// for all Viewables
						Iterator iter = null;
						if(iomFile instanceof ItfWriter){
							ArrayList itftablev=ModelUtilities.getItfTables(td,model.getName(),topic.getName());
							iter=itftablev.iterator();
						}else{
							iter=topic.getViewables().iterator();
						}
						while (iter.hasNext())
						{
						  Object obj = iter.next();
						  if(obj instanceof Viewable){
							  if (!suppressViewable ((Viewable)obj))
							  {
								Viewable aclass=(Viewable)obj;
								// get sql name
								DbTableName sqlName=getSqlTableName(aclass);
								// if table exists?
								if(DbUtility.tableExists(conn,sqlName)){
									// dump it
									EhiLogger.logState(aclass.getScopedName(null)+"...");
									if(iomBasket==null){
										iomBasket=new StartBasketEvent(topic.getScopedName(null),topic.getScopedName(null));
										iomFile.write(iomBasket);
									}
									dumpObject(iomFile,aclass);
								}else{
									// skip it
									EhiLogger.traceUnusualState(aclass.getScopedName(null)+"...skipped; no table "+sqlName+" in db");
								}
							  }
							  
						  }else if(obj instanceof AttributeDef){
							  if(iomFile instanceof ItfWriter){
									AttributeDef attr=(AttributeDef)obj;
									// get sql name
									DbTableName sqlName=getSqlTableNameItfLineTable(attr);
									// if table exists?
									if(DbUtility.tableExists(conn,sqlName)){
										// dump it
										EhiLogger.logState(attr.getContainer().getScopedName(null)+"_"+attr.getName()+"...");
										if(iomBasket==null){
											iomBasket=new StartBasketEvent(topic.getScopedName(null),topic.getScopedName(null));
											iomFile.write(iomBasket);
										}
										dumpItfTableObject(iomFile,attr);
									}else{
										// skip it
										EhiLogger.traceUnusualState(attr.getScopedName(null)+"...skipped; no table "+sqlName+" in db");
									}
								  
							  }
						  }
						}
						if(iomBasket!=null){
							iomFile.write(new EndBasketEvent());
							saveObjStat(iomBasket.getBid(),filename,iomBasket.getType());
						}
				}
			}
		  }
		}
		iomFile.write(new EndTransferEvent());
	}
	public void doitJava()
	{
		try{
			expgen=new ch.interlis.ili2c.generator.IndentPrintWriter(new java.io.BufferedWriter(
				new java.io.FileWriter("c:/tmp/ili2db/export.java")));
			expgen.println("import ch.ehi.basics.logging.EhiLogger;");	
			expgen.println("import ch.interlis.iom.IomObject;");	
			expgen.println("public class TransferToXtf implements IdMapper {");	
			expgen.indent();
			expgen.println("private java.sql.Connection conn=null;");	
			
			expgen.println("private IomObject newObject(String className,String tid)");	
			expgen.println("{");	
			expgen.indent();
			expgen.println("// TODO create new object");	
			expgen.println("return null;");	
			expgen.unindent();
			expgen.println("}");
				
			expgen.println("private void writeObject(IomObject iomObj)");	
			expgen.println("{");	
			expgen.indent();
			expgen.println("// TODO write object");	
			expgen.unindent();
			expgen.println("}");	
			
			expgen.println("public String mapId(String idSpace,String id)");	
			expgen.println("{");	
			expgen.indent();
			expgen.println("// TODO mapId");	
			expgen.println("return id;");	
			expgen.unindent();
			expgen.println("}");	
			
			expgen.println("public String newId()");	
			expgen.println("{");	
			expgen.indent();
			expgen.println("// TODO newId");	
			expgen.println("throw new UnsupportedOperationException(\"this mapper doesn't generate new ids\");");	
			expgen.unindent();
			expgen.println("}");	
			
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to open file for java output",ex);
		}
		try{
			structQueue=null;
			// for all MODELs
			Iterator modeli = td.iterator ();
			while (modeli.hasNext ())
			{
			  Object mObj = modeli.next ();
			  if ((mObj instanceof Model) && !(suppressModel((Model)mObj)))
			  {
				Model model=(Model)mObj;
				// for all TOPICs
				Iterator topici=model.iterator();
				while(topici.hasNext()){
					Object tObj=topici.next();
					if (tObj instanceof Topic && !(suppressTopic((Topic)tObj))){
						Topic topic=(Topic)tObj;
						//IomBasket iomBasket=null;
						// for all Viewables
						Iterator iter = topic.getViewables().iterator();
						while (iter.hasNext())
						{
						  Object obj = iter.next();
						  if ((obj instanceof Viewable) && !suppressViewableIfJava ((Viewable)obj))
						  {
							Viewable aclass=(Viewable)obj;
							// dump it
							EhiLogger.logState(aclass.getScopedName(null)+"...");
							genClassHelper(aclass);
						  }else if(obj instanceof Domain){
							Domain domainDef=(Domain)obj;
							if(domainDef.getType() instanceof EnumerationType){
								String enumName=domainDef.getScopedName(null);
								enumTypes.add(enumName);
							}
						  }
						}
					}else if ((tObj instanceof Viewable) && !suppressViewableIfJava ((Viewable)tObj))
					{
					  Viewable aclass=(Viewable)tObj;
					  // dump it
					  EhiLogger.logState(aclass.getScopedName(null)+"...");
					  genClassHelper(aclass);
					}else if(tObj instanceof Domain){
					  Domain domainDef=(Domain)tObj;
					  if(domainDef.getType() instanceof EnumerationType){
						String enumName=domainDef.getScopedName(null);
						enumTypes.add(enumName);
					  }
					}
				}
			  }
			}
		}
		finally{
			if(expgen!=null){
				{
					expgen.println("private void addAny(String iliClassName,String select){");
					expgen.indent();
						Iterator linei=addanyLines.iterator();
						String sep="";
						while(linei.hasNext()){
							String line=(String)linei.next();
							expgen.println(sep+line);
							sep="else ";
						}
						if(sep.length()>0){
							expgen.println("else{EhiLogger.logError(\"unknown class \"+iliClassName);}");
						}else{
							expgen.println("EhiLogger.logError(\"unknown class \"+iliClassName);");
						}
					expgen.unindent();				
					expgen.println("}");
				}
				{
					expgen.println("private EnumMapper getEnumMapper(String enumName){");
					expgen.indent();
						Iterator enumi=enumTypes.iterator();
						String sep="";
						while(enumi.hasNext()){
							String enumName=(String)enumi.next();
							expgen.println(sep+"if(enumName.equals(\""+enumName+"\")){return new IdentityEnumMapper();}");
							sep="else ";
						}
						if(sep.length()>0){
							expgen.println("else{throw new IllegalArgumentException(\"unknown enum \"+enumName);}");
						}else{
							expgen.println("throw new IllegalArgumentException(\"unknown enum \"+enumName);");
						}
					expgen.unindent();				
					expgen.println("}");
				}
				expgen.unindent();
				expgen.println("}");	
				expgen.close();
			}
		}
	}
	/** dumps all struct values of a given struct attr.
	 */
	private void dumpStructs(StructWrapper wrapper)
	{
		Viewable baseClass=((CompositionType)wrapper.getParentAttr().getDomain()).getComponentType();

		HashMap structelev=new HashMap();
		HashSet structClassv=new HashSet();

		String stmt=createQueryStmt4Type(baseClass,wrapper);
		EhiLogger.traceBackendCmd(stmt);
		java.sql.Statement dbstmt = null;
		try{
			
			dbstmt = conn.createStatement();
			java.sql.ResultSet rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String tid=rs.getString(colT_ID);
				String structEleClass=null;
				Viewable structClass=null;
				if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(baseClass)){
					structEleClass=ili2sqlName.mapSqlTableName(rs.getString(TransferFromIli.T_TYPE));
					structClass=(Viewable)tag2class.get(structEleClass);
				}else{
					structEleClass=baseClass.getScopedName(null);
					structClass=baseClass;
				}
				IomObject iomObj=wrapper.getParent().addattrobj(wrapper.getParentAttr().getName(),structEleClass);
				structelev.put(tid,iomObj);
				structClassv.add(structClass);
			}
		}catch(java.sql.SQLException ex){		
			EhiLogger.logError("failed to query structure elements "+baseClass.getScopedName(null),ex);
		}finally{
			if(dbstmt!=null){
				try{
					dbstmt.close();
				}catch(java.sql.SQLException ex){		
					EhiLogger.logError("failed to close query of structure elements "+baseClass.getScopedName(null),ex);
				}
			}
		}
		
		Iterator classi=structClassv.iterator();
		while(classi.hasNext()){
			Viewable aclass=(Viewable)classi.next();
			dumpObjHelper(null,aclass,wrapper,structelev);
		}
	}
	private void dumpItfTableObject(IoxWriter out,AttributeDef attr)
	{
		String stmt=createItfLineTableQueryStmt(attr,geomConv);
		EhiLogger.traceBackendCmd(stmt);
		
		SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAliases();
		String geomAttrName=ch.interlis.iom_j.itf.ModelUtilities.getHelperTableGeomAttrName(attr);
		String refAttrName=null;
		if(type instanceof SurfaceType){
			refAttrName=ch.interlis.iom_j.itf.ModelUtilities.getHelperTableMainTableRef(attr);
		}
		
		java.sql.PreparedStatement dbstmt = null;
		try{
			
			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
			java.sql.ResultSet rs=dbstmt.executeQuery();
			while(rs.next()){
				int valuei=1;
				int sqlid=rs.getInt(valuei);
				valuei++;
				
				if(writeIliTid){
					// String sqlIliTid=rs.getString(valuei);
					// TODO (forward) references need mapping from t_id to t_ili_tid
					valuei++;
				}
				
				Viewable aclass=(Viewable)attr.getContainer();

				Iom_jObject iomObj;
				iomObj=new Iom_jObject(aclass.getScopedName(null)+"_"+attr.getName(),Integer.toString(sqlid));
				
				// geomAttr
				Object geomobj=rs.getObject(valuei);
				valuei++;
				if(!rs.wasNull()){
					try{
					boolean is3D=false;
					IomObject polyline=geomConv.toIomPolyline(geomobj,ili2sqlName.mapIliAttrName(attr,TransferFromIli.ITF_LINETABLE_GEOMATTR),is3D);
					iomObj.addattrobj(geomAttrName,polyline);
					}catch(ConverterException ex){
						EhiLogger.logError("Object "+sqlid+": failed to convert polyline",ex);
					}	
				}
				
				// is of type SURFACE?
				if(type instanceof SurfaceType){
					// -> mainTable
					IomObject ref=iomObj.addattrobj(refAttrName,"REF");
					ref.setobjectrefoid(rs.getString(valuei));
					valuei++;
					
				}
				
				Table lineAttrTable=type.getLineAttributeStructure();
				if(lineAttrTable!=null){
				    Iterator attri = lineAttrTable.getAttributes ();
				    while(attri.hasNext()){
						AttributeDef lineattr=(AttributeDef)attri.next();
						valuei = addAttrValue(rs, valuei, sqlid, iomObj, lineattr);
				    }
				}
				
				if(out!=null){
					// write object
					out.write(new ObjectEvent(iomObj));
				}
			}
			}catch(java.sql.SQLException ex){		
				EhiLogger.logError("failed to query "+attr.getScopedName(null),ex);
			}catch(ch.interlis.iox.IoxException ex){		
				EhiLogger.logError("failed to write "+attr.getScopedName(null),ex);
			}finally{
				if(dbstmt!=null){
					try{
						dbstmt.close();
					}catch(java.sql.SQLException ex){		
						EhiLogger.logError("failed to close query of "+attr.getScopedName(null),ex);
					}
				}
			}
	}
	/** dumps all objects of a given class.
	 */
	private void dumpObject(IoxWriter out,Viewable aclass)
	{
		dumpObjHelper(out,aclass,null,null);
	}
	/** helper to dump all objects/structvalues of a given class/structure.
	 */
	private void dumpObjHelper(IoxWriter out,Viewable aclass,StructWrapper structWrapper,HashMap structelev)
	{
		String stmt=createQueryStmt(aclass,structWrapper);
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement dbstmt = null;
		try{
			
			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
			if(structWrapper!=null){
				dbstmt.setInt(1,structWrapper.getParentSqlId());
				if(createGenericStructRef){
					dbstmt.setString(2,ili2sqlName.mapIliAttributeDef(structWrapper.getParentAttr()));
				}
			}
			java.sql.ResultSet rs=dbstmt.executeQuery();
			while(rs.next()){
				int valuei=1;
				int sqlid=rs.getInt(valuei);
				valuei++;
				if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
					//String t_type=rs.getString(valuei);
					valuei++;
				}
				if(writeIliTid && structWrapper==null){
					// String sqlIliTid=rs.getString(valuei);
					// TODO (forward) references need mapping from t_id to t_ili_tid
					valuei++;
				}
				Iom_jObject iomObj;
				if(structWrapper==null){
					iomObj=new Iom_jObject(aclass.getScopedName(null),Integer.toString(sqlid));
					iomObj.setattrvalue(ItfWriter2.INTERNAL_T_ID, Integer.toString(sqlid));
				}else{
					iomObj=(Iom_jObject)structelev.get(Integer.toString(sqlid));
					if(createGenericStructRef){
						valuei+=4;
					}else{
						valuei+=2;
					}
				}

				Iterator iter = aclass.getAttributesAndRoles2();
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
					   valuei = addAttrValue(rs, valuei, sqlid, iomObj, baseAttr);
				   }
				   if(obj.obj instanceof RoleDef){
					   RoleDef role = (RoleDef) obj.obj;
					   if(role.getExtending()==null){
						String roleName=role.getName();
						String sqlRoleName=ili2sqlName.mapIliRoleDef(role);
						// a role of an embedded association?
						if(obj.embedded){
							AssociationDef roleOwner = (AssociationDef) role.getContainer();
							if(roleOwner.getDerivedFrom()==null){
								 // TODO if(orderPos!=0){
								int value=rs.getInt(valuei);
								valuei++;
								if(!rs.wasNull()){
									IomObject ref=iomObj.addattrobj(roleName,roleOwner.getScopedName(null));
									ref.setobjectrefoid(Integer.toString(value));
								}
							}
						 }else{
							 // TODO if(orderPos!=0){
							IomObject ref=iomObj.addattrobj(roleName,"REF");
							ref.setobjectrefoid(Integer.toString(rs.getInt(valuei)));
							valuei++;
						 }
					   }
					}
				}
				updateObjStat(iomObj.getobjecttag(), sqlid);
				if(out!=null){
					// collect structvalues
					while(!structQueue.isEmpty()){
						StructWrapper wrapper=(StructWrapper)structQueue.remove(0);
						dumpStructs(wrapper);
					}
					// write object
					out.write(new ObjectEvent(iomObj));
				}
			} // while rs
		}catch(java.sql.SQLException ex){		
			EhiLogger.logError("failed to query "+aclass.getScopedName(null),ex);
		}catch(ch.interlis.iox.IoxException ex){		
			EhiLogger.logError("failed to write "+aclass.getScopedName(null),ex);
		}finally{
			if(dbstmt!=null){
				try{
					dbstmt.close();
				}catch(java.sql.SQLException ex){		
					EhiLogger.logError("failed to close query of "+aclass.getScopedName(null),ex);
				}
			}
		}
	}
	private int addAttrValue(java.sql.ResultSet rs, int valuei, int sqlid,
			Iom_jObject iomObj, AttributeDef attr) throws SQLException {
		if(attr.getExtending()==null){
			String attrName=attr.getName();
			String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr);
			if( TransferFromIli.isBoolean(td,attr)) {
					boolean value=rs.getBoolean(valuei);
					valuei++;
					if(!rs.wasNull()){
						if(value){
							iomObj.setattrvalue(attrName,"true");
						}else{
							iomObj.setattrvalue(attrName,"false");
						}
					}
			}else if( TransferFromIli.isIli1Date(td,attr)) {
				java.sql.Date value=rs.getDate(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyyMMdd");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( TransferFromIli.isIli2Date(td,attr)) {
				java.sql.Date value=rs.getDate(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( TransferFromIli.isIli2Time(td,attr)) {
				java.sql.Time value=rs.getTime(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("HH:mm:ss.SSS");
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else if( TransferFromIli.isIli2DateTime(td,attr)) {
				java.sql.Timestamp value=rs.getTimestamp(valuei);
				valuei++;
				if(!rs.wasNull()){
					java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // with timezone: yyyy-MM-dd'T'HH:mm:ss.SSSZ 
					iomObj.setattrvalue(attrName,fmt.format(value));
				}
			}else{
				Type type = attr.getDomainResolvingAliases();
				if (type instanceof CompositionType){
					// enque iomObj as parent
					enqueueStructAttr(new StructWrapper(sqlid,attr,iomObj));
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
						ref.setobjectrefoid(Integer.toString(value));
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
	private ArrayList enumTypes=new ArrayList();
	private ArrayList addanyLines=new ArrayList();
	private void genClassHelper(Viewable aclass)
	{
		boolean doStruct=false;
		if(aclass instanceof Table){
			doStruct=!((Table)aclass).isIdentifiable();
		}
		if(doStruct){
			expgen.println("private void add"+aclass.getName()+"(String parentTid,String parentAttrSql,IomObject parent,String parentAttrIli)");
		}else{
			expgen.println("private void add"+aclass.getName()+"(String subset)");
			String addany="if(iliClassName.equals(\""+aclass.getScopedName(null)+"\")){add"+aclass.getName()+"(select);}";
			addanyLines.add(addany);
		}
		expgen.println("{");
		expgen.indent();

			expgen.println("String tabName=\""+createQueryStmtFromClause(aclass)+"\";");
			expgen.println("String stmt=\""+createQueryStmt(aclass,null)+"\";");
			if(!doStruct){
				expgen.println("if(subset!=null){");
				expgen.println("stmt=stmt+\" AND \"+subset;");
				expgen.println("}");
			}
			expgen.println("EhiLogger.traceBackendCmd(stmt);");
			expgen.println("java.sql.PreparedStatement dbstmt = null;");
			expgen.println("try{");
			expgen.indent();
				//expgen.println("dbstmt = conn.createStatement();");
				//expgen.println("java.sql.ResultSet rs=dbstmt.executeQuery(stmt);");
				expgen.println("dbstmt = conn.prepareStatement(stmt);");
				expgen.println("dbstmt.clearParameters();");
				if(doStruct){
					expgen.println("dbstmt.setString(1,parentTid);");
					expgen.println("dbstmt.setString(2,parentAttrSql);");
				}
				expgen.println("java.sql.ResultSet rs=dbstmt.executeQuery();");
				expgen.println("while(rs.next()){");
				expgen.indent();
					expgen.println("String tid=DbUtility.getString(rs,\"T_Id\",false,tabName);");
					expgen.println("String recInfo=tabName+\" \"+tid;");
					expgen.println("IomObject iomObj;");
					if(!doStruct){
						expgen.println("iomObj=newObject(\""+aclass.getScopedName(null)+"\",mapId(\""+getSqlTableName(aclass)+"\",tid));");
					}else{
						expgen.println("iomObj=(IomObject)parent.addattrobj(parentAttrIli,\""+aclass.getScopedName(null)+"\");");
					}
					Iterator iter = aclass.getAttributesAndRoles2();
					while (iter.hasNext()) {
					   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
					   if (obj.obj instanceof AttributeDef) {
						   AttributeDef attr = (AttributeDef) obj.obj;
						   if(attr.getExtending()==null){
							String attrName=attr.getName();
							String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr);
							Type type = attr.getDomain();
							if( (type instanceof TypeAlias) 
								&& TransferFromIli.isBoolean(td,type)) {
									expgen.println("Boolean prop_"+attrName+"=Db2Xtf.getBoolean(rs,\""+sqlAttrName+"\","
										+(type.isMandatoryConsideringAliases()?"false":"true")
										+",recInfo,iomObj,\""+attrName+"\");");
							}else{
								type = attr.getDomainResolvingAliases();
								if (type instanceof CompositionType){
									// enque iomObj as parent
									//enqueueStructAttr(new StructWrapper(tid,attr,iomObj));
									CompositionType ct=(CompositionType)type;
									expgen.println("add"+ct.getComponentType().getName()+"(tid,\""+ili2sqlName.mapIliAttributeDef(attr)+"\",iomObj,\""+attr.getName()+"\");");
								}else if (type instanceof PolylineType){
								 }else if(type instanceof SurfaceOrAreaType){
								 }else if(type instanceof CoordType){
								}else if(type instanceof EnumerationType){
									String enumName=null;
									if(attr.getDomain() instanceof TypeAlias){
										Domain domainDef=((TypeAlias)attr.getDomain()).getAliasing();
										enumName=domainDef.getScopedName(null);
									}else{
										enumName=aclass.getScopedName(null)+"->"+attrName;
										enumTypes.add(enumName);
									}
									expgen.println("String prop_"+attrName+"=Db2Xtf.getEnum(rs,\""+sqlAttrName+"\","
										+(type.isMandatoryConsideringAliases()?"false":"true")
										+",recInfo,getEnumMapper(\""+enumName+"\"),iomObj,\""+attrName+"\");");
								}else{
									expgen.println("String prop_"+attrName+"=Db2Xtf.getString(rs,\""+sqlAttrName+"\","
										+(type.isMandatoryConsideringAliases()?"false":"true")
										+",recInfo,iomObj,\""+attrName+"\");");
								}
							   }
							}
					   }
					   if(obj.obj instanceof RoleDef){
						   RoleDef role = (RoleDef) obj.obj;
						   if(role.getExtending()==null){
							String roleName=role.getName();
							String sqlRoleName=ili2sqlName.mapIliRoleDef(role);
							// a role of an embedded association?
							if(obj.embedded){
								AssociationDef roleOwner = (AssociationDef) role.getContainer();
								if(roleOwner.getDerivedFrom()==null){
									 // TODO if(orderPos!=0){
									expgen.println("String prop_"+roleName+"=Db2Xtf.getRef(rs,\""+sqlRoleName+"\","
										+"true"
										+",recInfo,this,\""+getSqlTableName(role.getDestination())+"\",iomObj,\""+roleName+"\",\""+roleOwner.getScopedName(null)+"\");");
								}
							 }else{
								 // TODO if(orderPos!=0){
								expgen.println("String prop_"+roleName+"=Db2Xtf.getRef(rs,\""+sqlRoleName+"\","
									+"false"
									+",recInfo,this,\""+getSqlTableName(role.getDestination())+"\",iomObj,\""+roleName+"\",\"REF\");");
							 }
						   }
						}
					}
					// add referenced and referencing objects if it is not a struct
					if(!doStruct && aclass instanceof AbstractClassDef){
						AbstractClassDef aclass2=(AbstractClassDef)aclass;
						Iterator associ=aclass2.getTargetForRoles();
						while(associ.hasNext()){
							RoleDef roleThis=(RoleDef)associ.next();
							if(roleThis.getKind()==RoleDef.Kind.eAGGREGATE
								  || roleThis.getKind()==RoleDef.Kind.eCOMPOSITE){
								RoleDef oppEnd=roleThis.getOppEnd();
								if(roleThis.isAssociationEmbedded()){
									expgen.println("addPendingObject(\""+oppEnd.getDestination().getScopedName(null)+"\",\"T_Id\",prop_"+oppEnd.getName()+");");
								}else if(oppEnd.isAssociationEmbedded()){
									expgen.println("addPendingObject(\""+oppEnd.getDestination().getScopedName(null)+"\",\""+ili2sqlName.mapIliRoleDef(roleThis)+"\",tid);");
								}else{
									expgen.println("addPendingObject(\""+((AssociationDef)(oppEnd.getContainer())).getScopedName(null)+"\",\""+ili2sqlName.mapIliRoleDef(roleThis)+"\",prop_"+roleThis.getName()+");");
								}
							}
						}
					}
					// writeObject if it is not a struct
					if(!doStruct){
						expgen.println("writeObject(iomObj);");
					}
				expgen.unindent();
				expgen.println("}");
			expgen.unindent();
			expgen.println("}catch(java.sql.SQLException ex){");
			expgen.indent();
				expgen.println("EhiLogger.logError(\"failed to query \"+tabName,ex);");
			expgen.unindent();
			expgen.println("}finally{");
			expgen.indent();
				expgen.println("if(dbstmt!=null){");
				expgen.indent();
				expgen.println("try{");
				expgen.indent();
					expgen.println("dbstmt.close();");
				expgen.unindent();
				expgen.println("}catch(java.sql.SQLException ex){");
				expgen.indent();
					expgen.println("EhiLogger.logError(\"failed to close query of \"+tabName,ex);");
				expgen.unindent();
				expgen.println("}");
				expgen.unindent();
				expgen.println("}");
			expgen.unindent();
			expgen.println("}");
		expgen.unindent();
		expgen.println("}");
	}
	protected boolean suppressModel (Model model)
	{
	  if (model == null)
		return true;


	  if (model == td.INTERLIS)
		return true;


	  if ((model instanceof TypeModel) || (model instanceof PredefinedModel))
		return true;


	  return false;
	}
	protected boolean suppressTopic (Topic topic)
	{
	  if (topic == null)
		return true;


	  if (topic.isAbstract ())
		return true;


	  return false;
	}
	protected boolean suppressViewable (Viewable v)
	{
	  if (v == null)
		return true;


	  if (v.isAbstract())
		return true;

	  if(v instanceof AssociationDef){
		  AssociationDef assoc=(AssociationDef)v;
		  if(assoc.isLightweight()){
			  return true;
		  }
		  if(assoc.getDerivedFrom()!=null){
			  return true;
		  }
	  }

	  Topic topic;
	  if ((v instanceof View) && ((topic=(Topic)v.getContainer (Topic.class)) != null)
		  && !topic.isViewTopic())
		return true;


	  /* STRUCTUREs do not need to be printed with their INTERLIS container,
		 but where they are used. */
	  if ((v instanceof Table) && !((Table)v).isIdentifiable())
		return true;


	  return false;
	}
	protected boolean suppressViewableIfJava (Viewable v)
	{
	  if (v == null)
		return true;


	  if (v.isAbstract())
		return true;

	  if(v instanceof AssociationDef){
		  AssociationDef assoc=(AssociationDef)v;
		  if(assoc.isLightweight()){
			  return true;
		  }
		  if(assoc.getDerivedFrom()!=null){
			  return true;
		  }
	  }

	  Topic topic;
	  if ((v instanceof View) && ((topic=(Topic)v.getContainer (Topic.class)) != null)
		  && !topic.isViewTopic())
		return true;

	  return false;
	}
	/** maps ili class name to a sql table name.
	 */
	private DbTableName getSqlTableName(Viewable def){
		String sqlname=ili2sqlName.mapIliClassDef(def);
		return new DbTableName(schema,sqlname);
	}
	private DbTableName getSqlTableNameItfLineTable(AttributeDef def){
		String sqlname=ili2sqlName.mapItfLineTableAsTable(def);
		return new DbTableName(schema,sqlname);
	}
	private String createItfLineTableQueryStmt(AttributeDef attr,SqlGeometryConverter conv){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		if(writeIliTid){
			ret.append(", r0."+TransferFromIli.T_ILI_TID);
		}
		String sep=",";
		
		SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAliases();
		
		// geomAttr
		 ret.append(sep);
		 sep=",";
		 ret.append(conv.getSelectValueWrapperPolyline(ili2sqlName.mapIliAttrName(attr,TransferFromIli.ITF_LINETABLE_GEOMATTR)));
		 //ret.append(ili2sqlName.mapIliAttrName(geomAttrName));

		 // is it of type SURFACE?
		 if(type instanceof SurfaceType){
			 // -> mainTable
			 ret.append(sep);
			 sep=",";
			 ret.append(ili2sqlName.mapIliAttrName(attr,TransferFromIli.ITF_LINETABLE_MAINTABLEREF));
		 }

			Table lineAttrTable=type.getLineAttributeStructure();
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
					AttributeDef lineattr=(AttributeDef)attri.next();
				   sep = addAttrToQueryStmt(ret, sep, lineattr);
			    }
			}
		 
		ret.append(" FROM ");
		String sqlTabName=ili2sqlName.mapItfLineTableAsTable(attr);
		if(schema!=null){
			sqlTabName=schema+"."+sqlTabName;
		}
		ret.append(sqlTabName);
		ret.append(" r0");

		return ret.toString();
	}
	/** creates sql query statement for a class.
	 * @param aclass type of objects to build query for
	 * @param wrapper not null, if building query for struct values
	 * @return SQL-Query statement
	 */
	private String createQueryStmt(Viewable aclass,StructWrapper structWrapper){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
			ret.append(", r0."+TransferFromIli.T_TYPE);
		}
		if(writeIliTid && structWrapper==null){
			ret.append(", r0."+TransferFromIli.T_ILI_TID);
		}
		if(structWrapper!=null){
			if(createGenericStructRef){
				ret.append(", r0."+TransferFromIli.T_PARENT_ID);
				ret.append(", r0."+TransferFromIli.T_PARENT_TYPE);
				ret.append(", r0."+TransferFromIli.T_PARENT_ATTR);
			}else{
				ret.append(", r0."+ili2sqlName.mapIliAttributeDefQualified(structWrapper.getParentAttr()));
			}
			ret.append(", r0."+TransferFromIli.T_SEQ);
		}
		String sep=",";
		Iterator iter = aclass.getAttributesAndRoles2();
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
			   sep = addAttrToQueryStmt(ret, sep, baseAttr);
		   }
		   if(obj.obj instanceof RoleDef){
			   RoleDef role = (RoleDef) obj.obj;
			   if(role.getExtending()==null){
				String roleName=ili2sqlName.mapIliRoleDef(role);
				// a role of an embedded association?
				if(obj.embedded){
					AssociationDef roleOwner = (AssociationDef) role.getContainer();
					if(roleOwner.getDerivedFrom()==null){
						 // TODO if(orderPos!=0){
						 ret.append(sep);
						 sep=",";
						 ret.append(roleName);
					}
				 }else{
					 // TODO if(orderPos!=0){
					 ret.append(sep);
					 sep=",";
					 ret.append(roleName);
				 }
			   }
			}
		}
		// stdcols
		if(createStdCols){
			ret.append(sep);
			sep=",";
			ret.append("r0."+TransferFromIli.T_LAST_CHANGE);
			ret.append(sep);
			sep=",";
			ret.append("r0."+TransferFromIli.T_CREATE_DATE);
			ret.append(sep);
			sep=",";
			ret.append("r0."+TransferFromIli.T_USER);
		}

		ret.append(" FROM ");
		ArrayList tablev=new ArrayList(10);
		tablev.add(aclass);
		Viewable base=(Viewable)aclass.getExtending();
		while(base!=null){
			tablev.add(base);		
			base=(Viewable)base.getExtending();
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
			ret.append(getSqlTableName((Viewable)tablev.get(i)));
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
		if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
			ret.append(sep+" r0."+TransferFromIli.T_TYPE+"='"+getSqlTableName(aclass).getName()+"'");
			sep=" AND";
		}
		if(structWrapper!=null){
			if(createGenericStructRef){
				ret.append(sep+" r0."+TransferFromIli.T_PARENT_ID+"=? AND r0."+TransferFromIli.T_PARENT_ATTR+"=? ORDER BY r0."+TransferFromIli.T_SEQ+" ASC");
			}else{
				ret.append(sep+" r0."+ili2sqlName.mapIliAttributeDefQualified(structWrapper.getParentAttr())+"=? ORDER BY r0."+TransferFromIli.T_SEQ+" ASC");
			}
		}
		return ret.toString();
	}
	private String addAttrToQueryStmt(StringBuffer ret, String sep, AttributeDef attr) {
		if(attr.getExtending()==null){
			Type type = attr.getDomainResolvingAliases();
			 String attrName=ili2sqlName.mapIliAttributeDef(attr);
			if (type instanceof CompositionType){
			}else if (type instanceof PolylineType){
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperPolyline(attrName));
			 }else if(type instanceof SurfaceOrAreaType){
				 if(createItfLineTables){
				 }else{
					 ret.append(sep);
					 sep=",";
					 ret.append(geomConv.getSelectValueWrapperSurface(attrName));
				 }
				 if(createItfAreaRef){
					 if(type instanceof AreaType){
						 ret.append(sep);
						 sep=",";
						 ret.append(geomConv.getSelectValueWrapperCoord(attrName+TransferFromIli.ITF_MAINTABLE_GEOTABLEREF_SUFFIX));
					 }
				 }
			 }else if(type instanceof CoordType){
				 ret.append(sep);
				 sep=",";
				 ret.append(geomConv.getSelectValueWrapperCoord(attrName));
			}else{
				 ret.append(sep);
				 sep=",";
				 ret.append(attrName);
			}
		   }
		return sep;
	}
	private String createQueryStmtFromClause(Viewable aclass){
		StringBuffer ret = new StringBuffer();
		ArrayList tablev=new ArrayList(10);
		tablev.add(aclass);
		Viewable base=(Viewable)aclass.getExtending();
		while(base!=null){
			tablev.add(base);		
			base=(Viewable)base.getExtending();
		}
		String sep="";
		int tablec=tablev.size();
		for(int i=0;i<tablec;i++){
			ret.append(sep);
			ret.append(getSqlTableName((Viewable)tablev.get(i)));
			sep=", ";
		}
		return ret.toString();
	}
	/** creates sql query statement for a structattr.
	 * @param aclass type of objects to build query for
	 * @param wrapper not null, if building query for struct values
	 * @return SQL-Query statement
	 */
	private String createQueryStmt4Type(Viewable aclass,StructWrapper wrapper){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
			ret.append(", r0."+TransferFromIli.T_TYPE);
		}
		ret.append(" FROM ");
		Viewable rootClass=(Viewable)aclass.getRootExtending();
		if(rootClass==null){
		 rootClass=aclass;
		}
		ret.append(getSqlTableName(rootClass));
		ret.append(" r0");
		if(wrapper!=null){
			if(createGenericStructRef){
				ret.append(" WHERE r0."+TransferFromIli.T_PARENT_ID+"="+wrapper.getParentSqlId()+" AND r0."+TransferFromIli.T_PARENT_ATTR+"='"
						+ili2sqlName.mapIliAttributeDef(wrapper.getParentAttr())
						+"' ORDER BY r0."+TransferFromIli.T_SEQ+" ASC");
			}else{
				ret.append(" WHERE r0."+ili2sqlName.mapIliAttributeDefQualified(wrapper.getParentAttr())+"="+wrapper.getParentSqlId()
						+" ORDER BY r0."+TransferFromIli.T_SEQ+" ASC");
			}
		}
		return ret.toString();
	}
	/** add an entry to list of not yet processed struct attributes
	 * @param wrapper wrapper around a struct attribute
	 */
	private void enqueueStructAttr(StructWrapper wrapper)
	{
		structQueue.add(wrapper);
	}
	private String mapItfCode2XtfCode(EnumerationType type,int itfCode)
	{
		return enumMapper.mapItfCode2XtfCode(type, Integer.toString(itfCode));
	}	

	private HashSet<BasketStat> basketStat=null;
	private HashMap<String, ClassStat> objStat=new HashMap<String, ClassStat>();
	private void updateObjStat(String tag, int sqlId)
	{
		if(objStat.containsKey(tag)){
			ClassStat stat=objStat.get(tag);
			stat.addEndid(sqlId);
		}else{
			ClassStat stat=new ClassStat(tag,sqlId);
			objStat.put(tag,stat);
		}
	}
	private void saveObjStat(String iliBasketId,String file,String topic)
	{
		// save it for later output to log
		basketStat.add(new BasketStat(file,topic,iliBasketId,objStat));
		// setup new collection
		objStat=new HashMap<String, ClassStat>();
	}

}
