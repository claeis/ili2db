package ch.ehi.ili2pg;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;

import org.junit.Test;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class SRSVerificationTest {

	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	
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
	public void verifySRS_True() throws Exception
	{
	    final String DBSCHEMA = "srsVerifySchema_verifySRS";
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/SRSVerification/ModelA.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setDbschema(DBSCHEMA);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setDefaultSrsAuthority("EPSG");
				config.setDefaultSrsCode("2000");
				Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void srsAuthNotExist_False() throws Exception
	{
        final String DBSCHEMA = "srsVerifySchema_srsAuthNotExist";
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/SRSVerification/ModelA.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setDbschema(DBSCHEMA);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setDefaultSrsAuthority("TPSG");
				config.setDefaultSrsCode("2000");
				Ili2db.readSettingsFromDb(config);
				try{
					Ili2db.run(config,null);
					fail();
				} catch (Ili2dbException e) {
					Assert.assertEquals("TPSG/2000 does not exist", e.getMessage());
				}
			} 
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void srsCodeNotExist_False() throws Exception
	{
        final String DBSCHEMA = "srsVerifySchema_srsCodeNotExist";
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/SRSVerification/ModelA.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setDbschema(DBSCHEMA);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setDefaultSrsAuthority("EPSG");
				config.setDefaultSrsCode("999999999");
				Ili2db.readSettingsFromDb(config);
				try{
					Ili2db.run(config,null);
					fail();
				} catch (Ili2dbException e) {
					Assert.assertEquals("EPSG/999999999 does not exist", e.getMessage());
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void srsAuthLowerCase_False() throws Exception
	{
        final String DBSCHEMA = "srsVerifySchema_srsAuthLowerCase";
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/SRSVerification/ModelA.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setDbschema(DBSCHEMA);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setDefaultSrsAuthority("epsg");
				config.setDefaultSrsCode("2000");
				Ili2db.readSettingsFromDb(config);
				try{
					Ili2db.run(config,null);
					fail();
				} catch (Ili2dbException e) {
					Assert.assertEquals("epsg/2000 does not exist", e.getMessage());
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}