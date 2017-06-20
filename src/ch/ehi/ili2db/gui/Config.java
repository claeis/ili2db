package ch.ehi.ili2db.gui;

import ch.ehi.basics.settings.Settings;

public class Config extends Settings {
	public static final String FALSE = "False";
	public static final String TRUE = "True";
	public static final String PREFIX="ch.ehi.ili2db";
	public static final String SENDER=PREFIX+".sender";
	private static final String DEFAULT_SRS_AUTHORITY=PREFIX+".defaultSrsAuthority";
	private static final String DEFAULT_SRS_CODE=PREFIX+".defaultSrsCode";
	private static final String UUID_DEFAULT_VALUE=PREFIX+".uuidDefaultValue";
	private static final String CREATE_ENUMCOL_AS_ITFCODE=PREFIX+".createEnumColAsItfCode";
	public static final String CREATE_ENUMCOL_AS_ITFCODE_YES="yes";
	private static final String BEAUTIFY_ENUM_DISPNAME=PREFIX+".beautifyEnumDispName";
	public static final String BEAUTIFY_ENUM_DISPNAME_UNDERSCORE="underscore";
	private static final String CREATE_ENUM_DEFS=PREFIX+".createEnumDefs";
	public static final String CREATE_ENUM_DEFS_NO="no";
	public static final String CREATE_ENUM_DEFS_SINGLE="singleTable";
	public static final String CREATE_ENUM_DEFS_MULTI="multiTable";
	public static final String CREATE_ENUM_COLS=PREFIX+".createEnumCols";
	public static final String CREATE_ENUM_TXT_COL="addTxtCol";
	public static final String CREATE_DATASET_COLS=PREFIX+".createDatasetCols";
	public static final String CREATE_DATASET_COL="addDatasetCol";
	private static final String CREATE_FK=PREFIX+".createForeignKey";
	public static final String CREATE_FK_YES="yes";
	private static final String CREATE_FKIDX=PREFIX+".createForeignKeyIndex";
	public static final String CREATE_FKIDX_YES="yes";
	private static final String CREATE_STD_COLS=PREFIX+".createStdCols";
	public static final String CREATE_STD_COLS_ALL="all";
	private static final String CREATE_TYPE_DISCRIMINATOR=PREFIX+".typeDiscriminator";
	public static final String CREATE_TYPE_DISCRIMINATOR_ALWAYS="always";
	private static final String STRUCT_MAPPING=PREFIX+".structMapping";
	public static final String STRUCT_MAPPING_GENERICREF="genericRef";
	public static final String INHERITANCE_TRAFO=PREFIX+".inheritanceTrafo";
	public static final String INHERITANCE_TRAFO_SMART1="smart1";
	public static final String INHERITANCE_TRAFO_SMART2="smart2";
	public static final String CATALOGUE_REF_TRAFO=PREFIX+".catalogueRefTrafo";
	public static final String CATALOGUE_REF_TRAFO_COALESCE="coalesce";
	public static final String MULTISURFACE_TRAFO=PREFIX+".multiSurfaceTrafo";
	public static final String MULTISURFACE_TRAFO_COALESCE="coalesce";
	public static final String MULTILINE_TRAFO=PREFIX+".multiLineTrafo";
	public static final String MULTILINE_TRAFO_COALESCE="coalesce";
	public static final String MULTILINGUAL_TRAFO=PREFIX+".multilingualTrafo";
	public static final String MULTILINGUAL_TRAFO_EXPAND="expand";
	public static final String UNIQUE_CONSTRAINTS=PREFIX+".uniqueConstraints";
	public static final String UNIQUE_CONSTRAINTS_CREATE="create";
	public static final String NUMERIC_CHECK_CONSTRAINTS=PREFIX+".numericCheckConstraints";
	public static final String NUMERIC_CHECK_CONSTRAINTS_CREATE="create";
	public static final String GEOMATTR_PER_TABLE=PREFIX+".geomAttrPerTable";
	public static final String GEOMATTR_PER_TABLE_ONE="oneGeomAttrPerTable";
	private static final String NAME_OPTIMIZATION=PREFIX+".nameOptimization";
	public static final String NAME_OPTIMIZATION_DISABLE="disable";
	public static final String NAME_OPTIMIZATION_TOPIC="topic";
	public static final String MAX_SQLNAME_LENGTH=PREFIX+".maxSqlNameLength";
	private static final String SQL_NULL=PREFIX+".SqlNull";
	public static final String SQL_NULL_ENABLE="enable";
	private static final String STROKE_ARCS=PREFIX+".StrokeArcs";
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
	private static final String INIT_STRATEGY=PREFIX+".initStrategy";
	private static final String ILI2DB_CUSTOM_STRATEGY=PREFIX+".ili2dbCustomStrategy";
	private static final String DO_ITF_LINE_TABLES=PREFIX+".doItfLineTables";
	private static final String COLNAME_T_ID=PREFIX+".colName_T_ID";
	public static final String VER4_TRANSLATION=PREFIX+".ver4_translation";
	public static final String ILI1TRANSLATION=PREFIX+".ili1translation";
	public static final String DELETE_DATA="data";
	public static final String CREATE_META_INFO=PREFIX+".createMetaInfo";
	private int function;
	private String dburl;
	private String dbusr;
	private String dbpwd;
	private String dbhost;
	private String dbport;
	private String dbdatabase;
	private String dbfile;
	private String dbschema=null;
	private String modeldir;
	private String models=null;
	private String datasetName=null;
	private String baskets=null;
	private String topics=null;
	private String createscript;
	private String dropscript;
	private String xtffile;
	private String preScript;
	private String postScript;
	private String idGenerator=null;
	private String geometryConverter;
	private String jdbcDriver=null;
	private String ddlGenerator=null;
	private String deleteMode=null;
	private String logfile=null;
	private boolean configReadFromDb=false;
	private boolean itfTransferFile=false;
	private String validConfigFileName=null;
	private boolean validation=false;
	private Long minIdSeqValue=null;
	private Long maxIdSeqValue=null;
	private boolean setupPgExt=false;
	private String transferFileFormat=null;
	final static public String ILIGML20="ILIGML20"; 
	
	static public final int FC_IMPORT=0;
	static public final int FC_SCHEMAIMPORT=1;
	static public final int FC_EXPORT=2;
	static public final int FC_UPDATE=3;
	static public final int FC_DELETE=4;
	static public final int FC_REPLACE=5;
	public String getIdGenerator() {
		return idGenerator;
	}
	public void setIdGenerator(String idGeneratorClassName) {
		this.idGenerator = idGeneratorClassName;
	}
	public String getGeometryConverter() {
		return geometryConverter;
	}
	public void setGeometryConverter(String adapterClassName) {
		this.geometryConverter = adapterClassName;
	}
	public String getCreatescript() {
		return createscript;
	}
	public void setCreatescript(String createscript) {
		this.createscript = createscript;
	}
	public String getDbpwd() {
		return dbpwd;
	}
	public void setDbpwd(String dbpwd) {
		this.dbpwd = dbpwd;
	}
	public String getDburl() {
		return dburl;
	}
	public void setDburl(String dburl) {
		this.dburl = dburl;
	}
	public String getDbusr() {
		return dbusr;
	}
	public void setDbusr(String dbusr) {
		this.dbusr = dbusr;
	}
	public String getDropscript() {
		return dropscript;
	}
	public void setDropscript(String dropscript) {
		this.dropscript = dropscript;
	}
	public String getModeldir() {
		return modeldir;
	}
	public void setModeldir(String modeldir) {
		this.modeldir = modeldir;
	}
	public String getModels() {
		return models;
	}
	public void setModels(String models) {
		this.models = models;
	}
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	public String getBaskets() {
		return baskets;
	}
	public void setBaskets(String baskets) {
		this.baskets = baskets;
	}
	public String getTopics() {
		return topics;
	}
	public void setTopics(String topics) {
		this.topics = topics;
	}
	public String getXtffile() {
		return xtffile;
	}
	public void setXtffile(String xtffile) {
		this.xtffile = xtffile;
	}
	public int getFunction() {
		return function;
	}
	public void setFunction(int function) {
		this.function = function;
	}
	public String getDbfile() {
		return dbfile;
	}
	public void setDbfile(String dbfile) {
		this.dbfile = dbfile;
	}
	public String getDbhost() {
		return dbhost;
	}
	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}
	public String getDbport() {
		return dbport;
	}
	public void setDbport(String dbport) {
		this.dbport = dbport;
	}
	public String getDbdatabase() {
		return dbdatabase;
	}
	public void setDbdatabase(String dbdatabase) {
		this.dbdatabase = dbdatabase;
	}
	public String getDdlGenerator() {
		return ddlGenerator;
	}
	public void setDdlGenerator(String ddlGenerator) {
		this.ddlGenerator = ddlGenerator;
	}
	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	public String getSender() {
		return getValue(SENDER);
	}
	public void setSender(String sender) {
		setValue(SENDER,sender);
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
	public String getCreateEnumColAsItfCode() {
		return getValue(CREATE_ENUMCOL_AS_ITFCODE);
	}
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
	public String getStructMapping() {
		return getValue(STRUCT_MAPPING);
	}
	public void setStructMapping(String value) {
		setValue(STRUCT_MAPPING,value);
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
	public String getMultilingualTrafo() {
		return getValue(MULTILINGUAL_TRAFO);
	}
	public void setMultilingualTrafo(String value) {
		setValue(MULTILINGUAL_TRAFO,value);
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
	public String getStrokeArcs() {
		return getValue(STROKE_ARCS);
	}
	public void setStrokeArcs(String value) {
		setValue(STROKE_ARCS,value);
	}
	public boolean getDoItfLineTables() {
		return TRUE.equals(getValue(DO_ITF_LINE_TABLES));
	}
	public void setDoItfLineTables(boolean value) {
		setValue(DO_ITF_LINE_TABLES,value?TRUE:FALSE);
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
		return getValue(INIT_STRATEGY);
	}
	public void setInitStrategy(String value) {
		setValue(INIT_STRATEGY,value);
	}
	public String getIli2dbCustomStrategy() {
		return getValue(ILI2DB_CUSTOM_STRATEGY);
	}
	public void setIli2dbCustomStrategy(String value) {
		setValue(ILI2DB_CUSTOM_STRATEGY,value);
	}
	public java.sql.Connection getJdbcConnection() {
		return (java.sql.Connection)getTransientObject(ch.ehi.sqlgen.generator.SqlConfiguration.JDBC_CONNECTION);
	}
	public void setJdbcConnection(java.sql.Connection value) {
		setTransientObject(ch.ehi.sqlgen.generator.SqlConfiguration.JDBC_CONNECTION,value);
	}
	public String getDeleteMode() {
		return deleteMode;
	}
	public void setDeleteMode(String deleteMode) {
		this.deleteMode = deleteMode;
	}
	public String getLogfile() {
		return logfile;
	}
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}
	public boolean isConfigReadFromDb() {
		return configReadFromDb;
	}
	public void setConfigReadFromDb(boolean configReadFromDb) {
		this.configReadFromDb = configReadFromDb;
	}
	public String getDbschema() {
		return dbschema;
	}
	public void setDbschema(String dbschema) {
		this.dbschema = dbschema;
	}
	public boolean isItfTransferfile() {
		return itfTransferFile;
	}
	public void setItfTransferfile(boolean value) {
		itfTransferFile=value;
	}
	public String getValidConfigFile() {
		return validConfigFileName;
	}
	public void setValidConfigFile(String fileName) {
		this.validConfigFileName = fileName;
	}
	public boolean isValidation() {
		return validation;
	}
	public void setValidation(boolean enable) {
		this.validation = enable;
	}
	public Long getMinIdSeqValue() {
		return minIdSeqValue;
	}
	public void setMinIdSeqValue(Long minIdSeqValue) {
		this.minIdSeqValue = minIdSeqValue;
	}
	public Long getMaxIdSeqValue() {
		return maxIdSeqValue;
	}
	public void setMaxIdSeqValue(Long maxIdSeqValue) {
		this.maxIdSeqValue = maxIdSeqValue;
	}
	public String getTransferFileFormat() {
		return transferFileFormat;
	}
	public void setTransferFileFormat(String transferFileFormat) {
		this.transferFileFormat = transferFileFormat;
	}
	private boolean onlyMultiplicityReduction=false;
	public boolean isOnlyMultiplicityReduction() {
		return onlyMultiplicityReduction;
	}
	public void setOnlyMultiplicityReduction(boolean onlyMultiplicityReduction) {
		this.onlyMultiplicityReduction = onlyMultiplicityReduction;
	}
	private boolean skipGeometryErrors=false;
	public boolean isSkipGeometryErrors() {
		return skipGeometryErrors;
	}
	public void setSkipGeometryErrors(boolean skipGeometryTypeValidation) {
		this.skipGeometryErrors = skipGeometryTypeValidation;
	}
	public boolean isDisableAreaValidation() {
		return disableAreaValidation;
	}
	public void setDisableAreaValidation(boolean disableAreaValidation) {
		this.disableAreaValidation = disableAreaValidation;
	}
	private boolean disableAreaValidation=false;
	public void setVer4_translation(boolean b) {
		setValue(VER4_TRANSLATION,TRUE);
	}
	public boolean getVer4_translation() {
		return TRUE.equals(getValue(VER4_TRANSLATION))?true:false;
	}
	public void setIli1Translation(String modelMapping) {
		setValue(ILI1TRANSLATION,modelMapping);
	}
	public String getIli1Translation() {
		return getValue(ILI1TRANSLATION);
	}
	public String getPreScript() {
		return preScript;
	}
	public void setPreScript(String preScript) {
		this.preScript = preScript;
	}
	public String getPostScript() {
		return postScript;
	}
	public void setPostScript(String postScript) {
		this.postScript = postScript;
	}
	public boolean isSetupPgExt() {
		return setupPgExt;
	}
	public void setSetupPgExt(boolean setupPgExt) {
		this.setupPgExt = setupPgExt;
	}
	public void setCreateMetaInfo(boolean value) {
		setValue(CREATE_META_INFO,value?TRUE:FALSE);
	}
	public boolean getCreateMetaInfo() {
		return TRUE.equals(getValue(CREATE_META_INFO))?true:false;
	}
}
