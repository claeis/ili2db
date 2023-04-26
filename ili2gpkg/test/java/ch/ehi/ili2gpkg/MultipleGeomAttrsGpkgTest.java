package ch.ehi.ili2gpkg;

import ch.ehi.ili2db.AbstractTestSetup;

public class MultipleGeomAttrsGpkgTest extends ch.ehi.ili2db.MultipleGeomAttrsTest {
	
    private static final String GPKGFILENAME=TEST_OUT+"MultipleGeomAttrsGpkgTest.gpkg";
    
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    @Override
    public void importIliExtendedClassSmart1() throws Exception {
    }
    @Override
    public void importIliExtendedClassSmart2() throws Exception {
    }
    @Override
    public void importXtf() throws Exception {
    }
    @Override
    public void importXtfExtendedClassSmart1() throws Exception {
    }
    @Override
    public void importXtfExtendedClassSmart2() throws Exception {
    }
    @Override
    public void exportXtf() throws Exception {
    }
    @Override
    public void exportXtfExtendedClassSmart1() throws Exception {
    }
    @Override
    public void exportXtfExtendedClassSmart2() throws Exception {
    }
}