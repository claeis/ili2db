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
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class XtfVersionTest {
	
    protected static final String TEST_OUT="test/data/XtfVersion/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
    @Test
    public void importItf10() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Simple10a.itf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModeldir(Ili2db.ILI_FROM_DB+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+TEST_OUT+"repos");
        Ili2db.run(config,null);
    }
    @Test
    public void importXtf22() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Simple22a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModeldir(Ili2db.ILI_FROM_DB+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+TEST_OUT+"repos");
        Ili2db.run(config,null);
    }
	@Test
	public void importXtf23() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"Simple23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setModeldir(Ili2db.ILI_FROM_DB+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+TEST_OUT+"repos");
		Ili2db.run(config,null);
	}
    @Test
    public void importXtf24() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
        File data=new File(TEST_OUT,"Simple24a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModeldir(Ili2db.ILI_FROM_DB+ch.interlis.ili2c.Main.ILIDIR_SEPARATOR+TEST_OUT+"repos");
        Ili2db.run(config,null);
    }
}