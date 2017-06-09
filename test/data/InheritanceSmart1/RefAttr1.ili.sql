CREATE SEQUENCE InheritanceSmart1.t_ili2db_seq;;
-- RefAttr1.TopicA.ClassA1
CREATE TABLE InheritanceSmart1.topica_classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_classa1 IS '@iliname RefAttr1.TopicA.ClassA1';
-- RefAttr1.TopicA.ClassA2
CREATE TABLE InheritanceSmart1.topica_classa2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_classa2 IS '@iliname RefAttr1.TopicA.ClassA2';
-- RefAttr1.TopicA.StructA1
CREATE TABLE InheritanceSmart1.topica_structa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,ref_topica_classa1 bigint NULL
  ,ref_topica_classa2 bigint NULL
  ,topica_classb_struct bigint NULL
  ,topica_classc_struct bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_structa1 IS '@iliname RefAttr1.TopicA.StructA1';
COMMENT ON COLUMN InheritanceSmart1.topica_structa1.topica_classb_struct IS '@iliname RefAttr1.TopicA.ClassB.struct';
COMMENT ON COLUMN InheritanceSmart1.topica_structa1.topica_classc_struct IS '@iliname RefAttr1.TopicA.ClassC.struct';
-- RefAttr1.TopicA.StructA2
CREATE TABLE InheritanceSmart1.topica_structa2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,ref_topica_classa1 bigint NULL
  ,ref_topica_classa2 bigint NULL
  ,topica_classd_struct bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_structa2 IS '@iliname RefAttr1.TopicA.StructA2';
COMMENT ON COLUMN InheritanceSmart1.topica_structa2.topica_classd_struct IS '@iliname RefAttr1.TopicA.ClassD.struct';
-- RefAttr1.TopicA.ClassB
CREATE TABLE InheritanceSmart1.topica_classb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_classb IS '@iliname RefAttr1.TopicA.ClassB';
-- RefAttr1.TopicA.ClassC
CREATE TABLE InheritanceSmart1.topica_classc (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_classc IS '@iliname RefAttr1.TopicA.ClassC';
-- RefAttr1.TopicA.ClassD
CREATE TABLE InheritanceSmart1.topica_classd (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.topica_classd IS '@iliname RefAttr1.TopicA.ClassD';
CREATE TABLE InheritanceSmart1.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart1.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart1.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceSmart1.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,Owner)
)
;
ALTER TABLE InheritanceSmart1.topica_classa1 ADD CONSTRAINT topica_classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_classa2 ADD CONSTRAINT topica_classa2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa1 ADD CONSTRAINT topica_structa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa1 ADD CONSTRAINT topica_structa1_ref_topica_classa1_fkey FOREIGN KEY ( ref_topica_classa1 ) REFERENCES InheritanceSmart1.topica_classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa1 ADD CONSTRAINT topica_structa1_ref_topica_classa2_fkey FOREIGN KEY ( ref_topica_classa2 ) REFERENCES InheritanceSmart1.topica_classa2 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa1 ADD CONSTRAINT topica_structa1_topica_classb_struct_fkey FOREIGN KEY ( topica_classb_struct ) REFERENCES InheritanceSmart1.topica_classb DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa1 ADD CONSTRAINT topica_structa1_topica_classc_struct_fkey FOREIGN KEY ( topica_classc_struct ) REFERENCES InheritanceSmart1.topica_classc DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa2 ADD CONSTRAINT topica_structa2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa2 ADD CONSTRAINT topica_structa2_ref_topica_classa1_fkey FOREIGN KEY ( ref_topica_classa1 ) REFERENCES InheritanceSmart1.topica_classa1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa2 ADD CONSTRAINT topica_structa2_ref_topica_classa2_fkey FOREIGN KEY ( ref_topica_classa2 ) REFERENCES InheritanceSmart1.topica_classa2 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_structa2 ADD CONSTRAINT topica_structa2_topica_classd_struct_fkey FOREIGN KEY ( topica_classd_struct ) REFERENCES InheritanceSmart1.topica_classd DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_classb ADD CONSTRAINT topica_classb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_classc ADD CONSTRAINT topica_classc_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.topica_classd ADD CONSTRAINT topica_classd_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES InheritanceSmart1.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES InheritanceSmart1.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName_key UNIQUE (iliversion,modelName)
;
ALTER TABLE InheritanceSmart1.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,Owner)
;
