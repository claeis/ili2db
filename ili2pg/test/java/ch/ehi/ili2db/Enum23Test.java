package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.repository.DbTableName;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Enum23Test {
	private static final String DBSCHEMA = "Enum23";
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
	public void importWithoutBeautify() throws Exception
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
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
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
	public void importWithBeautify() throws Exception
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
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setBeautifyEnumDispName(Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
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
	public void importExtended() throws Exception
	{
		Connection jdbcConnection=null;
		try{
		    Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(
	        		dburl, dbuser, dbpwd);
	        stmt=jdbcConnection.createStatement();			
			stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
			{
				File data=new File("test/data/Enum23/Enum23b.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_MULTI);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
					String stmtTxt="SELECT t_ili2db_attrname.iliname, t_ili2db_attrname.sqlname, t_ili2db_attrname.owner, t_ili2db_attrname.target FROM "+DBSCHEMA+".t_ili2db_attrname;";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertEquals("Enum23b.TestA.ClassA1.attr1",rs.getString(1));
					Assert.assertEquals("attr1",rs.getString(2));
					Assert.assertEquals("classa1",rs.getString(3));
				}
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}		
	}
	
	@Test
	public void importSingleTable() throws Exception
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
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(config.CREATE_FK_YES);
				config.setTidHandling(Config.TID_HANDLING_PROPERTY);
				config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
				config.setCreateEnumDefs(Config.CREATE_ENUM_DEFS_SINGLE);
				config.setCatalogueRefTrafo(null);
				config.setMultiSurfaceTrafo(null);
				config.setMultilingualTrafo(null);
				config.setInheritanceTrafo(null);
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