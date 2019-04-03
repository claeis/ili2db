DROP SCHEMA IF EXISTS PreAndPostScriptSchema CASCADE;
CREATE SCHEMA PreAndPostScriptSchema;

CREATE SEQUENCE PreAndPostScriptSchema.t_ili2db_seq start 1000;
-- ModelA.TopicA.ClassA
CREATE TABLE PreAndPostScriptSchema.classa (
  T_Id bigint PRIMARY KEY DEFAULT nextval('PreAndPostScriptSchema.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_datasetname varchar(200) NOT NULL
  ,attr1 varchar(60) NULL
)
;
CREATE INDEX classa_t_datasetname_idx ON preandpostscriptschema.classa ( t_datasetname );
COMMENT ON TABLE PreAndPostScriptSchema.classa IS '@iliname ModelA.TopicA.ClassA';
-- ModelA.TopicA.ClassB
CREATE TABLE PreAndPostScriptSchema.classb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('PreAndPostScriptSchema.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_datasetname varchar(200) NOT NULL
  ,attr2 varchar(60) NULL
)
;
CREATE INDEX classb_t_datasetname_idx ON preandpostscriptschema.classb ( t_datasetname );
COMMENT ON TABLE PreAndPostScriptSchema.classb IS '@iliname ModelA.TopicA.ClassB';
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
  ,domains varchar(1024) NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,importrun bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE PreAndPostScriptSchema.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (ColOwner,SqlName)
)
;
ALTER TABLE PreAndPostScriptSchema.classa ADD CONSTRAINT classa_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES PreAndPostScriptSchema.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE PreAndPostScriptSchema.classb ADD CONSTRAINT classb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES PreAndPostScriptSchema.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES PreAndPostScriptSchema.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( importrun ) REFERENCES PreAndPostScriptSchema.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES PreAndPostScriptSchema.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE PreAndPostScriptSchema.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_Owner_SqlName_key UNIQUE (ColOwner,SqlName)
;
