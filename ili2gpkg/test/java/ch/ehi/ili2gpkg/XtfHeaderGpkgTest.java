package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.XtfHeaderTest;

public class XtfHeaderGpkgTest extends XtfHeaderTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"XtfVersion.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
	
	
}