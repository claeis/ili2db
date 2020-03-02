package ch.ehi.ili2mysql;

import org.junit.Test;

import ch.ehi.ili2db.AbstractTestSetup;

public class SimpleMysqlTest extends ch.ehi.ili2db.SimpleTest {
	
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new MysqlTestSetup(dburl,dbuser,dbpwd);
    } 

    @Override
    @Test
    public void importXtfCoord() throws Exception
    {
        //ch.ehi.basics.logging.EhiLogger.getInstance().setTraceFilter(false);
        super.importXtfCoord();
    }    
	
}