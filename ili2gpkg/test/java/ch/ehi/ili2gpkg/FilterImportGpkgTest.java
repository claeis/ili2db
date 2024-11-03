package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class FilterImportGpkgTest extends ch.ehi.ili2db.FilterImportTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"FilterImport.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
		
}