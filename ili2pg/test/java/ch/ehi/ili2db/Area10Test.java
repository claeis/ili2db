package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Area10Test {
	private static final String DBSCHEMA = "Area10";
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
	public void importAreaOk() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {        
				File data=new File("test/data/Area10/Beispiel1a.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT boflaechen.t_id FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("4",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT boflaechen.form FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("010A000020155500000100000001090000000100000001020000000500000000000000F7EB4241000000000E2D304100000000F7EB4241000000002C2D304100000000FCEB4241000000002C2D304100000000FCEB4241000000000E2D304100000000F7EB4241000000000E2D3041",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importOpenPolygonWithValidation() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/Area10/Beispiel1b.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setSkipGeometryErrors(true);
				config.setValidation(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT boflaechen.t_id FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("4",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.iliname FROM "+DBSCHEMA+".t_ili2db_attrname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Beispiel1.Bodenbedeckung.BoFlaechen.Art",rs.getString(1));
				}
		    }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importOpenPolygonWithoutValidation() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        { 
				File data=new File("test/data/Area10/Beispiel1b.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setSkipGeometryErrors(true);
				config.setValidation(false);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT boflaechen.t_id FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("4",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.iliname FROM "+DBSCHEMA+".t_ili2db_attrname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Beispiel1.Bodenbedeckung.BoFlaechen.Art",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
}