INSERT INTO array23.t_ili2db_dataset VALUES (1, 'Array23a.xtf-1');
INSERT INTO array23.t_ili2db_basket VALUES (3, 1, 'Array23.TestA', 'Array23.TestA', 'Array23a.xtf-3');
INSERT INTO array23.auto VALUES (4, 3, '13', '{Rot,Blau}');
INSERT INTO array23.auto VALUES (5, 3, '14', NULL);
INSERT INTO array23.datatypes VALUES (6, 3, '100', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO array23.datatypes VALUES (7, 3, '101', '{15b6bcce-8772-4595-bf82-f727a665fbf3}', '{t}', '{09:00:00}', '{2002-09-24}', '{"1900-01-01 12:30:05"}', '{5}', '{6.0}');
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.aDateTime', 'adatetime', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.numericDec', 'numericdec', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Auto.Farben', 'farben', 'auto', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.ADate_.Value', 'avalue', 'adate_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.NumericInt_.Value', 'avalue', 'numericint_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.numericInt', 'numericint', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Farbe.Wert', 'wert', 'farbe', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.aTime', 'atime', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.ADateTime_.Value', 'avalue', 'adatetime_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.AUuid_.Value', 'avalue', 'auuid_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.ABoolean_.Value', 'avalue', 'aboolean_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Binbox_.Value', 'avalue', 'binbox_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.NumericDec_.Value', 'avalue', 'numericdec_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Xmlbox_.Value', 'avalue', 'xmlbox_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.aBoolean', 'aboolean', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.ATime_.Value', 'avalue', 'atime_', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.aUuid', 'auuid', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_attrname VALUES ('Array23.TestA.Datatypes.aDate', 'adate', 'datatypes', NULL);
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.Binbox_', 'binbox_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.Xmlbox_', 'xmlbox_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.Farbe', 'farbe');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.Datatypes', 'datatypes');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.Auto', 'auto');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.AUuid_', 'auuid_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.ABoolean_', 'aboolean_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.NumericDec_', 'numericdec_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.ATime_', 'atime_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.NumericInt_', 'numericint_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.ADateTime_', 'adatetime_');
INSERT INTO array23.t_ili2db_classname VALUES ('Array23.TestA.ADate_', 'adate_');
INSERT INTO array23.t_ili2db_import VALUES (2, 1, '2018-01-12 11:47:17.613', 'postgres', 'test\data\Array\Array23a.xtf');
INSERT INTO array23.t_ili2db_import_basket VALUES (8, 2, 3, 4, 3, 7);
INSERT INTO array23.t_ili2db_import_object VALUES (9, 8, 'Array23.TestA.Datatypes', 2, 6, 7);
INSERT INTO array23.t_ili2db_import_object VALUES (10, 8, 'Array23.TestA.Auto', 2, 4, 5);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.Datatypes', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.ABoolean_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.ADateTime_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.ADate_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.ATime_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.NumericInt_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.Binbox_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.Auto', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.Xmlbox_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.AUuid_', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.Farbe', NULL);
INSERT INTO array23.t_ili2db_inheritance VALUES ('Array23.TestA.NumericDec_', NULL);
INSERT INTO array23.t_ili2db_model VALUES ('Array23.ili', '2.3', 'Array23', 'INTERLIS 2.3;

MODEL Array23
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-09-17" =
	
  TOPIC TestA =
  	DOMAIN
		RGB = (Rot, Blau, Gruen);
		ABoolean = BOOLEAN;
		NumericInt = 0 .. 10;
		NumericDec = 0.0 .. 10.0;
		Binbox = BLACKBOX BINARY;
		Xmlbox = BLACKBOX XML;
  	 
	STRUCTURE Farbe =
	   Wert: RGB;
	END Farbe;
	CLASS Auto =
	  !!@ili2db.mapping=ARRAY
	  Farben: LIST {0..*} OF Farbe;
	END Auto;    

		STRUCTURE AUuid_ =
			Value : INTERLIS.UUIDOID;
		END AUuid_;
		STRUCTURE ABoolean_ =
			Value: ABoolean;
		END ABoolean_;
		STRUCTURE ATime_ =
			Value: INTERLIS.XMLTime;
		END ATime_;
		STRUCTURE ADate_ =
			Value: INTERLIS.XMLDate;
		END ADate_;
		STRUCTURE ADateTime_ =
			Value: INTERLIS.XMLDateTime;
		END ADateTime_;
		STRUCTURE NumericInt_ =
			Value: NumericInt;
		END NumericInt_;
		STRUCTURE NumericDec_ =
			Value: NumericDec;
		END NumericDec_;
		STRUCTURE Binbox_ =
			Value: Binbox;
		END Binbox_;
		STRUCTURE Xmlbox_ =
			Value: Xmlbox;
		END Xmlbox_;

	CLASS Datatypes =
	  	!!@ili2db.mapping=ARRAY
		aUuid : LIST OF AUuid_;
	  	!!@ili2db.mapping=ARRAY
		aBoolean : LIST OF ABoolean_;
	  	!!@ili2db.mapping=ARRAY
		aTime : LIST OF ATime_;
	  	!!@ili2db.mapping=ARRAY
		aDate : LIST OF ADate_;
	  	!!@ili2db.mapping=ARRAY
		aDateTime : LIST OF ADateTime_;
	  	!!@ili2db.mapping=ARRAY
		numericInt : LIST OF NumericInt_;
	  	!!@ili2db.mapping=ARRAY
		numericDec : LIST OF NumericDec_;
	  	!!@ili2db.mapping=ARRAY
		!!binbox : LIST OF Binbox_; !! 2017-11-28 not yet supported by pg jdbc driver
	  	!!@ili2db.mapping=ARRAY
		!!xmlbox : LIST OF Xmlbox_;  !! 2017-11-28  not yet supported by pg jdbc driver
	END Datatypes;    
  END TestA;
END Array23.
', '2018-01-12 11:47:17.601');

INSERT INTO array23.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.11.1-20180103');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO array23.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiPointTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_table_prop VALUES ('binbox_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('farbe', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('adatetime_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('adate_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('xmlbox_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('aboolean_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('numericint_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('atime_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('auuid_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_table_prop VALUES ('numericdec_', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Xmlbox_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.numericInt', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.aUuid', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.AUuid_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.NumericDec_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.ADateTime_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.aDateTime', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Binbox_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Farbe', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.numericDec', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Auto.Farben', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Auto', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.aTime', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.ABoolean_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.ATime_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.NumericInt_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.aBoolean', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.ADate_', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO array23.t_ili2db_trafo VALUES ('Array23.TestA.Datatypes.aDate', 'ch.ehi.ili2db.arrayTrafo', 'coalesce');

SELECT pg_catalog.setval('t_ili2db_seq', 10, true);