package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class MultilingualText24GpkgTest extends ch.ehi.ili2db.MultilingualText24Test {
    private static final String GPKG_FILENAME = TEST_OUT + DBSCHEMA + ".gpkg";
    private static final String DB_URL = "jdbc:sqlite:" + GPKG_FILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKG_FILENAME, DB_URL);
    }
}
