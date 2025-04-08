package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class Array24GpkgTest extends ch.ehi.ili2db.Array24Test{
    
    private static final String GPKGFILENAME=TEST_DATA_DIR+"Array24.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    
}