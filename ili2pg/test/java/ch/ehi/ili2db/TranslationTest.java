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
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.repository.DbTableName;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class TranslationTest {
	private static final String DBSCHEMA = "Translation";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
	Statement stmt=null;
	@Before
	public void initDb() throws Exception
	{
	    Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(
        		dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
	}
	@After
	public void endDb() throws Exception
	{
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
	public void importIli23() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
		File data=new File("test/data/Translation/EnumOk.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setVer4_translation(true);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importIli10() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Translation/ModelBsimple10.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importIli10lineTable() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/Translation/ModelBsimple10.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setDoItfLineTables(true);
		config.setAreaRef(config.AREA_REF_KEEP);
		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importXtf23() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        {
    		File data=new File("test/data/Translation/EnumOka.xtf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setCreateFk(config.CREATE_FK_YES);
    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
    		config.setCatalogueRefTrafo(null);
    		config.setMultiSurfaceTrafo(null);
    		config.setMultilingualTrafo(null);
    		config.setInheritanceTrafo(null);
    		config.setVer4_translation(true);
    		config.setDatasetName("EnumOka");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        exportXtf23();
	}
	public void exportXtf23() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		File data=new File("test/data/Translation/EnumOka-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setDatasetName("EnumOka");
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importItf10() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        {
    		File data=new File("test/data/Translation/ModelAsimple10a.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setCreateFk(config.CREATE_FK_YES);
    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
    		config.setCatalogueRefTrafo(null);
    		config.setMultiSurfaceTrafo(null);
    		config.setMultilingualTrafo(null);
    		config.setInheritanceTrafo(null);
    		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
    		config.setDatasetName("ModelAsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        {
    		File data=new File("test/data/Translation/ModelBsimple10a.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setDatasetName("ModelBsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        exportItf10();
	}
	@Test
	public void importItf10lineTable() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        {
    		File data=new File("test/data/Translation/ModelAsimple10a.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setCreateFk(config.CREATE_FK_YES);
    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
    		config.setCatalogueRefTrafo(null);
    		config.setMultiSurfaceTrafo(null);
    		config.setMultilingualTrafo(null);
    		config.setInheritanceTrafo(null);
    		config.setDoItfLineTables(true);
    		config.setAreaRef(config.AREA_REF_KEEP);
    		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
    		config.setDatasetName("ModelAsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        {
    		File data=new File("test/data/Translation/ModelBsimple10a.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setDatasetName("ModelBsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        exportItf10lineTable();
	}
	
	//@Test
	public void exportItf10() throws Exception
	{
        {
    		File data=new File("test/data/Translation/ModelAsimple10a-out.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_EXPORT);
    		config.setDatasetName("ModelAsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        {
    		File data=new File("test/data/Translation/ModelBsimple10a-out.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_EXPORT);
    		config.setDatasetName("ModelBsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
	}
	//@Test
	public void exportItf10lineTable() throws Exception
	{
        {
    		File data=new File("test/data/Translation/ModelAsimple10a-out.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_EXPORT);
    		config.setDatasetName("ModelAsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
        {
    		File data=new File("test/data/Translation/ModelBsimple10a-out.itf");
    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
    		config.setFunction(Config.FC_EXPORT);
    		config.setDatasetName("ModelBsimple10");
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
	}
	
}
