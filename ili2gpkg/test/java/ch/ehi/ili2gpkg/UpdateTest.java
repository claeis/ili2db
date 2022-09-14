package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class UpdateTest extends ch.ehi.ili2db.UpdateTest {
    private static final String GPKGFILENAME = TEST_OUT + "Update.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }
}
