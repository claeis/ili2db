package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class Oid23GpkgTest extends ch.ehi.ili2db.Oid23Test {
    private static final String GPKGFILENAME=TEST_DATA_DIR+"Oid23.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    
}