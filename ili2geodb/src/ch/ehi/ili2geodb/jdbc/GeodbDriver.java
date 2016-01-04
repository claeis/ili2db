package ch.ehi.ili2geodb.jdbc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import ch.ehi.basics.logging.EhiLogger;

import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceName;
import com.esri.arcgis.geodatabase.IWorkspaceProxy;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.IName;

public class GeodbDriver implements Driver {
	private static final String BASE_URL="jdbc:geodb.ehi.ch:mdb:";
	static {
		  try {
			java.sql.DriverManager.registerDriver(new GeodbDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean acceptsURL(String url) throws SQLException {
		if(url.startsWith(BASE_URL)){
			return true;
		}
		return false;
	}

	public Connection connect(String url, Properties props) throws SQLException {
		if(!acceptsURL(url)){
			return null;
		}
		String fileName=url.substring(BASE_URL.length());
		IWorkspace wksp = null;
		try {
			com.esri.arcgis.datasourcesGDB.AccessWorkspaceFactory workspaceFactory = new com.esri.arcgis.datasourcesGDB.AccessWorkspaceFactory();
			java.io.File file=new java.io.File(fileName);
			if(file.exists()){
				com.esri.arcgis.system.PropertySet pset = null;
				if(props!=null && !props.isEmpty()){
					//pset=new com.esri.arcgis.system.PropertySet();
					//pset.setProperty("USER", "gdb");
					//pset.setProperty("PASSWORD", "gdb");
				}
				wksp = new IWorkspaceProxy(workspaceFactory.openFromFile(fileName, 0 ));
			}else{
				//Create a new Access workspace\personal geodatabase
				IWorkspaceName workspaceName = workspaceFactory.create(file.getParent(), 
				    file.getName(), null, 0);

				//Cast for IName
				IName name = (IName)workspaceName;

				//Open a reference to the access workspace through the name object
				wksp = new IWorkspaceProxy(name.open());
				
			}
			
		} catch (UnknownHostException ex) {
			throw new SQLException(ex);
		} catch (AutomationException ex) {
			throw new SQLException(ex);
		} catch (IOException ex) {
			throw new SQLException(ex);
		}
		return new GeodbConnection(wksp,url);
	}

	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
