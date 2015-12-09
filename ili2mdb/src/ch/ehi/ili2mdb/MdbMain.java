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
package ch.ehi.ili2mdb;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class MdbMain extends ch.ehi.ili2db.AbstractMain {


	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setJdbcDriver("sun.jdbc.odbc.JdbcOdbcDriver");
		config.setGeometryConverter(ch.ehi.ili2mdb.converter.AccessGeometryConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorMdb.class.getName());
	}
	@Override
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				if(config.getDbfile()!=null){
				  	String url = "jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ="+config.getDbfile();
				  	return url;
				}
				return null;
			}
		};
	}

	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new MdbDbPanelDescriptor();
	}

	static public void main(String args[]){
		new MdbMain().domain(args);
	}

	public String getAPP_NAME() {
		return "ili2mdb";
	}

	public String getDB_PRODUCT_NAME() {
		return "ACCESS";
	}

	public String getJAR_NAME() {
		return "ili2mdb.jar";
	}
	protected void printConnectOptions() {
		System.err.println("--dbfile mdbfile       The filename of the database.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	protected int doArgs(String args[],int argi,Config config)
	{
		String arg=args[argi];
		if(arg.equals("--dbfile")){
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
		}
		return argi;
	}
	@Override
	protected void printSpecificOptions() {
	}
}
