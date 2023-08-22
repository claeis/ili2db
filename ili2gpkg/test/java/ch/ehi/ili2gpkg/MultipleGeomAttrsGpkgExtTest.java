package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class MultipleGeomAttrsGpkgExtTest extends ch.ehi.ili2db.MultipleGeomAttrsTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"MultipleGeomAttrsGpkgExtTest.gpkg";
    
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL,true);
    }
}