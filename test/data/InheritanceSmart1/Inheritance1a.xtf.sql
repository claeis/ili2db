CREATE SEQUENCE InheritanceSmart1.t_ili2db_seq;;
-- Inheritance1.TestA.ClassA1
CREATE TABLE InheritanceSmart1.classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classa1 IS '@iliname Inheritance1.TestA.ClassA1';
COMMENT ON COLUMN InheritanceSmart1.classa1.attra1 IS '@iliname attrA1';
-- Inheritance1.TestA.ClassA2b
CREATE TABLE InheritanceSmart1.classa2b (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra2b varchar(30) NULL
  ,attra2 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classa2b IS '@iliname Inheritance1.TestA.ClassA2b';
COMMENT ON COLUMN InheritanceSmart1.classa2b.attra2b IS '@iliname attrA2b';
COMMENT ON COLUMN InheritanceSmart1.classa2b.attra2 IS '@iliname attrA2';
-- Inheritance1.TestA.ClassA3
CREATE TABLE InheritanceSmart1.classa3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra3 varchar(40) NULL
  ,attra3b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classa3 IS '@iliname Inheritance1.TestA.ClassA3';
COMMENT ON COLUMN InheritanceSmart1.classa3.attra3 IS '@iliname attrA3';
COMMENT ON COLUMN InheritanceSmart1.classa3.attra3b IS '@iliname attrA3b';
-- Inheritance1.TestA.ClassA4
CREATE TABLE InheritanceSmart1.classa4 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra4 varchar(30) NULL
  ,attra4b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classa4 IS '@iliname Inheritance1.TestA.ClassA4';
COMMENT ON COLUMN InheritanceSmart1.classa4.attra4 IS '@iliname attrA4';
COMMENT ON COLUMN InheritanceSmart1.classa4.attra4b IS '@iliname attrA4b';
-- Inheritance1.TestA.ClassA4x
CREATE TABLE InheritanceSmart1.classa4x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra4x varchar(30) NULL
  ,a bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classa4x IS '@iliname Inheritance1.TestA.ClassA4x';
COMMENT ON COLUMN InheritanceSmart1.classa4x.attra4x IS '@iliname attrA4x';
-- Inheritance1.TestB.StructB1
CREATE TABLE InheritanceSmart1.structb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrb1 varchar(30) NULL
  ,classb1_s1 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structb1 IS '@iliname Inheritance1.TestB.StructB1';
COMMENT ON COLUMN InheritanceSmart1.structb1.attrb1 IS '@iliname attrB1';
COMMENT ON COLUMN InheritanceSmart1.structb1.classb1_s1 IS '@iliname Inheritance1.TestB.ClassB1.s1';
-- Inheritance1.TestB.StructB2b
CREATE TABLE InheritanceSmart1.structb2b (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrb2b varchar(30) NULL
  ,attrb2 varchar(30) NULL
  ,classb1_s2 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structb2b IS '@iliname Inheritance1.TestB.StructB2b';
COMMENT ON COLUMN InheritanceSmart1.structb2b.attrb2b IS '@iliname attrB2b';
COMMENT ON COLUMN InheritanceSmart1.structb2b.attrb2 IS '@iliname attrB2';
COMMENT ON COLUMN InheritanceSmart1.structb2b.classb1_s2 IS '@iliname Inheritance1.TestB.ClassB1.s2';
-- Inheritance1.TestB.StructB3
CREATE TABLE InheritanceSmart1.structb3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,attrb3 varchar(40) NULL
  ,attrb3b varchar(30) NULL
  ,classb1_s3a bigint NULL
  ,classb1_s3b bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structb3 IS '@iliname Inheritance1.TestB.StructB3';
COMMENT ON COLUMN InheritanceSmart1.structb3.attrb3 IS '@iliname attrB3';
COMMENT ON COLUMN InheritanceSmart1.structb3.attrb3b IS '@iliname attrB3b';
COMMENT ON COLUMN InheritanceSmart1.structb3.classb1_s3a IS '@iliname Inheritance1.TestB.ClassB1.s3a';
COMMENT ON COLUMN InheritanceSmart1.structb3.classb1_s3b IS '@iliname Inheritance1.TestB.ClassB1.s3b';
-- Inheritance1.TestB.ClassB1
CREATE TABLE InheritanceSmart1.classb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrb1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classb1 IS '@iliname Inheritance1.TestB.ClassB1';
COMMENT ON COLUMN InheritanceSmart1.classb1.attrb1 IS '@iliname attrB1';
-- Inheritance1.TestC.StructC1
CREATE TABLE InheritanceSmart1.structc1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc1 varchar(30) NULL
  ,classc1a_s1a bigint NULL
  ,classc1a_s1 bigint NULL
  ,classc1b_s1b bigint NULL
  ,classc1b_s1 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structc1 IS '@iliname Inheritance1.TestC.StructC1';
COMMENT ON COLUMN InheritanceSmart1.structc1.attrstrc1 IS '@iliname attrStrC1';
COMMENT ON COLUMN InheritanceSmart1.structc1.classc1a_s1a IS '@iliname Inheritance1.TestC.ClassC1a.s1a';
COMMENT ON COLUMN InheritanceSmart1.structc1.classc1a_s1 IS '@iliname Inheritance1.TestC.ClassC1.s1';
COMMENT ON COLUMN InheritanceSmart1.structc1.classc1b_s1b IS '@iliname Inheritance1.TestC.ClassC1b.s1b';
COMMENT ON COLUMN InheritanceSmart1.structc1.classc1b_s1 IS '@iliname Inheritance1.TestC.ClassC1.s1';
-- Inheritance1.TestC.ClassC1a
CREATE TABLE InheritanceSmart1.classc1a (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrc1a varchar(30) NULL
  ,attrc1 varchar(30) NULL
  ,attrc1ax varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classc1a IS '@iliname Inheritance1.TestC.ClassC1a';
COMMENT ON COLUMN InheritanceSmart1.classc1a.attrc1a IS '@iliname attrC1a';
COMMENT ON COLUMN InheritanceSmart1.classc1a.attrc1 IS '@iliname attrC1';
COMMENT ON COLUMN InheritanceSmart1.classc1a.attrc1ax IS '@iliname attrC1ax';
-- Inheritance1.TestC.ClassC1b
CREATE TABLE InheritanceSmart1.classc1b (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrc1b varchar(30) NULL
  ,attrc1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classc1b IS '@iliname Inheritance1.TestC.ClassC1b';
COMMENT ON COLUMN InheritanceSmart1.classc1b.attrc1b IS '@iliname attrC1b';
COMMENT ON COLUMN InheritanceSmart1.classc1b.attrc1 IS '@iliname attrC1';
-- Inheritance1.TestC.StructC2
CREATE TABLE InheritanceSmart1.structc2 (
  classc1a_s1ax bigint NULL
  ,T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc2 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structc2 IS '@iliname Inheritance1.TestC.StructC2';
COMMENT ON COLUMN InheritanceSmart1.structc2.classc1a_s1ax IS '@iliname Inheritance1.TestC.ClassC1ax.s1ax';
COMMENT ON COLUMN InheritanceSmart1.structc2.attrstrc2 IS '@iliname attrStrC2';
-- Inheritance1.TestC.StructC3a
CREATE TABLE InheritanceSmart1.structc3a (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc3a varchar(30) NULL
  ,classc3a_s3a bigint NULL
  ,classc3a_s3 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structc3a IS '@iliname Inheritance1.TestC.StructC3a';
COMMENT ON COLUMN InheritanceSmart1.structc3a.attrstrc3a IS '@iliname attrStrC3a';
COMMENT ON COLUMN InheritanceSmart1.structc3a.classc3a_s3a IS '@iliname Inheritance1.TestC.ClassC3a.s3a';
COMMENT ON COLUMN InheritanceSmart1.structc3a.classc3a_s3 IS '@iliname Inheritance1.TestC.ClassC3.s3';
-- Inheritance1.TestC.StructC3b
CREATE TABLE InheritanceSmart1.structc3b (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc3b varchar(30) NULL
  ,classc3a_s3a bigint NULL
  ,classc3a_s3 bigint NULL
)
;
COMMENT ON TABLE InheritanceSmart1.structc3b IS '@iliname Inheritance1.TestC.StructC3b';
COMMENT ON COLUMN InheritanceSmart1.structc3b.attrstrc3b IS '@iliname attrStrC3b';
COMMENT ON COLUMN InheritanceSmart1.structc3b.classc3a_s3a IS '@iliname Inheritance1.TestC.ClassC3a.s3a';
COMMENT ON COLUMN InheritanceSmart1.structc3b.classc3a_s3 IS '@iliname Inheritance1.TestC.ClassC3.s3';
-- Inheritance1.TestC.ClassC3a
CREATE TABLE InheritanceSmart1.classc3a (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrc3 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classc3a IS '@iliname Inheritance1.TestC.ClassC3a';
COMMENT ON COLUMN InheritanceSmart1.classc3a.attrc3 IS '@iliname attrC3';
-- Inheritance1.TestD.ClassD1
CREATE TABLE InheritanceSmart1.classd1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrd1 varchar(30) NULL
  ,attrd1b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classd1 IS '@iliname Inheritance1.TestD.ClassD1';
COMMENT ON COLUMN InheritanceSmart1.classd1.attrd1 IS '@iliname attrD1';
COMMENT ON COLUMN InheritanceSmart1.classd1.attrd1b IS '@iliname attrD1b';
-- Inheritance1.TestD.ClassD1x
CREATE TABLE InheritanceSmart1.classd1x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrd1x varchar(30) NULL
  ,d1 bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classd1x IS '@iliname Inheritance1.TestD.ClassD1x';
COMMENT ON COLUMN InheritanceSmart1.classd1x.attrd1x IS '@iliname attrD1x';
-- Inheritance1.TestD.d2x
CREATE TABLE InheritanceSmart1.d2x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,d2 bigint NOT NULL
  ,x2 bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceSmart1.d2x IS '@iliname Inheritance1.TestD.d2x';
-- Inheritance1.TestE.ClassE1
CREATE TABLE InheritanceSmart1.classe1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceSmart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attre1 varchar(30) NULL
  ,attre0 varchar(30) NULL
  ,attrex varchar(30) NULL
  ,attrex1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceSmart1.classe1 IS '@iliname Inheritance1.TestE.ClassE1';
COMMENT ON COLUMN InheritanceSmart1.classe1.attre1 IS '@iliname attrE1';
COMMENT ON COLUMN InheritanceSmart1.classe1.attre0 IS '@iliname attrE0';
COMMENT ON COLUMN InheritanceSmart1.classe1.attrex IS '@iliname attrEx';
COMMENT ON COLUMN InheritanceSmart1.classe1.attrex1 IS '@iliname attrEx';
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
  ,importrun bigint NOT NULL
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
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
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
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (ColOwner,SqlName)
)
;
ALTER TABLE InheritanceSmart1.classa1 ADD CONSTRAINT classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classa2b ADD CONSTRAINT classa2b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classa3 ADD CONSTRAINT classa3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classa4 ADD CONSTRAINT classa4_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classa4x ADD CONSTRAINT classa4x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classa4x ADD CONSTRAINT classa4x_a_fkey FOREIGN KEY ( a ) REFERENCES InheritanceSmart1.classa4 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb1 ADD CONSTRAINT structb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb1 ADD CONSTRAINT structb1_classb1_s1_fkey FOREIGN KEY ( classb1_s1 ) REFERENCES InheritanceSmart1.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb2b ADD CONSTRAINT structb2b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb2b ADD CONSTRAINT structb2b_classb1_s2_fkey FOREIGN KEY ( classb1_s2 ) REFERENCES InheritanceSmart1.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb3 ADD CONSTRAINT structb3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb3 ADD CONSTRAINT structb3_classb1_s3a_fkey FOREIGN KEY ( classb1_s3a ) REFERENCES InheritanceSmart1.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structb3 ADD CONSTRAINT structb3_classb1_s3b_fkey FOREIGN KEY ( classb1_s3b ) REFERENCES InheritanceSmart1.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classb1 ADD CONSTRAINT classb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc1 ADD CONSTRAINT structc1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc1 ADD CONSTRAINT structc1_classc1a_s1a_fkey FOREIGN KEY ( classc1a_s1a ) REFERENCES InheritanceSmart1.classc1a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc1 ADD CONSTRAINT structc1_classc1a_s1_fkey FOREIGN KEY ( classc1a_s1 ) REFERENCES InheritanceSmart1.classc1a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc1 ADD CONSTRAINT structc1_classc1b_s1b_fkey FOREIGN KEY ( classc1b_s1b ) REFERENCES InheritanceSmart1.classc1b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc1 ADD CONSTRAINT structc1_classc1b_s1_fkey FOREIGN KEY ( classc1b_s1 ) REFERENCES InheritanceSmart1.classc1b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classc1a ADD CONSTRAINT classc1a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classc1b ADD CONSTRAINT classc1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc2 ADD CONSTRAINT structc2_classc1a_s1ax_fkey FOREIGN KEY ( classc1a_s1ax ) REFERENCES InheritanceSmart1.classc1a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc2 ADD CONSTRAINT structc2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3a ADD CONSTRAINT structc3a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3a ADD CONSTRAINT structc3a_classc3a_s3a_fkey FOREIGN KEY ( classc3a_s3a ) REFERENCES InheritanceSmart1.classc3a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3a ADD CONSTRAINT structc3a_classc3a_s3_fkey FOREIGN KEY ( classc3a_s3 ) REFERENCES InheritanceSmart1.classc3a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3b ADD CONSTRAINT structc3b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3b ADD CONSTRAINT structc3b_classc3a_s3a_fkey FOREIGN KEY ( classc3a_s3a ) REFERENCES InheritanceSmart1.classc3a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.structc3b ADD CONSTRAINT structc3b_classc3a_s3_fkey FOREIGN KEY ( classc3a_s3 ) REFERENCES InheritanceSmart1.classc3a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classc3a ADD CONSTRAINT classc3a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classd1 ADD CONSTRAINT classd1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classd1x ADD CONSTRAINT classd1x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classd1x ADD CONSTRAINT classd1x_d1_fkey FOREIGN KEY ( d1 ) REFERENCES InheritanceSmart1.classd1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.d2x ADD CONSTRAINT d2x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.d2x ADD CONSTRAINT d2x_d2_fkey FOREIGN KEY ( d2 ) REFERENCES InheritanceSmart1.classd1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.d2x ADD CONSTRAINT d2x_x2_fkey FOREIGN KEY ( x2 ) REFERENCES InheritanceSmart1.classd1x DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.classe1 ADD CONSTRAINT classe1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES InheritanceSmart1.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES InheritanceSmart1.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES InheritanceSmart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceSmart1.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE InheritanceSmart1.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_Owner_SqlName_key UNIQUE (ColOwner,SqlName)
;
