package ch.ehi.ili2fgdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.sql.Connection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class MultilineTest {
	
	private Connection jdbcConnection=null;
	private static final String TEST_OUT="test/data/MultiLine/";
    private static final String FGDBFILENAME=TEST_OUT+"MultiLine.gdb";
	
	public Config initConfig(String xtfFilename,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2fgdb.FgdbMain().initConfig(config);
		config.setDbfile(FGDBFILENAME);
		config.setDburl(FgdbDriver.BASE_URL+FGDBFILENAME);
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}
	
	@After
	public void endDb() throws Exception
	{
		if(jdbcConnection!=null){
			jdbcConnection.close();
		}
	}
	
	@Test
	public void importIli() throws Exception
	{
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"MultiLine2.ili");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_SCHEMAIMPORT);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
		config.setStrokeArcs(Config.STROKE_ARCS_ENABLE);
		//Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
		
	}
	
	@Test
	public void importXtf() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(FGDBFILENAME);
	    Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(TEST_OUT,"MultiLine2b.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
		config.setFunction(Config.FC_IMPORT);
		config.setDoImplicitSchemaImport(true);
		config.setCreateFk(Config.CREATE_FK_YES);
		config.setCreateNumChecks(true);
		config.setTidHandling(Config.TID_HANDLING_PROPERTY);
		config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
		config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
		//Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
	}
    @Test
    public void importXtfStrokeArcs() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        File fgdbFile=new File(FGDBFILENAME);
        Fgdb4j.deleteFileGdb(fgdbFile);
        Class driverClass = Class.forName(FgdbDriver.class.getName());
        File data=new File(TEST_OUT,"MultiLine2b.xtf");
        Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
        config.setMultiLineTrafo(Config.MULTILINE_TRAFO_COALESCE);
        config.setStrokeArcs(Config.STROKE_ARCS_ENABLE);
        //Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
	
	@Test
	public void exportXtf() throws Exception
	{
		String path2Data="test/data/Simple/";
		{
			importXtf();
		}
		//EhiLogger.getInstance().setTraceFilter(false);
	    File fgdbFile=new File(path2Data+"Simple.gdb");
	    //Fgdb4j.deleteFileGdb(fgdbFile);
	    Class driverClass = Class.forName(FgdbDriver.class.getName());
		File data=new File(path2Data,"Simple23a-out.xtf");
		Config config=initConfig(data.getPath(),data.getPath()+".log");
		config.setFunction(Config.FC_EXPORT);
		config.setModels("MultiLine2");
		Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
		{
			XtfReader reader=new XtfReader(data);
			assertTrue(reader.read() instanceof StartTransferEvent);
			assertTrue(reader.read() instanceof StartBasketEvent);
			IoxEvent event=reader.read();
			{
				assertTrue(event instanceof ObjectEvent);
				IomObject iomObj=((ObjectEvent)event).getIomObject();
				String attrtag=iomObj.getobjecttag();
				assertEquals("MultiLine2.TestA.ClassA1",attrtag);
				
				IomObject multiline=iomObj.getattrobj("geom", 0);
                assertEquals("MultiLine2.MultiLinie2D {Linien [MultiLine2.LinieStruktur2D {Linie POLYLINE {sequence SEGMENTS {segment [COORD {C1 600001.000, C2 205001.000}, COORD {C1 600020.000, C2 205015.000}, ARC {A1 600010.000, A2 205018.000, C1 600001.000, C2 205015.000}, COORD {C1 600001.000, C2 205001.000}]}}}, MultiLine2.LinieStruktur2D {Linie POLYLINE {sequence SEGMENTS {segment [COORD {C1 600005.000, C2 205005.000}, COORD {C1 600010.000, C2 205010.000}, ARC {A1 600007.000, A2 205009.000, C1 600005.000, C2 205010.000}, COORD {C1 600005.000, C2 205005.000}]}}}]}",multiline.toString());
				
			}
		}
	}
}