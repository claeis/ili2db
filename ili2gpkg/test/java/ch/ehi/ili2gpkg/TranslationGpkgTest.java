package ch.ehi.ili2gpkg;

import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;

public class TranslationGpkgTest extends ch.ehi.ili2db.TranslationTest {
    
    private static final String GPKGFILENAME=TEST_OUT+"Translation.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
    @Override
    protected void validateImportItf10lineTable_Geom(Statement stmt) throws SQLException {
        
    }


}