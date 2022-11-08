package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ListOfBagOf24Test extends ch.ehi.ili2db.ListOfBagOf24Test {
    private static final String GPKGFILENAME = "test/data/ListOfBagOfPrimTypes24/ListOfBagOfPrimTypes24.gpkg";
    private static final String DBURL = "jdbc:sqlite:" + GPKGFILENAME;

    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME, DBURL);
    }

    @Override
    protected void assertTableContainsColumns(Connection jdbcConnection, String tableName, String... expectedColumns) throws SQLException {
        Statement statement = null;
        try {
            statement = jdbcConnection.createStatement();

            String query = "PRAGMA table_info('" + tableName + "')";
            ResultSet resultSet = statement.executeQuery(query);
            List<String> actualValues = new ArrayList<String>();
            while (resultSet.next()) {
                actualValues.add(resultSet.getString("name"));
            }

            assertThat(actualValues, containsInAnyOrder(expectedColumns));
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
