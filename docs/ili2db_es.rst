==================
ili2db-Instructivo
==================

Resumen
=======

Ili2pg, ili2fgdb e ili2gpkg es un programa creado en Java que escribe un
archivo de transferencia interlis (itf o xtf) en una base de datos
(PostgreSQL/Postgis, GeoPackage, Filegeodatabase) conforme a un modelo
de Interlis (ili) y usando una transferencia 1:1 o que crea un archivo
de transferencia desde la base de datos mediante una transferencia 1:1.
Las siguientes funciones son posibles:

-  1:1-Conversión de un archivo de modelo (ili) a un esquema de una base
   de datos.

-  1:1-Import de un archivo de transferencia (itf o xtf) con un archivo
   de modelo asociado en una base de datos.

-  1:1-Export de tablas de base de datos en un archivo de transferencia
   (itf o xtf).

-  1:1-Export de tablas de base de datos en un archivo de transferencia
   GML [1]_.

1:1-Import a una Base de Datos
------------------------------

El import 1:1 escribe todos los objetos (los datos) del archivo de
transferencia de Interlis en la base de datos. Si las tablas de la base
de datos respectivamente el esquema todavía no existen, se crearán las
tablas y el esquema durante el import.

Es posible crear un esquema con tablas vacías del modelo Interlis en la
base de datos (sólo PostGIS).

Con Interlis 1 las geometrias de Area- y Surface pueden ser polygonadas
ocpionalmente.

Los arcos son importados como arcos y por lo tanto no segmentados. Sin
embargo pueden ser segmentados opcionalmente.

Los atributos del tipo de datos Interlis "Enumeration" también se pueden
importar opcionalmente como texto (por ejemplo tipo de cobertura de
suelo 0 = "Construccion").

A las geometrías se les puede asignar un código EPSG. Los atributos de
geometría pueden indexarse opcionalmente.

1:1-Export desde la Base de Datos
---------------------------------

El export 1:1 escribe todas las tablas de un modelo Interlis en un
archivo de transferencia de Interlis (itf o xtf).

Las geometrías de tipo Area y Surface se convierten en líneas durante el
export von Interlis 1.

Mensajes Log (Registro)
-----------------------

Los mensajes log (registro) están destinados a mostrar al usuario lo que
está haciendo el programa. Al principio, aparece información sobre la
versión del programa. Si el programa se ejecuta sin errores, el
siguiente mensaje se emite al final.::

  Info: ili2fgdb-3.10.7-20170823

  ...

  Info: compile models...

  ...

  Info: ...export done

En caso de error, se indica al final del programa. Sin embargo, el error
por lo normal se emite desde antes.::

  Info: ili2fgdb-3.10.7-20170823

  ...

  Info: compile models...

  ...

  Error: DM01.Bodenbedeckung.BoFlaeche\_Geometrie: intersection tids 48, 48

  ...

  Error: ...import failed

Requisitos de sistema en tiempo de ejecución
--------------------------------------------

El programa requiere Java 1.8.

**PostGIS:** Se requiere por lo menos PostgreSQL 8.3 con PostGIS 1.5. En
el caso de usar el modelo de datos Interlis INTERLIS.UUIDOID como OID,
la función uuid\_generate\_v4() será empleada. Para ello es necesario de
configurar la extensión de PostgreSQL uuid-ossp (CREATE EXTENSION
"uuid-ossp";).

**FileGDB:** `*Visual Studio 2015 C y C++
Runtimes* <https://www.microsoft.com/en-us/download/details.aspx?id=48145>`__
es requerido. Según versión Java (la versión de Java y no la versión de
Windows es relevante) es necesario instalar la versión 32-bit o 64-bit
de esta libreria de entorno en tiempo de ejecución. En el caso que esta
librería no este instalada, se genera un error durante la carga de la
FileGDB.dll.

Licencia
--------

GNU Lesser General Public License

Funcionamiento
==============

In den folgenden Abschnitten wird die Funktionsweise anhand einzelner
Anwendungsfälle beispielhaft beschrieben. Die detaillierte Beschreibung
einzelner Funktionen ist im Kapitel „Referenz“ zu finden.

En las siguientes secciones se describe el funcionamiento según los
casos de aplicación. La descripción detallada de las funciones
especificas se encuentra en el capítulo "Referencia".

Funciones de Import
-------------------

Caso 1
~~~~~~

Las tablas aún no existen y se van a crear en la base de datos.

**PostGIS:** java -jar ili2pg.jar --schemaimport --dbdatabase mogis
--dbusr julia --dbpwd romeo path/to/dm01av.ili

**GeoPackage:** java -jar ili2gpkg.jar --schemaimport --dbfile
mogis.gpkg path/to/dm01av.ili

**FileGDB:** java -jar ili2fgdb.jar --schemaimport --dbfile mogis.gdb
path/to/dm01av.ili

No se importan datos, sólo se crean las tablas vacías.

**PostGIS:** Las tablas vacías se crean en el esquema por defecto del
usuario julia. Las columnas de geometría están registradas en la tabla
public.geometry\_columns.

El equipo local se acepta como el host y el puerto predeterminado para
la conexión a la base de datos.

**GeoPackage:** Las columnas de geometría están registradas en las
tablas gpkg\_contents y gpkg\_geometry\_columns.

Si el archivo mogis.gpkg aún no existe, se genera y se inicializa con
las metatablas requeridas para GeoPackage. Si el archivo ya existe, se
complementan las tablas.

**FileGDB:** Si el archivo mogis.gdb aún no existe, se le crea. Si el
archivo ya existe, se complementan las tablas.

Caso 2 (solo PostGIS)
~~~~~~~~~~~~~~~~~~~~~

El esquema deseado y las tablas no existen y se debe crear el esquema de
BD y el modelo de datos:

**PostGIS:** java -jar ili2pg.jar --schemaimport --dbdatabase mogis
--dbschema dm01av --dbusr julia --dbpwd romeo path/to/dm01av.ili

No se importan datos, sólo se crean el esquema dm01av y las tablas
vacías. Las columnas de geometría están registradas en la tabla
public.geometry\_columns.

Caso 3
~~~~~~

Las tablas no existen y deben crearse en la base de datos; los datos
deben importarse:

**PostGIS:** java -jar ili2pg.jar --import --dbhost ofaioi4531 --dbport
5432 --dbdatabase mogis --dbusr julia --dbpwd romeo --createEnumTabs
--createBasketCol --log path/to/logfile path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --dbfile mogis.gpkg
--createEnumTabs --createBasketCol --log path/to/logfile
path/to/260100.itf

**FileGDB:** java -jar ili2fgdb.jar --import --dbfile mogis.gdb
--createEnumTabs --createBasketCol --log path/to/logfile
path/to/260100.itf

Todas las tablas se crean en la base de datos y se importa el archivo de
transferencia 260100.itf. Se registran las columnas de geometría. Como
Primary-Key se crea un atributo adicional (t\_id). Además, se crea un
atributo t\_basket. Este muestra, como clave foranea, a una metatabla de
ayuda (fecha de import, usuario, nombre de modelo, ruta del archivo
itf).

Los tipos enumerados son desplegados en tablas de consulta
(Lookup-tables).

Se crea un archivo log (registro). Contiene la hora del import, nombre
de usuario, parámetros de base de datos (sin contraseña), nombre (de
rutas completas) del archivo ili e itf, todos los nombres de las tablas
importadas, incluido el número de elementos importados por tabla. Los
mensajes de error (en el caso de un import interrumpido) también se
escriben en el archivo log.

Caso 4
~~~~~~

Las tablas ya existen y el contenido de las tablas debe ser ampliado:

**PostGIS:** java -jar ili2pg.jar --import --dbdatabase mogis --dbusr
julia --dbpwd romeo path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --dbfile mogis.gpkg
path/to/260100.itf

**FileGDB:** java -jar ili2fgdb.jar --import --dbfile mogis.gdb
path/to/260100.itf

Das Itf 260100.itf wird importiert und die Daten den bereits vorhanden
Tabellen hinzugefügt. Die Tabellen können zusätzliche Attribute
enthalten (z.B. bfsnr, datum etc.), welche beim Import leer bleiben.

El itf 260100.itf es importado y se agregan los datos a las tablas ya
existentes. Las tablas pueden contener atributos adicionales (por
ejemplo, bfsnr [un codigo municipal], fecha, etc.) que permanecen vacíos
en el import.

Caso 5
~~~~~~

Las tablas ya existen y el contenido de las tablas debe ser reemplazado
por el contenido del itf:

**PostGIS:** java -jar ili2pg.jar --import --deleteData --dbdatabase
mogis --dbusr julia --dbpwd romeo path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --deleteData --dbfile
mogis.gpkg path/to/260100.itf

**FileGDB:** java -jar ili2fgdb.jar --import --deleteData --dbfile
mogis.gdb path/to/260100.itf

El itf 260100.itf es importado y se eliminan los datos existentes en las
tablas existentes. Las tablas pueden contener atributos adicionales (por
ejemplo, bfsnr [un codigo municipal], fecha, etc.) que permanecen vacíos
en el import.

Caso 6
~~~~~~

Las enumeraciones también se agregan como atributos de texto:

**PostGIS:** java -jar ili2pg.jar --import --createEnumTxtCol
--dbdatabase mogis --dbusr julia --dbpwd romeo path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --createEnumTxtCol
--dbfile mogis.gpkg path/to/260100.itf

**FileGDB:** java -jar ili2fgdb.jar --import --createEnumTxtCol --dbfile
mogis.gdb path/to/260100.itf

El itf es importado en la base de datos. Además, los atributos de tipo
enumeration se agregan en su representación textual (atributo "art" = 0
⇒ "art\_txt" = "construccion").

Caso 7
~~~~~~

Se agrega un identificador especial SRS (Spatial Reference System) a las
geometrías:

**PostGIS:** java -jar ili2pg.jar --import --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbfile mogis.gpkg path/to/260100.itf

**FileGDB:** java -jar ili2fgdb.jar --import --defaultSrsAuth EPSG
--defaultSrsCode 2056 --dbfile mogis.gdb path/to/260100.itf

El itf es importado en la base de datos. Además, a cada geometría se añade un SRS-ID (Código EPSG 2056). El mismo identificador también se utiliza para registrar las columnas de geometría en las metatablas de la base de datos.

Caso 8
~~~~~~

Las geometrías están indexadas:

**PostGIS:** java -jar ili2pg.jar --import --createGeomIdx --dbdatabase
mogis --dbusr julia --dbpwd romeo path/to/260100.itf

**GeoPackage:** java -jar ili2gpkg.jar --import --createGeomIdx --dbfile
mogis.gpkg path/to/260100.itf

El itf es importado en la base de datos. Las geometrías están indexadas.

**FileGDB:** Las geometrías siempre están indexadas.

Caso 9
~~~~~~

Si al importar el Itf ocurren errores (por ejemplo, falta de conformidad
con el modelo o restricciones [constraints] violadas en la BD), se
cancelerá el import.

**PostGIS, GeoPackage:** En caso de error, no se importa ningún dato, es
decir la importación en la base de datos es un solo commit.

**FileGDB:** Debido a que FileGDB no soporta transacciones, los datos se
importan solo parcialmente y el FileGDB al final puede estar en un
estado inconsistente.

Funciones de Export
-------------------

Caso 1
~~~~~~

Las tablas se escriben desde la base de datos en un archivo de
transferencia de Interlis 1 (itf):

**PostGIS:** java -jar ili2pg.jar --export --models DM01AV --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/output.itf

**GeoPackage:** java -jar ili2gpkg.jar --export --models DM01AV --dbfile
mogis.gpkg path/to/output.itf

**FileGDB:** java -jar ili2fgdb.jar --export --models DM01AV --dbfile
mogis.gdb path/to/output.itf

Las tablas se escriben, de acuerdo con el modelo Interlis DM01AV, en el
archivo de transferencia de Interlis 1 output.itf. Las tablas que faltan
en la base de datos se escriben en el archivo como tablas vacías o no
(según la definición en el modelo de datos). Los atributos que faltan en
una tabla de la base de datos se sustituyen por un "@".

Anhand des Parameters --models wird definiert, welche Daten exportiert
werden. Alternativ kann auch der Parameter --topics, --baskets oder
--dataset verwendet werden, um die zu exportierenden Daten auszuwählen.
Einer dieser Parameter muss also zwingend beim Export angegeben werden.

El parámetro --models define qué datos se exportan. Alternativamente, el
parámetro --topics, --baskets o --dataset puede utilizarse para
seleccionar los datos que se van a exportar. Por lo tanto, es
obligatorio de especificar a uno de estos parámetros durante el export.

Caso 2
~~~~~~

Las tablas se escriben desde la base de datos en un archivo de
transferencia de Interlis 2 (xtf):

**PostGIS:** java -jar ili2pg.jar --export --models DM01AV --dbhost
ofaioi4531 --dbport 5432 --dbdatabase mogis --dbusr julia --dbpwd romeo
path/to/output.xtf

**GeoPackage:** java -jar ili2gpkg.jar --export --models DM01AV --dbfile
mogis.gpkg path/to/output.xtf

**FileGDB:** java -jar ili2fgdb.jar --export --models DM01AV --dbfile
mogis.gdb path/to/output.xtf

Las tablas se escriben, de acuerdo con el modelo Interlis DM01AV, en el
archivo de transferencia de Interlis 2 output.xtf. Tablas y atributos
que faltan en la base de datos no se escriben en el archivo.

El parámetro --model define qué datos se exportan. Alternativamente, el
parámetro --topics, --baskets o --dataset puede utilizarse para
seleccionar los datos que se van a exportar Por lo tanto, es obligatorio
de especificar a uno de estos parámetros durante el export.

Referencia
==========

Las siguientes secciones describen, pero de manera aislada, aspectos
especificos. El funcionamiento como un todo se describe a modo de casos
de uso en el capítulo "Funcionalidad" (véase más arriba).

La documentación es válida para todas las variantes ili2xy, a menos que
exista una referencia específica a PostGIS, GeoPackage o FileGDB.

Sintaxis de llamada
-------------------

**PostGIS:** java -jar ili2pg.jar [Options] [file]

**GeoPackage:** java -jar ili2gpkg.jar [Options] [file]

**FileGDB:** java -jar ili2fgdb.jar [Options] [file]

Opciones:

+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Opción                        | Descripción                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
+===============================+========================================================================================================================================================================================================================================================================================================================================================================================================================================================================================+
| --import                      | Importa datos de un archivo de transferencia a la base de datos.                                                                                                                                                                                                                                                                                                                                                                                                                       |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | Las tablas también se crean implícitamente, si áun no existen (ver el capítulo sobre reglas de mapeo). Si las tablas ya existen en la base de datos, pueden contener columnas adicionales (por ejemplo, bfsnr [nota: un código municipal], fecha, etc.) que permanecen vacías en la importación.                                                                                                                                                                                       |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | Si durante el import se define un identificador de registro (--dataset), este identificador de registro aún no pueda existir en la base de datos. Para reemplazar los datos existentes, se puede usar la opción --replace.                                                                                                                                                                                                                                                             |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | TO DO: Las tablas ya existen (y (no) corresponden a la clase ili)                                                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --update                      | Actualiza los datos en la base de datos utilizando un archivo de transferencia, es decir, se insertan nuevos objetos, se actualizan los objetos existentes y se eliminan los objetos que ya no están en el archivo de transferencia. Esta función requiere que el esquema de base de datos se haya creado con la opción --createBasketCol y que las clases y los temas tengan un OID estable.                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --replace                     | Reemplaza los datos de la base de datos con un identificador de registro/conjunto de datos (--dataset) con los datos de un archivo de transferencia. Esta función requiere que el esquema de la base de datos se haya creado con la opción --createBasketCol.                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --delete                      | Elimina los datos de la base de datos utilizando un identificador de registro de datos (--dataset). Esta función requiere que el esquema de la base de datos se haya creado con la opción --createBasketCol.                                                                                                                                                                                                                                                                           |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --export                      | Exporta datos de la base de datos a un archivo de transferencia.                                                                                                                                                                                                                                                                                                                                                                                                                       |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | El parámetro --models, --topics, --baskets o --dataset define qué datos se exportan.                                                                                                                                                                                                                                                                                                                                                                                                   |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | Si los datos se escriben en Interlis 1, Interlis 2 o formato GML depende de la extensión del nombre de archivo de salida. La extensión .itf se debe utilizar para un archivo de transferencia de Interlis 1. La extensión .xtf para Interlis 2 y, la extensión .gml para un archivo de transferencia GML.                                                                                                                                                                              |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | Las opciones --topics y --baskets requieren que el esquema de la base de datos se haya creado con la opción --createBasketCol.                                                                                                                                                                                                                                                                                                                                                         |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --schemaimport                | Crea la estructura de tabla en la base de datos (vea el capítulo Reglas de mapeo).                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --validConfig filename        | Nombre del archivo de configuración que se utilizará para la validación.                                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableValidation           | Desactiva la validación de los datos.                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableAreaValidation       | Desactiva la validación de la topología AREA.                                                                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --forceTypeValidation         | Restringe el ablandamiento de la validación usando --validConfig a "multiplicity".                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbhost host                 | **PostGIS:** El nombre host de la base de datos. El valor por defecto es localhost.                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbport port                 | **PostGIS:** Numero de puerto en el que se puede acceder a la base de datos. Por defecto es 5432.                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbdatabase database         | **PostGIS:** Nombre de la base de datos.                                                                                                                                                                                                                                                                                                                                                                                                                                               |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbusr username              | **PostGIS:** El nombre de usuario para el acceso a la base de datos y las entradas en metatablas.                                                                                                                                                                                                                                                                                                                                                                                      |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | **GeoPackage:** El nombre de usuario para entradas en metatablas.                                                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbpwd password              | **PostGIS:** El password para el acceso a la base de datos.                                                                                                                                                                                                                                                                                                                                                                                                                            |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbschema schema             | **PostGIS:** Define el nombre del esquema de la base de datos. Default no es un valor, es decir el esquema actual del usuario definido como -user.                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dbfile filename             | **GeoPackage:** Nombre de archivo de GeoPackage.                                                                                                                                                                                                                                                                                                                                                                                                                                       |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | **FileGDB:** Nombre de archivo de la FileGeodatabase de ESRI.                                                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --setupPgExt                  | **PostGIS:** crea la extensión PostgreSQL 'uuid-ossp' y 'postgis' (si aún no creadas).                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --deleteData                  | Durante un import de datos (--import), se eliminan todos los datos de las tablas existentes/usadas (con DELETE, la estructura de la tabla permanece).                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --defaultSrsAuth auth         | SRS Authority para columnas de geometría, donde este valor no se puede determinar (siempre es el caso para Interlis 1 e Interlis 2.3). La capitalización es significativa. El valor por defecto es EPSG.                                                                                                                                                                                                                                                                               |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --defaultSrsCode code         | Código de SRS para columnas de geometrias, donde este valor no se puede determinar (siempre es el caso para Interlis 1 e Interlis 2.3). El valor por defecto es es 21781.                                                                                                                                                                                                                                                                                                              |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --fgdbXyResolution value      | **FileGDB:** Desintegración/resolución XY para columnas de geometría.                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --fgdbXyTolerance value       | **FileGDB:** Tolerancia XY para columnas de geometría.                                                                                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --modeldir path               | Rutas de archivos que contienen archivos de modelo (ili). Varias rutas pueden ser separados por punto y coma (;). Las URL de los repositorios de modelos también son posibles. El valor predeterminado es:                                                                                                                                                                                                                                                                             |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | %ILI\_FROM\_DB;%XTF\_DIR;http://models.interlis.ch/;%JAR\_DIR                                                                                                                                                                                                                                                                                                                                                                                                                          |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | Se apoyan los siguientes espaciadores:                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | %ILI\_FROM\_DB es un espaciador para los modelos existentes en la base de datos (en la tabla t\_ili2db\_model).                                                                                                                                                                                                                                                                                                                                                                        |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | %XTF\_DIR es un espaciador para el directorio con el archivo de transferencia.                                                                                                                                                                                                                                                                                                                                                                                                         |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | %JAR\_DIR es un espaciador para el directorio del programa ili2db (ili2pg.jar respectivamente arcguvo ili2gpkg.jar).                                                                                                                                                                                                                                                                                                                                                                   |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | %ILI\_FROM\_DB como regla, debe ser la primera ruta (para que varios Imports y Export usen el mismo modelo).                                                                                                                                                                                                                                                                                                                                                                           |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | El nombre del primer modelo (modelo principal) al que ili2db busca el archivo ili no depende de la versión de idioma de Interlis. Se busca con la siguiente secuencia para un archivo ili: primero Interlis 2.3, luego 1.0 y último 2.2.                                                                                                                                                                                                                                               |
|                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|                               | En un import, se tiene en cuenta la versión del lenguaje INTERLIS del modelo principal, de la manera que por ejemplo el modelo Units se destingue para ili2.2 o ili2.3.                                                                                                                                                                                                                                                                                                                |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --models modelname            | Nombres del modelo (no necesariamente idénticos al nombre de archivo!) para los que se va a crear la estructura de tabla en la base de datos. Varios nombres de modelos pueden ser separados por punto y coma (;). Normalmente, el nombre no tiene que ser especificado, y el programa determina el valor automáticamente de los datos. Si sólo se especifica un archivo ili como archivo, el nombre del último modelo de este archivo ili se toma como nombre de modelo.              |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dataset name                | Nombre/identificador del registro/conjunto de datos (forma corta para varios BID). Por ejemplo, un BFSNr [nota: codigo municipal] o una abreviación de departamento/cantón. Requiere la opción --createBasketCol.                                                                                                                                                                                                                                                                      |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --baskets BID                 | BID de los Baskets a importar o exportar. Varios BID pueden ser separados por punto y coma (;).                                                                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --topics topicname            | Nombre de topic de los Baskets que se importarán o exportarán. Varios nombres pueden ser separados por punto y coma (;). Se debe utilizar el nombre de topic calificado (Model.Topic).                                                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createscript filename       | Crea un script SQL además de la estructura de tablas en la base de datos para crear la estructura de tabla independiente del programa. El script se genera adicionalmente a las tablas de la base de datos, es decir no es posible crear solo el script (sin la base de datos).                                                                                                                                                                                                        |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --dropscript filename         | Crea script SQL para eliminar la estructura de tablas independiente del programa.                                                                                                                                                                                                                                                                                                                                                                                                      |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --preScript filename          | Script SQL que se ejecuta antes del Import/Export (del esquema).                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --postScript filename         | Script SQL que se ejecuta después del Import/Export (del esquema).                                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --noSmartMapping              | Todas las optimizaciones de mapeo estructural están deshabilitadas. (ver --smart1Inheritance, --coalesceCatalogueRef, --coalesceMultiSurface, --coalesceMultiLine, --expandMultilingual)                                                                                                                                                                                                                                                                                               |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --smart1Inheritance           | Mapea la jerarquía de herencia con una estrategia dinámica. La estrategia NewClass se utiliza para las clases referenciadas y cuyas clases base no se mapean utilizando una estrategia NewClass. Las clases abstractas se mapean mediante una estrategia SubClase. Las clases concretas, sin una clase base o sus clases base directas con una estrategia SubClase, se mapean usando una estrategia NewClass. Todas las demás clases se mapean utilizando una estrategia SuperClase.   |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --smart2Inheritance           | Mapea la jerarquía de herencia con una estrategia dinámica. Las clases abstractas se mapean mediante una estrategia SubClase. Las clases concretas se mapean utilizando una estrategia NewAndSubClass.                                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceCatalogueRef        | Los atributos de estructura cuya máxima cardinalidad es 1, cuyo tipo de base es CHBase:CatalogReference o CHBase:MandatoryCatalogueReference y que no tienen otros atributos que no sean "Reference", se mapean directamente con una clave foránea a la tabla de destino (que realiza la clase concreta CHBase:Item), es decir, sin registro en la tabla para la estructura con el atributo "Referencia".                                                                              |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceMultiSurface        | Los atributos de estructura cuya máxima cardinalidad es 1, cuyo tipo de base es CHBase:MultiSurface y que no tienen otros atributos que no sean "Surfaces" se mapean directamente como columna con el tipo MULTISURFACE (o MULTIPOLYGON, si --strokeArcs).                                                                                                                                                                                                                             |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --coalesceMultiLine           | Los atributos de estructura cuya máxima cardinalidad es 1, cuyo tipo de base es CHBase:MultiLine y que no tienen otros atributos que no sean "Lines" se mapean directamente como columna con el tipo MULTICURVE (o MULTILINESTRING, si --strokeArcs).                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --expandMultilingual          | Los atributos de estructura cuya máxima cardinalidad es 1, cuyo tipo de base es LocalizationCH\_V1.MultilingualText o LocalizationCH\_V1.MultilingualMText y que no tienen otros atributos que no sean "LocalizedText" se mapean directamente como columnas en la tabla del atributo de estructura, es decir, sin registros en las tablas para las estructuras multilingües.                                                                                                           |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createGeomIdx               | Crea un índice espacial para cada columna de geometría en la base de datos. (véase el capítulo sobre reglas de mapeo/atributos de geometría).                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumColAsItfCode      | Crea el valor enumerado como un código itf para atributos de enumeración. Esta opción sólo está permitida si no hay extensiones de enumeraciones en el modelo. Sin esta opción, el código XTF se utiliza como un valor enumerativo en la base de datos. (véase el capítulo sobre reglas de mapeo/enumeraciones).                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumTxtCol            | Crea una columna adicional con el nombre del valor enumerado para los atributos de enumeración (véase el capítulo sobre reglas de mapeo/enumeraciones de imágenes).                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createEnumTabs              | Crea una tabla con las enumeraciones para cada definición de enumeración (véase el capítulo sobre reglas de mapeo/ enumeraciones).                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createSingleEnumTab         | Crea una tabla única con todos los valores enumerados de todas las definiciones de enumeración (véase el capítulo sobre reglas de mapeo/enumeraciones).                                                                                                                                                                                                                                                                                                                                |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createMetaInfo              | Crea meta-tablas adicionales T\_ILI2DB\_TABLE\_PROP y T\_ILI2DB\_COLUMN\_PROP con más información del modelo Interlis (ver capítulo Metadatos).                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --beautifyEnumDispName        | Mejora el texto que se muestra para el elemento enumerado. Al importar, la columna del código XTF se rellena sin guión bajo ("calle paviementada" en lugar de "calle\_pavimentada") (véase el capítulo sobre reglas de mapeo/enumeraciones).                                                                                                                                                                                                                                           |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createStdCols               | Crea columnas de metadatos T\_User, T\_CreateDate, T\_LastChange adicionales en cada tabla (véase el capítulo sobre reglas de mapeo/enumeraciones).                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --t\_id\_Name name            | Define el nombre de la columna de llave técnica interna de cada tabla (no debe confundirse con el identificador de transferencia externo). El valor predeterminado es T\_Id. (véase el capítulo sobre reglas de mapeo/tablas).                                                                                                                                                                                                                                                         |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --idSeqMin zahl               | **PostGIS:** Define el valor mínimo para el generador de llaves técnicas internas.                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --idSeqMax zahl               | **PostGIS:** Define el valor máximo para el generador de llaves técnicas internas.                                                                                                                                                                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createTypeDiscriminator     | Crea una columna para el discriminador de tipo para cada tabla (incluso si el modelo no utiliza herencia). Para las clases con herencia, la columna siempre se crea. (véase el capítulo sobre reglas de mapeo/tablas).                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --disableNameOptimization     | Desactiva el uso de nombres de clase no calificados. Para todos los nombres de tabla, se utilizan nombres de clase Interlis calificados (Model.Topic.Class) (y mapeados en un nombre de tabla válido). (véase el capítulo sobre reglas de mapeo/convenciones de nomenclatura).                                                                                                                                                                                                         |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --nameByTopic                 | Para todos los nombres de tablas, se utilizan parcialmente nombres de clase Interlis (Title.Class) calificados (y se mapean en un nombre de tabla válido). (véase el capítulo sobre reglas de mapeo/convenciones de nomenclatura).                                                                                                                                                                                                                                                     |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --maxNameLength length        | Define la longitud máxima de los nombres de los elementos de la base de datos (nombres de tablas, nombres de columnas, etc.). El valor predeterminado es 60. Si el nombre Interlis es más largo, se trunca. (véase el capítulo sobre reglas de mapeo/convenciones de nomenclatura).                                                                                                                                                                                                    |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --sqlEnableNull               | No crea instrucciones NOT NULL en columnas de atributos Interlis. (véase el capítulo sobre reglas de mapeo/atributos).                                                                                                                                                                                                                                                                                                                                                                 |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --strokeArcs                  | Segmenta los Arcos durante el import de datos. El radio se pierde. Los arcos están segmentados de tal manera que la desviación de las líneas generadas es menor que la precisión de coordenadas de los puntos de interpolación.                                                                                                                                                                                                                                                        |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --oneGeomPerTable             | **PostGIS:** Crea tablas de ayuda si hay más de un atributo de geometría en una clase/tabla, de modo que sólo haya una columna de geometría por tabla de la base de datos.                                                                                                                                                                                                                                                                                                             |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --skipPolygonBuilding         | Para los archivos ITF, se leen las tablas de línea tal como están en el archivo ITF, es decir no se forman polígonos.                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --skipGeometryErrors          | Los errores de geometría se ignoran (y no se informan). Se deben configurar mensajes de error más específicos mediante --validConfig.                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --keepAreaRef                 | Para atributos AREA de los archivos ITF, se inserta el punto de referencia del perimetro como una columna adicional en la tabla.                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --importTid                   | Lee la identificación de transferencia (del archivo de transferencia) en una columna adicional T\_Ili\_Tid. (véase el capítulo sobre reglas de mapeo/tablas).                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createBasketCol             | Crea una columna adicional T\_basket en cada tabla para identificar el contenedor/basket. (véase el capítulo sobre reglas de mapeo/ metadatos).                                                                                                                                                                                                                                                                                                                                        |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createDatasetCol            | Crea en cada tabla una columna adicional T\_datasetname con el nombre/identificador del registro/conjunto de datos. La opción requiere la opción --dataset. La columna es redundante con la columna datasetname de la tabla t\_ili2db\_dataset (véase el capítulo reglas de mapeo/metadatos).                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createFk                    | Genera una condición de clave foránea para columnas que hacen referencia a registros en otras tablas.                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createFkIdx                 | Crea un índice para cada columna de clave foránea en la base de datos. También se puede utilizar sin la opción --createFk.                                                                                                                                                                                                                                                                                                                                                             |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createUnique                | Crea condiciones UNIQUE para restricciones/[constraints] INTERLIS-UNIQUE en la base de datos (si es posible).                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --createNumChecks             | Crea restricciones/[constraints) CHECK en la base de datos para tipos de datos numéricos.                                                                                                                                                                                                                                                                                                                                                                                              |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ver4-translation            | Usa las reglas de mapeo ili2db 4.x para modelos traducidos (incompatible con ili2db 3.x).                                                                                                                                                                                                                                                                                                                                                                                              |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --translation modelT=modelU   | Para los modelos INTERLIS 1 traducidos (modelT) define el modelo del lenguaje original (ModelU).                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --ILIGML20                    | Para el export utiliza el formato de transferencia eCH-0118-2.0.                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --log filename                | Escribe los mensajes de log (registro) en un archivo.                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --proxy host                  | Define el nombre del host que se utilizará como proxy para acceder a repositorios de modelos.                                                                                                                                                                                                                                                                                                                                                                                          |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --proxyPort port              | Puerto en el proxy.                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --gui                         | Inicia un GUI simple.                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --trace                       | Genera mensajes log (registro) adicionales (importante para el análisis de errores de programa).                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --help                        | Muestra un breve texto de ayuda.                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| --version                     | Muestra la versión del programa.                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
+-------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Reglas de mapeo
---------------

Clases/estructuras
~~~~~~~~~~~~~~~~~~

Dependiendo de la opción del programa, las clases se mapean de forma
diferente. Las reglas de mapeo para el nombre de la tabla se describen
en la sección de convenciones de nomenclatura.

+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Numero**   | **Ejemplo Interlis**   | **Ejemplo SQL**                     | **Comentarios**                                                                                                                                                                                                                                                                                                                                  |
+==============+========================+=====================================+==================================================================================================================================================================================================================================================================================================================================================+
| 1            | CLASS A=               | CREATE TABLE A (                    | Para cada clase se crea una tabla.                                                                                                                                                                                                                                                                                                               |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY           | Cada tabla tiene al menos una columna T\_Id. Esta columna es la llave primaria interna de la base de datos (y no el TID del archivo de transferencia).                                                                                                                                                                                           |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 2            | CLASS A =              | CREATE TABLE A (                    | Con la opción --createTypeDiscriminator, cada tabla (que representa una clase o estructura que no tiene una clase base) recibe una columna adicional, T\_Type. Esta columna contiene el nombre de clase específico (el nombre SQL del nombre calificado de clase Interlis [2]_) del objeto de cada registro.                                     |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY,          | Para las tablas de las clases que tienen una clase base no se crea esta columna.                                                                                                                                                                                                                                                                 |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_Type varchar(60) NOT NULL        |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 3            | CLASS A =              | CREATE TABLE A (                    | La opción --createStdCols proporciona a todas las tablas tres columnas adicionales para la hora del último cambio, la hora de creación y el usuario que realizó el último cambio. Estas columnas deben ser actualizadas por la aplicación, y normalmente se requieren/utilizan para la implementación de un control de concurrencia optimista.   |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY,          |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_LastChange timestamp NOT NULL,   |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_CreateDate timestamp NOT NULL,   |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_User varchar(40) NOT NULL        |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 4            | CLASS A =              | CREATE TABLE A (                    | Con la opción --importTid, cada tabla (que representa una clase que no tiene una clase base) recibe una columna adicional T\_Ili\_Tid. Esta columna contiene el TID del archivo de transferencia.                                                                                                                                                |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY,          | Esta columna NO es la llave primaria interna de la base de datos.                                                                                                                                                                                                                                                                                |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_Ili\_Tid varchar(200) NULL       |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 5            | CLASS A =              | CREATE TABLE A (                    | Con la opción --t\_id\_name oidname se especifica el nombre de la llave primaria interna de la base de datos (no la columna TID del archivo de transferencia).                                                                                                                                                                                   |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | oidname integer PRIMARY KEY         |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 6            | STRUCTURE C =          | CREATE TABLE C (                    | Las estructuras se mapean generalmente como clases.                                                                                                                                                                                                                                                                                              |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END C;                 | T\_Id integer PRIMARY KEY,          | La tabla de estructura contiene adicionalmente una columna T\_seq, la cual especifica el orden de los elementos estructurales.                                                                                                                                                                                                                   |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_seq integer NOT NULL             | Dado que los elementos estructurales no tienen un TID, no obtienen una columna T\_Ili\_Tid con la opción --importTid.                                                                                                                                                                                                                            |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 7            | CLASS A =              | CREATE TABLE A (                    | La opción --createBasketCol proporciona a cada tabla una columna adicional T\_basket. Esta columna contiene la clave foránea en la tabla t\_ili2db\_basket.                                                                                                                                                                                      |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY,          |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_basket integer NOT NULL          |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 8            | CLASS A =              | CREATE TABLE A (                    | La opción --createDatasetCol proporciona a cada tabla una columna adicional, T\_datasetname.                                                                                                                                                                                                                                                     |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              | END A;                 | T\_Id integer PRIMARY KEY,          | La columna es redunante a la columna datasetname de la tabla t\_ili2db\_dataset (vease el capítulo reglas de mapeo/Metadatos).                                                                                                                                                                                                                   |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | T\_datasetname varchar(200)         |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | NOT NULL                            |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        |                                     |                                                                                                                                                                                                                                                                                                                                                  |
|              |                        | );                                  |                                                                                                                                                                                                                                                                                                                                                  |
+--------------+------------------------+-------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Herencia
~~~~~~~~

En general, la herencia se puede modelar de acuerdo con tres estrategias
diferentes:

NewClass
    Esta estrategia es posible para cada clase. En esta estrategia, se
    crea una nueva tabla para una clase, por lo que un objeto Interlis
    se distribuye a registros en varias tablas.

SuperClass
    Esta estrategia sólo es posible para las clases con una super clase.
    En esta estrategia, no se crea ninguna nueva tabla para la clase, es
    decir los atributos de la clase se agregan como columnas adicionales
    en la tabla de la superclase.

SubClass
    Esta estrategia sólo es posible para clases con al menos una
    subclase. En esta estrategia, no se crea ninguna nueva tabla para
    una clase, es decir, los atributos de la clase se complementan como
    columnas adicionales en las tablas de las subclases.

ili2db mapea la herencia según clase con una estrategia diferente
(--smart1Inheritance o --smart2Inheritance) o lo hace para todas las
clases de manera uniforme y de acuerdo con la estrategia NewClass
(--noSmartMapping).

Para -smart1Inheritance se mapea de la siguiente manera: La estrategia
NewClass se aplica para las clases referenciadas y cuyas clases base no
se mapean con una estrategia NewClass. Las clases abstractas se mapean
mediante una estrategia SubClass. Las clases concretas sin una clase
base o cuyas clases base directas son mapeadas con una estrategia
SubClass, se mapean usando una estrategia NewClass. Todas las demás
clases se mapean utilizando una estrategia SuperClass.

--smart2Inheritance mapea de la siguiente manera: Las clases abstractas
son mapeadas usando una estrategia de SubClass. Las clases concretas se
mapean utilizando una estrategia NewAndSubClass.

+--------------+---------------------------+---------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Numero**   | **Ejemplo Interlis**      | **Ejemplo SQL**                 | **Comentarios**                                                                                                                                                                                                                                                          |
+==============+===========================+=================================+==========================================================================================================================================================================================================================================================================+
| 1            | CLASS A =                 | CREATE TABLE A (                | --noSmartMapping crea una tabla para cada clase. Un objeto A devuelve un registro en las tablas A. Un objeto B devuelve un registro en las tablas A y B. El T\_Id es idéntico para ambos registros.                                                                      |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_1 : TEXT\*20;   | T\_Id integer PRIMARY KEY,      |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END A;                    | T\_Type varchar(60) NOT NULL,   |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | CLASS B EXTENDS A =       | Attribut\_1 varchar(20)         |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_2 : TEST\*20;   | );                              |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END B;                    | CREATE TABLE B (                |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | T\_Id integer PRIMARY KEY,      |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | Attribut\_2 varchar(20)         |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | );                              |                                                                                                                                                                                                                                                                          |
+--------------+---------------------------+---------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 2            | CLASS A (ABSTRACT) =      | CREATE TABLE B (                | Para --smart1Inheritance, no se crea ninguna tabla para clases abstractas (A) (a menos que referencian). Se crea una tabla para la clase concreta más general (B). No se crea ninguna tabla para las clases concretas extendidas (C) que extienden una clase concreta.   |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_1 : TEXT\*20;   | T\_Id integer PRIMARY KEY,      |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END A;                    | T\_Type varchar(60) NOT NULL,   |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | CLASS B EXTENDS A =       | Attribut\_1 varchar(20),        |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_2 : TEST\*20;   | Attribut\_2 varchar(20),        |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END B;                    | Attribut\_3 varchar(20)         |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | CLASS C EXTENDS B =       | );                              |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_3 : TEST\*20;   |                                 |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END C;                    |                                 |                                                                                                                                                                                                                                                                          |
+--------------+---------------------------+---------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 3            | CLASS A (ABSTRACT) =      | CREATE TABLE B (                | Para --smart2Inheritance, no se crea ninguna tabla para las clases abstractas (A) (incluso si son referenciadas). Para las clases concretas (B y C), se crea una tabla completa (incluidos los atributos heredados).                                                     |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_1 : TEXT\*20;   | T\_Id integer PRIMARY KEY,      |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END A;                    | T\_Type varchar(60) NOT NULL,   |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | CLASS B EXTENDS A =       | Attribut\_1 varchar(20),        |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_2 : TEST\*20;   | Attribut\_2 varchar(20)         |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END B;                    | );                              |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | CLASS C EXTENDS B =       | CREATE TABLE C (                |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | Attribut\_3 : TEST\*20;   | T\_Id integer PRIMARY KEY,      |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              | END B;                    | T\_Type varchar(60) NOT NULL,   |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | Attribut\_1 varchar(20),        |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | Attribut\_2 varchar(20),        |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | Attribut\_3 varchar(20)         |                                                                                                                                                                                                                                                                          |
|              |                           |                                 |                                                                                                                                                                                                                                                                          |
|              |                           | );                              |                                                                                                                                                                                                                                                                          |
+--------------+---------------------------+---------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Los atributos EXTENDED no resultant en ningun columna, sólo la
definición de base del atributo devuelve una columna.

Atributos (general)
~~~~~~~~~~~~~~~~~~~

+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| **Numero**   | **Ejemplo Interlis**                            | **Ejemplo SQL**                    | **Comentarios**                                                                      |
+==============+=================================================+====================================+======================================================================================+
| 1            | textLimited : TEXT\*10;                         | textLimited varchar(10) NULL       |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | textUnlimited : TEXT;                           | textUnlimited text NULL            |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | mtextLimited : MTEXT\*10;                       | mtextLimited varchar(10) NULL      |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | mtextUnlimited : MTEXT;                         | mtextUnlimited text NULL           |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 2            | aufzaehlung : (null, eins, zwei,                | aufzaehlung varchar(255) NULL      | Dependiendo de la opción, otros mapeos son posibles. Véase capítulo Enumeraciones.   |
|              |                                                 |                                    |                                                                                      |
|              | drei, mehr (                                    |                                    |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | vier, fuenf, sechs, sieben, acht ,neun, zehn)   |                                    |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | );                                              |                                    |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 3            | horizAlignment : HALIGNMENT;                    | horizAlignment varchar(255) NULL   |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | vertAlignment : VALIGNMENT;                     | vertAlignment varchar(255) NULL    |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 4            | aBoolean : BOOLEAN;                             | aBoolean boolean NULL              |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 5            | numericInt : 0 .. 10;                           | numericInt integer NULL            |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | numericDec : 0.0 .. 10.0;                       | numericDec decimal(4,1) NULL       |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 6            | aTime : INTERLIS.XMLTime;                       | aTime time NULL                    |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | aDate : INTERLIS.XMLDate;                       | aDate date NULL                    |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | aDateTime : INTERLIS.XMLDateTime;               | aDateTime timestamp NULL           |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 7            | aOid : OID TEXT\*30;                            | aOid varchar(255) NULL             |                                                                                      |
|              |                                                 |                                    |                                                                                      |
|              | aUuid : INTERLIS.UUIDOID;                       | aUuid uuid NULL                    |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+
| 8            | aClass : CLASS;                                 | aClass varchar(255) NULL           |                                                                                      |
+--------------+-------------------------------------------------+------------------------------------+--------------------------------------------------------------------------------------+

Relaciones/Atributos de referencia
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TO DO (por hacer)

Atributo de geometrias (general)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

TO DO (por hacer)

SURFACE/AREA/ITF/XTF
~~~~~~~~~~~~~~~~~~~~

TO DO (por hacer)

Atributos de estructura
~~~~~~~~~~~~~~~~~~~~~~~

Las estructuras se mapean generalmente como clases (véase el capítulo
sobre el mapeo de las clases). Los atributos de estructura (es decir,
cuando se utiliza una estructura como un tipo de atributo, como BAG OF o
LIST OF) se asignan a la tabla de la estructura, independientemente de
la cardinalidad, mediante una clave foránea. Para algunas estructuras,
un mapeo alternativo se utiliza en el caso de Smart-Mapping.

+--------------+------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **Numero**   | **Ejemplo Interlis**   | **Ejemplo SQL**                      | **Comentarios**                                                                                                                                                                                                                    |
+==============+========================+======================================+====================================================================================================================================================================================================================================+
| 1            | STRUCTURE C =          | CREATE TABLE C (                     | Para cada atributo de estructura, se crea una columna para la clave foránea en la tabla de estructura. El nombre de la columna es el nombre de atributo calificado INTERLIS [3]_.                                                  |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              | END C;                 | T\_Id integer PRIMARY KEY,           | La tabla de estructura contiene adicionalmente una columna T\_seq que especifica el orden de los elementos estructurales.                                                                                                          |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              | CLASS D =              | T\_seq integer NOT NULL,             |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              | attr1 : LIST OF C;     | D\_attr1 integer,                    |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              | attr2 : LIST OF C;     | D\_attr2 integer                     |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              | END D;                 | );                                   |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              |                        | CREATE TABLE D (                     |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              |                        | T\_Id integer PRIMARY KEY            |                                                                                                                                                                                                                                    |
|              |                        |                                      |                                                                                                                                                                                                                                    |
|              |                        | );                                   |                                                                                                                                                                                                                                    |
+--------------+------------------------+--------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Ejemplo XML::

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

Ejemplo de la alternativa de mapeo 1:

+---------------+----------+------------+------------+
|    Tabla C    |          |            |            |
+===============+==========+============+============+
| t\_id         | t\_seq   | D\_attr1   | D\_attr2   |
+---------------+----------+------------+------------+
| 7             | 0        | 6          |            |
+---------------+----------+------------+------------+
| 8             | 1        | 6          |            |
+---------------+----------+------------+------------+
| 9             | 0        |            | 6          |
+---------------+----------+------------+------------+

+---------------+---------------+
|   Tabla D     |               |
+===============+===============+
| t\_id         | T\_Ili\_Tid   |
+---------------+---------------+
| 6             | 2             |
+---------------+---------------+


En las siguientes estructuras, Smart-Mapping utiliza un mapeo
alternativo para los atributos de estructura:

-  Estructura con el metaatributo Interlis ili2db.mapping=MultiSurface

-  Estructura con el metaatributo Interlis ili2db.mapping=MultiLine

-  GeometryCHLV03\_V1.MultiSurface

-  GeometryCHLV03\_V1.MultiLine

-  GeometryCHLV03\_V1.MultiDirectedLine

-  GeometryCHLV95\_V1.MultiSurface

-  GeometryCHLV95\_V1.MultiLine

-  GeometryCHLV95\_V1.MultiDirectedLine

-  CatalogueObjects\_V1.Catalogues.CatalogueReference

-  CatalogueObjects\_V1.Catalogues.MandatoryCatalogueReference

-  LocalisationCH\_V1.MultilingualMText

-  LocalisationCH\_V1.MultilingualText

Enumeraciones
~~~~~~~~~~~~~

Hay dos variantes y diferentes opciones para mapear enumeraciones.

-  Variante 1 donde el valor de la enumeración se almacena como código
   XTF

-  Variante 2 donde el valor de la enumeración se almacena como código
   ITF

-  Opcionalmente, se puede crear una columna adicional que puede
   contener el texto mostrado

-  Opcionalmente, se pueden crear tablas adicionales que contengan todos
   los valores enumerados.

+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|   Número     |   Ejemplo Interlis            |   Ejemplo SQL                       |   Comentarios                                                                                                                                                                                                                                                                                                                                |
+==============+===============================+=====================================+==============================================================================================================================================================================================================================================================================================================================================+
| 1            | farbe : (rot, blau, gruen);   | farbe varchar(255) NULL             | Mapeo por defecto. El código XTF (el código tal como está en el archivo de transferencia XTF) se utiliza como valor de enumeración en la base de datos. En el ejemplo: rojo, azul o verde                                                                                                                                                    |
+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 2            | farbe : (rot, blau, gruen);   | farbe integer NULL                  | Mapeo con la opción --createEnumColAsItfCode. El código ITF (el código tal como está en el archivo de transferencia ITF) se utiliza como un valor de enumeración en la base de datos. En el ejemplo, esto significa: 0, 1 ó 2. Esta opción sólo está permitida si no hay extensiones de enumeraciones en el modelo.                          |
+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 3            | farbe : (rot, blau, gruen);   | farbe varchar(255) NULL,            | Mapeo con la opción --createEnumTxtCol. Se crea una columna adicional con el nombre de atributo + \`\` \_txt\`\` (en el ejemplo art\_txt). La columna adicional puede contener cualquier valor que se desee como texto mostrado. Al importar, la columna se rellena con el código XTF. La opción se puede utilizar para la variante 1 o 2.   |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | farbe\_txt varchar(255) NULL        |                                                                                                                                                                                                                                                                                                                                              |
+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 4            | DOMAIN                        | CREATE TABLE Farbe (                | Mapeo con la opción --createEnumTabs. Se crea una tabla con las enumeraciones individuales para cada definición de enumeración.                                                                                                                                                                                                              |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | Farbe : (rot, blau,           | itfCode integer PRIMARY KEY,        | itfCode es el código ITF del valor de enumeración.                                                                                                                                                                                                                                                                                           |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | !!@ili2db.dispName=grün       | iliCode varchar(1024) NOT NULL,     | iliCode es el nombre calificado del elemento (=código XTF) del valor de enumeración.                                                                                                                                                                                                                                                         |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | gruen                         | seq integer NULL,                   | seq define la secuencia de los elementos de enumeración.                                                                                                                                                                                                                                                                                     |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | );                            | dispName varchar(250) NOT NULL,     | dispName define el texto mostrado para el enumerador. Al importar, la columna se rellena con el código XTF. Si el elemento de enumeración tiene el metaatributo @ili2db.dispName, se utiliza su valor.                                                                                                                                       |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | description varchar(1024) NULL,     | description contiene la descripción del enumerador. Al importar, la columna se llena con el comentario ilidoc del modelo.                                                                                                                                                                                                                    |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | inactive boolean NOT NULL           | inactivo: TRUE para ocultar un valor de enumeración durante captura, sin tener que eliminarlo. Si se importa con FALSE se llena la columna.                                                                                                                                                                                                  |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | );                                  |                                                                                                                                                                                                                                                                                                                                              |
+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 5            | DOMAIN                        | CREATE TABLE T\_ILI2DB\_ENUM (      | Mapeo con la opción --createSingleEnumTab. Se crea una sola tabla para los valores de enumeraciones de todas las enumeraciones.                                                                                                                                                                                                              |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | Farbe : (rot, blau,           | thisClass varchar(1024) NOT NULL,   | thisClass es el nombre calificado de la definición de enumeración.                                                                                                                                                                                                                                                                           |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | !!@ili2db.dispName=grün       | baseClass varchar(1024) NOT NULL,   | baseClass es el nombre calificado de la definición de enumeración base.                                                                                                                                                                                                                                                                      |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | gruen                         | itfCode integer NOT NULL,           | itfCode es el código ITF del valor de enumeración.                                                                                                                                                                                                                                                                                           |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              | );                            | iliCode varchar(1024) NOT NULL,     | iliCode es el nombre calificado del elemento (=código XTF) del valor de enumeración.                                                                                                                                                                                                                                                         |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | seq integer NULL,                   | seq define la secuencia de los elementos de enumeración.                                                                                                                                                                                                                                                                                     |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | dispName varchar(250) NOT NULL,     | dispName define el texto mostrado para el enumerador. Al importar, la columna se rellena con el código XTF. Si el elemento de enumeración tiene el metaatributo @ili2db.dispName, se utiliza su valor.                                                                                                                                       |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | description varchar(1024) NULL,     | description contiene la descripción del enumerador. Al importar, la columna se llena con el comentario ilidoc del modelo.                                                                                                                                                                                                                    |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | inactive boolean NOT NULL           | inactivo: TRUE para ocultar un valor de enumeración durante la captura, sin tener que eliminarlo. Si se importa con FALSE se llena la columna.                                                                                                                                                                                               |
|              |                               |                                     |                                                                                                                                                                                                                                                                                                                                              |
|              |                               | );                                  |                                                                                                                                                                                                                                                                                                                                              |
+--------------+-------------------------------+-------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

Metadatos
~~~~~~~~~

+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
|   Tabla                     |   Descripción                                                                                                                                                                                                                                             |
+=============================+===========================================================================================================================================================================================================================================================+
| t\_ili2db\_attrname         | Mapeo de los nombres de atributos                                                                                                                                                                                                                         |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_basket           | Baskets en la base de datos. Se requiere si se utiliza la opción --createBasketCol.                                                                                                                                                                       |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_classname        | Mapeo de los nombres cualificados de las clases Interlis en nombres SQL. No hay una tabla de base de datos de cada registro; dependiendo de la forma de mapeo de la clase Interlis, el nombre SQL sólo se utiliza como contenido de la columna t\_type.   |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_dataset          | Registros/conjuntos de datos en la base de datos (colección de baskets). Se requiere si se utiliza la opción --createBasketCol.                                                                                                                           |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import           | No utilizar, se elimina el uso. No es necesario para la exportar.                                                                                                                                                                                         |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import\_basket   | No utilizar, se elimina el uso. No es necesario para la exportar.                                                                                                                                                                                         |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_import\_object   | No utilizar, se elimina el uso. No es necesario para la exportar.                                                                                                                                                                                         |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_inheritance      | Mapeo de la jerarquía de herencia de las clases Interlis (en las tablas están los nombres calificados de clase Interlis). No es necesario para exportar.                                                                                                  |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_enum             | Valores enumerados si se utiliza la opción --createSingleEnumTab. No es necesario para la exportar.                                                                                                                                                       |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_model            | Modelos que se requerían durante el import (para que el export se pueda hacer con los mismos modelos).                                                                                                                                                    |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_settings         | Configuración del programa para ili2db                                                                                                                                                                                                                    |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_trafo            | Configuración del mapeo semántico (especialmente herencia).                                                                                                                                                                                               |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_table\_prop      | Información adicional acerca de las tablas de DB desde el modelo Interlis (por ejemplo, si es una tabla con valores enumerados). Sólo se crea usando la opción --createMetaInfo.                                                                          |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_ili2db\_column\_prop     | Información adicional acerca las columnas de DB desde el modelo Interlis (por ejemplo, si se trata de MTEXT). Sólo se crea usando la opción --createMetaInfo.                                                                                             |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| t\_key\_object              | Tabla de ayuda para el ID-Generator. No es necesario para exportar.                                                                                                                                                                                       |
+-----------------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

TO DO (por hacer)

Convenciones de Nomenclatura
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

El mapeo de los nombres de clase en nombres de tablas sigue las tres
siguientes posibles estrategias:

Nombre No-calificado
    Se utiliza el nombre de clase (sin prefijo de topic y/o nombre de
    modelo). Si ya existe el nombre, se utilizará el nombre completo.

Nombre Calificado con Topic
    Al nombre de clase no cualificado se agrega el nombre de topic como
    prefijo. Si ya se existe el nombre, se utilizará el nombre completo.

Nombre Calificado (totalmente)
    El nombre de la tabla se compone del nombre de modelo, topic y
    clase.

Si el nombre de la tabla es demasiado largo, se acorta mediante la
eliminación de las vocales (excepto las dos primeras y las dos últimas
letras). Si todavía es demasiado largo después de eso, se quitarán
letras en el medio del nombre.

Si ahora el nombre de la tabla corresponde a una palabra clave SQL, se
complementa con un 'a' al inicio.

Si el nombre de la tabla no es único, se complementa con un número: '0', '1', etc. hasta que sea único.

La asignación automática de nombres se puede anular haciendo las
modificaciones correspondientes en la tabla t\_ili2db\_classname antes
del primer import.

.. [1]
   GML 3.2; las respectivas reglas de codificación corresponden a eCH-0118-1.0

.. [2]
   El nombre SQL se deriva de las convenciones de nomenclatura. La traducción específica se almacena en la tabla T\_ILI2DB\_CLASSNAME.

.. [3]
   El nombre SQL se deriva de las convenciones de nomenclatura. La traducción específica se almacena en la tabla T\_ILI2DB\_ATTRNAME.

.. [4]
   El nombre SQL se deriva de las convenciones de nomenclatura. La traducción específica se almacena en la tabla T\_ILI2DB\_CLASSNAME.

