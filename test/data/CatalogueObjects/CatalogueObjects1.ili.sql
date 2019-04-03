CREATE SEQUENCE CatalogueObjects1.t_ili2db_seq;;
-- Localisation_V1.LocalisedText
CREATE TABLE CatalogueObjects1.localisedtext (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,alanguage varchar(255) NULL
  ,atext text NOT NULL
  ,multilingualtext_localisedtext bigint NULL
)
;
COMMENT ON TABLE CatalogueObjects1.localisedtext IS '@iliname Localisation_V1.LocalisedText';
COMMENT ON COLUMN CatalogueObjects1.localisedtext.alanguage IS '@iliname Language';
COMMENT ON COLUMN CatalogueObjects1.localisedtext.atext IS '@iliname Text';
COMMENT ON COLUMN CatalogueObjects1.localisedtext.multilingualtext_localisedtext IS '@iliname Localisation_V1.MultilingualText.LocalisedText';
-- Localisation_V1.MultilingualText
CREATE TABLE CatalogueObjects1.multilingualtext (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
)
;
COMMENT ON TABLE CatalogueObjects1.multilingualtext IS '@iliname Localisation_V1.MultilingualText';
-- CatalogueObjects1.TopicA.Katalog_Programm
CREATE TABLE CatalogueObjects1.topica_katalog_programm (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid uuid NULL DEFAULT uuid_generate_v4()
  ,code varchar(20) NOT NULL
  ,programm text NULL
  ,programm_de text NULL
  ,programm_fr text NULL
  ,programm_rm text NULL
  ,programm_it text NULL
  ,programm_en text NULL
)
;
COMMENT ON TABLE CatalogueObjects1.topica_katalog_programm IS '@iliname CatalogueObjects1.TopicA.Katalog_Programm';
COMMENT ON COLUMN CatalogueObjects1.topica_katalog_programm.code IS '@iliname Code';
-- CatalogueObjects1.TopicA.Katalog_ProgrammRef
CREATE TABLE CatalogueObjects1.topica_katalog_programmref (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,reference_topica_katalog_programm bigint NULL
  ,reference_topicb_katalog_ohneuuid bigint NULL
  ,topicb_nutzung_programm bigint NULL
)
;
COMMENT ON TABLE CatalogueObjects1.topica_katalog_programmref IS '@iliname CatalogueObjects1.TopicA.Katalog_ProgrammRef';
COMMENT ON COLUMN CatalogueObjects1.topica_katalog_programmref.topicb_nutzung_programm IS '@iliname CatalogueObjects1.TopicB.Nutzung.Programm';
-- CatalogueObjects1.TopicB.Katalog_OhneUuid
CREATE TABLE CatalogueObjects1.topicb_katalog_ohneuuid (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE CatalogueObjects1.topicb_katalog_ohneuuid IS '@iliname CatalogueObjects1.TopicB.Katalog_OhneUuid';
-- CatalogueObjects1.TopicB.Nutzung
CREATE TABLE CatalogueObjects1.topicb_nutzung (
  T_Id bigint PRIMARY KEY DEFAULT nextval('CatalogueObjects1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE CatalogueObjects1.topicb_nutzung IS '@iliname CatalogueObjects1.TopicB.Nutzung';
CREATE TABLE CatalogueObjects1.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
  ,domains varchar(1024) NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE CatalogueObjects1.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE CatalogueObjects1.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,importrun bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE CatalogueObjects1.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE CatalogueObjects1.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE CatalogueObjects1.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE CatalogueObjects1.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE CatalogueObjects1.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,ColOwner)
)
;
ALTER TABLE CatalogueObjects1.localisedtext ADD CONSTRAINT localisedtext_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.localisedtext ADD CONSTRAINT localisedtext_multilingualtext_lclsdtext_fkey FOREIGN KEY ( multilingualtext_localisedtext ) REFERENCES CatalogueObjects1.multilingualtext DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.multilingualtext ADD CONSTRAINT multilingualtext_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topica_katalog_programm ADD CONSTRAINT topica_katalog_programm_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topica_katalog_programmref ADD CONSTRAINT topica_katalog_programmref_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topica_katalog_programmref ADD CONSTRAINT topica_katalog_programmref_reference_tpc_ktlg_prgramm_fkey FOREIGN KEY ( reference_topica_katalog_programm ) REFERENCES CatalogueObjects1.topica_katalog_programm DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topica_katalog_programmref ADD CONSTRAINT topica_katalog_programmref_reference_topcb_ktlg_hnuid_fkey FOREIGN KEY ( reference_topicb_katalog_ohneuuid ) REFERENCES CatalogueObjects1.topicb_katalog_ohneuuid DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topica_katalog_programmref ADD CONSTRAINT topica_katalog_programmref_topicb_nutzung_programm_fkey FOREIGN KEY ( topicb_nutzung_programm ) REFERENCES CatalogueObjects1.topicb_nutzung DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topicb_katalog_ohneuuid ADD CONSTRAINT topicb_katalog_ohneuuid_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.topicb_nutzung ADD CONSTRAINT topicb_nutzung_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES CatalogueObjects1.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE CatalogueObjects1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES CatalogueObjects1.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES CatalogueObjects1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE CatalogueObjects1.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE CatalogueObjects1.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,ColOwner)
;
