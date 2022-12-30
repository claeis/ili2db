package ch.ehi.ili2pg;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.Test;

import ch.ehi.ili2db.AbstractTestSetup;
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

public class SimplePgTest extends ch.ehi.ili2db.SimpleTest {
    private final static String DBSCHEMA="simple";
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
    
    @Test
    public void exportEmptyGeom() throws Exception {
        setup.resetDb();
        File data=new File(TEST_OUT,"SimpleCoord23.ili");
        Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_SCHEMAIMPORT);
        config.setCreateFk(Config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setBasketHandling(null);
        config.setDefaultSrsCode("2056");
        setup.setXYParams(config);
        Ili2db.run(config,null);
        
        Connection jdbcConnection = setup.createConnection();
        Statement stmt = jdbcConnection.createStatement();
        stmt.execute("INSERT INTO "+DBSCHEMA+".classa1(t_ili_tid,attr2) VALUES ('o1',ST_GeomFromText('POINT EMPTY', 2056))");

        data=new File(TEST_OUT,"SimpleCoord23empty-out.xtf");
        config=setup.initConfig(data.getPath(),data.getPath()+".log");
        config.setFunction(Config.FC_EXPORT);
        config.setExportTid(true);
        config.setModels("SimpleCoord23");
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
                assertEquals("o1", oid);
                String attrtag=iomObj.getobjecttag();
                assertEquals("SimpleCoord23.TestA.ClassA1", attrtag);
                {
                    {
                        IomObject coord=iomObj.getattrobj("attr2", 0);
                        assertNull(coord);
                    }
                }
            }
            assertTrue(reader.read() instanceof EndBasketEvent);
            assertTrue(reader.read() instanceof EndTransferEvent);
        }
    }

}
