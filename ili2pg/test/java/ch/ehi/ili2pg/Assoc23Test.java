package ch.ehi.ili2pg;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Assoc23Test {
	
	private static final String DBSCHEMA = "Assoc23";
	private static final String TEST_OUT="test/data/Assoc23/";
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
    public void importIli_1toN_WithAttr_NewClass() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(null);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
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
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,DBSCHEMA);
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
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,DBSCHEMA);
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
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
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
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,DBSCHEMA);
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
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,DBSCHEMA);
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
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
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
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues,DBSCHEMA);
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
                Ili2dbAssert.assertTrafoTable(jdbcConnection, expectedValues,DBSCHEMA);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void importXtf_1toN_WithAttr_NewClass() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(null);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+DBSCHEMA+".classa1 WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT t_id,a2 FROM "+DBSCHEMA+".assocab2 WHERE b2="+b1_id);
                assertTrue(rs.next());
                long ab_id1=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                rs=stmt.executeQuery("SELECT attra2 FROM "+DBSCHEMA+".assocab2p WHERE t_id="+ab_id1);
                assertFalse(rs.next());

                rs=stmt.executeQuery("SELECT t_id,a2 FROM "+DBSCHEMA+".assocab2 WHERE b2="+b2_id);
                assertTrue(rs.next());
                long ab_id2=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                rs=stmt.executeQuery("SELECT attra2 FROM "+DBSCHEMA+".assocab2p WHERE t_id="+ab_id2);
                assertTrue(rs.next());
                assertEquals("b2_attrA2",rs.getString(1));
                
            }
        }finally{
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
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+DBSCHEMA+".classa1 WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a2,attra2,t_type FROM "+DBSCHEMA+".assocab2 WHERE b2="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                assertEquals("assocab2",rs.getString(3));

                rs=stmt.executeQuery("SELECT a2,attra2,t_type FROM "+DBSCHEMA+".assocab2 WHERE b2="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA2",rs.getString(2));
                assertEquals("assocab2p",rs.getString(3));
                
            }
        }finally{
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
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"Assoc4a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setCreateFk(config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            // verify db content
            {
                ResultSet rs=null;
                rs=stmt.executeQuery("SELECT t_id FROM "+DBSCHEMA+".classa1 WHERE t_ili_tid='a1'");
                assertTrue(rs.next());
                long a1_id=rs.getLong(1);
                
                rs=stmt.executeQuery("SELECT t_id,a0_classa1 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b1'");
                assertTrue(rs.next());
                long b1_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT t_id,a0_classa1 FROM "+DBSCHEMA+".classb1 WHERE t_ili_tid='b2'");
                assertTrue(rs.next());
                long b2_id=rs.getLong(1);
                assertEquals(a1_id,rs.getLong(2));
                
                rs=stmt.executeQuery("SELECT a1_classa1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1_classb1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals(null,rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a1_classa1,attra1 FROM "+DBSCHEMA+".assocab1 WHERE b1_classb1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA1",rs.getString(2));
                
                rs=stmt.executeQuery("SELECT a2_classa1 FROM "+DBSCHEMA+".assocab2 WHERE b2_classb1="+b1_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));

                rs=stmt.executeQuery("SELECT a2_classa1,attra2 FROM "+DBSCHEMA+".assocab2p WHERE b2_classb1="+b2_id);
                assertTrue(rs.next());
                assertEquals(a1_id,rs.getLong(1));
                assertEquals("b2_attrA2",rs.getString(2));
                
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	@Test
	public void importXtfRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1a.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
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
	public void importXtfRefForward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1b.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
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
	public void importXtfRefUnkownFail() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc1z.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
	    		Ili2db.readSettingsFromDb(config);
	    		try{
		    		Ili2db.run(config,null);
		    		Assert.fail();
	    		}catch(Ili2dbException ex){
	    			
	    		}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void importXtfExtRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc2a.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
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
	public void importXtfExtFileRefBackward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				{
					File data=new File(TEST_OUT,"Assoc2b1.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setCreateFk(config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					File data=new File(TEST_OUT,"Assoc2b2.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_IMPORT);
		    		config.setCreateFk(config.CREATE_FK_YES);
		    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		    		config.setCatalogueRefTrafo(null);
		    		config.setMultiSurfaceTrafo(null);
		    		config.setMultilingualTrafo(null);
		    		config.setInheritanceTrafo(null);
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
	public void importXtfExtRefForward() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_OUT,"Assoc2c.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_IMPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
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
	public void exportXtfRefBackward() throws Exception
	{
		{
			importXtfRefBackward();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=null;
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1a-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setCreateFk(config.CREATE_FK_YES);
	    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
	    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
	    		config.setCatalogueRefTrafo(null);
	    		config.setMultiSurfaceTrafo(null);
	    		config.setMultilingualTrafo(null);
	    		config.setInheritanceTrafo(null);
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
	public void exportXtfRefForward() throws Exception
	{
		{
			importXtfRefForward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1b-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
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
	public void exportXtfRefUnkownFail() throws Exception
	{
		{
			importXtfRefUnkownFail();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc1z-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
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
	public void exportXtfExtRefBackward() throws Exception
	{
		{
			importXtfExtRefBackward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc2a-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc2");
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
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
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
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
				 }
			 }
		}
	}
	
	@Test
	public void exportXtfExtFileRefBackward() throws Exception
	{
		{
			importXtfExtFileRefBackward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				{
					data=new File(TEST_OUT,"Assoc2b1-out.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
		    		config.setModels("Assoc2");
		    		Ili2db.readSettingsFromDb(config);
		    		Ili2db.run(config,null);
				}
				{
					data=new File(TEST_OUT,"Assoc2b2-out.xtf");
		    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		    		config.setFunction(Config.FC_EXPORT);
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
				 // assoc2b1
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestB.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("b3",0);
					 assertEquals("b3",obj1.getobjectrefoid());
				 }
				 {
					 IomObject obj0 = objs.get("a1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
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
	public void exportXtfExtRefForward() throws Exception
	{
		{
			importXtfExtRefForward();
		}
		File data=null;
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			{
				data=new File(TEST_OUT,"Assoc2c-out.xtf");
	    		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	    		config.setFunction(Config.FC_EXPORT);
	    		config.setModels("Assoc2");
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
					 Assert.assertEquals("Assoc2.TestA.ClassA1", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a2");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b3");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB3", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("a1b");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassA1b", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("b1");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("Assoc2.TestA.ClassB1", obj0.getobjecttag());
				 }
			 }
		}
	}
    @Test
    public void exportXtf_1toN_WithAttr_NewClass() throws Exception
    {
        {
            importXtf_1toN_WithAttr_NewClass();
        }
        File data=null;
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                data=new File(TEST_OUT,"Assoc4a-out.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
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
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                data=new File(TEST_OUT,"Assoc4a-out.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
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
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            {
                data=new File(TEST_OUT,"Assoc4a-out.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
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
}