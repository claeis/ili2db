package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
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

public abstract class EnumAll23Test {
    protected static final String TEST_OUT="test/data/Enum23/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;

    @Test
    public void importIliFkTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            File data=new File(TEST_OUT,"EnumAll23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
            Ili2db.run(config,null);
            
            // verify generated script
            {
                jdbcConnection=setup.createConnection();
                {
                    Statement stmt=null;
                    String stmtTxt="select count(*) from "+setup.prefixName("enum1")+" where iliCode='Test1' and thisClass='EnumAll23.Enum1' and baseClass is NULL";
                    try{
                        stmt=jdbcConnection.createStatement();
                         Assert.assertTrue(stmt.execute(stmtTxt));
                         ResultSet rs=stmt.getResultSet();
                         {
                             Assert.assertTrue(rs.next());
                             Assert.assertEquals(1, rs.getInt(1));
                         }
                    }finally {
                        if(stmt!=null) {
                            stmt.close();
                        }
                    }
                }
                jdbcConnection.close();
                jdbcConnection=null;
                
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
    @Test
    public void importXtfFkTable() throws Exception
    {
        {
            importIliFkTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(false);
            config.setImportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
        }
    }
    @Test
    public void exportXtfFkTable() throws Exception
    {
        {
            importXtfFkTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_EXPORT);
            config.setDoImplicitSchemaImport(false);
            config.setModels("EnumAll23");
            config.setExportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            {
                XtfReader reader=new XtfReader(data);
                assertTrue(reader.read() instanceof StartTransferEvent);
                assertTrue(reader.read() instanceof StartBasketEvent);
                
                HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                IoxEvent event=reader.read();
                while(event instanceof ObjectEvent) {
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    objs.put(iomObj.getobjectoid(),iomObj);
                    event=reader.read();
                }
                assertEquals(3,objs.size());
                assertEquals("EnumAll23.TestA.ClassA1 oid o1 {attr1 Test3.Test3a, attr1a Test3.Test3a, attr1b Test3.Test3a}",objs.get("o1").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o2 {attr1 Test3, attr1a Test3, attr1b Test3}",objs.get("o2").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o3 {attr1 Test2, attr1a Test2, attr1b Test2}",objs.get("o3").toString());
                assertTrue(event instanceof EndBasketEvent);
                assertTrue(reader.read() instanceof EndTransferEvent);
                reader.close();
                
            }
        }
    }
        
    @Test
    public void importIliSingleTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            File data=new File(TEST_OUT,"EnumAll23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
            Ili2db.run(config,null);
            
            // verify generated script
            {
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
    @Test
    public void importXtfSingleTable() throws Exception
    {
        {
            importIliSingleTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(false);
            config.setImportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
        }
    }
    @Test
    public void exportXtfSingleTable() throws Exception
    {
        {
            importXtfSingleTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_EXPORT);
            config.setDoImplicitSchemaImport(false);
            config.setModels("EnumAll23");
            config.setExportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            {
                XtfReader reader=new XtfReader(data);
                assertTrue(reader.read() instanceof StartTransferEvent);
                assertTrue(reader.read() instanceof StartBasketEvent);
                
                HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                IoxEvent event=reader.read();
                while(event instanceof ObjectEvent) {
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    objs.put(iomObj.getobjectoid(),iomObj);
                    event=reader.read();
                }
                assertEquals(3,objs.size());
                assertEquals("EnumAll23.TestA.ClassA1 oid o1 {attr1 Test3.Test3a, attr1a Test3.Test3a, attr1b Test3.Test3a}",objs.get("o1").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o2 {attr1 Test3, attr1a Test3, attr1b Test3}",objs.get("o2").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o3 {attr1 Test2, attr1a Test2, attr1b Test2}",objs.get("o3").toString());
                assertTrue(event instanceof EndBasketEvent);
                assertTrue(reader.read() instanceof EndTransferEvent);
                reader.close();
                
            }
        }
    }
    @Test
    public void importIliMultiTable() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            setup.resetDb();
            File data=new File(TEST_OUT,"EnumAll23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setCreateNumChecks(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setCreateMetaInfo(true);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
            Ili2db.run(config,null);
            
            // verify generated script
            {                
                
            }
            
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }   
    }
    @Test
    public void importXtfMultiTable() throws Exception
    {
        {
            importIliMultiTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(false);
            config.setImportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
        }
    }
    @Test
    public void exportXtfMultiTable() throws Exception
    {
        {
            importXtfMultiTable();
        }
        {
            // rum import without schema generation
            File data=new File(TEST_OUT,"EnumAll23-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setXtffile(data.getPath());
            config.setFunction(Config.FC_EXPORT);
            config.setDoImplicitSchemaImport(false);
            config.setModels("EnumAll23");
            config.setExportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
            {
                XtfReader reader=new XtfReader(data);
                assertTrue(reader.read() instanceof StartTransferEvent);
                assertTrue(reader.read() instanceof StartBasketEvent);
                
                HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                IoxEvent event=reader.read();
                while(event instanceof ObjectEvent) {
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    objs.put(iomObj.getobjectoid(),iomObj);
                    event=reader.read();
                }
                assertEquals(3,objs.size());
                assertEquals("EnumAll23.TestA.ClassA1 oid o1 {attr1 Test3.Test3a, attr1a Test3.Test3a, attr1b Test3.Test3a}",objs.get("o1").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o2 {attr1 Test3, attr1a Test3, attr1b Test3}",objs.get("o2").toString());
                assertEquals("EnumAll23.TestA.ClassA1 oid o3 {attr1 Test2, attr1a Test2, attr1b Test2}",objs.get("o3").toString());
                assertTrue(event instanceof EndBasketEvent);
                assertTrue(reader.read() instanceof EndTransferEvent);
                reader.close();
                
            }
        }
    }
}
