package ch.ehi.ili2db;

import java.sql.Connection;
import java.sql.SQLException;

import ch.ehi.ili2db.gui.Config;

public abstract class AbstractTestSetup {
    /** wrapper around the db specific initConfig() in the db specific main class
     */
    abstract protected void initConfig(Config config);
    /** init config including setting of db connection params
     */
    public abstract Config initConfig(String xtfFilename,String logfile);
    /** clear/remove the dbschema or dbfile
     */
    public abstract void resetDb() throws SQLException;
    /** get an open connection
     */
    public abstract Connection createConnection() throws SQLException;
    public void setXYParams(Config config) {
    }
    public Connection createDbSchema() throws SQLException {
        return createConnection();
    }
    public String prefixName(String name) {
        return name;
    }
    public String getSchema() {
        return null;
    }
    
    public boolean supportsCompoundGeometry(){
        return true;
    }

}
