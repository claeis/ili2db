package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
public class Dataset23Smart1Test {
	private static final String DBSCHEMA = "Dataset1smart1";
	private static final String DATASETNAME_A = "Testset1";
	private static final String DATASETNAME_B = "Testset2";
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

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	                Ili2db.setNoSmartMapping(config);
					config.setDatasetName(DATASETNAME_A);
					config.setFunction(Config.FC_IMPORT);
			        config.setDoImplicitSchemaImport(true);
					config.setCreateFk(config.CREATE_FK_YES);
					config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
					config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
					config.setCreateImportTabs(true);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1b1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
					config.setDatasetName(DATASETNAME_B);
					config.setFunction(Config.FC_IMPORT);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				
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
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importXtfDatasetEmpty() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                {
                    File data=new File("test/data/Dataset23Smart1/Dataset1c1.xtf");
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(config.CREATE_FK_YES);
                    config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void replaceXtfDatasetEmpty() throws Exception
    {
        {
            importXtfDatasetEmpty();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            {
                {
                    File data=new File("test/data/Dataset23Smart1/Dataset1c2.xtf");
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_REPLACE);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void deleteXtfDatasetEmpty() throws Exception
    {
        {
            importXtfDatasetEmpty();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            {
                {
                    File data=new File("test/data/Dataset23Smart1/Dataset1c2.xtf");
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setDatasetName(DATASETNAME_A);
                    config.setFunction(Config.FC_DELETE);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	@Test
	public void importXtfNoDatasetName() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    Ili2db.setNoSmartMapping(config);
					config.setDatasetName(null);
					config.setFunction(Config.FC_IMPORT);
			        config.setDoImplicitSchemaImport(true);
					config.setCreateFk(config.CREATE_FK_YES);
					config.setBasketHandling(Config.NULL);
					config.setInheritanceTrafo(config.INHERITANCE_TRAFO_SMART1);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				{
					File data=new File("test/data/Dataset23Smart1/Dataset1a1.xtf");
					Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
					config.setDatasetName(null);
					config.setFunction(Config.FC_IMPORT);
					Ili2db.readSettingsFromDb(config);
					Ili2db.run(config,null);
				}
				Assert.assertTrue(stmt.execute("SELECT DISTINCT t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertTrue(rs.getString(1).startsWith("Dataset1a1.xtf"));
                    Assert.assertTrue(rs.next());
                    Assert.assertTrue(rs.getString(1).startsWith("Dataset1a1.xtf"));
                    Assert.assertFalse(rs.next());
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void deleteXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/InsertIntoTable.sql"));		
			Config config=initConfig(null,DBSCHEMA,"test/data/Dataset23Smart1/Dataset1b1-out.xtf"+".log");
			config.setDatasetName(DATASETNAME_B);
			config.setFunction(Config.FC_DELETE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_dataset.t_id, t_ili2db_dataset.datasetname FROM "+DBSCHEMA+".t_ili2db_dataset WHERE t_ili2db_dataset.t_id = 1"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("Testset1",rs.getString(2));
			}
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
	public void replaceXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/InsertIntoTable.sql"));
			File data=new File("test/data/Dataset23Smart1/Dataset1a2.xtf");
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
	public void exportXtf() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/CreateTable.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/Dataset23Smart1/InsertIntoTable.sql"));		
			
			File data=new File("test/data/Dataset23Smart1/Dataset1-out.xtf");
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
				 IomObject obj0 = objs.get("4");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Dataset1.TestA.ClassA1", obj0.getobjecttag());
				 Assert.assertEquals("a1", obj0.getattrvalue("attr1"));
			 }
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
