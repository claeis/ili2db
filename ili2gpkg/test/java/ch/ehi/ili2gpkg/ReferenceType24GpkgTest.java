package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class ReferenceType24GpkgTest extends ch.ehi.ili2db.ReferenceType24Test{
    
    private static final String GPKGFILENAME=TEST_DATA_DIR+"ReferenceType24.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    
}