package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class RounderTest extends ch.ehi.ili2db.RounderTest {
    private final static String DBSCHEMA="rounder";
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    }

    @Override
    protected void assertRounding24_classMultiKoord2(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(lcoord) FROM " + setup.prefixName("classmultikoord2") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTIPOINT(2460001 1045001,2460002.001 1045002.001)", rs.getObject(1));
        }
    }

    @Override
    protected void assertRounding24_multiLine2(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straightsarcs2d) FROM " + setup.prefixName("multiline2") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTICURVE(COMPOUNDCURVE(CIRCULARSTRING(2460001 1045001,2460005 1045004,2460006 1045006),(2460006 1045006,2460010 1045010)),COMPOUNDCURVE((2460020 1045020,2460030.001 1045030.001)))", rs.getObject(1));
        }
    }

    @Override
    protected void assertRounding24_multiSurface2(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surfacearcs2d) FROM " + setup.prefixName("multisurface2") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTISURFACE(CURVEPOLYGON(COMPOUNDCURVE((2460001 1045001,2460020 1045015),CIRCULARSTRING(2460020 1045015,2460010 1045018,2460001 1045015),(2460001 1045015,2460001 1045001))),CURVEPOLYGON(COMPOUNDCURVE((2460101 1045101,2460120.001 1045115.001,2460101 1045115,2460101 1045101))))", rs.getObject(1));
        }
    }
}
