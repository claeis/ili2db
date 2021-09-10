package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiCoord24Test extends ch.ehi.ili2db.MultiCoord24Test {
    private static final String GPKGFILENAME = TEST_OUT + "MultiCoord24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertMultiChoord24_classa1_geomattr1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT geomattr1 FROM " + setup.prefixName("classa1") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        while (rs.next()) {
            assertEquals("MULTICOORD {coord [COORD {C1 2530001.0, C2 1150002.0}, COORD {C1 2740003.0, C2 1260004.0}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        }
    }
}
