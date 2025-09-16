package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

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
public class Dataset23NoSmartTest {
	private static final String DBSCHEMA = "Dataset1nosmart";
	private static final String DATASETNAME_A = "Testset1_a";
	private static final String DATASETNAME_B = "Testset1_b";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
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

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importXtfDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Dataset23NoSmart/Dataset1a1.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setDatasetName(DATASETNAME_A);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateImportTabs(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			{
				File data=new File("test/data/Dataset23NoSmart/Dataset1b1.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setDatasetName(DATASETNAME_B);
				config.setFunction(Config.FC_IMPORT);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
			// dataset exists test
            HashSet<String> expectedDatasets= new HashSet<String>(Arrays.asList(new String[]{"Testset1_a","Testset1_b"}));

			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset"));
			{
				ResultSet rs=stmt.getResultSet();
				while(!expectedDatasets.isEmpty()) {
	                Assert.assertTrue(rs.next());
	                String dataset=rs.getString(2);
	                Assert.assertTrue(expectedDatasets.remove(dataset));
				}
                Assert.assertFalse(rs.next());
			}
			// class generated test
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.ClassA1'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("classa1",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.ClassA1b'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("classa1b",rs.getString(2));
			}
			// class generated test
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.StructS1b'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("structs1b",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.StructS1'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("structs1",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.ClassB1'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("classb1",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'Dataset1.TestA.ClassB1b'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("classb1b",rs.getString(2));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void deleteXtfDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/InsertIntoTable.sql"));
			Config config=initConfig(null,DBSCHEMA,"test/data/Dataset23NoSmart/Dataset1b1-out.xtf"+".log");
			config.setDatasetName(DATASETNAME_B);
			config.setFunction(Config.FC_DELETE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// SETA
			// dataset exists test
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 1"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("Testset1_a",rs.getString(2));
			}
			// SETB
			// dataset exists test
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 11"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertFalse(rs.next());
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

	@Test
	public void replaceXtfDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/InsertIntoTable.sql")); 
			File data=new File("test/data/Dataset23NoSmart/Dataset1a2.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_REPLACE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
            HashSet<String> expectedDatasets= new HashSet<String>(Arrays.asList(new String[]{DATASETNAME_A,DATASETNAME_B}));
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset"));
			{
				ResultSet rs=stmt.getResultSet();
				while(!expectedDatasets.isEmpty()) {
	                Assert.assertTrue(rs.next());
	                Assert.assertTrue(expectedDatasets.remove(rs.getString(2)));
				}
                Assert.assertFalse(rs.next());
			}
			
			Assert.assertTrue(stmt.execute("SELECT classa1.attr1 FROM "+DBSCHEMA+".classa1"));
			{
				ResultSet rs=stmt.getResultSet();
				for(int i=0;i<4;i++) {
	                Assert.assertTrue(rs.next());
	                String attr1=rs.getString(1);
	                Assert.assertTrue("b1".equals(attr1) || "a2".equals(attr1));
				}
                Assert.assertFalse(rs.next());
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfDataset() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23NoSmart/InsertIntoTable.sql")); 
			File data=new File("test/data/Dataset23NoSmart/Dataset1-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_EXPORT);
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
				 IomObject obj0 = objs.get("6");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Dataset1.TestA.ClassA1b", obj0.getobjecttag());
				 Assert.assertEquals("a1", obj0.getattrvalue("attr1"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
