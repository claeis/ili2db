package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CreateFK23GpkgTest {
	private static final String TEST_OUT="test/data/CreateFK23/";
	private String gpkgFileName=TEST_OUT+"test.gpkg";
	Connection jdbcConnection=null;
	Statement stmt=null;
	
	public void initDb() throws Exception
	{
	    Class driverClass = Class.forName("org.sqlite.JDBC");
        jdbcConnection = DriverManager.getConnection(
        		"jdbc:sqlite:"+gpkgFileName, null, null);
        stmt=jdbcConnection.createStatement();
	}
	
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);
		
		config.setDbfile(gpkgFileName);
		config.setDburl("jdbc:sqlite:"+gpkgFileName);
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
	public void importIli_CreateFK() throws Exception
	{
		File gpkgFile=new File(TEST_OUT+"test.gpkg");
		if(gpkgFile.exists()){
			Assert.assertTrue(gpkgFile.delete());
		}
		File data=new File(TEST_OUT+"model1.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		EhiLogger.getInstance().setTraceFilter(false);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setCreatescript(TEST_OUT+"importIli_CreateFK.sql");
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}