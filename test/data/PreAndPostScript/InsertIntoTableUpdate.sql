INSERT INTO preandpostscriptschema.t_ili2db_dataset VALUES (1, 'DataSetA');
INSERT INTO preandpostscriptschema.t_ili2db_basket VALUES (3, 1, 'ModelA.TopicA', 'c34c86ec-2a75-4a89-a194-f9ebc422f7bc', 'MainUpdate.xtf-3');
INSERT INTO preandpostscriptschema.classa VALUES (4, 3, 'DataSetA', 'c34c86ec-2a75-4a89-a194-f9ebc422f8bc', 'mainValueUpdate');
INSERT INTO preandpostscriptschema.t_ili2db_attrname VALUES ('ModelA.TopicA.ClassA.attr1', 'attr1', 'classa', NULL);
INSERT INTO preandpostscriptschema.t_ili2db_attrname VALUES ('ModelA.TopicA.ClassB.attr2', 'attr2', 'classb', NULL);
INSERT INTO preandpostscriptschema.t_ili2db_classname VALUES ('ModelA.TopicA.ClassA', 'classa');
INSERT INTO preandpostscriptschema.t_ili2db_classname VALUES ('ModelA.TopicA.ClassB', 'classb');
INSERT INTO preandpostscriptschema.t_ili2db_import VALUES (2, 1, '2017-05-16 13:15:59.936', 'postgres', 'test\data\PreAndPostScript\MainUpdate.xtf');
INSERT INTO preandpostscriptschema.t_ili2db_import_basket VALUES (5, 2, 3, 1, 3, 4);
INSERT INTO preandpostscriptschema.t_ili2db_import_object VALUES (6, 5, 'ModelA.TopicA.ClassA', 1, 4, 4);
INSERT INTO preandpostscriptschema.t_ili2db_inheritance VALUES ('ModelA.TopicA.ClassB', NULL);
INSERT INTO preandpostscriptschema.t_ili2db_inheritance VALUES ('ModelA.TopicA.ClassA', NULL);
INSERT INTO preandpostscriptschema.t_ili2db_model VALUES ('ModelAUpdate.ili', '2.3', 'ModelA', 'INTERLIS 2.3;

MODEL ModelA
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-07-07" =
  TOPIC TopicA =
  	BASKET OID AS INTERLIS.UUIDOID;
    CLASS ClassA =
    	OID AS INTERLIS.UUIDOID;
    	attr1 : TEXT*60;
    END ClassA;
    CLASS ClassB =
    	OID AS INTERLIS.UUIDOID;
    	attr2 : TEXT*60;
    END ClassB;
  END TopicA;
END ModelA.', '2017-05-16 13:15:59.92');

INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.createDatasetCols', 'addDatasetCol');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.2-20170421');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO preandpostscriptschema.t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO preandpostscriptschema.t_ili2db_trafo VALUES ('ModelA.TopicA.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO preandpostscriptschema.t_ili2db_trafo VALUES ('ModelA.TopicA.ClassB', 'ch.ehi.ili2db.inheritance', 'newClass');