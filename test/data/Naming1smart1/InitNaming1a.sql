DROP SCHEMA IF EXISTS Naming1smart1 CASCADE;
CREATE SCHEMA Naming1smart1;

CREATE SEQUENCE Naming1smart1.t_ili2db_seq;
SELECT pg_catalog.setval('Naming1smart1.t_ili2db_seq', 16, true);

CREATE TABLE Naming1smart1.T_ILI2DB_BASKET (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NULL
  ,topic varchar(200) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attachmentKey varchar(200) NOT NULL
)
;
-- Naming1.TestClass.Classa1
CREATE TABLE Naming1smart1.naming1testclass_classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Naming1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attra varchar(10) NULL
)
;
COMMENT ON TABLE Naming1smart1.naming1testclass_classa1 IS '@iliname Naming1.TestClass.Classa1';
COMMENT ON COLUMN Naming1smart1.naming1testclass_classa1.attra IS '@iliname attrA';
CREATE TABLE Naming1smart1.T_ILI2DB_DATASET (
  T_Id bigint PRIMARY KEY
  ,datasetName varchar(200) NULL
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_IMPORT_OBJECT (
  T_Id bigint PRIMARY KEY
  ,import_basket bigint NOT NULL
  ,class varchar(200) NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Naming1smart1.T_ILI2DB_IMPORT_OBJECT IS 'DEPRECATED, do not use';
CREATE TABLE Naming1smart1.T_ILI2DB_ATTRNAME (
  IliName varchar(1024) NOT NULL
  ,SqlName varchar(1024) NOT NULL
  ,Owner varchar(1024) NOT NULL
  ,Target varchar(1024) NULL
  ,PRIMARY KEY (Owner,SqlName)
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_MODEL (
  file varchar(250) NOT NULL
  ,iliversion varchar(3) NOT NULL
  ,modelName text NOT NULL
  ,content text NOT NULL
  ,importDate timestamp NOT NULL
  ,PRIMARY KEY (iliversion,modelName)
)
;
-- Naming1.TestClass.ClassA1
CREATE TABLE Naming1smart1.testclass_classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Naming1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr1 varchar(60) NULL
)
;
COMMENT ON TABLE Naming1smart1.testclass_classa1 IS '@iliname Naming1.TestClass.ClassA1';
CREATE TABLE Naming1smart1.T_ILI2DB_TRAFO (
  iliname varchar(1024) NOT NULL
  ,tag varchar(1024) NOT NULL
  ,setting varchar(1024) NOT NULL
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_IMPORT_BASKET (
  T_Id bigint PRIMARY KEY
  ,import bigint NOT NULL
  ,basket bigint NOT NULL
  ,objectCount integer NULL
  ,start_t_id bigint NULL
  ,end_t_id bigint NULL
)
;
COMMENT ON TABLE Naming1smart1.T_ILI2DB_IMPORT_BASKET IS 'DEPRECATED, do not use';
CREATE TABLE Naming1smart1.T_ILI2DB_INHERITANCE (
  thisClass varchar(1024) PRIMARY KEY
  ,baseClass varchar(1024) NULL
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_CLASSNAME (
  IliName varchar(1024) PRIMARY KEY
  ,SqlName varchar(1024) NOT NULL
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_SETTINGS (
  tag varchar(60) PRIMARY KEY
  ,setting varchar(255) NULL
)
;
CREATE TABLE Naming1smart1.T_ILI2DB_IMPORT (
  T_Id bigint PRIMARY KEY
  ,dataset bigint NOT NULL
  ,importDate timestamp NOT NULL
  ,importUser varchar(40) NOT NULL
  ,importFile varchar(200) NULL
)
;
COMMENT ON TABLE Naming1smart1.T_ILI2DB_IMPORT IS 'DEPRECATED, do not use';
-- Naming1.TestAttr.ClassA1
CREATE TABLE Naming1smart1.testattr_classa1 (
  T_Id bigint PRIMARY KEY DEFAULT nextval('Naming1smart1.t_ili2db_seq')
  ,T_basket bigint NOT NULL
  ,T_Type varchar(60) NOT NULL
  ,T_Ili_Tid varchar(200) NULL
  ,attr1 varchar(60) NULL
  ,attr11 varchar(10) NULL
  ,attra varchar(10) NULL
  ,attra1 varchar(10) NULL
)
;
COMMENT ON TABLE Naming1smart1.testattr_classa1 IS '@iliname Naming1.TestAttr.ClassA1';
COMMENT ON COLUMN Naming1smart1.testattr_classa1.attr11 IS '@iliname Attr1';
COMMENT ON COLUMN Naming1smart1.testattr_classa1.attra IS '@iliname attrA';
COMMENT ON COLUMN Naming1smart1.testattr_classa1.attra1 IS '@iliname attrA';
ALTER TABLE Naming1smart1.T_ILI2DB_BASKET ADD CONSTRAINT T_ILI2DB_BASKET_dataset_fkey FOREIGN KEY ( dataset ) REFERENCES Naming1smart1.T_ILI2DB_DATASET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Naming1smart1.naming1testclass_classa1 ADD CONSTRAINT naming1testclass_classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Naming1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Naming1smart1.T_ILI2DB_DATASET ADD UNIQUE (datasetName)
;
ALTER TABLE Naming1smart1.T_ILI2DB_ATTRNAME ADD UNIQUE (Owner,SqlName)
;
ALTER TABLE Naming1smart1.T_ILI2DB_MODEL ADD UNIQUE (iliversion,modelName)
;
ALTER TABLE Naming1smart1.testclass_classa1 ADD CONSTRAINT testclass_classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Naming1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Naming1smart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_import_fkey FOREIGN KEY ( import ) REFERENCES Naming1smart1.T_ILI2DB_IMPORT DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Naming1smart1.T_ILI2DB_IMPORT_BASKET ADD CONSTRAINT T_ILI2DB_IMPORT_BASKET_basket_fkey FOREIGN KEY ( basket ) REFERENCES Naming1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE Naming1smart1.testattr_classa1 ADD CONSTRAINT testattr_classa1_T_basket_fkey FOREIGN KEY ( T_basket ) REFERENCES Naming1smart1.T_ILI2DB_BASKET DEFERRABLE INITIALLY DEFERRED;


INSERT INTO Naming1smart1.t_ili2db_dataset VALUES (1, 'Testset1');


--
-- TOC entry 3951 (class 0 OID 749734)
-- Dependencies: 375
-- Data for Name: t_ili2db_basket; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_basket VALUES (3, 1, 'Naming1.TestAttr', '1', 'Naming1a.xtf-3');
INSERT INTO Naming1smart1.t_ili2db_basket VALUES (11, 1, 'Naming1.TestClass', '2', 'Naming1a.xtf-3');


--
-- TOC entry 3952 (class 0 OID 749742)
-- Dependencies: 376
-- Data for Name: naming1testclass_classa1; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.naming1testclass_classa1 VALUES (13, 11, 'c2', 'attrA''');


--
-- TOC entry 3955 (class 0 OID 749758)
-- Dependencies: 379
-- Data for Name: t_ili2db_attrname; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1.Attr1', 'attr11', 'testattr_classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestClass.ClassA1.attr1', 'attr1', 'testclass_classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1a.attrA', 'attra', 'testattr_classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1.attr1', 'attr1', 'testattr_classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestClass.Classa1.attrA', 'attra', 'naming1testclass_classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1b.attrA', 'attra1', 'testattr_classa1', NULL);


--
-- TOC entry 3961 (class 0 OID 749799)
-- Dependencies: 385
-- Data for Name: t_ili2db_classname; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1a', 'testattr_classa1a');
INSERT INTO Naming1smart1.t_ili2db_classname VALUES ('Naming1.TestClass.ClassA1', 'testclass_classa1');
INSERT INTO Naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1', 'testattr_classa1');
INSERT INTO Naming1smart1.t_ili2db_classname VALUES ('Naming1.TestClass.Classa1', 'naming1testclass_classa1');
INSERT INTO Naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1b', 'testattr_classa1b');


--
-- TOC entry 3963 (class 0 OID 749812)
-- Dependencies: 387
-- Data for Name: t_ili2db_import; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_import VALUES (2, 1, '2016-11-17 17:48:35.897', 'postgres', 'test\data\Naming1smart1\Naming1a.xtf');


--
-- TOC entry 3959 (class 0 OID 749786)
-- Dependencies: 383
-- Data for Name: t_ili2db_import_basket; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_import_basket VALUES (7, 2, 3, 3, 3, 6);
INSERT INTO Naming1smart1.t_ili2db_import_basket VALUES (14, 2, 11, 2, 11, 13);


--
-- TOC entry 3954 (class 0 OID 749753)
-- Dependencies: 378
-- Data for Name: t_ili2db_import_object; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_import_object VALUES (8, 7, 'Naming1.TestAttr.ClassA1a', 1, 5, 5);
INSERT INTO Naming1smart1.t_ili2db_import_object VALUES (9, 7, 'Naming1.TestAttr.ClassA1', 1, 4, 4);
INSERT INTO Naming1smart1.t_ili2db_import_object VALUES (10, 7, 'Naming1.TestAttr.ClassA1b', 1, 6, 6);
INSERT INTO Naming1smart1.t_ili2db_import_object VALUES (15, 14, 'Naming1.TestClass.ClassA1', 1, 12, 12);
INSERT INTO Naming1smart1.t_ili2db_import_object VALUES (16, 14, 'Naming1.TestClass.Classa1', 1, 13, 13);


--
-- TOC entry 3960 (class 0 OID 749791)
-- Dependencies: 384
-- Data for Name: t_ili2db_inheritance; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1', NULL);
INSERT INTO Naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestClass.Classa1', NULL);
INSERT INTO Naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestClass.ClassA1', NULL);
INSERT INTO Naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1b', 'Naming1.TestAttr.ClassA1');
INSERT INTO Naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1a', 'Naming1.TestAttr.ClassA1');


--
-- TOC entry 3956 (class 0 OID 749766)
-- Dependencies: 380
-- Data for Name: t_ili2db_model; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_model VALUES ('Naming1.ili', '2.3', 'Naming1', 'INTERLIS 2.3;

MODEL Naming1
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-11-17" =
    
  TOPIC TestAttr =
    
    CLASS ClassA1 =
    	attr1 : TEXT*60;
    	Attr1 : TEXT*10;
    END ClassA1;
    CLASS ClassA1a EXTENDS ClassA1 =
    	attrA : TEXT*10;
    END ClassA1a;
    CLASS ClassA1b EXTENDS ClassA1 =
    	attrA : TEXT*10;
    END ClassA1b;
    
  END TestAttr;

  TOPIC TestClass =
    
    CLASS ClassA1 =
    	attr1 : TEXT*60;
    END ClassA1;
    CLASS Classa1  =
    	attrA : TEXT*10;
    END Classa1;
    
  END TestClass;
  
  
END Naming1.
', '2016-11-17 17:48:35.881');





--
-- TOC entry 3962 (class 0 OID 749807)
-- Dependencies: 386
-- Data for Name: t_ili2db_settings; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.5.0-20161114');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO Naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');


--
-- TOC entry 3958 (class 0 OID 749780)
-- Dependencies: 382
-- Data for Name: t_ili2db_trafo; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1a', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO Naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestClass.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO Naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO Naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestClass.Classa1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO Naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1b', 'ch.ehi.ili2db.inheritance', 'superClass');


--
-- TOC entry 3964 (class 0 OID 749817)
-- Dependencies: 388
-- Data for Name: testattr_classa1; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.testattr_classa1 VALUES (4, 3, 'testattr_classa1', 'a1', 'a1 first', 'a1 second', NULL, NULL);
INSERT INTO Naming1smart1.testattr_classa1 VALUES (5, 3, 'testattr_classa1a', 'a2', 'a2 first', 'a2 second', 'a2', NULL);
INSERT INTO Naming1smart1.testattr_classa1 VALUES (6, 3, 'testattr_classa1b', 'a3', 'a3 first', 'a3 second', NULL, 'a3');


--
-- TOC entry 3957 (class 0 OID 749774)
-- Dependencies: 381
-- Data for Name: testclass_classa1; Type: TABLE DATA; Schema: naming1smart1; Owner: postgres
--

INSERT INTO Naming1smart1.testclass_classa1 VALUES (12, 11, 'c1', 'attr1');


-- Completed on 2016-11-17 17:49:36

--
-- PostgreSQL database dump complete
--

