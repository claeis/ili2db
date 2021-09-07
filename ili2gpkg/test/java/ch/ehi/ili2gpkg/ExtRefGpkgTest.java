package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2gpkg.GpkgTestSetup;

public class ExtRefGpkgTest extends ch.ehi.ili2db.ExtRefTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"ExtRef.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
		
}