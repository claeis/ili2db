package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class GenericValueRanges24Test extends ch.ehi.ili2db.GenericValueRanges24Test {
    private static final String GPKGFILENAME = TEST_OUT + "GenericValueRanges24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertAttrCoord(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrcoord FROM " + setup.prefixName("classa") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("COORD {C1 2530001.0, C2 1150002.0}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiCoord(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrmulticoord FROM " + setup.prefixName("classa_attrmulticoord") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("MULTICOORD {coord [COORD {C1 2530001.0, C2 1150002.0}, COORD {C1 2740003.0, C2 1260004.0}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrLine(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrline FROM " + setup.prefixName("classa_attrline") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiLine(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrmultiline FROM " + setup.prefixName("classa_attrmultiline") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrSurface(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrsurface FROM " + setup.prefixName("classa_attrsurface") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("MULTISURFACE {surface SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.111, C2 1070000.111}, COORD {C1 2480000.999, C2 1070000.111}, COORD {C1 2480000.999, C2 1070000.999}, COORD {C1 2480000.111, C2 1070000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.555, C2 1070000.222}, COORD {C1 2480000.666, C2 1070000.222}, COORD {C1 2480000.666, C2 1070000.666}, COORD {C1 2480000.555, C2 1070000.222}]}}}]}}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiSurface(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT attrmultisurface FROM " + setup.prefixName("classa_attrmultisurface") + ";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        assertTrue(rs.next());
        assertEquals("MULTISURFACE {surface SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.111, C2 1070000.111}, COORD {C1 2480000.999, C2 1070000.111}, COORD {C1 2480000.999, C2 1070000.999}, COORD {C1 2480000.111, C2 1070000.111}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.555, C2 1070000.222}, COORD {C1 2480000.666, C2 1070000.222}, COORD {C1 2480000.666, C2 1070000.666}, COORD {C1 2480000.555, C2 1070000.222}]}}}]}}", gpkg2iox.read(rs.getBytes(1)).toString());
        assertFalse(rs.next());
    }
}
