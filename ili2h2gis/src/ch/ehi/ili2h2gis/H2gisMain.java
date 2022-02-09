/* This file is part of the ili2ora project.
 * For more information, please see <http://www.interlis.ch>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.ehi.ili2h2gis;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;

import java.text.ParseException;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class H2gisMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2h2gis.H2gisColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.ili2h2gis.GeneratorH2gis.class.getName());
		config.setJdbcDriver("org.h2.Driver");
		config.setIdGenerator(ch.ehi.ili2h2gis.H2gisIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2h2gis.H2gisMapping.class.getName());
		config.setStrokeArcs(Config.STROKE_ARCS_ENABLE);
	}
	@Override
    public DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				 * jdbc:h2:[file:][<path>]<databaseName>
				 * jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName>
				 * jdbc:h2:ssl://<server>[:<port>]/[<path>]<databaseName>
				 */
                if(config.getDbhost()==null && config.getDbfile()!=null){
                    // file based
                    // jdbc:h2:[file:][<path>]<databaseName>
                    return "jdbc:h2:file:"+new java.io.File(config.getDbfile()).getAbsolutePath();
                }else if(config.getDbdatabase()!=null){
                    String host=config.getDbhost()!=null?config.getDbhost():"localhost";
                    String port=config.getDbport()!=null?":"+config.getDbport():"";
                    return "jdbc:h2:tcp://"+host+port+"/"+config.getDbdatabase();
                }
				return null;
			}
		};
	}

	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return null;
	}

	static public void main(String args[]){
		new H2gisMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2h2gis";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "h2gis";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2h2gis.jar";
	}
	@Override
	protected void printConnectOptions() {
        System.err.println("--dbfile path          The path to the database files. (file based db)");
		System.err.println("--dbhost  host         The host name of the server. Defaults to localhost. (server based db; optional)");
		System.err.println("--dbport  port         The port number the server is listening on. Defaults to 9092. (server based db; optional)");
		System.err.println("--dbdatabase database  The database name. (server based db)");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	@Override
	protected void printSpecificOptions() {
        System.err.println("--dbschema  schema     The name of the schema in the database. Defaults to not set.");
		System.err.println("--oneGeomPerTable      If more than one geometry per table, create secondary table.");
	}
	@Override
	protected int doArgs(String args[],int argi,Config config) throws ParseException
	{
		String arg=args[argi];
		if(arg.equals("--dbhost")){
			argi++;
			config.setDbhost(args[argi]);
			argi++;
		}else if(arg.equals("--dbport")){
			argi++;
			config.setDbport(args[argi]);
			argi++;
		}else if(arg.equals("--dbdatabase")){
			argi++;
			config.setDbdatabase(args[argi]);
			argi++;
        }else if(arg.equals("--dbfile")){
            argi++;
            config.setDbfile(args[argi]);
            argi++;
		}else if(arg.equals("--dbusr")){
			argi++;
			config.setDbusr(args[argi]);
			argi++;
		}else if(arg.equals("--dbpwd")){
			argi++;
			config.setDbpwd(args[argi]);
			argi++;
        }else if(arg.equals("--dbschema")){
            argi++;
            config.setDbschema(args[argi]);
            argi++;
		}else if(isOption(arg, "--oneGeomPerTable")){
			config.setOneGeomPerTable(parseBooleanArgument(arg));
			argi++;
		}
		return argi;
	}
}
