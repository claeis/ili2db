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
package ch.ehi.ili2db.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.logging.StdListener;
import ch.ehi.basics.logging.StdLogEvent;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlColumnConverter;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.fromili.CustomMappingNull;
import ch.ehi.ili2db.fromili.IliFromDb;
import ch.ehi.ili2db.fromili.ModelElementSelector;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.fromxtf.BasketStat;
import ch.ehi.ili2db.fromxtf.ClassStat;
import ch.ehi.ili2db.fromxtf.TransferFromXtf;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.ili2db.mapping.Viewable2TableMapper;
import ch.ehi.ili2db.mapping.Viewable2TableMapping;
import ch.ehi.ili2db.toxtf.TransferToXtf;
import ch.ehi.ili2db.metaattr.MetaAttrUtility;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.generator.Generator;
import ch.ehi.sqlgen.generator.GeneratorDriver;
//import ch.ehi.sqlgen.generator_impl.oracle.GeneratorOracle;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.gui.UserSettings;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Ili2cMetaAttrs;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.IliFiles;
import ch.interlis.iom_j.iligml.Iligml10Writer;
import ch.interlis.iom_j.iligml.Iligml20Writer;
//import ch.interlis.iom.swig.iom_javaConstants;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.itf.ItfWriter;
import ch.interlis.iom_j.itf.ItfWriter2;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfModel;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iom_j.xtf.XtfWriter;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.IoxWriter;
import ch.interlis.iox_j.utility.IoxUtility;
import ch.interlis.iox_j.logging.FileLogger;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.logging.StdLogger;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class Ili2db {
	public static final String XTF="%XTF";
	public static final String XTF_DIR="%XTF_DIR";
	public static final String JAR_DIR="%JAR_DIR";
	public static final String ILI_FROM_DB="%ILI_FROM_DB";
	private final static String SETTINGS_FILE = System.getProperty("user.home") + "/.ili2db";
	public static final String SETTING_DIRUSED="ch.ehi.ili2db.dirused";
    public static final char NO_BREAK_SPACE='\u00A0';
	public static void readAppSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			if(file.exists()){
				settings.load(file);
			}
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to load settings from file "+SETTINGS_FILE,ex);
		}
	}
	public static void writeAppSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			settings.store(file,"ili2db settings");
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to settings settings to file "+SETTINGS_FILE,ex);
		}
	}
	public static void readSettingsFromDb(Config config)
	throws Ili2dbException
	{
		boolean connectionFromExtern=config.getJdbcConnection()!=null;
		String dburl=config.getDburl();
		String dbusr=config.getDbusr();
		String dbpwd=config.getDbpwd();
		if(!connectionFromExtern && dburl==null){
			EhiLogger.logError("no dburl given");
			return;
		}
		if(dbusr==null){
			//EhiLogger.logError("no dbusr given");
			//return;
			dbusr="";
		}
		if(dbpwd==null){
			//EhiLogger.logError("no dbpwd given");
			//return;
			dbpwd="";
		}
		if(!connectionFromExtern){
			String jdbcDriver=config.getJdbcDriver();
			if(jdbcDriver==null){
				EhiLogger.logError("no JDBC driver given");
				return;
			}
			if(jdbcDriver.equals("ch.ehi.ili2geodb.jdbc.GeodbDriver")){
				return;
			}
			
			try{
				Class.forName(jdbcDriver);
			}catch(Exception ex){
				EhiLogger.logError("failed to load JDBC driver",ex);
				return;
			}
			
			Ili2dbLibraryInit ao=null;
			try{
				ao=getInitStrategy(config); 
				ao.init();
			}finally{
				if(ao!=null){
					ao.end();
				}
			}
			
		}
		
		CustomMapping customMapping=getCustomMappingStrategy(config);
		
		// open db connection
		Connection conn=null;
		String url = dburl;
		try {
			if(connectionFromExtern){
				conn=config.getJdbcConnection();
			}else{
				conn = connect(url, dbusr, dbpwd, config, customMapping);
			}
			customMapping.postConnect(conn, config);
			TransferFromIli.readSettings(conn,config,config.getDbschema(),customMapping);
		} catch (SQLException e) {
			EhiLogger.logError(e);
		} catch (IOException e) {
            EhiLogger.logError(e);
        }finally{
			if(!connectionFromExtern && conn!=null){
				try{
					conn.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}finally{
					config.setJdbcConnection(null);
					conn=null;
				}
			}
		}
		
	}
	public static void run(Config config,String appHome)
	throws Ili2dbException
	{
		if(config.getFunction()==Config.FC_IMPORT){
			runUpdate(config,appHome,Config.FC_IMPORT);
        }else if(config.getFunction()==Config.FC_VALIDATE){
            runExport(config,appHome,Config.FC_VALIDATE);
		}else if(config.getFunction()==Config.FC_UPDATE){
			runUpdate(config,appHome,Config.FC_UPDATE);
		}else if(config.getFunction()==Config.FC_REPLACE){
			runUpdate(config,appHome,Config.FC_REPLACE);
		}else if(config.getFunction()==Config.FC_DELETE){
			runUpdate(config,appHome,Config.FC_DELETE);
		}else if(config.getFunction()==Config.FC_EXPORT){
			runExport(config,appHome,Config.FC_EXPORT);
		}else if(config.getFunction()==Config.FC_SCHEMAIMPORT || config.getFunction()==Config.FC_SCRIPT){
			runSchemaImport(config,appHome);
		}else{
			throw new Ili2dbException("function not supported");
		}

		
	}
	public static void runImport(Config config,String appHome) 
	throws Ili2dbException
	{
		runUpdate(config,appHome,Config.FC_IMPORT);
	}
	public static void runUpdate(Config config,String appHome,int function) 
	throws Ili2dbException
		{
		ch.ehi.basics.logging.FileListener logfile=null;
        ch.interlis.iox_j.logging.XtfErrorsLogger xtflog=null;
		if(config.getLogfile()!=null){
			logfile=new FileLogger(new java.io.File(config.getLogfile()));
			EhiLogger.getInstance().addListener(logfile);
		}
		String xtflogFilename=config.getXtfLogfile();
        if(xtflogFilename!=null){
            File f=new java.io.File(xtflogFilename);
            try {
                if(isWriteable(f)) {
                    xtflog=new ch.interlis.iox_j.logging.XtfErrorsLogger(f, config.getSender());
                    EhiLogger.getInstance().addListener(xtflog);
                }else {
                    throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">");
                }
            } catch (IOException e) {
                throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">",e);
            }
        }
		StdLogger logStderr=new StdLogger(config.getLogfile());
		EhiLogger.getInstance().addListener(logStderr);
		EhiLogger.getInstance().removeListener(StdListener.getInstance());
		
		try{
			boolean connectionFromExtern=config.getJdbcConnection()!=null;
			logGeneralInfo(config);
			
			//String zipfilename=null;
			java.util.zip.ZipEntry zipXtfEntry=null;
			java.util.zip.ZipFile zipFile=null;
			String inputFilename=config.getXtffile();
			if(function==Config.FC_DELETE){
				if(config.getDatasetName()==null){
					throw new Ili2dbException("no datasetName given");
				}
			}else if(function==Config.FC_VALIDATE){
                if(config.getDatasetName()==null){
                    throw new Ili2dbException("no datasetName given");
                }
			}else{
				if(inputFilename==null){
					throw new Ili2dbException("no xtf-file given");
				}
				if(ch.ehi.basics.view.GenericFileFilter.getFileExtension(inputFilename).toLowerCase().equals("zip")){
					try {
						zipFile=new java.util.zip.ZipFile(inputFilename);
					} catch (IOException ex) {
						throw new Ili2dbException(ex);
					}
					java.util.Enumeration filei=zipFile.entries();
					while(filei.hasMoreElements()){
						java.util.zip.ZipEntry zipEntry=(java.util.zip.ZipEntry)filei.nextElement();
						String ext=ch.ehi.basics.view.GenericFileFilter.getFileExtension(zipEntry.getName()).toLowerCase();
						if(ext!=null && (ext.equals("xml") || ext.equals("xtf") || ext.equals("itf"))){
							zipXtfEntry=zipEntry;
							break;
						}
					}
					if(zipXtfEntry==null){
						throw new Ili2dbException("no xtf/itf-file in zip-archive "+zipFile.getName());
					}
				}
			}
				String modeldir=config.getModeldir();
				if(modeldir==null){
					throw new Ili2dbException("no modeldir given");
				}
				EhiLogger.traceState("modeldir <"+modeldir+">");
				
			String iliVersion=null;
			ch.interlis.ili2c.config.Configuration modelv=new ch.interlis.ili2c.config.Configuration();
			if(function!=Config.FC_DELETE){
				String models=config.getModels();
				if(models==null){
					throw new Ili2dbException("no models given");
				}
				EhiLogger.traceState("models <"+models+">");
				String modelnames[]=getModelNames(models);
				for(int modeli=0;modeli<modelnames.length;modeli++){
					String m=modelnames[modeli];
					if(m.equals(XTF)){
						// read modelname from xtf-file
						if(zipXtfEntry!=null){
							try{
								java.io.InputStream in=zipFile.getInputStream(zipXtfEntry);
								m=getModelFromXtf(in,zipXtfEntry.getName());
			                    if(m!=null){
			                        modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(m,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));             
			                    }
							}catch(java.io.IOException ex){
								throw new Ili2dbException(ex);
							}
						}else{
							List<String> modelsFromXtf=null;
                            try {
                                modelsFromXtf = IoxUtility.getModels(new java.io.File(inputFilename));
                                if(iliVersion==null) {
                                    iliVersion=IoxUtility.getModelVersion(new String[] {inputFilename},new LogEventFactory());
                                }
                            } catch (IoxException e) {
                                throw new Ili2dbException(e);
                            }
		                    for(String modelFromXtf:modelsFromXtf){
		                        modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelFromXtf,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));             
		                    }
						}
					}else {
                        modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(m,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));             
					}
				}
				
			}

			String dburl=config.getDburl();
			String dbusr=config.getDbusr();
			String dbpwd=config.getDbpwd();
			if(!connectionFromExtern && dburl==null){
				throw new Ili2dbException("no dburl given");
			}
			if(dbusr==null){
				//EhiLogger.logError("no dbusr given");
				//return;
				dbusr="";
			}
			if(dbpwd==null){
				//EhiLogger.logError("no dbpwd given");
				//return;
				dbpwd="";
			}
			String dbschema=config.getDbschema();
			if(dbschema!=null){
				EhiLogger.logState("dbschema <"+dbschema+">");
			}
			String geometryConverter=config.getGeometryConverter();
			if(geometryConverter==null){
				throw new Ili2dbException("no geoemtry converter given");
			}
			String ddlGenerator=config.getDdlGenerator();
			if(ddlGenerator==null){
				throw new Ili2dbException("no DDL generator given");
			}
			String idGenerator=config.getIdGenerator();
			if(idGenerator==null){
				throw new Ili2dbException("no ID generator given");
			}
			if(!connectionFromExtern){
				String jdbcDriver=config.getJdbcDriver();
				if(jdbcDriver==null){
					throw new Ili2dbException("no JDBC driver given");
				}
				try{
					Class.forName(jdbcDriver);
				}catch(Exception ex){
					throw new Ili2dbException("failed to load JDBC driver",ex);
				}
			}
			CustomMapping customMapping=getCustomMappingStrategy(config);
			
			// open db connection
			Connection conn=null;
			String url = dburl;
			ch.ehi.basics.logging.ErrorTracker errs=null;
			try{
				try {
					if(connectionFromExtern){
						conn=config.getJdbcConnection();
					}else{
						conn = connect(url, dbusr, dbpwd, config, customMapping);
					}
					customMapping.postConnect(conn, config);
				} catch (SQLException ex) {
					throw new Ili2dbException("failed to get db connection", ex);
				} catch (IOException e) {
                    throw new Ili2dbException("failed to get db connection", e);
                }
			  logDBVersion(conn);
			  
			  if(!connectionFromExtern){
				  // switch off auto-commit
				  try {
					conn.setAutoCommit(false);
				} catch (SQLException ex) {
					throw new Ili2dbException("failed to switch off auto-commit",ex);
				}
			  }
			  
              // run DB specific pre-processing
              customMapping.prePreScript(conn, config);
              
			  // run pre-script
			  if(config.getPreScript()!=null){
				  try {
					  DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPreScript()));
					  EhiLogger.logState("run update pre-script...");
				  } catch (FileNotFoundException e) {
					  throw new Ili2dbException("update pre-script statements failed",e);
				  }
			  }

                // create db schema
                if (function == Config.FC_IMPORT) {
                    if (config.getDbschema() != null) {
                        if (!DbUtility.schemaExists(conn, config.getDbschema())) {
                            DbUtility.createSchema(conn, config.getDbschema());
                        }
                    }
                }
				
                // verify dataset/basket settings
				if(function==Config.FC_DELETE){
					boolean createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
					if(!createBasketCol){
						throw new Ili2dbException("delete requires column "+DbNames.T_BASKET_COL);
					}
					String datasetName=config.getDatasetName();
					// map datasetName to modelnames
					Long datasetId=getDatasetId(datasetName, conn, config);
					if(datasetId==null){
						throw new Ili2dbException("dataset <"+datasetName+"> doesn't exist");
					}
					getBasketSqlIdsFromDatasetId(datasetId,modelv,conn,config);
				}else if(function==Config.FC_IMPORT){
					String datasetName=config.getDatasetName();
					if(datasetName!=null){
						if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.DATASETS_TAB))){
							Long datasetId=getDatasetId(datasetName, conn, config);
							if(datasetId!=null){
								throw new Ili2dbException("dataset <"+datasetName+"> already exists");
							}
						}
						boolean createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
						if(!createBasketCol){
							throw new Ili2dbException("import with dataset name requires column "+DbNames.T_BASKET_COL);
						}
					}
				}
				
				if(modelv.getSizeFileEntry()==0){
					throw new Ili2dbException("no models given");
				}

				
				// compile required ili files
				setupIli2cPathmap(config, appHome, inputFilename,conn,customMapping);
			    Ili2cMetaAttrs ili2cMetaAttrs=new Ili2cMetaAttrs();
			    ch.interlis.ili2c.config.Configuration ili2cConfig=null;
				try {
					ili2cConfig = (ch.interlis.ili2c.config.Configuration)modelv.clone();
				} catch (CloneNotSupportedException e1) {
					throw new Ili2dbException(e1);
				}
			    setupIli2cMetaAttrs(ili2cMetaAttrs,config,ili2cConfig);
				
				EhiLogger.logState("compile models...");
				ili2cConfig.setAutoCompleteModelList(true);
				ili2cConfig.setGenerateWarnings(false);
				if(iliVersion!=null) {
	                config.setValue(UserSettings.ILI_LANGUAGE_VERSION, iliVersion);
				}
				TransferDescription td = ch.interlis.ili2c.Main.runCompiler(ili2cConfig,
						config,ili2cMetaAttrs);
				if (td == null) {
					throw new Ili2dbException("compiler failed");
				}
				
				// if meta attribute table already exists, read it
                if(config.getCreateMetaInfo() && customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.META_ATTRIBUTES_TAB))){
                    // set elements' meta-attributes
                    MetaAttrUtility.addMetaAttrsFromDb(td, conn, config.getDbschema());
                }
                // import meta-attributes from .toml file
                if(config.getIliMetaAttrsFile()!=null){
                    if(config.getCreateMetaInfo()){
                        try{
                            EhiLogger.logState("import meta-attributes from toml file");
                            MetaAttrUtility.addMetaAttrsFromConfigFile(td, new java.io.File(config.getIliMetaAttrsFile()));
                        }catch(IOException e){
                            throw new Ili2dbException("import meta-attributes failed",e);
                        }
                    }else{
                        throw new Ili2dbException("import meta-attributes requires --createMetaInfo option");
                    }
                }
                if(config.getModelSrsCode()!=null) {
                    addModellSrsCode(td,config.getModelSrsCode());
                }
			  	
				// read mapping file
				NameMapping mapping=new NameMapping(td,config);
				  if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.CLASSNAME_TAB))){
					  // read mapping from db
					  mapping.readTableMappingTable(conn,config.getDbschema());
				  }
				  if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.ATTRNAME_TAB))){
					  // read mapping from db
					  mapping.readAttrMappingTable(conn,config.getDbschema());
				  }
				  TrafoConfig trafoConfig=new TrafoConfig(config.getBatchSize());
				  trafoConfig.readTrafoConfig(conn, config.getDbschema(),customMapping);

				ModelElementSelector ms=new ModelElementSelector();
				ArrayList<String> modelNames=new ArrayList<String>();
				for(int modeli=0;modeli<modelv.getSizeFileEntry();modeli++){
					if(modelv.getFileEntry(modeli).getKind()==ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE){
						String m=modelv.getFileEntry(modeli).getFilename();
						EhiLogger.traceState("use model "+m);
						modelNames.add(m);				
					}
				}
				// use models explicitly given by user --models, --topics and/or as read from transferfile
				java.util.List<Element> eles=ms.getModelElements(modelNames,td, td.getIli1Format()!=null && config.getDoItfLineTables(),Config.CREATE_ENUM_DEFS_MULTI.equals(config.getCreateEnumDefs()),config);
				Viewable2TableMapping class2wrapper=Viewable2TableMapper.getClass2TableMapping(td.getIli1Format()!=null,config,trafoConfig,eles,mapping);

				Generator gen=null;
				try{
					gen=(Generator)Class.forName(ddlGenerator).newInstance();
				}catch(Exception ex){
					throw new Ili2dbException("failed to load/create DDL generator",ex);
				}
				DbIdGen idGen=null;
				try{
					idGen=(DbIdGen)Class.forName(idGenerator).newInstance();
				}catch(Exception ex){
					throw new Ili2dbException("failed to load/create ID generator",ex);
				}
				idGen.init(config.getDbschema(),config);
				SqlColumnConverter geomConverter=null;
				try{
					geomConverter=(SqlColumnConverter)Class.forName(geometryConverter).newInstance();
				}catch(Exception ex){
					throw new Ili2dbException("failed to load/create geometry converter",ex);
				}
				geomConverter.setup(conn, config);

				idGen.initDb(conn,dbusr);
				
				// create table structure
				if(function==Config.FC_IMPORT && config.isDoImplicitSchemaImport()){
					EhiLogger.logState("create table structure, if not existing...");
	                idGen.initDbDefs(gen);
					try{
						TransferFromIli trsfFromIli=new TransferFromIli();
						
						// map ili-classes to sql-tables
						DbSchema schema;
						try {
							schema = trsfFromIli.doit(td,eles,mapping,config,idGen,trafoConfig,class2wrapper,customMapping);
						} catch (Ili2dbException e) {
							throw new Ili2dbException("mapping of ili-classes to sql-tables failed",e);
						}
						if(schema==null){
							return;
						}

						trsfFromIli.addBasketsTable(schema);
	                    if(config.isCreateImportTabs()) {
	                        trsfFromIli.addImportsTable(schema);
	                    }

						TransferFromIli.addInheritanceTable(schema,config);
						TransferFromIli.addSettingsTable(schema);
						TransferFromIli.addTrafoConfigTable(schema);
						TransferFromIli.addModelsTable(schema,config);
						trsfFromIli.addEnumTable(schema);
						TransferFromIli.addTableMappingTable(schema,config);
						TransferFromIli.addAttrMappingTable(schema,config);
						DbExtMetaInfo.addMetaInfoTables(schema);
						idGen.addMappingTable(schema);
						
						if(config.getCreateMetaInfo()){
							MetaAttrUtility.addMetaAttributesTable(schema);
						}
						
						GeneratorDriver drv=new GeneratorDriver(gen);
						drv.visitSchema(config,schema);
						{
		                    GeneratorJdbc insertCollector = config.getCreatescript()!=null?(GeneratorJdbc)gen:null;
                            // update mapping table
                            mapping.updateTableMappingTable(insertCollector,conn,config.getDbschema());
                            mapping.updateAttrMappingTable(insertCollector,conn,config.getDbschema());
                            trafoConfig.updateTrafoConfig(insertCollector,conn, config.getDbschema(),customMapping);
                            // update inheritance table
                            trsfFromIli.updateInheritanceTable(insertCollector,conn,config.getDbschema());
                            // update enumerations table
                            trsfFromIli.updateEnumTable(insertCollector,conn);
                            trsfFromIli.updateMetaInfoTables(insertCollector,conn);
                            TransferFromIli.addModels(insertCollector,conn,td,config.getDbschema(),customMapping,false);
                            if(!config.isConfigReadFromDb()){
                                TransferFromIli.updateSettings(insertCollector,conn,config,config.getDbschema());
                            }
                            if(config.getCreateMetaInfo()){
                                // update meta-attributes table
                                MetaAttrUtility.updateMetaAttributesTable(insertCollector,conn, config.getDbschema(), td,mapping);
                                // set elements' meta-attributes
                                MetaAttrUtility.addMetaAttrsFromDb(td, conn, config.getDbschema());
                            }
						}
	                        // create script requested by user?
	                        String createscript=config.getCreatescript();
	                        if(createscript!=null && (gen instanceof GeneratorJdbc)){
	                            writeScript(createscript,((GeneratorJdbc)gen).iteratorCreateLines());
	                        }
	                        // drop script requested by user?
	                        String dropscript=config.getDropscript();
	                        if(dropscript!=null && (gen instanceof GeneratorJdbc)){
	                            writeScript(dropscript,((GeneratorJdbc)gen).iteratorDropLines());
	                        }
					}catch(java.io.IOException ex){
						throw new Ili2dbException(ex);
					}
				}
				
				// process xtf files
				EhiLogger.logState("process data file...");
				Map<String,BasketStat> stat=new HashMap<String,BasketStat>();
				errs=new ch.ehi.basics.logging.ErrorTracker();
				EhiLogger.getInstance().addListener(errs);
				if(zipXtfEntry!=null){
					IoxReader ioxReader=null;
					java.io.InputStream in = null;
					try {
						  EhiLogger.logState("data <"+inputFilename+":"+zipXtfEntry.getName()+">");
						in = zipFile.getInputStream(zipXtfEntry);
						if(isItfFilename(zipXtfEntry.getName())){
							if(config.getDoItfLineTables()){
								ioxReader=new ItfReader(in);
								((ItfReader)ioxReader).setModel(td);		
							}else{
								ioxReader=new ItfReader2(in,config.isSkipGeometryErrors());
								((ItfReader2)ioxReader).setModel(td);		
							}
						}else{
							ioxReader=new XtfReader(in);
						}
						transferFromXtf(conn,ioxReader,function,mapping,td,dbusr,geomConverter,idGen,config,stat,trafoConfig,class2wrapper,customMapping);
					} catch (IOException ex) {
						throw new Ili2dbException(ex);
					} catch (IoxException ex) {
						throw new Ili2dbException(ex);
					}finally{
						if(ioxReader!=null){
							try {
								ioxReader.close();
							} catch (IoxException e) {
								throw new Ili2dbException(e);
							}
							ioxReader=null;
						}
						if(in!=null){
							try {
								in.close();
							} catch (IOException e) {
								throw new Ili2dbException(e);
							}
							in=null;
						}
					}
					// save attachments
					String attachmentKey=config.getAttachmentKey();
					String attachmentsBase=config.getAttachmentsPath();
					if(attachmentsBase!=null){
						java.io.File basePath=new java.io.File(attachmentsBase,attachmentKey);
						java.util.Enumeration filei=zipFile.entries();
						while(filei.hasMoreElements()){
							java.util.zip.ZipEntry zipEntry=(java.util.zip.ZipEntry)filei.nextElement();
							if(!zipXtfEntry.getName().equals(zipEntry.getName())){
								// save file
								java.io.File destFile=new java.io.File(basePath,zipEntry.getName());
								java.io.File parent=destFile.getAbsoluteFile().getParentFile();
								if(!parent.exists()){
									if(!parent.mkdirs()){
										throw new Ili2dbException("failed to create "+parent.getAbsolutePath());
									}
								}
								try {
									copyStream(destFile,zipFile.getInputStream(zipEntry));
								} catch (IOException ex) {
									throw new Ili2dbException("failed to save attachment "+zipEntry.getName(),ex);
								}
							}
						}
						
					}
				}else{
					IoxReader ioxReader=null;
					try {
						if(function!=Config.FC_DELETE){
							  EhiLogger.logState("data <"+inputFilename+">");
								if(isItfFilename(inputFilename)){
									config.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE_DO);
									if(config.getDoItfLineTables()){
										ioxReader=new ItfReader(new java.io.File(inputFilename));
										((ItfReader)ioxReader).setModel(td);		
										((ItfReader)ioxReader).setBidPrefix(config.getDatasetName());		
									}else{
										ioxReader=new ItfReader2(new java.io.File(inputFilename),config.isSkipGeometryErrors());
										((ItfReader2)ioxReader).setModel(td);		
										((ItfReader2)ioxReader).setBidPrefix(config.getDatasetName());		
									}
                                }else{
                                    ioxReader=Xtf24Reader.createReader(new java.io.File(inputFilename));
                                    if(ioxReader instanceof ch.interlis.iox_j.IoxIliReader) {
                                        ((ch.interlis.iox_j.IoxIliReader) ioxReader).setModel(td);
                                    }
								}
						}
						transferFromXtf(conn,ioxReader,function,mapping,td,dbusr,geomConverter,idGen,config,stat,trafoConfig,class2wrapper,customMapping);
					} catch (IoxException e) {
						throw new Ili2dbException(e);
					}finally{
						if(ioxReader!=null){
							try {
								ioxReader.close();
							} catch (IoxException e) {
								throw new Ili2dbException(e);
							}
							ioxReader=null;
						}
					}
				}
				// run post-script
				if(config.getPostScript()!=null){
					try {
						DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPostScript()));
						EhiLogger.logState("run update post-script...");
					} catch (FileNotFoundException e) {
						throw new Ili2dbException("update post-script statements failed",e);
					}
				}
				
                // run DB specific post processing
                customMapping.postPostScript(conn, config);
				
                String functionTxt="import";
                if(function==Config.FC_DELETE) {
                    functionTxt="delete";
                }else if(function==Config.FC_UPDATE) {
                    functionTxt="update";
                }else if(function==Config.FC_REPLACE) {
                    functionTxt="replace";
                }
				if(errs.hasSeenErrors()){
					if(!connectionFromExtern){
						try {
							conn.rollback();
						} catch (SQLException e) {
							EhiLogger.logError("rollback failed",e);
						}
					}
					throw new Ili2dbException("..."+functionTxt+" failed");
				}else{
					if(!connectionFromExtern){
						try {
							conn.commit();
						} catch (SQLException e) {
							EhiLogger.logError("commit failed",e);
							throw new Ili2dbException("..."+functionTxt+" failed");
						}
					}
					logStatistics(td.getIli1Format()!=null,stat);
					EhiLogger.logState("..."+functionTxt+" done");
				}
			}finally{
				if(!connectionFromExtern){
					if(conn!=null){
						try{
							conn.close();
						}catch(java.sql.SQLException ex){
							EhiLogger.logError(ex);
						}finally{
							config.setJdbcConnection(null);
							conn=null;
						}
					}
				}
				if(errs!=null){
					EhiLogger.getInstance().removeListener(errs);
					errs=null;
				}
			}
		}catch(Ili2dbException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}catch(java.lang.RuntimeException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}finally{
            if(xtflog!=null){
                EhiLogger.getInstance().removeListener(xtflog);
                xtflog.close();
                xtflog=null;
            }
			if(logfile!=null){
				EhiLogger.getInstance().removeListener(logfile);
				logfile.close();
				logfile=null;
			}
			if(logStderr!=null){
				EhiLogger.getInstance().addListener(StdListener.getInstance());
				EhiLogger.getInstance().removeListener(logStderr);
			}
		}
		

	}
	private static void addModellSrsCode(TransferDescription td, String modelSrsCodeTxt) {
        String modelNameSrsCodes[]=modelSrsCodeTxt.split(";");
        for(String modelNameSrsCode:modelNameSrsCodes) {
            String srsMapping[]=modelNameSrsCode.split("=");
            String modelName=srsMapping[0];
            String epsgCode=srsMapping[1];
            if(modelName!=null && epsgCode!=null){
                Model model=(Model)td.getElement(modelName);
                if(model!=null) {
                    model.setMetaValue(Ili2cMetaAttrs.ILI2C_CRS, TransferFromIli.EPSG+":"+epsgCode);
                }else {
                    EhiLogger.logAdaption("SRS assignment to model ignored; unkonwn model <"+modelName+">");
                }
            }
        }
    }
    private static void setupIli2cMetaAttrs(Ili2cMetaAttrs ili2cMetaAttrs,
			Config config,ch.interlis.ili2c.config.Configuration modelv) {
	    {
    		String ili2translation=config.getIli1Translation();
    		if(ili2translation!=null){
                String modelNameMappings[]=ili2translation.split(";");
                for(String modelNameMapping:modelNameMappings) {
                    String modelNames[]=modelNameMapping.split("=");
                    String translatedModelName=modelNames[0];
                    String originLanguageModelName=modelNames[1];
                    if(translatedModelName!=null && originLanguageModelName!=null){
                        ili2cMetaAttrs.setMetaAttrValue(translatedModelName, Ili2cMetaAttrs.ILI2C_TRANSLATION_OF, originLanguageModelName);
                        if(modelv!=null){
                            modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(originLanguageModelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));
                            modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(translatedModelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));
                        }
                    }
                }
    		}
		}
	    {
	        String srsModelAssignment=config.getSrsModelAssignment();
	        if(srsModelAssignment!=null){
	            String modelNames[]=srsModelAssignment.split("=");
	            String originalSrsModelName=modelNames[0];
	            String alternativeSrsModelName=modelNames[1];
	            if(originalSrsModelName!=null && alternativeSrsModelName!=null){
	                if(modelv!=null){
	                    modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(originalSrsModelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));
	                    modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(alternativeSrsModelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));
	                }
	            }
	        }
	    }
	}
	private static void logStatistics(boolean isIli1,Map<String,BasketStat> stat)
	{
		ArrayList<BasketStat> statv=new ArrayList<BasketStat>(stat.values());
		java.util.Collections.sort(statv,new java.util.Comparator<BasketStat>(){
			@Override
			public int compare(BasketStat b0, BasketStat b1) {
				int ret=b0.getDatasource().compareTo(b1.getDatasource());
				if(ret==0){
					ret=b0.getTopic().compareTo(b1.getTopic());
					if(ret==0){
						ret=b0.getBasketId().compareTo(b1.getBasketId());
					}
				}
				return ret;
			}
			
		});
		for(BasketStat basketStat:statv){
			if(isIli1){
				EhiLogger.logState(basketStat.getDatasource()+": "+basketStat.getTopic());
			}else{
				EhiLogger.logState(basketStat.getDatasource()+": "+basketStat.getTopic()+" BID="+basketStat.getBasketId());
			}
			HashMap<String, ClassStat> objStat=basketStat.getObjStat();
			ArrayList<String> classv=new ArrayList<String>(objStat.keySet());
			java.util.Collections.sort(classv,new java.util.Comparator<String>(){
				@Override
				public int compare(String b0, String b1) {
					int ret=b0.compareTo(b1);
					return ret;
				}
			});
			for(String className : classv){
				ClassStat classStat=objStat.get(className);
				String objCount=Long.toString(classStat.getObjcount());
				if(objCount.length()<6){
					objCount=ch.ehi.basics.tools.StringUtility.STRING(6-objCount.length(), ' ')+objCount;
				}
				EhiLogger.logState(Character.toString(NO_BREAK_SPACE)+objCount+" objects in CLASS "+className);
			}
		}
	}
	private static void copyStream(java.io.File outFile, java.io.InputStream in) throws IOException {
		java.io.BufferedWriter out=new java.io.BufferedWriter(new java.io.FileWriter(outFile));
		byte[] bt = new byte[1024];
		int i;
		while((i=in.read(bt)) != -1)
					{
						out.write(new String(bt,0,i));
					}
		out.close();
	}
	
	@Deprecated
	public static Ili2dbLibraryInit getInitStrategy(Config config)
	throws Ili2dbException
	{
		String initClassName=config.getInitStrategy();
		if(initClassName==null){
			return new Ili2dbLibraryInitNull();
		}
		Ili2dbLibraryInit init=null;
		try{
			init=(Ili2dbLibraryInit)Class.forName(initClassName).newInstance();
		}catch(Exception ex){
			throw new Ili2dbException("failed to load/create init strategy",ex);
		}
		return init;
	}
	public static void runSchemaImport(Config config,String appHome) 
	throws Ili2dbException
	{
		ch.ehi.basics.logging.FileListener logfile=null;
        ch.interlis.iox_j.logging.XtfErrorsLogger xtflog=null;
		if(config.getLogfile()!=null){
			logfile=new FileLogger(new java.io.File(config.getLogfile()));
			EhiLogger.getInstance().addListener(logfile);
		}
        String xtflogFilename=config.getXtfLogfile();
        if(xtflogFilename!=null){
            File f=new java.io.File(xtflogFilename);
            try {
                if(isWriteable(f)) {
                    xtflog=new ch.interlis.iox_j.logging.XtfErrorsLogger(f, config.getSender());
                    EhiLogger.getInstance().addListener(xtflog);
                }else {
                    throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">");
                }
            } catch (IOException e) {
                throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">",e);
            }
        }
		StdLogger logStderr=new StdLogger(config.getLogfile());
		EhiLogger.getInstance().addListener(logStderr);
		EhiLogger.getInstance().removeListener(StdListener.getInstance());
		
        final boolean importToDb = config.getFunction()!=Config.FC_SCRIPT;
		try{
			boolean connectionFromExtern=config.getJdbcConnection()!=null;
			logGeneralInfo(config);
			
			Ili2dbLibraryInit ao=null;
            Connection conn=null;
			try{
				ao=getInitStrategy(config); 
				ao.init();
				
			ch.interlis.ili2c.config.Configuration modelv=new ch.interlis.ili2c.config.Configuration();
			String xtffile=config.getXtffile();
			String ilifile=null;
			if(xtffile!=null && xtffile.endsWith(".ili")){
				ilifile=xtffile;
				modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(ilifile,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
			}
			
			String models=config.getModels();
			if(models!=null){
				String modelnames[]=models.split(";");
				for(int modeli=0;modeli<modelnames.length;modeli++){
					String m=modelnames[modeli];
					if(m!=null){
						if(m.equals(XTF)){
							// ignore it
						}else{
							modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(m,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
						}
					}
				}
			}
			if(modelv.getSizeFileEntry()==0){
				throw new Ili2dbException("no models given");
			}

			String dburl=config.getDburl();
			String dbusr=config.getDbusr();
			String dbpwd=config.getDbpwd();
            if(!connectionFromExtern && dburl==null && importToDb){
				throw new Ili2dbException("no dburl given");
			}
			if(dbusr==null){
				//EhiLogger.logError("no dbusr given");
				//return;
				dbusr="";
			}
			if(dbpwd==null){
				//EhiLogger.logError("no dbpwd given");
				//return;
				dbpwd="";
			}
			String dbschema=config.getDbschema();
			if(dbschema!=null){
				EhiLogger.logState("dbschema <"+dbschema+">");
			}
			String geometryConverter=config.getGeometryConverter();
			if(geometryConverter==null){
				throw new Ili2dbException("no geoemtry converter given");
			}
			String ddlGenerator=config.getDdlGenerator();
			if(ddlGenerator==null){
				throw new Ili2dbException("no DDL generator given");
			}
			String idGenerator=config.getIdGenerator();
			if(idGenerator==null){
				throw new Ili2dbException("no ID generator given");
			}
			if(!connectionFromExtern){
				String jdbcDriver=config.getJdbcDriver();
				if(jdbcDriver==null){
					throw new Ili2dbException("no JDBC driver given");
				}
				try{
					Class.forName(jdbcDriver);
				}catch(Exception ex){
					throw new Ili2dbException("failed to load JDBC driver",ex);
				}
			}

			CustomMapping customMapping=getCustomMappingStrategy(config);
			// open db connection
			String url = dburl;
			if(importToDb) {
	            try{
	                if(connectionFromExtern){
	                    conn=config.getJdbcConnection();
	                }else{
	                    conn = connect(url, dbusr, dbpwd, config, customMapping);
	                }
	                customMapping.postConnect(conn, config);
	              logDBVersion(conn);
	              
	              if(!connectionFromExtern){
	                  // switch off auto-commit
	                  conn.setAutoCommit(false);
	              }
	                            
	            }catch(SQLException ex){
	                throw new Ili2dbException(ex);
	            } catch (IOException e) {
                    throw new Ili2dbException(e);
                }
			}

            // run DB specific pre-processing
            if(importToDb) {
                customMapping.prePreScript(conn, config);
            }
			
			// run pre-script
            if(importToDb) {
                if(config.getPreScript()!=null){
                    try {
                        EhiLogger.logState("run schemaImport pre-script...");
                        DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPreScript()));
                    } catch (FileNotFoundException e) {
                        throw new Ili2dbException("schemaImport pre-script statements failed",e);
                    }
                }
            }
			
			// setup ilidirs+pathmap for ili2c
			setupIli2cPathmap(config, appHome, ilifile,conn,customMapping);
		    Ili2cMetaAttrs ili2cMetaAttrs=new Ili2cMetaAttrs();
		    setupIli2cMetaAttrs(ili2cMetaAttrs,config,modelv);
			
			// compile required ili files
			EhiLogger.logState("compile models...");
			TransferDescription td;
			modelv.setAutoCompleteModelList(true);
			modelv.setGenerateWarnings(false);
			td = ch.interlis.ili2c.Main.runCompiler(modelv,
					config,ili2cMetaAttrs);
			if (td == null) {
				throw new Ili2dbException("compiler failed");
			}
            // import meta-attributes from .toml file
            if(config.getIliMetaAttrsFile()!=null){
                if(config.getCreateMetaInfo()){
                    try{
                        EhiLogger.logState("import meta-attributes from toml file");
                        MetaAttrUtility.addMetaAttrsFromConfigFile(td, new java.io.File(config.getIliMetaAttrsFile()));
                    }catch(IOException e){
                        throw new Ili2dbException("import meta-attributes failed",e);
                    }
                }else{
                    throw new Ili2dbException("import meta-attributes requires --createMetaInfo option");
                }
            }
            if(config.getModelSrsCode()!=null) {
                addModellSrsCode(td,config.getModelSrsCode());
            }
			
			// an INTERLIS 1 model?
			if(td.getIli1Format()!=null){
				config.setItfTransferfile(true);
			}
			Generator gen=null;
			try{
				gen=(Generator)Class.forName(ddlGenerator).newInstance();
			}catch(Exception ex){
				throw new Ili2dbException("failed to load/create DDL generator",ex);
			}
			  // create db schema
            if(config.getDbschema()!=null){
                if(importToDb) {
                    if(!DbUtility.schemaExists(conn, config.getDbschema())){
                        DbUtility.createSchema(conn, config.getDbschema());
                    }
                }else {
                    if(gen instanceof GeneratorJdbc){
                        String sql=customMapping.getCreateSchemaStmt(config.getDbschema());
                        if(sql!=null) {
                            ((GeneratorJdbc) gen).addCreateLine(((GeneratorJdbc) gen).new Stmt(sql));
                        }
                    }
                }
            }
			DbIdGen idGen=null;
			try{
				idGen=(DbIdGen)Class.forName(idGenerator).newInstance();
			}catch(Exception ex){
				throw new Ili2dbException("failed to load/create ID generator",ex);
			}
		  	idGen.init(config.getDbschema(),config);

			// read mapping file
			NameMapping mapping=new NameMapping(td,config);
            if(importToDb) {
                if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.CLASSNAME_TAB))){
                    // read mapping from db
                    mapping.readTableMappingTable(conn,config.getDbschema());
                }
                if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.ATTRNAME_TAB))){
                    // read mapping from db
                    mapping.readAttrMappingTable(conn,config.getDbschema());
                }
            }
            
			  TrafoConfig trafoConfig=new TrafoConfig();
			  trafoConfig.readTrafoConfig(conn, config.getDbschema(),customMapping);

			ModelElementSelector ms=new ModelElementSelector();
			ArrayList<String> modelNames=new ArrayList<String>();
			if(models!=null){
				String modelnames[]=models.split(";");
				for(int modeli=0;modeli<modelnames.length;modeli++){
					String m=modelnames[modeli];
					if(m!=null){
						if(m.equals(XTF)){
							// ignore it
						}else{
							modelNames.add(m);				
						}
					}
				}
			}
			// use models explicitly given by user (or last model of given ili-file)
			java.util.List<Element> eles=ms.getModelElements(modelNames,td, td.getIli1Format()!=null && config.getDoItfLineTables(),Config.CREATE_ENUM_DEFS_MULTI.equals(config.getCreateEnumDefs()),config);
			Viewable2TableMapping class2wrapper=Viewable2TableMapper.getClass2TableMapping(td.getIli1Format()!=null,config,trafoConfig,eles,mapping);

			SqlColumnConverter geomConverter=null;
			try{
				geomConverter=(SqlColumnConverter)Class.forName(geometryConverter).newInstance();
			}catch(Exception ex){
				throw new Ili2dbException("failed to load/create geometry converter",ex);
			}
			geomConverter.setup(conn, config);

			if(importToDb) {
	            if(config.getDefaultSrsCode()!=null && config.getDefaultSrsAuthority()!=null){
	                try {
	                    if(geomConverter.getSrsid(config.getDefaultSrsAuthority(), config.getDefaultSrsCode(), conn)==null){
	                        throw new Ili2dbException(config.getDefaultSrsAuthority()+"/"+config.getDefaultSrsCode()+" does not exist");
	                    }
	                } catch (ConverterException ex) {
	                    throw new Ili2dbException("failed to query existence of SRS",ex);
	                }
	            }else if(config.getModelSrsCode()!=null && config.getDefaultSrsAuthority()!=null){
	                String modelSrsCodeTxt=config.getModelSrsCode();
	                String modelNameSrsCodes[]=modelSrsCodeTxt.split(";");
	                for(String modelNameSrsCode:modelNameSrsCodes) {
	                    String srsMapping[]=modelNameSrsCode.split("=");
	                    String modelName=srsMapping[0];
	                    String srsCode=srsMapping[1];
	                    if(modelName!=null && srsCode!=null){
	                        try {
	                            if(geomConverter.getSrsid(config.getDefaultSrsAuthority(), srsCode, conn)==null){
	                                throw new Ili2dbException(config.getDefaultSrsAuthority()+"/"+srsCode+" does not exist");
	                            }
	                        } catch (ConverterException ex) {
	                            throw new Ili2dbException("failed to query existence of SRS",ex);
	                        }
	                    }
	                }
	            }
			}
			
			// create table structure
			EhiLogger.logState("create table structure, if not existing...");
			try{
				TransferFromIli trsfFromIli=new TransferFromIli();
				
				// map ili-classes to sql-tables
				// TODO move default SRS to config
				DbSchema schema;
				try {
					schema = trsfFromIli.doit(td,eles,mapping,config,idGen,trafoConfig,class2wrapper,customMapping);
				} catch (Ili2dbException e) {
					throw new Ili2dbException("mapping of ili-classes to sql-tables failed",e);
				}
				if(schema==null){
					return;
				}

				if(!(conn instanceof GeodbConnection)){
					trsfFromIli.addBasketsTable(schema);
					if(config.isCreateImportTabs()) {
	                    trsfFromIli.addImportsTable(schema);
					}
					TransferFromIli.addInheritanceTable(schema,config);
					TransferFromIli.addSettingsTable(schema);
					TransferFromIli.addTrafoConfigTable(schema);
					TransferFromIli.addModelsTable(schema,config);
					trsfFromIli.addEnumTable(schema);
					TransferFromIli.addTableMappingTable(schema,config);
					TransferFromIli.addAttrMappingTable(schema,config);
					DbExtMetaInfo.addMetaInfoTables(schema);
					idGen.addMappingTable(schema);

					if(config.getCreateMetaInfo()){
						MetaAttrUtility.addMetaAttributesTable(schema);
					}
				}
				
				// TODO create geodb domains
				if(conn instanceof GeodbConnection){
					
				}
							
				GeneratorDriver drv=new GeneratorDriver(gen);
				idGen.initDb(conn,dbusr);
				idGen.initDbDefs(gen);
				
				drv.visitSchema(config,schema);
				validateSchemaNames(schema,mapping);

                if(!(conn instanceof GeodbConnection)){
                    GeneratorJdbc insertCollector = config.getCreatescript()!=null?(GeneratorJdbc)gen:null;
                    // update mapping table
                    mapping.updateTableMappingTable(insertCollector,conn,config.getDbschema());
                    mapping.updateAttrMappingTable(insertCollector,conn,config.getDbschema());
                    trafoConfig.updateTrafoConfig(insertCollector,conn, config.getDbschema(),customMapping);
                    
                    // update inheritance table
                    trsfFromIli.updateInheritanceTable(insertCollector,conn,config.getDbschema());
                    // update enum table
                    trsfFromIli.updateEnumTable(insertCollector,conn);
                    trsfFromIli.updateMetaInfoTables(insertCollector,conn);
                    TransferFromIli.addModels(insertCollector,conn,td,config.getDbschema(),customMapping,false);
                    if(!config.isConfigReadFromDb()){
                        TransferFromIli.updateSettings(insertCollector,conn,config,config.getDbschema());
                    }
                    if(config.getCreateMetaInfo()){
                        // update meta-attributes table
                        MetaAttrUtility.updateMetaAttributesTable(insertCollector,conn, config.getDbschema(), td,mapping);
                        // set elements' meta-attributes
                        if(conn!=null) {
                            MetaAttrUtility.addMetaAttrsFromDb(td, conn, config.getDbschema());
                        }
                    }
                }
				
                // is a create script requested by user?
                String createscript=config.getCreatescript();
                if(createscript!=null && (gen instanceof GeneratorJdbc)){
                    writeScript(createscript,((GeneratorJdbc) gen).iteratorCreateLines());
                }
                
                // is a drop script requested by user?
                String dropscript=config.getDropscript();
                if(dropscript!=null && (gen instanceof GeneratorJdbc)){
                    writeScript(dropscript,((GeneratorJdbc) gen).iteratorDropLines());
                }
				
				// run post-script
				if(importToDb) {
	                if(config.getPostScript()!=null){
	                    try {
	                        EhiLogger.logState("run schemaImport post-script...");
	                        DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPostScript()));
	                    } catch (FileNotFoundException e) {
	                        throw new Ili2dbException("schemaImport post-script statements failed",e);
	                    }
	                }
				}
				
                // run DB specific post processing
                if(importToDb) {
                    customMapping.postPostScript(conn, config);
                }
				
                if(importToDb) {
                    if(!connectionFromExtern){
                        try {
                            conn.commit();
                        } catch (SQLException e) {
                            throw new Ili2dbException("failed to commit",e);
                        }
                    }
                }
				
			}catch(java.io.IOException ex){
				throw new Ili2dbException(ex);
			}
			
				
			}finally{
	            try{
	                if(importToDb) {
	                    if(!connectionFromExtern){
	                        if(conn!=null){
	                            try{
	                                conn.close();
	                            }finally{
	                                conn=null;
	                                config.setJdbcConnection(null);
	                            }
	                        }
	                    }
	                }
	            }catch(java.sql.SQLException ex){
	                EhiLogger.logError(ex);
	            }
				ao.end();
			}
            EhiLogger.logState("...done");
			
		}catch(Ili2dbException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}catch(java.lang.RuntimeException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}finally{
            if(xtflog!=null){
                EhiLogger.getInstance().removeListener(xtflog);
                xtflog.close();
                xtflog=null;
            }
			if(logfile!=null){
				EhiLogger.getInstance().removeListener(logfile);
				logfile.close();
				logfile=null;
			}
			if(logStderr!=null){
				EhiLogger.getInstance().addListener(StdListener.getInstance());
				EhiLogger.getInstance().removeListener(logStderr);
			}
		}
		
}
	private static void validateSchemaNames(DbSchema schema, NameMapping mapping) {
	    if(false) {
	        boolean invalidNames=false;
	        for(DbTable table:schema.getTables()) {
	            String tabName=table.getName().getName();
	            if(!mapping.isValidSqlName(tabName)) {
	                EhiLogger.logAdaption("invalid table name "+tabName);
	                invalidNames=true;
	            }
	            for(Iterator colIt=table.iteratorColumn();colIt.hasNext();) {
	                DbColumn dbCol=(DbColumn) colIt.next();
	                String colName=dbCol.getName();
	                if(!mapping.isValidSqlName(colName)) {
	                    EhiLogger.logAdaption("invalid column name "+tabName+"."+colName);
	                    invalidNames=true;
	                }
	            }
	        }
	        if(invalidNames) {
	            throw new IllegalStateException("invalid names found");
	        }
	    }
        
    }
    private static void logGeneralInfo(Config config) {
		EhiLogger.logState(config.getSender());
		EhiLogger.logState("ili2c-"+ch.interlis.ili2c.Main.getVersion());
		EhiLogger.logState("iox-ili-"+ch.interlis.iox_j.utility.IoxUtility.getVersion());
		EhiLogger.logState("java.version "+System.getProperty("java.version"));
		EhiLogger.logState("user.name <"+System.getProperty("user.name")+">");
		EhiLogger.logState("maxMemory "+java.lang.Runtime.getRuntime().maxMemory()/1024L+" KB");
		EhiLogger.logState("currentTime "+new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
	}
	private static void setupIli2cPathmap(Config config, String appHome,
			String xtffile,java.sql.Connection conn, CustomMapping mapping) throws Ili2dbException {
		config.setValue(ch.interlis.ili2c.gui.UserSettings.ILIDIRS,config.getModeldir());
		java.util.HashMap pathMap=new java.util.HashMap();
		if(xtffile!=null){
			pathMap.put(Ili2db.XTF_DIR,new java.io.File(xtffile).getAbsoluteFile().getParent());
		}else{
			pathMap.put(Ili2db.XTF_DIR,null);
		}
		pathMap.put(Ili2db.JAR_DIR,appHome);
		config.setTransientObject(ch.interlis.ili2c.gui.UserSettings.ILIDIRS_PATHMAP,pathMap);
		
	  	// if ilimodels exists in db
		if(conn!=null){
			IliFiles iliFiles = null;
			String url=null;
			try {
				url=conn.getMetaData().getURL();
				url=mapping.shortenConnectUrl4IliCache(url);
				iliFiles=TransferFromIli.readIliFiles(conn,config.getDbschema(),mapping,config.isVer3_export());
			} catch (SQLException e) {
				throw new Ili2dbException(e);
			}
			if (iliFiles != null) {
				String dbSchema=config.getDbschema();
				if(dbSchema!=null){
					url=url+"/"+dbSchema;
				}
				pathMap.put(Ili2db.ILI_FROM_DB, url);
				config.setTransientValue(
						ch.interlis.ili2c.gui.UserSettings.TEMP_REPOS_URI, url);
				config.setTransientObject(
						ch.interlis.ili2c.gui.UserSettings.TEMP_REPOS_ILIFILES,
						iliFiles);
				config.setTransientObject(
						ch.interlis.ili2c.gui.UserSettings.CUSTOM_ILI_RESOLVER,
						new IliFromDb(url,conn,dbSchema,config));
			}		  	
		}
	}
	public static void runExport(Config config,String appHome,int function)
	throws Ili2dbException
	{
	    String functionName="export";
        if(function==Config.FC_VALIDATE) {
            functionName="validate";
        }
		ch.ehi.basics.logging.FileListener logfile=null;
		ch.interlis.iox_j.logging.XtfErrorsLogger xtflog=null;
		if(config.getLogfile()!=null){
			logfile=new FileLogger(new java.io.File(config.getLogfile()));
			EhiLogger.getInstance().addListener(logfile);
		}
        String xtflogFilename=config.getXtfLogfile();
        if(xtflogFilename!=null){
            File f=new java.io.File(xtflogFilename);
            try {
                if(isWriteable(f)) {
                    xtflog=new ch.interlis.iox_j.logging.XtfErrorsLogger(f, config.getSender());
                    EhiLogger.getInstance().addListener(xtflog);
                }else {
                    throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">");
                }
            } catch (IOException e) {
                throw new Ili2dbException("failed to write to logfile <"+f.getPath()+">",e);
            }
        }
		StdLogger logStderr=new StdLogger(config.getLogfile());
		EhiLogger.getInstance().addListener(logStderr);
		EhiLogger.getInstance().removeListener(StdListener.getInstance());
		
		try{
			boolean connectionFromExtern=config.getJdbcConnection()!=null;
			logGeneralInfo(config);
			
			String xtffile=config.getXtffile();
			if(function!=Config.FC_VALIDATE) {
	            if(xtffile==null){
	                throw new Ili2dbException("no xtf-file given");
	            }
			}
			String modeldir=config.getModeldir();
			if(modeldir==null){
				throw new Ili2dbException("no modeldir given");
			}

			String dburl=config.getDburl();
			String dbusr=config.getDbusr();
			String dbpwd=config.getDbpwd();
			if(!connectionFromExtern && dburl==null){
				throw new Ili2dbException("no dburl given");
			}
			if(dbusr==null){
				//EhiLogger.logError("no dbusr given");
				//return;
				dbusr="";
			}
			if(dbpwd==null){
				//EhiLogger.logError("no dbpwd given");
				//return;
				dbpwd="";
			}
			String dbschema=config.getDbschema();
			if(dbschema!=null){
				EhiLogger.logState("dbschema <"+dbschema+">");
			}
			String geometryConverter=config.getGeometryConverter();
			if(geometryConverter==null){
				throw new Ili2dbException("no geoemtry converter given");
			}
			if(!connectionFromExtern){
				String jdbcDriver=config.getJdbcDriver();
				if(jdbcDriver==null){
					throw new Ili2dbException("no JDBC driver given");
				}
				// open db connection
				try{
					Class.forName(jdbcDriver);
				}catch(Exception ex){
					throw new Ili2dbException("failed to load JDBC driver",ex);
				}
			}
			
			String baskets=config.getBaskets();
			String topics=config.getTopics();
			String models=config.getModels();
			String datasetName=config.getDatasetName();
			if(models==null && baskets==null && topics==null && datasetName==null){
				throw new Ili2dbException("no dataset, baskets, models or topics given");
			}
			
			CustomMapping customMapping=getCustomMappingStrategy(config);
			
			Connection conn=null;
			String url = dburl;
			try{
			  //DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			  try {
				  if(connectionFromExtern){
					  conn=config.getJdbcConnection();
				  }else{
						conn = connect(url, dbusr, dbpwd, config, customMapping);
				  }
					customMapping.postConnect(conn, config);
			} catch (SQLException e) {
				throw new Ili2dbException("failed to get db connection",e);
			} catch (IOException e) {
                throw new Ili2dbException("failed to get db connection",e);
            }
			logDBVersion(conn);
			  
            // run DB specific pre-processing
            customMapping.prePreScript(conn, config);
              
			// run pre-script 
			if(config.getPreScript()!=null){
				try {
					EhiLogger.logState("run "+functionName+" pre-script...");
					DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPreScript()));
				} catch (FileNotFoundException e) {
					throw new Ili2dbException(functionName+" pre-script statements failed",e);
				}
			}
			  
			ch.interlis.ili2c.config.Configuration modelv=new ch.interlis.ili2c.config.Configuration();
			boolean createBasketCol=Config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
			String exportModelnames[]=null;
			long basketSqlIds[]=null;
			if(datasetName!=null){
				if(!createBasketCol){
					throw new Ili2dbException("dataset wise "+functionName+" requires column "+DbNames.T_BASKET_COL);
				}
				// map datasetName to sqlBasketId and modelnames
				String datasetNames[] = datasetName.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
				List<Long> tmpListOfBasket = new ArrayList<Long>();
				for (String dtName : datasetNames) {
	                Long datasetId=getDatasetId(dtName, conn, config);
	                if(datasetId==null){
	                    throw new Ili2dbException("dataset <"+dtName+"> doesn't exist");
	                }
	                long tmpbasketSqlIds[]=getBasketSqlIdsFromDatasetId(datasetId,modelv,conn,config);
	                for (int i = 0; i < tmpbasketSqlIds.length; i++) {
	                    tmpListOfBasket.add(tmpbasketSqlIds[i]);
	                }
				}
				if (tmpListOfBasket.size() > 0) {
				    basketSqlIds = new long[tmpListOfBasket.size()];
				    for (int i = 0; i < tmpListOfBasket.size(); i++) {
				        basketSqlIds[i] = tmpListOfBasket.get(i);
				    }
				}
			}else if(baskets!=null){
				if(!createBasketCol){
					throw new Ili2dbException("basket wise "+functionName+" requires column "+DbNames.T_BASKET_COL);
				}
				// BIDs
				String basketids[]=baskets.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
				// map BID to sqlBasketId and modelnames
				basketSqlIds=getBasketSqlIdsFromBID(basketids,modelv,conn,config);
			}else if(topics!=null){
				if(!createBasketCol){
					throw new Ili2dbException("topic wise "+functionName+" requires column "+DbNames.T_BASKET_COL);
				}
				// TOPICs
				String topicv[]=topics.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
				// map BID to sqlBasketId and modelnames
				basketSqlIds=getBasketSqlIdsFromTopic(topicv,modelv,conn,config);
		        if(basketSqlIds==null){
		            basketSqlIds=new long[0];
		        }
			}else{
				if(createBasketCol){
					String[] modelnames = getModelNames(models);
					basketSqlIds=getBasketSqlIdsFromModel(modelnames,modelv,conn,config);
				}else{
					exportModelnames=getModelNames(models);
					for(int modeli=0;modeli<exportModelnames.length;modeli++){
						String m=exportModelnames[modeli];
						if(m.equals(XTF)){
							// TODO read modelname from db
						}
						modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(m,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
					}
				}
			}
			if(modelv.getSizeFileEntry()==0){
				throw new Ili2dbException("no models given");
			}
			if(config.getExportModels()!=null) {
	            for(String modelName:getModelNames(config.getExportModels())) {
	                modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));             
	            }
			}
			

			String adapterClassName=config.getGeometryConverter();
			if(adapterClassName==null){
				throw new Ili2dbException("no adapter given");
			}
			

			SqlColumnConverter geomConverter=null;
			try{
				geomConverter=(SqlColumnConverter)Class.forName(geometryConverter).newInstance();
			}catch(Exception ex){
				throw new Ili2dbException("failed to load/create geometry converter",ex);
			}
			

				// compile required ili files
				setupIli2cPathmap(config, appHome, xtffile,conn,customMapping);
			    Ili2cMetaAttrs ili2cMetaAttrs=new Ili2cMetaAttrs();
			    setupIli2cMetaAttrs(ili2cMetaAttrs,config,null); // don't add ili1 model translations to model list (should already be in list because of topicname in t_baskets table)
				EhiLogger.logState("compile models...");
				modelv.setAutoCompleteModelList(true);
				modelv.setGenerateWarnings(false);
				TransferDescription td = ch.interlis.ili2c.Main.runCompiler(modelv,
						config,ili2cMetaAttrs);
				if (td == null) {
					throw new Ili2dbException("compiler failed");
				}
			  
				if(config.getCreateMetaInfo()){
					// set elements' meta-attributes
					if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.META_ATTRIBUTES_TAB))){
						MetaAttrUtility.addMetaAttrsFromDb(td, conn, config.getDbschema());
					}
				}
				if(config.getModelSrsCode()!=null) {
				    addModellSrsCode(td,config.getModelSrsCode());
				}
			  
			  geomConverter.setup(conn, config);
			  
			  // get mapping definition
			  NameMapping mapping=new NameMapping(td,config);
			  if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.CLASSNAME_TAB))){
				  // read mapping from db
				  mapping.readTableMappingTable(conn,config.getDbschema());
			  }
			  if(customMapping.tableExists(conn,new DbTableName(config.getDbschema(),DbNames.ATTRNAME_TAB))){
				  // read mapping from db
				  mapping.readAttrMappingTable(conn,config.getDbschema());
			  }
			  TrafoConfig trafoConfig=new TrafoConfig();
			  trafoConfig.readTrafoConfig(conn, config.getDbschema(),customMapping);

				ModelElementSelector ms=new ModelElementSelector();
				ArrayList<String> modelNames=new ArrayList<String>();
				{
					Iterator<ch.interlis.ili2c.config.FileEntry> modi=modelv.iteratorFileEntry();
					while(modi.hasNext()){
						ch.interlis.ili2c.config.FileEntry mod=modi.next();
						if(mod.getKind()==ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE){
							modelNames.add(mod.getFilename());
							EhiLogger.traceState("modelname <"+mod.getFilename()+">");
						}
					}
				}
				
				// use models explicitly given by user (--models, --baskets, --dataset, --topics)
				// but remove model models that are crs translated
				String srsModelAssignment=config.getSrsModelAssignment();
				if(srsModelAssignment!=null) {
	                String srsModelNames[]=srsModelAssignment.split("=");
	                String originalSrsModelName=srsModelNames[0];
	                String alternativeSrsModelName=srsModelNames[1];
				    modelNames.remove(alternativeSrsModelName);
				}
			  java.util.List<Element> eles=ms.getModelElements(modelNames,td, td.getIli1Format()!=null && config.getDoItfLineTables(),Config.CREATE_ENUM_DEFS_MULTI.equals(config.getCreateEnumDefs()),config);
			  Viewable2TableMapping class2wrapper=Viewable2TableMapper.getClass2TableMapping(td.getIli1Format()!=null,config,trafoConfig,eles,mapping);

			  // process xtf files
			  EhiLogger.logState("process data...");
			  if(function!=Config.FC_VALIDATE) {
	              EhiLogger.logState("data <"+xtffile+">");
			  }
				Map<String,BasketStat> stat=new HashMap<String,BasketStat>();
				ch.ehi.basics.logging.ErrorTracker errs=new ch.ehi.basics.logging.ErrorTracker();
				EhiLogger.getInstance().addListener(errs);
				transferToXtf(conn,function,xtffile,customMapping,mapping,td,geomConverter,config.getSender(),config,exportModelnames,basketSqlIds,stat,trafoConfig,class2wrapper);
				if (errs.hasSeenErrors()) {
					throw new Ili2dbException("..."+functionName+" failed");
				} else {
					logStatistics(td.getIli1Format() != null, stat);
					EhiLogger.logState("..."+functionName+" done");
				}
			  EhiLogger.getInstance().removeListener(errs);
			  
			  	// run post-script
				if(config.getPostScript()!=null){
					try {
						DbUtility.executeSqlScript(conn, new java.io.FileReader(config.getPostScript()));
						EhiLogger.logState("run "+functionName+" post-script...");
					} catch (FileNotFoundException e) {
						throw new Ili2dbException(functionName+" post-script statements failed",e);
					}
				}
				
				// run DB specific post processing
				customMapping.postPostScript(conn, config);
				
			//}catch(Exception ex){
				//EhiLogger.logError(ex);
			}finally{
				if(!connectionFromExtern){
					try{
						conn.close();
					}catch(java.sql.SQLException ex){
						EhiLogger.logError(ex);
					}finally{
						conn=null;
						config.setJdbcConnection(null);
					}
				}
			}			
		}catch(Ili2dbException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}catch(java.lang.RuntimeException ex){
			if(logfile!=null){
				logfile.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
			}
            if(xtflog!=null){
                xtflog.logEvent(new StdLogEvent(LogEvent.ERROR,null,ex,null));
            }
			throw ex;
		}finally{
            if(xtflog!=null){
                EhiLogger.getInstance().removeListener(xtflog);
                xtflog.close();
                xtflog=null;
            }
			if(logfile!=null){
				EhiLogger.getInstance().removeListener(logfile);
				logfile.close();
				logfile=null;
			}
			if(logStderr!=null){
				EhiLogger.getInstance().addListener(StdListener.getInstance());
				EhiLogger.getInstance().removeListener(logStderr);
			}
		}
	}
	private static Connection connect(String url, String dbusr, String dbpwd,
			Config config, CustomMapping customMapping) throws SQLException, IOException {
		Connection conn;
		EhiLogger.logState("dburl <" + url + ">");
		EhiLogger.logState("dbusr <" + dbusr + ">");
		String dbParams=config.getDbParams();
		if(dbParams!=null) {
	        EhiLogger.logState("dbparams <" + dbParams + ">");
		    java.io.Reader reader=null;
		    try {
	            reader=new java.io.InputStreamReader(new FileInputStream(dbParams),"UTF-8");
	            java.util.Properties props=new java.util.Properties();
	            props.load(reader);
	            config.setDbProperties(props);
            }finally {
		        if(reader!=null) {
		            try {
                        reader.close();
                    } catch (IOException e) {
                        ; // igonre
                    }
		            reader=null;
		        }
		    }
		}
		customMapping.preConnect(url, dbusr, dbpwd, config);
		conn = customMapping.connect(url, dbusr, dbpwd,config);
		config.setJdbcConnection(conn);
		return conn;
	}
	public static Long getDatasetId(String datasetName,Connection conn,Config config) throws Ili2dbException {
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		String sqlName=DbNames.DATASETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.PreparedStatement getstmt=null;
        java.sql.ResultSet res=null;
		try{
			String stmt="SELECT "+colT_ID+" FROM "+sqlName+" WHERE "+DbNames.DATASETS_TAB_DATASETNAME+"= ?";
			if(datasetName==null) {
				stmt="SELECT "+colT_ID+" FROM "+sqlName+" WHERE "+DbNames.DATASETS_TAB_DATASETNAME+" IS NULL";
			}
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			if(datasetName!=null) {
				getstmt.setString(1,datasetName);
			}
			res=getstmt.executeQuery();
			if(res.next()){
				long sqlId=res.getLong(1);
				return sqlId;
			}
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to query "+sqlName,ex);
		}finally{
            if(res!=null){
                try{
                    res.close();
                }catch(java.sql.SQLException ex){
                    EhiLogger.logError(ex);
                }
            }
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		return null;
	}
	public static long[] getBasketSqlIdsFromDatasetId(long datasetId,
			Configuration modelv,Connection conn,Config config) throws Ili2dbException {
		ArrayList<Long> ret=new ArrayList<Long>();
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		HashSet<String> models=new HashSet<String>();
		java.sql.PreparedStatement getstmt=null;
		try{
			String stmt="SELECT "+colT_ID+","+DbNames.BASKETS_TAB_TOPIC_COL+" FROM "+sqlName+" WHERE "+DbNames.BASKETS_TAB_DATASET_COL+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setLong(1,datasetId);
			java.sql.ResultSet res=getstmt.executeQuery();
			while(res.next()){
				long sqlId=res.getLong(1);
				String topicQName=res.getString(2);
				String topicName[]=splitIliQName(topicQName.toString());
				if(topicName[0]==null){
					// just a topicname
					throw new Ili2dbException("unexpected unqualified name "+topicQName+" in table "+sqlName);
				}
				String modelName=topicName[0];
				if(!models.contains(modelName)){
					modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
					models.add(modelName);
				}
				ret.add(sqlId);
			}
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to query "+sqlName,ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		long ret2[]=new long[ret.size()];
		int idx=0;
		for(long x:ret){
			ret2[idx++]=x;
		}
		return ret2;
	}
	private static long[] getBasketSqlIdsFromBID(String[] basketids,
			Configuration modelv,Connection conn,Config config) throws Ili2dbException {
		long ret[]=new long[basketids.length];
		int retidx=0;
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		HashSet<String> models=new HashSet<String>();
		for(String basketid:basketids){
			StringBuilder topicQName=new StringBuilder();
			Long sqlId=getBasketSqlIdFromBID(basketid, conn, schema,colT_ID, topicQName);
			if(sqlId==null){
				throw new Ili2dbException("no basket with BID "+basketid+" in table "+sqlName);
			}
			String topicName[]=splitIliQName(topicQName.toString());
			if(topicName[0]==null){
				// just a topicname
				throw new Ili2dbException("unexpected unqualified name "+topicQName+" in table "+sqlName);
			}
			String modelName=topicName[0];
			if(!models.contains(modelName)){
				modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
				models.add(modelName);
			}
			ret[retidx++]=sqlId;
		}
		return ret;
	}
	public static Long getBasketSqlIdFromBID(String basketid,Connection conn,String schema, String colT_ID,StringBuilder topicName) throws Ili2dbException {

		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		long sqlId=0;
		java.sql.PreparedStatement getstmt=null;
		try{
			String stmt="SELECT "+colT_ID+","+DbNames.BASKETS_TAB_TOPIC_COL+" FROM "+sqlName+" WHERE "+DbNames.T_ILI_TID_COL+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setString(1,basketid);
			java.sql.ResultSet res=getstmt.executeQuery();
			if(res.next()){
				sqlId=res.getLong(1);
				topicName.append(res.getString(2));
				return sqlId;
			}
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to query "+sqlName,ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		return null;
	}
	public static long[] getBasketSqlIdsFromTopic(String[] topics,
			Configuration modelv,Connection conn,Config config) throws Ili2dbException {
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		String qryTopics[][]=new String[topics.length][];
		int idx=0;
		for(String topic:topics){
			qryTopics[idx++]=splitIliQName(topic);
		}
		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		HashSet<Long> bids=new HashSet<Long>();
		String topicQName=null;
		long sqlId=0;
		java.sql.PreparedStatement getstmt=null;
		try{
	        HashSet<String> models=new HashSet<String>();
			String stmt="SELECT "+colT_ID+","+DbNames.BASKETS_TAB_TOPIC_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			java.sql.ResultSet res=getstmt.executeQuery();
			while(res.next()){
				sqlId=res.getLong(1);
				topicQName=res.getString(2);
				String dbTopic[]=splitIliQName(topicQName);
				String modelName=null;
				for(String qryTopic[]:qryTopics){
					if(qryTopic[0]==null && qryTopic[1].equals(dbTopic[1])){
						// found one
						modelName=dbTopic[0];
						break;
					}else if(qryTopic[0]!=null && qryTopic[0].equals(dbTopic[0]) && qryTopic[1].equals(dbTopic[1])){
						// found one
						modelName=dbTopic[0];
						break;
					}
				}
				// found a basket with given topicName?
				if(modelName!=null){
					if(!models.contains(modelName)){
						modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
						models.add(modelName);
					}
					if(!bids.contains(sqlId)){
						bids.add(sqlId);
					}
				}
			}
		}catch(java.sql.SQLException ex){
			throw new Ili2dbException("failed to query "+sqlName,ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		if(bids.size()==0) {
	        HashSet<String> models=new HashSet<String>();
            for(String qryTopic[]:qryTopics){
                String modelName=qryTopic[0];
                if(!models.contains(modelName)){
                    modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));             
                    models.add(modelName);
                }
            }
		    
		}
		long ret[]=new long[bids.size()];
		idx=0;
		for(long bid:bids){
			ret[idx++]=bid;
		}
		return ret;
	}
	private static long[] getBasketSqlIdsFromModel(String[] qryModels,
			Configuration modelv,Connection conn,Config config) throws Ili2dbException {
		String schema=config.getDbschema();
		String colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}

		int idx=0;
		String sqlName=DbNames.BASKETS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		HashSet<String> models=new HashSet<String>();
		HashSet<Long> bids=new HashSet<Long>();
		String topicQName=null;
		long sqlId=0;
		java.sql.PreparedStatement getstmt=null;
        java.sql.ResultSet res=null;
		try{
			String stmt="SELECT "+colT_ID+","+DbNames.BASKETS_TAB_TOPIC_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			res=getstmt.executeQuery();
			while(res.next()){
				sqlId=res.getLong(1);
				topicQName=res.getString(2);
				String dbTopic[]=splitIliQName(topicQName);
				String modelName=null;
				for(String qryModel:qryModels){
					if(qryModel.equals(dbTopic[0])){
						// found one
						modelName=qryModel;
						break;
					}
				}
				// found a basket with given topicName?
				if(modelName!=null){
					if(!models.contains(modelName)){
						modelv.addFileEntry(new ch.interlis.ili2c.config.FileEntry(modelName,ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
						models.add(modelName);
					}
					if(!bids.contains(sqlId)){
						bids.add(sqlId);
					}
				}
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
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		if(bids.size()==0){
			throw new Ili2dbException("no baskets with given model names in table "+sqlName);
		}
		long ret[]=new long[bids.size()];
		idx=0;
		for(long bid:bids){
			ret[idx++]=bid;
		}
		return ret;
	}
	private static String[] splitIliQName(String topicQName){
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
		String ret[]=new String[2];
		ret[0]=modelName;
		ret[1]=topicName;
		return ret;
	}
	@Deprecated
	public static String getModelFromXtf(String filename)
	{
		ch.interlis.iox.StartBasketEvent be=null;
		try{
			IoxReader ioxReader=null;
			if(isItfFilename(filename)){
				ioxReader=new ch.interlis.iom_j.itf.ItfReader(new java.io.File(filename));
			}else{
				ioxReader=new ch.interlis.iom_j.xtf.XtfReader(new java.io.File(filename));
			}
			// get first basket
			ch.interlis.iox.IoxEvent event;
			do{
				event=ioxReader.read();
				if(event instanceof ch.interlis.iox.StartBasketEvent){
					be=(ch.interlis.iox.StartBasketEvent)event;
					break;
				}
			}while(!(event instanceof ch.interlis.iox.EndTransferEvent));
			ioxReader.close();
			ioxReader=null;
		}catch(ch.interlis.iox.IoxException ex){
			EhiLogger.logError("failed to read model from xml file "+filename,ex);
			return null;
		}
		// no baskets?
		if(be==null){
			// no model
			return null;
		}
		String qtopic[]=be.getType().split("\\.");
		String model=qtopic[0];
		//EhiLogger.debug("model from xtf <"+model+">");
		return model;
	}
	public static String getModelFromXtf(java.io.InputStream f,String filename)
	{
		ch.interlis.iox.StartBasketEvent be=null;
		try{
			IoxReader ioxReader=null;
			if(isItfFilename(filename)){
				ioxReader=new ch.interlis.iom_j.itf.ItfReader(f);
			}else{
				ioxReader=new ch.interlis.iom_j.xtf.XtfReader(f);
			}
			// get first basket
			ch.interlis.iox.IoxEvent event;
			do{
				event=ioxReader.read();
				if(event instanceof ch.interlis.iox.StartBasketEvent){
					be=(ch.interlis.iox.StartBasketEvent)event;
					break;
				}
			}while(!(event instanceof ch.interlis.iox.EndTransferEvent));
			ioxReader.close();
			ioxReader=null;
		}catch(ch.interlis.iox.IoxException ex){
			EhiLogger.logError("failed to read model from xml file "+filename,ex);
			return null;
		}
		// no baskets?
		if(be==null){
			// no model
			return null;
		}
		String qtopic[]=be.getType().split("\\.");
		String model=qtopic[0];
		//EhiLogger.debug("model from xtf <"+model+">");
		return model;
	}
	public static ArrayList getModelDirv(String modeldir, String xtffile,String ili2dbHome) {
		ArrayList modeldirv=new ArrayList();
		String modeldirs[]=modeldir.split(ch.interlis.ili2c.Main.ILIDIR_SEPARATOR);
		for(int modeli=0;modeli<modeldirs.length;modeli++){
			String m=modeldirs[modeli];
			if(m.equals(XTF_DIR)){
				if(xtffile!=null){
					m=new java.io.File(xtffile).getAbsoluteFile().getParentFile().getAbsolutePath();
				}else{
					m=null;
				}
			}else if(m.equals(JAR_DIR)){
				if(ili2dbHome!=null){
					m=ili2dbHome;
				}else{
					m=null;
				}
			}
			if(m!=null && m.length()>0){
				modeldirv.add(m);				
			}
		}
		return modeldirv;
	}

	static private void transferFromXtf(Connection conn,IoxReader reader,int function,NameMapping ili2sqlName,TransferDescription td,
			String dbusr,
			SqlColumnConverter geomConv,
			DbIdGen idGen,
			Config config,
			Map<String,BasketStat> stat,
			TrafoConfig trafoConfig,Viewable2TableMapping class2wrapper,CustomMapping customMapping){	
		try{
			TransferFromXtf trsfr=new TransferFromXtf(function,ili2sqlName,td,conn,dbusr,geomConv,idGen,config,trafoConfig,class2wrapper);
			trsfr.doit(reader,config,stat,customMapping);
		}catch(ch.interlis.iox.IoxException ex){
			EhiLogger.logError("failed to transfer data from file to db",ex);
		} catch (Ili2dbException ex) {
			EhiLogger.logError("failed to transfer data from file to db",ex);
		}
	}
	/** transfer data from database to xml file
	*/
	static private void transferToXtf(Connection conn,int function,String xtffile,CustomMapping customMapping,NameMapping ili2sqlName,TransferDescription td
			,SqlColumnConverter geomConv
			,String sender
			,Config config
			,String exportParamModelnames[]
			,long basketSqlIds[]
			,Map<String,BasketStat> stat
			,TrafoConfig trafoConfig
			,Viewable2TableMapping class2wrapper){	

	    if(function==Config.FC_VALIDATE) {
	        try{
	            TransferToXtf trsfr=new TransferToXtf(ili2sqlName,td,conn,geomConv,config,trafoConfig,class2wrapper);
	            trsfr.doit(function,customMapping.shortenConnectUrl4Log(config.getDburl()),null,sender,exportParamModelnames,basketSqlIds,stat,customMapping);
	        }catch(ch.interlis.iox.IoxException ex){
	            EhiLogger.logError("failed to validate data from db",ex);
	        } catch (Ili2dbException ex) {
                EhiLogger.logError("failed to validate data from db",ex);
            }
	    }else {
	        java.io.File outfile=new java.io.File(xtffile);
	        IoxWriter ioxWriter=null;
	        try{
	            if(Config.ILIGML20.equals(config.getTransferFileFormat())){
	                ioxWriter=new Iligml20Writer(outfile,td);
	            }else{
	                String ext=ch.ehi.basics.view.GenericFileFilter.getFileExtension(xtffile).toLowerCase();
	                if(config.isItfTransferfile()){
	                    if(!config.getDoItfLineTables()){
	                        ioxWriter=new ItfWriter2(outfile,td);
	                    }else{
	                        ioxWriter=new ItfWriter(outfile,td);
	                    }
	                    config.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE_DO);
	                }else if(ext!=null && ext.equals("gml")){
	                    ioxWriter=new Iligml10Writer(outfile,td);
	                }else{
	                    ioxWriter=new XtfWriter(outfile,td);
                        final XtfModel[] xtfModelList = buildModelList(td,config,conn);
                        if(xtfModelList!=null && xtfModelList.length!=0) {
                            ((XtfWriter) ioxWriter).setModels(xtfModelList);
                        }
	                }
	            }
	            TransferToXtf trsfr=new TransferToXtf(ili2sqlName,td,conn,geomConv,config,trafoConfig,class2wrapper);
	            trsfr.doit(function,outfile.getName(),ioxWriter,sender,exportParamModelnames,basketSqlIds,stat,customMapping);
	            //trsfr.doitJava();
	            ioxWriter.flush();
	        }catch(ch.interlis.iox.IoxException ex){
	            EhiLogger.logError("failed to write xml output",ex);
	        } catch (Ili2dbException ex) {
                EhiLogger.logError("failed to write xml output",ex);
            }finally{
	            if(ioxWriter!=null){
	                try{
	                    ioxWriter.close();
	                }catch(ch.interlis.iox.IoxException ex){
	                    EhiLogger.logError("failed to close xml output",ex);
	                }
	            }
	            ioxWriter=null;
	        }
	    }
	}
    private static XtfModel[] buildModelList(TransferDescription td, Config config, Connection conn) 
            throws Ili2dbException 
    {
        String modelNames=config.getExportModels();
        if(modelNames!=null) {
            return buildModelList(td, modelNames);
        }
        modelNames=config.getModels();
        if(modelNames!=null && !modelNames.equals(XTF)) {
            return buildModelList(td, modelNames);
        }
        String topicNames=config.getTopics();
        if(topicNames!=null) {
            String topicv[]=topicNames.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
            String qryModels[]=new String[topicv.length];
            int idx=0;
            for(String topic:topicv){
                String qryTopic[]=splitIliQName(topic);
                qryModels[idx++]=qryTopic[0];
            }
            return buildModelList(td, qryModels);
        }

        String datasetName=config.getDatasetName();
        if(datasetName!=null) {
            String datasetNames[] = datasetName.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
            List<String> tmpListOfModels = new ArrayList<String>();
            for (String dtName : datasetNames) {
                Long datasetId=getDatasetId(dtName, conn, config);
                if(datasetId==null){
                    throw new Ili2dbException("dataset <"+dtName+"> doesn't exist");
                }
                ch.interlis.ili2c.config.Configuration modelv=new ch.interlis.ili2c.config.Configuration();
                getBasketSqlIdsFromDatasetId(datasetId,modelv,conn,config);
                for (int i = 0; i < modelv.getSizeFileEntry(); i++) {
                    String name=modelv.getFileEntry(i).getFilename();
                    if(!tmpListOfModels.contains(name)) {
                        tmpListOfModels.add(name);
                    }
                }
            }
            String qryModels[]=new String[tmpListOfModels.size()];
            for (int i = 0; i < tmpListOfModels.size(); i++) {
                qryModels[i] = tmpListOfModels.get(i);
            }
            return buildModelList(td, qryModels);
        }
        String baskets=config.getBaskets();
        if(baskets!=null){
            String basketids[]=baskets.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
            // map BID to sqlBasketId and modelnames
            ch.interlis.ili2c.config.Configuration modelv=new ch.interlis.ili2c.config.Configuration();
            getBasketSqlIdsFromBID(basketids,modelv,conn,config);
            List<String> tmpListOfModels = new ArrayList<String>();
            for (int i = 0; i < modelv.getSizeFileEntry(); i++) {
                String name=modelv.getFileEntry(i).getFilename();
                if(!tmpListOfModels.contains(name)) {
                    tmpListOfModels.add(name);
                }
            }
            String qryModels[]=new String[tmpListOfModels.size()];
            for (int i = 0; i < tmpListOfModels.size(); i++) {
                qryModels[i] = tmpListOfModels.get(i);
            }
            return buildModelList(td, qryModels);
        }
        return null;
    }
    private static XtfModel[] buildModelList(TransferDescription td,String modelNames){
        List<Model> modelv=getModels(modelNames, td);
        return buildModelList(modelv);
    }
    private static XtfModel[] buildModelList(TransferDescription td,String modelNames[]){
        List<Model> modelv=getModels(modelNames, td);
        return buildModelList(modelv);
    }
    private static XtfModel[] buildModelList(List<Model> modelv) {
        XtfModel[] ret=new XtfModel[modelv.size()];
        for(int i=0;i<modelv.size();i++){
            Model model=(Model)modelv.get(i);
            ret[i]=new XtfModel();
            ret[i].setName(model.getName());
            String version=model.getModelVersion();
            ret[i].setVersion(version==null?"":version);
            String issuer=model.getIssuer();
            ret[i].setUri(issuer==null?"":issuer);
        }
        return ret;
    }
	
	static private HashSet getModelNames(ArrayList modelv){
		HashSet ret=new HashSet();
		Iterator modeli=modelv.iterator();
		while(modeli.hasNext()){
			String topic=(String)modeli.next(); // modelname or qualified topicname
			int endModelName=topic.indexOf('.');
			if(endModelName<=0){
				// just a modelname
				ret.add(topic);
			}else{
				// qualified topicname; get model name
				String model=topic.substring(0,endModelName);
				ret.add(model);
			}
		}
		return ret;
	}
	public static String[] getModelNames(String models) {
		String modelnames[]=models.split(ch.interlis.ili2c.Main.MODELS_SEPARATOR);
		return modelnames;
	}
	static private void writeScript(String filename,Iterator linei)
	throws java.io.IOException
	{
		java.io.PrintStream out=new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(filename)),false,"UTF-8");
		while(linei.hasNext()){
		  GeneratorJdbc.AbstractStmt stmt=(GeneratorJdbc.AbstractStmt)linei.next();
		  String line=stmt.getLine();
		  if(stmt instanceof GeneratorJdbc.Stmt){
			out.println(line+";");
		  }else{
			out.println("-- "+line);
		  }
		}
		out.close();				
	}
	public static boolean isItfFilename(String filename)
	{
		String xtfExt=ch.ehi.basics.view.GenericFileFilter.getFileExtension(new java.io.File(filename));
		if(xtfExt!=null && "itf".equals(xtfExt.toLowerCase())){
			return true;
		}
		return false;
	}
	public static void logDBVersion(Connection conn)
	{
		try {
			EhiLogger.logState("databaseProduct <"
					+ conn.getMetaData().getDatabaseProductName() + ">");
			EhiLogger.logState("databaseVersion <"
					+ conn.getMetaData().getDatabaseProductVersion() + ">");
			EhiLogger.logState("driverName <"
					+ conn.getMetaData().getDriverName() + ">");
			EhiLogger.logState("driverVersion <"
					+ conn.getMetaData().getDriverVersion() + ">");
			if(conn.getMetaData().getURL().startsWith("jdbc:postgresql:")){
				try {
					java.sql.Statement stmt=conn.createStatement();
					String sql="SELECT PostGIS_Full_Version()";
					ResultSet rs=stmt.executeQuery(sql);
					if(rs.next()){
						String ver=rs.getString(1);
						EhiLogger.logState("postGISVersion <"+ ver + ">");
					}
				} catch (SQLException e) {
					throw new IllegalStateException("failed to get PostGIS version",e);
				}
			}
		} catch (SQLException e) {
			EhiLogger.logError(e);
		}
		
	}
	private static CustomMapping getCustomMappingStrategy(ch.ehi.ili2db.gui.Config config)
	throws Ili2dbException
	{
		String mappingClassName=config.getIli2dbCustomStrategy();
		if(mappingClassName==null){
			return new CustomMappingNull();
		}
		CustomMapping mapping=null;
		try{
			mapping=(CustomMapping)Class.forName(mappingClassName).newInstance();
		}catch(Exception ex){
			throw new Ili2dbException("failed to load/create custom mapping strategy",ex);
		}
		return mapping;
	}
	public static void setSkipPolygonBuilding(Config config) {
        config.setDoXtfLineTables(true);
		config.setDoItfLineTables(true);
		config.setAreaRef(Config.AREA_REF_KEEP);
	}
	public static void setNoSmartMapping(Config config) {
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultiLineTrafo(null);
		config.setMultiPointTrafo(null);
		config.setArrayTrafo(null);
        config.setJsonTrafo(null);
		config.setMultilingualTrafo(null);
        config.setLocalisedTrafo(null);
		config.setInheritanceTrafo(null);
	}
	public static List<Model> getModels(String modelNames, TransferDescription td) {
		if(modelNames==null) {
			return new ArrayList<Model>();
		}
		String modelNamev[]=getModelNames(modelNames);
		return getModels(modelNamev,td);
	}
    public static List<Model> getModels(String modelNamev[], TransferDescription td) {
        if(modelNamev==null) {
            return new ArrayList<Model>();
        }
        List<Model> models=new ArrayList<Model>();
        for(String modelName:modelNamev) {
            Model model=(Model)td.getElement(Model.class, modelName);
            if(model==null) {
                throw new IllegalArgumentException("unknown model <"+modelName+">");
            }
            models.add(model);
        }
        return models;
    }
    public static String quoteSqlStringValue(String value) {
        if(value==null) {
            return "NULL";
        }
        return "'"+value.replace("'", "''")+"'";
    }
    private static boolean isWriteable(File f) throws IOException {
        f.createNewFile();
        return f.canWrite();
    }

}
