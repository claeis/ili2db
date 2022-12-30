package ch.ehi.ili2pg;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.interlis.iox.IoxException;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CrsTest {
	private static final String DBSCHEMA = "Crs";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
	Statement stmt=null;
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
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
	private int getColumnCrsId(String schemaName, String tableName, String attrName, Connection db) throws SQLException  {
		String queryBuild="SELECT srid FROM geometry_columns WHERE "
		+(schemaName!=null?"f_table_schema='"+schemaName.toLowerCase()+"' AND ":"")
		+"f_table_name='"+tableName.toLowerCase()+"' " 
		+"AND f_geometry_column='"+attrName.toLowerCase()+"'";
		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt = db.createStatement();
			rs=stmt.executeQuery(queryBuild);
			if(!rs.next()) {
				throw new SQLException("no column in given table");
			}
			int crs=rs.getInt(1);
			return crs;
		}finally {
			if(rs!=null) {
				rs.close();
				rs=null;
			}
			if(stmt!=null) {
				stmt.close();
				stmt=null;
			}
		}
	}
	
	@Test
	public void importIliCoord() throws Exception
	{
	    Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
		stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Crs/CrsCoord23.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsAuthority("EPSG");
		config.setDefaultSrsCode("4326");
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		assertEquals(21781,getColumnCrsId(DBSCHEMA, "classa1", "attrLV03", jdbcConnection));
		assertEquals(2056,getColumnCrsId(DBSCHEMA, "classa1", "attrLV95", jdbcConnection));
        assertEquals(4326,getColumnCrsId(DBSCHEMA, "classa1", "attrWGS84", jdbcConnection));
        {
            // t_ili2db_attrname
            String [][] expectedValues=new String[][] {
                {"CrsCoord23.TestA.ClassA1.attrLV03", "attrlv03",  "classa1", null},   
                {"CrsCoord23.TestA.ClassA1.attrLV95", "attrlv95",  "classa1", null},
                {"CrsCoord23.TestA.ClassA1.attrWGS84", "attrwgs84",  "classa1", null},
                {"CrsCoord23.TestA.ClassA1.attr1",    "attr1", "classa1", null},
            };
            Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
        }
        {
            // t_ili2db_trafo
            String [][] expectedValues=new String[][] {
                {"CrsCoord23.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"}
            };
            Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
        }
	}
    @Test
    public void importIliCoordNoDefault_Fail() throws Exception
    {
        try {
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            File data=new File("test/data/Crs/CrsCoord23.ili");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
            //config.setDefaultSrsAuthority("EPSG"); no default to check fail behavior
            //config.setDefaultSrsCode("4326");
            //Ili2db.readSettingsFromDb(config);
            try {
                Ili2db.run(config,null); // should throw an exception
                fail();
            }catch(Ili2dbException ex) {
                assertEquals("no CRS for attribute CrsCoord23.TestA.ClassA1.attrWGS84",ex.getMessage());
            }
        }finally {
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    @Test
    public void importIliNoCoord() throws Exception
    {
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        File data=new File("test/data/Crs/CrsNoCoord23.ili");
        Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        //Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        {
            // t_ili2db_attrname
            String [][] expectedValues=new String[][] {
                {"CrsNoCoord23.TestA.ClassA1.attr1",    "attr1", "classa1", null},
            };
            Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
        }
        {
            // t_ili2db_trafo
            String [][] expectedValues=new String[][] {
                {"CrsNoCoord23.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"}
            };
            Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
        }
    }
	@Test
	public void importXtfCoord() throws Exception
	{
	    Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
		stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File("test/data/Crs/CrsCoord23a.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsAuthority("EPSG");
		config.setDefaultSrsCode("4326");
		//Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
    @Test
    public void importXtfCoordNoDefault_Fail() throws Exception
    {
        try {
            
            try {
                Class driverClass = Class.forName("org.postgresql.Driver");
                jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
                stmt=jdbcConnection.createStatement();
                stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
                
                //EhiLogger.getInstance().setTraceFilter(false);
                File data=new File("test/data/Crs/CrsCoord23a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setCreateNumChecks(true);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                //config.setDefaultSrsAuthority("EPSG");
                //config.setDefaultSrsCode("4326");
                //Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null); // should throw an exception
                fail();
            }catch(Ili2dbException ex) {
                assertEquals("no CRS for attribute CrsCoord23.TestA.ClassA1.attrWGS84",ex.getMessage());
            }
        }finally {
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    @Test
    public void importXtfNoCoord() throws Exception
    {
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File("test/data/Crs/CrsNoCoord23a.xtf");
        Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        //Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
}
