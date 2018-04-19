DROP SCHEMA IF EXISTS Oid23 CASCADE;
CREATE SCHEMA Oid23;

CREATE SEQUENCE Oid23.t_ili2db_seq;
-- Oid23.TestA.ClassA1
CREATE TABLE Oid23.classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Oid23.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid uuid NULL DEFAULT uuid_generate_v4()
)
;
COMMENT ON TABLE Oid23.classa1 IS '@iliname Oid23.TestA.ClassA1';
-- Oid23.TestA.ClassA1b
CREATE TABLE Oid23.classa1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE Oid23.classa1b IS '@iliname Oid23.TestA.ClassA1b';
-- Oid23.TestA.ClassB1
CREATE TABLE Oid23.classb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Oid23.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid uuid NULL DEFAULT uuid_generate_v4()
)
;
COMMENT ON TABLE Oid23.classb1 IS '@iliname Oid23.TestA.ClassB1';
-- Oid23.TestA.ClassB1b
CREATE TABLE Oid23.classb1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE Oid23.classb1b IS '@iliname Oid23.TestA.ClassB1b';
-- Oid23.TestC.ClassC1
CREATE TABLE Oid23.classc1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Oid23.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,a bigint NULL
)
;
COMMENT ON TABLE Oid23.classc1 IS '@iliname Oid23.TestC.ClassC1';
CREATE TABLE Oid23.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE Oid23.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE Oid23.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Oid23.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE Oid23.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Oid23.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE Oid23.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE Oid23.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE Oid23.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,Owner)
)
;
ALTER TABLE Oid23.classa1 ADD CONSTRAINT classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classa1b ADD CONSTRAINT classa1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES Oid23.classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classa1b ADD CONSTRAINT classa1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classb1 ADD CONSTRAINT classb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classb1b ADD CONSTRAINT classb1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES Oid23.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classb1b ADD CONSTRAINT classb1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classc1 ADD CONSTRAINT classc1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.classc1 ADD CONSTRAINT classc1_a_fkey FOREIGN KEY ( a ) REFERENCES Oid23.classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES Oid23.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE Oid23.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES Oid23.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES Oid23.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Oid23.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE Oid23.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,Owner)
;
