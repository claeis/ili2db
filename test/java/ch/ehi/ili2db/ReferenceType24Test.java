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

}