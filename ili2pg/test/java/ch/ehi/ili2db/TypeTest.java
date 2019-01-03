package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.base.DbNames;

public class TypeTest {
    private static final String DBSCHEMA = "ClassType";
    private static final String TEST_DATA_DIR = "test/data/ClassType/";

    String dburl = System.getProperty("dburl");
	String dbuser = System.getProperty("dbusr");
	String dbpwd = System.getProperty("dbpwd");

    public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);
		config.setDburl(dburl);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

    @Test
    public void columnPropType() throws Exception {
        Connection jdbcConnection = null;
        Statement stmt = null;

		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

            File data = new File(TEST_DATA_DIR, "ClassType.ili");
            Config config = initConfig(data.getPath(), DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateMetaInfo(true);

            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);

            String query = "SELECT setting FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+
                " WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = 'ch.ehi.ili2db.types' and "
                + DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = 'T_Type' and "
                + DbNames.META_INFO_TABLE_TAB_TABLENAME_COL+" = 'classa1';";

            Assert.assertTrue(stmt.execute(query));
            ResultSet rs = stmt.getResultSet();

            Assert.assertTrue(rs.next());
            String setting = rs.getString(1);
            Assert.assertTrue(setting.contains("classa1"));
            Assert.assertTrue(setting.contains("classa1b"));
            Assert.assertTrue(setting.contains("classa1c"));

        }finally{
			if(jdbcConnection != null){
				jdbcConnection.close();
			}
		}
    }

    @Test
    public void checkConstraint() throws Exception {
        Connection jdbcConnection = null;
        Statement stmt = null;

		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

            File data = new File(TEST_DATA_DIR, "ClassType.ili");
            Config config = initConfig(data.getPath(), DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateTypeConstraint(true);
            config.setCreateMetaInfo(true);

            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);

            String query = "SELECT pg_catalog.pg_get_constraintdef(r.oid, true) as condef FROM pg_catalog.pg_constraint r WHERE r.conrelid = 'classtype.classa1'::regclass AND r.contype = 'c';";

            Assert.assertTrue(stmt.execute(query));
            ResultSet rs = stmt.getResultSet();

            Assert.assertTrue(rs.next());
            String constraint = rs.getString(1);
            Assert.assertTrue(constraint.contains("classa1"));
            Assert.assertTrue(constraint.contains("classa1b"));
            Assert.assertTrue(constraint.contains("classa1c"));

        }finally{
			if(jdbcConnection != null){
				jdbcConnection.close();
			}
		}
    }
}
