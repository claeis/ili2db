package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;
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

/**
 * tests the ili2db feature to import external references as string/text (instead of a db FK)
 */
	
public abstract class ExtRefTest {
	protected static final String TEST_OUT="test/data/ExtRef/";
    
	protected AbstractTestSetup setup=createTestSetup();
	
    abstract protected AbstractTestSetup createTestSetup();
		
	@Test
	public void importIli_noExtRef() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"ExtRef1.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setSqlExtRefCols(Config.NULL);
		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
    @Test
    public void importIli_extRef() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"ExtRef1.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setSqlExtRefCols(Config.SQL_EXTREF_ENABLE);
        config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_noExtRef() throws Exception
    {
        {
            importIli_noExtRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_noExtRef_refUpperCase() throws Exception
    {
        {
            importIli_noExtRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1a_refUpperCase.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_noExtRef_tidUpperCase() throws Exception
    {
        {
            importIli_noExtRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1a_tidUpperCase.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setValidation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_noExtRef_tidUpperCase_differentImports() throws Exception
    {
        {
            importIli_noExtRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        {
            File data=new File(TEST_OUT,"ExtRef1cat_tidUpperCase.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"ExtRef1data_tidUpperCase.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
    }
    @Test
    public void importXtf_extRef() throws Exception
    {
        {
            importIli_extRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1b.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void exportXtf_noExtRef() throws Exception
    {
        {
            importXtf_noExtRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1a-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setTopics("ExtRef1.Topic1");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        XtfReader reader=null;
        try{
            reader=new XtfReader(data);
            assertTrue(reader.read() instanceof StartTransferEvent);
            
            IoxEvent event=reader.read();
            assertTrue(event instanceof StartBasketEvent);
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            event=reader.read();
            while(event instanceof ObjectEvent) {
                IomObject iomObj=((ObjectEvent)event).getIomObject();
                final String oid = getoid(iomObj);
                objs.put(oid,iomObj);
                event=reader.read();
            }
            assertEquals(5,objs.size());
            assertEquals("ExtRef1.Topic1.ClassB1b oid 9f580d78-1707-4955-9ea5-a67f22d53a41 {}",objs.get("9f580d78-1707-4955-9ea5-a67f22d53a41").toString());
            assertEquals("ExtRef1.Topic1.ClassA1 oid d5539ded-b2fd-4398-8605-31890bec8525 {a1_b -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}",objs.get("d5539ded-b2fd-4398-8605-31890bec8525").toString());
            assertEquals("ExtRef1.Topic1.ClassA2 oid 21ea7f04-17bc-491e-abd4-de8b833939cc {strB2 ExtRef1.Topic1.StructA2 {refb3 -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}}",objs.get("21ea7f04-17bc-491e-abd4-de8b833939cc").toString());
            assertEquals("ExtRef1.Topic1.a2 {a2_a -> d5539ded-b2fd-4398-8605-31890bec8525 REF {}, a2_b -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}",objs.get("d5539ded-b2fd-4398-8605-31890bec8525:9f580d78-1707-4955-9ea5-a67f22d53a41").toString());
            assertEquals("ExtRef1.Topic1.ClassC oid 1400a7b6-eac1-443a-a6dd-6aca8c265d71 {progref ExtRef1.TopicA.Katalog_ProgrammRef {Reference -> 5880375a-52cd-4b2d-af50-c3a6fc5c5352 REF {}}}",objs.get("1400a7b6-eac1-443a-a6dd-6aca8c265d71").toString());
            assertTrue(event instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }finally {
            reader.close();
        }
    }
    @Test
    public void exportXtf_extRef() throws Exception
    {
        {
            importXtf_extRef();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef1b-out.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setTopics("ExtRef1.Topic1");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        XtfReader reader=null;
        try{
            reader=new XtfReader(data);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            IoxEvent event=reader.read();
            while(event instanceof ObjectEvent) {
                IomObject iomObj=((ObjectEvent)event).getIomObject();
                final String oid = getoid(iomObj);
                objs.put(oid,iomObj);
                event=reader.read();
            }
            assertEquals(4,objs.size());
            assertEquals("ExtRef1.Topic1.ClassA1 oid d5539ded-b2fd-4398-8605-31890bec8525 {a1_b -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}",objs.get("d5539ded-b2fd-4398-8605-31890bec8525").toString());
            assertEquals("ExtRef1.Topic1.ClassA2 oid 21ea7f04-17bc-491e-abd4-de8b833939cc {strB2 ExtRef1.Topic1.StructA2 {refb3 -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}}",objs.get("21ea7f04-17bc-491e-abd4-de8b833939cc").toString());
            assertEquals("ExtRef1.Topic1.a2 {a2_a -> d5539ded-b2fd-4398-8605-31890bec8525 REF {}, a2_b -> 9f580d78-1707-4955-9ea5-a67f22d53a41 REF {}}",objs.get("d5539ded-b2fd-4398-8605-31890bec8525:9f580d78-1707-4955-9ea5-a67f22d53a41").toString());
            assertEquals("ExtRef1.Topic1.ClassC oid 1400a7b6-eac1-443a-a6dd-6aca8c265d71 {progref ExtRef1.TopicA.Katalog_ProgrammRef {Reference -> 5880375a-52cd-4b2d-af50-c3a6fc5c5352 REF {}}}",objs.get("1400a7b6-eac1-443a-a6dd-6aca8c265d71").toString());
            assertTrue(event instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }finally {
            reader.close();
        }
    }

    private String getoid(IomObject iomObj) {
        if(iomObj.getobjecttag().equals("ExtRef1.Topic1.a2")) {
            return getref(iomObj,"a2_a")+":"+getref(iomObj,"a2_b");
        }
        return iomObj.getobjectoid();
    }

    private String getref(IomObject iomObj, String roleName) {
        return iomObj.getattrobj(roleName, 0).getobjectrefoid();
    }
}