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
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Dataset10Test {
	private static final String DBSCHEMA = "Dataset10";
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 
	Connection jdbcConnection=null;
	Statement stmt=null;
	@Before
	public void initDb() throws Exception
	{
	    Class driverClass = Class.forName("org.postgresql.Driver");
        jdbcConnection = DriverManager.getConnection(
        		dburl, dbuser, dbpwd);
        stmt=jdbcConnection.createStatement();
	}
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
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
	public void importItf() throws Exception
	{
        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
        
		File data=new File("test/data/Dataset10/Dataset10a.itf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		final String datasetName="ceis";
		config.setDatasetName(datasetName);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);

		String stmtTxt="SELECT "+DbNames.T_ILI_TID_COL+" FROM "+DBSCHEMA+"."+DbNames.BASKETS_TAB+" WHERE "+DbNames.BASKETS_TAB_TOPIC_COL+"='Dataset10.TopicB'";
		Assert.assertTrue(stmt.execute(stmtTxt));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals(datasetName+".TopicB",rs.getObject(1));
		}
	}
	
}
