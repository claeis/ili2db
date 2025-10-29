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

public abstract class DocumentationTest {
	
    protected static final String TEST_OUT="test/data/Documentation/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	@Test
	public void importIli() throws Exception
	{
	    //EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"Docu23.ili");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateNlsTab(true);
        Ili2db.run(config,null);
	}
}