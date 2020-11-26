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
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.interlis.ili2c.gui.UserSettings;

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
	protected abstract int doArgs(String args[],int argi,Config config);
	public void initConfig(Config config)
	{
		config.setSender(getAPP_NAME()+"-"+getVersion());
		config.setModeldir(Ili2db.ILI_FROM_DB+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+Ili2db.XTF_DIR+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+ch.interlis.ili2c.Main.ILI_REPOSITORY+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+Ili2db.JAR_DIR);
		config.setModels(Ili2db.XTF);
		config.setDefaultSrsAuthority("EPSG");
		config.setMaxSqlNameLength(Integer.toString(NameMapping.DEFAULT_NAME_LENGTH));
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
		config.setMultiPointTrafo(Config.MULTIPOINT_TRAFO_COALESCE);
		config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
        config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
		config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
        config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
		config.setValidation(true);
	}
	protected abstract DbUrlConverter getDbUrlConverter();

	public void domain(String args[]){
		Config config=new Config();
		initConfig(config);
		ch.ehi.basics.settings.Settings settings=new ch.ehi.basics.settings.Settings();
		config.setAppSettings(settings);
		if(args.length==0){
			Ili2db.readAppSettings(settings);
			ch.ehi.ili2db.gui.MainWizard.main(config,getAPP_HOME(),getAPP_NAME(),getDbPanelDescriptor(),getDbUrlConverter());
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
		for(;argi<args.length;){
			while(argi<args.length){
				int oldargi=argi;
				argi=doArgs(args,argi,config);
				if(argi==oldargi){
					break;
				}
			}
			if(argi>=args.length){
				break;
			}
			String arg=args[argi];
			if(arg.equals("--modeldir")){
				argi++;
				config.setModeldir(args[argi]);
				argi++;
			}else if(arg.equals("--models")){
				argi++;
				config.setModels(args[argi]);
				argi++;
			}else if(arg.equals("--exportModels")){
				argi++;
				config.setExportModels(args[argi]);
				argi++;
            }else if(arg.equals("--exportCrsModels")){
                argi++;
                config.setCrsExportModels(args[argi]);
                argi++;
            }else if(arg.equals("--nameLang")){
                argi++;
                config.setNameLanguage(args[argi]);
                argi++;
			}else if(arg.equals("--dataset")){
				argi++;
				config.setDatasetName(args[argi]);
				argi++;
			}else if(arg.equals("--baskets")){
				argi++;
				config.setBaskets(args[argi]);
				argi++;
			}else if(arg.equals("--topics")){
				argi++;
				config.setTopics(args[argi]);
				argi++;
			}else if(arg.equals("--gui")){
				doGui=true;
				argi++;
            }else if(arg.equals("--validate")){
                config.setFunction(Config.FC_VALIDATE);
                argi++;
			}else if(arg.equals("--import")){
				config.setFunction(Config.FC_IMPORT);
				argi++;
			}else if(arg.equals("--update")){
				config.setFunction(Config.FC_UPDATE);
				argi++;
			}else if(arg.equals("--delete")){
				config.setFunction(Config.FC_DELETE);
				argi++;
			}else if(arg.equals("--replace")){
				config.setFunction(Config.FC_REPLACE);
				argi++;
			}else if(arg.equals("--export")){
				config.setFunction(Config.FC_EXPORT);
				argi++;
            }else if(arg.equals("--export3")){
                config.setFunction(Config.FC_EXPORT);
                config.setVer3_export(true);
                argi++;
			}else if(arg.equals("--schemaimport")){
				config.setFunction(Config.FC_SCHEMAIMPORT);
				argi++;
			}else if(arg.equals("--preScript")){
				argi++;	
				config.setPreScript(args[argi]);
				argi++;	
			}else if(arg.equals("--postScript")){
				argi++;	
				config.setPostScript(args[argi]);
				argi++;
			}else if(arg.equals("--deleteData")){
				argi++;
				config.setDeleteMode(Config.DELETE_DATA);
			}else if(arg.equals("--trace")){
				EhiLogger.getInstance().setTraceFilter(false); 
				argi++;
            }else if(arg.equals("--dbparams")){
                argi++;
                config.setDbParams(args[argi]);
                argi++;
			}else if(arg.equals("--dropscript")){
				argi++;
				config.setDropscript(args[argi]);
				argi++;
			}else if(arg.equals("--createscript")){
				argi++;
				config.setCreatescript(args[argi]);
				argi++;
			}else if(arg.equals("--log")){
				argi++;
				config.setLogfile(args[argi]);
				argi++;
            }else if(arg.equals("--xtflog")){
                argi++;
                config.setXtfLogfile(args[argi]);
                argi++;
			}else if(arg.equals("--defaultSrsAuth")){
				argi++;
				String auth=args[argi];
				if(auth.equalsIgnoreCase("NULL")){
					auth=null;
				}
				config.setDefaultSrsAuthority(auth);
				argi++;
			}else if(arg.equals("--defaultSrsCode")){
				argi++;
				config.setDefaultSrsCode(args[argi]);
				argi++;
            }else if(arg.equals("--modelSrsCode")){
                argi++;
                config.setModelSrsCode(args[argi]);
                argi++;
            }else if(arg.equals("--multiSrs")){
                argi++;
                config.setUseEpsgInNames(true);
            }else if(arg.equals("--domains")){
                argi++;
                config.setDomainAssignments(args[argi]);
                argi++;
            }else if(arg.equals("--altSrsModel")){
                argi++;
                config.setSrsModelAssignment(args[argi]);
                argi++;
			}else if(arg.equals("--attachmentsPath")){
				argi++;
				config.setAttachmentsPath(args[argi]);
				argi++;
			}else if(arg.equals("--validConfig")){
				argi++;
				config.setValidConfigFile(args[argi]);
				argi++;
			}else if(arg.equals("--disableValidation")){
				argi++;
				config.setValidation(false);
			}else if(arg.equals("--disableAreaValidation")){
				argi++;
				config.setDisableAreaValidation(true);
            }else if(arg.equals("--disableRounding")){
                argi++;
                config.setDisableRounding(true);
			}else if(arg.equals("--forceTypeValidation")){
				argi++;
				config.setOnlyMultiplicityReduction(true);
			}else if(arg.equals("--createSingleEnumTab")){
				argi++;
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
			}else if(arg.equals("--createEnumTabs")){
				argi++;
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
            }else if(arg.equals("--createEnumTabsWithId")){
                argi++;
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
			}else if(arg.equals("--createEnumTxtCol")){
				argi++;
				config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
			}else if(arg.equals("--createEnumColAsItfCode")){
				argi++;
				config.setValue(Config.CREATE_ENUMCOL_AS_ITFCODE,Config.CREATE_ENUMCOL_AS_ITFCODE_YES);
			}else if(arg.equals("--beautifyEnumDispName")){
				argi++;
				config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
			}else if(arg.equals("--noSmartMapping")){
				argi++;
				Ili2db.setNoSmartMapping(config);
			}else if(arg.equals("--smart1Inheritance")){
				argi++;
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
			}else if(arg.equals("--smart2Inheritance")){
				argi++;
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
			}else if(arg.equals("--coalesceCatalogueRef")){
				argi++;
				config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
			}else if(arg.equals("--coalesceMultiSurface")){
				argi++;
				config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
			}else if(arg.equals("--coalesceMultiLine")){
				argi++;
				config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
			}else if(arg.equals("--coalesceMultiPoint")){
				argi++;
				config.setMultiPointTrafo(Config.MULTIPOINT_TRAFO_COALESCE);
			}else if(arg.equals("--coalesceArray")){
				argi++;
				config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
            }else if(arg.equals("--coalesceJson")){
                argi++;
                config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
			}else if(arg.equals("--expandMultilingual")){
				argi++;
				config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
            }else if(arg.equals("--expandLocalised")){
                argi++;
                config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
			}else if(arg.equals("--createFk")){
				argi++;
				config.setCreateFk(Config.CREATE_FK_YES);
			}else if(arg.equals("--createFkIdx")){
				argi++;
				config.setCreateFkIdx(Config.CREATE_FKIDX_YES);
			}else if(arg.equals("--createUnique")){
				argi++;
				config.setCreateUniqueConstraints(true);
			}else if(arg.equals("--createNumChecks")){
				argi++;
				config.setCreateNumChecks(true);
            }else if(arg.equals("--createImportTabs")){
                argi++;
                config.setCreateImportTabs(true);
			}else if(arg.equals("--createStdCols")){
				argi++;
				config.setCreateStdCols(Config.CREATE_STD_COLS_ALL);
			}else if(arg.equals("--t_id_Name")){
				argi++;
				config.setColT_ID(args[argi]);
				argi++;
			}else if(arg.equals("--idSeqMin")){
				argi++;
				config.setMinIdSeqValue(Long.parseLong(args[argi]));
				argi++;
			}else if(arg.equals("--idSeqMax")){
				argi++;
				config.setMaxIdSeqValue(Long.parseLong(args[argi]));
				argi++;
			}else if(arg.equals("--createTypeDiscriminator")){
				argi++;
				config.setCreateTypeDiscriminator(Config.CREATE_TYPE_DISCRIMINATOR_ALWAYS);
			}else if(arg.equals("--createGeomIdx")){
				argi++;
				config.setValue(Config.CREATE_GEOM_INDEX,Config.TRUE);
			}else if(arg.equals("--disableNameOptimization")){
				argi++;
				config.setNameOptimization(Config.NAME_OPTIMIZATION_DISABLE);
			}else if(arg.equals("--nameByTopic")){
				argi++;
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			}else if(arg.equals("--maxNameLength")){
				argi++;
				config.setMaxSqlNameLength(args[argi]);
				argi++;
			}else if(arg.equals("--structWithGenericRef")){
				argi++;
				config.setStructMapping(Config.STRUCT_MAPPING_GENERICREF);
			}else if(arg.equals("--sqlEnableNull")){
				argi++;
				config.setSqlNull(Config.SQL_NULL_ENABLE);
			}else if(arg.equals("--strokeArcs")){
				argi++;
				Config.setStrokeArcs(config,Config.STROKE_ARCS_ENABLE);
			}else if(arg.equals("--skipPolygonBuilding")){
				argi++;
				Ili2db.setSkipPolygonBuilding(config);
			}else if(arg.equals("--skipPolygonBuildingErrors")){
				// DEPRECATED remove option
				argi++;
				config.setSkipGeometryErrors(true);
			}else if (arg.equals("--skipReferenceErrors")) { 
                argi++;
                config.setSkipReferenceErrors(true);
			}else if(arg.equals("--skipGeometryErrors")){
				argi++;
				config.setSkipGeometryErrors(true);
			}else if(arg.equals("--keepAreaRef")){
				argi++;
				config.setAreaRef(Config.AREA_REF_KEEP);
            }else if(arg.equals("--createTidCol")){
                argi++;
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			}else if(arg.equals("--importTid")){
				argi++;
                config.setImportTid(true);
            }else if(arg.equals("--exportTid")){
                argi++;
                config.setExportTid(true);
            }else if(arg.equals("--importBid")){
                argi++;
                config.setImportBid(true);
			}else if(arg.equals("--createBasketCol")){
				argi++;
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			}else if(arg.equals("--createDatasetCol")){
				argi++;
				config.setCreateDatasetCols(Config.CREATE_DATASET_COL);
			}else if(arg.equals("--ILIGML20")){
				argi++;
				config.setTransferFileFormat(Config.ILIGML20);
            }else if(arg.equals("--ver4-noSchemaImport")){
                argi++;
                config.setDoImplicitSchemaImport(false);
                EhiLogger.logAdaption("--ver4-noSchemaImport is a deprecated option");
            }else if(arg.equals("--doSchemaImport")){
                argi++;
                config.setDoImplicitSchemaImport(true);
			}else if(arg.equals("--ver4-translation")){
				argi++;
				config.setVer3_translation(false);
                EhiLogger.logAdaption("--ver4-translation is a deprecated option");
            }else if(arg.equals("--ver3-translation")){
                argi++;
                config.setVer3_translation(true);
			}else if(arg.equals("--translation")){
				argi++;
				config.setIli1Translation(args[argi]);
				argi++;
			}else if(arg.equals("--proxy")){
				argi++;
				config.setValue(UserSettings.HTTP_PROXY_HOST,args[argi]);
				argi++;
			}else if(arg.equals("--proxyPort")){
				argi++;
				config.setValue(UserSettings.HTTP_PROXY_PORT,args[argi]);
				argi++;
			}else if(arg.equals("--createMetaInfo")){
				argi++;
				config.setCreateMetaInfo(true);
			}else if(arg.equals("--version")){
				printVersion();
				return;
			}else if(arg.equals("--iliMetaAttrs")){
				argi++;	
				config.setIliMetaAttrsFile(args[argi]);
				argi++;
            }else if(arg.equals("--createTypeConstraint")){
                argi++;
                config.setCreateTypeConstraint(true);
			}else if(arg.equals("--help")){
					printVersion ();
					System.err.println();
					printDescription ();
					System.err.println();
					printUsage ();
					System.err.println();
					System.err.println("OPTIONS");
					System.err.println();
					System.err.println("--import               do an import.");
					System.err.println("--update               do an update.");
					System.err.println("--replace              do a replace.");
					System.err.println("--delete               do a delete.");
					System.err.println("--export               do an export.");
					System.err.println("--schemaimport         do an schema import.");
					System.err.println("--preScript file       before running a function, run a script.");
					System.err.println("--postScript file      after running a function, run a script.");
                    System.err.println("--dbparams file        config file with connection parameters.");
					printConnectOptions();
					System.err.println("--validConfig file     Config file for validation.");
					System.err.println("--disableValidation    Disable validation of data.");
					System.err.println("--disableAreaValidation Disable AREA validation.");
					System.err.println("--forceTypeValidation  restrict customization of validation related to \"multiplicity\"");
                    System.err.println("--disableRounding      Disable rounding of import/export data.");
					System.err.println("--deleteData           on schema/data import, delete existing data from existing tables.");
					System.err.println("--defaultSrsAuth  auth Default SRS authority "+config.getDefaultSrsAuthority());
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
					System.err.println("--expandMultilingual   enable smart mapping of CHBase:MultilingualText");
                    System.err.println("--expandLocalised      enable smart mapping of CHBase:LocalisedText");
					System.err.println("--createGeomIdx        create a spatial index on geometry columns.");
					System.err.println("--createEnumColAsItfCode create enum type column with value according to ITF (instead of XTF).");
					System.err.println("--createEnumTxtCol     create an additional column with the text of the enumeration value.");
					System.err.println("--createEnumTabs       generate tables for enum definitions and use xtfcode to reference entries in generated enum tables.");
                    System.err.println("--createEnumTabsWithId generate tables with "+DbNames.T_ID_COL+" for enum definitions and use ids to reference entries in generated enum tables.");
					System.err.println("--createSingleEnumTab  generate all enum definitions in a single table.");
					System.err.println("--beautifyEnumDispName replace underscore with space in dispName of enum table entries");
					System.err.println("--createStdCols        generate "+DbNames.T_USER_COL+", "+DbNames.T_CREATE_DATE_COL+", "+DbNames.T_LAST_CHANGE_COL+" columns.");
					System.err.println("--t_id_Name name       change name of t_id column ("+DbNames.T_ID_COL+")");
					System.err.println("--idSeqMin minValue    sets the minimum value of the id sequence generator.");
					System.err.println("--idSeqMax maxValue    sets the maximum value of the id sequence generator.");
					System.err.println("--createTypeDiscriminator  generate always a type discriminator column.");
					System.err.println("--structWithGenericRef  generate one generic reference to parent in struct tables.");
					System.err.println("--disableNameOptimization disable use of unqualified class name as table name.");
					System.err.println("--nameByTopic          use topic+class name as table name.");
					
                    
                    System.err.println("--nameLang lang        use names of ili model in given language as table/column name.");
					System.err.println("--maxNameLength length max length of sql names ("+config.getMaxSqlNameLength()+")");
					System.err.println("--sqlEnableNull        create no NOT NULL constraints in db schema.");
					System.err.println("--strokeArcs           stroke ARCS on import.");
					System.err.println("--skipPolygonBuilding  keep linetables; don't build polygons on import.");
					System.err.println("--skipReferenceErrors  ignore/do not report reference errors.");
					System.err.println("--skipGeometryErrors   ignore/do not report geometry errors.");
					System.err.println("--keepAreaRef          keep arreaRef as additional column on import.");
                    System.err.println("--createTidCol         create an additional column "+DbNames.T_ILI_TID_COL);
                    System.err.println("--importTid            read transient TIDs into column "+DbNames.T_ILI_TID_COL);
					System.err.println("--exportTid            write transient TIDs from column "+DbNames.T_ILI_TID_COL);
                    System.err.println("--importBid            read transient BIDs into "+DbNames.BASKETS_TAB+"."+DbNames.T_ILI_TID_COL);
                    System.err.println("--createImportTabs     create tables with import statistics. ("+DbNames.IMPORTS_TAB+")");
					System.err.println("--createBasketCol      generate "+DbNames.T_BASKET_COL+" column.");
					System.err.println("--createDatasetCol     generate "+DbNames.T_DATASET_COL+" column (Requires --dataset)");
					System.err.println("--createFk             generate foreign key constraints.");
					System.err.println("--createFkIdx          create an index on foreign key columns.");
					System.err.println("--createUnique         create UNIQUE db constraints.");
					System.err.println("--createNumChecks      create CHECK db constraints for numeric data types.");
					System.err.println("--ILIGML20             use eCH-0118-2.0 as transferformat");
					System.err.println("--exportModels modelname  export data according to the given base ili-models");
                    System.err.println("--exportCrsModels modelname  export data according to the given ili-model (with alternate CRS)");
					System.err.println("--ver4-noSchemaImport  do no implicit schema import during data import");
                    System.err.println("--doSchemaImport       do implicit schema import during data import");
					System.err.println("--ver4-translation     supports TRANSLATION OF in ili2db 4.x mode (incompatible with ili2db 3.x versions).");
                    System.err.println("--ver3-translation     supports TRANSLATION OF in ili2db 3.x mode (incompatible with ili2db 4.x versions).");
					System.err.println("--translation translatedModel=originModel assigns a translated model to its orginal language equivalent.");
					System.err.println("--createMetaInfo       Create aditional ili-model information.");
					System.err.println("--iliMetaAttrs file    Import meta-attributes from a .toml file (Requires --createMetaInfo)");
                    System.err.println("--createTypeConstraints   Create CHECK constraint on t_type columns.");
					printSpecificOptions();
					System.err.println("--proxy host           proxy server to access model repositories.");
					System.err.println("--proxyPort port       proxy port to access model repositories.");
					System.err.println("--log filename         log messages to given file.");
                    System.err.println("--xtflog filename      log messages to given XTF file.");
					System.err.println("--gui                  start GUI.");
					System.err.println("--trace                enable trace messages.");
					System.err.println("--help                 Display this help text.");
					System.err.println("--version              Display the version of "+getAPP_NAME());
					System.err.println();
					return;
				
			}else if(arg.startsWith("-")){
				EhiLogger.logError(arg+": unknown option");
				return;
			}else if(argi+1<args.length){
				EhiLogger.logError(arg+": invalid placed argument");
				return;
			}else{
				break;
			}
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
			ch.ehi.ili2db.gui.MainWizard.main(config,getAPP_HOME(),getAPP_NAME(),getDbPanelDescriptor(),getDbUrlConverter());
			Ili2db.writeAppSettings(settings);
		}else{
		    if(config.getFunction()!=Config.FC_SCRIPT) {
	            config.setDburl(getDbUrlConverter().makeUrl(config));
		    }
			try {
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
}
