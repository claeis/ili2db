package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.config.FileEntry;
import ch.interlis.ili2c.config.FileEntryKind;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.jts.Iox2jts;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public abstract class TranslationTest {
	protected static final String TEST_OUT = "test/data/Translation/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
	@Test
	public void importIli23() throws Exception
	{
        setup.resetDb();
        File data=new File(TEST_OUT,"EnumOk.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModels("EnumOkA;EnumOkB");
        config.setVer3_translation(false);
        config.setCreateNlsTab(true);
        config.setCreateMetaInfo(true);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // class[a] is imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'EnumOkX.TopicX.ClassX'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("classx",rs.getString(2));
            }
            // class[b] is NOT imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'EnumOkB.TopicB.ClassB'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            Assert.assertTrue(stmt.execute("SELECT "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LABEL_COL+", "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_DESCRIPTION_COL+" FROM "+setup.prefixName(DbNames.NLS_TAB)+" WHERE "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_ILIELEMENT_COL+" = 'EnumOkX.TopicX.ClassX' AND "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LANG_COL+" = 'de'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("Class A",rs.getString(1));
                Assert.assertEquals("ilidoc Class A",rs.getString(2));
            }
            // Domain Enum
            Assert.assertTrue(stmt.execute("SELECT "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LABEL_COL+", "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_DESCRIPTION_COL+" FROM "+setup.prefixName(DbNames.NLS_TAB)+" WHERE "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_ILIELEMENT_COL+" = 'EnumOkX.TopicX.ClassX.attrX.x1' AND "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LANG_COL+" = 'de'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("a1",rs.getString(1));
                Assert.assertEquals(null,rs.getString(2));
            }
            // Attr Enum
            Assert.assertTrue(stmt.execute("SELECT "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LABEL_COL+", "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_DESCRIPTION_COL+" FROM "+setup.prefixName(DbNames.NLS_TAB)+" WHERE "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_ILIELEMENT_COL+" = 'EnumOkX.DomainX.x1' AND "+DbNames.NLS_TAB+"."+DbNames.NLS_TAB_LANG_COL+" = 'de'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("a1",rs.getString(1));
                Assert.assertEquals(null,rs.getString(2));
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"EnumOkX.TopicX.n_1X.n_1X_X2", "n_1x_x2", "classx",  "classx2"},
                    {"EnumOkX.TopicX.n_nX.n_nX_X2", "n_nx_x2", "n_nx",    "classx2"},
                    {"EnumOkX.TopicX.ClassX.attrX", "attrx",   "classx",  null},
                    {"EnumOkX.TopicX.ClassX.attrX2", "classx_attrx2",   "struct",  "classx"},
                    {"EnumOkX.TopicX.ClassX.attrX3", "classx_attrx3",   "trstructa",  "classx"},
                    {"EnumOkX.TopicX.ClassX.attrX4", "attrx4",   "classx",  null},
                    {"EnumOkX.TopicX.n_nX.n_nX_X",  "n_nx_x",  "n_nx",    "classx"},
                    {"Basis.Struct.attrA", "attra",   "struct",  null},
                    {"Basis.Struct.attrA2", "attra2",   "struct",  null},
                    {"TranslatedBasisA.TrStructA.trAttrA", "trattra",   "trstructa",  null},
                    {"TranslatedBasisA.TrStructA.trAttrA2", "trattra2",   "trstructa",  null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"EnumOkX.TopicX.n_1X", "ch.ehi.ili2db.inheritance",   "embedded"},
                    {"EnumOkX.TopicX.ClassX2",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"EnumOkX.TopicX.ClassX",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"EnumOkX.TopicX.n_nX", "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Basis.Struct",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"TranslatedBasisA.TrStructA",   "ch.ehi.ili2db.inheritance",   "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
        }finally{
            if(stmt!=null){
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
	}
    @Test
    public void importIli23assocref() throws Exception
    {
        setup.resetDb();
        File data=new File(TEST_OUT,"Translation23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModels("Translation23_de;Translation23_fr");
        config.setVer3_translation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"Translation23_de.TestB_de.ClassB1_de.attrRef_de", "classb1_de_attrref_de",   "structb0_de", "classb1_de"},
                    {"Translation23_de.TestA_de.ClassA1_de.attrA_de",   "attra_de",    "classa1_de",  null},
                    {"Translation23_de.TestB_de.ClassB1_de.attrB_de",   "attrb_de",    "classb1_de",  null},
                    {"Translation23_de.TestB_de.a2b_de.a_de",   "a_de",    "classb1_de",  "classa1_de"},
                    {"Translation23_de.TestB_de.StructB0_de.refA_de",   "refa_de", "structb0_de", "classa1_de"},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Translation23_de.TestA_de.ClassA1_de",    "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Translation23_de.TestB_de.StructB0_de",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Translation23_de.TestB_de.a2b_de",        "ch.ehi.ili2db.inheritance",   "embedded"},
                    {"Translation23_de.TestB_de.ClassB1_de",    "ch.ehi.ili2db.inheritance",   "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIli23schemaDE_IT() throws Exception
    {
        
        setup.resetDb();
        File data=new File(TEST_OUT,"EnumOk.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setModels("BasisC;TranslatedBasisB;EnumOkX;EnumOkA;EnumOkB");
        config.setNameLanguage("de;it");
        config.setVer3_translation(false);
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // class[a] is imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'EnumOkX.TopicX.ClassX'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("classa",rs.getString(2));
            }
            // class[b] is NOT imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'EnumOkA.TopicA.ClassA'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"EnumOkX.TopicX.n_1X.n_1X_X2", "n_1a_a2", "classa",  "classa2"},
                    {"EnumOkX.TopicX.n_nX.n_nX_X2", "n_na_a2", "n_na",    "classa2"},
                    {"EnumOkX.TopicX.ClassX.attrX", "attra",   "classa",  null},
                    {"EnumOkX.TopicX.ClassX.attrX2", "classa_attra2",   "structc",  "classa"},
                    {"EnumOkX.TopicX.ClassX.attrX3", "classa_attra3",   "trstructa",  "classa"},
                    {"EnumOkX.TopicX.ClassX.attrX4", "attra4",   "classa", null},
                    {"EnumOkX.TopicX.n_nX.n_nX_X",  "n_na_a",  "n_na",    "classa"},
                    {"Basis.Struct.attrA", "attrc",   "structc",  null},
                    {"Basis.Struct.attrA2", "attrc2",   "structc",  null},
                    {"TranslatedBasisA.TrStructA.trAttrA", "trattra",   "trstructa",  null},
                    {"TranslatedBasisA.TrStructA.trAttrA2", "trattra2",   "trstructa",  null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"EnumOkX.TopicX.n_1X", "ch.ehi.ili2db.inheritance",   "embedded"},
                    {"EnumOkX.TopicX.ClassX2",  "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"EnumOkX.TopicX.ClassX",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"EnumOkX.TopicX.n_nX", "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Basis.Struct",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"Basis.Struct2",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"TranslatedBasisA.TrStructA",   "ch.ehi.ili2db.inheritance",   "newClass"},
                    {"TranslatedBasisA.TrStructA2",   "ch.ehi.ili2db.inheritance",   "newClass"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
	@Test
	public void importIli10() throws Exception
	{
        setup.resetDb();
        File data=new File(TEST_OUT,"ModelBsimple10.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setIli1Translation("ModelBsimple10=ModelAsimple10");
        config.setDefaultSrsAuthority("EPSG");
        config.setDefaultSrsCode("21781");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();

            
            // class[a] is imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("classa",rs.getString(2));
            }
            // class[b] is NOT imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA3.geomA",  "geoma", "classa3", null},   
                    {"ModelAsimple10.TopicA.ClassA2.geomA",   "geoma", "classa2", null},
                    {"ModelAsimple10.TopicA.ClassA.attrA",    "attra", "classa", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA3", "ch.ehi.ili2db.inheritance", "newClass"},
                    
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
		}finally{
		    if(stmt!=null) {
		        stmt.close();
		    }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
    @Test
    public void importIli10Multi() throws Exception
    {
        setup.resetDb();
        File data=new File(TEST_OUT,"ModelCsimple10.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setIli1Translation("ModelBsimple10=ModelAsimple10;ModelCsimple10=ModelAsimple10");
        config.setDefaultSrsAuthority("EPSG");
        config.setDefaultSrsCode("21781");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // class[a] is imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("classa",rs.getString(2));
            }
            // class[b] is NOT imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA3.geomA",  "geoma", "classa3", null},   
                    {"ModelAsimple10.TopicA.ClassA2.geomA",   "geoma", "classa2", null},
                    {"ModelAsimple10.TopicA.ClassA.attrA",    "attra", "classa", null},
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA3", "ch.ehi.ili2db.inheritance", "newClass"},
                    
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
	        
	@Test
	public void importIli10lineTable() throws Exception
	{
        setup.resetDb();
        File data=new File(TEST_OUT,"ModelBsimple10.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        Ili2db.setSkipPolygonBuilding(config);
        config.setIli1Translation("ModelBsimple10=ModelAsimple10");
        config.setDefaultSrsAuthority("EPSG");
        config.setDefaultSrsCode("21781");
        Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // class[a2] is imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname, t_ili2db_classname.sqlname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelAsimple10.TopicA.ClassA2'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("classa2",rs.getString(2));
            }
            // class[b2] is NOT imported
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_classname.iliname FROM "+setup.prefixName("t_ili2db_classname")+" WHERE t_ili2db_classname.iliname = 'ModelBsimple10.TopicB.ClassB2'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertFalse(rs.next());
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA2.geomA._geom", "_geom", "classa2_geoma", null}, 
                    {"ModelAsimple10.TopicA.ClassA3.geomA",   "geoma", "classa3", null},   
                    {"ModelAsimple10.TopicA.ClassA3.geomA._geom", "_geom", "classa3_geoma", null}, 
                    {"ModelAsimple10.TopicA.ClassA2.geomA._ref", "_ref", "classa2_geoma", null}, 
                    {"ModelAsimple10.TopicA.ClassA.attrA", "attra", "classa", null}
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection, expectedValues, setup.getSchema());
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"ModelAsimple10.TopicA.ClassA3", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA2", "ch.ehi.ili2db.inheritance", "newClass"},
                    {"ModelAsimple10.TopicA.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"}
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
            }
		}finally{
		    if(stmt!=null) {
		        stmt.close();
		    }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
	
	@Test
	public void importXtf23() throws Exception
	{
        setup.resetDb();
        {
            File data=new File(TEST_OUT,"EnumOka.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setVer3_translation(false);
            config.setDatasetName("EnumOka");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"EnumOkb.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("EnumOkb");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // tid's of class[a]
            HashSet<String> expectedTids= new HashSet<String>(Arrays.asList(new String[]{"o1","o2","x1","x2"}));
            Assert.assertTrue(stmt.execute("SELECT t_id, t_ili_tid FROM "+setup.prefixName("classx")));
            {
                ResultSet rs=stmt.getResultSet();
                while(!expectedTids.isEmpty()) {
                    Assert.assertTrue(rs.next());
                    String tid=rs.getString(2);
                    assertTrue(expectedTids.remove(tid));
                }
                Assert.assertFalse(rs.next());
            }
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'EnumOkA.Test1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("EnumOkA.TopicA",rs.getString(2));
            }
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'EnumOkB.Test1'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("EnumOkB.TopicB",rs.getString(2));
            }
		}finally{
		    if(stmt!=null) {
		        stmt.close();
		    }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}
    @Test
    public void importXtf23assoc_root2root() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"assoc_root2root_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
    @Test
    public void importXtf23assoc_root2translated() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"assoc_root2translated_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
    @Test
    public void importXtf23assoc_translated2translated() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"assoc_translated2translated_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
    @Test
    public void importXtf23assoc_translated2root() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"assoc_translated2root_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
    @Test
    public void importXtf23refattr_root2root() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"refattr_root2root_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
    }
    @Test
    public void importXtf23refattr_root2translated() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"refattr_root2translated_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
    }
    @Test
    public void importXtf23refattr_translated2translated() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"refattr_translated2translated_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
    }
    @Test
    public void importXtf23refattr_translated2root() throws Exception
    {
        {
            importIli23assocref();
        }
        {
            File data=new File(TEST_OUT,"refattr_translated2root_Ok.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("assoc");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
    }
    @Test
    public void importXtf23schemaDE_IT() throws Exception
    {
        {
            importIli23schemaDE_IT();
        }
        {
            File data=new File(TEST_OUT,"EnumOka.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("EnumOka");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"EnumOkb.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setDatasetName("EnumOkb");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                // tid's of class[a]
                HashSet<String> expectedTids= new HashSet<String>(Arrays.asList(new String[]{"o1","o2","x1","x2"}));
                HashSet<String> expectedIds= new HashSet<String>();
                Assert.assertTrue(stmt.execute("SELECT t_id, t_ili_tid FROM "+setup.prefixName("classa")));
                {
                    ResultSet rs=stmt.getResultSet();
                    while(!expectedTids.isEmpty()) {
                        Assert.assertTrue(rs.next());
                        String tid=rs.getString(2);
                        if(tid.equals("o2") || tid.equals("x2")) {
                            expectedIds.add(rs.getString(1));
                        }
                        assertTrue(expectedTids.remove(tid));
                    }
                    Assert.assertFalse(rs.next());
                }
                Assert.assertTrue(stmt.execute("SELECT classa_attra2 FROM "+setup.prefixName("structc")));
                {
                    ResultSet rs=stmt.getResultSet();
                    while(!expectedIds.isEmpty()) {
                        Assert.assertTrue(rs.next());
                        String tid=rs.getString(1);
                        assertTrue(expectedIds.remove(tid));
                    }
                    Assert.assertFalse(rs.next());
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'EnumOkA.Test1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("EnumOkA.TopicA",rs.getString(2));
                }
                Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'EnumOkB.Test1'"));
                {
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("EnumOkB.TopicB",rs.getString(2));
                }
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
    }
	
	@Test
	public void exportXtf23Original() throws Exception
	{
		{
			importXtf23();
		}
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
		{
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
		}
		try{
			
			
			HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
			XtfReader reader=new XtfReader(data);
			IoxEvent event=null;
			 do{
		        event=reader.read();
		        if(event instanceof StartTransferEvent){
		        }else if(event instanceof StartBasketEvent){
		        }else if(event instanceof ObjectEvent){
		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
		        	String oid=getOid(iomObj);
		        	if(oid!=null){
			        	objs.put(oid, iomObj);
		        	}
		        }else if(event instanceof EndBasketEvent){
		        }else if(event instanceof EndTransferEvent){
		        }
			 }while(!(event instanceof EndTransferEvent));
			 {
				 IomObject obj0 = objs.get("o1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("o2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
				 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA4"));
				 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA2"));
                 struct=obj0.getattrobj("attrA3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisA.TrStructA", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("trAttrA"));
                 Assert.assertEquals("a3.a32", struct.getattrvalue("trAttrA2"));
			 }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
			 {
				 IomObject obj0 = objs.get("x1");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
			 }
			 {
				 IomObject obj0 = objs.get("x2");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
				 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB4"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA2"));
                 struct=obj0.getattrobj("attrB3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisB.TrStructB", struct.getobjecttag());
                 Assert.assertEquals("b3.b32", struct.getattrvalue("trAttrB"));
                 Assert.assertEquals("b3.b32", struct.getattrvalue("trAttrB2"));
			 }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
		}finally{
		}
	}
    @Test
    public void deleteXtf23BasketA() throws Exception
    {
        {
            importXtf23();
        }
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            File data=new File(TEST_OUT,"EnumOka-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_DELETE);
            config.setDatasetName("EnumOka");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        Statement stmt=null;
        ResultSet rs=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // tid's of class[a]
            HashSet<String> expectedTids= new HashSet<String>(Arrays.asList(new String[]{ //"o1","o2",
                                                                                            "x1","x2"}));
            Assert.assertTrue(stmt.execute("SELECT t_id, t_ili_tid FROM "+setup.prefixName("classx")));
            {
                rs=stmt.getResultSet();
                while(!expectedTids.isEmpty()) {
                    Assert.assertTrue(rs.next());
                    String tid=rs.getString(2);
                    assertTrue(expectedTids.remove(tid));
                }
                Assert.assertFalse(rs.next());
                assertTrue(expectedTids.isEmpty());
            }
            
        }finally{
            if(rs!=null) {
                rs.close();
            }
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void deleteXtf23BasketB() throws Exception
    {
        {
            importXtf23();
        }
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            File data=new File(TEST_OUT,"EnumOka-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_DELETE);
            config.setDatasetName("EnumOkb");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        ResultSet rs=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            

            // tid's of class[a]
            HashSet<String> expectedTids= new HashSet<String>(Arrays.asList(new String[]{ "o1","o2",
                                                                                          //  "x1","x2"
                    }));
            Assert.assertTrue(stmt.execute("SELECT t_id, t_ili_tid FROM "+setup.prefixName("classx")));
            {
                rs=stmt.getResultSet();
                while(!expectedTids.isEmpty()) {
                    Assert.assertTrue(rs.next());
                    String tid=rs.getString(2);
                    assertTrue(expectedTids.remove(tid));
                }
                Assert.assertFalse(rs.next());
                assertTrue(expectedTids.isEmpty());
            }
            
        }finally{
            if(rs!=null) {
                rs.close();
            }
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportXtf23schemaDE_IT_Original() throws Exception
    {
        {
            importXtf23schemaDE_IT();
        }
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        try{
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    String oid=getOid(iomObj);
                    if(oid!=null){
                        objs.put(oid, iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA4"));
                 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA2"));
             }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB4"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA2"));
             }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
        }finally{
        }
    }
    private String getOid(IomObject iomObj) {
        String oid=iomObj.getobjectoid();
        if(oid!=null) {
            return oid;
        }
        String tag=iomObj.getobjecttag();
        if(tag.equals("EnumOkA.TopicA.n_nA")) {
            oid=getAssocId(iomObj, "n_nA_A", "n_nA_A2");
            return oid;
        }else if(tag.equals("EnumOkB.TopicB.n_nB")) {
            oid=getAssocId(iomObj, "n_nB_B", "n_nB_B2");
            return oid;
        }
        return null;
    }

    private String getAssocId(IomObject iomObj, String roleName1, String roleName2) {
        IomObject refObj1=iomObj.getattrobj(roleName1, 0);
        if(refObj1==null) {
            return null;
        }
        IomObject refObj2=iomObj.getattrobj(roleName2, 0);
        if(refObj2==null) {
            return null;
        }
        String ref1=refObj1.getobjectrefoid();
        if(ref1==null) {
            return null;
        }
        String ref2=refObj2.getobjectrefoid();
        if(ref2==null) {
            return null;
        }
        return ref1+":"+ref2;
    }

    @Test
    public void exportXtf23Translated() throws Exception
    {
        {
            importXtf23();
        }
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            config.setExportModels("EnumOkB");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        try{
            
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    String oid=getOid(iomObj);
                    if(oid!=null){
                        objs.put(oid, iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 struct=obj0.getattrobj("attrB3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisB.TrStructB", struct.getobjecttag());
                 Assert.assertEquals("b3.b32", struct.getattrvalue("trAttrB"));
             }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 struct=obj0.getattrobj("attrB3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisB.TrStructB", struct.getobjecttag());
                 Assert.assertEquals("b3.b32", struct.getattrvalue("trAttrB"));
             }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
        }finally{
        }
    }
    @Test
    public void exportXtf23schemaDE_IT_Translated() throws Exception
    {
        {
            importXtf23schemaDE_IT();
        }
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            config.setExportModels("EnumOkB");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        try{
            
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    String oid=getOid(iomObj);
                    if(oid!=null){
                        objs.put(oid, iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
             }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB", obj0.getobjecttag());
                 Assert.assertEquals("b2.b21", obj0.getattrvalue("attrB"));
                 IomObject struct=obj0.getattrobj("attrB2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
             }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.ClassB2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkB.TopicB.n_nB", obj0.getobjecttag());
             }
        }finally{
        }
    }
    @Test
    public void exportXtf23OriginLang() throws Exception
    {
        {
            importXtf23();
        }
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            config.setExportModels("EnumOkA");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            
        }
        try{
            
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    String oid=getOid(iomObj);
                    if(oid!=null){
                        objs.put(oid, iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 struct=obj0.getattrobj("attrA3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisA.TrStructA", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("trAttrA"));
             }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
                 struct=obj0.getattrobj("attrA3", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("TranslatedBasisA.TrStructA", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("trAttrA"));
             }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
        }finally{
        }
    }
    @Test
    public void exportXtf23schemaDE_IT_OriginLang() throws Exception
    {
        {
            importXtf23schemaDE_IT();
        }
        File data=new File(TEST_OUT,"EnumOka-out.xtf");
        {
            //EhiLogger.getInstance().setTraceFilter(false);
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setBaskets("EnumOkA.Test1;EnumOkB.Test1");
            config.setExportModels("EnumOkA");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        try{
            
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    String oid=getOid(iomObj);
                    if(oid!=null){
                        objs.put(oid, iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             {
                 IomObject obj0 = objs.get("o1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
             }
             {
                 IomObject obj0 = objs.get("o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("o1:o3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x2");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA", obj0.getobjecttag());
                 Assert.assertEquals("a2.a21", obj0.getattrvalue("attrA"));
                 IomObject struct=obj0.getattrobj("attrA2", 0);
                 Assert.assertNotNull(struct);
                 Assert.assertEquals("Basis.Struct", struct.getobjecttag());
                 Assert.assertEquals("a3.a32", struct.getattrvalue("attrA"));
             }
             {
                 IomObject obj0 = objs.get("x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.ClassA2", obj0.getobjecttag());
             }
             {
                 IomObject obj0 = objs.get("x1:x3");
                 Assert.assertNotNull(obj0);
                 Assert.assertEquals("EnumOkA.TopicA.n_nA", obj0.getobjecttag());
             }
        }finally{
        }
    }
	
	@Test
	public void importItf10() throws Exception
	{
        setup.resetDb();
        {
            File data=new File(TEST_OUT,"ModelAsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setIli1Translation("ModelBsimple10=ModelAsimple10");
            config.setDatasetName("ModelAsimple10");
            config.setDefaultSrsAuthority("EPSG");
            config.setDefaultSrsCode("21781");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"ModelBsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
    		// tid's of class[a]
			Assert.assertTrue(stmt.execute("SELECT classa.attra FROM "+setup.prefixName("classa")));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("o10",rs.getString(1));
			}
			// bid's of classa and classb are created
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelAsimple10.TopicA'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
			}
			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelBsimple10.TopicB'"));
			{
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
			}
		}finally{
		    if(stmt!=null) {
		        stmt.close();
		    }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importItf10Multi() throws Exception
    {
        setup.resetDb();
        {
            File data=new File(TEST_OUT,"ModelAsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            config.setIli1Translation("ModelBsimple10=ModelAsimple10;ModelCsimple10=ModelAsimple10");
            config.setDatasetName("ModelAsimple10");
            config.setDefaultSrsAuthority("EPSG");
            config.setDefaultSrsCode("21781");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"ModelBsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"ModelCsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setDatasetName("ModelCsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        
        Connection jdbcConnection=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            // tid's of class[a]
            Assert.assertTrue(stmt.execute("SELECT classa.attra FROM "+setup.prefixName("classa")));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("o10",rs.getString(1));
            }
            // bid's of classa and classb are created
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelAsimple10.TopicA'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
            }
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelBsimple10.TopicB'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
            }
            Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelCsimple10.TopicC'"));
            {
                ResultSet rs=stmt.getResultSet();
                Assert.assertTrue(rs.next());
                Assert.assertEquals("ModelCsimple10.TopicC",rs.getString(2));
            }
        }finally{
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
	@Test
	public void exportItf10() throws Exception
	{
		{
			importItf10();
		}

        File data=new File(TEST_OUT,"ModelAsimple10a-out.itf");
		{
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelAsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
		}
        File data2=new File(TEST_OUT,"ModelBsimple10a-out.itf");
		{
            Config config=setup.initConfig(data2.getPath(),data2.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
		}
		try{
	        {
	        	{
		    		
		    		// compile model
		    		TransferDescription td2=null;
		    		Configuration ili2cConfig=new Configuration();
		    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelAsimple10.ili", FileEntryKind.ILIMODELFILE);
		    		ili2cConfig.addFileEntry(fileEntry);
		    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
		    		assertNotNull(td2);
		    		
		    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
		    		ItfReader reader=new ItfReader(data);
		    		reader.setModel(td2);
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
						 IomObject obj0 = objs.get("10");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
						 Assert.assertEquals("o10",obj0.getattrvalue("attrA"));
					 }
		    		 {
						 IomObject obj0 = objs.get("11");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
						 Assert.assertEquals("o11",obj0.getattrvalue("attrA"));
					 }
					 {
						 IomObject obj0 = objs.get("12");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelAsimple10.TopicA.ClassA2", obj0.getobjecttag());
					 }
					 {
						IomObject iomObj = objs.get("15");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA3",attrtag);
						IomObject coord=iomObj.getattrobj("geomA", 0);
						assertTrue(coord.getattrvalue("C1").equals("480005.000"));
						assertTrue(coord.getattrvalue("C2").equals("70005.000"));
					 }
					 {
						 IomObject iomObj = objs.get("16");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA2_geomA",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA2", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
					 {
						 IomObject iomObj = objs.get("17");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelAsimple10.TopicA.ClassA3_geomA",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA3", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
	        	}
		        {
		    		
		    		// compile model
		    		TransferDescription td2=null;
		    		Configuration ili2cConfig=new Configuration();
		    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelBsimple10.ili", FileEntryKind.ILIMODELFILE);
		    		ili2cConfig.addFileEntry(fileEntry);
		    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
		    		assertNotNull(td2);
		    		
		    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
		    		ItfReader reader=new ItfReader(data2);
		    		reader.setModel(td2);
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
						 IomObject obj0 = objs.get("21");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
						 Assert.assertEquals("o21",obj0.getattrvalue("attrB"));
					 }
		    		 {
						 IomObject obj0 = objs.get("20");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
						 Assert.assertEquals("o20",obj0.getattrvalue("attrB"));
					 }
					 {
						 IomObject obj0 = objs.get("22");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
					 }
					 {
						 IomObject obj0 = objs.get("25");
						 Assert.assertNotNull(obj0);
						 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
						 IomObject obj1=obj0.getattrobj("geomB", 0);
						 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
						 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
					 }
					 {
						IomObject iomObj = objs.get("26");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelBsimple10.TopicB.ClassB2_geomB",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB2", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
					 {
						 IomObject iomObj = objs.get("27");
						String attrtag=iomObj.getobjecttag();
						assertEquals("ModelBsimple10.TopicB.ClassB3_geomB",attrtag);
						IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB3", 0);
						// convert
						CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
						// polygon1
						Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
						{
							com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord, coords[0]);
							com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
							assertEquals(coord2, coords[1]);
							com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
							assertEquals(coord3, coords[2]);
							com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
							assertEquals(coord4, coords[3]);
							com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
							assertEquals(coord5, coords[4]);
						}
					 }
		        }
	        }
		}finally{
		}
	}
    @Test
    public void deleteItf10DatasetA() throws Exception
    {
        {
            importItf10();
        }
        {
            File data=new File(TEST_OUT,"ModelAsimple10a-out.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_DELETE);
            config.setDatasetName("ModelAsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        ResultSet rs=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                {
                    rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName("classa")+" where classa.attra in ('o10','o11')");
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(0, rs.getLong(1));
                    rs.close();
                    rs=null;
                }
                {
                    rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName("classa")+" where classa.attra in ('o20','o21')");
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(2, rs.getLong(1));
                    rs.close();
                    rs=null;
                }
            }
        }finally{
            if(rs!=null) {
                rs.close();
            }
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void deleteItf10DatasetB() throws Exception
    {
        {
            importItf10();
        }
        {
            File data2=new File(TEST_OUT,"ModelBsimple10a-out.itf");
            Config config=setup.initConfig(data2.getPath(),data2.getPath()+".log");
            config.setFunction(Config.FC_DELETE);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        Connection jdbcConnection=null;
        ResultSet rs=null;
        Statement stmt=null;
        try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            {
                {
                    rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName("classa")+" where classa.attra in ('o10','o11')");
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(2, rs.getLong(1));
                    rs.close();
                    rs=null;
                }
                {
                    rs=stmt.executeQuery("SELECT count(*) FROM "+setup.prefixName("classa")+" where classa.attra in ('o20','o21')");
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(0, rs.getLong(1));
                    rs.close();
                    rs=null;
                }
            }
        }finally{
            if(rs!=null) {
                rs.close();
            }
            if(stmt!=null) {
                stmt.close();
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void exportItf10Multi() throws Exception
    {
        {
            importItf10Multi();
        }
        File dataA=new File(TEST_OUT,"ModelAsimple10a-out.itf");
        {
            Config config=setup.initConfig(dataA.getPath(),dataA.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelAsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        File dataB=new File(TEST_OUT,"ModelBsimple10a-out.itf");
        {
            Config config=setup.initConfig(dataB.getPath(),dataB.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        File dataC=new File(TEST_OUT,"ModelCsimple10a-out.itf");
        {
            Config config=setup.initConfig(dataC.getPath(),dataC.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelCsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        try{
            {
                {
                    
                    // compile model
                    TransferDescription td2=null;
                    Configuration ili2cConfig=new Configuration();
                    FileEntry fileEntry=new FileEntry("test/data/Translation/ModelAsimple10.ili", FileEntryKind.ILIMODELFILE);
                    ili2cConfig.addFileEntry(fileEntry);
                    td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
                    assertNotNull(td2);
                    
                    HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                    ItfReader reader=new ItfReader(dataA);
                    reader.setModel(td2);
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
                         IomObject obj0 = objs.get("10");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
                         Assert.assertEquals("o10",obj0.getattrvalue("attrA"));
                     }
                     {
                         IomObject obj0 = objs.get("11");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
                         Assert.assertEquals("o11",obj0.getattrvalue("attrA"));
                     }
                     {
                         IomObject obj0 = objs.get("12");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelAsimple10.TopicA.ClassA2", obj0.getobjecttag());
                     }
                     {
                        IomObject iomObj = objs.get("15");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelAsimple10.TopicA.ClassA3",attrtag);
                        IomObject coord=iomObj.getattrobj("geomA", 0);
                        assertTrue(coord.getattrvalue("C1").equals("480005.000"));
                        assertTrue(coord.getattrvalue("C2").equals("70005.000"));
                     }
                     {
                         IomObject iomObj = objs.get("16");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelAsimple10.TopicA.ClassA2_geomA",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA2", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                     {
                         IomObject iomObj = objs.get("17");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelAsimple10.TopicA.ClassA3_geomA",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA3", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                }
                {
                    
                    // compile model
                    TransferDescription td2=null;
                    Configuration ili2cConfig=new Configuration();
                    FileEntry fileEntry=new FileEntry("test/data/Translation/ModelBsimple10.ili", FileEntryKind.ILIMODELFILE);
                    ili2cConfig.addFileEntry(fileEntry);
                    td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
                    assertNotNull(td2);
                    
                    HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                    ItfReader reader=new ItfReader(dataB);
                    reader.setModel(td2);
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
                         IomObject obj0 = objs.get("21");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
                         Assert.assertEquals("o21",obj0.getattrvalue("attrB"));
                     }
                     {
                         IomObject obj0 = objs.get("20");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
                         Assert.assertEquals("o20",obj0.getattrvalue("attrB"));
                     }
                     {
                         IomObject obj0 = objs.get("22");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
                     }
                     {
                         IomObject obj0 = objs.get("25");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
                         IomObject obj1=obj0.getattrobj("geomB", 0);
                         Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
                         Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
                     }
                     {
                        IomObject iomObj = objs.get("26");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelBsimple10.TopicB.ClassB2_geomB",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB2", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                     {
                         IomObject iomObj = objs.get("27");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelBsimple10.TopicB.ClassB3_geomB",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassB3", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                }
                {
                    
                    // compile model
                    TransferDescription td2=null;
                    Configuration ili2cConfig=new Configuration();
                    FileEntry fileEntry=new FileEntry("test/data/Translation/ModelCsimple10.ili", FileEntryKind.ILIMODELFILE);
                    ili2cConfig.addFileEntry(fileEntry);
                    td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
                    assertNotNull(td2);
                    
                    HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
                    ItfReader reader=new ItfReader(dataC);
                    reader.setModel(td2);
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
                         IomObject obj0 = objs.get("31");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelCsimple10.TopicC.ClassC", obj0.getobjecttag());
                         Assert.assertEquals("o31",obj0.getattrvalue("attrC"));
                     }
                     {
                         IomObject obj0 = objs.get("30");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelCsimple10.TopicC.ClassC", obj0.getobjecttag());
                         Assert.assertEquals("o30",obj0.getattrvalue("attrC"));
                     }
                     {
                         IomObject obj0 = objs.get("32");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelCsimple10.TopicC.ClassC2", obj0.getobjecttag());
                     }
                     {
                         IomObject obj0 = objs.get("35");
                         Assert.assertNotNull(obj0);
                         Assert.assertEquals("ModelCsimple10.TopicC.ClassC3", obj0.getobjecttag());
                         IomObject obj1=obj0.getattrobj("geomC", 0);
                         Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
                         Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
                     }
                     {
                        IomObject iomObj = objs.get("36");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelCsimple10.TopicC.ClassC2_geomC",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassC2", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                     {
                         IomObject iomObj = objs.get("37");
                        String attrtag=iomObj.getobjecttag();
                        assertEquals("ModelCsimple10.TopicC.ClassC3_geomC",attrtag);
                        IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassC3", 0);
                        // convert
                        CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
                        // polygon1
                        Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
                        {
                            com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord, coords[0]);
                            com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
                            assertEquals(coord2, coords[1]);
                            com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
                            assertEquals(coord3, coords[2]);
                            com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
                            assertEquals(coord4, coords[3]);
                            com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
                            assertEquals(coord5, coords[4]);
                        }
                     }
                }
            }
        }finally{
        }
    }
	
	@Test
	public void importItf10lineTable() throws Exception
	{
        setup.resetDb();
        {
            File data=new File(TEST_OUT,"ModelAsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setImportBid(true);
            config.setImportTid(true);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
            Ili2db.setSkipPolygonBuilding(config);
            config.setIli1Translation("ModelBsimple10=ModelAsimple10");
            config.setDatasetName("ModelAsimple10");
            config.setDefaultSrsAuthority("EPSG");
            config.setDefaultSrsCode("21781");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
        {
            File data=new File(TEST_OUT,"ModelBsimple10a.itf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_IMPORT);
            config.setImportTid(true);
            config.setImportBid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
        }
		Connection jdbcConnection=null;
        Statement stmt=null;
		try{
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
 			validateImportItf10lineTable_Geom(stmt);
 			// bid's of classa and classb are created
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelAsimple10.TopicA'"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelAsimple10.TopicA",rs.getString(2));
 			}
 			Assert.assertTrue(stmt.execute("SELECT t_ili2db_basket.t_id, t_ili2db_basket.topic FROM "+setup.prefixName("t_ili2db_basket")+" WHERE t_ili2db_basket.t_ili_tid = 'ModelBsimple10.TopicB'"));
 			{
 				ResultSet rs=stmt.getResultSet();
 				Assert.assertTrue(rs.next());
 				Assert.assertEquals("ModelBsimple10.TopicB",rs.getString(2));
 			}
		}finally{
		    if(stmt!=null) {
		        stmt.close();
		    }
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

    protected void validateImportItf10lineTable_Geom(Statement stmt) throws SQLException {
        Assert.assertTrue(stmt.execute("SELECT st_asewkt(classa2_geoma._geom) FROM "+setup.prefixName("classa2_geoma")));
        {
        	ResultSet rs=stmt.getResultSet();
        	Assert.assertTrue(rs.next());
        	Assert.assertEquals("SRID=21781;COMPOUNDCURVE((480000 70000,480010 70000,480010 70010,480000 70010,480000 70000))",rs.getString(1));
        }
        Assert.assertTrue(stmt.execute("SELECT st_asewkt(classa3_geoma._geom) FROM "+setup.prefixName("classa3_geoma")));
        {
        	ResultSet rs=stmt.getResultSet();
        	Assert.assertTrue(rs.next());
        	Assert.assertEquals("SRID=21781;COMPOUNDCURVE((480000 70000,480010 70000,480010 70010,480000 70010,480000 70000))",rs.getString(1));
        }
    }
    
	@Test
	public void exportItf10lineTable() throws Exception
	{
		{
			importItf10lineTable();
		}
        File dataA=new File(TEST_OUT,"ModelAsimple10a-out.itf");
		{
            Config config=setup.initConfig(dataA.getPath(),dataA.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelAsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
		}
        File dataB=new File(TEST_OUT,"ModelBsimple10a-out.itf");
		{
            Config config=setup.initConfig(dataB.getPath(),dataB.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setDatasetName("ModelBsimple10");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
		}
		try{
			//EhiLogger.getInstance().setTraceFilter(false);
	        {
	    		
	    		// compile model
	    		TransferDescription td2=null;
	    		Configuration ili2cConfig=new Configuration();
	    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelAsimple10.ili", FileEntryKind.ILIMODELFILE);
	    		ili2cConfig.addFileEntry(fileEntry);
	    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
	    		assertNotNull(td2);
	    		
	    		ItfReader reader=new ItfReader(dataA);
	    		reader.setModel(td2);
	    		IoxEvent event=null;
	    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
	    		 do{
	    		        event=reader.read();
	    		        if(event instanceof StartTransferEvent){
	    		        }else if(event instanceof StartBasketEvent){
	    		        }else if(event instanceof ObjectEvent){
	    		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
	    		    		assertNotNull(iomObj.getobjectoid());
	    		    		objs.put(iomObj.getobjectoid(), iomObj);
	    		        }else if(event instanceof EndBasketEvent){
	    		        }else if(event instanceof EndTransferEvent){
	    		        }
	    		 }while(!(event instanceof EndTransferEvent));
				 {
					 IomObject obj0 = objs.get("10");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
					 Assert.assertEquals("o10",obj0.getattrvalue("attrA"));
				 }
	    		 {
					 IomObject obj0 = objs.get("11");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA", obj0.getobjecttag());
					 Assert.assertEquals("o11",obj0.getattrvalue("attrA"));
				 }
				 {
					 IomObject obj0 = objs.get("12");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA2", obj0.getobjecttag());
				 }
				 {
					 IomObject iomObj = objs.get("13");
					String attrtag=iomObj.getobjecttag();
					assertEquals("ModelAsimple10.TopicA.ClassA2_geomA",attrtag);
					IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA2", 0);
					// convert
					CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
					// polygon1
					Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
					{
						com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord, coords[0]);
						com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
						assertEquals(coord2, coords[1]);
						com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
						assertEquals(coord3, coords[2]);
						com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
						assertEquals(coord4, coords[3]);
						com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord5, coords[4]);
					}
					IomObject iomObj2=iomObj.getattrobj("_itf_ref_ClassA2", 0);
					assertEquals("12", iomObj2.getobjectrefoid());
				 }
				 {
					 IomObject iomObj = objs.get("14");
					String attrtag=iomObj.getobjecttag();
					assertEquals("ModelAsimple10.TopicA.ClassA3_geomA",attrtag);
					IomObject multisurface=iomObj.getattrobj("_itf_geom_ClassA3", 0);
					// convert
					CoordinateList jtsMultipolygon=Iox2jts.polyline2JTS(multisurface, false, 0);
					// polygon1
					Coordinate[] coords=jtsMultipolygon.toCoordinateArray();
					{
						com.vividsolutions.jts.geom.Coordinate coord=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord, coords[0]);
						com.vividsolutions.jts.geom.Coordinate coord2=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70000.0"));
						assertEquals(coord2, coords[1]);
						com.vividsolutions.jts.geom.Coordinate coord3=new com.vividsolutions.jts.geom.Coordinate(new Double("480010.0"), new Double("70010.0"));
						assertEquals(coord3, coords[2]);
						com.vividsolutions.jts.geom.Coordinate coord4=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70010.0"));
						assertEquals(coord4, coords[3]);
						com.vividsolutions.jts.geom.Coordinate coord5=new com.vividsolutions.jts.geom.Coordinate(new Double("480000.0"), new Double("70000.0"));
						assertEquals(coord5, coords[4]);
					}
				 }
				 {
					 IomObject obj0 = objs.get("15");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelAsimple10.TopicA.ClassA3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("geomA", 0);
					 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
					 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
				 }
	        }
	        {
	    		
	    		// compile model
	    		TransferDescription td2=null;
	    		Configuration ili2cConfig=new Configuration();
	    		FileEntry fileEntry=new FileEntry("test/data/Translation/ModelBsimple10.ili", FileEntryKind.ILIMODELFILE);
	    		ili2cConfig.addFileEntry(fileEntry);
	    		td2=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
	    		assertNotNull(td2);
	    		
	    		ItfReader reader=new ItfReader(dataB);
	    		reader.setModel(td2);
	    		IoxEvent event=null;
	    		HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
	    		 do{
    		        event=reader.read();
    		        if(event instanceof StartTransferEvent){
    		        }else if(event instanceof StartBasketEvent){
    		        }else if(event instanceof ObjectEvent){
    		        	IomObject iomObj=((ObjectEvent)event).getIomObject();
    		    		assertNotNull(iomObj.getobjectoid());
    		    		objs.put(iomObj.getobjectoid(), iomObj);
    		        }else if(event instanceof EndBasketEvent){
    		        }else if(event instanceof EndTransferEvent){
    		        }
	    		 }while(!(event instanceof EndTransferEvent));
				 {
					 IomObject obj0 = objs.get("21");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
					 Assert.assertEquals("o21",obj0.getattrvalue("attrB"));
				 }
	    		 {
					 IomObject obj0 = objs.get("20");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB", obj0.getobjecttag());
					 Assert.assertEquals("o20",obj0.getattrvalue("attrB"));
				 }
				 {
					 IomObject obj0 = objs.get("22");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB2", obj0.getobjecttag());
				 }
				 {
					 IomObject obj0 = objs.get("25");
					 Assert.assertNotNull(obj0);
					 Assert.assertEquals("ModelBsimple10.TopicB.ClassB3", obj0.getobjecttag());
					 IomObject obj1=obj0.getattrobj("geomB", 0);
					 Assert.assertEquals("480005.000",obj1.getattrvalue("C1"));
					 Assert.assertEquals("70005.000",obj1.getattrvalue("C2"));
				 }
	        }
		}finally{
		}
	}
}