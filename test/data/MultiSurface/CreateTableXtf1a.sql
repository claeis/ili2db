DROP SCHEMA IF EXISTS MultiSurface CASCADE;
CREATE SCHEMA MultiSurface;

CREATE SEQUENCE MultiSurface.t_ili2db_seq;
-- GeometryCHLV03_V1.SurfaceStructure
CREATE TABLE MultiSurface.surfacestructure (
  T_Id bigint PRIMARY KEY DEFAULT nextval('MultiSurface.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,multisurface_surfaces bigint NULL
)
;
SELECT AddGeometryColumn('multisurface','surfacestructure','surface',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'CURVEPOLYGON',2);
COMMENT ON TABLE MultiSurface.surfacestructure IS '@iliname GeometryCHLV03_V1.SurfaceStructure';
COMMENT ON COLUMN MultiSurface.surfacestructure.surface IS '@iliname Surface';
COMMENT ON COLUMN MultiSurface.surfacestructure.multisurface_surfaces IS '@iliname GeometryCHLV03_V1.MultiSurface.Surfaces';
-- GeometryCHLV03_V1.MultiSurface
CREATE TABLE MultiSurface.multisurface (
  T_Id bigint PRIMARY KEY DEFAULT nextval('MultiSurface.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,classa1_geom bigint NULL
)
;
COMMENT ON TABLE MultiSurface.multisurface IS '@iliname GeometryCHLV03_V1.MultiSurface';
COMMENT ON COLUMN MultiSurface.multisurface.classa1_geom IS '@iliname MultiSurface1.TestA.ClassA1.geom';
-- MultiSurface1.TestA.ClassA1
CREATE TABLE MultiSurface.classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('MultiSurface.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
SELECT AddGeometryColumn('multisurface','classa1','point',(SELECT srid FROM SPATIAL_REF_SYS WHERE AUTH_NAME='EPSG' AND AUTH_SRID=21781),'POINT',2);
COMMENT ON TABLE MultiSurface.classa1 IS '@iliname MultiSurface1.TestA.ClassA1';
CREATE TABLE MultiSurface.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
  ,domains varchar(1024) NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE MultiSurface.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE MultiSurface.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,importrun bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE MultiSurface.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE MultiSurface.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE MultiSurface.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE MultiSurface.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
CREATE TABLE MultiSurface.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE MultiSurface.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,ColOwner)
)
;
ALTER TABLE MultiSurface.surfacestructure ADD CONSTRAINT surfacestructure_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES MultiSurface.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.surfacestructure ADD CONSTRAINT surfacestructure_multisurface_surfaces_fkey FOREIGN KEY ( multisurface_surfaces ) REFERENCES MultiSurface.multisurface DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.multisurface ADD CONSTRAINT multisurface_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES MultiSurface.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.multisurface ADD CONSTRAINT multisurface_classa1_geom_fkey FOREIGN KEY ( classa1_geom ) REFERENCES MultiSurface.classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.classa1 ADD CONSTRAINT classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES MultiSurface.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES MultiSurface.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE MultiSurface.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( importrun ) REFERENCES MultiSurface.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES MultiSurface.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE MultiSurface.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName_key UNIQUE (iliversion,modelName)
;
ALTER TABLE MultiSurface.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,ColOwner)
;
