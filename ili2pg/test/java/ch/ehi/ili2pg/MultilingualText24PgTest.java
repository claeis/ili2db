package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

public class MultilingualText24PgTest extends ch.ehi.ili2db.MultilingualText24Test {
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }
}
