package ch.ehi.ili2db;

import java.io.File;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

public class Inheritance1Test {
	private static final String INHERITANCE1_SMART1 = "Inheritance1_smart1";
	private static final String INHERITANCE1_NOSMART = "Inheritance1_nosmart";
	String dbhost=null;
	String dbport=null;
	String dbname="ili2db";
	String dbuser="postgres";
	String dbpwd="ola2011";

	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);
		
		
		config.setDbhost(dbhost);
		config.setDbport(dbport);
		config.setDbdatabase(dbname);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setDburl(getDbUrlConverter().makeUrl(config));


		config.setXtffile(xtfFilename);
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
		
	}
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				    * jdbc:postgresql:database
				    * jdbc:postgresql://host/database
				    * jdbc:postgresql://host:port/database
				    */
				if(config.getDbdatabase()!=null){
					if(config.getDbhost()!=null){
						if(config.getDbport()!=null){
							return "jdbc:postgresql://"+config.getDbhost()+":"+config.getDbport()+"/"+config.getDbdatabase();
						}
						return "jdbc:postgresql://"+config.getDbhost()+"/"+config.getDbdatabase();
					}
					return "jdbc:postgresql:"+config.getDbdatabase();
				}
				return null;
			}
		};
	}

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importSmart1() throws Ili2dbException
	{
		File data=new File("test/data/Inheritance1a.xtf");
		Config config=initConfig(data.getPath(),INHERITANCE1_SMART1,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void exportSmart1() throws Ili2dbException
	{
		File data=new File("test/data/Inheritance1a-smartOut.xtf");
		Config config=initConfig(data.getPath(),INHERITANCE1_SMART1,data.getPath()+".log");
		config.setModels("Inheritance1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importNoSmart() throws Ili2dbException
	{
		File data=new File("test/data/Inheritance1a.xtf");
		Config config=initConfig(data.getPath(),INHERITANCE1_NOSMART,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void exportNoSmart() throws Ili2dbException
	{
		File data=new File("test/data/Inheritance1a-noSmartOut.xtf");
		Config config=initConfig(data.getPath(),INHERITANCE1_NOSMART,data.getPath()+".log");
		config.setModels("Inheritance1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}
