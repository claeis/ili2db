package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2pg.PgTestSetup;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class TranslationPgTest extends ch.ehi.ili2db.TranslationTest {
	private static final String DBSCHEMA = "Translation";
	
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 

}