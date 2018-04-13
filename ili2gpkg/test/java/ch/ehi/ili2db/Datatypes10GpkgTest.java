package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Datatypes10GpkgTest {
	
	private static final String TEST_OUT="test/data/Datatypes10/";
    private static final String GPKGFILENAME=TEST_OUT+"Datatypes10.gpkg";
	private Connection jdbcConnection=null;
	private Statement stmt=null;
	
	public void initDb() throws Exception
	{
	    Class driverClass = Class.forName("org.sqlite.JDBC");
        jdbcConnection = DriverManager.getConnection("jdbc:sqlite:"+GPKGFILENAME, null, null);
        stmt=jdbcConnection.createStatement();
	}
	
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2gpkg.GpkgMain().initConfig(config);
		config.setDbfile(GPKGFILENAME);
		config.setDburl("jdbc:sqlite:"+GPKGFILENAME);
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
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
		File data=new File(TEST_OUT,"Datatypes10.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}

	@Test
	public void importItf() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
		File data=new File(TEST_OUT,"Datatypes10a.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		initDb();
		String stmtTxt="SELECT atext FROM tablea AS a INNER JOIN subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
		Assert.assertTrue(stmt.execute(stmtTxt));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals("obj11",rs.getObject(1));
		}
	}
	
	@Test
	public void exportItf() throws Exception
	{
		{
			importItf();
		}
		File data=new File(TEST_OUT,"Datatypes10a-out.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("Datatypes10");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		{
			ItfReader reader=new ItfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("11",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.TableA",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("10",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.TableA",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("11",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.OtherTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("10",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.OtherTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("30",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("31",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("32",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}

	@Test
	public void importItfWithSkipPolygonBuilding() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
		File data=new File(TEST_OUT,"Datatypes10a.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_IMPORT);
		config.setCreateFk(config.CREATE_FK_YES);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
		config.setDoItfLineTables(true);
		config.setCatalogueRefTrafo(null);
		config.setMultiSurfaceTrafo(null);
		config.setMultilingualTrafo(null);
		config.setInheritanceTrafo(null);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		initDb();
		String stmtTxt="SELECT atext FROM tablea AS a INNER JOIN subtable AS b ON (a.t_id=b.main)  WHERE b.t_ili_tid='30'";
		Assert.assertTrue(stmt.execute(stmtTxt));
		{
			ResultSet rs=stmt.getResultSet();
			Assert.assertTrue(rs.next());
			Assert.assertEquals("obj11",rs.getObject(1));
		}
	}
	
	@Test
	public void exportItfWithSkipPolygonBuilding() throws Exception
	{
		{
			importItfWithSkipPolygonBuilding();
		}
		File data=new File(TEST_OUT,"Datatypes10a-ltout.itf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("Datatypes10");
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);

		{
			ItfReader reader=new ItfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("10",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.TableA",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("11",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.TableA",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("10",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.OtherTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("11",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.OtherTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("30",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("31",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			{
				event=reader.read();
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj1=((ObjectEvent)event).getIomObject();
				{
					String oid=iomObj1.getobjectoid();
					assertEquals("32",oid);
					String attrtag=iomObj1.getobjecttag();
					assertEquals("Datatypes10.Topic.SubTable",attrtag);
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}