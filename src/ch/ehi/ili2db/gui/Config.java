package ch.ehi.ili2db.gui;

import java.util.Properties;

import ch.ehi.basics.settings.Settings;
import ch.ehi.sqlgen.generator.SqlConfiguration;
import ch.interlis.iox_j.inifile.MetaConfig;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.iox_j.validator.Validator;

public class Config extends Settings {
	public static final String FALSE = "False";
	public static final String TRUE = "True";
	/** use only as a special value for cmdline options or config file settings to explicitly unset a setting. Do not use internally.
	 */
    public static final String NULL = MetaConfig.NULL;
	public static final String PREFIX="ch.ehi.ili2db";
	public static final String SENDER=PREFIX+".sender";
    public static final String NAME_LANGUAGE=PREFIX+".nameLanguage";
    public static final String TRANSIENT_MODEL=PREFIX+".model";
	private static final String DEFAULT_SRS_AUTHORITY=PREFIX+".defaultSrsAuthority";
	private static final String DEFAULT_SRS_CODE=PREFIX+".defaultSrsCode";
    private static final String MODEL_SRS_CODE=PREFIX+".modelSrsCode";
	private static final String UUID_DEFAULT_VALUE=PREFIX+".uuidDefaultValue";
	public static final String CREATE_ENUMCOL_AS_ITFCODE=PREFIX+".createEnumColAsItfCode";
	public static final String CREATE_ENUMCOL_AS_ITFCODE_YES="yes";
	private static final String BEAUTIFY_ENUM_DISPNAME=PREFIX+".beautifyEnumDispName";
	public static final String BEAUTIFY_ENUM_DISPNAME_UNDERSCORE="underscore";
	private static final String CREATE_ENUM_DEFS=PREFIX+".createEnumDefs";
	public static final String CREATE_ENUM_DEFS_NO="no";
	public static final String CREATE_ENUM_DEFS_SINGLE="singleTable";
	public static final String CREATE_ENUM_DEFS_MULTI="multiTable";
    public static final String CREATE_ENUM_DEFS_MULTI_WITH_ID="multiTableWithId";
	public static final String CREATE_ENUM_COLS=PREFIX+".createEnumCols";
	public static final String CREATE_ENUM_TXT_COL="addTxtCol";
	public static final String CREATE_DATASET_COLS=PREFIX+".createDatasetCols";
	public static final String CREATE_DATASET_COL="addDatasetCol";
	private static final String CREATE_FK=PREFIX+".createForeignKey";
	public static final String CREATE_FK_YES="yes";
	private static final String CREATE_FKIDX=PREFIX+".createForeignKeyIndex";
	public static final String CREATE_FKIDX_YES="yes";
	public static final String CREATE_GEOM_INDEX=SqlConfiguration.CREATE_GEOM_INDEX;
	private static final String CREATE_STD_COLS=PREFIX+".createStdCols";
	public static final String CREATE_STD_COLS_ALL="all";
	private static final String CREATE_TYPE_DISCRIMINATOR=PREFIX+".typeDiscriminator";
	public static final String CREATE_TYPE_DISCRIMINATOR_ALWAYS="always";
	public static final String INHERITANCE_TRAFO=PREFIX+".inheritanceTrafo";
	public static final String INHERITANCE_TRAFO_SMART1="smart1";
	public static final String INHERITANCE_TRAFO_SMART2="smart2";
	public static final String CATALOGUE_REF_TRAFO=PREFIX+".catalogueRefTrafo";
	public static final String CATALOGUE_REF_TRAFO_COALESCE="coalesce";
	public static final String MULTISURFACE_TRAFO=PREFIX+".multiSurfaceTrafo";
	public static final String MULTISURFACE_TRAFO_COALESCE="coalesce";
	public static final String MULTILINE_TRAFO=PREFIX+".multiLineTrafo";
	public static final String MULTILINE_TRAFO_COALESCE="coalesce";
	public static final String MULTIPOINT_TRAFO=PREFIX+".multiPointTrafo";
	public static final String MULTIPOINT_TRAFO_COALESCE="coalesce";
	public static final String ARRAY_TRAFO=PREFIX+".arrayTrafo";
	public static final String ARRAY_TRAFO_COALESCE="coalesce";
    public static final String JSON_TRAFO=PREFIX+".jsonTrafo";
    public static final String JSON_TRAFO_COALESCE="coalesce";
	public static final String MULTILINGUAL_TRAFO=PREFIX+".multilingualTrafo";
	public static final String MULTILINGUAL_TRAFO_EXPAND="expand";
    public static final String LOCALISED_TRAFO=PREFIX+".localisedTrafo";
    public static final String LOCALISED_TRAFO_EXPAND="expand";
	public static final String UNIQUE_CONSTRAINTS=PREFIX+".uniqueConstraints";
	public static final String UNIQUE_CONSTRAINTS_CREATE="create";
	public static final String NUMERIC_CHECK_CONSTRAINTS=PREFIX+".numericCheckConstraints";
	public static final String NUMERIC_CHECK_CONSTRAINTS_CREATE="create";
    public static final String TEXT_CHECK_CONSTRAINTS=PREFIX+".textCheckConstraints";
    public static final String TEXT_CHECK_CONSTRAINTS_CREATE="create";
    public static final String DATETIME_CHECK_CONSTRAINTS=PREFIX+".datetimeCheckConstraints";
    public static final String DATETIME_CHECK_CONSTRAINTS_CREATE="create";
    public static final String MANDATORY_CHECK_CONSTRAINTS=PREFIX+".mandatoryCheckConstraints";
    public static final String MANDATORY_CHECK_CONSTRAINTS_CREATE="create";
    public static final String IMPORT_TABS=PREFIX+".importTabs";
    public static final String IMPORT_TABS_CREATE="simple";
	public static final String GEOMATTR_PER_TABLE=PREFIX+".geomAttrPerTable";
	public static final String GEOMATTR_PER_TABLE_ONE="oneGeomAttrPerTable";
	private static final String NAME_OPTIMIZATION=PREFIX+".nameOptimization";
	public static final String NAME_OPTIMIZATION_DISABLE="disable";
	public static final String NAME_OPTIMIZATION_TOPIC="topic";
	public static final String MAX_SQLNAME_LENGTH=PREFIX+".maxSqlNameLength";
	private static final String SQL_NULL=PREFIX+".SqlNull";
	public static final String SQL_NULL_ENABLE="enable";
	public static final String SQL_COLS_AS_TEXT=PREFIX+".SqlColsAsText";
	public static final String SQL_COLS_AS_TEXT_ENABLE="enable";
	public static final String SQL_EXTREF=PREFIX+".SqlExtRefCols";
	public static final String SQL_EXTREF_ENABLE="enable";
	public static final String STROKE_ARCS=PREFIX+".StrokeArcs";
	public static final String STROKE_ARCS_ENABLE="enable";
	private static final String AREA_REF=PREFIX+".AreaRef";
	public static final String AREA_REF_KEEP="keep";
	private static final String TID_HANDLING=PREFIX+".TidHandling";
	public static final String TID_HANDLING_PROPERTY="property";
	private static final String APP_SETTINGS=PREFIX+".AppSettings";
	private static final String BASKET_HANDLING=PREFIX+".BasketHandling";
	public static final String BASKET_HANDLING_READWRITE="readWrite";
	private static final String ATTACHMENTS_PATH=PREFIX+".attachmentsPath";
	private static final String ATTACHMENT_KEY=PREFIX+".attachmentKey";
	private static final String DO_ITF_LINE_TABLES=PREFIX+".doItfLineTables";
    private static final String DO_XTF_LINE_TABLES=PREFIX+".doXtfLineTables";
	private static final String COLNAME_T_ID=PREFIX+".colName_T_ID";
    public static final String VER3_TRANSLATION=PREFIX+".ver3_translation";
	public static final String ILI1TRANSLATION=PREFIX+".ili1translation";
	public static final String DELETE_DATA="data";
	public static final String CREATE_META_INFO=PREFIX+".createMetaInfo";
	public static final String USE_EPGS_IN_NAMES=PREFIX+".useEpsgInNames";
	public static final String SRS_MODEL_ASSIGNMENT=PREFIX+".srsModelAssignment";
	public static final String MODELS_TAB_MODELNAME_COLSIZE = PREFIX+".modelsTabModelnameColSize";
    public static final String ATTRNAME_TAB_SQLNAME_COLSIZE = PREFIX+".attrTabSqlnameColSize";
    public static final String ATTRNAME_TAB_OWNER_COLSIZE = PREFIX+".attrTabOwnerColSize";
    public static final String CLASSNAME_TAB_ILINAME_COLSIZE = PREFIX+".classnameTabIlinameColSize";
    public static final String INHERIT_TAB_THIS_COLSIZE = PREFIX+".inheritTabThisColSize";
	public static final String CREATE_TYPE_CONSTRAINT=PREFIX+".createTypeConstraint";
    public static final String METACONFIGFILENAME=PREFIX+".metaConfigFileName";

    public static final String METACONFIG_ILI2DB="ch.ehi.ili2db";
	
    public static final String TRANSIENT_STRING_DBURL=PREFIX+".dburl";
    public static final String TRANSIENT_STRING_DBUSR=PREFIX+".dbusr";
    public static final String TRANSIENT_STRING_DBPWD=PREFIX+".dbpwd";
    public static final String TRANSIENT_STRING_DBPARAMS=PREFIX+".dbparams";
    public static final String TRANSIENT_STRING_DBHOST=PREFIX+".dbhost";
    public static final String TRANSIENT_STRING_DBPORT=PREFIX+".dbport";
    public static final String TRANSIENT_STRING_DBDATABASE=PREFIX+".dbdatabase";
    public static final String TRANSIENT_STRING_DBFILE=PREFIX+".dbfile";
    public static final String TRANSIENT_STRING_DBSCHEMA=PREFIX+".dbschema";
    public static final String TRANSIENT_STRING_MODELDIR=PREFIX+".modeldir";
    public static final String TRANSIENT_STRING_MODELS=PREFIX+".models";
    public static final String TRANSIENT_STRING_NAMELANGUAGE=PREFIX+".nameLanguage";
    public static final String TRANSIENT_STRING_EXPORTMODELS=PREFIX+".exportModels";
    public static final String TRANSIENT_STRING_EXPORTCRSMODELS=PREFIX+".exportCrsModels";
    public static final String TRANSIENT_STRING_DATASETNAME=PREFIX+".datasetName";
    public static final String TRANSIENT_STRING_BASKETS=PREFIX+".baskets";
    public static final String TRANSIENT_STRING_TOPICS=PREFIX+".topics";
    public static final String TRANSIENT_STRING_CREATSCRIPT=PREFIX+".createscript";
    public static final String TRANSIENT_STRING_DROPSCRIPT=PREFIX+".dropscript";
    public static final String TRANSIENT_STRING_XTFFILE=PREFIX+".xtffile";
    public static final String TRANSIENT_STRING_PRESCRIPT=PREFIX+".preScript";
    public static final String TRANSIENT_STRING_POSTSCRIPT=PREFIX+".postScript";
    public static final String TRANSIENT_STRING_IDGENERATOR=PREFIX+".idGenerator";
    public static final String TRANSIENT_STRING_GEOMETRYCONVERTER=PREFIX+".geometryConverter";
    public static final String TRANSIENT_STRING_ILI2DBCUSTOMSTRATEGY=PREFIX+".ili2dbCustomStrategy";
    public static final String TRANSIENT_STRING_INITSTRATEGY=PREFIX+".initStrategy";
    public static final String TRANSIENT_STRING_JDBCDRIVER=PREFIX+".jdbcDriver";
    public static final String TRANSIENT_STRING_DDLGENERATOR=PREFIX+".ddlGenerator";
    public static final String TRANSIENT_STRING_DELETEMODE=PREFIX+".deleteMode";
    public static final String TRANSIENT_STRING_LOGFILE=PREFIX+".logfile";
    public static final String TRANSIENT_STRING_XTFLOGFILE=PREFIX+".xtfLogfile";
    public static final String TRANSIENT_STRING_VALIDCONFIGFILENAME=PREFIX+".validConfigFileName";
    public static final String TRANSIENT_STRING_REFERENCEDATA=PREFIX+".referenceData";
    public static final String TRANSIENT_STRING_ILIMETAATTRSFILE=PREFIX+".iliMetaAttrsFile";
    public static final String TRANSIENT_STRING_DOMAINASSIGNMENTS=PREFIX+".domainAssignments";
    public static final String TRANSIENT_STRING_TRANSFERFILEFORMAT=PREFIX+".transferFileFormat";
    public static final String TRANSIENT_BOOLEAN_CONFIGREADFROMDB=PREFIX+".configReadFromDb";
    public static final String TRANSIENT_BOOLEAN_ITFTRANSFERFILE=PREFIX+".itfTransferFile";
    public static final String TRANSIENT_BOOLEAN_VALIDATION=PREFIX+".validation";
    public static final String TRANSIENT_BOOLEAN_SKIPREFERENCEERRRORS=PREFIX+".skipReferenceErrors";
    public static final String TRANSIENT_BOOLEAN_EXPORTTID=PREFIX+".exportTid";
    public static final String TRANSIENT_BOOLEAN_VER3_EXPORT=PREFIX+".ver3_export";
    public static final String TRANSIENT_BOOLEAN_IMPORTTID=PREFIX+".importTid";
    public static final String TRANSIENT_BOOLEAN_IMPORTBID=PREFIX+".importBid";
    public static final String TRANSIENT_BOOLEAN_DISABLEROUNDING=PREFIX+".disableRounding";
    public static final String TRANSIENT_BOOLEAN_SETUPPGEXT=PREFIX+".setupPgExt";
    public static final String TRANSIENT_BOOLEAN_REPAIRTOUCHINGLINES=PREFIX+".repairTouchingLines";
    public static final String TRANSIENT_BOOLEAN_LOGTIME=PREFIX+".logTime";
    public static final String TRANSIENT_BOOLEAN_DOIMPLICITSCHEMAIMPORT=PREFIX+".doImplicitSchemaImport";
    public static final String TRANSIENT_BOOLEAN_DISABLEAREAVALIDATION=PREFIX+".disableAreaValidation";
    public static final String TRANSIENT_BOOLEAN_ONLYMULTIPLICITYREDUCTION=PREFIX+".onlyMultiplicityReduction";
    public static final String TRANSIENT_BOOLEAN_SKIPGEOMETRYERRORS=PREFIX+".skipGeometryErrors";
    public static final String TRANSIENT_INTEGER_FETCHSIZE=PREFIX+".fetchSize";
    public static final String TRANSIENT_INTEGER_BATCHSIZE=PREFIX+".batchSize";
    public static final String TRANSIENT_INTEGER_FUNCTION=PREFIX+".function";
    public static final String TRANSIENT_LONG_MINIDSEQVALUE=PREFIX+".minIdSeqValue";
    public static final String TRANSIENT_LONG_MAXIDSEQVALUE=PREFIX+".maxIdSeqValue";
    public static final String TRANSIENT_PROPERTIES_DBPROPS=PREFIX+".dbprops";
    public static final String TRANSIENT_STRING_PLUGINFOLDER=PREFIX+".pluginfolder";
    
	final static public String ILIGML20="ILIGML20"; 
	
    static public final int FC_UNDEFINED=0;
	static public final int FC_IMPORT=1;
	static public final int FC_SCHEMAIMPORT=2;
	static public final int FC_EXPORT=3;
	static public final int FC_UPDATE=4;
	static public final int FC_DELETE=5;
	static public final int FC_REPLACE=6;
    static public final int FC_SCRIPT=7;
    static public final int FC_VALIDATE=8;
	public String getIdGenerator() {
		return getTransientValue(TRANSIENT_STRING_IDGENERATOR);
	}
	public void setIdGenerator(String idGeneratorClassName) {
	    setTransientValue(TRANSIENT_STRING_IDGENERATOR,idGeneratorClassName);
	}
	public String getGeometryConverter() {
		return getTransientValue(TRANSIENT_STRING_GEOMETRYCONVERTER);
	}
	public void setGeometryConverter(String adapterClassName) {
	    setTransientValue(TRANSIENT_STRING_GEOMETRYCONVERTER,adapterClassName);
	}
	public String getIli2dbCustomStrategy() {
		return getTransientValue(TRANSIENT_STRING_ILI2DBCUSTOMSTRATEGY);
	}
	public void setIli2dbCustomStrategy(String value) {
        setTransientValue(TRANSIENT_STRING_ILI2DBCUSTOMSTRATEGY,value);
	}
	public String getCreatescript() {
		return getTransientValue(TRANSIENT_STRING_CREATSCRIPT);
	}
	public void setCreatescript(String createscript) {
	    setTransientValue(TRANSIENT_STRING_CREATSCRIPT,createscript);
	}
	public String getDbpwd() {
		return getTransientValue(TRANSIENT_STRING_DBPWD);
	}
	public void setDbpwd(String dbpwd) {
	    setTransientValue(TRANSIENT_STRING_DBPWD,dbpwd);
	}
	public String getDburl() {
		return getTransientValue(TRANSIENT_STRING_DBURL);
	}
	public void setDburl(String dburl) {
	    setTransientValue(TRANSIENT_STRING_DBURL,dburl);
	}
	public String getDbusr() {
		return getTransientValue(TRANSIENT_STRING_DBUSR);
	}
	public void setDbusr(String dbusr) {
	    setTransientValue(TRANSIENT_STRING_DBUSR,dbusr);
	}
    public Properties getDbProperties() {
        return (Properties)getTransientObject(TRANSIENT_PROPERTIES_DBPROPS);
    }
    public void setDbProperties(Properties props) {
        setTransientObject(TRANSIENT_PROPERTIES_DBPROPS,props);
    }
	public String getDropscript() {
		return getTransientValue(TRANSIENT_STRING_DROPSCRIPT);
	}
	public void setDropscript(String dropscript) {
	    setTransientValue(TRANSIENT_STRING_DROPSCRIPT,dropscript);
	}
	public String getModeldir() {
		return getTransientValue(TRANSIENT_STRING_MODELDIR);
	}
	public void setModeldir(String modeldir) {
	    setTransientValue(TRANSIENT_STRING_MODELDIR,modeldir);
	}
	public String getModels() {
		return getTransientValue(TRANSIENT_STRING_MODELS);
	}
	public void setModels(String models) {
	    setTransientValue(TRANSIENT_STRING_MODELS,models);
	}
	public String getExportModels() {
		return getTransientValue(TRANSIENT_STRING_EXPORTMODELS);
	}
	public void setExportModels(String models) {
	    setTransientValue(TRANSIENT_STRING_EXPORTMODELS,models);
	}
    public String getCrsExportModels() {
        return getTransientValue(TRANSIENT_STRING_EXPORTCRSMODELS);
    }
    public void setCrsExportModels(String models) {
        setTransientValue(TRANSIENT_STRING_EXPORTCRSMODELS,models);
    }
	public String getDatasetName() {
		return getTransientValue(TRANSIENT_STRING_DATASETNAME);
	}
	public void setDatasetName(String datasetName) {
	    setTransientValue(TRANSIENT_STRING_DATASETNAME,datasetName);
	}
	public String getBaskets() {
		return getTransientValue(TRANSIENT_STRING_BASKETS);
	}
	public void setBaskets(String baskets) {
	    setTransientValue(TRANSIENT_STRING_BASKETS, baskets);
	}
	public String getTopics() {
		return getTransientValue(TRANSIENT_STRING_TOPICS);
	}
	public void setTopics(String topics) {
	    setTransientValue(TRANSIENT_STRING_TOPICS,topics);
	}
	public String getXtffile() {
        String value=getTransientValue(TRANSIENT_STRING_XTFFILE);
        if(MetaConfig.NULL.equals(value))return null;
        return value;
	}
	public void setXtffile(String xtffile) {
	    setTransientValue(TRANSIENT_STRING_XTFFILE,xtffile);
	}
	public int getFunction() {
	    Integer function=(Integer)getTransientObject(TRANSIENT_INTEGER_FUNCTION);
	    if(function==null) return FC_UNDEFINED;
		return function;
	}
	public void setFunction(int function) {
	    setTransientObject(TRANSIENT_INTEGER_FUNCTION,new Integer(function));
	}
    public String getDbParams() {
        return getTransientValue(TRANSIENT_STRING_DBPARAMS);
    }
    public void setDbParams(String propFile) {
        setTransientValue(TRANSIENT_STRING_DBPARAMS, propFile);
    }
	public String getDbfile() {
		return getTransientValue(TRANSIENT_STRING_DBFILE);
	}
    public void setDbfile(String dbfile) {
        setTransientValue(TRANSIENT_STRING_DBFILE,dbfile);
	}
	public String getDbhost() {
		return getTransientValue(TRANSIENT_STRING_DBHOST);
	}
	public void setDbhost(String dbhost) {
	    setTransientValue(TRANSIENT_STRING_DBHOST,dbhost);
	}
	public String getDbport() {
		return getTransientValue(TRANSIENT_STRING_DBPORT);
	}
	public void setDbport(String dbport) {
	    setTransientValue(TRANSIENT_STRING_DBPORT,dbport);
	}
	public String getDbdatabase() {
		return getTransientValue(TRANSIENT_STRING_DBDATABASE);
	}
	public void setDbdatabase(String dbdatabase) {
	    setTransientValue(TRANSIENT_STRING_DBDATABASE,dbdatabase);
	}
	public String getDdlGenerator() {
		return getTransientValue(TRANSIENT_STRING_DDLGENERATOR);
	}
	public void setDdlGenerator(String ddlGenerator) {
	    setTransientValue(TRANSIENT_STRING_DDLGENERATOR, ddlGenerator);
	}
	public String getJdbcDriver() {
		return getTransientValue(TRANSIENT_STRING_JDBCDRIVER);
	}
	public void setJdbcDriver(String jdbcDriver) {
	    setTransientValue(TRANSIENT_STRING_JDBCDRIVER, jdbcDriver);
	}
	public String getSender() {
		return getValue(SENDER);
	}
	public void setSender(String sender) {
		setValue(SENDER,sender);
	}
    public String getNameLanguage() {
        return getValue(NAME_LANGUAGE);
    }
    public void setNameLanguage(String models) {
        setValue(NAME_LANGUAGE,models);
    }
	public String getDefaultSrsAuthority() {
		return getValue(DEFAULT_SRS_AUTHORITY);
	}
	public void setDefaultSrsAuthority(String value) {
		setValue(DEFAULT_SRS_AUTHORITY,value);
	}
	public String getDefaultSrsCode() {
		return getValue(DEFAULT_SRS_CODE);
	}
	public void setDefaultSrsCode(String value) {
		setValue(DEFAULT_SRS_CODE,value);
	}
    public String getModelSrsCode() {
        return getValue(MODEL_SRS_CODE);
    }
    public void setModelSrsCode(String value) {
        setValue(MODEL_SRS_CODE,value);
    }
	public String getUuidDefaultValue() {
		return getValue(UUID_DEFAULT_VALUE);
	}
	public void setUuidDefaultValue(String value) {
		setValue(UUID_DEFAULT_VALUE,value);
	}
	public String getCreateEnumDefs() {
		return getValue(CREATE_ENUM_DEFS);
	}
	public void setCreateEnumDefs(String value) {
		setValue(CREATE_ENUM_DEFS,value);
	}
	public String getBeautifyEnumDispName() {
		return getValue(BEAUTIFY_ENUM_DISPNAME);
	}
	public void setBeautifyEnumDispName(String value) {
		setValue(BEAUTIFY_ENUM_DISPNAME,value);
	}
	@Deprecated
	public String getCreateEnumColAsItfCode() {
		return getValue(CREATE_ENUMCOL_AS_ITFCODE);
	}
	@Deprecated
	public void setCreateEnumColAsItfCode(String value) {
		setValue(CREATE_ENUMCOL_AS_ITFCODE,value);
	}
	public String getCreateEnumCols() {
		return getValue(CREATE_ENUM_COLS);
	}
	public void setCreateEnumCols(String value) {
		setValue(CREATE_ENUM_COLS,value);
	}
	public String getCreateDatasetCols() {
		return getValue(CREATE_DATASET_COLS);
	}
	public void setCreateDatasetCols(String value) {
		setValue(CREATE_DATASET_COLS,value);
	}
	public String getCreateFk() {
		return getValue(CREATE_FK);
	}
	public void setCreateFk(String value) {
		setValue(CREATE_FK,value);
	}
	public String getCreateFkIdx() {
		return getValue(CREATE_FKIDX);
	}
	public void setCreateFkIdx(String value) {
		setValue(CREATE_FKIDX,value);
	}
	public String getCreateStdCols() {
		return getValue(CREATE_STD_COLS);
	}
	public void setCreateStdCols(String value) {
		setValue(CREATE_STD_COLS,value);
	}
	public String getMaxSqlNameLength() {
		return getValue(MAX_SQLNAME_LENGTH);
	}
	public void setMaxSqlNameLength(String value) {
		setValue(MAX_SQLNAME_LENGTH,value);
	}
	public String getColT_ID() {
		return getValue(COLNAME_T_ID);
	}
	public void setColT_ID(String value) {
		setValue(COLNAME_T_ID,value);
	}
	public String getCreateTypeDiscriminator() {
		return getValue(CREATE_TYPE_DISCRIMINATOR);
	}
	public void setCreateTypeDiscriminator(String value) {
		setValue(CREATE_TYPE_DISCRIMINATOR,value);
	}
	public String getInheritanceTrafo() {
		return getValue(INHERITANCE_TRAFO);
	}
	public void setInheritanceTrafo(String value) {
		setValue(INHERITANCE_TRAFO,value);
	}
	public String getCatalogueRefTrafo() {
		return getValue(CATALOGUE_REF_TRAFO);
	}
	public void setCatalogueRefTrafo(String value) {
		setValue(CATALOGUE_REF_TRAFO,value);
	}
	public String getMultiSurfaceTrafo() {
		return getValue(MULTISURFACE_TRAFO);
	}
	public void setMultiSurfaceTrafo(String value) {
		setValue(MULTISURFACE_TRAFO,value);
	}
	public String getMultiLineTrafo() {
		return getValue(MULTILINE_TRAFO);
	}
	public void setMultiLineTrafo(String value) {
		setValue(MULTILINE_TRAFO,value);
	}
	public String getMultiPointTrafo() {
		return getValue(MULTIPOINT_TRAFO);
	}
	public void setMultiPointTrafo(String value) {
		setValue(MULTIPOINT_TRAFO,value);
	}
	public String getArrayTrafo() {
		return getValue(ARRAY_TRAFO);
	}
	public void setArrayTrafo(String value) {
		setValue(ARRAY_TRAFO,value);
	}
    public String getJsonTrafo() {
        return getValue(JSON_TRAFO);
    }
    public void setJsonTrafo(String value) {
        setValue(JSON_TRAFO,value);
    }
	public String getMultilingualTrafo() {
		return getValue(MULTILINGUAL_TRAFO);
	}
	public void setMultilingualTrafo(String value) {
		setValue(MULTILINGUAL_TRAFO,value);
	}
    public String getLocalisedTrafo() {
        return getValue(LOCALISED_TRAFO);
    }
    public void setLocalisedTrafo(String value) {
        setValue(LOCALISED_TRAFO,value);
    }
	public String getNameOptimization() {
		return getValue(NAME_OPTIMIZATION);
	}
	public void setNameOptimization(String value) {
		setValue(NAME_OPTIMIZATION,value);
	}
	public String getSqlNull() {
		return getValue(SQL_NULL);
	}
	public void setSqlNull(String value) {
		setValue(SQL_NULL,value);
	}
	@Deprecated
	public String getStrokeArcs() {
		return getValue(STROKE_ARCS);
	}
	public static String getStrokeArcs(Settings config) {
		return config.getValue(STROKE_ARCS);
	}
	@Deprecated
	public void setStrokeArcs(String value) {
		setValue(STROKE_ARCS,value);
	}
	public static void setStrokeArcs(Settings config,String value) {
		config.setValue(STROKE_ARCS,value);
	}
	public boolean getDoItfLineTables() {
		return TRUE.equals(getValue(DO_ITF_LINE_TABLES));
	}
	public void setDoItfLineTables(boolean value) {
		setValue(DO_ITF_LINE_TABLES,value?TRUE:FALSE);
	}
    public boolean getDoXtfLineTables() {
        return TRUE.equals(getValue(DO_XTF_LINE_TABLES));
    }
    public void setDoXtfLineTables(boolean value) {
        setValue(DO_XTF_LINE_TABLES,value?TRUE:FALSE);
    }
	public void setCreateUniqueConstraints(boolean ignore) {
		setValue(UNIQUE_CONSTRAINTS,ignore?UNIQUE_CONSTRAINTS_CREATE:null);
	}
	public boolean isCreateUniqueConstraints() {
		return UNIQUE_CONSTRAINTS_CREATE.equals(getValue(UNIQUE_CONSTRAINTS));
	}
	public void setCreateNumChecks(boolean ignore) {
		setValue(NUMERIC_CHECK_CONSTRAINTS,ignore?NUMERIC_CHECK_CONSTRAINTS_CREATE:null);
	}
	public boolean isCreateCreateNumChecks() {
		return NUMERIC_CHECK_CONSTRAINTS_CREATE.equals(getValue(NUMERIC_CHECK_CONSTRAINTS));
	}
    public void setCreateTextChecks(boolean ignore) {
        setValue(TEXT_CHECK_CONSTRAINTS,ignore?TEXT_CHECK_CONSTRAINTS_CREATE:null);
    }
    public boolean isCreateCreateTextChecks() {
        return TEXT_CHECK_CONSTRAINTS_CREATE.equals(getValue(TEXT_CHECK_CONSTRAINTS));
    }
    public void setCreateDateTimeChecks(boolean ignore) {
        setValue(DATETIME_CHECK_CONSTRAINTS,ignore?DATETIME_CHECK_CONSTRAINTS_CREATE:null);
    }
    public boolean isCreateCreateDateTimeChecks() {
        return DATETIME_CHECK_CONSTRAINTS_CREATE.equals(getValue(DATETIME_CHECK_CONSTRAINTS));
    }
    public void setCreateMandatoryChecks(boolean ignore) {
        setValue(MANDATORY_CHECK_CONSTRAINTS,ignore?MANDATORY_CHECK_CONSTRAINTS_CREATE:null);
    }
    public boolean isCreateMandatoryChecks() {
        return MANDATORY_CHECK_CONSTRAINTS_CREATE.equals(getValue(MANDATORY_CHECK_CONSTRAINTS));
    }
    public void setCreateImportTabs(boolean ignore) {
        setValue(IMPORT_TABS,ignore?IMPORT_TABS_CREATE:null);
    }
    public boolean isCreateImportTabs() {
        return IMPORT_TABS_CREATE.equals(getValue(IMPORT_TABS));
    }

	public void setOneGeomPerTable(boolean onlyOne) {
		setValue(GEOMATTR_PER_TABLE,onlyOne?GEOMATTR_PER_TABLE_ONE:null);
	}
	public boolean isOneGeomPerTable() {
		return GEOMATTR_PER_TABLE_ONE.equals(getValue(GEOMATTR_PER_TABLE));
	}
	public String getAreaRef() {
		return getValue(AREA_REF);
	}
	public void setAreaRef(String value) {
		setValue(AREA_REF,value);
	}
	public Settings getAppSettings() {
		return (Settings)getTransientObject(APP_SETTINGS);
	}
	public void setAppSettings(Settings value) {
		setTransientObject(APP_SETTINGS,value);
	}
	public String getTidHandling() {
		return getValue(TID_HANDLING);
	}
	public void setTidHandling(String value) {
		setValue(TID_HANDLING,value);
	}
    public void setImportTid(boolean enable) {
        setTransientObject(TRANSIENT_BOOLEAN_IMPORTTID,new Boolean(enable));
    }
    public boolean isImportTid() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_IMPORTTID);
        if(value==null)return false;
        return value;
    }
    public void setExportTid(boolean enable) {
        setTransientObject(TRANSIENT_BOOLEAN_EXPORTTID,new Boolean(enable));
    }
    public boolean isExportTid() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_EXPORTTID);
        if(value==null)return false;
        return value;
    }
	
	public String getBasketHandling() {
		return getValue(BASKET_HANDLING);
	}
	public void setBasketHandling(String value) {
		setValue(BASKET_HANDLING,value);
	}
	public String getAttachmentsPath() {
		return getValue(ATTACHMENTS_PATH);
	}
	public void setAttachmentsPath(String value) {
		setValue(ATTACHMENTS_PATH,value);
	}
	public String getAttachmentKey() {
		return getTransientValue(ATTACHMENT_KEY);
	}
	public void setAttachmentKey(String value) {
		setTransientValue(ATTACHMENT_KEY,value);
	}
	public String getInitStrategy() {
		return getTransientValue(TRANSIENT_STRING_INITSTRATEGY);
	}
	public void setInitStrategy(String value) {
	    setTransientValue(TRANSIENT_STRING_INITSTRATEGY,value);
	}
	public java.sql.Connection getJdbcConnection() {
		return (java.sql.Connection)getTransientObject(ch.ehi.sqlgen.generator.SqlConfiguration.JDBC_CONNECTION);
	}
	public void setJdbcConnection(java.sql.Connection value) {
		setTransientObject(ch.ehi.sqlgen.generator.SqlConfiguration.JDBC_CONNECTION,value);
	}
	public String getDeleteMode() {
		return getTransientValue(TRANSIENT_STRING_DELETEMODE);
	}
	public void setDeleteMode(String deleteMode) {
	    setTransientValue(TRANSIENT_STRING_DELETEMODE,deleteMode);
	}
	public String getLogfile() {
		String value=getTransientValue(TRANSIENT_STRING_LOGFILE);
        if(MetaConfig.NULL.equals(value))return null;
		return value;
	}
	public void setLogfile(String logfile) {
	    setTransientValue(TRANSIENT_STRING_LOGFILE,logfile);
	}
    public String getXtfLogfile() {
        String value=getTransientValue(TRANSIENT_STRING_XTFLOGFILE);
        if(MetaConfig.NULL.equals(value))return null;
        return value;
    }
    public void setXtfLogfile(String logfile) {
        setTransientValue(TRANSIENT_STRING_XTFLOGFILE,logfile);
    }
	public boolean isConfigReadFromDb() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_CONFIGREADFROMDB);
        if(value==null)return false;
        return value;
	}
	public void setConfigReadFromDb(boolean configReadFromDb) {
        setTransientObject(TRANSIENT_BOOLEAN_CONFIGREADFROMDB,new Boolean(configReadFromDb));
	}
	public String getDbschema() {
		return getTransientValue(TRANSIENT_STRING_DBSCHEMA);
	}
	public void setDbschema(String dbschema) {
	    setTransientValue(TRANSIENT_STRING_DBSCHEMA,dbschema);
	}
	public boolean isItfTransferfile() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_ITFTRANSFERFILE);
        if(value==null)return false;
        return value;
	}
	public void setItfTransferfile(boolean value) {
        setTransientObject(TRANSIENT_BOOLEAN_ITFTRANSFERFILE,new Boolean(value));
	}
    public String getMetaConfigFile() {
        return getValue(METACONFIGFILENAME);
    }
    public void setMetaConfigFile(String fileName) {
        setValue(METACONFIGFILENAME,fileName);
    }
	public String getValidConfigFile() {
		return getTransientValue(TRANSIENT_STRING_VALIDCONFIGFILENAME);
	}
	public void setValidConfigFile(String fileName) {
	    setTransientValue(TRANSIENT_STRING_VALIDCONFIGFILENAME ,fileName);
	}
    public String getReferenceData() {
        return getTransientValue(TRANSIENT_STRING_REFERENCEDATA);
    }
    public void setReferenceData(String fileName) {
        setTransientValue(TRANSIENT_STRING_REFERENCEDATA ,fileName);
    }
	public boolean isValidation() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_VALIDATION);
        if(value==null)return false;
        return value;
	}
	public void setValidation(boolean enable) {
        setTransientObject(TRANSIENT_BOOLEAN_VALIDATION,new Boolean(enable));
	}
	public Long getMinIdSeqValue() {
        return (Long)getTransientObject(TRANSIENT_LONG_MINIDSEQVALUE);
	}
	public void setMinIdSeqValue(Long minIdSeqValue) {
        setTransientObject(TRANSIENT_LONG_MINIDSEQVALUE,new Long(minIdSeqValue));
	}
	public Long getMaxIdSeqValue() {
		return (Long)getTransientObject(TRANSIENT_LONG_MAXIDSEQVALUE);
	}
	public void setMaxIdSeqValue(Long maxIdSeqValue) {
        setTransientObject(TRANSIENT_LONG_MAXIDSEQVALUE,new Long(maxIdSeqValue));
	}
	public String getTransferFileFormat() {
		return getTransientValue(TRANSIENT_STRING_TRANSFERFILEFORMAT);
	}
	public void setTransferFileFormat(String transferFileFormat) {
	    setTransientValue(TRANSIENT_STRING_TRANSFERFILEFORMAT, transferFileFormat);
	}
	public boolean isOnlyMultiplicityReduction() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_ONLYMULTIPLICITYREDUCTION);
        if(value==null)return false;
        return value;
	}
	public void setOnlyMultiplicityReduction(boolean onlyMultiplicityReduction) {
        setTransientObject(TRANSIENT_BOOLEAN_ONLYMULTIPLICITYREDUCTION,new Boolean(onlyMultiplicityReduction));
	}
    public boolean isSkipReferenceErrors() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_SKIPREFERENCEERRRORS);
        if(value==null)return false;
        return value;
    }
    public void setSkipReferenceErrors(boolean skipReferenceErrors) {
        setTransientObject(TRANSIENT_BOOLEAN_SKIPREFERENCEERRRORS,new Boolean(skipReferenceErrors));
    }
	public boolean isSkipGeometryErrors() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_SKIPGEOMETRYERRORS);
        if(value==null)return false;
        return value;
	}
	public void setSkipGeometryErrors(boolean skipGeometryTypeValidation) {
        setTransientObject(TRANSIENT_BOOLEAN_SKIPGEOMETRYERRORS,new Boolean(skipGeometryTypeValidation));
	}
	public boolean isDisableAreaValidation() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_DISABLEAREAVALIDATION);
        if(value==null)return false;
        return value;
	}
	public void setDisableAreaValidation(boolean disableAreaValidation) {
        setTransientObject(TRANSIENT_BOOLEAN_DISABLEAREAVALIDATION,new Boolean(disableAreaValidation));
	}
	public void setVer3_translation(boolean b) {
		setValue(VER3_TRANSLATION,b?TRUE:FALSE);
	}
	public boolean isVer3_translation() {
		return TRUE.equals(getValue(VER3_TRANSLATION))?true:false;
	}
	public void setIli1Translation(String modelMapping) {
		setValue(ILI1TRANSLATION,modelMapping);
	}
	public String getIli1Translation() {
		return getValue(ILI1TRANSLATION);
	}
	public String getPreScript() {
		return getTransientValue(TRANSIENT_STRING_PRESCRIPT);
	}
	public void setPreScript(String preScript) {
	    setTransientValue(TRANSIENT_STRING_PRESCRIPT,preScript);
	}
	public String getPostScript() {
		return getTransientValue(TRANSIENT_STRING_POSTSCRIPT);
	}
	public void setPostScript(String postScript) {
	    setTransientValue(TRANSIENT_STRING_POSTSCRIPT,postScript);
	}
	public boolean isSetupPgExt() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_SETUPPGEXT);
        if(value==null)return false;
        return value;
	}
	public void setSetupPgExt(boolean setupPgExt) {
        setTransientObject(TRANSIENT_BOOLEAN_SETUPPGEXT,new Boolean(setupPgExt));
	}
	public void setCreateMetaInfo(boolean value) {
		setValue(CREATE_META_INFO,value?TRUE:FALSE);
	}
	public boolean getCreateMetaInfo() {
		return TRUE.equals(getValue(CREATE_META_INFO))?true:false;
	}
	public String getIliMetaAttrsFile() {
		return getTransientValue(TRANSIENT_STRING_ILIMETAATTRSFILE);
	}
	public void setIliMetaAttrsFile(String iliMetaAttrsFile) {
	    setTransientValue(TRANSIENT_STRING_ILIMETAATTRSFILE,iliMetaAttrsFile);
	}
    public boolean isDoImplicitSchemaImport() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_DOIMPLICITSCHEMAIMPORT);
        if(value==null)return false;
        return value;
    }
    public void setDoImplicitSchemaImport(boolean doImplicitSchemaImport) {
        setTransientObject(TRANSIENT_BOOLEAN_DOIMPLICITSCHEMAIMPORT,new Boolean(doImplicitSchemaImport));
    }
    public boolean isImportBid() {
        Boolean value=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_IMPORTBID);
        if(value==null)return false;
        return value;
    }
    public void setImportBid(boolean enable) {
        setTransientObject(TRANSIENT_BOOLEAN_IMPORTBID,new Boolean(enable));
    }
    public void setFetchSize(Integer fetchSize) {
        setTransientObject(TRANSIENT_INTEGER_FETCHSIZE,new Integer(fetchSize));
    }
    public Integer getFetchSize() {
        return (Integer)getTransientObject(TRANSIENT_INTEGER_FETCHSIZE);
    }
    public void setBatchSize(Integer batchSize) {
        setTransientObject(TRANSIENT_INTEGER_BATCHSIZE,new Integer(batchSize));
    }
    public Integer getBatchSize() {
        return (Integer)getTransientObject(TRANSIENT_INTEGER_BATCHSIZE);
    }
    public void setUseEpsgInNames(boolean value) {
        setValue(USE_EPGS_IN_NAMES,value?TRUE:FALSE);
    }
    public boolean useEpsgInNames() {
        return TRUE.equals(getValue(USE_EPGS_IN_NAMES))?true:false;
    }
    public String getDomainAssignments() {
        return getTransientValue(TRANSIENT_STRING_DOMAINASSIGNMENTS);
    }
    public void setDomainAssignments(String value) {
        setTransientValue(TRANSIENT_STRING_DOMAINASSIGNMENTS,value);
    }
    public void setSrsModelAssignment(String value) {
        setValue(SRS_MODEL_ASSIGNMENT,value);
    }
    public String getSrsModelAssignment() {
        return getValue(SRS_MODEL_ASSIGNMENT);
    }
    public void setCreateTypeConstraint(boolean value) {
		setValue(CREATE_TYPE_CONSTRAINT,value?TRUE:FALSE);
	}
	public boolean getCreateTypeConstraint() {
		return TRUE.equals(getValue(CREATE_TYPE_CONSTRAINT))?true:false;
	}
    public boolean isVer3_export() {
        Boolean ver3_export=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_VER3_EXPORT);
        if(ver3_export==null)return false;
        return ver3_export;
    }
    public void setVer3_export(boolean b) {
        setTransientObject(TRANSIENT_BOOLEAN_VER3_EXPORT,new Boolean(b));
    }
    public boolean isDisableRounding() {
        Boolean disableRounding=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_DISABLEROUNDING);
        if(disableRounding==null)return false;
        return disableRounding;
    }
    public void setDisableRounding(boolean disableRounding) {
        setTransientObject(TRANSIENT_BOOLEAN_DISABLEROUNDING,new Boolean(disableRounding));
    }

	public String getSqlColsAsText() {
		return getValue(SQL_COLS_AS_TEXT);
	}

	public void setSqlColsAsText(String value) {
		setValue(SQL_COLS_AS_TEXT,value);
	}
    public String getSqlExtRefCols() {
        return getValue(SQL_EXTREF);
    }

    public void setSqlExtRefCols(String value) {
        setValue(SQL_EXTREF,value);
    }
    public boolean getRepairTouchingLines(){
        Boolean repairTouchingLines=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_REPAIRTOUCHINGLINES);
        if(repairTouchingLines==null)return true;
        return repairTouchingLines;
    }
	public void setRepairTouchingLines(boolean value) {
        setTransientObject(TRANSIENT_BOOLEAN_REPAIRTOUCHINGLINES,new Boolean(value));
	}
    public boolean isLogtime() {
        Boolean logTime=(Boolean)getTransientObject(TRANSIENT_BOOLEAN_LOGTIME);
        if(logTime==null)return false;
        return logTime;
    }
    public void setLogtime(boolean value) {
        setTransientObject(TRANSIENT_BOOLEAN_LOGTIME,new Boolean(value));
    }

	public void setVerbose(boolean value) {
		setTransientValue(Validator.CONFIG_VERBOSE, value ? ValidationConfig.TRUE : ValidationConfig.FALSE);
	}
	public boolean isVerbose() {
		return ValidationConfig.TRUE.equals(getTransientValue(Validator.CONFIG_VERBOSE));
	}

    public String getPluginsFolder() {
        return getTransientValue(TRANSIENT_STRING_PLUGINFOLDER);
    }

    public void setPluginsFolder(String path) {
        setTransientValue(TRANSIENT_STRING_PLUGINFOLDER, path);
    }
}
