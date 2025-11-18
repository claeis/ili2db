package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class DocumentationGpkgTest extends ch.ehi.ili2db.DocumentationTest{
    
    private static final String GPKGFILENAME=TEST_OUT+"Documentation.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    
}