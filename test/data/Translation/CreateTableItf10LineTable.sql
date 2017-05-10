DROP SCHEMA IF EXISTS Translation CASCADE;
CREATE SCHEMA Translation;

CREATE SEQUENCE Translation.t_ili2db_seq;;
-- ModelAsimple10.TopicA.ClassA
CREATE TABLE Translation.classa (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Translation.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra varchar(10) NULL
)
;
COMMENT ON TABLE Translation.classa IS '@iliname ModelAsimple10.TopicA.ClassA';
COMMENT ON COLUMN Translation.classa.attra IS '@iliname attrA';
-- ModelAsimple10.TopicA.ClassA2
CREATE TABLE Translation.classa2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Translation.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE Translation.classa2 IS '@iliname ModelAsimple10.TopicA.ClassA2';
-- ModelAsimple10.TopicA.ClassA2.geomA
CREATE TABLE Translation.classa2_geoma (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Translation.t_ili2db_seq')
  ,T_Ili_Tid varchar(200) NULL
  ,T_basket bigint NOT NULL
  ,_ref bigint NOT NULL
)
;
SELECT AddGeometryColumn('translation','classa2_geoma','_geom',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'COMPOUNDCURVE',2);
COMMENT ON TABLE Translation.classa2_geoma IS '@iliname ModelAsimple10.TopicA.ClassA2.geomA';
-- ModelAsimple10.TopicA.ClassA3
CREATE TABLE Translation.classa3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Translation.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
SELECT AddGeometryColumn('translation','classa3','geoma_ref',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'POINT',2);
COMMENT ON TABLE Translation.classa3 IS '@iliname ModelAsimple10.TopicA.ClassA3';
-- ModelAsimple10.TopicA.ClassA3.geomA
CREATE TABLE Translation.classa3_geoma (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Translation.t_ili2db_seq')
  ,T_Ili_Tid varchar(200) NULL
  ,T_basket bigint NOT NULL
)
;
SELECT AddGeometryColumn('translation','classa3_geoma','_geom',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'COMPOUNDCURVE',2);
COMMENT ON TABLE Translation.classa3_geoma IS '@iliname ModelAsimple10.TopicA.ClassA3.geomA';
CREATE TABLE Translation.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE Translation.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE Translation.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE Translation.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE Translation.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Translation.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE Translation.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Translation.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE Translation.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE Translation.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE Translation.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE Translation.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
CREATE TABLE Translation.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE Translation.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,Owner)
)
;
ALTER TABLE Translation.classa ADD CONSTRAINT classa_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.classa2 ADD CONSTRAINT classa2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.classa2_geoma ADD CONSTRAINT classa2_geoma_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.classa2_geoma ADD CONSTRAINT classa2_geoma__ref_fkey FOREIGN KEY ( _ref ) REFERENCES Translation.classa2 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.classa3 ADD CONSTRAINT classa3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.classa3_geoma ADD CONSTRAINT classa3_geoma_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES Translation.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE Translation.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES Translation.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES Translation.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Translation.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName_key UNIQUE (iliversion,modelName)
;
ALTER TABLE Translation.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,Owner)
;
