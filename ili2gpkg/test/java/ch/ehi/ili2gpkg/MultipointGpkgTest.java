package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.MultipointTest;

public class MultipointGpkgTest extends MultipointTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"MultiPoint.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    @Override
    protected void importXtfSmartCustom_assert_classa1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT geom FROM "+setup.prefixName("classa1")+";");
        Gpkg2iox gpkg2iox = new Gpkg2iox();

        while(rs.next()){
            assertEquals("MULTICOORD {coord [COORD {C1 600030.0, C2 200020.0}, COORD {C1 600015.0, C2 200005.0}]}", gpkg2iox.read(rs.getBytes(1)).toString());
        }
    }
	
	
}