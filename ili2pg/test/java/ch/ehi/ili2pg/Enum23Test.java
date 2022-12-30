package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
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
public class Enum23Test extends ch.ehi.ili2db.Enum23Test{
	private static final String DBSCHEMA = "Enum23";
    String dburl;
    String dbuser;
    String dbpwd; 
	Connection jdbcConnection=null;
	Statement stmt=null;

    @Override
    protected AbstractTestSetup createTestSetup() {
        dburl=System.getProperty("dburl"); 
        dbuser=System.getProperty("dbusr");
        dbpwd=System.getProperty("dbpwd"); 
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    }

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
	public void importIliWithoutBeautify() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2_ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele_2",rs.getString(1));
				}
	        }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}	
	}

	@Test
	public void importIliWithBeautify() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
		
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2 ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele 2",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test2_ele'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2 ele",rs.getString(1));
				}
				{
					String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test3.ele_2'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test3.ele 2",rs.getString(1));
				}
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"boolean")));
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"classa1_attr2")));
				Assert.assertFalse(DbUtility.tableExists(jdbcConnection, new DbTableName(DBSCHEMA,"classa1_attr4")));
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}		
	}
    @Test
    public void importIliWithTxtCol() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
                config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1 WHERE ilicode ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("testelevier",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_attr3 WHERE ilicode ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Attr3_elevier",rs.getString(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importIliWithTxtCol_fr() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
                config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
                config.setNameLanguage("fr");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1_fr WHERE ilicode ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele fr",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1_fr WHERE ilicode ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3 fr.ele 2 fr",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".enum1_fr WHERE ilicode ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("testelevier_fr",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_fr_attr3_fr WHERE ilicode ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele fr",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_fr_attr3_fr WHERE ilicode ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3 fr.ele 2 fr",rs.getString(1));
                }
                if(true){
                    String stmtTxt="SELECT dispName FROM "+DBSCHEMA+".classa1_fr_attr3_fr WHERE ilicode ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Attr3_elevier_fr",rs.getString(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importIliWithTxtColwoEnumTab() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
                config.setCreateEnumDefs(null);
                config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importIliWithTxtColwoEnumTab_fr() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumCols(Config.CREATE_ENUM_TXT_COL);
                config.setCreateEnumDefs(null);
                config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
                config.setNameLanguage("fr");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfWithTxtCol() throws Exception
    {
        {
            importIliWithTxtCol();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            {
                File data=new File("test/data/Enum23/Enum23a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("testelevier",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Attr3_elevier",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_txt FROM "+DBSCHEMA+".classa1 WHERE attr4 is null";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(null,rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_txt FROM "+DBSCHEMA+".classa1 WHERE attr4=true";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("true",rs.getString(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfWithTxtColwoEnumTab() throws Exception
    {
        {
            importIliWithTxtColwoEnumTab();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            {
                File data=new File("test/data/Enum23/Enum23a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_txt FROM "+DBSCHEMA+".classa1 WHERE attr2 ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("testelevier",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3.ele 2",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_txt FROM "+DBSCHEMA+".classa1 WHERE attr3 ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Attr3_elevier",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_txt FROM "+DBSCHEMA+".classa1 WHERE attr4 is null";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(null,rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_txt FROM "+DBSCHEMA+".classa1 WHERE attr4=true";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("true",rs.getString(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfWithTxtColwoEnumTab_fr() throws Exception
    {
        {
            importIliWithTxtColwoEnumTab_fr();
        }
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            {
                File data=new File("test/data/Enum23/Enum23a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_IMPORT);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
        
                {
                    String stmtTxt="SELECT attr2_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr2_fr ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr2_fr ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3 fr.ele 2 fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr2_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr2_fr ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("testelevier_fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr3_fr ='Test2_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2 ele fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr3_fr ='Test3.ele_2'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test3 fr.ele 2 fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr3_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr3_fr ='Test4_ele'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Attr3_elevier_fr",rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr4_fr is null";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(null,rs.getString(1));
                }
                {
                    String stmtTxt="SELECT attr4_fr_txt FROM "+DBSCHEMA+".classa1_fr WHERE attr4_fr=true";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("true",rs.getString(1));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
	
	@Test
	public void importIliExtendedMultiTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23c.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".enum1";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(4,rs.getInt(1));
                }
                {
                    String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".enum1b";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(5,rs.getInt(1));
                }
                {
                    String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".enum1c";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(5,rs.getInt(1));
                }
                {
                    String stmtTxt="SELECT count(*) FROM "+DBSCHEMA+".enum1ccc";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals(6,rs.getInt(1));
                }
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}		
	}
    @Test
    public void importIliExtendedFkTableInheritance0() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_BASE_COL+" IS NULL AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs = null;
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                    stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1b' AND "+DbNames.ENUM_TAB_BASE_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                }
                {
                    HashMap<String,String> enums=new HashMap<String,String>();
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL
                            + " FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"='"+DbExtMetaInfo.TAG_COL_ENUMDOMAIN+"'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    int rc=0;
                    while(rs.next()) {
                        rc++;
                        String aclass=rs.getString(1);
                        String domain=rs.getString(2);
                        enums.put(aclass,domain);
                    }
                    Assert.assertEquals(5,rc);
                    Assert.assertEquals("Enum23c.Enum1ccc",enums.get("classa1ccc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1cc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1c"));
                    Assert.assertEquals("Enum23c.Enum1b",enums.get("classa1b"));
                    Assert.assertEquals("Enum23c.Enum1",enums.get("classa1a"));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importIliExtendedFkTableInheritance1() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_BASE_COL+" IS NULL AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs = null;
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                    stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1b' AND "+DbNames.ENUM_TAB_BASE_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                }
                {
                    HashMap<String,String> enums=new HashMap<String,String>();
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL
                            + " FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"='"+DbExtMetaInfo.TAG_COL_ENUMDOMAIN+"'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    int rc=0;
                    while(rs.next()) {
                        rc++;
                        String aclass=rs.getString(1);
                        String domain=rs.getString(2);
                        enums.put(aclass,domain);
                    }
                    Assert.assertEquals(5,rc);
                    Assert.assertEquals("Enum23c.Enum1ccc",enums.get("classa1ccc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1cc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1c"));
                    Assert.assertEquals("Enum23c.Enum1b",enums.get("classa1b"));
                    Assert.assertEquals("Enum23c.Enum1",enums.get("classa1a"));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importIliExtendedFkTableInheritance2() throws Exception
    {
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_BASE_COL+" IS NULL AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    ResultSet rs = null;
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                    stmtTxt="SELECT * "
                            + " FROM "+DBSCHEMA+".enum1 WHERE "+DbNames.ENUM_TAB_THIS_COL+"='Enum23c.Enum1b' AND "+DbNames.ENUM_TAB_BASE_COL+"='Enum23c.Enum1' AND "+DbNames.ENUM_TAB_ILICODE_COL+"='Test1'";
                    Assert.assertTrue(stmt.execute(stmtTxt));
                    rs=stmt.getResultSet();
                    Assert.assertTrue(rs.next());
                    Assert.assertFalse(rs.next());
                    Assert.assertFalse(stmt.getMoreResults());
                }
                {
                    HashMap<String,String> enums=new HashMap<String,String>();
                    String stmtTxt="SELECT "+DbNames.META_INFO_COLUMN_TAB_SUBTYPE_COL+","+DbNames.META_INFO_COLUMN_TAB_SETTING_COL
                            + " FROM "+DBSCHEMA+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_COLUMN_TAB_TAG_COL+"='"+DbExtMetaInfo.TAG_COL_ENUMDOMAIN+"'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    int rc=0;
                    while(rs.next()) {
                        rc++;
                        String aclass=rs.getString(1);
                        String domain=rs.getString(2);
                        enums.put(aclass,domain);
                    }
                    Assert.assertEquals(5,rc);
                    Assert.assertEquals("Enum23c.Enum1ccc",enums.get("classa1ccc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1cc"));
                    Assert.assertEquals("Enum23c.Enum1c",enums.get("classa1c"));
                    Assert.assertEquals("Enum23c.Enum1b",enums.get("classa1b"));
                    Assert.assertEquals("Enum23c.Enum1",enums.get("classa1a"));
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfExtendedFkTableInheritance0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1a'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1b'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2bA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1c'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1cc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1ccc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA.Test2cAA", rs.getString(1));
                    rs.close();
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfExtendedFkTableInheritance1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1a'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1b'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2bA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1c'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1cc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1 left join "+DBSCHEMA+".enum1 on classa1.attr1=enum1.t_id where t_ili_tid='1ccc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA.Test2cAA", rs.getString(1));
                    rs.close();
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void importXtfExtendedFkTableInheritance2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File("test/data/Enum23/Enum23c.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setTidHandling(Config.TID_HANDLING_PROPERTY);
                config.setImportTid(true);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI_WITH_ID);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1a left join "+DBSCHEMA+".enum1 on classa1a.attr1=enum1.t_id where t_ili_tid='1a'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1b left join "+DBSCHEMA+".enum1 on classa1b.attr1=enum1.t_id where t_ili_tid='1b'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2bA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1c left join "+DBSCHEMA+".enum1 on classa1c.attr1=enum1.t_id where t_ili_tid='1c'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1cc left join "+DBSCHEMA+".enum1 on classa1cc.attr1=enum1.t_id where t_ili_tid='1cc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA", rs.getString(1));
                    rs.close();
                }
                {
                    String stmtTxt="select iliCode from "+DBSCHEMA+".classa1ccc left join "+DBSCHEMA+".enum1 on classa1ccc.attr1=enum1.t_id where t_ili_tid='1ccc'";
                    ResultSet rs=stmt.executeQuery(stmtTxt);
                    Assert.assertTrue(rs.next());
                    Assert.assertEquals("Test2.Test2cA.Test2cAA", rs.getString(1));
                    rs.close();
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void exportXtfExtendedFkTableInheritance0() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtfExtendedFkTableInheritance0();
        
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            File data=new File("test/data/Enum23/Enum23c-inh0-out.xtf");
            {
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Enum23c");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
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
                     IomObject obj0 = objs.get("1a");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1a", obj0.getobjecttag());
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1b");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1b", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2bA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1c");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1c", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1cc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1cc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1ccc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1ccc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA.Test2cAA", obj0.getattrvalue("attr1"));
                 }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void exportXtfExtendedFkTableInheritance1() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtfExtendedFkTableInheritance1();
        
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            File data=new File("test/data/Enum23/Enum23c-inh1-out.xtf");
            {
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Enum23c");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
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
                     IomObject obj0 = objs.get("1a");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1a", obj0.getobjecttag());
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1b");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1b", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2bA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1c");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1c", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1cc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1cc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1ccc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1ccc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA.Test2cAA", obj0.getattrvalue("attr1"));
                 }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
    @Test
    public void exportXtfExtendedFkTableInheritance2() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        importXtfExtendedFkTableInheritance2();
        
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            stmt=jdbcConnection.createStatement();          
            File data=new File("test/data/Enum23/Enum23c-inh2-out.xtf");
            {
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                config.setFunction(Config.FC_EXPORT);
                config.setExportTid(true);
                config.setModels("Enum23c");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
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
                     IomObject obj0 = objs.get("1a");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1a", obj0.getobjecttag());
                     Assert.assertEquals("Test2", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1b");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1b", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2bA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1c");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1c", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1cc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1cc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA", obj0.getattrvalue("attr1"));
                 }
                 {
                     IomObject obj0 = objs.get("1ccc");
                     Assert.assertNotNull(obj0);
                     Assert.assertEquals("Enum23c.TestA.ClassA1ccc", obj0.getobjecttag());
                     Assert.assertEquals("Test2.Test2cA.Test2cAA", obj0.getattrvalue("attr1"));
                 }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }       
    }
	
	@Test
	public void importIliSingleTable() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT "+DbNames.ENUM_TAB_DISPNAME_COL+" FROM "+DBSCHEMA+"."+DbNames.ENUM_TAB+" WHERE "+DbNames.ENUM_TAB_ILICODE_COL+" ='Test2_ele' AND "+DbNames.ENUM_TAB_THIS_COL+"='Enum23.Enum1'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Test2_ele",rs.getString(1));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}
