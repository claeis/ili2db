package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class Json23Test {
	
	private static final String TEST_OUT="test/data/Json/";
    private static final String GPKGFILENAME=TEST_OUT+"Json23.gpkg";
	private Connection jdbcConnection=null;
	
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);
		config.setDbfile(GPKGFILENAME);
		config.setDburl("jdbc:sqlite:"+GPKGFILENAME);
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}
	
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	
	@Test
	public void importIli() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
	    if(gpkgFile.exists()){ 
	        File file = new File(gpkgFile.getAbsolutePath());
	        file.delete();
	    }
		File data=new File(TEST_OUT,"Json23.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
        config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
		Ili2db.run(config,null);
		{
		    java.sql.ResultSet rs=null;
	        try{
	            jdbcConnection=DriverManager.getConnection(config.getDburl(),null,null);
	            java.sql.Statement stmt=jdbcConnection.createStatement();
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Json23.TestA.Farbe.r",  "r", "farbe" ,null },
                        {"Json23.TestA.Auto.Farben",  "farben",    "auto",null },  
                        {"Json23.TestA.Farbe.active", "aactive",    "farbe" ,null },
                        {"Json23.TestA.Farbe.g",  "g", "farbe" ,null },
                        {"Json23.TestA.Farbe.name",   "aname", "farbe" ,null },
                        {"Json23.TestA.Farbe.b",  "b", "farbe"                 ,null },        
                    };
                    Ili2dbAssert.assertAttrNameTableFromGpkg(jdbcConnection, expectedValues);
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Json23.TestA.Farbe",    "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Json23.TestA.Auto.Farben",  "ch.ehi.ili2db.jsonTrafo",   "coalesce"},
                        {"Json23.TestA.Auto", "ch.ehi.ili2db.inheritance", "newClass"}
                    };
                    Ili2dbAssert.assertTrafoTableFromGpkg(jdbcConnection, expectedValues);
                }
	            rs=stmt.executeQuery("SELECT mime_type FROM gpkg_data_columns WHERE table_name='auto' AND column_name='farben'");
	            assertTrue(rs.next());
	            assertEquals("application/json",rs.getString(1));
	        }finally {
	            if(rs!=null) {
	                rs.close();
	            }
	        }
		}
	}
	
	
	@Test
	public void importXtf() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    File gpkgFile=new File(GPKGFILENAME);
	    if (gpkgFile.exists()) {
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
	    }
		File data=new File(TEST_OUT,"Json23a.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setJsonTrafo(Config.JSON_TRAFO_COALESCE);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
        {
            java.sql.ResultSet rs=null;
            try{
                jdbcConnection=DriverManager.getConnection(config.getDburl(),null,null);
                java.sql.Statement stmt=jdbcConnection.createStatement();
                rs=stmt.executeQuery("SELECT farben FROM auto WHERE t_ili_tid='1'");
                assertTrue(rs.next());
                assertEquals("[{\"@type\":\"Json23.TestA.Farbe\",\"r\":10,\"g\":11,\"b\":12,\"name\":\"f1\",\"active\":false}"
                        + ",{\"@type\":\"Json23.TestA.Farbe\",\"r\":20,\"g\":21,\"b\":22,\"name\":\"f2\",\"active\":false}]",rs.getString(1));
                rs=stmt.executeQuery("SELECT farben FROM auto WHERE t_ili_tid='2'");
                assertTrue(rs.next());
                rs.getString(1);
                assertEquals(true,rs.wasNull());
            }finally {
                if(rs!=null) {
                    rs.close();
                }
            }
        }
	}
	
	@Test
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"Json23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Json23");
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
			
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			IoxEvent event=reader.read();
			while(event instanceof ObjectEvent) {
	            IomObject iomObj=((ObjectEvent)event).getIomObject();
			    objs.put(iomObj.getobjectoid(),iomObj);
			    event=reader.read();
			}
            assertEquals(2,objs.size());
			assertEquals("Json23.TestA.Auto oid 1 {Farben [Json23.TestA.Farbe {active false, b 12, g 11, name f1, r 10}, Json23.TestA.Farbe {active false, b 22, g 21, name f2, r 20}]}",objs.get("1").toString());
            assertEquals("Json23.TestA.Auto oid 2 {}",objs.get("2").toString());
			assertTrue(event instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
			reader.close();
		}
	}
	
}