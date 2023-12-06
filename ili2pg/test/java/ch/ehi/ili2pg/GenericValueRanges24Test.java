package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class GenericValueRanges24Test extends ch.ehi.ili2db.GenericValueRanges24Test {
    private static final String DBSCHEMA = "GenericValueRanges24";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertAttrCoord(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrcoord) FROM " + setup.prefixName("classa") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;POINT(2530001 1150002)", rs.getObject(1));
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiCoord(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrmulticoord) FROM " + setup.prefixName("classa_attrmulticoord") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;MULTIPOINT(2530001 1150002,2740003 1260004)", rs.getObject(1));
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrLine(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrline) FROM " + setup.prefixName("classa_attrline") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;COMPOUNDCURVE((2480000 1070000,2490000 1080000))", rs.getObject(1));
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiLine(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrmultiline) FROM " + setup.prefixName("classa_attrmultiline") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;MULTICURVE(COMPOUNDCURVE((2480000 1070000,2490000 1080000)),COMPOUNDCURVE((2480000 1070000,2490000 1080000)))", rs.getObject(1));
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrSurface(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrsurface) FROM " + setup.prefixName("classa_attrsurface") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;CURVEPOLYGON(COMPOUNDCURVE((2480000.111 1070000.111,2480000.999 1070000.111,2480000.999 1070000.999,2480000.111 1070000.111)),COMPOUNDCURVE((2480000.555 1070000.222,2480000.666 1070000.222,2480000.666 1070000.666,2480000.555 1070000.222)))", rs.getObject(1));
        assertFalse(rs.next());
    }

    @Override
    protected void assertAttrMultiSurface(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attrmultisurface) FROM " + setup.prefixName("classa_attrmultisurface") + ";");
        assertTrue(rs.next());
        assertEquals("SRID=2056;MULTISURFACE(CURVEPOLYGON(COMPOUNDCURVE((2480000.111 1070000.111,2480000.999 1070000.111,2480000.999 1070000.999,2480000.111 1070000.111)),COMPOUNDCURVE((2480000.555 1070000.222,2480000.666 1070000.222,2480000.666 1070000.666,2480000.555 1070000.222))))", rs.getObject(1));
        assertFalse(rs.next());
    }
}
