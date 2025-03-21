package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Array24Test;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Array24PgTest extends Array24Test {
    private static final String DBSCHEMA = "Array24";
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
            {
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
            }
            {
                ResultSet rs = stmt.executeQuery("SELECT "
                        + "array_length(auuid,1),auuid[1],"
                        + "array_length(aboolean,1),aboolean[1],"
                        + "array_length(atime,1),atime[1],"
                        + "array_length(adate,1),adate[1],"
                        + "array_length(adatetime,1),adatetime[1],"
                        + "array_length(numericint,1),numericint[1],"
                        + "array_length(numericdec,1),numericdec[1] "
                        + "FROM "+DBSCHEMA+".datatypes WHERE t_ili_tid='101';");
                ResultSetMetaData rsmd2=rs.getMetaData();
                assertEquals(14, rsmd2.getColumnCount());
                // second row
                rs.next();
                assertEquals(1, rs.getInt(1));
                assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", rs.getString(2));
                assertEquals(1, rs.getInt(3));
                assertEquals("t", rs.getString(4));
                assertEquals(1, rs.getInt(5));
                assertEquals("09:00:00", rs.getString(6));
                assertEquals(1, rs.getInt(7));
                assertEquals("2002-09-24", rs.getString(8));
                assertEquals(1, rs.getInt(9));
                assertEquals("1900-01-01 12:30:05", rs.getString(10));
                assertEquals(1, rs.getInt(11));
                assertEquals("5", rs.getString(12));
                assertEquals(1, rs.getInt(13));
                assertEquals("6.0", rs.getString(14));
            }
            {
                ResultSet rs = stmt.executeQuery("SELECT "
                        + "array_length(art,1) "
                        + "FROM "+DBSCHEMA+".gebaeude WHERE t_ili_tid='300';");
                rs.next();
                assertEquals(2, rs.getInt(1));
            }
            
            
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