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
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

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

public abstract class XtfHeaderTest {
	
    protected static final String TEST_OUT="test/data/XtfHeader/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	public void importXtf23() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
        setup.resetDb();
		File data=new File(TEST_OUT,"Simple23a.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setDatasetName("Simple23a");
        config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		Ili2db.run(config,null);
	}
    @Test
    public void exportXtf23byModels() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtf23();
        File data=new File(TEST_OUT,"Simple23b-out.xtf");
        File referenceData=new File(TEST_OUT,"Simple23b-ref.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setModels("SimpleB");
        Ili2db.run(config,null);
        Diff xmlboxDiff = DiffBuilder.compare(Input.fromFile(referenceData)).withTest(Input.fromFile(data))
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .checkForSimilar().normalizeWhitespace().build();
          Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());                
    }
    @Test
    public void exportXtf23byTopics() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtf23();
        File data=new File(TEST_OUT,"Simple23b-out.xtf");
        File referenceData=new File(TEST_OUT,"Simple23b-ref.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setTopics("SimpleB.TestB");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        Diff xmlboxDiff = DiffBuilder.compare(Input.fromFile(referenceData)).withTest(Input.fromFile(data))
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .checkForSimilar().normalizeWhitespace().build();
          Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());                
    }
    @Test
    public void exportXtf23byDataset() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtf23();
        File data=new File(TEST_OUT,"Simple23a-out.xtf");
        File referenceData=new File(TEST_OUT,"Simple23a-ref.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setDatasetName("Simple23a");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        Diff xmlboxDiff = DiffBuilder.compare(Input.fromFile(referenceData)).withTest(Input.fromFile(data))
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .checkForSimilar().normalizeWhitespace().build();
          Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());                
    }
    @Test
    public void exportXtf23byBaskets() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtf23();
        File data=new File(TEST_OUT,"Simple23a-out.xtf");
        File referenceData=new File(TEST_OUT,"Simple23a-ref.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setBaskets("0075e866-4e5d-4645-9b2d-87db843002cd");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        Diff xmlboxDiff = DiffBuilder.compare(Input.fromFile(referenceData)).withTest(Input.fromFile(data))
                .withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
                .checkForSimilar().normalizeWhitespace().build();
          Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());                
    }
}