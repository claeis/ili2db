package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class MultipointTest {
	private static final String DBSCHEMA = "MultiPoint";
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
	public void importXtfSmartCustom() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/MultiPoint/MultiPoint2a.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultiLineTrafo(null);
			config.setMultiPointTrafo(config.MULTIPOINT_TRAFO_COALESCE);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// assertions
			ResultSet rs = stmt.executeQuery("SELECT st_asewkt(geom) FROM multipoint.classa1;");
			ResultSetMetaData rsmd=rs.getMetaData();
			assertEquals(1, rsmd.getColumnCount());
			while(rs.next()){
			  	assertEquals("SRID=21781;MULTIPOINT(600030 200020,600015 200005)", rs.getObject(1));
			}
		}catch(Exception e) {
			throw new IoxException(e);
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
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiPoint/CreateTableMultiPoint2a.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/MultiPoint/InsertIntoTableMultiPoint2a.sql"));
			
	        File data=new File("test/data/MultiPoint/MultiPoint2a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("MultiPoint2");
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
				 Assert.assertEquals("MultiPoint2.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiPoint2.MultiPoint2D {points [MultiPoint2.PointStruktur2D {coord COORD {C1 600030.000, C2 200020.000}}, MultiPoint2.PointStruktur2D {coord COORD {C1 600015.000, C2 200005.000}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}