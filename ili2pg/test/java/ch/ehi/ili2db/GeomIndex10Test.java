package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.generator.SqlConfiguration;

public class GeomIndex10Test {
	private static final String DBSCHEMA = "GeomIndex10";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd");
	Connection jdbcConnection=null;
	Statement stmt=null;
	
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
	public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File("test/data/GeomIndex/GeomIndex10.ili");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setValue(SqlConfiguration.CREATE_GEOM_INDEX,"True");
			config.setMaxSqlNameLength("20");
            Ili2db.setSkipPolygonBuilding(config);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			{
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"GeomIndex10.Topic.FlaechenTable2.surface._geom",    "_geom", "flaechentable2_suace",null},
                        {"GeomIndex10.Topic.FlaechenTable2.surface._ref", "_ref",  "flaechentable2_suace",null},
                        {"GeomIndex10.Topic.FlaechenTable.surface._geom", "_geom", "flaechentable_surace",null},
                        {"GeomIndex10.Topic.FlaechenTable.surface._ref",  "_ref",  "flaechentable_surace",null},
                        {"GeomIndex10.Topic.flaace__geom_idx.dy", "dy",    "flaace__geom_idx",null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"GeomIndex10.Topic.FlaechenTable2",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"GeomIndex10.Topic.FlaechenTable", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"GeomIndex10.Topic.flaace__geom_idx", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}