INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.ClassB.struct', 'topica_classb_struct', 'topica_structa1', 'topica_classb');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.StructA0.ref', 'ref_topica_classa1', 'topica_structa2', 'topica_classa1');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.StructA0.ref', 'ref_topica_classa1', 'topica_structa1', 'topica_classa1');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.ClassC.struct', 'topica_classc_struct', 'topica_structa1', 'topica_classc');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.StructA0.ref', 'ref_topica_classa2', 'topica_structa1', 'topica_classa2');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.StructA0.ref', 'ref_topica_classa2', 'topica_structa2', 'topica_classa2');
INSERT INTO inheritancesmart1.t_ili2db_attrname VALUES ('RefAttr1.TopicA.ClassD.struct', 'topica_classd_struct', 'topica_structa2', 'topica_classd');
INSERT INTO inheritancesmart1.t_ili2db_dataset VALUES (1, 'Testset1');
INSERT INTO inheritancesmart1.t_ili2db_basket VALUES (3, 1, 'RefAttr1.TopicA', 'RefAttr1.TopicA', 'RefAttr1a.xtf-3');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassC', 'topica_classc');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassD', 'topica_classd');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassB', 'topica_classb');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.StructA2', 'topica_structa2');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.StructA1', 'topica_structa1');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassA2', 'topica_classa2');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.StructA0', 'topica_structa0');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassA11', 'topica_classa11');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassA0', 'topica_classa0');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.ClassA1', 'topica_classa1');
INSERT INTO inheritancesmart1.t_ili2db_classname VALUES ('RefAttr1.TopicA.StructA11', 'topica_structa11');
INSERT INTO inheritancesmart1.t_ili2db_import VALUES (2, 1, '2017-05-05 10:07:08.652', 'postgres', 'test\data\InheritanceSmart1\RefAttr1a.xtf');
INSERT INTO inheritancesmart1.t_ili2db_import_basket VALUES (18, 2, 3, 8, 3, 17);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (19, 18, 'RefAttr1.TopicA.ClassC', 1, 13, 13);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (20, 18, 'RefAttr1.TopicA.ClassD', 1, 16, 16);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (21, 18, 'RefAttr1.TopicA.ClassB', 3, 7, 11);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (22, 18, 'RefAttr1.TopicA.StructA2', 1, 17, 17);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (23, 18, 'RefAttr1.TopicA.StructA1', 4, 8, 15);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (24, 18, 'RefAttr1.TopicA.ClassA2', 1, 6, 6);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (25, 18, 'RefAttr1.TopicA.ClassA11', 1, 5, 5);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (26, 18, 'RefAttr1.TopicA.ClassA1', 1, 4, 4);
INSERT INTO inheritancesmart1.t_ili2db_import_object VALUES (27, 18, 'RefAttr1.TopicA.StructA11', 1, 12, 12);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.StructA2', 'RefAttr1.TopicA.StructA0');
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassC', NULL);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassA0', NULL);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassD', NULL);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassA11', 'RefAttr1.TopicA.ClassA1');
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassB', NULL);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.StructA0', NULL);
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassA2', 'RefAttr1.TopicA.ClassA0');
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.StructA1', 'RefAttr1.TopicA.StructA0');
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.StructA11', 'RefAttr1.TopicA.StructA1');
INSERT INTO inheritancesmart1.t_ili2db_inheritance VALUES ('RefAttr1.TopicA.ClassA1', 'RefAttr1.TopicA.ClassA0');
INSERT INTO inheritancesmart1.t_ili2db_model VALUES ('RefAttr1.ili', '2.3', 'RefAttr1', 'INTERLIS 2.3;

MODEL RefAttr1 (en) AT "mailto:ce@eisenhutinformatik.ch"
  VERSION "2017-04-20" =

    TOPIC TopicA =
	CLASS ClassA0 (ABSTRACT) =
	END ClassA0;
	
	CLASS ClassA1 EXTENDS ClassA0=
	END ClassA1;

	CLASS ClassA11 EXTENDS ClassA1=
	END ClassA11;
	
	CLASS ClassA2 EXTENDS ClassA0=
	END ClassA2;
	
	STRUCTURE StructA0 (ABSTRACT) =
	 ref : REFERENCE TO ClassA0;
	END StructA0;
	
	STRUCTURE StructA1 EXTENDS StructA0 =
	 ref (EXTENDED) : REFERENCE TO ClassA1;
	END StructA1;

	STRUCTURE StructA11 EXTENDS StructA1 =
	 ref (EXTENDED) : REFERENCE TO ClassA11;
	END StructA11;
	
	STRUCTURE StructA2 EXTENDS StructA0 =
	 ref (EXTENDED) : REFERENCE TO ClassA2;
	END StructA2;
	
	CLASS ClassB =
		struct: MANDATORY StructA1; 		
	END ClassB;

	CLASS ClassC =
		struct: BAG {0..*} OF StructA1; 		
	END ClassC;
	
	CLASS ClassD =
		struct: MANDATORY StructA2; 		
	END ClassD;

    END TopicA;

END RefAttr1.', '2017-05-05 10:07:08.637');

INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO inheritancesmart1.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassC', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassD', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassB', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.StructA2', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.StructA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassA2', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.StructA0', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassA11', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassA0', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO inheritancesmart1.t_ili2db_trafo VALUES ('RefAttr1.TopicA.StructA11', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO inheritancesmart1.topica_classa1 VALUES (4, 3, 'topica_classa1', 'a1.1');
INSERT INTO inheritancesmart1.topica_classa1 VALUES (5, 3, 'topica_classa11', 'a11.1');
INSERT INTO inheritancesmart1.topica_classa2 VALUES (6, 3, 'a2.1');
INSERT INTO inheritancesmart1.topica_classb VALUES (7, 3, 'b.1');
INSERT INTO inheritancesmart1.topica_classb VALUES (9, 3, 'b.2');
INSERT INTO inheritancesmart1.topica_classb VALUES (11, 3, 'b.3');
INSERT INTO inheritancesmart1.topica_classc VALUES (13, 3, 'c.1');
INSERT INTO inheritancesmart1.topica_classd VALUES (16, 3, 'd.1');
INSERT INTO inheritancesmart1.topica_structa1 VALUES (8, 3, 'topica_structa1', 0, NULL, 4, 7, NULL);
INSERT INTO inheritancesmart1.topica_structa1 VALUES (10, 3, 'topica_structa1', 0, NULL, 5, 9, NULL);
INSERT INTO inheritancesmart1.topica_structa1 VALUES (12, 3, 'topica_structa11', 0, NULL, 5, 11, NULL);
INSERT INTO inheritancesmart1.topica_structa1 VALUES (14, 3, 'topica_structa1', 0, NULL, 4, NULL, 13);
INSERT INTO inheritancesmart1.topica_structa1 VALUES (15, 3, 'topica_structa1', 1, NULL, 5, NULL, 13);
INSERT INTO inheritancesmart1.topica_structa2 VALUES (17, 3, 0, 6, NULL, 16);