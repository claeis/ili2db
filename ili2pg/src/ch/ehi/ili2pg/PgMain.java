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
package ch.ehi.ili2pg;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;

import java.text.ParseException;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class PgMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2pg.converter.PostgisColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorPostgresql.class.getName());
		config.setJdbcDriver("org.postgresql.Driver");
		config.setIdGenerator(ch.ehi.ili2pg.PgSequenceBasedIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2pg.PgCustomStrategy.class.getName());
		config.setUuidDefaultValue("uuid_generate_v4()");
	}
	@Override
	public DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				    * jdbc:postgresql:database
				    * jdbc:postgresql://host/database
				    * jdbc:postgresql://host:port/database
				    */
				if(config.getDbdatabase()!=null){
					if(config.getDbhost()!=null){
						if(config.getDbport()!=null){
							return "jdbc:postgresql://"+config.getDbhost()+":"+config.getDbport()+"/"+config.getDbdatabase();
						}
						return "jdbc:postgresql://"+config.getDbhost()+"/"+config.getDbdatabase();
					}else {
					    if(config.getDbport()!=null) {
                            return "jdbc:postgresql://localhost:"+config.getDbport()+"/"+config.getDbdatabase();
					    }
					}
					return "jdbc:postgresql:"+config.getDbdatabase();
				}
				return null;
			}
		};
	}

	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new PgDbPanelDescriptor();
	}

	static public void main(String args[]){
		new PgMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2pg";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "postgis";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2pg.jar";
	}
	/*
	    * jdbc:postgresql:database
	    * jdbc:postgresql://host/database
	    * jdbc:postgresql://host:port/database
	    */
	@Override
	protected void printConnectOptions() {
		System.err.println("--dbhost  host         The host name of the server. Defaults to localhost.");
		System.err.println("--dbport  port         The port number the server is listening on. Defaults to 5432.");
		System.err.println("--dbdatabase database  The database name.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	@Override
	protected void printSpecificOptions() {
		System.err.println("--dbschema  schema     The name of the schema in the database. Defaults to not set.");
		System.err.println("--oneGeomPerTable      If more than one geometry per table, create secondary table.");
		System.err.println("--setupPgExt           create extensions 'uuid-ossp' and 'postgis'.");
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
		}else if(isOption(arg, "--setupPgExt")){
			config.setSetupPgExt(parseBooleanArgument(arg));
			argi++;
		}
		return argi;
	}
}
