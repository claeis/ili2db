package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CatalogueObjectsTest {
	private static final String DBSCHEMA = "CatalogueObjects1";
	private static final String DATASETNAME = "Testset1";
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
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
		
	}

	@Test
	public void importIliSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

			File data=new File("test/data/CatalogueObjects/CatalogueObjects1.ili");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
			config.setDatasetName(DATASETNAME);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			config.setCreatescript(data.getPath()+".sql");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
	@Test
	public void importXtfSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

			File data=new File("test/data/CatalogueObjects/CatalogueObjects1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
			config.setDatasetName(DATASETNAME);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
			config.setCreatescript(data.getPath()+".sql");
			//config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
		exportXtfSmart1();
	}
	//@Test
	public void exportXtfSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);

			File data=new File("test/data/CatalogueObjects/CatalogueObjects1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			//config.setDatasetName(DATASETNAME);
			config.setBaskets("CatalogueObjects1.TopicB.1");
			//config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}

}
