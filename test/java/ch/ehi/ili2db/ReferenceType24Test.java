package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.ili2c.Ili2cFailure;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class ReferenceType24Test {
    protected static final String TEST_DATA_DIR="test/data/ReferenceType/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup();
	
	@Test
    public void importIliSimple() throws Exception {
        // EhiLogger.getInstance().setTraceFilter(false);
        try {
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "ReferenceSimple24.ili");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // assertions
            // t_ili2db_attrname
            String[][] attrName_expectedValues = new String[][] {
                    { "ReferenceSimple24.TopicA.CatArray.Name", "aname", "catarray", null },
                    { "ReferenceSimple24.TopicA.Item.Name", "aname", "item", null },
                    { "ReferenceSimple24.TopicA.CatArray.Liste", "liste", "catarray_liste", "item" },
                    { "ReferenceSimple24.TopicA.CatArray.Liste", "catarray_liste", "catarray_liste", "catarray" },
            };
            // t_ili2db_trafo
            String[][] trafo_expectedValues = new String[][] {
                    { "ReferenceSimple24.TopicA.CatArray", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceSimple24.TopicA.Item", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceSimple24.TopicA.CatArray.Liste", "ch.ehi.ili2db.secondaryTable", "catarray_liste" }, 
            };
            String[][] columnForeignKey_expectedValues = new String[][] {
                    { "catarray_liste", null, "liste", "item" },
                    { "catarray_liste", null, "catarray_liste", "catarray" },
            };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues, columnForeignKey_expectedValues);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
    @Test
    public void importIliExternal() throws Exception {
        // EhiLogger.getInstance().setTraceFilter(false);
        try {
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "ReferenceExternal24.ili");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // assertions
            // t_ili2db_attrname
            String[][] attrName_expectedValues = new String[][] {
                    { "ReferenceExternal24.TopicA.CatArray.Name", "aname", "catarray", null },
                    { "ReferenceExternal24.TopicA.CatArray.Liste", "liste", "catarray", "item" },
                    { "ReferenceExternal24.TopicA.Item.Name", "aname", "item", null },
            };
            // t_ili2db_trafo
            String[][] trafo_expectedValues = new String[][] {
                    { "ReferenceExternal24.TopicA.CatArray", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExternal24.TopicA.Item", "ch.ehi.ili2db.inheritance", "newClass" },
            };
            String[][] columnForeignKey_expectedValues = new String[][] {
                    { "catarray", null, "liste", "item" },
            };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues, columnForeignKey_expectedValues);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }

    private void importIli_Assert(String[][] attrName_expectedValues, String[][] trafo_expectedValues,String[][] columnForeignKey_expectedValues)
            throws SQLException {
        Ili2dbAssert.assertAttrNameTable(setup,attrName_expectedValues);
        Ili2dbAssert.assertTrafoTable(setup,trafo_expectedValues);
        Ili2dbAssert.assertColumnTable_foreignKey(setup,columnForeignKey_expectedValues);
    }
    @Test
    public void importXtfSimple() throws Exception
    {
        {
            importIliSimple();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceSimple24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
    @Test
    public void importXtfExternal_noValidation() throws Exception
    {
        {
            importIliExternal();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExternal24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExternal24b.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
    @Test
    public void importXtfExternal_doValidation() throws Exception
    {
        {
            importIliExternal();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExternal24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setValidation(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExternal24b.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setValidation(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
	
	@Test
	public void exportXtfSimple() throws Exception {
	    {
	        importXtfSimple();
	    }
		try {
			File data = new File(TEST_DATA_DIR,"ReferenceSimple24a-out.xtf");
			Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
			config.setModels("ReferenceSimple24");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			exportXtfSimple_Assert(data);
		}catch(Exception e) {
			throw new IoxException(e);
		} finally {
		}
	}
    @Test
    public void exportXtfExternal() throws Exception {
        {
            importXtfExternal_noValidation();
        }
        try {
            File data = new File(TEST_DATA_DIR,"ReferenceExternal24a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setModels("ReferenceExternal24");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            exportXtfExternal_Assert(data);
        }catch(Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }

    private void exportXtfSimple_Assert(File data) throws IoxException, Ili2cFailure {
        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        Xtf24Reader reader = new Xtf24Reader(data);
        ch.interlis.ili2c.config.Configuration ili2cConfig=new ch.interlis.ili2c.config.Configuration();
        ili2cConfig.addFileEntry(new FileEntry(new File(TEST_DATA_DIR,"ReferenceSimple24.ili").getPath(),FileEntryKind.ILIMODELFILE));
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);
        reader.setModel(td);
        IoxEvent event = null;
        do {
        	event = reader.read();
        	if (event instanceof StartTransferEvent) {
        	} else if (event instanceof StartBasketEvent) {
        	} else if (event instanceof ObjectEvent) {
        		IomObject iomObj = ((ObjectEvent) event).getIomObject();
        		if (iomObj.getobjectoid() != null) {
        			objs.put(iomObj.getobjectoid(), iomObj);
        		}
        	} else if (event instanceof EndBasketEvent) {
        	} else if (event instanceof EndTransferEvent) {
        	}
        } while (!(event instanceof EndTransferEvent));
        // check values of array
        {
            IomObject obj0 = objs.get("1");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceSimple24.TopicA.Item", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("2");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceSimple24.TopicA.Item", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("3");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceSimple24.TopicA.Item", obj0.getobjecttag());
        }
        {
        	IomObject obj0 = objs.get("10");
        	Assert.assertNotNull(obj0);
        	Assert.assertEquals("ReferenceSimple24.TopicA.CatArray", obj0.getobjecttag());
        	Assert.assertEquals(2,obj0.getattrvaluecount("Liste"));
        	IomObject item0=obj0.getattrobj("Liste", 0);
            Assert.assertEquals("1", item0.getobjectrefoid());
            IomObject item1=obj0.getattrobj("Liste", 1);
            Assert.assertEquals("2", item1.getobjectrefoid());
        }
        {
            IomObject obj0 = objs.get("20");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceSimple24.TopicA.CatArray", obj0.getobjecttag());
            Assert.assertEquals(1,obj0.getattrvaluecount("Liste"));
            IomObject item0=obj0.getattrobj("Liste", 0);
            Assert.assertEquals("3", item0.getobjectrefoid());
        }
    }
    private void exportXtfExternal_Assert(File data) throws IoxException, Ili2cFailure {
        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        Xtf24Reader reader = new Xtf24Reader(data);
        ch.interlis.ili2c.config.Configuration ili2cConfig=new ch.interlis.ili2c.config.Configuration();
        ili2cConfig.addFileEntry(new FileEntry(new File(TEST_DATA_DIR,"ReferenceExternal24.ili").getPath(),FileEntryKind.ILIMODELFILE));
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);
        reader.setModel(td);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        // check values of array
        {
            IomObject obj0 = objs.get("1");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExternal24.TopicA.Item", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("10");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExternal24.TopicA.CatArray", obj0.getobjecttag());
            Assert.assertEquals(1,obj0.getattrvaluecount("Liste"));
            IomObject item0=obj0.getattrobj("Liste", 0);
            Assert.assertEquals("1", item0.getobjectrefoid());
        }
    }

    @Test
    public void importIliSmart0() throws Exception {
        // EhiLogger.getInstance().setTraceFilter(false);
        try {
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "ReferenceExt24.ili");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // assertions
            // t_ili2db_attrname
            String[][] attrName_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.Item.Name", "aname", "item", null },
                    { "ReferenceExt24.TopicA.ItemOne.One", "aone", "itemone", null },
                    { "ReferenceExt24.TopicA.ItemTwo.Two", "two", "itemtwo", null },
                    { "ReferenceExt24.TopicA.CatArrays.Name", "aname", "catarrays", null },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste", "catarrays_liste", "item" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste",  "catarrays_liste", "catarrays_liste", "catarrays"},
                    { "ReferenceExt24.TopicA.CatArrayUno.Uno", "uno", "catarrayuno", null }, 
                    { "ReferenceExt24.TopicA.CatArrayDue.Due", "due", "catarraydue", null },
            };
            // t_ili2db_trafo
            String[][] trafo_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.CatArrays", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.CatArrayUno", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.CatArrayDue", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.Item", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.ItemOne", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.ItemTwo", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "ch.ehi.ili2db.secondaryTable", "catarrays_liste" }, 
            };
            String[][] columnForeignKey_expectedValues = new String[][] {
                    { "catarrays_liste", null, "liste", "item" },
                    { "catarrays_liste", null, "catarrays_liste", "catarrays" }, 
                    { "catarrayuno", null, "T_Id", "catarrays" },
                    { "catarraydue", null, "T_Id", "catarrays" }, 
                    { "itemone", null, "T_Id", "item" },
                    { "itemtwo", null, "T_Id", "item" }, 
            };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues, columnForeignKey_expectedValues);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
    @Test
    public void importIliSmart1() throws Exception {
        //EhiLogger.getInstance().setTraceFilter(false);
        try {
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "ReferenceExt24.ili");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            Ili2db.setNoSmartMapping(config);
            config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // assertions
            // t_ili2db_attrname
            String[][] attrName_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.Item.Name", "aname", "itemone", null },
                    { "ReferenceExt24.TopicA.ItemOne.One", "aone", "itemone", null },
                    { "ReferenceExt24.TopicA.Item.Name", "aname", "itemtwo", null },
                    { "ReferenceExt24.TopicA.ItemTwo.Two", "two", "itemtwo", null },
                    { "ReferenceExt24.TopicA.CatArrays.Name", "aname", "catarrayuno", null },
                    { "ReferenceExt24.TopicA.CatArrayUno.Uno", "uno", "catarrayuno", null }, 
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemone", "catarrayuno_liste", "itemone" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemtwo", "catarrayuno_liste", "itemtwo" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste",  "catarrayuno_liste", "catarrayuno_liste", "catarrayuno"},
                    { "ReferenceExt24.TopicA.CatArrays.Name", "aname", "catarraydue", null },
                    { "ReferenceExt24.TopicA.CatArrayDue.Due", "due", "catarraydue", null },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemone", "catarraydue_liste", "itemone" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemtwo", "catarraydue_liste", "itemtwo" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste",  "catarraydue_liste", "catarraydue_liste", "catarraydue"},
            };
            // t_ili2db_trafo
            String[][] trafo_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.CatArrays", "ch.ehi.ili2db.inheritance", "subClass" },
                    { "ReferenceExt24.TopicA.CatArrayUno", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.CatArrayDue", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.Item", "ch.ehi.ili2db.inheritance", "subClass" },
                    { "ReferenceExt24.TopicA.ItemOne", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.ItemTwo", "ch.ehi.ili2db.inheritance", "newClass" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste(ReferenceExt24.TopicA.CatArrayUno)", "ch.ehi.ili2db.secondaryTable", "catarrayuno_liste" }, 
                    { "ReferenceExt24.TopicA.CatArrays.Liste(ReferenceExt24.TopicA.CatArrayDue)", "ch.ehi.ili2db.secondaryTable", "catarraydue_liste" }, 
            };
            String[][] columnForeignKey_expectedValues = new String[][] {
                    { "catarrayuno_liste", null, "liste_itemone", "itemone" },
                    { "catarrayuno_liste", null, "liste_itemtwo", "itemtwo" },
                    { "catarrayuno_liste", null, "catarrayuno_liste", "catarrayuno" }, 
                    { "catarraydue_liste", null, "liste_itemone", "itemone" },
                    { "catarraydue_liste", null, "liste_itemtwo", "itemtwo" },
                    { "catarraydue_liste", null, "catarraydue_liste", "catarraydue" }, 
            };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues, columnForeignKey_expectedValues);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
    @Test
    public void importIliSmart2() throws Exception {
        //EhiLogger.getInstance().setTraceFilter(false);
        try {
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "ReferenceExt24.ili");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            Ili2db.setNoSmartMapping(config);
            config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            // assertions
            // t_ili2db_attrname
            String[][] attrName_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.Item.Name", "aname", "itemone", null },
                    { "ReferenceExt24.TopicA.ItemOne.One", "aone", "itemone", null },
                    { "ReferenceExt24.TopicA.Item.Name", "aname", "itemtwo", null },
                    { "ReferenceExt24.TopicA.ItemTwo.Two", "two", "itemtwo", null },
                    { "ReferenceExt24.TopicA.CatArrays.Name", "aname", "catarrayuno", null },
                    { "ReferenceExt24.TopicA.CatArrayUno.Uno", "uno", "catarrayuno", null }, 
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemone", "catarrayuno_liste", "itemone" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemtwo", "catarrayuno_liste", "itemtwo" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste",  "catarrayuno_liste", "catarrayuno_liste", "catarrayuno"},
                    { "ReferenceExt24.TopicA.CatArrays.Name", "aname", "catarraydue", null },
                    { "ReferenceExt24.TopicA.CatArrayDue.Due", "due", "catarraydue", null },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemone", "catarraydue_liste", "itemone" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste", "liste_itemtwo", "catarraydue_liste", "itemtwo" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste",  "catarraydue_liste", "catarraydue_liste", "catarraydue"},
            };
            // t_ili2db_trafo
            String[][] trafo_expectedValues = new String[][] {
                    { "ReferenceExt24.TopicA.CatArrays", "ch.ehi.ili2db.inheritance", "subClass" },
                    { "ReferenceExt24.TopicA.CatArrayUno", "ch.ehi.ili2db.inheritance", "newAndSubClass" },
                    { "ReferenceExt24.TopicA.CatArrayDue", "ch.ehi.ili2db.inheritance", "newAndSubClass" },
                    { "ReferenceExt24.TopicA.Item", "ch.ehi.ili2db.inheritance", "subClass" },
                    { "ReferenceExt24.TopicA.ItemOne", "ch.ehi.ili2db.inheritance", "newAndSubClass" },
                    { "ReferenceExt24.TopicA.ItemTwo", "ch.ehi.ili2db.inheritance", "newAndSubClass" },
                    { "ReferenceExt24.TopicA.CatArrays.Liste(ReferenceExt24.TopicA.CatArrayUno)", "ch.ehi.ili2db.secondaryTable", "catarrayuno_liste" }, 
                    { "ReferenceExt24.TopicA.CatArrays.Liste(ReferenceExt24.TopicA.CatArrayDue)", "ch.ehi.ili2db.secondaryTable", "catarraydue_liste" }, 
            };
            String[][] columnForeignKey_expectedValues = new String[][] {
                    { "catarrayuno_liste", null, "liste_itemone", "itemone" },
                    { "catarrayuno_liste", null, "liste_itemtwo", "itemtwo" },
                    { "catarrayuno_liste", null, "catarrayuno_liste", "catarrayuno" }, 
                    { "catarraydue_liste", null, "liste_itemone", "itemone" },
                    { "catarraydue_liste", null, "liste_itemtwo", "itemtwo" },
                    { "catarraydue_liste", null, "catarraydue_liste", "catarraydue" }, 
            };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues, columnForeignKey_expectedValues);
        } catch (Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }

    @Test
    public void importXtfSmart0() throws Exception
    {
        {
            importIliSmart0();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExt24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
    @Test
    public void importXtfSmart1() throws Exception
    {
        {
            importIliSmart1();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExt24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
    @Test
    public void importXtfSmart2() throws Exception
    {
        {
            importIliSmart2();
        }
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            File data=new File(TEST_DATA_DIR,"ReferenceExt24a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }

    
    @Test
    public void exportXtfSmart0() throws Exception {
        {
            importXtfSmart0();
        }
        try {
            File data = new File(TEST_DATA_DIR,"ReferenceExt24a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setModels("ReferenceExt24");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            exportXtf_Assert(data);
        }catch(Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
    @Test
    public void exportXtfSmart1() throws Exception {
        {
            importXtfSmart1();
        }
        try {
            File data = new File(TEST_DATA_DIR,"ReferenceExt24a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setModels("ReferenceExt24");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            exportXtf_Assert(data);
        }catch(Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
    @Test
    public void exportXtfSmart2() throws Exception {
        {
            importXtfSmart2();
        }
        try {
            File data = new File(TEST_DATA_DIR,"ReferenceExt24a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setModels("ReferenceExt24");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            exportXtf_Assert(data);
        }catch(Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }

    private void exportXtf_Assert(File data) throws IoxException, Ili2cFailure {
        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        Xtf24Reader reader = new Xtf24Reader(data);
        ch.interlis.ili2c.config.Configuration ili2cConfig=new ch.interlis.ili2c.config.Configuration();
        ili2cConfig.addFileEntry(new FileEntry(new File(TEST_DATA_DIR,"ReferenceExt24.ili").getPath(),FileEntryKind.ILIMODELFILE));
        TransferDescription td = ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
        assertNotNull(td);
        reader.setModel(td);
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof StartTransferEvent) {
            } else if (event instanceof StartBasketEvent) {
            } else if (event instanceof ObjectEvent) {
                IomObject iomObj = ((ObjectEvent) event).getIomObject();
                if (iomObj.getobjectoid() != null) {
                    objs.put(iomObj.getobjectoid(), iomObj);
                }
            } else if (event instanceof EndBasketEvent) {
            } else if (event instanceof EndTransferEvent) {
            }
        } while (!(event instanceof EndTransferEvent));
        // check values of array
        {
            IomObject obj0 = objs.get("1");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExt24.TopicA.ItemOne", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("2");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExt24.TopicA.ItemTwo", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("3");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExt24.TopicA.ItemOne", obj0.getobjecttag());
        }
        {
            IomObject obj0 = objs.get("10");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExt24.TopicA.CatArrayUno", obj0.getobjecttag());
            Assert.assertEquals(2,obj0.getattrvaluecount("Liste"));
            IomObject item0=obj0.getattrobj("Liste", 0);
            Assert.assertEquals("1", item0.getobjectrefoid());
            IomObject item1=obj0.getattrobj("Liste", 1);
            Assert.assertEquals("2", item1.getobjectrefoid());
        }
        {
            IomObject obj0 = objs.get("20");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("ReferenceExt24.TopicA.CatArrayDue", obj0.getobjecttag());
            Assert.assertEquals(1,obj0.getattrvaluecount("Liste"));
            IomObject item0=obj0.getattrobj("Liste", 0);
            Assert.assertEquals("3", item0.getobjectrefoid());
        }
    }
}