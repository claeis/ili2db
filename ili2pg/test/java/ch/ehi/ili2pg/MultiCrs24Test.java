package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Ili2dbAssert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class MultiCrs24Test extends ch.ehi.ili2db.MultiCrs24Test {
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertImportedData(Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT st_asewkt(attr2_2056),st_asewkt(attr2_21781),st_asewkt(attr3_2056),st_asewkt(attr3_21781),st_asewkt(attr4_2056),st_asewkt(attr4_21781) FROM "+DBSCHEMA+".classa1 ORDER BY t_id ASC;");

        assertTrue(rs.next());
        assertEquals("SRID=2056;POINT(2460001 1045001)", rs.getObject(1));
        assertNull(rs.getObject(2));
        assertEquals("SRID=2056;COMPOUNDCURVE((2480000 1070000,2490000 1080000))", rs.getObject(3));
        assertNull(rs.getObject(4));
        assertEquals("SRID=2056;MULTICURVE(COMPOUNDCURVE((2480000 1070000,2490000 1080000)),COMPOUNDCURVE((2480000 1070000,2490000 1080000)))", rs.getObject(5));
        assertNull(rs.getObject(6));

        assertTrue(rs.next());
        assertNull(rs.getObject(1));
        assertEquals("SRID=21781;POINT(460002 45002)", rs.getObject(2));
        assertNull(rs.getObject(3));
        assertEquals("SRID=21781;COMPOUNDCURVE((480000 70000,490000 80000))", rs.getObject(4));
        assertNull(rs.getObject(5));
        assertEquals("SRID=21781;MULTICURVE(COMPOUNDCURVE((480000 70000,490000 80000)),COMPOUNDCURVE((480000 70000,490000 80000)))", rs.getObject(6));
    }

    @Override
    protected void assertAttributeNameTable(Connection jdbcConnection) throws SQLException {
        String [][] expectedValues=new String[][] {
                {"MultiCrs24.TestA.ClassA1.attr4:2056", "attr4_2056", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr4:21781", "attr4_21781", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr3:2056", "attr3_2056", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr3:21781", "attr3_21781", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr2:2056", "attr2_2056", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr2:21781", "attr2_21781", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr1", "attr1", "classa1", null}
        };
        Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
    }

    @Override
    protected void assertTrafoTable(Connection jdbcConnection) throws SQLException {
        String[][] expectedValues = new String[][]{
                {"MultiCrs24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
        };
        Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues, DBSCHEMA);
    }
}
