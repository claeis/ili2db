====================================
Anleitung zur Programmfamilie ili2db
====================================

Überblick
=========

ilid2db ist eine in Java erstellte Programmfamilie, die zurzeit ili2pg, 
ili2fgdb, ili2gpkg, ili2ora, ili2mssql, ili2mysql und ili2h2gis umfasst.

Damit kann eine
Interlis-Transferdatei (itf oder xtf) einem Interlis-Modell entsprechend
(ili) mittels 1:1-Transfer in eine Datenbank (PostgreSQL/Postgis bzw.
GeoPackage) gelesen werden oder aus der Datenbank mittels einem 1:1-Transfer
eine solche Transferdatei erstellt werden. Folgende Funktionen sind möglich:

- erstellt das Datenbankschema aus einem Interlis Modell

- importiert Daten aus einer Transferdatei in die Datenbank

- exportiert Daten aus der Datenbank in eine Transferdatei


Folgende Transferformate werden unterstützt:

-  INTERLIS 1

-  INTERLIS 2

-  GML 3.2 [1]_


Schemaimport in die Datenbank
-----------------------------
Beim Schemaimport (``--schemaimport``) wird anhand des INTERLIS Modells das 
Datenbankschema angelegt. 

Diverse Optionen beeinflussen die Abbildung.

Den Geometrien kann mittels Parameter ein EPSG-Code zugewiesen werden.
Die Geometrie-Attribute können optional indexiert werden.

Beispiel::
	
  java -jar ili2gpkg.jar --schemaimport --dbfile mogis.gpkg path/to/dm01av.ili

Import in die Datenbank
---------------------------

Der Import (``--import``) schreibt alle Objekte (im Sinne der eigentlichen Daten)
der Transferdatei in die Datenbank. 

Diverse Optionen beeinflussen, was mit den bestehenden Daten in der DB geschieht.

Area- und Surface-Geometrien werden bei Interlis 1 polygoniert.

Kreisbögen werden als Kreisbögen importiert und somit nicht segmentiert
(oder können optional auch segmentiert werden).

Beispiel::
	
  java -jar ili2gpkg.jar --import --dbfile mogis.gpkg path/to/data.xtf

Export aus der Datenbank
----------------------------

Der Export (``--export``) schreibt alle Daten aus der Datenbank in eine
Transferdatei.

Mit weiteren Optionen wird gesteuert, welche Daten aus der Datenbank exportiert 
werden.

Genau einer der Parameter ``--models``, ``--topics``, ``--baskets`` oder ``--dataset``
muss zwingend verwendet werden, um die zu exportierenden DB-Records auszuwählen.

Der Parameter ``--exportModels`` definiert das Export-Modell, indem die Daten 
exportiert werden (der Parameter ist also keine Alternative, 
sondern ein Zusatz für ``--models``, ``--topics``, ``--baskets`` oder ``--dataset``). 
Als Export-Modelle sind Basis-Modelle (also z.B. Bundes-Modell 
statt Kantons-Modell) oder übersetzte Modelle (also z.B. DM_IT statt DM_DE) zulässig.
Ohne die Option ``--exportModels`` werden die Daten so wie sie erfasst sind 
(bzw. importiert wurden), exportiert.

Geometrien vom Typ Area und Surface werden bei Interlis 1 während dem
Export in Linien umgewandelt.

Beispiel::
	
  java -jar ili2gpkg.jar --export --models DM01 --dbfile mogis.gpkg path/to/output.xtf

Log-Meldungen
-------------
Die Log-Meldungen sollen dem Benutzer zeigen, was das Programm macht.
Am Anfang erscheinen Angaben zur Programm-Version.
Falls das Programm ohne Fehler durchläuft, wird das am Ende ausgegeben.::
	
  Info: ili2fgdb-3.10.7-20170823
  ...
  Info: compile models...
  ...
  Info: ...export done

Bei einem Fehler wird das am Ende des Programms vermerkt. Der eigentliche 
Fehler wird aber in der Regel schon früher ausgegeben.::
	
  Info: ili2fgdb-3.10.7-20170823
  ...
  Info: compile models...
  ...
  Error: DM01.Bodenbedeckung.BoFlaeche_Geometrie: intersection tids 48, 48
  ...
  Error: ...import failed

Fehlerhafte Daten
-----------------
Um fehlerhaften Daten zu importieren (um sie danach (z.B. im GIS) zu flicken), muss mindestens die 
Validierung ausgeschaltet werden (``--disableValidation``). Das DB Schema muss 
aber auch so angelegt werden, dass fehlerhafte Werte als Text importiert werden können (``--sqlColsAsText``) 
bzw. durch ``NULL`` ersetzt werden können (``--sqlEnableNull``). 
Und die Programmlogik für den Datenimport muss die Fehler 
tolerieren (``--skipReferenceErrors`` und ``--skipGeometryErrors``), so dass 
z.B. eine Referenz auf ein nicht vorhandenes Objekt ignoriert wird.

Um solche Daten zu importieren (um sie danach zu flicken)::
	
  java -jar ili2gpkg.jar --schemaimport --sqlEnableNull --sqlColsAsText --dbfile mogis.gpkg path/to/mo.ili
  java -jar ili2gpkg.jar --import --skipReferenceErrors --skipGeometryErrors --disableValidation --dbfile mogis.gpkg path/to/data.xtf

Bei ITF (Interlis 1): Fehlerhafte AREA Attribute können für 
den ganzen Datensatz nicht als Polygone 
gelesen werden, weil ein Programm nicht erkennen kann, welche Linien und 
Punkte falsch sind (Punkt und/oder Linie zu viel oder zu wenig; Linie zu kurz oder zu lang); 
und somit nicht erkennen kann, bei welchem Polygon der Fehler ist. 
Dass diese Daten nicht gelesen werden können, hat also nicht in erster Linie 
mit der Validierung zu tun, sondern damit, dass aus den Linien+Punkten 
keine Polygone gebildet werden können. Die Polygonbildung muss also 
ausgeschaltet werden (``--skipPolygonBuilding``).

Um solche Daten zu importieren (um sie danach zu flicken)::
	
  java -jar ili2gpkg.jar --schemaimport --sqlEnableNull --sqlColsAsText --skipPolygonBuilding --dbfile mogis.gpkg path/to/mo.ili
  java -jar ili2gpkg.jar --import --skipReferenceErrors --skipPolygonBuilding --skipGeometryErrors --disableValidation --dbfile mogis.gpkg path/to/data.itf

Bei XTF (Interlis 2): Fehlerhafte SURFACE/AREA Attribute können 
als Linien (statt als Polygone) eingelesen werden. Die Polygonbildung muss also 
ausgeschaltet werden (``--skipPolygonBuilding``).

Um solche Daten zu importieren (um sie danach zu flicken)::
	
  java -jar ili2gpkg.jar --schemaimport --sqlEnableNull --sqlColsAsText --skipPolygonBuilding --dbfile mogis.gpkg path/to/mo.ili
  java -jar ili2gpkg.jar --import --skipReferenceErrors --skipPolygonBuilding --skipGeometryErrors --disableValidation --dbfile mogis.gpkg path/to/data.xtf


Laufzeitanforderungen
---------------------

Das Programm setzt Java 1.8 voraus.

**PostGIS:** Als Datenbank muss mindestens PostgreSQL 8.3 und PostGIS
1.5 vorhanden sein. Falls das Interlis Datenmodell INTERLIS.UUIDOID als 
OID verwendet, wird die Funktion uuid_generate_v4() verwendet. 
Dazu muss die PostgreSQL-Erweiterung uuid-ossp konfiguriert sein
(``CREATE EXTENSION "uuid-ossp";``). Mit der Option ``--setupPgExt``
erstellt ili2pg die fehlenden notwendigen Erweiterungen.

**FileGDB:** Es muss `Visual Studio 2015 C and C++ Runtimes <https://www.microsoft.com/en-us/download/details.aspx?id=48145>`_ 
installiert sein. Je nach Java Version (Die Java Version ist massgebend, nicht die Windows Version) muss 
die 32-bit oder 64-bit Version dieser Laufzeitbibliothek installiert sein. Falls diese Laufzeitbibliothek nicht 
installiert ist, gibt es einen Fehler beim laden der FileGDB.dll.
Zur Laufzeit entpackt ili2fgdb zwei DLLs/Shared-Libraries und lädt 
diese. Der Benutzer benötigt also die Berechtigungen, um diese Bibliotheken zu 
laden.

**GeoPackage:** Zur Laufzeit entpackt ili2gpkg eine DLL/Shared-Library und lädt 
diese. Der Benutzer benötigt also die Berechtigungen, um die Bibliothek zu laden.

Lizenz
------

GNU Lesser General Public License

Funktionsweise
==============

In den folgenden Abschnitten wird die Funktionsweise anhand einzelner
Anwendungsfälle beispielhaft beschrieben. Die detaillierte Beschreibung
einzelner Funktionen ist im Kapitel „Referenz“ zu finden.

Schemaimport-Funktionen
-----------------------

Fall 1.1
~~~~~~~~

Die Tabellen existieren nicht und sollen in der Datenbank angelegt
werden (``--schemaimport``).

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --dbdatabase mogis
--dbusr julia --dbpwd romeo path/to/dm01.ili``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --dbfile
mogis.gpkg path/to/dm01.ili``

**FileGDB:** ``java -jar ili2fgdb.jar --schemaimport --dbfile
mogis.gdb path/to/dm01.ili``


Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt.

**PostGIS:** Die leeren Tabellen werden im Default-Schema des Benutzers
julia angelegt. Die Geometrie-Spalten werden in der Tabelle
public.geometry\_columns registriert.

Als Host wird der lokale Rechner angenommen und für die Verbindung zur
Datenbank der Standard-Port.

**GeoPackage:** Die Geometrie-Spalten werden in den Tabellen
gpkg\_contents und gpkg\_geometry\_columns registriert.

Falls die Datei mogis.gpkg noch nicht existiert, wird sie erzeugt und
mit den für GeoPackage nötigen Metatabellen initialisiert.
Falls die Datei schon existiert, werden die Tabellen ergänzt.

**FileGDB:** Falls die Datei mogis.gdb noch nicht existiert, wird sie erzeugt.
Falls die Datei schon existiert, werden die Tabellen ergänzt.

Fall 1.2 (nur PostGIS)
~~~~~~~~~~~~~~~~~~~~~~

Das gewünschte Schema und die Tabellen existieren nicht und es soll das
DB-Schema und -Datenmodell angelegt werden:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --dbdatabase mogis
--dbschema dm01av --dbusr julia --dbpwd romeo path/to/dm01.ili``

Es werden keine Daten importiert, sondern nur das Schema dm01av (``--dbschema dm01av``) und die
leeren Tabellen angelegt. Die Geometrie-Spalten werden in der Tabelle
public.geometry\_columns registriert.

Fall 1.3
~~~~~~~~

Die Tabellen existieren nicht und sollen in der Datenbank angelegt
werden. Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --dbhost ofaioi4531 --dbport
5432 --dbdatabase mogis --dbusr julia --dbpwd romeo 
--createEnumTabs --createBasketCol --log path/to/logfile path/to/dm01.ili``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --dbfile mogis.gpkg
--createEnumTabs --createBasketCol --log path/to/logfile path/to/dm01.ili``

**FileGDB:** ``java -jar ili2fgdb.jar --schemaimport --dbfile mogis.gdb
--createEnumTabs --createBasketCol --log path/to/logfile path/to/dm01.ili``

Alle Tabellen werden in der Datenbank erstellt. 
Die Geometrie-Spalten werden registriert. Als Primary-Key
wird ein zusätzliches Attribut erstellt (t\_id). Zusätzlich wir ein
t\_basket Attribut erstellt (``--createBasketCol``). Dieses zeigt als Fremdschlüssel auf eine
Meta-Hilfstabelle (Importdatum, Benutzer, Modellname, Pfad der
Itf-Datei).

Die Aufzähltypen werden in Lookup-Tables abgebildet (``--createEnumTabs``).

Es wird ein Logfile angelegt (``--log path/to/logfile``). 
Dieses enthält Zeitpunkt des Schemaimports, Name
des Benutzers, Datenbankparameter (ohne Passwort), Name (ganzer Pfade)
der Ili-Datei, sämtliche Namen der importierten Tabellen. Allfällige Fehlermeldungen
(bei Importabbruch) werden auch in die Logdatei geschrieben.

Fall 1.4
~~~~~~~~

Enumerations werden zusätzlich als Textattribut hinzugefügt:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --createEnumTxtCol
--dbdatabase mogis --dbusr julia --dbpwd romeo path/to/dm01.ili``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --createEnumTxtCol
--dbfile mogis.gpkg path/to/dm01.ili``

**FileGDB:** ``java -jar ili2fgdb.jar --schemaimport --createEnumTxtCol
--dbfile mogis.gdb path/to/dm01.ili``

Das Modell wird in die Datenbank importiert. Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt.
Zusätzlich werden die
Attribute vom Typ Enumeration in ihrer Textrepräsentation (Attribut
„art“ = 0 ⇒ „art\_txt“ = „Gebaeude“) hinzugefügt (``--createEnumTxtCol``).

Fall 1.5
~~~~~~~~

Den Geometrien wird ein spezieller SRS (Spatial Reference System)
Identifikator hinzugefügt:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/dm01.ili``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbfile mogis.gpkg path/to/dm01.ili``

**FileGDB:** ``java -jar ili2fgdb.jar --schemaimport --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbfile mogis.gdb path/to/dm01.ili``

Das Modell wird in die Datenbank importiert. Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt.
Zusätzlich wird jeder
Geometrie eine SRS-ID (EPSG-Code 2056) hinzugefügt 
(``--defaultSrsAuth EPSG --defaultSrsCode 2056``). 
Ebenfalls wird derselbe Identifikator für
die Registrierung der Geometriespalten in den Metatabellen der Datenbank
benutzt.

Fall 1.6
~~~~~~~~

Geometrien werden indexiert:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --createGeomIdx --dbdatabase
mogis --dbusr julia --dbpwd romeo path/to/dm01.ili``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --createGeomIdx --dbfile
mogis.gpkg path/to/dm01.ili``

Das Modell wird in die Datenbank importiert. Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt.
Die Geometrien werden
indexiert (``--createGeomIdx``).

**FileGDB:** Die Geometrien sind grundsätzlich immer indexiert.

Fall 1.7
~~~~~~~~

Die Tabellen existieren nicht und sollen in der Datenbank angelegt
werden (``--schemaimport``).
Das Modell und die Abbildungsparameter ergeben sich aus der Meta-Konfiguration, die aus einem Repository bezogen wird.

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --metaConfig ilidata:metconfigId --dbdatabase
mogis --dbusr julia --dbpwd romeo``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --metaConfig ilidata:metconfigId --dbfile
mogis.gpkg``

Das Modell wird in die Datenbank importiert. Es werden keine Daten importiert, sondern nur die leeren Tabellen
angelegt.


Import-Funktionen
-----------------

Fall 2.1
~~~~~~~~

Die Tabellen existieren bereits und der Inhalt der Tabellen soll
erweitert werden (``--import``):

**PostGIS:** ``java -jar ili2pg.jar --import --dbdatabase mogis --dbusr
julia --dbpwd romeo path/to/260100.itf``

**GeoPackage:** ``java -jar ili2gpkg.jar --import --dbfile mogis.gpkg
path/to/260100.itf``

**FileGDB:** ``java -jar ili2fgdb.jar --import --dbfile mogis.gdb
path/to/260100.itf``

Das Itf 260100.itf wird importiert und die Daten den bereits vorhanden
Tabellen hinzugefügt. Die Tabellen können zusätzliche Attribute
enthalten (z.B. bfsnr, datum etc.), welche beim Import leer bleiben.

Fall 2.2
~~~~~~~~

Die Tabellen existieren bereits und der Inhalt der Tabellen soll durch
den Inhalt des itf ersetzt werden (``--import``):

**PostGIS:** ``java -jar ili2pg.jar --import --deleteData --dbdatabase
mogis --dbusr julia --dbpwd romeo --log path/to/logfile path/to/260100.itf``

**GeoPackage:** ``java -jar ili2gpkg.jar --import --deleteData --dbfile
mogis.gpkg --log path/to/logfile path/to/260100.itf``

**FileGDB:** ``java -jar ili2fgdb.jar --import --deleteData --dbfile
mogis.gdb --log path/to/logfile path/to/260100.itf``

Das Itf 260100.itf wird importiert und die bestehenden Daten in den
bereits vorhanden Tabellen gelöscht (``--deleteData``). Die Tabellen können zusätzliche
Attribute enthalten (z.B. bfsnr, datum etc.), welche beim Import leer
bleiben.

Es wird ein Logfile angelegt (``--log path/to/logfile``). Dieses enthält Zeitpunkt des Imports, Name
des Benutzers, Datenbankparameter (ohne Passwort), Name (ganzer Pfade)
der Ili- und Itf-Datei, sämtliche Namen der importierten Tabellen inkl.
Anzahl der importierten Elemente pro Tabelle. Allfällige Fehlermeldungen
(bei Importabbruch) werden auch in die Logdatei geschrieben.

Fall 2.3
~~~~~~~~

Tauchen beim Import des Itf Fehler auf (z. B. mangelnde
Modellkonformität oder verletzte Constraints in der DB), bricht der
Import ab.

**PostGIS, GeoPackage:** Bei einem Fehler werden keine Daten importiert,
d.h. der Import in die Datenbank ist ein einzelner Commit.

**FileGDB:** Da die FileGDB keine Transaktionen unterstützt, werden die Daten 
teilweise importiert, und die FileGDB befindet sich danach evtl. in einem 
inkonsistenten Zustand.

Fall 2.4
~~~~~~~~

Die Tabellen existieren bereits und der Inhalt einer Datei aus einem Repository (z.B. eine Katalog-Datei) soll importiert werden:

**PostGIS:** ``java -jar ili2pg.jar --import --dbdatabase mogis --dbusr
julia --dbpwd romeo ilidata:dataId``

**GeoPackage:** ``java -jar ili2gpkg.jar --import --dbfile mogis.gpkg
--metaConfig ilidata:dataId``

**FileGDB:** ``java -jar ili2fgdb.jar --import --dbfile mogis.gdb
--metaConfig ilidata:dataId``

Die Daten mit der gegebenen ``dataId`` werden aus dem Repository bezogen und den bereits vorhanden
Tabellen hinzugefügt. 

Fall 2.5
~~~~~~~~

Die Tabellen existieren bereits und die Daten gemäss einer Meta-Konfiguration sollen importiert werden:

**PostGIS:** ``java -jar ili2pg.jar --import --dbdatabase mogis --dbusr
julia --dbpwd romeo --metaConfig ilidata:metconfigId``

**GeoPackage:** ``java -jar ili2gpkg.jar --import --dbfile mogis.gpkg
--metaConfig ilidata:metconfigId``

**FileGDB:** ``java -jar ili2fgdb.jar --import --dbfile mogis.gdb
--metaConfig ilidata:metconfigId``

Die Meta-Konfiguration mit der gegebenen ``metconfigId`` wird aus dem Repository bezogen.
Die Referenz-/Katalog-Daten ergeben sich aus der Meta-Konfiguration und werden den bereits vorhanden
Tabellen hinzugefügt. 


Export-Funktionen
-----------------

Fall 3.1
~~~~~~~~

Die Tabellen werden aus der Datenbank in eine Interlis 1-Transfer-Datei
geschrieben (``--export``):

**PostGIS:** ``java -jar ili2pg.jar --export --models DM01AV --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/output.itf``

**GeoPackage:** ``java -jar ili2gpkg.jar --export --models DM01AV --dbfile
mogis.gpkg path/to/output.itf``

**FileGDB:** ``java -jar ili2fgdb.jar --export --models DM01AV --dbfile
mogis.gdb path/to/output.itf``

Die Daten aller Tabellen des Interlis-Modells DM01AV (``--models DM01AV``) 
werden in die
Interlis 1-Transferdatei output.itf geschrieben. Fehlende Tabellen in
der Datenbank werden dementsprechend als leere Tabellen oder gar nicht
(gemäss Definition im Datenmodell) in die Datei geschrieben. Fehlende
Attribute in einer Datenbanktabelle werden mit einem „@“ substituiert.

Anhand des Parameters --models wird definiert, welche Daten exportiert
werden. Alternativ kann auch der Parameter --topics, --baskets oder --dataset
verwendet werden, um die zu exportierenden Daten auszuwählen. Einer
dieser Parameter muss also zwingend beim Export angegeben werden.

Fall 3.2
~~~~~~~~

Die Tabellen werden aus der Datenbank in eine Interlis 2-Transfer-Datei
geschrieben (``--export``):

**PostGIS:** ``java -jar ili2pg.jar --export --models DM01AV --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/output.xtf``

**GeoPackage:** ``java -jar ili2gpkg.jar --export --models DM01AV --dbfile
mogis.gpkg path/to/output.xtf``

**FileGDB:** ``java -jar ili2fgdb.jar --export --models DM01AV --dbfile
mogis.gdb path/to/output.xtf``

Die Daten aller Tabellen des Interlis-Modells DM01AV (``--models DM01AV``) 
werden in die
Interlis 2-Transferdatei output.xtf geschrieben. Fehlende Tabellen und
Attribute in der Datenbank werden gar nicht in die Datei geschrieben.

Anhand des Parameters --models wird definiert, welche Daten exportiert
werden. Alternativ kann auch der Parameter --topics, --baskets oder --dataset
verwendet werden, um die zu exportierenden Daten auszuwählen. Einer
dieser Parameter muss also zwingend beim Export angegeben werden.

Prüf-Funktionen
-----------------

Fall 4.1
~~~~~~~~

Die Daten in der Datenbank werden anhand des Interlis-Modells geprüft (``--validate``):

**PostGIS:** ``java -jar ili2pg.jar --validate --models DM01AV --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo``

**GeoPackage:** ``java -jar ili2gpkg.jar --validate --models DM01AV --dbfile
mogis.gpkg``

**FileGDB:** ``java -jar ili2fgdb.jar --validate --models DM01AV --dbfile
mogis.gdb``

Anhand des Parameters --models wird definiert, welche Daten geprüft
werden. Alternativ kann auch der Parameter --topics, --baskets oder --dataset
verwendet werden, um die zu prüfenden Daten auszuwählen. Einer
dieser Parameter muss also zwingend beim Prüfen angegeben werden.

Migration von 3.x nach 4.x
--------------------------
Die von ili2b 4.x benutzten Schemaabbildungsregeln sind zum Teil nicht 
kompatibel mit den Regeln von ili2db 3.x.
Das einfachste für die Datenmigration ist darum:

- Daten mit 3.x exportieren

- Schema mit 4.x anlegen

- Daten mit 4.x importieren

Ab ili2db 4.1 gibt es eine Option ``--export3`` um Daten aus einer mit 3.x angelegten 
DB zu exportieren.

Die wichtigsten Optionen, um zu 3.x kompatibles Verhalten zu erhalten sind:

- ``--createTidCol``  damit ``--importTid`` und ``--exportTid`` funktioniert

- ``--doSchemaImport`` damit ``--import`` auch die Tabellen anlegt

- ``--ver3-translation`` um bei Modellen mit ``TRANSLATION OF`` die 3.x Tabellen zu erhalten

Fall 5.1
~~~~~~~~

Die Tabellen existieren nicht und sollen in der Datenbank angelegt
werden und die Daten sollen importiert werden (``--import``):

**PostGIS:** ``java -jar ili2pg.jar --import --doSchemaImport --dbhost ofaioi4531 --dbport
5432 --dbdatabase mogis --dbusr julia --dbpwd romeo 
--createEnumTabs --createBasketCol --log path/to/logfile path/to/260100.itf``

**GeoPackage:** ``java -jar ili2gpkg.jar --import --doSchemaImport --dbfile mogis.gpkg
--createEnumTabs --createBasketCol --log path/to/logfile path/to/260100.itf``

**FileGDB:** ``java -jar ili2fgdb.jar --import --doSchemaImport --dbfile mogis.gdb
--createEnumTabs --createBasketCol --log path/to/logfile path/to/260100.itf``

Alle Tabellen werden in der Datenbank erstellt (``--doSchemaImport``) und das Itf 260100.itf
importiert. Die Geometrie-Spalten werden registriert. Als Primary-Key
wird ein zusätzliches Attribut erstellt (t\_id). Zusätzlich wir ein
t\_basket Attribut erstellt (``--createBasketCol``). Dieses zeigt als Fremdschlüssel auf eine
Meta-Hilfstabelle (Importdatum, Benutzer, Modellname, Pfad der
Itf-Datei).

Die Aufzähltypen werden in Lookup-Tables abgebildet (``--createEnumTabs``).

Es wird ein Logfile angelegt (``--log path/to/logfile``). Dieses enthält Zeitpunkt des Imports, Name
des Benutzers, Datenbankparameter (ohne Passwort), Name (ganzer Pfade)
der Ili- und Itf-Datei, sämtliche Namen der importierten Tabellen inkl.
Anzahl der importierten Elemente pro Tabelle. Allfällige Fehlermeldungen
(bei Importabbruch) werden auch in die Logdatei geschrieben.

Meta-Konfig-Funktionen
----------------------

Fall 6.1
~~~~~~~~

Die Konfiguration mit der die Datenbank erstellt wurde, wird als INI-Datei exportiert (``--exportMetaConfig``):

**PostGIS:** ``java -jar ili2pg.jar --exportMetaConfig --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo --metaConfig  config.ini``

**GeoPackage:** ``java -jar ili2gpkg.jar --exportMetaConfig --dbfile
mogis.gpkg --metaConfig  config.ini``

**FileGDB:** ``java -jar ili2fgdb.jar --exportMetaConfig --dbfile
mogis.gdb --metaConfig  config.ini``

Die erstellte INI-Datei ist nicht ganz vollständig. iliMetaAttrs, pre- und postScript werden nicht berücksichtigt.

Mit Hilfe der INI-Datei kann danach die Datenbank neu erstellt werden:

**PostGIS:** ``java -jar ili2pg.jar --schemaimport --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogisNeu --dbusr julia --dbpwd romeo --metaConfig  config.ini``

**GeoPackage:** ``java -jar ili2gpkg.jar --schemaimport --dbfile
mogisNeu.gpkg --metaConfig  config.ini``

**FileGDB:** ``java -jar ili2fgdb.jar --schemaimport --dbfile
mogisNeu.gdb --metaConfig  config.ini``

Referenz
========

In den folgenden Abschnitten werden einzelne Aspekte detailliert, aber
isoliert, beschrieben. Die Funktionsweise als Ganzes wird anhand
einzelner Anwendungsfälle beispielhaft im Kapitel „Funktionsweise“
(weiter oben) beschrieben.

Die Dokumentation gilt grundsätzlich für alle ili2xy Varianten, ausser es 
gibt einen spezifischen Hinweis auf PostGIS, GeoPackage oder FileGDB.

Aufruf-Syntax
-------------

**PostGIS:** ``java -jar ili2pg.jar [Options] [file]``

**GeoPackage:** ``java -jar ili2gpkg.jar [Options] [file]``

**FileGDB:** ``java -jar ili2fgdb.jar [Options] [file]``

Der Rückgabewert ist wie folgt:

  - 0 import/export ok, keine Fehler festgestellt
  - !0 import/export nicht ok, Fehler festgestellt

Optionen:

+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Option                        | Beschreibung                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
+===============================+============================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================+
| --import                      | Importiert Daten aus einer Transferdatei in die Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Die Tabellen werden implizit auch angelegt, falls sie noch nicht vorhanden sind (siehe Kapitel Abbildungsregeln). Falls die Tabellen in der Datenbank schon vorhanden sind, können sie zusätzliche Spalten enthalten (z.B. bfsnr, datum etc.), welche beim Import leer bleiben.                                                                                                                                                                                                                                                            |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Falls beim Import ein Datensatz-Identifikator (--dataset) definiert wird, darf dieser Datensatz-Identifikator in der Datenbank noch nicht vorhanden sein. Um die bestehenden Daten zu ersetzen, kann die Option --replace verwendet werden.                                                                                                                                                                                                                                                                                                |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | TODO Die Tabellen sind schon vorhanden (und entsprechen (nicht) der ili-Klasse)                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --update                      | Aktualisiert die Daten in der Datenbank anhand einer Transferdatei, d.h. neue Objekte werden eingefügt, bestehende Objekte werden aktualisiert und in der Transferdatei nicht mehr vorhandene Objekte werden gelöscht. Diese Funktion bedingt, dass das Datenbankschema mit der Option --createBasketCol erstellt wurde, und dass die Klassen und Topics eine stabile OID haben.                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --replace                     | Ersetzt die Daten in der Datenbank anhand eines Datensatz-Identifikators (--dataset) mit den Daten aus einer Transferdatei. Diese Funktion bedingt, dass das Datenbankschema mit der Option --createBasketCol erstellt wurde.                                                                                                                                                                                                                                                                                                              |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --delete                      | Löscht die Daten in der Datenbank anhand eines Datensatz-Identifikators (--dataset). Diese Funktion bedingt, dass das Datenbankschema mit der Option --createBasketCol erstellt wurde.                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --export                      | Exportiert Daten aus der Datenbank in eine Transferdatei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Mit dem Parameter --models, --topics, --baskets oder --dataset wird definiert, welche Daten exportiert werden.                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                               | Existieren zum gegeben Modell (--models) oder Topic (--topics) in der DB keine Daten, wird ohne Fehlermeldung eine leere Transferdatei erstellt.                                                                                                                                                                                                                                                                                                                                                                                           |
|                               | Wird eine Datensatz (--dataset) oder Basket (--baskets) bezeichnet, der in der DB nicht vorhanden ist, bricht der Export mit einem Fehler ab.                                                                                                                                                                                                                                                                                                                                                                                              |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Ob die Daten im Interlis 1-, Interlis 2- oder GML-Format geschrieben werden, ergibt sich aus der Dateinamenserweiterung der Ausgabedatei. Für eine Interlis 1-Transferdatei muss die Erweiterung .itf verwendet werden. Für eine GML-Transferdatei muss die Erweiterung .gml verwendet werden.                                                                                                                                                                                                                                             |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Die Optionen --topics und --baskets bedingen, dass das Datenbankschema mit der Option --createBasketCol erstellt wurde.                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --export3                     | Exportiert Daten aus einer Datenbank die mit ili2db 3.x angelegt wurde in eine Transferdatei.                                                                                                                                                                                                                                                                                                                                                                                                                                              |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --validate                    | Prüft die Daten in der Datenbank (ohne Export in eine Transferdatei).                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Mit dem Parameter --models, --topics, --baskets oder --dataset wird definiert, welche Daten geprüft werden.                                                                                                                                                                                                                                                                                                                                                                                                                                |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Die Optionen --topics und --baskets bedingen, dass das Datenbankschema mit der Option --createBasketCol erstellt wurde.                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --schemaimport                | Erstellt die Tabellenstruktur in der Datenbank (siehe Kapitel Abbildungsregeln).                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --exportMetaConfig            | Die Konfiguration mit der die Datenbank erstellt wurde, wird als INI-Datei exportiert.                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Mit dem Parameter --metaConfig wird die Ziel-Datei definiert.                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                               | Die erstellte INI-Datei ist nicht ganz vollständig. iliMetaAttrs, pre- und postScript werden nicht berücksichtigt.                                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --iliMetaAttrs filename       | Name der Konfigurationsdatei, die zusätzliche Interlis-Metaattribute enthält (Meta-Attribute, die in den ili-Dateien nicht enthalten sind).                                                                                                                                                                                                                                                                                                                                                                                                |
|                               | ``filename`` kann auch die Form ``ilidata:DatesetId``  haben,                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                               | dann wird die entsprechende Datei aus den Repositories benutzt.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Die Konfigurationsdatei ist Zeilenorientiert und besteht aus Abschnitten. Pro Modellelement gibt es einen Abschnitt. Der Abschnitt beginnt mit dem qualifizierten Elementnamen in eckigen Klammern. Innerhalb des Abschnitts sind die Metaattribute zu diesem Modellelement. Beispiel::                                                                                                                                                                                                                                                    |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               |   [Model1.Topic1.Structure1]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               |   MetaAttr1=AttrValue1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|                               |   MetaAttr2=AttrValue2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|                               |   [Model1.Topic1.ClassA.AttrB]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                               |   MetaAttrN=AttrValueN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ``--metaConfig  filename``    | Konfiguriert ili2db mit Hilfe einer INI-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                               | ``filename`` kann auch die Form ``ilidata:DatesetId``  haben,                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                               | dann wird die entsprechende Datei aus den Repositories benutzt.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Der Eintrag im ilidata.xml soll mit folgenden Kategorien markiert werden.                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               |   .. code:: xml                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               |      <categories>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|                               |        <DatasetIdx16.Code_>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|                               |          <value>http://codes.interlis.ch/type/metaconfig</value> <!-- Hinweis, dass es eine Meta-Config-Datei ist.  -->                                                                                                                                                                                                                                                                                                                                                                                                                    |
|                               |        </DatasetIdx16.Code_>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               |        <DatasetIdx16.Code_>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|                               |          <value>http://codes.interlis.ch/model/Simple23</value> <!-- Hinweis auf des ili-Modell Simple23 -->                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               |        </DatasetIdx16.Code_>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               |      </categories>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --validConfig filename        | Name der Konfigurationsdatei, die für die Validierung verwendet werden soll.                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               | ``filename`` kann auch die Form ``ilidata:DatesetId``  haben,                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                               | dann wird die entsprechende Datei aus den Repositories benutzt.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableValidation           | Schaltet die Validierung der Daten aus.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableAreaValidation       | Schaltet die Validierung der AREA Topologie aus.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --forceTypeValidation         | Beschränkt die Aufweichung der Validierung mittels --validConfig auf "multiplicity".                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableBoundaryRecoding     | Deaktiviert beim Import die Umcodierung von Randlinien von Flächen (damit sie OGC/ISO konform sind)                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbhost host                 | **PostGIS:** Der hostname der Datenbank. Default ist localhost.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbport port                 | **PostGIS:** Die Port-Nummer, unter der die Datenbank angesprochen warden kann. Default ist 5432.                                                                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbdatabase database         | **PostGIS:** Der Name der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbusr username              | **PostGIS:** Der Benutzername für den Datenbankzugang und Einträge in Metatabellen.                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | **GeoPackage:** Der Benutzername für Einträge in Metatabellen.                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbpwd password              | **PostGIS:** Das Passwort für den Datenbankzugriff.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbparams filename           | Datei (UTF-8 codiert) mit zusätzlichen Parametern für den Datenbankzugriff. Einfaches zeilenorientiertes Format mit Parameter=Wert pro Zeile. Die möglichen Parameter sind beim jeweiligen JDBC Treiber beschrieben.                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbschema schema             | **PostGIS:** Definiert den Namen des Datenbank-Schemas. Default ist kein Wert, d.h. das aktuelle Schema des Benutzers der mit –user definiert wird.                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbfile filename             | **GeoPackage:** Name der GeoPackage-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | **FileGDB:** Name der ESRI File Geodatabase-Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --setupPgExt                  | **PostGIS:** erstellt postgreql Erweiterungen 'uuid-ossp' und 'postgis' (falls noch nicht vorhanden)                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableRounding             | Beim Import und Export werden die Daten per Default gerundet gem. Angaben im Modell. Mit dieser Option findet keine Rundung statt.                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --deleteData                  | bei einem Datenimport (--import) werden alle Daten in den existierenden/benutzten Tabellen gelöscht (Mit DELETE, die Tabellenstruktur bleibt unverändert).                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --defaultSrsAuth auth         | SRS Authority für Geometriespalten, wo sich dieser Wert nicht ermitteln lässt (für ili1 und ili2.3 immer der Fall). Gross-/Kleinschreibung ist signifikant. Default ist EPSG                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --defaultSrsCode code         | SRS Code für Geometriespalten, wo sich dieser Wert nicht ermitteln lässt. Kein Default                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --modelSrsCode model=epsgCode | SRS Code für Geometriespalten des gegebenen Modells, wo sich dieser Wert nicht pro Attribut ermitteln lässt. Mehrere Definitionen können durch Strichpunkt getrennt wrden, z.B.: --modelSrsCode ModelA=2056;ModelB=21781                                                                                                                                                                                                                                                                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --fgdbXyResolution value      | **FileGDB:** XY-Auflösung für Geometriespalten                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --fgdbXyTolerance value       | **FileGDB:** XY-Toleranz für Geometriespalten                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --modeldir path               | Dateipfade, die Modell-Dateien (ili-Dateien) enthalten. Mehrere Pfade können durch Semikolon ‚;‘ getrennt werden. Es sind auch URLs von Modell-Repositories möglich. Default ist                                                                                                                                                                                                                                                                                                                                                           |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %ILI\_FROM\_DB;%XTF\_DIR;http://models.interlis.ch/;%JAR\_DIR                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Es werden folgende Platzhalter unterstützt:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %ILI\_FROM\_DB ist ein Platzhalter für die in der Datenbank vorhandenen Modelle (in der Tabelle t\_ili2db\_model).                                                                                                                                                                                                                                                                                                                                                                                                                         |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %XTF\_DIR ist ein Platzhalter für das Verzeichnis mit der Transferdatei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %JAR\_DIR ist ein Platzhalter für das Verzeichnis des ili2db Programms (ili2pg.jar bzw. ili2gpkg.jar Datei).                                                                                                                                                                                                                                                                                                                                                                                                                               |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | %ILI_FROM_DB sollte i.d.R. der erste Pfad sein (damit mehrere Imports und Exports das selbe Modell verwenden).                                                                                                                                                                                                                                                                                                                                                                                                                             |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Der erste Modellname (Hauptmodell), zu dem ili2db die ili-Datei sucht, ist nicht von der INTERLIS-Sprachversion abhängig. Es wird in folgender Reihenfolge nach einer ili-Datei gesucht: zuerst INTERLIS 2.3, dann 1.0 und zuletzt 2.2.                                                                                                                                                                                                                                                                                                    |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Beim Auflösen eines IMPORTs wird die INTERLIS Sprachversion des Hauptmodells berücksichtigt, so dass also z.B. das Modell Units für ili2.2 oder ili2.3 unterschieden wird.                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --models modelname            | Namen des Modells (nicht zwingend identisch mit dem Dateinamen!), für das die Tabellenstruktur in der Datenbank erstellt werden soll. Mehrere Modellnamen können durch Semikolon ‚;‘ getrennt werden. Normalerweise muss der Namen nicht angegeben werden, und das Programm ermittelt den Wert automatisch aus den Daten. Wird beim --schemaimport nur eine ili-Datei als file angegeben, wird der Name des letzten Modells aus dieser ili-Datei als modelname genommen.                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dataset name                | Name/Identifikator des Datensatzes (Kurzform für mehrere BIDs). Kann z.B. eine BFSNr oder ein Kantonskürzel sein. Beim Daten Export können mehrere Datensatznamen durch Semikolon ‚;‘ getrennt werden. Bedingt die Option --createBasketCol.                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --baskets BID                 | BID der Baskets, die importiert, exportiert oder validiert werden sollen. Mehrere BIDs können durch Semikolon ‚;‘ getrennt werden.                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --topics topicname            | Topic-Namen der Baskets, die importiert, exportiert oder validiert werden sollen. Mehrere Namen können durch Semikolon ‚;‘ getrennt werden. Es muss der qualifizierte Topic-Name (Model.Topic) verwendet werden.                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createscript filename       | Erstellt zusätzlich zur Tabellenstruktur in der Datenbank ein SQL-Skript um die Tabellenstruktur unabhängig vom Programm erstellen zu können. Das Skript wird zusätzlich zu den Tabellen in der Datenbank erzeugt, d.h. es ist nicht möglich, nur das Skript zu erstellen (ohne Datenbank).                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dropscript filename         | Erstellt ein SQL-Skript um die Tabellenstruktur unabhängig vom Programm löschen zu können.                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --preScript filename          | SQL-Skript, das vor dem (Schema-)Import/Export ausgeführt wird.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --postScript filename         | SQL-Skript, das nach dem (Schema-)Import/Export ausgeführt wird.                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --noSmartMapping              | Alle strukturellen Abbildungsoptimierungen werden ausgeschaltet. (s.a. --smart1Inheritance, --coalesceCatalogueRef, --coalesceMultiSurface, --coalesceMultiLine, --coalesceMultiPoint, --expandMultilingual, --expandLocalised, --coalesceArray)                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --smart1Inheritance           | Bildet die Vererbungshierarchie mit einer dymamischen Strategie ab. Für Klassen, die referenziert werden und deren Basisklassen nicht mit einer NewClass-Strategie abgebildet werden, wird die NewClass-Strategie verwendet. Abstrakte Klassen werden mit einer SubClass-Strategie abgebildet. Konkrete Klassen, ohne Basisklasse oder deren direkte Basisklassen mit einer SubClass-Strategie abgebildet werden, werden mit einer NewClass-Strategie abgebildet. Alle anderen Klassen werden mit einer SuperClass-Strategie abgebildet.   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --smart2Inheritance           | Bildet die Vererbungshierarchie mit einer dymamischen Strategie ab. Abstrakte Klassen werden mit einer SubClass-Strategie abgebildet. Konkrete Klassen werden mit einer NewAndSubClass-Strategie abgebildet.                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceCatalogueRef        | Strukturattribute deren maximale Kardinalität 1 ist, deren Basistyp CHBase:CatalogueReference oder CHBase:MandatoryCatalogueReference ist und die ausser „Reference“ keine weiteren Attribute haben, werden direkt mit einem Fremdschlüssel auf die Ziel-Tabelle (die die konkrete CHBase:Item Klasse realisiert) abgebildet, d.h. kein Record in der Tabelle für die Struktur mit dem „Reference“ Attribut.                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceMultiSurface        | Strukturattribute deren maximale Kardinalität 1 ist, deren Basistyp CHBase:MultiSurface ist und die ausser „Surfaces“ keine weiteren Attribute haben, werden direkt als Spalte mit dem Typ MULTISURFACE (oder MULTIPOLYGON, falls --strokeArcs) abgebildet.                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceMultiLine           | Strukturattribute deren maximale Kardinalität 1 ist, deren Basistyp CHBase:MultiLine ist und die ausser „Lines“ keine weiteren Attribute haben, werden direkt als Spalte mit dem Typ MULTICURVE (oder MULTILINESTRING, falls --strokeArcs) abgebildet.                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceMultiPoint          | Strukturattribute deren maximale Kardinalität 1 ist, die nur ein Attribut haben, werden direkt als Spalte mit dem Typ MULTIPOINT abgebildet.                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceArray               | Strukturattribute mit dem Metaattribut ``ili2db.mapping=ARRAY``, die nur ein Attribut haben, werden direkt als Spalte mit dem Typ ARRAY abgebildet.                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceJson                | Strukturattribute mit dem Metaattribut ``ili2db.mapping=JSON``, werden direkt als Spalte mit dem Typ JSON abgebildet.                                                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --expandStruct                | Strukturattribute mit dem Metaattribut ``ili2db.mapping=EXPAND`` und maximaler Kardinalität<=5, werden ausmultipliziert, d.h. die Attribute der Struktur werden direkt als Spalten der Tabelle des Strukturattributes abgebildet.                                                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --expandMultilingual          | Strukturattribute deren maximale Kardinalität 1 ist, deren Basistyp LocalisationCH\_V1.MultilingualText oder LocalisationCH\_V1.MultilingualMText ist (oder das Metaattribut ili2db.mapping=Multilingual haben) und die ausser „LocalisedText“ keine weiteren Attribute haben, werden direkt als Spalten in der Tabelle des Strukturattributes abgebildet, d.h. keine Records in den Tabellen für die Multilingual-Strukturen.                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --expandLocalised             | Strukturattribute deren maximale Kardinalität 1 ist, deren Basistyp LocalisationCH\_V1.LocalisedText oder LocalisationCH\_V1.LocalisedMText (oder das Metaattribut ili2db.mapping=Localised haben) ist und die ausser „Language“ und „Text“ keine weiteren Attribute haben, werden direkt als Spalten in der Tabelle des Strukturattributes abgebildet, d.h. keine Records in den Tabellen für die Multilingual-Strukturen.                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createGeomIdx               | Erstellt für jede Geometriespalte in der Datenbank einen räumlichen Index. (siehe Kapitel Abbildungsregeln/Geometrieattribute)                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumColAsItfCode      | Bildet bei Aufzählungsattributen den Aufzählungswert als ITF-Code ab. Diese Option ist nur zulässig, wenn im Modell keine Erweiterungen von Aufzählungen vorkommen. Ohne diese Option wird der XTF-Code als Aufzählwert in der Datenbank verwendet. (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumTxtCol            | Erstellt für Aufzählungsattribute eine zusätzliche Spalte mit dem Namen des Aufzählwertes. (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                                                                                                                                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumTabs              | Erstellt pro Aufzählungsdefinition eine Tabelle mit den einzelnen Aufzählwerten. (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createSingleEnumTab         | Erstellt eine einzige Tabelle mit allen Aufzählwerten aller Aufzählungsdefinitionen. (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumTabsWithId        | Erstellt pro Basis-Aufzählungsdefinition eine Tabelle mit den einzelnen Aufzählwerten, inkl. aller Aufzählungserweiterungen von dieser Basisdefinition.                                                                                                                                                                                                                                                                                                                                                                                    |
|                               | So können auch Fremdschlüssel (--createFk) definiert werden. (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createMetaInfo              | Erstellt zusätzliche Meta-Tabellen T_ILI2DB_TABLE_PROP, T_ILI2DB_COLUMN_PROP, T_ILI2DB_META_ATTRS mit weiteren Angaben aus dem Interlis Modell. (siehe Kapitel Metadaten)                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createNlsTab                | Erstellt eine zusätzliche Meta-Tabelle T_ILI2DB_NLS mit weiteren Angaben aus dem Interlis Modell für mehrsprachige Anwendungen.                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --beautifyEnumDispName        | Verschönert den Anzeigetext für das Aufzählelement. Beim Import wird die Spalte mit dem XTF-Code ohne Untersstriche befüllt ("Strasse befestigt" statt "Strasse_befestigt") (siehe Kapitel Abbildungsregeln/Aufzählungen)                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createStdCols               | Erstellt in jeder Tabelle zusätzliche Metadatenspalten T\_User, T\_CreateDate, T\_LastChange. (siehe Kapitel Abbildungsregeln/Tabellen)                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --t\_id\_Name name            | Definiert den Namen für die interne technische Schlüsselspalte in jeder Tabelle (nicht zu verwechseln mit dem externen Transferidentifikator). Default ist T\_Id. (siehe Kapitel Abbildungsregeln/Tabellen)                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --idSeqMin zahl               | **PostGIS:** Definiert den Minimalwert für den Generator der internen technischen Schlüssel                                                                                                                                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --idSeqMax zahl               | **PostGIS:** Definiert den Maximalwert für den Generator der internen technischen Schlüssel                                                                                                                                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createTypeDiscriminator     | Erstellt für jede Tabelle (auch wenn das Modell keine Vererbung benutzt) eine Spalte für den Typdiskriminator. Für Klassen mit Vererbung wird die Spalte immer erstellt. (siehe Kapitel Abbildungsregeln/Tabellen)                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableNameOptimization     | Schaltet die Nutzung von unqualifizierten Klassennamen aus. Für alle Tabellennamen werden qualifizierte Interlis-Klassennamen (Model.Topic.Class) verwendet (und in einen gültigen Tabellennamen abgebildet). (siehe Kapitel Abbildungsregeln/Namenskonventionen)                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --nameByTopic                 | Für alle Tabellennamen werden teilweise qualifizierte Interlis-Klassennamen (Topic.Class) verwendet (und in einen gültigen Tabellennamen abgebildet). (siehe Kapitel Abbildungsregeln/Namenskonventionen)                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --nameLang lang               | Für alle Tabellen- und Spaltennamen werden Namen aus dem Interlis-Modell der gegebenen Sprache verwendet. Die möglichen Sprachnamen ergeben sich aus den Interlis-Modellen (``MODEL Name (lang) ...``).                                                                                                                                                                                                                                                                                                                                    |
|                               | Mehrere Sprachen können durch Semikolon getrennt werden, um die Priorität zu regeln. Ist für einen Namen kein Modell in einer der gegebenen Sprache vorhanden, wird der Namen aus dem Modell in der Ursprungssprache verwendet.                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --maxNameLength length        | Definiert die maximale Länge der Namen für Datenbankelemente (Tabellennamen, Spaltennamen , usw.) Default ist 60. Ist der Interlis-Name länger, wird er gekürzt. (siehe Kapitel Abbildungsregeln/Namenskonventionen)                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --sqlEnableNull               | Erstellt keine NOT NULL Anweisungen bei Spalten die Interlis-Attribute abbilden. (siehe Kapitel Abbildungsregeln/Attribute)                                                                                                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --sqlExtRefCols               | Erstellt Spalten die Interlis-Rollen oder Interlis-Referenz-Attribute abbilden als Textspalten, so dass der Referenzwert aus der Transferdatei in der Spalte aufgenommen werden kann und die referenzierten Objekte nicht vorliegen müssen. (siehe Kapitel Abbildungsregeln/Beziehungen)                                                                                                                                                                                                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --strokeArcs                  | Segmentiert Kreisbogen beim Datenimport. Der Radius geht somit verloren. Die Kreisbogen werden so segmentiert, dass die Abweichung der erzeugten Geraden kleiner als die Koordinatengenauigkeit der Stützpunkte ist.                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --oneGeomPerTable             | **PostGIS:** Erzeugt Hilfstabellen, falls in einer Klasse/Tabelle mehr als ein Geometrie-Attribut ist, so dass pro Tabelle in der Datenbank nur eine Geometriespalte ist.                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --gpkgMultiGeomPerTable       | **GeoPackage:** Erzeugt mehrere Geometriespalten pro Tabelle, falls in einer Klasse/Tabelle mehr als ein Geometrie-Attribut ist.                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --skipPolygonBuilding         | Bei ITF-Dateien werden die Linientabellen gelesen, so wie sie in der ITF-Datei sind, d.h. es werden keine Polygon gebildet.                                                                                                                                                                                                                                                                                                                                                                                                                |
|                               | Bei XTF-Dateien werden die Polygone (SURFACE/AREA) als Linien eingelesen, d.h. es können auch offene Ränder oder sich kreuzende Randlinien eingelesen werden, um sie zu korrigieren.                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --skipGeometryErrors          | Geometry Fehler werden ignoriert (und nicht rapportiert). Spezifischere Fehlermeldungen müssen mittels --validConfig konfiguriert werden.                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --skipReferenceErrors         | Referenzfehler (z.B. Verweise auf nicht vorhandene Objekte) werden ignoriert (und nicht rapportiert). Die Option bedingt, dass der Schema Import mit --sqlEnableNull erfolgte, damit die fehlenden Referenzen beim Insert auf der DB nicht zu einem NULL Constraint Fehler führen.                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --keepAreaRef                 | Bei ITF-Dateien wird für AREA Attribute der Gebietsreferenzpunkt als zusätzliche Spalte in der Tabelle eingefügt.                                                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createTidCol                | Erstellt in jeder Tabelle eine zusätzlich Spalte T\_Ili\_Tid. (siehe Kapitel Abbildungsregeln/Tabellen)                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --importTid                   | Liest die Transferidentifikation (TID aus der Transferdatei) in eine zusätzliche Spalte T\_Ili\_Tid. (siehe Kapitel Abbildungsregeln/Tabellen). Bedingt beim Schema Import die Option --createTidCol.                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --exportTid                   | Verwendet den Wert der Spalte T\_Ili\_Tid als Transferidentifikation (TID in der Transferdatei). (siehe Kapitel Abbildungsregeln/Tabellen). Bedingt beim Schema Import die Option --createTidCol.                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --importBid                   | Liest die Behälteridentifikation (BID aus der Transferdatei) in die Spalte T\_Ili\_Tid der Tabelle t\_ili2db\_basket.                                                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --exportFetchSize rows        | Definiert die Anzahl Records pro SQL Abfrage beim Export aus der DB.                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --importBatchSize rows        | Definiert die Anzahl Records pro SQL-Insert/-Update beim Import in die DB.                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createBasketCol             | Erstellt in jeder Tabelle eine zusätzlich Spalte T\_basket um den Behälter identifizieren zu können. (siehe Kapitel Abbildungsregeln/Metadaten)                                                                                                                                                                                                                                                                                                                                                                                            |
|                               | Beim ``--schemaimport`` von Modellen mit erweiterten TOPICs muss die Option benutzt werden, damit die Daten korrekt exportiert werden können.                                                                                                                                                                                                                                                                                                                                                                                              |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createDatasetCol            | Erstellt in jeder Tabelle eine zusätzlich Spalte T\_datasetname mit dem Namen/Identifikator des Datensatzes. Die Option bedingt die Option --dataset. Die Spalte ist redundant zur Spalte datasetname der Tabelle t_ili2db_dataset (siehe Kapitel Abbildungsregeln/Metadaten).                                                                                                                                                                                                                                                             |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createFk                    | Erzeugt eine Fremdschlüsselbedingung bei Spalten die Records in anderen Tabellen referenzieren.                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createFkIdx                 | Erstellt für jede Fremdschlüsselpalte in der Datenbank einen Index. Kann auch ohne die Option --createFk benutzt werden.                                                                                                                                                                                                                                                                                                                                                                                                                   |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createUnique                | Erstellt für INTERLIS-UNIQUE-Constraints in der Datenbank UNIQUE Bedingungen (sofern abbildbar).                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createNumChecks             | Erstellt für numerische Datentypen CHECK-Constraints in der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createTextChecks            | Erstellt für Text Datentypen CHECK-Constraints in der Datenbank. (nicht nur Leerzeichen; bei TEXT keine Zeilenumbrüche)                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createDateTimeChecks        | Erstellt für Datum und Zeit Datentypen CHECK-Constraints in der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createMandatoryChecks       | Erstellt für NULLable FK Spalten die MANDATORRY Referenzattribute oder Rollen implementieren CHECK-Constraints in der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createTypeConstraint        | Erstellt für die t\_type Spalte ein CHECK-Constraint in der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --plugins folder              | Verzeichnis mit JAR-Dateien, die Zusatzfunktionen enthalten. Die Zusatzfunktionen müssen das Java-Interface ``ch.interlis.iox_j.validator.InterlisFunction`` implementieren, und der Name der Java-Klasse muss mit ``IoxPlugin`` enden.                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --sqlColsAsText               | Bildet alle (einfachen/unstrukturierten) Interlis-Attribute als TEXT-Spalten ab, so dass fehlerhafte Daten importiert werden können.                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createImportTabs            | Erstellt die t\_ili2db\_import Tabellen in der Datenbank.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --doSchemaImport              | Beim Datenimport werden die Tabellen angelegt, d.h. es muss nicht zuerst ein --schemaimport gemacht werden.                                                                                                                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ver4-noSchemaImport         | Nicht mehr verwenden, wird entfernt. Beim Datenimport wird keine Tabellen angelegt, d.h. es muss zuerst explizit ein --schemaimport gemacht werden.                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ver3-translation            | Verwendet ili2db 3.x Abbildungsregeln für übersetzte Modelle (Inkompatibel mit ili2db 4.x Abbildungen).                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ver4-translation            | Nicht mehr verwenden, wird entfernt. Verwendet ili2db 4.x Abbildungsregeln für übersetzte Modelle (Inkompatibel mit ili2db 3.x Abbildungen).                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --translation modelT=modelU   | Definiert bei übersetzten INTERLIS 1 Modellen (modelT), das Modell der Ursprungssprache (ModelU). Mehrere Übersetzungen können durch Strichpunkt getrennt wrden, z.B.: --translation modelT1=modelU;modelT2=modelU                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --exportModels modelname      | Beim Export/Prüfen werden die Daten gem. dem gegebenen Export-Modell exportiert/geprüft. Ohne die Option ``--exportModels`` werden die Daten so wie sie erfasst sind (bzw. importiert wurden), exportiert/validiert. Mehrere Modellnamen können durch Semikolon ‚;‘ getrennt werden. Als Export-Modelle sind Basis-Modelle (also z.B. Bundes-Modell statt Kantons-Modell) oder übersetzte Modelle (also z.B. DM_IT statt DM_DE) zulässig.                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --exportCrsModels modelname   | Beim Export/Prüfen werden die Daten gem. dem gegebenen Modell, das ein alternatives CRS zum Original Modell hat, exportiert/geprüft. Ohne die Option ``--exportCrsModels`` werden die Daten so wie sie erfasst sind, exportiert/validiert.                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ILIGML20                    | Verwendet beim Export eCH-0118-2.0 als Transferformat.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --log filename                | Schreibt die log-Meldungen in eine Datei.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --logtime                     | Ergänzt die log-Meldungen in der Log-Datei mit Zeitstempeln.                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --xtflog result.xtf           | Schreibt die log-Meldungen in eine INTERLIS 2-Datei.  Die Datei result.xtf entspricht dem Modell IliVErrors.                                                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --proxy host                  | Definiert den Name des Hosts der als Proxy für den Zugriff auf Modell-Repositories benutzt werden soll.                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --proxyPort port              | Port auf dem Proxy.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --gui                         | Startet ein einfaches GUI.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --verbose                     | Schreibt detailiertere validierungs log-Meldungen                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --trace                       | Erzeugt zusätzliche Log-Meldungen (wichtig für Programm-Fehleranalysen)                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --help                        | Zeigt einen kurzen Hilfetext an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --version                     | Zeigt die Version des Programmes an.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Abbildungsregeln
----------------

Umfang der Abbildung
~~~~~~~~~~~~~~~~~~~~~

Alle explizit genannten Modelle (mit ``--models``) werden vollständig importiert. 
Direkt oder indirekt importierte Modelle (via ``IMPORTS``) werden nicht importiert, 
ausser denjenigen Klassen die direkt oder indirekt via Assoziationen oder 
Referenzattribute referenziert werden.

Wird via ``--models`` kein Modell explizit bezeichnet, wird das letzte Modell der 
ili-Datei importiert.

Wird das Schema als Teil des Daten-Imports (``--doSchemaImport`` ) 
angelegt (ohne ``--models``), werden die Modelle gemäss dem Element ``MODELS`` aus der Transferdatei angelegt.


Klassen/Strukturen
~~~~~~~~~~~~~~~~~~

Je nach Programmoption, werden Klassen unterschiedlich abgebildet. Die
Abbildungsregeln für den Tabellennamen sind im Abschnitt
Namenskonventionen beschrieben.

+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS       | Beispiel SQL                        | Kommentare                                                                                                                                                                                                                                                                                                                                                                         |
+==============+=========================+=====================================+====================================================================================================================================================================================================================================================================================================================================================================================+
| 1            | ::                      | ::                                  | Für jede Klasse wird eine Tabelle erstellt.                                                                                                                                                                                                                                                                                                                                        |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A=               |  CREATE TABLE A (                   | Jede Tabelle hat mindestens eine Spalte T\_Id. Diese Spalte ist der Datenbank interne Primärschlüssel (und nicht die TID aus der Transferdatei).                                                                                                                                                                                                                                   |
|              |  END A;                 |    T_Id integer PRIMARY KEY         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 2            | ::                      | ::                                  | Mit der Option --createTypeDiscriminator erhält jede Tabelle (die eine Klasse oder Struktur repräsentiert, die keine Basisklasse hat) eine zusätzliche Spalte T\_Type. Diese Spalte enthält den konkreten Klassenname (der SQL-Name des qualifizierten INTERLIS-Klassennamens [2]_) des Objektes jedes einzelnen Records.                                                          |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |   CLASS A =             |  CREATE TABLE A (                   | Tabellen für Klassen die eine Basisklasse haben, erhalten diese Spalte nicht.                                                                                                                                                                                                                                                                                                      |
|              |   END A;                |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_Type varchar(60) NOT NULL       |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 3            | ::                      | ::                                  | Mit der Option --createStdCols erhalten alle Tabellen drei zusätzliche Spalten für den Zeitpunkt der letzten Änderung, den Zeitpunkt der Erstellung und den Benutzer, der die letzte Änderung durchgeführt hat. Diese Spalten müssen durch die Applikation nachgeführt werden, und werden typischerweise für die Implementierung eines optimistischen Lockings benötigt/benutzt.   |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A =              |  CREATE TABLE A (                   |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  END A;                 |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_LastChange timestamp NOT NULL,  |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_CreateDate timestamp NOT NULL,  |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_User varchar(40) NOT NULL       |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 4            | ::                      | ::                                  | Mit der Option --createTidCol erhält jede Tabelle (die eine Klasse repräsentiert, die keine Basisklasse hat) eine zusätzliche Spalte T\_Ili\_Tid. Diese Spalte enthält die TID aus der Transferdatei.                                                                                                                                                                              |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A =              |  CREATE TABLE A (                   | Diese Spalte ist NICHT der Datenbank interne Primärschlüssel.                                                                                                                                                                                                                                                                                                                      |
|              |  END A;                 |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_Ili_Tid varchar(200) NULL       |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 5            | ::                      | ::                                  | Mit dem Metaattribut ili2db.oid erhält die Tabelle (die eine Klasse repräsentiert, die keine Basisklasse hat) eine zusätzliche Spalte T\_Ili\_Tid, wie wenn die Klasse eine OID hätte. Diese Spalte enthält die TID aus der Transferdatei.                                                                                                                                         |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  !!@ili2db.oid=MyOID    |  CREATE TABLE A (                   | MyOID muss eine im Modell vorhandene OID-Domain Definition sein (z.b. INTERLIS.UUIDOID). Das Metaattribut steht typischerweise in einer externen Datei, die beim Schemaimport mit --iliMetaAttrs mitgegeben wird.                                                                                                                                                                  |
|              |  CLASS A =              |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  END A;                 |   T_Ili_Tid varchar(200) NULL       | Diese Spalte ist NICHT der Datenbank interne Primärschlüssel.                                                                                                                                                                                                                                                                                                                      |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 6            | ::                      | ::                                  | Mit der Option --t\_id\_Name oidname wird der Namen der Spalte für den Datenbank internen Primärschlüssel (nicht die Spalte für die TID aus der Transferdatei) festgelegt.                                                                                                                                                                                                         |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A =              |  CREATE TABLE A (                   |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  END A;                 |   oidname integer PRIMARY KEY       |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 7            | ::                      | ::                                  | Strukturen werden im Allgemeinen abgebildet wie Klassen.                                                                                                                                                                                                                                                                                                                           |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  STRUCTURE C =          |  CREATE TABLE C (                   | Die Strukturtabelle enthält zusätzlich eine Spalte T\_seq, die die Reihenfolge der Strukturelement festlegt.                                                                                                                                                                                                                                                                       |
|              |  END C;                 |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_seq integer NOT NULL            | Da Strukturelemente keine TID haben, erhalten sie auch mit der Option --createTidCol kein Spalte T\_Ili\_Tid.                                                                                                                                                                                                                                                                      |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 8            | ::                      | ::                                  | Mit der Option --createBasketCol erhält jede Tabelle eine zusätzliche Spalte T\_basket. Diese Spalte enthält den Fremschlüssel auf die Tabelle t\_ili2db\_basket.                                                                                                                                                                                                                  |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A =              |  CREATE TABLE A (                   |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  END A;                 |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_basket integer NOT NULL         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 9            | ::                      | ::                                  | Mit der Option --createDatasetCol erhält jede Tabelle eine zusätzliche Spalte T\_datasetname.                                                                                                                                                                                                                                                                                      |
|              |                         |                                     |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |  CLASS A =              |  CREATE TABLE A (                   | Die Spalte ist redunant zur Spalte datasetname der Tabelle t_ili2db_dataset (siehe Kapitel Abbildungsregeln/Metadaten)                                                                                                                                                                                                                                                             |
|              |  END A;                 |   T_Id integer PRIMARY KEY,         |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |   T_datasetname varchar(200)        |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |                           NOT NULL  |                                                                                                                                                                                                                                                                                                                                                                                    |
|              |                         |  );                                 |                                                                                                                                                                                                                                                                                                                                                                                    |
+--------------+-------------------------+-------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Vererbung
~~~~~~~~~

Im allgemeinen lässt sich Vererbung nach drei unterschidlichen
Strategien abbilden:

NewClass
	Diese Strategie ist für jede Klasse möglich. Bei dieser
	Strategie wird für eine Klasse eine neue Tabelle angelegt, ein
	Interlis-Objekt verteilt sich somit auf Records in mehreren Tabellen.

SuperClass 
	Diese Strategie ist nur für Klassen mit einer Super-Klasse
	möglich. Bei dieser Strategie wird für die Klasse keine neue Tabelle
	angelegt, d.h. die Attribute der Klasse werden als weitere Spalten in
	der Tabelle der Super-Klasse ergänzt.

SubClass
	Diese Strategie ist nur für Klassen mit mindestens einer
	Sub-Klasse möglich. Bei dieser Strategie wird für eine Klasse keine neue
	Tabelle angelegt, d.h. die Attribute der Klasse werden als weitere
	Spalten in den Tabellen der Sub-Klassen ergänzt.

ili2db bildet die Vererbung nach einer je nach Klasse unterschiedlichen
Strategie (--smart1Inheritance oder --smart2Inheritance) oder für alle 
Klassen einheitlich nach der NewClass-Strategie (--noSmartMapping) ab.

Bei --smart1Inheritance wird wie folgt abgebildet: Fuer Klassen, die
referenziert werden und deren Basisklassen nicht mit einer
NewClass-Strategie abgebildet werden, wird die NewClass-Strategie
verwendet. Abstrakte Klassen werden mit einer SubClass-Strategie
abgebildet. Konkrete Klassen, ohne Basisklasse oder deren direkte
Basisklassen mit einer SubClass-Strategie abgebildet werden, werden mit
einer NewClass-Strategie abgebildet. Alle anderen Klassen werden mit
einer SuperClass-Strategie abgebildet.

Bei --smart2Inheritance wird wie folgt abgebildet: Abstrakte Klassen werden 
mit einer SubClass-Strategie abgebildet. 
Konkrete Klassen werden mit einer NewAndSubClass-Strategie abgebildet. 

+--------------+---------------------------+---------------------------------+------------------------------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS         | Beispiel SQL                    | Kommentare                                                                                           |
+==============+===========================+=================================+======================================================================================================+
| 1            | ::                        | ::                              | Bei --noSmartMapping wird für jede Klasse eine Tabelle erstellt. Ein Objekt A ergibt                 |
|              |                           |                                 | ein Record in Tabellen A.                                                                            |
|              |  CLASS A =                |  CREATE TABLE A (               | Ein Objekt B ergibt je ein Record in Tabellen A und B. Die T\_Id ist bei beiden Records identisch.   |
|              |   Attribut_1 : TEXT*20;   |   T_Id integer PRIMARY KEY,     |                                                                                                      |
|              |  END A;                   |   T_Type varchar(60) NOT NULL,  |                                                                                                      |
|              |                           |   Attribut_1 varchar(20)        |                                                                                                      |
|              |                           |  );                             |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |  CLASS B EXTENDS A =      |  CREATE TABLE B (               |                                                                                                      |
|              |   Attribut_2 : TEST*20;   |   T_Id integer PRIMARY KEY,     |                                                                                                      |
|              |  END B;                   |   Attribut_2 varchar(20)        |                                                                                                      |
|              |                           |  );                             |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
+--------------+---------------------------+---------------------------------+------------------------------------------------------------------------------------------------------+
| 2            | ::                        | ::                              | Bei --smart1Inheritance wird für abstrakte Klassen (A) keine Tabelle erstellt (ausser sie wird       |
|              |                           |                                 | referenziert). Für die allgemeinste konkrete Klasse (B) wird eine Tabelle erstellt.                  |
|              |  CLASS A (ABSTRACT) =     |                                 | Für erweiterte konkrete Klassen (C), die eine konkrete Klasse erweitern,                             |
|              |   Attribut_1 : TEXT*20;   |                                 | wird keine eigene Tabelle erstellt.                                                                  |
|              |  END A;                   |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |  CLASS B EXTENDS A =      |  CREATE TABLE B (               |                                                                                                      |
|              |   Attribut_2 : TEST*20;   |   T_Id integer PRIMARY KEY,     |                                                                                                      |
|              |  END B;                   |   T_Type varchar(60) NOT NULL,  |                                                                                                      |
|              |                           |   Attribut_1 varchar(20),       |                                                                                                      |
|              |                           |   Attribut_2 varchar(20),       |                                                                                                      |
|              |                           |   Attribut_3 varchar(20)        |                                                                                                      |
|              |                           |  );                             |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |  CLASS C EXTENDS B =      |                                 |                                                                                                      |
|              |   Attribut_3 : TEST*20;   |                                 |                                                                                                      |
|              |  END C;                   |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
+--------------+---------------------------+---------------------------------+------------------------------------------------------------------------------------------------------+
| 3            | ::                        | ::                              | Bei --smart2Inheritance wird für abstrakte Klassen (A) keine Tabelle erstellt (auch nicht, wenn sie  |
|              |                           |                                 | referenziert wird). Für konkrete Klassen (B und C) wird je eine vollständige Tabelle erstellt        |
|              |  CLASS A (ABSTRACT) =     |                                 | (inkl. geerbte Attribute).                                                                           |
|              |   Attribut_1 : TEXT*20;   |                                 |                                                                                                      |
|              |  END A;                   |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |  CLASS B EXTENDS A =      |  CREATE TABLE B (               |                                                                                                      |
|              |   Attribut_2 : TEST*20;   |   T_Id integer PRIMARY KEY,     |                                                                                                      |
|              |  END B;                   |   T_Type varchar(60) NOT NULL,  |                                                                                                      |
|              |                           |   Attribut_1 varchar(20),       |                                                                                                      |
|              |                           |   Attribut_2 varchar(20)        |                                                                                                      |
|              |                           |  );                             |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |  CLASS C EXTENDS B =      |  CREATE TABLE C (               |                                                                                                      |
|              |   Attribut_3 : TEST*20;   |   T_Id integer PRIMARY KEY,     |                                                                                                      |
|              |  END B;                   |   T_Type varchar(60) NOT NULL,  |                                                                                                      |
|              |                           |   Attribut_1 varchar(20),       |                                                                                                      |
|              |                           |   Attribut_2 varchar(20),       |                                                                                                      |
|              |                           |   Attribut_3 varchar(20)        |                                                                                                      |
|              |                           |  );                             |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
|              |                           |                                 |                                                                                                      |
+--------------+---------------------------+---------------------------------+------------------------------------------------------------------------------------------------------+

EXTENDED Attribute ergeben keine Spalte, nur die Basis-Definition des
Attributs ergibt eine Spalte.

Attribute (allgemein)
~~~~~~~~~~~~~~~~~~~~~

+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS                                             | Beispiel SQL                         | Kommentare                                                                        |
+==============+===============================================================+======================================+===================================================================================+
| 1            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |  textLimited : TEXT*10;                                       |  textLimited varchar(10) NULL        |                                                                                   |
|              |  textUnlimited : TEXT;                                        |  textUnlimited text NULL             |                                                                                   |
|              |  mtextLimited : MTEXT*10;                                     |  mtextLimited varchar(10) NULL       |                                                                                   |
|              |  mtextUnlimited : MTEXT;                                      |  mtextUnlimited text NULL            |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 2            | ::                                                            | ::                                   | Je nach Option, sind andere Abbildungen möglich. Siehe Kapitel Aufzählungen.      |
|              |                                                               |                                      |                                                                                   |
|              |  aufzaehlung : (null, eins, zwei,                             |  aufzaehlung varchar(255) NULL       |                                                                                   |
|              |     drei, mehr (                                              |                                      |                                                                                   |
|              |           vier, fuenf, sechs, sieben, acht ,neun, zehn)       |                                      |                                                                                   |
|              |     );                                                        |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 3            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |  horizAlignment : HALIGNMENT;                                 |  horizAlignment varchar(255) NULL    |                                                                                   |
|              |  vertAlignment : VALIGNMENT;                                  |  vertAlignment varchar(255) NULL     |                                                                                   |
|              |                                                               |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 4            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |  aBoolean : BOOLEAN;                                          |  aBoolean boolean NULL               |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 5            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |   numericInt : 0 .. 10;                                       |  numericInt integer NULL             |                                                                                   |
|              |   numericDec : 0.0 .. 10.0;                                   |  numericDec decimal(4,1) NULL        |                                                                                   |
|              |                                                               |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 6            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |   aTime : INTERLIS.XMLTime;                                   |  aTime time NULL                     |                                                                                   |
|              |   aDate : INTERLIS.XMLDate;                                   |  aDate date NULL                     |                                                                                   |
|              |   aDateTime : INTERLIS.XMLDateTime;                           |  aDateTime timestamp NULL            |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 7            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |   aOid : OID TEXT*30;                                         |  aOid varchar(255) NULL              |                                                                                   |
|              |   aUuid : INTERLIS.UUIDOID;                                   |  aUuid uuid NULL                     |                                                                                   |
|              |                                                               |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 8            | ::                                                            | ::                                   |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |   aClass : CLASS;                                             |  aClass varchar(255) NULL            |                                                                                   |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      |                                                                                   |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
		   

TID/OID
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TODO

Beziehungen
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Beziehungen werden abhängig von der maximalen Kardinalität auf zwei Arten abgebildet:

- direkt als Fremdschlüssel bei einer der Tabellen, die die an der Assoziation beteiligten Klassen abbilden
- mit Hilfe einer Zwischentabelle, mit je einen Fremschlüssel auf die beteiligten Tabellen

Die Stärke der Beziehung (Assoziation, Aggregation oder Komposition) beeinflusst die Art der Abbildung nicht.

+--------------+-----------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS           | Beispiel SQL                         | Kommentare                                                                                           |
+==============+=============================+======================================+======================================================================================================+
| 1            | ::                          | ::                                   | Wenn die maximale Kardinalität bei einer der beiden Rollen nicht grösser als 1 ist,                  |
|              |                             |                                      | wird keine Zwischentabelle erstellt.                                                                 |
|              |  CLASS A =                  |  CREATE TABLE A (                    |                                                                                                      |
|              |  END A;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  CLASS B =                  |  CREATE TABLE B (                    |                                                                                                      |
|              |  END B;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |   role_a integer REFERENCES A(T_id)  |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  ASSOCIATION a2b =          |                                      |                                                                                                      |
|              |    role_A -- {0..1} ClassA; |                                      |                                                                                                      |
|              |    role_B -- {0..*} ClassB; |                                      |                                                                                                      |
|              |  END a2b;                   |                                      |                                                                                                      |
+--------------+-----------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------+
| 2            | ::                          | ::                                   | Wenn die maximale Kardinalität bei beiden Rollen grösser als 1 ist,                                  |
|              |                             |                                      | wird eine Zwischentabelle erstellt.                                                                  |
|              |  CLASS A =                  |  CREATE TABLE A (                    |                                                                                                      |
|              |  END A;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  CLASS B =                  |  CREATE TABLE B (                    |                                                                                                      |
|              |  END B;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  ASSOCIATION a2b =          |  CREATE TABLE a2b (                  |                                                                                                      |
|              |    role_A -- {0..*} ClassA; |   role_a integer REFERENCES A(T_id)  |                                                                                                      |
|              |    role_B -- {0..*} ClassB; |   role_b integer REFERENCES B(T_id)  |                                                                                                      |
|              |  END a2b;                   |  );                                  |                                                                                                      |
+--------------+-----------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------+
| 3            | ::                          | ::                                   | Wenn die Option --sqlExtRefCols benutzt wird,                                                        |
|              |                             |                                      | wird bei EXTERNAL statt der Fremdschlüsselspalte ein Text-Spalte erstellt, die den REF Wert          |
|              |  CLASS A =                  |  CREATE TABLE A (                    | aus der Transferdatei aufnimmt. So muss das refernzierte Objekt nicht importiert werden.             |
|              |  END A;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  CLASS B =                  |  CREATE TABLE B (                    |                                                                                                      |
|              |  END B;                     |   T_Id integer PRIMARY KEY,          |                                                                                                      |
|              |                             |   role_a varchar(255)                |                                                                                                      |
|              |                             |  );                                  |                                                                                                      |
|              |                             |                                      |                                                                                                      |
|              |  ASSOCIATION a2b =          |                                      |                                                                                                      |
|              |    role_A (EXTERNAL)        |                                      |                                                                                                      |
|              |           -- {0..1} ClassA; |                                      |                                                                                                      |
|              |    role_B -- {0..*} ClassB; |                                      |                                                                                                      |
|              |  END a2b;                   |                                      |                                                                                                      |
+--------------+-----------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------+

Referenzattribute
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
TODO

Geometrieattribute (allgemein)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TODO

SURFACE/AREA/ITF/XTF
~~~~~~~~~~~~~~~~~~~~

TODO

Strukturattribute
~~~~~~~~~~~~~~~~~

Strukturen werden im Allgemeinen abgebildet wie Klassen (siehe Kapitel zu der Abbildung von Klassen). Strukturattribute (also wenn eine Struktur
als Attributstyp verwendet wird, z.B. bei BAG OF oder LIST OF) werden unabhängig von der Kardinalität durch einen Fremdschlüssel bei 
der Tabelle der Struktur abgebildet. Bei gewissen Strukturen wird bei Smart-Mapping eine alternative Abbildung verwendet.

+--------------+-------------------------+--------------------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS       | Beispiel SQL                         | Kommentare                                                                                                                                                                                                                                    |
+==============+=========================+======================================+===============================================================================================================================================================================================================================================+
| 1            | ::                      | ::                                   | Für jedes Strukturattribut wird in der Tabelle der Struktur eine Spalte für den Fremdschlüssel erstellt. Der Name der Spalte ist der qualifizierte INTERLIS-Attributnamen [3]_.                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
|              |  STRUCTURE C =          |  CREATE TABLE C (                    | Die Strukturtabelle enthält zusätzlich eine Spalte T\_seq die die Reihenfolge der Strukturelement festlegt.                                                                                                                                   |
|              |  END C;                 |   T_Id integer PRIMARY KEY,          |                                                                                                                                                                                                                                               |
|              |                         |   T_seq integer NOT NULL,            |                                                                                                                                                                                                                                               |
|              |                         |   D_attr1 integer,                   |                                                                                                                                                                                                                                               |
|              |                         |   D_attr2 integer                    |                                                                                                                                                                                                                                               |
|              |                         |  );                                  |                                                                                                                                                                                                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
|              |  CLASS D =              |  CREATE TABLE D (                    |                                                                                                                                                                                                                                               |
|              |   attr1 : LIST OF C;    |   T_Id integer PRIMARY KEY           |                                                                                                                                                                                                                                               |
|              |   attr2 : LIST OF C;    |  );                                  |                                                                                                                                                                                                                                               |
|              |  END D;                 |                                      |                                                                                                                                                                                                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
|              |                         |                                      |                                                                                                                                                                                                                                               |
+--------------+-------------------------+--------------------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Beispiel XML::
	
|<BspTable.TopicA.D TID="2">
|  <attr1>                      
|    <BspTable.TopicA.C>
|    </BspTable.TopicA.C>
|    <BspTable.TopicA.C>
|    </BspTable.TopicA.C>
|  </attr1>
|  <attr2>
|    <BspTable.TopicA.C>
|    </BspTable.TopicA.C>
|  </attr2>
|</BspTable.TopicA.D>

Beispiel für Abbildungsvariante 1:

+-------------+----------+------------+------------+
| Tabelle C   |          |            |            |
+=============+==========+============+============+
| t\_id       | t\_seq   | D\_attr1   | D\_attr2   |
+-------------+----------+------------+------------+
| 7           | 0        | 6          |            |
+-------------+----------+------------+------------+
| 8           | 1        | 6          |            |
+-------------+----------+------------+------------+
| 9           | 0        |            | 6          |
+-------------+----------+------------+------------+

+-------------+---------------+
| Tabelle D   |               |
+=============+===============+
| t\_id       | T\_Ili\_Tid   |
+-------------+---------------+
| 6           | 2             |
+-------------+---------------+


Bei den folgenden Strukturen wird bei Smart-Mapping für die Strukturattribute eine alternative Abbildung verwendet:

- Strukturen mit dem Interlis Metaattribut ili2db.mapping=MultiSurface

- Strukturen mit dem Interlis Metaattribut ili2db.mapping=MultiLine

- Strukturen mit dem Interlis Metaattribut ili2db.mapping=MultiPoint

- GeometryCHLV03_V1.MultiSurface

- GeometryCHLV03_V1.MultiLine

- GeometryCHLV03_V1.MultiDirectedLine

- GeometryCHLV95_V1.MultiSurface

- GeometryCHLV95_V1.MultiLine

- GeometryCHLV95_V1.MultiDirectedLine

- CatalogueObjects_V1.Catalogues.CatalogueReference

- CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference

- Strukturen mit dem Interlis Metaattribut ili2db.mapping=Multilingual

- LocalisationCH_V1.MultilingualMText

- LocalisationCH_V1.MultilingualText

- Strukturen mit dem Interlis Metaattribut ili2db.mapping=Localised

- LocalisationCH_V1.LocalisedMText

- LocalisationCH_V1.LocalisedText

- Strukturattribute mit dem Interlis Metaattribut ili2db.mapping=JSON

- Strukturattribute mit dem Interlis Metaattribut ili2db.mapping=ARRAY

- Strukturattribute mit dem Interlis Metaattribut ili2db.mapping=EXPAND (20)

+--------------+------------------------------+--------------------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS            | Beispiel SQL                         | Kommentare                                                                                                                                                                                                                                    |
+==============+==============================+======================================+===============================================================================================================================================================================================================================================+
| 20           | ::                           | ::                                   | Strukturattribute mit dem Metaattribut ``ili2db.mapping=EXPAND`` und maximaler Kardinalität<=5, werden ausmultipliziert, d.h. die Attribute der Struktur werden direkt als Spalten der Tabelle des Strukturattributes abgebildet.             |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |  STRUCTURE C =               |                                      |                                                                                                                                                                                                                                               |
|              |    attrC : TEXT*10;          |                                      |                                                                                                                                                                                                                                               |
|              |  END C;                      |                                      |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |  CLASS D =                   |  CREATE TABLE D (                    |                                                                                                                                                                                                                                               |
|              |   !!@ili2db.mapping=EXPAND   |   T_Id integer PRIMARY KEY,          |                                                                                                                                                                                                                                               |
|              |   attrD : LIST {0..2} OF C;  |   attrD_0_T_Type varchar(60),        | Typ der Struktur (SQL-Name der Struktur, gem. t_ili2db_classname) des Strukturelementes 0 oder NULL falls kein Strukturelement                                                                                                                |
|              |  END D;                      |   attrD_0_attrC varchar(10),         |                                                                                                                                                                                                                                               |
|              |                              |   attrD_1_T_Type varchar(60),        | Typ der Struktur (SQL-Name der Struktur, gem. t_ili2db_classname) des Strukturelementes 1 oder NULL falls kein Strukturelement                                                                                                                |
|              |                              |   attrD_1_attrC varchar(10)          |                                                                                                                                                                                                                                               |
|              |                              |  );                                  |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
|              |                              |                                      |                                                                                                                                                                                                                                               |
+--------------+------------------------------+--------------------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+


Aufzählungen
~~~~~~~~~~~~

Für die Abbildung von Aufzählungen gibt es zwei Varianten und verschiedene Optionen. 

- Variante 1 bei der der Aufzählwert als XTF-Code gespeichert wird

- Variante 2 bei der der Aufzählwert als ITF-Code gespeichert wird

- Optional kann eine zusätzliche Spalte erzeugt werden, die den Anzeigtext enthalten kann

- Optional können zusätzliche Tabellen erzeugt werden, die alle Aufzählwerte enthalten.

+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| Nummer       | Beispiel INTERLIS                                             | Beispiel SQL                         | Kommentare                                                                        |
+==============+===============================================================+======================================+===================================================================================+
| 1            | ::                                                            | ::                                   | Default-Abbilung. Der XTF-Code (der Code wie er in der XTF-Transferdatei steht)   |
|              |                                                               |                                      | wird als Aufzählwert in der Datenbank verwendet. Im Beispiel also:                |
|              |  farbe : (rot, blau, gruen);                                  |  farbe varchar(255) NULL             | rot, blau oder gruen                                                              |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 2            | ::                                                            | ::                                   | Abbilung mit der Option ``--createEnumColAsItfCode``. Der ITF-Code (der Code      |
|              |                                                               |                                      | wie er in der ITF-Transferdatei steht) wird als Aufzählwert in der Datenbank      |
|              |  farbe : (rot, blau, gruen);                                  |  farbe integer NULL                  | verwendet. Im Beispiel also: 0, 1 oder 2. Diese Option ist nur zulässig, wenn im  |
|              |                                                               |                                      | Modell keine Erweiterungen von Aufzählungen vorkommen.                            |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 3            | ::                                                            | ::                                   | Abbilung mit der Option ``--createEnumTxtCol``. Es wird eine zusätzliche Spalte   |
|              |                                                               |                                      | mit dem Attributnamen+``_txt`` erstellt (Im Besipiel ``art_txt``).                |
|              |  farbe : (rot, blau, gruen);                                  |  farbe varchar(255) NULL,            | Die zusätzliche Spalte kann einen beliebigen Wert enthalten, der als Anzeigetext  |
|              |                                                               |  farbe_txt varchar(255) NULL         | gedacht ist. Beim Import wird die Spalte mit dem XTF-Code befüllt.                |
|              |                                                               |                                      | Die Option kann bei Variante 1 oder 2 benutzt werden.                             |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 4            | ::                                                            | ::                                   | Abbildung mit der Option ``--createEnumTabs``. Es wird pro Aufzählungsdefinition  |
|              |                                                               |                                      | eine Tabelle mit den einzelnen Aufzählwerten erstellt.                            |
|              |  DOMAIN                                                       |  CREATE TABLE Farbe (                |                                                                                   |
|              |    Farbe : (rot, blau,                                        |   itfCode integer PRIMARY KEY,       | itfCode ist der ITF-Code des Aufzählwertes.                                       |
|              |               !!@ili2db.dispName=grün                         |   iliCode varchar(1024) NOT NULL,    |                                                                                   |
|              |               gruen                                           |   seq integer NULL,                  | iliCode ist der qualifizierte Elementnamen (=XTF-Code) des Aufzählwertes.         |
|              |            );                                                 |   dispName varchar(250) NOT NULL,    |                                                                                   |
|              |                                                               |   description varchar(1024) NULL,    | seq Definiert die Reihenfolge der Aufzählelemente.                                |
|              |                                                               |   inactive boolean NOT NULL          |                                                                                   |
|              |                                                               |  );                                  |                                                                                   |
|              |                                                               |                                      | dispName definiert den Anzeigetext für das Aufzählelement. Beim Import wird die   |
|              |                                                               |                                      | Spalte mit dem XTF-Code befüllt. Falls das Aufzählelement das Metaattribut        |
|              |                                                               |                                      | @ili2db.dispName hat, wird dessen Wert verwendet.                                 |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | description enthält die Beschreibung des Aufzählelements. Beim Import wird die    |
|              |                                                               |                                      | Spalte mit dem ilidoc Kommentar aus dem Modell befüllt.                           |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | inactive TRUE um einen Aufzählwert für die Erfassung auszublenden, ohne dass      |
|              |                                                               |                                      | er gelöscht werden muss. Wird beim Import mit FALSE befüllt.                      |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 5            | ::                                                            | ::                                   | Abbildung mit der Option ``--createSingleEnumTab``. Es wird                       |
|              |                                                               |                                      | eine einzige Tabelle für die Aufzählwerte aller Aufzählungen erstellt.            |
|              |  DOMAIN                                                       |  CREATE TABLE T_ILI2DB_ENUM (        |                                                                                   |
|              |    Farbe : (rot, blau,                                        |   thisClass varchar(1024) NOT NULL,  | thisClass ist der qualifizierte Namen der Aufzählungsdefinition.                  |
|              |               !!@ili2db.dispName=grün                         |   baseClass varchar(1024) NOT NULL,  |                                                                                   |
|              |               gruen                                           |   itfCode integer NOT NULL,          | baseClass ist der qualifizierte Namen der Basis-Aufzählungsdefinition             |
|              |            );                                                 |   iliCode varchar(1024) NOT NULL,    |                                                                                   |
|              |                                                               |   seq integer NULL,                  | itfCode ist der ITF-Code des Aufzählwertes.                                       |
|              |                                                               |   dispName varchar(250) NOT NULL,    |                                                                                   |
|              |                                                               |   description varchar(1024) NULL,    |                                                                                   |
|              |                                                               |   inactive boolean NOT NULL          | iliCode ist der qualifizierte Elementnamen (=XTF-Code) des Aufzählwertes.         |
|              |                                                               |  );                                  |                                                                                   |
|              |                                                               |                                      | seq Definiert die Reihenfolge der Aufzählelemente.                                |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | dispName definiert den Anzeigetext für das Aufzählelement. Beim Import wird die   |
|              |                                                               |                                      | Spalte mit dem XTF-Code befüllt. Falls das Aufzählelement das Metaattribut        |
|              |                                                               |                                      | @ili2db.dispName hat, wird dessen Wert verwendet.                                 |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | description enthält die Beschreibung des Aufzählelements. Beim Import wird die    |
|              |                                                               |                                      | Spalte mit dem ilidoc Kommentar aus dem Modell befüllt.                           |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | inactive TRUE um einen Aufzählwert für die Erfassung auszublenden, ohne dass      |
|              |                                                               |                                      | er gelöscht werden muss. Wird beim Import mit FALSE befüllt.                      |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+
| 6            | ::                                                            | ::                                   | Abbildung mit der Option ``--createEnumTabsWithId``. Es wird pro                  |
|              |                                                               |                                      | Basis-Aufzählungsdefinition eine Tabelle mit den einzelnen Aufzählwerten,         |
|              |                                                               |                                      | inkl. aller Aufzählungserweiterungen von dieser Basisdefinition.                  |
|              |  DOMAIN                                                       |  CREATE TABLE Farbe (                |                                                                                   |
|              |    Farbe : (rot, blau,                                        |   T_Id integer PRIMARY KEY,          | thisClass ist der qualifizierte Namen der Aufzählungsdefinition.                  |
|              |               !!@ili2db.dispName=grün                         |   thisClass varchar(1024) NOT NULL,  |                                                                                   |
|              |               gruen                                           |   baseClass varchar(1024) NOT NULL,  | baseClass ist der qualifizierte Namen der Basis-Aufzählungsdefinition             |
|              |            );                                                 |   itfCode integer NOT NULL,          |                                                                                   |
|              |                                                               |   iliCode varchar(1024) NOT NULL,    | itfCode ist der ITF-Code des Aufzählwertes.                                       |
|              |                                                               |   seq integer NULL,                  |                                                                                   |
|              |                                                               |   dispName varchar(250) NOT NULL,    |                                                                                   |
|              |                                                               |   description varchar(1024) NULL,    | iliCode ist der qualifizierte Elementnamen (=XTF-Code) des Aufzählwertes.         |
|              |                                                               |   inactive boolean NOT NULL          |                                                                                   |
|              |                                                               |  );                                  | seq Definiert die Reihenfolge der Aufzählelemente.                                |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | dispName definiert den Anzeigetext für das Aufzählelement. Beim Import wird die   |
|              |                                                               |                                      | Spalte mit dem XTF-Code befüllt. Falls das Aufzählelement das Metaattribut        |
|              |                                                               |                                      | @ili2db.dispName hat, wird dessen Wert verwendet.                                 |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | description enthält die Beschreibung des Aufzählelements. Beim Import wird die    |
|              |                                                               |                                      | Spalte mit dem ilidoc Kommentar aus dem Modell befüllt.                           |
|              |                                                               |                                      |                                                                                   |
|              |                                                               |                                      | inactive TRUE um einen Aufzählwert für die Erfassung auszublenden, ohne dass      |
|              |                                                               |                                      | er gelöscht werden muss. Wird beim Import mit FALSE befüllt.                      |
+--------------+---------------------------------------------------------------+--------------------------------------+-----------------------------------------------------------------------------------+

INTERLIS-Metaattribute
~~~~~~~~~~~~~~~~~~~~~~
Einzelne Abbildungen können direkt im Modell über Metaaatribute konfiguriert werden. 
Metaattribute stehen unmittelbar vor dem Modellelement das sie betreffen und beginnen mit ``!!@``.
Falls der Wert (rechts von ```=```) aus mehreren durch Leerstellen getrennten Wörtern besteht, muss er mit Gänsefüsschen eingerahmt werden (```"..."```).

+------------------+--------------------------+-----------------------------------------------------------------------------------+
| Modelelement     | Metaattribut             | Beschreibung                                                                      |
+==================+==========================+===================================================================================+
| AttributeDef     | ::                       | Strukturattribute mit diesem Meta-Attribut                                        |
|                  |                          | werden als Spalte mit dem Datentyp Array oder JSON oder als Spalten in der        |
|                  |  ili2db.mapping          | Haupttabelle abgebildet.                                                          |
|                  |                          | D.h. es gibt keine weiteren Records in einer Hilfstabelle.                        |
|                  |                          | Siehe auch Programm-Optionen --coalesceArray, --coalesceJson und --expandStruct.  |
|                  |                          | Mögliche Werte: ARRAY, JSON, EXPAND                                               |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ClassDef         | ::                       | Strukturen mit diesem Meta-Attribut                                               |
|                  |                          | werden als Spalte mit dem enstprechenden Multi-Geometrie Datentyp abgebildet.     |
|                  |  ili2db.mapping          | D.h. es gibt keine weiteren Records in einer Hilfstabelle.                        |
|                  |                          | Mögliche Werte: MultiSurface, MultiLine, MultiPoint                               |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ClassDef,        | ::                       | Definiert den Anzeigetext für das entsprechende Modell-Element.                   |
| AttributeDef,    |                          | Für Aufzählelemente ist es der Wert der Saplte dispName in der jeweiligen Tabelle |
| EnumElement      |  ili2db.dispName         | mit den Aufzählwerten.                                                            |
|                  |                          | Für Klassen ist es in der Tabelle t\_ili2db\_table_prop der Wert mit              |
|                  |                          | dem Tag ch.ehi.ili2db.dispName.                                                   |
|                  |                          | Für Attribute ist es in der Tabelle t\_ili2db\_column_prop der Wert mit           |
|                  |                          | dem Tag ch.ehi.ili2db.dispName.                                                   |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+
| ClassDef         | ::                       | Mit dem Metaattribut ili2db.oid erhält die Tabelle (die eine Klasse               |
|                  |                          | repräsentiert, die keine Basisklasse hat) eine zusätzliche Spalte                 |
|                  |  ili2db.oid              | T\_Ili\_Tid, wie wenn die Klasse eine OID hätte.                                  |
|                  |                          | Siehe auch `Klassen/Strukturen`_.                                                 |
|                  |                          |                                                                                   |
+------------------+--------------------------+-----------------------------------------------------------------------------------+

Ein Modell kann beliebige weitere Metaattribute enthalten; diese werden 
durch ili2db beim Schemaimpot in t\_ili2db\_meta\_attrs abgelegt.
Mit Hilfe der Option ``--iliMetaAttrs`` können beliebige weitere Metaattribute
definiert werden, ohne das Modell (die ili-Datei) zu ändern.

Meta-Konfiguration
~~~~~~~~~~~~~~~~~~

In der Meta-Konfigurationsdatei werden die folgenden Parameter unterstützt (hier nicht aufgeführte Kommandozeilenargument werden in der Meta-Konfiguration nicht unterstützt).
Im Aufruf evtl. vorhandene Kommandozeilenargumente übersteuern die Angaben in der Meta-Konfigurationsdatei.

+---------------------------------+----------------------------------------------------+-----------------------------------------------------------------------------------+
| Konfiguration                   | Beispiel                                           | Beschreibung                                                                      |
+=================================+====================================================+===================================================================================+
|                                 | .. code::                                          |                                                                                   |
|                                 |                                                    |                                                                                   |
| baseConfig                      |   [CONFIGURATION]                                  | Basis-Meta-Konfiguration, auf der die aktuelle Meta-Konfiguration aufbaut.        |
|                                 |   baseConfig=ilidata:DatesetId                     | Statt ``ilidata:DatesetId`` kann auch die Form ``file:/localfile``                |  
|                                 |                                                    | benutzt werden, dann wird die entsprechende lokale Datei benutzt.                 |
|                                 |                                                    |                                                                                   |
|                                 |                                                    | Mehrere Basiskonfigurationen werden mit einem Strichpunkt ";" getrennt.           |
|                                 |                                                    |                                                                                   |
+---------------------------------+----------------------------------------------------+-----------------------------------------------------------------------------------+
|                                 | .. code::                                          |                                                                                   |
|                                 |                                                    |                                                                                   |
| org.interlis2.validator.config  |   [CONFIGURATION]                                  | Validierungs-Konfiguration, die benutzt werden soll.                              |
|                                 |   org.interlis2.validator.config=ilidata:DatesetId | Statt ``ilidata:DatesetId`` kann auch die Form ``file:/localfile``                |  
|                                 |                                                    | benutzt werden, dann wird die entsprechende lokale Datei  benutzt.                |
|                                 |                                                    |                                                                                   |
|                                 |                                                    | Mehrere Validierungs-Konfigurationen werden mit einem Strichpunkt ";" getrennt.   |
|                                 |                                                    |                                                                                   |
+---------------------------------+----------------------------------------------------+-----------------------------------------------------------------------------------+
|                                 | .. code::                                          |                                                                                   |
|                                 |                                                    |                                                                                   |
| ch.interlis.referenceData       |   [CONFIGURATION]                                  | Basis-Daten (z.B. Kataloge), die benutzt werden sollen.                           |
|                                 |   ch.interlis.referenceData=ilidata:DatesetId      | Statt ``ilidata:DatesetId`` kann auch die Form ``file:/localfile``                |  
|                                 |                                                    | benutzt werden, dann wird die entsprechende lokale Datei  benutzt.                |
|                                 |                                                    |                                                                                   |
|                                 |                                                    | Mehrere Basis-Daten werden mit einem Strichpunkt ";" getrennt.                    |
|                                 |                                                    |                                                                                   |
+---------------------------------+----------------------------------------------------+-----------------------------------------------------------------------------------+
|                                 | .. code::                                          |                                                                                   |
|                                 |                                                    |                                                                                   |
| models                          |   [ch.ehi.ilivalidator]                            | Entspricht dem Kommandozeilenargument ``--models``.                               |
|                                 |   models=Simple23                                  | Hier nicht aufgeführte Kommandozeilenargument werden in der Meta-Konfiguration    |  
|                                 |                                                    | nicht unterstützt.                                                                |
|                                 |                                                    | models                                                                            |
|                                 |                                                    | exportModels                                                                      |
|                                 |                                                    | exportCrsModels                                                                   |
|                                 |                                                    | nameLang                                                                          |
|                                 |                                                    | dataset                                                                           |
|                                 |                                                    | baskets                                                                           |
|                                 |                                                    | topics                                                                            |
|                                 |                                                    | defaultSrsAuth                                                                    |
|                                 |                                                    | defaultSrsCode                                                                    |
|                                 |                                                    | modelSrsCode                                                                      |
|                                 |                                                    | multiSrs                                                                          |
|                                 |                                                    | domains                                                                           |
|                                 |                                                    | altSrsModel                                                                       |
|                                 |                                                    | validConfig                                                                       |
|                                 |                                                    | disableValidation                                                                 |
|                                 |                                                    | disableAreaValidation                                                             |
|                                 |                                                    | disableRounding                                                                   |
|                                 |                                                    | disableBoundaryRecoding                                                           |
|                                 |                                                    | forceTypeValidation                                                               |
|                                 |                                                    | createSingleEnumTab                                                               |
|                                 |                                                    | createEnumTabs                                                                    |
|                                 |                                                    | createEnumTabsWithId                                                              |
|                                 |                                                    | createEnumTxtCol                                                                  |
|                                 |                                                    | createEnumColAsItfCode                                                            |
|                                 |                                                    | beautifyEnumDispName                                                              |
|                                 |                                                    | noSmartMapping                                                                    |
|                                 |                                                    | smart1Inheritance                                                                 |
|                                 |                                                    | smart2Inheritance                                                                 |
|                                 |                                                    | coalesceCatalogueRef                                                              |
|                                 |                                                    | coalesceMultiSurface                                                              |
|                                 |                                                    | coalesceMultiLine                                                                 |
|                                 |                                                    | coalesceMultiPoint                                                                |
|                                 |                                                    | coalesceArray                                                                     |
|                                 |                                                    | coalesceJson                                                                      |
|                                 |                                                    | expandMultilingual                                                                |
|                                 |                                                    | expandLocalised                                                                   |
|                                 |                                                    | createFk                                                                          |
|                                 |                                                    | createFkIdx                                                                       |
|                                 |                                                    | createUnique                                                                      |
|                                 |                                                    | createNumChecks                                                                   |
|                                 |                                                    | createTextChecks                                                                  |
|                                 |                                                    | createDateTimeChecks                                                              |
|                                 |                                                    | createMandatoryChecks                                                             |
|                                 |                                                    | createImportTabs                                                                  |
|                                 |                                                    | createStdCols                                                                     |
|                                 |                                                    | t_id_Name                                                                         |
|                                 |                                                    | idSeqMin                                                                          |
|                                 |                                                    | idSeqMax                                                                          |
|                                 |                                                    | createTypeDiscriminator                                                           |
|                                 |                                                    | createGeomIdx                                                                     |
|                                 |                                                    | disableNameOptimization                                                           |
|                                 |                                                    | nameByTopic                                                                       |
|                                 |                                                    | maxNameLength                                                                     |
|                                 |                                                    | sqlColsAsText                                                                     |
|                                 |                                                    | sqlEnableNull                                                                     |
|                                 |                                                    | sqlExtRefCols                                                                     |
|                                 |                                                    | strokeArcs                                                                        |
|                                 |                                                    | skipPolygonBuilding                                                               |
|                                 |                                                    | skipReferenceErrors                                                               |
|                                 |                                                    | skipGeometryErrors                                                                |
|                                 |                                                    | keepAreaRef                                                                       |
|                                 |                                                    | createTidCol                                                                      |
|                                 |                                                    | importTid                                                                         |
|                                 |                                                    | exportTid                                                                         |
|                                 |                                                    | importBid                                                                         |
|                                 |                                                    | exportFetchSize                                                                   |
|                                 |                                                    | importBatchSize                                                                   |
|                                 |                                                    | createBasketCol                                                                   |
|                                 |                                                    | createDatasetCol                                                                  |
|                                 |                                                    | ILIGML20                                                                          |
|                                 |                                                    | ver3-translation                                                                  |
|                                 |                                                    | translation                                                                       |
|                                 |                                                    | createMetaInfo                                                                    |
|                                 |                                                    | iliMetaAttrs                                                                      |
|                                 |                                                    | createTypeConstraint                                                              |
|                                 |                                                    |                                                                                   |
|                                 |                                                    |                                                                                   |
|                                 |                                                    |                                                                                   |
+---------------------------------+----------------------------------------------------+-----------------------------------------------------------------------------------+

Beispiel für eine Meta-Konfigurationsdatei:

.. code::
	
    [CONFIGURATION]
    ch.interlis.referenceData=ilidata:63553eb4-a0dc-48eb-8596-ca1aa9bdbc0f
    
    [ch.ehi.ili2db]
    models=Simple23
    defaultSrsCode = 2056
    smart2Inheritance = true
    strokeArcs = false
    createBasketCol = true


Metadaten
~~~~~~~~~

+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Tabelle                     | Beschreibung                                                                                                                                                                                                                         |
+=============================+======================================================================================================================================================================================================================================+
| t\_ili2db\_attrname         | Abbildung von Attributnamen                                                                                                                                                                                                          |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_basket           | In der Datenbank vorhandene Baskets. Wird benötigt, wenn die Option --createBasketCol verwendet wird.                                                                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_classname        | Abbildung der qualifizierten Interlis Klassennamen auf Sql-Namen. Nicht aus jedem Eintrag gibt es eine Datenbank-Tabelle, je nach Abbildungsart der Interlis-Klasse wird der Sql-Name nur als Inhalt der Spalte t\_type verwendet.   |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_dataset          | In der Datenbank vorhandene Datensätze (Sammlung von Baskets). Wird benötigt, wenn die Option --createBasketCol verwendet wird.                                                                                                      |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import           | Nicht mehr verwenden, wird entfernt. Wird beim Export nicht benötigt.                                                                                                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import\_basket   | Nicht mehr verwenden, wird entfernt. Wird beim Export nicht benötigt.                                                                                                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import\_object   | Nicht mehr verwenden, wird entfernt. Wird beim Export nicht benötigt.                                                                                                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_inheritance      | Abbildung der Interlis Klassen Vererbungshierarchie (in der Tabellen sind die qualifizierten Interlis Klassennamen). Wird beim Export nicht benötigt.                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_enum             | Aufzählwerte, falls die Option --createSingleEnumTab verwendet wird. Wird beim Export nicht benötigt.                                                                                                                                |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_model            | Modelle, die beim Import benötigt wurden (so dass der Export mit denselben Modellen erfolgen kann).                                                                                                                                  |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_settings         | Programmeinstellungen für ili2db                                                                                                                                                                                                     |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_trafo            | Konfiguration der semantischen Abbildung (insb. der Vererbung)                                                                                                                                                                       |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_table_prop       | Weitere Angaben zu den DB-Tabellen aus dem Interlis Modell (z.B. ob es eine Tabelle mit Aufzählwerten ist). Wird nur erstellt mit Option --createMetaInfo.                                                                           |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_column_prop      | Weitere Angaben zu den DB-Spalten aus dem Interlis Modell (z.B. ob es MTEXT ist). Wird nur erstellt mit Option --createMetaInfo.                                                                                                     |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_nls              | Mehrsprechige Daten je Modell-Element; z.B. der Name . Wird nur erstellt mit der Option --createNlsTab.                                                                                                                              |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_meta\_attrs      | Interlis-Meta-Attribute (auch für Modell-Elemente, denen kein DB-Element entspricht; z.B. die einem FK gegenüberliegende Rolle). Wird nur erstellt mit der Option --createMetaInfo.                                                  |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_key\_object              | Hilfstabelle für den ID-Generator. Wird beim Export nicht benötigt.                                                                                                                                                                  |
+-----------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

TODO alle Tabelle beschreiben

t\_ili2db\_basket
......................
Angaben zu den einzelnen Behältern (Ein Behälter ist eine Instanz eines TOPICs).

- t_id Diese Spalte ist der Datenbank interne Primärschlüssel (und nicht die BID aus der Transferdatei). Wird mit der Option --createBasketCol von den einzelnen Records für die Objekte via Spalte t_basket referenziert. 
- dataset Fremdschlüssel auf die Tabelle t\_ili2db\_dataset
- topic Der qualifizierte Name des Interlis-TOPICs.
- t_ili_tid BID aus der Transferdatei falls das Topic ein stabild BID hat (BASKET OID AS) oder die Option --importBid beim Datenimport benutzt wurde. Wird beim Datenexport mittels --baskets benutzt, um zu ermitteln, welche Objekte exportiert werden sollen.
- attachmentKey Nicht mehr verwenden, wird entfernt. (Als Pfad-Prefix für Zusatzdatein zu diesem Behälter gedacht.)

t\_ili2db\_column_prop
......................

Weitere Angaben zu den DB-Spalten aus dem Interlis Modell (z.B. ob es MTEXT ist). 
Wird nur erstellt mit Option --createMetaInfo. Die Tabelle ist so aufgebaut, dass
sie beliebige (auch zukünftige) Werte/Zusatzangaben aufnehmen kann.

- tablename Name der Tabelle
- columnname Name der Spalte
- subtype Name des Subtyps (Inhalt der Spalte t_type), falls die Angabe nicht für alle Klassen gilt
- tag Name des Wertes/der Zusatzinformation
- setting Wert/Zusatzinformation

+------------------------------------+---------------------------------------------------------------+
| Tag                                | Beschreibung                                                  |
+====================================+===============================================================+
| ``ch.ehi.ili2db.typeKind``         | Art des Interlis Datentyps. Mögliche Werte:                   |
|                                    |                                                               |
|                                    | - TextType: MTEXT \| TEXT \| NAME \| URI                      |
|                                    | - EnumerationType: ENUM                                       | 
|                                    | - EnumTreeValueType: ENUMTREE                                 | 
|                                    | - AlignmentType: ENUM                                         |
|                                    | - BooleanType: BOOLEAN                                        |
|                                    | - NumericType: NUMERIC                                        |
|                                    | - FormattedType: FORMATTED                                    |
|                                    | - DateTimeType: DATE \| TIMEOFDAY \| DATETIME                 |
|                                    | - CoordinateType: COORD \| MULTICOORD                         |
|                                    | - OIDType: OID                                                |
|                                    | - BlackboxType: XML \| BINARY                                 |
|                                    | - ClassType: CLASSQNAME                                       |
|                                    | - AttributePathType: ATTRIBUTEQNAME                           |
|                                    | - LineType: POLYLINE \| SURFACE \| AREA \|                    |
|                                    |              MULTIPOLYLINE \| MULTISURFACE \| MULTIAREA       |
|                                    | - ReferenceAttr: REFERENCE                                    |
|                                    | - RestrictedStructureRef: STRUCTURE                           |
|                                    |                                                               |
|                                    | Generell: Domain basiert oder Inline wird nicht unterschieden.|
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c1Min``            | Bei Geometriespalten der Minimalwert der 1. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c1Max``            | Bei Geometriespalten der Maximalwert der 1. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c2Min``            | Bei Geometriespalten der Minimalwert der 2. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c2Max``            | Bei Geometriespalten der Maximalwert der 2. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c3Min``            | Bei Geometriespalten der Minimalwert der 3. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.c3Max``            | Bei Geometriespalten der Maximalwert der 3. Dimension         |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.geomType``         | Bei Geometriespalten die Art der Geometrie. Mögliche Werte:   |
|                                    | POINT, LINESTRING, POLYGON, MULTIPOINT, MULTILINESTRING       |
|                                    | MULTIPOLYGON, GEOMETRYCOLLECTION, CIRCULARSTRING              |
|                                    | COMPOUNDCURVE, CURVEPOLYGON, MULTICURVE, MULTISURFACE         |
|                                    | POLYHEDRALSURFACE, TIN, TRIANGLE                              |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.srid``             | Bei Geometriespalten der EPSG Code                            |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.coordDimension``   | Bei Geometriespalten die Dimension der Geometrie              |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.dispName``         | Benutzerfreundliche Bezeichnung der Spalte (z.B. im UI)       |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.textKind``         | Falls mehrzeilige Textspalte, der Wert MTEXT                  |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.unit``             | Name der numerischen Einheit z.B. m                           |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.foreignKey``       | Bei Fremdschlüsseln der Name der Zieltabelle. Damit auch      |
|                                    | ohne ``--createFk`` die Beziehungen ermittelt werden können   |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.enumDomain``       | Bei Aufzählungsattributen der Name der konkreten Aufzählung.  |
|                                    | Damit auch bei erweiterten Attributen/Aufzählungen die für die|
|                                    | Subklasse relevante Aufzählung ermittelt werden kann          |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.oidDomain``        | Bei Klassen mit OID der qualifizierte Name des                |
|                                    | OID-Wertebereichs (z.B. INTERLIS.UUIDOID)                     |
+------------------------------------+---------------------------------------------------------------+

t\_ili2db\_nls
......................
Mehrsprechige Daten je Modell-Element. 
Wird nur erstellt mit der Option ``--createNlsTab``.

- ilielement Qualifizierter Name des betroffenen Interlis-Elements in der Wurzel-Original-Sprache (dem Modell, das keine Übersetzung ist)
- lang Sprachcode
- uivariant Code für unterschiedliche Benutzer-Schnittstellen  (z.B. kleiner/grosser Bildschirm) (``ili`` für Einträge die durch den Modellimport erzeugt werden)
- label Name des Elementes (oder Meta-Attrubut ili2db.dispName (bzw. ili2db.dispName_{LANG}) falls vorhanden)
- mnemonic Tastenkürzel oder Kurzname des Elementes
- tooltip kurze Beschreibung des Elementes
- descr längere Beschreibung des Elementes
- symbol Symbol des Elementes(z.B. für Werkzeugleiste)


t\_ili2db\_table_prop
......................
Weitere Angaben zu den DB-Tabellen aus dem Interlis Modell (z.B. ob es 
eine Tabelle mit Aufzählwerten ist). Wird nur erstellt mit Option --createMetaInfo.

- tablename Name der Tabelle
- tag Name des Wertes/der Zusatzinformation
- setting Wert/Zusatzinformation

+------------------------------------+---------------------------------------------------------------+
| Tag                                | Beschreibung                                                  |
+====================================+===============================================================+
| ``ch.ehi.ili2db.dispName``         | Benutzerfreundliche Bezeichnung der Tabelle (z.B. im UI)      |
+------------------------------------+---------------------------------------------------------------+
| ``ch.ehi.ili2db.tableKind``        | Art/Zweck der Tabelle                                         |
|                                    |                                                               |
|                                    | - ``ENUM``  Tabelle für eine Interlis Aufzählung              |
|                                    | - ``SECONDARY`` Hilfstabelle, falls eine Interlis Klasse      |
|                                    |   in mehrere Tabellen unterteilt wird (z.B. wegen der         |
|                                    |   Option --oneGeomPerTable)                                   |
|                                    | - ``ASSOCIATION`` Tabelle für eine Interlis Beziehung         |
|                                    | - ``STRUCTURE``  Tabelle für eine Interlis Struktur           |
|                                    | - ``CATALOGUE`` Tabelle für eine Interlis Klasse, die direkt  |
|                                    |   oder indirekt CatalogueObjects_V1.Catalogues.Item erweitert |
+------------------------------------+---------------------------------------------------------------+

t\_ili2db\_meta\_attrs
......................
Interlis-Meta-Attribute (auch für Modell-Elemente, denen kein DB-Element 
entspricht; z.B. die einem FK gegenüberliegende Rolle). 
Diese Tabelle wird nur erstellt 
mit der Option ``--createMetaInfo``.
Die Tabelle enthält auch von ili2db aufgrund des Modells generierte 
Meta-Attribute (z.B. Kardinaliät einer Rolle). Die Namen dieser generierten
Meta-Attribute beginnen 
mit ``ili2db.ili.``.

- ilielement Qualifizierter Name des betroffenen Interlis-Elements
- attr_name Name des Meta-Attributs
- attr_value Wert des Meta-Attributs

Von ili2db werden automatisch aufgrund des Interlis-Modells folgende 
Meta-Attribute generiert:

+------------------------------------+---------------------------------------------------------------+
| Tag                                | Beschreibung                                                  |
+====================================+===============================================================+
| ``ili2db.ili.lang``                | Sprache der Namen der Modellelemente (gemäss INTERLIS-Modell).|
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.translationOf``       | Names des Modells in der Ursprungssprache, falls es           |
|                                    | übersetzt ist (``TRANLSLATION OF``)                           |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.topicClasses``        | Lister der in einem Behälter möglichen Objekte                |
|                                    | (als Leerzeichen getrennte Liste der SQL-Namen der Klassen)   |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.attrCardinalityMin``  | minimum Anzahl Werte eines Attributes                         |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.attrCardinalityMax``  | maximum Anzahl Werte eines Attributes                         |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.assocCardinalityMin`` | minimum Anzahl Objekte zu einer Bezeihungs-Rolle              |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.assocCardinalityMax`` | maximum Anzahl Objekte zu einer Beziehungs-Rolle              |
+------------------------------------+---------------------------------------------------------------+
| ``ili2db.ili.assocKind``           | Stärke der Rolle                                              |
|                                    |                                                               |
|                                    | - ``ASSOCIATE`` Assoziation (``--``)  Beziehung zwischen      |
|                                    |          unabhängigen Objekten                                |
|                                    | - ``AGGREGATE`` Aggregation (``-<>``)  Beziehung zwischen     |
|                                    |    Teilobjekten und einem Ganzen. Ein Teilobjekt              |
|                                    |    kann Teil von mehreren Ganzen sein                         |
|                                    | - ``COMPOSITE`` Komposition (``-<#>``)  Beziehung zwischen    |
|                                    |          Teilobjekten und einem Ganzen. Ein Teilobjekt        |
|                                    |          kann nur Teil von einem Ganzen sein                  |
+------------------------------------+---------------------------------------------------------------+

Namenskonvention
~~~~~~~~~~~~~~~~

Die Abbildung der Klassennamen in Tabellennamen erfolgt nach drei
möglichen Strategien:

Unqualifiziert
	Es wird der Klassennamen verwendet (ohne voranstellen
	von Topic- und/oder Model-Namen). Falls der Name schon benutzt ist, wird
	der voll qualifizierte Name verwendet.

Mit Topic qualifiziert
	Dem unqualifizierten Klassennamen wird der
	Topic-Name vorangestellt. Falls der Name schon benutzt ist, wird der
	voll qualifizierte Name verwendet.

Voll qualifiziert
	Der Tabellenname wird aus Model-, Topic- und
	Klassenname zusammengesetzt.

Falls der Tabellenname zu lang ist, wird er gekürzt, in dem die Vokale
entfernt werden (ausser die ersten beiden und letzten beiden
Buchstaben). Falls er danach immer noch zu lang ist, werden in der der
Mitte des Namens Buchstaben entfernt.

Falls der Tabellenname nun einem SQL-Schlüsselwort entspricht, wird er
um ein führeneds ‚a‘ ergänzt.

Falls der Tabellenname nun nicht eindeutig ist, wird er um eine Ziffer
ergänzt: ‚0‘, ‚1‘, usw. bis er eindeutig ist.

Die automatische Namensabbildung kann übersteuert werden, indem vor dem
ersten Import entsprechende Einträge in der Tabelle t\_ili2db\_classname
gemacht werden.

.. [1]
   GML 3.2; die verwendeten Kodierungsregeln entsprechen eCH-0118-1.0

.. [2]
   Der SQL-Name ergibt sich aus den Namenskonventionen. Die konkrete
   Übersetzung ist in der Tabelle T\_ILI2DB\_CLASSNAME hinterlegt.

.. [3]
   Der SQL-Name ergibt sich aus den Namenskonventionen. Die konkrete
   Übersetzung ist in der Tabelle T\_ILI2DB\_ATTRNAME hinterlegt.

.. [4]
   Der SQL-Name ergibt sich aus den Namenskonventionen. Die konkrete
   Übersetzung ist in der Tabelle T\_ILI2DB\_CLASSNAME hinterlegt.
