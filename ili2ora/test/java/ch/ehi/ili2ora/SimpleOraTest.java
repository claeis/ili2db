package ch.ehi.ili2ora;

import org.junit.Ignore;
import org.junit.Test;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleOraTest extends SimpleTest {
    private static final String DBSCHEMA="simple";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        String dbschemaprefix=System.getProperty("dbschemaprefix");
        String dbtablespace=System.getProperty("dbtablespace");
        
        return new OraTestSetup(dburl, dbuser, dbpwd, DBSCHEMA, dbschemaprefix, dbtablespace);
    }
    
    @Override
    @Test
    @Ignore("Fails because date format and string literal is too long (fields 'import_date' and 'content' of T_ILI2DB_MODEL)")
    public void createScriptFromIliCoord() throws Exception {
        super.createScriptFromIliCoord();
    }
}
