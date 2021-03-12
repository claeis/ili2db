package ch.ehi.ili2h2gis;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleH2gisTest extends SimpleTest {
    private final static String DBSCHEMA="simple";
    private static final String H2GISFILENAME=TEST_OUT+"Simple.h2";
    private static final String DBURL="jdbc:h2:file:"+new java.io.File(H2GISFILENAME).getAbsolutePath();
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new H2gisTestSetup(H2GISFILENAME,DBURL,DBSCHEMA);
    }
	
	
}