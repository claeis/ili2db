============================================================
ili2db - imports/exports interlis transfer files to a sql db
============================================================

Features
========
- Translates INTERLIS 1+2 data model definitions to a SQL schema.
- Loads INTERLIS 1+2 data into a SQL database.
- Extracts INTERLIS 1+2 data from a SQL database.

This is the developer documentation, see `<../docs/ili2db.rst>`_ for user documentation.

License
=======
ili2db is licensed under the LGPL (Lesser GNU Public License).

Build System Requirements
=========================
For the current version of ili2db, you will need a JDK (Java Development Kit) and Apache Ant installed on your system.

The JDK (Java Development Kit), version 1.6 or later, can be downloaded for free from the Website <http://www.oracle.com/technetwork/java/javase/downloads/index.html>.

Apache Ant can be downloaded from <http://ant.apache.org/>.

How to build
============
**PostGIS:** ``ant -f build-ili2pg.xml jar bindist srcdist``

**GeoPackage:** ``ant -f build-ili2gpkg.xml jar bindist srcdist``

**FileGDB:** ``ant -f build-ili2fgdb.xml jar bindist srcdist``

Runtime dependencies
====================
+---------------------------------------+--------------------------------+
| Dependency                            | Source Website                 |
+=======================================+================================+
| libs/antlr.jar                        |                                |
+---------------------------------------+--------------------------------+
|libs/ehibasics.jar                     |                                |
+---------------------------------------+--------------------------------+
|libs/ili2c-core.jar                    |                                |
+---------------------------------------+--------------------------------+
|libs/ili2c-tool.jar                    |                                |
+---------------------------------------+--------------------------------+
|libs/iox-api.jar                       |                                |
+---------------------------------------+--------------------------------+
|libs/iox-ili.jar                       |                                |
+---------------------------------------+--------------------------------+
|libs/jts-core-1.14.0.jar               |                                |
+---------------------------------------+--------------------------------+
|libs/gson-2.6.2.jar                    |                                |
+---------------------------------------+--------------------------------+
|libs/toml4j-0.5.1.jar                  |                                |
+---------------------------------------+--------------------------------+
|libs/ehisqlgen.jar                     |                                |
+---------------------------------------+--------------------------------+
|libs/postgresql-9.4.1208.jre6.jar [1]_ |                                |
+---------------------------------------+--------------------------------+
|libs/sqlite-jdbc-3.8.11.2.jar [2]_     |                                |
+---------------------------------------+--------------------------------+
|libs/fgdb4j.jar [3]_                   |                                |
+---------------------------------------+--------------------------------+


Development dependencies
========================
TODO

Design
======
TODO

.. [1]
  ili2pg only

.. [2]
  ili2gpkg only

.. [3]
  ili2fgdb only
