SET search_path = translation, pg_catalog;

INSERT INTO t_ili2db_dataset VALUES (1, 'ModelAsimple10');
INSERT INTO t_ili2db_dataset VALUES (14, 'ModelBsimple10');
INSERT INTO t_ili2db_basket VALUES (3, 1, 'ModelAsimple10.TopicA', 'ModelAsimple10.TopicA', 'ModelAsimple10a.itf-3');
INSERT INTO t_ili2db_basket VALUES (16, 14, 'ModelBsimple10.TopicB', 'ModelBsimple10.TopicB', 'ModelBsimple10a.itf-16');
INSERT INTO classa VALUES (4, 3, '10', 'o10');
INSERT INTO classa VALUES (5, 3, '11', 'o11');
INSERT INTO classa VALUES (17, 16, '21', 'o21');
INSERT INTO classa VALUES (18, 16, '20', 'o20');
INSERT INTO classa2 VALUES (6, 3, '12');
INSERT INTO classa2 VALUES (19, 16, '22');
INSERT INTO classa2_geoma VALUES (7, '13', 3, 6, '0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140');
INSERT INTO classa2_geoma VALUES (20, '23', 16, 19, '0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140');
INSERT INTO classa3 VALUES (9, 3, '15', '01010000201555000000000000144C1D41000000005017F140');
INSERT INTO classa3 VALUES (22, 16, '25', '01010000201555000000000000144C1D41000000005017F140');
INSERT INTO classa3_geoma VALUES (8, '14', 3, '0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140');
INSERT INTO classa3_geoma VALUES (21, '24', 16, '0109000020155500000100000001020000000500000000000000004C1D41000000000017F14000000000284C1D41000000000017F14000000000284C1D4100000000A017F14000000000004C1D4100000000A017F14000000000004C1D41000000000017F140');
INSERT INTO t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA2.geomA._geom', '_geom', 'classa2_geoma', NULL);
INSERT INTO t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA.attrA', 'attra', 'classa', NULL);
INSERT INTO t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA3.geomA', 'geoma', 'classa3', NULL);
INSERT INTO t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA3.geomA._geom', '_geom', 'classa3_geoma', NULL);
INSERT INTO t_ili2db_attrname VALUES ('ModelAsimple10.TopicA.ClassA2.geomA._ref', '_ref', 'classa2_geoma', NULL);
INSERT INTO t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA2.geomA', 'classa2_geoma');
INSERT INTO t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA', 'classa');
INSERT INTO t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA3.geomA', 'classa3_geoma');
INSERT INTO t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA3', 'classa3');
INSERT INTO t_ili2db_classname VALUES ('ModelAsimple10.TopicA.ClassA2', 'classa2');
INSERT INTO t_ili2db_import VALUES (2, 1, '2017-05-09 14:16:38.014', 'postgres', 'test\data\Translation\ModelAsimple10a.itf');
INSERT INTO t_ili2db_import VALUES (15, 14, '2017-05-09 14:16:46.688', 'postgres', 'test\data\Translation\ModelBsimple10a.itf');
INSERT INTO t_ili2db_import_basket VALUES (10, 2, 3, 6, 3, 9);
INSERT INTO t_ili2db_import_basket VALUES (23, 15, 16, 6, 16, 22);
INSERT INTO t_ili2db_import_object VALUES (11, 10, 'ModelAsimple10.TopicA.ClassA', 2, 4, 5);
INSERT INTO t_ili2db_import_object VALUES (12, 10, 'ModelAsimple10.TopicA.ClassA3', 1, 9, 9);
INSERT INTO t_ili2db_import_object VALUES (13, 10, 'ModelAsimple10.TopicA.ClassA2', 1, 6, 6);
INSERT INTO t_ili2db_import_object VALUES (24, 23, 'ModelAsimple10.TopicA.ClassA', 2, 17, 18);
INSERT INTO t_ili2db_import_object VALUES (25, 23, 'ModelAsimple10.TopicA.ClassA3', 1, 22, 22);
INSERT INTO t_ili2db_import_object VALUES (26, 23, 'ModelAsimple10.TopicA.ClassA2', 1, 19, 19);
INSERT INTO t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA3', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA2', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('ModelAsimple10.TopicA.ClassA', NULL);
INSERT INTO t_ili2db_model VALUES ('ModelBsimple10.ili', '1.0', 'ModelBsimple10', 'TRANSFER ModelB;

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

', '2017-05-09 14:16:37.935');
INSERT INTO t_ili2db_model VALUES ('ModelAsimple10.ili', '1.0', 'ModelAsimple10', 'TRANSFER ModelA;

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

', '2017-05-09 14:16:37.935');

INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.AreaRef', 'keep');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.doItfLineTables', 'True');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.ili1translation', 'ModelBsimple10=ModelAsimple10');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA3', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('ModelAsimple10.TopicA.ClassA2', 'ch.ehi.ili2db.inheritance', 'newClass');