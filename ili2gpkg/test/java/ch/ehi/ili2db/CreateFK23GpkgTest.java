package ch.ehi.ili2db;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;

public class CreateFK23GpkgTest {
	
	private static final String TEST_OUT="test/data/CreateFK23/";
    private static final String GPKGFILENAME=TEST_OUT+"test.gpkg";
	private Connection jdbcConnection=null;
	
	public void initDb() throws Exception
	{
	    Class driverClass = Class.forName("org.sqlite.JDBC");
        jdbcConnection = DriverManager.getConnection("jdbc:sqlite:"+GPKGFILENAME, null, null);
        Statement stmt=jdbcConnection.createStatement();
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
		config.setDbfile(GPKGFILENAME);
		config.setDburl("jdbc:sqlite:"+GPKGFILENAME);
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
		EhiLogger.getInstance().setTraceFilter(false);
		File gpkgFile=new File(GPKGFILENAME);
		if(gpkgFile.exists()){
			gpkgFile.delete();
		}
		File data=new File(TEST_OUT,"model1.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setCreatescript(TEST_OUT+"importIli_CreateFK.sql");
		Ili2db.readSettingsFromDb(config);
		try {
            Ili2db.run(config,null);
            fail();
        } catch (Exception e) {
        	e.printStackTrace();
            Assert.assertEquals("loop in create table statements: classa1->classb1->classa1", e.getCause().getMessage());
        }
	}
}