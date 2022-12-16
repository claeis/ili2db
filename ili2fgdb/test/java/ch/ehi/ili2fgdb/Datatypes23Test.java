package ch.ehi.ili2fgdb;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
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
import ch.interlis.iox_j.jts.Iox2jts;

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
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfAttr() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Attr.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		//Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfLine() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Line.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		//Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfSurface() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Surface.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setImportTid(true);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		//Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
	
	@Test
	public void exportXtfLine() throws Exception
	{
		{
			importXtfLine();
		}
		String fileName="Datatypes23Line-out.xtf";
		File data=new File(TEST_OUT,fileName);	
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
					assertEquals("2460001.000",segment.getattrvalue("C1"));
					assertEquals("1045001.000",segment.getattrvalue("C2"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertEquals("2460010.000",segment.getattrvalue("C1"));
					assertEquals("1045010.000",segment.getattrvalue("C2"));
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
					assertEquals("2460001.000",segment.getattrvalue("C1"));
					assertEquals("1045001.000",segment.getattrvalue("C2"));
					assertEquals("300.000",segment.getattrvalue("C3"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertEquals("2460010.000",segment.getattrvalue("C1"));
					assertEquals("1045010.000",segment.getattrvalue("C2"));
					assertEquals("300.000",segment.getattrvalue("C3"));
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
					assertEquals("2460001.000",segment.getattrvalue("C1"));
					assertEquals("1045001.000",segment.getattrvalue("C2"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertEquals("2460005.000",segment.getattrvalue("A1"));
					assertEquals("1045004.000",segment.getattrvalue("A2"));
					assertEquals("2460006.000",segment.getattrvalue("C1"));
					assertEquals("1045006.000",segment.getattrvalue("C2"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertEquals("2460010.000",segment.getattrvalue("C1"));
					assertEquals("1045010.000",segment.getattrvalue("C2"));
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
					assertEquals("2460001.000",segment.getattrvalue("C1"));
					assertEquals("1045001.000",segment.getattrvalue("C2"));
					assertEquals("300.000",segment.getattrvalue("C3"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 1);
					assertEquals("2460005.000",segment.getattrvalue("A1"));
					assertEquals("1045004.000",segment.getattrvalue("A2"));
					assertEquals("2460006.000",segment.getattrvalue("C1"));
					assertEquals("1045006.000",segment.getattrvalue("C2"));
					assertEquals("300.000",segment.getattrvalue("C3"));
				}
				{
					IomObject segment=sequence.getattrobj("segment", 2);
					assertEquals("2460010.000",segment.getattrvalue("C1"));
					assertEquals("1045010.000",segment.getattrvalue("C2"));
					assertEquals("300.000",segment.getattrvalue("C3"));
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
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"Datatypes23Surface-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setExportTid(true);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
		config.setBasketHandling(null);
        Ili2db.run(config,null);
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
				String oid=iomObj.getobjectoid();
				assertEquals("SimpleSurface2.0", oid);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.SimpleSurface2",attrtag);
				String oid=iomObj.getobjectoid();
				assertEquals("SimpleSurface2.1", oid);
				
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(multisurface, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord4, coords[3]);
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.SimpleSurface2",attrtag);
				String oid=iomObj.getobjectoid();
				assertEquals("SimpleSurface2.2", oid);
				
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(multisurface, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045001.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045015.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460020.0"), new Double("1045015.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045001.0"));
					assertEquals(coord4, coords[3]);
					
					com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord5, coords[4]);
					com.vividsolutions.jts.geom.Coordinate coord6=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
					assertEquals(coord6, coords[5]);
					com.vividsolutions.jts.geom.Coordinate coord7=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
					assertEquals(coord7, coords[6]);
					com.vividsolutions.jts.geom.Coordinate coord8=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord8, coords[7]);
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.SimpleSurface2",attrtag);
				String oid=iomObj.getobjectoid();
				assertEquals("SimpleSurface2.3", oid);
				
				IomObject multisurface=iomObj.getattrobj("surface2d", 0);
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(multisurface, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord4, coords[3]);
				}
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject(); 
				String attrtag=iomObj.getobjecttag();
				assertEquals("Datatypes23.Topic.Surface2",attrtag);
				String oid=iomObj.getobjectoid();
				assertEquals("Surface2.0", oid);
			}
			event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				IomObject multisurface=iomObj.getattrobj("surfacearcs2d", 0);
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(multisurface, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045001.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045015.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045018.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460020.0"), new Double("1045015.0"));
					assertEquals(coord4, coords[3]);
					com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("2460001.0"), new Double("1045001.0"));
					assertEquals(coord5, coords[4]);
					
					com.vividsolutions.jts.geom.Coordinate coord6=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord6, coords[5]);
					com.vividsolutions.jts.geom.Coordinate coord7=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
					assertEquals(coord7, coords[6]);
					com.vividsolutions.jts.geom.Coordinate coord8=new com.vividsolutions.jts.geom.Coordinate(new Double("2460007.0"), new Double("1045009.0"));
					assertEquals(coord8, coords[7]);
					com.vividsolutions.jts.geom.Coordinate coord9=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
					assertEquals(coord9, coords[8]);
					com.vividsolutions.jts.geom.Coordinate coord10=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord10, coords[9]);
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
	public void exportXtfAttr() throws Exception
	{
		{
			importXtfAttr();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File dataAttr=new File(TEST_OUT,"Datatypes23Attr-out.xtf");
		Config config=initConfig(dataAttr.getPath(),dataAttr.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Datatypes23");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
				assertEquals("abc100\"\"''",attr);
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
				assertEquals("AAAA",attr);
			}
			{
				DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				String attr=iomObj.getattrvalue("xmlbox");
				is.setCharacterStream(new StringReader(attr));
				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("x");
				Node node=nodes.item(0);
				assertEquals(null,node.getNodeValue());
				NodeList nodes2 = doc.getElementsByTagName("a");
				Node node2=nodes2.item(0);
				assertEquals(null,node2.getNodeValue());
			}
			{
				String attr=iomObj.getattrvalue("aClass");
				assertEquals("DM01AVCH24D.FixpunkteKategorie1.LFP1",attr);
			}
			{
				String attr=iomObj.getattrvalue("aAttribute");
				assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer",attr);
			}
            event=reader.read();
            assertTrue(event instanceof ObjectEvent);
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}