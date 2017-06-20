SET search_path = translation, pg_catalog;

INSERT INTO t_ili2db_dataset VALUES (1, 'EnumOka');
INSERT INTO t_ili2db_basket VALUES (3, 1, 'EnumOkA.TopicA', 'EnumOkA.Test1', 'EnumOka.xtf-3');
INSERT INTO t_ili2db_basket VALUES (8, 1, 'EnumOkB.TopicB', 'EnumOkB.Test1', 'EnumOka.xtf-3');
INSERT INTO classa VALUES (4, 3, 'o1', NULL);
INSERT INTO classa VALUES (5, 3, 'o2', 'a2.a21');
INSERT INTO classa VALUES (9, 8, 'x1', NULL);
INSERT INTO classa VALUES (10, 8, 'x2', 'a2.a21');
INSERT INTO t_ili2db_attrname VALUES ('EnumOkA.TopicA.ClassA.attrA', 'attra', 'classa', NULL);
INSERT INTO t_ili2db_classname VALUES ('EnumOkA.TopicA.ClassA', 'classa');
INSERT INTO t_ili2db_import VALUES (2, 1, '2017-05-09 10:17:43.835', 'postgres', 'test\data\Translation\EnumOka.xtf');
INSERT INTO t_ili2db_import_basket VALUES (6, 2, 3, 2, 3, 5);
INSERT INTO t_ili2db_import_basket VALUES (11, 2, 8, 2, 8, 10);
INSERT INTO t_ili2db_import_object VALUES (7, 6, 'EnumOkA.TopicA.ClassA', 2, 4, 5);
INSERT INTO t_ili2db_import_object VALUES (12, 11, 'EnumOkA.TopicA.ClassA', 2, 9, 10);
INSERT INTO t_ili2db_inheritance VALUES ('EnumOkA.TopicA.ClassA', NULL);
INSERT INTO t_ili2db_model VALUES ('EnumOk.ili', '2.3', 'EnumOkA EnumOkB', 'INTERLIS 2.3;

MODEL EnumOkA (de) AT "http://www.interlis.ch/ili2c/tests/" VERSION "1" =

	TOPIC TopicA =

		CLASS ClassA =
			attrA : (a1,a2(a21,a22));
		END ClassA;

	END TopicA;

END EnumOkA.

MODEL EnumOkB (fr) AT "http://www.interlis.ch/ili2c/tests/" VERSION "1"
TRANSLATION OF EnumOkA [ "1"]
=

	TOPIC TopicB =

		CLASS ClassB =
			attrB : (b1,b2(b21,b22));
		END ClassB;

	END TopicB;

END EnumOkB.
', '2017-05-09 10:17:43.819');

INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.ver4_translation', 'True');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO t_ili2db_trafo VALUES ('EnumOkA.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');