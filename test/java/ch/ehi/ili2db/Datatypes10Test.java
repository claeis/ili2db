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
public class Datatypes10Test {
	private static final String DBSCHEMA = "Datatypes10";
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
	public void importIli() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
		File data=new File("test/data/Datatypes10/Datatypes10.ili");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
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

		{
			String stmtTxt="SELECT numeric_precision,numeric_scale FROM information_schema.columns WHERE table_schema ='datatypes10' AND table_name = 'table' AND column_name = 'dim1'";
			Assert.assertTrue(stmt.execute(stmtTxt));
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals(2,rs.getInt(1));
			Assert.assertEquals(1,rs.getInt(2));
		}
		{
			String stmtTxt="SELECT numeric_precision,numeric_scale FROM information_schema.columns WHERE table_schema ='datatypes10' AND table_name = 'table' AND column_name = 'dim2'";
			Assert.assertTrue(stmt.execute(stmtTxt));
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals(2,rs.getInt(1));
			Assert.assertEquals(1,rs.getInt(2));
		}
		
	}

	@Test
	public void importItf() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
		File data=new File("test/data/Datatypes10/Datatypes10a.itf");
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

		String stmtTxt="SELECT atext FROM "+DBSCHEMA+".table AS a INNER JOIN "+DBSCHEMA+".subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
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
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setModels("Datatypes10");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
}
