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
package ch.ehi.ili2db.fromxtf;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.converter.*;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.mapping.Mapping;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.metamodel.*;
import ch.interlis.iom.IomConstants;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.EnumCodeMapper;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.*;
import ch.interlis.iox_j.IoxInvalidDataException;
import ch.interlis.iox_j.jts.Iox2jts;
import ch.interlis.iox_j.jts.Iox2jtsException;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 17.02.2005 $
 */
public class TransferFromXtf {
	private Mapping ili2sqlName=null;
	/** mappings from xml-tags to Viewable|AttributeDef
	 */
	private HashMap tag2class=null;
	/** list of seen but unknown types; maintained to prevent duplicate error messages
	 */
	private HashSet unknownTypev=null;
	private TransferDescription td=null;
	private Connection conn=null;
	private String schema=null; // name of dbschema or null
	private java.sql.Timestamp today=null;
	private String dbusr=null;
	private SqlGeometryConverter geomConv=null;
	private int defaultSrsid=0;
	private boolean createStdCols=false;
	private boolean createEnumTxtCol=false;
	private boolean createEnumColAsItfCode=false;
	private boolean createTypeDiscriminator=false;
	private boolean createGenericStructRef=false;
	private boolean readIliTid=false;
	private boolean createBasketCol=false;
	private String xtffilename=null;
	private String attachmentKey=null;
	private boolean doItfLineTables=false;
	private boolean createItfLineTables=false;
	private boolean isItfReader=false;
	private boolean createItfAreaRef=false;
	private boolean deleteExistingData=false;
	private String colT_ID=null;
	private EnumCodeMapper enumTypes=new EnumCodeMapper();
	//private int sqlIdGen=1;
	private ch.ehi.ili2db.base.DbIdGen idGen=null;
	private HashMap xtfId2sqlId=new HashMap();
	/** list of not yet processed struct values
	 */
	private ArrayList structQueue=null;
	public TransferFromXtf(Mapping ili2sqlName1,
			TransferDescription td1,
			Connection conn1,
			String dbusr1,
			SqlGeometryConverter geomConv,
			Config config){
		ili2sqlName=ili2sqlName1;
		td=td1;
		conn=conn1;
		dbusr=dbusr1;
		if(dbusr==null || dbusr.length()==0){
			dbusr=System.getProperty("user.name");
		}
		schema=config.getDbschema();
		this.geomConv=geomConv;
		idGen=new ch.ehi.ili2db.base.DbIdGen(conn,dbusr);
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createEnumTxtCol=config.CREATE_ENUM_TXT_COL.equals(config.getCreateEnumCols());
		createEnumColAsItfCode=config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getCreateEnumColAsItfCode());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=TransferFromIli.T_ID;
		}
		createTypeDiscriminator=config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		createGenericStructRef=config.STRUCT_MAPPING_GENERICREF.equals(config.getStructMapping());
		readIliTid=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		createBasketCol=config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		doItfLineTables=config.isItfTransferfile();
		createItfLineTables=doItfLineTables && config.getDoItfLineTables();
		createItfAreaRef=doItfLineTables &&  config.AREA_REF_KEEP.equals(config.getAreaRef());
		xtffilename=config.getXtffile();
		
		deleteExistingData=config.DELETE_DATA.equals(config.getDeleteMode());
		
		String defaultCrsAuthority=config.getDefaultSrsAuthority();
		String defaultCrsCode=config.getDefaultSrsCode();
		try{
			defaultSrsid=geomConv.getSrsid(defaultCrsAuthority,defaultCrsCode,conn);
		}catch(UnsupportedOperationException ex){
			EhiLogger.logAdaption("no CRS support by converter; use -1 as default srsid");
			defaultSrsid=-1;
		}catch(ConverterException ex){
			throw new IllegalArgumentException("failed to get srsid for "+defaultCrsAuthority+":"+defaultCrsCode+", "+ex.getLocalizedMessage());
		}
	}
		
	public void doit(IoxReader reader,Config config,HashSet<BasketStat> stat)
	throws IoxException
	{
		basketStat=stat;
		today=new java.sql.Timestamp(System.currentTimeMillis());
		if(doItfLineTables){
			tag2class=ch.interlis.iom_j.itf.ModelUtilities.getTagMap(td);
		}else{
			tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
		}
		isItfReader=reader instanceof ItfReader;
		unknownTypev=new HashSet();
		structQueue=new ArrayList();
		boolean surfaceAsPolyline=true;
		int datasetSqlId=newObjSqlId();
		int importSqlId=0;
		int basketSqlId=0;
		int startTid=0;
		int endTid=0;
		int objCount=0;

		try {
			writeDataset(datasetSqlId);
			importSqlId=writeImportStat(datasetSqlId,xtffilename,today,dbusr);
		} catch (SQLException e) {
			EhiLogger.logError(e);
		} catch (ConverterException e) {
			EhiLogger.logError(e);
		}
		
		StartBasketEvent basket=null;
		// more baskets?
		IoxEvent event=reader.read();
		while(event!=null){
			if(event instanceof StartBasketEvent){
				basket=(StartBasketEvent)event;
				EhiLogger.logState("Basket "+basket.getType()+"(oid "+basket.getBid()+")...");
				try {
					basketSqlId=getObjSqlId(basket.getBid());
					if(attachmentKey==null){
						if(xtffilename!=null){
							attachmentKey=new java.io.File(xtffilename).getName()+"-"+Integer.toString(basketSqlId);
						}else{
							attachmentKey=Integer.toString(basketSqlId);
						}
						config.setAttachmentKey(attachmentKey);
					}
					writeBasket(datasetSqlId,basket,basketSqlId,attachmentKey);
				} catch (SQLException ex) {
					EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
				} catch (ConverterException ex) {
					EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
				}
				startTid=getLastSqlId();
				objCount=0;
			}else if(event instanceof EndBasketEvent){
				if(reader instanceof ItfReader2){
		        	ArrayList<IoxInvalidDataException> dataerrs = ((ItfReader2) reader).getDataErrs();
		        	if(dataerrs.size()>0){
		        		for(IoxInvalidDataException dataerr:dataerrs){
		        			EhiLogger.logError(dataerr);
		        		}
		        		((ItfReader2) reader).clearDataErrs();
		        	}
				}
				// TODO update import counters
				endTid=getLastSqlId();
				try {
					String filename=null;
					if(xtffilename!=null){
						filename=new java.io.File(xtffilename).getName();
					}
					int importId=writeImportBasketStat(importSqlId,basketSqlId,startTid,endTid,objCount);
					saveObjStat(importId,basket.getBid(),filename,basket.getType());
				} catch (SQLException ex) {
					EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
				} catch (ConverterException ex) {
					EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
				}
			}else if(event instanceof ObjectEvent){
				objCount++;
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				// translate object
				try{
					//EhiLogger.debug(iomObj.toString());
					writeObject(basketSqlId,iomObj,null);
				}catch(ConverterException ex){
					EhiLogger.debug(iomObj.toString());
					EhiLogger.logError("Object "+iomObj.getobjectoid()+" at (line "+iomObj.getobjectline()+",col "+iomObj.getobjectcol()+")",ex);
				}catch(java.sql.SQLException ex){
					EhiLogger.debug(iomObj.toString());
					EhiLogger.logError("Object "+iomObj.getobjectoid()+" at (line "+iomObj.getobjectline()+",col "+iomObj.getobjectcol()+")",ex);
				}catch(java.lang.RuntimeException ex){
					EhiLogger.traceState(iomObj.toString());
					throw ex;
				}
				while(!structQueue.isEmpty()){
					StructWrapper struct=(StructWrapper)structQueue.remove(0); // get front
					try{
						writeObject(basketSqlId,struct.getStruct(),struct);
					}catch(ConverterException ex){
						EhiLogger.logError("Object "+iomObj.getobjectoid()+"; Struct at (line "+struct.getStruct().getobjectline()+",col "+struct.getStruct().getobjectcol()+")",ex);
					}catch(java.sql.SQLException ex){
						EhiLogger.logError("Object "+iomObj.getobjectoid()+"; Struct at (line "+struct.getStruct().getobjectline()+",col "+struct.getStruct().getobjectcol()+")",ex);
					}
				}
			}else if(event instanceof EndTransferEvent){
				break;
			}
			event=reader.read();
		}
		
	}
	/** if structEle==null, iomObj is an object. If structEle!=null iomObj is a struct value.
	 */
	private void writeObject(int basketSqlId,IomObject iomObj,StructWrapper structEle)
		throws java.sql.SQLException,ConverterException
	{
		String tag=iomObj.getobjecttag();
		//EhiLogger.debug("tag "+tag);
		Object modelele=tag2class.get(tag);
		if(modelele==null){
			if(!unknownTypev.contains(tag)){
				EhiLogger.logError("unknown type <"+tag+">, line "+Integer.toString(iomObj.getobjectline())+", col "+Integer.toString(iomObj.getobjectcol()));
			}
			return;
		}
		// is it a SURFACE or AREA line table?
		if(createItfLineTables && modelele instanceof AttributeDef){
			writeItfLineTableObject(basketSqlId,iomObj,(AttributeDef)modelele);
			return;
		}
		// ASSERT: an ordinary class/table
		Viewable aclass=(Viewable)modelele;		
		String sqlType=(String)ili2sqlName.mapIliClassDef(aclass);
		 int sqlId;
		 // is it an object?
		 if(structEle==null){
				// map oid of transfer file to a sql id
			 	String tid=iomObj.getobjectoid();
			 	if(tid!=null && tid.length()>0){
					sqlId=getObjSqlId(tid);
			 	}else{
					 // it is an assoc without tid
					 // get a new sql id
					 sqlId=newObjSqlId();
			 	}
		 }else{
			 // it is a struct value
			 // get a new sql id
			 sqlId=newObjSqlId();
		 }
		 updateObjStat(tag,sqlId);
		 // loop over all classes; start with leaf, end with the base of the inheritance hierarchy
		 while(aclass!=null){
			String sqlname=(String)ili2sqlName.mapIliClassDef(aclass);
			if(schema!=null){
				sqlname=schema+"."+sqlname;
			}
			String insert = getInsertStmt(sqlname,aclass,structEle);
			EhiLogger.traceBackendCmd(insert);
			PreparedStatement ps = conn.prepareStatement(insert);
			try{
				int valuei=1;
				
				ps.setInt(valuei, sqlId);
				valuei++;
				
				if(createBasketCol){
					ps.setInt(valuei, basketSqlId);
					valuei++;
				}
				
				if(aclass.getExtending()==null){
					if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
						ps.setString(valuei, sqlType);
						valuei++;
					}
					// if class
					if(structEle==null){
						if((aclass instanceof Table) && ((Table)aclass).isIdentifiable()){
							if(readIliTid){
								// import TID from transfer file
								ps.setString(valuei, iomObj.getobjectoid());
								valuei++;
							}
						}
					}
					// if struct, add ref to parent
					if(structEle!=null){
						ps.setInt(valuei, structEle.getParentSqlId());
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
			 
				Iterator iter = aclass.getDefinedAttributesAndRoles2();
				while (iter.hasNext()) {
					ViewableTransferElement obj = (ViewableTransferElement)iter.next();
					if (obj.obj instanceof AttributeDef) {
						AttributeDef attr = (AttributeDef) obj.obj;
						valuei = addAttrValue(iomObj, sqlType, sqlId, ps,
								valuei, attr);
					}
					if(obj.obj instanceof RoleDef){
						RoleDef role = (RoleDef) obj.obj;
						if(role.getExtending()==null){
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
										String refoid=structvalue.getobjectrefoid();
										long orderPos=structvalue.getobjectreforderpos();
										if(orderPos!=0){
										   // refoid,orderPos
										   //ret.setStringAttribute(roleName, refoid);
										   //ret.setStringAttribute(roleName+".orderPos", Long.toString(orderPos));
										}else{
										   // refoid
										   //ret.setStringAttribute(roleName, refoid);
										}
									   int refsqlId=getObjSqlId(refoid);
									   ps.setInt(valuei, refsqlId);
									}else{
										ps.setNull(valuei, Types.INTEGER);
									}
									valuei++;
								}
							 }else{
								 IomObject structvalue=iomObj.getattrobj(roleName,0);
								 String refoid=structvalue.getobjectrefoid();
								 long orderPos=structvalue.getobjectreforderpos();
								 if(orderPos!=0){
									// refoid,orderPos
									//ret.setStringAttribute(roleName, refoid);
									//ret.setStringAttribute(roleName+".orderPos", Long.toString(orderPos));
								 }else{
									// refoid
									//ret.setStringAttribute(roleName, refoid);
								 }
								int refsqlId=getObjSqlId(refoid);
								ps.setInt(valuei, refsqlId);
								valuei++;
							 }
						}
					 }
				}
				if(createStdCols){
					// T_LastChange
					ps.setTimestamp(valuei, today);
					valuei++;
					// T_CreateDate
					ps.setTimestamp(valuei, today);
					valuei++;
					// T_User
					ps.setString(valuei, dbusr);
					valuei++;
				}
				ps.executeUpdate();
			}finally{
				ps.close();
			}
			aclass=(Viewable)aclass.getExtending();
		 }
	}

	private int addAttrValue(IomObject iomObj, String sqlType, int sqlId,
			PreparedStatement ps, int valuei, AttributeDef attr)
			throws SQLException, ConverterException {
		if(attr.getExtending()==null){
			 String attrName=attr.getName();
			if( TransferFromIli.isBoolean(td,attr)) {
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
			}else if( TransferFromIli.isIli1Date(td,attr)) {
				String value=iomObj.getattrvalue(attrName);
				if(value!=null){
					GregorianCalendar gdate=new GregorianCalendar(Integer.parseInt(value.substring(0,4)),Integer.parseInt(value.substring(4,6))-1,Integer.parseInt(value.substring(6,8)));
					java.sql.Date date=new java.sql.Date(gdate.getTimeInMillis());
					ps.setDate(valuei,date);
				}else{
					ps.setNull(valuei,Types.DATE);
				}
				valuei++;
			}else if( TransferFromIli.isIli2Date(td,attr)) {
				String value=iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Date date=new java.sql.Date(xmldate.toGregorianCalendar().getTimeInMillis());
					ps.setDate(valuei,date);
				}else{
					ps.setNull(valuei,Types.DATE);
				}
				valuei++;
			}else if( TransferFromIli.isIli2Time(td,attr)) {
				String value=iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Time time=new java.sql.Time(xmldate.toGregorianCalendar().getTimeInMillis());
					ps.setTime(valuei,time);
				}else{
					ps.setNull(valuei,Types.TIME);
				}
				valuei++;
			}else if( TransferFromIli.isIli2DateTime(td,attr)) {
				String value=iomObj.getattrvalue(attrName);
				if(value!=null){
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					java.sql.Timestamp datetime=new java.sql.Timestamp(xmldate.toGregorianCalendar().getTimeInMillis());
					ps.setTimestamp(valuei,datetime);
				}else{
					ps.setNull(valuei,Types.TIMESTAMP);
				}
				valuei++;
			}else{
				Type type = attr.getDomainResolvingAliases();
			 
				if (type instanceof CompositionType){
					 // enqueue struct values
					 int structc=iomObj.getattrvaluecount(attrName);
					 for(int structi=0;structi<structc;structi++){
					 	IomObject struct=iomObj.getattrobj(attrName,structi);
					 	String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr);
					 	enqueStructValue(sqlId,sqlType,sqlAttrName,struct,structi,attr);
					 }
				}else if (type instanceof PolylineType){
					 IomObject value=iomObj.getattrobj(attrName,0);
					 if(value!=null){
						ps.setObject(valuei,geomConv.fromIomPolyline(value,getSrsid(type),false,getP((PolylineType)type)));
					 }else{
						geomConv.setPolylineNull(ps,valuei);
					 }
					 valuei++;
				 }else if(type instanceof SurfaceOrAreaType){
					 if(createItfLineTables){
					 }else{
						 IomObject value=iomObj.getattrobj(attrName,0);
						 if(value!=null){
							 Object geomObj = geomConv.fromIomSurface(value,getSrsid(type),((SurfaceOrAreaType)type).getLineAttributeStructure()!=null,false,getP((SurfaceOrAreaType)type));
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
								boolean is3D=false;
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
							ps.setString(valuei, value);
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
					 if(refoid!=null){
							int refsqlId=getObjSqlId(refoid);
							ps.setInt(valuei, refsqlId);
					 }else{
							ps.setNull(valuei,Types.INTEGER);
					 }
					valuei++;
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
	private HashMap typeCache=new HashMap();
	private double getP(LineType type)
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
	private void writeItfLineTableObject(int basketSqlId,IomObject iomObj,AttributeDef attrDef)
	throws java.sql.SQLException,ConverterException
	{
		SurfaceOrAreaType type = (SurfaceOrAreaType)attrDef.getDomainResolvingAliases();
		String geomAttrName=ch.interlis.iom_j.itf.ModelUtilities.getHelperTableGeomAttrName(attrDef);
		String refAttrName=null;
		if(type instanceof SurfaceType){
			refAttrName=ch.interlis.iom_j.itf.ModelUtilities.getHelperTableMainTableRef(attrDef);
		}
		Table lineAttrTable=type.getLineAttributeStructure();
		
		// map oid of transfer file to a sql id
		int sqlId=getObjSqlId(iomObj.getobjectoid());
		
		String insert=createItfLineTableInsertStmt(attrDef);
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try {
			int valuei = 1;

			ps.setInt(valuei, sqlId);
			valuei++;

			if (createBasketCol) {
				ps.setInt(valuei, basketSqlId);
				valuei++;
			}
			
			if(readIliTid){
				// import TID from transfer file
				ps.setString(valuei, iomObj.getobjectoid());
				valuei++;
			}

			IomObject value = iomObj.getattrobj(geomAttrName, 0);
			if (value != null) {
				ps.setObject(valuei,
						geomConv.fromIomPolyline(value, getSrsid(type), false,getP(type)));
			} else {
				geomConv.setPolylineNull(ps, valuei);
			}
			valuei++;

			if (type instanceof SurfaceType) {
				IomObject structvalue = iomObj.getattrobj(refAttrName, 0);
				String refoid = structvalue.getobjectrefoid();
				int refsqlId = getObjSqlId(refoid); // TODO handle non unique
													// tids
				ps.setInt(valuei, refsqlId);
				valuei++;
			}
			
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
			    	AttributeDef lineattr=(AttributeDef)attri.next();
					valuei = addAttrValue(iomObj, ili2sqlName.mapItfLineTableAsTable(attrDef), sqlId, ps,
							valuei, lineattr);
			    }
			}

			if (createStdCols) {
				// T_LastChange
				ps.setTimestamp(valuei, today);
				valuei++;
				// T_CreateDate
				ps.setTimestamp(valuei, today);
				valuei++;
				// T_User
				ps.setString(valuei, dbusr);
				valuei++;
			}
			ps.executeUpdate();
		} finally {
			ps.close();
		}
		
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
	private void saveObjStat(int sqlImportId,String iliBasketId,String file,String topic) throws SQLException
	{
		for(String className : objStat.keySet()){
			ClassStat stat=objStat.get(className);
			writeImportStatDetail(sqlImportId,stat.getStartid(),stat.getEndid(),stat.getObjcount(),className);
		}
		// save it for later output to log
		basketStat.add(new BasketStat(file,topic,iliBasketId,objStat));
		// setup new collection
		objStat=new HashMap<String, ClassStat>();
	}

	private int writeImportStat(int datasetSqlId,String importFile,java.sql.Timestamp importDate,String importUsr)
	throws java.sql.SQLException,ConverterException
	{
		String sqlname=TransferFromIli.IMPORTS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+TransferFromIli.IMPORTS_TAB_DATASET
			+", "+TransferFromIli.IMPORTS_TAB_IMPORTDATE
			+", "+TransferFromIli.IMPORTS_TAB_IMPORTUSER
			+", "+TransferFromIli.IMPORTS_TAB_IMPORTFILE
			+") VALUES (?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			
			int key=newObjSqlId();
			ps.setInt(valuei, key);
			valuei++;
			
			ps.setInt(valuei, datasetSqlId);
			valuei++;

			ps.setTimestamp(valuei, importDate);
			valuei++;

			ps.setString(valuei, importUsr);
			valuei++;

			ps.setString(valuei, importFile);
			valuei++;
			
			ps.executeUpdate();
			
			return key;
		}finally{
			ps.close();
		}
		
	}
	private int writeImportBasketStat(int importSqlId,int basketSqlId,int startTid,int endTid,int objCount)
	throws java.sql.SQLException,ConverterException
	{
		String sqlname=TransferFromIli.IMPORTS_BASKETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+TransferFromIli.IMPORTS_BASKETS_TAB_IMPORT
			+", "+TransferFromIli.IMPORTS_BASKETS_TAB_BASKET
			+", "+TransferFromIli.IMPORTS_TAB_OBJECTCOUNT
			+", "+TransferFromIli.IMPORTS_TAB_STARTTID
			+", "+TransferFromIli.IMPORTS_TAB_ENDTID
			+") VALUES (?,?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			
			int key=newObjSqlId();
			ps.setInt(valuei, key);
			valuei++;
			
			ps.setInt(valuei, importSqlId);
			valuei++;

			ps.setInt(valuei, basketSqlId);
			valuei++;

			ps.setInt(valuei, objCount);
			valuei++;

			ps.setInt(valuei, startTid);
			valuei++;
			
			ps.setInt(valuei, endTid);
			valuei++;
			
			ps.executeUpdate();
			
			return key;
		}finally{
			ps.close();
		}
		
	}

	private void writeImportStatDetail(int importSqlId,int startTid,int endTid,int objCount,String importClassName)
	throws java.sql.SQLException
	{
		String sqlname=TransferFromIli.IMPORTS_OBJECTS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+TransferFromIli.IMPORTS_OBJECTS_TAB_IMPORT
			+", "+TransferFromIli.IMPORTS_OBJECTS_TAB_CLASS
			+", "+TransferFromIli.IMPORTS_TAB_OBJECTCOUNT
			+", "+TransferFromIli.IMPORTS_TAB_STARTTID
			+", "+TransferFromIli.IMPORTS_TAB_ENDTID
			+") VALUES (?,?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			
			ps.setInt(valuei, newObjSqlId());
			valuei++;
			
			ps.setInt(valuei, importSqlId);
			valuei++;

			ps.setString(valuei, importClassName);
			valuei++;

			ps.setInt(valuei, objCount);
			valuei++;

			ps.setInt(valuei, startTid);
			valuei++;
			
			ps.setInt(valuei, endTid);
			valuei++;
			
			ps.executeUpdate();
		}finally{
			ps.close();
		}
		
	}

	private int writeBasket(int datasetSqlId,StartBasketEvent iomBasket,int basketSqlId,String attachmentKey)
	throws java.sql.SQLException,ConverterException

	{
		String bid=iomBasket.getBid();
		String tag=iomBasket.getType();

		String sqlname=TransferFromIli.BASKETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+TransferFromIli.BASKETS_TAB_TOPIC
			+", "+TransferFromIli.T_ILI_TID
			+", "+TransferFromIli.BASKETS_TAB_ATTACHMENT_KEY
			+", "+TransferFromIli.BASKETS_TAB_DATASET
			+") VALUES (?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			ps.setInt(valuei, basketSqlId);
			valuei++;

			ps.setString(valuei, tag);
			valuei++;

			ps.setString(valuei, bid);
			valuei++;
			
			ps.setString(valuei, attachmentKey);
			valuei++;
			
			ps.setInt(valuei, datasetSqlId);
			valuei++;
			
			ps.executeUpdate();
		}finally{
			ps.close();
		}
		return basketSqlId;
}
	private int writeDataset(int datasetSqlId)
	throws java.sql.SQLException

	{

		String sqlname=TransferFromIli.DATASETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+") VALUES (?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			ps.setInt(valuei, datasetSqlId);
			valuei++;
			
			ps.executeUpdate();
		}finally{
			ps.close();
		}
		return datasetSqlId;
}
	private void enqueStructValue(int parentSqlId,String parentSqlType,String parentSqlAttr,IomObject struct,int structi,AttributeDef attr)
	{
		structQueue.add(new StructWrapper(parentSqlId,parentSqlType,parentSqlAttr,struct,structi,attr));
	}

	/** maps the xtfId to a sqlId.
	 */
	private int getObjSqlId(String xtfId){
		if(xtfId2sqlId.containsKey(xtfId)){
			return ((Integer)xtfId2sqlId.get(xtfId)).intValue();
		}
		int ret=newObjSqlId();
		xtfId2sqlId.put(xtfId,new Integer(ret));
		return ret;
	}
	/** gets a new obj id.
	 */
	int idBlockStart=0;
	int lastLocalId=0;
	private int newObjSqlId(){
		int ret;
		final int BLOCK_SIZE=20;
		if(lastLocalId!=0 && lastLocalId<BLOCK_SIZE){
			lastLocalId++;
			ret=idBlockStart+lastLocalId;
		}else{
			lastLocalId=1;
			ret=idBlockStart=idGen.getLastId("T_Id",schema)+lastLocalId;
			idGen.setLastId(idBlockStart+BLOCK_SIZE,"T_Id",schema);
		}
		return ret;
	}
	private int getLastSqlId()
	{
		return idBlockStart+lastLocalId;
	}
	/** creates an insert statement for a given viewable.
	 * @param sqlname table name of viewable
	 * @param aclass viewable
	 * @return insert statement
	 */
	private String createInsertStmt(String sqlname,Viewable aclass,StructWrapper structEle){
		StringBuffer ret = new StringBuffer();
		StringBuffer values = new StringBuffer();
		ret.append("INSERT INTO ");
		ret.append(sqlname);
		String sep=" (";

		// add T_Id
		ret.append(sep);
		sep=",";
		ret.append(colT_ID);
		values.append("?");
		
		// add T_basket
		if(createBasketCol){
			ret.append(sep);
			sep=",";
			ret.append(TransferFromIli.T_BASKET);
			values.append(",?");
		}
		
		// if root, add type
		if(aclass.getExtending()==null){
			if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(aclass)){
				ret.append(sep);
				sep=",";
				ret.append(TransferFromIli.T_TYPE);
				values.append(",?");				
			}
			// if Class
			if((aclass instanceof Table) && ((Table)aclass).isIdentifiable()){
				if(readIliTid){
					ret.append(sep);
					sep=",";
					ret.append(TransferFromIli.T_ILI_TID);
					values.append(",?");				
				}
			}
			// if STRUCTURE, add ref to parent
			if((aclass instanceof Table) && !((Table)aclass).isIdentifiable()){
				if(createGenericStructRef){
					ret.append(sep);
					sep=",";
					ret.append(TransferFromIli.T_PARENT_ID);
					values.append(",?");
					ret.append(sep);
					sep=",";
					ret.append(TransferFromIli.T_PARENT_TYPE);
					values.append(",?");
					// attribute name in parent class
					ret.append(sep);
					sep=",";
					ret.append(TransferFromIli.T_PARENT_ATTR);
					values.append(",?");
				}else{
					ret.append(sep);
					sep=",";
					ret.append(ili2sqlName.mapIliAttributeDefQualified(structEle.getParentAttr()));
					values.append(",?");
				}
				// seqeunce (not null if LIST)
				ret.append(sep);
				sep=",";
				ret.append(TransferFromIli.T_SEQ);
				values.append(",?");
			}
		}
		
		Iterator iter = aclass.getDefinedAttributesAndRoles2();
		while (iter.hasNext()) {
		   ViewableTransferElement obj = (ViewableTransferElement)iter.next();
		   if (obj.obj instanceof AttributeDef) {
			   AttributeDef attr = (AttributeDef) obj.obj;
			   sep = addAttrToInsertStmt(ret, values, sep, attr);
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
							values.append(",?");
					}
				 }else{
					 // TODO if(orderPos!=0){
					 ret.append(sep);
					 sep=",";
					 ret.append(roleName);
						values.append(",?");
				 }
			   }
			}
		}
		// stdcols
		if(createStdCols){
			ret.append(sep);
			sep=",";
			ret.append(TransferFromIli.T_LAST_CHANGE);
			values.append(",?");
			ret.append(sep);
			sep=",";
			ret.append(TransferFromIli.T_CREATE_DATE);
			values.append(",?");
			ret.append(sep);
			sep=",";
			ret.append(TransferFromIli.T_USER);
			values.append(",?");
		}

		ret.append(") VALUES (");
		ret.append(values);
		ret.append(")");
		return ret.toString();
	}

	private String addAttrToInsertStmt(
			StringBuffer ret, StringBuffer values, String sep, AttributeDef attr) {
		if(attr.getExtending()==null){
			Type type = attr.getDomainResolvingAliases();
			String attrSqlName=ili2sqlName.mapIliAttributeDef(attr);
			if (TransferFromIli.isBoolean(td,attr)) {
					ret.append(sep);
					sep = ",";
					ret.append(attrSqlName);
					values.append(",?");
			}else if (type instanceof CompositionType){
			}else if (type instanceof PolylineType){
				 ret.append(sep);
				 sep=",";
				 ret.append(attrSqlName);
					values.append(","+geomConv.getInsertValueWrapperPolyline("?",getSrsid(type)));
			 }else if(type instanceof SurfaceOrAreaType){
				 if(createItfLineTables){
				 }else{
					 ret.append(sep);
					 sep=",";
					 ret.append(attrSqlName);
						values.append(","+geomConv.getInsertValueWrapperSurface("?",getSrsid(type)));
				 }
				 if(createItfAreaRef){
					 if(type instanceof AreaType){
						 ret.append(sep);
						 sep=",";
						 ret.append(attrSqlName+TransferFromIli.ITF_MAINTABLE_GEOTABLEREF_SUFFIX);
						 values.append(","+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
					 }
				 }
			 }else if(type instanceof CoordType){
				 ret.append(sep);
				 sep=",";
				 ret.append(attrSqlName);
					values.append(","+geomConv.getInsertValueWrapperCoord("?",getSrsid(type)));
			}else if(type instanceof EnumerationType){
				ret.append(sep);
				sep = ",";
				ret.append(attrSqlName);
				values.append(",?");
				if(createEnumTxtCol){
					ret.append(sep);
					sep = ",";
					ret.append(attrSqlName+TransferFromIli.ENUM_TXT_COL_SUFFIX);
					values.append(",?");
				}
			}else{
				ret.append(sep);
				sep = ",";
				ret.append(attrSqlName);
				values.append(",?");
			}
		   }
		return sep;
	}
	private String createItfLineTableInsertStmt(AttributeDef attrDef) {
		SurfaceOrAreaType type = (SurfaceOrAreaType)attrDef.getDomainResolvingAliases();
		
		StringBuffer stmt = new StringBuffer();
		StringBuffer values = new StringBuffer();
		stmt.append("INSERT INTO ");
		String sqlTabName=ili2sqlName.mapItfLineTableAsTable(attrDef);
		if(schema!=null){
			sqlTabName=schema+"."+sqlTabName;
		}
		stmt.append(sqlTabName);
		String sep=" (";

		// add T_Id
		stmt.append(sep);
		sep=",";
		stmt.append(colT_ID);
		values.append("?");
		
		// add T_basket
		if(createBasketCol){
			stmt.append(sep);
			sep=",";
			stmt.append(TransferFromIli.T_BASKET);
			values.append(",?");
		}
		
		if(readIliTid){
			stmt.append(sep);
			sep=",";
			stmt.append(TransferFromIli.T_ILI_TID);
			values.append(",?");				
		}
		
		// POLYLINE
		 stmt.append(sep);
		 sep=",";
		 stmt.append(ili2sqlName.mapIliAttrName(attrDef,TransferFromIli.ITF_LINETABLE_GEOMATTR));
		values.append(","+geomConv.getInsertValueWrapperPolyline("?",getSrsid(type)));

		// -> mainTable
		if(type instanceof SurfaceType){
			stmt.append(sep);
			sep=",";
			stmt.append(ili2sqlName.mapIliAttrName(attrDef,TransferFromIli.ITF_LINETABLE_MAINTABLEREF));
			values.append(",?");
		}
		
		Table lineAttrTable=type.getLineAttributeStructure();
		if(lineAttrTable!=null){
		    Iterator attri = lineAttrTable.getAttributes ();
		    while(attri.hasNext()){
		    	AttributeDef lineattr=(AttributeDef)attri.next();
			   sep = addAttrToInsertStmt(stmt, values, sep, lineattr);
		    }
		}
		
		// stdcols
		if(createStdCols){
			stmt.append(sep);
			sep=",";
			stmt.append(TransferFromIli.T_LAST_CHANGE);
			values.append(",?");
			stmt.append(sep);
			sep=",";
			stmt.append(TransferFromIli.T_CREATE_DATE);
			values.append(",?");
			stmt.append(sep);
			sep=",";
			stmt.append(TransferFromIli.T_USER);
			values.append(",?");
		}

		stmt.append(") VALUES (");
		stmt.append(values);
		stmt.append(")");
		return stmt.toString();
	}
	private HashMap insertStmts=new HashMap();
	/** gets an insert statement for a given viewable. Creates only a new
	 *  statement if this is not yet seen sqlname.
	 * @param sqlname table name of viewable
	 * @param aclass viewable
	 * @return insert statement
	 */
	private String getInsertStmt(String sqlname,Viewable aclass,StructWrapper structEle){
		Object key=null;
		if(!createGenericStructRef && structEle!=null && aclass.getExtending()==null){
			key=structEle.getParentAttr();
		}else{
			key=sqlname;
		}
		if(insertStmts.containsKey(key)){
			return (String)insertStmts.get(key);
		}
		String stmt=createInsertStmt(sqlname,aclass,structEle);
		EhiLogger.traceBackendCmd(stmt);
		insertStmts.put(key,stmt);
		return stmt;
	}
	private int getSrsid(Type type){
		return defaultSrsid;
	}
	private int mapXtfCode2ItfCode(EnumerationType type,String xtfCode)
	{
		return Integer.parseInt(enumTypes.mapXtfCode2ItfCode(type, xtfCode));
	}	
}
