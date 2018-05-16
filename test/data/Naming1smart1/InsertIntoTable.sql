INSERT INTO naming1smart1.t_ili2db_dataset VALUES (1, 'Testset1');
INSERT INTO naming1smart1.t_ili2db_basket VALUES (3, 1, 'Naming1.TestAttr', '1', 'Naming1a.xtf-3');
INSERT INTO naming1smart1.t_ili2db_basket VALUES (11, 1, 'Naming1.TestClass', '2', 'Naming1a.xtf-3');
INSERT INTO naming1smart1.naming1testclass_classa1 VALUES (13, 11, 'c2', 'attrA''');
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1.Attr1', 'attr11', 'testattr_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestClass.ClassA1.attr1', 'attr1', 'testclass_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1a.attrA', 'attra', 'testattr_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1.attr1', 'attr1', 'testattr_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestClass.Classa1.attrA', 'attra', 'naming1testclass_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_attrname VALUES ('Naming1.TestAttr.ClassA1b.attrA', 'attra1', 'testattr_classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1a', 'testattr_classa1a');
INSERT INTO naming1smart1.t_ili2db_classname VALUES ('Naming1.TestClass.ClassA1', 'testclass_classa1');
INSERT INTO naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1', 'testattr_classa1');
INSERT INTO naming1smart1.t_ili2db_classname VALUES ('Naming1.TestClass.Classa1', 'naming1testclass_classa1');
INSERT INTO naming1smart1.t_ili2db_classname VALUES ('Naming1.TestAttr.ClassA1b', 'testattr_classa1b');
INSERT INTO naming1smart1.t_ili2db_import VALUES (2, 1, '2017-05-02 11:30:29.993', 'postgres', 'test\data\Naming1smart1\Naming1a.xtf');
INSERT INTO naming1smart1.t_ili2db_import_basket VALUES (7, 2, 3, 3, 3, 6);
INSERT INTO naming1smart1.t_ili2db_import_basket VALUES (14, 2, 11, 2, 11, 13);
INSERT INTO naming1smart1.t_ili2db_import_object VALUES (8, 7, 'Naming1.TestAttr.ClassA1a', 1, 5, 5);
INSERT INTO naming1smart1.t_ili2db_import_object VALUES (9, 7, 'Naming1.TestAttr.ClassA1', 1, 4, 4);
INSERT INTO naming1smart1.t_ili2db_import_object VALUES (10, 7, 'Naming1.TestAttr.ClassA1b', 1, 6, 6);
INSERT INTO naming1smart1.t_ili2db_import_object VALUES (15, 14, 'Naming1.TestClass.ClassA1', 1, 12, 12);
INSERT INTO naming1smart1.t_ili2db_import_object VALUES (16, 14, 'Naming1.TestClass.Classa1', 1, 13, 13);
INSERT INTO naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1', NULL);
INSERT INTO naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1a', 'Naming1.TestAttr.ClassA1');
INSERT INTO naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestAttr.ClassA1b', 'Naming1.TestAttr.ClassA1');
INSERT INTO naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestClass.Classa1', NULL);
INSERT INTO naming1smart1.t_ili2db_inheritance VALUES ('Naming1.TestClass.ClassA1', NULL);
INSERT INTO naming1smart1.t_ili2db_model VALUES ('Naming1.ili', '2.3', 'Naming1', 'INTERLIS 2.3;

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
', '2017-05-02 11:30:29.983');

INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO naming1smart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1a', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestClass.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestClass.Classa1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO naming1smart1.t_ili2db_trafo VALUES ('Naming1.TestAttr.ClassA1b', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO naming1smart1.testattr_classa1 VALUES (4, 3, 'testattr_classa1', 'a1', 'a1 first', 'a1 second', NULL, NULL);
INSERT INTO naming1smart1.testattr_classa1 VALUES (5, 3, 'testattr_classa1a', 'a2', 'a2 first', 'a2 second', 'a2', NULL);
INSERT INTO naming1smart1.testattr_classa1 VALUES (6, 3, 'testattr_classa1b', 'a3', 'a3 first', 'a3 second', NULL, 'a3');
INSERT INTO naming1smart1.testclass_classa1 VALUES (12, 11, 'c1', 'attr1');