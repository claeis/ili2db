package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiPolyline24Test extends ch.ehi.ili2db.MultiPolyline24Test {
    private static final String GPKGFILENAME = TEST_OUT + "MultiPolyline24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertMultiPolyline24_classa1_geomattr1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT geomattr1 FROM " + setup.prefixName("classa1") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        while (rs.next()) {
            assertEquals("MULTIPOLYLINE {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.111, C2 70000.111, C3 5000.111}, COORD {C1 480000.222, C2 70000.222, C3 5000.222}, COORD {C1 480000.333, C2 70000.333, C3 5000.333}]}}}", gpkg2iox.read(rs.getBytes(1)).toString());
        }
    }
}
