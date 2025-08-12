package ch.ehi.ili2gpkg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import ch.ehi.ili2db.Ili2dbAssert;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class BlackBoxTypes23GpkgTest {
	private static final String TEST_OUT="test/data/BlackBoxTypes23/";
    private static final String GPKGFILENAME=TEST_OUT+"BlackBoxTypes23.gpkg";
	private Connection jdbcConnection=null;
	private Statement stmt=null;
	
	@Before
	public void initDb() throws Exception
	{
	    File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){
            gpkgFile.delete();
        }
	}
	
	public void openDb() throws Exception
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
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
		{
			File data=new File(TEST_OUT,"BlackBoxTypes23.ili");
			Config config=initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			
			openDb();
			{
				ResultSet rs=jdbcConnection.getMetaData().getColumns(null, null, "classa", "xmlbox");
				Assert.assertTrue(rs.next());
				Assert.assertEquals(java.sql.Types.VARCHAR,rs.getInt("DATA_TYPE"));
			}
			{
				ResultSet rs=jdbcConnection.getMetaData().getColumns(null, null, "classa", "binbox");
				Assert.assertTrue(rs.next());
				Assert.assertEquals(java.sql.Types.VARCHAR,rs.getInt("DATA_TYPE"));
			}
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"BlackBoxTypes23.Topic.ClassA.xmlbox", "xmlbox", "classa", null},
                    {"BlackBoxTypes23.Topic.ClassA.binbox", "binbox", "classa", null},
                };
                Ili2dbAssert.assertAttrNameTableFromGpkg(jdbcConnection, expectedValues);
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"BlackBoxTypes23.Topic.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},    
                };
                Ili2dbAssert.assertTrafoTableFromGpkg(jdbcConnection, expectedValues);
            }
		}
	}
	
	@Test
	public void importXtf() throws Exception
	{
        File gpkgFile=new File(GPKGFILENAME);
        if(gpkgFile.exists()){ 
            File file = new File(gpkgFile.getAbsolutePath());
            file.delete();
        }
		{
			File data=new File(TEST_OUT,"BlackBoxTypes23a.xtf");
			Config config=initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setImportTid(true);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportXtf() throws Exception
	{
		{
			importXtf();
		}
		File data=new File(TEST_OUT,"BlackBoxTypes23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setModels("BlackBoxTypes23");
		config.setFunction(Config.FC_EXPORT);
		config.setExportTid(true);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		{
			XtfReader reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			
			IoxEvent event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj=((ObjectEvent)event).getIomObject();
			
			{
				String oid=iomObj.getobjectoid();
				assertEquals("o0",oid);
				String attrtag=iomObj.getobjecttag();
				assertEquals("BlackBoxTypes23.Topic.ClassA",attrtag);
				{
					String attr=iomObj.getattrvalue("binbox");
					assertTrue(attr==null);
				}
				{
					String attr=iomObj.getattrvalue("xmlbox");
					assertTrue(attr==null);
				}
			}
			event=reader.read();
			assertTrue(event instanceof ObjectEvent);
			IomObject iomObj2=((ObjectEvent)event).getIomObject();
			{
				String oid=iomObj2.getobjectoid();
				assertEquals("o1",oid);
				String attrtag=iomObj2.getobjecttag();
				assertEquals("BlackBoxTypes23.Topic.ClassA",attrtag);
				{
					String attr=iomObj2.getattrvalue("binbox");
					assertTrue(attr!=null);
					assertEquals("AAAA",attr);
				}
				{
					DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					InputSource is = new InputSource();
					String attr=iomObj2.getattrvalue("xmlbox");
					assertTrue(attr!=null);
					is.setCharacterStream(new StringReader(attr));
					Document doc = db.parse(is);
					NodeList nodes = doc.getElementsByTagName("anyXml");
					assertTrue(nodes!=null);
					Node node=nodes.item(0);
					assertEquals(null,node.getNodeValue());
				}
			}
			assertTrue(reader.read() instanceof EndBasketEvent);
			assertTrue(reader.read() instanceof EndTransferEvent);
		}
	}
}