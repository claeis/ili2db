package ch.ehi.ili2db;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class MetaConfigTest {
	
    private static final String DATASETNAME="Test";
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
        config.setDatasetName(DATASETNAME);
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
                    Assert.assertEquals(2, attraValues.size());
                    Assert.assertTrue(attraValues.contains("2f1774b2-8427-4d49-9810-27acb08a7839"));
                    Assert.assertTrue(attraValues.contains("d3663025-735d-4d13-b0f3-fed3496820b7"));
                }
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName("classa2"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("644b3619-952a-4e03-8efa-f7e69bcadf3c"));
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
    public void importXtf_forwardRef() throws Exception
    {
        importIli();
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"ExtRef23a.xtf");
        Config config=setup.initConfig(data.getPath(),TEST_OUT+"importIli.log");
        config.setFunction(Config.FC_IMPORT);
        config.setModeldir(TEST_OUT+"repos");
        config.setMetaConfigFile(IliManager.FILE_URI_PREFIX+TEST_OUT+"ExtRef23-metaForward.ini");
        config.setDatasetName(DATASETNAME);
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
                    Assert.assertEquals(3, attraValues.size());
                    Assert.assertTrue(attraValues.contains("87c68171-79cf-4091-acd1-df21480342d2"));
                    Assert.assertTrue(attraValues.contains("d3663025-735d-4d13-b0f3-fed3496820b7"));
                    Assert.assertTrue(attraValues.contains("2f1774b2-8427-4d49-9810-27acb08a7839"));
                }
                {
                    HashSet<String> attraValues=new HashSet<String>();
                    ResultSet rs=stmt.executeQuery("SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName("classa2"));
                    while(rs.next()) {
                        attraValues.add(rs.getString(1));
                    }
                    Assert.assertEquals(1, attraValues.size());
                    Assert.assertTrue(attraValues.contains("644b3619-952a-4e03-8efa-f7e69bcadf3c"));
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
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
        {
            Connection jdbcConnection=null;
            Statement stmt=null;
            try{
                jdbcConnection = setup.createConnection();
                stmt=jdbcConnection.createStatement();
                stmt.execute("UPDATE "+setup.prefixName("classa2")+" SET attrA2 = NULL");
            }finally {
                if(stmt!=null) {
                    stmt.close();
                }
                if(jdbcConnection!=null) {
                    jdbcConnection.close();
                }
            }
        }
		//EhiLogger.getInstance().setTraceFilter(false);
		File data=new File(TEST_OUT,"ExtRef23a-out.xtf");
		Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setDatasetName(DATASETNAME);
        config.setMetaConfigFile(IliManager.FILE_URI_PREFIX+TEST_OUT+"ExtRef23-metaNoValidation.ini");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        XtfReader reader=null;
		try{
			reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			IoxEvent event=reader.read();
			while(event!=null){
	            if(event instanceof ObjectEvent) {
	                IomObject iomObj=((ObjectEvent)event).getIomObject();
	                String oid=iomObj.getobjectoid();
	                if(oid!=null) {
	                    objs.put(oid,iomObj);
	                }
	            }
                event=reader.read();
			}
			assertEquals(3,objs.size());
			{
	            IomObject iomObj=objs.get("d3663025-735d-4d13-b0f3-fed3496820b7");
	            assertEquals("ExtRef23.TestA.ClassA1",iomObj.getobjecttag());
			}
			{
	            IomObject iomObj=objs.get("2f1774b2-8427-4d49-9810-27acb08a7839");
	            assertEquals("ExtRef23.TestA.ClassA1",iomObj.getobjecttag());
			}
            {
                IomObject iomObj=objs.get("644b3619-952a-4e03-8efa-f7e69bcadf3c");
                assertEquals("ExtRef23.TestA.ClassA2",iomObj.getobjecttag());
                assertNull(iomObj.getattrvalue("attrA2"));
            }
		}finally {
		    if(reader!=null)reader.close();
		}
	}
}