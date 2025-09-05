package ch.ehi.ili2pg;

import ch.ehi.ili2db.AbstractTestSetup;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Oid23Test extends ch.ehi.ili2db.Oid23Test {
	private static final String DBSCHEMA = "Oid23";
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl = System.getProperty("dburl");
        String dbuser = System.getProperty("dbusr");
        String dbpwd = System.getProperty("dbpwd");

        return new PgTestSetup(dburl, dbuser, dbpwd, DBSCHEMA);
    }
}