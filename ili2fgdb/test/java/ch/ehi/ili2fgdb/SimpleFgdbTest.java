package ch.ehi.ili2fgdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class SimpleFgdbTest {
	
	private static final String TEST_OUT="test/data/Simple/";
    private static final String FGDBFILENAME=TEST_OUT+"Simple.gdb";
	private Connection jdbcConnection=null;
	
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2fgdb.FgdbMain().initConfig(config);
		config.setDbfile(FGDBFILENAME);
		config.setDburl(FgdbDriver.BASE_URL+FGDBFILENAME);
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
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Simple23.ili");
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
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importIliStruct() throws Exception
	{
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleStruct23.ili");
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
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importIliInheritanceNewClass() throws Exception
	{
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleInheritance23.ili");
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
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importIliCoord() throws Exception
	{
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleCoord23.ili");
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
		config.setDefaultSrsCode("2056");
		config.setValue(GeneratorFgdb.XY_RESOLUTION, "0.005");
		config.setValue(GeneratorFgdb.XY_TOLERANCE, "0.05");
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Simple23a.xtf");
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
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfStruct() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleStruct23a.xtf");
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
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfInheritanceNewClass() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
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
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfInheritanceSmart1() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfInheritanceSmart2() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfCoord() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleCoord23a.xtf");
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
		config.setDefaultSrsCode("2056");
		config.setValue(GeneratorFgdb.XY_RESOLUTION, "0.0005");
		config.setValue(GeneratorFgdb.XY_TOLERANCE, "0.005");
		//Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}
	
	@Test
	public void importXtfWithDelete() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Simple23a.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setDatasetName("importXtfWithDelete");
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setDeleteMode(Config.DELETE_DATA);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		//Ili2db.readSettingsFromDb(config);
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
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Simple23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Simple23");
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
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			int attrCount=iomObj.getattrcount();
			assertEquals(1,attrCount);
			String oid=iomObj.getobjectoid();
			assertEquals("o1",oid);
			String attrtag=iomObj.getobjecttag();
			assertEquals("Simple23.TestA.ClassA1",attrtag);
			{
				String attr=iomObj.getattrvalue("attr1");
				assertEquals("gugus",attr);
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
	public void exportXtfStruct() throws Exception
	{
		{
			importXtfStruct();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleStruct23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("SimpleStruct23");
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
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			String oid=iomObj.getobjectoid();
			assertEquals("o1",oid);
			String attrtag=iomObj.getobjecttag();
			assertEquals("SimpleStruct23.TestA.ClassB1",attrtag);
			{
				String attr=iomObj.getattrvalue("attrb1");
				assertEquals("gugus",attr);
			}
			assertEquals(2,iomObj.getattrvaluecount("attrb2"));
			{
				String attr=iomObj.getattrobj("attrb2",0).getattrvalue("attra");
				assertEquals("me",attr);
			}
			{
				String attr=iomObj.getattrobj("attrb2",1).getattrvalue("attra");
				assertEquals("do",attr);
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
	public void exportXtfInheritance() throws Exception
	{
		{
			importXtfInheritanceSmart2();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleInheritance23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("SimpleInheritance23");
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
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			{	
				String oid=iomObj.getobjectoid();
				assertEquals("ClassB1.1", oid);
				String attrtag=iomObj.getobjecttag();
				assertEquals("SimpleInheritance23.TestA.ClassB1", attrtag);
				{
					String attr=iomObj.getattrvalue("attrb1");
					assertEquals("gugus",attr);
				}
				assertEquals(2,iomObj.getattrvaluecount("attrb2"));
				{
					String attr=iomObj.getattrobj("attrb2",0).getattrvalue("attra");
					assertEquals("me1",attr);
				}
				{
					String attr=iomObj.getattrobj("attrb2",1).getattrvalue("attra");
					assertEquals("do1",attr);
				}
				{
					String attr=iomObj.getattrvalue("attrc1");
					assertEquals(null,attr);
				}
			}
			event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			iomObj=((ObjectEvent)event).getIomObject();
			{
				String oid=iomObj.getobjectoid();
				assertEquals("ClassC1.2", oid);
				String attrtag=iomObj.getobjecttag();
				assertEquals("SimpleInheritance23.TestA.ClassC1", attrtag);
				{
					String attr=iomObj.getattrvalue("attrb1");
					assertEquals("gugus2",attr);
				}
                assertEquals(2,iomObj.getattrvaluecount("attrb2"));
                {
                    String attr=iomObj.getattrobj("attrb2",0).getattrvalue("attra");
                    assertEquals("me2",attr);
                }
                {
                    String attr=iomObj.getattrobj("attrb2",1).getattrvalue("attra");
                    assertEquals("do2",attr);
                }
				{
					String attr=iomObj.getattrvalue("attrc1");
					assertEquals("fix2",attr);
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
	public void exportXtfCoord() throws Exception
	{
		{
			importXtfCoord();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"SimpleCoord23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("SimpleCoord23");
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
			event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			{
				String oid=iomObj.getobjectoid();
				assertEquals("o1", oid);
				String attrtag=iomObj.getobjecttag();
				assertEquals("SimpleCoord23.TestA.ClassA1", attrtag);
				{
					{
						IomObject coord=iomObj.getattrobj("attr2", 0);
						assertTrue(coord.getattrvalue("C1").equals("2460001.0"));
						assertTrue(coord.getattrvalue("C2").equals("1045001.0"));
					}
				}
			}
			event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj2=((ObjectEvent)event).getIomObject();
			{
				String oid=iomObj2.getobjectoid();
				assertEquals("o2", oid);
				String attrtag=iomObj2.getobjecttag();
				assertEquals("SimpleCoord23.TestA.ClassA1", attrtag);
				{
					{
						IomObject coord=iomObj2.getattrobj("attr2", 0);
						assertTrue(coord.getattrvalue("C1").equals("2460002.0"));
						assertTrue(coord.getattrvalue("C2").equals("1045002.0"));
					}
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}