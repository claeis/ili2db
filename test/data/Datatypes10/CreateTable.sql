DROP SCHEMA IF EXISTS Datatypes10 CASCADE;
CREATE SCHEMA Datatypes10;

CREATE SEQUENCE Datatypes10.t_ili2db_seq;
-- Datatypes10.Topic.TableA
CREATE TABLE Datatypes10.tablea (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Datatypes10.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,dim1 decimal(2,1) NULL
  ,dim2 decimal(2,1) NULL
  ,radians decimal(2,1) NULL
  ,grads decimal(4,1) NULL
  ,degrees decimal(4,1) NULL
  ,bereich decimal(2,1) NULL
  ,bereichint integer NULL
  ,atext varchar(10) NULL
  ,datum date NULL
  ,aufzaehlung varchar(255) NULL
  ,horizalignment varchar(255) NULL
  ,vertalignment varchar(255) NULL
)
;
SELECT AddGeometryColumn('datatypes10','tablea','koord2',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'POINT',2);
SELECT AddGeometryColumn('datatypes10','tablea','koord3',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'POINT',3);
SELECT AddGeometryColumn('datatypes10','tablea','linientyp',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'COMPOUNDCURVE',2);
SELECT AddGeometryColumn('datatypes10','tablea','surface',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'CURVEPOLYGON',2);
SELECT AddGeometryColumn('datatypes10','tablea','area',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'CURVEPOLYGON',2);
COMMENT ON TABLE Datatypes10.tablea IS '@iliname Datatypes10.Topic.TableA';
COMMENT ON COLUMN Datatypes10.tablea.bereichint IS '@iliname bereichInt';
COMMENT ON COLUMN Datatypes10.tablea.atext IS '@iliname text';
COMMENT ON COLUMN Datatypes10.tablea.horizalignment IS '@iliname horizAlignment';
COMMENT ON COLUMN Datatypes10.tablea.vertalignment IS '@iliname vertAlignment';
-- Datatypes10.Topic.OtherTable
CREATE TABLE Datatypes10.othertable (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Datatypes10.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,otherattr varchar(30) NULL
)
;
COMMENT ON TABLE Datatypes10.othertable IS '@iliname Datatypes10.Topic.OtherTable';
COMMENT ON COLUMN Datatypes10.othertable.otherattr IS '@iliname otherAttr';
-- Datatypes10.Topic.SubTable
CREATE TABLE Datatypes10.subtable (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Datatypes10.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,main bigint NULL
)
;
COMMENT ON TABLE Datatypes10.subtable IS '@iliname Datatypes10.Topic.SubTable';
CREATE TABLE Datatypes10.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE Datatypes10.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE Datatypes10.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Datatypes10.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE Datatypes10.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Datatypes10.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE Datatypes10.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE Datatypes10.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE Datatypes10.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,Owner)
)
;
ALTER TABLE Datatypes10.tablea ADD CONSTRAINT tablea_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Datatypes10.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.othertable ADD CONSTRAINT othertable_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Datatypes10.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.subtable ADD CONSTRAINT subtable_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Datatypes10.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.subtable ADD CONSTRAINT subtable_main_fkey FOREIGN KEY ( main ) REFERENCES Datatypes10.tablea DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES Datatypes10.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE Datatypes10.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES Datatypes10.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES Datatypes10.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Datatypes10.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE Datatypes10.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,Owner)
;
