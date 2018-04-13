INSERT INTO translation.t_ili2db_dataset VALUES (1, 'ModelAsimple10');
INSERT INTO translation.t_ili2db_dataset VALUES (12, 'ModelBsimple10');
INSERT INTO translation.t_ili2db_basket VALUES (3, 1, 'ModelAsimple10.TopicA', 'ModelAsimple10.TopicA', 'ModelAsimple10a.itf-3');
INSERT INTO translation.t_ili2db_basket VALUES (14, 12, 'ModelBsimple10.TopicB', 'ModelBsimple10.TopicB', 'ModelBsimple10a.itf-14');
INSERT INTO translation.classa VALUES (4, 3, '10', 'o10');
INSERT INTO translation.classa VALUES (5, 3, '11', 'o11');
INSERT INTO translation.classa VALUES (15, 14, '21', 'o21');
INSERT INTO translation.classa VALUES (16, 14, '20', 'o20');
INSERT INTO translation.classa2 VALUES (6, 3, '12', '010A000020155500000100000001090000000100000001020000000500000000000000004C1D41000000000017F14000000000004C1D4100000000A017F14000000000284C1D4100000000A017F14000000000284C1D41000000000017F14000000000004C1D41000000000017F140');
INSERT INTO translation.classa2 VALUES (17, 14, '22', '010A000020155500000100000001090000000100000001020000000500000000000000004C1D41000000000017F14000000000004C1D4100000000A017F14000000000284C1D4100000000A017F14000000000284C1D41000000000017F14000000000004C1D41000000000017F140');
INSERT INTO translation.classa3 VALUES (7, 3, '15', '010A000020155500000100000001090000000100000001020000000500000000000000004C1D41000000000017F14000000000004C1D4100000000A017F14000000000284C1D4100000000A017F14000000000284C1D41000000000017F14000000000004C1D41000000000017F140');
INSERT INTO translation.classa3 VALUES (18, 14, '25', '010A000020155500000100000001090000000100000001020000000500000000000000004C1D41000000000017F14000000000004C1D4100000000A017F14000000000284C1D4100000000A017F14000000000284C1D41000000000017F14000000000004C1D41000000000017F140');
INSERT INTO translation.t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA.attrA', 'attra', 'classa', NULL);
INSERT INTO translation.t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA3.geomA', 'geoma', 'classa3', NULL);
INSERT INTO translation.t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA2.geomA', 'geoma', 'classa2', NULL);
INSERT INTO translation.t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA', 'classa');
INSERT INTO translation.t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA3', 'classa3');
INSERT INTO translation.t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA2', 'classa2');
INSERT INTO translation.t_ili2db_import VALUES (2, 1, '2017-05-09 12:06:35.717', 'postgres', 'test\data\Translation\ModelAsimple10a.itf');
INSERT INTO translation.t_ili2db_import VALUES (13, 12, '2017-05-09 12:06:43.69', 'postgres', 'test\data\Translation\ModelBsimple10a.itf');
INSERT INTO translation.t_ili2db_import_basket VALUES (8, 2, 3, 4, 3, 7);
INSERT INTO translation.t_ili2db_import_basket VALUES (19, 13, 14, 4, 14, 18);
INSERT INTO translation.t_ili2db_import_object VALUES (9, 8, 'ModelAsimple10.TopicA.ClassA', 2, 4, 5);
INSERT INTO translation.t_ili2db_import_object VALUES (10, 8, 'ModelAsimple10.TopicA.ClassA3', 1, 7, 7);
INSERT INTO translation.t_ili2db_import_object VALUES (11, 8, 'ModelAsimple10.TopicA.ClassA2', 1, 6, 6);
INSERT INTO translation.t_ili2db_import_object VALUES (20, 19, 'ModelAsimple10.TopicA.ClassA', 2, 15, 16);
INSERT INTO translation.t_ili2db_import_object VALUES (21, 19, 'ModelAsimple10.TopicA.ClassA3', 1, 18, 18);
INSERT INTO translation.t_ili2db_import_object VALUES (22, 19, 'ModelAsimple10.TopicA.ClassA2', 1, 17, 17);
INSERT INTO translation.t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA3', NULL);
INSERT INTO translation.t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA2', NULL);
INSERT INTO translation.t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA', NULL);
INSERT INTO translation.t_ili2db_model VALUES ('ModelBsimple10.ili', '1.0', 'ModelBsimple10', 'TRANSFER ModelB;

MODEL ModelBsimple10 
	DOMAIN
		LkoordB = COORD2 480000.000 70000.000 850000.000 310000.000;
	TOPIC TopicB =

		TABLE ClassB =
			attrB : OPTIONAL TEXT*10;
			NO IDENT
		END ClassB;

		TABLE ClassB2 =
			geomB : OPTIONAL SURFACE WITH (STRAIGHTS,ARCS) VERTEX LkoordB WITHOUT OVERLAPS > 0.1;
			NO IDENT
		END ClassB2;

		TABLE ClassB3 =
			geomB : OPTIONAL AREA WITH (STRAIGHTS,ARCS) VERTEX LkoordB WITHOUT OVERLAPS > 0.1;
			NO IDENT
		END ClassB3;
		
	END TopicB.

END ModelBsimple10.


FORMAT FREE;
CODE BLANK = DEFAULT, UNDEFINED = DEFAULT, CONTINUE = DEFAULT;
TID = ANY;
END.

', '2017-05-09 12:06:35.701');
INSERT INTO translation.t_ili2db_model VALUES ('ModelAsimple10.ili', '1.0', 'ModelAsimple10', 'TRANSFER ModelA;

MODEL ModelAsimple10
	DOMAIN
		LkoordA = COORD2 480000.000 70000.000 850000.000 310000.000;
	TOPIC TopicA =

		TABLE ClassA =
			attrA : OPTIONAL TEXT*10;
			NO IDENT
		END ClassA;
		TABLE ClassA2 =
			geomA : OPTIONAL SURFACE WITH (STRAIGHTS,ARCS) VERTEX LkoordA WITHOUT OVERLAPS > 0.1;
			NO IDENT
		END ClassA2;

		TABLE ClassA3 =
			geomA : OPTIONAL AREA WITH (STRAIGHTS,ARCS) VERTEX LkoordA WITHOUT OVERLAPS > 0.1;
			NO IDENT
		END ClassA3;

	END TopicA.

END ModelAsimple10.

FORMAT FREE;
CODE BLANK = DEFAULT, UNDEFINED = DEFAULT, CONTINUE = DEFAULT;
TID = ANY;
END.

', '2017-05-09 12:06:35.701');

INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.ili1translation', 'ModelBsimple10=ModelAsimple10');
INSERT INTO translation.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO translation.t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO translation.t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA3', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO translation.t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA2', 'ch.ehi.ili2db.inheritance', 'newClass');