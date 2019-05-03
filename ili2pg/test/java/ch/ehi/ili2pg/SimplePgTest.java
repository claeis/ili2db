package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

public class SimplePgTest extends ch.ehi.ili2db.SimpleTest {
    private final static String DBSCHEMA="simple";
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 

}
