package ch.ehi.ili2pg;

import java.io.File;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class MandatoryChecks23PgTest {
    private final static String DBSCHEMA="mandatorychecks";
    protected static final String TEST_OUT="test/data/MandatoryChecks/";
    protected AbstractTestSetup setup=createTestSetup();
    
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
    
    @Test
    public void importIli_RefAttr_NoSmart() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"RefAttr23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_RefAttr_NoSmart() throws Exception
    {
        {
            importIli_RefAttr_NoSmart();
        }
        File data=new File(TEST_OUT,"RefAttr23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_CatalogRef() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"CatalogRef23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_CatalogRef() throws Exception
    {
        {
            importIli_CatalogRef();
        }
        File data=new File(TEST_OUT,"CatalogRef23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_RefAttr_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"RefAttr23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_RefAttr_Smart1() throws Exception
    {
        {
            importIli_RefAttr_Smart1();
        }
        File data=new File(TEST_OUT,"RefAttr23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_RefAttr_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"RefAttr23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_RefAttr_Smart2() throws Exception
    {
        {
            importIli_RefAttr_Smart2();
        }
        File data=new File(TEST_OUT,"RefAttr23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_Assoc_NoSmart() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Assoc23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_Assoc_NoSmart() throws Exception
    {
        {
            importIli_Assoc_NoSmart();
        }
        File data=new File(TEST_OUT,"Assoc23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_Assoc_Smart1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Assoc23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_Assoc_Smart1() throws Exception
    {
        {
            importIli_Assoc_Smart1();
        }
        File data=new File(TEST_OUT,"Assoc23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void importIli_Assoc_Smart2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Assoc23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
        config.setCreateMandatoryChecks(true);
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf_Assoc_Smart2() throws Exception
    {
        {
            importIli_Assoc_Smart2();
        }
        File data=new File(TEST_OUT,"Assoc23a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(false);
        config.setImportTid(true);
        config.setImportBid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }

}
