package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.Main;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class RounderTest {
	
    protected static final String TEST_OUT="test/data/Rounder/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	
    @Test
	public void importXtfWithoutRounding() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"Rounding1a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
		config.setCreateNumChecks(true);
		config.setDisableRounding(true);
		Ili2db.run(config,null);
		
        Connection jdbcConnection = setup.createConnection();
		try{
            Statement stmt=jdbcConnection.createStatement();
            Assert.assertTrue(stmt.execute("SELECT ST_X(lcoord),ST_Y(lcoord) FROM "+setup.prefixName("ClassKoord2")+" WHERE t_ili_tid = 'Coord2.1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(2460001.0001,rs.getDouble(1),0.00005);
                Assert.assertEquals(1045001.0001,rs.getDouble(2),0.00005);
            }
		}finally {
		    jdbcConnection.close();
            jdbcConnection=null;
		}
	}
    @Test
    public void importXtfWithRounding() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Rounding1a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setCreateNumChecks(true);
        Ili2db.run(config,null);
        
        Connection jdbcConnection = setup.createConnection();
        try{
            Statement stmt=jdbcConnection.createStatement();
            Assert.assertTrue(stmt.execute("SELECT ST_X(lcoord),ST_Y(lcoord) FROM "+setup.prefixName("ClassKoord2")+" WHERE t_ili_tid = 'Coord2.1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals(2460001.000,rs.getDouble(1),0.00005);
                Assert.assertEquals(1045001.000,rs.getDouble(2),0.00005);
            }
        }finally {
            jdbcConnection.close();
            jdbcConnection=null;
        }
    }
	
	
	@Test
	public void exportXtfWithRounding() throws Exception
	{
		{
			importXtfWithoutRounding();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"Rounding1a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Rounding23");
        config.setExportTid(true);
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
             Assert.assertEquals(4, objs.size());
             {
                 IomObject obj0 = objs.get("Attr.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.ClassAttr oid Attr.1 {numericDec 1.0}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Coord2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.ClassKoord2 oid Coord2.1 {lcoord COORD {C1 2460001.000, C2 1045001.000}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Line2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.Line2 oid Line2.1 {straightsarcs2d POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, ARC {A1 2460005.000, A2 1045004.000, C1 2460006.000, C2 1045006.000}, COORD {C1 2460010.000, C2 1045010.000}]}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Surface2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.Surface2 oid Surface2.1 {surfacearcs2d MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460020.000, C2 1045015.000}, ARC {A1 2460010.000, A2 1045018.000, C1 2460001.000, C2 1045015.000}, COORD {C1 2460001.000, C2 1045001.000}]}}}}}}", obj0.toString());
             }
		}
	}
    @Test
    public void exportXtfWithoutRounding() throws Exception
    {
        {
            importXtfWithoutRounding();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"Rounding1a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("Rounding23");
        config.setExportTid(true);
        config.setDisableRounding(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
             Assert.assertEquals(4, objs.size());
             {
                 IomObject obj0 = objs.get("Attr.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.ClassAttr oid Attr.1 {numericDec 1.0}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Coord2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.ClassKoord2 oid Coord2.1 {lcoord COORD {C1 2460001.0001, C2 1045001.0001}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Line2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.Line2 oid Line2.1 {straightsarcs2d POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.0, C2 1045001.0}, ARC {A1 2460005.0001, A2 1045004.0001, C1 2460006.0001, C2 1045006.0001}, COORD {C1 2460010.0, C2 1045010.0}]}}}", obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("Surface2.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("Rounding23.Topic.Surface2 oid Surface2.1 {surfacearcs2d MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.0, C2 1045001.0}, COORD {C1 2460020.0001, C2 1045015.0001}, ARC {A1 2460010.0, A2 1045018.0, C1 2460001.0, C2 1045015.0}, COORD {C1 2460001.0, C2 1045001.0}]}}}}}}", obj0.toString());
             }
        }
    }

    private void importXtf24(boolean disableRounding) throws Exception {
        setup.resetDb();
        File data = new File(TEST_OUT, "Rounding24.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setDisableRounding(disableRounding);
        Ili2db.run(config, null);
    }

    private HashMap<String, IomObject> readXtf24Objects(File data) throws Exception {
        Configuration ili2cConfig = new Configuration();
        FileEntry fileEntry = new FileEntry(TEST_OUT + "Rounding24.ili", FileEntryKind.ILIMODELFILE);
        ili2cConfig.addFileEntry(fileEntry);
        TransferDescription td = Main.runCompiler(ili2cConfig);
        Assert.assertNotNull(td);

        HashMap<String, IomObject> objs = new HashMap<>();
        Xtf24Reader reader = new Xtf24Reader(data);
        try {
            reader.setModel(td);
            IoxEvent event;
            do {
                event = reader.read();
                if (event instanceof ObjectEvent) {
                    IomObject iomObj = ((ObjectEvent) event).getIomObject();
                    if (iomObj.getobjectoid() != null) {
                        objs.put(iomObj.getobjectoid(), iomObj);
                    }
                }
            } while (!(event instanceof EndTransferEvent));
        } finally {
            reader.close();
        }
        return objs;
    }

    protected abstract void assertRounding24_classMultiKoord2(Statement stmt) throws Exception;
    protected abstract void assertRounding24_multiLine2(Statement stmt) throws Exception;
    protected abstract void assertRounding24_multiSurface2(Statement stmt) throws Exception;

    @Test
    public void importXtf24MultiWithRounding() throws Exception {
        importXtf24(false);

        try (Connection jdbcConnection = setup.createConnection(); Statement stmt = jdbcConnection.createStatement()) {
            assertRounding24_classMultiKoord2(stmt);
            assertRounding24_multiLine2(stmt);
            assertRounding24_multiSurface2(stmt);
        }
    }

    @Test
    public void exportXtf24MultiWithRounding() throws Exception {
        importXtf24(true);

        File data = new File(TEST_OUT, "Rounding24-out.xtf");
        Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("Rounding24");
        config.setExportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config, null);

        HashMap<String, IomObject> objs = readXtf24Objects(data);
        Assert.assertEquals(3, objs.size());
        {
            IomObject obj = objs.get("MultiCoord2.1");
            Assert.assertNotNull(obj);
            Assert.assertEquals("Rounding24.Topic.ClassMultiKoord2 oid MultiCoord2.1 {lcoord MULTICOORD {coord [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460002.001, C2 1045002.001}]}}", obj.toString());
        }
        {
            IomObject obj = objs.get("MultiLine2.1");
            Assert.assertNotNull(obj);
            Assert.assertEquals("Rounding24.Topic.MultiLine2 oid MultiLine2.1 {straightsarcs2d MULTIPOLYLINE {polyline [POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, ARC {A1 2460005.000, A2 1045004.000, C1 2460006.000, C2 1045006.000}, COORD {C1 2460010.000, C2 1045010.000}]}}, POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460020.000, C2 1045020.000}, COORD {C1 2460030.001, C2 1045030.001}]}}]}}", obj.toString());
        }
        {
            IomObject obj = objs.get("MultiSurface2.1");
            Assert.assertNotNull(obj);
            Assert.assertEquals("Rounding24.Topic.MultiSurface2 oid MultiSurface2.1 {surfacearcs2d MULTISURFACE {surface [SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460020.000, C2 1045015.000}, ARC {A1 2460010.000, A2 1045018.000, C1 2460001.000, C2 1045015.000}, COORD {C1 2460001.000, C2 1045001.000}]}}}}, SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460101.000, C2 1045101.000}, COORD {C1 2460120.001, C2 1045115.001}, COORD {C1 2460101.000, C2 1045115.000}, COORD {C1 2460101.000, C2 1045101.000}]}}}}]}}", obj.toString());
        }
    }
}
