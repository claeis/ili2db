package ch.ehi.ili2db;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.DbNames;
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

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
abstract public class Dataset10Test {
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup();
	
	private static final String TEST_OUT="test/data/Dataset10/";
		
	@Test
	public void importItf() throws Exception
	{
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
		    setup.resetDb();
	        { 
				File data=new File(TEST_OUT,"Dataset10a.itf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setImportBid(true);
				final String datasetName="ceis";
				config.setDatasetName(datasetName);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
		            jdbcConnection = setup.createConnection();
		            stmt=jdbcConnection.createStatement();
					String stmtTxt="SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName(DbNames.BASKETS_TAB)+" WHERE "+DbNames.BASKETS_TAB_TOPIC_COL+"='Dataset10.TopicB'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(datasetName+".TopicB",rs.getObject(1));
				}
	        }
		}finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importItf_fileNameWithSpace() throws Exception
    {
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            setup.resetDb();
            { 
                File data_raw=new File(TEST_OUT,"Dataset10a.itf");
                File data=new File(TEST_OUT,"Dataset 10a.itf");
                java.nio.file.Files.copy(data_raw.toPath(),data.toPath(),StandardCopyOption.REPLACE_EXISTING);
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setValidation(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    jdbcConnection = setup.createConnection();
                    stmt=jdbcConnection.createStatement();
                    String stmtTxt="SELECT "+DbNames.T_ILI_TID_COL+" FROM "+setup.prefixName(DbNames.BASKETS_TAB)+" WHERE "+DbNames.BASKETS_TAB_TOPIC_COL+"='Dataset10.TopicB'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void exportItf() throws Exception
	{
		{
			importItf();
		}
		try{
			{
				File data=new File(TEST_OUT,"Dataset10a-out.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				final String datasetName="ceis";
				config.setDatasetName(datasetName);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);

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
				 {
					 {
						 IomObject obj1 = objs.get("11");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicA.TableA", obj1.getobjecttag());
					 }
					 {
						 IomObject obj1 = objs.get("20");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicB.TableB", obj1.getobjecttag());
					 }
					 {
						 IomObject obj1 = objs.get("10");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicA.TableA", obj1.getobjecttag());
					 }
					 {
						 IomObject obj1 = objs.get("21");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicB.TableB", obj1.getobjecttag());
					 }
				}
	        }
		}finally{
		}
	}
	
	@Test
	public void importItfWithDatasetCol() throws Exception
	{
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
		    setup.resetDb();
	        {
				File data=new File(TEST_OUT,"Dataset10b.itf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateDatasetCols(Config.CREATE_DATASET_COL);
				final String datasetName="ceis";
				config.setDatasetName(datasetName);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
                    jdbcConnection = setup.createConnection();
                    stmt=jdbcConnection.createStatement();
					String stmtTxt="SELECT "+DbNames.T_DATASET_COL+" FROM "+setup.prefixName("tablea")+" WHERE "+DbNames.T_ILI_TID_COL+"='10'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(datasetName,rs.getObject(1));
				}
	        }
		}finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportItfWithDatasetCol() throws Exception
	{
		{
			importItfWithDatasetCol();
		}
		try{
			{
				File data=new File(TEST_OUT,"Dataset10b-out.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				final String datasetName="ceis";
				config.setDatasetName(datasetName);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);

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
				 {
					 {
						 IomObject obj1 = objs.get("20");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicB.TableB", obj1.getobjecttag());
					 }
					 {
						 IomObject obj1 = objs.get("10");
						 Assert.assertNotNull(obj1);
						 Assert.assertEquals("Dataset10.TopicA.TableA", obj1.getobjecttag());
					 }
				 }
	        }
		}finally{
		}
	}
}