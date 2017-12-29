package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
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
public class Array23Test {
	private static final String DBSCHEMA = "Array23";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
	Statement stmt=null;

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
	public void testJdbcArray() throws Exception
	{
	    Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
		stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        
		stmt.execute("CREATE SCHEMA "+DBSCHEMA);        
		stmt.execute("CREATE TABLE "+DBSCHEMA+".auto ("
				  +"T_Id SERIAL PRIMARY KEY"
				  +",farben varchar(255) ARRAY NULL"
				+")");
		PreparedStatement ps=jdbcConnection.prepareStatement("INSERT INTO "+DBSCHEMA+".auto (farben) VALUES (?)");
		java.sql.Array farben=jdbcConnection.createArrayOf("VARCHAR", new String[] {"rot","blau"});
		ps.setArray(1, farben);
		ps.executeUpdate();
		ps.close();
		ps=jdbcConnection.prepareStatement("SELECT farben FROM "+DBSCHEMA+".auto");
		farben=jdbcConnection.createArrayOf("VARCHAR", new String[] {"rot","blau"});
		ResultSet rs=ps.executeQuery();
		rs.next();
		farben=rs.getArray(1);
		System.out.println(((String[])farben.getArray())[0]);
		ps.close();
		
		stmt.close();
		jdbcConnection.close();
	}
	@Test
	public void importSmartCustom() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/Array/Array23a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultiLineTrafo(null);
			config.setArrayTrafo(config.ARRAY_TRAFO_COALESCE);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			exportSmartCustom();
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	//@Test
	public void exportSmartCustom() throws Exception {
		Connection jdbcConnection = null;
		try {
			Class driverClass = Class.forName("org.postgresql.Driver");
			jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
			stmt = jdbcConnection.createStatement();
			if (false) {
				stmt.execute("DROP SCHEMA IF EXISTS " + DBSCHEMA + " CASCADE");
				DbUtility.executeSqlScript(jdbcConnection,
						new java.io.FileReader("test/data/MultiSurface/CreateTableMultiSurface2a.sql"));
				DbUtility.executeSqlScript(jdbcConnection,
						new java.io.FileReader("test/data/MultiSurface/InsertIntoTableMultiSurface2a.sql"));
			}
			File data = new File("test/data/Array/Array23a-out.xtf");
			Config config = initConfig(data.getPath(), DBSCHEMA, data.getPath() + ".log");
			config.setModels("Array23");
			config.setFunction(Config.FC_EXPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
			XtfReader reader = new XtfReader(data);
			IoxEvent event = null;
			do {
				event = reader.read();
				if (event instanceof StartTransferEvent) {
				} else if (event instanceof StartBasketEvent) {
				} else if (event instanceof ObjectEvent) {
					IomObject iomObj = ((ObjectEvent) event).getIomObject();
					if (iomObj.getobjectoid() != null) {
						objs.put(iomObj.getobjectoid(), iomObj);
					}
				} else if (event instanceof EndBasketEvent) {
				} else if (event instanceof EndTransferEvent) {
				}
			} while (!(event instanceof EndTransferEvent));
			// check values of array
			{
				IomObject obj0 = objs.get("13");
				Assert.assertNotNull(obj0);
				Assert.assertEquals("Array23.TestA.Auto", obj0.getobjecttag());
				Assert.assertEquals(2,obj0.getattrvaluecount("Farben"));
				Assert.assertEquals("Rot",obj0.getattrobj("Farben", 0).getattrvalue("Wert"));
				Assert.assertEquals("Blau",obj0.getattrobj("Farben", 1).getattrvalue("Wert"));
			}
			{
				IomObject obj0 = objs.get("14");
				Assert.assertNotNull(obj0);
				Assert.assertEquals("Array23.TestA.Auto", obj0.getobjecttag());
				Assert.assertEquals(0,obj0.getattrvaluecount("Farben"));
			}
			{
				IomObject obj0 = objs.get("100");
				Assert.assertNotNull(obj0);
				Assert.assertEquals("Array23.TestA.Datatypes", obj0.getobjecttag());
				Assert.assertEquals(0,obj0.getattrvaluecount("aUuid"));
				Assert.assertEquals(0,obj0.getattrvaluecount("aBoolean"));
				Assert.assertEquals(0,obj0.getattrvaluecount("aTime"));
				Assert.assertEquals(0,obj0.getattrvaluecount("aDate"));
				Assert.assertEquals(0,obj0.getattrvaluecount("aDateTime"));
				Assert.assertEquals(0,obj0.getattrvaluecount("numericInt"));
				Assert.assertEquals(0,obj0.getattrvaluecount("numericDec"));
			}
			{
				IomObject obj0 = objs.get("101");
				Assert.assertNotNull(obj0);
				Assert.assertEquals("Array23.TestA.Datatypes", obj0.getobjecttag());
				Assert.assertEquals(1,obj0.getattrvaluecount("aUuid"));
				Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3",obj0.getattrobj("aUuid",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("aBoolean"));
				Assert.assertEquals("true",obj0.getattrobj("aBoolean",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("aTime"));
				Assert.assertEquals("09:00:00.000",obj0.getattrobj("aTime",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("aDate"));
				Assert.assertEquals("2002-09-24",obj0.getattrobj("aDate",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("aDateTime"));
				Assert.assertEquals("1900-01-01T12:30:05.000",obj0.getattrobj("aDateTime",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("numericInt"));
				Assert.assertEquals("5",obj0.getattrobj("numericInt",0).getattrvalue("Value"));
				Assert.assertEquals(1,obj0.getattrvaluecount("numericDec"));
				Assert.assertEquals("6.0",obj0.getattrobj("numericDec",0).getattrvalue("Value"));
			}
		} finally {
			if (jdbcConnection != null) {
				jdbcConnection.close();
			}
		}
	}
}