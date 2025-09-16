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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.StatementExecutionHelper;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.fromxtf.EnumValueMap;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.ColumnWrapper;
import ch.ehi.ili2db.mapping.MultiLineMappings;
import ch.ehi.ili2db.mapping.MultiPointMappings;
import ch.ehi.ili2db.mapping.MultiSurfaceMappings;
import ch.ehi.ili2db.mapping.StructAttrPath;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.ili2db.metaattr.IliMetaAttrNames;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
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
import ch.interlis.ili2c.metamodel.AbstractCoordType;
import ch.interlis.ili2c.metamodel.AbstractEnumerationType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.AttributeRef;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Evaluable;
import ch.interlis.ili2c.metamodel.ExtendableContainer;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.ObjectPath;
import ch.interlis.ili2c.metamodel.PathEl;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.ilirepository.IliFiles;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class TransferFromIli {
	public static final String EPSG = "EPSG";
    private static final String SRS_MAPPING_TO_ORIGINAL = "ch.ehi.ili2db.fromili.SrsMapping2Original";
    private static final String SRS_MAPPING_TO_ALTERNATE = "ch.ehi.ili2db.fromili.SrsMapping2Alternate";
    private DbSchema schema=null;
	private HashSet<Element> visitedElements=null;
	private Viewable2TableMapping class2wrapper=null;
	private HashSet<ViewableWrapper> visitedWrapper=null;
	private Set<Element> visitedEnums=null;
	private TransferDescription td=null;
	private ch.ehi.ili2db.mapping.NameMapping ili2sqlName=null;
	private String createEnumTable=null;
	private boolean createStdCols=false;
	private boolean createIliTidCol=false;
	private boolean createBasketCol=false;
	private boolean createDatasetCol=false;
	private CustomMapping customMapping=null;
	private boolean createFk=false;
	private boolean createFkIdx=false;
	private String colT_ID=null;
	private String nl=System.getProperty("line.separator");
	private FromIliRecordConverter recConv=null;
	private DbExtMetaInfo metaInfo=new DbExtMetaInfo();
	private Integer defaultCrsCode=null;
	private String srsModelAssignment=null;
	private Integer batchSize = null;
	private boolean useEpsgInNames=false;
	public DbSchema doit(TransferDescription td1,java.util.List<Element> modelEles,ch.ehi.ili2db.mapping.NameMapping ili2sqlName,ch.ehi.ili2db.gui.Config config,DbIdGen idGen,TrafoConfig trafoConfig,Viewable2TableMapping class2wrapper1,CustomMapping customMapping1)
	throws Ili2dbException
	{
		this.ili2sqlName=ili2sqlName;
		createEnumTable=config.getCreateEnumDefs();
		createStdCols=Config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createFk=Config.CREATE_FK_YES.equals(config.getCreateFk());
		createFkIdx=Config.CREATE_FKIDX_YES.equals(config.getCreateFkIdx());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		
		createIliTidCol=Config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		createDatasetCol=Config.CREATE_DATASET_COL.equals(config.getCreateDatasetCols());
		if(config.getDefaultSrsCode()!=null) {
	        defaultCrsCode=Integer.parseInt(config.getDefaultSrsCode());
		}
        srsModelAssignment=config.getSrsModelAssignment();
        useEpsgInNames=config.useEpsgInNames();

		customMapping=customMapping1;
		customMapping.fromIliInit(config);

		schema=new DbSchema();
		schema.setName(config.getDbschema());
		visitedElements=new HashSet<Element>();
		class2wrapper=class2wrapper1;
		visitedEnums=new HashSet<Element>();
		td=td1;
		recConv=new FromIliRecordConverter(td,ili2sqlName,config,schema,customMapping,idGen,visitedEnums,trafoConfig,class2wrapper,metaInfo);

		batchSize = config.getBatchSize();

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
				Topic topic = (Topic) modelo;
				if (topic.getDefferedGenerics().length > 0 && !useEpsgInNames) {
					throw new Ili2dbException("Mapping of Topic " + topic.getScopedName(null) + " requires the '--multiSrs' option because it declares deferred generics.");
				}
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
				    for(int epsgCode:getEpsgCodes(attr,srsModelAssignment,defaultCrsCode)) {
	                    generateItfLineTable(attr,epsgCode,pass);
				    }
				}else if(attr.getDomainResolvingAll() instanceof AbstractEnumerationType){
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
		if(def.getType() instanceof AbstractEnumerationType){
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
			if(isLightweightAssociation(assoc)) {
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
		}
	}
	private void generateItfLineTable(AttributeDef attr,Integer epsgCode,int pass)
	throws Ili2dbException
	{
		if(pass==1){
			DbTableName sqlName=getSqlTableNameItfLineTable(attr,epsgCode);
			DbTable dbTable=new DbTable();
			dbTable.setName(sqlName);
			dbTable.setIliName(attr.getContainer().getScopedName(null)+"."+attr.getName());
		  	schema.addTable(dbTable);
			return;
		}
		// second pass; add columns
		DbTableName sqlTableName=getSqlTableNameItfLineTable(attr,epsgCode);
		DbTable dbTable=schema.findTable(sqlTableName);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
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
		  if(createDatasetCol){
				DbColVarchar t_dsName=new DbColVarchar();
				t_dsName.setName(DbNames.T_DATASET_COL);
				t_dsName.setSize(DbNames.DATASETNAME_COL_SIZE);
				t_dsName.setNotNull(true);
				t_dsName.setIndex(true);
				dbTable.addColumn(t_dsName);
		  }
			SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAll();

			Model model = (Model) attr.getContainer(Model.class);
			DbColGeometry dbCol = recConv.generatePolylineType(model, type, attr.getContainer().getScopedName(null)+"."+attr.getName(), epsgCode);
			  recConv.setCrs(dbCol, epsgCode);
			  dbCol.setName(ili2sqlName.getSqlColNameItfLineTableGeomAttr(attr,sqlTableName.getName()));
			  dbCol.setNotNull(true);
			  dbTable.addColumn(dbCol);
			
			if(type instanceof SurfaceType){
				  dbColId=new DbColId();
				  dbColId.setName(ili2sqlName.getSqlColNameItfLineTableRefAttr(attr,sqlTableName.getName()));
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
			    	recConv.generateAttr(dbTable,null,new ViewableWrapper(sqlTableName.getSchema(),sqlTableName.getName(),lineAttrTable),new ColumnWrapper(new StructAttrPath(new ViewableTransferElement(lineattr))));
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
	private DbTableName getSqlTableNameItfLineTable(AttributeDef def,Integer epsgCode){
		String sqlname=ili2sqlName.mapItfGeometryAsTable((Viewable)def.getContainer(),def,epsgCode);
		return new DbTableName(schema.getName(),sqlname);
	}
	static public void addModelsTable(DbSchema schema,Settings config)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.MODELS_TAB));
		DbColVarchar fileCol=new DbColVarchar();
		fileCol.setName(DbNames.MODELS_TAB_FILENAME_COL);
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
		int modelNameColSize=DbColVarchar.UNLIMITED;
		String modelNameColSizeStr=config.getValue(Config.MODELS_TAB_MODELNAME_COLSIZE);
		if(modelNameColSizeStr!=null) {
			try {
				modelNameColSize=Integer.parseInt(modelNameColSizeStr);
			} catch (NumberFormatException e) {
			}
		}
		importsCol.setSize(modelNameColSize);
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
	public static ch.interlis.ilirepository.IliFiles readIliFiles(java.sql.Connection conn,String schema,CustomMapping mapping,boolean isVer3_export)
	throws Ili2dbException
	{
		String sqlName=DbNames.MODELS_TAB;
		if(!mapping.tableExists(conn,new DbTableName(schema,sqlName))){
			return null;
		}
		ch.interlis.ilirepository.IliFiles ret=new ch.interlis.ilirepository.IliFiles();
		try{
			String reposUri=conn.getMetaData().getURL();
			reposUri=mapping.shortenConnectUrl4IliCache(reposUri);
			if(schema!=null){
				sqlName=schema+"."+sqlName;
				reposUri=reposUri+"/"+schema;
			}
			// select entries
			String insStmt="SELECT "+DbNames.MODELS_TAB_FILENAME_COL+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+" FROM "+sqlName;
			if(isVer3_export) {
	            insStmt="SELECT "+DbNames.MODELS_TAB_FILENAME_COL_VER3+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+" FROM "+sqlName;
	            if(isMsSqlServer(conn) || isOracle(conn)) {
	                // 'file' is keyword in sql server and oracle
	                insStmt="SELECT \""+DbNames.MODELS_TAB_FILENAME_COL_VER3+"\","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+" FROM "+sqlName;
	            }
			}
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = null;
            java.sql.ResultSet rs=null;
			try{
	            insPrepStmt = conn.prepareStatement(insStmt);
				rs=insPrepStmt.executeQuery();
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
			    if(rs!=null) {
			        rs.close();
			        rs=null;
			    }
			    if(insPrepStmt!=null) {
	                insPrepStmt.close();
	                insPrepStmt=null;
			    }
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return ret;
	}
    public static boolean isPostgresql(java.sql.Connection conn) throws SQLException {
        return conn.getMetaData().getURL().startsWith("jdbc:postgresql:");
    }
	private static boolean isMsSqlServer(java.sql.Connection conn) throws SQLException {
		return conn.getMetaData().getURL().startsWith("jdbc:sqlserver:");
	}
	private static boolean isOracle(java.sql.Connection conn) throws SQLException {
		return conn.getMetaData().getURL().startsWith("jdbc:oracle:thin:@");
	}	
	public static String readIliFile(java.sql.Connection conn,String schema,String filename,boolean isVer3_export)
	throws Ili2dbException
	{
		String sqlName=DbNames.MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			// select entries
			String selStmt="SELECT "+DbNames.MODELS_TAB_CONTENT_COL+" FROM "+sqlName+" WHERE "+DbNames.MODELS_TAB_FILENAME_COL+"=?";
			if(isVer3_export) {
	            selStmt="SELECT "+DbNames.MODELS_TAB_CONTENT_COL+" FROM "+sqlName+" WHERE "+DbNames.MODELS_TAB_FILENAME_COL_VER3+"=?";
	            if(isMsSqlServer(conn) || isOracle(conn)) {
	                selStmt="SELECT "+DbNames.MODELS_TAB_CONTENT_COL+" FROM "+sqlName+" WHERE \""+DbNames.MODELS_TAB_FILENAME_COL_VER3+"\"=?";
	            }
			}
			EhiLogger.traceBackendCmd(selStmt);
			java.sql.PreparedStatement selPrepStmt = null;
            java.sql.ResultSet rs=null;
			try{
	            selPrepStmt = conn.prepareStatement(selStmt);
				selPrepStmt.clearParameters();
				selPrepStmt.setString(1, filename);
				rs=selPrepStmt.executeQuery();
				while(rs.next()){
					String file=rs.getString(1);
					return file;
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read ili-file <"+filename+"> from db",ex);
			}finally{
			    if(rs!=null) {
			        rs.close();
			        rs=null;
			    }
			    if(selPrepStmt!=null) {
	                selPrepStmt.close();
	                selPrepStmt=null;
			    }
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return null;
	}
	public static void addModels(GeneratorJdbc gen, java.sql.Connection conn,TransferDescription td,String schema,CustomMapping mapping,boolean isVer3_export)
	throws Ili2dbException
	{
		// read existing models from db
		IliFiles iliModelsInDb = TransferFromIli.readIliFiles(conn,schema,mapping,isVer3_export);

		String sqlName=DbNames.MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.Timestamp today=new java.sql.Timestamp(System.currentTimeMillis());

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
                Model translatedModel=(Model)model.getTranslationOf();
                if(translatedModel!=null){
                    ilimodel.addDepenedency(translatedModel.getName());
                }
                ilifile.addModel(ilimodel);
            }
        }
        
		if(conn!=null) {
	        try{

	            // insert entries
	            String insStmt="INSERT INTO "+sqlName+" ("+DbNames.MODELS_TAB_FILENAME_COL+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+","+DbNames.MODELS_TAB_CONTENT_COL+","+DbNames.MODELS_TAB_IMPORTDATE_COL+") VALUES (?,?,?,?,?)";
	            EhiLogger.traceBackendCmd(insStmt);
	            java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
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
		if(gen!=null){
            try {
                for(ch.interlis.ili2c.modelscan.IliFile ilifile: ilifiles.values()){
                    ch.interlis.ili2c.modelscan.IliModel ilimodel=(ch.interlis.ili2c.modelscan.IliModel)ilifile.iteratorModel().next();
                    if(iliModelsInDb==null || iliModelsInDb.getFileWithModel(ilimodel.getName(), ilimodel.getIliVersion())==null){
                        String insStmt;
                        insStmt = "INSERT INTO "+sqlName+" ("+DbNames.MODELS_TAB_FILENAME_COL+","+DbNames.MODELS_TAB_ILIVERSION_COL+","+DbNames.MODELS_TAB_MODELNAME_COL+","+DbNames.MODELS_TAB_CONTENT_COL+","+DbNames.MODELS_TAB_IMPORTDATE_COL
                                +") VALUES ("+Ili2db.quoteSqlStringValue(ilifile.getFilename().getName())+","+Ili2db.quoteSqlStringValue(Double.toString(ilifile.getIliVersion()))+","+Ili2db.quoteSqlStringValue(IliImportsUtility.getIliImports(ilifile))+","+Ili2db.quoteSqlStringValue(readFileAsString(ilifile.getFilename()))+",'"+today+"')";
                        gen.addCreateLine(gen.new Stmt(insStmt));
                    }
                }
            } catch (IOException e) {
                throw new Ili2dbException("failed to create inserts to models-table "+sqlName,e);
            }
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
		settingCol.setSize(DbNames.SETTING_COL_SIZE);
		tab.addColumn(settingCol);
		schema.addTable(tab);
	}
	public static void readSettings(java.sql.Connection conn,Config settings,String schema,CustomMapping customMapping)
	throws Ili2dbException
	{
		String sqlName=DbNames.SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(customMapping.tableExists(conn,new DbTableName(schema,DbNames.SETTINGS_TAB))){
			try{
				// select entries
				String insStmt="SELECT "+DbNames.SETTINGS_TAB_TAG_COL+","+DbNames.SETTINGS_TAB_SETTING_COL+" FROM "+sqlName;
				EhiLogger.traceBackendCmd(insStmt);
				java.sql.PreparedStatement insPrepStmt = null;
                java.sql.ResultSet rs=null;
				boolean settingsExists=false;
				try{
	                insPrepStmt = conn.prepareStatement(insStmt);
					rs=insPrepStmt.executeQuery();
					while(rs.next()){
						String tag=rs.getString(1);
						String value=rs.getString(2);
						if(tag.equals(Config.SENDER))continue;
                        if(tag.equals(Config.METACONFIGFILENAME))continue;
						settings.setValue(tag,value);
						settingsExists=true;
					}
					if(settingsExists){
						settings.setConfigReadFromDb(true);
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to read setting",ex);
				}finally{
				    if(rs!=null) {
				        rs.close();
				        rs=null;
				    }
				    if(insPrepStmt!=null) {
	                    insPrepStmt.close();
	                    insPrepStmt=null;
				    }
				}
			}catch(java.sql.SQLException ex){		
				throw new Ili2dbException("failed to read settings-table "+sqlName,ex);
			}
		}
	}
	public static void updateSettings(GeneratorJdbc gen, java.sql.Connection conn,Config settings,String schema)
	throws Ili2dbException
	{

		String sqlName=DbNames.SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(conn!=null) {
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
		if(gen!=null){
            java.util.Iterator entri=settings.getValues().iterator();
            while(entri.hasNext()){
                String tag=(String)entri.next();
                String insStmt="INSERT INTO "+sqlName+" ("+DbNames.SETTINGS_TAB_TAG_COL+","+DbNames.SETTINGS_TAB_SETTING_COL
                        +") VALUES ("+Ili2db.quoteSqlStringValue(tag)+","+Ili2db.quoteSqlStringValue(settings.getValue(tag))+")";
                gen.addCreateLine(gen.new Stmt(insStmt));
            }
		}

	}
	
	static public void addInheritanceTable(DbSchema schema,Config config)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.INHERIT_TAB));
		DbColVarchar thisClass=new DbColVarchar();
		thisClass.setName(DbNames.INHERIT_TAB_THIS_COL);
		thisClass.setNotNull(true);
		thisClass.setPrimaryKey(true);
		int thisClassSize=1024;
		try {
		    thisClassSize=Integer.parseInt(config.getValue(Config.INHERIT_TAB_THIS_COLSIZE));
		}catch(NumberFormatException e) {
		    
		}
		thisClass.setSize(thisClassSize);
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

            // space separated list of assignments from generic to concrete domain "generic1=concrete1 generic2=concrete2"
            DbColVarchar domains=new DbColVarchar();
            domains.setName(DbNames.BASKETS_TAB_DOMAINS_COL);
            domains.setNotNull(false);
            domains.setSize(1024);
            tab.addColumn(domains);
			
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
			dsNameCol.setSize(DbNames.DATASETNAME_COL_SIZE);
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
			
			recConv.addKeyCol(tab);
			
			DbColId dbColDataset=new DbColId();
			dbColDataset.setName(DbNames.IMPORTS_TAB_DATASET_COL);
			dbColDataset.setNotNull(true);
			dbColDataset.setScriptComment("REFERENCES "+DbNames.DATASETS_TAB);
			if(false && createFk){
				// do not create ref so that entry in dataset table can be deleted without deleting import stat
				dbColDataset.setReferencedTable(new DbTableName(schema.getName(),DbNames.DATASETS_TAB));
			}
			if(createFkIdx){
				dbColDataset.setIndex(true);
			}
			tab.addColumn(dbColDataset);
			
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
			
			recConv.addKeyCol(tab);
			
			DbColId dbColImport=new DbColId();
			dbColImport.setName(DbNames.IMPORTS_BASKETS_TAB_IMPORTRUN_COL);
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
			if(false && createFk){
                // do not create ref so that entry in dataset table can be deleted without deleting import stat
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

			schema.addTable(tab);
			
		}
		if(false){
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
			DbColVarchar description=new DbColVarchar();
			description.setName(DbNames.ENUM_TAB_DESCRIPTION_COL);
			description.setNotNull(false);
			description.setSize(DbNames.ENUM_TAB_DESCRIPTION_COL_SIZE);
			tab.addColumn(description);
			schema.addTable(tab);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			addMissingEnumDomains(visitedEnums);
			java.util.Iterator<Element> entri=visitedEnums.iterator();
			while(entri.hasNext()){
				Object entro=entri.next();
				DbTableName thisSqlName=null;
				if(entro instanceof AttributeDef){
					AttributeDef attr=(AttributeDef)entro;
					ch.interlis.ili2c.metamodel.Type type=attr.getDomain();
					if(type instanceof ch.interlis.ili2c.metamodel.TypeAlias){
						continue; // skip it
					}else{
						thisSqlName=getSqlTableNameEnum(attr);
					}
				}else if(entro instanceof Domain){
					Domain domain=(Domain)entro;
					if(Ili2cUtility.isBoolean(td, domain.getType())){
						continue;
					}
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
					DbColVarchar description=new DbColVarchar();
					description.setName(DbNames.ENUM_TAB_DESCRIPTION_COL);
					description.setNotNull(false);
					description.setSize(DbNames.ENUM_TAB_DESCRIPTION_COL_SIZE);
					tab.addColumn(description);
					schema.addTable(tab);
					metaInfo.setTableInfo(tab.getName().getName(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_ENUM);
				}
			}
			
        }else if(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID.equals(createEnumTable)){
            addMissingEnumDomains(visitedEnums);
            java.util.HashSet<Element> enumDefs=new HashSet<Element>();
            java.util.Iterator<Element> entri=visitedEnums.iterator();
            while(entri.hasNext()){
                Object entro=entri.next();
                DbTableName thisSqlName=null;
                if(entro instanceof AttributeDef){
                    AttributeDef attr=(AttributeDef)entro;
                    ch.interlis.ili2c.metamodel.Type type=attr.getDomain();
                    if(type instanceof ch.interlis.ili2c.metamodel.TypeAlias){
                        continue; // skip it
                    }else{
                        attr=Ili2cUtility.getRootBaseAttr(attr);
                        if(!enumDefs.contains(attr)) {
                            thisSqlName=getSqlTableNameEnum(attr);
                            enumDefs.add(attr);
                        }
                    }
                }else if(entro instanceof Domain){
                    Domain domain=(Domain)entro;
                    if(Ili2cUtility.isBoolean(td, domain.getType())){
                        continue;
                    }
                    domain=Ili2cUtility.getRootBaseDomain(domain);
                    if(!enumDefs.contains(domain)) {
                        thisSqlName=getSqlTableName(domain);
                        enumDefs.add(domain);
                    }
                }
                if(thisSqlName!=null){
                    DbTable tab=new DbTable();
                    tab.setRequiresSequence(true);
                    tab.setName(thisSqlName);
                    recConv.addKeyCol(tab);
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
                    DbColNumber itfCode=new DbColNumber();
                    itfCode.setName(DbNames.ENUM_TAB_ITFCODE_COL);
                    itfCode.setNotNull(true);
                    itfCode.setSize(4);
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
                    DbColVarchar description=new DbColVarchar();
                    description.setName(DbNames.ENUM_TAB_DESCRIPTION_COL);
                    description.setNotNull(false);
                    description.setSize(DbNames.ENUM_TAB_DESCRIPTION_COL_SIZE);
                    tab.addColumn(description);
                    schema.addTable(tab);
                    metaInfo.setTableInfo(tab.getName().getName(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_ENUM);
                }
            }
            
		}
	}
	private void addMissingEnumDomains(Set<Element> enums) {
		java.util.Iterator<Element> entri=enums.iterator();
		HashSet<Domain> missingDomains=new HashSet<Domain>();
		while(entri.hasNext()){
			Object entro=entri.next();
			if(entro instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)entro;
				ch.interlis.ili2c.metamodel.Type type=attr.getDomain();
				if(type instanceof ch.interlis.ili2c.metamodel.TypeAlias){
					Domain domain=((ch.interlis.ili2c.metamodel.TypeAlias) type).getAliasing();
					if(!enums.contains(domain)){
						missingDomains.add(domain);
					}
				}
			}else if(entro instanceof Domain){
				// skip
			}
		}		
		enums.addAll(missingDomains);
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
			java.sql.PreparedStatement exstPrepStmt = null;
            java.sql.ResultSet rs=null;
			try{
	            exstPrepStmt = conn.prepareStatement(exstStmt);
				rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliClassQName=rs.getString(1);
					ret.add(iliClassQName);
				}
			}finally{
			    if(rs!=null) {
			        rs.close();
			        rs=null;
			    }
			    if(exstPrepStmt!=null) {
	                exstPrepStmt.close();
	                exstPrepStmt=null;
			    }
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read inheritance-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateInheritanceTable(GeneratorJdbc gen, java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String sqlName=DbNames.INHERIT_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(conn!=null) {
	        //String stmt="CREATE TABLE "+tabname+" ("+thisClassCol+" VARCHAR2(30) NOT NULL,"+baseClassCol+" VARCHAR2(30) NULL)";
	        HashSet<String> exstEntries=readInheritanceTable(conn,schema);
	        try{

	            // insert entries
	            String stmt="INSERT INTO "+sqlName+" ("+DbNames.INHERIT_TAB_THIS_COL+","+DbNames.INHERIT_TAB_BASE_COL+") VALUES (?,?)";
	            EhiLogger.traceBackendCmd(stmt);
	            java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
	            String thisClass=null;
				StatementExecutionHelper seHelper = new StatementExecutionHelper(batchSize);
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
								seHelper.write(ps);
	                        }
	                    }
	                }

					seHelper.flush(ps);

				}catch(java.sql.SQLException ex){
	                throw new Ili2dbException("failed to insert inheritance-relation for class "+thisClass,ex);
	            }finally{
	                ps.close();
	            }
	        }catch(java.sql.SQLException ex){       
	            throw new Ili2dbException("failed to update inheritance-table "+sqlName,ex);
	        }
		}
		if(gen!=null){
            for(Object aclass:visitedElements){
                if(aclass instanceof Viewable){
                    String thisClass=((Viewable) aclass).getScopedName(null);
                    Viewable base=(Viewable) ((Viewable) aclass).getExtending();
                    String baseScopedName=base!=null?base.getScopedName():null;
                    String stmt="INSERT INTO "+sqlName+" ("+DbNames.INHERIT_TAB_THIS_COL+","+DbNames.INHERIT_TAB_BASE_COL
                            +") VALUES ("+Ili2db.quoteSqlStringValue(thisClass)+","+Ili2db.quoteSqlStringValue(baseScopedName)+")";
                    gen.addCreateLine(gen.new Stmt(stmt));
                }
            }
		    
		}

	}
	public void updateEnumTable(GeneratorJdbc gen, java.sql.Connection conn)
	throws Ili2dbException
	{
		if(Config.CREATE_ENUM_DEFS_SINGLE.equals(createEnumTable)){
			updateSingleEnumTable(gen,conn);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			updateMultiEnumTable(gen,conn);
        }else if(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID.equals(createEnumTable)){
            updateMultiEnumTableWithId(gen,conn);
		}
	}
	public void updateSingleEnumTable(GeneratorJdbc gen, java.sql.Connection conn)
	throws Ili2dbException
	{
		DbTableName tabName=new DbTableName(schema.getName(),DbNames.ENUM_TAB);
		String sqlName=tabName.getName();
		if(tabName.getSchema()!=null){
			sqlName=tabName.getSchema()+"."+sqlName;
		}
		if(conn!=null) {
	        try{

	            // insert entries
	            String insStmt="INSERT INTO "+sqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+","+DbNames.ENUM_TAB_THIS_COL+","+DbNames.ENUM_TAB_BASE_COL+") VALUES (?,?,?,?,?,?,?,?)";
	            EhiLogger.traceBackendCmd(insStmt);
	            java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
	            String thisClass=null;
	            try{
	                addMissingEnumDomains(visitedEnums);
	                java.util.Iterator<Element> entri=visitedEnums.iterator();
	                while(entri.hasNext()){
	                    Object entro=entri.next();
	                    if(entro instanceof AttributeDef){
	                        AttributeDef attr=(AttributeDef)entro;
	                        if(attr.getDomain() instanceof ch.interlis.ili2c.metamodel.TypeAlias){
	                            continue;
	                        }
	                        AbstractEnumerationType type=(AbstractEnumerationType)attr.getDomainResolvingAll();
	                        
	                        thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
	                        AttributeDef base=(AttributeDef)attr.getExtending();
	                        String baseClass=null;
	                        if(base!=null){
	                            baseClass=base.getContainer().getScopedName(null)+"."+base.getName();
	                        }
	                        Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,null,true,thisClass,tabName);
	                        updateEnumEntries(null,exstEntries,sqlName, insPrepStmt,EnumValueMap.createEnumValueMap(attr, ili2sqlName), type.isOrdered(),thisClass, baseClass);
	                    }else if(entro instanceof Domain){
	                        Domain domain=(Domain)entro;
	                        if(Ili2cUtility.isBoolean(td, domain.getType())){
	                            continue;
	                        }
	                        AbstractEnumerationType type=(AbstractEnumerationType)domain.getType();
	                        
	                        thisClass=domain.getScopedName(null);
	                        Domain base=(Domain)domain.getExtending();
	                        String baseClass=null;
	                        if(base!=null){
	                            baseClass=base.getScopedName(null);
	                        }
	                        Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,null,true,thisClass,tabName);
	                        updateEnumEntries(null,exstEntries,sqlName,insPrepStmt, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),thisClass, baseClass);
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
		if(gen!=null){
            addMissingEnumDomains(visitedEnums);
            java.util.Iterator<Element> entri=visitedEnums.iterator();
            try {
                while(entri.hasNext()){
                    Object entro=entri.next();
                    if(entro instanceof AttributeDef){
                        AttributeDef attr=(AttributeDef)entro;
                        if(attr.getDomain() instanceof ch.interlis.ili2c.metamodel.TypeAlias){
                            continue;
                        }
                        AbstractEnumerationType type=(AbstractEnumerationType)attr.getDomainResolvingAll();
                        
                        String thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
                        AttributeDef base=(AttributeDef)attr.getExtending();
                        String baseClass=null;
                        if(base!=null){
                            baseClass=base.getContainer().getScopedName(null)+"."+base.getName();
                        }
                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,sqlName,null, EnumValueMap.createEnumValueMap(attr, ili2sqlName), type.isOrdered(),thisClass, baseClass);
                    }else if(entro instanceof Domain){
                        Domain domain=(Domain)entro;
                        if(Ili2cUtility.isBoolean(td, domain.getType())){
                            continue;
                        }
                        AbstractEnumerationType type=(AbstractEnumerationType)domain.getType();
                        
                        String thisClass=domain.getScopedName(null);
                        Domain base=(Domain)domain.getExtending();
                        String baseClass=null;
                        if(base!=null){
                            baseClass=base.getScopedName(null);
                        }
                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,sqlName,null, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),thisClass, baseClass);
                    }
                }
            }catch(java.sql.SQLException ex){       
                throw new Ili2dbException("failed to create inserts for enum-table "+sqlName,ex);
            }
		    
		}

	}
	public void updateMultiEnumTable(GeneratorJdbc gen, java.sql.Connection conn)
	throws Ili2dbException
	{
		addMissingEnumDomains(visitedEnums);
		java.util.Iterator<Element> entri=visitedEnums.iterator();
		while(entri.hasNext()){
			Object entro=entri.next();
			if(entro instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)entro;
				if(attr.getDomain() instanceof ch.interlis.ili2c.metamodel.TypeAlias){
					continue;
				}
				AbstractEnumerationType type=(AbstractEnumerationType)attr.getDomainResolvingAll();
				String thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
				DbTableName thisSqlName=getSqlTableNameEnum(attr);
				if(conn!=null) {
	                try{

	                    Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,null,false,thisClass,thisSqlName);
	                    // insert entries
	                    String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+") VALUES (?,?,?,?,?,?)";
	                    EhiLogger.traceBackendCmd(stmt);
	                    java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
	                    try{
	                        updateEnumEntries(null,exstEntries,thisSqlName.getQName(),ps, EnumValueMap.createEnumValueMap(attr, ili2sqlName), type.isOrdered(),null, null);
	                    }catch(java.sql.SQLException ex){
	                        throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
	                    }finally{
	                        ps.close();
	                    }
	                }catch(java.sql.SQLException ex){       
	                    throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
	                }
				}
				if(gen!=null){
                    try{

                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,thisSqlName.getQName(),null, EnumValueMap.createEnumValueMap(attr, ili2sqlName), type.isOrdered(),null, null);
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to create inserts to enum-table "+thisSqlName,ex);
                    }
				}
				
			}else if(entro instanceof Domain){
				Domain domain=(Domain)entro;
				if(Ili2cUtility.isBoolean(td, domain.getType())){
					continue;
				}
				AbstractEnumerationType type=(AbstractEnumerationType)domain.getType();
				
				String thisClass=domain.getScopedName(null);
				DbTableName thisSqlName=getSqlTableName(domain);
				if(conn!=null) {
	                try{
	                    Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,null,false,thisClass,thisSqlName);

	                    // insert entries
	                    String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+") VALUES (?,?,?,?,?,?)";
	                    EhiLogger.traceBackendCmd(stmt);
	                    java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
	                    try{
	                        updateEnumEntries(null,exstEntries,thisSqlName.getQName(),ps, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),null, null);
	                    }catch(java.sql.SQLException ex){
	                        throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
	                    }finally{
	                        ps.close();
	                    }
	                }catch(java.sql.SQLException ex){       
	                    throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
	                }
				}
				if(gen!=null){
                    try{
                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,thisSqlName.getQName(),null, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),null, null);
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to create inserts to enum-table "+thisSqlName,ex);
                    }
				}
			}
		}
		
		

	}
    public void updateMultiEnumTableWithId(GeneratorJdbc gen, java.sql.Connection conn)
    throws Ili2dbException
    {
        addMissingEnumDomains(visitedEnums);
        java.util.Iterator<Element> entri=visitedEnums.iterator();
        while(entri.hasNext()){
            Object entro=entri.next();
            if(entro instanceof AttributeDef){
                AttributeDef attr=(AttributeDef)entro;
                if(attr.getDomain() instanceof ch.interlis.ili2c.metamodel.TypeAlias){
                    continue;
                }
                AbstractEnumerationType type=(AbstractEnumerationType)attr.getDomainResolvingAll();
                String thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
                AttributeDef base=(AttributeDef)attr.getExtending();
                String baseClass=null;
                if(base!=null){
                    baseClass=base.getContainer().getScopedName(null)+"."+base.getName();
                }
                DbTableName thisSqlName=getSqlTableNameEnum(Ili2cUtility.getRootBaseAttr(attr));
                if(conn!=null) {
                    try{
                        Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,colT_ID,true,thisClass,thisSqlName);

                        // insert entries
                        String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+","+DbNames.ENUM_TAB_THIS_COL+","+DbNames.ENUM_TAB_BASE_COL+") VALUES (?,?,?,?,?,?,?,?)";
                        EhiLogger.traceBackendCmd(stmt);
                        java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
                        try{
                            updateEnumEntries(null,exstEntries,thisSqlName.getQName(),ps, EnumValueMap.createEnumValueMap(attr, ili2sqlName), type.isOrdered(),thisClass, baseClass);
                        }catch(java.sql.SQLException ex){
                            throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
                        }finally{
                            ps.close();
                        }
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
                    }
                    
                }
                if(gen!=null){
                    try{
                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,thisSqlName.getQName(),null, EnumValueMap.createEnumValueMap(attr, ili2sqlName),type.isOrdered(), thisClass, baseClass);
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to create inserts into enum-table "+thisSqlName,ex);
                    }
                }
                
            }else if(entro instanceof Domain){
                Domain domain=(Domain)entro;
                if(Ili2cUtility.isBoolean(td, domain.getType())){
                    continue;
                }
                AbstractEnumerationType type=(AbstractEnumerationType)domain.getType();
                String thisClass=domain.getScopedName(null);
                Domain base=(Domain)domain.getExtending();
                String baseClass=null;
                if(base!=null){
                    baseClass=base.getScopedName(null);
                }
                DbTableName thisSqlName=getSqlTableName(Ili2cUtility.getRootBaseDomain(domain));
                if(conn!=null) {
                    try{
                        Set<String> exstEntries=EnumValueMap.readIliCodesFromDb(conn,colT_ID,true,thisClass,thisSqlName);

                        // insert entries
                        //String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+") VALUES (?,?,?,?,?,?)";
                        String stmt="INSERT INTO "+thisSqlName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+","+DbNames.ENUM_TAB_THIS_COL+","+DbNames.ENUM_TAB_BASE_COL+") VALUES (?,?,?,?,?,?,?,?)";
                        EhiLogger.traceBackendCmd(stmt);
                        java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
                        try{
                            updateEnumEntries(null,exstEntries,thisSqlName.getQName(),ps, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),thisClass, baseClass);
                        }catch(java.sql.SQLException ex){
                            throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
                        }finally{
                            ps.close();
                        }
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
                    }
                }
                if(gen!=null){
                    try{
                        Set<String> exstEntries=new HashSet<String>();
                        updateEnumEntries(gen,exstEntries,thisSqlName.getQName(),null, EnumValueMap.createEnumValueMap(domain, ili2sqlName), type.isOrdered(),thisClass, baseClass);
                    }catch(java.sql.SQLException ex){       
                        throw new Ili2dbException("failed to create inserts into enum-table "+thisSqlName,ex);
                    }
                }
            }
        }
        
        

    }
	private void updateEnumEntries(GeneratorJdbc gen, java.util.Set<String> exstEntries,String sqlTableName,java.sql.PreparedStatement ps, EnumValueMap type, boolean isOrdered,String thisClass, String baseClass) 
	throws SQLException 
	{
		Iterator<String> evi=type.getXtfCodes().iterator();
		while(evi.hasNext()){
			String eleName=evi.next();
            int itfCode=type.mapXtfValueToItfCode(eleName);

            if(ps!=null) {
                // entry exists already?
                if(!exstEntries.contains(eleName)){
                    // insert only non-existing entries
                    if(isOrdered){
                        int seq=type.mapXtfValueToSeq(eleName);
                        ps.setInt(1, seq);
                    }else{
                        ps.setNull(1,java.sql.Types.NUMERIC);
                    }
                    ps.setString(2, eleName);
                    ps.setInt(3, itfCode);

                    String dispName = type.mapXtfValueToDisplayName(eleName);
                    if (dispName!=null){
                        ps.setString(4, dispName); // do not beautify name provided by user
                    }else{
                        ps.setString(4, ili2sqlName.beautifyEnumDispName(eleName)); 
                    }
                    ps.setBoolean(5, false);  // inactive
                    String description = type.mapXtfValueToDoc(eleName);
                    if (description!=null){
                        ps.setString(6, description);
                    }else{
                        ps.setNull(6, java.sql.Types.VARCHAR);
                    }

                    // single table for all enums?
                    if(thisClass!=null){
                        ps.setString(7, thisClass);
                        if(baseClass!=null){
                            ps.setString(8, baseClass);
                        }else{
                            ps.setNull(8,java.sql.Types.VARCHAR);
                        }
                    }
                    ps.executeUpdate();
                }
			}
            if(gen!=null) {
                StringBuffer insStmt=new StringBuffer("INSERT INTO "+sqlTableName+" ("+DbNames.ENUM_TAB_SEQ_COL+","+DbNames.ENUM_TAB_ILICODE_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_INACTIVE_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL);
                if(thisClass!=null){
                    insStmt.append(","+DbNames.ENUM_TAB_THIS_COL+","+DbNames.ENUM_TAB_BASE_COL);
                }
                insStmt.append(") VALUES (");
                // insert only non-existing entries
                if(isOrdered){
                    int seq=type.mapXtfValueToSeq(eleName);
                    insStmt.append(seq);
                }else{
                    insStmt.append("NULL");
                }
                insStmt.append(","+Ili2db.quoteSqlStringValue(eleName));
                insStmt.append(","+itfCode);

                String dispName = type.mapXtfValueToDisplayName(eleName);
                if (dispName!=null){
                    insStmt.append(","+Ili2db.quoteSqlStringValue(dispName)); // do not beautify name provided by user
                }else{
                    insStmt.append(","+ Ili2db.quoteSqlStringValue(ili2sqlName.beautifyEnumDispName(eleName))); 
                }
                insStmt.append(",'0'");  // inactive
                String description = type.mapXtfValueToDoc(eleName);
                insStmt.append(","+ Ili2db.quoteSqlStringValue(description));

                // single table for all enums?
                if(thisClass!=null){
                    insStmt.append(","+Ili2db.quoteSqlStringValue(thisClass));
                    insStmt.append(","+Ili2db.quoteSqlStringValue(baseClass));
                }
                insStmt=insStmt.append(")");
                gen.addCreateLine(gen.new Stmt(insStmt.toString()));
            }
		}
	}
	static public void addTableMappingTable(ch.ehi.sqlgen.repository.DbSchema schema,Config config)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),DbNames.CLASSNAME_TAB));
		ch.ehi.sqlgen.repository.DbColVarchar iliClassName=new ch.ehi.sqlgen.repository.DbColVarchar();
		iliClassName.setName(DbNames.CLASSNAME_TAB_ILINAME_COL);
		iliClassName.setNotNull(true);
		int ilinameSize=1024;
		try {
		    ilinameSize=Integer.parseInt(config.getValue(Config.CLASSNAME_TAB_ILINAME_COLSIZE));
		}catch(NumberFormatException e) {
		}
		iliClassName.setSize(ilinameSize);
		iliClassName.setPrimaryKey(true);
		tab.addColumn(iliClassName);
		ch.ehi.sqlgen.repository.DbColVarchar sqlTableName=new ch.ehi.sqlgen.repository.DbColVarchar();
		sqlTableName.setName(DbNames.CLASSNAME_TAB_SQLNAME_COL);
		sqlTableName.setNotNull(true);
		sqlTableName.setSize(1024);
		tab.addColumn(sqlTableName);
		schema.addTable(tab);
	}
	static public void addAttrMappingTable(ch.ehi.sqlgen.repository.DbSchema schema,Config config)
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
		int sqlnameColSize=1024;
		try {
		    sqlnameColSize=Integer.parseInt(config.getValue(Config.ATTRNAME_TAB_SQLNAME_COLSIZE));
		}catch(NumberFormatException e) {
		}
		sqlnameCol.setSize(sqlnameColSize);
		tab.addColumn(sqlnameCol);
		ch.ehi.sqlgen.repository.DbColVarchar ownerCol=new ch.ehi.sqlgen.repository.DbColVarchar();
		ownerCol.setName(DbNames.ATTRNAME_TAB_COLOWNER_COL);
		ownerCol.setNotNull(true);
		int ownerColSize=1024;
        try {
            ownerColSize=Integer.parseInt(config.getValue(Config.ATTRNAME_TAB_OWNER_COLSIZE));
        }catch(NumberFormatException e) {
        }
		ownerCol.setSize(ownerColSize);
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
	public void updateMetaInfoTables(GeneratorJdbc gen, java.sql.Connection conn) 
			throws Ili2dbException
	{
		for(ViewableWrapper v:visitedWrapper){
			if(v.isSecondaryTable()){
				metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_SECONDARY);
			}else if(v.getExtending()==null){
				if(v.isStructure()){
					metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_STRUCTURE);
				}else if(v.getViewable() instanceof AssociationDef){
					metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_ASSOCIATION);
				}else if(Ili2cUtility.isChbaseCatalogueItem(td, v.getViewable())){
					metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_CATALOGUE);
                }else if(v.getViewable() instanceof Table){
                    metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_TABLEKIND, DbExtMetaInfo.TAG_TAB_TABLEKIND_CLASS);
				}
			}

			String dispName = v.getViewable().getMetaValues().getValue(IliMetaAttrNames.METAATTR_DISPNAME);
			if (dispName!=null){
			    metaInfo.setTableInfo(v.getSqlTablename(), DbExtMetaInfo.TAG_TAB_DISPNAME, dispName);
			}
		}
		metaInfo.updateMetaInfoTables(gen,conn, schema.getName());
	}
    public static boolean isLightweightAssociation(AssociationDef roleOwner) {
        if(!roleOwner.isLightweight()) {
            return false;
        }
        for(ExtendableContainer<Element> assocEle:roleOwner.getExtensions()) {
            AssociationDef assoc=(AssociationDef)assocEle;
            if(assoc.getAttributes().hasNext()
                    || assoc.getLightweightAssociations().iterator().hasNext()) {
                return false;
            }
        }
        return true;
    }
    public static int[] getEpsgCodes(AttributeDef attr,String srsModelAssignment,Integer defaultCrsCode) {
        TransferDescription td=(TransferDescription)attr.getContainer(TransferDescription.class);
        if(Ili2cUtility.isMultiSurfaceAttr(td,attr)) {
            MultiSurfaceMappings multiSurfaceAttrs=new MultiSurfaceMappings();
            multiSurfaceAttrs.addMultiSurfaceAttr(attr);
            AttributeDef surfaceAttr = multiSurfaceAttrs.getSurfaceAttr(attr);
            attr=surfaceAttr;
        }else if(Ili2cUtility.isMultiLineAttr(td, attr)) {
            MultiLineMappings multiLineAttrs=new MultiLineMappings();
            multiLineAttrs.addMultiLineAttr(attr);
            AttributeDef polylineAttr=multiLineAttrs.getPolylineAttr(attr);
            attr=polylineAttr;
        }else if(Ili2cUtility.isMultiPointAttr(td, attr)) {
            MultiPointMappings multiPointAttrs=new MultiPointMappings();
            multiPointAttrs.addMultiPointAttr(attr);
            AttributeDef multipointAttr=multiPointAttrs.getCoordAttr(attr);
            attr=multipointAttr;
        }
        ch.interlis.ili2c.metamodel.Element attrOrDomainDef=attr;
        ch.interlis.ili2c.metamodel.Type attrType=attr.getDomain();
        if (attrType == null) {
            while ((attrType == null) && ((attrOrDomainDef instanceof LocalAttribute))) {
                Evaluable[] ev = ((LocalAttribute) attrOrDomainDef).getBasePaths();
                attrType = ((ObjectPath) ev[0]).getType();
                PathEl last = ((ObjectPath) ev[0]).getLastPathEl();
                attrOrDomainDef=((AttributeRef)last).getAttr();
            }
        }
        Domain coordDomain=null;
        if(attrType instanceof ch.interlis.ili2c.metamodel.TypeAlias) {
            attrOrDomainDef=((ch.interlis.ili2c.metamodel.TypeAlias)attrType).getAliasing();
            attrType=((Domain) attrOrDomainDef).getType();
            if(attrType instanceof AbstractCoordType) {
                coordDomain=(Domain) attrOrDomainDef;
            }
        }
        AbstractCoordType coord=null;
        if(attrType instanceof AbstractCoordType) {
            coord=(AbstractCoordType)attrType;
        }else if(attrType instanceof LineType) {
            coordDomain=((LineType)attrType).getControlPointDomain();
            if(coordDomain!=null){
                attrOrDomainDef=coordDomain;
                coord=(CoordType)coordDomain.getType();
            }
        }
        if(coord==null) {
            return null;
        }
        if(coord.isGeneric()) {
            Domain concreteCoordDomains[]=((Model) attr.getContainer(Model.class)).resolveGenericDomain(coordDomain);
            HashSet<Integer> codes=new HashSet<Integer>();
            for(Domain concreteCoordDomain: concreteCoordDomains) {
                String crs=((AbstractCoordType)concreteCoordDomain.getType()).getCrs(concreteCoordDomain);
                if(crs!=null) {
                    codes.add(parseEpsgCode(crs));
                }
            }
            ArrayList<Integer> codev=new ArrayList<Integer>(codes);
            Collections.sort(codev);
            int epsgCodes[]=new int[codev.size()];
            for(int i=0;i<epsgCodes.length;i++) {
                epsgCodes[i]=codev.get(i);
            }
            return epsgCodes;
        }
        String crs=coord.getCrs(attrOrDomainDef);
        if(crs!=null) {
            if(srsModelAssignment!=null) {
                Map<ch.interlis.ili2c.metamodel.Element,ch.interlis.ili2c.metamodel.Element> srsMapping=getSrsMappingToAlternate((TransferDescription)attrOrDomainDef.getContainer(TransferDescription.class),srsModelAssignment);
                ch.interlis.ili2c.metamodel.Element alternativeAttrOrDomainDef=srsMapping.get(attrOrDomainDef);
                if(alternativeAttrOrDomainDef!=null) {
                    AbstractCoordType alternativeCoord = null;
                    if(alternativeAttrOrDomainDef instanceof AttributeDef) {
                        Type attrType2=((AttributeDef)alternativeAttrOrDomainDef).getDomain();
                        if(attrType2 instanceof ch.interlis.ili2c.metamodel.TypeAlias) {
                            alternativeAttrOrDomainDef=((ch.interlis.ili2c.metamodel.TypeAlias)attrType2).getAliasing();
                            alternativeCoord = (AbstractCoordType)((Domain)alternativeAttrOrDomainDef).getType();
                        }else {
                            alternativeCoord = (AbstractCoordType) attrType2;
                        }
                    }else {
                        alternativeCoord = (AbstractCoordType) ((Domain)alternativeAttrOrDomainDef).getType();
                    }
                    String alternativeCrs=alternativeCoord.getCrs(alternativeAttrOrDomainDef);
                    if(alternativeCrs==null) {
                        throw new IllegalArgumentException("missing CRS definition "+alternativeAttrOrDomainDef.getScopedName());
                    }
                    int epsgCodes[]=new int[2];
                    epsgCodes[0]=parseEpsgCode(crs);
                    epsgCodes[1]=parseEpsgCode(alternativeCrs);
                    return epsgCodes;
                }
            }
            int epsgCodes[]=new int[1];
            epsgCodes[0]=parseEpsgCode(crs);
            return epsgCodes;
        }
        if(defaultCrsCode==null) {
            return null;
        }
        int epsgCodes[]=new int[1];
        epsgCodes[0]=defaultCrsCode;
        return epsgCodes;
    }
    public static Map<Element, Element> getSrsMappingToOriginal(TransferDescription td, String srsModelAssignment) {
        if(td.getTransientMetaValue(SRS_MAPPING_TO_ORIGINAL)==null) {
            initSrsMapping(td, srsModelAssignment);
        }
        return (Map<Element, Element>)td.getTransientMetaValue(SRS_MAPPING_TO_ORIGINAL);
    }
    public static Map<Element, Element> getSrsMappingToAlternate(TransferDescription td, String srsModelAssignment) {
        if(td.getTransientMetaValue(SRS_MAPPING_TO_ALTERNATE)==null) {
            initSrsMapping(td, srsModelAssignment);
        }
        return (Map<Element, Element>)td.getTransientMetaValue(SRS_MAPPING_TO_ALTERNATE);
    }
    private static void initSrsMapping(TransferDescription td, String srsModelAssignment) {
        String models[]=srsModelAssignment.split("=");
        Model originalModel=(Model)td.getElement(models[0]);
        Model alternateModel=(Model)td.getElement(models[1]);
        Map<Element, Element> map2originalModel=new HashMap<Element,Element>();
        setupSrsTranslation(map2originalModel,alternateModel,originalModel);
        td.setTransientMetaValue(SRS_MAPPING_TO_ORIGINAL,map2originalModel);
        Map<Element, Element> map2alternateModel=new HashMap<Element,Element>();
        setupSrsTranslation(map2alternateModel,originalModel,alternateModel);
        td.setTransientMetaValue(SRS_MAPPING_TO_ALTERNATE,map2alternateModel);
    }
    private static void setupSrsTranslation(Map<Element, Element> mapping,Element srcEle,Element destEle){
        mapping.put(srcEle, destEle);
        if(destEle instanceof Container){
            Iterator destIt=((Container) destEle).iterator();
            Iterator srcIt=((Container) srcEle).iterator();
            while(destIt.hasNext()){
                setupSrsTranslation(mapping,(Element)srcIt.next(),(Element)destIt.next());
            }
        }
    }
    
    public static int parseEpsgCode(String crs) {
        String crsv[]=crs.split(":");
        String auth=crsv[0];
        if(!auth.equals(EPSG)) {
            throw new IllegalArgumentException("unexpected SRS authority <"+auth+">");
        }
        return Integer.parseInt(crsv[1]);
    }
    @Deprecated
    public static int getEpsgCode(AttributeDef attr, Map<String, String> genericDomains,Integer defaultCrsCode) {
        ch.interlis.ili2c.metamodel.Element attrOrDomainDef=attr;
        ch.interlis.ili2c.metamodel.Type attrType=attr.getDomain();
        Domain coordDomain=null;
        if(attrType instanceof ch.interlis.ili2c.metamodel.TypeAlias) {
            attrOrDomainDef=((ch.interlis.ili2c.metamodel.TypeAlias)attrType).getAliasing();
            attrType=((Domain) attrOrDomainDef).getType();
            if(attrType instanceof CoordType) {
                coordDomain=(Domain) attrOrDomainDef;
            }
        }
        CoordType coord=null;
        if(attrType instanceof CoordType) {
            coord=(CoordType)attrType;
        }else if(attrType instanceof LineType) {
            coordDomain=((LineType)attrType).getControlPointDomain();
            if(coordDomain!=null){
                attrOrDomainDef=coordDomain;
                coord=(CoordType)coordDomain.getType();
            }
        }
        if(coord==null) {
            throw new IllegalArgumentException(attr.getScopedName()+" is not a geometry attribute");
        }
        if(coord.isGeneric()) {
            Domain concreteCoordDomain=((Model) attr.getContainer(Model.class)).mapGenericDomain(coordDomain,genericDomains);
            String crs=((CoordType)concreteCoordDomain.getType()).getCrs(concreteCoordDomain);
            if(crs==null) {
                
            }
            int epsgCode=parseEpsgCode(crs);
            return epsgCode;
        }
        String crs=coord.getCrs(attrOrDomainDef);
        if(crs!=null) {
            int epsgCode=parseEpsgCode(crs);
            return epsgCode;
        }
        if(defaultCrsCode==null) {
            throw new IllegalArgumentException("no CRS defined for "+attr.getScopedName());
        }
        return defaultCrsCode;
    }
    public static int getEpsgCode(Viewable aclass,AttributeDef attr, Map<String, String> genericDomains,Integer defaultCrsCode) {
        attr=getAttribute(aclass, attr.getName());
        ch.interlis.ili2c.metamodel.Element attrOrDomainDef=attr;
        ch.interlis.ili2c.metamodel.Type attrType=attr.getDomain();
        Domain coordDomain=null;
        if(attrType instanceof ch.interlis.ili2c.metamodel.TypeAlias) {
            attrOrDomainDef=((ch.interlis.ili2c.metamodel.TypeAlias)attrType).getAliasing();
            attrType=((Domain) attrOrDomainDef).getType();
            if (attrType instanceof AbstractCoordType) {
                coordDomain=(Domain) attrOrDomainDef;
            }
        }
        AbstractCoordType coord=null;
        if(attrType instanceof AbstractCoordType) {
            coord=(AbstractCoordType)attrType;
        }else if(attrType instanceof LineType) {
            coordDomain=((LineType)attrType).getControlPointDomain();
            if(coordDomain!=null){
                attrOrDomainDef=coordDomain;
                coord=(CoordType)coordDomain.getType();
            }
        }
        if(coord==null) {
            throw new IllegalArgumentException(attr.getScopedName()+" is not a geometry attribute");
        }
        if(coord.isGeneric()) {
            Model model = (Model) attr.getContainer(Model.class);
            Domain concreteCoordDomain = Ili2cUtility.resolveGenericCoordDomain(model, coordDomain, null, genericDomains);
            String crs=((AbstractCoordType)concreteCoordDomain.getType()).getCrs(concreteCoordDomain);
            if(crs==null) {
                
            }
            int epsgCode=parseEpsgCode(crs);
            return epsgCode;
        }
        String crs=coord.getCrs(attrOrDomainDef);
        if(crs!=null) {
            int epsgCode=parseEpsgCode(crs);
            return epsgCode;
        }
        if(defaultCrsCode==null) {
            throw new IllegalArgumentException("no CRS defined for "+attr.getScopedName());
        }
        return defaultCrsCode;
    }
    private static AttributeDef getAttribute(Viewable aclass, String name) {
        Iterator<ViewableTransferElement> attri=aclass.getAttributesAndRoles2();
        while(attri.hasNext()) {
            ViewableTransferElement prop=attri.next();
            if(prop.obj instanceof AttributeDef) {
                if(((AttributeDef)prop.obj).getName().equals(name)) {
                    return (AttributeDef)prop.obj;
                }
            }
        }
        return null;
    }
}
