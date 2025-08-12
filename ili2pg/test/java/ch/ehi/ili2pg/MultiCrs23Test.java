package ch.ehi.ili2pg;

import static org.junit.Assert.*;

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
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class MultiCrs23Test {
	private static final String DBSCHEMA = "MultiCrs23";
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
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

            File data=new File("test/data/Crs/MultiCrs23.ili");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setUseEpsgInNames(true);
            config.setModels("MultiCrs23_LV95");
            config.setSrsModelAssignment("MultiCrs23_LV95=MultiCrs23_LV03");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            final String ili2db_attrname_table=DBSCHEMA+"."+DbNames.ATTRNAME_TAB;
            // verify attr name mapping
            {
                String stmtTxt="SELECT "+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+" FROM "+ili2db_attrname_table+" WHERE "+DbNames.ATTRNAME_TAB_ILINAME_COL+"='MultiCrs23_LV95.TestA.ClassA1.attr2:2056'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("attr2_2056",rs.getString(1));
                Assert.assertEquals("classa1",rs.getString(2));
            }
            {
                String stmtTxt="SELECT "+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+" FROM "+ili2db_attrname_table+" WHERE "+DbNames.ATTRNAME_TAB_ILINAME_COL+"='MultiCrs23_LV95.TestA.ClassA1.attr2:21781'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("attr2_21781",rs.getString(1));
                Assert.assertEquals("classa1",rs.getString(2));
            }
            // verify columns exist
            {
                String stmtTxt="SELECT attr2_21781,attr2_2056 FROM "+DBSCHEMA+".classa1";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultiCrs23_LV95.TestA.ClassA1.attr2:21781", "attr2_21781", "classa1", null},   
                    {"MultiCrs23_LV95.TestA.ClassA1.attr1",   "attr1", "classa1", null},
                    {"MultiCrs23_LV95.TestA.ClassA1.attr2:2056",  "attr2_2056",    "classa1", null},   
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultiCrs23_LV95.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
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
    public void importIli_withoutEPSG() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

            File data=new File("test/data/Crs/MultiCrs23woEPSG.ili");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setUseEpsgInNames(true);
            config.setModelSrsCode("MultiCrs23woEPSG_LV95=2056;MultiCrs23woEPSG_LV03=21781");
            config.setModels("MultiCrs23woEPSG_LV95");
            config.setSrsModelAssignment("MultiCrs23woEPSG_LV95=MultiCrs23woEPSG_LV03");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            final String ili2db_attrname_table=DBSCHEMA+"."+DbNames.ATTRNAME_TAB;
            // verify attr name mapping
            {
                String stmtTxt="SELECT "+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+" FROM "+ili2db_attrname_table+" WHERE "+DbNames.ATTRNAME_TAB_ILINAME_COL+"='MultiCrs23woEPSG_LV95.TestA.ClassA1.attr2:2056'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("attr2_2056",rs.getString(1));
                Assert.assertEquals("classa1",rs.getString(2));
            }
            {
                String stmtTxt="SELECT "+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+" FROM "+ili2db_attrname_table+" WHERE "+DbNames.ATTRNAME_TAB_ILINAME_COL+"='MultiCrs23woEPSG_LV95.TestA.ClassA1.attr2:21781'";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("attr2_21781",rs.getString(1));
                Assert.assertEquals("classa1",rs.getString(2));
            }
            // verify columns exist
            {
                String stmtTxt="SELECT attr2_21781,attr2_2056 FROM "+DBSCHEMA+".classa1";
                Assert.assertTrue(stmt.execute(stmtTxt));
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultiCrs23woEPSG_LV95.TestA.ClassA1.attr2:21781", "attr2_21781", "classa1", null},   
                    {"MultiCrs23woEPSG_LV95.TestA.ClassA1.attr1",   "attr1", "classa1", null},
                    {"MultiCrs23woEPSG_LV95.TestA.ClassA1.attr2:2056",  "attr2_2056",    "classa1", null},   
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, DBSCHEMA);
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultiCrs23woEPSG_LV95.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
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
	public void importXtf() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

			File data=new File("test/data/Crs/MultiCrs23.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setDatasetName("Data");
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setUseEpsgInNames(true);
            config.setModels("MultiCrs23_LV95");
            config.setSrsModelAssignment("MultiCrs23_LV95=MultiCrs23_LV03");
			config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// assertions
			ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attr2_2056),st_asewkt(attr2_21781) FROM "+DBSCHEMA+".classa1 ORDER BY t_id ASC;");
			ResultSetMetaData rsmd=rs.getMetaData();
			assertTrue(rs.next());
			assertEquals("SRID=2056;POINT(2460001 1045001)", rs.getObject(1));
            assertEquals(null, rs.getObject(2));
            assertTrue(rs.next());
            assertEquals(null, rs.getObject(1));
            assertEquals("SRID=21781;POINT(460002 45002)", rs.getObject(2));
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importXtf_withoutEPSG() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");        

            File data=new File("test/data/Crs/MultiCrs23woEPSG.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setDatasetName("Data");
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setUseEpsgInNames(true);
            config.setModels("MultiCrs23woEPSG_LV95");
            config.setModelSrsCode("MultiCrs23woEPSG_LV95=2056;MultiCrs23woEPSG_LV03=21781");
            config.setSrsModelAssignment("MultiCrs23woEPSG_LV95=MultiCrs23woEPSG_LV03");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // assertions
            ResultSet rs = stmt.executeQuery("SELECT st_asewkt(attr2_2056),st_asewkt(attr2_21781) FROM "+DBSCHEMA+".classa1 ORDER BY t_id ASC;");
            ResultSetMetaData rsmd=rs.getMetaData();
            assertTrue(rs.next());
            assertEquals("SRID=2056;POINT(2460001 1045001)", rs.getObject(1));
            assertEquals(null, rs.getObject(2));
            assertTrue(rs.next());
            assertEquals(null, rs.getObject(1));
            assertEquals("SRID=21781;POINT(460002 45002)", rs.getObject(2));
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void exportXtf() throws Exception
	{
	    importXtf();
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
	        
	        File data=new File("test/data/Crs/MultiCrs23-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName("Data");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
            TransferDescription td=null;
            Configuration ili2cConfig=new Configuration();
            FileEntry fileEntry=new FileEntry("test/data/Crs/MultiCrs23.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
            td=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
            assertNotNull(td);

			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			IoxReader reader=Xtf24Reader.createReader(data);
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
				 IomObject obj0 = objs.get("1");
				 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV95.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 2460001.000, C2 1045001.000}", obj0.getattrobj("attr2", 0).toString());
			 }
			 {
				 IomObject obj0 = objs.get("2");
				 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV03.TestA.ClassA1", obj0.getobjecttag());
				 Assert.assertEquals("COORD {C1 460002.000, C2 45002.000}", obj0.getattrobj("attr2", 0).toString());
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
    public void exportXtf_LV95() throws Exception
    {
        importXtf();
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("UPDATE "+DBSCHEMA+".classa1 " + 
                    "SET attr2_2056 = ST_GeomFromEWKT('SRID=2056;POINT(2460002.000 1045002.000)') " + 
                    "WHERE attr1='2'");
            
            File data=new File("test/data/Crs/MultiCrs23-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName("Data");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setCrsExportModels("MultiCrs23_LV95");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            TransferDescription td=null;
            Configuration ili2cConfig=new Configuration();
            FileEntry fileEntry=new FileEntry("test/data/Crs/MultiCrs23.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
            td=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
            assertNotNull(td);

            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxReader reader=Xtf24Reader.createReader(data);
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
                 IomObject obj0 = objs.get("1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV95.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 2460001.000, C2 1045001.000}", obj0.getattrobj("attr2", 0).toString());
             }
             {
                 IomObject obj0 = objs.get("2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV95.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 2460002.000, C2 1045002.000}", obj0.getattrobj("attr2", 0).toString());
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
    public void exportXtf_LV03() throws Exception
    {
        importXtf();
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("UPDATE "+DBSCHEMA+".classa1 " + 
                    "SET attr2_21781 = ST_GeomFromEWKT('SRID=21781;POINT(460001.000 45001.000)') " + 
                    "WHERE attr1='1'");
            
            File data=new File("test/data/Crs/MultiCrs23-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName("Data");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setCrsExportModels("MultiCrs23_LV03");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            TransferDescription td=null;
            Configuration ili2cConfig=new Configuration();
            FileEntry fileEntry=new FileEntry("test/data/Crs/MultiCrs23.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
            td=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
            assertNotNull(td);

            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxReader reader=Xtf24Reader.createReader(data);
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
                 IomObject obj0 = objs.get("1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV03.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 460001.000, C2 45001.000}", obj0.getattrobj("attr2", 0).toString());
             }
             {
                 IomObject obj0 = objs.get("2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23_LV03.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 460002.000, C2 45002.000}", obj0.getattrobj("attr2", 0).toString());
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
    public void exportXtf_withoutEPSG() throws Exception
    {
        importXtf_withoutEPSG();
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            
            File data=new File("test/data/Crs/MultiCrs23woEPSG-out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setDatasetName("Data");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            TransferDescription td=null;
            Configuration ili2cConfig=new Configuration();
            FileEntry fileEntry=new FileEntry("test/data/Crs/MultiCrs23woEPSG.ili", FileEntryKind.ILIMODELFILE);
            ili2cConfig.addFileEntry(fileEntry);
            td=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
            assertNotNull(td);

            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxReader reader=Xtf24Reader.createReader(data);
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
                 IomObject obj0 = objs.get("1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23woEPSG_LV95.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 2460001.000, C2 1045001.000}", obj0.getattrobj("attr2", 0).toString());
             }
             {
                 IomObject obj0 = objs.get("2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultiCrs23woEPSG_LV03.TestA.ClassA1", obj0.getobjecttag());
                 Assert.assertEquals("COORD {C1 460002.000, C2 45002.000}", obj0.getattrobj("attr2", 0).toString());
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