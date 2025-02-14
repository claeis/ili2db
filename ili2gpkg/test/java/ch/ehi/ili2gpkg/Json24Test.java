package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2gpkg.GpkgTestSetup;

public class Json24Test extends ch.ehi.ili2db.Json24Test {
	
    private static final String GPKGFILENAME=TEST_OUT+"Json24.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
		
}