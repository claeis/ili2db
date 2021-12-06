package ch.ehi.ili2gpkg;


import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class RtreeSpatialIndexTest {
    private static final String TEST_OUT="test/data/RtreeSpatialIndex/";
    private static final String FILENAME_XTF=TEST_OUT+"RtreeSpatialIndex.xtf";
    private static final String MODEL_XTF="RtreeSpatialIndex";
    private static final String FILENAME_XTF_GPKG=TEST_OUT+"RtreeSpatialIndex.gpkg";
    private static final String PRIMARY_KEY_XTF_GPKG="T_Id";
    private static final String FILENAME_TEST_DATA_GPKG=TEST_OUT+"test_data.gpkg";
    private static final String PRIMARY_KEY_TEST_DATA_GPKG="id";

    private Connection jdbcConnection=null;
    private Statement stmt=null,stmt2=null;
    private ResultSet rs=null,rs2=null;

    @Before
    public void initDb() throws Exception {
        clearDb();
    }

    private void clearDb() throws Exception {
        File gpkgFile=new File(FILENAME_XTF_GPKG);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
    }

    @After
    public void endDb() throws Exception {
        closeDb();
        clearDb();
    }

    private void closeDb() throws Exception {
        if(jdbcConnection!=null) {
            if(stmt!=null) {
                stmt.close();
            }
            if(stmt2!=null) {
                stmt2.close();
            }
            jdbcConnection.close();
        }
    }

    private void openDb(String filename) throws Exception {
        jdbcConnection=DriverManager.getConnection("jdbc:sqlite:"+filename,null,null);
        stmt=jdbcConnection.createStatement();
        stmt2=jdbcConnection.createStatement();
    }

    // this test shows that the SQL functions ST_IsEmpty, ST_MinX, ST_MaxX, ST_MinY, ST_MaxY required for a rtree spatial index aren't available from sqlite-jdbc
    @Test
    public void testSQLFunctionsMissing() throws Exception {
        createSchema(false);

        openDb(FILENAME_XTF_GPKG);
        // don't load the SQL functions ST_IsEmpty, ST_MinX, ST_MaxX, ST_MinY, ST_MaxY

        try {
            // using ST_IsEmpty must throw an exception because the function isn't available
            stmt.execute("SELECT ST_IsEmpty(geometry) FROM point2d");
            Assert.fail();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSQLFunctionsImplementation() throws Exception {
        openDb(FILENAME_TEST_DATA_GPKG);

        // load the SQL functions ST_IsEmpty, ST_MinX, ST_MaxX, ST_MinY, ST_MaxY
        Config config = new Config();
        config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE);

        GpkgMapping gpkgMapping = new GpkgMapping();
        gpkgMapping.postConnect(jdbcConnection, config);

        // get table_name,column_name for all geometric columns having a rtree spatial index
        ArrayList<Tuple<String>> tabColTuples=new ArrayList<Tuple<String>>();
        rs=stmt.executeQuery("SELECT table_name,column_name FROM gpkg_extensions WHERE extension_name = 'gpkg_rtree_index'");
        while(rs.next()) {
            tabColTuples.add(new Tuple<String>(rs.getString("table_name"),rs.getString("column_name")));
        }
        Assert.assertTrue(tabColTuples.size() > 0);

        for(Tuple<String> tabColTuple:tabColTuples) {
            String table_name=tabColTuple.first;
            String column_name=tabColTuple.second;

            // test if the results of ST_MinX, ST_MaxX, ST_MinY, ST_MaxY match the test data
            rs=stmt.executeQuery("SELECT id,minx,maxx,miny,maxy FROM rtree_"+table_name+"_"+column_name);
            while(rs.next()) {
                long id=rs.getLong("id");
                double minx=rs.getDouble("minx");
                double maxx=rs.getDouble("maxx");
                double miny=rs.getDouble("miny");
                double maxy=rs.getDouble("maxy");

                rs2=stmt2.executeQuery("SELECT ST_MinX("+column_name+") AS minx,ST_MaxX("+column_name+") AS maxx,"
                        +"ST_MinY("+column_name+") AS miny,ST_MaxY("+column_name+") AS maxy FROM "+table_name+
                        " WHERE "+PRIMARY_KEY_TEST_DATA_GPKG+" = "+Long.toString(id));
                Assert.assertTrue(rs2.next());

                // tolerance for floating-point equality
                double tol=1.0e-4;

                Assert.assertTrue(Math.abs(minx-rs2.getDouble("minx"))<tol);
                Assert.assertTrue(Math.abs(maxx-rs2.getDouble("maxx"))<tol);
                Assert.assertTrue(Math.abs(miny-rs2.getDouble("miny"))<tol);
                Assert.assertTrue(Math.abs(maxy-rs2.getDouble("maxy"))<tol);
            }

            // test if the result of ST_IsEmpty matches the test data
            rs=stmt.executeQuery("SELECT "+PRIMARY_KEY_TEST_DATA_GPKG+" FROM "+table_name+" WHERE "+
                    PRIMARY_KEY_TEST_DATA_GPKG+" NOT IN (SELECT id FROM rtree_"+table_name+"_"+column_name+")");
            while(rs.next()) {
                long primary_key=rs.getLong(PRIMARY_KEY_TEST_DATA_GPKG);

                rs2=stmt2.executeQuery("SELECT * FROM "+table_name+" WHERE "+PRIMARY_KEY_TEST_DATA_GPKG+" = "+Long.toString(primary_key)+" AND NOT "+
                        "("+column_name+" ISNULL OR ST_IsEmpty("+column_name+"))"); // use short-circuit evaluation, ST_IsEmpty(NULL) is invalid
                Assert.assertFalse(rs2.next());
            }
        }
    }

    @Test
    public void testSpatialIndexImplementation() throws Exception {
        createSchema(true);
        openDb(FILENAME_XTF_GPKG);

        // get table_name,column_name for all geometric columns having a rtree spatial index
        ArrayList<Tuple<String>> tabColTuples=new ArrayList<Tuple<String>>();
        rs=stmt.executeQuery("SELECT table_name,column_name FROM gpkg_geometry_columns WHERE "+
                "table_name IN (SELECT table_name FROM gpkg_extensions WHERE extension_name = 'gpkg_rtree_index')");
        while(rs.next()) {
            tabColTuples.add(new Tuple<String>(rs.getString("table_name"),rs.getString("column_name")));
        }
        Assert.assertTrue(tabColTuples.size() > 0);

        // rtree "create virtual table" and "create trigger" SQL statements syntax verification
        for(Tuple<String> tabColTuple:tabColTuples) {
            String table_name = tabColTuple.first;
            String column_name = tabColTuple.second;

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE tbl_name = 'rtree_"+table_name+"_"+column_name+"'");
            Assert.assertTrue(rs.next());
            String createStmt=rs.getString("sql");
            Assert.assertEquals("CREATE VIRTUAL TABLE \"rtree_"+table_name+"_"+column_name+"\" USING rtree(id,minx,maxx,miny,maxy)",
                    createStmt);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_insert'");
            Assert.assertTrue(rs.next());
            String triggerStmt1=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_insert\" AFTER INSERT ON \""+table_name+"\" "+
                    "WHEN (NEW.\""+column_name+"\" NOT NULL AND NOT ST_IsEmpty(NEW.\""+column_name+"\")) "+
                    "BEGIN "+
                    "INSERT OR REPLACE INTO \"rtree_"+table_name+"_"+column_name+"\" VALUES ("+
                    "NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\","+
                    "ST_MinX(NEW.\""+column_name+"\"),ST_MaxX(NEW.\""+column_name+"\"),"+
                    "ST_MinY(NEW.\""+column_name+"\"),ST_MaxY(NEW.\""+column_name+"\")"+
                    "); "+
                    "END",
                    triggerStmt1);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_delete'");
            Assert.assertTrue(rs.next());
            String triggerStmt6=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_delete\" AFTER DELETE ON \""+table_name+"\" "+
                    "WHEN OLD.\""+column_name+"\" NOT NULL "+
                    "BEGIN "+
                    "DELETE FROM \"rtree_"+table_name+"_"+column_name+"\" WHERE id = OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\"; "+
                    "END",
                    triggerStmt6);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_update1'");
            Assert.assertTrue(rs.next());
            String triggerStmt2=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_update1\" AFTER UPDATE OF \""+column_name+"\" ON \""+table_name+"\" "+
                    "WHEN OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\" = NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\" AND "+
                    "(NEW.\""+column_name+"\" NOTNULL AND NOT ST_IsEmpty(NEW.\""+column_name+"\")) "+
                    "BEGIN "+
                    "INSERT OR REPLACE INTO \"rtree_"+table_name+"_"+column_name+"\" VALUES ("+
                    "NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\","+
                    "ST_MinX(NEW.\""+column_name+"\"),ST_MaxX(NEW.\""+column_name+"\"),"+
                    "ST_MinY(NEW.\""+column_name+"\"),ST_MaxY(NEW.\""+column_name+"\")"+
                    "); "+
                    "END",
                    triggerStmt2);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_update2'");
            Assert.assertTrue(rs.next());
            String triggerStmt3=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_update2\" AFTER UPDATE OF \""+column_name+"\" ON \""+table_name+"\" "+
                    "WHEN OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\" = NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\" AND "+
                    "(NEW.\""+column_name+"\" ISNULL OR ST_IsEmpty(NEW.\""+column_name+"\")) "+
                    "BEGIN "+
                    "DELETE FROM \"rtree_"+table_name+"_"+column_name+"\" WHERE id = OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\"; "+
                    "END",
                    triggerStmt3);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_update3'");
            Assert.assertTrue(rs.next());
            String triggerStmt4=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_update3\" AFTER UPDATE ON \""+table_name+"\" "+
                    "WHEN OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\" != NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\" AND "+
                    "(NEW.\""+column_name+"\" NOTNULL AND NOT ST_IsEmpty(NEW.\""+column_name+"\")) "+
                    "BEGIN "+
                    "DELETE FROM \"rtree_"+table_name+"_"+column_name+"\" WHERE id = OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\"; "+
                    "INSERT OR REPLACE INTO \"rtree_"+table_name+"_"+column_name+"\" VALUES ("+
                    "NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\","+
                    "ST_MinX(NEW.\""+column_name+"\"),ST_MaxX(NEW.\""+column_name+"\"),"+
                    "ST_MinY(NEW.\""+column_name+"\"),ST_MaxY(NEW.\""+column_name+"\")"+
                    "); "+
                    "END",
                    triggerStmt4);

            rs=stmt.executeQuery("SELECT sql FROM sqlite_master WHERE type = 'trigger' AND name = 'rtree_"+table_name+"_"+column_name+"_update4'");
            Assert.assertTrue(rs.next());
            String triggerStmt5=rs.getString("sql");
            Assert.assertEquals("CREATE TRIGGER \"rtree_"+table_name+"_"+column_name+"_update4\" AFTER UPDATE ON \""+table_name+"\" "+
                    "WHEN OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\" != NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\" AND "+
                    "(NEW.\""+column_name+"\" ISNULL OR ST_IsEmpty(NEW.\""+column_name+"\")) "+
                    "BEGIN "+
                    "DELETE FROM \"rtree_"+table_name+"_"+column_name+"\" WHERE id IN (OLD.\""+ PRIMARY_KEY_XTF_GPKG +"\",NEW.\""+ PRIMARY_KEY_XTF_GPKG +"\"); "+
                    "END",
                    triggerStmt5);
        }
    }

    @Test
    public void testTableContentGpkgExtensions() throws Exception {
        createSchema(true);
        openDb(FILENAME_XTF_GPKG);

        // test if table gpkg_extensions exists
        ResultSet rs=stmt.executeQuery("SELECT EXISTS (SELECT * FROM sqlite_master WHERE type='table' AND name='gpkg_extensions')");
        Assert.assertTrue(rs.next() && rs.getBoolean(1));

        // test if there are entries in table gpkg_extensions with extension_name='gpkg_rtree_index'
        rs=stmt.executeQuery("SELECT COUNT(*) AS count FROM gpkg_extensions WHERE extension_name = 'gpkg_rtree_index'");
        Assert.assertTrue(rs.next() && rs.getInt("count") > 0);

        // test if each entry of table gpkg_extensions with extension_name='gpkg_rtree_index' has scope='write-only'
        rs=stmt.executeQuery("SELECT table_name,column_name,scope FROM gpkg_extensions WHERE extension_name = 'gpkg_rtree_index'");
        while(rs.next()) {
            Assert.assertTrue("write-only".equals(rs.getString("scope")));
        }
    }

    private void createSchema(boolean createGeomIdx) throws Exception {
        Config config=new Config();
        (new GpkgMain()).initConfig(config);

        config.setDbfile(FILENAME_XTF_GPKG);
        config.setDburl("jdbc:sqlite:"+ FILENAME_XTF_GPKG);

        config.setXtffile(FILENAME_XTF);
        config.setModels(MODEL_XTF);
        config.setModeldir(TEST_OUT);
        config.setDefaultSrsCode("2056");

        config.setFunction(Config.FC_SCHEMAIMPORT);
        if(createGeomIdx) {
            config.setValue(Config.CREATE_GEOM_INDEX, Config.TRUE);
        }

        Ili2db.run(config,null); // set up database schema
    }

    // helper class
    private class Tuple<T> {
        public T first=null;
        public T second=null;
        public Tuple(T first, T second) {
            this.first=first;
            this.second=second;
        }
        public Tuple() {}
    }
}
