package ch.ehi.ili2fgdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class MultipleGeomAttrsTest {
	
	private static final String TEST_OUT="test/data/MultipleGeomAttrs/";
    private static final String FGDBFILENAME=TEST_OUT+"MultipleGeomAttrs.gdb";
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
		File data=new File(TEST_OUT,"MultipleGeomAttrs1.ili");
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
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"MultipleGeomAttrs1a.xtf");
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
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"MultipleGeomAttrs1a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
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
				
				IomObject coordObj=iomObj.getattrobj("coord", 0);
				{
					assertTrue(coordObj.getattrvalue("C1").equals("2460001.0"));
					assertTrue(coordObj.getattrvalue("C2").equals("1045001.0"));
				}
				IomObject polylineObj=iomObj.getattrobj("line", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460002.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045002.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				IomObject attrObj=iomObj.getattrobj("surface", 0);
				IomObject surface=attrObj.getattrobj("surface", 0);
				IomObject boundary=surface.getattrobj("boundary", 0);
				IomObject polylineObj1=boundary.getattrobj("polyline", 0);
				IomObject sequence1=polylineObj1.getattrobj("sequence", 0);
				{
					IomObject segment=sequence1.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
				{
					IomObject segment=sequence1.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence1.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence1.getattrobj("segment", 3);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}