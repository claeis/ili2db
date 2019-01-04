package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Ignore;
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
public class InheritanceSmart1Test {
	private static final String DBSCHEMA = "InheritanceSmart1";
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

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/Inheritance1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				// base class is empty (sub struct strategy or super class strategy)
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'Inheritance1.TestA.ClassA1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(null,rs.getString(2));
				}
				// base class is empty (sub struct strategy or super class strategy)
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'Inheritance1.TestC.ClassC1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(null,rs.getString(2));
				}
				// base class not empty (new class stragety)
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'Inheritance1.TestA.ClassA4b'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Inheritance1.TestA.ClassA4",rs.getString(2));
				}
				// newClassStrategy
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_trafo.iliname, t_ili2db_trafo.setting FROM "+DBSCHEMA+".t_ili2db_trafo WHERE t_ili2db_trafo.iliname = 'Inheritance1.TestA.ClassA1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("newClass",rs.getString(2));
				}
				// subStructStrategy
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_trafo.iliname, t_ili2db_trafo.setting FROM "+DBSCHEMA+".t_ili2db_trafo WHERE t_ili2db_trafo.iliname = 'Inheritance1.TestC.ClassC1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("subClass",rs.getString(2));
				}
				// superClassStrategy
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_trafo.iliname, t_ili2db_trafo.setting FROM "+DBSCHEMA+".t_ili2db_trafo WHERE t_ili2db_trafo.iliname = 'Inheritance1.TestA.ClassA4b'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("superClass",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportSmart1() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/CreateTableSmart1.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/InsertIntoTableSmart1.sql"));

			File data=new File("test/data/InheritanceSmart1/Inheritance1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME);
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
				 Assert.assertEquals("Inheritance1.TestA.ClassA1", obj0.getobjecttag());
				 Assert.assertEquals("a1", obj0.getattrvalue("attrA1"));
			 }
			 {
				 IomObject obj0 = objs.get("8");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("Inheritance1.TestA.ClassA4b", obj0.getobjecttag());
				 Assert.assertEquals("a4", obj0.getattrvalue("attrA4"));
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importIliSubtypeFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/SubtypeFK.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// subtype class import
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.bc1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("bc1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classa",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassB'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classb",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'SubtypeFK23.Topic.ClassC'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("classc",rs.getString(2));
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"SubtypeFK23.Topic.bc1.b1", "b1", "classa", "classa"},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"SubtypeFK23.Topic.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"SubtypeFK23.Topic.ClassB",  "ch.ehi.ili2db.inheritance", "superClass"},
                        {"SubtypeFK23.Topic.bc1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"SubtypeFK23.Topic.ClassC",  "ch.ehi.ili2db.inheritance", "superClass"},
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
	public void importXtfSubtypeFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/SubtypeFKa.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// subtype value
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.target, t_ili2db_attrname.sqlname FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.target = 'classa'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("b1",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
	
	@Test
	public void importIliStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/StructAttr1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// imported classes
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'StructAttr1.TopicA.StructA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_structa",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+DBSCHEMA+".t_ili2db_classname WHERE t_ili2db_classname.iliname = 'StructAttr1.TopicB.StructA'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topicb_structa",rs.getString(2));
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicB.StructA.name",  "aname", "topicb_structa", null},    
                        {"StructAttr1.TopicA.ClassB.attr3",   "attr3", "topica_classa", null},
                        {"StructAttr1.TopicB.ClassB.attr3",   "attr3", "topicb_classb", null},
                        {"StructAttr1.TopicB.ClassA.attr1",   "topicb_classb_attr1",   "topicb_structa", "topicb_classb"},
                        {"StructAttr1.TopicA.ClassA.attr1",   "topica_classa_attr1",   "topica_structa", "topica_classa"},
                        {"StructAttr1.TopicA.ClassC.attr4",   "attr4", "topica_classa", null},
                        {"StructAttr1.TopicB.ClassA.attr2",   "attr2", "topicb_classb", null},
                        {"StructAttr1.TopicA.StructA.name",   "aname", "topica_structa", null},
                        {"StructAttr1.TopicA.ClassA.attr2",   "attr2", "topica_classa", null},
                        {"StructAttr1.TopicB.ClassC.attr4",   "attr4", "topicb_classb", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"StructAttr1.TopicB.StructA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassB", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicA.ClassA", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicB.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"StructAttr1.TopicB.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.StructA",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"StructAttr1.TopicA.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"StructAttr1.TopicB.ClassC", "ch.ehi.ili2db.inheritance", "superClass"},
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
	public void importXtfStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/StructAttr1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// subtype value
				Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.t_id = 5"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Anna",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.t_id = 7"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Berta",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT topica_structa.t_id, topica_structa.aname FROM "+DBSCHEMA+".topica_structa WHERE topica_structa.t_id = 9"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Claudia",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfStructAttrFK() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/CreateTableXtfStructAttrFK.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/InsertIntoTableXtfStructAttrFK.sql"));

	        File data=new File("test/data/InheritanceSmart1/StructAttr1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setDatasetName(DATASETNAME);
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
				 IomObject obj0 = objs.get("b3");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("StructAttr1.TopicB.ClassC", obj0.getobjecttag());
				 Assert.assertEquals("StructAttr1.TopicB.StructA {name Claudia}", obj0.getattrobj("attr1", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
	
	@Test
	@Ignore("incompatibility to ili2db-3.x")
	public void importIliRefAttrFK_3x() throws Exception
	{
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
		        File data=new File("test/data/InheritanceSmart1/RefAttr1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// ref import test
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.sqlname = 'ref_topica_classa1' AND t_ili2db_attrname.owner = 'topica_structa1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_structa1",rs.getString(2));
					Assert.assertEquals("topica_classa1",rs.getString(3));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.sqlname = 'ref_topica_classa2' AND t_ili2db_attrname.owner = 'topica_structa1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_structa1",rs.getString(2));
					Assert.assertEquals("topica_classa2",rs.getString(3));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.sqlname = 'ref_topica_classa1' AND t_ili2db_attrname.owner = 'topica_structa2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_structa2",rs.getString(2));
					Assert.assertEquals("topica_classa1",rs.getString(3));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+DBSCHEMA+".t_ili2db_attrname WHERE t_ili2db_attrname.sqlname = 'ref_topica_classa2' AND t_ili2db_attrname.owner = 'topica_structa2'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("topica_structa2",rs.getString(2));
					Assert.assertEquals("topica_classa2",rs.getString(3));
				}
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"RefAttr1.TopicA.StructA0.ref",  "ref_topica_classa2",    "topica_structa1",   "topica_classa2"},
                        {"RefAttr1.TopicA.StructA0.ref",  "ref_topica_classa1",    "topica_structa1",   "topica_classa1"},
                        {"RefAttr1.TopicA.StructA0.ref",  "ref_topica_classa2",    "topica_structa2",   "topica_classa2"},
                        {"RefAttr1.TopicA.ClassC.struct", "topica_classc_struct",  "topica_structa1",   "topica_classc"},
                        {"RefAttr1.TopicA.StructA2.ref",  "aref",  "topica_structa2",   "topica_classa2"},
                        {"RefAttr1.TopicA.StructA0.ref",  "ref_topica_classa1",    "topica_structa2",   "topica_classa1"},
                        {"RefAttr1.TopicA.ClassB.struct", "topica_classb_struct",  "topica_structa1",   "topica_classb"},
                        {"RefAttr1.TopicA.ClassD.struct", "topica_classd_struct",  "topica_structa2",   "topica_classd"},
                        {"RefAttr1.TopicA.StructA1.ref",  "aref",  "topica_structa1",   "topica_classa1"}
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"RefAttr1.TopicA.ClassA1",   "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassA11",  "ch.ehi.ili2db.inheritance", "superClass"},
                        {"RefAttr1.TopicA.StructA2",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassD",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassA0",   "ch.ehi.ili2db.inheritance", "subClass"},
                        {"RefAttr1.TopicA.StructA1",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassC",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassB",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.ClassA2",   "ch.ehi.ili2db.inheritance", "newClass"},
                        {"RefAttr1.TopicA.StructA0",  "ch.ehi.ili2db.inheritance", "subClass"},
                        {"RefAttr1.TopicA.StructA11", "ch.ehi.ili2db.inheritance", "superClass"}
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
	public void importXtfRefAttrFK() throws Exception
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
		        File data=new File("test/data/InheritanceSmart1/RefAttr1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setDatasetName(DATASETNAME);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				//config.setValidation(false);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				
				// inheritance Ref test.
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.ClassA1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("RefAttr1.TopicA.ClassA0",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.ClassA11'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("RefAttr1.TopicA.ClassA1",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.StructA1'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("RefAttr1.TopicA.StructA0",rs.getString(2));
				}
				Assert.assertTrue(stmt.execute("SELECT t_ili2db_inheritance.thisclass, t_ili2db_inheritance.baseclass FROM "+DBSCHEMA+".t_ili2db_inheritance WHERE t_ili2db_inheritance.thisclass = 'RefAttr1.TopicA.StructA11'"));
				{
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("RefAttr1.TopicA.StructA1",rs.getString(2));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
    @Ignore("incompatibility to ili2db-3.x")
	public void exportXtfRefAttrFK_3x() throws Exception
	{
	    EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
	        Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/CreateTableXtfRefAttrFK.sql"));
	        DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader("test/data/InheritanceSmart1/InsertIntoTableXtfRefAttrFK.sql"));
	        
	        File data=new File("test/data/InheritanceSmart1/RefAttr1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setDatasetName(DATASETNAME);
			//config.setValidation(false);
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
				 IomObject obj0 = objs.get("b.3");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
				 Assert.assertEquals("RefAttr1.TopicA.StructA11 {ref -> a11.1 REF {}}", obj0.getattrobj("struct", 0).toString());
			 }
			 {
				 IomObject obj0 = objs.get("b.1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
				 Assert.assertEquals("RefAttr1.TopicA.StructA1 {ref -> a1.1 REF {}}", obj0.getattrobj("struct", 0).toString());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}    
	}
    @Test
    public void exportXtfRefAttrFK() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        importXtfRefAttrFK();
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            
            File data=new File("test/data/InheritanceSmart1/RefAttr1a-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setDatasetName(DATASETNAME);
            //config.setValidation(false);
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
                 IomObject obj0 = objs.get("b.3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("RefAttr1.TopicA.StructA11 {ref -> a11.1 REF {}}", obj0.getattrobj("struct", 0).toString());
             }
             {
                 IomObject obj0 = objs.get("b.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("RefAttr1.TopicA.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("RefAttr1.TopicA.StructA1 {ref -> a1.1 REF {}}", obj0.getattrobj("struct", 0).toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }    
    }
}