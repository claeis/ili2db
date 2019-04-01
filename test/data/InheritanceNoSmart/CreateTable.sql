DROP SCHEMA IF EXISTS InheritanceNoSmart CASCADE;
CREATE SCHEMA InheritanceNoSmart;

CREATE SEQUENCE InheritanceNoSmart.t_ili2db_seq;
-- Inheritance1.TestA.ClassA1
CREATE TABLE InheritanceNoSmart.classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa1 IS '@iliname Inheritance1.TestA.ClassA1';
COMMENT ON COLUMN InheritanceNoSmart.classa1.attra1 IS '@iliname attrA1';
-- Inheritance1.TestA.ClassA2
CREATE TABLE InheritanceNoSmart.classa2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra2 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa2 IS '@iliname Inheritance1.TestA.ClassA2';
COMMENT ON COLUMN InheritanceNoSmart.classa2.attra2 IS '@iliname attrA2';
-- Inheritance1.TestA.ClassA2b
CREATE TABLE InheritanceNoSmart.classa2b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attra2b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa2b IS '@iliname Inheritance1.TestA.ClassA2b';
COMMENT ON COLUMN InheritanceNoSmart.classa2b.attra2b IS '@iliname attrA2b';
-- Inheritance1.TestA.ClassA3
CREATE TABLE InheritanceNoSmart.classa3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra3 varchar(40) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa3 IS '@iliname Inheritance1.TestA.ClassA3';
COMMENT ON COLUMN InheritanceNoSmart.classa3.attra3 IS '@iliname attrA3';
-- Inheritance1.TestA.ClassA3b
CREATE TABLE InheritanceNoSmart.classa3b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attra3b varchar(30) NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa3b IS '@iliname Inheritance1.TestA.ClassA3b';
COMMENT ON COLUMN InheritanceNoSmart.classa3b.attra3b IS '@iliname attrA3b';
-- Inheritance1.TestA.ClassA4
CREATE TABLE InheritanceNoSmart.classa4 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra4 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa4 IS '@iliname Inheritance1.TestA.ClassA4';
COMMENT ON COLUMN InheritanceNoSmart.classa4.attra4 IS '@iliname attrA4';
-- Inheritance1.TestA.ClassA4b
CREATE TABLE InheritanceNoSmart.classa4b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attra4b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa4b IS '@iliname Inheritance1.TestA.ClassA4b';
COMMENT ON COLUMN InheritanceNoSmart.classa4b.attra4b IS '@iliname attrA4b';
-- Inheritance1.TestA.ClassA4x
CREATE TABLE InheritanceNoSmart.classa4x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra4x varchar(30) NULL
  ,a bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classa4x IS '@iliname Inheritance1.TestA.ClassA4x';
COMMENT ON COLUMN InheritanceNoSmart.classa4x.attra4x IS '@iliname attrA4x';
-- Inheritance1.TestB.StructB1
CREATE TABLE InheritanceNoSmart.structb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrb1 varchar(30) NULL
  ,classb1_s1 bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structb1 IS '@iliname Inheritance1.TestB.StructB1';
COMMENT ON COLUMN InheritanceNoSmart.structb1.attrb1 IS '@iliname attrB1';
COMMENT ON COLUMN InheritanceNoSmart.structb1.classb1_s1 IS '@iliname Inheritance1.TestB.ClassB1.s1';
-- Inheritance1.TestB.StructB2
CREATE TABLE InheritanceNoSmart.structb2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,attrb2 varchar(30) NULL
  ,classb1_s2 bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structb2 IS '@iliname Inheritance1.TestB.StructB2';
COMMENT ON COLUMN InheritanceNoSmart.structb2.attrb2 IS '@iliname attrB2';
COMMENT ON COLUMN InheritanceNoSmart.structb2.classb1_s2 IS '@iliname Inheritance1.TestB.ClassB1.s2';
-- Inheritance1.TestB.StructB2b
CREATE TABLE InheritanceNoSmart.structb2b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrb2b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structb2b IS '@iliname Inheritance1.TestB.StructB2b';
COMMENT ON COLUMN InheritanceNoSmart.structb2b.attrb2b IS '@iliname attrB2b';
-- Inheritance1.TestB.StructB3
CREATE TABLE InheritanceNoSmart.structb3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,attrb3 varchar(40) NULL
  ,classb1_s3a bigint NULL
  ,classb1_s3b bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structb3 IS '@iliname Inheritance1.TestB.StructB3';
COMMENT ON COLUMN InheritanceNoSmart.structb3.attrb3 IS '@iliname attrB3';
COMMENT ON COLUMN InheritanceNoSmart.structb3.classb1_s3a IS '@iliname Inheritance1.TestB.ClassB1.s3a';
COMMENT ON COLUMN InheritanceNoSmart.structb3.classb1_s3b IS '@iliname Inheritance1.TestB.ClassB1.s3b';
-- Inheritance1.TestB.StructB3b
CREATE TABLE InheritanceNoSmart.structb3b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrb3b varchar(30) NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structb3b IS '@iliname Inheritance1.TestB.StructB3b';
COMMENT ON COLUMN InheritanceNoSmart.structb3b.attrb3b IS '@iliname attrB3b';
-- Inheritance1.TestB.ClassB1
CREATE TABLE InheritanceNoSmart.classb1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrb1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classb1 IS '@iliname Inheritance1.TestB.ClassB1';
COMMENT ON COLUMN InheritanceNoSmart.classb1.attrb1 IS '@iliname attrB1';
-- Inheritance1.TestB.ClassB1b
CREATE TABLE InheritanceNoSmart.classb1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classb1b IS '@iliname Inheritance1.TestB.ClassB1b';
-- Inheritance1.TestC.StructC1
CREATE TABLE InheritanceNoSmart.structc1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc1 varchar(30) NULL
  ,classc1_s1 bigint NULL
  ,classc1a_s1a bigint NULL
  ,classc1b_s1b bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc1 IS '@iliname Inheritance1.TestC.StructC1';
COMMENT ON COLUMN InheritanceNoSmart.structc1.attrstrc1 IS '@iliname attrStrC1';
COMMENT ON COLUMN InheritanceNoSmart.structc1.classc1_s1 IS '@iliname Inheritance1.TestC.ClassC1.s1';
COMMENT ON COLUMN InheritanceNoSmart.structc1.classc1a_s1a IS '@iliname Inheritance1.TestC.ClassC1a.s1a';
COMMENT ON COLUMN InheritanceNoSmart.structc1.classc1b_s1b IS '@iliname Inheritance1.TestC.ClassC1b.s1b';
-- Inheritance1.TestC.ClassC1
CREATE TABLE InheritanceNoSmart.classc1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrc1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc1 IS '@iliname Inheritance1.TestC.ClassC1';
COMMENT ON COLUMN InheritanceNoSmart.classc1.attrc1 IS '@iliname attrC1';
-- Inheritance1.TestC.ClassC1a
CREATE TABLE InheritanceNoSmart.classc1a (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrc1a varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc1a IS '@iliname Inheritance1.TestC.ClassC1a';
COMMENT ON COLUMN InheritanceNoSmart.classc1a.attrc1a IS '@iliname attrC1a';
-- Inheritance1.TestC.ClassC1b
CREATE TABLE InheritanceNoSmart.classc1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrc1b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc1b IS '@iliname Inheritance1.TestC.ClassC1b';
COMMENT ON COLUMN InheritanceNoSmart.classc1b.attrc1b IS '@iliname attrC1b';
-- Inheritance1.TestC.StructC2
CREATE TABLE InheritanceNoSmart.structc2 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Seq bigint NULL
  ,attrstrc2 varchar(30) NULL
  ,classc1ax_s1ax bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc2 IS '@iliname Inheritance1.TestC.StructC2';
COMMENT ON COLUMN InheritanceNoSmart.structc2.attrstrc2 IS '@iliname attrStrC2';
COMMENT ON COLUMN InheritanceNoSmart.structc2.classc1ax_s1ax IS '@iliname Inheritance1.TestC.ClassC1ax.s1ax';
-- Inheritance1.TestC.ClassC1ax
CREATE TABLE InheritanceNoSmart.classc1ax (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrc1ax varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc1ax IS '@iliname Inheritance1.TestC.ClassC1ax';
COMMENT ON COLUMN InheritanceNoSmart.classc1ax.attrc1ax IS '@iliname attrC1ax';
-- Inheritance1.TestC.StructC3
CREATE TABLE InheritanceNoSmart.structc3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Seq bigint NULL
  ,classc3_s3 bigint NULL
  ,classc3a_s3a bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc3 IS '@iliname Inheritance1.TestC.StructC3';
COMMENT ON COLUMN InheritanceNoSmart.structc3.classc3_s3 IS '@iliname Inheritance1.TestC.ClassC3.s3';
COMMENT ON COLUMN InheritanceNoSmart.structc3.classc3a_s3a IS '@iliname Inheritance1.TestC.ClassC3a.s3a';
-- Inheritance1.TestC.StructC3a
CREATE TABLE InheritanceNoSmart.structc3a (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrstrc3a varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc3a IS '@iliname Inheritance1.TestC.StructC3a';
COMMENT ON COLUMN InheritanceNoSmart.structc3a.attrstrc3a IS '@iliname attrStrC3a';
-- Inheritance1.TestC.StructC3b
CREATE TABLE InheritanceNoSmart.structc3b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrstrc3b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc3b IS '@iliname Inheritance1.TestC.StructC3b';
COMMENT ON COLUMN InheritanceNoSmart.structc3b.attrstrc3b IS '@iliname attrStrC3b';
-- Inheritance1.TestC.StructC3bb
CREATE TABLE InheritanceNoSmart.structc3bb (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.structc3bb IS '@iliname Inheritance1.TestC.StructC3bb';
-- Inheritance1.TestC.ClassC3
CREATE TABLE InheritanceNoSmart.classc3 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrc3 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc3 IS '@iliname Inheritance1.TestC.ClassC3';
COMMENT ON COLUMN InheritanceNoSmart.classc3.attrc3 IS '@iliname attrC3';
-- Inheritance1.TestC.ClassC3a
CREATE TABLE InheritanceNoSmart.classc3a (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classc3a IS '@iliname Inheritance1.TestC.ClassC3a';
-- Inheritance1.TestD.ClassD1
CREATE TABLE InheritanceNoSmart.classd1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrd1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classd1 IS '@iliname Inheritance1.TestD.ClassD1';
COMMENT ON COLUMN InheritanceNoSmart.classd1.attrd1 IS '@iliname attrD1';
-- Inheritance1.TestD.ClassD1b
CREATE TABLE InheritanceNoSmart.classd1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrd1b varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classd1b IS '@iliname Inheritance1.TestD.ClassD1b';
COMMENT ON COLUMN InheritanceNoSmart.classd1b.attrd1b IS '@iliname attrD1b';
-- Inheritance1.TestD.ClassD1x
CREATE TABLE InheritanceNoSmart.classd1x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attrd1x varchar(30) NULL
  ,d1 bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classd1x IS '@iliname Inheritance1.TestD.ClassD1x';
COMMENT ON COLUMN InheritanceNoSmart.classd1x.attrd1x IS '@iliname attrD1x';
-- Inheritance1.TestD.d2x
CREATE TABLE InheritanceNoSmart.d2x (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,d2 bigint NOT NULL
  ,x2 bigint NOT NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.d2x IS '@iliname Inheritance1.TestD.d2x';
-- Inheritance1.TestE.ClassE0
CREATE TABLE InheritanceNoSmart.classe0 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('InheritanceNoSmart.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attre0 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classe0 IS '@iliname Inheritance1.TestE.ClassE0';
COMMENT ON COLUMN InheritanceNoSmart.classe0.attre0 IS '@iliname attrE0';
-- Inheritance1.TestE.ClassE1
CREATE TABLE InheritanceNoSmart.classe1 (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attre1 varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classe1 IS '@iliname Inheritance1.TestE.ClassE1';
COMMENT ON COLUMN InheritanceNoSmart.classe1.attre1 IS '@iliname attrE1';
-- Inheritance1.TestE.ClassE1a
CREATE TABLE InheritanceNoSmart.classe1a (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrex varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classe1a IS '@iliname Inheritance1.TestE.ClassE1a';
COMMENT ON COLUMN InheritanceNoSmart.classe1a.attrex IS '@iliname attrEx';
-- Inheritance1.TestE.ClassE1b
CREATE TABLE InheritanceNoSmart.classe1b (
  T_Id bigint PRIMARY KEY
  ,T_basket bigint NOT NULL
  ,attrex varchar(30) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.classe1b IS '@iliname Inheritance1.TestE.ClassE1b';
COMMENT ON COLUMN InheritanceNoSmart.classe1b.attrex IS '@iliname attrEx';
CREATE TABLE InheritanceNoSmart.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,importrun bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE InheritanceNoSmart.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_MODEL (
  filename varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (modelName,iliversion)
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE InheritanceNoSmart.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,ColOwner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (SqlName,ColOwner)
)
;
ALTER TABLE InheritanceNoSmart.classa1 ADD CONSTRAINT classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa2 ADD CONSTRAINT classa2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa2b ADD CONSTRAINT classa2b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classa2 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa2b ADD CONSTRAINT classa2b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa3 ADD CONSTRAINT classa3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa3b ADD CONSTRAINT classa3b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classa3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa3b ADD CONSTRAINT classa3b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa4 ADD CONSTRAINT classa4_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa4b ADD CONSTRAINT classa4b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classa4 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa4b ADD CONSTRAINT classa4b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa4x ADD CONSTRAINT classa4x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classa4x ADD CONSTRAINT classa4x_a_fkey FOREIGN KEY ( a ) REFERENCES InheritanceNoSmart.classa4 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb1 ADD CONSTRAINT structb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb1 ADD CONSTRAINT structb1_classb1_s1_fkey FOREIGN KEY ( classb1_s1 ) REFERENCES InheritanceNoSmart.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb2 ADD CONSTRAINT structb2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb2 ADD CONSTRAINT structb2_classb1_s2_fkey FOREIGN KEY ( classb1_s2 ) REFERENCES InheritanceNoSmart.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb2b ADD CONSTRAINT structb2b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.structb2 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb2b ADD CONSTRAINT structb2b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb3 ADD CONSTRAINT structb3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb3 ADD CONSTRAINT structb3_classb1_s3a_fkey FOREIGN KEY ( classb1_s3a ) REFERENCES InheritanceNoSmart.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb3 ADD CONSTRAINT structb3_classb1_s3b_fkey FOREIGN KEY ( classb1_s3b ) REFERENCES InheritanceNoSmart.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb3b ADD CONSTRAINT structb3b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.structb3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structb3b ADD CONSTRAINT structb3b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classb1 ADD CONSTRAINT classb1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classb1b ADD CONSTRAINT classb1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classb1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classb1b ADD CONSTRAINT classb1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc1 ADD CONSTRAINT structc1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc1 ADD CONSTRAINT structc1_classc1_s1_fkey FOREIGN KEY ( classc1_s1 ) REFERENCES InheritanceNoSmart.classc1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc1 ADD CONSTRAINT structc1_classc1a_s1a_fkey FOREIGN KEY ( classc1a_s1a ) REFERENCES InheritanceNoSmart.classc1a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc1 ADD CONSTRAINT structc1_classc1b_s1b_fkey FOREIGN KEY ( classc1b_s1b ) REFERENCES InheritanceNoSmart.classc1b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1 ADD CONSTRAINT classc1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1a ADD CONSTRAINT classc1a_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classc1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1a ADD CONSTRAINT classc1a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1b ADD CONSTRAINT classc1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classc1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1b ADD CONSTRAINT classc1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc2 ADD CONSTRAINT structc2_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc2 ADD CONSTRAINT structc2_classc1ax_s1ax_fkey FOREIGN KEY ( classc1ax_s1ax ) REFERENCES InheritanceNoSmart.classc1ax DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1ax ADD CONSTRAINT classc1ax_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classc1a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc1ax ADD CONSTRAINT classc1ax_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3 ADD CONSTRAINT structc3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3 ADD CONSTRAINT structc3_classc3_s3_fkey FOREIGN KEY ( classc3_s3 ) REFERENCES InheritanceNoSmart.classc3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3 ADD CONSTRAINT structc3_classc3a_s3a_fkey FOREIGN KEY ( classc3a_s3a ) REFERENCES InheritanceNoSmart.classc3a DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3a ADD CONSTRAINT structc3a_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.structc3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3a ADD CONSTRAINT structc3a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3b ADD CONSTRAINT structc3b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.structc3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3b ADD CONSTRAINT structc3b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3bb ADD CONSTRAINT structc3bb_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.structc3b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.structc3bb ADD CONSTRAINT structc3bb_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc3 ADD CONSTRAINT classc3_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc3a ADD CONSTRAINT classc3a_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classc3 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classc3a ADD CONSTRAINT classc3a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classd1 ADD CONSTRAINT classd1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classd1b ADD CONSTRAINT classd1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classd1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classd1b ADD CONSTRAINT classd1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classd1x ADD CONSTRAINT classd1x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classd1x ADD CONSTRAINT classd1x_d1_fkey FOREIGN KEY ( d1 ) REFERENCES InheritanceNoSmart.classd1b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.d2x ADD CONSTRAINT d2x_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.d2x ADD CONSTRAINT d2x_d2_fkey FOREIGN KEY ( d2 ) REFERENCES InheritanceNoSmart.classd1b DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.d2x ADD CONSTRAINT d2x_x2_fkey FOREIGN KEY ( x2 ) REFERENCES InheritanceNoSmart.classd1x DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe0 ADD CONSTRAINT classe0_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1 ADD CONSTRAINT classe1_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classe0 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1 ADD CONSTRAINT classe1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1a ADD CONSTRAINT classe1a_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classe1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1a ADD CONSTRAINT classe1a_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1b ADD CONSTRAINT classe1b_T_Id_fkey FOREIGN KEY ( T_Id ) REFERENCES InheritanceNoSmart.classe1 DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.classe1b ADD CONSTRAINT classe1b_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES InheritanceNoSmart.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_DATASET ADD CONSTRAINT T_ILI2DB_DATASET_datasetName_key UNIQUE (datasetName)
;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( importrun ) REFERENCES InheritanceNoSmart.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES InheritanceNoSmart.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_MODEL ADD CONSTRAINT T_ILI2DB_MODEL_modelName_iliversion_key UNIQUE (modelName,iliversion)
;
ALTER TABLE InheritanceNoSmart.T_ILI2DB_ATTRNAME ADD CONSTRAINT T_ILI2DB_ATTRNAME_SqlName_Owner_key UNIQUE (SqlName,ColOwner)
;
