SET search_path = inheritancesmart1, pg_catalog;

INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassA.attr1', 'topica_classa_attr1', 'topica_structa', 'topica_classa');
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassB.attr3', 'attr3', 'topica_classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassA.attr1', 'topicb_classb_attr1', 'topicb_structa', 'topicb_classb');
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassC.attr4', 'attr4', 'topica_classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassA.attr2', 'attr2', 'topica_classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.StructA.name', 'aname', 'topicb_structa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassC.attr4', 'attr4', 'topicb_classb', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.StructA.name', 'aname', 'topica_structa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassB.attr3', 'attr3', 'topicb_classb', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassA.attr2', 'attr2', 'topicb_classb', NULL);
INSERT INTO t_ili2db_dataset VALUES (1, 'Testset1');
INSERT INTO t_ili2db_basket VALUES (3, 1, 'StructAttr1.TopicA', 'StructAttr1.TopicA', 'StructAttr1a.xtf-3');
INSERT INTO t_ili2db_basket VALUES (15, 1, 'StructAttr1.TopicB', 'StructAttr1.TopicB', 'StructAttr1a.xtf-3');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassC', 'topica_classc');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassA', 'topica_classa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassB', 'topica_classb');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.StructA', 'topica_structa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassA', 'topicb_classa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassB', 'topicb_classb');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassC', 'topicb_classc');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.StructA', 'topicb_structa');
INSERT INTO t_ili2db_import VALUES (2, 1, '2017-05-05 10:03:09.297', 'postgres', 'test\data\InheritanceSmart1\StructAttr1a.xtf');
INSERT INTO t_ili2db_import_basket VALUES (10, 2, 3, 3, 3, 9);
INSERT INTO t_ili2db_import_basket VALUES (20, 2, 15, 2, 15, 19);
INSERT INTO t_ili2db_import_object VALUES (11, 10, 'StructAttr1.TopicA.ClassC', 1, 8, 8);
INSERT INTO t_ili2db_import_object VALUES (12, 10, 'StructAttr1.TopicA.ClassA', 1, 4, 4);
INSERT INTO t_ili2db_import_object VALUES (13, 10, 'StructAttr1.TopicA.ClassB', 1, 6, 6);
INSERT INTO t_ili2db_import_object VALUES (14, 10, 'StructAttr1.TopicA.StructA', 3, 5, 9);
INSERT INTO t_ili2db_import_object VALUES (21, 20, 'StructAttr1.TopicB.ClassB', 1, 16, 16);
INSERT INTO t_ili2db_import_object VALUES (22, 20, 'StructAttr1.TopicB.ClassC', 1, 18, 18);
INSERT INTO t_ili2db_import_object VALUES (23, 20, 'StructAttr1.TopicB.StructA', 2, 17, 19);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassC', 'StructAttr1.TopicB.ClassB');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.StructA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.StructA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassB', 'StructAttr1.TopicA.ClassA');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassC', 'StructAttr1.TopicA.ClassB');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassB', 'StructAttr1.TopicB.ClassA');
INSERT INTO t_ili2db_model VALUES ('StructAttr1.ili', '2.3', 'StructAttr1', 'INTERLIS 2.3;

MODEL StructAttr1 (en) AT "mailto:ce@eisenhutinformatik.ch"
  VERSION "2017-04-18" =

    TOPIC TopicA =
	STRUCTURE StructA =
	 name : MANDATORY TEXT*1024;
	END StructA;
	CLASS ClassA =
		attr1: MANDATORY StructA; 		
		attr2: TEXT*1024;
	END ClassA;
    
    	CLASS ClassB EXTENDS ClassA =
    		attr3 : MANDATORY TEXT*1024;
    	END ClassB;
    	CLASS ClassC EXTENDS ClassB =
    		attr4 : MANDATORY TEXT*1024;
    	END ClassC;
    END TopicA;
    TOPIC TopicB =
	STRUCTURE StructA =
	 name : MANDATORY TEXT*1024;
	END StructA;
	CLASS ClassA (ABSTRACT)=
		attr1: MANDATORY StructA; 		
		attr2: TEXT*1024;
	END ClassA;
    
    	CLASS ClassB EXTENDS ClassA =
    		attr3 : MANDATORY TEXT*1024;
    	END ClassB;
    	CLASS ClassC EXTENDS ClassB =
    		attr4 : MANDATORY TEXT*1024;
    	END ClassC;
    END TopicB;

END StructAttr1.', '2017-05-05 10:03:09.281');

INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassC', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassB', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.StructA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassA', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassB', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassC', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.StructA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO topica_classa VALUES (4, 3, 'topica_classa', 'a1', 'text2', NULL, NULL);
INSERT INTO topica_classa VALUES (6, 3, 'topica_classb', 'a2', 'text2', 'text3', NULL);
INSERT INTO topica_classa VALUES (8, 3, 'topica_classc', 'a3', 'text2', 'text3', 'text4');
INSERT INTO topica_structa VALUES (5, 3, 0, 'Anna', 4);
INSERT INTO topica_structa VALUES (7, 3, 0, 'Berta', 6);
INSERT INTO topica_structa VALUES (9, 3, 0, 'Claudia', 8);
INSERT INTO topicb_classb VALUES (16, 15, 'topicb_classb', 'b2', 'text3', 'text2', NULL);
INSERT INTO topicb_classb VALUES (18, 15, 'topicb_classc', 'b3', 'text3', 'text2', 'text4');
INSERT INTO topicb_structa VALUES (17, 15, 0, 'Berta', 16);
INSERT INTO topicb_structa VALUES (19, 15, 0, 'Claudia', 18);