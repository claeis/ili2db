INSERT INTO dataset1nosmart.t_ili2db_dataset VALUES (1, 'Testset1_a');
INSERT INTO dataset1nosmart.t_ili2db_dataset VALUES (11, 'Testset1_b');
INSERT INTO dataset1nosmart.t_ili2db_basket VALUES (3, 1, 'Dataset1.TestA', 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc', 'Dataset1a1.xtf-3');
INSERT INTO dataset1nosmart.t_ili2db_basket VALUES (13, 11, 'Dataset1.TestA', '2', 'Dataset1b1.xtf-13');
INSERT INTO dataset1nosmart.classa1 VALUES (4, 3, 'classa1', 'a1');
INSERT INTO dataset1nosmart.classa1 VALUES (6, 3, 'classa1b', 'a1');
INSERT INTO dataset1nosmart.classa1 VALUES (14, 13, 'classa1', 'b1');
INSERT INTO dataset1nosmart.classa1 VALUES (16, 13, 'classa1b', 'b1');
INSERT INTO dataset1nosmart.classa1b VALUES (6, 3);
INSERT INTO dataset1nosmart.classa1b VALUES (16, 13);
INSERT INTO dataset1nosmart.structs1 VALUES (5, 3, 'structs1', 0, 4);
INSERT INTO dataset1nosmart.structs1 VALUES (15, 13, 'structs1', 0, 14);
INSERT INTO dataset1nosmart.t_ili2db_attrname VALUES ('Dataset1.TestA.ClassA1.attr1', 'attr1', 'classa1', NULL);
INSERT INTO dataset1nosmart.t_ili2db_attrname VALUES ('Dataset1.TestA.ClassA1.attr2', 'classa1_attr2', 'structs1', 'classa1');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.ClassA1b', 'classa1b');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.StructS1b', 'structs1b');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.ClassA1', 'classa1');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.StructS1', 'structs1');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.ClassB1', 'classb1');
INSERT INTO dataset1nosmart.t_ili2db_classname VALUES ('Dataset1.TestA.ClassB1b', 'classb1b');
INSERT INTO dataset1nosmart.t_ili2db_import VALUES (2, 1, '2017-05-08 12:07:41.345', 'postgres', 'test\data\Dataset1NoSmart\Dataset1a1.xtf');
INSERT INTO dataset1nosmart.t_ili2db_import VALUES (12, 11, '2017-05-08 12:07:51.426', 'postgres', 'test\data\Dataset1NoSmart\Dataset1b1.xtf');
INSERT INTO dataset1nosmart.t_ili2db_import_basket VALUES (7, 2, 3, 2, 3, 6);
INSERT INTO dataset1nosmart.t_ili2db_import_basket VALUES (17, 12, 13, 2, 13, 16);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (8, 7, 'Dataset1.TestA.ClassA1b', 1, 6, 6);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (9, 7, 'Dataset1.TestA.StructS1', 1, 5, 5);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (10, 7, 'Dataset1.TestA.ClassA1', 1, 4, 4);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (18, 17, 'Dataset1.TestA.ClassA1b', 1, 16, 16);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (19, 17, 'Dataset1.TestA.StructS1', 1, 15, 15);
INSERT INTO dataset1nosmart.t_ili2db_import_object VALUES (20, 17, 'Dataset1.TestA.ClassA1', 1, 14, 14);
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.ClassA1', NULL);
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.ClassB1', NULL);
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.StructS1b', 'Dataset1.TestA.StructS1');
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.ClassB1b', 'Dataset1.TestA.ClassB1');
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.ClassA1b', 'Dataset1.TestA.ClassA1');
INSERT INTO dataset1nosmart.t_ili2db_inheritance VALUES ('Dataset1.TestA.StructS1', NULL);
INSERT INTO dataset1nosmart.t_ili2db_model VALUES ('Dataset1.ili', '2.3', 'Dataset1', 'INTERLIS 2.3;

MODEL Dataset1
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-07-07" =
    
  TOPIC TestA =

    STRUCTURE StructS1 =
    END StructS1;

    STRUCTURE StructS1b EXTENDS StructS1 =
    END StructS1b;
    
    CLASS ClassA1 =
    	attr1 : TEXT*60;
    	attr2 : StructS1;
    END ClassA1;
    CLASS ClassA1b EXTENDS ClassA1 =
    END ClassA1b;
    CLASS ClassB1 =
    END ClassB1;
    CLASS ClassB1b EXTENDS ClassB1=
    END ClassB1b;
    
  END TestA;
  
  
END Dataset1.
', '2017-05-08 12:07:41.345');

INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO dataset1nosmart.t_ili2db_settings VALUES ('ch.ehi.ili2db.importTabs', 'simple');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.ClassA1b', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.StructS1b', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.StructS1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.ClassB1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO dataset1nosmart.t_ili2db_trafo VALUES ('Dataset1.TestA.ClassB1b', 'ch.ehi.ili2db.inheritance', 'newClass');