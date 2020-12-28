package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

public class Datatypes23Test extends ch.ehi.ili2db.Datatypes23Test {
    private final static String DBSCHEMA="Datatypes23";
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
}