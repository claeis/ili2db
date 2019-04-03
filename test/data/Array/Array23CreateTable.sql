DROP SCHEMA IF EXISTS array23 CASCADE;

CREATE SCHEMA array23;

SET search_path = array23, pg_catalog;

CREATE SEQUENCE t_ili2db_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE aboolean_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue boolean
);

CREATE TABLE adate_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue date
);

CREATE TABLE adatetime_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue timestamp without time zone
);

CREATE TABLE atime_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue time without time zone
);

CREATE TABLE auto (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    farben character varying(255)[]
);

CREATE TABLE auuid_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue uuid
);

CREATE TABLE binbox_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue bytea
);

CREATE TABLE datatypes (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    auuid uuid[],
    aboolean boolean[],
    atime time without time zone[],
    adate date[],
    adatetime timestamp without time zone[],
    numericint integer[],
    numericdec numeric(3,1)[]
);

CREATE TABLE farbe (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    wert character varying(255)
);

CREATE TABLE numericdec_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue numeric(3,1)
);

CREATE TABLE numericint_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue integer
);

CREATE TABLE t_ili2db_attrname (
    iliname character varying(1024) NOT NULL,
    sqlname character varying(1024) NOT NULL,
    colowner character varying(1024) NOT NULL,
    target character varying(1024)
);

CREATE TABLE t_ili2db_basket (
    t_id bigint NOT NULL,
    dataset bigint,
    topic character varying(200) NOT NULL,
    t_ili_tid character varying(200),
    attachmentkey character varying(200) NOT NULL,
    domains character varying(1024)
);

CREATE TABLE t_ili2db_classname (
    iliname character varying(1024) NOT NULL,
    sqlname character varying(1024) NOT NULL
);

CREATE TABLE t_ili2db_column_prop (
    tablename character varying(255) NOT NULL,
    subtype character varying(255),
    columnname character varying(255) NOT NULL,
    tag character varying(1024) NOT NULL,
    setting character varying(1024) NOT NULL
);

CREATE TABLE t_ili2db_dataset (
    t_id bigint NOT NULL,
    datasetname character varying(200)
);

CREATE TABLE t_ili2db_import (
    t_id bigint NOT NULL,
    dataset bigint NOT NULL,
    importdate timestamp without time zone NOT NULL,
    importuser character varying(40) NOT NULL,
    importfile character varying(200)
);

CREATE TABLE t_ili2db_import_basket (
    t_id bigint NOT NULL,
    importrun bigint NOT NULL,
    basket bigint NOT NULL,
    objectcount integer,
    start_t_id bigint,
    end_t_id bigint
);

CREATE TABLE t_ili2db_import_object (
    t_id bigint NOT NULL,
    import_basket bigint NOT NULL,
    class character varying(200) NOT NULL,
    objectcount integer,
    start_t_id bigint,
    end_t_id bigint
);

CREATE TABLE t_ili2db_inheritance (
    thisclass character varying(1024) NOT NULL,
    baseclass character varying(1024)
);

CREATE TABLE t_ili2db_model (
    filename character varying(250) NOT NULL,
    iliversion character varying(3) NOT NULL,
    modelname text NOT NULL,
    content text NOT NULL,
    importdate timestamp without time zone NOT NULL
);

CREATE TABLE t_ili2db_settings (
    tag character varying(60) NOT NULL,
    setting character varying(255)
);

CREATE TABLE t_ili2db_table_prop (
    tablename character varying(255) NOT NULL,
    tag character varying(1024) NOT NULL,
    setting character varying(1024) NOT NULL
);

CREATE TABLE t_ili2db_trafo (
    iliname character varying(1024) NOT NULL,
    tag character varying(1024) NOT NULL,
    setting character varying(1024) NOT NULL
);

CREATE TABLE xmlbox_ (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    avalue xml
);