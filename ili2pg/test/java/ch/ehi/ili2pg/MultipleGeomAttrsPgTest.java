package ch.ehi.ili2pg;

public class MultipleGeomAttrsPgTest extends ch.ehi.ili2db.MultipleGeomAttrsTest {
	
    private final static String DBSCHEMA="MultipleGeomAttrs";
    
    @Override
    protected PgTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
	
}