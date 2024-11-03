package ch.ehi.ili2db;

import java.io.File;
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

public abstract class FilterImportTest {
	
	protected static final String TEST_OUT = "test/data/FilterImport/";
	
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
	
	@Test
	public void importXtfByBID() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    setup.resetDb();
			{
				File data=new File(TEST_OUT,"FilterImport1a.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
		        Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setBaskets("TestA1");
				config.setImportBid(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);

	            jdbcConnection=setup.createConnection();
	            Statement stmt=null;
	            stmt=jdbcConnection.createStatement();
	            
				// check that only one of the two classa1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+setup.prefixName("classa1");
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(1,rs.getInt(1));
				}
				// check that no classb1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+setup.prefixName("classb1");
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(0,rs.getInt(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfByBID() throws Exception
	{
		{
			importXtfByBID();
		}
		try{
			{
				File data=new File(TEST_OUT,"FilterImport1a-out.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				config.setBaskets("TestA1");
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
					 IomObject obj1 = objs.get("a1");
					 Assert.assertNotNull(obj1);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj1.getobjecttag());
					 
					 IomObject obj2 = objs.get("a2");
					 Assert.assertNull(obj2);
					 
					 IomObject obj3 = objs.get("b1");
					 Assert.assertNull(obj3);
				 }
	        }
		}finally{
		}
	}
	
	@Test
	public void importXtfByTopic() throws Exception
	{
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			{
				File data=new File(TEST_OUT,"FilterImport1a.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
		        config.setDoImplicitSchemaImport(true);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setImportTid(true);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setTopics("FilterImport.TestA");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
		
                jdbcConnection=setup.createConnection();
                Statement stmt=null;
                stmt=jdbcConnection.createStatement();
                
				// check that the classa1 objects from both baskets were imported
				{
					String stmtTxt="SELECT count(*) FROM "+setup.prefixName("classa1");
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(2,rs.getInt(1));
				}
				// check that no classb1 object was imported
				{
					String stmtTxt="SELECT count(*) FROM "+setup.prefixName("classb1");
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals(0,rs.getInt(1));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
	@Test
	public void exportXtfByTopic() throws Exception
	{
		{
			importXtfByTopic();
		}
		try{
			{
				File data=new File(TEST_OUT,"FilterImport1a-out.xtf");
				Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
				config.setFunction(Config.FC_EXPORT);
				config.setExportTid(true);
				config.setTopics("FilterImport.TestA");
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
					 IomObject obj1 = objs.get("a1");
					 Assert.assertNotNull(obj1);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj1.getobjecttag());
					 
					 IomObject obj2 = objs.get("a2");
					 Assert.assertNotNull(obj2);
					 Assert.assertEquals("FilterImport.TestA.ClassA1", obj2.getobjecttag());
					 
					 IomObject obj3 = objs.get("b1");
					 Assert.assertNull(obj3);
				 }
	        }
		}finally{
		}
	}
    @Test
    public void importXtf24SkipBasket() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            {
                File data=new File(TEST_OUT,"SkipBasket.xtf");
                Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setModels("SkipBasket");
                config.setTopics("SkipBasket.TopicA");
                config.setImportBid(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);

                jdbcConnection=setup.createConnection();
                Statement stmt=null;
                stmt=jdbcConnection.createStatement();
                
                // check that only one basket was imported
                {
                    String stmtTxt="SELECT count(*) FROM "+setup.prefixName(DbNames.BASKETS_TAB);
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(1,rs.getInt(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
}
