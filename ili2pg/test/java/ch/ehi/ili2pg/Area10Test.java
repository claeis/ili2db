package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import ch.ehi.basics.logging.EhiLogger;
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
import ch.interlis.iox_j.jts.Iox2jts;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Area10Test {
	
	private static final String DBSCHEMA = "Area10";
	private static final String TEST_OUT="test/data/Area10/";
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
	public void importItfAreaOk() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {        
				File data=new File(TEST_OUT,"Beispiel1a.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
	            config.setDefaultSrsAuthority("EPSG");
	            config.setDefaultSrsCode("2056");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT count(boflaechen.t_id) FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT st_asewkt(boflaechen.form) FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("SRID=2056;CURVEPOLYGON(COMPOUNDCURVE((2480110 1060110,2480110 1060140,2480120 1060140,2480120 1060110,2480110 1060110)))",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importItfOpenPolygonWithValidation() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"Beispiel1b.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setSkipGeometryErrors(true);
                config.setSqlNull(Config.SQL_NULL_ENABLE);
				config.setValidation(true);
                config.setDefaultSrsAuthority("EPSG");
                config.setDefaultSrsCode("2056");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT count(boflaechen.t_id) FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.iliname FROM "+DBSCHEMA+".t_ili2db_attrname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Beispiel1.Bodenbedeckung.BoFlaechen.Art",rs.getString(1));
				}
		    }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importItOpenPolygonWithoutValidation() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        { 
				File data=new File(TEST_OUT,"Beispiel1b.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
				config.setSkipGeometryErrors(true);
                config.setSqlNull(Config.SQL_NULL_ENABLE);
				config.setValidation(false);
                config.setDefaultSrsAuthority("EPSG");
                config.setDefaultSrsCode("2056");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				Assert.assertTrue(stmt.execute("SELECT boflaechen.art FROM "+DBSCHEMA+".BoFlaechen WHERE t_ili_tid='1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Art1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT count(boflaechen.t_id) FROM "+DBSCHEMA+".boFlaechen"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("1",rs.getString(1));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.iliname FROM "+DBSCHEMA+".t_ili2db_attrname"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Beispiel1.Bodenbedeckung.BoFlaechen.Art",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Ignore("no attributes and attrvalues exported.")
	public void exportAreaOk() throws Exception
	{
		{
			importItfAreaOk();
		}
		File data=null;
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        {        
				data=new File(TEST_OUT,"Beispiel1a-out.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setModels("Beispiel1");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
		{
			ItfReader reader=new ItfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			IoxEvent event=reader.read();
			{
				{
					assertTrue(event instanceof ObjectEvent);
					IomObject iomObj=((ObjectEvent)event).getIomObject();
					String attrtag=iomObj.getobjecttag();
					assertEquals("Beispiel1.Bodenbedeckung.BoFlaechen",attrtag);
					String oid=iomObj.getobjectoid();
					assertEquals("1",oid);
					{
						IomObject attrObj=iomObj.getattrobj("Form", 0);
						// convert
						Polygon jtsPolygon=Iox2jts.surface2JTS(attrObj, 2056);
						// polygon1
						assertEquals(1,jtsPolygon.getNumGeometries());
						Coordinate[] coords=jtsPolygon.getCoordinates();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2480110.0"), new Double("1060110.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2480120.0"), new Double("1060110.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2480120.0"), new Double("1060140.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2480110.0"), new Double("1060140.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("2480110.0"), new Double("1060110.0"));
							assertEquals(coord5, coords[4]);
						}
					}
				}
				assertTrue(reader.read() instanceof EndBasketEvent);
				assertTrue(reader.read() instanceof EndTransferEvent);
			}
		}
	}
}