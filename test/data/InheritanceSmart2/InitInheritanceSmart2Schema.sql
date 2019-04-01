DROP SCHEMA IF EXISTS InheritanceSmart2 CASCADE;
CREATE SCHEMA InheritanceSmart2;

CREATE SEQUENCE InheritanceSmart2.t_ili2db_seq;

CREATE TABLE InheritanceSmart2.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
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
CREATE TABLE InheritanceSmart2.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
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
CREATE TABLE InheritanceSmart2.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceSmart2.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceSmart2.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
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
CREATE TABLE InheritanceSmart2.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,ColOwner)
)
;
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
CREATE TABLE InheritanceSmart2.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE InheritanceSmart2.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
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
ALTER TABLE InheritanceSmart2.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset FOREIGN KEY ( dataset ) REFERENCES InheritanceSmart2.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classa3b ADD CONSTRAINT classa3b_T_basket FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_T_basket FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_a_classa3b FOREIGN KEY ( a_classa3b ) REFERENCES InheritanceSmart2.classa3b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.classb ADD CONSTRAINT classb_a_classa3c FOREIGN KEY ( a_classa3c ) REFERENCES InheritanceSmart2.classa3c DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_iliversion_modelName UNIQUE (iliversion,modelName)
;
ALTER TABLE InheritanceSmart2.classa3c ADD CONSTRAINT classa3c_T_basket FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner UNIQUE (SqlName,ColOwner)
;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_T_basket FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_aa_classa3b FOREIGN KEY ( aa_classa3b ) REFERENCES InheritanceSmart2.classa3b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_aa_classa3c FOREIGN KEY ( aa_classa3c ) REFERENCES InheritanceSmart2.classa3c DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.aa2bb ADD CONSTRAINT aa2bb_bb FOREIGN KEY ( bb ) REFERENCES InheritanceSmart2.classb DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName UNIQUE (datasetName)
;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import FOREIGN KEY ( importrun ) REFERENCES InheritanceSmart2.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart2.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket FOREIGN KEY ( basket ) REFERENCES InheritanceSmart2.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;


SELECT pg_catalog.setval('InheritanceSmart2.t_ili2db_seq', 19, true);

INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassB.attrB', 'attrb', 'classb', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.a2b.a', 'a_classa3b', 'classb', 'classa3b');
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.a2b.a', 'a_classa3c', 'classb', 'classa3c');
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassA3c.attrA3c', 'attra3c', 'classa3c', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassA3.attrA3', 'attra3', 'classa3c', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassA3.attrA3', 'attra3', 'classa3b', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.aa2bb.bb', 'bb', 'aa2bb', 'classb');
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.aa2bb.aa', 'aa_classa3c', 'aa2bb', 'classa3c');
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.aa2bb.aa', 'aa_classa3b', 'aa2bb', 'classa3b');
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassA3b.attrA3b', 'attra3b', 'classa3c', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_attrname VALUES ('Inheritance2.TestA.ClassA3b.attrA3b', 'attra3b', 'classa3b', NULL);

INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.a2b', 'a2b');
INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.ClassA3b', 'classa3b');
INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.ClassA3c', 'classa3c');
INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.aa2bb', 'aa2bb');
INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.ClassA3', 'classa3');
INSERT INTO InheritanceSmart2.t_ili2db_classname VALUES ('Inheritance2.TestA.ClassB', 'classb');

INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.ClassA3c', 'Inheritance2.TestA.ClassA3b');
INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.ClassA3b', 'Inheritance2.TestA.ClassA3');
INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.ClassB', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.aa2bb', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.a2b', NULL);
INSERT INTO InheritanceSmart2.t_ili2db_inheritance VALUES ('Inheritance2.TestA.ClassA3', NULL);

INSERT INTO InheritanceSmart2.t_ili2db_model VALUES ('Inheritance2.ili', '2.3', 'Inheritance2', 'INTERLIS 2.3;

MODEL Inheritance2
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-05-26" =
    
  TOPIC TestA =
  
  CLASS ClassA3 (ABSTRACT) =
  	attrA3 : TEXT*20;
  END ClassA3;
  
  CLASS ClassA3b EXTENDS ClassA3 =
  	attrA3b : TEXT*30;
  END ClassA3b;

  CLASS ClassA3c EXTENDS ClassA3b =
  	attrA3c : TEXT*40;
  END ClassA3c;

  CLASS ClassB =
  	attrB : TEXT*40;
  END ClassB;

  ASSOCIATION a2b =
  	a (EXTERNAL) -- {1} ClassA3b;
  	b (EXTERNAL) -- {0..*} ClassB;
  END a2b;

  ASSOCIATION aa2bb =
  	aa (EXTERNAL) -- {0..*} ClassA3b;
  	bb (EXTERNAL) -- {0..*} ClassB;
  END aa2bb;
  
  END TestA;
  
  
END Inheritance2.

', '2016-11-22 07:34:12.448');


INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.5.0-20161114');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart2');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO InheritanceSmart2.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');


INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.a2b', 'ch.ehi.ili2db.inheritance', 'newAndSubClass');
INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.ClassA3b', 'ch.ehi.ili2db.inheritance', 'newAndSubClass');
INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.ClassA3c', 'ch.ehi.ili2db.inheritance', 'newAndSubClass');
INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.aa2bb', 'ch.ehi.ili2db.inheritance', 'newAndSubClass');
INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.ClassA3', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO InheritanceSmart2.t_ili2db_trafo VALUES ('Inheritance2.TestA.ClassB', 'ch.ehi.ili2db.inheritance', 'newAndSubClass');

