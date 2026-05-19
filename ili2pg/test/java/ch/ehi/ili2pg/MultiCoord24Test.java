package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class MultiCoord24Test extends ch.ehi.ili2db.MultiCoord24Test {
    private static final String DBSCHEMA = "MultiChoord24";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertMultiChoord24_classa1_geomattr1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(geomattr1) FROM " + setup.prefixName("classa1") + ";");
        while (rs.next()) {
            assertEquals("SRID=2056;MULTIPOINT(2530001 1150002,2740003 1260004)", rs.getObject(1));
        }
    }
}
