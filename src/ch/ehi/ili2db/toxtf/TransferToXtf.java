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

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.fromxtf.BasketStat;
import ch.ehi.ili2db.fromxtf.ClassStat;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PredefinedModel;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.TypeAlias;
import ch.interlis.ili2c.metamodel.TypeModel;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.iligml.Iligml10Writer;
import ch.interlis.iom_j.iligml.Iligml20Writer;
import ch.interlis.iom_j.itf.ItfWriter;
import ch.interlis.iom_j.itf.ModelUtilities;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxWriter;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.ValidationConfig;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 06.05.2005 $
 */
public class TransferToXtf {
	private NameMapping ili2sqlName=null;
	private TransferDescription td=null;
	private Connection conn=null;
	private String schema=null; // name of db schema or null
	private String colT_ID=null;
	private boolean createTypeDiscriminator=false;
	private boolean createGenericStructRef=false;
	private boolean writeIliTid=false;
	private SqlColumnConverter geomConv=null;
	private ToXtfRecordConverter recConv=null;
	private Viewable2TableMapping class2wrapper=null;
	private Config config=null;
	private ch.interlis.iox_j.validator.Validator validator=null;
	
	/** map of xml-elementnames to interlis classdefs.
	 *  Used to map typenames read from the T_TYPE column to the classdef.
	 */
	private HashMap tag2class=null; // map<String tag, Viewable classDef>
	private SqlidPool sqlidPool=new SqlidPool();
	private ArrayList<FixIomObjectRefs> delayedObjects=null;
	private ch.interlis.ili2c.generator.IndentPrintWriter expgen=null;
	public TransferToXtf(NameMapping ili2sqlName1,TransferDescription td1,Connection conn1,SqlColumnConverter geomConv,Config config,TrafoConfig trafoConfig,Viewable2TableMapping class2wrapper1){
		ili2sqlName=ili2sqlName1;
		td=td1;
		class2wrapper=class2wrapper1;
		tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
		conn=conn1;
		schema=config.getDbschema();
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		createTypeDiscriminator=config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		createGenericStructRef=config.STRUCT_MAPPING_GENERICREF.equals(config.getStructMapping());
		writeIliTid=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		this.geomConv=geomConv;
		recConv=new ToXtfRecordConverter(td,ili2sqlName,config,null,geomConv,conn,sqlidPool,trafoConfig,class2wrapper);
		this.config=config;

	}
	public void doit(String filename,IoxWriter iomFile,String sender,String exportParamModelnames[],long basketSqlIds[],HashSet<BasketStat> stat)
	throws ch.interlis.iox.IoxException
	{
		this.basketStat=stat;
		boolean referrs=false;
		if(config.isValidation()){
			ValidationConfig modelConfig=new ValidationConfig();
			modelConfig.mergeIliMetaAttrs(td);
			String configFilename=config.getValidConfigFile();
			if(configFilename!=null){
				try {
					modelConfig.mergeConfigFile(new File(configFilename));
				} catch (FileNotFoundException e) {
					EhiLogger.logError("validator config file <"+configFilename+"> not found");
				}
			}
			IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
			LogEventFactory errFactory=new LogEventFactory();
			errFactory.setDataSource(filename);
			if(iomFile instanceof ItfWriter){
				config.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES_DO);
			}else if(iomFile instanceof Iligml10Writer || iomFile instanceof Iligml20Writer){
				String crsAuthority=config.getDefaultSrsAuthority();
				String crsCode=config.getDefaultSrsCode();
				if(crsAuthority!=null && crsCode!=null){
					if(iomFile instanceof Iligml10Writer){
						((Iligml10Writer)iomFile).setDefaultCrs(crsAuthority+":"+crsCode);
					}else if(iomFile instanceof Iligml20Writer){
						((Iligml20Writer)iomFile).setDefaultCrs(crsAuthority+":"+crsCode);
					}
				}
			}
			validator=new ch.interlis.iox_j.validator.Validator(td,modelConfig, errHandler, errFactory, config);
			
		}
		
		StartTransferEvent startEvent=new StartTransferEvent();
		startEvent.setSender(sender);
		if(validator!=null)validator.validate(startEvent);
		iomFile.write(startEvent);
		if(basketSqlIds!=null){
			for(long basketSqlId : basketSqlIds){
				StringBuilder basketXtfId=new StringBuilder();
				Topic topic=getTopicByBasketId(basketSqlId,basketXtfId);
				if(topic==null){
					throw new IoxException("no basketId "+basketSqlId+" in db");
				}else{
					referrs = referrs || doBasket(filename, iomFile, topic,basketSqlId,basketXtfId.toString());				
				}
			}
			
		}else{
			// for all MODELs
			for(String modelName:exportParamModelnames)
			{
			  Object mObj = td.getElement(Model.class, modelName);
			  if (mObj!=null && (mObj instanceof Model) && !(suppressModel((Model)mObj)))
			  {
				Model model=(Model)mObj;
				// for all TOPICs
				Iterator topici=model.iterator();
				while(topici.hasNext()){
					Object tObj=topici.next();
					if (tObj instanceof Topic && !(suppressTopic((Topic)tObj))){
							Topic topic=(Topic)tObj;
							referrs = referrs || doBasket(filename, iomFile, topic,null,topic.getScopedName(null));
					}
				}
			  }
			}
		}
		if(referrs){
			throw new IoxException("dangling references");
		}
		EndTransferEvent endEvent=new EndTransferEvent();
		if(validator!=null)validator.validate(endEvent);
		iomFile.write(endEvent);
		if(validator!=null)validator.close();
	}
	private Topic getTopicByBasketId(long basketSqlId, StringBuilder basketXtfId) throws IoxException {
		
		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		String topicName=null;
		String bid=null;
		java.sql.PreparedStatement getstmt=null;
		try{
			String stmt="SELECT "+DbNames.BASKETS_TAB_TOPIC_COL+","+DbNames.T_ILI_TID_COL+" FROM "+sqlName+" WHERE "+colT_ID+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setLong(1,basketSqlId);
			java.sql.ResultSet res=getstmt.executeQuery();
			if(res.next()){
				topicName=res.getString(1);
				bid=res.getString(2);
			}
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to query "+sqlName,ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		if(topicName!=null){
			Topic topic=getTopicDef(td,topicName);
			if(topic==null){
				throw new IoxException("unkonw Topic "+topicName+" in table "+sqlName);
			}
			basketXtfId.append(bid);
			return topic;
		}
		return null;
	}
	public static Topic getTopicDef(TransferDescription td,String topicQName) {
		String modelName=null;
		String topicName=null;
		int endModelName=topicQName.indexOf('.');
		if(endModelName<=0){
			// just a topicname
			topicName=topicQName;
		}else{
			// qualified topicname; get model name
			modelName=topicQName.substring(0,endModelName);
			topicName=topicQName.substring(endModelName+1);
		}
			  Iterator modeli = td.iterator ();
			  while (modeli.hasNext ())
			  {
				Object mObj = modeli.next ();
				if(mObj instanceof Model){
				  Model model=(Model)mObj;
				  if(modelName==null || modelName.equals(model.getName())){
					  Iterator topici=model.iterator();
					  while(topici.hasNext()){
						Object tObj=topici.next();
						if (tObj instanceof Topic){
							Topic topic=(Topic)tObj;
							if(topicName.equals(topic.getName())){
								return topic;
							}
						}
					  }
				  }
				}
			  }
		return null;
	}
	private boolean doBasket(String filename, IoxWriter iomFile,Topic topic,Long basketSqlId,String basketXtfId) throws IoxException {
		Model model=(Model) topic.getContainer();
		boolean referrs=false;
		StartBasketEvent iomBasket=null;
		delayedObjects=new ArrayList<FixIomObjectRefs>();
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
			  if((obj instanceof View) && !TransferFromIli.isTransferableView(obj)){
				  // skip it
			  }else if (!suppressViewable ((Viewable)obj))
			  {
				Viewable aclass=(Viewable)obj;
				ViewableWrapper wrapper=class2wrapper.get(aclass);
				
				// get sql name
				DbTableName sqlName=recConv.getSqlType(wrapper.getViewable());
				// if table exists?
				if(DbUtility.tableExists(conn,sqlName)){
					// dump it
					EhiLogger.logState(aclass.getScopedName(null)+"...");
					if(iomBasket==null){
						iomBasket=new StartBasketEvent(topic.getScopedName(null),basketXtfId);
						if(validator!=null)validator.validate(iomBasket);
						iomFile.write(iomBasket);
					}
					dumpObject(iomFile,aclass,basketSqlId);
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
							if(validator!=null)validator.validate(iomBasket);
							iomFile.write(iomBasket);
						}
						dumpItfTableObject(iomFile,attr,basketSqlId);
					}else{
						// skip it
						EhiLogger.traceUnusualState(attr.getScopedName(null)+"...skipped; no table "+sqlName+" in db");
					}
				  
			  }
		  }
		}
		if(iomBasket!=null){
			// fix forward references
			for(FixIomObjectRefs fixref : delayedObjects){
				boolean skipObj=false;
				for(IomObject ref:fixref.getRefs()){
					long sqlid=fixref.getTargetSqlid(ref);
					if(sqlidPool.containsSqlid(sqlid)){
						// fix it
						ref.setobjectrefoid(sqlidPool.getXtfid(sqlid));
					}else{
						// object in another basket
						Viewable aclass=fixref.getTargetClass(ref);
						// read object
						String tid=readObjectTid(aclass,sqlid);
						if(tid==null){
							EhiLogger.logError("unknown referenced object "+aclass.getScopedName(null)+" sqlid "+fixref.getTargetSqlid(ref)+" referenced from "+fixref.getRoot().getobjecttag()+" TID "+fixref.getRoot().getobjectoid());
							referrs=true;
							skipObj=true;
						}else{
							// fix reference
							ref.setobjectrefoid(tid);
						}
					}
					
				}
				if(!skipObj){
					ObjectEvent objEvent=new ObjectEvent(fixref.getRoot());
					if(validator!=null)validator.validate(objEvent);
					iomFile.write(objEvent);
				}
			}
			EndBasketEvent endBasket=new EndBasketEvent();
			if(validator!=null)validator.validate(endBasket);
			iomFile.write(endBasket);
			saveObjStat(iomBasket.getBid(),filename,iomBasket.getType());
		}
		return referrs;
	}
	private String readObjectTid(Viewable aclass, long sqlid) {
		String sqlIliTid = null;
		if (writeIliTid || Ili2cUtility.isViewableWithOid(aclass)) {
			String stmt = createQueryStmt4xtfid(aclass);
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement dbstmt = null;
			try {

				dbstmt = conn.prepareStatement(stmt);
				dbstmt.clearParameters();
				dbstmt.setLong(1, sqlid);
				java.sql.ResultSet rs = dbstmt.executeQuery();
				if(rs.next()) {
					sqlIliTid = rs.getString(2);
					sqlidPool.putSqlid2Xtfid(sqlid, sqlIliTid);
				}else{
					// unknown object
					return null;
				}
			} catch (java.sql.SQLException ex) {
				EhiLogger.logError("failed to query " + aclass.getScopedName(null),	ex);
			} finally {
				if (dbstmt != null) {
					try {
						dbstmt.close();
					} catch (java.sql.SQLException ex) {
						EhiLogger.logError("failed to close query of "+ aclass.getScopedName(null), ex);
					}
				}
			}
		}else{
			sqlIliTid = Long.toString(sqlid);
			sqlidPool.putSqlid2Xtfid(sqlid, sqlIliTid);
		}
		return sqlIliTid;
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
	 * @throws IoxException 
	 */
	private void dumpStructs(StructWrapper structWrapper,FixIomObjectRefs fixref) throws IoxException
	{
		Viewable baseClass=((CompositionType)structWrapper.getParentAttr().getDomain()).getComponentType();

		HashMap<String,IomObject> structelev=new HashMap<String,IomObject>();
		HashSet<Viewable> structClassv=new HashSet<Viewable>();

		String stmt=createQueryStmt4Type(baseClass,structWrapper);
		EhiLogger.traceBackendCmd(stmt);
		java.sql.Statement dbstmt = null;
		try{
			
			dbstmt = conn.createStatement();
			java.sql.ResultSet rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String sqlid=rs.getString(colT_ID);
				String structEleClass=null;
				Viewable structClass=null;
				String structEleSqlType=rs.getString(DbNames.T_TYPE_COL);
				structEleClass=ili2sqlName.mapSqlTableName(structEleSqlType);
				if(structEleClass==null){
					throw new IoxException("unknown "+DbNames.T_TYPE_COL+" '"+structEleSqlType+"' in table "+getStructRootTableName(baseClass));
				}
				structClass=(Viewable)tag2class.get(structEleClass);
				IomObject iomObj=structWrapper.getParent().addattrobj(structWrapper.getParentAttr().getName(),structEleClass);
				structelev.put(sqlid,iomObj);
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
		
		Iterator<Viewable> classi=structClassv.iterator();
		while(classi.hasNext()){
			Viewable aclass=classi.next();
			dumpObjHelper(null,aclass,null,fixref,structWrapper,structelev);
		}
	}
	private void dumpItfTableObject(IoxWriter out,AttributeDef attr,Long basketSqlId)
	{
		String stmt=createItfLineTableQueryStmt(attr,basketSqlId,geomConv);
		String sqlTabName=ili2sqlName.mapGeometryAsTable(attr);
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
			int paramIdx=1;
			if(basketSqlId!=null){
				dbstmt.setLong(paramIdx++,basketSqlId);
			}
			java.sql.ResultSet rs=dbstmt.executeQuery();
			while(rs.next()){
				int valuei=1;
				long sqlid=rs.getLong(valuei);
				valuei++;
				
				String sqlIliTid=null;
				if(writeIliTid){
					sqlIliTid=rs.getString(valuei);
					valuei++;
				}else{
					sqlIliTid=Long.toString(sqlid);
				}
				
				Viewable aclass=(Viewable)attr.getContainer();

				Iom_jObject iomObj;
				iomObj=new Iom_jObject(aclass.getScopedName(null)+"_"+attr.getName(),sqlIliTid);
				
				// geomAttr
				Object geomobj=rs.getObject(valuei);
				valuei++;
				if(!rs.wasNull()){
					try{
					boolean is3D=false;
					IomObject polyline=geomConv.toIomPolyline(geomobj,ili2sqlName.getSqlColNameItfLineTableGeomAttr(attr,sqlTabName),is3D);
					iomObj.addattrobj(geomAttrName,polyline);
					}catch(ConverterException ex){
						EhiLogger.logError("Object "+sqlid+": failed to convert polyline",ex);
					}	
				}
				
				// is of type SURFACE?
				if(type instanceof SurfaceType){
					// -> mainTable
					IomObject ref=iomObj.addattrobj(refAttrName,"REF");
					long refSqlId=rs.getLong(valuei);
					if(sqlidPool.containsSqlid(refSqlId)){
						String refTid=sqlidPool.getXtfid(refSqlId);
						ref.setobjectrefoid(refTid);
					}else{
						EhiLogger.logError("unknown referenced object "+attr.getContainer().getScopedName(null)+" sqlid "+refSqlId+" referenced from "+sqlTabName+" "+colT_ID+" "+sqlid);
					}
					valuei++;
					
				}
				
				Table lineAttrTable=type.getLineAttributeStructure();
				if(lineAttrTable!=null){
				    Iterator attri = lineAttrTable.getAttributes ();
				    while(attri.hasNext()){
						AttributeDef lineattr=(AttributeDef)attri.next();
						valuei = recConv.addAttrValue(rs, valuei, sqlid, iomObj, lineattr,null,class2wrapper.get(lineAttrTable),null);
				    }
				}
				
				if(out!=null){
					// write object
					ObjectEvent objEvent=new ObjectEvent(iomObj);
					if(validator!=null)validator.validate(objEvent);
					out.write(objEvent);
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
	private void dumpObject(IoxWriter out,Viewable aclass,Long basketSqlId)
	{
		dumpObjHelper(out,aclass,basketSqlId,null,null,null);
	}
	/** helper to dump all objects/structvalues of a given class/structure.
	 */
	private void dumpObjHelper(IoxWriter out,Viewable aclass,Long basketSqlId,FixIomObjectRefs fixref,StructWrapper structWrapper,HashMap<String,IomObject> structelev)
	{
		String stmt=recConv.createQueryStmt(aclass,basketSqlId,structWrapper);
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement dbstmt = null;
		try{
			
			dbstmt = conn.prepareStatement(stmt);
			recConv.setStmtParams(dbstmt, basketSqlId, fixref, structWrapper);
			java.sql.ResultSet rs=dbstmt.executeQuery();
			while(rs.next()){
				// list of not yet processed struct attrs
				ArrayList<StructWrapper> structQueue=new ArrayList<StructWrapper>();
				long sqlid = recConv.getT_ID(rs);
				Iom_jObject iomObj=null;
				fixref=new FixIomObjectRefs();
				iomObj = recConv.convertRecord(rs, aclass, fixref, structWrapper,
						structelev, structQueue, sqlid);
				updateObjStat(iomObj.getobjecttag(), sqlid);
				// collect structvalues
				while(!structQueue.isEmpty()){
					StructWrapper wrapper=(StructWrapper)structQueue.remove(0);
					dumpStructs(wrapper,fixref);
				}
				if(structWrapper==null){
					if(!fixref.needsFixing() || out instanceof ItfWriter){
						// no forward references
						// write object
						ObjectEvent objEvent=new ObjectEvent(iomObj);
						if(validator!=null)validator.validate(objEvent);
						if(out!=null){
							out.write(objEvent);
						}
					}else{
						delayedObjects.add(fixref);
					}
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
			expgen.println("String stmt=\""+recConv.createQueryStmt(aclass,null,null)+"\";");
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
						expgen.println("iomObj=newObject(\""+aclass.getScopedName(null)+"\",mapId(\""+recConv.getSqlType(aclass)+"\",tid));");
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
							String sqlAttrName=ili2sqlName.mapIliAttributeDef(attr,recConv.getSqlType(aclass).getName(),null);
							Type type = attr.getDomain();
							if( (type instanceof TypeAlias) 
								&& Ili2cUtility.isBoolean(td,type)) {
									expgen.println("Boolean prop_"+attrName+"=Db2Xtf.getBoolean(rs,\""+sqlAttrName+"\","
										+(type.isMandatoryConsideringAliases()?"false":"true")
										+",recInfo,iomObj,\""+attrName+"\");");
							}else{
								type = attr.getDomainResolvingAliases();
								if (type instanceof CompositionType){
									// enque iomObj as parent
									//enqueueStructAttr(new StructWrapper(tid,attr,iomObj));
									CompositionType ct=(CompositionType)type;
									expgen.println("add"+ct.getComponentType().getName()+"(tid,\""+ili2sqlName.mapIliAttributeDef(attr,recConv.getSqlType(aclass).getName(),null)+"\",iomObj,\""+attr.getName()+"\");");
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
							String sqlRoleName=ili2sqlName.mapIliRoleDef(role,recConv.getSqlType(aclass).getName(),recConv.getSqlType(role.getDestination()).getName());
							// a role of an embedded association?
							if(obj.embedded){
								AssociationDef roleOwner = (AssociationDef) role.getContainer();
								if(roleOwner.getDerivedFrom()==null){
									 // TODO if(orderPos!=0){
									expgen.println("String prop_"+roleName+"=Db2Xtf.getRef(rs,\""+sqlRoleName+"\","
										+"true"
										+",recInfo,this,\""+recConv.getSqlType(role.getDestination())+"\",iomObj,\""+roleName+"\",\""+roleOwner.getScopedName(null)+"\");");
								}
							 }else{
								 // TODO if(orderPos!=0){
								expgen.println("String prop_"+roleName+"=Db2Xtf.getRef(rs,\""+sqlRoleName+"\","
									+"false"
									+",recInfo,this,\""+recConv.getSqlType(role.getDestination())+"\",iomObj,\""+roleName+"\",\"REF\");");
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
									expgen.println("addPendingObject(\""+oppEnd.getDestination().getScopedName(null)+"\",\""+ili2sqlName.mapIliRoleDef(roleThis,null,null)+"\",tid);");
								}else{
									expgen.println("addPendingObject(\""+((AssociationDef)(oppEnd.getContainer())).getScopedName(null)+"\",\""+ili2sqlName.mapIliRoleDef(roleThis,null,null)+"\",prop_"+roleThis.getName()+");");
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
	public static boolean suppressViewable (Viewable v)
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
	private DbTableName getSqlTableNameItfLineTable(AttributeDef def){
		String sqlname=ili2sqlName.mapGeometryAsTable(def);
		return new DbTableName(schema,sqlname);
	}
	private String createItfLineTableQueryStmt(AttributeDef attr,Long basketSqlId,SqlColumnConverter conv){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		if(writeIliTid){
			ret.append(", r0."+DbNames.T_ILI_TID_COL);
		}
		String sep=",";
		
		SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAliases();
		String sqlTabName=ili2sqlName.mapGeometryAsTable(attr);
		
		// geomAttr
		 ret.append(sep);
		 sep=",";
		 ret.append(conv.getSelectValueWrapperPolyline(ili2sqlName.getSqlColNameItfLineTableGeomAttr(attr,sqlTabName)));
		 //ret.append(ili2sqlName.mapIliAttrName(geomAttrName));

		 // is it of type SURFACE?
		 if(type instanceof SurfaceType){
			 // -> mainTable
			 ret.append(sep);
			 sep=",";
			 ret.append(ili2sqlName.getSqlColNameItfLineTableRefAttr(attr,sqlTabName));
		 }

			Table lineAttrTable=type.getLineAttributeStructure();
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
					AttributeDef lineattr=(AttributeDef)attri.next();
				   sep = recConv.addAttrToQueryStmt(ret, sep, lineattr,sqlTabName);
			    }
			}
		 
		ret.append(" FROM ");
		if(schema!=null){
			sqlTabName=schema+"."+sqlTabName;
		}
		ret.append(sqlTabName);
		ret.append(" r0");
		if(basketSqlId!=null){
			ret.append(" WHERE r0."+DbNames.T_BASKET_COL+"=?");
		}

		return ret.toString();
	}
	private String createQueryStmt4xtfid(Viewable aclass){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		ret.append(", r0."+DbNames.T_ILI_TID_COL);
		ret.append(" FROM ");
		ArrayList tablev=new ArrayList(10);
		tablev.add(aclass);
		Viewable base=(Viewable)aclass.getRootExtending();
		if(base==null){
			base=aclass;
		}
		ret.append(recConv.getSqlType(base));
		ret.append(" r0");
		ret.append(" WHERE r0."+colT_ID+"=?");
		return ret.toString();
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
			ret.append(recConv.getSqlType((Viewable)tablev.get(i)));
			sep=", ";
		}
		return ret.toString();
	}
	private String getStructRootTableName(Viewable aclass) {
		ViewableWrapper root=class2wrapper.get(aclass);
		while(root.getExtending()!=null){
			root=root.getExtending();
		}
		return root.getSqlTablename();
	}
	
	/** creates sql query statement for a structattr.
	 * @param aclass type of objects to build query for
	 * @param wrapper not null, if building query for struct values
	 * @return SQL-Query statement
	 */
	private String createQueryStmt4Type(Viewable aclass,StructWrapper wrapper){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		ret.append(", r0."+DbNames.T_TYPE_COL);
		ret.append(", r0."+DbNames.T_SEQ_COL);
		ret.append(" FROM (");
		
		// might have multiple tables!
		int tabidx=0;
		String subSelectSep="";
		for(ViewableWrapper root : recConv.getStructWrappers((Table)aclass)){
			tabidx++;
			String tabalias="r"+tabidx;
			ret.append(subSelectSep);
			ret.append("SELECT ");
			ret.append(tabalias+"."+colT_ID);
			if(createTypeDiscriminator || root.includesMultipleTypes()){
				ret.append(", "+tabalias+"."+DbNames.T_TYPE_COL);
			}else{
				ret.append(",'"+root.getSqlTablename()+"' AS "+DbNames.T_TYPE_COL);
				
			}
			ret.append(","+tabalias+"."+DbNames.T_SEQ_COL);
			ret.append(" FROM ");
			ret.append(recConv.getSqlType(root.getViewable()));
			ret.append(" "+tabalias);
			if(wrapper!=null){
				if(createGenericStructRef){
					ret.append(" WHERE "+tabalias+"."+DbNames.T_PARENT_ID_COL+"="+wrapper.getParentSqlId()+" AND "+tabalias+"."+DbNames.T_PARENT_ATTR_COL+"='"
							+ili2sqlName.mapIliAttributeDef(wrapper.getParentAttr(),recConv.getSqlType(wrapper.getParentTable().getViewable()).getName(),null));
				}else{
					ret.append(" WHERE "+tabalias+"."+ili2sqlName.mapIliAttributeDefReverse(wrapper.getParentAttr(),recConv.getSqlType(root.getViewable()).getName(),recConv.getSqlType(wrapper.getParentTable().getViewable()).getName())+"="+wrapper.getParentSqlId());
				}
			}
			subSelectSep=" UNION ";
		}
		ret.append(" ) AS r0 ORDER BY "+DbNames.T_SEQ_COL+" ASC");

		return ret.toString();
	}

	private HashSet<BasketStat> basketStat=null;
	private HashMap<String, ClassStat> objStat=new HashMap<String, ClassStat>();
	private void updateObjStat(String tag, long sqlId)
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
