package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiSurfaceArea24Test extends ch.ehi.ili2db.MultiSurfaceArea24Test {
    private static final String DBSCHEMA = "MultiSurfaceArea24";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertMultiSurfaceArea24_classa12_geomattr(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(geomattr1) FROM " + setup.prefixName("classa1") + ";");

        while (rs.next()) {
            assertEquals("SRID=2056;MULTISURFACE(CURVEPOLYGON(COMPOUNDCURVE((480000.111 70000.111 5000.111,480000.999 70000.111 5000.111,480000.999 70000.999 5000.111,480000.111 70000.111 5000.111)),COMPOUNDCURVE((480000.555 70000.222 5000.111,480000.666 70000.222 5000.111,480000.666 70000.666 5000.111,480000.555 70000.222 5000.111))),CURVEPOLYGON(COMPOUNDCURVE((490000.111 70000.111 5000.111,490000.999 70000.111 5000.111,490000.999 70000.999 5000.111,490000.111 70000.111 5000.111)),COMPOUNDCURVE((490000.555 70000.222 5000.111,490000.666 70000.222 5000.111,490000.666 70000.666 5000.111,490000.555 70000.222 5000.111))))", rs.getObject(1));
        }

        rs = stmt.executeQuery("SELECT st_asewkt(geomattr1) FROM " + setup.prefixName("classa2") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTISURFACE(CURVEPOLYGON(COMPOUNDCURVE((480000.111 70000.111 5000.111,480000.999 70000.111 5000.111,480000.999 70000.999 5000.111,480000.111 70000.111 5000.111)),COMPOUNDCURVE((480000.555 70000.222 5000.111,480000.666 70000.222 5000.111,480000.666 70000.666 5000.111,480000.555 70000.222 5000.111))),CURVEPOLYGON(COMPOUNDCURVE((490000.111 70000.111 5000.111,490000.999 70000.111 5000.111,490000.999 70000.999 5000.111,490000.111 70000.111 5000.111)),COMPOUNDCURVE((490000.555 70000.222 5000.111,490000.666 70000.222 5000.111,490000.666 70000.666 5000.111,490000.555 70000.222 5000.111))))", rs.getObject(1));
        }
    }
}
