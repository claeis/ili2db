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
package ch.ehi.ili2fgdb;

import java.nio.ByteBuffer;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class FgdbMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2fgdb.FgdbColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb.class.getName());
		config.setJdbcDriver(ch.ehi.ili2fgdb.jdbc.FgdbDriver.class.getName());
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2fgdb.FgdbMapping.class.getName());
		config.setInitStrategy(ch.ehi.ili2fgdb.InitFgdbApi.class.getName());
		config.setOneGeomPerTable(true);
	}

	@Override
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				    * jdbc:fgdb4j:sample.gdb
				    */
				if(config.getDbfile()!=null){
					return FgdbDriver.BASE_URL+config.getDbfile();
				}
				return null;
			}
		};
	}

	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new FgdbDbPanelDescriptor();
	}

	static public void main(String args[]){
		new FgdbMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2fgdb";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "FileGDB";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2fgdb.jar";
	}
	@Override
	protected void printConnectOptions() {
		System.err.println("--dbfile fgdbfolder       The foldername of the database.");
	}
	@Override
	protected void printSpecificOptions() {
	}
	@Override
	protected int doArgs(String args[],int argi,Config config)
	{
		String arg=args[argi];
		if(arg.equals("--dbfile")){
			argi++;
			config.setDbfile(args[argi]);
			argi++;
		}
		return argi;
	}
}
