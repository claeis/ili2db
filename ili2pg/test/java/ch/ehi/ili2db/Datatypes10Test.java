package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes10Test {
	private static final String DBSCHEMA = "Datatypes10";
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
	public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/Datatypes10/Datatypes10.ili");
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
				config.setModels("Datatypes10");
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
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Datatypes10/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Datatypes10/InsertIntoTable.sql"));
			File data=new File("test/data/Datatypes10/Datatypes10a-out.itf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Datatypes10");
			config.setFunction(Config.FC_EXPORT);
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
				File data=new File("test/data/Datatypes10/Datatypes10a.itf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setDoItfLineTables(true);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
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
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Datatypes10/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Datatypes10/InsertIntoTable.sql"));
	        File data=new File("test/data/Datatypes10/Datatypes10a-ltout.itf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setModels("Datatypes10");
			config.setFunction(Config.FC_EXPORT);
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
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
