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
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class MultisurfaceTest {
	
	private static final String TEST_OUT="test/data/MultiSurface/";
    private static final String FGDBFILENAME=TEST_OUT+"MultiSurface.gdb";
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
		File data=new File(TEST_OUT,"MultiSurface2.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultiLineTrafo(null);
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
		File data=new File(TEST_OUT,"MultiSurface2a.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultiLineTrafo(null);
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
	
	@Ignore("unexpected nullPointerException in methode: ch.ehi.ili2db.toxtf.ToXtfRecordConverter.addAttrValue")
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
		File data=new File("test/data/Simple/Simple23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("MultiSurface2");
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
				assertEquals("MultiLine2.TestA.ClassA1",attrtag);
				
				IomObject attrObj=iomObj.getattrobj("geom", 0);
				IomObject multisurface=attrObj.getattrobj("Flaechen", 0);
				{
					IomObject surfaceObj=multisurface.getattrobj("Flaeche", 0);
					{	
						IomObject surface=surfaceObj.getattrobj("surface", 0);
						IomObject boundary=surface.getattrobj("boundary", 0);
						IomObject polylineObj=boundary.getattrobj("polyline", 0);
						IomObject sequence=polylineObj.getattrobj("sequence", 0);
						{
							IomObject segment=sequence.getattrobj("segment", 0);
							assertTrue(segment.getattrvalue("C1").equals("600030.0"));
							assertTrue(segment.getattrvalue("C2").equals("200020.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 1);
							assertTrue(segment.getattrvalue("C1").equals("600045.0"));
							assertTrue(segment.getattrvalue("C2").equals("200040.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 2);
							assertTrue(segment.getattrvalue("C1").equals("600010.0"));
							assertTrue(segment.getattrvalue("C2").equals("200040.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 3);
							assertTrue(segment.getattrvalue("C1").equals("600030.0"));
							assertTrue(segment.getattrvalue("C2").equals("200020.0"));
						}
					}
				}
				IomObject multisurface2=attrObj.getattrobj("Flaechen", 1);
				{
					IomObject surfaceObj=multisurface2.getattrobj("Flaeche", 0);
					{
						IomObject surface=surfaceObj.getattrobj("surface", 0);
						IomObject boundary=surface.getattrobj("boundary", 0);
						IomObject polylineObj=boundary.getattrobj("polyline", 0);
						IomObject sequence=polylineObj.getattrobj("sequence", 0);
						{
							IomObject segment=sequence.getattrobj("segment", 0);
							assertTrue(segment.getattrvalue("C1").equals("600015.0"));
							assertTrue(segment.getattrvalue("C2").equals("200005.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 1);
							assertTrue(segment.getattrvalue("C1").equals("600040.0"));
							assertTrue(segment.getattrvalue("C2").equals("200010.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 2);
							assertTrue(segment.getattrvalue("C1").equals("600005.0"));
							assertTrue(segment.getattrvalue("C2").equals("200010.0"));
						}
						{
							IomObject segment=sequence.getattrobj("segment", 3);
							assertTrue(segment.getattrvalue("C1").equals("600015.0"));
							assertTrue(segment.getattrvalue("C2").equals("200005.0"));
						}
					}
				}
				assertTrue(reader.read() instanceof EndBasketEvent);
				assertTrue(reader.read() instanceof EndTransferEvent);
			}
		}
	}
}