package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

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
public class MultisurfaceTest {
	private static final String DBSCHEMA = "MultiSurface";
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
	public void importXtfNoSmartChbase() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
              
			File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// FIXME test that geometry is imported to the structure table
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfNoSmartChbase() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/CreateTableXtf1a.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/InsertIntoTableXtf1a.sql"));
			File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("MultiSurface1");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
			 // check oid
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("o1", obj0.getobjectoid());
			 }
			 // check objecttag
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiSurface1.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
                 Assert.assertEquals("COORD {C1 600031.000, C2 200021.000}", obj0.getattrobj("point", 0).toString());
                 Assert.assertEquals("GeometryCHLV03_V1.MultiSurface {Surfaces [GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600030.000, C2 200020.000}, COORD {C1 600045.000, C2 200040.000}, COORD {C1 600010.000, C2 200040.000}, COORD {C1 600030.000, C2 200020.000}]}}}}}}, GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600015.000, C2 200005.000}, COORD {C1 600040.000, C2 200010.000}, COORD {C1 600010.000, C2 200020.000}, COORD {C1 600005.000, C2 200010.000}, COORD {C1 600015.000, C2 200005.000}]}}}}}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfSmartChbase() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// imported attrValues of classa1
			Assert.assertTrue(stmt.execute("SELECT classa1.geom, classa1.t_id, classa1.apoint FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'o1'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("010C0000201555000002000000010A0000000100000001090000000100000001020000000400000000000000BC4F224100000000A06A084100000000DA4F224100000000406B084100000000944F224100000000406B084100000000BC4F224100000000A06A0841010A00000001000000010900000001000000010200000005000000000000009E4F224100000000286A084100000000D04F224100000000506A084100000000944F224100000000A06A0841000000008A4F224100000000506A0841000000009E4F224100000000286A0841", rs.getString(1));
				Assert.assertEquals("01010000201555000000000000BE4F224100000000A86A0841", rs.getString(3));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importXtfSmartChbaseNull() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

            File data=new File("test/data/MultiSurface/MultiSurface1null.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // imported attrValues of classa1
            Assert.assertTrue(stmt.execute("SELECT classa1.geom, classa1.t_id, classa1.apoint FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'o1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(null, rs.getString(1));
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void exportXtfSmartChbase() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/CreateTableMultiSurface1a.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/InsertIntoTableMultiSurface1a.sql"));

			File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("MultiSurface1");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
			 // check oid
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("o1", obj0.getobjectoid());
			 }
			 // check objecttag
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiSurface1.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
                 Assert.assertEquals("COORD {C1 600031.000, C2 200021.000}", obj0.getattrobj("point", 0).toString());
                 Assert.assertEquals("GeometryCHLV03_V1.MultiSurface {Surfaces [GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600030.000, C2 200020.000}, COORD {C1 600045.000, C2 200040.000}, COORD {C1 600010.000, C2 200040.000}, COORD {C1 600030.000, C2 200020.000}]}}}}}}, GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600015.000, C2 200005.000}, COORD {C1 600040.000, C2 200010.000}, COORD {C1 600010.000, C2 200020.000}, COORD {C1 600005.000, C2 200010.000}, COORD {C1 600015.000, C2 200005.000}]}}}}}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfSmartCustom() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/MultiSurface/MultiSurface2a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
	
			// imported attrValues of classa1
			Assert.assertTrue(stmt.execute("SELECT classa1.geom, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = '13'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("010C0000201555000002000000010A0000000100000001090000000100000001020000000400000000000000BC4F224100000000A06A084100000000DA4F224100000000406B084100000000944F224100000000406B084100000000BC4F224100000000A06A0841010A00000001000000010900000001000000010200000005000000000000009E4F224100000000286A084100000000D04F224100000000506A084100000000944F224100000000A06A0841000000008A4F224100000000506A0841000000009E4F224100000000286A0841", rs.getString(1));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfSmartCustom() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/CreateTableMultiSurface2a.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/InsertIntoTableMultiSurface2a.sql"));

			File data=new File("test/data/MultiSurface/MultiSurface2a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("MultiSurface2");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
			 // check oid
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("13", obj0.getobjectoid());
			 }
			 // check objecttag
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiSurface2.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiSurface2.MultiFlaeche2D {Flaechen [MultiSurface2.FlaecheStruktur2D {Flaeche MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600030.000, C2 200020.000}, COORD {C1 600045.000, C2 200040.000}, COORD {C1 600010.000, C2 200040.000}, COORD {C1 600030.000, C2 200020.000}]}}}}}}, MultiSurface2.FlaecheStruktur2D {Flaeche MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600015.000, C2 200005.000}, COORD {C1 600040.000, C2 200010.000}, COORD {C1 600010.000, C2 200020.000}, COORD {C1 600005.000, C2 200010.000}, COORD {C1 600015.000, C2 200005.000}]}}}}}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfSmartChbaseSingleGeom() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/MultiSurface/MultiSurface1a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
			config.setOneGeomPerTable(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			// imported attrValues of classa1
			Assert.assertTrue(stmt.execute("SELECT classa1.geom, classa1.t_id FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'o1'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("010C0000201555000002000000010A0000000100000001090000000100000001020000000400000000000000BC4F224100000000A06A084100000000DA4F224100000000406B084100000000944F224100000000406B084100000000BC4F224100000000A06A0841010A00000001000000010900000001000000010200000005000000000000009E4F224100000000286A084100000000D04F224100000000506A084100000000944F224100000000A06A0841000000008A4F224100000000506A0841000000009E4F224100000000286A0841", rs.getString(1));
			}
			// FIXME test point/coord value in seconardy table 
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportSmartChbaseSingleGeom() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/CreateTableSingleGeom.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiSurface/InsertIntoTableSingleGeom.sql"));

			File data=new File("test/data/MultiSurface/MultiSurface1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("MultiSurface1");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
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
			 // check oid
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("o1", obj0.getobjectoid());
			 }
			 // check objecttag
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiSurface1.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("COORD {C1 600031.000, C2 200021.000}", obj0.getattrobj("point", 0).toString());
				 Assert.assertEquals("GeometryCHLV03_V1.MultiSurface {Surfaces [GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600030.000, C2 200020.000}, COORD {C1 600045.000, C2 200040.000}, COORD {C1 600010.000, C2 200040.000}, COORD {C1 600030.000, C2 200020.000}]}}}}}}, GeometryCHLV03_V1.SurfaceStructure {Surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600015.000, C2 200005.000}, COORD {C1 600040.000, C2 200010.000}, COORD {C1 600010.000, C2 200020.000}, COORD {C1 600005.000, C2 200010.000}, COORD {C1 600015.000, C2 200005.000}]}}}}}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

    @Test
    public void exportXtfEmptyGeom() throws Exception {
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        File data=new File("test/data/MultiSurface/MultiSurface2.ili");
        Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setDefaultSrsCode("21781");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        stmt.execute("INSERT INTO "+DBSCHEMA+".classa1(geom) VALUES (ST_GeomFromText('MULTISURFACE EMPTY', 21781))");

        File dataXtf=new File("test/data/MultiSurface/MultiSurface2Empty-out.xtf");

        config.setXtffile(dataXtf.getPath());
        config.setLogfile(dataXtf.getPath()+".log");
        config.setModels("MultiSurface2");
        config.setFunction(Config.FC_EXPORT);
        config.setValidation(false); // ClassA1.geom is in model MANDATORY (can not be empty)

        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        {
            XtfReader reader=new XtfReader(dataXtf);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            IoxEvent event=reader.read();
            assertTrue(event instanceof ObjectEvent);
            IomObject iomObj=((ObjectEvent)event).getIomObject();
            {
                String attrtag=iomObj.getobjecttag();
                assertEquals("MultiSurface2.TestA.ClassA1", attrtag);
                {
                    {
                        IomObject geom=iomObj.getattrobj("geom", 0);
                        assertNull(geom);
                    }
                }
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
        
    }
    @Test
    public void exportXtfMultiSurfaceWithPolygon() throws Exception {
        Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        File data=new File("test/data/MultiSurface/MultiSurface2.ili");
        Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setDefaultSrsCode("21781");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        // 600030.000 200020.000, 600045.000 200040.000, 600010.000 200040.000, 600030.000 200020.000
        stmt.execute("INSERT INTO "+DBSCHEMA+".classa1(geom) VALUES (ST_GeomFromText('MULTISURFACE (POLYGON((600030.000 200020.000, 600045.000 200040.000, 600010.000 200040.000, 600030.000 200020.000 )))', 21781))");

        File dataXtf=new File("test/data/MultiSurface/MultiSurface2WithPolygon-out.xtf");

        config.setXtffile(dataXtf.getPath());
        config.setLogfile(dataXtf.getPath()+".log");
        config.setModels("MultiSurface2");
        config.setFunction(Config.FC_EXPORT);
        config.setValidation(false); // ClassA1.geom is in model MANDATORY (can not be empty)

        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        {
            XtfReader reader=new XtfReader(dataXtf);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            IoxEvent event=reader.read();
            assertTrue(event instanceof ObjectEvent);
            IomObject iomObj=((ObjectEvent)event).getIomObject();
            {
                String attrtag=iomObj.getobjecttag();
                assertEquals("MultiSurface2.TestA.ClassA1", attrtag);
                {
                    {
                        IomObject geom=iomObj.getattrobj("geom", 0);
                        assertEquals("MultiSurface2.MultiFlaeche2D {Flaechen MultiSurface2.FlaecheStruktur2D {Flaeche MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 600030.000, C2 200020.000}, COORD {C1 600045.000, C2 200040.000}, COORD {C1 600010.000, C2 200040.000}, COORD {C1 600030.000, C2 200020.000}]}}}}}}}",geom.toString());
                    }
                }
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
        
    }
}