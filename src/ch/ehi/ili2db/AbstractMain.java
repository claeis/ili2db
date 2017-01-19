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
import ch.ehi.sqlgen.generator.SqlConfiguration;

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
		config.setDefaultSrsCode("21781");
		config.setMaxSqlNameLength(Integer.toString(NameMapping.DEFAULT_NAME_LENGTH));
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
		config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
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
			}else if(arg.equals("--schemaimport")){
				config.setFunction(Config.FC_SCHEMAIMPORT);
				argi++;
			}else if(arg.equals("--deleteData")){
				argi++;
				config.setDeleteMode(Config.DELETE_DATA);
			}else if(arg.equals("--trace")){
				EhiLogger.getInstance().setTraceFilter(false); 
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
			}else if(arg.equals("--createSingleEnumTab")){
				argi++;
				config.setCreateEnumDefs(config.CREATE_ENUM_DEFS_SINGLE);
			}else if(arg.equals("--createEnumTabs")){
				argi++;
				config.setCreateEnumDefs(config.CREATE_ENUM_DEFS_MULTI);
			}else if(arg.equals("--createEnumTxtCol")){
				argi++;
				config.setCreateEnumCols(config.CREATE_ENUM_TXT_COL);
			}else if(arg.equals("--createEnumColAsItfCode")){
				argi++;
				config.setCreateEnumColAsItfCode(config.CREATE_ENUMCOL_AS_ITFCODE_YES);
			}else if(arg.equals("--beautifyEnumDispName")){
				argi++;
				config.setBeautifyEnumDispName(config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
			}else if(arg.equals("--noSmartMapping")){
				argi++;
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
			}else if(arg.equals("--smart1Inheritance")){
				argi++;
				config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
			}else if(arg.equals("--smart2Inheritance")){
				argi++;
				config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART2);
			}else if(arg.equals("--coalesceCatalogueRef")){
				argi++;
				config.setCatalogueRefTrafo(config.CATALOGUE_REF_TRAFO_COALESCE);
			}else if(arg.equals("--coalesceMultiSurface")){
				argi++;
				config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
			}else if(arg.equals("--expandMultilingual")){
				argi++;
				config.setMultilingualTrafo(config.MULTILINGUAL_TRAFO_EXPAND);
			}else if(arg.equals("--createFk")){
				argi++;
				config.setCreateFk(config.CREATE_FK_YES);
			}else if(arg.equals("--createFkIdx")){
				argi++;
				config.setCreateFkIdx(config.CREATE_FKIDX_YES);
			}else if(arg.equals("--createUnique")){
				argi++;
				config.setCreateUniqueConstraints(true);
			}else if(arg.equals("--createNumChecks")){
				argi++;
				config.setCreateNumChecks(true);
			}else if(arg.equals("--createStdCols")){
				argi++;
				config.setCreateStdCols(config.CREATE_STD_COLS_ALL);
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
				config.setCreateTypeDiscriminator(config.CREATE_TYPE_DISCRIMINATOR_ALWAYS);
			}else if(arg.equals("--createGeomIdx")){
				argi++;
				config.setValue(SqlConfiguration.CREATE_GEOM_INDEX,"True");
			}else if(arg.equals("--disableNameOptimization")){
				argi++;
				config.setNameOptimization(config.NAME_OPTIMIZATION_DISABLE);
			}else if(arg.equals("--nameByTopic")){
				argi++;
				config.setNameOptimization(config.NAME_OPTIMIZATION_TOPIC);
			}else if(arg.equals("--maxNameLength")){
				config.setMaxSqlNameLength(args[argi]);
				argi++;
			}else if(arg.equals("--structWithGenericRef")){
				argi++;
				config.setStructMapping(config.STRUCT_MAPPING_GENERICREF);
			}else if(arg.equals("--sqlEnableNull")){
				argi++;
				config.setSqlNull(config.SQL_NULL_ENABLE);
			}else if(arg.equals("--strokeArcs")){
				argi++;
				config.setStrokeArcs(config.STROKE_ARCS_ENABLE);
			}else if(arg.equals("--skipPolygonBuilding")){
				argi++;
				config.setDoItfLineTables(true);
				config.setAreaRef(config.AREA_REF_KEEP);
			}else if(arg.equals("--skipPolygonBuildingErrors")){
				argi++;
				config.setIgnorePolygonBuildingErrors(true);
			}else if(arg.equals("--keepAreaRef")){
				argi++;
				config.setAreaRef(config.AREA_REF_KEEP);
			}else if(arg.equals("--importTid")){
				argi++;
				config.setTidHandling(config.TID_HANDLING_PROPERTY);
			}else if(arg.equals("--createBasketCol")){
				argi++;
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			}else if(arg.equals("--ILIGML20")){
				argi++;
				config.setTransferFileFormat(Config.ILIGML20);
			}else if(arg.equals("--version")){
				printVersion();
				return;
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
					printConnectOptions();
					System.err.println("--validConfig file     Config file for validation.");
					System.err.println("--disableValidation    Disable validation of data.");
					System.err.println("--deleteData           on schema/data import, delete existing data from existing tables.");
					System.err.println("--defaultSrsAuth  auth Default SRS authority "+config.getDefaultSrsAuthority());
					System.err.println("--defaultSrsCode  code Default SRS code "+config.getDefaultSrsCode());
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
					System.err.println("--expandMultilingual   enable smart mapping of CHBase:MultilingualText");
					System.err.println("--createGeomIdx        create a spatial index on geometry columns.");
					System.err.println("--createEnumColAsItfCode create enum type column with value according to ITF (instead of XTF).");
					System.err.println("--createEnumTxtCol     create an additional column with the text of the enumeration value.");
					System.err.println("--createEnumTabs       generate tables with enum definitions.");
					System.err.println("--createSingleEnumTab  generate all enum definitions in a single table.");
					System.err.println("--beautifyEnumDispName replace underscore with space in dispName of enum table entries");
					System.err.println("--createStdCols        generate "+DbNames.T_USER_COL+", "+DbNames.T_CREATE_DATE_COL+", "+DbNames.T_LAST_CHANGE_COL+" columns.");
					System.err.println("--t_id_Name name       change name of t_id column ("+DbNames.T_ID_COL+")");
					System.err.println("--idSeqMin minValue    sets the minimum value of the id sequence generator.");
					System.err.println("--idSeqMax maxValue    sets the maximum value of the id sequence generator.");
					System.err.println("--createTypeDiscriminator  generate always a type discriminaor colum.");
					System.err.println("--structWithGenericRef  generate one generic reference to parent in struct tables.");
					System.err.println("--disableNameOptimization disable use of unqualified class name as table name.");
					System.err.println("--nameByTopic          use topic+class name as table name.");
					System.err.println("--maxNameLength length max length of sql names ("+config.getMaxSqlNameLength()+")");
					System.err.println("--sqlEnableNull        create no NOT NULL constraints in db schema.");
					System.err.println("--strokeArcs           stroke ARCS on import.");
					System.err.println("--skipPolygonBuilding  keep linetables; don't build polygons on import.");
					System.err.println("--skipPolygonBuildingErrors  report build polygon errors as info.");
					System.err.println("--keepAreaRef          keep arreaRef as additional column on import.");
					System.err.println("--importTid            read TID into additional column "+DbNames.T_ILI_TID_COL);
					System.err.println("--createBasketCol      generate "+DbNames.T_BASKET_COL+" column.");
					System.err.println("--createFk             generate foreign key constraints.");
					System.err.println("--createFkIdx          create an index on foreign key columns.");
					System.err.println("--createUnique         create UNIQUE db constraints.");
					System.err.println("--ILIGML20             use eCH-0118-2.0 as transferformat");
					printSpecificOptions();
					System.err.println("--log filename         log message to given file.");
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
			config.setDburl(getDbUrlConverter().makeUrl(config));
			try {
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,getAPP_HOME());
			} catch (Exception ex) {
				EhiLogger.logError(ex);
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
		java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle(ch.ehi.basics.i18n.ResourceBundle.class2qpackageName(this.getClass())+".Version");
		//java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle("ch/ehi/ili2db/Version");
			// Major version numbers identify significant functional changes.
			// Minor version numbers identify smaller extensions to the functionality.
			// Micro versions are even finer grained versions.
			StringBuffer ret=new StringBuffer(20);
		ret.append(resVersion.getString("versionMajor"));
			ret.append('.');
		ret.append(resVersion.getString("versionMinor"));
			ret.append('.');
		ret.append(resVersion.getString("versionMicro"));
			ret.append('-');
		ret.append(resVersion.getString("versionDate"));
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
