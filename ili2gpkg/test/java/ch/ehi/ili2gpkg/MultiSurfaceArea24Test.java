package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiSurfaceArea24Test extends ch.ehi.ili2db.MultiSurfaceArea24Test {
    private static final String GPKGFILENAME = TEST_OUT + "MultiSurfaceArea24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertMultiSurfaceArea24_classa12_geomattr(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT geomattr1 FROM " + setup.prefixName("classa1") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        while (rs.next()) {
            assertEquals("MULTISURFACE {surface [SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.111, C2 70000.111, C3 5000.111}, COORD {C1 480000.999, C2 70000.111, C3 5000.111}, COORD {C1 480000.999, C2 70000.999, C3 5000.111}, COORD {C1 480000.111, C2 70000.111, C3 5000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.555, C2 70000.222, C3 5000.111}, COORD {C1 480000.666, C2 70000.222, C3 5000.111}, COORD {C1 480000.666, C2 70000.666, C3 5000.111}, COORD {C1 480000.555, C2 70000.222, C3 5000.111}]}}}]}, SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 490000.111, C2 70000.111, C3 5000.111}, COORD {C1 490000.999, C2 70000.111, C3 5000.111}, COORD {C1 490000.999, C2 70000.999, C3 5000.111}, COORD {C1 490000.111, C2 70000.111, C3 5000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 490000.555, C2 70000.222, C3 5000.111}, COORD {C1 490000.666, C2 70000.222, C3 5000.111}, COORD {C1 490000.666, C2 70000.666, C3 5000.111}, COORD {C1 490000.555, C2 70000.222, C3 5000.111}]}}}]}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        }

        rs = stmt.executeQuery("SELECT geomattr1 FROM " + setup.prefixName("classa2") + ";");
        while (rs.next()) {
            assertEquals("MULTISURFACE {surface [SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.111, C2 70000.111, C3 5000.111}, COORD {C1 480000.999, C2 70000.111, C3 5000.111}, COORD {C1 480000.999, C2 70000.999, C3 5000.111}, COORD {C1 480000.111, C2 70000.111, C3 5000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.555, C2 70000.222, C3 5000.111}, COORD {C1 480000.666, C2 70000.222, C3 5000.111}, COORD {C1 480000.666, C2 70000.666, C3 5000.111}, COORD {C1 480000.555, C2 70000.222, C3 5000.111}]}}}]}, SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 490000.111, C2 70000.111, C3 5000.111}, COORD {C1 490000.999, C2 70000.111, C3 5000.111}, COORD {C1 490000.999, C2 70000.999, C3 5000.111}, COORD {C1 490000.111, C2 70000.111, C3 5000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 490000.555, C2 70000.222, C3 5000.111}, COORD {C1 490000.666, C2 70000.222, C3 5000.111}, COORD {C1 490000.666, C2 70000.666, C3 5000.111}, COORD {C1 490000.555, C2 70000.222, C3 5000.111}]}}}]}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        }

    }
}
