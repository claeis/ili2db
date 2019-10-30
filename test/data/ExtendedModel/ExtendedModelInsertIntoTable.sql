INSERT INTO extendedmodel23.t_ili2db_dataset VALUES (1, 'ExtendedModel1.xtf-1');
INSERT INTO extendedmodel23.t_ili2db_basket VALUES (3, 1, 'BaseModel.TestA', 'b1', 'ExtendedModel1.xtf-3');
INSERT INTO extendedmodel23.t_ili2db_basket VALUES (11, 1, 'BaseModel.TestB', 'b2', 'ExtendedModel1.xtf-3');
INSERT INTO extendedmodel23.t_ili2db_basket VALUES (15, 1, 'ExtendedModel.TestAp', 'b3', 'ExtendedModel1.xtf-3');
INSERT INTO extendedmodel23.t_ili2db_basket VALUES (25, 1, 'ExtendedModel.TestBp', 'b4', 'ExtendedModel1.xtf-3');
INSERT INTO extendedmodel23.classa1 VALUES (4, 3, '1');
INSERT INTO extendedmodel23.classa1 VALUES (16, 15, '31');
INSERT INTO extendedmodel23.classa3 VALUES (6, 3, '3');
INSERT INTO extendedmodel23.classa3 VALUES (18, 15, '33');
INSERT INTO extendedmodel23.classap1 VALUES (19, 15, '34');
INSERT INTO extendedmodel23.classa2 VALUES (5, 3, 'classa2', '2', 'a1','felix','rot', 6, NULL, NULL);
INSERT INTO extendedmodel23.classa2 VALUES (17, 15, 'extendedmodeltestap_classa2', '32', 'a2','urs','rot.dunkel', 18, 1.1, 19);
INSERT INTO extendedmodel23.classb1 VALUES (12, 11, '20');
INSERT INTO extendedmodel23.extendedmodeltestbp_classb1 VALUES (26, 25, '40');
INSERT INTO extendedmodel23.t_ili2db_attrname VALUES ('ExtendedModel.TestAp.ClassA2.wert', 'wert', 'classa2', NULL);
INSERT INTO extendedmodel23.t_ili2db_attrname VALUES ('BaseModel.TestA.AssocA1.a3', 'a3', 'classa2', 'classa3');
INSERT INTO extendedmodel23.t_ili2db_attrname VALUES ('BaseModel.TestA.ClassA2.farbe', 'farbe', 'classa2', NULL);
INSERT INTO extendedmodel23.t_ili2db_attrname VALUES ('BaseModel.TestA.ClassA2.name', 'aname', 'classa2', NULL);
INSERT INTO extendedmodel23.t_ili2db_attrname VALUES ('ExtendedModel.TestAp.AssocAp1.ap1', 'ap1', 'classa2', 'classap1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('BaseModel.TestA.AssocA1', 'assoca1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('ExtendedModel.TestAp.AssocAp1', 'assocap1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('ExtendedModel.TestBp.ClassB1', 'extendedmodeltestbp_classb1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('ExtendedModel.TestAp.ClassAp1', 'classap1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('ExtendedModel.TestAp.ClassA2', 'extendedmodeltestap_classa2');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('BaseModel.TestB.ClassB1', 'classb1');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('BaseModel.TestA.ClassA3', 'classa3');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('BaseModel.TestA.ClassA2', 'classa2');
INSERT INTO extendedmodel23.t_ili2db_classname VALUES ('BaseModel.TestA.ClassA1', 'classa1');
INSERT INTO extendedmodel23.t_ili2db_import VALUES (2, 1, '2018-01-12 11:58:24.493', 'postgres', 'test\data\ExtendedModel\ExtendedModel1.xtf');
INSERT INTO extendedmodel23.t_ili2db_import_basket VALUES (7, 2, 3, 3, 3, 6);
INSERT INTO extendedmodel23.t_ili2db_import_basket VALUES (13, 2, 11, 1, 11, 12);
INSERT INTO extendedmodel23.t_ili2db_import_basket VALUES (20, 2, 15, 4, 15, 19);
INSERT INTO extendedmodel23.t_ili2db_import_basket VALUES (27, 2, 25, 1, 25, 26);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (8, 7, 'BaseModel.TestA.ClassA3', 1, 6, 6);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (9, 7, 'BaseModel.TestA.ClassA2', 1, 5, 5);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (10, 7, 'BaseModel.TestA.ClassA1', 1, 4, 4);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (14, 13, 'BaseModel.TestB.ClassB1', 1, 12, 12);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (21, 20, 'ExtendedModel.TestAp.ClassAp1', 1, 19, 19);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (22, 20, 'ExtendedModel.TestAp.ClassA2', 1, 17, 17);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (23, 20, 'BaseModel.TestA.ClassA3', 1, 18, 18);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (24, 20, 'BaseModel.TestA.ClassA1', 1, 16, 16);
INSERT INTO extendedmodel23.t_ili2db_import_object VALUES (28, 27, 'ExtendedModel.TestBp.ClassB1', 1, 26, 26);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('BaseModel.TestA.ClassA3', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('BaseModel.TestA.ClassA1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('ExtendedModel.TestAp.ClassAp1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('BaseModel.TestA.AssocA1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('ExtendedModel.TestBp.ClassB1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('BaseModel.TestB.ClassB1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('ExtendedModel.TestAp.ClassA2', 'BaseModel.TestA.ClassA2');
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('ExtendedModel.TestAp.AssocAp1', NULL);
INSERT INTO extendedmodel23.t_ili2db_inheritance VALUES ('BaseModel.TestA.ClassA2', NULL);
INSERT INTO extendedmodel23.t_ili2db_model VALUES ('ExtendedModel.ili', '2.3', 'BaseModel ExtendedModel{ BaseModel}', 'INTERLIS 2.3;

MODEL BaseModel
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2017-10-23" =
    
  DOMAIN
  	Farbe = (rot,blau);
  	
  TOPIC TestA =
  
    CLASS ClassA1 =
    END ClassA1;

    CLASS ClassA2 =
      attr : TEXT*20;
      name : TEXT*20;
      farbe : Farbe;
    END ClassA2;

    CLASS ClassA3 =
    END ClassA3;
    
    ASSOCIATION AssocA1 =
      a2 -- {0..*} ClassA2;
      a3 -- {0..1} ClassA3;
    END AssocA1;
    
  END TestA;
  
  !! TOPIC das nicht erweitert wird
  TOPIC TestB =

    CLASS ClassB1 =
    END ClassB1;
    
  END TestB;
  
END BaseModel.

MODEL ExtendedModel
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2017-10-23" =
  IMPORTS BaseModel;
  
  DOMAIN
  	Farbe EXTENDS BaseModel.Farbe = (rot (dunkel, hell));
    
  TOPIC TestAp EXTENDS BaseModel.TestA =
  
    !! ClassA1 unveraendert
    
    !! ClassA2 erweitert
    !!   zusaetzliches Attribut
    !!   Aufzaehlung spezialisiert
    !!   zusaetzliche eingebettete Rolle
	CLASS ClassA2 (EXTENDED) =
      name (EXTENDED) : TEXT*20;
	  farbe (EXTENDED) : ExtendedModel.Farbe;
	  wert : 1.0 .. 100.0;
    END ClassA2;
    
    !! zusaetzliche Klasse
    CLASS ClassAp1 =
    END ClassAp1;

    !! zusaetzliche Assoc (fuer zusaetliche Rolle in ClassA2)
    ASSOCIATION AssocAp1 =
      ap2 -- {0..*} ClassA2;
      ap1 -- {0..1} ClassAp1;
    END AssocAp1;
    
  END TestAp;
  
  !! zusaetzliches TOPIC
  TOPIC TestBp =

    CLASS ClassB1 =
    END ClassB1;
    
  END TestBp;
  
END ExtendedModel.
', '2018-01-12 11:58:24.408');

INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.arrayTrafo', 'coalesce');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.11.1-20180103');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiPointTrafo', 'coalesce');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiLineTrafo', 'coalesce');
INSERT INTO extendedmodel23.t_ili2db_settings VALUES ('ch.ehi.ili2db.ver3_translation','True');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('BaseModel.TestA.AssocA1', 'ch.ehi.ili2db.inheritance', 'embedded');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('ExtendedModel.TestAp.AssocAp1', 'ch.ehi.ili2db.inheritance', 'embedded');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('ExtendedModel.TestBp.ClassB1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('ExtendedModel.TestAp.ClassAp1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('ExtendedModel.TestAp.ClassA2', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('BaseModel.TestB.ClassB1', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('BaseModel.TestA.ClassA3', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('BaseModel.TestA.ClassA2', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO extendedmodel23.t_ili2db_trafo VALUES ('BaseModel.TestA.ClassA1', 'ch.ehi.ili2db.inheritance', 'newClass');

SELECT pg_catalog.setval('t_ili2db_seq', 28, true);