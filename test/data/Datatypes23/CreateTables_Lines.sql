DROP SCHEMA IF EXISTS datatypes23 CASCADE;

SET search_path = datatypes23, pg_catalog;

CREATE SCHEMA datatypes23;

CREATE SEQUENCE t_ili2db_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE classattr (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    aufzaehlung character varying(255),
    ai32id character varying(255),
    auuid uuid,
    astandardid character varying(255),
    textlimited character varying(30),
    textunlimited text,
    mtextlimited character varying(30),
    mtextunlimited text,
    nametext character varying(255),
    uritext character varying(1023),
    horizalignment character varying(255),
    vertalignment character varying(255),
    aboolean boolean,
    numericint integer,
    numericdec numeric(3,1),
    atime time without time zone,
    adate date,
    adatetime timestamp without time zone,
    binbox bytea,
    xmlbox xml,
    aclass character varying(255),
    aattribute character varying(255),
    CONSTRAINT classattr_numericdec_check CHECK (((numericdec >= 0.0) AND (numericdec <= 10.0))),
    CONSTRAINT classattr_numericint_check CHECK (((numericint >= 0) AND (numericint <= 10)))
);

CREATE TABLE classkoord2 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    lcoord public.geometry(Point,21781)
);

CREATE TABLE classkoord3 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    hcoord public.geometry(PointZ,21781)
);

CREATE TABLE line2 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    straightsarcs2d public.geometry(CompoundCurve,21781)
);

CREATE TABLE line3 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    straightsarcs3d public.geometry(CompoundCurveZ,21781)
);

CREATE TABLE simpleline2 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    straights2d public.geometry(CompoundCurve,21781)
);

CREATE TABLE simpleline3 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    straights3d public.geometry(CompoundCurveZ,21781)
);

CREATE TABLE simplesurface2 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    surface2d public.geometry(CurvePolygon,21781)
);

CREATE TABLE simplesurface3 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    surface3d public.geometry(CurvePolygonZ,21781)
);

CREATE TABLE surface2 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    surfacearcs2d public.geometry(CurvePolygon,21781)
);

CREATE TABLE surface3 (
    t_id bigint DEFAULT nextval('t_ili2db_seq'::regclass) NOT NULL,
    t_basket bigint NOT NULL,
    t_ili_tid character varying(200),
    surfacearcs3d public.geometry(CurvePolygonZ,21781)
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