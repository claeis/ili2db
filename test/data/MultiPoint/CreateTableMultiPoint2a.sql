DROP SCHEMA IF EXISTS multipoint CASCADE;

CREATE SCHEMA multipoint;

SET search_path = multipoint, pg_catalog;

CREATE SEQUENCE t_ili2db_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE classa1 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    geom public.geometry(MultiPoint,21781)
);

CREATE TABLE multipoint2d (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint
);

CREATE TABLE pointstruktur2d (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_seq bigint,
    multipoint2d_points bigint,
    coord public.geometry(Point,21781)
);

CREATE TABLE t_ili2db_attrname (
    iliname character varying(1024) NOT NULL,
    sqlname character varying(1024) NOT NULL,
    owner character varying(1024) NOT NULL,
    target character varying(1024)
);

CREATE TABLE t_ili2db_basket (
    t_id bigint NOT NULL,
    dataset bigint,
    topic character varying(200) NOT NULL,
    t_ili_tid character varying(200),
    attachmentkey character varying(200) NOT NULL
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
    import bigint NOT NULL,
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
    file character varying(250) NOT NULL,
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