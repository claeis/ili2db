package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.jts.Iox2jts;
import ch.interlis.iox_j.jts.Iox2jtsException;

public abstract class MultipleGeomAttrsTest {
	
    protected static final String TEST_OUT="test/data/MultipleGeomAttrs/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
	@Test
	public void importIli() throws Exception
	{
	    //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();

		File data=new File(TEST_OUT,"MultipleGeomAttrs1.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setOneGeomPerTable(false);
		Ili2db.run(config,null);
		
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrs1.Topic.ClassA.line", "line", "classa", null},
                    {"MultipleGeomAttrs1.Topic.ClassA.coord", "coord", "classa", null},
                    {"MultipleGeomAttrs1.Topic.ClassA.surface", "surface", "classa", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrs1.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
	}
    @Test
    public void importIliOneGeom() throws Exception
    {
        setup.resetDb();

        File data=new File(TEST_OUT,"MultipleGeomAttrs1.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setOneGeomPerTable(true);
        Ili2db.run(config,null);
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrs1.Topic.ClassA.line", "line", "classa_line", null},
                    {"MultipleGeomAttrs1.Topic.ClassA.coord", "coord", "classa", null},
                    {"MultipleGeomAttrs1.Topic.ClassA.surface", "surface", "classa_surface", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrs1.Topic.ClassA.line:2056(MultipleGeomAttrs1.Topic.ClassA)",  "ch.ehi.ili2db.secondaryTable",  "classa_line"},
                    {"MultipleGeomAttrs1.Topic.ClassA.surface:2056(MultipleGeomAttrs1.Topic.ClassA)",    "ch.ehi.ili2db.secondaryTable",  "classa_surface"},
                    {"MultipleGeomAttrs1.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliExtendedClassSmart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        //config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setOneGeomPerTable(false);
        Ili2db.run(config,null);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classa",null},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb", null},  
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1.coord", "coord",  "classb", null},    
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2.coord", "coord1",  "classb", null}                  
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAp",  "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx",  "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1", "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2", "ch.ehi.ili2db.inheritance", "superClass"  },              
                    
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliExtendedClassSmart1OneGeom() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        //config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setOneGeomPerTable(true);
        Ili2db.run(config,null);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classa_line",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classa_surface",null},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb", null},  
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1.coord", "coord",  "classb_coord", null},    
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2.coord", "coord",  "multpldclasstopicb_classb_coord", null}                  
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAp",  "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx",  "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1", "ch.ehi.ili2db.inheritance", "superClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2", "ch.ehi.ili2db.inheritance", "superClass"  },              
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line:2056(MultipleGeomAttrsExtendedClass.Topic.ClassA)",  "ch.ehi.ili2db.secondaryTable",    "classa_line"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface:2056(MultipleGeomAttrsExtendedClass.Topic.ClassA)",   "ch.ehi.ili2db.secondaryTable",    "classa_surface"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1.coord:2056(MultipleGeomAttrsExtendedClass.TopicB.ClassB)",  "ch.ehi.ili2db.secondaryTable",    "classb_coord"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2.coord:2056(MultipleGeomAttrsExtendedClass.TopicB.ClassB)",  "ch.ehi.ili2db.secondaryTable",    "multpldclasstopicb_classb_coord"}
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliExtendedClassSmart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        //config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setOneGeomPerTable(false);
        Ili2db.run(config,null);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classap", null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classap", null},                
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classap",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx.coord", "coord", "classax",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classax",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classax",null},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2.coord",   "coord", "classb2", null},   
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1.coord",   "coord", "classb1", null},   
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb1", null},  
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb", null},    
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb2", null}                  
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAp",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},                
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},                
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliExtendedClassSmart2OneGeom() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        //config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setOneGeomPerTable(true);
        Ili2db.run(config,null);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classa",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classa_line",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classa_surface",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.coord", "coord", "classap", null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classap_line", null},                
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classap_surface",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx.coord", "coord", "classax",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line",  "line",  "classax_line",null},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface",   "surface",   "classax_surface",null},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb", null},    
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1.coord",   "coord", "classb1", null},   
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb1_geom", null},  
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2.coord",   "coord", "classb2", null},   
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom", "geom",  "classb2_geom", null}                  
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA",   "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAp",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},                
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassAx",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},                
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB1", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB2", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line:2056(MultipleGeomAttrsExtendedClass.Topic.ClassA)", "ch.ehi.ili2db.secondaryTable",  "classa_line"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface:2056(MultipleGeomAttrsExtendedClass.Topic.ClassA)",  "ch.ehi.ili2db.secondaryTable",  "classa_surface"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line:2056(MultipleGeomAttrsExtendedClass.Topic.ClassAp)",    "ch.ehi.ili2db.secondaryTable",  "classap_line"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface:2056(MultipleGeomAttrsExtendedClass.Topic.ClassAp)", "ch.ehi.ili2db.secondaryTable",  "classap_surface"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.line:2056(MultipleGeomAttrsExtendedClass.Topic.ClassAx)",    "ch.ehi.ili2db.secondaryTable",  "classax_line"},
                    {"MultipleGeomAttrsExtendedClass.Topic.ClassA.surface:2056(MultipleGeomAttrsExtendedClass.Topic.ClassAx)", "ch.ehi.ili2db.secondaryTable",  "classax_surface"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom:2056(MultipleGeomAttrsExtendedClass.TopicB.ClassB2)", "ch.ehi.ili2db.secondaryTable",  "classb2_geom"},
                    {"MultipleGeomAttrsExtendedClass.TopicB.ClassB.geom:2056(MultipleGeomAttrsExtendedClass.TopicB.ClassB1)", "ch.ehi.ili2db.secondaryTable",  "classb1_geom"}                
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void importXtfOneGeom() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"MultipleGeomAttrs1a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setOneGeomPerTable(true);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
    @Test
    public void importXtf() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrs1a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setOneGeomPerTable(false);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
    }

    @Test
    public void importXtfExtendedClassSmart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass_a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
//        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
    }
    @Test
    public void importXtfExtendedClassSmart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass_a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
//        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
    }
	
	@Test
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"MultipleGeomAttrs1a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("MultipleGeomAttrs1");
		Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		{
			XtfReader reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			IoxEvent event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("MultipleGeomAttrs1.Topic.ClassA",attrtag);
				
				assertObjectProperties(iomObj);
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
    @Test
    public void exportXtfOneGeom() throws Exception
    {
        {
            importXtfOneGeom();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"MultipleGeomAttrs1a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("MultipleGeomAttrs1");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
            XtfReader reader=new XtfReader(data);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            IoxEvent event=reader.read();
            {
                assertTrue(event instanceof ObjectEvent);
                IomObject iomObj=((ObjectEvent)event).getIomObject();
                String attrtag=iomObj.getobjecttag();
                assertEquals("MultipleGeomAttrs1.Topic.ClassA",attrtag);
                
                assertObjectProperties(iomObj);
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
    }
	
	@Test
    public void exportXtfExtendedClassSmart1() throws Exception
    {
        {
            importXtfExtendedClassSmart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        //Fgdb4j.deleteFileGdb(fgdbFile);
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("MultipleGeomAttrsExtendedClass");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
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
             Assert.assertEquals(6, objs.size());
             {
                 IomObject obj0 = objs.get("ClassA.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassA oid ClassA.1 {coord COORD {C1 2460001.000, C2 1045001.000}, line POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460002.000, C2 1045002.000}, COORD {C1 2460010.000, C2 1045010.000}]}}, surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassAp.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassAp oid ClassAp.1 {coord COORD {C1 2460001.000, C2 1045001.000}, line POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460002.000, C2 1045002.000}, COORD {C1 2460010.000, C2 1045010.000}]}}, surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassAx.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassAx oid ClassAx.1 {coord COORD {C1 2460002.000, C2 1045002.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB oid ClassB.1 {geom COORD {C1 2460001.000, C2 1045001.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB1.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB1 oid ClassB1.1 {coord COORD {C1 2460002.100, C2 1045002.100}, geom COORD {C1 2460002.000, C2 1045002.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB2.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB2 oid ClassB2.1 {coord COORD {C1 2460003.100, C2 1045003.100}, geom COORD {C1 2460003.000, C2 1045003.000}}", obj0.toString());
             }
        }
    }
    @Test
    public void exportXtfExtendedClassSmart2() throws Exception
    {
        {
            importXtfExtendedClassSmart2();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        //Fgdb4j.deleteFileGdb(fgdbFile);
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("MultipleGeomAttrsExtendedClass");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
        }catch(Exception ex){
            EhiLogger.logError(ex);
            Assert.fail();
        }
        {
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
             Assert.assertEquals(6, objs.size());
             {
                 IomObject obj0 = objs.get("ClassA.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassA oid ClassA.1 {coord COORD {C1 2460001.000, C2 1045001.000}, line POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460002.000, C2 1045002.000}, COORD {C1 2460010.000, C2 1045010.000}]}}, surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassAp.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassAp oid ClassAp.1 {coord COORD {C1 2460001.000, C2 1045001.000}, line POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460002.000, C2 1045002.000}, COORD {C1 2460010.000, C2 1045010.000}]}}, surface MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassAx.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.Topic.ClassAx oid ClassAx.1 {coord COORD {C1 2460002.000, C2 1045002.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB oid ClassB.1 {geom COORD {C1 2460001.000, C2 1045001.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB1.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB1 oid ClassB1.1 {coord COORD {C1 2460002.100, C2 1045002.100}, geom COORD {C1 2460002.000, C2 1045002.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("ClassB2.1");
                 Assert.assertEquals("MultipleGeomAttrsExtendedClass.TopicB.ClassB2 oid ClassB2.1 {coord COORD {C1 2460003.100, C2 1045003.100}, geom COORD {C1 2460003.000, C2 1045003.000}}", obj0.toString());
             }
        }
    }

    public void assertObjectProperties(IomObject iomObj) throws Iox2jtsException {
        IomObject coordObj=iomObj.getattrobj("coord", 0);
        {
            assertEquals("2460001.000",coordObj.getattrvalue("C1"));
            assertEquals("1045001.000",coordObj.getattrvalue("C2"));
        }
        IomObject polylineObj=iomObj.getattrobj("line", 0);
        IomObject sequence=polylineObj.getattrobj("sequence", 0);
        {
            IomObject segment=sequence.getattrobj("segment", 0);
            assertEquals("2460002.000",segment.getattrvalue("C1"));
            assertEquals("1045002.000",segment.getattrvalue("C2"));
        }
        {
            IomObject segment=sequence.getattrobj("segment", 1);
            assertEquals("2460010.000",segment.getattrvalue("C1"));
            assertEquals("1045010.000",segment.getattrvalue("C2"));
        }
        IomObject attrObj=iomObj.getattrobj("surface", 0);
        
        // convert
        MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(attrObj, 0, 2056);
        // polygon1
        Geometry polygon1=jtsMultipolygon.getGeometryN(0);
        assertEquals(1,polygon1.getNumGeometries());
        Coordinate[] coords=polygon1.getCoordinates();
        {
            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
            assertEquals(coord, coords[0]);
            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
            assertEquals(coord2, coords[1]);
            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
            assertEquals(coord3, coords[2]);
            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
            assertEquals(coord4, coords[3]);
        }
    }
}