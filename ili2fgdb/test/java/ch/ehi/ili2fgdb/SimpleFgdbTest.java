package ch.ehi.ili2fgdb;

import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.ili2db.AbstractTestSetup;

public class SimpleFgdbTest extends ch.ehi.ili2db.SimpleTest {
	
    private static final String FGDBFILENAME=TEST_OUT+"Simple.gdb";
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new FgdbTestSetup(FGDBFILENAME);
    }
	
    @Test
    @Override
    @Ignore("fgdb jdbc driver doesn't support DDL stmts")
    public void createScriptFromIliCoord() throws Exception
    {
        
    }
    @Test
    @Override
    @Ignore("requires support of DELETE FROM see #153")
    public void importXtfStructWithDelete() throws Exception
    {
        super.importXtfStructWithDelete();
    }

	
}