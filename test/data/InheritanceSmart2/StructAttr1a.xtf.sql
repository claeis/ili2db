CREATE SEQUENCE InheritanceSmart2.t_ili2db_seq;;
-- StructAttr1.TopicA.StructA
CREATE TABLE InheritanceSmart2.topica_structa (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,aname varchar(1024) NOT NULL
  ,topica_classa_attr1 bigint NULL
  ,topica_classb_attr1 bigint NULL
  ,topica_classc_attr1 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topica_structa IS '@iliname StructAttr1.TopicA.StructA';
COMMENT ON COLUMN InheritanceSmart2.topica_structa.aname IS '@iliname name';
COMMENT ON COLUMN InheritanceSmart2.topica_structa.topica_classa_attr1 IS '@iliname StructAttr1.TopicA.ClassA.attr1';
COMMENT ON COLUMN InheritanceSmart2.topica_structa.topica_classb_attr1 IS '@iliname StructAttr1.TopicA.ClassA.attr1';
COMMENT ON COLUMN InheritanceSmart2.topica_structa.topica_classc_attr1 IS '@iliname StructAttr1.TopicA.ClassA.attr1';
-- StructAttr1.TopicA.ClassA
CREATE TABLE InheritanceSmart2.topica_classa (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr2 varchar(1024) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topica_classa IS '@iliname StructAttr1.TopicA.ClassA';
-- StructAttr1.TopicA.ClassB
CREATE TABLE InheritanceSmart2.topica_classb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr3 varchar(1024) NOT NULL
  ,attr2 varchar(1024) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topica_classb IS '@iliname StructAttr1.TopicA.ClassB';
-- StructAttr1.TopicA.ClassC
CREATE TABLE InheritanceSmart2.topica_classc (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr4 varchar(1024) NOT NULL
  ,attr3 varchar(1024) NOT NULL
  ,attr2 varchar(1024) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topica_classc IS '@iliname StructAttr1.TopicA.ClassC';
-- StructAttr1.TopicB.StructA
CREATE TABLE InheritanceSmart2.topicb_structa (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,aname varchar(1024) NOT NULL
  ,topicb_classb_attr1 bigint NULL
  ,topicb_classc_attr1 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topicb_structa IS '@iliname StructAttr1.TopicB.StructA';
COMMENT ON COLUMN InheritanceSmart2.topicb_structa.aname IS '@iliname name';
COMMENT ON COLUMN InheritanceSmart2.topicb_structa.topicb_classb_attr1 IS '@iliname StructAttr1.TopicB.ClassA.attr1';
COMMENT ON COLUMN InheritanceSmart2.topicb_structa.topicb_classc_attr1 IS '@iliname StructAttr1.TopicB.ClassA.attr1';
-- StructAttr1.TopicB.ClassB
CREATE TABLE InheritanceSmart2.topicb_classb (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr3 varchar(1024) NOT NULL
  ,attr2 varchar(1024) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topicb_classb IS '@iliname StructAttr1.TopicB.ClassB';
-- StructAttr1.TopicB.ClassC
CREATE TABLE InheritanceSmart2.topicb_classc (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart2.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr4 varchar(1024) NOT NULL
  ,attr3 varchar(1024) NOT NULL
  ,attr2 varchar(1024) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.topicb_classc IS '@iliname StructAttr1.TopicB.ClassC';
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
  ,importrun bigint NOT NULL
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
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
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
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (ColOwner,SqlName)
)
;
ALTER TABLE InheritanceSmart2.topica_structa ADD CONSTRAINT topica_structa_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_structa ADD CONSTRAINT topica_structa_topica_classa_attr1_fkey FOREIGN KEY ( topica_classa_attr1 ) REFERENCES InheritanceSmart2.topica_classa DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_structa ADD CONSTRAINT topica_structa_topica_classb_attr1_fkey FOREIGN KEY ( topica_classb_attr1 ) REFERENCES InheritanceSmart2.topica_classb DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_structa ADD CONSTRAINT topica_structa_topica_classc_attr1_fkey FOREIGN KEY ( topica_classc_attr1 ) REFERENCES InheritanceSmart2.topica_classc DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_classa ADD CONSTRAINT topica_classa_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_classb ADD CONSTRAINT topica_classb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topica_classc ADD CONSTRAINT topica_classc_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topicb_structa ADD CONSTRAINT topicb_structa_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topicb_structa ADD CONSTRAINT topicb_structa_topicb_classb_attr1_fkey FOREIGN KEY ( topicb_classb_attr1 ) REFERENCES InheritanceSmart2.topicb_classb DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topicb_structa ADD CONSTRAINT topicb_structa_topicb_classc_attr1_fkey FOREIGN KEY ( topicb_classc_attr1 ) REFERENCES InheritanceSmart2.topicb_classc DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topicb_classb ADD CONSTRAINT topicb_classb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.topicb_classc ADD CONSTRAINT topicb_classc_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES InheritanceSmart2.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES InheritanceSmart2.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName_key UNIQUE (iliversion,modelName)
;
ALTER TABLE InheritanceSmart2.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_Owner_SqlName_key UNIQUE (ColOwner,SqlName)
;
