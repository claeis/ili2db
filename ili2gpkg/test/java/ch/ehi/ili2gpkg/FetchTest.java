package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class FetchTest extends ch.ehi.ili2db.FetchTest {

    private static final String GPKGFILENAME = TEST_OUT + "FetchSize.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

}
