package ch.ehi.ili2db.base;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.gui.Config;

public abstract class AbstractJdbcMapping implements CustomMapping {
	@Override
	public Connection connect(String url, String dbusr, String dbpwd,
			Config config) throws SQLException {
		return DriverManager.getConnection(url, dbusr, dbpwd);
	}

    @Override
    public void prePreScript(Connection conn, Config config)
    {
        
    }
    @Override
    public void postPostScript(Connection conn, Config config)
    {
        
    }
    @Override
    public String shortenConnectUrl4IliCache(String url) {
        return url;
    }
}
