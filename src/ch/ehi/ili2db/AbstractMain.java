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
package ch.ehi.ili2db;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.interlis.ili2c.gui.UserSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public abstract class AbstractMain {
	public abstract String getAPP_NAME();
	public abstract String getDB_PRODUCT_NAME();
	public abstract String getJAR_NAME();
	public abstract AbstractDbPanelDescriptor getDbPanelDescriptor();
	protected abstract void printConnectOptions();
	protected abstract void printSpecificOptions();
	protected abstract int doArgs(String args[],int argi,Config config) throws ParseException;
	public void initConfig(Config config)
	{
		config.setSender(getAPP_NAME()+"-"+getVersion());
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
	}
	public abstract DbUrlConverter getDbUrlConverter();

	public void domain(String args[]){
		Config config=new Config();
		initConfig(config);
		ch.ehi.basics.settings.Settings settings=new ch.ehi.basics.settings.Settings();
		config.setAppSettings(settings);
		if(args.length==0){
			Ili2db.readAppSettings(settings);
			runGUI(config);
			Ili2db.writeAppSettings(settings);
			return;
		}else{
			for(int argi=0;argi<args.length;argi++){
				String arg=args[argi];
				if(arg.equals("--gui")){
					Ili2db.readAppSettings(settings);
					break;
				}
			}
		}
		int argi=0;
		boolean doGui=false;
		try {
			for (; argi < args.length; ) {
				while (argi < args.length) {
					int oldargi = argi;
					argi = doArgs(args, argi, config);
					if (argi == oldargi) {
						break;
					}
				}
				if (argi >= args.length) {
					break;
				}
				String arg = args[argi];
				if (arg.equals("--modeldir")) {
					argi++;
					config.setModeldir(args[argi]);
					argi++;
				} else if (arg.equals("--models")) {
					argi++;
					config.setModels(args[argi]);
					argi++;
				} else if (arg.equals("--exportModels")) {
					argi++;
					config.setExportModels(args[argi]);
					argi++;
				} else if (arg.equals("--exportCrsModels")) {
					argi++;
					config.setCrsExportModels(args[argi]);
					argi++;
				} else if (arg.equals("--nameLang")) {
					argi++;
					config.setNameLanguage(args[argi]);
					argi++;
				} else if (arg.equals("--dataset")) {
					argi++;
					config.setDatasetName(args[argi]);
					argi++;
				} else if (arg.equals("--baskets")) {
					argi++;
					config.setBaskets(args[argi]);
					argi++;
				} else if (arg.equals("--topics")) {
					argi++;
					config.setTopics(args[argi]);
					argi++;
				} else if (isOption(arg,"--gui")) {
					doGui = parseBooleanArgument(arg);
					argi++;
				} else if (isOption(arg, "--validate")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_VALIDATE);
					argi++;
				} else if (isOption(arg, "--import")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_IMPORT);
					argi++;
				} else if (isOption(arg, "--update")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_UPDATE);
					argi++;
				} else if (isOption(arg, "--delete")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_DELETE);
					argi++;
				} else if (isOption(arg, "--replace")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_REPLACE);
					argi++;
				} else if (isOption(arg, "--export")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_EXPORT);
					argi++;
                } else if (isOption(arg, "--exportMetaConfig")) {
                    if (parseBooleanArgument(arg)) {
                        config.setFunction(Config.FC_EXPORT_METACONFIG);
                    }
                    argi++;
				} else if (arg.equals("--export3")) {
					if (parseBooleanArgument(arg)) {
						config.setFunction(Config.FC_EXPORT);
						config.setVer3_export(true);
					}
					argi++;
				} else if (isOption(arg, "--schemaimport")) {
					if (parseBooleanArgument(arg))
						config.setFunction(Config.FC_SCHEMAIMPORT);
					argi++;
				} else if (arg.equals("--preScript")) {
					argi++;
					config.setPreScript(args[argi]);
					argi++;
				} else if (arg.equals("--postScript")) {
					argi++;
					config.setPostScript(args[argi]);
					argi++;
				} else if (isOption(arg, "--deleteData")) {
					if (parseBooleanArgument(arg))
						config.setDeleteMode(Config.DELETE_DATA);
					argi++;
				} else if (isOption(arg, "--trace")) {
					EhiLogger.getInstance().setTraceFilter(!parseBooleanArgument(arg));
					argi++;
				} else if (arg.equals("--dbparams")) {
					argi++;
					config.setDbParams(args[argi]);
					argi++;
				} else if (arg.equals("--dropscript")) {
					argi++;
					config.setDropscript(args[argi]);
					argi++;
				} else if (arg.equals("--createscript")) {
					argi++;
					config.setCreatescript(args[argi]);
					argi++;
				} else if (arg.equals("--log")) {
					argi++;
					config.setLogfile(args[argi]);
					argi++;
                } else if (arg.equals("--logtime")) {
                    argi++;
                    config.setLogtime(parseBooleanArgument(arg));
				} else if (arg.equals("--xtflog")) {
					argi++;
					config.setXtfLogfile(args[argi]);
					argi++;
				} else if (arg.equals("--defaultSrsAuth")) {
					argi++;
					String auth = args[argi];
					if (auth.equalsIgnoreCase("NULL")) {
						auth = null;
					}
					config.setDefaultSrsAuthority(auth);
					argi++;
				} else if (arg.equals("--defaultSrsCode")) {
					argi++;
					config.setDefaultSrsCode(args[argi]);
					argi++;
				} else if (arg.equals("--modelSrsCode")) {
					argi++;
					config.setModelSrsCode(args[argi]);
					argi++;
				} else if (isOption(arg, "--multiSrs")) {
					config.setUseEpsgInNames(parseBooleanArgument(arg));
					argi++;
				} else if (arg.equals("--domains")) {
					argi++;
					config.setDomainAssignments(args[argi]);
					argi++;
				} else if (arg.equals("--altSrsModel")) {
					argi++;
					config.setSrsModelAssignment(args[argi]);
					argi++;
				} else if (arg.equals("--attachmentsPath")) {
					argi++;
					config.setAttachmentsPath(args[argi]);
					argi++;
				} else if (arg.equals("--validConfig")) {
					argi++;
					config.setValidConfigFile(args[argi]);
					argi++;
                } else if (arg.equals("--metaConfig")) {
                    argi++;
                    config.setMetaConfigFile(args[argi]);
                    argi++;
				} else if (isOption(arg, "--disableValidation")) {
					argi++;
					config.setValidation(!parseBooleanArgument(arg));
				} else if (isOption(arg, "--disableAreaValidation")) {
					argi++;
					config.setDisableAreaValidation(parseBooleanArgument(arg));
				} else if (isOption(arg, "--disableRounding")) {
					argi++;
					config.setDisableRounding(parseBooleanArgument(arg));
				} else if (isOption(arg, "--disableBoundaryRecoding")) {
					argi++;
					config.setRepairTouchingLines(!parseBooleanArgument(arg));
				} else if (isOption(arg, "--forceTypeValidation")) {
					argi++;
					config.setOnlyMultiplicityReduction(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createSingleEnumTab")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
				} else if (isOption(arg, "--createEnumTabs")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				} else if (isOption(arg, "--createEnumTabsWithId")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
				} else if (isOption(arg, "--createEnumTxtCol")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
				} else if (isOption(arg, "--createEnumColAsItfCode")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setValue(Config.CREATE_ENUMCOL_AS_ITFCODE, Config.CREATE_ENUMCOL_AS_ITFCODE_YES);
				} else if (isOption(arg, "--beautifyEnumDispName")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
				} else if (isOption(arg, "--noSmartMapping")) {
					argi++;
					if (parseBooleanArgument(arg))
						Ili2db.setNoSmartMapping(config);
				} else if (isOption(arg, "--smart1Inheritance")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				} else if (isOption(arg, "--smart2Inheritance")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
				} else if (isOption(arg, "--coalesceCatalogueRef")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
				} else if (isOption(arg, "--coalesceMultiSurface")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
				} else if (isOption(arg, "--coalesceMultiLine")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
				} else if (isOption(arg, "--coalesceMultiPoint")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setMultiPointTrafo(Config.MULTIPOINT_TRAFO_COALESCE);
				} else if (isOption(arg, "--coalesceArray")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
				} else if (isOption(arg, "--coalesceJson")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
                } else if (isOption(arg, "--expandStruct")) {
                    argi++;
                    if (parseBooleanArgument(arg))
                        config.setStructTrafo(Config.STRUCT_TRAFO_EXPAND);
				} else if (isOption(arg, "--expandMultilingual")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
				} else if (isOption(arg, "--expandLocalised")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
				} else if (isOption(arg, "--createFk")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateFk(Config.CREATE_FK_YES);
				} else if (isOption(arg, "--createFkIdx")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateFkIdx(Config.CREATE_FKIDX_YES);
				} else if (isOption(arg, "--createUnique")) {
					argi++;
					config.setCreateUniqueConstraints(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createNumChecks")) {
					argi++;
					config.setCreateNumChecks(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createTextChecks")) {
					argi++;
					config.setCreateTextChecks(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createDateTimeChecks")) {
					argi++;
					config.setCreateDateTimeChecks(parseBooleanArgument(arg));
                } else if (isOption(arg, "--createMandatoryChecks")) {
                    argi++;
                    config.setCreateMandatoryChecks(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createImportTabs")) {
					argi++;
					config.setCreateImportTabs(parseBooleanArgument(arg));
				} else if (isOption(arg, "--createStdCols")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateStdCols(Config.CREATE_STD_COLS_ALL);
				} else if (arg.equals("--t_id_Name")) {
					argi++;
					config.setColT_ID(args[argi]);
					argi++;
				} else if (arg.equals("--idSeqMin")) {
					argi++;
					config.setMinIdSeqValue(Long.parseLong(args[argi]));
					argi++;
				} else if (arg.equals("--idSeqMax")) {
					argi++;
					config.setMaxIdSeqValue(Long.parseLong(args[argi]));
					argi++;
				} else if (isOption(arg, "--createTypeDiscriminator")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateTypeDiscriminator(Config.CREATE_TYPE_DISCRIMINATOR_ALWAYS);
				} else if (isOption(arg, "--createGeomIdx")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE);
				} else if (isOption(arg, "--disableNameOptimization")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setNameOptimization(Config.NAME_OPTIMIZATION_DISABLE);
				} else if (isOption(arg, "--nameByTopic")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				} else if (arg.equals("--maxNameLength")) {
					argi++;
					config.setMaxSqlNameLength(args[argi]);
					argi++;
                } else if (isOption(arg, "--sqlColsAsText")) {
                    argi++;
                    if (parseBooleanArgument(arg))
                        config.setSqlColsAsText(Config.SQL_COLS_AS_TEXT_ENABLE);
				} else if (isOption(arg, "--sqlEnableNull")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setSqlNull(Config.SQL_NULL_ENABLE);
                } else if (isOption(arg, "--sqlExtRefCols")) {
                    argi++;
                    if (parseBooleanArgument(arg))
                        config.setSqlExtRefCols(Config.SQL_EXTREF_ENABLE);
				} else if (isOption(arg, "--strokeArcs")) {
					argi++;
					if (parseBooleanArgument(arg))
						Config.setStrokeArcs(config, Config.STROKE_ARCS_ENABLE);
				} else if (isOption(arg, "--skipPolygonBuilding")) {
					argi++;
					if (parseBooleanArgument(arg))
						Ili2db.setSkipPolygonBuilding(config);
				} else if (isOption(arg, "--skipPolygonBuildingErrors")) {
					// DEPRECATED remove option
					argi++;
					config.setSkipGeometryErrors(parseBooleanArgument(arg));
				} else if (isOption(arg, "--skipReferenceErrors")) {
					argi++;
					config.setSkipReferenceErrors(parseBooleanArgument(arg));
				} else if (isOption(arg, "--skipGeometryErrors")) {
					argi++;
					config.setSkipGeometryErrors(parseBooleanArgument(arg));
				} else if (isOption(arg, "--keepAreaRef")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setAreaRef(Config.AREA_REF_KEEP);
				} else if (isOption(arg, "--createTidCol")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				} else if (isOption(arg, "--importTid")) {
					argi++;
					config.setImportTid(parseBooleanArgument(arg));
				} else if (isOption(arg, "--exportTid")) {
					argi++;
					config.setExportTid(parseBooleanArgument(arg));
                } else if (isOption(arg, "--importBid")) {
                    argi++;
                    config.setImportBid(parseBooleanArgument(arg));
                } else if (isOption(arg, "--exportFetchSize")) {
                    argi++;
                    config.setFetchSize(Integer.parseInt(args[argi]));
                    argi++;
                } else if (isOption(arg, "--importBatchSize")) {
                    argi++;
                    config.setBatchSize(Integer.parseInt(args[argi]));
                    argi++;
                } else if (isOption(arg, "--createBasketCol")) {
                    argi++;
                    if (parseBooleanArgument(arg))
                        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                } else if (isOption(arg, "--createDatasetCol")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setCreateDatasetCols(Config.CREATE_DATASET_COL);
				} else if (isOption(arg, "--ILIGML20")) {
					argi++;
					if (parseBooleanArgument(arg))
						config.setTransferFileFormat(Config.ILIGML20);
				} else if (isOption(arg, "--ver4-noSchemaImport")) {
					argi++;
					if (parseBooleanArgument(arg)) {
						config.setDoImplicitSchemaImport(false);
						EhiLogger.logAdaption("--ver4-noSchemaImport is a deprecated option");
					}
				} else if (isOption(arg, "--doSchemaImport")) {
					argi++;
					config.setDoImplicitSchemaImport(parseBooleanArgument(arg));
				} else if (isOption(arg, "--ver4-translation")) {
					argi++;
					if (parseBooleanArgument(arg)) {
						config.setVer3_translation(false);
						EhiLogger.logAdaption("--ver4-translation is a deprecated option");
					}
				} else if (isOption(arg, "--ver3-translation")) {
					argi++;
					config.setVer3_translation(parseBooleanArgument(arg));
				} else if (arg.equals("--translation")) {
					argi++;
					config.setIli1Translation(args[argi]);
					argi++;
				} else if (arg.equals("--proxy")) {
					argi++;
					config.setValue(UserSettings.HTTP_PROXY_HOST, args[argi]);
					argi++;
				} else if (arg.equals("--proxyPort")) {
					argi++;
					config.setValue(UserSettings.HTTP_PROXY_PORT, args[argi]);
					argi++;
				} else if (isOption(arg, "--createMetaInfo")) {
					argi++;
					config.setCreateMetaInfo(parseBooleanArgument(arg));
                } else if (isOption(arg, "--createNlsTab")) {
                    argi++;
                    config.setCreateNlsTab(parseBooleanArgument(arg));
				} else if (arg.equals("--version")) {
					printVersion();
					return;
				} else if (arg.equals("--iliMetaAttrs")) {
					argi++;
					config.setIliMetaAttrsFile(args[argi]);
					argi++;
				} else if (isOption(arg, "--createTypeConstraint")) {
					argi++;
					config.setCreateTypeConstraint(parseBooleanArgument(arg));
				} else if (arg.equals("--verbose")) {
					argi++;
					config.setVerbose(parseBooleanArgument(arg));
				}else if(arg.equals("--plugins")) {
					argi++;
					config.setPluginsFolder(args[argi]);
					argi++;
				} else if (arg.equals("--help")) {
					printVersion();
					System.err.println();
					printDescription();
					System.err.println();
					printUsage();
					System.err.println();
					System.err.println("OPTIONS");
					System.err.println();
					System.err.println("--import               do an import.");
					System.err.println("--update               do an update.");
					System.err.println("--replace              do a replace.");
					System.err.println("--delete               do a delete.");
					System.err.println("--export               do an export.");
                    System.err.println("--validate             validates the data in the db (without export).");
					System.err.println("--schemaimport         do a schema import.");
					System.err.println("--exportMetaConfig     exports a Meta-Config file of an existing db.");
					System.err.println("--preScript file       before running a function, run a script.");
					System.err.println("--postScript file      after running a function, run a script.");
					System.err.println("--dbparams file        config file with connection parameters.");
					printConnectOptions();
                    System.err.println("--metaConfig file      Meta-Config file for ili2db.");
					System.err.println("--validConfig file     Config file for validation.");
					System.err.println("--disableValidation    Disable validation of data.");
					System.err.println("--disableAreaValidation Disable AREA validation.");
					System.err.println("--forceTypeValidation  restrict customization of validation related to \"multiplicity\"");
					System.err.println("--disableBoundaryRecoding disables the correction of self touching lines");
					System.err.println("--disableRounding      Disable rounding of import/export data.");
					System.err.println("--deleteData           on schema/data import, delete existing data from existing tables.");
					System.err.println("--defaultSrsAuth  auth Default SRS authority " + config.getDefaultSrsAuthority());
					System.err.println("--defaultSrsCode  code Default SRS code");
					System.err.println("--modelSrsCode  model=code SRS code per model");
					System.err.println("--multiSrs             create a DB schema that supports multiple SRS codes");
					System.err.println("--domains genericDomain=concreteDomain overrides the generic domain assignments on export");
					System.err.println("--altSrsModel originalSrsModel=alternativeSrsModel assigns a model with an alternative SRS (but same structure as orinal model)");
					System.err.println("--modeldir  path       Path(s) of directories containing ili-files.");
					System.err.println("--models modelname     Name(s) of ili-models to generate an db schema for.");
					System.err.println("--dataset name         Name of dataset.");
					System.err.println("--baskets BID          Basket-Id(s) of ili-baskets to export.");
					System.err.println("--topics topicname     Name(s) of ili-topics to export.");
					System.err.println("--createscript filename  Generate a sql script that creates the db schema.");
					System.err.println("--dropscript filename  Generate a sql script that drops the generated db schema.");
					System.err.println("--noSmartMapping       disable all smart mappings");
					System.err.println("--smart1Inheritance     enable smart1 mapping of class/structure inheritance");
					System.err.println("--smart2Inheritance     enable smart2 mapping of class/structure inheritance");
					System.err.println("--coalesceCatalogueRef enable smart mapping of CHBase:CatalogueReference");
					System.err.println("--coalesceMultiSurface enable smart mapping of CHBase:MultiSurface");
					System.err.println("--coalesceMultiLine    enable smart mapping of CHBase:MultiLine");
					System.err.println("--coalesceMultiPoint   enable smart mapping of MultiPoint structures");
					System.err.println("--coalesceArray        enable smart mapping of ARRAY structures");
					System.err.println("--coalesceJson         enable smart mapping of JSON structures");
                    System.err.println("--expandStruct         enable unfolding/expanding of structures");
					System.err.println("--expandMultilingual   enable smart mapping of CHBase:MultilingualText");
					System.err.println("--expandLocalised      enable smart mapping of CHBase:LocalisedText");
					System.err.println("--createGeomIdx        create a spatial index on geometry columns.");
					System.err.println("--createEnumColAsItfCode create enum type column with value according to ITF (instead of XTF).");
					System.err.println("--createEnumTxtCol     create an additional column with the text of the enumeration value.");
					System.err.println("--createEnumTabs       generate tables for enum definitions and use xtfcode to reference entries in generated enum tables.");
					System.err.println("--createEnumTabsWithId generate tables with " + DbNames.T_ID_COL + " for enum definitions and use ids to reference entries in generated enum tables.");
					System.err.println("--createSingleEnumTab  generate all enum definitions in a single table.");
					System.err.println("--beautifyEnumDispName replace underscore with space in dispName of enum table entries");
					System.err.println("--createStdCols        generate " + DbNames.T_USER_COL + ", " + DbNames.T_CREATE_DATE_COL + ", " + DbNames.T_LAST_CHANGE_COL + " columns.");
					System.err.println("--t_id_Name name       change name of t_id column (" + DbNames.T_ID_COL + ")");
					System.err.println("--idSeqMin minValue    sets the minimum value of the id sequence generator.");
					System.err.println("--idSeqMax maxValue    sets the maximum value of the id sequence generator.");
					System.err.println("--createTypeDiscriminator  generate always a type discriminator column.");
					System.err.println("--disableNameOptimization disable use of unqualified class name as table name.");
					System.err.println("--nameByTopic          use topic+class name as table name.");
					System.err.println("--nameLang lang        use names of ili model in given language as table/column name.");
					System.err.println("--maxNameLength length max length of sql names (" + config.getMaxSqlNameLength() + ")");
					System.err.println("--sqlEnableNull        create no NOT NULL constraints in db schema.");
					System.err.println("--sqlColsAsText        Every simple-type attribute will be mapped to a text column, to enable the import of invalid data");
                    System.err.println("--sqlExtRefCols        external references will be mapped to a column with the ili OID domain, to enable the import of not available objects");
					System.err.println("--strokeArcs           stroke ARCS on import.");
					System.err.println("--skipPolygonBuilding  keep linetables; don't build polygons on import.");
					System.err.println("--skipReferenceErrors  ignore/do not report reference errors.");
					System.err.println("--skipGeometryErrors   ignore/do not report geometry errors.");
					System.err.println("--keepAreaRef          keep areaRef as additional column on import.");
					System.err.println("--createTidCol         create an additional column " + DbNames.T_ILI_TID_COL);
					System.err.println("--importTid            read transient TIDs into column " + DbNames.T_ILI_TID_COL);
					System.err.println("--exportTid            write transient TIDs from column " + DbNames.T_ILI_TID_COL);
					System.err.println("--importBid            read transient BIDs into " + DbNames.BASKETS_TAB + "." + DbNames.T_ILI_TID_COL);
                    System.err.println("--exportFetchSize nrOfRecords      set the fetch size for the SQL query statements");
                    System.err.println("--importBatchSize nrOfRecords     set the batch size for the SQL insert/update statements");
					System.err.println("--createImportTabs     create tables with import statistics. (" + DbNames.IMPORTS_TAB + ")");
					System.err.println("--createBasketCol      generate " + DbNames.T_BASKET_COL + " column.");
					System.err.println("--createDatasetCol     generate " + DbNames.T_DATASET_COL + " column (Requires --dataset)");
					System.err.println("--createFk             generate foreign key constraints.");
					System.err.println("--createFkIdx          create an index on foreign key columns.");
					System.err.println("--createUnique         create UNIQUE db constraints.");
					System.err.println("--createNumChecks      create CHECK db constraints for numeric data types.");
					System.err.println("--createTextChecks     create CHECK db constraints for text data types.");
					System.err.println("--createDateTimeChecks create CHECK db constraints for date/time data types.");
                    System.err.println("--createMandatoryChecks create CHECK db constraints for MANDATORY attributes.");
					System.err.println("--ILIGML20             use eCH-0118-2.0 as transferformat");
					System.err.println("--exportModels modelname  export data according to the given base ili-models");
					System.err.println("--exportCrsModels modelname  export data according to the given ili-model (with alternate CRS)");
					System.err.println("--ver4-noSchemaImport  do no implicit schema import during data import");
					System.err.println("--doSchemaImport       do implicit schema import during data import");
					System.err.println("--ver4-translation     supports TRANSLATION OF in ili2db 4.x mode (incompatible with ili2db 3.x versions).");
					System.err.println("--ver3-translation     supports TRANSLATION OF in ili2db 3.x mode (incompatible with ili2db 4.x versions).");
					System.err.println("--translation translatedModel=originModel assigns a translated model to its orginal language equivalent.");
					System.err.println("--createMetaInfo       Create aditional ili-model information.");
                    System.err.println("--createNlsTab         Create a helper table with multilingual data about model elements.");
					System.err.println("--iliMetaAttrs file    Import meta-attributes from a .toml file (Requires --createMetaInfo)");
					System.err.println("--createTypeConstraints   Create CHECK constraint on t_type columns.");
					System.err.println("--plugins folder       directory with jar files that contain user defined functions.");
					printSpecificOptions();
					System.err.println("--proxy host           proxy server to access model repositories.");
					System.err.println("--proxyPort port       proxy port to access model repositories.");
					System.err.println("--log filename         log messages to given file.");
                    System.err.println("--logtime              include timestamps in logfile.");
					System.err.println("--xtflog filename      log messages to given XTF file.");
					System.err.println("--verbose              print additional information in validation results.");
					System.err.println("--gui                  start GUI.");
					System.err.println("--trace                enable trace messages.");
					System.err.println("--help                 Display this help text.");
					System.err.println("--version              Display the version of " + getAPP_NAME());
					System.err.println();
					return;

				} else if (arg.startsWith("-")) {
					EhiLogger.logError(arg + ": unknown option");
					System.exit(1);
				} else if (argi + 1 < args.length) {
					EhiLogger.logError(arg + ": invalid placed argument");
					System.exit(1);
				} else {
					break;
				}
			}
		} catch (ParseException ex){
			EhiLogger.logError(ex);
			System.exit(1);
		}
		if(config.getFunction()==Config.FC_UNDEFINED) {
		    if(config.getCreatescript()!=null || config.getDropscript()!=null) {
                config.setFunction(Config.FC_SCRIPT);
		    }
		}
		if(argi+1==args.length){
			String xtfFilename=args[argi];
			config.setXtffile(xtfFilename);
			if(Ili2db.isItfFilename(xtfFilename)){
				config.setItfTransferfile(true);
			}
		}
		if(doGui){
			runGUI(config);
			Ili2db.writeAppSettings(settings);
		}else{
			try {
	            if(config.getFunction()!=Config.FC_SCRIPT) {
	                final String dbUrl = getDbUrlConverter().makeUrl(config);
	                config.setDburl(dbUrl);
	                if(dbUrl==null) {
	                    printConnectOptions();
	                    throw new Ili2dbException("incomplete DB connect options given");
	                }
	            }
	            if(config.getFunction()!=Config.FC_SCRIPT) {
	                Ili2db.readSettingsFromDb(config);
	            }
				Ili2db.run(config,getAPP_HOME());
			} catch (Exception ex) {
				EhiLogger.logError(ex);
				System.exit(1);
			}
		}

	}

	protected boolean parseBooleanArgument(String arg) throws ParseException {
		int indexEquals = arg.indexOf('=');
		if (indexEquals >= 0){
			String value = arg.substring(indexEquals + 1);

			if (value.equalsIgnoreCase("true")) return true;
			if (value.equalsIgnoreCase("false")) return false;

			String errorMessage = String.format("Could not parse boolean value <%s> for option <%s>.", value, arg.substring(0,indexEquals));
			EhiLogger.logAdaption(errorMessage);
			throw new ParseException(errorMessage, indexEquals+1);
		}

		return true;
	}

	protected boolean isOption(String arg, String optionName) {
		return arg.equals(optionName) || arg.startsWith(optionName + '=');
 	}

	private void printVersion ()
	{
	  System.err.println("INTERLIS 2-loader for "+getDB_PRODUCT_NAME()+", Version "+getVersion());
	  System.err.println("  Developed by Eisenhut Informatik AG, CH-3401 Burgdorf");
	  System.err.println("  See http://www.interlis.ch for information about INTERLIS");
	  System.err.println("  Parts of this program have been generated by ANTLR; see http://www.antlr.org");
	  System.err.println("  This product includes software developed by the");
	  System.err.println("  Apache Software Foundation (http://www.apache.org/).");
	}


	private void printDescription ()
	{
	  System.err.println("DESCRIPTION");
	  System.err.println("  Translates INTERLIS 2 data model definitions to a "+getDB_PRODUCT_NAME()+" schema.");
	  System.err.println("  Loads INTERLIS 2 data into a "+getDB_PRODUCT_NAME()+" database.");
	  System.err.println("  Unloads INTERLIS 2 data from a "+getDB_PRODUCT_NAME()+" database.");
	}


	private void printUsage()
	{
	  System.err.println ("USAGE");
	  System.err.println("  java -jar "+getJAR_NAME()+" [Options] [file.xtf]");
	}
	private  String version=null;
	public String getVersion() {
		  if(version==null){
		java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle(ch.ehi.basics.i18n.ResourceBundle.class2qpackageName(AbstractMain.class)+".Version");
		//java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle("ch/ehi/ili2db/Version");
			// Major version numbers identify significant functional changes.
			// Minor version numbers identify smaller extensions to the functionality.
			// Micro versions are even finer grained versions.
			StringBuffer ret=new StringBuffer(20);
            ret.append(resVersion.getString("version"));
            ret.append('-');
            ret.append(resVersion.getString("versionCommit"));
			version=ret.toString();
		  }
		  return version;
	}
	public String getAPP_HOME()
	{
	  String classpath = System.getProperty("java.class.path");
	  int index = classpath.toLowerCase().indexOf(getJAR_NAME());
	  int start = classpath.lastIndexOf(java.io.File.pathSeparator,index) + 1;
	  if(index > start)
	  {
		  return classpath.substring(start,index - 1);
	  }
	  return null;
	}
	
    public boolean runGUI(Config config) {
    //ch.ehi.ili2db.gui.MainWizard dialog=new ch.ehi.ili2db.gui.MainWizard();
    //return dialog.showDialog();
    Class dialogClass=null;
    try {
        dialogClass=Class.forName(preventOptimziation("ch.ehi.ili2db.gui.MainWizard")); // avoid, that graalvm native-image detects a reference to MainFrame
    } catch (ClassNotFoundException e) {
        // ignore; report later
    } 
    Method mainFrameShowDialog=null;
    if(dialogClass!=null) {
        try {
            mainFrameShowDialog = dialogClass.getMethod("showDialog");
        }catch(NoSuchMethodException ex) {
            // ignore; report later
        }
    }
    if(mainFrameShowDialog!=null) {
        try {
            Object dialog=dialogClass.newInstance();
            Object ret=mainFrameShowDialog.invoke(dialog);
            return (Boolean)ret;                 
        } catch (IllegalArgumentException ex) {
            EhiLogger.logError("failed to open GUI",ex);
        } catch (IllegalAccessException ex) {
            EhiLogger.logError("failed to open GUI",ex);
        } catch (InvocationTargetException ex) {
            EhiLogger.logError("failed to open GUI",ex);
        } catch (InstantiationException ex) {
            EhiLogger.logError("failed to open GUI",ex);
        }
    }else {
        EhiLogger.logError(getAPP_NAME()+": no GUI available");
    }
    return false;
    }
    private static String preventOptimziation(String val) {
        StringBuffer buf=new StringBuffer(val.length());
        buf.append(val);
        return buf.toString();
    }
	
}
