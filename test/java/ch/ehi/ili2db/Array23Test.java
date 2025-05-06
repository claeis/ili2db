package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;

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
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class Array23Test {
    protected static final String TEST_DATA_DIR="test/data/Array/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup();
	
	@Test
	public void importIli() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		try{
            setup.resetDb();

			File data=new File(TEST_DATA_DIR,"Array23.ili");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// assertions
            // t_ili2db_attrname
            String [][] attrName_expectedValues=new String[][] {
                {"Array23.TestA.Gebaeude.Art", "art", "gebaeude",    "katalog"},
                {"Array23.TestA.KatalogRef.Ref",    "aref",    "katalogref",  "katalog"},
                {"Array23.TestA.Katalog.val",    "val",    "katalog",  null},
                {"Array23.TestA.Binbox_.Value",   "avalue",    "binbox_",null},   
                {"Array23.TestA.NumericDec_.Value",   "avalue",    "numericdec_",null},   
                {"Array23.TestA.Datatypes.aBoolean",  "aboolean",  "datatypes" ,null},
                {"Array23.TestA.Datatypes.aDate", "adate", "datatypes" ,null},
                {"Array23.TestA.Xmlbox_.Value",   "avalue",    "xmlbox_"   ,null},
                {"Array23.TestA.ADateTime_.Value",    "avalue",    "adatetime_"   ,null}, 
                {"Array23.TestA.Farbe.Wert",  "wert",  "farbe" ,null},
                {"Array23.TestA.Datatypes.numericInt",    "numericint",    "datatypes",null}, 
                {"Array23.TestA.Datatypes.numericDec",    "numericdec",    "datatypes" ,null},
                {"Array23.TestA.NumericInt_.Value",   "avalue",    "numericint_"   ,null},
                {"Array23.TestA.Auto.Farben", "farben",    "auto"  ,null},
                {"Array23.TestA.Datatypes.aDateTime", "adatetime", "datatypes",null}, 
                {"Array23.TestA.AUuid_.Value",    "avalue",    "auuid_"    ,null},
                {"Array23.TestA.Datatypes.aUuid", "auuid", "datatypes" ,null},
                {"Array23.TestA.ABoolean_.Value", "avalue",    "aboolean_" ,null},
                {"Array23.TestA.ADate_.Value",    "avalue",    "adate_"    ,null},
                {"Array23.TestA.ATime_.Value",    "avalue",    "atime_"    ,null},
                {"Array23.TestA.Datatypes.aTime", "atime", "datatypes"         ,null}         };
                // t_ili2db_trafo
                String [][] trafo_expectedValues=new String[][] {
                    {"Array23.TestA.Gebaeude.Art", "ch.ehi.ili2db.arrayTrafo",    "coalesce"},
                    {"Array23.TestA.KatalogRef",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Gebaeude",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Katalog",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Datatypes.aDate", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.AUuid_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Auto.Farben", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.NumericInt_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Binbox_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.ATime_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.aTime", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.Datatypes.aBoolean",  "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.ABoolean_",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.aUuid", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.Datatypes.aDateTime", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.NumericDec_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.ADate_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.numericInt",    "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.ADateTime_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Farbe",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Auto",    "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Xmlbox_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.numericDec",    "ch.ehi.ili2db.arrayTrafo",  "coalesce"}                      
                };
                String [][] columnForeignKey_expectedValues=new String[][] {
                    {"katalogref",null,"aref","katalog"},
                    {"gebaeude",null,"art","katalog"},
                };
	        importIli_Assert(attrName_expectedValues, trafo_expectedValues,columnForeignKey_expectedValues);
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
		}
	}
    @Test
    public void importIliEnumFkTable() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            setup.resetDb();

            File data=new File(TEST_DATA_DIR,"Array23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // assertions
            // t_ili2db_attrname
            String [][] attrName_expectedValues=new String[][] {
                {"Array23.TestA.Gebaeude.Art", "art", "gebaeude",    "katalog"},
                {"Array23.TestA.KatalogRef.Ref",    "aref",    "katalogref",  "katalog"},
                {"Array23.TestA.Katalog.val",    "val",    "katalog",  null},
                {"Array23.TestA.Binbox_.Value",   "avalue",    "binbox_",null},   
                {"Array23.TestA.NumericDec_.Value",   "avalue",    "numericdec_",null},   
                {"Array23.TestA.Datatypes.aBoolean",  "aboolean",  "datatypes" ,null},
                {"Array23.TestA.Datatypes.aDate", "adate", "datatypes" ,null},
                {"Array23.TestA.Xmlbox_.Value",   "avalue",    "xmlbox_"   ,null},
                {"Array23.TestA.ADateTime_.Value",    "avalue",    "adatetime_"   ,null}, 
                {"Array23.TestA.Farbe.Wert",  "wert",  "farbe" ,null},
                {"Array23.TestA.Datatypes.numericInt",    "numericint",    "datatypes",null}, 
                {"Array23.TestA.Datatypes.numericDec",    "numericdec",    "datatypes" ,null},
                {"Array23.TestA.NumericInt_.Value",   "avalue",    "numericint_"   ,null},
                {"Array23.TestA.Auto.Farben", "farben",    "auto"  ,null},
                {"Array23.TestA.Datatypes.aDateTime", "adatetime", "datatypes",null}, 
                {"Array23.TestA.AUuid_.Value",    "avalue",    "auuid_"    ,null},
                {"Array23.TestA.Datatypes.aUuid", "auuid", "datatypes" ,null},
                {"Array23.TestA.ABoolean_.Value", "avalue",    "aboolean_" ,null},
                {"Array23.TestA.ADate_.Value",    "avalue",    "adate_"    ,null},
                {"Array23.TestA.ATime_.Value",    "avalue",    "atime_"    ,null},
                {"Array23.TestA.Datatypes.aTime", "atime", "datatypes"         ,null}         };
                // t_ili2db_trafo
                String [][] trafo_expectedValues=new String[][] {
                    {"Array23.TestA.Gebaeude.Art", "ch.ehi.ili2db.arrayTrafo",    "coalesce"},
                    {"Array23.TestA.KatalogRef",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Gebaeude",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Katalog",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Array23.TestA.Datatypes.aDate", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.AUuid_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Auto.Farben", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.NumericInt_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Binbox_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.ATime_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.aTime", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.Datatypes.aBoolean",  "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.ABoolean_",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.aUuid", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.Datatypes.aDateTime", "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.NumericDec_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.ADate_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.numericInt",    "ch.ehi.ili2db.arrayTrafo",  "coalesce"},
                    {"Array23.TestA.ADateTime_",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Farbe",   "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Auto",    "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Xmlbox_", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"Array23.TestA.Datatypes.numericDec",    "ch.ehi.ili2db.arrayTrafo",  "coalesce"}                      
                };
                String [][] columnForeignKey_expectedValues=new String[][] {
                    {"katalogref",null,"aref","katalog"},
                    {"gebaeude",null,"art","katalog"},
                    {"farbe",null,"wert","rgb"},
                    {"auto",null,"farben","rgb"},
                };
            importIli_Assert(attrName_expectedValues, trafo_expectedValues,columnForeignKey_expectedValues);
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }

    private void importIli_Assert(String[][] attrName_expectedValues, String[][] trafo_expectedValues,String [][]columnForeignKey_expectedValues)
            throws SQLException {
        Ili2dbAssert.assertAttrNameTable(setup,attrName_expectedValues);
        Ili2dbAssert.assertTrafoTable(setup,trafo_expectedValues);
        Ili2dbAssert.assertColumnTable_foreignKey(setup,columnForeignKey_expectedValues);
    }
    @Test
    public void importXtf() throws Exception
    {
        EhiLogger.getInstance().setTraceFilter(false);
        try{
            setup.resetDb();

            File data=new File(TEST_DATA_DIR,"Array23a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            importXtfEnumFkTable_Assert();
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }
    @Test
    public void importXtfEnumFkTable() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        try{
            setup.resetDb();

            File data=new File(TEST_DATA_DIR,"Array23a.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setArrayTrafo(Config.ARRAY_TRAFO_COALESCE);
            config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // assertions
            importXtfEnumFkTable_Assert();
        }catch(Exception e) {
            throw new IoxException(e);
        }finally{
        }
    }

    protected void importXtfEnumFkTable_Assert() throws SQLException {
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT "
                    + "auuid,"
                    + "aboolean,"
                    + "atime,"
                    + "adate,"
                    + "adatetime,"
                    + "numericint,"
                    + "numericdec"
                    + " FROM "+setup.prefixName("datatypes")+" WHERE t_ili_tid='100';");
            ResultSetMetaData rsmd=rs.getMetaData();
            assertEquals(7, rsmd.getColumnCount());
            // first row
            rs.next();
            assertEquals(null, rs.getString(1));
            assertEquals(null, rs.getString(2));
            assertEquals(null, rs.getString(3));
            assertEquals(null, rs.getString(4));
            assertEquals(null, rs.getString(5));
            assertEquals(null, rs.getString(6));
            assertEquals(null, rs.getString(7));
            
            ResultSet rs2 = stmt.executeQuery("SELECT "
                    + "auuid,"
                    + "aboolean,"
                    + "atime,"
                    + "adate,"
                    + "adatetime,"
                    + "numericint,"
                    + "numericdec "
                    + "FROM "+setup.prefixName("datatypes")+" WHERE t_ili_tid='101';");
            ResultSetMetaData rsmd2=rs2.getMetaData();
            // second row
            rs2.next();
            assertEquals("[\"15b6bcce-8772-4595-bf82-f727a665fbf3\"]", rs2.getString(1));
            assertEquals("[true]", rs2.getString(2));
            assertEquals("[\"09:00:00.000\"]", rs2.getString(3));
            assertEquals("[\"2002-09-24\"]", rs2.getString(4));
            assertEquals("[\"1900-01-01T12:30:05.000\"]", rs2.getString(5));
            assertEquals("[5]", rs2.getString(6));
            assertEquals("[6.0]", rs2.getString(7));
            
        }finally {
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
	public void exportXtf() throws Exception {
	    {
	        importXtf();
	    }
		try {
			
			File data = new File(TEST_DATA_DIR,"Array23a-out.xtf");
			Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
			config.setModels("Array23");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config, null);
			exportXtf_Assert(data);
		}catch(Exception e) {
			throw new IoxException(e);
		} finally {
		}
	}

    private void exportXtf_Assert(File data) throws IoxException {
        HashMap<String, IomObject> objs = new HashMap<String, IomObject>();
        XtfReader reader = new XtfReader(data);
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
        	IomObject obj0 = objs.get("13");
        	Assert.assertNotNull(obj0);
        	Assert.assertEquals("Array23.TestA.Auto", obj0.getobjecttag());
        	Assert.assertEquals(2,obj0.getattrvaluecount("Farben"));
        	Assert.assertEquals("Rot",obj0.getattrobj("Farben", 0).getattrvalue("Wert"));
        	Assert.assertEquals("Blau",obj0.getattrobj("Farben", 1).getattrvalue("Wert"));
        }
        {
        	IomObject obj0 = objs.get("14");
        	Assert.assertNotNull(obj0);
        	Assert.assertEquals("Array23.TestA.Auto", obj0.getobjecttag());
        	Assert.assertEquals(0,obj0.getattrvaluecount("Farben"));
        }
        {
        	IomObject obj0 = objs.get("100");
        	Assert.assertNotNull(obj0);
        	Assert.assertEquals("Array23.TestA.Datatypes", obj0.getobjecttag());
        	Assert.assertEquals(0,obj0.getattrvaluecount("aUuid"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("aBoolean"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("aTime"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("aDate"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("aDateTime"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("numericInt"));
        	Assert.assertEquals(0,obj0.getattrvaluecount("numericDec"));
        }
        {
        	IomObject obj0 = objs.get("101");
        	Assert.assertNotNull(obj0);
        	Assert.assertEquals("Array23.TestA.Datatypes", obj0.getobjecttag());
        	Assert.assertEquals(1,obj0.getattrvaluecount("aUuid"));
        	Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3",obj0.getattrobj("aUuid",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("aBoolean"));
        	Assert.assertEquals("true",obj0.getattrobj("aBoolean",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("aTime"));
        	Assert.assertEquals("09:00:00.000",obj0.getattrobj("aTime",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("aDate"));
        	Assert.assertEquals("2002-09-24",obj0.getattrobj("aDate",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("aDateTime"));
        	Assert.assertEquals("1900-01-01T12:30:05.000",obj0.getattrobj("aDateTime",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("numericInt"));
        	Assert.assertEquals("5",obj0.getattrobj("numericInt",0).getattrvalue("Value"));
        	Assert.assertEquals(1,obj0.getattrvaluecount("numericDec"));
        	Assert.assertEquals("6.0",obj0.getattrobj("numericDec",0).getattrvalue("Value"));
        }
        {
            IomObject obj0 = objs.get("300");
            Assert.assertNotNull(obj0);
            Assert.assertEquals("Array23.TestA.Gebaeude", obj0.getobjecttag());
            Assert.assertEquals(2,obj0.getattrvaluecount("Art"));
            Assert.assertEquals("200",obj0.getattrobj("Art", 0).getattrobj("Ref", 0).getobjectrefoid());
            Assert.assertEquals("202",obj0.getattrobj("Art", 1).getattrobj("Ref", 0).getobjectrefoid());
        }
    }
    @Test
    public void exportXtfEnumFkTable() throws Exception {
        {
            importXtfEnumFkTable();
        }
        try {
            
            File data = new File(TEST_DATA_DIR,"Array23a-out.xtf");
            Config config = setup.initConfig(data.getPath(), data.getPath() + ".log");
            config.setModels("Array23");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);
            exportXtf_Assert(data);
        }catch(Exception e) {
            throw new IoxException(e);
        } finally {
        }
    }
}