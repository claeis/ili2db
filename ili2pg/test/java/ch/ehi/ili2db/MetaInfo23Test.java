package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.metaattr.MetaAttrUtility;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class MetaInfo23Test {
	private static final String DBSCHEMA = "MetaInfo23";
    private static final String TEST_DATA_DIR = "test/data/MetaInfo";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
	Statement stmt=null;
	
	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);
		config.setDburl(dburl);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}
	
	@Test
	public void importIli() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File(TEST_DATA_DIR,"MetaInfo23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				config.setCreateMetaInfo(true);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String selStmt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+", "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+" FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"=?";
					java.sql.PreparedStatement selPrepStmt = jdbcConnection.prepareStatement(selStmt);
					{
						selPrepStmt.setString(1, "classa1");
						selPrepStmt.setString(2, "numx");
						selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_UNIT);
						ResultSet rs = selPrepStmt.executeQuery();
						Assert.assertTrue(rs.next());
						Assert.assertEquals("m",rs.getString(1));
						Assert.assertEquals("classa1b",rs.getString(2));
                        Assert.assertFalse(rs.next());
					}
					{
						selPrepStmt.setString(1, "classa1");
						selPrepStmt.setString(2, "numa");
						selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_UNIT);
						ResultSet rs = selPrepStmt.executeQuery();
						Assert.assertTrue(rs.next());
						Assert.assertEquals("m",rs.getString(1));
						Assert.assertEquals(null,rs.getString(2));
                        Assert.assertFalse(rs.next());
					}
                    {
                        selPrepStmt.setString(1, "classc");
                        selPrepStmt.setString(2, "geom");
                        selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_C1_MAX);
                        ResultSet rs = selPrepStmt.executeQuery();
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("2870000.000",rs.getString(1));
                        Assert.assertEquals(null,rs.getString(2));
                        Assert.assertFalse(rs.next());
                    }
					
		            {
		                // t_ili2db_attrname
		                String [][] expectedValues=new String[][] {
		                    {"MetaInfo23.TestA.ClassA.num0", "num0", "classa1", null},
		                    {"MetaInfo23.TestA.a2b.a", "a", "a2b", "classa1"},
		                    {"MetaInfo23.TestA.a2b.b", "b", "a2b", "classb1"},
		                    {"MetaInfo23.TestA.ClassA1.enumb", "enumb", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.enuma", "enuma", "classa1", null}, 
		                    {"MetaInfo23.TestA.ClassA1.mtextb", "mtextb", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.mtexta", "mtexta", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.texta", "texta", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1b.numx", "numx", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.numa", "numa", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.textb", "textb", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.numb", "numb", "classa1", null},   
		                    {"MetaInfo23.TestA.ClassA1.structa", "classa1_structa", "structa1", "classa1"},
		                    {"MetaInfo23.TestA.ClassC.geom","geom","classc",null}
		                };
		                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
		            }
		            {
		                // t_ili2db_trafo
		                String [][] expectedValues=new String[][] {
		                    {"MetaInfo23.TestA.Codelist", "ch.ehi.ili2db.inheritance", "newClass"},
		                    {"MetaInfo23.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "superClass"},
		                    {"MetaInfo23.TestA.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
		                    {"MetaInfo23.TestA.a2b", "ch.ehi.ili2db.inheritance", "newClass"},
		                    {"CatalogueObjects_V1.Catalogues.Item", "ch.ehi.ili2db.inheritance", "subClass"},
		                    {"MetaInfo23.TestA.StructA1", "ch.ehi.ili2db.inheritance", "newClass"},
		                    {"MetaInfo23.TestA.ClassB1",  "ch.ehi.ili2db.inheritance", "newClass"},
		                    {"MetaInfo23.TestA.ClassA1",  "ch.ehi.ili2db.inheritance", "newClass"},
		                    {"MetaInfo23.TestA.ClassC","ch.ehi.ili2db.inheritance","newClass"}
		                };
		                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
		            }
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    @Test
    public void importIliView() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"View23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
                config.setCatalogueRefTrafo(null);
                config.setMultiSurfaceTrafo(null);
                config.setMultilingualTrafo(null);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCreateMetaInfo(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliAssoc() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"Assoc23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCreateMetaInfo(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String selStmt="SELECT "+DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL+" FROM "+DBSCHEMA+"."+DbNames.META_ATTRIBUTES_TAB+" WHERE "+DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL+"=? AND "+DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL+"=?";
                    java.sql.PreparedStatement selPrepStmt = jdbcConnection.prepareStatement(selStmt);
                    {
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.comp1.comp1_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_KIND);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals(MetaAttrUtility.METAATTRVALUE_ASSOC_KIND_COMPOSITE,rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.comp1.comp1_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("0",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.comp1.comp1_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("1",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                    // 
                    {
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.agg3.agg3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_KIND);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals(MetaAttrUtility.METAATTRVALUE_ASSOC_KIND_AGGREGATE,rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.agg3.agg3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("0",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.agg3.agg3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("*",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                    {
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.assoc3.assoc3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_KIND);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals(MetaAttrUtility.METAATTRVALUE_ASSOC_KIND_ASSOCIATE,rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.assoc3.assoc3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("0",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Assoc23.Topic.assoc3.assoc3_a");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ASSOC_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("*",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliBag() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_DATA_DIR,"Bag23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCreateMetaInfo(true);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String selStmt="SELECT "+DbNames.META_ATTRIBUTES_TAB_ATTRVALUE_COL+" FROM "+DBSCHEMA+"."+DbNames.META_ATTRIBUTES_TAB+" WHERE "+DbNames.META_ATTRIBUTES_TAB_ILIELEMENT_COL+"=? AND "+DbNames.META_ATTRIBUTES_TAB_ATTRNAME_COL+"=?";
                    java.sql.PreparedStatement selPrepStmt = jdbcConnection.prepareStatement(selStmt);
                    {
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA21");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("1",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA21");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("1",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                    {
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA22");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("0",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA22");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("1",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                    {
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA23");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MIN);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("0",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                        {
                            selPrepStmt.setString(1, "Bag23.Topic.ClassA2.attrA23");
                            selPrepStmt.setString(2, MetaAttrUtility.ILI2DB_ILI_ATTR_CARDINALITY_MAX);
                            ResultSet rs = selPrepStmt.executeQuery();
                            Assert.assertTrue(rs.next());
                            Assert.assertEquals("*",rs.getString(1));
                            Assert.assertFalse(rs.next());
                        }
                    }
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importXtfTwice() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                
                File data=new File(TEST_DATA_DIR,"MetaInfo23a.xtf");
                {
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    config.setCreateFk(Config.CREATE_FK_YES);
                    config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                    config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                    config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
                    config.setCatalogueRefTrafo(null);
                    config.setMultiSurfaceTrafo(null);
                    config.setMultilingualTrafo(null);
                    config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                    config.setCreateMetaInfo(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                    config.setFunction(Config.FC_IMPORT);
                    config.setDoImplicitSchemaImport(true);
                    Ili2db.readSettingsFromDb(config);
                    Ili2db.run(config,null);
                }
                {
                    String selStmt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+", "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+" FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"=?";
                    java.sql.PreparedStatement selPrepStmt = jdbcConnection.prepareStatement(selStmt);
                    {
                        selPrepStmt.setString(1, "classa1");
                        selPrepStmt.setString(2, "numx");
                        selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_UNIT);
                        ResultSet rs = selPrepStmt.executeQuery();
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("m",rs.getString(1));
                        Assert.assertEquals("classa1b",rs.getString(2));
                        Assert.assertFalse(rs.next());
                    }
                    {
                        selPrepStmt.setString(1, "classa1");
                        selPrepStmt.setString(2, "numa");
                        selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_UNIT);
                        ResultSet rs = selPrepStmt.executeQuery();
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("m",rs.getString(1));
                        Assert.assertEquals(null,rs.getString(2));
                        Assert.assertFalse(rs.next());
                    }
                    {
                        selPrepStmt.setString(1, "classc");
                        selPrepStmt.setString(2, "geom");
                        selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_C1_MAX);
                        ResultSet rs = selPrepStmt.executeQuery();
                        Assert.assertTrue(rs.next());
                        Assert.assertEquals("2870000.000",rs.getString(1));
                        Assert.assertEquals(null,rs.getString(2));
                        Assert.assertFalse(rs.next());
                    }
                    
                    {
                        // t_ili2db_attrname
                        String [][] expectedValues=new String[][] {
                            {"MetaInfo23.TestA.ClassA.num0", "num0", "classa1", null},
                            {"MetaInfo23.TestA.a2b.a", "a", "a2b", "classa1"},
                            {"MetaInfo23.TestA.a2b.b", "b", "a2b", "classb1"},
                            {"MetaInfo23.TestA.ClassA1.enumb", "enumb", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.enuma", "enuma", "classa1", null}, 
                            {"MetaInfo23.TestA.ClassA1.mtextb", "mtextb", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.mtexta", "mtexta", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.texta", "texta", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1b.numx", "numx", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.numa", "numa", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.textb", "textb", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.numb", "numb", "classa1", null},   
                            {"MetaInfo23.TestA.ClassA1.structa", "classa1_structa", "structa1", "classa1"},
                            {"MetaInfo23.TestA.ClassC.geom","geom","classc",null}
                        };
                        Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                    }
                    {
                        // t_ili2db_trafo
                        String [][] expectedValues=new String[][] {
                            {"MetaInfo23.TestA.Codelist", "ch.ehi.ili2db.inheritance", "newClass"},
                            {"MetaInfo23.TestA.ClassA1b", "ch.ehi.ili2db.inheritance", "superClass"},
                            {"MetaInfo23.TestA.ClassA", "ch.ehi.ili2db.inheritance", "subClass"},
                            {"MetaInfo23.TestA.a2b", "ch.ehi.ili2db.inheritance", "newClass"},
                            {"CatalogueObjects_V1.Catalogues.Item", "ch.ehi.ili2db.inheritance", "subClass"},
                            {"MetaInfo23.TestA.StructA1", "ch.ehi.ili2db.inheritance", "newClass"},
                            {"MetaInfo23.TestA.ClassB1",  "ch.ehi.ili2db.inheritance", "newClass"},
                            {"MetaInfo23.TestA.ClassA1",  "ch.ehi.ili2db.inheritance", "newClass"},
                            {"MetaInfo23.TestA.ClassC","ch.ehi.ili2db.inheritance","newClass"}
                        };
                        Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                    }
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    @Test
    public void importIliT_TypeColumnProp() throws Exception {
        Connection jdbcConnection = null;
        Statement stmt = null;

        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

            File data = new File(TEST_DATA_DIR, "T_Type23.ili");
            Config config = initConfig(data.getPath(), DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateMetaInfo(true);

            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);

            String query = "SELECT setting FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+
                " WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+" = 'ch.ehi.ili2db.types' and "
                + DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+" = 'T_Type' and "
                + DbNames.META_INFO_TABLE_TAB_TABLENAME_COL+" = 'classa1';";

            Assert.assertTrue(stmt.execute(query));
            ResultSet rs = stmt.getResultSet();

            Assert.assertTrue(rs.next());
            String setting = rs.getString(1);
            Assert.assertEquals("[\"classa1\",\"classa1b\",\"classa1c\",\"classa1d\"]", setting);

        }finally{
            if(jdbcConnection != null){
                jdbcConnection.close();
            }
        }
    }

    @Test
    public void importIliT_TypeConstraint() throws Exception {
        Connection jdbcConnection = null;
        Statement stmt = null;

        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");

            File data = new File(TEST_DATA_DIR, "T_Type23.ili");
            Config config = initConfig(data.getPath(), DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateTypeConstraint(true);

            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);

            String query = "SELECT pg_catalog.pg_get_constraintdef(r.oid, true) as condef FROM pg_catalog.pg_constraint r WHERE r.conrelid = '"+DBSCHEMA.toLowerCase()+".classa1'::regclass AND r.contype = 'c';";

            Assert.assertTrue(stmt.execute(query));
            ResultSet rs = stmt.getResultSet();

            Assert.assertTrue(rs.next());
            String constraint = rs.getString(1);
            Assert.assertTrue(constraint.contains("classa1"));
            Assert.assertTrue(constraint.contains("classa1b"));
            Assert.assertTrue(constraint.contains("classa1c"));
            Assert.assertTrue(constraint.contains("classa1d"));

        }finally{
            if(jdbcConnection != null){
                jdbcConnection.close();
            }
        }
    }
	
}