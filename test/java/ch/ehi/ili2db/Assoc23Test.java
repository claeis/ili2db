package ch.ehi.ili2db;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
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
public abstract class Assoc23Test {
	
	protected static final String TEST_DATA_DIR="test/data/Assoc23/";
    private static final String EXTREFFORWARD = "ExtRefForward";
	
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup();
	
    @Test
    public void importIli_1toN_WithAttr_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab0.a0","a0","classb1","classa1"},
                    {"Assoc4.Test.assocab1.a1","a1","assocab1","classa1"},
                    {"Assoc4.Test.assocab2.a2","a2","assocab2","classa1"},
                    {"Assoc4.Test.assocab1.attrA1","attra1","assocab1",null},
                    {"Assoc4.Test.assocab2p.attrA2","attra2","assocab2p",null},
                    {"Assoc4.Test.assocab1.b1","b1","assocab1","classb1"},
                    {"Assoc4.Test.assocab2.b2","b2","assocab2","classb1"  }
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.assocab2","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassA1p","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassB1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassB1p","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassA1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.assocab2p","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.assocab0","ch.ehi.ili2db.inheritance","embedded"}
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_1toN_WithAttr_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab0.a0","a0","classb1","classa1"},
                    {"Assoc4.Test.assocab1.a1","a1","assocab1","classa1"},
                    {"Assoc4.Test.assocab2.a2","a2","assocab2","classa1"},
                    {"Assoc4.Test.assocab1.attrA1","attra1","assocab1",null},
                    {"Assoc4.Test.assocab2p.attrA2","attra2","assocab2",null},
                    {"Assoc4.Test.assocab1.b1","b1","assocab1","classb1"},
                    {"Assoc4.Test.assocab2.b2","b2","assocab2","classb1"}                
                    };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.assocab2","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassA1p","ch.ehi.ili2db.inheritance","superClass"},
                    {"Assoc4.Test.ClassB1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.ClassB1p","ch.ehi.ili2db.inheritance","superClass"},
                    {"Assoc4.Test.ClassA1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc4.Test.assocab2p","ch.ehi.ili2db.inheritance","superClass"},
                    {"Assoc4.Test.assocab0","ch.ehi.ili2db.inheritance","embedded"}
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_1toN_WithAttr_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab0.a0","a0_classa1","classb1","classa1"},
                    {"Assoc4.Test.assocab0.a0","a0_classa1","classb1p","classa1"},
                    {"Assoc4.Test.assocab0.a0","a0_classa1p","classb1","classa1p"},
                    {"Assoc4.Test.assocab0.a0","a0_classa1p","classb1p","classa1p"},
                    {"Assoc4.Test.assocab1.a1","a1_classa1","assocab1","classa1"},
                    {"Assoc4.Test.assocab1.a1","a1_classa1p","assocab1","classa1p"},
                    {"Assoc4.Test.assocab2.a2","a2_classa1","assocab2","classa1"},
                    {"Assoc4.Test.assocab2.a2","a2_classa1","assocab2p","classa1"},
                    {"Assoc4.Test.assocab2.a2","a2_classa1p","assocab2","classa1p"},
                    {"Assoc4.Test.assocab2.a2","a2_classa1p","assocab2p","classa1p"},
                    {"Assoc4.Test.assocab1.attrA1","attra1","assocab1",null},
                    {"Assoc4.Test.assocab2p.attrA2","attra2","assocab2p",null},
                    {"Assoc4.Test.assocab1.b1","b1_classb1","assocab1","classb1"},
                    {"Assoc4.Test.assocab1.b1","b1_classb1p","assocab1","classb1p"},
                    {"Assoc4.Test.assocab2.b2","b2_classb1","assocab2","classb1"},
                    {"Assoc4.Test.assocab2.b2","b2_classb1","assocab2p","classb1"},
                    {"Assoc4.Test.assocab2.b2","b2_classb1p","assocab2","classb1p"},
                    {"Assoc4.Test.assocab2.b2","b2_classb1p","assocab2p","classb1p"}                    
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Assoc4.Test.assocab1","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.assocab2","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.ClassA1p","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.ClassB1","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.ClassB1p","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.ClassA1","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.assocab2p","ch.ehi.ili2db.inheritance","newAndSubClass"},
                    {"Assoc4.Test.assocab0","ch.ehi.ili2db.inheritance","embedded"}
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_NtoN_OR() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_OR.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_OR.TopicA.ClassA.Name","aname","classa",null},
                    {"AssocNtoN_OR.TopicA.ClassB1.Name","aname","classb1",null},
                    {"AssocNtoN_OR.TopicA.ClassB2.Name","aname","classb2",null},
                    {"AssocNtoN_OR.TopicA.a2b.a","a","a2b","classa"},
                    {"AssocNtoN_OR.TopicA.a2b.b","b_classb1","a2b","classb1"},
                    {"AssocNtoN_OR.TopicA.a2b.b","b_classb2","a2b","classb2"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_OR.TopicA.ClassA","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNtoN_OR.TopicA.ClassB1","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNtoN_OR.TopicA.ClassB2","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNtoN_OR.TopicA.a2b","ch.ehi.ili2db.inheritance","newClass"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_1toN_OR() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc1toN_OR.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Assoc1toN_OR.TopicA.ClassA.Name","aname","classa",null},
                    {"Assoc1toN_OR.TopicA.ClassB1.Name","aname","classb1",null},
                    {"Assoc1toN_OR.TopicA.ClassB2.Name","aname","classb2",null},
                    {"Assoc1toN_OR.TopicA.a2b.a","a","classb1","classa"},
                    {"Assoc1toN_OR.TopicA.a2b.a","a","classb2","classa"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Assoc1toN_OR.TopicA.ClassA","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc1toN_OR.TopicA.ClassB1","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc1toN_OR.TopicA.ClassB2","ch.ehi.ili2db.inheritance","newClass"},
                    {"Assoc1toN_OR.TopicA.a2b",   "ch.ehi.ili2db.inheritance", "embedded"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_Nto1_OR() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocNto1_OR.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"AssocNto1_OR.TopicA.ClassA.Name","aname","classa",null},
                    {"AssocNto1_OR.TopicA.ClassB1.Name","aname","classb1",null},
                    {"AssocNto1_OR.TopicA.ClassB2.Name","aname","classb2",null},
                    {"AssocNto1_OR.TopicA.a2b.b","b_classb1","classa","classb1"},
                    {"AssocNto1_OR.TopicA.a2b.b","b_classb2","classa","classb2"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"AssocNto1_OR.TopicA.ClassA","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNto1_OR.TopicA.ClassB1","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNto1_OR.TopicA.ClassB2","ch.ehi.ili2db.inheritance","newClass"},
                    {"AssocNto1_OR.TopicA.a2b",   "ch.ehi.ili2db.inheritance", "embedded"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void importXtf_1toN_WithAttr_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("classa1")+" WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT t_id,a2 FROM "+setup.prefixName("assocab2")+" WHERE b2="+b1_id);
                assertTrue(rs.next());
                long ab_id1=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                rs=stmt.executeQuery("SELECT attra2 FROM "+setup.prefixName("assocab2p")+" WHERE t_id="+ab_id1);
                assertFalse(rs.next());

                rs=stmt.executeQuery("SELECT t_id,a2 FROM "+setup.prefixName("assocab2")+" WHERE b2="+b2_id);
                assertTrue(rs.next());
                long ab_id2=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                rs=stmt.executeQuery("SELECT attra2 FROM "+setup.prefixName("assocab2p")+" WHERE t_id="+ab_id2);
                assertTrue(rs.next());
                assertEquals("b2_attrA2",rs.getString(1));
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_1toN_WithAttr_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("classa1")+" WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a2,attra2,t_type FROM "+setup.prefixName("assocab2")+" WHERE b2="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                assertEquals("assocab2",rs.getString(3));

                rs=stmt.executeQuery("SELECT a2,attra2,t_type FROM "+setup.prefixName("assocab2")+" WHERE b2="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA2",rs.getString(2));
                assertEquals("assocab2p",rs.getString(3));
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_1toN_WithAttr_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"Assoc4a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("classa1")+" WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0_classa1 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0_classa1 FROM "+setup.prefixName("classb1")+" WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1_classa1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1_classb1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1_classa1,attra1 FROM "+setup.prefixName("assocab1")+" WHERE b1_classb1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a2_classa1 FROM "+setup.prefixName("assocab2")+" WHERE b2_classb1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));

                rs=stmt.executeQuery("SELECT a2_classa1,attra2 FROM "+setup.prefixName("assocab2p")+" WHERE b2_classb1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA2",rs.getString(2));
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	@Test
	public void importXtfRefBackward_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			{
				File data=new File(TEST_DATA_DIR,"Assoc1a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	            Ili2db.setNoSmartMapping(config);
	    		config.setFunction(Config.FC_IMPORT);
	            config.setDoImplicitSchemaImport(true);
	    		config.setCreateFk(Config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setImportTid(true);
	    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfRefForward_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			{
				File data=new File(TEST_DATA_DIR,"Assoc1b.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	            Ili2db.setNoSmartMapping(config);
	    		config.setFunction(Config.FC_IMPORT);
	            config.setDoImplicitSchemaImport(true);
	    		config.setCreateFk(Config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setImportTid(true);
	    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfRefUnkownFail_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			{
	            LogCollector logCollector = new LogCollector();
	            EhiLogger.getInstance().addListener(logCollector);

				File data=new File(TEST_DATA_DIR,"Assoc1z.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	            Ili2db.setNoSmartMapping(config);
	    		config.setFunction(Config.FC_IMPORT);
	            config.setDoImplicitSchemaImport(true);
	    		config.setCreateFk(Config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
	    		config.setValidation(false);
	    		Ili2db.readSettingsFromDb(config);
	    		try{
		    		Ili2db.run(config,null);
		    		Assert.fail();
	    		}catch(Ili2dbException ex){
                    ArrayList<String> errorTxt = new ArrayList<String>();
                    errorTxt.add("unknown referenced object Assoc1.Test.ClassA1 TID a1x referenced from Assoc1.Test.a2b1 TID a1x:b1");
                    errorTxt.add("unknown referenced object Assoc1.Test.ClassA2 TID a2x referenced from Assoc1.Test.ClassB2 TID b2");
                    errorTxt.add("unknown referenced object Assoc1.Test.ClassB3 TID b3x referenced from Assoc1.Test.ClassA3 TID a3");
                    errorTxt.add("failed to transfer data from file to db");
                    int counter = 0;
                    for (LogEvent event : logCollector.getErrs()) {
                        if (errorTxt.contains(event.getEventMsg()) ){
                            counter++;
                        }
                    }
                    Assert.assertEquals(4, counter);
	    		}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importXtfRefUnkownSkipErrors_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                LogCollector logCollector = new LogCollector();
                EhiLogger.getInstance().addListener(logCollector);

                File data=new File(TEST_DATA_DIR,"Assoc1z.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setValidation(false);
                config.setSkipReferenceErrors(true);
                config.setSqlNull(Config.SQL_NULL_ENABLE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void importXtfExtRefBackward_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			{
				File data=new File(TEST_DATA_DIR,"Assoc2a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
	    		config.setFunction(Config.FC_IMPORT);
	            config.setDoImplicitSchemaImport(true);
	    		config.setCreateFk(Config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setImportTid(true);
	    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
    @Test
    public void importIliExtRef_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2.ili");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setFunction(Config.FC_SCHEMAIMPORT);
                    config.setCreateFk(Config.CREATE_FK_YES);
                    config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                    config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
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
    public void importIliExtRef_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2.ili");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setFunction(Config.FC_SCHEMAIMPORT);
                    config.setCreateFk(Config.CREATE_FK_YES);
                    config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                    config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
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
    public void importIliExtRef_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2.ili");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    Ili2db.setNoSmartMapping(config);
                    config.setFunction(Config.FC_SCHEMAIMPORT);
                    config.setCreateFk(Config.CREATE_FK_YES);
                    config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                    config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                    config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
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
	public void importXtfExtFileRefBackward_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    {
	        importIliExtRef_Smart0();
	    }
		Connection jdbcConnection=null;
		try{
			{
				{
					File data=new File(TEST_DATA_DIR,"Assoc2b1.xtf");
	                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setImportTid(true);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					File data=new File(TEST_DATA_DIR,"Assoc2b2.xtf");
	                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setImportTid(true);
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
    public void importXtfExtFileRefBackward_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importIliExtRef_Smart1();
        }
        Connection jdbcConnection=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2b1.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportTid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2b2.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportTid(true);
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
    public void importXtfExtFileRefBackward_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            importIliExtRef_Smart2();
        }
        Connection jdbcConnection=null;
        try{
            {
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2b1.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportTid(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    File data=new File(TEST_DATA_DIR,"Assoc2b2.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setImportTid(true);
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
	public void importXtfExtRefForward_Smart0() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            setup.resetDb();
			{
				File data=new File(TEST_DATA_DIR,"Assoc2c.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
	    		config.setFunction(Config.FC_IMPORT);
	            config.setDoImplicitSchemaImport(true);
	    		config.setCreateFk(Config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setImportTid(true);
                config.setImportBid(true);
	    		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
	    		config.setDatasetName(EXTREFFORWARD);
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                // BID=Assoc2.TestB
                // BID=Assoc2.TestA
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName(DbNames.BASKETS_TAB)+" WHERE t_ili_tid='Assoc2.TestB'");
                assertTrue(rs.next());
                long bid_basket1=rs.getLong(1);

                
                rs=stmt.executeQuery("SELECT t_basket FROM "+setup.prefixName("classb2")+" WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_bid=rs.getLong(1);
                assertEquals(bid_basket1,b2_bid);
            }
		}finally{
            if(stmt!=null){
                stmt.close();
            }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void replaceXtfExtRefForward_Smart0() throws Exception
    {
        {
            importXtfExtRefForward_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"Assoc2c.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_REPLACE);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setDatasetName(EXTREFFORWARD);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                // BID=Assoc2.TestB
                // BID=Assoc2.TestA
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName(DbNames.BASKETS_TAB)+" WHERE t_ili_tid='Assoc2.TestB'");
                assertTrue(rs.next());
                long bid_basket1=rs.getLong(1);

                
                rs=stmt.executeQuery("SELECT t_basket FROM "+setup.prefixName("classb2")+" WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_bid=rs.getLong(1);
                assertEquals(bid_basket1,b2_bid);
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void exportXtfRefBackward_Smart0() throws Exception
	{
		{
			importXtfRefBackward_Smart0();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=null;
		Connection jdbcConnection=null;
		try{
			{
				data=new File(TEST_DATA_DIR,"Assoc1a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setExportTid(true);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
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
			 {
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfRefForward_Smart0() throws Exception
	{
		{
			importXtfRefForward_Smart0();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			{
				data=new File(TEST_DATA_DIR,"Assoc1b-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setExportTid(true);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
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
			 {
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc1.Test.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfRefUnkownFail_Smart0() throws Exception
	{
		{
			importXtfRefUnkownFail_Smart0();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			{
				data=new File(TEST_DATA_DIR,"Assoc1z-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc1");
	    		Ili2db.readSettingsFromDb(config);
	    		try{
		    		Ili2db.run(config,null);
	    		}catch(Ili2dbException ex){
	    			Assert.fail();
	    		}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
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
			 {
				int obj0 = objs.size();
				assertEquals(0,obj0);
			 }
		}
	}
	
	
	@Test
	public void exportXtfExtFileRef_Smart0() throws Exception
	{
		{
			importXtfExtFileRefBackward_Smart0();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			{
				{
					data=new File(TEST_DATA_DIR,"Assoc2b2-out.xtf");
	                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
                    config.setExportTid(true);
		    		config.setModels("Assoc2");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
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
			 {
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 // assoc2b2
				 {
					 IomObject obj0 = objs.get("b2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassB2", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("a2",0);
					 assertEquals("a2",obj1.getobjectrefoid());
					 
					 IomObject obj2=obj0.getattrobj("strA2",0);
					 IomObject obj3=obj2.getattrobj("refa2",0);
					 assertEquals("a2",obj3.getobjectrefoid());
				 }
			 }
		}
	}
    @Test
    public void exportXtfExtFileRef_Smart1() throws Exception
    {
        {
            importXtfExtFileRefBackward_Smart1();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                {
                    data=new File(TEST_DATA_DIR,"Assoc2b2-out.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_EXPORT);
                    config.setExportTid(true);
                    config.setModels("Assoc2");
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
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
             {
                 {
                     IomObject obj0 = objs.get("a3");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
                     IomObject obj1=obj0.getattrobj("b3",0);
                     assertEquals("b3",obj1.getobjectrefoid());
                 }
                 // assoc2b2
                 {
                     IomObject obj0 = objs.get("b2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc2.TestB.ClassB2", obj0.getobjecttag());
                     IomObject obj1=obj0.getattrobj("a2",0);
                     assertEquals("a2",obj1.getobjectrefoid());
                     
                     IomObject obj2=obj0.getattrobj("strA2",0);
                     IomObject obj3=obj2.getattrobj("refa2",0);
                     assertEquals("a2",obj3.getobjectrefoid());
                 }
             }
        }
    }
    @Test
    public void exportXtfExtFileRef_Smart2() throws Exception
    {
        {
            importXtfExtFileRefBackward_Smart2();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                {
                    data=new File(TEST_DATA_DIR,"Assoc2b2-out.xtf");
                    Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                    config.setFunction(Config.FC_EXPORT);
                    config.setExportTid(true);
                    config.setModels("Assoc2");
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
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
             {
                 {
                     IomObject obj0 = objs.get("a3");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
                     IomObject obj1=obj0.getattrobj("b3",0);
                     assertEquals("b3",obj1.getobjectrefoid());
                 }
                 // assoc2b2
                 {
                     IomObject obj0 = objs.get("b2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc2.TestB.ClassB2", obj0.getobjecttag());
                     IomObject obj1=obj0.getattrobj("a2",0);
                     assertEquals("a2",obj1.getobjectrefoid());
                     
                     IomObject obj2=obj0.getattrobj("strA2",0);
                     IomObject obj3=obj2.getattrobj("refa2",0);
                     assertEquals("a2",obj3.getobjectrefoid());
                 }
             }
        }
    }
	
	@Test
	public void exportXtfExtRef_Smart0() throws Exception
	{
		{
			importXtfExtRefForward_Smart0();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			{
				data=new File(TEST_DATA_DIR,"Assoc2c-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setExportTid(true);
	    		config.setModels("Assoc2_0;Assoc2");
	    		Ili2db.readSettingsFromDb(config);
	    		Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
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
			 {
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2_0.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2_0.TestA.ClassA2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2_0.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2_0.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2_0.TestA.ClassB1", obj0.getobjecttag());
				 }
			 }
		}
	}
    @Test
    public void exportXtf_1toN_WithAttr_Smart0() throws Exception
    {
        {
            importXtf_1toN_WithAttr_Smart0();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"Assoc4a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Assoc4");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
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
             reader.close();
             {
                 assertEquals(3,objs.size());
                 {
                     IomObject obj0 = objs.get("a1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassA1", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("b1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("b2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     Assert.assertEquals("b2_attrA1",refA1.getattrvalue("attrA1"));
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                     Assert.assertEquals("b2_attrA2",refA2.getattrvalue("attrA2"));
                 }
             }
        }
    }
    @Test
    public void exportXtf_1toN_WithAttr_Smart1() throws Exception
    {
        {
            importXtf_1toN_WithAttr_Smart1();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"Assoc4a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Assoc4");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
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
             reader.close();
             {
                 assertEquals(3,objs.size());
                 {
                     IomObject obj0 = objs.get("a1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassA1", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("b1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("b2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     Assert.assertEquals("b2_attrA1",refA1.getattrvalue("attrA1"));
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                     Assert.assertEquals("b2_attrA2",refA2.getattrvalue("attrA2"));
                 }
             }
        }
    }
    @Test
    public void exportXtf_1toN_WithAttr_Smart2() throws Exception
    {
        {
            importXtf_1toN_WithAttr_Smart2();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"Assoc4a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Assoc4");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
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
             reader.close();
             {
                 assertEquals(3,objs.size());
                 {
                     IomObject obj0 = objs.get("a1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassA1", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("b1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("b2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc4.Test.ClassB1", obj0.getobjecttag());
                     IomObject refA0=obj0.getattrobj("a0",0);
                     Assert.assertNotNull(refA0);
                     assertEquals("a1",refA0.getobjectrefoid());
                     IomObject refA1=obj0.getattrobj("a1",0);
                     Assert.assertNotNull(refA1);
                     assertEquals("a1",refA1.getobjectrefoid());
                     Assert.assertEquals("b2_attrA1",refA1.getattrvalue("attrA1"));
                     IomObject refA2=obj0.getattrobj("a2",0);
                     Assert.assertNotNull(refA2);
                     assertEquals("a1",refA2.getobjectrefoid());
                     Assert.assertEquals("b2_attrA2",refA2.getattrvalue("attrA2"));
                 }
             }
        }
    }
    
    @Test
    public void importIliwithoutOid_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocUpdate1.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                jdbcConnection = setup.createConnection();
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"AssocUpdate1.TestA.a2b.a", "a", "a2b", "classa1"},
                        {"AssocUpdate1.TestA.a2b.b", "b", "a2b", "classb1"},
                        {"AssocUpdate1.TestA.a2b.attrAB", "attrab", "a2b", null},
                        {"AssocUpdate1.TestA.ClassA1.attrA", "attra", "classa1", null},
                        {"AssocUpdate1.TestA.ClassB1.attrB", "attrb", "classb1", null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"AssocUpdate1.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"AssocUpdate1.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"AssocUpdate1.TestA.a2b",     "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfwithoutOid_Smart0() throws Exception
    {
        importIliwithoutOid_Smart0();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocUpdate1a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setDatasetName("AssocUpdate1a");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void updateXtfwithoutOid_Smart0() throws Exception
    {
        importXtfwithoutOid_Smart0();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocUpdate1b.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_UPDATE);
                config.setDatasetName("AssocUpdate1a");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtfwithoutOid_Smart0() throws Exception
    {
        importXtfwithoutOid_Smart0();
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocUpdate1a-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setModels("AssocUpdate1");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void importIli_NtoN_Extended_Smart0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn.Vorname",    "vorname", "besitzerin",  null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude.Name", "aname",   "gebaeude",    null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.BesitzerIn",    "besitzerin",  "gebaeude_besitzerin", "besitzerin"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.Gebaeude",  "gebaeude",    "gebaeude_besitzerin", "gebaeude"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"AssocNtoN_Extended.ExtendedKonst.Gebaeude_BesitzerIn",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn",   "ch.ehi.ili2db.inheritance",   "newClass"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_NtoN_Extended_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn.Vorname",    "vorname", "besitzerin",  null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude.Name", "aname",   "gebaeude",    null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.BesitzerIn",    "besitzerin",  "gebaeude_besitzerin", "besitzerin"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.Gebaeude",  "gebaeude",    "gebaeude_besitzerin", "gebaeude"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"AssocNtoN_Extended.ExtendedKonst.Gebaeude_BesitzerIn",    "ch.ehi.ili2db.inheritance",   "superClass"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn",   "ch.ehi.ili2db.inheritance",   "newClass"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli_NtoN_Extended_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.ili");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn.Vorname",    "vorname", "besitzerin",  null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude.Name", "aname",   "gebaeude",    null},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.BesitzerIn",    "besitzerin",  "gebaeude_besitzerin", "besitzerin"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.Gebaeude",  "gebaeude",    "gebaeude_besitzerin", "gebaeude"},
                    {"AssocNtoN_Extended.ExtendedKonst.Gebaeude_BesitzerIn.BesitzerIn",    "besitzerin",  "asscntn_xtndedextendedkonst_gebaeude_besitzerin", "besitzerin"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn.Gebaeude",  "gebaeude",    "asscntn_xtndedextendedkonst_gebaeude_besitzerin", "gebaeude"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude",  "ch.ehi.ili2db.inheritance",   "newAndSubClass"},
                    {"AssocNtoN_Extended.Konstruktionen.BesitzerIn",    "ch.ehi.ili2db.inheritance",   "newAndSubClass"},
                    {"AssocNtoN_Extended.ExtendedKonst.Gebaeude_BesitzerIn",    "ch.ehi.ili2db.inheritance",   "newAndSubClass"},
                    {"AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn",   "ch.ehi.ili2db.inheritance",   "newAndSubClass"},
                 };
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,setup.getSchema());
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_NtoN_Extended_Smart0() throws Exception
    {
        {
            importIli_NtoN_Extended_Smart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(false);
                config.setImportTid(true);
                config.setImportBid(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g1'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p1'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g2'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p2'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_NtoN_OR() throws Exception
    {
        {
            importIli_NtoN_OR();
        }
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_OR.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_1toN_OR() throws Exception
    {
        {
            importIli_1toN_OR();
        }
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"Assoc1toN_OR.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_Nto1_OR() throws Exception
    {
        {
            importIli_Nto1_OR();
        }
        EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocNto1_OR.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_NtoN_Extended_Smart1() throws Exception
    {
        {
            importIli_NtoN_Extended_Smart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(false);
                config.setImportTid(true);
                config.setImportBid(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g1'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p1'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g2'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p2'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtf_NtoN_Extended_Smart2() throws Exception
    {
        {
            importIli_NtoN_Extended_Smart2();
        }
        // EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                File data=new File(TEST_DATA_DIR,"AssocNtoN_Extended.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(false);
                config.setImportTid(true);
                config.setImportBid(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify db content
            {
                ResultSet rs=null;
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g1'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p1'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                {
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("gebaeude")+" WHERE t_ili_tid='g2'");
                    assertTrue(rs.next());
                    long g1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT t_id FROM "+setup.prefixName("besitzerin")+" WHERE t_ili_tid='p2'");
                    assertTrue(rs.next());
                    long p1_id=rs.getLong(1);
                    
                    rs=stmt.executeQuery("SELECT besitzerin FROM "+setup.prefixName("asscntn_xtndedextendedkonst_gebaeude_besitzerin")+" WHERE gebaeude="+g1_id);
                    assertTrue(rs.next());
                    assertEquals(p1_id,rs.getLong(1));
                }
                
                
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    private String getTid(IomObject obj)
    {
        String tid=obj.getobjectoid();
        if(tid!=null) {
            return tid;
        }
        IomObject refA0=obj.getattrobj("BesitzerIn",0);
        if(refA0!=null) {
            String ref1=refA0.getobjectrefoid();
            IomObject refA1=obj.getattrobj("Gebaeude",0);
            String ref2=refA1.getobjectrefoid();
            return ref1+ref2;
        }
        refA0=obj.getattrobj("a",0);
        if(refA0!=null) {
            String ref1=refA0.getobjectrefoid();
            IomObject refA1=obj.getattrobj("b",0);
            String ref2=refA1.getobjectrefoid();
            return ref1+":"+ref2;
        }
        throw new IllegalArgumentException("unexpected obj "+obj.toString());
    }
    private void assertExportXtf_NtoN_Extended(File data) throws IoxException {
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
                    objs.put(getTid(iomObj), iomObj);
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             reader.close();
             {
                 assertEquals(6,objs.size());
                 {
                     IomObject obj0 = objs.get("g1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.Konstruktionen.Gebaeude", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("g2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.Konstruktionen.Gebaeude", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("p1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.Konstruktionen.BesitzerIn", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("p2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.Konstruktionen.BesitzerIn", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("p1g1");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.Konstruktionen.Gebaeude_BesitzerIn", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("p2g2");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_Extended.ExtendedKonst.Gebaeude_BesitzerIn", obj0.getobjecttag());
                 }
             }
        }
    }
    private void assertExportXtf_NtoN_OR(File data) throws IoxException {
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
                    objs.put(getTid(iomObj), iomObj);
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             reader.close();
             {
                 assertEquals(5,objs.size());
                 {
                     IomObject obj0 = objs.get("10");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_OR.TopicA.ClassA", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("20");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_OR.TopicA.ClassB1", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("30");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_OR.TopicA.ClassB2", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("10:20");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_OR.TopicA.a2b", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("10:30");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNtoN_OR.TopicA.a2b", obj0.getobjecttag());
                 }
             }
        }
    }
    private void assertExportXtf_1toN_OR(File data) throws IoxException {
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
                    objs.put(getTid(iomObj), iomObj);
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             reader.close();
             {
                 assertEquals(3,objs.size());
                 {
                     IomObject obj0 = objs.get("10");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc1toN_OR.TopicA.ClassA", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("20");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc1toN_OR.TopicA.ClassB1", obj0.getobjecttag());
                     IomObject refA=obj0.getattrobj("a",0);
                     Assert.assertNotNull(refA);
                     Assert.assertEquals("10", refA.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("30");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Assoc1toN_OR.TopicA.ClassB2", obj0.getobjecttag());
                     IomObject refA=obj0.getattrobj("a",0);
                     Assert.assertNotNull(refA);
                     Assert.assertEquals("10", refA.getobjectrefoid());
                 }
             }
        }
    }
    private void assertExportXtf_Nto1_OR(File data) throws IoxException {
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
                    objs.put(getTid(iomObj), iomObj);
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             reader.close();
             {
                 assertEquals(4,objs.size());
                 {
                     IomObject obj0 = objs.get("10");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNto1_OR.TopicA.ClassA", obj0.getobjecttag());
                     IomObject refA=obj0.getattrobj("b",0);
                     Assert.assertNotNull(refA);
                     Assert.assertEquals("20", refA.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("11");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNto1_OR.TopicA.ClassA", obj0.getobjecttag());
                     IomObject refA=obj0.getattrobj("b",0);
                     Assert.assertNotNull(refA);
                     Assert.assertEquals("30", refA.getobjectrefoid());
                 }
                 {
                     IomObject obj0 = objs.get("20");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNto1_OR.TopicA.ClassB1", obj0.getobjecttag());
                 }
                 {
                     IomObject obj0 = objs.get("30");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("AssocNto1_OR.TopicA.ClassB2", obj0.getobjecttag());
                 }
             }
        }
    }
    @Test
    public void exportXtf_NtoN_Extended_Smart0() throws Exception
    {
        {
            importXtf_NtoN_Extended_Smart0();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"AssocNtoN_Extended-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("AssocNtoN_Extended");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_NtoN_Extended(data);
    }
    @Test
    public void exportXtf_NtoN_OR() throws Exception
    {
        {
            importXtf_NtoN_OR();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"AssocNtoN_OR-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("AssocNtoN_OR");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_NtoN_OR(data);
    }
    @Test
    public void exportXtf_1toN_OR() throws Exception
    {
        {
            importXtf_1toN_OR();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"Assoc1toN_OR-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Assoc1toN_OR");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_1toN_OR(data);
    }
    @Test
    public void exportXtf_Nto1_OR() throws Exception
    {
        {
            importXtf_Nto1_OR();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"AssocNto1_OR-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("AssocNto1_OR");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_Nto1_OR(data);
    }
    @Test
    public void exportXtf_NtoN_Extended_Smart1() throws Exception
    {
        {
            importXtf_NtoN_Extended_Smart1();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"AssocNtoN_Extended-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("AssocNtoN_Extended");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_NtoN_Extended(data);
    }
    @Test
    public void exportXtf_NtoN_Extended_Smart2() throws Exception
    {
        {
            importXtf_NtoN_Extended_Smart2();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            {
                data=new File(TEST_DATA_DIR,"AssocNtoN_Extended-out.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("AssocNtoN_Extended");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
        assertExportXtf_NtoN_Extended(data);
    }
}