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
package ch.ehi.ili2db;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2db.gui.Config;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class GenericMain extends AbstractMain {
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new GenericDbPanelDescriptor();
	}

	static public void main(String args[]){
		new GenericMain().domain(args);
	}

	@Override
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				return config.getDburl();
			}
		};
	}
	@Override
	public String getAPP_NAME() {
		return "ili2db";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "generic OGC database";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2db.jar";
	}
	@Override
	protected void printConnectOptions() {
		System.err.println("-dburl  url           URL to access database.");
		System.err.println("-dbusr  username      User name to access database.");
		System.err.println("-dbpwd  password      Password of user used to access database.");
		System.err.println("-adapter classname    qualified name of java class, that implements the DB specifics.");
	}
	@Override
	protected int doArgs(String args[],int argi,Config config)
	{
		String arg=args[argi];
		if(arg.equals("-dburl")){
			argi++;
			config.setDburl(args[argi]);
			argi++;
		}else if(arg.equals("-dbusr")){
			argi++;
			config.setDbusr(args[argi]);
			argi++;
		}else if(arg.equals("-dbpwd")){
			argi++;
			config.setDbpwd(args[argi]);
			argi++;
		}else if(arg.equals("-adapter")){
			argi++;
			config.setGeometryConverter(args[argi]);
			argi++;
		}
		return argi;
	}

	@Override
	protected void printSpecificOptions() {
	}
}
