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

public abstract class MetaConfigTest {
	
    protected static final String TEST_OUT="test/data/MetaConfig/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	@Test
	public void importIli() throws Exception
	{
        setup.resetDb();
		//File data=new File(TEST_OUT,"Simple23.ili");
		Config config=setup.initConfig(null,TEST_OUT+"importIli.log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setModeldir(TEST_OUT+"repos");
		config.setMetaConfigFile("ilidata:163d16de-83df-4d84-8039-256f2261e226");
        Ili2db.run(config,null);
	}
	
    @Test
	public void importXtf_ReferenceDataOnly() throws Exception
	{
        importIli();
		//EhiLogger.getInstance().setTraceFilter(false);
        Config config=setup.initConfig(null,TEST_OUT+"importIli.log");
        config.setFunction(Config.FC_IMPORT);
        config.setModeldir(TEST_OUT+"repos");
        config.setMetaConfigFile("ilidata:163d16de-83df-4d84-8039-256f2261e226");
        Ili2db.run(config,null);
        // verify import
        {
            Connection jdbcConnection=null;
            Statement stmt=null;
            try{
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName("classa1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("2f1774b2-8427-4d49-9810-27acb08a7839"));
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
	}
    @Test
    public void importXtf() throws Exception
    {
        importIli();
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef23a.xtf");
        Config config=setup.initConfig(data.getPath(),TEST_OUT+"importIli.log");
        config.setFunction(Config.FC_IMPORT);
        config.setModeldir(TEST_OUT+"repos");
        config.setMetaConfigFile("ilidata:163d16de-83df-4d84-8039-256f2261e226");
        Ili2db.run(config,null);
        // verify import
        {
            Connection jdbcConnection=null;
            Statement stmt=null;
            try{
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName("classa1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("2f1774b2-8427-4d49-9810-27acb08a7839"));
                }
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT attrB FROM "+setup.prefixName("classb1"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("d3663025-735d-4d13-b0f3-fed3496820b7"));
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
            
    }
	
	
	//@Test
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
    //@Test
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
    //@Test
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
	
}