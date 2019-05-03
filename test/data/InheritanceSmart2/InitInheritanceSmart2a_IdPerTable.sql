INSERT INTO InheritanceSmart2.t_ili2db_dataset VALUES (1, 'Testset1');

INSERT INTO InheritanceSmart2.t_ili2db_basket VALUES (3, 1, 'Inheritance2.TestA', 'Inheritance2.TestA', 'Inheritance2a.xtf-3');

INSERT INTO InheritanceSmart2.classa3b VALUES (1, 3, '1', 'attra3b-10', 'attra3-10');

INSERT INTO InheritanceSmart2.classa3c VALUES (1, 3, '2', 'attra3c-20', 'attra3b-20', 'attra3-20');

INSERT INTO InheritanceSmart2.classb VALUES (1, 3, '3', 'attrb-30', 1, NULL);
INSERT INTO InheritanceSmart2.classb VALUES (2, 3, '4', 'attrb-40', NULL, 1);
INSERT INTO InheritanceSmart2.classb VALUES (3, 3, '5', 'attrb-50', 1, NULL);

INSERT INTO InheritanceSmart2.aa2bb VALUES (1, 3, NULL, 1, NULL, 2);
INSERT INTO InheritanceSmart2.aa2bb VALUES (2, 3, NULL, NULL, 1, 2);
INSERT INTO InheritanceSmart2.aa2bb VALUES (3, 3, NULL, 1, NULL, 3);

INSERT INTO InheritanceSmart2.t_ili2db_import VALUES (2, 1, '2016-11-22 07:34:12.73', 'postgres', 'test\data\InheritanceSmart2\Inheritance2a.xtf');

INSERT INTO InheritanceSmart2.t_ili2db_import_basket VALUES (15, 2, 3, 8, 3, 14);

INSERT INTO InheritanceSmart2.t_ili2db_import_object VALUES (16, 15, 'Inheritance2.TestA.ClassA3b', 1, 4, 4);
INSERT INTO InheritanceSmart2.t_ili2db_import_object VALUES (17, 15, 'Inheritance2.TestA.ClassA3c', 1, 5, 5);
INSERT INTO InheritanceSmart2.t_ili2db_import_object VALUES (18, 15, 'Inheritance2.TestA.aa2bb', 3, 10, 14);
INSERT INTO InheritanceSmart2.t_ili2db_import_object VALUES (19, 15, 'Inheritance2.TestA.ClassB', 3, 6, 8);


