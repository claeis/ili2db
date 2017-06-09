CREATE SEQUENCE InheritanceSmart2.t_ili2db_seq;;
-- Inheritance2.TestA.ClassA3b
CREATE TABLE InheritanceSmart2.classa3b (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra3b varchar(30) NULL
  ,attra3 varchar(20) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.classa3b IS '@iliname Inheritance2.TestA.ClassA3b';
COMMENT ON COLUMN InheritanceSmart2.classa3b.attra3b IS '@iliname attrA3b';
COMMENT ON COLUMN InheritanceSmart2.classa3b.attra3 IS '@iliname attrA3';
-- Inheritance2.TestA.ClassA3c
CREATE TABLE InheritanceSmart2.classa3c (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra3c varchar(40) NULL
  ,attra3b varchar(30) NULL
  ,attra3 varchar(20) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.classa3c IS '@iliname Inheritance2.TestA.ClassA3c';
COMMENT ON COLUMN InheritanceSmart2.classa3c.attra3c IS '@iliname attrA3c';
COMMENT ON COLUMN InheritanceSmart2.classa3c.attra3b IS '@iliname attrA3b';
COMMENT ON COLUMN InheritanceSmart2.classa3c.attra3 IS '@iliname attrA3';
-- Inheritance2.TestA.ClassB
CREATE TABLE InheritanceSmart2.classb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrb varchar(40) NULL
  ,a_classa3b bigint NULL
  ,a_classa3c bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart2.classb IS '@iliname Inheritance2.TestA.ClassB';
COMMENT ON COLUMN InheritanceSmart2.classb.attrb IS '@iliname attrB';
-- Inheritance2.TestA.aa2bb
CREATE TABLE InheritanceSmart2.aa2bb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,aa_classa3b bigint NULL
  ,aa_classa3c bigint NULL
  ,bb bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceSmart2.aa2bb IS '@iliname Inheritance2.TestA.aa2bb';
CREATE TABLE InheritanceSmart2.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart2.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart2.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart2.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,Owner)
)
;
ALTER TABLE InheritanceSmart2.classa3b ADD CONSTRAINT classa3b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classa3c ADD CONSTRAINT classa3c_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_a_classa3b_fkey FOREIGN KEY ( a_classa3b ) REFERENCES InheritanceSmart2.classa3b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_a_classa3c_fkey FOREIGN KEY ( a_classa3c ) REFERENCES InheritanceSmart2.classa3c DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_aa_classa3b_fkey FOREIGN KEY ( aa_classa3b ) REFERENCES InheritanceSmart2.classa3b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_aa_classa3c_fkey FOREIGN KEY ( aa_classa3c ) REFERENCES InheritanceSmart2.classa3c DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_bb_fkey FOREIGN KEY ( bb ) REFERENCES InheritanceSmart2.classb DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES InheritanceSmart2.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES InheritanceSmart2.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE InheritanceSmart2.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,Owner)
;
