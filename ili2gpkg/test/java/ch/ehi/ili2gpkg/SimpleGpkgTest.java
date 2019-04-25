package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleGpkgTest extends SimpleTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"Simple.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
	
	
}