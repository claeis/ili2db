package ch.ehi.ili2mssql;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleMsSqlTest extends SimpleTest {
    private final static String DBSCHEMA="simple";
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new MsSqlTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }
}
