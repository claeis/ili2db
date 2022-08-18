package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class EnumAll23GpkgTest extends ch.ehi.ili2db.EnumAll23Test {
    private static final String GPKGFILENAME=TEST_OUT + "EnumAll23.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
}
