INSERT INTO multisurface.t_ili2db_dataset VALUES (1, NULL);
INSERT INTO multisurface.t_ili2db_basket VALUES (3, 1, 'MultiSurface2.TestA', 'MultiSurface2.TestA', 'MultiSurface2a.xtf-3');
INSERT INTO multisurface.classa1 VALUES (4, 3, '13', '010C0000201555000002000000010A0000000100000001090000000100000001020000000400000000000000BC4F224100000000A06A084100000000DA4F224100000000406B084100000000944F224100000000406B084100000000BC4F224100000000A06A0841010A00000001000000010900000001000000010200000005000000000000009E4F224100000000286A084100000000D04F224100000000506A084100000000944F224100000000A06A0841000000008A4F224100000000506A0841000000009E4F224100000000286A0841');
INSERT INTO multisurface.t_ili2db_attrname VALUES ('MultiSurface2.TestA.ClassA1.geom', 'geom', 'classa1', NULL);
INSERT INTO multisurface.t_ili2db_attrname VALUES ('MultiSurface2.FlaecheStruktur2D.Flaeche', 'flaeche', 'flaechestruktur2d', NULL);
INSERT INTO multisurface.t_ili2db_attrname VALUES ('MultiSurface2.MultiFlaeche2D.Flaechen', 'multiflaeche2d_flaechen', 'flaechestruktur2d', 'multiflaeche2d');
INSERT INTO multisurface.t_ili2db_classname VALUES ('MultiSurface2.TestA.ClassA1', 'classa1');
INSERT INTO multisurface.t_ili2db_classname VALUES ('MultiSurface2.FlaecheStruktur2D', 'flaechestruktur2d');
INSERT INTO multisurface.t_ili2db_classname VALUES ('MultiSurface2.MultiFlaeche2D', 'multiflaeche2d');
INSERT INTO multisurface.t_ili2db_import VALUES (2, 1, '2017-05-04 17:32:15.389', 'postgres', 'test\data\MultiSurface\MultiSurface2a.xtf');
INSERT INTO multisurface.t_ili2db_import_basket VALUES (5, 2, 3, 1, 3, 4);
INSERT INTO multisurface.t_ili2db_import_object VALUES (6, 5, 'MultiSurface2.TestA.ClassA1', 1, 4, 4);
INSERT INTO multisurface.t_ili2db_inheritance VALUES ('MultiSurface2.TestA.ClassA1', NULL);
INSERT INTO multisurface.t_ili2db_inheritance VALUES ('MultiSurface2.MultiFlaeche2D', NULL);
INSERT INTO multisurface.t_ili2db_inheritance VALUES ('MultiSurface2.FlaecheStruktur2D', NULL);
INSERT INTO multisurface.t_ili2db_model VALUES ('MultiSurface2.ili', '2.3', 'MultiSurface2', 'INTERLIS 2.3;

MODEL MultiSurface2
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-09-17" =
  DOMAIN
  	Lkoord = COORD 480000.000.. 850000.000 [INTERLIS.m],
		70000.000 .. 310000.000 [INTERLIS.m],
		ROTATION 2 -> 1;
	Einzelflaeche2D = SURFACE WITH (STRAIGHTS,ARCS) VERTEX Lkoord WITHOUT OVERLAPS > 0.001;
	
   STRUCTURE FlaecheStruktur2D =
      Flaeche: Einzelflaeche2D;
   END FlaecheStruktur2D;

   !!@ili2db.mapping=MultiSurface
   STRUCTURE MultiFlaeche2D =
      Flaechen: BAG {1..*} OF FlaecheStruktur2D;
   END MultiFlaeche2D;
	
  TOPIC TestA =
  
    CLASS ClassA1 =
	geom : MANDATORY MultiFlaeche2D;    	
    END ClassA1;
    
  END TestA;
  
  
END MultiSurface2.
', '2017-05-04 17:32:15.374');

INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO multisurface.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO multisurface.t_ili2db_trafo VALUES ('MultiSurface2.TestA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multisurface.t_ili2db_trafo VALUES ('MultiSurface2.FlaecheStruktur2D', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multisurface.t_ili2db_trafo VALUES ('MultiSurface2.MultiFlaeche2D', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO multisurface.t_ili2db_trafo VALUES ('MultiSurface2.TestA.ClassA1.geom', 'ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');