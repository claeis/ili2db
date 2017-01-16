
# ili2db Suite

Ili2db is a suite of DB tool for managing interlis import/export to relational databases.

## Table of contents
- [ili2geodb](#ili2geodb)
- [ili2gpkg](#ili2gpkg)
- [ili2mdb](#ili2mdb)
- [ili2ora](#ili2ora)
- [ili2pg](#ili2pg)


## ili2geodb 
TBD


## ili2gpkg
TBD


## ili2mdb
TBD


## ili2ora
TBD


## ili2pg

Ili2pg is an interlis 2 loader for postgis DB, it translates the intelis 2 data model definitions to a postgis schema. There are several feature for import and export of data from the DB.

### USAGE

java -jar ili2pg.jar [Options] [file.xtf|file.ili]
  
### Options
```
--import                     perform dataset import in the db
--update                     perform dataset update in the db
--replace                    perform dataset replace in the db
--delete                     perform dataset remove from db
--export                     perform dataset export from db
--schemaimport               perform a schema import of a new schema
--dbhost  host               hostname of the server (defaults is localhost)
--dbport  port               port number the server is listening on (defaults is 5432)
--dbdatabase database        database name
--dbusr  username            username to access database
--dbpwd  password            password to access database
--validConfig file           config file for validation
--disableValidation          disable data validation
--deleteData                 delete existing data from existing tables on schema/data import
--defaultSrsAuth  auth       default SRS authority EPSG
--defaultSrsCode  code       default SRS code 21781
--modeldir  path             path(s) of directories containing ili-files
--models modelname           name(s) of ili-models to generate an db schema for
--dataset name               name of dataset
--baskets BID                basket-Id(s) of ili-baskets to export
--topics topicname           name(s) of ili-topics to export
--createscript filename      generate a sql script that creates the db schema
--dropscript filename        generate a sql script that drops the generated db schema
--noSmartMapping             disable all smart mappings
--smart1Inheritance          enable smart1 mapping of class/structure inheritance
--smart2Inheritance          enable smart2 mapping of class/structure inheritance
--coalesceCatalogueRef       enable smart mapping of CHBase:CatalogueReference
--coalesceMultiSurface       enable smart mapping of CHBase:MultiSurface
--expandMultilingual         enable smart mapping of CHBase:MultilingualText
--createGeomIdx              create a spatial index on geometry columns
--createEnumColAsItfCode     create enum type column with value according to ITF (instead of XTF)
--createEnumTxtCol           create an additional column with the text of the enumeration value
--createEnumTabs             generate tables with enum definitions
--createSingleEnumTab        generate all enum definitions in a single table
--beautifyEnumDispName       replace underscore with space in dispName of enum table entries
--createStdCols              generate T_User, T_CreateDate, T_LastChange columns
--t_id_Name name             change name of t_id column (T_Id)
--idSeqMin minValue          sets the minimum value of the id sequence generator
--idSeqMax maxValue          sets the maximum value of the id sequence generator
--createTypeDiscriminator    generate always a type discriminaor colum
--structWithGenericRef       generate one generic reference to parent in struct tables
--disableNameOptimization    disable use of unqualified class name as table name
--nameByTopic                use topic+class name as table name
--maxNameLength length       max length of sql names (60)
--sqlEnableNull              create no NOT NULL constraints in db schema
--strokeArcs                 stroke ARCS on import
--skipPolygonBuilding        keep linetables; don't build polygons on import
--skipPolygonBuildingErrors  report build polygon errors as info
--keepAreaRef                keep arreaRef as additional column on import
--importTid                  read TID into additional column T_Ili_Tid
--createBasketCol            generate T_basket column
--createFk                   generate foreign key constraints
--createFkIdx                create an index on foreign key columns
--createUnique               create UNIQUE db constraints
--ILIGML20                   use eCH-0118-2.0 as transferformat
--dbschema  schema           name of the schema in the database (default not set)
--oneGeomPerTable            in case more than one geometry per table, create secondary table
--log filename               log message to given file
--gui                        start GUI
--trace                      enable trace messages
--help                       display this help text
--version                    display the version of ili2pg
```

## Credits

_Developed by Eisenhut Informatik AG, CH-3401 Burgdorf 
 See http://www.interlis.ch for information about INTERLIS
 Parts of this program have been generated by ANTLR; see http://www.antlr.org
 This product includes software developed by the
 Apache Software Foundation (http://www.apache.org/)._