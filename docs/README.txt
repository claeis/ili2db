ili2db - imports/exports interlis transfer files to a sql db

Features
Translates INTERLIS 1+2 data model definitions to a SQL schema.
Loads INTERLIS 1+2 data into a SQL database.
Extracts INTERLIS 1+2 data from a SQL database.

License
ili2db is licensed under the LGPL (Lesser GNU Public License).

Status
ili2db is in stable state.

System Requirements
For the current version of ili2db, you will need a JRE (Java Runtime Environment) installed on your system, version 1.8 or later.
The JRE (Java Runtime Environment) can be downloaded for free from the Website <http://www.java.com/>.

Download ili2db
<https://downloads.interlis.ch/>

Installing ili2db
To install ili2db, choose a directory and extract the distribution file there.

Running ili2db
ili2db can be started with

PostGIS: 
java -jar ili2pg.jar --schemaimport --dbdatabase mogis --dbusr julia --dbpwd romeo path/to/dm01av.ili

GeoPackage: 
java -jar ili2gpkg.jar --schemaimport --dbfile mogis.gpkg path/to/dm01av.ili

Documentation
User documentation <https://docs.interlis.ch/>
