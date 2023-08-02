package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Assoc23Test;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Assoc23PgTest extends Assoc23Test {
    private static final String DBSCHEMA = "Assoc23";
    String dburl;
    String dbuser;
    String dbpwd; 

    @Override
    protected AbstractTestSetup createTestSetup() {
        dburl=System.getProperty("dburl"); 
        dbuser=System.getProperty("dbusr");
        dbpwd=System.getProperty("dbpwd"); 
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    }
}