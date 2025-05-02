package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class Array23GpkgTest extends ch.ehi.ili2db.Array23Test{
    
    private static final String GPKGFILENAME=TEST_DATA_DIR+"Array23.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    
}