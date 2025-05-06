package ch.ehi.ili2db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;

import static ch.ehi.ili2db.Ili2dbAssert.assertTableContainsValues;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class Ili2dbAssert {
    
    public Ili2dbAssert() { }
    
    public static void assertAttrNameTable(AbstractTestSetup setup, String[][] expectedValues) throws SQLException {
        Connection jdbcConnection=null;
        try {
            jdbcConnection=setup.createConnection();
            String dbschema=setup.getSchema();
            assertAttrNameTable(jdbcConnection,expectedValues,dbschema);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Deprecated
    public static void assertAttrNameTable(Connection jdbcConnection, String[][] expectedValues, String dbschema) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        try {
            String tabname=DbNames.ATTRNAME_TAB;
            if(dbschema!=null) {
                tabname=dbschema+"."+DbNames.ATTRNAME_TAB;
            }
            String query = "SELECT "+DbNames.ATTRNAME_TAB+"."+DbNames.ATTRNAME_TAB_ILINAME_COL+", "+DbNames.ATTRNAME_TAB+"."+DbNames.ATTRNAME_TAB_SQLNAME_COL+", "+DbNames.ATTRNAME_TAB+"."+DbNames.ATTRNAME_TAB_COLOWNER_COL+", "+DbNames.ATTRNAME_TAB+"."+DbNames.ATTRNAME_TAB_TARGET_COL+" FROM "+tabname;
            
            Assert.assertTrue(stmt.execute(query));
            Set<String[]> foundValues = getValuesFromTableAttrName(stmt.getResultSet());
            if (foundValues.size() != expectedValues.length) {
                String message = "Anzahl Records stimmen nicht ueberein.";
                throw new org.junit.ComparisonFailure(message, Integer.toString(expectedValues.length), Integer.toString(foundValues.size()));  
            }
            
            //Sort the array expectedValues
            Arrays.sort(expectedValues, new ColumnComparator());
            
            //Convert FoundedValues to 2d Array
            String[][] foundValues2DArray = new String[foundValues.size()][];
            
            int index = 0;
            for (Iterator<String[]> foundIterator = foundValues.iterator(); foundIterator.hasNext();) {  
                String row[] = foundIterator.next(); 
                foundValues2DArray[index] = row;
                index++;
            }
            
            //Sort the array foundValues
            Arrays.sort(foundValues2DArray, new ColumnComparator());
            
            for (int j = 0; j < foundValues2DArray.length; j++) {
                String[] foundValue = foundValues2DArray[j];
                String[] expectedValue = expectedValues[j];
                if (foundValue.length != expectedValue.length) {
                    String message = "Anzahl Werte stimmen nicht ueberein fuer " + expectedValue[0];
                    throw new org.junit.ComparisonFailure(message, Integer.toString(expectedValue.length), Integer.toString(foundValue.length));                
                }
                for (int i = 0; i < foundValue.length; i++) {
                    if ((foundValue[i] == null && expectedValue[i] == null) 
                            || ((foundValue[i] != null && expectedValue[i] != null) && foundValue[i].equals(expectedValue[i]))) {
                    }else {
                        String message = "Werte stimmen nicht ueberein fuer: " + expectedValue[0];
                        throw new org.junit.ComparisonFailure(message, expectedValue[i], foundValue[i]);                    
                    }
                }            
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void assertColumnTable_foreignKey(AbstractTestSetup setup, String[][] expectedValues) throws SQLException {
        Connection jdbcConnection=null;
        try {
            jdbcConnection=setup.createConnection();
            assertTableContainsValues(
                    jdbcConnection,
                    setup.prefixName(DbNames.META_INFO_COLUMN_TAB),
                    new String[] {
                            DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL,
                            DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL,
                            DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL,
                            DbNames.META_INFO_COLUMN_TAB_SETTING_COL
                    }, expectedValues,
                    DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_FOREIGNKEY+"'"
            );        
        }finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    public static void assertTrafoTable(AbstractTestSetup setup, String[][] expectedValues) throws SQLException {
        Connection jdbcConnection=null;
        try {
            jdbcConnection=setup.createConnection();
            String dbschema=setup.getSchema();
            assertTrafoTable(jdbcConnection,expectedValues,dbschema);
        } finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        }
    }
    @Deprecated
    public static void assertTrafoTable(Connection jdbcConnection, String[][] expectedValues, String dbschema) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        try {
            String tabname = DbNames.TRAFO_TAB;
            if(dbschema!=null) {
                tabname = dbschema+"."+DbNames.TRAFO_TAB;
            }
            String query = "SELECT "+DbNames.TRAFO_TAB+"."+DbNames.TRAFO_TAB_ILINAME_COL+", "+DbNames.TRAFO_TAB+"."+DbNames.TRAFO_TAB_TAG_COL+", "+DbNames.TRAFO_TAB+"."+DbNames.TRAFO_TAB_SETTING_COL+" FROM "+tabname;
            Assert.assertTrue(stmt.execute(query));
            Set<String[]> foundValues = getValuesFromTableTrafo(stmt.getResultSet());
            if (foundValues.size() != expectedValues.length) {
                String message = "Anzahl Eintraege stimmen nicht ueberein: ";
                throw new org.junit.ComparisonFailure(message, Integer.toString(expectedValues.length),Integer.toString(foundValues.size()) );  
            }
            
            //Sort the array expectedValues
            Arrays.sort(expectedValues, new ColumnComparator());
            
            //Convert FoundedValues to 2d Array
            String[][] foundValues2DArray = new String[foundValues.size()][];
            
            int index = 0;
            for (Iterator<String[]> foundIterator = foundValues.iterator(); foundIterator.hasNext();) {  
                String row[] = foundIterator.next(); 
                foundValues2DArray[index] = row;
                index++;
            }
            
            //Sort the array foundValues
            Arrays.sort(foundValues2DArray, new ColumnComparator());
            
            for (int j = 0; j < foundValues2DArray.length; j++) {
                String[] foundValue = foundValues2DArray[j];
                String[] expectedValue = expectedValues[j];
                if (foundValue.length != expectedValue.length) {
                    String message = "Anzahl Werte stimmen nicht ueberein fuer " + expectedValue[0];
                    throw new org.junit.ComparisonFailure(message, Integer.toString(expectedValue.length), Integer.toString(foundValue.length));                
                }
                for (int i = 0; i < foundValue.length; i++) {
                    if ((foundValue[i] == null && expectedValue[i] == null) 
                            || ((foundValue[i] != null && expectedValue[i] != null) && foundValue[i].equals(expectedValue[i]))) {
                    }else {
                        String message = "Werte stimmen nicht ueberein fuer: " + expectedValue[0];
                        throw new org.junit.ComparisonFailure(message, expectedValue[i], foundValue[i]);                    
                    }
                }            
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    @Deprecated
    public static void assertTrafoTableFromGpkg(Connection jdbcConnection, String[][] expectedValues) throws SQLException {
        assertTrafoTable(jdbcConnection, expectedValues,null);
    }
    @Deprecated
    public static void assertAttrNameTableFromGpkg(Connection jdbcConnection, String[][] expectedValues) throws SQLException {
        assertAttrNameTable(jdbcConnection, expectedValues,null);
    }

    public static void assertTableContainsValues(Connection jdbcConnection, String table, String[] columns, String[][] expectedValues, String filter) throws SQLException {
        Statement statement = null;
        try {
            statement = jdbcConnection.createStatement();

            String query = "SELECT " + join(",", columns) + " FROM " + table + (filter == null ? "" : " WHERE " + filter);
            ResultSet resultSet = statement.executeQuery(query);
            List<String[]> actualValues = new ArrayList<String[]>();
            while (resultSet.next()) {
                String[] values = new String[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    values[i] = resultSet.getString(i + 1);
                }
                actualValues.add(values);
            }

            assertThat(actualValues, containsInAnyOrder(expectedValues));
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    private static Set<String[]> getValuesFromTableTrafo(ResultSet resultSet) throws SQLException {
        Set<String[]> result = new HashSet<String[]>();
        while (resultSet.next()) {
            String[] values = new String[3];
            values[0] = resultSet.getString(DbNames.TRAFO_TAB_ILINAME_COL);
            values[1] = resultSet.getString(DbNames.TRAFO_TAB_TAG_COL);
            values[2] = resultSet.getString(DbNames.TRAFO_TAB_SETTING_COL);
            result.add(values);
        }
        return result;
    }
    
    private static Set<String[]> getValuesFromTableAttrName(ResultSet resultSet) throws SQLException {
        Set<String[]> result = new HashSet<String[]>();
        while (resultSet.next()) {
            String[] values = new String[4];
            values[0] = resultSet.getString(DbNames.ATTRNAME_TAB_ILINAME_COL);
            values[1] = resultSet.getString(DbNames.ATTRNAME_TAB_SQLNAME_COL);
            values[2] = resultSet.getString(DbNames.ATTRNAME_TAB_COLOWNER_COL);
            values[3] = resultSet.getString(DbNames.ATTRNAME_TAB_TARGET_COL);
            result.add(values);
        }
        return result;
    }

    /**
     * see String.join introduced in Java 8.
     *
     * @deprecated replaced by String.join in Java 8
     */
    @Deprecated
    private static String join(String delimiter, String[] elements) {
        if (elements == null || elements.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            sb.append(delimiter);
            sb.append(elements[i]);
        }

        return sb.toString();
    }
}

//Class that extends Comparator
class ColumnComparator implements Comparator<String[]> {

    ColumnComparator() {
    }

    //Overriding compare method
    @Override
    public int compare(String[] row1, String[] row2) {
        //Compare the columns to sort
        for(int i=0;i<row1.length;i++) {
            int c=row1[i].compareTo(row2[i]);
            if(c!=0) {
                return c;
            }
        }
        return 0;
    }
}
