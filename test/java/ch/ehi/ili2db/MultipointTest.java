package ch.ehi.ili2db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.sqlgen.DbUtility;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public abstract class MultipointTest {
    protected static final String TEST_OUT="test/data/MultiPoint/";
    
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
	
	@Test
	public void importXtfSmartCustom() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
            setup.resetDb();

			File data=new File(TEST_OUT,"MultiPoint2a.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_IMPORT);
	        config.setDoImplicitSchemaImport(true);
			config.setCreateFk(Config.CREATE_FK_YES);
            config.setImportTid(true);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setMultiPointTrafo(Config.MULTIPOINT_TRAFO_COALESCE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// assertions
            jdbcConnection = setup.createConnection();
            Statement stmt=jdbcConnection.createStatement();
			importXtfSmartCustom_assert_classa1(stmt);
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}

    protected void importXtfSmartCustom_assert_classa1(Statement stmt) throws Exception {
        ResultSet rs = stmt.executeQuery("SELECT st_asewkt(geom) FROM "+setup.prefixName("classa1")+";");
        while(rs.next()){
          	assertEquals("SRID=21781;MULTIPOINT(600030 200020,600015 200005)", rs.getObject(1));
        }
    }
	
	@Test
	public void exportXtfSmartCustom() throws Exception
	{
	    {
	        importXtfSmartCustom();
	    }
		try{
	        File data=new File(TEST_OUT,"MultiPoint2a-out.xtf");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
			config.setModels("MultiPoint2");
			config.setFunction(Config.FC_EXPORT);
			config.setExportTid(true);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
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
			 // check oid
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("13", obj0.getobjectoid());
			 }
			 // check objecttag
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiPoint2.TestA.ClassA1", obj0.getobjecttag());
			 }
			 // check values of attrnames
			 {
				 IomObject obj0 = objs.get("13");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("MultiPoint2.MultiPoint2D {points [MultiPoint2.PointStruktur2D {coord COORD {C1 600030.000, C2 200020.000}}, MultiPoint2.PointStruktur2D {coord COORD {C1 600015.000, C2 200005.000}}]}", obj0.getattrobj("geom", 0).toString());
			 }
		}catch(Exception e) {
			throw new IoxException(e);
		}finally{
		}
	}
}