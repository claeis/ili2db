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
package ch.ehi.ili2ora;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class OraMain extends ch.ehi.ili2db.AbstractMain {
	private final String DB_PORT="1521";
	private final String DB_HOST="localhost";
	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2ora.converter.OracleGeometryConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorOracle.class.getName());
		config.setJdbcDriver("oracle.jdbc.OracleDriver");
		config.setDbport(DB_PORT);
	}
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				 *  jdbc:oracle:thin:@myhost:1521:orcl
				    */
				String dbport=config.getDbport();
				if(dbport==null){
					dbport=DB_PORT;
				}
				String dbhost=config.getDbhost();
				if(dbhost==null){
					dbhost=DB_HOST;
				}
				if(config.getDbdatabase()!=null ){
					String url = "jdbc:oracle:thin:@" + dbhost + ":" + dbport + ":" + config.getDbdatabase();
					return url;
				}
				return null;
			}
		};
	}

	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new OraDbPanelDescriptor();
	}

	static public void main(String args[]){
		new OraMain().domain(args);
	}

	public String getAPP_NAME() {
		return "ili2ora";
	}

	public String getDB_PRODUCT_NAME() {
		return "oracle";
	}

	public String getJAR_NAME() {
		return "ili2ora.jar";
	}
	/*
	    * jdbc:postgresql:database
	    * jdbc:postgresql://host/database
	    * jdbc:postgresql://host:port/database
	    */
	protected void printConnectOptions() {
		System.err.println("--dbhost  host         The host name of the server. Defaults to "+DB_HOST+".");
		System.err.println("--dbport  port         The port number the server is listening on. Defaults to "+DB_PORT+".");
		System.err.println("--dbdatabase sid       The database name.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
		System.err.println("--geomwkb              Geometry as WKB (to be used if no Oracle Spatial).");
		System.err.println("--geomwkt              Geometry as WKT (to be used if no Oracle Spatial).");
	}
	protected int doArgs(String args[],int argi,Config config)
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
		}else if(arg.equals("--geomwkb")){
			argi++;
			config.setGeometryConverter(ch.ehi.ili2ora.converter.OracleWKBGeometryConverter.class.getName());
			config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorOracleWKB.class.getName());
		}else if(arg.equals("--geomwkt")){
			argi++;
			config.setGeometryConverter(ch.ehi.ili2ora.converter.OracleWKTGeometryConverter.class.getName());
			config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorOracleWKT.class.getName());
		}
		return argi;
	}
	@Override
	protected void printSpecificOptions() {
		
	}
}
