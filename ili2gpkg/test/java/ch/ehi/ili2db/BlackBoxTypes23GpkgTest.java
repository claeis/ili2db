package ch.ehi.ili2db;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.Statement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class BlackBoxTypes23GpkgTest {
    String gpkgFileName="test/data/BlackBoxTypes23/BlackBoxTypes23.gpkg";
	Connection jdbcConnection=null;
	Statement stmt=null;
	@Before
	public void initDb() throws Exception
	{
	    File gpkgFile=new File(gpkgFileName);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
	}
	public void openDb() throws Exception
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
	public void importIli() throws Exception
	{
		{
			File data=new File("test/data/BlackBoxTypes23/BlackBoxTypes23.ili");
			Config config=initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			openDb();
			{
				ResultSet rs=jdbcConnection.getMetaData().getColumns(null, null, "classa", "xmlbox");
				Assert.assertTrue(rs.next());
				Assert.assertEquals(java.sql.Types.VARCHAR,rs.getInt("DATA_TYPE"));
			}
			{
				ResultSet rs=jdbcConnection.getMetaData().getColumns(null, null, "classa", "binbox");
				Assert.assertTrue(rs.next());
				Assert.assertEquals(java.sql.Types.VARCHAR,rs.getInt("DATA_TYPE"));
			}
		}
	}
	@Test
	public void importXtf() throws Exception
	{
		{
			File data=new File("test/data/BlackBoxTypes23/BlackBoxTypes23a.xtf");
			Config config=initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
		exportXtf();
	}
	
	//@Test
	public void exportXtf() throws Exception
	{
		File data=new File("test/data/BlackBoxTypes23/BlackBoxTypes23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("BlackBoxTypes23");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}
