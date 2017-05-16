DROP SCHEMA IF EXISTS PreAndPostScriptSchema CASCADE;
CREATE SCHEMA PreAndPostScriptSchema;

CREATE SEQUENCE PreAndPostScriptSchema.t_ili2db_seq start 10000;

CREATE TABLE PreAndPostScriptSchema.helperClass (
  T_Id bigint PRIMARY KEY DEFAULT nextval('PreAndPostScriptSchema.t_ili2db_seq')
  ,attr1 varchar(60) NULL
)
;
INSERT INTO PreAndPostScriptSchema.helperClass(attr1) VALUES ('preValue');