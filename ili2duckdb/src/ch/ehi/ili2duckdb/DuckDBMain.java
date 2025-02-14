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
package ch.ehi.ili2duckdb;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;

import java.text.ParseException;


/**
 * @author sz
 */
public class DuckDBMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2duckdb.DuckDBColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.ili2duckdb.GeneratorDuckDB.class.getName());
		config.setJdbcDriver("org.duckdb.DuckDBDriver");
		config.setIdGenerator(ch.ehi.ili2duckdb.DuckDBIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2duckdb.DuckDBMapping.class.getName());
		config.setUuidDefaultValue("uuid()");
	}
	@Override
    public DbUrlConverter getDbUrlConverter() {
        return new DbUrlConverter(){
            public String makeUrl(Config config) {
                /*
                 * jdbc:duckdb:/path/to/mydatabase.duckdb
                 */
                if(config.getDbfile()!=null){
                    return "jdbc:duckdb:"+config.getDbfile();
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
		new DuckDBMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2duckdb";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "DuckDB";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2duckdb.jar";
	}
	@Override
	protected void printConnectOptions() {
        System.err.println("--dbfile path          The path to the database file.");
	}
    @Override
    protected void printSpecificOptions() {
        System.err.println("--dbschema  schema     The name of the schema in the database. Defaults to not set.");
    }
	@Override
	protected int doArgs(String args[],int argi,Config config) throws ParseException
	{
		String arg=args[argi];
		if(arg.equals("--dbfile")){
			argi++;
            config.setDbfile(args[argi]);
			argi++;
		} else if(arg.equals("--dbschema")){
            argi++;
            config.setDbschema(args[argi]);
            argi++;
        }
		return argi;
	}
}
