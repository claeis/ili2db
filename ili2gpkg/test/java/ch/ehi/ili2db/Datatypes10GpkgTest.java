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
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes10GpkgTest {
    String gpkgFileName="test/data/Datatypes10/Datatypes10.gpkg";
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
	public void importIli() throws Exception
	{
	    File gpkgFile=new File(gpkgFileName);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
		File data=new File("test/data/Datatypes10/Datatypes10.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
	}

	@Test
	public void importItf() throws Exception
	{
	    File gpkgFile=new File(gpkgFileName);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
        
		File data=new File("test/data/Datatypes10/Datatypes10a.itf");
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

		initDb();
		String stmtTxt="SELECT atext FROM tablea AS a INNER JOIN subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
		Assert.assertTrue(stmt.execute(stmtTxt));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals("obj11",rs.getObject(1));
		}
		exportItf();
	}
	
	//@Test
	public void exportItf() throws Ili2dbException
	{
		File data=new File("test/data/Datatypes10/Datatypes10a-out.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("Datatypes10");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}

	@Test
	public void importItfWithSkipPolygonBuilding() throws Exception
	{
	    File gpkgFile=new File(gpkgFileName);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
        
		File data=new File("test/data/Datatypes10/Datatypes10a.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setDoItfLineTables(true);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);

		initDb();
		String stmtTxt="SELECT atext FROM tablea AS a INNER JOIN subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
		Assert.assertTrue(stmt.execute(stmtTxt));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals("obj11",rs.getObject(1));
		}
		exportItfWithSkipPolygonBuilding();
	}
	
	//@Test
	public void exportItfWithSkipPolygonBuilding() throws Ili2dbException
	{
		File data=new File("test/data/Datatypes10/Datatypes10a-ltout.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("Datatypes10");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
}
