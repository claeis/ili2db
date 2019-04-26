
SET search_path = inheritancenosmart, pg_catalog;

INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.StructA.name', 'aname', 'topicb_structa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassB.attr3', 'attr3', 'topicb_classb', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassA.attr1', 'topicb_classa_attr1', 'topicb_structa', 'topicb_classa');
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassA.attr2', 'attr2', 'topicb_classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassA.attr1', 'topica_classa_attr1', 'topica_structa', 'topica_classa');
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassC.attr4', 'attr4', 'topica_classc', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassB.attr3', 'attr3', 'topica_classb', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.StructA.name', 'aname', 'topica_structa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicA.ClassA.attr2', 'attr2', 'topica_classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('StructAttr1.TopicB.ClassC.attr4', 'attr4', 'topicb_classc', NULL);


INSERT INTO t_ili2db_basket VALUES (2, 1, 'StructAttr1.TopicA', NULL, 'StructAttr1a.xtf-2', '');
INSERT INTO t_ili2db_basket VALUES (9, 1, 'StructAttr1.TopicB', NULL, 'StructAttr1a.xtf-2', '');


INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.StructA', 'topicb_structa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassB', 'topica_classb');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassA', 'topica_classa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassA', 'topicb_classa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassB', 'topicb_classb');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.StructA', 'topica_structa');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicA.ClassC', 'topica_classc');
INSERT INTO t_ili2db_classname VALUES ('StructAttr1.TopicB.ClassC', 'topicb_classc');

INSERT INTO t_ili2db_column_prop VALUES ('topicb_structa', NULL, 'topicb_classa_attr1', 'ch.ehi.ili2db.foreignKey', 'topicb_classa');
INSERT INTO t_ili2db_column_prop VALUES ('topicb_classa', NULL, 'T_Type', 'ch.ehi.ili2db.types', '["topicb_classb","topicb_classc"]');
INSERT INTO t_ili2db_column_prop VALUES ('topicb_classb', NULL, 'T_Id', 'ch.ehi.ili2db.foreignKey', 'topicb_classa');
INSERT INTO t_ili2db_column_prop VALUES ('topica_classb', NULL, 'T_Id', 'ch.ehi.ili2db.foreignKey', 'topica_classa');
INSERT INTO t_ili2db_column_prop VALUES ('topica_classa', NULL, 'T_Type', 'ch.ehi.ili2db.types', '["topica_classa","topica_classb","topica_classc"]');
INSERT INTO t_ili2db_column_prop VALUES ('topica_classc', NULL, 'T_Id', 'ch.ehi.ili2db.foreignKey', 'topica_classb');
INSERT INTO t_ili2db_column_prop VALUES ('topicb_classc', NULL, 'T_Id', 'ch.ehi.ili2db.foreignKey', 'topicb_classb');
INSERT INTO t_ili2db_column_prop VALUES ('topica_structa', NULL, 'topica_classa_attr1', 'ch.ehi.ili2db.foreignKey', 'topica_classa');

INSERT INTO t_ili2db_dataset VALUES (1, 'Testset1');

INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassB', 'StructAttr1.TopicB.ClassA');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassB', 'StructAttr1.TopicA.ClassA');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.StructA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.StructA', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicB.ClassC', 'StructAttr1.TopicB.ClassB');
INSERT INTO t_ili2db_inheritance VALUES ('StructAttr1.TopicA.ClassC', 'StructAttr1.TopicA.ClassB');


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

END StructAttr1.', '2019-04-26 11:58:12.229');


SELECT pg_catalog.setval('t_ili2db_seq', 13, true);


INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.jsonTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-developer-workspace');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multiPointTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multiLineTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');


INSERT INTO t_ili2db_table_prop VALUES ('topicb_structa', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO t_ili2db_table_prop VALUES ('topica_structa', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');

INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.StructA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassB', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassB', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.StructA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicA.ClassC', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('StructAttr1.TopicB.ClassC', 'ch.ehi.ili2db.inheritance', 'newClass');

INSERT INTO topica_classa VALUES (3, 2, 'topica_classa', 'a1', 'text2');
INSERT INTO topica_classa VALUES (5, 2, 'topica_classb', 'a2', 'text2');
INSERT INTO topica_classa VALUES (7, 2, 'topica_classc', 'a3', 'text2');


INSERT INTO topica_classb VALUES (5, 2, 'text3');
INSERT INTO topica_classb VALUES (7, 2, 'text3');


INSERT INTO topica_classc VALUES (7, 2, 'text4');


INSERT INTO topica_structa VALUES (4, 2, 0, 'Anna', 3);
INSERT INTO topica_structa VALUES (6, 2, 0, 'Berta', 5);
INSERT INTO topica_structa VALUES (8, 2, 0, 'Claudia', 7);

INSERT INTO topicb_classa VALUES (10, 9, 'topicb_classb', 'b2', 'text2');
INSERT INTO topicb_classa VALUES (12, 9, 'topicb_classc', 'b3', 'text2');

INSERT INTO topicb_classb VALUES (10, 9, 'text3');
INSERT INTO topicb_classb VALUES (12, 9, 'text3');

INSERT INTO topicb_classc VALUES (12, 9, 'text4');

INSERT INTO topicb_structa VALUES (11, 9, 0, 'Berta', 10);
INSERT INTO topicb_structa VALUES (13, 9, 0, 'Claudia', 12);

