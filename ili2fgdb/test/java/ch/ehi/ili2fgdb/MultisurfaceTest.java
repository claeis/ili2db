package ch.ehi.ili2fgdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

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
import ch.interlis.iox_j.jts.Iox2jts;

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
        File data=new File("test/data/MultiSurface/MultiSurface2a-out.xtf");
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
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(attrObj, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("600030.0"), new Double("200020.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("600045.0"), new Double("200040.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("600010.0"), new Double("200040.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("600030.0"), new Double("200020.0"));
					assertEquals(coord4, coords[3]);
					com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("600015.0"), new Double("200005.0"));
					assertEquals(coord5, coords[4]);
					com.vividsolutions.jts.geom.Coordinate coord6=new com.vividsolutions.jts.geom.Coordinate(new Double("600040.0"), new Double("200010.0"));
					assertEquals(coord6, coords[5]);
					com.vividsolutions.jts.geom.Coordinate coord7=new com.vividsolutions.jts.geom.Coordinate(new Double("600005.0"), new Double("200010.0"));
					assertEquals(coord7, coords[6]);
					com.vividsolutions.jts.geom.Coordinate coord8=new com.vividsolutions.jts.geom.Coordinate(new Double("600015.0"), new Double("200005.0"));
					assertEquals(coord8, coords[7]);
				}
				assertTrue(reader.read() instanceof EndBasketEvent);
				assertTrue(reader.read() instanceof EndTransferEvent);
			}
		}
	}
}