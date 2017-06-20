SET search_path = oid23, pg_catalog;

INSERT INTO t_ili2db_dataset VALUES (1, NULL);
INSERT INTO t_ili2db_dataset VALUES (9, NULL);
INSERT INTO t_ili2db_basket VALUES (3, 1, 'Oid23.TestA', 'Oid23.TestA', 'Oid1a.xtf-3');
INSERT INTO t_ili2db_basket VALUES (11, 9, 'Oid23.TestC', 'Oid23.TestC', 'Oid1c.xtf-11');
INSERT INTO classa1 VALUES (4, 3, 'classa1', 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc');
INSERT INTO classb1 VALUES (5, 3, 'classb1b', '81fc3941-01ec-4c51-b1ba-46b6295d9b4e');
INSERT INTO classb1b VALUES (5, 3);
INSERT INTO classc1 VALUES (12, 11, '1', 4);
INSERT INTO t_ili2db_attrname VALUES ('Oid23.TestC.ac.a', 'a', 'classc1', 'classa1');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestC.ClassC1', 'classc1');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestA.ClassB1b', 'classb1b');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestC.ac', 'ac');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestA.ClassA1b', 'classa1b');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestA.ClassA1', 'classa1');
INSERT INTO t_ili2db_classname VALUES ('Oid23.TestA.ClassB1', 'classb1');
INSERT INTO t_ili2db_import VALUES (2, 1, '2017-05-08 09:38:34.271', 'postgres', 'test\data\Oid23\Oid1a.xtf');
INSERT INTO t_ili2db_import VALUES (10, 9, '2017-05-08 09:38:44.113', 'postgres', 'test\data\Oid23\Oid1c.xtf');
INSERT INTO t_ili2db_import_basket VALUES (6, 2, 3, 2, 3, 5);
INSERT INTO t_ili2db_import_basket VALUES (13, 10, 11, 1, 11, 12);
INSERT INTO t_ili2db_import_object VALUES (7, 6, 'Oid23.TestA.ClassB1b', 1, 5, 5);
INSERT INTO t_ili2db_import_object VALUES (8, 6, 'Oid23.TestA.ClassA1', 1, 4, 4);
INSERT INTO t_ili2db_import_object VALUES (14, 13, 'Oid23.TestC.ClassC1', 1, 12, 12);
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestA.ClassB1', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestA.ClassA1', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestC.ac', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestA.ClassA1b', 'Oid23.TestA.ClassA1');
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestA.ClassB1b', 'Oid23.TestA.ClassB1');
INSERT INTO t_ili2db_inheritance VALUES ('Oid23.TestC.ClassC1', NULL);
INSERT INTO t_ili2db_model VALUES ('Oid23.ili', '2.3', 'Oid23', 'INTERLIS 2.3;

MODEL Oid23
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2015-12-03" =
    
  TOPIC TestA =
  
    CLASS ClassA1 =
    	OID AS INTERLIS.UUIDOID;
    END ClassA1;
    
    CLASS ClassA1b EXTENDS ClassA1 =
    END ClassA1b;
    
    CLASS ClassB1 =
    END ClassB1;
    
    CLASS ClassB1b EXTENDS ClassB1=
    	OID AS INTERLIS.UUIDOID;
    END ClassB1b;
    
  END TestA;
  
  TOPIC TestC =
  
  	DEPENDS ON TestA;
    
    CLASS ClassC1 =
    END ClassC1;
    
    ASSOCIATION =
      a (EXTERNAL)-- {0..1} Oid23.TestA.ClassA1;
      c -- {0..*} ClassC1;
    END;
    
  END TestC;
  
END Oid23.', '2017-05-08 09:38:34.271');

INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestC.ClassC1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestA.ClassB1b', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestC.ac', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestA.ClassA1b', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Oid23.TestA.ClassB1', 'ch.ehi.ili2db.inheritance', 'newClass');