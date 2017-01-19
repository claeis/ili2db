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
package ch.ehi.ili2db.fromili;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.sqlgen.repository.DbColBoolean;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbIndex;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ilirepository.IliFiles;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class TransferFromIli {
	private DbSchema schema=null;
	private HashSet<Element> visitedElements=null;
	private Viewable2TableMapping class2wrapper=null;
	private HashSet<ViewableWrapper> visitedWrapper=null;
	private HashSet visitedEnums=null;
	private TransferDescription td=null;
	private ch.ehi.ili2db.mapping.NameMapping ili2sqlName=null;
	private String createEnumTable=null;
	private boolean createStdCols=false;
	private boolean createIliTidCol=false;
	private boolean createBasketCol=false;
	private CustomMapping customMapping=null;
	private boolean createItfLineTables=false;
	private boolean createFk=false;
	private boolean createFkIdx=false;
	private boolean isIli1Model=false;
	private boolean deleteExistingData=false;
	private String colT_ID=null;
	private String nl=System.getProperty("line.separator");
	private FromIliRecordConverter recConv=null;
	public DbSchema doit(TransferDescription td1,java.util.List<Element> modelEles,ch.ehi.ili2db.mapping.NameMapping ili2sqlName,ch.ehi.ili2db.gui.Config config,DbIdGen idGen,TrafoConfig trafoConfig,Viewable2TableMapping class2wrapper1,CustomMapping customMapping1)
	throws Ili2dbException
	{
		this.ili2sqlName=ili2sqlName;
		createEnumTable=config.getCreateEnumDefs();
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createFk=config.CREATE_FK_YES.equals(config.getCreateFk());
		createFkIdx=config.CREATE_FKIDX_YES.equals(config.getCreateFkIdx());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		deleteExistingData=config.DELETE_DATA.equals(config.getDeleteMode());
		if(deleteExistingData){
			EhiLogger.logState("delete existing data...");
		}
		
		createIliTidCol=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		createBasketCol=config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		
		isIli1Model=td1.getIli1Format()!=null;
		createItfLineTables=isIli1Model && config.getDoItfLineTables();
		
		customMapping=customMapping1;
		customMapping.fromIliInit(config);

		schema=new DbSchema();
		schema.setName(config.getDbschema());
		visitedElements=new HashSet<Element>();
		class2wrapper=class2wrapper1;
		visitedEnums=new HashSet();
		td=td1;
		recConv=new FromIliRecordConverter(td,ili2sqlName,config,schema,customMapping,idGen,visitedEnums,trafoConfig,class2wrapper);

		visitedWrapper=new HashSet<ViewableWrapper>();
		generatModelEles(modelEles,1);
		visitedWrapper=new HashSet<ViewableWrapper>();
		generatModelEles(modelEles,2);
		
		// sys_interlisnames
		// interlis LONGVARCHAR(767)
		// db VARCHAR(30)
		
		customMapping.fromIliEnd(config);
		return schema;		

	}
	private void generatModelEles(java.util.List<Element> modelEles, int pass)
			throws Ili2dbException {
		Iterator modeli=modelEles.iterator();
		while(modeli.hasNext()){
			Object modelo=modeli.next();
			if(modelo instanceof Model){
				Model model=(Model)modelo;
				//generateModel(model);
			}else if (modelo instanceof Topic){
				//generateTopic((Topic)modelo);
			}else if (modelo instanceof Domain){
				if(pass==2){
					generateDomain((Domain)modelo);
					visitedElements.add((Domain)modelo);
				}
			}else if (modelo instanceof Viewable){
				if(modelo instanceof Table && ((Table)modelo).isIli1LineAttrStruct()){
					// skip it
				}else if((modelo instanceof View) && !isTransferableView(modelo)){
					// skip it
				}else{
					try{
						ViewableWrapper wrapper=class2wrapper.get((Viewable)modelo);
						if(wrapper!=null){
							generateViewable(wrapper,pass);
						}
						if(pass==2){
							visitedElements.add((Viewable)modelo);
						}
					}catch(Ili2dbException ex){
						throw new Ili2dbException("mapping of "+((Viewable)modelo).getScopedName(null)+" failed",ex);
					}
				}
			}else if (modelo instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)modelo;
				if(attr.getDomainResolvingAll() instanceof SurfaceOrAreaType){
					generateItfLineTable(attr,pass);
				}else if(attr.getDomainResolvingAll() instanceof EnumerationType){
					if(pass==2){
						visitedEnums.add(attr);
					}
				}else{
					// skip it
				}
			}else{
				// skip it
			}
		}
	}
	public static boolean isTransferableView(Object modelo) {
		if(!(modelo instanceof View)){
			return false;
		}
		View view=(View) modelo;
		Topic parent=(Topic)view.getContainer();
		if(!parent.isViewTopic()){
			return false;
		}
		if(view.isTransient()){
			return false;
		}
		return true;
	}

	private void generateDomain(Domain def)
	throws Ili2dbException
	{
		if(def.getType() instanceof EnumerationType){
			visitedEnums.add(def);
		}
	}
	private void generateViewable(ViewableWrapper def,int pass)
	throws Ili2dbException
	{
		if(def.getViewable() instanceof AssociationDef){
			AssociationDef assoc=(AssociationDef)def.getViewable();
			if(assoc.getDerivedFrom()!=null){
				return;
			}
			if(assoc.isLightweight() 
				&& !assoc.getAttributes().hasNext()
				&& !assoc.getLightweightAssociations().iterator().hasNext()) {
				if(pass==1){
					customMapping.fixupViewable(null,def.getViewable());
				}
				return;
			}
		}
		
		EhiLogger.traceState("wrapper of viewable "+def.getViewable());
		if(!visitedWrapper.contains(def)){
			visitedWrapper.add(def);
			recConv.generateTable(def,pass);
			for(ViewableWrapper secondary:def.getSecondaryTables()){
				recConv.generateTable(secondary,pass);
			}
			
		  	if(false && createItfLineTables){
		  		for(AttributeDef attr : recConv.getSurfaceAttrs()){
		  			generateItfLineTable(attr,pass);
		  		}
		  	}
		}
	}
	private void generateItfLineTable(AttributeDef attr,int pass)
	throws Ili2dbException
	{
		if(pass==1){
			DbTableName sqlName=getSqlTableNameItfLineTable(attr);
			DbTable dbTable=new DbTable();
			dbTable.setName(sqlName);
			dbTable.setIliName(attr.getContainer().getScopedName(null)+"."+attr.getName());
		  	schema.addTable(dbTable);
			return;
		}
		// second pass; add columns
		DbTableName sqlName=getSqlTableNameItfLineTable(attr);
		DbTable dbTable=schema.findTable(sqlName);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+attr.getContainer().getScopedName(null)+"."+attr.getName());
		cmtSep=nl;
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
		}
		if(deleteExistingData){
			dbTable.setDeleteDataIfTableExists(true);
		}
		  dbTable.setRequiresSequence(true);
		DbColId dbColId=recConv.addKeyCol(dbTable);
		  if(createIliTidCol){
				recConv.addIliTidCol(dbTable,null);
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
			SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAll();
			
			DbColGeometry dbCol = recConv.generatePolylineType(type, attr.getContainer().getScopedName(null)+"."+attr.getName());
			  dbCol.setName(ili2sqlName.getSqlColNameItfLineTableGeomAttr(attr,sqlName.getName()));
			  dbCol.setNotNull(true);
			  dbTable.addColumn(dbCol);
			
			if(type instanceof SurfaceType){
				  dbColId=new DbColId();
				  dbColId.setName(ili2sqlName.getSqlColNameItfLineTableRefAttr(attr,sqlName.getName()));
				  dbColId.setNotNull(true);
				  dbColId.setPrimaryKey(false);
				  dbColId.setScriptComment("REFERENCES "+recConv.getSqlType((Viewable)attr.getContainer()));
				  if(createFk){
					  dbColId.setReferencedTable(recConv.getSqlType((Viewable)attr.getContainer()));
				  }
					if(createFkIdx){
						dbColId.setIndex(true);
					}
				  dbTable.addColumn(dbColId);
			}
			
			Table lineAttrTable=type.getLineAttributeStructure();
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
			    	AttributeDef lineattr=(AttributeDef)attri.next();
			    	recConv.generateAttr(dbTable,lineAttrTable,lineattr);
			    }
			}
		
			  if(createStdCols){
					AbstractRecordConverter.addStdCol(dbTable);
			  }
	}
	/** setup a mapping from a qualified model or topic name
	 * to the corresponding java object.
	 */
	private HashMap setupTopicTagMap(TransferDescription td){
		HashMap ret=new HashMap();
		Iterator modeli = td.iterator ();
		while (modeli.hasNext ())
		{
		  Object mObj = modeli.next ();
		  if(mObj instanceof Model){
			Model model=(Model)mObj;
			ret.put(model.getScopedName(null),model);
			Iterator topici=model.iterator();
			while(topici.hasNext()){
				Object tObj=topici.next();
				if (tObj instanceof Topic){
				  	Topic topic=(Topic)tObj;
   					ret.put(topic.getScopedName(null),topic);
				}
			}
		  }
		}
		return ret;
	}
	private DbTableName getSqlTableName(Domain def){
		String sqlname=ili2sqlName.mapIliDomainDef(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbTableName getSqlTableNameEnum(AttributeDef def){
		String sqlname=ili2sqlName.mapIliEnumAttributeDefAsTable(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbTableName getSqlTableNameItfLineTable(AttributeDef def){
		String sqlname=ili2sqlName.mapGeometryAsTable(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	static public void addModelsTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.MODELS_TAB));
		DbColVarchar fileCol=new DbColVarchar();
		fileCol.setName(DbNames.MODELS_TAB_FILE_COL);
		fileCol.setNotNull(true);
		fileCol.setSize(250);
		tab.addColumn(fileCol);
		DbColVarchar iliversionCol=new DbColVarchar();
		iliversionCol.setName(DbNames.MODELS_TAB_ILIVERSION_COL);
		iliversionCol.setNotNull(true);
		iliversionCol.setSize(3);
		tab.addColumn(iliversionCol);
		DbColVarchar importsCol=new DbColVarchar();
		importsCol.setName(DbNames.MODELS_TAB_MODELNAME_COL);
		importsCol.setNotNull(true);
		importsCol.setSize(DbColVarchar.UNLIMITED);
		tab.addColumn(importsCol);
		DbColVarchar contentCol=new DbColVarchar();
		contentCol.setName(DbNames.MODELS_TAB_CONTENT_COL);
		contentCol.setNotNull(true);
		contentCol.setSize(DbColVarchar.UNLIMITED);
		tab.addColumn(contentCol);
		DbColDateTime importDateCol=new DbColDateTime();
		importDateCol.setName(DbNames.MODELS_TAB_IMPORTDATE_COL);
		importDateCol.setNotNull(true);
		tab.addColumn(importDateCol);
		DbIndex pk=new DbIndex();
		pk.setPrimary(true);
		pk.addAttr(importsCol);
		pk.addAttr(iliversionCol);
		tab.addIndex(pk);
		schema.addTable(tab);
	}
	public static ch.interlis.ilirepository.IliFiles readIliFiles(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String sqlName=DbNames.MODELS_TAB;
		if(!DbUtility.tableExists(conn,new DbTableName(schema,sqlName))){
			return null;
		}
		ch.interlis.ilirepository.IliFiles ret=new ch.interlis.ilirepository.IliFiles();
		try{
			String reposUri=conn.getMetaData().getURL();
			if(schema!=null){
				sqlName=schema+"."+sqlName;
				reposUri=reposUri+"/"+schema;
			}
			// select entries
			String insStmt="SELECT "+DbNames.MODELS_TAB_FILE_COL+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			try{
				java.sql.ResultSet rs=insPrepStmt.executeQuery();
				while(rs.next()){
					String file=rs.getString(1);
					double iliversion=Double.parseDouble(rs.getString(2));
					String imports=rs.getString(3);
					ch.interlis.ili2c.modelscan.IliFile iliFile=IliImportsUtility.parseIliImports(iliversion, imports);
					iliFile.setPath(file);
					iliFile.setRepositoryUri(reposUri);
					ret.addFile(iliFile);
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read IliFiles from db",ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return ret;
	}
	public static String readIliFile(java.sql.Connection conn,String schema,String filename)
	throws Ili2dbException
	{
		String sqlName=DbNames.MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			// select entries
			String selStmt="SELECT "+DbNames.MODELS_TAB_CONTENT_COL+" FROM "+sqlName+" WHERE "+DbNames.MODELS_TAB_FILE_COL+"=?";
			EhiLogger.traceBackendCmd(selStmt);
			java.sql.PreparedStatement selPrepStmt = conn.prepareStatement(selStmt);
			try{
				selPrepStmt.clearParameters();
				selPrepStmt.setString(1, filename);
				java.sql.ResultSet rs=selPrepStmt.executeQuery();
				while(rs.next()){
					String file=rs.getString(1);
					return file;
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read ili-file <"+filename+"> from db",ex);
			}finally{
				selPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return null;
	}
	public static void addModels(java.sql.Connection conn,TransferDescription td,String schema)
	throws Ili2dbException
	{
		// read existing models from db
		IliFiles iliModelsInDb = TransferFromIli.readIliFiles(conn,schema);

		String sqlName=DbNames.MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.Timestamp today=new java.sql.Timestamp(System.currentTimeMillis());

		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+DbNames.MODELS_TAB_FILE_COL+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+","+DbNames.MODELS_TAB_CONTENT_COL+","+DbNames.MODELS_TAB_IMPORTDATE_COL+") VALUES (?,?,?,?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			java.util.Iterator entri=td.iterator();
			HashMap<java.io.File,ch.interlis.ili2c.modelscan.IliFile> ilifiles=new HashMap<java.io.File,ch.interlis.ili2c.modelscan.IliFile>();
			while(entri.hasNext()){
				Object entro=entri.next();
				if(entro instanceof ch.interlis.ili2c.metamodel.Model){
					if(entro instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
						continue;
					}
					ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)entro;
					java.io.File file=new java.io.File(model.getFileName());
					ch.interlis.ili2c.modelscan.IliFile ilifile=null;
					if(ilifiles.containsKey(file)){
						ilifile=ilifiles.get(file);
					}else{
						ilifile=new ch.interlis.ili2c.modelscan.IliFile();
						ilifile.setFilename(file);
						ilifiles.put(file,ilifile);
					}
					ch.interlis.ili2c.modelscan.IliModel ilimodel=new ch.interlis.ili2c.modelscan.IliModel();
					ilimodel.setIliVersion(Double.parseDouble(model.getIliVersion()));
					ilimodel.setName(model.getName());
					Model imports[]=model.getImporting();
					for(Model importm : imports){
						ilimodel.addDepenedency(importm.getName());
					}
					ilifile.addModel(ilimodel);
				}
			}
			try{
				for(ch.interlis.ili2c.modelscan.IliFile ilifile: ilifiles.values()){
					ch.interlis.ili2c.modelscan.IliModel ilimodel=(ch.interlis.ili2c.modelscan.IliModel)ilifile.iteratorModel().next();
					if(iliModelsInDb==null || iliModelsInDb.getFileWithModel(ilimodel.getName(), ilimodel.getIliVersion())==null){
						insPrepStmt.clearParameters();
						insPrepStmt.setString(1, ilifile.getFilename().getName());
						insPrepStmt.setString(2, Double.toString(ilifile.getIliVersion()));
						insPrepStmt.setString(3, IliImportsUtility.getIliImports(ilifile));
						insPrepStmt.setString(4, readFileAsString(ilifile.getFilename()));
						insPrepStmt.setTimestamp(5, today);
						
						insPrepStmt.executeUpdate();
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert model",ex);
			} catch (IOException e) {
				throw new Ili2dbException("failed to update models-table "+sqlName,e);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update models-table "+sqlName,ex);
		}

	}
	public static String readFileAsString(java.io.File filePath) throws java.io.IOException {
	    java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(filePath));
	    try {
	        long len = filePath.length();
	        if (len > Integer.MAX_VALUE) throw new java.io.IOException("File "+filePath+" too large, was "+len+" bytes.");
	        byte[] bytes = new byte[(int) len];
	        dis.read(bytes);
	        return new String(bytes, "UTF-8");
	    } finally {
	        dis.close();
	    }
	}

	static public void addTrafoConfigTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.TRAFO_TAB));
		ch.ehi.sqlgen.repository.DbColVarchar iliName=new ch.ehi.sqlgen.repository.DbColVarchar();
		iliName.setName(DbNames.TRAFO_TAB_ILINAME_COL);
		iliName.setNotNull(true);
		iliName.setSize(1024);
		//iliClassName.setPrimaryKey(true);
		tab.addColumn(iliName);
		ch.ehi.sqlgen.repository.DbColVarchar configCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		configCol.setName(DbNames.TRAFO_TAB_TAG_COL);
		configCol.setNotNull(true);
		configCol.setSize(1024);
		tab.addColumn(configCol);
		ch.ehi.sqlgen.repository.DbColVarchar valCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		valCol.setName(DbNames.TRAFO_TAB_SETTING_COL);
		valCol.setNotNull(true);
		valCol.setSize(1024);
		tab.addColumn(valCol);
		schema.addTable(tab);
	}
	static public void addSettingsTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.SETTINGS_TAB));
		DbColVarchar tagCol=new DbColVarchar();
		tagCol.setName(DbNames.SETTINGS_TAB_TAG_COL);
		tagCol.setNotNull(true);
		tagCol.setPrimaryKey(true);
		tagCol.setSize(60);
		tab.addColumn(tagCol);
		DbColVarchar settingCol=new DbColVarchar();
		settingCol.setName(DbNames.SETTINGS_TAB_SETTING_COL);
		settingCol.setNotNull(false);
		settingCol.setSize(255);
		tab.addColumn(settingCol);
		schema.addTable(tab);
	}
	public static void readSettings(java.sql.Connection conn,Config settings,String schema)
	throws Ili2dbException
	{
		String sqlName=DbNames.SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(DbUtility.tableExists(conn,new DbTableName(schema,DbNames.SETTINGS_TAB))){
			try{
				// select entries
				String insStmt="SELECT "+DbNames.SETTINGS_TAB_TAG_COL+","+DbNames.SETTINGS_TAB_SETTING_COL+" FROM "+sqlName;
				EhiLogger.traceBackendCmd(insStmt);
				java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
				boolean settingsExists=false;
				try{
					java.sql.ResultSet rs=insPrepStmt.executeQuery();
					while(rs.next()){
						String tag=rs.getString(1);
						String value=rs.getString(2);
						if(tag.equals(Config.SENDER))continue;
						settings.setValue(tag,value);
						settingsExists=true;
					}
					if(settingsExists){
						settings.setConfigReadFromDb(true);
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to read setting",ex);
				}finally{
					insPrepStmt.close();
				}
			}catch(java.sql.SQLException ex){		
				throw new Ili2dbException("failed to read settings-table "+sqlName,ex);
			}
		}
	}
	public static void updateSettings(java.sql.Connection conn,Config settings,String schema)
	throws Ili2dbException
	{

		String sqlName=DbNames.SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+DbNames.SETTINGS_TAB_TAG_COL+","+DbNames.SETTINGS_TAB_SETTING_COL+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			try{
				java.util.Iterator entri=settings.getValues().iterator();
				while(entri.hasNext()){
					String tag=(String)entri.next();
					insPrepStmt.clearParameters();
					insPrepStmt.setString(1, tag);
					insPrepStmt.setString(2, settings.getValue(tag));
					insPrepStmt.executeUpdate();
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert setting",ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update settings-table "+sqlName,ex);
		}

	}
	
	static public void addInheritanceTable(DbSchema schema,int sqlNameSize)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.INHERIT_TAB));
		DbColVarchar thisClass=new DbColVarchar();
		thisClass.setName(DbNames.INHERIT_TAB_THIS_COL);
		thisClass.setNotNull(true);
		thisClass.setPrimaryKey(true);
		thisClass.setSize(1024);
		tab.addColumn(thisClass);
		DbColVarchar baseClass=new DbColVarchar();
		baseClass.setName(DbNames.INHERIT_TAB_BASE_COL);
		baseClass.setNotNull(false);
		baseClass.setSize(1024);
		tab.addColumn(baseClass);
		schema.addTable(tab);
	}
	public void addBasketsTable(DbSchema schema)
	{
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.BASKETS_TAB));
			
			// primary key
			recConv.addKeyCol(tab);
			
			// optional reference to dataset table
			DbColId dbColDataset=new DbColId();
			dbColDataset.setName(DbNames.BASKETS_TAB_DATASET_COL);
			dbColDataset.setNotNull(false);
			dbColDataset.setPrimaryKey(false);
			if(createFk){
				dbColDataset.setReferencedTable(new DbTableName(schema.getName(),DbNames.DATASETS_TAB));
			}
			if(createFkIdx){
				dbColDataset.setIndex(true);
			}
			tab.addColumn(dbColDataset);
			
			// qualified name of ili topic
			DbColVarchar thisClass=new DbColVarchar();
			thisClass.setName(DbNames.BASKETS_TAB_TOPIC_COL);
			thisClass.setNotNull(true);
			thisClass.setSize(200);
			tab.addColumn(thisClass);

			// basket id as read from xtf
			recConv.addIliTidCol(tab,null); 
			
			// name of subdirectory in attachments folder
			DbColVarchar attkey=new DbColVarchar();
			attkey.setName(DbNames.BASKETS_TAB_ATTACHMENT_KEY_COL);
			attkey.setNotNull(true);
			attkey.setSize(200);
			tab.addColumn(attkey);
			
			schema.addTable(tab);
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.DATASETS_TAB));
			
			// primary key
			recConv.addKeyCol(tab);
			
			// name of dataset
			DbColVarchar dsNameCol=new DbColVarchar();
			dsNameCol.setName(DbNames.DATASETS_TAB_DATASETNAME);
			dsNameCol.setNotNull(false);
			dsNameCol.setSize(200);
			tab.addColumn(dsNameCol);

			DbIndex dbIndex=new DbIndex();
			dbIndex.setPrimary(false);
			dbIndex.setUnique(true);
			dbIndex.addAttr(dsNameCol);
			tab.addIndex(dbIndex);
			
			schema.addTable(tab);

			
		}
	}
	public void addImportsTable(DbSchema schema)
	{
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.IMPORTS_TAB));
			tab.setComment(DbNames.DEPRECATED);
			
			recConv.addKeyCol(tab);
			
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(DbNames.IMPORTS_TAB_DATASET_COL);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+DbNames.DATASETS_TAB);
			if(false && createFk){
				// do not create ref so that entry in dataset table can be deleted without deleting import stat
				dbColBasket.setReferencedTable(new DbTableName(schema.getName(),DbNames.DATASETS_TAB));
			}
			if(createFkIdx){
				dbColBasket.setIndex(true);
			}
			tab.addColumn(dbColBasket);
			
			DbColDateTime dbColImpDate=new DbColDateTime();
			dbColImpDate.setName(DbNames.IMPORTS_TAB_IMPORTDATE_COL);
			dbColImpDate.setNotNull(true);
			tab.addColumn(dbColImpDate);
		
			DbColVarchar dbColUsr=new DbColVarchar();
			dbColUsr.setName(DbNames.IMPORTS_TAB_IMPORTUSER_COL);
			dbColUsr.setNotNull(true);
			dbColUsr.setSize(40);
			tab.addColumn(dbColUsr);
			
			DbColVarchar dbColFile=new DbColVarchar();
			dbColFile.setName(DbNames.IMPORTS_TAB_IMPORTFILE_COL);
			dbColFile.setNotNull(false); // NULLable so that delete can only be logged
			dbColFile.setSize(200);
			tab.addColumn(dbColFile);
			
			schema.addTable(tab);
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.IMPORTS_BASKETS_TAB));
			tab.setComment(DbNames.DEPRECATED);
			
			recConv.addKeyCol(tab);
			
			DbColId dbColImport=new DbColId();
			dbColImport.setName(DbNames.IMPORTS_BASKETS_TAB_IMPORT_COL);
			dbColImport.setNotNull(true);
			dbColImport.setScriptComment("REFERENCES "+DbNames.IMPORTS_TAB);
			if(createFk){
				dbColImport.setReferencedTable(new DbTableName(schema.getName(),DbNames.IMPORTS_TAB));
			}
			if(createFkIdx){
				dbColImport.setIndex(true);
			}
			tab.addColumn(dbColImport);
			
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(DbNames.IMPORTS_BASKETS_TAB_BASKET_COL);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+DbNames.BASKETS_TAB);
			if(createFk){
				dbColBasket.setReferencedTable(new DbTableName(schema.getName(),DbNames.BASKETS_TAB));
			}
			if(createFkIdx){
				dbColBasket.setIndex(true);
			}
			tab.addColumn(dbColBasket);
						
			DbColNumber dbColObjc=new DbColNumber();
			dbColObjc.setName(DbNames.IMPORTS_TAB_OBJECTCOUNT_COL);
			dbColObjc.setNotNull(false);
			tab.addColumn(dbColObjc);

			DbColId dbColStartId=new DbColId();
			dbColStartId.setName(DbNames.IMPORTS_TAB_STARTTID_COL);
			dbColStartId.setNotNull(false);
			tab.addColumn(dbColStartId);

			DbColId dbColEndId=new DbColId();
			dbColEndId.setName(DbNames.IMPORTS_TAB_ENDTID_COL);
			dbColEndId.setNotNull(false);
			tab.addColumn(dbColEndId);

			schema.addTable(tab);
			
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DbNames.IMPORTS_OBJECTS_TAB));
			tab.setComment(DbNames.DEPRECATED);
			
			recConv.addKeyCol(tab);
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(DbNames.IMPORTS_OBJECTS_TAB_IMPORT_COL);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+DbNames.IMPORTS_BASKETS_TAB);
			tab.addColumn(dbColBasket);
					
			// qualified name of ili class
			DbColVarchar dbColClass=new DbColVarchar();
			dbColClass.setName(DbNames.IMPORTS_OBJECTS_TAB_CLASS_COL);
			dbColClass.setNotNull(true);
			dbColClass.setSize(200);
			tab.addColumn(dbColClass);
						
			DbColNumber dbColObjc=new DbColNumber();
			dbColObjc.setName(DbNames.IMPORTS_TAB_OBJECTCOUNT_COL);
			dbColObjc.setNotNull(false);
			tab.addColumn(dbColObjc);

			DbColId dbColStartId=new DbColId();
			dbColStartId.setName(DbNames.IMPORTS_TAB_STARTTID_COL);
			dbColStartId.setNotNull(false);
			tab.addColumn(dbColStartId);

			DbColId dbColEndId=new DbColId();
			dbColEndId.setName(DbNames.IMPORTS_TAB_ENDTID_COL);
			dbColEndId.setNotNull(false);
			tab.addColumn(dbColEndId);

			schema.addTable(tab);
		}
	}
	public void addEnumTable(DbSchema schema)
	{
		if(Config.CREATE_ENUM_DEFS_SINGLE.equals(createEnumTable)){
			DbTable tab=new DbTable();
			DbColVarchar thisClass=new DbColVarchar();
			thisClass.setName(DbNames.ENUM_TAB_THIS_COL);
			thisClass.setNotNull(true);
			thisClass.setSize(1024);
			tab.addColumn(thisClass);
			DbColVarchar baseClass=new DbColVarchar();
			baseClass.setName(DbNames.ENUM_TAB_BASE_COL);
			baseClass.setNotNull(false);
			baseClass.setSize(1024);
			tab.addColumn(baseClass);
			DbColNumber seq=new DbColNumber();
			seq.setName(DbNames.ENUM_TAB_SEQ_COL);
			seq.setNotNull(false);
			seq.setSize(4);
			tab.addColumn(seq);
			DbColBoolean inactiveCol=new DbColBoolean();
			inactiveCol.setName(DbNames.ENUM_TAB_INACTIVE_COL);
			inactiveCol.setNotNull(true);
			tab.addColumn(inactiveCol);
			DbColVarchar iliCode=new DbColVarchar();
			iliCode.setName(DbNames.ENUM_TAB_ILICODE_COL);
			iliCode.setNotNull(true);
			iliCode.setSize(1024);
			tab.addColumn(iliCode);
			tab.setName(new DbTableName(schema.getName(),DbNames.ENUM_TAB));
			DbColNumber itfCode=new DbColNumber();
			itfCode.setName(DbNames.ENUM_TAB_ITFCODE_COL);
			itfCode.setNotNull(true);
			itfCode.setSize(4);
			tab.addColumn(itfCode);
			DbColVarchar dispName=new DbColVarchar();
			dispName.setName(DbNames.ENUM_TAB_DISPNAME_COL);
			dispName.setNotNull(true);
			dispName.setSize(250);
			tab.addColumn(dispName);
			schema.addTable(tab);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			java.util.Iterator entri=visitedEnums.iterator();
			while(entri.hasNext()){
				Object entro=entri.next();
				DbTableName thisSqlName=null;
				if(entro instanceof AttributeDef){
					AttributeDef attr=(AttributeDef)entro;
					attr.getDomain();
					
					thisSqlName=getSqlTableNameEnum(attr);
					
				}else if(entro instanceof Domain){
					Domain domain=(Domain)entro;
					domain.getType();
					
					thisSqlName=getSqlTableName(domain);
				}
				if(thisSqlName!=null){
					DbTable tab=new DbTable();
					tab.setName(thisSqlName);
					DbColNumber itfCode=new DbColNumber();
					itfCode.setName(DbNames.ENUM_TAB_ITFCODE_COL);
					itfCode.setNotNull(true);
					itfCode.setSize(4);
					itfCode.setPrimaryKey(true);
					tab.addColumn(itfCode);
					DbColVarchar iliCode=new DbColVarchar();
					iliCode.setName(DbNames.ENUM_TAB_ILICODE_COL);
					iliCode.setNotNull(true);
					iliCode.setSize(1024);
					tab.addColumn(iliCode);
					DbColNumber seq=new DbColNumber();
					seq.setName(DbNames.ENUM_TAB_SEQ_COL);
					seq.setNotNull(false);
					seq.setSize(4);
					tab.addColumn(seq);
					DbColBoolean inactiveCol=new DbColBoolean();
					inactiveCol.setName(DbNames.ENUM_TAB_INACTIVE_COL);
					inactiveCol.setNotNull(true);
					tab.addColumn(inactiveCol);
					DbColVarchar dispName=new DbColVarchar();
					dispName.setName(DbNames.ENUM_TAB_DISPNAME_COL);
					dispName.setNotNull(true);
					dispName.setSize(250);
					tab.addColumn(dispName);
					schema.addTable(tab);
				}
			}
			
		}
	}
	private static HashSet<String> readInheritanceTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> ret=new HashSet<String>();
		String sqlName=DbNames.INHERIT_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
			exstStmt="SELECT "+DbNames.INHERIT_TAB_THIS_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
			try{
				java.sql.ResultSet rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliClassQName=rs.getString(1);
					ret.add(iliClassQName);
				}
			}finally{
				exstPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read inheritance-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateInheritanceTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String sqlName=DbNames.INHERIT_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		//String stmt="CREATE TABLE "+tabname+" ("+thisClassCol+" VARCHAR2(30) NOT NULL,"+baseClassCol+" VARCHAR2(30) NULL)";
		HashSet<String> exstEntries=readInheritanceTable(conn,schema);
		try{

			// insert entries
			String stmt="INSERT INTO "+sqlName+" ("+DbNames.INHERIT_TAB_THIS_COL+","+DbNames.INHERIT_TAB_BASE_COL+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
			String thisClass=null;
			try{
				for(Object aclass:visitedElements){
					if(aclass instanceof Viewable){
						thisClass=((Viewable) aclass).getScopedName(null);
						if(!exstEntries.contains(thisClass)){
							Viewable base=(Viewable) ((Viewable) aclass).getExtending();
							ps.setString(1, thisClass);
							if(base!=null){
								ps.setString(2, base.getScopedName(null));
							}else{
								ps.setNull(2,java.sql.Types.VARCHAR);
							}
							ps.executeUpdate();
						}
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert inheritance-relation for class "+thisClass,ex);
			}finally{
				ps.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update inheritance-table "+sqlName,ex);
		}

	}
	public void updateEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{
		if(Config.CREATE_ENUM_DEFS_SINGLE.equals(createEnumTable)){
			updateSingleEnumTable(conn);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			updateMultiEnumTable(conn);
		}
	}
	private static HashSet readEnumTable(java.sql.Connection conn,boolean singleTable,String qualifiedIliName,DbTableName sqlDbName)
	throws Ili2dbException
	{
		HashSet ret=new HashSet();
		String sqlName=sqlDbName.getName();
		if(sqlDbName.getSchema()!=null){
			sqlName=sqlDbName.getSchema()+"."+sqlName;
		}
		try{
			String exstStmt=null;
			if(!singleTable){
				exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+" FROM "+sqlName;
			}else{
				exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+" FROM "+sqlName+" WHERE "+DbNames.ENUM_TAB_THIS_COL+" = '"+qualifiedIliName+"'";
			}
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
			try{
				java.sql.ResultSet rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliCode=rs.getString(1);
					ret.add(iliCode);
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read enum values for type "+qualifiedIliName,ex);
			}finally{
				exstPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read enum-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateSingleEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{
		DbTableName tabName=new DbTableName(schema.getName(),DbNames.ENUM_TAB);
		String sqlName=tabName.getName();
		if(tabName.getSchema()!=null){
			sqlName=tabName.getSchema()+"."+sqlName;
		}
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_THIS_COL+","+DbNames.ENUM_TAB_BASE_COL+") VALUES (?,?,?,?,?,?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			String thisClass=null;
			try{
				java.util.Iterator entri=visitedEnums.iterator();
				while(entri.hasNext()){
					Object entro=entri.next();
					if(entro instanceof AttributeDef){
						AttributeDef attr=(AttributeDef)entro;
						EnumerationType type=(EnumerationType)attr.getDomainResolvingAll();
						
						thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
						AttributeDef base=(AttributeDef)attr.getExtending();
						String baseClass=null;
						if(base!=null){
							baseClass=base.getContainer().getScopedName(null)+"."+base.getName();
						}
						HashSet exstEntries=readEnumTable(conn,true,thisClass,tabName);
						updateEnumEntries(exstEntries,insPrepStmt, type, thisClass, baseClass);
					}else if(entro instanceof Domain){
						Domain domain=(Domain)entro;
						EnumerationType type=(EnumerationType)domain.getType();
						
						thisClass=domain.getScopedName(null);
						Domain base=(Domain)domain.getExtending();
						String baseClass=null;
						if(base!=null){
							baseClass=base.getScopedName(null);
						}
						HashSet exstEntries=readEnumTable(conn,true,thisClass,tabName);
						updateEnumEntries(exstEntries,insPrepStmt, type, thisClass, baseClass);
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update enum-table "+sqlName,ex);
		}

	}
	public void updateMultiEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{

		java.util.Iterator entri=visitedEnums.iterator();
		while(entri.hasNext()){
			Object entro=entri.next();
			if(entro instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)entro;
				EnumerationType type=(EnumerationType)attr.getDomainResolvingAll();
				String thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
				DbTableName thisSqlName=getSqlTableNameEnum(attr);
				HashSet exstEntries=readEnumTable(conn,false,thisClass,thisSqlName);
				try{

					// insert entries
					String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+") VALUES (?,?,?,?,?)";
					EhiLogger.traceBackendCmd(stmt);
					java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
					try{
						updateEnumEntries(exstEntries,ps, type, null, null);
					}catch(java.sql.SQLException ex){
						throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
					}finally{
						ps.close();
					}
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
				}
				
			}else if(entro instanceof Domain){
				Domain domain=(Domain)entro;
				EnumerationType type=(EnumerationType)domain.getType();
				
				String thisClass=domain.getScopedName(null);
				DbTableName thisSqlName=getSqlTableName(domain);
				HashSet exstEntries=readEnumTable(conn,false,thisClass,thisSqlName);
				try{

					// insert entries
					String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+") VALUES (?,?,?,?,?)";
					EhiLogger.traceBackendCmd(stmt);
					java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
					try{
						updateEnumEntries(exstEntries,ps, type, null, null);
					}catch(java.sql.SQLException ex){
						throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
					}finally{
						ps.close();
					}
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
				}
			}
		}
		
		

	}
	private void updateEnumEntries(HashSet exstEntires,java.sql.PreparedStatement ps, EnumerationType type, String thisClass, String baseClass) 
	throws SQLException 
	{
		java.util.ArrayList ev=new java.util.ArrayList();
		ch.interlis.iom_j.itf.ModelUtilities.buildEnumList(ev,"",type.getConsolidatedEnumeration());
		boolean isOrdered=type.isOrdered();
		int itfCode=0;
		int seq=0;
		Iterator evi=ev.iterator();
		while(evi.hasNext()){
			String ele=(String)evi.next();
			// entry exists already?
			if(!exstEntires.contains(ele)){
				// insert only non-existing entries
				if(isOrdered){
					ps.setInt(1, seq);
				}else{
					ps.setNull(1,java.sql.Types.NUMERIC);
				}
				ps.setString(2, ele);
				ps.setInt(3, itfCode);
				ps.setString(4, recConv.beautifyEnumDispName(ele)); // dispName
				ps.setBoolean(5, false);  // inactive
				// single table for all enums?
				if(thisClass!=null){
					ps.setString(6, thisClass);
					if(baseClass!=null){
						ps.setString(7, baseClass);
					}else{
						ps.setNull(7,java.sql.Types.VARCHAR);
					}
				}
				ps.executeUpdate();
			}
			itfCode++;
			seq++;
		}
	}
	static public void addTableMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.CLASSNAME_TAB));
		ch.ehi.sqlgen.repository.DbColVarchar iliClassName=new ch.ehi.sqlgen.repository.DbColVarchar();
		iliClassName.setName(DbNames.CLASSNAME_TAB_ILINAME_COL);
		iliClassName.setNotNull(true);
		iliClassName.setSize(1024);
		iliClassName.setPrimaryKey(true);
		tab.addColumn(iliClassName);
		ch.ehi.sqlgen.repository.DbColVarchar sqlTableName=new ch.ehi.sqlgen.repository.DbColVarchar();
		sqlTableName.setName(DbNames.CLASSNAME_TAB_SQLNAME_COL);
		sqlTableName.setNotNull(true);
		sqlTableName.setSize(1024);
		tab.addColumn(sqlTableName);
		schema.addTable(tab);
	}
	static public void addAttrMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.ATTRNAME_TAB));
		ch.ehi.sqlgen.repository.DbColVarchar ilinameCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		ilinameCol.setName(DbNames.ATTRNAME_TAB_ILINAME_COL);
		ilinameCol.setNotNull(true);
		ilinameCol.setSize(1024);
		tab.addColumn(ilinameCol);
		ch.ehi.sqlgen.repository.DbColVarchar sqlnameCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		sqlnameCol.setName(DbNames.ATTRNAME_TAB_SQLNAME_COL);
		sqlnameCol.setNotNull(true);
		sqlnameCol.setSize(1024);
		tab.addColumn(sqlnameCol);
		ch.ehi.sqlgen.repository.DbColVarchar ownerCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		ownerCol.setName(DbNames.ATTRNAME_TAB_OWNER_COL);
		ownerCol.setNotNull(true);
		ownerCol.setSize(1024);
		tab.addColumn(ownerCol);
		ch.ehi.sqlgen.repository.DbColVarchar targetCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		targetCol.setName(DbNames.ATTRNAME_TAB_TARGET_COL);
		targetCol.setNotNull(false);
		targetCol.setSize(1024);
		tab.addColumn(targetCol);
		DbIndex pk=new DbIndex();
		pk.setPrimary(true);
		pk.addAttr(ownerCol);
		pk.addAttr(sqlnameCol);
		tab.addIndex(pk);
		schema.addTable(tab);
	}
}
