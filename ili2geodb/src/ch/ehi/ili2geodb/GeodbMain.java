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
package ch.ehi.ili2geodb;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.converter.NullColumnConverter;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class GeodbMain extends ch.ehi.ili2db.AbstractMain {

	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(NullColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.ili2geodb.sqlgen.GeodbGenerator.class.getName());
		config.setJdbcDriver(ch.ehi.ili2geodb.jdbc.GeodbDriver.class.getName());
		config.setInitStrategy(ch.ehi.ili2geodb.InitAOEngine.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2geodb.GeodbMapping.class.getName());
		config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_NO);
	}
	
	@Override
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				if(config.getDbfile()!=null){
				  	String url = "jdbc:geodb.ehi.ch:mdb:"+config.getDbfile();
				  	return url;
				}
				return null;
			}
		};
	}

	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return new GeodbDbPanelDescriptor();
	}

	static public void main(String args[]){
		bootstrapArcobjectsJar();
		new GeodbMain().domain(args);
	}

	@Override
	public String getAPP_NAME() {
		return "ili2geodb";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "ArcGIS Geodatabase";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2geodb.jar";
	}
	@Override
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
	protected void printConnectOptions() {
		System.err.println("--dbfile mdbfile       The filename of the database.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	@Override
	protected void printSpecificOptions() {
	}
	private static void bootstrapArcobjectsJar()
    {
        String arcEngineHome = System.getenv("ARCGISHOME");
        String jarPath = arcEngineHome + "java" + File.separator + "lib" +
            File.separator + "arcobjects.jar";

        File f = new File(jarPath);

        URLClassLoader sysloader = (URLClassLoader)
            ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try
        {

            Method method = sysclass.getDeclaredMethod("addURL", new Class[]
            {
                URL.class
            }
            );
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]
            {
                f.toURL()
            }
            );

        }
        catch (Throwable t)
        {
            EhiLogger.logError("Could not add arcobjects.jar to system classloader",t);
        }
    }
	
}
