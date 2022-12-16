package ch.ehi.ili2fgdb;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Dataset23Smart1Test {
    String fgdbFileName="test/data/Dataset23Smart1/Dataset23Smart1.gdb";
	private static final String DATASETNAME_A = "Testset1";
	private static final String DATASETNAME_B = "Testset2";
	Connection jdbcConnection=null;

	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
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

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    File fgdbFile=new File(fgdbFileName);
		    Fgdb4j.deleteFileGdb(fgdbFile);
		    Class driverClass = Class.forName(FgdbDriver.class.getName());
	        jdbcConnection = DriverManager.getConnection(
	        		FgdbDriver.BASE_URL+fgdbFileName, null, null);
	        Statement stmt=jdbcConnection.createStatement();
			{
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),null,data.getPath()+".log");
	                Ili2db.setNoSmartMapping(config);
					config.setDatasetName(DATASETNAME_A);
					config.setFunction(Config.FC_IMPORT);
			        config.setDoImplicitSchemaImport(true);
					config.setCreateFk(config.CREATE_FK_YES);
					config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
					config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1b1.xtf");
					Config config=initConfig(data.getPath(),null,data.getPath()+".log");
					config.setDatasetName(DATASETNAME_B);
					config.setFunction(Config.FC_IMPORT);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	@Test
	public void importNoDatasetName() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    File fgdbFile=new File(fgdbFileName);
		    Fgdb4j.deleteFileGdb(fgdbFile);
		    Class driverClass = Class.forName(FgdbDriver.class.getName());
	        jdbcConnection = DriverManager.getConnection(
	        		FgdbDriver.BASE_URL+fgdbFileName, null, null);
	        Statement stmt=jdbcConnection.createStatement();
			{
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),null,data.getPath()+".log");
	                Ili2db.setNoSmartMapping(config);
					config.setDatasetName(null);
					config.setFunction(Config.FC_IMPORT);
			        config.setDoImplicitSchemaImport(true);
					config.setCreateFk(config.CREATE_FK_YES);
					config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
					config.setBasketHandling(null);
					config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),null,data.getPath()+".log");
					config.setDatasetName(null);
					config.setFunction(Config.FC_IMPORT);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
