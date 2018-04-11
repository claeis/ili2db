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

public class Datatypes23Test {
	
	private static final String TEST_OUT="test/data/Datatypes23/";
    private static final String FGDBFILENAME=TEST_OUT+"Datatypes23.gdb";
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
	public void endDb() throws Exception {
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
		File data=new File(TEST_OUT,"Datatypes23.ili");
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
	public void importXtfAttr() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Attr.xtf");
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
	public void importXtfLine() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Line.xtf");
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
	public void importXtfSurface() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Surface.xtf");
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
	public void exportXtfLine() throws Exception
	{
		{
			importXtfLine();
		}
		String fileName="Datatypes23Line-out.xtf";
		File data=new File(TEST_OUT,fileName);	
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
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
				assertEquals("Datatypes23.Topic.SimpleLine2",attrtag);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject polylineObj=iomObj.getattrobj("straights2d", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject polylineObj=iomObj.getattrobj("straights3d", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
					assertTrue(segment.getattrvalue("C3").equals("300.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
					assertTrue(segment.getattrvalue("C3").equals("300.0"));
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.Line2",attrtag);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject polylineObj=iomObj.getattrobj("straightsarcs2d", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("A1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("A2").equals("1045004.0"));
					assertTrue(segment.getattrvalue("C1").equals("2460006.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045006.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject polylineObj=iomObj.getattrobj("straightsarcs3d", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
					assertTrue(segment.getattrvalue("C3").equals("300.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("A1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("A2").equals("1045004.0"));
					assertTrue(segment.getattrvalue("C1").equals("2460006.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045006.0"));
					assertTrue(segment.getattrvalue("C3").equals("300.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
					assertTrue(segment.getattrvalue("C3").equals("300.0"));
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
	public void exportXtfSurface() throws Exception
	{
		{
			importXtfSurface();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Surface-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		config.setBasketHandling(null);
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
				assertEquals("Datatypes23.Topic.SimpleSurface2",attrtag);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				assertTrue(multisurface.getattrvaluecount("surface")==1);
				
				IomObject surface=multisurface.getattrobj("surface", 0);
				IomObject boundary=surface.getattrobj("boundary", 0);
				IomObject polylineObj=boundary.getattrobj("polyline", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 3);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				assertTrue(multisurface.getattrvaluecount("surface")==1);
				
				IomObject surface=multisurface.getattrobj("surface", 0);
				IomObject boundary=surface.getattrobj("boundary", 0);
				IomObject polylineObj=boundary.getattrobj("polyline", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045015.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460020.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045015.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 3);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				assertTrue(multisurface.getattrvaluecount("surface")==1);
				
				IomObject surface=multisurface.getattrobj("surface", 0);
				IomObject boundary=surface.getattrobj("boundary", 0);
				IomObject polylineObj=boundary.getattrobj("polyline", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("C1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045010.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 3);
					assertTrue(segment.getattrvalue("C1").equals("2460005.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045005.0"));
				}
			}
			event=reader.read();
			{
				// Datatypes23.Topic.Surface2 oid Surface2.0 {}
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject(); 
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.Surface2",attrtag);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject multisurface=iomObj.getattrobj("surfacearcs2d", 0);
				assertTrue(multisurface.getattrvaluecount("surface")==1);
				
				IomObject surface=multisurface.getattrobj("surface", 0);
				IomObject boundary=surface.getattrobj("boundary", 0);
				IomObject polylineObj=boundary.getattrobj("polyline", 0);
				IomObject sequence=polylineObj.getattrobj("sequence", 0);
				{
					IomObject segment=sequence.getattrobj("segment", 0);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045015.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertTrue(segment.getattrvalue("A1").equals("2460010.0"));
					assertTrue(segment.getattrvalue("A2").equals("1045018.0"));
					assertTrue(segment.getattrvalue("C1").equals("2460020.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045015.0"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 3);
					assertTrue(segment.getattrvalue("C1").equals("2460001.0"));
					assertTrue(segment.getattrvalue("C2").equals("1045001.0"));
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Ignore("datatypes: binbox, xmlbox not valid written")
	@Test
	public void exportXtfAttr() throws Exception
	{
		{
			importXtfAttr();
		}
		EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File dataAttr=new File(TEST_OUT,"Datatypes23Attr-out.xtf");
		Config config=initConfig(dataAttr.getPath(),dataAttr.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
		{
			XtfReader reader=new XtfReader(dataAttr);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			String attrtag=iomObj.getobjecttag();
			assertEquals("Datatypes23.Topic.ClassAttr",attrtag);
			{
				String attr=iomObj.getattrvalue("aufzaehlung");
				assertEquals("mehr.vier",attr);
			}
			{
				String attr=iomObj.getattrvalue("aI32id");
				assertEquals("22",attr);
			}
			{
				String attr=iomObj.getattrvalue("aUuid");
				assertEquals("15B6BCCE-8772-4595-BF82-F727A665FBF3",attr);
			}
			{
				String attr=iomObj.getattrvalue("aStandardid");
				assertEquals("chgAAAAAAAAA0azD",attr);
			}
			{
				String attr=iomObj.getattrvalue("textLimited");
				assertEquals("abc100",attr);
			}
			{
				String attr=iomObj.getattrvalue("textUnlimited");
				assertEquals("abc101",attr);
			}
			{
				String attr=iomObj.getattrvalue("mtextLimited");
				assertEquals("abc200\n" + 
						"end200",attr);
			}
			{
				String attr=iomObj.getattrvalue("mtextUnlimited");
				assertEquals("abc201\n" + 
						"end201",attr);
			}
			{
				String attr=iomObj.getattrvalue("nametext");
				assertEquals("ClassA",attr);
			}
			{
				String attr=iomObj.getattrvalue("uritext");
				assertEquals("mailto:ceis@localhost",attr);
			}
			{
				String attr=iomObj.getattrvalue("horizAlignment");
				assertEquals("Left",attr);
			}
			{
				String attr=iomObj.getattrvalue("vertAlignment");
				assertEquals("Top",attr);
			}
			{
				String attr=iomObj.getattrvalue("aBoolean");
				assertEquals("true",attr);
			}
			{
				String attr=iomObj.getattrvalue("numericInt");
				assertEquals("5",attr);
			}
			{
				String attr=iomObj.getattrvalue("numericDec");
				assertEquals("6.0",attr);
			}
			{
				String attr=iomObj.getattrvalue("aTime");
				assertEquals("09:00:00.000",attr);
			}
			{
				String attr=iomObj.getattrvalue("aDate");
				assertEquals("2002-09-24",attr);
			}
			{
				String attr=iomObj.getattrvalue("aDateTime");
				assertEquals("1900-01-01T12:30:05.000",attr);
			}
			{
				String attr=iomObj.getattrvalue("binbox");
				assertEquals("<BINBLBOX>AAAA</BINBLBOX>",attr);
			}
			{
				String attr=iomObj.getattrvalue("xmlbox");
				assertEquals("<XMLBLBOX><x>\n" + 
						"							<a></a>\n" + 
						"						</x></XMLBLBOX>",attr);
			}
			{
				String attr=iomObj.getattrvalue("aClass");
				assertEquals("DM01AVCH24D.FixpunkteKategorie1.LFP1",attr);
			}
			{
				String attr=iomObj.getattrvalue("aAttribute");
				assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer",attr);
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}