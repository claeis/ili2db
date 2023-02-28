package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiPolyline24Test extends ch.ehi.ili2db.MultiPolyline24Test {
    private static final String DBSCHEMA = "MultiPolyline24";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertMultiPolyline24_classa1_geomattr1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(geomattr1) FROM " + setup.prefixName("classa1") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTICURVE(COMPOUNDCURVE((480000.111 70000.111 4000.111,480000.222 70000.222 4000.222,480000.333 70000.333 4000.333)),COMPOUNDCURVE((480000.444 70000.444 4000.444,480000.555 70000.555 4000.555,480000.666 70000.666 4000.666)))", rs.getObject(1));
        }
    }
}
