package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import net.iharder.Base64;

public abstract class Datatypes23Test {
    private static final String EXPECTED_XMLBOX = "<x xmlns=\"http://www.interlis.ch/INTERLIS2.3\">\n" + 
    "                           <a></a>\n" + 
    "                       </x>";
    protected static final String TEST_OUT = "test/data/Datatypes23/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	private void doImportIli(Connection jdbcConnection,Statement stmt,boolean withMetadata) throws Exception{
        File data=new File(TEST_OUT+"Datatypes23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateTextChecks(true);
        config.setCreateNumChecks(true);
        config.setCreateDateTimeChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        if(withMetadata) {
            config.setCreateMetaInfo(true);
        }
        //Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
        {
            // t_ili2db_attrname
            String [][] expectedValues=new String[][] {
                {"Datatypes23.Topic.SimpleSurface3.surface3d", "surface3d", "simplesurface3", null},
                {"Datatypes23.Topic.ClassAttr.vertAlignment", "vertalignment", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aTime", "atime", "classattr", null},
                {"Datatypes23.Topic.SimpleSurface2.surface2d", "surface2d", "simplesurface2", null},
                {"Datatypes23.Topic.Surface2.surfacearcs2d", "surfacearcs2d", "surface2", null},
                {"Datatypes23.Topic.ClassAttr.aUuid", "auuid", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aClass", "aclass", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.uritext", "uritext", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.horizAlignment", "horizalignment", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aI32id", "ai32id", "classattr", null},
                {"Datatypes23.Topic.Line3.straightsarcs3d", "straightsarcs3d", "line3", null},
                {"Datatypes23.Topic.ClassAttr.mtextLimited", "mtextlimited", "classattr", null},
                {"Datatypes23.Topic.SimpleLine3.straights3d", "straights3d", "simpleline3", null},
                {"Datatypes23.Topic.ClassAttr.aAttribute", "aattribute", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aBoolean", "aboolean", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aDateTime", "adatetime", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.formattedText", "formattedtext", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.textLimited", "textlimited", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.nametext", "nametext", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aufzaehlung", "aufzaehlung", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.xmlbox", "xmlbox", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aStandardid", "astandardid", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.aDate", "adate", "classattr", null},
                {"Datatypes23.Topic.ClassKoord3.hcoord", "hcoord", "classkoord3", null},
                {"Datatypes23.Topic.ClassKoord2.lcoord", "lcoord", "classkoord2", null},
                {"Datatypes23.Topic.ClassAttr.numericInt", "numericint", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.numericBigInt", "numericbigint", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.numericDec", "numericdec", "classattr", null},
                {"Datatypes23.Topic.SimpleLine2.straights2d", "straights2d", "simpleline2", null},
                {"Datatypes23.Topic.Line2.straightsarcs2d", "straightsarcs2d", "line2", null},
                {"Datatypes23.Topic.ClassAttr.textUnlimited", "textunlimited", "classattr", null},
                {"Datatypes23.Topic.ClassAttr.binbox", "binbox", "classattr", null},
                {"Datatypes23.Topic.Surface3.surfacearcs3d", "surfacearcs3d", "surface3", null},
                {"Datatypes23.Topic.ClassAttr.mtextUnlimited", "mtextunlimited", "classattr", null},
                {"Datatypes23.Topic.Form.a", "a", "form", null},
                {"Datatypes23.Topic.Form.b", "b", "form", null},
            };
            Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
            
        }
        {
            // t_ili2db_trafo
            String [][] expectedValues=new String[][] {
                {"Datatypes23.Topic.SimpleLine3", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.SimpleSurface3", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.Surface2", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.SimpleSurface2", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.Surface3", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.Form", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.ClassAttr", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.ClassKoord3", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.Line3", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.ClassKoord2", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.SimpleLine2", "ch.ehi.ili2db.inheritance", "newClass"},
                {"Datatypes23.Topic.Line2", "ch.ehi.ili2db.inheritance", "newClass"}
            };
            Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
        }
	}
    @Test
    public void importIli() throws Exception{
        Connection jdbcConnection=null;
        Statement stmt=null;
        try {
            //EhiLogger.getInstance().setTraceFilter(false);
            setup.resetDb();
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            doImportIli(jdbcConnection,stmt,false);
        }catch(SQLException e) {
            throw new IoxException(e);
        }finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
    @Test
    public void importIliWithMetadata() throws Exception{
        Connection jdbcConnection=null;
        Statement stmt=null;
        java.sql.PreparedStatement selPrepStmt=null;
        try {
            //EhiLogger.getInstance().setTraceFilter(false);
            setup.resetDb();
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            doImportIli(jdbcConnection,stmt,true);
            {
                String selStmt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SETTING_COL+", "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+" FROM "+setup.prefixName(DbNames.META_INFO_COLUMN_TAB)+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_COLUMNNAME_COL+"=? AND "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"=?";
                selPrepStmt = jdbcConnection.prepareStatement(selStmt);
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "textunlimited");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_TEXT,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "mtextlimited");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_MTEXT,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "nametext");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_NAME,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "uritext");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_URI,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "xmlbox");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_XML,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "binbox");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_BINARY,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "horizalignment");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_ENUM,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "astandardid");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_OID,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "adate");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_DATE,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "formattedtext");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_FORMATTED,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "aclass");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_CLASSQNAME,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                {
                    selPrepStmt.setString(1, "classattr");
                    selPrepStmt.setString(2, "aattribute");
                    selPrepStmt.setString(3, DbExtMetaInfo.TAG_COL_TYPEKIND);
                    ResultSet rs = selPrepStmt.executeQuery();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(DbExtMetaInfo.TAG_COL_TYPEKIND_ATTRIBUTEQNAME,rs.getString(1));
                    Assert.assertEquals(null,rs.getString(2));
                    Assert.assertFalse(rs.next());
                }
                
            }
        }catch(SQLException e) {
            throw new IoxException(e);
        }finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(selPrepStmt!=null) {
                selPrepStmt.close();
                selPrepStmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
	
	@Test
	public void importXtfAttr() throws Exception{
	    //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        Statement stmt=null;
		try {
            setup.resetDb();
            jdbcConnection = setup.createConnection();
	        stmt=jdbcConnection.createStatement();
			File data=new File(TEST_OUT+"Datatypes23Attr.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	        Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			//Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
			String stmtTxt="SELECT * FROM "+setup.prefixName("classattr")+" ORDER BY t_id ASC";
			{
				 Assert.assertTrue(stmt.execute(stmtTxt));
				 ResultSet rs=stmt.getResultSet();
				 {
	                 Assert.assertTrue(rs.next());
	                 Assert.assertEquals("22", rs.getString("aI32id"));
	                 Assert.assertEquals(true, rs.getBoolean("aBoolean"));
	                 Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", rs.getString("aUuid"));
	                 Assert.assertEquals("abc100\"\"''", rs.getString("textLimited"));
	                 Assert.assertEquals("Left", rs.getString("horizAlignment"));
	                 Assert.assertEquals("mailto:ceis@localhost", rs.getString("uritext"));
	                 Assert.assertEquals("5", rs.getString("numericInt"));
	                String xmlbox = rs.getString("xmlbox");
	                String expectedXmlbox = EXPECTED_XMLBOX;
	                Diff xmlboxDiff = DiffBuilder.compare(Input.fromString(expectedXmlbox)).withTest(Input.fromString(xmlbox))
	                        .checkForSimilar().normalizeWhitespace().build();
	                                
	                  Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());                
	                 Assert.assertEquals("mehr.vier", rs.getString("aufzaehlung"));
	                 Assert.assertEquals("09:00:00", rs.getString("aTime"));
	                 Assert.assertEquals("abc200\n" + 
	                        "end200", rs.getString("mtextLimited"));
	                 Assert.assertEquals("chgAAAAAAAAA0azD", rs.getString("aStandardid"));
	                 Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", rs.getString("aAttribute"));
	                 Assert.assertEquals("2002-09-24", rs.getString("aDate"));
	                 Assert.assertEquals("Top", rs.getString("vertAlignment"));
	                 Assert.assertEquals("ClassA", rs.getString("nametext"));
	                 Assert.assertEquals("abc101", rs.getString("textUnlimited"));
	                 Assert.assertEquals("6.0", rs.getString("numericDec"));
	                 Assert.assertEquals("abc201\n" +
	                        "end201", rs.getString("mtextUnlimited"));
	                 Assert.assertEquals("1900-01-01 12:30:05", rs.getString("aDateTime"));
				 }
		            {
		                Assert.assertTrue(rs.next());
	                     Assert.assertEquals(null, rs.getString("textLimited"));
	                     Assert.assertTrue(rs.wasNull());
	                     Assert.assertEquals("textNull", rs.getString("textUnlimited"));
		                
		            }
			}
			{
				// byte array
				String stmtT="SELECT binbox FROM "+setup.prefixName("classattr")+" ORDER BY t_id ASC";
				Assert.assertTrue(stmt.execute(stmtT));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				byte[] bytes=(byte[])rs.getObject("binbox");
				Assert.assertFalse(rs.wasNull());
				String wkbText=Base64.encodeBytes(bytes);
				Assert.assertEquals("AAAA", wkbText);
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
		}
	}

	@Test
	public void importXtfAttr_asText() throws Exception{
		Connection jdbcConnection=null;
		Statement stmt=null;
		try {
			setup.resetDb();
			jdbcConnection = setup.createConnection();
			stmt=jdbcConnection.createStatement();
			File data=new File(TEST_OUT+"Datatypes23Attr_invalidSimpleTypes.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
	        Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
			config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setSqlColsAsText(Config.SQL_COLS_AS_TEXT_ENABLE);
			config.setValidation(false);
			//Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			String stmtTxt="SELECT * FROM "+setup.prefixName("classattr")+" ORDER BY t_id ASC";
			{
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				{
					Assert.assertTrue(rs.next());
					Assert.assertEquals("22", rs.getString("aI32id"));
					Assert.assertEquals("falschhhhhhh", rs.getString("aBoolean"));
					Assert.assertEquals("23A", rs.getString("aUuid"));
					Assert.assertEquals("1111111111111111111111111111111111111111", rs.getString("textLimited"));
					Assert.assertEquals("42", rs.getString("horizAlignment"));
					Assert.assertEquals("mailto:ceis@localhost", rs.getString("uritext"));
					Assert.assertEquals("im not an int", rs.getString("numericInt"));
					Assert.assertEquals("im not a BIG int", rs.getString("numericBigInt"));
					String xmlbox = rs.getString("xmlbox");
					String expectedXmlbox = EXPECTED_XMLBOX;
					Diff xmlboxDiff = DiffBuilder.compare(Input.fromString(expectedXmlbox)).withTest(Input.fromString(xmlbox))
							.checkForSimilar().normalizeWhitespace().build();

					Assert.assertFalse(xmlboxDiff.toString(), xmlboxDiff.hasDifferences());
					Assert.assertEquals("not an enum", rs.getString("aufzaehlung"));
					Assert.assertEquals("is this a time?", rs.getString("aTime"));
					Assert.assertEquals("abc200\n" +
							"end200", rs.getString("mtextLimited"));
					Assert.assertEquals("111", rs.getString("aStandardid"));
					Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", rs.getString("aAttribute"));
					Assert.assertEquals("one date", rs.getString("aDate"));
					Assert.assertEquals("delete", rs.getString("vertAlignment"));
					Assert.assertEquals("ClassA", rs.getString("nametext"));
					Assert.assertEquals("abc101", rs.getString("textUnlimited"));
					Assert.assertEquals("im not a decimal", rs.getString("numericDec"));
					Assert.assertEquals("abc201\n" +
							"end201", rs.getString("mtextUnlimited"));
					Assert.assertEquals("date and time are nice", rs.getString("aDateTime"));
				}
				{
					Assert.assertTrue(rs.next());
					Assert.assertEquals(null, rs.getString("textLimited"));
					Assert.assertTrue(rs.wasNull());
					Assert.assertEquals("textNull", rs.getString("textUnlimited"));

				}
			}
			{
				// byte array
				String stmtT="SELECT binbox FROM "+setup.prefixName("classattr")+" ORDER BY t_id ASC";
				Assert.assertTrue(stmt.execute(stmtT));
				ResultSet rs=stmt.getResultSet();
				Assert.assertTrue(rs.next());
				String bytes=rs.getString("binbox");
				Assert.assertFalse(rs.wasNull());
				Assert.assertEquals("AAAA", bytes);
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
			if(stmt!=null) {
				stmt.close();
				stmt=null;
			}
			if(jdbcConnection!=null){
				jdbcConnection.close();
				jdbcConnection=null;
			}
		}
	}

	@Test
	public void importXtfLine() throws Exception{
        Connection jdbcConnection=null;
        Statement stmt=null;
		try {
            setup.resetDb();
            jdbcConnection = setup.createConnection();
	        stmt=jdbcConnection.createStatement();
			File data=new File(TEST_OUT+"Datatypes23Line.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			//Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
			// imported polyline
			{
				ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straightsarcs2d) FROM "+setup.prefixName("line2")+" WHERE t_ili_tid = 'Line2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straightsarcs2d) FROM "+setup.prefixName("line2")+" WHERE t_ili_tid = 'Line2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				    if(setup.supportsCompoundGeometry()) {
	                    assertEquals("COMPOUNDCURVE(CIRCULARSTRING(2460001 1045001,2460005 1045004,2460006 1045006),(2460006 1045006,2460010 1045010))", rs.getObject(1));
				    }else {
	                    assertEquals("LINESTRING (2460001 1045001, 2460001.2286122167 1045001.0801162157, 2460001.4546624995 1045001.1671990677, 2460001.6779386075 1045001.2611667924, 2460001.8982309024 1045001.3619311622, 2460002.11533255 1045001.4693975681, 2460002.3290397087 1045001.5834651083, 2460002.539151727 1045001.704026683, 2460002.7454713276 1045001.8309690953, 2460002.9478047937 1045001.9641731572, 2460003.1459621512 1045002.1035138015, 2460003.339757348 1045002.2488601991, 2460003.5290084267 1045002.4000758822, 2460003.7135376967 1045002.5570188722, 2460003.893171901 1045002.7195418131, 2460004.0677423775 1045002.8874921099, 2460004.237085221 1045003.0607120714, 2460004.4010414314 1045003.2390390589, 2460004.5594570693 1045003.4223056388, 2460004.712183395 1045003.6103397394, 2460004.859077012 1045003.8029648125, 2460005 1045004, 2460005.138642302 1045004.2071654596, 2460005.2706842828 1045004.4185989732, 2460005.395994664 1045004.6340903277, 2460005.514448858 1045004.8534252757, 2460005.6259290944 1045005.0763857483, 2460005.730324536 1045005.3027500722, 2460005.8275313913 1045005.5322931898, 2460005.9174530134 1045005.7647868829, 2460006 1045006, 2460010 1045010)",rs.getObject(1));
				    }
				}
			}
			assertImportXtfLine_line3(stmt);
			{
				ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straights2d) FROM "+setup.prefixName("simpleline2")+" WHERE t_ili_tid = 'SimpleLine2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straights2d) FROM "+setup.prefixName("simpleline2")+" WHERE t_ili_tid = 'SimpleLine2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
                    if(setup.supportsCompoundGeometry()) {
                        assertEquals("COMPOUNDCURVE((2460001 1045001,2460010 1045010))", rs.getObject(1));
                    }else {
                        assertEquals("LINESTRING (2460001 1045001, 2460010 1045010)", rs.getObject(1));
                    }
				}
			}
			assertImportXtfLine_simpleline3(stmt);
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
		}
	}

    protected void assertImportXtfLine_simpleline3(Statement stmt) throws SQLException {
        {
        	ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straights3d) FROM "+setup.prefixName("simpleline3")+" WHERE t_ili_tid = 'SimpleLine3.1';");
        	ResultSetMetaData rsmd=rs.getMetaData();
        	assertEquals(1, rsmd.getColumnCount());
        	while(rs.next()){
                String geom=rs.getString(1);
                if(setup.supportsCompoundGeometry()) {
                    assertEquals("COMPOUNDCURVE Z ((2460001 1045001 300,2460010 1045010 300))", geom);
                }else {
                    assertEquals("LINESTRING (2460001 1045001 300, 2460010 1045010 300)", geom);
                }
        	}
        }
    }

    protected void assertImportXtfLine_line3(Statement stmt) throws SQLException {
        {
        	ResultSet rs = stmt.executeQuery("SELECT ST_AsText(straightsarcs3d) FROM "+setup.prefixName("line3")+" WHERE t_ili_tid = 'Line3.1';");
        	ResultSetMetaData rsmd=rs.getMetaData();
        	assertEquals(1, rsmd.getColumnCount());
        	while(rs.next()){
        	    String geom=rs.getString(1);
        	    if(setup.supportsCompoundGeometry()) {
        	        geom=geom.replaceFirst("[0-9\\.#A-Z]*[nN][aA][nN]", "NAN");
                    assertEquals("COMPOUNDCURVE Z (CIRCULARSTRING Z (2460001 1045001 300,2460005 1045004 NAN,2460006 1045006 300),(2460006 1045006 300,2460010 1045010 300))", geom);
        	    }else {
                    assertEquals("LINESTRING (2460001 1045001 300, 2460001.2286122167 1045001.0801162157 0, 2460001.4546624995 1045001.1671990677 0, 2460001.6779386075 1045001.2611667924 0, 2460001.8982309024 1045001.3619311622 0, 2460002.11533255 1045001.4693975681 0, 2460002.3290397087 1045001.5834651083 0, 2460002.539151727 1045001.704026683 0, 2460002.7454713276 1045001.8309690953 0, 2460002.9478047937 1045001.9641731572 0, 2460003.1459621512 1045002.1035138015 0, 2460003.339757348 1045002.2488601991 0, 2460003.5290084267 1045002.4000758822 0, 2460003.7135376967 1045002.5570188722 0, 2460003.893171901 1045002.7195418131 0, 2460004.0677423775 1045002.8874921099 0, 2460004.237085221 1045003.0607120714 0, 2460004.4010414314 1045003.2390390589 0, 2460004.5594570693 1045003.4223056388 0, 2460004.712183395 1045003.6103397394 0, 2460004.859077012 1045003.8029648125 0, 2460005 1045004 0, 2460005.138642302 1045004.2071654596 0, 2460005.2706842828 1045004.4185989732 0, 2460005.395994664 1045004.6340903277 0, 2460005.514448858 1045004.8534252757 0, 2460005.6259290944 1045005.0763857483 0, 2460005.730324536 1045005.3027500722 0, 2460005.8275313913 1045005.5322931898 0, 2460005.9174530134 1045005.7647868829 0, 2460006 1045006 0, 2460010 1045010 300)", geom);
        	    }
        	}
        }
    }
	
	@Test
	public void importXtfSurface() throws Exception
	{
        Connection jdbcConnection=null;
        Statement stmt=null;
		try {
            setup.resetDb();
            jdbcConnection = setup.createConnection();
	        stmt=jdbcConnection.createStatement();
			File data=new File(TEST_OUT+"Datatypes23Surface.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setCreateNumChecks(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			//Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
			// imported surface
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surfacearcs2d) FROM "+setup.prefixName("surface2")+" WHERE t_ili_tid = 'Surface2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surfacearcs2d) FROM "+setup.prefixName("surface2")+" WHERE t_ili_tid = 'Surface2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				    String geom=rs.getString(1);
				    if(setup.supportsCompoundGeometry()) {
	                    assertEquals("CURVEPOLYGON(COMPOUNDCURVE((2460001 1045001,2460020 1045015),CIRCULARSTRING(2460020 1045015,2460010 1045018,2460001 1045015),(2460001 1045015,2460001 1045001)),COMPOUNDCURVE((2460005 1045005,2460010 1045010),CIRCULARSTRING(2460010 1045010,2460007 1045009,2460005 1045010),(2460005 1045010,2460005 1045005)))", geom);//2460001 1045001
				    }else {
	                    assertEquals("POLYGON ((2460001 1045001, 2460020 1045015, 2460019.7082612957 1045015.2006541417, 2460019.412285995 1045015.3950047981, 2460019.112210273 1045015.5829625512, 2460018.808172189 1045015.764440924, 2460018.500311628 1045015.939356421, 2460018.1887702323 1045016.1076285659, 2460017.873691337 1045016.2691799388, 2460017.555219907 1045016.4239362122, 2460017.2335024653 1045016.5718261849, 2460016.908687031 1045016.7127818147, 2460016.580923046 1045016.8467382498, 2460016.2503613112 1045016.9736338586, 2460015.917153913 1045017.0934102583, 2460015.5814541555 1045017.2060123412, 2460015.24341649 1045017.3113883008, 2460014.9031964433 1045017.409489655, 2460014.560950545 1045017.5002712687, 2460014.216836259 1045017.5836913744, 2460013.8710119063 1045017.6597115917, 2460013.523636597 1045017.7282969448, 2460013.1748701534 1045017.7894158785, 2460012.8248730386 1045017.8430402727, 2460012.473806281 1045017.8891454556, 2460012.121831402 1045017.9277102149, 2460011.76911034 1045017.9587168073, 2460011.415805378 1045017.9821509673, 2460011.0620790664 1045017.9980019131, 2460010.70809415 1045018.0062623518, 2460010.3540134914 1045018.0069284829, 2460010 1045018, 2460009.643919222 1045017.9853609515, 2460009.2882374497 1045017.9630383442, 2460008.93312046 1045017.9330425821, 2460008.5787337674 1045017.895387646, 2460008.225242546 1045017.8500910861, 2460007.8728115517 1045017.7971740144, 2460007.521605047 1045017.7366610947, 2460007.171786724 1045017.6685805311, 2460006.8235196276 1045017.5929640549, 2460006.476966079 1045017.5098469096, 2460006.1322876005 1045017.4192678347, 2460005.789644842 1045017.3212690479, 2460005.4491975037 1045017.2158962246, 2460005.111104262 1045017.1031984775, 2460004.7755226977 1045016.9832283331, 2460004.442609219 1045016.8560417076, 2460004.1125189913 1045016.7216978804, 2460003.785405865 1045016.5802594674, 2460003.461422302 1045016.4317923903, 2460003.1407193053 1045016.2763658474, 2460002.82344635 1045016.1140522807, 2460002.509751312 1045015.9449273415, 2460002.1997803985 1045015.7690698565, 2460001.8936780826 1045015.5865617899, 2460001.591587034 1045015.3974882056, 2460001.2936480516 1045015.201937228, 2460001 1045015, 2460001 1045001), (2460005 1045005, 2460010 1045010, 2460009.88144299 1045009.8868162549, 2460009.75776753 1045009.7792490412, 2460009.629239437 1045009.6775295539, 2460009.4961349573 1045009.5818764193, 2460009.3587401723 1045009.4924952249, 2460009.217350386 1045009.4095780788, 2460009.0722694886 1045009.3333031948, 2460008.9238093025 1045009.2638345113, 2460008.772288915 1045009.2013213376, 2460008.618033989 1045009.1458980337, 2460008.461376066 1045009.0976837213, 2460008.3026518514 1045009.0567820276, 2460008.1422024923 1045009.0232808628, 2460007.980372844 1045008.9972522313, 2460007.8175107273 1045008.9787520766, 2460007.653966183 1045008.967820161, 2460007.490090718 1045008.9644799808, 2460007.326236551 1045008.9687387149, 2460007.162755855 1045008.9805872099, 2460007 1045009, 2460006.839709452 1045009.026670705, 2460006.680813669 1045009.0606783001, 2460006.5236482956 1045009.1019509495, 2460006.3685453194 1045009.150401471, 2460006.2158323727 1045009.2059275198, 2460006.0658320384 1045009.2684118057, 2460005.91886117 1045009.3377223398, 2460005.7752302215 1045009.413712714, 2460005.6352425925 1045009.4962224099, 2460005.499193985 1045009.5850771382, 2460005.367371782 1045009.6800892063, 2460005.2400544384 1045009.7810579155, 2460005.117510893 1045009.8877699845, 2460005 1045010, 2460005 1045005))",geom);
				    }
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surface2d) FROM "+setup.prefixName("simplesurface2")+" WHERE t_ili_tid = 'SimpleSurface2.0';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				  	assertEquals(null, rs.getObject(1));
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surface2d) FROM "+setup.prefixName("simplesurface2")+" WHERE t_ili_tid = 'SimpleSurface2.1';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
				    String geom=rs.getString(1);
                    if(setup.supportsCompoundGeometry()) {
                        assertEquals("CURVEPOLYGON(COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", geom);
                    }else {
                        assertEquals("POLYGON ((2460005 1045005, 2460010 1045010, 2460005 1045010, 2460005 1045005))", geom);
                    }
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surface2d) FROM "+setup.prefixName("simplesurface2")+" WHERE t_ili_tid = 'SimpleSurface2.2';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
                    String geom=rs.getString(1);
                    if(setup.supportsCompoundGeometry()) {
                        assertEquals("CURVEPOLYGON(COMPOUNDCURVE((2460001 1045001,2460020 1045015,2460001 1045015,2460001 1045001)),COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", geom);
                    }else {
                        assertEquals("POLYGON ((2460001 1045001, 2460020 1045015, 2460001 1045015, 2460001 1045001), (2460005 1045005, 2460010 1045010, 2460005 1045010, 2460005 1045005))", geom);
                    }
				}
			}
			{
				ResultSet rs = stmt.executeQuery("SELECT st_astext(surface2d) FROM "+setup.prefixName("simplesurface2")+" WHERE t_ili_tid = 'SimpleSurface2.3';");
				ResultSetMetaData rsmd=rs.getMetaData();
				assertEquals(1, rsmd.getColumnCount());
				while(rs.next()){
                    String geom=rs.getString(1);
                    if(setup.supportsCompoundGeometry()) {
                        assertEquals("CURVEPOLYGON(COMPOUNDCURVE((2460005 1045005,2460010 1045010,2460005 1045010,2460005 1045005)))", geom);
                    }else {
                        assertEquals("POLYGON ((2460005 1045005, 2460010 1045010, 2460005 1045010, 2460005 1045005))", geom);
                    }
				}
			}
		}catch(SQLException e) {
			throw new IoxException(e);
		}finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
		}
	}
    @Test
    public void importXtfSurface_asLines() throws Exception
    {
        Connection jdbcConnection=null;
        Statement stmt=null;
        try {
            setup.resetDb();
            jdbcConnection = setup.createConnection();
            stmt=jdbcConnection.createStatement();
            File data=new File(TEST_OUT+"Datatypes23Surface_asLines.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
            config.setFunction(Config.FC_IMPORT);
            config.setDoImplicitSchemaImport(true);
            config.setValidation(false);
            config.setCreateFk(Config.CREATE_FK_YES);
            config.setTidHandling(Config.TID_HANDLING_PROPERTY);
            config.setImportTid(true);
            Ili2db.setSkipPolygonBuilding(config);
            //Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // imported surface
            {
                ResultSet rs = stmt.executeQuery("SELECT st_astext(surface2d) FROM "+setup.prefixName("simplesurface2")+" WHERE t_ili_tid = 'SimpleSurface2.1';");
                assertTrue(rs.next());
                String geom=rs.getString(1);
                if(setup.supportsCompoundGeometry()) {
                    assertEquals("MULTICURVE(COMPOUNDCURVE((2460005 1045005,2460010 1045005,2460010 1045010,2460005 1045010,2460010 1045010)))", geom);
                }else {
                    assertEquals("MULTILINESTRING ((2460005 1045005, 2460010 1045005, 2460010 1045010, 2460005 1045010, 2460010 1045010))", geom);
                }
            }
        }catch(SQLException e) {
            throw new IoxException(e);
        }finally{
            if(stmt!=null) {
                stmt.close();
                stmt=null;
            }
            if(jdbcConnection!=null){
                jdbcConnection.close();
                jdbcConnection=null;
            }
        }
    }
	
	@Test
	public void exportXtfAttr() throws Exception
	{
		{
		    importXtfAttr();
		}
		try{
            {
                Connection jdbcConnection = setup.createConnection();
                Statement stmt=jdbcConnection.createStatement();
                String stmtTxt="UPDATE "+setup.prefixName("classattr")+" SET textLimited = '' WHERE textUnlimited='textNull';";
                stmt.execute(stmtTxt);
                jdbcConnection.close();
                jdbcConnection=null;
            }
			
			File data=new File(TEST_OUT+"Datatypes23Attr-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setModels("Datatypes23");
			config.setExportTid(true);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
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
	                 IomObject obj1 = objs.get("ClassAttr.1");
	                 Assert.assertNotNull(obj1);
	                 Assert.assertEquals("Datatypes23.Topic.ClassAttr", obj1.getobjecttag());
	                 // datatypes23
	                 Assert.assertEquals("22", obj1.getattrvalue("aI32id"));
	                 Assert.assertEquals("true", obj1.getattrvalue("aBoolean"));
	                 Assert.assertEquals("15b6bcce-8772-4595-bf82-f727a665fbf3", obj1.getattrvalue("aUuid"));
	                 Assert.assertEquals("abc100\"\"''", obj1.getattrvalue("textLimited"));
	                 Assert.assertEquals("Left", obj1.getattrvalue("horizAlignment"));
	                 Assert.assertEquals("mailto:ceis@localhost", obj1.getattrvalue("uritext"));
	                 Assert.assertEquals("5", obj1.getattrvalue("numericInt"));
                     Assert.assertEquals("9223372036854775800", obj1.getattrvalue("numericBigInt"));
	                 
	                 // do xml comparison
	                 String expectedXmlValue=EXPECTED_XMLBOX;
	                 String actualXmlValue=obj1.getattrvalue("xmlbox");
	                 {
	                     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	                     DocumentBuilder builder = dbf.newDocumentBuilder();
	                     Document actualDoc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(actualXmlValue)));    
	                     Document expectedDoc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(expectedXmlValue)));    
	                     Diff diff = DiffBuilder
	                     .compare(expectedDoc)
	                     .withTest(actualDoc)
	                     .ignoreComments()
	                     .ignoreWhitespace()
	                     .checkForSimilar()
	                     .build();
	                     //System.out.println(diff.toString());
	                     Assert.assertFalse(diff.hasDifferences());
	                 }
	                 
	                 Assert.assertEquals("mehr.vier", obj1.getattrvalue("aufzaehlung"));
	                 Assert.assertEquals("09:00:00.000", obj1.getattrvalue("aTime"));
	                 Assert.assertEquals("abc200\n" + 
	                        "end200", obj1.getattrvalue("mtextLimited"));
	                 Assert.assertEquals("AAAA", obj1.getattrvalue("binbox"));
	                 Assert.assertEquals("chgAAAAAAAAA0azD", obj1.getattrvalue("aStandardid"));
	                 Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", obj1.getattrvalue("aAttribute"));
	                 Assert.assertEquals("2002-09-24", obj1.getattrvalue("aDate"));
	                 Assert.assertEquals("Top", obj1.getattrvalue("vertAlignment"));
	                 Assert.assertEquals("ClassA", obj1.getattrvalue("nametext"));
	                 Assert.assertEquals("abc101", obj1.getattrvalue("textUnlimited"));
	                 Assert.assertEquals("6.0", obj1.getattrvalue("numericDec"));
	                 Assert.assertEquals("abc201\n" + 
	                        "end201", obj1.getattrvalue("mtextUnlimited"));
	                 Assert.assertEquals("1900-01-01T12:30:05.000", obj1.getattrvalue("aDateTime"));
	                 Assert.assertEquals("DM01AVCH24D.FixpunkteKategorie1.LFP1", obj1.getattrvalue("aClass"));
				     
				 }
				 {
	                 IomObject obj1 = objs.get("ClassAttr.2");
	                 Assert.assertNotNull(obj1);
	                 Assert.assertEquals("Datatypes23.Topic.ClassAttr", obj1.getobjecttag());
                     Assert.assertEquals(0, obj1.getattrvaluecount("textLimited"));
				 }
			 }
		}finally{
		}
	}

	@Test
	public void exportXtfAttr_invalidValuesAsText() throws Exception
	{
		{
			importXtfAttr_asText();
		}
		try{
			{
				Connection jdbcConnection = setup.createConnection();
				Statement stmt=jdbcConnection.createStatement();
				String stmtTxt="UPDATE "+setup.prefixName("classattr")+" SET textLimited = '' WHERE textUnlimited='textNull';";
				stmt.execute(stmtTxt);
				jdbcConnection.close();
				jdbcConnection=null;
			}

			File data=new File(TEST_OUT+"Datatypes23Attr_invalidSimpleTypes-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setModels("Datatypes23");
			config.setExportTid(true);
			config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
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
					IomObject obj1 = objs.get("ClassAttr.1");
					Assert.assertNotNull(obj1);
					Assert.assertEquals("Datatypes23.Topic.ClassAttr", obj1.getobjecttag());
					// datatypes23
					Assert.assertEquals("22", obj1.getattrvalue("aI32id"));
					Assert.assertEquals("falschhhhhhh", obj1.getattrvalue("aBoolean"));
					Assert.assertEquals("23A", obj1.getattrvalue("aUuid"));
					Assert.assertEquals("1111111111111111111111111111111111111111", obj1.getattrvalue("textLimited"));
					Assert.assertEquals("42", obj1.getattrvalue("horizAlignment"));
					Assert.assertEquals("mailto:ceis@localhost", obj1.getattrvalue("uritext"));
					Assert.assertEquals("im not an int", obj1.getattrvalue("numericInt"));
					Assert.assertEquals("im not a BIG int", obj1.getattrvalue("numericBigInt"));

					// do xml comparison
					String expectedXmlValue=EXPECTED_XMLBOX;
					String actualXmlValue=obj1.getattrvalue("xmlbox");
					{
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document actualDoc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(actualXmlValue)));
						Document expectedDoc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(expectedXmlValue)));
						Diff diff = DiffBuilder
								.compare(expectedDoc)
								.withTest(actualDoc)
								.ignoreComments()
								.ignoreWhitespace()
								.checkForSimilar()
								.build();
						//System.out.println(diff.toString());
						Assert.assertFalse(diff.hasDifferences());
					}

					Assert.assertEquals("not an enum", obj1.getattrvalue("aufzaehlung"));
					Assert.assertEquals("is this a time?", obj1.getattrvalue("aTime"));
					Assert.assertEquals("abc200\n" +
							"end200", obj1.getattrvalue("mtextLimited"));
					Assert.assertEquals("AAAA", obj1.getattrvalue("binbox"));
					Assert.assertEquals("111", obj1.getattrvalue("aStandardid"));
					Assert.assertEquals("Grunddatensatz.Fixpunkte.LFP.Nummer", obj1.getattrvalue("aAttribute"));
					Assert.assertEquals("one date", obj1.getattrvalue("aDate"));
					Assert.assertEquals("delete", obj1.getattrvalue("vertAlignment"));
					Assert.assertEquals("ClassA", obj1.getattrvalue("nametext"));
					Assert.assertEquals("abc101", obj1.getattrvalue("textUnlimited"));
					Assert.assertEquals("im not a decimal", obj1.getattrvalue("numericDec"));
					Assert.assertEquals("abc201\n" +
							"end201", obj1.getattrvalue("mtextUnlimited"));
					Assert.assertEquals("date and time are nice", obj1.getattrvalue("aDateTime"));
					Assert.assertEquals("DM01AVCH24D.FixpunkteKategorie1.LFP1", obj1.getattrvalue("aClass"));

				}
				{
					IomObject obj1 = objs.get("ClassAttr.2");
					Assert.assertNotNull(obj1);
					Assert.assertEquals("Datatypes23.Topic.ClassAttr", obj1.getobjecttag());
					Assert.assertEquals(0, obj1.getattrvaluecount("textLimited"));
				}
				{
                    IomObject obj1 = objs.get("ClassKoord2.1");
                    Assert.assertNotNull(obj1);
                    Assert.assertEquals("Datatypes23.Topic.ClassKoord2", obj1.getobjecttag());
                    Assert.assertEquals(1, obj1.getattrvaluecount("lcoord"));
				    
				}
			}
		}finally{
		}
	}

	@Test
	public void exportXtfLine() throws Exception {		
	    {
	        importXtfLine();
	    }
		try{
			
			File data=new File(TEST_OUT+"Datatypes23Line-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			config.setModels("Datatypes23");
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
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
                 IomObject obj1 = objs.get("SimpleLine2.0");
                 Assert.assertNotNull(obj1);
                 Assert.assertEquals("Datatypes23.Topic.SimpleLine2", obj1.getobjecttag());
                 Assert.assertEquals(0, obj1.getattrvaluecount("straights2d"));
              }
             {
                 IomObject obj1 = objs.get("SimpleLine2.1");
                 Assert.assertNotNull(obj1);
                 Assert.assertEquals("Datatypes23.Topic.SimpleLine2", obj1.getobjecttag());
                 IomObject lineSegment=obj1.getattrobj("straights2d",0);
                 Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460010.000, C2 1045010.000}]}}", lineSegment.toString());
              }
			 {
				IomObject obj1 = objs.get("Line3.1");
				Assert.assertNotNull(obj1);
				Assert.assertEquals("Datatypes23.Topic.Line3", obj1.getobjecttag());
				IomObject lineSegment=obj1.getattrobj("straightsarcs3d",0);
				if(setup.supportsCompoundGeometry()) {
	                Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000, C3 300.000}, ARC {A1 2460005.000, A2 1045004.000, C1 2460006.000, C2 1045006.000, C3 300.000}, COORD {C1 2460010.000, C2 1045010.000, C3 300.000}]}}", lineSegment.toString());
				}else {
	                Assert.assertEquals("POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000, C3 300.000}, COORD {C1 2460001.229, C2 1045001.080, C3 0.000}, COORD {C1 2460001.455, C2 1045001.167, C3 0.000}, COORD {C1 2460001.678, C2 1045001.261, C3 0.000}, COORD {C1 2460001.898, C2 1045001.362, C3 0.000}, COORD {C1 2460002.115, C2 1045001.469, C3 0.000}, COORD {C1 2460002.329, C2 1045001.583, C3 0.000}, COORD {C1 2460002.539, C2 1045001.704, C3 0.000}, COORD {C1 2460002.745, C2 1045001.831, C3 0.000}, COORD {C1 2460002.948, C2 1045001.964, C3 0.000}, COORD {C1 2460003.146, C2 1045002.104, C3 0.000}, COORD {C1 2460003.340, C2 1045002.249, C3 0.000}, COORD {C1 2460003.529, C2 1045002.400, C3 0.000}, COORD {C1 2460003.714, C2 1045002.557, C3 0.000}, COORD {C1 2460003.893, C2 1045002.720, C3 0.000}, COORD {C1 2460004.068, C2 1045002.887, C3 0.000}, COORD {C1 2460004.237, C2 1045003.061, C3 0.000}, COORD {C1 2460004.401, C2 1045003.239, C3 0.000}, COORD {C1 2460004.559, C2 1045003.422, C3 0.000}, COORD {C1 2460004.712, C2 1045003.610, C3 0.000}, COORD {C1 2460004.859, C2 1045003.803, C3 0.000}, COORD {C1 2460005.000, C2 1045004.000, C3 0.000}, COORD {C1 2460005.139, C2 1045004.207, C3 0.000}, COORD {C1 2460005.271, C2 1045004.419, C3 0.000}, COORD {C1 2460005.396, C2 1045004.634, C3 0.000}, COORD {C1 2460005.514, C2 1045004.853, C3 0.000}, COORD {C1 2460005.626, C2 1045005.076, C3 0.000}, COORD {C1 2460005.730, C2 1045005.303, C3 0.000}, COORD {C1 2460005.828, C2 1045005.532, C3 0.000}, COORD {C1 2460005.917, C2 1045005.765, C3 0.000}, COORD {C1 2460006.000, C2 1045006.000, C3 0.000}, COORD {C1 2460010.000, C2 1045010.000, C3 300.000}]}}", lineSegment.toString());
				}
			 }
		}finally{
		}
	}
	
	@Test
	public void exportXtfSurface() throws Exception {
	    {
	        importXtfSurface();
	    }
		try{
			
			File data=new File(TEST_OUT+"Datatypes23Surface-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			config.setModels("Datatypes23");
			config.setValidation(false);
			Ili2db.readSettingsFromDb(config);
			try{
				Ili2db.run(config,null);
			}catch(Exception ex){
				EhiLogger.logError(ex);
				Assert.fail();
			}
			// tests
			// read objects of db and write objectValue to HashMap
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
                 IomObject obj1 = objs.get("SimpleSurface2.0");
                 Assert.assertNotNull(obj1);
                 Assert.assertEquals("Datatypes23.Topic.SimpleSurface2", obj1.getobjecttag());
                 Assert.assertEquals(0, obj1.getattrvaluecount("surface2d"));
              }
             {
                 IomObject obj1 = objs.get("SimpleSurface2.1");
                 Assert.assertNotNull(obj1);
                 Assert.assertEquals("Datatypes23.Topic.SimpleSurface2", obj1.getobjecttag());
                 IomObject surface = obj1.getattrobj("surface2d", 0);
                 Assert.assertEquals("MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}}}", surface.toString());
              }
			 {
				IomObject obj1 = objs.get("Surface2.1");
				Assert.assertNotNull(obj1);
				Assert.assertEquals("Datatypes23.Topic.Surface2", obj1.getobjecttag());
				IomObject surface = obj1.getattrobj("surfacearcs2d", 0);
				if(setup.supportsCompoundGeometry()) {
	                Assert.assertEquals("MULTISURFACE {surface SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460020.000, C2 1045015.000}, ARC {A1 2460010.000, A2 1045018.000, C1 2460001.000, C2 1045015.000}, COORD {C1 2460001.000, C2 1045001.000}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, ARC {A1 2460007.000, A2 1045009.000, C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}]}}", surface.toString());
				}else {
	                Assert.assertEquals("MULTISURFACE {surface SURFACE {boundary [BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460001.000, C2 1045001.000}, COORD {C1 2460020.000, C2 1045015.000}, COORD {C1 2460019.708, C2 1045015.201}, COORD {C1 2460019.412, C2 1045015.395}, COORD {C1 2460019.112, C2 1045015.583}, COORD {C1 2460018.808, C2 1045015.764}, COORD {C1 2460018.500, C2 1045015.939}, COORD {C1 2460018.189, C2 1045016.108}, COORD {C1 2460017.874, C2 1045016.269}, COORD {C1 2460017.555, C2 1045016.424}, COORD {C1 2460017.234, C2 1045016.572}, COORD {C1 2460016.909, C2 1045016.713}, COORD {C1 2460016.581, C2 1045016.847}, COORD {C1 2460016.250, C2 1045016.974}, COORD {C1 2460015.917, C2 1045017.093}, COORD {C1 2460015.581, C2 1045017.206}, COORD {C1 2460015.243, C2 1045017.311}, COORD {C1 2460014.903, C2 1045017.409}, COORD {C1 2460014.561, C2 1045017.500}, COORD {C1 2460014.217, C2 1045017.584}, COORD {C1 2460013.871, C2 1045017.660}, COORD {C1 2460013.524, C2 1045017.728}, COORD {C1 2460013.175, C2 1045017.789}, COORD {C1 2460012.825, C2 1045017.843}, COORD {C1 2460012.474, C2 1045017.889}, COORD {C1 2460012.122, C2 1045017.928}, COORD {C1 2460011.769, C2 1045017.959}, COORD {C1 2460011.416, C2 1045017.982}, COORD {C1 2460011.062, C2 1045017.998}, COORD {C1 2460010.708, C2 1045018.006}, COORD {C1 2460010.354, C2 1045018.007}, COORD {C1 2460010.000, C2 1045018.000}, COORD {C1 2460009.644, C2 1045017.985}, COORD {C1 2460009.288, C2 1045017.963}, COORD {C1 2460008.933, C2 1045017.933}, COORD {C1 2460008.579, C2 1045017.895}, COORD {C1 2460008.225, C2 1045017.850}, COORD {C1 2460007.873, C2 1045017.797}, COORD {C1 2460007.522, C2 1045017.737}, COORD {C1 2460007.172, C2 1045017.669}, COORD {C1 2460006.824, C2 1045017.593}, COORD {C1 2460006.477, C2 1045017.510}, COORD {C1 2460006.132, C2 1045017.419}, COORD {C1 2460005.790, C2 1045017.321}, COORD {C1 2460005.449, C2 1045017.216}, COORD {C1 2460005.111, C2 1045017.103}, COORD {C1 2460004.776, C2 1045016.983}, COORD {C1 2460004.443, C2 1045016.856}, COORD {C1 2460004.113, C2 1045016.722}, COORD {C1 2460003.785, C2 1045016.580}, COORD {C1 2460003.461, C2 1045016.432}, COORD {C1 2460003.141, C2 1045016.276}, COORD {C1 2460002.823, C2 1045016.114}, COORD {C1 2460002.510, C2 1045015.945}, COORD {C1 2460002.200, C2 1045015.769}, COORD {C1 2460001.894, C2 1045015.587}, COORD {C1 2460001.592, C2 1045015.397}, COORD {C1 2460001.294, C2 1045015.202}, COORD {C1 2460001.000, C2 1045015.000}, COORD {C1 2460001.000, C2 1045001.000}]}}}, BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460009.881, C2 1045009.887}, COORD {C1 2460009.758, C2 1045009.779}, COORD {C1 2460009.629, C2 1045009.678}, COORD {C1 2460009.496, C2 1045009.582}, COORD {C1 2460009.359, C2 1045009.492}, COORD {C1 2460009.217, C2 1045009.410}, COORD {C1 2460009.072, C2 1045009.333}, COORD {C1 2460008.924, C2 1045009.264}, COORD {C1 2460008.772, C2 1045009.201}, COORD {C1 2460008.618, C2 1045009.146}, COORD {C1 2460008.461, C2 1045009.098}, COORD {C1 2460008.303, C2 1045009.057}, COORD {C1 2460008.142, C2 1045009.023}, COORD {C1 2460007.980, C2 1045008.997}, COORD {C1 2460007.818, C2 1045008.979}, COORD {C1 2460007.654, C2 1045008.968}, COORD {C1 2460007.490, C2 1045008.964}, COORD {C1 2460007.326, C2 1045008.969}, COORD {C1 2460007.163, C2 1045008.981}, COORD {C1 2460007.000, C2 1045009.000}, COORD {C1 2460006.840, C2 1045009.027}, COORD {C1 2460006.681, C2 1045009.061}, COORD {C1 2460006.524, C2 1045009.102}, COORD {C1 2460006.369, C2 1045009.150}, COORD {C1 2460006.216, C2 1045009.206}, COORD {C1 2460006.066, C2 1045009.268}, COORD {C1 2460005.919, C2 1045009.338}, COORD {C1 2460005.775, C2 1045009.414}, COORD {C1 2460005.635, C2 1045009.496}, COORD {C1 2460005.499, C2 1045009.585}, COORD {C1 2460005.367, C2 1045009.680}, COORD {C1 2460005.240, C2 1045009.781}, COORD {C1 2460005.118, C2 1045009.888}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045005.000}]}}}]}}", surface.toString());
				}
			 }
		}finally{
		}
	}
    @Test
    public void exportXtfSurface_asLines() throws Exception {
        {
            importXtfSurface_asLines();
        }
        try{
            
            File data=new File(TEST_OUT+"Datatypes23Surface_asLines-out.xtf");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setExportTid(true);
            config.setModels("Datatypes23");
            config.setValidation(false);
            Ili2db.readSettingsFromDb(config);
            try{
                Ili2db.run(config,null);
            }catch(Exception ex){
                EhiLogger.logError(ex);
                Assert.fail();
            }
            // tests
            // read objects of db and write objectValue to HashMap
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
                 IomObject obj1 = objs.get("SimpleSurface2.1");
                 Assert.assertNotNull(obj1);
                 Assert.assertEquals("Datatypes23.Topic.SimpleSurface2", obj1.getobjecttag());
                 IomObject surface = obj1.getattrobj("surface2d", 0);
                 Assert.assertEquals("MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 2460005.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045005.000}, COORD {C1 2460010.000, C2 1045010.000}, COORD {C1 2460005.000, C2 1045010.000}, COORD {C1 2460010.000, C2 1045010.000}]}}}}}", surface.toString());
              }
        }finally{
        }
    }
}
