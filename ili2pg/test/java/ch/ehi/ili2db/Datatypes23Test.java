package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes23Test {
    private static final String DBSCHEMA = "Datatypes23";
    String dburl=System.getProperty("dburl"); 
    String dbuser=System.getProperty("dbusr");
    String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
    Statement stmt=null;
	@After
	public void endDb() throws Exception
	{
	    if(stmt!=null) {
	        stmt.close();
	        stmt=null;
	    }
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
	
	@Test
	public void importIli() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
	}
	@Test
	public void importXtfAttr() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Attr.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		exportXtfAttr();
	}
	@Test
	public void importXtfLine() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Line.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		exportXtfLine();
	}
	@Test
	public void importXtfSurface() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Surface.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		exportXtfSurface();
	}
	//@Test
	public void exportXtfLine() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        //stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Line-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		
	}
	//@Test
	public void exportXtfSurface() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        //stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Surface-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		config.setBasketHandling(null);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		
	}
	//@Test
	public void exportXtfAttr() throws Exception
	{
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        //stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Datatypes23/Datatypes23Attr-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		
	}
}
