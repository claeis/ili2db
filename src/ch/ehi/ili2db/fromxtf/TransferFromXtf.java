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
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.types.OutParam;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.StatementExecutionHelper;
import ch.ehi.ili2db.converter.AbstractRecordConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.ColumnWrapper;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.StructAttrPath;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.ehi.ili2db.toxtf.TransferToXtf;
import ch.ehi.iox.objpool.ObjectPoolManager;
import ch.ehi.iox.objpool.impl.IomObjectSerializer;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AbstractPatternDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.itf.ModelUtilities;
import ch.interlis.iom_j.xtf.XtfWriter;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.IoxIliReader;
import ch.interlis.iox_j.IoxInvalidDataException;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.PipelinePool;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.filter.Rounder;
import ch.interlis.iox_j.filter.TranslateToOrigin;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.iox_j.validator.Validator;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 17.02.2005 $
 */
public class TransferFromXtf {
	private NameMapping ili2sqlName=null;
	/** mappings from xml-tags to Viewable|AttributeDef
	 */
	private HashMap tag2class=null;
	/** list of seen but unknown types; maintained to prevent duplicate error messages
	 */
	private HashSet unknownTypev=null;
	private TransferDescription td=null;
	private XtfWriter debugLogger=null;
	private Connection conn=null;
	private CustomMapping customMapping=null;
	private String schema=null; // name of dbschema or null
	private java.sql.Timestamp today=null;
	private String dbusr=null;
	private SqlColumnConverter geomConv=null;
	private boolean createStdCols=false;
	private boolean createSqlExtRef=false;
	private boolean readIliTid=false;
    private boolean readIliBid=false;
	private boolean createBasketCol=false;
	private boolean createDatasetCol=false;
	private String attachmentKey=null;
	private boolean doItfLineTables=false;
	private boolean createItfLineTables=false;
	private boolean isItfReader=false;
	private int functionCode=0;
	private String colT_ID=null;
	//private int sqlIdGen=1;
	private ch.ehi.ili2db.base.DbIdGen idGen=null;
	private XtfidPool oidPool=null;
	private ObjectPoolManager recman = null;
	private java.util.Map<String, IomObject> objPool=null;
	private HashMap<String,HashSet<Long>> existingObjectsOfCurrentBasket=null;
	private ArrayList<FixIomObjectExtRefs> delayedObjects=null;
	private TrafoConfig trafoConfig=null;
	private FromXtfRecordConverter recConv=null;
	private Viewable2TableMapping class2wrapper=null;
	private TranslateToOrigin languageFilter=null;
	private Rounder rounder=null;
	/** list of not yet processed struct values
	 */
	private ArrayList<AbstractStructWrapper> structQueue=null;
    private Integer defaultCrsCode=null;
    private String srsModelAssignment=null;
    private Map<Element,Element> crsFilter=null;
    private boolean createImportTabs=false;
    private Integer batchSize = null;
    private boolean doBatchInsert=false;
    private Map<String,StatementExecutionHelper> cachedStatementExecutionHelper=new HashMap<String,StatementExecutionHelper>();
    private Map<String,PreparedStatement> cachedPreparedStatement=new HashMap<String,PreparedStatement>();
	public TransferFromXtf(int function,NameMapping ili2sqlName1,
			TransferDescription td1,
			Connection conn1,
			String dbusr1,
			SqlColumnConverter geomConv,
			DbIdGen idGen,
			Config config,TrafoConfig trafoConfig1,Viewable2TableMapping class2wrapper1){
		ili2sqlName=ili2sqlName1;
		td=td1;
		conn=conn1;
		trafoConfig=trafoConfig1;
		dbusr=dbusr1;
		class2wrapper=class2wrapper1;
		if(dbusr==null || dbusr.length()==0){
			dbusr=System.getProperty("user.name");
		}
		schema=config.getDbschema();
		this.geomConv=geomConv;
		this.idGen=idGen;
		oidPool=new XtfidPool(idGen);
		createStdCols=Config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		if(config.getDefaultSrsCode()!=null) {
	        defaultCrsCode=Integer.parseInt(config.getDefaultSrsCode());
		}
        srsModelAssignment=config.getSrsModelAssignment();
		readIliTid=config.isImportTid();
		readIliBid=config.isImportBid();
		createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		createDatasetCol=Config.CREATE_DATASET_COL.equals(config.getCreateDatasetCols());
		doItfLineTables=config.isItfTransferfile();
		createItfLineTables=doItfLineTables && config.getDoItfLineTables();
		if(createItfLineTables){
			config.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES_DO);
		}
		createSqlExtRef=Config.SQL_EXTREF_ENABLE.equals(config.getSqlExtRefCols());
		createImportTabs=config.isCreateImportTabs();
        batchSize = config.getBatchSize();
        if(batchSize!=null) {
            doBatchInsert=true;
        }
		
		functionCode=function;
		if(!config.isVer3_translation() || config.getIli1Translation()!=null){
			languageFilter=new TranslateToOrigin(td1, config);
		}
	    if(config.getSrsModelAssignment()!=null) {
	        crsFilter=TransferFromIli.getSrsMappingToOriginal(td, config.getSrsModelAssignment());
	    }

	    if(config.isDisableRounding()) {
	        rounder=null;
	    }else {
	        rounder=new Rounder(td,config);
	    }
	}
		
    public void doitStart(Config config,Map<String,BasketStat> stat,CustomMapping customMapping1,String lastXtffilename)
    throws IoxException, Ili2dbException
    {
        if(functionCode==Config.FC_UPDATE || functionCode==Config.FC_REPLACE){
            if(!createBasketCol){
                throw new Ili2dbException("update/replace requires a basket column");
            }
        }
        if(functionCode!=Config.FC_DELETE){
            if(readIliTid && !Config.TID_HANDLING_PROPERTY.equals(config.getTidHandling())) {
                throw new Ili2dbException("TID import requires a "+DbNames.T_ILI_TID_COL+" column");
            }
        }
        debugLogger=null; //new XtfWriter(new File("debug.xtf"),td);
        if(debugLogger!=null) {
            debugLogger.write(new ch.interlis.iox_j.StartTransferEvent());
        }
        this.customMapping=customMapping1;
        globals=new Globals();
        globals.config=config;
        // limit import to given BIDs
        globals.limitedToBids=null;
        {
            String baskets=config.getBaskets();
            if(baskets!=null){
                String basketidv[]=baskets.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
                globals.limitedToBids=new HashSet<String>();
                for(String basketid:basketidv){
                    globals.limitedToBids.add(basketid);
                }
            }
        }
        // limit import to given TOPICs
        globals.limitedToTopics=null;
        {
            String topics=config.getTopics();
            if(topics!=null){
                String topicv[]=topics.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
                globals.limitedToTopics=new HashSet<String>();
                for(String topic:topicv){
                    globals.limitedToTopics.add(topic);
                }
            }
        }
        globals.datasetName=config.getDatasetName();
        globals.datasetSqlId=null;

        today=new java.sql.Timestamp(System.currentTimeMillis());
        if(doItfLineTables){
            tag2class=ch.interlis.iom_j.itf.ModelUtilities.getTagMap(td);
        }else{
            tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
        }
        unknownTypev=new HashSet();
        boolean surfaceAsPolyline=true;
        globals.ignoreUnresolvedReferences=config.isSkipReferenceErrors();
        // setup list of objects that have external/forward references
        delayedObjects=new ArrayList<FixIomObjectExtRefs>();
        recman=new ObjectPoolManager();
        {
            objPool=recman.newObjectPool(new IomObjectSerializer());
            globals.importSqlId=0;
            globals.basketSqlId=0;
            globals.startTid=0;
            globals.endTid=0;
            globals.objCountPerBasket=0;
            globals.objCount=0;
            globals.referrs=false;
            
            recConv=null;
            if(functionCode==Config.FC_DELETE || functionCode==Config.FC_REPLACE){
                if(globals.datasetName==null) {
                        throw new Ili2dbException("delete/replace requires a dataset name");
                }
                // delete existing data base on basketSqlId
                globals.datasetSqlId=Ili2db.getDatasetId(globals.datasetName,conn,config);
                if(globals.datasetSqlId==null){
                    if(functionCode==Config.FC_DELETE){
                        // nothing to do
                    }else if(functionCode==Config.FC_REPLACE){
                        // new dataset, not a replace!
                        globals.datasetSqlId=oidPool.newObjSqlId();
                        try {
                            writeDataset(globals.datasetSqlId,globals.datasetName);
                        } catch (SQLException e) {
                            EhiLogger.logError(e);
                        }
                    }
                }else{
                    deleteObjectsOfExistingDataset(globals.datasetSqlId,config);
                    if(functionCode==Config.FC_DELETE){
                        String sqlName=DbNames.DATASETS_TAB;
                        if(schema!=null){
                            sqlName=schema+"."+sqlName;
                        }
                        java.sql.PreparedStatement getstmt = null;
                        try{
                            String stmt="DELETE FROM "+sqlName+" WHERE "+colT_ID+"= ?";
                            EhiLogger.traceBackendCmd(stmt);
                            getstmt=conn.prepareStatement(stmt);
                            getstmt.setLong(1,globals.datasetSqlId);
                            getstmt.executeUpdate();
                        }catch(java.sql.SQLException ex){
                            throw new Ili2dbException("failed to delete from "+sqlName,ex);
                        }finally{
                            if(getstmt!=null){
                                try{
                                    getstmt.close();
                                    getstmt=null;
                                }catch(java.sql.SQLException ex){
                                    EhiLogger.logError(ex);
                                }
                            }
                        }
                    }
                }
            }else if(functionCode==Config.FC_UPDATE){
                if(globals.datasetName==null) {
                    throw new Ili2dbException("update requires a dataset name");
                }
                try {
                    globals.datasetSqlId=Ili2db.getDatasetId(globals.datasetName,conn,config);
                    if(globals.datasetSqlId!=null){
                    }else{
                        globals.datasetSqlId=oidPool.newObjSqlId();
                        writeDataset(globals.datasetSqlId,globals.datasetName);
                    }
                } catch (SQLException e) {
                    EhiLogger.logError(e);
                }
            }else if(functionCode==Config.FC_IMPORT){
                try {
                    if(globals.datasetName==null) {
                        globals.datasetSqlId=oidPool.newObjSqlId();
                        if(lastXtffilename!=null){
                            globals.datasetName=makeValidBid(new java.io.File(lastXtffilename).getName())+"-"+Long.toString(globals.datasetSqlId);
                        }else{
                            globals.datasetName=Long.toString(globals.datasetSqlId);
                        }
                    }else {
                        globals.datasetSqlId=Ili2db.getDatasetId(globals.datasetName,conn,config);
                        if(globals.datasetSqlId!=null){
                            throw new Ili2dbException("dataset "+globals.datasetName+" already exists");
                        }else{
                            globals.datasetSqlId=oidPool.newObjSqlId();
                        }
                    }
                    
                    if(Config.DELETE_DATA.equals(config.getDeleteMode())){
                        EhiLogger.logState("delete existing data...");
                        Set<DbTableName> tables=new HashSet<DbTableName>();
                        for(Viewable viewable:class2wrapper.getViewables()) {
                            ViewableWrapper wrapper=class2wrapper.get(viewable);
                            DbTableName sqltableName = wrapper.getSqlTable();
                            if(!tables.contains(sqltableName)) {
                                tables.add(sqltableName);
                                if(customMapping.tableExists(conn,sqltableName)) {
                                    // drop data
                                    deleteExistingObjectsHelper(sqltableName, null);
                                }
                            }
                        }
                        // drop datasets+baskets
                        DbTableName sqltableName=new DbTableName(schema,DbNames.DATASETS_TAB);
                        if(customMapping.tableExists(conn,sqltableName)) {
                            deleteExistingObjectsHelper(sqltableName, null);
                        }
                        sqltableName=new DbTableName(schema,DbNames.BASKETS_TAB);
                        if(customMapping.tableExists(conn,sqltableName)) {
                            deleteExistingObjectsHelper(sqltableName, null);
                        }
                    }

                    
                    writeDataset(globals.datasetSqlId,globals.datasetName);
                } catch (SQLException e) {
                    EhiLogger.logError(e);
                }
            }else {
                throw new IllegalArgumentException("unexpected function code "+functionCode);
            }            
            if(functionCode==Config.FC_DELETE){
                return;
            }
            globals.validator=null;
            if(config.isValidation()){
                ValidationConfig modelConfig=new ValidationConfig();
                modelConfig.mergeIliMetaAttrs(td);
                String configFilename=config.getValidConfigFile();
                if(configFilename!=null){
                    try {
                        modelConfig.mergeConfigFile(new File(configFilename));
                    } catch (java.io.IOException e) {
                        EhiLogger.logError("failed to read validator config file <"+configFilename+">");
                    }
                }
                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.AREA_OVERLAP_VALIDATION, config.isDisableAreaValidation()?ValidationConfig.OFF:null);
                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.DEFAULT_GEOMETRY_TYPE_VALIDATION, config.isSkipGeometryErrors()?ValidationConfig.OFF:null);
                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ALLOW_ONLY_MULTIPLICITY_REDUCTION, config.isOnlyMultiplicityReduction()?ValidationConfig.ON:null);
                if(rounder==null) {
                    modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.DISABLE_ROUNDING, ValidationConfig.TRUE);
                }
                IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
                globals.errFactory=new LogEventFactory();
                globals.pipelinePool=new PipelinePool();
                globals.validator=new ch.interlis.iox_j.validator.Validator(td,modelConfig, errHandler, globals.errFactory, globals.pipelinePool,config);               
                globals.validator.setAutoSecondPass(false);
            }
        }
    }
    private String makeValidBid(String name) {
        if(name==null) {
            return null;
        }
        StringBuffer buf=new StringBuffer();
        for(char c:name.toCharArray()) {
            if(Character.isLetter(c) || c=='_') {
                buf.append(c);
            }else if((c=='-' || c=='.' || Character.isDigit(c)) && buf.length()>0) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    public static class Globals {
        String datasetName=null;
        Long datasetSqlId=null;
        ch.interlis.iox_j.validator.Validator validator=null;
        LogEventFactory errFactory=null;
        PipelinePool pipelinePool=null;
        HashSet<String> limitedToTopics=null;
        HashSet<String> limitedToBids=null;
        long importSqlId=0;
        long basketSqlId=0;
        long startTid=0;
        long endTid=0;
        long objCountPerBasket=0;
        long objCount=0;
        boolean referrs=false;
        HashMap<String, ClassStat> objStat=null;            

        Config config=null;
        boolean ignoreUnresolvedReferences=false;
        
    }
    private Globals globals=null;
	public void doit(String xtffilename,IoxReader reader,Map<String,BasketStat> stat)
	throws IoxException, Ili2dbException
	{
        
		isItfReader=reader instanceof ItfReader;
		structQueue=new ArrayList<AbstractStructWrapper>();

        recConv=new FromXtfRecordConverter(td,ili2sqlName,tag2class,globals.config,idGen,geomConv,conn,dbusr,isItfReader,oidPool,trafoConfig,class2wrapper,globals.datasetName,schema);
		
		try{
			if(functionCode==Config.FC_DELETE){
				return;
			}
			
			if(reader instanceof ItfReader) {
				((ItfReader)reader).setBidPrefix(globals.datasetName);		
			}else if(reader instanceof ItfReader2) {
				((ItfReader2)reader).setBidPrefix(globals.datasetName);		
			}
			if(globals.limitedToTopics!=null && globals.limitedToTopics.size()>0 && reader instanceof IoxIliReader) {
			    String topics[]=new String[globals.limitedToTopics.size()];
			    int idx=0;
			    for(String topic:globals.limitedToTopics) {
			        topics[idx++]=topic;
			    }
			    ((IoxIliReader)reader).setTopicFilter(topics);
			}

			if(globals.validator!=null){
				globals.errFactory.setDataSource(xtffilename);
				if(reader instanceof ItfReader2){
					((ItfReader2) reader).setIoxDataPool(globals.pipelinePool);
				}
			}
			
            if(functionCode==Config.FC_REPLACE || functionCode==Config.FC_UPDATE || functionCode==Config.FC_IMPORT){
                if(globals.config.isCreateImportTabs()) {
                    try {
                        globals.importSqlId=writeImportStat(globals.datasetSqlId,xtffilename,today,dbusr);
                    } catch (SQLException e) {
                        EhiLogger.logError(e);
                    } catch (ConverterException e) {
                        EhiLogger.logError(e);
                    }
                }
            }            
			
			StartBasketEvent basket=null;
            long startTime=System.currentTimeMillis();
            long currentSlice=0l;
			// more baskets?
			IoxEvent event=reader.read();
			try{
				boolean skipBasket=false;
				while(event!=null){
					if(event instanceof StartBasketEvent){
						basket=(StartBasketEvent)event;
						// do not import this this basket? 
						if((globals.limitedToBids!=null && !globals.limitedToBids.contains(basket.getBid()))
								|| (globals.limitedToTopics!=null && !globals.limitedToTopics.contains(basket.getType()))){
							// do not import this basket
							skipBasket=true;
							EhiLogger.logState("Skip Basket "+basket.getType()+"(oid "+basket.getBid()+")");
						}else{
							// import this basket
							EhiLogger.logState("Basket "+basket.getType()+"(oid "+basket.getBid()+")...");
							skipBasket=false;
							try {
	                            if(rounder!=null) {
	                                event=rounder.filter(event);
	                            }
								if(globals.validator!=null) {
								    globals.validator.validate(event);
								}
								if(languageFilter!=null){
									event=languageFilter.filter(event);
								}
								Long existingBasketSqlId=null;
								if(functionCode==Config.FC_UPDATE){
									// read existing oid/sqlid mapping (but might also be a new basket)
									existingObjectsOfCurrentBasket=new HashMap<String,HashSet<Long>>();
                                    Topic topic=(Topic)td.getElement(basket.getType());
                                    boolean hasBid=topic.getBasketOid()!=null;
									if(readIliBid || hasBid) {
	                                    existingBasketSqlId=readExistingSqlObjIds(reader instanceof ItfReader,basket.getBid());
	                                    if(existingBasketSqlId==null){
	                                        // new basket 
	                                        globals.basketSqlId=oidPool.getBasketSqlId(basket.getBid());
	                                    }else{
	                                        // existing basket
	                                        globals.basketSqlId=existingBasketSqlId;
	                                        // drop existing structeles
	                                        dropExistingStructEles(basket.getType(),globals.basketSqlId);
	                                    }
									}else {
									    String topicv[]=new String[] {basket.getType()};
						                Configuration dummy=new Configuration();
                                        long[] basketSqlIds = Ili2db.getBasketSqlIdsFromTopic(topicv,dummy,conn,globals.config);
									    for(long oldBasketSqlId:basketSqlIds) {
	                                        // read existing objects from baskets with same topic
									        readExistingSqlObjIds(reader instanceof ItfReader,topic,oldBasketSqlId);
	                                        // drop existing sturct eles
                                            dropExistingStructEles(basket.getType(),oldBasketSqlId);
									    }
									    // remove old baskets from dataset
									    removeBasketsFromDataset(basketSqlIds);
									    // new basket
									    existingBasketSqlId=null;
									    globals.basketSqlId=oidPool.getBasketSqlId(basket.getBid());
									}
								}else{
								    globals.basketSqlId=oidPool.getBasketSqlId(basket.getBid());
								}
								if(attachmentKey==null){
									if(xtffilename!=null){
										attachmentKey=new java.io.File(xtffilename).getName()+"-"+Long.toString(globals.basketSqlId);
									}else{
										attachmentKey=Long.toString(globals.basketSqlId);
									}
									globals.config.setAttachmentKey(attachmentKey);
								}
								if(existingBasketSqlId==null){
								    Topic topic=(Topic)td.getElement(basket.getType());
								    boolean hasBid=topic.getBasketOid()!=null;
									writeBasket(globals.datasetSqlId,basket,globals.basketSqlId,attachmentKey,hasBid?hasBid:readIliBid,basket.getDomains());
								}else{
									// TODO update attachmentKey of existing basket
								    // TODO update domains of existing basket
								}
							} catch (SQLException ex) {
								EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
							} catch (ConverterException ex) {
								EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
							}
							globals.startTid=oidPool.getLastSqlId();
							globals.objStat=new HashMap<String, ClassStat>();
							globals.objCountPerBasket=0;
                            String filename=null;
                            if(xtffilename!=null){
                                filename=new java.io.File(xtffilename).getName();
                            }
                            // save it for later output to log
                            stat.put(Long.toString(globals.basketSqlId),new BasketStat(filename,basket.getType(),basket.getBid(),globals.objStat));
						}
					}else if(event instanceof EndBasketEvent){
						if(reader instanceof ItfReader2){
				        	ArrayList<IoxInvalidDataException> dataerrs = ((ItfReader2) reader).getDataErrs();
				        	if(dataerrs.size()>0){
				        		if(!skipBasket){
				        			if(!globals.config.isSkipGeometryErrors()){
						        		for(IoxInvalidDataException dataerr:dataerrs){
						        			EhiLogger.logError(dataerr);
						        		}
				        			}
				        		}
				        		((ItfReader2) reader).clearDataErrs();
				        	}
						}
						if(!skipBasket){
	                        if(rounder!=null) {
	                            event=rounder.filter(event);
	                        }
	                        if(globals.validator!=null) {
	                            globals.validator.validate(event);
	                        }
	                        if(languageFilter!=null){
	                            event=languageFilter.filter(event);
	                        }
							// fix external/forward references
							ArrayList<FixIomObjectExtRefs> fixedObjects=new ArrayList<FixIomObjectExtRefs>();
							for(FixIomObjectExtRefs fixref : delayedObjects){
								boolean skipObj=false;
								for(IomObject ref:fixref.getRefs()){
                                    boolean targetObjFound=false;
									for(Viewable aclass:fixref.getTargetClass(ref)) {
	                                    String xtfid=ref.getobjectrefoid();
	                                    if(aclass instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)aclass).getOid())) {
	                                        xtfid=Validator.normalizeUUID(xtfid);
	                                    }
	                                    String rootClassName=Ili2cUtility.getRootViewable(aclass).getScopedName(null);
	                                    if(oidPool.containsXtfid(rootClassName,xtfid)){
	                                        // reference now resolvable
	                                        targetObjFound=true;
	                                        break;
	                                    }
									}
									if(!targetObjFound) {
                                        // reference not yet known, try to resolve again at end of transfer
                                        skipObj=true;
                                        break;
									}
								}
								if(!skipObj){
								    HashMap<String, ClassStat> fixrefObjStat=stat.get(Long.toString(fixref.getBasketSqlId())).getObjStat();
									doObject(globals.datasetName,fixref.getBasketSqlId(),fixref.getGenericDomains(),objPool.get(fixref.getRootTid()),fixrefObjStat);
									fixedObjects.add(fixref);
								}
							}
							delayedObjects.removeAll(fixedObjects);
							if(functionCode==Config.FC_UPDATE){
								// delete no longer existing objects
								// also delete objects that still have missing references (delayedObjects)!
								deleteExisitingObjects(existingObjectsOfCurrentBasket);

								// clear existingObjectsOfCurrentBasket so it reflects the state of the DB.
								// existingObjectsOfCurrentBasket is later used to decide whether to use an INSERT or UPDATE statement.
								existingObjectsOfCurrentBasket.clear();
							}

							// TODO update import counters
							globals.endTid=oidPool.getLastSqlId();
							try {
								if(globals.config.isCreateImportTabs()) {
	                                long importId=writeImportBasketStat(globals.importSqlId,globals.basketSqlId,globals.startTid,globals.endTid,globals.objCountPerBasket);
	                                writeObjStat(importId,globals.basketSqlId,globals.objStat);
								}
							} catch (SQLException ex) {
								EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
							} catch (ConverterException ex) {
								EhiLogger.logError("Basket "+basket.getType()+"(oid "+basket.getBid()+")",ex);
							}
						}
						
						skipBasket=false;
					}else if(event instanceof ObjectEvent){
						if(!skipBasket){
	                        if(rounder!=null) {
	                            event=rounder.filter(event);
	                        }
	                        if(globals.validator!=null) {
	                            globals.validator.validate(event);
	                        }
							if(languageFilter!=null){
								event=languageFilter.filter(event);
							}
							globals.objCountPerBasket++;
							globals.objCount++;
							IomObject iomObj=((ObjectEvent)event).getIomObject();
							if(allReferencesKnown(globals.basketSqlId,basket.getDomains(),iomObj)){
								// translate object
								doObject(globals.datasetName,globals.basketSqlId, basket.getDomains(),iomObj,globals.objStat);
							}
		                    long currentTime=System.currentTimeMillis();
		                    long slice=(currentTime-startTime)/1000l/60l/10l;
		                    if(slice>currentSlice) {
		                        currentSlice=slice;
		                        //EhiLogger.logState("...object count "+validator.getObjectCount()+" (structured elements "+validator.getStructCount()+")...");
		                        EhiLogger.logState("...object count "+globals.objCount);
		                    }
						}
					}else if(event instanceof EndTransferEvent){
                        if(rounder!=null) {
                            event=rounder.filter(event);
                        }
						if(globals.validator!=null) {
						    globals.validator.validate(event);
						}
						if(languageFilter!=null){
							event=languageFilter.filter(event);
						}
						
                        EhiLogger.traceState("...EndTransferEvent done");
                        EhiLogger.logState("object count "+globals.objCount);
						
						break;
					}else if(event instanceof StartTransferEvent){
                        if(rounder!=null) {
                            event=rounder.filter(event);
                        }
						if(globals.validator!=null) {
						    globals.validator.validate(event);
						}
						if(languageFilter!=null){
							event=languageFilter.filter(event);
						}
					}
					event=reader.read();
				}
			}finally{
			}
		}finally{
		}
		
		
	}
    public void doitEnd(Map<String,BasketStat> stat)
    throws IoxException, Ili2dbException
    {
        {
            {
                EhiLogger.traceState("write delayed objects ("+delayedObjects.size()+")...");
                for(FixIomObjectExtRefs fixref : delayedObjects){
                    boolean skipObj=false;
                    for(IomObject ref:fixref.getRefs()){
                        boolean foundTargetObj=false;
                        for(Viewable aclass:fixref.getTargetClass(ref)) {
                            String xtfid=ref.getobjectrefoid();
                            String rootClassName=Ili2cUtility.getRootViewable(aclass).getScopedName(null);
                            if(aclass instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)aclass).getOid())) {
                                xtfid=Validator.normalizeUUID(xtfid);
                            }
                            if(oidPool.containsXtfid(rootClassName,xtfid)){
                                foundTargetObj=true;
                                break;
                            }else {
                                if(fixref.isExternalTarget(ref) && (readIliTid || Ili2cUtility.isViewableWithOid(aclass))){
                                    // read object
                                    Long sqlid=readObjectSqlid(aclass,xtfid);
                                    if(sqlid!=null){
                                        foundTargetObj=true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(!foundTargetObj){
                            if(!globals.ignoreUnresolvedReferences){
                                EhiLogger.logError("unknown referenced object "+getScopedNames(fixref.getTargetClass(ref))+" TID "+ref.getobjectrefoid()+" referenced from "+fixref.getRootTag()+" TID "+fixref.getRootTid());
                                globals.referrs=true;
                                skipObj=true;
                            }
                        }
                    }
                    if(!skipObj){
                        globals.objStat=stat.get(Long.toString(fixref.getBasketSqlId())).getObjStat();
                        doObject(globals.datasetName,fixref.getBasketSqlId(),fixref.getGenericDomains(),objPool.get(fixref.getRootTid()),globals.objStat);
                    }
                }
            }
            flushBatchedRecords();
        }
        if(globals.validator!=null){
            globals.validator.doSecondPass();
        }
        {
            if(globals.referrs){
                throw new IoxException("dangling references");
            }
            
        }
    }
    private String getScopedNames(Iterable<Viewable> targetClassv) {
        StringBuffer ret=new StringBuffer();
        String sep="";
        for(Viewable targetClass : targetClassv) {
            ret.append(sep);
            ret.append(targetClass.getScopedName());
            sep=" OR ";
        }
        return ret.toString();
    }

    public void doitFinally()
    {
        {
            if(rounder!=null) {
                rounder.close();
                rounder=null;
            }
            if(globals.validator!=null){
                globals.validator.close();
                globals.validator=null;
            }
            if(languageFilter!=null){
                languageFilter.close();
            }
            closePreparedStatements();
            if(debugLogger!=null) {
                try {
                    debugLogger.write(new ch.interlis.iox_j.EndTransferEvent());
                    debugLogger.close();
                } catch (IoxException e) {
                    EhiLogger.logError("failed to write to debugLogger",e);
                }
                debugLogger=null;
            }
        }
        {
            recman.close();
        }
    }

    private void dropExistingStructEles(String topic, long basketSqlId) throws Ili2dbException {
		// get all structs that are reachable from this topic
		HashSet<AbstractClassDef> classv=getStructs(topic);
		// delete all structeles
		HashSet<ViewableWrapper> visitedTables=new HashSet<ViewableWrapper>();
		for(AbstractClassDef aclass1:classv){

            if(languageFilter!=null){
                aclass1 = (AbstractClassDef)translateViewable(aclass1);
            }
			ViewableWrapper wrapper = class2wrapper.get(aclass1);
			
			 while(wrapper!=null){
				 {
						if (!visitedTables.contains(wrapper)) {
							visitedTables.add(wrapper);
							// if table exists?
							// get sql name
							DbTableName sqlName = wrapper.getSqlTable();
							if (customMapping.tableExists(conn, sqlName)) {
								// delete it
								dropRecords(sqlName, basketSqlId);
							} else {
								// skip it; no table
							}
						}
				 }
				wrapper=wrapper.getExtending();
			 }
			
			
			
		}
	}

	private HashSet<AbstractClassDef> getStructs(String topicQName) {
		Topic def=TransferToXtf.getTopicDef(td, topicQName);
		HashSet<AbstractClassDef> visitedStructs=new HashSet<AbstractClassDef>();
		while(def!=null){
			Iterator classi=def.iterator();
			while(classi.hasNext()){
				Object classo=classi.next();
				if(classo instanceof Viewable){
					if(classo instanceof Table && ((Table)classo).isIdentifiable()){
						getStructs_Helper((AbstractClassDef)classo,visitedStructs);
					}else if(classo instanceof AssociationDef && !((AssociationDef)classo).isIdentifiable()){
					    // handle associations without an OID like struct eles
					    visitedStructs.add((AssociationDef)classo);
                    }
				}
			}
			def=(Topic)def.getExtending();
		}

		return visitedStructs;
	}
	private void getStructs_Helper(AbstractClassDef aclass,HashSet<AbstractClassDef> accu) {
		if(accu.contains(aclass)){
			return;
		}
		java.util.Set seed=null;
		if(aclass instanceof Table && !((Table)aclass).isIdentifiable()){
			// STRUCTURE
			seed=aclass.getExtensions();
		}else{
			// CLASS
			seed=new HashSet();
			seed.add(aclass);
		}
		for(Object defo:seed){
			AbstractClassDef def=(AbstractClassDef) defo;
			if(accu.contains(def)){
				continue;
			}
			if(def instanceof Table && !((Table)def).isIdentifiable()){
				accu.add(def);
			}
			while(def!=null){
				Iterator attri=def.iterator();
				while(attri.hasNext()){
					Object attro=attri.next();
					if(attro instanceof AttributeDef){
						AttributeDef attr=(AttributeDef)attro;
						Type type=attr.getDomain();
						if(type instanceof CompositionType){
							CompositionType compType=(CompositionType)type;
							getStructs_Helper(compType.getComponentType(),accu);
							Iterator resti=compType.iteratorRestrictedTo();
							while(resti.hasNext()){
								AbstractClassDef rest=(AbstractClassDef)resti.next();
								getStructs_Helper(rest,accu);
							}
						}
					}
				}
				// base viewable
				def=(AbstractClassDef)def.getExtending();
				if(accu.contains(def)){
					def=null;
				}
			}
		}
	}

	private void dropRecords(DbTableName sqlTableName, long basketSqlId) {
		// DELETE FROM products WHERE t_id in (10,20);
		String stmt = "DELETE FROM "+sqlTableName.getQName()+" WHERE "+DbNames.T_BASKET_COL+"="+basketSqlId;
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement dbstmt = null;
		try {

			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
			dbstmt.executeUpdate();
		} catch (java.sql.SQLException ex) {
			EhiLogger.logError("failed to delete from " + sqlTableName,	ex);
		} finally {
			if (dbstmt != null) {
				try {
					dbstmt.close();
				} catch (java.sql.SQLException ex) {
					EhiLogger.logError("failed to close delete stmt of "+ sqlTableName, ex);
				}
			}
		}
	}

	private void deleteExisitingObjects(HashMap<String,HashSet<Long>> existingObjects) {
		for(String sqlType:existingObjects.keySet()){
			HashSet<Long> objs=existingObjects.get(sqlType);
			StringBuilder ids=new StringBuilder();
			String sep="";
			if(objs.size()>0){
				for(Long sqlId:objs){
					ids.append(sep);
					ids.append(sqlId);
					sep=",";
				}
				Object classo=tag2class.get(ili2sqlName.mapSqlTableName(sqlType));
				if(classo instanceof Viewable){
					Viewable aclass=(Viewable) classo;
					while(aclass!=null){
						deleteExistingObjectsHelper(recConv.getSqlType(aclass),ids.toString());
						aclass=(Viewable) aclass.getExtending();
					}
				}else if(classo instanceof AttributeDef){
				    AttributeDef geomAttr=(AttributeDef)classo;
				    int epsgCodes[]=TransferFromIli.getEpsgCodes(geomAttr, srsModelAssignment,defaultCrsCode);
				    for(int epsgCode:epsgCodes) {
	                    deleteExistingObjectsHelper(getSqlTableNameItfLineTable(geomAttr,epsgCode),ids.toString());
				    }
				}else{
					throw new IllegalStateException("unexpetced sqlType <"+sqlType+">");
				}
			}
		}
	}

	private void deleteExistingObjectsHelper(DbTableName sqlTableName,
			String ids) {
		// DELETE FROM products WHERE t_id in (10,20);
		String stmt = null;
		if(ids!=null) {
	        stmt="DELETE FROM "+sqlTableName.getQName()+" WHERE "+colT_ID+" in ("+ids+")";
		}else {
	        stmt="DELETE FROM "+sqlTableName.getQName();
		}
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement dbstmt = null;
		try {

			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
			dbstmt.executeUpdate();
		} catch (java.sql.SQLException ex) {
			EhiLogger.logError("failed to delete from " + sqlTableName,	ex);
		} finally {
			if (dbstmt != null) {
				try {
					dbstmt.close();
				} catch (java.sql.SQLException ex) {
					EhiLogger.logError("failed to close delete stmt of "+ sqlTableName, ex);
				}
			}
		}
	}

    private Long readExistingSqlObjIds(boolean isItf,String bid) throws Ili2dbException {
        StringBuilder topicQName=new StringBuilder();
        Long basketSqlId=Ili2db.getBasketSqlIdFromBID(bid, conn, schema,colT_ID, topicQName);
        if(basketSqlId==null){
            // new basket
            return null;
        }
        Topic topic=TransferToXtf.getTopicDef(td, topicQName.toString());
        if(topic==null){
            throw new Ili2dbException("unkown topic "+topicQName.toString());
        }
        readExistingSqlObjIds(isItf,topic, basketSqlId);
        return basketSqlId;
    }
	private void readExistingSqlObjIds(boolean isItf,Topic topic, long basketSqlId) throws Ili2dbException {
		Model model=(Model) topic.getContainer();
		// for all Viewables
		Iterator iter = null;
		if(isItf){
			ArrayList itftablev=ModelUtilities.getItfTables(td,model.getName(),topic.getName());
			iter=itftablev.iterator();
		}else{
			iter=getXtfTables(td, topic).iterator();
		}
		HashSet<String> visitedTables=new HashSet<String>();
		while (iter.hasNext())
		{
		  Object obj = iter.next();
		  if(obj instanceof Viewable){
			  if((obj instanceof View) && !TransferFromIli.isTransferableView(obj)){
				  // skip it
			  }else if (!TransferToXtf.suppressViewable ((Viewable)obj))
			  {
				Viewable aclass=(Viewable)obj;
				if(aclass.isAbstract()){
					throw new IllegalArgumentException("unexpected abstract viewable "+aclass.getScopedName(null));
				}
				if(aclass instanceof AssociationDef && !((AssociationDef)aclass).isIdentifiable()) {
				    continue;
				}
				// get sql name
				DbTableName sqlName=recConv.getSqlType(aclass);
				ViewableWrapper wrapper=recConv.getViewableWrapper(sqlName.getName());
				ViewableWrapper base=wrapper.getExtending();
				while(base!=null){
					wrapper=base;
					base=wrapper.getExtending();
				}
				sqlName=wrapper.getSqlTable();
				if(!visitedTables.contains(sqlName.getQName())){
					visitedTables.add(sqlName.getQName());
					// if table exists?
					if(customMapping.tableExists(conn,sqlName)){
						// dump it
						EhiLogger.logState(aclass.getScopedName(null)+" read ids...");
						readObjectSqlIds(!wrapper.includesMultipleTypes(),sqlName,basketSqlId);
					}else{
						// skip it
						EhiLogger.traceUnusualState(aclass.getScopedName(null)+"...skipped; no table "+sqlName+" in db");
					}
				}
			  }
			  
		  }else if(obj instanceof AttributeDef){
			  if(isItf){
					AttributeDef attr=(AttributeDef)obj;
					int epsgCodes[]=TransferFromIli.getEpsgCodes(attr, srsModelAssignment,defaultCrsCode);
					for(int epsgCode:epsgCodes) {
	                    // get sql name
	                    DbTableName sqlName=getSqlTableNameItfLineTable(attr,epsgCode);
	                    // if table exists?
	                    if(customMapping.tableExists(conn,sqlName)){
	                        // dump it
	                        EhiLogger.logState(attr.getContainer().getScopedName(null)+"_"+attr.getName()+" read ids...");
	                        readObjectSqlIds(isItf,sqlName,basketSqlId);
	                    }else{
	                        // skip it
	                        EhiLogger.traceUnusualState(attr.getScopedName(null)+"...skipped; no table "+sqlName+" in db");
	                    }
					}
			  }
		  }
		  
		}		
	}
	static private  ArrayList<Viewable> getXtfTables(TransferDescription td, Topic topic) {
		ArrayList<Viewable> ret= new ArrayList<Viewable>();
	    Iterator iter = topic.getViewables().iterator();
	    while (iter.hasNext())
	    {
	      Object obj = iter.next();
	      if ((obj instanceof Viewable) && !AbstractPatternDef.suppressViewableInTransfer((Viewable)obj))
	      {
				Viewable v = (Viewable) obj;		
				ret.add(v);
	      }	
	    }
		return ret;
	}

	private void deleteObjectsOfExistingDataset(long datasetSqlId,Config config) throws Ili2dbException {
		// get basket id, topicname
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.PreparedStatement getstmt=null;
        java.sql.ResultSet res=null;
		try{
			String stmt="SELECT "+colT_ID+","+DbNames.BASKETS_TAB_TOPIC_COL+" FROM "+sqlName+" WHERE "+DbNames.BASKETS_TAB_DATASET_COL+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setLong(1,datasetSqlId);
			res=getstmt.executeQuery();
			while(res.next()){
				long sqlId=res.getLong(1);
				String topicQName=res.getString(2);
				deleteObjectsOfBasket(sqlId,topicQName);
			}
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to query "+sqlName,ex);
		}finally{
            if(res!=null){
                try{
                    res.close();
                    res=null;
                }catch(java.sql.SQLException ex){
                    EhiLogger.logError(ex);
                }
            }
			if(getstmt!=null){
				try{
					getstmt.close();
					getstmt=null;
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		try{
			String stmt="DELETE FROM "+sqlName+" WHERE "+DbNames.BASKETS_TAB_DATASET_COL+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setLong(1,datasetSqlId);
			getstmt.executeUpdate();
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to delete from "+sqlName,ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
					getstmt=null;
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
	}
	private void deleteObjectsOfBasket(long basketSqlId,String topicQName) throws Ili2dbException {
		boolean isItf=false;
		Topic topic=TransferToXtf.getTopicDef(td, topicQName);
		if(topic==null){
			throw new Ili2dbException("unkown topic "+topicQName.toString());
		}
		Model model=(Model) topic.getContainer();
		// for all Viewables
		// see also export
		Iterator iter = null;
		if(isItf){
			ArrayList itftablev=ModelUtilities.getItfTables(td,model.getName(),topic.getName());
			iter=itftablev.iterator();
		}else{
			// get transferable viewables of topic
			iter= topic.getViewables().iterator();
		}
		
		
		
		HashSet<ViewableWrapper> visitedTables=new HashSet<ViewableWrapper>();
		while (iter.hasNext())
		{
		  Object obj = iter.next();
		  if(obj instanceof Viewable){
			  if((obj instanceof View) && !TransferFromIli.isTransferableView(obj)){
				  // skip it
			  }else if (!TransferToXtf.suppressViewable ((Viewable)obj))
			  {
					Viewable aclass1 = (Viewable) obj;
					if(languageFilter!=null){
					    aclass1 = translateViewable(aclass1);
					}
					ViewableWrapper wrapper = class2wrapper.get(aclass1);
					
					 while(wrapper!=null){
						 {
								if (!visitedTables.contains(wrapper)) {
									visitedTables.add(wrapper);
									// if table exists?
									// get sql name
									DbTableName sqlName = wrapper.getSqlTable();
									if (customMapping.tableExists(conn, sqlName)) {
										// delete it
										dropRecords(sqlName, basketSqlId);
									} else {
										// skip it; no table
									}
								}
						 }
						for(ViewableWrapper secondary:wrapper.getSecondaryTables()){
							if (!visitedTables.contains(secondary)) {
								visitedTables.add(secondary);
								// if table exists?
								// get sql name
								DbTableName sqlName = secondary.getSqlTable();
								if (customMapping.tableExists(conn, sqlName)) {
									// delete it
									dropRecords(sqlName, basketSqlId);
								} else {
									// skip it; no table
								}
							}
						}
						wrapper=wrapper.getExtending();
					 }
			  }
			  
		  }else if(obj instanceof AttributeDef){
			  if(isItf){
					AttributeDef attr=(AttributeDef)obj;
					int epsgCodes[]=TransferFromIli.getEpsgCodes(attr, srsModelAssignment,defaultCrsCode);
					for(int epsgCode:epsgCodes) {
	                    // get sql name
	                    DbTableName sqlName=getSqlTableNameItfLineTable(attr,epsgCode);
	                    // if table exists?
	                    if(customMapping.tableExists(conn,sqlName)){
	                        dropRecords(sqlName, basketSqlId);
	                    }else{
	                        // skip it; no table
	                    }
					}
			  }
		  }
		}		
		dropExistingStructEles(topicQName,basketSqlId);
		
		if(createImportTabs) {
	        String sqlName=DbNames.IMPORTS_BASKETS_TAB;
	        if(schema!=null){
	            sqlName=schema+"."+sqlName;
	        }
	        java.sql.PreparedStatement getstmt=null;
	        try{
	            String stmt="DELETE FROM "+sqlName+" WHERE "+DbNames.IMPORTS_BASKETS_TAB_BASKET_COL+"= ?";
	            EhiLogger.traceBackendCmd(stmt);
	            getstmt=conn.prepareStatement(stmt);
	            getstmt.setLong(1,basketSqlId);
	            getstmt.executeUpdate();
	        }catch(java.sql.SQLException ex){
	            throw new Ili2dbException("failed to delete from "+sqlName,ex);
	        }finally{
	            if(getstmt!=null){
	                try{
	                    getstmt.close();
	                    getstmt=null;
	                }catch(java.sql.SQLException ex){
	                    EhiLogger.logError(ex);
	                }
	            }
	        }
		}
		
	}

    private Viewable translateViewable(Viewable aclass1) throws Ili2dbException {
        IomObject iomObj=new Iom_jObject(aclass1.getScopedName(),"dummy");
        IoxEvent event=new ObjectEvent(iomObj);
        try {
            event=languageFilter.filter(event);
            iomObj=((ObjectEvent) event).getIomObject();
        } catch (IoxException e) {
            throw new Ili2dbException("failed to translate "+aclass1.getScopedName(),e);
        }
        if(!iomObj.getobjecttag().equals(aclass1.getScopedName())) {
           aclass1=(Viewable)tag2class.get(iomObj.getobjecttag()); 
        }
        return aclass1;
    }

	private void doObject(String datasetName,long basketSqlId, Map<String,String> genericDomains,IomObject iomObj,Map<String, ClassStat> objStat) {
		try{
			//EhiLogger.debug(iomObj.toString());
			writeObject(datasetName,basketSqlId,genericDomains,iomObj,null,objStat);
		}catch(ConverterException ex){
            EhiLogger.traceState(iomObj.toString());
            EhiLogger.logError("Object "+iomObj.getobjectoid()+" at (line "+iomObj.getobjectline()+",col "+iomObj.getobjectcol()+")",ex);
		    if(debugLogger!=null) {
                try {
                    debugLogger.write(new ObjectEvent(iomObj));
                } catch (IoxException e) {
                    EhiLogger.logError("failed to write to debugLogger",ex);
                }
		    }
		}catch(java.sql.SQLException ex){
            EhiLogger.traceState(iomObj.toString());
            EhiLogger.logError("Object "+iomObj.getobjectoid()+" at (line "+iomObj.getobjectline()+",col "+iomObj.getobjectcol()+")",ex);
            if(debugLogger!=null) {
                try {
                    debugLogger.write(new ObjectEvent(iomObj));
                } catch (IoxException e) {
                    EhiLogger.logError("failed to write to debugLogger",ex);
                }
            }
		}catch(java.lang.RuntimeException ex){
            EhiLogger.traceState(iomObj.toString());
            if(debugLogger!=null) {
                try {
                    debugLogger.write(new ObjectEvent(iomObj));
                } catch (IoxException e) {
                    EhiLogger.logError("failed to write to debugLogger",ex);
                }
            }
			throw ex;
		}
		while(!structQueue.isEmpty()){
			AbstractStructWrapper struct=structQueue.remove(0); // get front
			try{
				writeObject(datasetName,basketSqlId,genericDomains,struct.getStruct(),struct,objStat);
			}catch(ConverterException ex){
				EhiLogger.logError("Object "+iomObj.getobjectoid()+"; Struct at (line "+struct.getStruct().getobjectline()+",col "+struct.getStruct().getobjectcol()+")",ex);
			}catch(java.sql.SQLException ex){
				EhiLogger.logError("Object "+iomObj.getobjectoid()+"; Struct at (line "+struct.getStruct().getobjectline()+",col "+struct.getStruct().getobjectcol()+")",ex);
			}catch(java.lang.RuntimeException ex){
				EhiLogger.traceState(iomObj.toString());
				throw ex;
			}
		}
	}
	private boolean allReferencesKnown(long basketSqlId,Map<String,String> genericDomains,IomObject iomObj) {
		String tag=iomObj.getobjecttag();
		//EhiLogger.debug("tag "+tag);
		Object modelele=tag2class.get(tag);
		if(modelele==null){
			return true;
		}
		// is it a SURFACE or AREA line table?
		if(createItfLineTables && modelele instanceof AttributeDef){
			return true;
		}
	 	String tid=iomObj.getobjectoid();
	 	if((tid==null || tid.length()==0) && modelele instanceof AssociationDef){
	 		tid = getAssociationId(iomObj,(AssociationDef)modelele);
	 	}
	 	if(tid!=null && tid.length()>0){
            if(modelele instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)modelele).getOid())) {
                tid=Validator.normalizeUUID(tid);
            }
			oidPool.createObjSqlId(Ili2cUtility.getRootViewable(getCrsMappedOrSame((Viewable) modelele)).getScopedName(null),tag,tid);
	 	}
        objPool.put(tid,iomObj);
        FixIomObjectExtRefs extref=new FixIomObjectExtRefs(basketSqlId,genericDomains,tag,tid);
        allReferencesKnownHelper(iomObj, extref);
        if(!extref.needsFixing()){
            return true;
        }
        //EhiLogger.debug("needs fixing "+iomObj.getobjectoid());
        delayedObjects.add(extref);
		return false;
	}

	private String getAssociationId(IomObject iomObj, AssociationDef modelele) {
		String tag=modelele.getScopedName(null);
		String tid;
		Iterator<ViewableTransferElement> rolei=modelele.getAttributesAndRoles2();
	 		String sep="";
	 		tid="";
	 		while(rolei.hasNext()){
	 			ViewableTransferElement prop=rolei.next();
	 			if(prop.obj instanceof RoleDef && !prop.embedded){
	 				String roleName=((RoleDef) prop.obj).getName();
	 				IomObject refObj=iomObj.getattrobj(roleName, 0);
	 				String ref=null;
	 				if(refObj!=null){
		 				ref=refObj.getobjectrefoid();
	 				}
	 				if(ref!=null){
		 				tid=tid+sep+ref;
		 				sep=":";
	 				}else{
	 			 		throw new IllegalStateException("REF required ("+tag+"/"+roleName+")");
	 				}
	 			}
	 		}
		return tid;
	 	}
	private void allReferencesKnownHelper(IomObject iomObj,FixIomObjectExtRefs extref) {
		
		String tag=iomObj.getobjecttag();
		//EhiLogger.debug("tag "+tag);
		Object modelele=tag2class.get(tag);
		if(modelele==null){
			return;
		}
		// ASSERT: an ordinary class/table
		Viewable aclass=(Viewable)modelele;		
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
								allReferencesKnownHelper(iomObj, attr, extref);
							}
						}
					}
					if(obj.obj instanceof RoleDef){
						RoleDef role = (RoleDef) obj.obj;
						{ // if(role.getExtending()==null)
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
		                                 boolean targetObjFound=false;
										for(Iterator<AbstractClassDef> targetClassIt=role.iteratorDestination();targetClassIt.hasNext();) {
										    AbstractClassDef targetClass=targetClassIt.next();
                                            String refoid=structvalue.getobjectrefoid();
	                                        if(targetClass instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)targetClass).getOid())) {
	                                            refoid=Validator.normalizeUUID(refoid);
	                                        }
	                                        if(oidPool.containsXtfid(Ili2cUtility.getRootViewable(getCrsMappedOrSame(targetClass)).getScopedName(null),refoid)){
	                                            targetObjFound=true;
	                                        }
										}
										if(!targetObjFound) {
                                            if(!createSqlExtRef || !role.isExternal()) {
                                                extref.addFix(structvalue, role,role.isExternal());
                                            }
										}
									}
								}
							 }else{
								 IomObject structvalue=iomObj.getattrobj(roleName,0);
								 boolean targetObjFound=false;
                                 for(Iterator<AbstractClassDef> targetClassIt=role.iteratorDestination();targetClassIt.hasNext();) {
                                     AbstractClassDef targetClass=targetClassIt.next();
                                     String refoid=structvalue.getobjectrefoid();
                                     if(targetClass instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)targetClass).getOid())) {
                                         refoid=Validator.normalizeUUID(refoid);
                                     }
                                     if(oidPool.containsXtfid(Ili2cUtility.getRootViewable(getCrsMappedOrSame(targetClass)).getScopedName(null),refoid)){
                                         targetObjFound=true;
                                     }
                                 }
                                 if(!targetObjFound) {
                                     if(!createSqlExtRef || !role.isExternal()) {
                                         extref.addFix(structvalue, role,role.isExternal());
                                     }
                                 }
							 }
						}
					 }
				}
	}
	private void allReferencesKnownHelper(IomObject iomObj, AttributeDef attr,FixIomObjectExtRefs extref)
	{
		String attrName=attr.getName();
		if( attr.isDomainBoolean()) {
		}else if( attr.isDomainIli1Date()) {
		}else if( attr.isDomainIli2Date()) {
		}else if( attr.isDomainIli2Time()) {
		}else if( attr.isDomainIli2DateTime()) {
		}else{
			Type type = attr.getDomainResolvingAliases();
		 
			if (type instanceof CompositionType){
				 // enqueue struct values
				 int structc=iomObj.getattrvaluecount(attrName);
				 for(int structi=0;structi<structc;structi++){
				 	IomObject struct=iomObj.getattrobj(attrName,structi);
				 	allReferencesKnownHelper(struct, extref);
				 }
			}else if (type instanceof PolylineType){
			 }else if(type instanceof SurfaceOrAreaType){
			 }else if(type instanceof CoordType){
			}else if(type instanceof NumericType){
			}else if(type instanceof EnumerationType){
			}else if(type instanceof ReferenceType){
				 IomObject structvalue=iomObj.getattrobj(attrName,0);
				 String refoid=null;
				 if(structvalue!=null){
					 refoid=structvalue.getobjectrefoid();
				 }
				 if(refoid!=null){
					 	ReferenceType refType = (ReferenceType)type;
                        Viewable targetClass=refType.getReferred(); 
                        if(targetClass instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)targetClass).getOid())) {
                            refoid=Validator.normalizeUUID(refoid);
                        }
						if(!oidPool.containsXtfid(Ili2cUtility.getRootViewable(getCrsMappedOrSame(targetClass)).getScopedName(null),refoid)){
                            if(!createSqlExtRef || !refType.isExternal()) {
                                extref.addFix(structvalue, targetClass,refType.isExternal());
                            }
						}
				 }
			}else{
			}
		}
	}

	private Long readObjectSqlid(Viewable xclass, String xtfid) {
		java.sql.PreparedStatement dbstmt = null;
        java.sql.ResultSet rs = null;
		long sqlid=0;
		String sqlType=null;
		try {
	        String stmt = createQueryStmt4sqlid(xclass);
	        EhiLogger.traceBackendCmd(stmt);

			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
            dbstmt.setString(1, xtfid);
			rs = dbstmt.executeQuery();
			if(rs.next()) {
				sqlid = rs.getLong(1);
				sqlType=rs.getString(3);
			}else{
				// unknown object
				return null;
			}
		} catch (java.sql.SQLException ex) {
			EhiLogger.logError("failed to query " + xclass.getScopedName(null),	ex);
		} finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException ex) {
                    EhiLogger.logError("failed to close query of "+ xclass.getScopedName(null), ex);
                }
                rs=null;
            }
			if (dbstmt != null) {
				try {
					dbstmt.close();
				} catch (java.sql.SQLException ex) {
					EhiLogger.logError("failed to close query of "+ xclass.getScopedName(null), ex);
				}
				dbstmt=null;
			}
		}
		// remember found sqlid
		Viewable aclass=(Viewable) tag2class.get(ili2sqlName.mapSqlTableName(sqlType));
		oidPool.putXtfid2sqlid(Ili2cUtility.getRootViewable(aclass).getScopedName(null),aclass.getScopedName(null),xtfid, sqlid);
		return sqlid;
	}
	private String createQueryStmt4sqlid(Viewable aclass) throws SQLException{
		ArrayList<ViewableWrapper> wrappers = recConv.getTargetTables(aclass);
		StringBuffer ret = new StringBuffer();
		int i=1;
		boolean isPg=TransferFromIli.isPostgresql(conn);
		ret.append("SELECT "+colT_ID+","+DbNames.T_ILI_TID_COL+","+DbNames.T_TYPE_COL+" FROM (");
		String sep="";
		HashSet<ViewableWrapper> visitedWrappers=new HashSet<ViewableWrapper>();
		for(ViewableWrapper wrapper:wrappers){
		    wrapper=wrapper.getRoot();
		    if(!visitedWrappers.contains(wrapper)) {
		        visitedWrappers.add(wrapper);
	            ret.append(sep);
	            ret.append("SELECT r"+i+"."+colT_ID);
	            if(isPg) {
	                ret.append(", "+"CAST(r"+i+"."+DbNames.T_ILI_TID_COL+" AS text)");
	            }else {
	                ret.append(", r"+i+"."+DbNames.T_ILI_TID_COL);
	            }
	            if(recConv.createTypeDiscriminator() ||wrapper.includesMultipleTypes()){
	                ret.append(", r"+i+"."+DbNames.T_TYPE_COL);
	            }else{
	                ret.append(", '"+wrapper.getSqlTable().getName()+"' "+DbNames.T_TYPE_COL);
	            }
	            ret.append(" FROM ");
	            ret.append(wrapper.getSqlTable().getQName());
	            ret.append(" r"+i+"");
	            i++;
	            sep=" UNION ";
		    }
		}
		ret.append(") r0");
		ret.append(" WHERE r0."+DbNames.T_ILI_TID_COL+"=?");
		return ret.toString();
	}
	private void readObjectSqlIds(boolean noTypeCol,DbTableName sqltablename, long basketsqlid) {
		String stmt = createQueryStmt4sqlids(noTypeCol,sqltablename.getQName());
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement dbstmt = null;
        java.sql.ResultSet rs = null;
		try {

			dbstmt = conn.prepareStatement(stmt);
			dbstmt.clearParameters();
			dbstmt.setLong(1, basketsqlid);
			rs = dbstmt.executeQuery();
			while(rs.next()) {
				long sqlid = rs.getLong(1);
				String xtfid=rs.getString(2);
				String sqlType=null;
				if(!noTypeCol){
					sqlType=rs.getString(3);
				}else{
					sqlType=sqltablename.getName();
				}
				Viewable aclass=(Viewable) tag2class.get(ili2sqlName.mapSqlTableName(sqlType));
				oidPool.putXtfid2sqlid(Ili2cUtility.getRootViewable(aclass).getScopedName(null),aclass.getScopedName(null),xtfid, sqlid);
				addExistingObjects(sqlType,sqlid);
			}
		} catch (java.sql.SQLException ex) {
			EhiLogger.logError("failed to query " + sqltablename,	ex);
		} finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (java.sql.SQLException ex) {
                    EhiLogger.logError("failed to close query of "+ sqltablename, ex);
                }
                rs=null;
            }
			if (dbstmt != null) {
				try {
					dbstmt.close();
				} catch (java.sql.SQLException ex) {
					EhiLogger.logError("failed to close query of "+ sqltablename, ex);
				}
				dbstmt=null;
			}
		}
	}
	private void addExistingObjects(String sqlType, long sqlid) {
		HashSet<Long> objs=null;
		if(existingObjectsOfCurrentBasket.containsKey(sqlType)){
			objs=existingObjectsOfCurrentBasket.get(sqlType);
		}else{
			objs=new HashSet<Long>();
			existingObjectsOfCurrentBasket.put(sqlType, objs);
		}
		objs.add(sqlid);
	}
	private boolean existingObjectsContains(String sqlType, long sqlid) {
		if(existingObjectsOfCurrentBasket.containsKey(sqlType)){
			HashSet<Long> objs=existingObjectsOfCurrentBasket.get(sqlType);
			return objs.contains(sqlid);
		}
		return false;
	}
	private void existingObjectsRemove(String sqlType, long sqlid) {
		if(existingObjectsOfCurrentBasket.containsKey(sqlType)){
			HashSet<Long> objs=existingObjectsOfCurrentBasket.get(sqlType);
			objs.remove(sqlid);
		}
		return;
	}

	private String createQueryStmt4sqlids(boolean noTypeCol,String sqltablename){
		StringBuffer ret = new StringBuffer();
		ret.append("SELECT r0."+colT_ID);
		ret.append(", r0."+DbNames.T_ILI_TID_COL);
		if(!noTypeCol){
			ret.append(", r0."+DbNames.T_TYPE_COL);
		}
		ret.append(" FROM ");
		ret.append(sqltablename);
		ret.append(" r0");
		ret.append(" WHERE r0."+DbNames.T_BASKET_COL+"=?");
		return ret.toString();
	}

	/** if structEle==null, iomObj is an object. If structEle!=null iomObj is a struct value.
	 */
	private void writeObject(String datasetName,long basketSqlId,Map<String,String> genericDomains,IomObject iomObj,AbstractStructWrapper structEle,Map<String, ClassStat> objStat)
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
		    AttributeDef geomAttr=(AttributeDef)modelele;
			writeItfLineTableObject(datasetName,basketSqlId,iomObj,geomAttr,TransferFromIli.getEpsgCode(geomAttr, genericDomains,defaultCrsCode));
			return;
		}
		// ASSERT: an ordinary class/table
        Viewable aclass0=(Viewable)modelele;      
		Viewable aclass1=getCrsMappedOrSame(aclass0);		
		String sqlType=(String)ili2sqlName.mapIliClassDef(aclass1);
		 long sqlId;
		 boolean updateObj=false;
		 // is it an object?
		 if(structEle==null){
				// map oid of transfer file to a sql id
			 	String tid=iomObj.getobjectoid();
			 	if(tid!=null && tid.length()>0){
			 	    if(aclass1 instanceof AbstractClassDef && AbstractRecordConverter.isUuidOid(td,((AbstractClassDef)aclass1).getOid())) {
			 	        tid=Validator.normalizeUUID(tid);
			 	    }
					sqlId=oidPool.getObjSqlId(Ili2cUtility.getRootViewable(aclass1).getScopedName(null),tid);
			 		if(functionCode==Config.FC_UPDATE && existingObjectsContains(sqlType,sqlId)){
			 			updateObj=true;
			 			existingObjectsRemove(sqlType,sqlId);
			 		}
			 	}else{
					 // it is an assoc without tid
					 // get a new sql id
					 sqlId=oidPool.newObjSqlId();
			 	}
		 }else{
			 // it is a struct value
			 // get a new sql id
			 sqlId=oidPool.newObjSqlId();
		 }
		 updateObjStat(objStat,tag,sqlId);
		 // loop over all classes; start with leaf, end with the base of the inheritance hierarchy
		 ViewableWrapper tableWrapper=class2wrapper.get(aclass1);
		 if(tableWrapper==null) {
		     throw new IllegalStateException("no ViewableWrapper found for "+aclass1.getScopedName());
		 }
		 while(tableWrapper!=null){
			 {
		         OutParam<String> stmtKey=new OutParam<String>();
					String insert = getInsertStmt(updateObj,aclass1,tableWrapper,structEle,stmtKey);
					EhiLogger.traceBackendCmd(insert);
					PreparedStatement ps = getPreparedStatement(stmtKey.value,insert);
		            StatementExecutionHelper seHelper = getStatementExecutionHelper(stmtKey.value);

                    recConv.writeRecord(basketSqlId, genericDomains,iomObj, aclass1,structEle, tableWrapper, sqlType,
                            sqlId, updateObj, ps,structQueue,aclass0, 0, 0);
                    seHelper.write(ps);
                    closeUnbatchedPreparedStatement(stmtKey.value);
			 }
			for(ViewableWrapper secondary:tableWrapper.getSecondaryTables()){
				// secondarytable contains attributes of this class?
				if(secondary.containsAttributes(recConv.getIomObjectAttrs(aclass1).keySet())){
			         OutParam<String> stmtKey=new OutParam<String>();
					String insert = getInsertStmt(updateObj,aclass1,secondary,structEle,stmtKey);
					EhiLogger.traceBackendCmd(insert);
					PreparedStatement ps = getPreparedStatement(stmtKey.value,insert);
					StatementExecutionHelper seHelper = getStatementExecutionHelper(stmtKey.value);

					AttributeDef attr = secondary.getPrimitiveCollectionAttr();
					if (attr != null) {
						for (int i = 0; i < iomObj.getattrvaluecount(attr.getName()); i++) {
							recConv.writeRecord(basketSqlId, genericDomains, iomObj, aclass1, structEle, secondary, sqlType,
									oidPool.newObjSqlId(), updateObj, ps, structQueue, aclass0, i, sqlId);
							seHelper.write(ps);
						}
					} else {
						recConv.writeRecord(basketSqlId, genericDomains,iomObj, aclass1,structEle, secondary, sqlType,
								sqlId, updateObj, ps,structQueue,aclass0, 0, 0);
						seHelper.write(ps);
					}

					closeUnbatchedPreparedStatement(stmtKey.value);
				}
				
			}
			tableWrapper=tableWrapper.getExtending();
		 }
		 // add StructWrapper around embedded associations that are mapped to a link table
		 for(Iterator roleIt=aclass0.getAttributesAndRoles2();roleIt.hasNext();) {
		     ViewableTransferElement roleEle=(ViewableTransferElement) roleIt.next();
		     if(roleEle.embedded && roleEle.obj instanceof RoleDef) {
		         RoleDef role=(RoleDef)roleEle.obj;
                 AssociationDef roleOwner = (AssociationDef) role.getContainer();
                 if(roleOwner.getDerivedFrom()==null && !TransferFromIli.isLightweightAssociation(roleOwner)){
                     IomObject refObj=iomObj.getattrobj(role.getName(), 0);
                     if(refObj!=null && refObj.getobjectrefoid()!=null) {
                         //EhiLogger.logState("refObj "+refObj);
                         if(refObj.getobjecttag().equals("REF")) {
                             refObj.setobjecttag(roleOwner.getScopedName());
                         }
                         structQueue.add(new EmbeddedLinkWrapper(iomObj.getobjectoid(),sqlType,refObj,role));
                     }
                 }
		     }
		 }
	}
	
    private StatementExecutionHelper getStatementExecutionHelper(String key) {
        StatementExecutionHelper helper=cachedStatementExecutionHelper.get(key);
        if(helper==null) {
            helper=new StatementExecutionHelper(batchSize);
            cachedStatementExecutionHelper.put(key,helper);
        }
        return helper;
    }

    private PreparedStatement getPreparedStatement(String key, String sql) throws SQLException {
        PreparedStatement stmt=cachedPreparedStatement.get(key);
        if(stmt==null) {
            stmt=conn.prepareStatement(sql);
            cachedPreparedStatement.put(key,stmt);
        }
        return stmt;
    }
    private void closeUnbatchedPreparedStatement(String key) throws SQLException {
        if(!doBatchInsert) {
            PreparedStatement stmt=cachedPreparedStatement.get(key);
            StatementExecutionHelper helper=cachedStatementExecutionHelper.get(key);
            try {
                if(stmt!=null) {
                    try {
                        if(helper!=null) {
                            helper.flush(stmt);
                        }
                    }finally {
                        stmt.close();
                    }
                }
            }finally {
                if(stmt!=null) {
                    cachedPreparedStatement.remove(key);
                }
                if(helper!=null) {
                    cachedStatementExecutionHelper.remove(key);
                }
            }
        }
    }
    private void closePreparedStatements() {
        if(doBatchInsert) {
            try {
                for(String key:cachedPreparedStatement.keySet()) {
                    PreparedStatement stmt=cachedPreparedStatement.get(key);
                    StatementExecutionHelper helper=cachedStatementExecutionHelper.get(key);
                    if(stmt!=null) {
                        try {
                            if(helper!=null) {
                                helper.flush(stmt);
                            }
                        } catch (SQLException ex) {
                            EhiLogger.logError("flush of recordbatch "+key+" failed", ex);
                        }finally {
                            try {
                                stmt.close();
                            } catch (SQLException ex) {
                                EhiLogger.logError("close of statement "+key+" failed", ex);
                            }
                        }
                    }
                }
            }finally {
                cachedPreparedStatement.clear();
                cachedStatementExecutionHelper.clear();
            }
        }
    }

    private void flushBatchedRecords() {
        if(doBatchInsert) {
            for(String key:cachedPreparedStatement.keySet()) {
                PreparedStatement stmt=cachedPreparedStatement.get(key);
                StatementExecutionHelper helper=cachedStatementExecutionHelper.get(key);
                if(stmt!=null) {
                    if(helper!=null) {
                        try {
                            helper.flush(stmt);
                        } catch (SQLException ex) {
                            EhiLogger.logError("flush of recordbatch "+key+" failed", ex);
                        }
                    }
                }
            }
            
        }
    }

    private Viewable getCrsMappedOrSame(Viewable aclass) {
        if(crsFilter==null) {
            return aclass;
        }
        Viewable aclass0=(Viewable)crsFilter.get(aclass);
        if(aclass0!=null) {
            return aclass0;
        }
        return aclass;
    }


	private DbTableName getSqlTableNameItfLineTable(AttributeDef attrDef,Integer epsgCode){
		String sqlTabName=ili2sqlName.mapItfGeometryAsTable((Viewable)attrDef.getContainer(),attrDef,epsgCode);
		return new DbTableName(schema,sqlTabName);
		
	}

	private void writeItfLineTableObject(String datasetName,long basketSqlId,IomObject iomObj,AttributeDef attrDef,Integer epsgCode)
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
		String idTag=attrDef.getContainer().getScopedName(null)+"."+attrDef.getName();
		long sqlId=oidPool.createObjSqlId(idTag,idTag,iomObj.getobjectoid());
		
		String sqlTableName=getSqlTableNameItfLineTable(attrDef,epsgCode).getQName();
		String insert=createItfLineTableInsertStmt(attrDef,epsgCode);
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try {
			int valuei = 1;

			ps.setLong(valuei, sqlId);
			valuei++;

			if (createBasketCol) {
				ps.setLong(valuei, basketSqlId);
				valuei++;
			}
			if (createDatasetCol) {
				ps.setString(valuei, datasetName);
				valuei++;
			}
			
			if(readIliTid){
				// import TID from transfer file
				ps.setString(valuei, iomObj.getobjectoid());
				valuei++;
			}

			IomObject value = iomObj.getattrobj(geomAttrName, 0);
			if (value != null) {
				Model model = (Model) attrDef.getContainer(Model.class);
				boolean is3D=((CoordType)(type).getControlPointDomain().getType()).getDimensions().length==3;
				ps.setObject(valuei,
						geomConv.fromIomPolyline(value, epsgCode, is3D,recConv.getP(type, model, epsgCode)));
			} else {
				geomConv.setPolylineNull(ps, valuei);
			}
			valuei++;

			if (type instanceof SurfaceType) {
				IomObject structvalue = iomObj.getattrobj(refAttrName, 0);
				String refoid = structvalue.getobjectrefoid();
				long refsqlId = oidPool.getObjSqlId(attrDef.getContainer().getScopedName(null),refoid); 
				ps.setLong(valuei, refsqlId);
				valuei++;
			}
			
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
			    	AttributeDef lineattr=(AttributeDef)attri.next();
					valuei = recConv.addAttrValue(iomObj, ili2sqlName.mapItfGeometryAsTable((Viewable)attrDef.getContainer(),attrDef,null), sqlId, sqlTableName,ps,
							valuei, new ColumnWrapper(new StructAttrPath(new ViewableTransferElement(lineattr))),lineattr,null,new HashMap<String,String>(),null, 0);
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
	private void updateObjStat(Map<String, ClassStat> objStat,String tag, long sqlId)
	{
		if(objStat.containsKey(tag)){
			ClassStat stat=objStat.get(tag);
			stat.addEndid(sqlId);
		}else{
			ClassStat stat=new ClassStat(tag,sqlId);
			objStat.put(tag,stat);
		}
	}
    private void writeObjStat(long sqlImportId,long basketSqlId,HashMap<String, ClassStat> objStat) throws SQLException
    {
        for(String className : objStat.keySet()){
            ClassStat stat=objStat.get(className);
            writeImportStatDetail(sqlImportId,stat.getStartid(),stat.getEndid(),stat.getObjcount(),className);
        }
    }

	private long writeImportStat(long datasetSqlId,String importFile,java.sql.Timestamp importDate,String importUsr)
	throws java.sql.SQLException,ConverterException
	{
		String sqlname=DbNames.IMPORTS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+DbNames.IMPORTS_TAB_DATASET_COL
			+", "+DbNames.IMPORTS_TAB_IMPORTDATE_COL
			+", "+DbNames.IMPORTS_TAB_IMPORTUSER_COL
			+", "+DbNames.IMPORTS_TAB_IMPORTFILE_COL
			+") VALUES (?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			
			long key=oidPool.newObjSqlId();
			ps.setLong(valuei, key);
			valuei++;
			
			ps.setLong(valuei, datasetSqlId);
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
	private long writeImportBasketStat(long importSqlId,long basketSqlId,long startTid,long endTid,long objCount)
	throws java.sql.SQLException,ConverterException
	{
		String sqlname=DbNames.IMPORTS_BASKETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+DbNames.IMPORTS_BASKETS_TAB_IMPORTRUN_COL
			+", "+DbNames.IMPORTS_BASKETS_TAB_BASKET_COL
			+", "+DbNames.IMPORTS_TAB_OBJECTCOUNT_COL
			+") VALUES (?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			
			long key=oidPool.newObjSqlId();
			ps.setLong(valuei, key);
			valuei++;
			
			ps.setLong(valuei, importSqlId);
			valuei++;

			ps.setLong(valuei, basketSqlId);
			valuei++;

			ps.setLong(valuei, objCount);
			valuei++;

			ps.executeUpdate();
			
			return key;
		}finally{
			ps.close();
		}
		
	}

	private void writeImportStatDetail(long importSqlId,long startTid,long endTid,long objCount,String importClassName)
	throws java.sql.SQLException
	{
	    if(false) {
	        String sqlname=DbNames.IMPORTS_OBJECTS_TAB;
	        if(schema!=null){
	            sqlname=schema+"."+sqlname;
	        }
	        String insert = "INSERT INTO "+sqlname
	            +"("+colT_ID 
	            +", "+DbNames.IMPORTS_OBJECTS_TAB_IMPORT_COL
	            +", "+DbNames.IMPORTS_OBJECTS_TAB_CLASS_COL
	            +", "+DbNames.IMPORTS_TAB_OBJECTCOUNT_COL
	            +", "+DbNames.IMPORTS_TAB_STARTTID_COL
	            +", "+DbNames.IMPORTS_TAB_ENDTID_COL
	            +") VALUES (?,?,?,?,?,?)";
	        EhiLogger.traceBackendCmd(insert);
	        PreparedStatement ps = conn.prepareStatement(insert);
	        try{
	            int valuei=1;
	            
	            ps.setLong(valuei, oidPool.newObjSqlId());
	            valuei++;
	            
	            ps.setLong(valuei, importSqlId);
	            valuei++;

	            ps.setString(valuei, importClassName);
	            valuei++;

	            ps.setLong(valuei, objCount);
	            valuei++;

	            ps.setLong(valuei, startTid);
	            valuei++;
	            
	            ps.setLong(valuei, endTid);
	            valuei++;
	            
	            ps.executeUpdate();
	        }finally{
	            ps.close();
	        }
	    }
		
	}

    private void removeBasketsFromDataset(long[] basketSqlIds) throws SQLException {
        if(basketSqlIds==null || basketSqlIds.length==0) {
            return;
        }
        String sqlname=DbNames.BASKETS_TAB;
        if(schema!=null){
            sqlname=schema+"."+sqlname;
        }
        StringBuffer crit=new StringBuffer();
        String sep="";
        for(long basketSqlId:basketSqlIds) {
            crit.append(sep);
            crit.append(Long.toString(basketSqlId));
            sep=",";
        }
        String insert = "UPDATE "+sqlname+" SET "+DbNames.BASKETS_TAB_DATASET_COL+"=NULL"
            +" WHERE "+colT_ID+" IN ("+crit.toString()+")";
        EhiLogger.traceBackendCmd(insert);
        conn.createStatement().execute(insert);
    }

	private long writeBasket(long datasetSqlId,StartBasketEvent iomBasket,long basketSqlId,String attachmentKey,boolean importBid,Map<String,String> genericDomains)
	throws java.sql.SQLException,ConverterException

	{
		String bid=iomBasket.getBid();
		String tag=iomBasket.getType();
		String domains=XtfWriter.domainsToString(genericDomains);

		String sqlname=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+", "+DbNames.BASKETS_TAB_TOPIC_COL
			+", "+DbNames.T_ILI_TID_COL
			+", "+DbNames.BASKETS_TAB_ATTACHMENT_KEY_COL
			+", "+DbNames.BASKETS_TAB_DATASET_COL
            +", "+DbNames.BASKETS_TAB_DOMAINS_COL
			+") VALUES (?,?,?,?,?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			ps.setLong(valuei, basketSqlId);
			valuei++;

			ps.setString(valuei, tag);
			valuei++;

			if(importBid) {
	            ps.setString(valuei, bid);
			}else {
			    ps.setNull(valuei, Types.VARCHAR);
			}
			valuei++;
			
			ps.setString(valuei, attachmentKey);
			valuei++;
			
			ps.setLong(valuei, datasetSqlId);
			valuei++;
			
            ps.setString(valuei, domains);
            valuei++;
            
			ps.executeUpdate();
		}finally{
			ps.close();
		}
		return basketSqlId;
}

	private long writeDataset(long datasetSqlId,String datasetName)
	throws java.sql.SQLException

	{

		String sqlname=DbNames.DATASETS_TAB;
		if(schema!=null){
			sqlname=schema+"."+sqlname;
		}
		String insert = "INSERT INTO "+sqlname
			+"("+colT_ID 
			+","+DbNames.DATASETS_TAB_DATASETNAME
			+") VALUES (?,?)";
		EhiLogger.traceBackendCmd(insert);
		PreparedStatement ps = conn.prepareStatement(insert);
		try{
			int valuei=1;
			ps.setLong(valuei, datasetSqlId);
			valuei++;
			
			ps.setString(valuei, datasetName);
			valuei++;
			
			ps.executeUpdate();
		}finally{
			ps.close();
		}
		return datasetSqlId;
}


	private String createItfLineTableInsertStmt(AttributeDef attrDef,Integer epsgCode) {
		SurfaceOrAreaType type = (SurfaceOrAreaType)attrDef.getDomainResolvingAliases();
		
		StringBuffer stmt = new StringBuffer();
		StringBuffer values = new StringBuffer();
		stmt.append("INSERT INTO ");
		DbTableName sqlTabName=getSqlTableNameItfLineTable(attrDef,epsgCode);
		stmt.append(sqlTabName.getQName());
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
			stmt.append(DbNames.T_BASKET_COL);
			values.append(",?");
		}
		if(createDatasetCol){
			stmt.append(sep);
			sep=",";
			stmt.append(DbNames.T_DATASET_COL);
			values.append(",?");
		}
		
		if(readIliTid){
			stmt.append(sep);
			sep=",";
			stmt.append(DbNames.T_ILI_TID_COL);
			values.append(",?");				
		}
		
		// POLYLINE
		 stmt.append(sep);
		 sep=",";
		 stmt.append(ili2sqlName.getSqlColNameItfLineTableGeomAttr(attrDef,sqlTabName.getName()));
		values.append(","+geomConv.getInsertValueWrapperPolyline("?",epsgCode));

		// -> mainTable
		if(type instanceof SurfaceType){
			stmt.append(sep);
			sep=",";
			stmt.append(ili2sqlName.getSqlColNameItfLineTableRefAttr(attrDef,sqlTabName.getName()));
			values.append(",?");
		}
		
		Table lineAttrTable=type.getLineAttributeStructure();
		if(lineAttrTable!=null){
		    Iterator attri = lineAttrTable.getAttributes ();
		    while(attri.hasNext()){
		    	AttributeDef lineattr=(AttributeDef)attri.next();
			   sep = recConv.addAttrToInsertStmt(false,stmt, values, sep, new ColumnWrapper(new StructAttrPath(new ViewableTransferElement(lineattr))),sqlTabName.getName());
		    }
		}
		
		// stdcols
		if(createStdCols){
			stmt.append(sep);
			sep=",";
			stmt.append(DbNames.T_LAST_CHANGE_COL);
			values.append(",?");
			stmt.append(sep);
			sep=",";
			stmt.append(DbNames.T_CREATE_DATE_COL);
			values.append(",?");
			stmt.append(sep);
			sep=",";
			stmt.append(DbNames.T_USER_COL);
			values.append(",?");
		}

		stmt.append(") VALUES (");
		stmt.append(values);
		stmt.append(")");
		return stmt.toString();
	}
	private Map<String,String> insertStmts=new HashMap<String,String>();
	private Map<String,String> updateStmts=new HashMap<String,String>();
	/** gets an insert statement for a given viewable. Creates only a new
	 *  statement if this is not yet seen sqlname.
	 * @param sqlname table name of viewable
	 * @param sqltable viewable
	 * @param stmtKey 
	 * @return insert statement
	 */
	private String getInsertStmt(boolean isUpdate,Viewable iomClass,ViewableWrapper sqltable,AbstractStructWrapper structEle, OutParam<String> stmtKey){
		final String key;
		if(structEle!=null && (structEle instanceof StructWrapper) && sqltable.getExtending()==null){
			ViewableWrapper parentTable=recConv.getViewableWrapper(((StructWrapper) structEle).getParentSqlType());
			key=sqltable.getSqlTablename()+":"+iomClass.getScopedName(null)+":"+parentTable.getSqlTablename()+":"+((StructWrapper) structEle).getParentAttr();
		}else{
            key=sqltable.getSqlTablename()+":"+iomClass.getScopedName(null);
		}
		if(stmtKey!=null) {
		    stmtKey.value=key;
		}
		if(isUpdate){
			if(updateStmts.containsKey(key)){
				return updateStmts.get(key);
			}
		}else{
			if(insertStmts.containsKey(key)){
				return insertStmts.get(key);
			}
		}
		String stmt=recConv.createInsertStmt(isUpdate,iomClass,new DbTableName(schema,sqltable.getSqlTablename()),sqltable,structEle);
		EhiLogger.traceBackendCmd(stmt);
		if(isUpdate){
			updateStmts.put(key,stmt);
		}else{
			insertStmts.put(key,stmt);
		}
		return stmt;
	}
}
