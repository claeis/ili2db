package ch.ehi.ili2mssql;

import ch.ehi.ili2db.AbstractTestSetup;

public class Enum23Test extends ch.ehi.ili2db.Enum23Test {
    private static final String DBSCHEMA = "Enum23";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new MsSqlTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

}
