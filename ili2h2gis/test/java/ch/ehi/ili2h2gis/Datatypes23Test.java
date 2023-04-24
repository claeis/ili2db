package ch.ehi.ili2h2gis;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;

public class Datatypes23Test extends ch.ehi.ili2db.Datatypes23Test {
	
    private static final String H2GISFILENAME=TEST_OUT+"Datatypes23.h2";
    private static final String DBURL="jdbc:h2:file:"+new java.io.File(H2GISFILENAME).getAbsolutePath();
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new H2gisTestSetup(H2GISFILENAME,DBURL);
    }
    @Override
    protected void assertImportXtfLine_simpleline3(Statement stmt) throws SQLException {
        {
            ResultSet rs = stmt.executeQuery("SELECT straights3d FROM "+setup.prefixName("simpleline3")+" WHERE t_ili_tid = 'SimpleLine3.1';");
            ResultSetMetaData rsmd=rs.getMetaData();
            assertEquals(1, rsmd.getColumnCount());
            while(rs.next()){
                String geom=rs.getString(1);
                assertEquals("LINESTRING (2460001 1045001 300, 2460010 1045010 300)", geom);
            }
        }
    }

    @Override
    protected void assertImportXtfLine_line3(Statement stmt) throws SQLException {
        {
            ResultSet rs = stmt.executeQuery("SELECT straightsarcs3d FROM "+setup.prefixName("line3")+" WHERE t_ili_tid = 'Line3.1';");
            ResultSetMetaData rsmd=rs.getMetaData();
            assertEquals(1, rsmd.getColumnCount());
            while(rs.next()){
                String geom=rs.getString(1);
                assertEquals("LINESTRING (2460001 1045001 300, 2460001.2286122167 1045001.0801162157, 2460001.4546624995 1045001.1671990677, 2460001.6779386075 1045001.2611667924, 2460001.8982309024 1045001.3619311622, 2460002.11533255 1045001.4693975681, 2460002.3290397087 1045001.5834651083, 2460002.539151727 1045001.704026683, 2460002.7454713276 1045001.8309690953, 2460002.9478047937 1045001.9641731572, 2460003.1459621512 1045002.1035138015, 2460003.339757348 1045002.2488601991, 2460003.5290084267 1045002.4000758822, 2460003.7135376967 1045002.5570188722, 2460003.893171901 1045002.7195418131, 2460004.0677423775 1045002.8874921099, 2460004.237085221 1045003.0607120714, 2460004.4010414314 1045003.2390390589, 2460004.5594570693 1045003.4223056388, 2460004.712183395 1045003.6103397394, 2460004.859077012 1045003.8029648125, 2460005 1045004, 2460005.138642302 1045004.2071654596, 2460005.2706842828 1045004.4185989732, 2460005.395994664 1045004.6340903277, 2460005.514448858 1045004.8534252757, 2460005.6259290944 1045005.0763857483, 2460005.730324536 1045005.3027500722, 2460005.8275313913 1045005.5322931898, 2460005.9174530134 1045005.7647868829, 2460006 1045006, 2460010 1045010 300)", geom);
            }
        }
    }
		
}