package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
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
import ch.interlis.iox_j.jts.Iox2jts;

public class MultipleGeomAttrsTest {
	
	private static final String TEST_OUT="test/data/MultipleGeomAttrs/";
    private static final String GPKGFILENAME=TEST_OUT+"MultipleGeomAttrs.gpkg";
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
		Ili2db.run(config,null);
	}
    @Test
    public void importIliExtendedClass() throws Exception
    {
        File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){ 
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass.ili");
        Config config=initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_SCHEMAIMPORT);
        //config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        config.setCatalogueRefTrafo(null);
        config.setMultiSurfaceTrafo(null);
        config.setMultilingualTrafo(null);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        Ili2db.run(config,null);
    }
	
	@Test
	public void importXtf() throws Exception
	{
		EhiLogger.getInstance().setTraceFilter(false);
	    File gpkgFile=new File(GPKGFILENAME);
        if (gpkgFile.exists()) {
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
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
		try{
			Ili2db.run(config,null);
		}catch(Exception ex){
			EhiLogger.logError(ex);
			Assert.fail();
		}
	}

    @Test
    public void importXtfExtendedClass() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        File gpkgFile=new File(GPKGFILENAME);
        if (gpkgFile.exists()) {
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass_a.xtf");
        Config config=initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
//        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        config.setCatalogueRefTrafo(null);
        config.setMultiSurfaceTrafo(null);
        config.setMultilingualTrafo(null);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
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
	    File gpkgFile=new File(GPKGFILENAME);
	    //Fgdb4j.deleteFileGdb(fgdbFile);
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
				
				// convert
				MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(attrObj, 0, 2056);
				// polygon1
				Geometry polygon1=jtsMultipolygon.getGeometryN(0);
				assertEquals(1,polygon1.getNumGeometries());
				Coordinate[] coords=polygon1.getCoordinates();
				{
					com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord, coords[0]);
					com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
					assertEquals(coord2, coords[1]);
					com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
					assertEquals(coord3, coords[2]);
					com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
					assertEquals(coord4, coords[3]);
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
	
	@Test
    public void exportXtfExtendedClass() throws Exception
    {
        {
            importXtfExtendedClass();
        }
        EhiLogger.getInstance().setTraceFilter(false);
        File gpkgFile=new File(GPKGFILENAME);
        //Fgdb4j.deleteFileGdb(fgdbFile);
        File data=new File(TEST_OUT,"MultipleGeomAttrsExtendedClass-out.xtf");
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
                assertEquals("MultipleGeomAttrs1ExtendedClass.Topic.ClassA",attrtag);
                
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
                
                // convert
                MultiPolygon jtsMultipolygon=Iox2jts.multisurface2JTS(attrObj, 0, 2056);
                // polygon1
                Geometry polygon1=jtsMultipolygon.getGeometryN(0);
                assertEquals(1,polygon1.getNumGeometries());
                Coordinate[] coords=polygon1.getCoordinates();
                {
                    com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
                    assertEquals(coord, coords[0]);
                    com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("2460010.0"), new Double("1045010.0"));
                    assertEquals(coord2, coords[1]);
                    com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045010.0"));
                    assertEquals(coord3, coords[2]);
                    com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("2460005.0"), new Double("1045005.0"));
                    assertEquals(coord4, coords[3]);
                }
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
    }
}