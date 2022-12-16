package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

public class MetaConfigPgTest extends ch.ehi.ili2db.MetaConfigTest {
    private final static String DBSCHEMA="metaconfig";
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
}
