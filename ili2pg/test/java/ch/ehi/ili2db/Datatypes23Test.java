package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes23Test {
    private static final String DBSCHEMA = "Datatypes23";
    private static final String TEST_OUT = "test/data/Datatypes23/";
    private static final String CREATETABLES_ATTRS = TEST_OUT+"CreateTables.sql";
    private static final String INSERTINTOTABLES_ATTRS = TEST_OUT+"InsertIntoTables.sql";
    private static final String CREATETABLES_LINES = TEST_OUT+"CreateTables_Lines.sql";
    private static final String INSERTINTOTABLES_LINES = TEST_OUT+"InsertIntoTables_Lines.sql";
    private static final String CREATETABLES_SURFACES = TEST_OUT+"CreateTables_Surface.sql";
    private static final String INSERTINTOTABLES_SURFACES = TEST_OUT+"InsertIntoTables_Surface.sql";
    
    String dburl=System.getProperty("dburl"); 
    String dbuser=System.getProperty("dbusr");
    String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
    Statement stmt=null;
	@After
	public void endDb() throws Exception
	{
	    if(stmt!=null) {
	        stmt.close();
	        stmt=null;
	    }
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
	public void importIli() throws Exception{
		try {
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File(TEST_OUT+"Datatypes23.ili");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			//Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Datatypes23.Topic.SimpleSurface3.surface3d", "surface3d", "simplesurface3", null},
                    {"Datatypes23.Topic.ClassAttr.vertAlignment", "vertalignment", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aTime", "atime", "classattr", null},
                    {"Datatypes23.Topic.SimpleSurface2.surface2d", "surface2d", "simplesurface2", null},
                    {"Datatypes23.Topic.Surface2.surfacearcs2d", "surfacearcs2d", "surface2", null},
                    {"Datatypes23.Topic.ClassAttr.aUuid", "auuid", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aClass", "aclass", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.uritext", "uritext", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.horizAlignment", "horizalignment", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aI32id", "ai32id", "classattr", null},
                    {"Datatypes23.Topic.Line3.straightsarcs3d", "straightsarcs3d", "line3", null},
                    {"Datatypes23.Topic.ClassAttr.mtextLimited", "mtextlimited", "classattr", null},
                    {"Datatypes23.Topic.SimpleLine3.straights3d", "straights3d", "simpleline3", null},
                    {"Datatypes23.Topic.ClassAttr.aAttribute", "aattribute", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aBoolean", "aboolean", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aDateTime", "adatetime", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.textLimited", "textlimited", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.nametext", "nametext", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aufzaehlung", "aufzaehlung", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.xmlbox", "xmlbox", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aStandardid", "astandardid", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.aDate", "adate", "classattr", null},
                    {"Datatypes23.Topic.ClassKoord3.hcoord", "hcoord", "classkoord3", null},
                    {"Datatypes23.Topic.ClassKoord2.lcoord", "lcoord", "classkoord2", null},
                    {"Datatypes23.Topic.ClassAttr.numericInt", "numericint", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.numericDec", "numericdec", "classattr", null},
                    {"Datatypes23.Topic.SimpleLine2.straights2d", "straights2d", "simpleline2", null},
                    {"Datatypes23.Topic.Line2.straightsarcs2d", "straightsarcs2d", "line2", null},
                    {"Datatypes23.Topic.ClassAttr.textUnlimited", "textunlimited", "classattr", null},
                    {"Datatypes23.Topic.ClassAttr.binbox", "binbox", "classattr", null},
                    {"Datatypes23.Topic.Surface3.surfacearcs3d", "surfacearcs3d", "surface3", null},
                    {"Datatypes23.Topic.ClassAttr.mtextUnlimited", "mtextunlimited", "classattr", null}
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Datatypes23.Topic.SimpleLine3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.SimpleSurface3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.Surface2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.SimpleSurface2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.Surface3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.ClassAttr", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.ClassKoord3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.Line3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.ClassKoord2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.SimpleLine2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Datatypes23.Topic.Line2", "ch.ehi.ili2db.inheritance", "newClass"}
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
            }
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfAttr() throws Exception{
		try {
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File(TEST_OUT+"Datatypes23Attr.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			//Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			String stmtTxt="SELECT * FROM "+DBSCHEMA+".classattr ORDER BY t_id ASC";
			{
				 Assert.assertTrue(stmt.execute(stmtTxt));
				 ResultSet rs=stmt.getResultSet();
				 Assert.assertTrue(rs.next());
				 Assert.assertEquals("22", rs.getString("aI32id"));
				 Assert.assertEquals("t", rs.getString("aBoolean"));
				 Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", rs.getString("aUuid"));
				 Assert.assertEquals("abc100", rs.getString("textLimited"));
				 Assert.assertEquals("Left", rs.getString("horizAlignment"));
				 Assert.assertEquals("mailto:ceis@localhost", rs.getString("uritext"));
				 Assert.assertEquals("5", rs.getString("numericInt"));
				 Assert.assertEquals("<x>\n" + 
				 		"							<a></a>\n" + 
				 		"						</x>", rs.getString("xmlbox"));
				 Assert.assertEquals("mehr.vier", rs.getString("aufzaehlung"));
				 Assert.assertEquals("09:00:00", rs.getString("aTime"));
				 Assert.assertEquals("abc200\n" + 
				 		"end200", rs.getString("mtextLimited"));
				 Assert.assertEquals("chgAAAAAAAAA0azD", rs.getString("aStandardid"));
				 Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", rs.getString("aAttribute"));
				 Assert.assertEquals("2002-09-24", rs.getString("aDate"));
				 Assert.assertEquals("Top", rs.getString("vertAlignment"));
				 Assert.assertEquals("ClassA", rs.getString("nametext"));
				 Assert.assertEquals("abc101", rs.getString("textUnlimited"));
				 Assert.assertEquals("6.0", rs.getString("numericDec"));
				 Assert.assertEquals("abc201\n" +
				 		"end201", rs.getString("mtextUnlimited"));
				 Assert.assertEquals("1900-01-01 12:30:05", rs.getString("aDateTime"));
			}
			{
				// byte array
				String stmtT="SELECT binbox FROM "+DBSCHEMA+".classattr ORDER BY t_id ASC";
				Assert.assertTrue(stmt.execute(stmtT));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				byte[] bytes=(byte[])rs.getObject("binbox");
				Assert.assertFalse(rs.wasNull());
				String wkbText=javax.xml.bind.DatatypeConverter.printBase64Binary(bytes);
				Assert.assertEquals("AAAA", wkbText);
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

	@Test
	public void importXtfLine() throws Exception{
		try {
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File(TEST_OUT+"Datatypes23Line.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			//Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// imported polyline
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straightsarcs2d) FROM datatypes23.line2 WHERE t_ili_tid = 'Line2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straightsarcs2d) FROM datatypes23.line2 WHERE t_ili_tid = 'Line2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;COMPOUNDCURVE(CIRCULARSTRING(2460001 1045001,2460005 1045004,2460006 1045006),(2460006 1045006,2460010 1045010))", rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straightsarcs3d) FROM datatypes23.line3 WHERE t_ili_tid = 'Line3.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;COMPOUNDCURVE(CIRCULARSTRING(2460001 1045001 300,2460005 1045004 0,2460006 1045006 300),(2460006 1045006 300,2460010 1045010 300))", rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straights2d) FROM datatypes23.simpleline2 WHERE t_ili_tid = 'SimpleLine2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straights2d) FROM datatypes23.simpleline2 WHERE t_ili_tid = 'SimpleLine2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;COMPOUNDCURVE((2460001 1045001,2460010 1045010))", rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(straights3d) FROM datatypes23.simpleline3 WHERE t_ili_tid = 'SimpleLine3.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;COMPOUNDCURVE((2460001 1045001 300,2460010 1045010 300))", rs.getObject(1));
				}
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfSurface() throws Exception
	{
		try {
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			File data=new File(TEST_OUT+"Datatypes23Surface.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			//Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// imported surface
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surfacearcs2d) FROM datatypes23.surface2 WHERE t_ili_tid = 'Surface2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surfacearcs2d) FROM datatypes23.surface2 WHERE t_ili_tid = 'Surface2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;CURVEPOLYGON(COMPOUNDCURVE((2460001 1045001,2460020 1045015),CIRCULARSTRING(2460020 1045015,2460010 1045018,2460001 1045015),(2460001 1045015,2460001 1045001)),COMPOUNDCURVE((2460005 1045005,2460010 1045010),CIRCULARSTRING(2460010 1045010,2460007 1045009,2460005 1045010),(2460005 1045010,2460005 1045005)))", rs.getObject(1));//2460001 1045001
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surface2d) FROM datatypes23.simplesurface2 WHERE t_ili_tid = 'SimpleSurface2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surface2d) FROM datatypes23.simplesurface2 WHERE t_ili_tid = 'SimpleSurface2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;CURVEPOLYGON(COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surface2d) FROM datatypes23.simplesurface2 WHERE t_ili_tid = 'SimpleSurface2.2';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;CURVEPOLYGON(COMPOUNDCURVE((2460001 1045001,2460020 1045015,2460001 1045015,2460001 1045001)),COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_asewkt(surface2d) FROM datatypes23.simplesurface2 WHERE t_ili_tid = 'SimpleSurface2.3';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals("SRID=21781;CURVEPOLYGON(COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", rs.getObject(1));
				}
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfAttr() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(CREATETABLES_ATTRS));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(INSERTINTOTABLES_ATTRS));
			
			File data=new File(TEST_OUT+"Datatypes23Attr-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setModels(DBSCHEMA);
			config.setBasketHandling(null);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			XtfReader reader=new XtfReader(data);
			IoxEvent event=null;
			 do{
		        event=reader.read();
		        if(event instanceof StartTransferEvent){
		        }else if(event instanceof StartBasketEvent){
		        }else if(event instanceof ObjectEvent){
		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
		        	if(iomObj.getobjectoid()!=null){
			        	objs.put(iomObj.getobjectoid(), iomObj);
		        	}
		        }else if(event instanceof EndBasketEvent){
		        }else if(event instanceof EndTransferEvent){
		        }
			 }while(!(event instanceof EndTransferEvent));
			 {
				 IomObject obj1 = objs.get("ClassAttr.1");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes23.Topic.ClassAttr", obj1.getobjecttag());
				 // datatypes23
				 Assert.assertEquals("22", obj1.getattrvalue("aI32id"));
				 Assert.assertEquals("true", obj1.getattrvalue("aBoolean"));
				 Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", obj1.getattrvalue("aUuid"));
				 Assert.assertEquals("abc100", obj1.getattrvalue("textLimited"));
				 Assert.assertEquals("Left", obj1.getattrvalue("horizAlignment"));
				 Assert.assertEquals("mailto:ceis@localhost", obj1.getattrvalue("uritext"));
				 Assert.assertEquals("5", obj1.getattrvalue("numericInt"));
				 Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><x>\n" + 
				 		"							<a></a>\n" + 
				 		"						</x>", obj1.getattrvalue("xmlbox"));
				 Assert.assertEquals("mehr.vier", obj1.getattrvalue("aufzaehlung"));
				 Assert.assertEquals("09:00:00.000", obj1.getattrvalue("aTime"));
				 Assert.assertEquals("abc200\n" + 
				 		"end200", obj1.getattrvalue("mtextLimited"));
				 Assert.assertEquals("AAAA", obj1.getattrvalue("binbox"));
				 Assert.assertEquals("chgAAAAAAAAA0azD", obj1.getattrvalue("aStandardid"));
				 Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", obj1.getattrvalue("aAttribute"));
				 Assert.assertEquals("2002-09-24", obj1.getattrvalue("aDate"));
				 Assert.assertEquals("Top", obj1.getattrvalue("vertAlignment"));
				 Assert.assertEquals("ClassA", obj1.getattrvalue("nametext"));
				 Assert.assertEquals("abc101", obj1.getattrvalue("textUnlimited"));
				 Assert.assertEquals("6.0", obj1.getattrvalue("numericDec"));
				 Assert.assertEquals("abc201\n" + 
				 		"end201", obj1.getattrvalue("mtextUnlimited"));
				 Assert.assertEquals("1900-01-01T12:30:05.000", obj1.getattrvalue("aDateTime"));
				 Assert.assertEquals("DM01AVCH24D.FixpunkteKategorie1.LFP1", obj1.getattrvalue("aClass"));
			 }
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfLine() throws Exception {		
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(CREATETABLES_LINES));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(INSERTINTOTABLES_LINES));
			
			File data=new File(TEST_OUT+"Datatypes23Line-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setModels(DBSCHEMA);
			config.setBasketHandling(null);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			XtfReader reader=new XtfReader(data);
			IoxEvent event=null;
			 do{
		        event=reader.read();
		        if(event instanceof StartTransferEvent){
		        }else if(event instanceof StartBasketEvent){
		        }else if(event instanceof ObjectEvent){
		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
		        	if(iomObj.getobjectoid()!=null){
			        	objs.put(iomObj.getobjectoid(), iomObj);
		        	}
		        }else if(event instanceof EndBasketEvent){
		        }else if(event instanceof EndTransferEvent){
		        }
			 }while(!(event instanceof EndTransferEvent));
			 {
				IomObject obj1 = objs.get("Line3.1");
				Assert.assertNotNull(obj1);
				Assert.assertEquals("Datatypes23.Topic.Line3", obj1.getobjecttag());
				IomObject lineSegment=obj1.getattrobj("straightsarcs3d",0);
				Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.0, C2 1045001.0, C3 300.0}, ARC {A1 2460005.0, A2 1045004.0, C1 2460006.0, C2 1045006.0, C3 300.0}, COORD {C1 2460010.0, C2 1045010.0, C3 300.0}]}}", lineSegment.toString());
			 }
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfSurface() throws Exception {
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(CREATETABLES_SURFACES));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(INSERTINTOTABLES_SURFACES));
			
			File data=new File(TEST_OUT+"Datatypes23Surface-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setModels(DBSCHEMA);
			config.setBasketHandling(null);
			//config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			XtfReader reader=new XtfReader(data);
			IoxEvent event=null;
			 do{
		        event=reader.read();
		        if(event instanceof StartTransferEvent){
		        }else if(event instanceof StartBasketEvent){
		        }else if(event instanceof ObjectEvent){
		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
		        	if(iomObj.getobjectoid()!=null){
			        	objs.put(iomObj.getobjectoid(), iomObj);
		        	}
		        }else if(event instanceof EndBasketEvent){
		        }else if(event instanceof EndTransferEvent){
		        }
			 }while(!(event instanceof EndTransferEvent));
			 {
				IomObject obj1 = objs.get("Surface2.1");
				Assert.assertNotNull(obj1);
				Assert.assertEquals("Datatypes23.Topic.Surface2", obj1.getobjecttag());
				IomObject surface = obj1.getattrobj("surfacearcs2d", 0);
				Assert.assertEquals("MULTISURFACE {surface SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.0, C2 1045001.0}, COORD {C1 2460020.0, C2 1045015.0}, ARC {A1 2460010.0, A2 1045018.0, C1 2460001.0, C2 1045015.0}, COORD {C1 2460001.0, C2 1045001.0}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.0, C2 1045005.0}, COORD {C1 2460010.0, C2 1045010.0}, ARC {A1 2460007.0, A2 1045009.0, C1 2460005.0, C2 1045010.0}, COORD {C1 2460005.0, C2 1045005.0}]}}}]}}", surface.toString());
			 }
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}