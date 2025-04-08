package ch.ehi.ili2db;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbMetaConfig;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.IoxIliReader;
import ch.interlis.iox_j.inifile.IniFileReader;
import ch.interlis.iox_j.validator.ValidationConfig;

public abstract class SimpleTest {
	
    protected static final String TEST_OUT="test/data/Simple/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	@Test
	public void importIli() throws Exception
	{
        setup.resetDb();
		File data=new File(TEST_OUT,"Simple23.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.run(config,null);
        validateImportIli();
	}
    protected void validateImportIli() throws Exception{
    }

    @Test
    public void createScriptFromIliCoord() throws Exception
    {
        File data=new File(TEST_OUT,"SimpleCoord23.ili");
        File outfile=new File(data.getPath()+"-out.sql");
        Config config=new Config();
        setup.initConfig(config);
        Ili2db.setNoSmartMapping(config);
        config.setLogfile(data.getPath()+".log");
        config.setXtffile(data.getPath());
        config.setFunction(Config.FC_SCRIPT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setCreateMetaInfo(true);
        config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
        config.setCreatescript(outfile.getPath());
        Ili2db.run(config,null);
        
        // verify generated script
        {
            setup.resetDb();
            
            Connection jdbcConnection=setup.createDbSchema();
            try {
                // execute generated script
                DbUtility.executeSqlScript(jdbcConnection, new java.io.FileReader(outfile));
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }

            // rum import without schema generation
            data=new File(TEST_OUT,"SimpleCoord23a.xtf");
            config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(false);
            config.setCreatescript(null);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
        }
    }
	
	@Test
	public void importIliStruct() throws Exception
	{
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleStruct23.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importIliInheritanceNewClass() throws Exception
	{
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleInheritance23.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importIliCoord() throws Exception
	{
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleCoord23.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setDefaultSrsCode("2056");
		setup.setXYParams(config);
		Ili2db.run(config,null);
        validateImportIliCoord();
	}
    @Test
    public void importIli10Coord() throws Exception
    {
        setup.resetDb();
        File data=new File(TEST_OUT,"SimpleCoord10.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsCode("2056");
        setup.setXYParams(config);
        Ili2db.run(config,null);
        validateImportIliCoord();
    }
	
    protected void validateImportIliCoord() throws Exception {
        
    }

    @Test
	public void importXtf() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"Simple23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfStruct() throws Exception
	{
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            //EhiLogger.getInstance().setTraceFilter(false);
            setup.resetDb();
            {
                File data=new File(TEST_OUT,"SimpleStruct23a.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setCreateNumChecks(true);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                Ili2db.run(config,null);
                
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify
            {
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT attra FROM "+setup.prefixName("structa1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(2, attraValues.size());
                    Assert.assertTrue(attraValues.contains("me"));
                    Assert.assertTrue(attraValues.contains("do"));
                }
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT attrb1 FROM "+setup.prefixName("classb1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("gugus"));
                }
            }
        }finally {
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }

		
		
	}
    @Test
    public void importXtfStructWithDelete() throws Exception
    {
        {
            importXtfStruct();
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            {
                //EhiLogger.getInstance().setTraceFilter(false);
                File data=new File(TEST_OUT,"SimpleStruct23b.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                config.setImportTid(true);
                config.setImportBid(true);
                config.setDeleteMode(Config.DELETE_DATA);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // verify
            {
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT attra FROM "+setup.prefixName("structa1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(2, attraValues.size());
                    Assert.assertTrue(attraValues.contains("me2"));
                    Assert.assertTrue(attraValues.contains("do2"));
                }
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT attrb1 FROM "+setup.prefixName("classb1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("gugus2"));
                }
                {
                    ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName(DbNames.BASKETS_TAB));
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(1, rs.getLong(1));
                }
                {
                    ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName(DbNames.DATASETS_TAB));
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(1, rs.getLong(1));
                }
            }
        }finally {
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null) {
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void importXtfInheritanceNewClass() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfInheritanceSmart1() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfInheritanceSmart2() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleInheritance23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setImportTid(true);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        Ili2db.run(config,null);
	}
	
	@Test
	public void importXtfCoord() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    setup.resetDb();
		File data=new File(TEST_OUT,"SimpleCoord23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setImportTid(true);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setDefaultSrsCode("2056");
		setup.setXYParams(config);
        Ili2db.run(config,null);
        validateImportIliCoord();
	}
    @Test
    public void importItfCoord() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"SimpleCoord10a.itf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setDefaultSrsCode("2056");
        setup.setXYParams(config);
        Ili2db.run(config,null);
        validateImportIliCoord();
    }
	
	@Test
	public void importXtfWithDelete() throws Exception
	{
        setup.resetDb();
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"Simple23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
		config.setDoImplicitSchemaImport(true);
		config.setDatasetName("importXtfWithDelete");
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setDeleteMode(Config.DELETE_DATA);
        Ili2db.run(config,null);
	}
	
	@Test
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"Simple23a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("Simple23");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
		{
			XtfReader reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			int attrCount=iomObj.getattrcount();
			assertEquals(1,attrCount);
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
    public void validateXtf() throws Exception
    {
        {
            importXtf();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        File log=new File(TEST_OUT,"Simple23a-validate.log");
        Config config=setup.initConfig(null,log.getPath());
        config.setFunction(Config.FC_VALIDATE);
        config.setModels("Simple23");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void validateXtfFail() throws Exception
    {
        {
            importXtf();
        }
        // modify data in db so that validation fails
        {
            Connection jdbcConnection = setup.createConnection();
            try{
                java.sql.Statement stmt=jdbcConnection.createStatement();
                stmt.executeUpdate("UPDATE "+setup.prefixName("classa1")+" SET attr1='text with newline\n'");
                stmt.close();
                stmt=null;
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        File log=new File(TEST_OUT,"Simple23a-validate.log");
        Config config=setup.initConfig(null,log.getPath());
        config.setFunction(Config.FC_VALIDATE);
        config.setModels("Simple23");
        Ili2db.readSettingsFromDb(config);
        try{
            Ili2db.run(config,null);
            fail();
        }catch(Exception ex){
            assertEquals(1,logCollector.getErrs().size());
            assertEquals("Attribute attr1 must not contain control characters",logCollector.getErrs().get(0).getEventMsg());
        }
    }
	
	@Test
	public void exportXtfStruct() throws Exception
	{
		{
			importXtfStruct();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"SimpleStruct23a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setExportTid(true);
		config.setModels("SimpleStruct23");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
	public void exportXtfInheritanceSmart2() throws Exception
	{
		{
			importXtfInheritanceSmart2();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"SimpleInheritance23a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setExportTid(true);
		config.setModels("SimpleInheritance23");
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
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"SimpleCoord23a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setExportTid(true);
		config.setModels("SimpleCoord23");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
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
                        assertEquals("2460001.000",coord.getattrvalue("C1"));
                        assertEquals("1045001.000",coord.getattrvalue("C2"));
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
						assertEquals("2460002.000",coord.getattrvalue("C1"));
						assertEquals("1045002.000",coord.getattrvalue("C2"));
					}
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
    @Test
    public void exportItfCoord() throws Exception
    {
        {
            importItfCoord();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"SimpleCoord10a-out.itf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("SimpleCoord10");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        {
            TransferDescription td=null;
            ch.interlis.ili2c.config.Configuration ili2cConfig=new ch.interlis.ili2c.config.Configuration();
            ili2cConfig.addFileEntry(new ch.interlis.ili2c.config.FileEntry(TEST_OUT+"SimpleCoord10.ili",ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));
            td=ch.interlis.ili2c.Main.runCompiler(ili2cConfig);
            assertNotNull(td);
            IoxReader reader=new ItfReader2(data,true);
            ((IoxIliReader) reader).setModel(td);
            assertTrue(reader.read() instanceof StartTransferEvent);
            assertTrue(reader.read() instanceof StartBasketEvent);
            IoxEvent event=reader.read();
            assertTrue(event instanceof ObjectEvent);
            IomObject iomObj=((ObjectEvent)event).getIomObject();
            {
                String oid=iomObj.getobjectoid();
                assertEquals("55", oid);
                String attrtag=iomObj.getobjecttag();
                assertEquals("SimpleCoord10.TestA.ClassA1", attrtag);
                {
                    {
                        IomObject coord=iomObj.getattrobj("attr2", 0);
                        assertEquals("2460000.001",coord.getattrvalue("C1"));
                        assertEquals("1045000.001",coord.getattrvalue("C2"));
                    }
                }
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
    }
    
    @Test
    public void exportMetaConfig() throws Exception
    {
        setup.resetDb();
        
        // schema import
        {
            File data=new File(TEST_OUT,"Simple23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.run(config,null);
        }
        
        // export Meta-Config
        File metaConfigFile=new File(TEST_OUT,"exportMetaConfig.ini");
        {
            Config config=setup.initConfig(metaConfigFile.getPath(),metaConfigFile.getPath()+".log");
            config.setFunction(Config.FC_EXPORT_METACONFIG);
            config.setMetaConfigFile(metaConfigFile.getPath());
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            ValidationConfig ini = IniFileReader.readFile(metaConfigFile);
            assertNotNull(ini.getConfigParams(Ili2dbMetaConfig.SECTION_ILI2DB));
            assertEquals(Config.TRUE.toLowerCase(), ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.NO_SMART_MAPPING));
            assertNull(ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.COALESCE_CATALOGUE_REF)); // not set, if no smart mapping
            
            assertEquals(Config.TRUE.toLowerCase(), ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.CREATE_FK));
            assertEquals(Config.TRUE.toLowerCase(), ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.CREATE_NUM_CHECKS));
            assertEquals(Config.TRUE.toLowerCase(), ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.CREATE_TID_COL));
            assertEquals(Config.TRUE.toLowerCase(), ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.CREATE_BASKET_COL));

            assertEquals(Config.FALSE.toLowerCase(),ini.getConfigValue(Ili2dbMetaConfig.SECTION_ILI2DB, Ili2dbMetaConfig.CREATE_GEOM_IDX));
            
            
        }
    }
    
}