INSERT INTO datatypes10.t_ili2db_dataset VALUES (1, NULL);
INSERT INTO datatypes10.t_ili2db_basket VALUES (3, 1, 'Datatypes10.Topic', 'itf0', 'Datatypes10a.itf-3');
INSERT INTO datatypes10.othertable VALUES (6, 3, '10', 'Other10');
INSERT INTO datatypes10.othertable VALUES (7, 3, '11', 'Other11');
INSERT INTO datatypes10.tablea VALUES (4, 3, '10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'obj10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO datatypes10.tablea VALUES (5, 3, '11', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'obj11', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO datatypes10.subtable VALUES (8, 3, '30', 5);
INSERT INTO datatypes10.subtable VALUES (9, 3, '31', 4);
INSERT INTO datatypes10.subtable VALUES (10, 3, '32', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.grads', 'grads', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.OtherTable.otherAttr', 'otherattr', 'othertable', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.radians', 'radians', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.bereichInt', 'bereichint', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.surface', 'surface', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.koord2', 'koord2', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.degrees', 'degrees', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.linientyp', 'linientyp', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.area', 'area', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.dim1', 'dim1', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.bereich', 'bereich', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.datum', 'datum', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.horizAlignment', 'horizalignment', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.SubTablemain.main', 'main', 'subtable', 'tablea');
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.dim2', 'dim2', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.text', 'atext', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.aufzaehlung', 'aufzaehlung', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.koord3', 'koord3', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_attrname VALUES ('Datatypes10.Topic.TableA.vertAlignment', 'vertalignment', 'tablea', NULL);
INSERT INTO datatypes10.t_ili2db_classname VALUES ('Datatypes10.Topic.SubTablemain', 'subtablemain');
INSERT INTO datatypes10.t_ili2db_classname VALUES ('Datatypes10.Topic.SubTable', 'subtable');
INSERT INTO datatypes10.t_ili2db_classname VALUES ('Datatypes10.Topic.LineAttrib1', 'lineattrib1');
INSERT INTO datatypes10.t_ili2db_classname VALUES ('Datatypes10.Topic.TableA', 'tablea');
INSERT INTO datatypes10.t_ili2db_classname VALUES ('Datatypes10.Topic.OtherTable', 'othertable');
INSERT INTO datatypes10.t_ili2db_import VALUES (2, 1, '2017-05-02 13:30:18.492', 'postgres', 'test\data\Datatypes10\Datatypes10a.itf');
INSERT INTO datatypes10.t_ili2db_import_basket VALUES (11, 2, 3, 7, 3, 10);
INSERT INTO datatypes10.t_ili2db_import_object VALUES (12, 11, 'Datatypes10.Topic.SubTable', 3, 8, 10);
INSERT INTO datatypes10.t_ili2db_import_object VALUES (13, 11, 'Datatypes10.Topic.TableA', 2, 4, 5);
INSERT INTO datatypes10.t_ili2db_import_object VALUES (14, 11, 'Datatypes10.Topic.OtherTable', 2, 6, 7);
INSERT INTO datatypes10.t_ili2db_inheritance VALUES ('Datatypes10.Topic.SubTable', NULL);
INSERT INTO datatypes10.t_ili2db_inheritance VALUES ('Datatypes10.Topic.OtherTable', NULL);
INSERT INTO datatypes10.t_ili2db_inheritance VALUES ('Datatypes10.Topic.SubTablemain', NULL);
INSERT INTO datatypes10.t_ili2db_inheritance VALUES ('Datatypes10.Topic.TableA', NULL);
INSERT INTO datatypes10.t_ili2db_model VALUES ('Datatypes10.ili', '1.0', 'Datatypes10', 'TRANSFER Ili1FmtTest;

MODEL Datatypes10
	TOPIC Topic=
		DOMAIN Lkoord = COORD2 1.00 100.0 9.99 999.9;
		TABLE TableA =
			!! Koord2
			koord2 : OPTIONAL COORD2 1.00 100.0 9.99 999.9;
			!! Koord3
			koord3 : OPTIONAL COORD3 1.00 100.0 1000 9.99 999.9 9999;
			!! Laenge
			dim1 : OPTIONAL DIM1 1.0 9.9;
			!! Flaechenmass
			dim2 : OPTIONAL DIM2 -1.0 9.9;
			!! Winkel
			radians : OPTIONAL RADIANS 0.0 6.2;
			grads : OPTIONAL GRADS 0.0 399.9;
			degrees : OPTIONAL DEGREES 0.0 359.9;
			!! Bereich
			bereich : OPTIONAL [0.0 .. 9.9 ];
			bereichInt : OPTIONAL [0 .. 9 ];
			!! Text
			text : OPTIONAL TEXT*10;
			!! Datum
			datum : OPTIONAL DATE;
			!! Aufzaehlung
			aufzaehlung : OPTIONAL (null, eins, zwei, drei, mehr ( vier, fuenf, sechs, sieben, acht ,neun, zehn)); !! == zwei Stellen
			!! HorizAlignment
			horizAlignment : OPTIONAL HALIGNMENT;
			!! VertAlignment
			vertAlignment : OPTIONAL VALIGNMENT;
			!! Linientyp
			linientyp : OPTIONAL POLYLINE WITH (STRAIGHTS) VERTEX Lkoord;
			!! Flaechentyp
			surface : OPTIONAL SURFACE WITH (STRAIGHTS,ARCS) VERTEX Lkoord;
			area : OPTIONAL AREA WITH (STRAIGHTS,ARCS) VERTEX Lkoord WITHOUT OVERLAPS > 0.0
					LINEATTR =
						attr : TEXT*20;
					END;
		NO IDENT
		END TableA;
		TABLE OtherTable =
			otherAttr : OPTIONAL TEXT*30;
		NO IDENT
		END OtherTable;
		TABLE SubTable =
			!! BezAttribut
			main : OPTIONAL -> TableA;
		NO IDENT
		END SubTable;
	END Topic.
END Datatypes10.

FORMAT FREE;
CODE BLANK = DEFAULT, UNDEFINED = DEFAULT, CONTINUE = DEFAULT;
TID = ANY;
END.
', '2017-05-02 13:30:18.477');

INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO datatypes10.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO datatypes10.t_ili2db_trafo VALUES ('Datatypes10.Topic.SubTablemain', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO datatypes10.t_ili2db_trafo VALUES ('Datatypes10.Topic.SubTable', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO datatypes10.t_ili2db_trafo VALUES ('Datatypes10.Topic.LineAttrib1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO datatypes10.t_ili2db_trafo VALUES ('Datatypes10.Topic.TableA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO datatypes10.t_ili2db_trafo VALUES ('Datatypes10.Topic.OtherTable', 'ch.ehi.ili2db.inheritance', 'newClass');