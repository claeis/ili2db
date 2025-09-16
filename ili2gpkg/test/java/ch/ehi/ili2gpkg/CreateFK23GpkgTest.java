package ch.ehi.ili2gpkg;

import java.io.File;
import java.sql.Connection;

import org.junit.Ignore;
import org.junit.Test;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;

public class CreateFK23GpkgTest {
	
	private static final String TEST_OUT="test/data/CreateFK23/";
    private static final String GPKGFILENAME=TEST_OUT+"createfk23.gpkg";
    private static final String DBURL="jdbc:sqlite:"+GPKGFILENAME;
    
	protected AbstractTestSetup setup=createTestSetup();
    protected AbstractTestSetup createTestSetup() {
        return new GpkgTestSetup(GPKGFILENAME,DBURL);
    }
		
	@Test
	public void importIli_CreateFK() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"model1.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setCreatescript(TEST_OUT+"importIli_CreateFK.sql");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
    @Test
    public void createScript_CreateFK() throws Exception
    {
        File data=new File(TEST_OUT,"model1.ili");
        File outfile=new File(data.getPath()+"-out.sql");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
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
            data=new File(TEST_OUT,"model1a.xtf");
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
    public void importIli_CreateFKrecursive() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"modelrecursive.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
}