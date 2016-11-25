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
public class MultisurfaceTest {
	private static final String DBSCHEMA = "MultiSurface";
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
	public void importNoSmartChbase() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
		File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
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

		Assert.assertTrue(stmt.execute("SELECT surfacestructure.surface,point FROM "+DBSCHEMA+".classa1 INNER JOIN "+DBSCHEMA+".multisurface ON (classa1.t_id=multisurface.classa1_geom) INNER JOIN "+DBSCHEMA+".surfacestructure ON (multisurface.t_id=surfacestructure.multisurface_surfaces) WHERE classa1.t_ili_tid='o1'"));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertNotNull(rs.getObject(1));
			Assert.assertNotNull(rs.getObject(2));
		}
		
		exportNoSmartChbase();
	}
	
	//@Test
	public void exportNoSmartChbase() throws Ili2dbException
	{
		File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setModels("MultiSurface1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importSmartChbase() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
		Assert.assertTrue(stmt.execute("SELECT geom,point FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid='o1'"));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertNotNull(rs.getObject(1));
			Assert.assertNotNull(rs.getObject(2));
		}
		
		exportSmartChbase();
	}
	
	//@Test
	public void exportSmartChbase() throws Exception
	{
		File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setModels("MultiSurface1");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importSmartCustom() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/MultiSurface/MultiSurface2a.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);

		Assert.assertTrue(stmt.execute("SELECT geom FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid='13'"));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertNotNull(rs.getObject(1));
		}
		
		exportSmartCustom();
	}
	
	//@Test
	public void exportSmartCustom() throws Exception
	{
		File data=new File("test/data/MultiSurface/MultiSurface2a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setModels("MultiSurface2");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	@Test
	public void importSmartChbaseSingleGeom() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
		config.setOneGeomPerTable(true);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
		Assert.assertTrue(stmt.execute("SELECT geom,point FROM "+DBSCHEMA+".classa1 INNER JOIN "+DBSCHEMA+".classa1_point ON (classa1.t_id=classa1_point.t_id) WHERE classa1.t_ili_tid='o1'"));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertNotNull(rs.getObject(1));
			Assert.assertNotNull(rs.getObject(2));
		}
		
		exportSmartChbaseSingleGeom();
	}
	//@Test
	public void exportSmartChbaseSingleGeom() throws Exception
	{
        //stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
		File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setModels("MultiSurface1");
		config.setFunction(Config.FC_EXPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(config.MULTISURFACE_TRAFO_COALESCE);
		config.setOneGeomPerTable(true);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}
