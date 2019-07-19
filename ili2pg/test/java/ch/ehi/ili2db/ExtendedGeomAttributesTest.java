package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class ExtendedGeomAttributesTest {
	
	protected static final String TEST_OUT="test/data/ExtendedAttributes/";
	private static final String DBSCHEMA = "ExtendedGeomAttributesTest";
	private static final String DBSCHEMA_ONE_GEOM = "ExtendedGeomAttributesTest_OneGeom";
	private static final String DATASETNAME = "Testset1";

	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd");

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
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}
	
	
	@Test
	public void importIli() throws Exception{
		
		Connection jdbcConnection=null;
		try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
	        	File data=new File(TEST_OUT,"ExtendedAttributes.ili");
	        	Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
	        	config.setFunction(Config.FC_SCHEMAIMPORT);
	        	config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
	        	config.setCreateFk(config.CREATE_FK_YES);
	        	config.setDefaultSrsCode("3116");
	        	Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				//Checks correct tables and columns creation
				{//parent class
					String stmtTxt="SELECT column_name FROM information_schema.columns WHERE table_schema ='"+DBSCHEMA.toLowerCase()+"' AND table_name = 'firstlevelclass'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					List<String> found_column_names = new ArrayList<String>();
					List<String> expected_column_names = new ArrayList<String>();
					expected_column_names.add("t_id");
					expected_column_names.add("my_geometry");
					expected_column_names.add("my_another_geometry");
					
					while(rs.next()) {
						found_column_names.add(rs.getString(1));
					}
					assertEquals(3, found_column_names.size());
					for(String column : found_column_names) {
						assertTrue(expected_column_names.contains(column));
					}
				}
				{//child class
					String stmtTxt="SELECT column_name FROM information_schema.columns WHERE table_schema ='"+DBSCHEMA.toLowerCase()+"' AND table_name = 'secondlevelclass'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					List<String> found_column_names = new ArrayList<String>();
					List<String> expected_column_names = new ArrayList<String>();
					expected_column_names.add("t_id");
					expected_column_names.add("my_geometry");
					expected_column_names.add("my_another_geometry");
					
					while(rs.next()) {
						found_column_names.add(rs.getString(1));
					}
					assertEquals(3, found_column_names.size());
					for(String column : found_column_names) {
						assertTrue(expected_column_names.contains(column));
					}
				}
				//Checks of correct attributes cardinalities
				{//optional attribute
					String stmtTxt = "SELECT attnotnull FROM pg_attribute where attrelid='"+DBSCHEMA.toLowerCase()+".\"firstlevelclass\"'::regclass AND attname='my_geometry'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertFalse(rs.getBoolean(1));
				}
				
				{//optional attribute
					String stmtTxt = "SELECT attnotnull FROM pg_attribute where attrelid='"+DBSCHEMA.toLowerCase()+".\"firstlevelclass\"'::regclass AND attname='my_another_geometry'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertFalse(rs.getBoolean(1));
				}
				{//override attribute from firstlevelclass, so cardinality must be mandatory
					String stmtTxt = "SELECT attnotnull FROM pg_attribute where attrelid='"+DBSCHEMA.toLowerCase()+".\"secondlevelclass\"'::regclass AND attname='my_geometry'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertTrue(rs.getBoolean(1));
				}
				{//NOT override attribute from firstlevelclass, so cardinality must be optional
					String stmtTxt = "SELECT attnotnull FROM pg_attribute where attrelid='"+DBSCHEMA.toLowerCase()+".\"secondlevelclass\"'::regclass AND attname='my_another_geometry'";
					Assert.assertTrue(stmt.execute(stmtTxt));
					ResultSet rs=stmt.getResultSet();
					Assert.assertTrue(rs.next());
					Assert.assertFalse(rs.getBoolean(1));
				}
				
	        }
		}finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
		
	}
	
	@Test
	public void importIliWithOneGeom() throws Exception{
		
		Connection jdbcConnection=null;
		try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA_ONE_GEOM+" CASCADE");
	        {
	        	File data=new File(TEST_OUT,"ExtendedAttributes.ili");
	        	Config config=initConfig(data.getPath(),DBSCHEMA_ONE_GEOM,data.getPath()+".log");	
	        	config.setFunction(Config.FC_SCHEMAIMPORT);
	        	config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
	        	config.setCreateFk(config.CREATE_FK_YES);
	        	config.setDefaultSrsCode("3116");
	        	config.setOneGeomPerTable(true);
	        	Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
	        }
	      //Check correct tables creation
			{//check tables quantity (must be 4: 2 for firstlevelclass and 2 for secondlevelclass)
				String stmtTxt="SELECT table_name FROM information_schema.tables WHERE table_schema ='"+DBSCHEMA_ONE_GEOM.toLowerCase()+"' AND (table_name LIKE 'firstlevelclass%' OR table_name LIKE 'secondlevelclass%')";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				List<String> found_table_names = new ArrayList<String>();
				while(rs.next()) {					
					found_table_names.add(rs.getString(1));
				}
				Assert.assertEquals(4, found_table_names.size());
			}
			{//check if a column is duplicated into several firstlevelclass tables
				String stmtTxt="SELECT column_name, count(column_name) FROM information_schema.columns WHERE table_schema ='"+DBSCHEMA_ONE_GEOM.toLowerCase()+"' AND table_name LIKE 'firstlevelclass%' AND column_name <> 't_id' GROUP BY column_name";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				
				while(rs.next()) {
					Assert.assertEquals(1, rs.getInt(2));
				}
			}
			{//check if a column is duplicated into several secondlevelclass tables
				String stmtTxt="SELECT column_name, count(column_name) FROM information_schema.columns WHERE table_schema ='"+DBSCHEMA_ONE_GEOM.toLowerCase()+"' AND table_name LIKE 'secondlevelclass%' AND column_name <> 't_id' GROUP BY column_name";
				Assert.assertTrue(stmt.execute(stmtTxt));
				ResultSet rs=stmt.getResultSet();
				
				while(rs.next()) {
					Assert.assertEquals(1, rs.getInt(2));
				}
			}
		}finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }   
		
	}

}
