package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Array23PgTest extends ch.ehi.ili2db.Array23Test {
    private static final String DBSCHEMA = "Array23";
    String dburl;
    String dbuser;
    String dbpwd; 

    @Override
    protected AbstractTestSetup createTestSetup() {
        dburl=System.getProperty("dburl"); 
        dbuser=System.getProperty("dbusr");
        dbpwd=System.getProperty("dbpwd"); 
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    }
    @Override
    protected void importXtfEnumFkTable_Assert() throws SQLException {
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT "
                    + "auuid,"
                    + "aboolean,"
                    + "atime,"
                    + "adate,"
                    + "adatetime,"
                    + "numericint,"
                    + "numericdec"
                    + " FROM "+DBSCHEMA+".datatypes WHERE t_ili_tid='100';");
            ResultSetMetaData rsmd=rs.getMetaData();
            assertEquals(7, rsmd.getColumnCount());
            // first row
            rs.next();
            assertEquals(null, rs.getString(1));
            assertEquals(null, rs.getString(2));
            assertEquals(null, rs.getString(3));
            assertEquals(null, rs.getString(4));
            assertEquals(null, rs.getString(5));
            assertEquals(null, rs.getString(6));
            assertEquals(null, rs.getString(7));
            
            ResultSet rs2 = stmt.executeQuery("SELECT "
                    + "array_length(auuid,1),auuid[1],"
                    + "array_length(aboolean,1),aboolean[1],"
                    + "array_length(atime,1),atime[1],"
                    + "array_length(adate,1),adate[1],"
                    + "array_length(adatetime,1),adatetime[1],"
                    + "array_length(numericint,1),numericint[1],"
                    + "array_length(numericdec,1),numericdec[1] "
                    + "FROM "+DBSCHEMA+".datatypes WHERE t_ili_tid='101';");
            ResultSetMetaData rsmd2=rs2.getMetaData();
            assertEquals(14, rsmd2.getColumnCount());
            // second row
            rs2.next();
            assertEquals(1, rs2.getInt(1));
            assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", rs2.getString(2));
            assertEquals(1, rs2.getInt(3));
            assertEquals("t", rs2.getString(4));
            assertEquals(1, rs2.getInt(5));
            assertEquals("09:00:00", rs2.getString(6));
            assertEquals(1, rs2.getInt(7));
            assertEquals("2002-09-24", rs2.getString(8));
            assertEquals(1, rs2.getInt(9));
            assertEquals("1900-01-01 12:30:05", rs2.getString(10));
            assertEquals(1, rs2.getInt(11));
            assertEquals("5", rs2.getString(12));
            assertEquals(1, rs2.getInt(13));
            assertEquals("6.0", rs2.getString(14));
            
        }finally {
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        
    }
}