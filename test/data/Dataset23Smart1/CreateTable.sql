DROP SCHEMA IF EXISTS Dataset1smart1 CASCADE;
CREATE SCHEMA Dataset1smart1;

CREATE SEQUENCE Dataset1smart1.t_ili2db_seq start 1000;
-- Dataset1.TestA.StructS1
CREATE TABLE Dataset1smart1.structs1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Dataset1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,classa1_attr2 bigint NULL
)
;
COMMENT ON TABLE Dataset1smart1.structs1 IS '@iliname Dataset1.TestA.StructS1';
COMMENT ON COLUMN Dataset1smart1.structs1.classa1_attr2 IS '@iliname Dataset1.TestA.ClassA1.attr2';
-- Dataset1.TestA.ClassA1
CREATE TABLE Dataset1smart1.classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Dataset1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,attr1 varchar(60) NULL
)
;
COMMENT ON TABLE Dataset1smart1.classa1 IS '@iliname Dataset1.TestA.ClassA1';
COMMENT ON COLUMN Dataset1smart1.classa1.attr1 IS '@iliname attr1';
-- Dataset1.TestA.ClassB1
CREATE TABLE Dataset1smart1.classb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Dataset1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
)
;
COMMENT ON TABLE Dataset1smart1.classb1 IS '@iliname Dataset1.TestA.ClassB1';
CREATE TABLE Dataset1smart1.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
  ,domains varchar(1024) NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE Dataset1smart1.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE Dataset1smart1.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,importrun bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Dataset1smart1.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE Dataset1smart1.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Dataset1smart1.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE Dataset1smart1.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE Dataset1smart1.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,ColOwner)
)
;
ALTER TABLE Dataset1smart1.structs1 ADD CONSTRAINT structs1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Dataset1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.structs1 ADD CONSTRAINT structs1_classa1_attr2_fkey FOREIGN KEY ( classa1_attr2 ) REFERENCES Dataset1smart1.classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.classa1 ADD CONSTRAINT classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Dataset1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.classb1 ADD CONSTRAINT classb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Dataset1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES Dataset1smart1.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE Dataset1smart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( importrun ) REFERENCES Dataset1smart1.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES Dataset1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Dataset1smart1.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName_key UNIQUE (iliversion,modelName)
;
ALTER TABLE Dataset1smart1.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,ColOwner)
;
