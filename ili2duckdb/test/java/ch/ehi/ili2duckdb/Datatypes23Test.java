package ch.ehi.ili2duckdb;

import java.io.File;

import ch.ehi.ili2db.AbstractTestSetup;

public class Datatypes23Test extends ch.ehi.ili2duckdb.impl.Datatypes23Test {

    private static final String DUCKDBFILENAME=TEST_OUT+"Datatypes23.duckdb";
    private static final String DBURL="jdbc:duckdb:"+new File(DUCKDBFILENAME).getAbsolutePath();
    
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new DuckDBTestSetup(DUCKDBFILENAME,DBURL);
    }
}
