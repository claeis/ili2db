package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class Enum23Test extends ch.ehi.ili2db.Enum23Test {
    private static final String GPKGFILENAME=TEST_OUT + "Enum23.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
}
