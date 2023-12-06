package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Ili2dbAssert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class MultiCrs24Test extends ch.ehi.ili2db.MultiCrs24Test {
    private static final String GPKGFILENAME = TEST_OUT + "MultiCrs24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertImportedData(Statement statement) throws Exception {
        String[] queries = {
                "SELECT attr2_2056 FROM classa1 ORDER BY t_id ASC;",
                "SELECT attr2_21781 FROM classa1_attr2_21781 ORDER BY t_id ASC;",
                "SELECT attr3_2056 FROM classa1_attr3_2056 ORDER BY t_id ASC;",
                "SELECT attr3_21781 FROM classa1_attr3_21781 ORDER BY t_id ASC;",
                "SELECT attr4_2056 FROM classa1_attr4_2056 ORDER BY t_id ASC;",
                "SELECT attr4_21781 FROM classa1_attr4_21781 ORDER BY t_id ASC;",
        };

        String[] entry1 = {
                "COORD {C1 2460001.0, C2 1045001.0}",
                null,
                "POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}",
                null,
                "MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 2480000.0, C2 1070000.0}, COORD {C1 2490000.0, C2 1080000.0}]}}]}",
                null
        };
        String[] entry2 = {
                null,
                "COORD {C1 460002.0, C2 45002.0}",
                null,
                "POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.0, C2 70000.0}, COORD {C1 490000.0, C2 80000.0}]}}",
                null,
                "MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.0, C2 70000.0}, COORD {C1 490000.0, C2 80000.0}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 480000.0, C2 70000.0}, COORD {C1 490000.0, C2 80000.0}]}}]}"
        };

        Gpkg2iox gpkg2iox = new Gpkg2iox();

        for (int i = 0; i < queries.length; i++) {
            ResultSet rs = statement.executeQuery(queries[i]);
            assertTrue(rs.next());
            assertEquals(entry1[i], geomToString(gpkg2iox, rs));

            assertTrue(rs.next());
            assertEquals(entry2[i], geomToString(gpkg2iox, rs));
        }
    }

    @Override
    protected void assertAttributeNameTable(Connection jdbcConnection) throws SQLException {
        // t_ili2db_attrname
        String [][] expectedValues=new String[][] {
                {"MultiCrs24.TestA.ClassA1.attr4:2056", "attr4_2056", "classa1_attr4_2056", null},
                {"MultiCrs24.TestA.ClassA1.attr4:21781", "attr4_21781", "classa1_attr4_21781", null},
                {"MultiCrs24.TestA.ClassA1.attr3:2056", "attr3_2056", "classa1_attr3_2056", null},
                {"MultiCrs24.TestA.ClassA1.attr3:21781", "attr3_21781", "classa1_attr3_21781", null},
                {"MultiCrs24.TestA.ClassA1.attr2:2056", "attr2_2056", "classa1", null},
                {"MultiCrs24.TestA.ClassA1.attr2:21781", "attr2_21781", "classa1_attr2_21781", null},
                {"MultiCrs24.TestA.ClassA1.attr1", "attr1", "classa1", null}
        };
        Ili2dbAssert.assertAttrNameTableFromGpkg(jdbcConnection, expectedValues);
    }

    @Override
    protected void assertTrafoTable(Connection jdbcConnection) throws SQLException {
        String[][] expectedValues = new String[][]{
                {"MultiCrs24.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                {"MultiCrs24.TestA.ClassA1.attr2:21781(MultiCrs24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr2_21781"},
                {"MultiCrs24.TestA.ClassA1.attr3:2056(MultiCrs24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr3_2056"},
                {"MultiCrs24.TestA.ClassA1.attr3:21781(MultiCrs24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr3_21781"},
                {"MultiCrs24.TestA.ClassA1.attr4:2056(MultiCrs24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr4_2056"},
                {"MultiCrs24.TestA.ClassA1.attr4:21781(MultiCrs24.TestA.ClassA1)", "ch.ehi.ili2db.secondaryTable", "classa1_attr4_21781"},
        };
        Ili2dbAssert.assertTrafoTableFromGpkg(jdbcConnection, expectedValues);
    }

    private static String geomToString(Gpkg2iox gpkg2iox, ResultSet rs) throws Exception {
        byte[] data = rs.getBytes(1);
        if (data == null) {
            return null;
        }
        return gpkg2iox.read(data).toString();
    }
}
