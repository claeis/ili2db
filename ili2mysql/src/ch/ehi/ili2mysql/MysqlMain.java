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
package ch.ehi.ili2mysql;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;

import java.text.ParseException;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class MysqlMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2mysql.MysqlColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.ili2mysql.GeneratorMysql.class.getName());
		config.setJdbcDriver("com.mysql.jdbc.Driver");
		config.setIdGenerator(ch.ehi.ili2mysql.MySqlIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2mysql.MysqlCustomStrategy.class.getName());
		config.setUuidDefaultValue("uuid()");
        config.setValue(Config.MODELS_TAB_MODELNAME_COLSIZE, "400");
        config.setValue(Config.ATTRNAME_TAB_SQLNAME_COLSIZE,"95");
        config.setValue(Config.ATTRNAME_TAB_OWNER_COLSIZE,"95");
        config.setValue(Config.CLASSNAME_TAB_ILINAME_COLSIZE,"766");
        config.setValue(Config.INHERIT_TAB_THIS_COLSIZE,"766");
	}
	@Override
    public DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				    * jdbc:mysql://[host[:port]][/database][?properties]
				    */
				if(config.getDbdatabase()!=null){
					if(config.getDbhost()!=null){
						if(config.getDbport()!=null){
							return "jdbc:mysql://"+config.getDbhost()+":"+config.getDbport()+"/"+config.getDbdatabase();
						}
						return "jdbc:mysql://"+config.getDbhost()+"/"+config.getDbdatabase();
					}
					return "jdbc:mysql:///"+config.getDbdatabase();
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
		new MysqlMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2mysql";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "mysql";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2mysql.jar";
	}
	@Override
	protected void printConnectOptions() {
		System.err.println("--dbhost  host         The host name of the server. Defaults to localhost.");
		System.err.println("--dbport  port         The port number the server is listening on. Defaults to 3306.");
		System.err.println("--dbdatabase database  The database name.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	@Override
	protected void printSpecificOptions() {
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
		}else if(arg.equals("--dbusr")){
			argi++;
			config.setDbusr(args[argi]);
			argi++;
		}else if(arg.equals("--dbpwd")){
			argi++;
			config.setDbpwd(args[argi]);
			argi++;
		}else if(isOption(arg, "--oneGeomPerTable")){
			config.setOneGeomPerTable(parseBooleanArgument(arg));
			argi++;
		}
		return argi;
	}
}
