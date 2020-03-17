package ch.ehi.ili2mssql;

import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleMsSqlTest extends SimpleTest {
    private static final String DBSCHEMA="simple";
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new MsSqlTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }
    @Override
    @Test
    @Ignore("fails because foreign keys are created after disabling them")
    public void importXtfInheritanceNewClass() throws Exception{
        super.importXtfInheritanceNewClass();
    }
}
