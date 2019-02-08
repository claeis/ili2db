package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
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

public class Oid23GpkgTest {
	private static final String TEST_OUT="test/data/Oid23/";
    private static final String GPKGFILENAME=TEST_OUT+"Oid23.gpkg";
	private Connection jdbcConnection=null;
	private Statement stmt=null;
	
	public void initDb() throws Exception
	{
        jdbcConnection = DriverManager.getConnection("jdbc:sqlite:"+GPKGFILENAME, null, null);
        stmt=jdbcConnection.createStatement();
	}
		
    @Before
    public void setupJdbc() throws Exception
    {
        Class driverClass = Class.forName("org.sqlite.JDBC");
    }

    @After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	
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
	
	@Test
	public void importIli() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            Assert.assertTrue(gpkgFile.delete());
        }
		File data=new File(TEST_OUT,"Oid1.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
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
		initDb();
        {
            // t_ili2db_attrname
            String [][] expectedValues=new String[][] {
                {"Oid1.TestC.ac.a", "a", "classc1", "classa1"},
            };
            Ili2dbAssert.assertAttrNameTableFromGpkg(jdbcConnection, expectedValues);
        }
        {
            // t_ili2db_trafo
            String [][] expectedValues=new String[][] {
                {"Oid1.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Oid1.TestA.ClassB1b", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Oid1.TestC.ac", "ch.ehi.ili2db.inheritance", "embedded"},
                {"Oid1.TestC.ClassC1", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Oid1.TestA.ClassB1", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Oid1.TestA.ClassA1", "ch.ehi.ili2db.inheritance", "newClass"},
            };
            Ili2dbAssert.assertTrafoTableFromGpkg(jdbcConnection, expectedValues);
        }
	}
	
	@Test
	public void importXtf() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            Assert.assertTrue(gpkgFile.delete());
        }
        {
    		File data=new File(TEST_OUT,"Oid1a.xtf");
    		Config config=initConfig(data.getPath(),data.getPath()+".log");
    		config.setFunction(Config.FC_IMPORT);
    		config.setCreateFk(config.CREATE_FK_YES);
    		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
    		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
    		config.setCatalogueRefTrafo(null);
    		config.setMultiSurfaceTrafo(null);
    		config.setMultilingualTrafo(null);
    		config.setInheritanceTrafo(null);
    		config.setValidation(false);
    		config.setImportBid(true);
    		Ili2db.readSettingsFromDb(config);
    		Ili2db.run(config,null);
        }
		{
			//EhiLogger.getInstance().setTraceFilter(false);
			File data=new File(TEST_OUT,"Oid1c.xtf");
			Config config=initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_IMPORT);
    		config.setValidation(false);
            config.setImportBid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"Oid1a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		config.setBaskets("Oid1.TestC");
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		{
			XtfReader reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			{
				String oid=iomObj.getobjectoid();
				assertEquals("1",oid);
				String attrtag=iomObj.getobjecttag();
				assertEquals("Oid1.TestC.ClassC1",attrtag);
				IomObject refObj=iomObj.getattrobj("a", 0);
				String refOid=refObj.getobjectrefoid();
				assertEquals(refOid,"c34c86ec-2a75-4a89-a194-f9ebc422f8bc");
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}