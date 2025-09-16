package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes10Test {
	
	private static final String DBSCHEMA = "Datatypes10";
	private static final String TEST_OUT="test/data/Datatypes10/";
	Connection jdbcConnection=null;
	Statement stmt=null;
	
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
	public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"Datatypes10.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setCreateNumChecks(true);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setDefaultSrsAuthority("EPSG");
                config.setDefaultSrsCode("21781");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT numeric_precision,numeric_scale FROM information_schema.columns WHERE table_schema ='datatypes10' AND table_name = 'tablea' AND column_name = 'dim1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(1));
					Assert.assertEquals(1,rs.getInt(2));
				}
				{
					String stmtTxt="SELECT numeric_precision,numeric_scale FROM information_schema.columns WHERE table_schema ='datatypes10' AND table_name = 'tablea' AND column_name = 'dim2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(1));
					Assert.assertEquals(1,rs.getInt(2));
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Datatypes10.Topic.TableA.dim2", "dim2",  "tablea", null},
                        {"Datatypes10.Topic.OtherTable.otherAttr",    "otherattr", "othertable", null},
                        {"Datatypes10.Topic.TableA.dim1", "dim1",  "tablea",null},
                        {"Datatypes10.Topic.TableA.radians",  "radians",   "tablea", null},
                        {"Datatypes10.Topic.TableA.linientyp",    "linientyp", "tablea",null},
                        {"Datatypes10.Topic.TableA.koord2",   "koord2",    "tablea", null},
                        {"Datatypes10.Topic.TableA.datum",    "datum", "tablea",null},    
                        {"Datatypes10.Topic.SubTablemain.main",   "main",  "subtable",  "tablea"},
                        {"Datatypes10.Topic.TableA.bereich",  "bereich",   "tablea",null},
                        {"Datatypes10.Topic.TableA.koord3",   "koord3",    "tablea",null},
                        {"Datatypes10.Topic.TableA.grads",    "grads", "tablea",null}, 
                        {"Datatypes10.Topic.TableA.aufzaehlung",  "aufzaehlung",   "tablea",null},
                        {"Datatypes10.Topic.TableA.surface",  "surface", "tablea", null},
                        {"Datatypes10.Topic.TableA.horizAlignment", "horizalignment",    "tablea",null},
                        {"Datatypes10.Topic.TableA.vertAlignment",    "vertalignment", "tablea",null},    
                        {"Datatypes10.Topic.TableA.text", "atext", "tablea", null},
                        {"Datatypes10.Topic.TableA.bereichInt",   "bereichint", "tablea",null},
                        {"Datatypes10.Topic.TableA.area", "area",  "tablea",null},
                        {"Datatypes10.Topic.TableA.degrees",  "degrees",   "tablea", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Datatypes10.Topic.SubTablemain", "ch.ehi.ili2db.inheritance", "embedded"},
                        {"Datatypes10.Topic.TableA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Datatypes10.Topic.SubTable", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Datatypes10.Topic.OtherTable", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Datatypes10.Topic.LineAttrib1", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

	@Test
	public void importItf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {     
				File data=new File(TEST_OUT,"Datatypes10a.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setModels("Datatypes10");
                config.setDefaultSrsAuthority("EPSG");
                config.setDefaultSrsCode("21781");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT atext FROM "+DBSCHEMA+".tablea AS a INNER JOIN "+DBSCHEMA+".subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("obj11",rs.getObject(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportItf() throws Exception
	{
		{
			importItf();
		}
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			File data=new File(TEST_OUT,"Datatypes10a-out.itf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Datatypes10");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// read objects of db and write objectValue to HashMap
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			ItfReader reader=new ItfReader(data);
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
				 IomObject obj0 = objs.get("10");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Datatypes10.Topic.OtherTable", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("11");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.OtherTable", obj1.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("30");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("31");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj1.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("32");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj1.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importItfWithSkipPolygonBuilding() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {    
				File data=new File(TEST_OUT,"Datatypes10a.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				Ili2db.setSkipPolygonBuilding(config);
                config.setDefaultSrsAuthority("EPSG");
                config.setDefaultSrsCode("21781");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT atext FROM "+DBSCHEMA+".tablea AS a INNER JOIN "+DBSCHEMA+".subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("obj11",rs.getObject(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportItfWithSkipPolygonBuilding() throws Exception
	{
		{
			importItfWithSkipPolygonBuilding();
		}
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        File data=new File(TEST_OUT,"Datatypes10a-ltout.itf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Datatypes10");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// read objects of db and write objectValue to HashMap
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			ItfReader reader=new ItfReader(data);
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
				 IomObject obj0 = objs.get("10");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Datatypes10.Topic.OtherTable", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("11");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.OtherTable", obj1.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("30");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("31");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj1.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("32");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("Datatypes10.Topic.SubTable", obj1.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}