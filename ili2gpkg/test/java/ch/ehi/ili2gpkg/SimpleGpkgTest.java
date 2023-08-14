package ch.ehi.ili2gpkg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleGpkgTest extends SimpleTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"Simple.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    @Override
    protected void validateImportIliCoord() throws Exception {
        super.validateImportIliCoord();
        Connection jdbcConnection=null;
        Statement stmt=null;
        try {
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT table_name FROM gpkg_contents");
            while(rs.next()) {
                Assert.assertEquals("classa1",rs.getString(1));
            }
            rs=stmt.executeQuery("SELECT table_name,column_name FROM gpkg_geometry_columns");
            while(rs.next()) {
                Assert.assertEquals("classa1",rs.getString(1));
                Assert.assertEquals("attr2",rs.getString(2));
            }
            
        }finally {
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
        
    }
	
	
}