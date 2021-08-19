package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class BatchTest extends ch.ehi.ili2db.BatchTest {

    private static final String GPKGFILENAME = TEST_OUT + "BatchSize.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }
}
