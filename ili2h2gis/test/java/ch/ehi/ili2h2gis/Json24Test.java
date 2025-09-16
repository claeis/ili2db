package ch.ehi.ili2h2gis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import ch.ehi.ili2db.AbstractTestSetup;

public class Json24Test extends ch.ehi.ili2db.Json24Test {
	
    private static final String H2GISFILENAME=TEST_OUT+"Json24.h2";
    private static final String DBURL="jdbc:h2:file:"+new java.io.File(H2GISFILENAME).getAbsolutePath();
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new H2gisTestSetup(H2GISFILENAME,DBURL);
    }
    @Override
    protected void importXtf_doAsserts(java.sql.Statement stmt) throws SQLException {
        java.sql.ResultSet rs=null;
        try {
            rs=stmt.executeQuery("SELECT farben FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='1'");
            assertTrue(rs.next());
            assertEquals("[{\"@type\":\"Json24.TestA.Farbe\",\"r\":10,\"g\":11,\"b\":12,\"name\":\"f1\",\"active\":false},{\"@type\":\"Json24.TestA.Farbe\",\"r\":20,\"g\":21,\"b\":22,\"name\":\"f2\",\"active\":false}]",rs.getString(1));
            rs=stmt.executeQuery("SELECT cast(farben as text) FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='2'");
            assertTrue(rs.next());
            rs.getString(1);
            assertEquals(true,rs.wasNull());
        }finally {
            if(rs!=null) {
                rs.close();
                rs=null;
            }
        }
    }
	
}