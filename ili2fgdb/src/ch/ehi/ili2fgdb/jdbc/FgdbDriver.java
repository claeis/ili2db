package ch.ehi.ili2fgdb.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.fgdb4j.Fgdb4jException;
import ch.ehi.fgdb4j.jni.Geodatabase;
import ch.ehi.fgdb4j.jni.fgbd4j;

public class FgdbDriver implements Driver {
	public static final String BASE_URL="jdbc:ili2fgdb:";
	static {
		  try {
			java.sql.DriverManager.registerDriver(new FgdbDriver());
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
		try {
			Fgdb4j.initialize();
		} catch (Fgdb4jException e) {
			throw new SQLException("failed to initialize fgdb4j",e);
		}
		Geodatabase wksp = null;
		java.io.File file=new java.io.File(fileName);
		if(file.exists()){
			if(props!=null && !props.isEmpty()){
				//pset=new com.esri.arcgis.system.PropertySet();
				//pset.setProperty("USER", "gdb");
				//pset.setProperty("PASSWORD", "gdb");
			}
			wksp=new Geodatabase();
			int ret=fgbd4j.OpenGeodatabase(file.getPath(), wksp);
			if(ret!=0){
				StringBuffer errorDescription=new StringBuffer();
				fgbd4j.GetErrorDescription(ret, errorDescription);
				throw new SQLException(errorDescription.toString());
			}
		}else{
			wksp=new Geodatabase();
			int ret=fgbd4j.CreateGeodatabase(file.getPath(), wksp);
			if(ret!=0){
				StringBuffer errorDescription=new StringBuffer();
				fgbd4j.GetErrorDescription(ret, errorDescription);
				throw new SQLException(errorDescription.toString());
			}
			
		}
		return new FgdbConnection(wksp,url);
	}

	public int getMajorVersion() {
		return Fgdb4j.getMajorVersion();
	}

	public int getMinorVersion() {
		return Fgdb4j.getMinorVersion();
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
