package ch.ehi.ili2fgdb;

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

public class MultilineTest {
    String fgdbFileName="test/data/MultiLine/MultiLine.gdb";
	Connection jdbcConnection=null;
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2fgdb.FgdbMain().initConfig(config);
		
		
		config.setDbfile(fgdbFileName);
		config.setDburl(FgdbDriver.BASE_URL+fgdbFileName);
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
	    File fgdbFile=new File(fgdbFileName);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File("test/data/MultiLine/MultiLine2.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
	}
	@Test
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(fgdbFileName);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File("test/data/MultiLine/MultiLine2b.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		
	}
	@Test
	public void exportXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(fgdbFileName);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File("test/data/Simple/Simple23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Simple23");
		Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		
	}
}
