package ch.ehi.ili2db;

import java.io.File;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class MultisurfaceTest {
	private static final String DBSCHEMA_NOSMART = "MultiSurfaceNoSmart";
	private static final String DBSCHEMA_SMART = "MultiSurfaceSmart";
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
	public void importNoSmartChbase() throws Ili2dbException
	{
		{
			File data=new File("test/data/MultiSurface1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA_NOSMART,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportNoSmartChbase() throws Ili2dbException
	{
		File data=new File("test/data/MultiSurface1a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA_NOSMART,data.getPath()+".log");
		config.setModels("MultiSurface1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importSmartChbase() throws Ili2dbException
	{
		{
			File data=new File("test/data/MultiSurface1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA_SMART,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportSmartChbase() throws Ili2dbException
	{
		File data=new File("test/data/MultiSurface1a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA_SMART,data.getPath()+".log");
		config.setModels("MultiSurface1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importSmartCustom() throws Ili2dbException
	{
		{
			File data=new File("test/data/MultiSurface2a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA_SMART,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportSmartCustom() throws Ili2dbException
	{
		File data=new File("test/data/MultiSurface2a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA_SMART,data.getPath()+".log");
		config.setModels("MultiSurface2");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}
