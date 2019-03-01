package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.jts.Iox2jts;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class TranslationTest {
	private static final String DBSCHEMA = "Translation";
	private static final String TEST_OUT = "test/data/Translation/";
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
	public void importIli23() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {       
				File data=new File(TEST_OUT,"EnumOk.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setModels("EnumOkA;EnumOkB");
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
				config.setVer4_translation(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// class[a] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'EnumOkA.TopicA.ClassA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa",rs.getString(2));
				}
				// class[b] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'EnumOkB.TopicB.ClassB'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"EnumOkA.TopicA.ClassA.attrA",  "attra", "classa", null}, 
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"EnumOkA.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
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
	public void importIli10() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"ModelBsimple10.ili");
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
	            config.setDefaultSrsAuthority("EPSG");
	            config.setDefaultSrsCode("21781");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// class[a] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa",rs.getString(2));
				}
				// class[b] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
	            {
	                // t_ili2db_attrname
	                String [][] expectedValues=new String[][] {
	                    {"ModelAsimple10.TopicA.ClassA3.geomA",  "geoma", "classa3", null},   
	                    {"ModelAsimple10.TopicA.ClassA2.geomA",   "geoma", "classa2", null},
	                    {"ModelAsimple10.TopicA.ClassA.attrA",    "attra", "classa", null},
	                };
	                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
	            }
	            {
	                // t_ili2db_trafo
	                String [][] expectedValues=new String[][] {
	                    {"ModelAsimple10.TopicA.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
	                    {"ModelAsimple10.TopicA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
	                    {"ModelAsimple10.TopicA.ClassA3", "ch.ehi.ili2db.inheritance", "newClass"},
	                    
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
	public void importIli10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"ModelBsimple10.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
                Ili2db.setSkipPolygonBuilding(config);
				config.setIli1Translation("ModelBsimple10=ModelAsimple10");
	            config.setDefaultSrsAuthority("EPSG");
	            config.setDefaultSrsCode("21781");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// class[a2] is imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa2",rs.getString(2));
				}
				// class[b2] is NOT imported
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertFalse(rs.next());
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"ModelAsimple10.TopicA.ClassA2.geomA._geom", "_geom", "classa2_geoma", null}, 
                        {"ModelAsimple10.TopicA.ClassA3.geomA",   "geoma", "classa3", null},   
                        {"ModelAsimple10.TopicA.ClassA3.geomA._geom", "_geom", "classa3_geoma", null}, 
                        {"ModelAsimple10.TopicA.ClassA2.geomA._ref", "_ref", "classa2_geoma", null}, 
                        {"ModelAsimple10.TopicA.ClassA.attrA", "attra", "classa", null}
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"ModelAsimple10.TopicA.ClassA3", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ModelAsimple10.TopicA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"ModelAsimple10.TopicA.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"}
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
	public void importXtf23() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File(TEST_OUT,"EnumOka.xtf");
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
	    		// tid's of class[a]
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 4"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("o1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 5"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("o2",rs.getString(2));
				}
				// tid's of class[b]
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 9"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("x1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT classa.t_id, classa.t_ili_tid FROM "+DBSCHEMA+".classa WHERE classa.t_id = 10"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("x2",rs.getString(2));
				}
				// bid's of classa and classb are created
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("EnumOkA.TopicA",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 8"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("EnumOkB.TopicB",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void exportXtf23() throws Exception
	{
		Connection jdbcConnection=null;
		{
			importXtf23();
		}
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
			
			//EhiLogger.getInstance().setTraceFilter(false);
			File data=new File(TEST_OUT,"EnumOka-out.xtf");
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
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("o2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
				 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
			 }
			 {
				 IomObject obj0 = objs.get("x1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("x2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
				 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importItf10() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File(TEST_OUT,"ModelAsimple10a.itf");
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
	            config.setDefaultSrsAuthority("EPSG");
	            config.setDefaultSrsCode("21781");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
	        {
	        	File data=new File(TEST_OUT,"ModelBsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
    		// tid's of class[a]
			Assert.assertTrue(stmt.execute("SELECT classa.attra FROM translation.classa"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("o10",rs.getString(1));
			}
			// bid's of classa and classb are created
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 14"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    
	@Test
	public void exportItf10() throws Exception
	{
		Connection jdbcConnection=null;
		{
			importItf10();
		}
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        {
	        	{
		    		File data=new File(TEST_OUT,"ModelAsimple10a-out.itf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
		    		config.setDatasetName("ModelAsimple10");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
		    		
		    		// compile model
		    		TransferDescription td2=null;
		    		Configuration ili2cConfig=new Configuration();
		    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelAsimple10.ili", FileEntryKind.ILIMODELFILE);
		    		ili2cConfig.addFileEntry(fileEntry);
		    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
		    		assertNotNull(td2);
		    		
		    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
		    		ItfReader reader=new ItfReader(data);
		    		reader.setModel(td2);
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
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
						 Assert.assertEquals("o10",obj0.getattrvalue("attrA"));
					 }
		    		 {
						 IomObject obj0 = objs.get("11");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
						 Assert.assertEquals("o11",obj0.getattrvalue("attrA"));
					 }
					 {
						 IomObject obj0 = objs.get("12");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA2", obj0.getobjecttag());
					 }
					 {
						IomObject iomObj = objs.get("15");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA3",attrtag);
						IomObject coord=iomObj.getattrobj("geomA", 0);
						assertTrue(coord.getattrvalue("C1").equals("480005.000"));
						assertTrue(coord.getattrvalue("C2").equals("70005.000"));
					 }
					 {
						 IomObject iomObj = objs.get("16");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA2_geomA",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA2", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
					 {
						 IomObject iomObj = objs.get("17");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA3_geomA",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA3", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
	        	}
		        {
					File data2=new File(TEST_OUT,"ModelBsimple10a-out.itf");
					Config config=initConfig(data2.getPath(),DBSCHEMA,data2.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
		    		config.setDatasetName("ModelBsimple10");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
		    		
		    		// compile model
		    		TransferDescription td2=null;
		    		Configuration ili2cConfig=new Configuration();
		    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelBsimple10.ili", FileEntryKind.ILIMODELFILE);
		    		ili2cConfig.addFileEntry(fileEntry);
		    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
		    		assertNotNull(td2);
		    		
		    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
		    		ItfReader reader=new ItfReader(data2);
		    		reader.setModel(td2);
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
						 IomObject obj0 = objs.get("21");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
						 Assert.assertEquals("o21",obj0.getattrvalue("attrB"));
					 }
		    		 {
						 IomObject obj0 = objs.get("20");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
						 Assert.assertEquals("o20",obj0.getattrvalue("attrB"));
					 }
					 {
						 IomObject obj0 = objs.get("22");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
					 }
					 {
						 IomObject obj0 = objs.get("25");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
						 IomObject obj1=obj0.getattrobj("geomB", 0);
						 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
						 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
					 }
					 {
						IomObject iomObj = objs.get("26");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelBsimple10.TopicB.ClassB2_geomB",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB2", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
					 {
						 IomObject iomObj = objs.get("27");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelBsimple10.TopicB.ClassB3_geomB",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB3", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
		        }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importItf10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	    		File data=new File(TEST_OUT,"ModelAsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                Ili2db.setSkipPolygonBuilding(config);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		config.setIli1Translation("ModelBsimple10=ModelAsimple10");
	    		config.setDatasetName("ModelAsimple10");
	            config.setDefaultSrsAuthority("EPSG");
	            config.setDefaultSrsCode("21781");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
	        {
	        	File data=new File(TEST_OUT,"ModelBsimple10a.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	        }
 			Assert.assertTrue(stmt.execute("SELECT st_asewkt(classa2_geoma._geom) FROM "+DBSCHEMA+".classa2_geoma"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("SRID=21781;COMPOUNDCURVE((480000 70000,480010 70000,480010 70010,480000 70010,480000 70000))",rs.getString(1));
 			}
 			Assert.assertTrue(stmt.execute("SELECT st_asewkt(classa3_geoma._geom) FROM "+DBSCHEMA+".classa3_geoma"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("SRID=21781;COMPOUNDCURVE((480000 70000,480010 70000,480010 70010,480000 70010,480000 70000))",rs.getString(1));
 			}
 			// bid's of classa and classb are created
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 3"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
 			}
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+DBSCHEMA+".t_ili2db_basket WHERE t_ili2db_basket.t_id = 16"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
 			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    
	@Test
	public void exportItf10lineTable() throws Exception
	{
		Connection jdbcConnection=null;
		{
			importItf10lineTable();
		}
		try{
			//EhiLogger.getInstance().setTraceFilter(false);
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        {
	        	File data=new File(TEST_OUT,"ModelAsimple10a-out.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelAsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	    		
	    		// compile model
	    		TransferDescription td2=null;
	    		Configuration ili2cConfig=new Configuration();
	    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelAsimple10.ili", FileEntryKind.ILIMODELFILE);
	    		ili2cConfig.addFileEntry(fileEntry);
	    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
	    		assertNotNull(td2);
	    		
	    		ItfReader reader=new ItfReader(data);
	    		reader.setModel(td2);
	    		IoxEvent event=null;
	    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
	    		 do{
	    		        event=reader.read();
	    		        if(event instanceof StartTransferEvent){
	    		        }else if(event instanceof StartBasketEvent){
	    		        }else if(event instanceof ObjectEvent){
	    		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
	    		    		assertNotNull(iomObj.getobjectoid());
	    		    		objs.put(iomObj.getobjectoid(), iomObj);
	    		        }else if(event instanceof EndBasketEvent){
	    		        }else if(event instanceof EndTransferEvent){
	    		        }
	    		 }while(!(event instanceof EndTransferEvent));
				 {
					 IomObject obj0 = objs.get("10");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
					 Assert.assertEquals("o10",obj0.getattrvalue("attrA"));
				 }
	    		 {
					 IomObject obj0 = objs.get("11");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
					 Assert.assertEquals("o11",obj0.getattrvalue("attrA"));
				 }
				 {
					 IomObject obj0 = objs.get("12");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA2", obj0.getobjecttag());
				 }
				 {
					 IomObject iomObj = objs.get("13");
					String attrtag=iomObj.getobjecttag();
					assertEquals("ModelAsimple10.TopicA.ClassA2_geomA",attrtag);
					IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA2", 0);
					// convert
					CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
					// polygon1
					Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
					{
						com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord, coords[0]);
						com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
						assertEquals(coord2, coords[1]);
						com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
						assertEquals(coord3, coords[2]);
						com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
						assertEquals(coord4, coords[3]);
						com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord5, coords[4]);
					}
					IomObject iomObj2=iomObj.getattrobj("_itf_ref_ClassA2", 0);
					assertEquals("12", iomObj2.getobjectrefoid());
				 }
				 {
					 IomObject iomObj = objs.get("14");
					String attrtag=iomObj.getobjecttag();
					assertEquals("ModelAsimple10.TopicA.ClassA3_geomA",attrtag);
					IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA3", 0);
					// convert
					CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
					// polygon1
					Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
					{
						com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord, coords[0]);
						com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
						assertEquals(coord2, coords[1]);
						com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
						assertEquals(coord3, coords[2]);
						com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
						assertEquals(coord4, coords[3]);
						com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord5, coords[4]);
					}
				 }
				 {
					 IomObject obj0 = objs.get("15");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("geomA", 0);
					 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
					 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
				 }
	        }
	        {
	        	File data=new File(TEST_OUT,"ModelBsimple10a-out.itf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setDatasetName("ModelBsimple10");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
	    		
	    		// compile model
	    		TransferDescription td2=null;
	    		Configuration ili2cConfig=new Configuration();
	    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelBsimple10.ili", FileEntryKind.ILIMODELFILE);
	    		ili2cConfig.addFileEntry(fileEntry);
	    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
	    		assertNotNull(td2);
	    		
	    		ItfReader reader=new ItfReader(data);
	    		reader.setModel(td2);
	    		IoxEvent event=null;
	    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
	    		 do{
    		        event=reader.read();
    		        if(event instanceof StartTransferEvent){
    		        }else if(event instanceof StartBasketEvent){
    		        }else if(event instanceof ObjectEvent){
    		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
    		    		assertNotNull(iomObj.getobjectoid());
    		    		objs.put(iomObj.getobjectoid(), iomObj);
    		        }else if(event instanceof EndBasketEvent){
    		        }else if(event instanceof EndTransferEvent){
    		        }
	    		 }while(!(event instanceof EndTransferEvent));
				 {
					 IomObject obj0 = objs.get("21");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
					 Assert.assertEquals("o21",obj0.getattrvalue("attrB"));
				 }
	    		 {
					 IomObject obj0 = objs.get("20");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
					 Assert.assertEquals("o20",obj0.getattrvalue("attrB"));
				 }
				 {
					 IomObject obj0 = objs.get("22");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("25");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("geomB", 0);
					 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
					 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
				 }
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}