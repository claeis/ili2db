package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public abstract class MultilingualTextTest {
    
    protected static final String TEST_OUT="test/data/MultilingualText/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	private static final String DBSCHEMA = "MultilingualText";
	
    @Test
    public void importIliSmartChbase() throws Exception
    {
        setup.resetDb();
        //EhiLogger.getInstance().setTraceFilter(false);

        File data=new File(TEST_OUT,"MultilingualText1.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
        config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);

        {
            Connection jdbcConnection = setup.createConnection();
            try{
                java.sql.Statement stmt=jdbcConnection.createStatement();
                stmt.close();
                stmt=null;
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    
	@Test
	public void importXtfSmartChbase() throws Exception
	{
	    {
            importIliSmartChbase();
	        
	    }
	    //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"MultilingualText1a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);

        {
            Connection jdbcConnection = setup.createConnection();
            try{
                java.sql.Statement stmt=jdbcConnection.createStatement();
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'a1.1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("a1.1-null", rs.getString(1));
                    Assert.assertEquals("a1.1-de", rs.getString(2));
                    Assert.assertEquals("a1.1-fr", rs.getString(3));
                    Assert.assertEquals("a1.1-it", rs.getString(4));
                    Assert.assertEquals("a1.1-rm", rs.getString(5));
                    Assert.assertEquals("a1.1-en", rs.getString(6));
                    
                    Assert.assertEquals("a1.1-de", rs.getString(7));
                    Assert.assertEquals("de", rs.getString(8));
                    
                    Assert.assertEquals("a1.1-null", rs.getString(9));
                    Assert.assertEquals("a1.1-de", rs.getString(10));
                    Assert.assertEquals("a1.1-fr", rs.getString(11));
                    Assert.assertEquals("a1.1-it", rs.getString(12));
                    Assert.assertEquals("a1.1-rm", rs.getString(13));
                    Assert.assertEquals("a1.1-en", rs.getString(14));
                    Assert.assertEquals("a1.1-de", rs.getString(15));
                    Assert.assertEquals("de", rs.getString(16));
                }
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'a1.2'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(null, rs.getString(1));
                    Assert.assertEquals(null, rs.getString(2));
                    Assert.assertEquals(null, rs.getString(3));
                    Assert.assertEquals(null, rs.getString(4));
                    Assert.assertEquals(null, rs.getString(5));
                    Assert.assertEquals(null, rs.getString(6));
                    
                    Assert.assertEquals(null, rs.getString(7));
                    Assert.assertEquals(null, rs.getString(8));
                    
                    Assert.assertEquals(null, rs.getString(9));
                    Assert.assertEquals(null, rs.getString(10));
                    Assert.assertEquals(null, rs.getString(11));
                    Assert.assertEquals(null, rs.getString(12));
                    Assert.assertEquals(null, rs.getString(13));
                    Assert.assertEquals(null, rs.getString(14));
                    
                    Assert.assertEquals(null, rs.getString(15));
                    Assert.assertEquals(null, rs.getString(16));
                }
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classb1 WHERE classb1.t_ili_tid = 'b1.1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    
                    Assert.assertEquals(null, rs.getString(1));
                    Assert.assertEquals("b1.1-de", rs.getString(2));
                    Assert.assertEquals(null, rs.getString(3));
                    Assert.assertEquals(null, rs.getString(4));
                    Assert.assertEquals(null, rs.getString(5));
                    Assert.assertEquals(null, rs.getString(6));
                    
                    Assert.assertEquals("b1.1-fr", rs.getString(7));
                    Assert.assertEquals("fr", rs.getString(8));
                    
                    Assert.assertEquals(null, rs.getString(9));
                    Assert.assertEquals("b1.1-de", rs.getString(10));
                    Assert.assertEquals(null, rs.getString(11));
                    Assert.assertEquals(null, rs.getString(12));
                    Assert.assertEquals(null, rs.getString(13));
                    Assert.assertEquals(null, rs.getString(14));
                    
                    Assert.assertEquals("b1.1-fr", rs.getString(15));
                    Assert.assertEquals("fr", rs.getString(16));
                }
                stmt.close();
                stmt=null;
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
	}
	
	@Test
	public void exportXtfSmartChbase() throws Exception
	{
	    {
	        importXtfSmartChbase();
	    }
		Connection jdbcConnection=null;
		try{

			File data=new File(TEST_OUT,"MultilingualText1a-out.xtf");
	        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setModels("MultilingualText1");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			//EhiLogger.getInstance().setTraceFilter(false);
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
				 IomObject obj0 = objs.get("a1.1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultilingualText0.TestA.ClassA1 oid a1.1 {atext LocalisationCH_V1.MultilingualText {LocalisedText [LocalisationCH_V1.LocalisedText {Text a1.1-null}, LocalisationCH_V1.LocalisedText {Language de, Text a1.1-de}, LocalisationCH_V1.LocalisedText {Language fr, Text a1.1-fr}, LocalisationCH_V1.LocalisedText {Language rm, Text a1.1-rm}, LocalisationCH_V1.LocalisedText {Language it, Text a1.1-it}, LocalisationCH_V1.LocalisedText {Language en, Text a1.1-en}]}, btext LocalisationCH_V1.LocalisedText {Language de, Text a1.1-de}, ctext LocalisationCH_V1.MultilingualMText {LocalisedText [LocalisationCH_V1.LocalisedMText {Text a1.1-null}, LocalisationCH_V1.LocalisedMText {Language de, Text a1.1-de}, LocalisationCH_V1.LocalisedMText {Language fr, Text a1.1-fr}, LocalisationCH_V1.LocalisedMText {Language rm, Text a1.1-rm}, LocalisationCH_V1.LocalisedMText {Language it, Text a1.1-it}, LocalisationCH_V1.LocalisedMText {Language en, Text a1.1-en}]}, dtext LocalisationCH_V1.LocalisedMText {Language de, Text a1.1-de}}"
				         , obj0.toString());
			 }
             {
                 IomObject obj0 = objs.get("a1.2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultilingualText0.TestA.ClassA1 oid a1.2 {}"
                         , obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("b1.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultilingualText0.TestA.ClassB1 oid b1.1 {atext LocalisationCH_V1.MultilingualText {LocalisedText LocalisationCH_V1.LocalisedText {Language de, Text b1.1-de}}, btext LocalisationCH_V1.LocalisedText {Language fr, Text b1.1-fr}, ctext LocalisationCH_V1.MultilingualMText {LocalisedText LocalisationCH_V1.LocalisedMText {Language de, Text b1.1-de}}, dtext LocalisationCH_V1.LocalisedMText {Language fr, Text b1.1-fr}}"
                         , obj0.toString());
             }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importIliSmartCustom() throws Exception
    {
        setup.resetDb();
        //EhiLogger.getInstance().setTraceFilter(false);

        File data=new File(TEST_OUT,"MultilingualTextCustom.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
        config.setLocalisedTrafo(Config.LOCALISED_TRAFO_EXPAND);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);

        {
            Connection jdbcConnection = setup.createConnection();
            try{
                java.sql.Statement stmt=jdbcConnection.createStatement();
                stmt.close();
                stmt=null;
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    
    @Test
    public void importXtfSmartCustom() throws Exception
    {
        {
            importIliSmartCustom();
            
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        File data=new File(TEST_OUT,"MultilingualTextCustom-a.xtf");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_IMPORT);
        config.setImportTid(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);

        {
            Connection jdbcConnection = setup.createConnection();
            try{
                java.sql.Statement stmt=jdbcConnection.createStatement();
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'a1.1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("a1.1-null", rs.getString(1));
                    Assert.assertEquals("a1.1-de", rs.getString(2));
                    Assert.assertEquals("a1.1-fr", rs.getString(3));
                    Assert.assertEquals("a1.1-it", rs.getString(4));
                    Assert.assertEquals("a1.1-rm", rs.getString(5));
                    Assert.assertEquals("a1.1-en", rs.getString(6));
                    
                    Assert.assertEquals("a1.1-de", rs.getString(7));
                    Assert.assertEquals("de", rs.getString(8));
                    
                    Assert.assertEquals("a1.1-null", rs.getString(9));
                    Assert.assertEquals("a1.1-de", rs.getString(10));
                    Assert.assertEquals("a1.1-fr", rs.getString(11));
                    Assert.assertEquals("a1.1-it", rs.getString(12));
                    Assert.assertEquals("a1.1-rm", rs.getString(13));
                    Assert.assertEquals("a1.1-en", rs.getString(14));
                    Assert.assertEquals("a1.1-de", rs.getString(15));
                    Assert.assertEquals("de", rs.getString(16));
                }
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classa1 WHERE classa1.t_ili_tid = 'a1.2'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(null, rs.getString(1));
                    Assert.assertEquals(null, rs.getString(2));
                    Assert.assertEquals(null, rs.getString(3));
                    Assert.assertEquals(null, rs.getString(4));
                    Assert.assertEquals(null, rs.getString(5));
                    Assert.assertEquals(null, rs.getString(6));
                    
                    Assert.assertEquals(null, rs.getString(7));
                    Assert.assertEquals(null, rs.getString(8));
                    
                    Assert.assertEquals(null, rs.getString(9));
                    Assert.assertEquals(null, rs.getString(10));
                    Assert.assertEquals(null, rs.getString(11));
                    Assert.assertEquals(null, rs.getString(12));
                    Assert.assertEquals(null, rs.getString(13));
                    Assert.assertEquals(null, rs.getString(14));
                    
                    Assert.assertEquals(null, rs.getString(15));
                    Assert.assertEquals(null, rs.getString(16));
                }
                Assert.assertTrue(stmt.execute("SELECT"
                        + " atext,atext_de,atext_fr,atext_it,atext_rm,atext_en"
                        + ",btext,btext_lang"
                        + ",ctext,ctext_de,ctext_fr,ctext_it,ctext_rm,ctext_en"
                        + ",dtext,dtext_lang"
                        + " FROM "+DBSCHEMA+".classb1 WHERE classb1.t_ili_tid = 'b1.1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    
                    Assert.assertEquals(null, rs.getString(1));
                    Assert.assertEquals("b1.1-de", rs.getString(2));
                    Assert.assertEquals(null, rs.getString(3));
                    Assert.assertEquals(null, rs.getString(4));
                    Assert.assertEquals(null, rs.getString(5));
                    Assert.assertEquals(null, rs.getString(6));
                    
                    Assert.assertEquals("b1.1-fr", rs.getString(7));
                    Assert.assertEquals("fr", rs.getString(8));
                    
                    Assert.assertEquals(null, rs.getString(9));
                    Assert.assertEquals("b1.1-de", rs.getString(10));
                    Assert.assertEquals(null, rs.getString(11));
                    Assert.assertEquals(null, rs.getString(12));
                    Assert.assertEquals(null, rs.getString(13));
                    Assert.assertEquals(null, rs.getString(14));
                    
                    Assert.assertEquals("b1.1-fr", rs.getString(15));
                    Assert.assertEquals("fr", rs.getString(16));
                }
                stmt.close();
                stmt=null;
            }finally {
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    
    @Test
    public void exportXtfSmartCustom() throws Exception
    {
        {
            importXtfSmartCustom();
        }
        Connection jdbcConnection=null;
        try{

            File data=new File(TEST_OUT,"MultilingualTextCustom-a-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setModels("MultilingualTextCustom1");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            //EhiLogger.getInstance().setTraceFilter(false);
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
                 IomObject obj0 = objs.get("a1.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultilingualTextCustom0.TestA.ClassA1 oid a1.1 {atext MultilingualTextCustom0.MultilingualText {LocalisedText [MultilingualTextCustom0.LocalisedText {Text a1.1-null}, MultilingualTextCustom0.LocalisedText {Language de, Text a1.1-de}, MultilingualTextCustom0.LocalisedText {Language fr, Text a1.1-fr}, MultilingualTextCustom0.LocalisedText {Language rm, Text a1.1-rm}, MultilingualTextCustom0.LocalisedText {Language it, Text a1.1-it}, MultilingualTextCustom0.LocalisedText {Language en, Text a1.1-en}]}, btext MultilingualTextCustom0.LocalisedText {Language de, Text a1.1-de}, ctext MultilingualTextCustom0.MultilingualMText {LocalisedText [MultilingualTextCustom0.LocalisedMText {Text a1.1-null}, MultilingualTextCustom0.LocalisedMText {Language de, Text a1.1-de}, MultilingualTextCustom0.LocalisedMText {Language fr, Text a1.1-fr}, MultilingualTextCustom0.LocalisedMText {Language rm, Text a1.1-rm}, MultilingualTextCustom0.LocalisedMText {Language it, Text a1.1-it}, MultilingualTextCustom0.LocalisedMText {Language en, Text a1.1-en}]}, dtext MultilingualTextCustom0.LocalisedMText {Language de, Text a1.1-de}}"
                         , obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("a1.2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultilingualTextCustom0.TestA.ClassA1 oid a1.2 {}"
                         , obj0.toString());
             }
             {
                 IomObject obj0 = objs.get("b1.1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("MultilingualTextCustom0.TestA.ClassB1 oid b1.1 {atext MultilingualTextCustom0.MultilingualText {LocalisedText MultilingualTextCustom0.LocalisedText {Language de, Text b1.1-de}}, btext MultilingualTextCustom0.LocalisedText {Language fr, Text b1.1-fr}, ctext MultilingualTextCustom0.MultilingualMText {LocalisedText MultilingualTextCustom0.LocalisedMText {Language de, Text b1.1-de}}, dtext MultilingualTextCustom0.LocalisedMText {Language fr, Text b1.1-fr}}"
                         , obj0.toString());
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
}