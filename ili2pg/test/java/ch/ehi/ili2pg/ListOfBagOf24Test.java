package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Ili2dbAssert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class ListOfBagOf24Test extends ch.ehi.ili2db.ListOfBagOf24Test {

    private static final String DBSCHEMA = "ListOf24";

    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }

    @Override
    protected void assertTableContainsColumns(Connection jdbcConnection, String tableName, String... expectedColumns) throws SQLException {
        String[][] expectedValues = new String[expectedColumns.length][1];

        for (int i = 0; i < expectedColumns.length; i++) {
            expectedValues[i] = new String[] {expectedColumns[i].toLowerCase()};
        }

        String filter = "table_schema = '" + setup.getSchema().toLowerCase() + "' AND table_name = '" + tableName + "'";
        Ili2dbAssert.assertTableContainsValues(jdbcConnection, "information_schema.columns", new String[]{"column_name"}, expectedValues, filter);
    }

    @Override
    protected void assertTableContainsValues(Connection jdbcConnection, String table, String[] columns, String[][] expectedValues) throws SQLException {
        super.assertTableContainsValues(jdbcConnection, setup.getSchema() + "." + table, columns, expectedValues);
    }

    @Override
    protected void assertClassA1Attr8(Connection jdbcConnection) throws Exception {
        Statement statement = null;
        try {
            statement = jdbcConnection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT st_asewkt(attr8) FROM " + setup.prefixName("classa1") + ";");
            while (resultSet.next()) {
                assertEquals("SRID=2056;POINT(480000 70000)", resultSet.getObject(1));
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    @Override
    protected void assertClassA1Attr9(Connection jdbcConnection) throws Exception {
        Statement statement = null;
        try {
            statement = jdbcConnection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT st_asewkt(attr9) FROM " + setup.prefixName("classa1_attr9") + ";");
            while (resultSet.next()) {
                assertEquals("SRID=2056;POINT(500000 72000)", resultSet.getObject(1));
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
