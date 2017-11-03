package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CatalogueObjectsTest {
	private static final String DBSCHEMA = "CatalogueObjects1";
	private static final String DATASETNAME = "Testset1";
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
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

	@Test
	public void importIli() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/CatalogueObjects/CatalogueObjects1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// import-test: attrname of catalogue
				{
					String stmtTxt="SELECT t_ili2db_attrname.iliname, t_ili2db_attrname.sqlname FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.iliname = 'CatalogueObjects1.TopicA.Katalog_Programm.Code'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("code",rs.getString(2));
				}
				// import-test: classname of catalogue
				{
					String stmtTxt="SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'CatalogueObjects1.TopicA.Katalog_Programm'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_katalog_programm",rs.getString(2));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File("test/data/CatalogueObjects/CatalogueObjects1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				//config.setValidation(false);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// object import
				{
					String stmtTxt="SELECT t_ili2db_import_object.t_id, t_ili2db_import_object.objectcount FROM "+DBSCHEMA+".t_ili2db_import_object WHERE t_ili2db_import_object.class = 'CatalogueObjects1.TopicA.Katalog_Programm'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(2));
				}
				{
					String stmtTxt="SELECT t_ili2db_import_object.t_id, t_ili2db_import_object.objectcount FROM "+DBSCHEMA+".t_ili2db_import_object WHERE t_ili2db_import_object.class = 'CatalogueObjects1.TopicA.Katalog_ProgrammRef'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(2));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}	
	
	@Test
	public void exportXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/CatalogueObjects/InsertIntoTable.sql"));
			File data=new File("test/data/CatalogueObjects/CatalogueObjects1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setDatasetName(DATASETNAME);
			config.setBaskets("CatalogueObjects1.TopicC.1");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
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
				 IomObject obj0 = objs.get("9");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Katalog_OhneUuid", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("10");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Nutzung", obj1.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}