package ch.ehi.ili2db.base;

public class DbNames {
	public static final String DEPRECATED="DEPRECATED, do not use";
	public static final String BASKETS_TAB="T_ILI2DB_BASKET";
	public static final String BASKETS_TAB_TOPIC_COL="topic";
	public static final String BASKETS_TAB_DATASET_COL="dataset";
	public static final String BASKETS_TAB_ATTACHMENT_KEY_COL="attachmentKey";
	public static final String DATASETS_TAB="T_ILI2DB_DATASET";
	public static final String DATASETS_TAB_DATASETNAME="datasetName";
	public static final int DATASETNAME_COL_SIZE=200;
	public static final String ENUM_TAB="T_ILI2DB_ENUM";
	public static final String ENUM_TAB_THIS_COL="thisClass";
	public static final String ENUM_TAB_BASE_COL="baseClass";
	public static final String ENUM_TAB_SEQ_COL="seq";
	public static final String ENUM_TAB_INACTIVE_COL="inactive";
	public static final String ENUM_TAB_ILICODE_COL="iliCode";
	public static final String ENUM_TAB_ITFCODE_COL="itfCode";
	public static final String ENUM_TAB_DISPNAME_COL="dispName";
	public static final String ENUM_TXT_COL_SUFFIX="_txt";
	public static final String MULTILINGUAL_TXT_COL_SUFFIX="";
	public static final String MULTILINGUAL_TXT_COL_PREFIX="_";
	public static final String MULTILINGUAL_TXT_DE_COL_SUFFIX=MULTILINGUAL_TXT_COL_PREFIX+IliNames.CHBASE1_LOCALISEDTEXT_LANG_DE;
	public static final String MULTILINGUAL_TXT_FR_COL_SUFFIX=MULTILINGUAL_TXT_COL_PREFIX+IliNames.CHBASE1_LOCALISEDTEXT_LANG_FR;
	public static final String MULTILINGUAL_TXT_RM_COL_SUFFIX=MULTILINGUAL_TXT_COL_PREFIX+IliNames.CHBASE1_LOCALISEDTEXT_LANG_RM;
	public static final String MULTILINGUAL_TXT_IT_COL_SUFFIX=MULTILINGUAL_TXT_COL_PREFIX+IliNames.CHBASE1_LOCALISEDTEXT_LANG_IT;
	public static final String MULTILINGUAL_TXT_EN_COL_SUFFIX=MULTILINGUAL_TXT_COL_PREFIX+IliNames.CHBASE1_LOCALISEDTEXT_LANG_EN;
	public static final String MULTILINGUAL_TXT_COL_SUFFIXS[]=new String[]{
			MULTILINGUAL_TXT_COL_SUFFIX,
			MULTILINGUAL_TXT_DE_COL_SUFFIX,
			MULTILINGUAL_TXT_FR_COL_SUFFIX,
			MULTILINGUAL_TXT_RM_COL_SUFFIX,
			MULTILINGUAL_TXT_IT_COL_SUFFIX,
			MULTILINGUAL_TXT_EN_COL_SUFFIX
			};
	public static final String ITF_MAINTABLE_GEOTABLEREF_COL_SUFFIX="_ref";
	public static final String ITF_LINETABLE_MAINTABLEREF_ILI_SUFFIX="_ref";
	public static final String ITF_LINETABLE_GEOMATTR_ILI_SUFFIX="_geom";
	public static final String T_ID_COL = "T_Id";
	public static final String T_ILI_TID_COL = "T_Ili_Tid";
	public static final String T_TYPE_COL = "T_Type";
	public static final String T_BASKET_COL="T_basket";
	public static final String T_DATASET_COL="T_datasetname";
	public static final String T_USER_COL = "T_User";
	public static final String T_CREATE_DATE_COL = "T_CreateDate";
	public static final String T_LAST_CHANGE_COL = "T_LastChange";
	public static final String T_SEQ_COL = "T_Seq";
	public static final String T_PARENT_ATTR_COL = "T_ParentAttr";
	public static final String T_PARENT_TYPE_COL = "T_ParentType";
	public static final String T_PARENT_ID_COL = "T_ParentId";
	public static final String MODELS_TAB="T_ILI2DB_MODEL";
	public static final String MODELS_TAB_FILE_COL="file";
	public static final String MODELS_TAB_ILIVERSION_COL="iliversion";
	public static final String MODELS_TAB_MODELNAME_COL="modelName";
	public static final String MODELS_TAB_CONTENT_COL="content";
	public static final String MODELS_TAB_IMPORTDATE_COL="importDate";
	public static final String SETTINGS_TAB="T_ILI2DB_SETTINGS";
	public static final String SETTINGS_TAB_TAG_COL="tag";
	public static final String SETTINGS_TAB_SETTING_COL="setting";
	public static final String INHERIT_TAB="T_ILI2DB_INHERITANCE";
	public static final String INHERIT_TAB_THIS_COL="thisClass";
	public static final String INHERIT_TAB_BASE_COL="baseClass";
	public static final String IMPORTS_TAB="T_ILI2DB_IMPORT";
	public static final String IMPORTS_TAB_IMPORTDATE_COL="importDate";
	public static final String IMPORTS_TAB_IMPORTUSER_COL="importUser";
	public static final String IMPORTS_TAB_IMPORTFILE_COL="importFile";
	public static final String IMPORTS_TAB_DATASET_COL="dataset";
	public static final String IMPORTS_TAB_OBJECTCOUNT_COL="objectCount";
	public static final String IMPORTS_TAB_STARTTID_COL="start_t_id";
	public static final String IMPORTS_TAB_ENDTID_COL="end_t_id";
	public static final String IMPORTS_BASKETS_TAB="T_ILI2DB_IMPORT_BASKET";
	public static final String IMPORTS_BASKETS_TAB_IMPORT_COL="import";
	public static final String IMPORTS_BASKETS_TAB_BASKET_COL="basket";
	public static final String IMPORTS_OBJECTS_TAB="T_ILI2DB_IMPORT_OBJECT";
	public static final String IMPORTS_OBJECTS_TAB_CLASS_COL="class";
	public static final String IMPORTS_OBJECTS_TAB_IMPORT_COL="import_basket";
	public static final String CLASSNAME_TAB="T_ILI2DB_CLASSNAME";
	public static final String CLASSNAME_TAB_ILINAME_COL="IliName";
	public static final String CLASSNAME_TAB_SQLNAME_COL="SqlName";
	public static final String ATTRNAME_TAB="T_ILI2DB_ATTRNAME";
	public static final String ATTRNAME_TAB_OWNER_COL="Owner";
	public static final String ATTRNAME_TAB_TARGET_COL="Target";
	public static final String ATTRNAME_TAB_ILINAME_COL="IliName";
	public static final String ATTRNAME_TAB_SQLNAME_COL="SqlName";
	public static final String TRAFO_TAB="T_ILI2DB_TRAFO";
	public static final String TRAFO_TAB_ILINAME_COL="iliname";
	public static final String TRAFO_TAB_TAG_COL="tag";
	public static final String TRAFO_TAB_SETTING_COL="setting";

	private DbNames(){}

}
