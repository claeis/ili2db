INSERT INTO BlackBoxTypes23.t_ili2db_dataset (t_id, datasetname) VALUES (1, NULL);
INSERT INTO BlackBoxTypes23.t_ili2db_basket (t_id, dataset, topic, t_ili_tid, attachmentkey) VALUES (3, 1, 'BlackBoxTypes23.Topic', 'BlackBoxTypes23.Topic', 'BlackBoxTypes23a.xtf-3');
INSERT INTO BlackBoxTypes23.classa (t_id, t_basket, t_ili_tid, xmlbox, binbox) VALUES (4, 3, 'o0', NULL, NULL);
INSERT INTO BlackBoxTypes23.classa (t_id, t_basket, t_ili_tid, xmlbox, binbox) VALUES (5, 3, 'o1', '<anyXml></anyXml>', '\x000000');
INSERT INTO BlackBoxTypes23.t_ili2db_attrname (iliname, sqlname, owner, target) VALUES ('BlackBoxTypes23.Topic.ClassA.binbox', 'binbox', 'classa', NULL);
INSERT INTO BlackBoxTypes23.t_ili2db_attrname (iliname, sqlname, owner, target) VALUES ('BlackBoxTypes23.Topic.ClassA.xmlbox', 'xmlbox', 'classa', NULL);
INSERT INTO BlackBoxTypes23.t_ili2db_classname (iliname, sqlname) VALUES ('BlackBoxTypes23.Topic.ClassA', 'classa');
INSERT INTO BlackBoxTypes23.t_ili2db_import (t_id, dataset, importdate, importuser, importfile) VALUES (2, 1, '2017-05-01 15:31:01.21', 'postgres', 'test\data\BlackBoxTypes23\BlackBoxTypes23a.xtf');
INSERT INTO BlackBoxTypes23.t_ili2db_import_basket (t_id, import, basket, objectcount, start_t_id, end_t_id) VALUES (6, 2, 3, 2, 3, 5);
INSERT INTO BlackBoxTypes23.t_ili2db_import_object (t_id, import_basket, class, objectcount, start_t_id, end_t_id) VALUES (7, 6, 'BlackBoxTypes23.Topic.ClassA', 2, 4, 5);
INSERT INTO BlackBoxTypes23.t_ili2db_inheritance (thisclass, baseclass) VALUES ('BlackBoxTypes23.Topic.ClassA', NULL);
INSERT INTO BlackBoxTypes23.t_ili2db_model (file, iliversion, modelname, content, importdate) VALUES ('BlackBoxTypes23.ili', '2.3', 'BlackBoxTypes23', 'INTERLIS 2.3;

MODEL BlackBoxTypes23
  AT "mailto:ce@eisenhutinformatik.ch" VERSION "2016-03-07" =
	TOPIC Topic=             
		CLASS ClassA =
		   !! BlackBoxType
		   xmlbox : BLACKBOX XML;
		   binbox : BLACKBOX BINARY;
		END ClassA;
	END Topic;
END BlackBoxTypes23.', '2017-05-01 15:31:01.195');

INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO BlackBoxTypes23.t_ili2db_settings (tag, setting) VALUES ('ch.ehi.ili2db.TidHandling', 'property');
INSERT INTO BlackBoxTypes23.t_ili2db_trafo (iliname, tag, setting) VALUES ('BlackBoxTypes23.Topic.ClassA', 'ch.ehi.ili2db.inheritance', 'newClass');