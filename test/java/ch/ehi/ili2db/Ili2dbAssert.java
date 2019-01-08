package ch.ehi.ili2db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;

public class Ili2dbAssert {
    
    public Ili2dbAssert() { }
    
    public static void assertAttrNameTable(Connection jdbcConnection, String[][] expectedValues, String dbschema) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        String query = "SELECT t_ili2db_attrname.iliname, t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+dbschema+".t_ili2db_attrname";

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
    }

    public static void assertTrafoTable(Connection jdbcConnection, String[][] expectedValues, String dbschema) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        String query = "SELECT t_ili2db_trafo.iliname, t_ili2db_trafo.tag, t_ili2db_trafo.setting FROM "+dbschema+".t_ili2db_trafo";
        Assert.assertTrue(stmt.execute(query));
        Set<String[]> foundValues = getValuesFromTableTrafo(stmt.getResultSet());
        if (foundValues.size() != expectedValues.length) {
            String message = "Werte stimmen nicht ueberein. in Test Klasse: ";
            throw new org.junit.ComparisonFailure(message, foundValues.toString(), expectedValues.toString());  
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
    }
    
    public static void assertTrafoTableFromGpkg(Connection jdbcConnection, String[][] expectedValues) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        String query = "SELECT t_ili2db_trafo.iliname, t_ili2db_trafo.tag, t_ili2db_trafo.setting FROM t_ili2db_trafo";
        Assert.assertTrue(stmt.execute(query));
        Set<String[]> foundValues = getValuesFromTableTrafo(stmt.getResultSet());
        if (foundValues.size() != expectedValues.length) {
            String message = "Werte stimmen nicht ueberein. in Test Klasse: ";
            throw new org.junit.ComparisonFailure(message, foundValues.toString(), expectedValues.toString());  
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
    }
    
    public static void assertAttrNameTableFromGpkg(Connection jdbcConnection, String[][] expectedValues) throws SQLException {
        Statement stmt = jdbcConnection.createStatement();
        String query = "SELECT t_ili2db_attrname.iliname, t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM t_ili2db_attrname";

        Assert.assertTrue(stmt.execute(query));
        Set<String[]> foundValues = getValuesFromTableAttrName(stmt.getResultSet());
        if (foundValues.size() != expectedValues.length) {
            String message = "Anzahl Records stimmen nicht ueberein in Test Klasse: ";
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
    }
    
    private static Set<String[]> getValuesFromTableTrafo(ResultSet resultSet) throws SQLException {
        Set<String[]> result = new HashSet<String[]>();
        while (resultSet.next()) {
            String[] values = new String[3];
            values[0] = resultSet.getString("iliname");
            values[1] = resultSet.getString("tag");
            values[2] = resultSet.getString("setting");
            result.add(values);
        }
        return result;
    }
    
    private static Set<String[]> getValuesFromTableAttrName(ResultSet resultSet) throws SQLException {
        Set<String[]> result = new HashSet<String[]>();
        while (resultSet.next()) {
            String[] values = new String[4];
            values[0] = resultSet.getString("iliname");
            values[1] = resultSet.getString("sqlname");
            values[2] = resultSet.getString("owner");
            values[3] = resultSet.getString("target");
            result.add(values);
        }
        return result;
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
