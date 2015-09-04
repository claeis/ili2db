package ch.ehi.ili2db.gui;

import ch.ehi.basics.settings.Settings;

public class Config extends Settings {
	private static final String PREFIX="ch.ehi.ili2db";
	public static final String SENDER=PREFIX+".sender";
	private static final String DEFAULT_SRS_AUTHORITY=PREFIX+".defaultSrsAuthority";
	private static final String DEFAULT_SRS_CODE=PREFIX+".defaultSrsCode";
	private static final String CREATE_ENUM_DEFS=PREFIX+".createEnumDefs";
	public static final String CREATE_ENUM_DEFS_NO="no";
	public static final String CREATE_ENUM_DEFS_SINGLE="singleTable";
	public static final String CREATE_ENUM_DEFS_MULTI="multiTable";
	public static final String CREATE_ENUM_COLS=PREFIX+".createEnumCols";
	public static final String CREATE_ENUM_TXT_COL="addTxtCol";
	private static final String CREATE_STD_COLS=PREFIX+".createStdCols";
	public static final String CREATE_STD_COLS_ALL="all";
	private static final String CREATE_TYPE_DISCRIMINATOR=PREFIX+".typeDiscriminator";
	public static final String CREATE_TYPE_DISCRIMINATOR_ALWAYS="always";
	private static final String STRUCT_MAPPING=PREFIX+".structMapping";
	public static final String STRUCT_MAPPING_GENERICREF="genericRef";
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
	public static final String DELETE_DATA="data";
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
	private String createscript;
	private String dropscript;
	private String xtffile;
	private String mappingConfig;
	private String geometryConverter;
	private String jdbcDriver=null;
	private String ddlGenerator=null;
	private String deleteMode=null;
	private String logfile=null;
	private boolean configReadFromDb=false;
	private boolean itfTransferFile=false;
	static public final int FC_IMPORT=0;
	static public final int FC_SCHEMAIMPORT=1;
	static public final int FC_EXPORT=2;
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
	public String getMappingConfigFilename() {
		return mappingConfig;
	}
	public void setMappingConfigFilename(String mappingConfig) {
		this.mappingConfig = mappingConfig;
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
	public String getCreateEnumDefs() {
		return getValue(CREATE_ENUM_DEFS);
	}
	public void setCreateEnumDefs(String value) {
		setValue(CREATE_ENUM_DEFS,value);
	}
	public String getCreateEnumCols() {
		return getValue(CREATE_ENUM_COLS);
	}
	public void setCreateEnumCols(String value) {
		setValue(CREATE_ENUM_COLS,value);
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
		return "True".equals(getValue(DO_ITF_LINE_TABLES));
	}
	public void setDoItfLineTables(boolean value) {
		setValue(DO_ITF_LINE_TABLES,value?"True":"False");
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
	public void setItfTranferfile(boolean value) {
		itfTransferFile=value;
	}
}
