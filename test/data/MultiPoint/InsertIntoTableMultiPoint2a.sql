INSERT INTO multipoint.t_ili2db_dataset VALUES (1, 'MultiPoint2a.xtf-1');
INSERT INTO multipoint.t_ili2db_basket VALUES (3, 1, 'MultiPoint2.TestA', 'MultiPoint2.TestA', 'MultiPoint2a.xtf-3');
INSERT INTO multipoint.classa1 VALUES (4, 3, '13', '01040000201555000002000000010100000000000000BC4F224100000000A06A08410101000000000000009E4F224100000000286A0841');
INSERT INTO multipoint.t_ili2db_attrname VALUES ('MultiPoint2.MultiPoint2D.points', 'multipoint2d_points', 'pointstruktur2d', 'multipoint2d');
INSERT INTO multipoint.t_ili2db_attrname VALUES ('MultiPoint2.TestA.ClassA1.geom', 'geom', 'classa1', NULL);
INSERT INTO multipoint.t_ili2db_attrname VALUES ('MultiPoint2.PointStruktur2D.coord', 'coord', 'pointstruktur2d', NULL);
INSERT INTO multipoint.t_ili2db_classname VALUES ('MultiPoint2.MultiPoint2D', 'multipoint2d');
INSERT INTO multipoint.t_ili2db_classname VALUES ('MultiPoint2.PointStruktur2D', 'pointstruktur2d');
INSERT INTO multipoint.t_ili2db_classname VALUES ('MultiPoint2.TestA.ClassA1', 'classa1');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('classa1', NULL, 'geom', 'ch.ehi.ili2db.c2Min', '70000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('classa1', NULL, 'geom', 'ch.ehi.ili2db.c1Min', '480000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('pointstruktur2d', NULL, 'coord', 'ch.ehi.ili2db.c2Min', '70000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('classa1', NULL, 'geom', 'ch.ehi.ili2db.c2Max', '310000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('pointstruktur2d', NULL, 'coord', 'ch.ehi.ili2db.c2Max', '310000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('pointstruktur2d', NULL, 'coord', 'ch.ehi.ili2db.c1Min', '480000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('classa1', NULL, 'geom', 'ch.ehi.ili2db.c1Max', '850000.0');
INSERT INTO multipoint.t_ili2db_column_prop VALUES ('pointstruktur2d', NULL, 'coord', 'ch.ehi.ili2db.c1Max', '850000.0');
INSERT INTO multipoint.t_ili2db_import VALUES (2, 1, '2018-01-12 12:15:18.993', 'postgres', 'test\data\MultiPoint\MultiPoint2a.xtf');
INSERT INTO multipoint.t_ili2db_import_basket VALUES (5, 2, 3, 1, 3, 4);
INSERT INTO multipoint.t_ili2db_import_object VALUES (6, 5, 'MultiPoint2.TestA.ClassA1', 1, 4, 4);
INSERT INTO multipoint.t_ili2db_inheritance VALUES ('MultiPoint2.TestA.ClassA1', NULL);
INSERT INTO multipoint.t_ili2db_inheritance VALUES ('MultiPoint2.MultiPoint2D', NULL);
INSERT INTO multipoint.t_ili2db_inheritance VALUES ('MultiPoint2.PointStruktur2D', NULL);
INSERT INTO multipoint.t_ili2db_model VALUES ('MultiPoint2.ili', '2.3', 'MultiPoint2', 'INTERLIS 2.3;

MODEL MultiPoint2
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-09-17" =
  DOMAIN
  	Lkoord = COORD 480000.000.. 850000.000 [INTERLIS.m],
		70000.000 .. 310000.000 [INTERLIS.m],
		ROTATION 2 -> 1;
	
   STRUCTURE PointStruktur2D =
      coord: Lkoord;
   END PointStruktur2D;

   !!@ili2db.mapping=MultiPoint
   STRUCTURE MultiPoint2D =
      points: BAG {1..*} OF PointStruktur2D;
   END MultiPoint2D;
	
  TOPIC TestA =
  
    CLASS ClassA1 =
	geom : MANDATORY MultiPoint2D;    	
    END ClassA1;
    
  END TestA;
  
  
END MultiPoint2.
', '2018-01-12 12:15:18.968');

INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.11.1-20180103');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO multipoint.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiPointTrafo', 'coalesce');
INSERT INTO multipoint.t_ili2db_table_prop VALUES ('pointstruktur2d', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO multipoint.t_ili2db_table_prop VALUES ('multipoint2d', 'ch.ehi.ili2db.tableKind', 'STRUCTURE');
INSERT INTO multipoint.t_ili2db_trafo VALUES ('MultiPoint2.MultiPoint2D', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multipoint.t_ili2db_trafo VALUES ('MultiPoint2.PointStruktur2D', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multipoint.t_ili2db_trafo VALUES ('MultiPoint2.TestA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multipoint.t_ili2db_trafo VALUES ('MultiPoint2.TestA.ClassA1.geom', 'ch.ehi.ili2db.multiPointTrafo', 'coalesce');

SELECT pg_catalog.setval('t_ili2db_seq', 6, true);