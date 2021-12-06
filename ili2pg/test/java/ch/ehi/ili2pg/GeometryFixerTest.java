package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class GeometryFixerTest extends ch.ehi.ili2db.GeometryFixerTest {
    private static final String DBSCHEMA = "GeometryFixer";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertStrokeArcsGeometryValid(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT ST_ISVALID(surfacearcs2d) FROM " + setup.prefixName("Surface1") + ";");
        while (rs.next()) {
            assertEquals(true, rs.getObject(1));
        }

        rs = stmt.executeQuery("SELECT ST_ISVALID(surfacestraight2d) FROM " + setup.prefixName("Surface2") + ";");
        while (rs.next()) {
            assertEquals(true, rs.getObject(1));
        }
    }

    @Override
    protected void assertGeometryWithoutStrokeArcs(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT ST_ISVALID(surfacearcs2d) FROM " + setup.prefixName("Surface1") + ";");
        while (rs.next()) {
            assertEquals(false, rs.getObject(1));
        }

        rs = stmt.executeQuery("SELECT ST_ISVALID(surfacestraight2d) FROM " + setup.prefixName("Surface2") + ";");
        while (rs.next()) {
            assertEquals(false, rs.getObject(1));
        }
    }

}
