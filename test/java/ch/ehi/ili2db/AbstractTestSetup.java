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
    abstract protected Config initConfig(String xtfFilename,String logfile);
    /** clear/remove the dbschema or dbfile
     */
    abstract protected void resetDb() throws SQLException;
    /** get an open connection
     */
    abstract protected Connection createConnection() throws SQLException;
    protected void setXYParams(Config config) {
    }
    protected Connection createDbSchema() throws SQLException {
        return createConnection();
    }

}
