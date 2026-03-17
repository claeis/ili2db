package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class InheritanceTest extends ch.ehi.ili2db.InheritanceTest {
    private static final String GPKGFILENAME = TEST_DATA_DIR + "Inheritance.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }
}
