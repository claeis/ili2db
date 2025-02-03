package ch.ehi.ili2duckdb;

import java.io.File;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.SimpleTest;

public class SimpleDuckDBTest extends SimpleTest {
    
    private final static String DBSCHEMA="foo";
    private static final String DUCKDBFILENAME=TEST_OUT+"Simple.duckdb";
    private static final String DBURL="jdbc:duckdb:"+new File(DUCKDBFILENAME).getAbsolutePath();
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new DuckDBTestSetup(DUCKDBFILENAME,DBURL,DBSCHEMA);
    }
}
