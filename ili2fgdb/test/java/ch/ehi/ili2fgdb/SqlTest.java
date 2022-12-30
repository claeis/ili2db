package ch.ehi.ili2fgdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.fgdb4j.Fgdb4j;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2fgdb.jdbc.FgdbDriver;
import ch.ehi.sqlgen.generator_impl.fgdb.GeneratorFgdb;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class SqlTest {
	
	private static final String TEST_OUT="test/data/Simple/";
    private static final String FGDBFILENAME=TEST_OUT+"Simple.gdb";
	
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
		
    public void importXtf() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        File fgdbFile=new File(FGDBFILENAME);
        Fgdb4j.deleteFileGdb(fgdbFile);
        Class driverClass = Class.forName(FgdbDriver.class.getName());
        File data=new File(TEST_OUT,"Simple23b.xtf");
        Config config=initConfig(data.getPath(),data.getPath()+".log");
        Ili2db.setNoSmartMapping(config);
        config.setFunction(Config.FC_IMPORT);
        config.setDoImplicitSchemaImport(true);
        config.setCreateFk(config.CREATE_FK_YES);
        config.setCreateNumChecks(true);
        config.setTidHandling(Config.TID_HANDLING_PROPERTY);
        config.setImportTid(true);
        config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
        //Ili2db.readSettingsFromDb(config);
        Ili2db.run(config,null);
    }
    @Test
    public void testSubQryByString() throws Exception {
        importXtf();
        String stmt="SELECT T_Id,T_Ili_Tid,T_Type FROM (SELECT r1.T_Id, r1.T_Ili_Tid, 'classa1' T_Type FROM classa1 r1) r0 WHERE r0.T_Ili_Tid=?";
        //String stmt="SELECT T_Id, T_Ili_Tid, 'kt_codelisten_kt_code' T_Type FROM kt_codelisten_kt_code WHERE T_Ili_Tid=?";
        Class driverClass = Class.forName(FgdbDriver.class.getName());
        Connection conn = DriverManager.getConnection(
                FgdbDriver.BASE_URL+FGDBFILENAME, null, null);
        
        java.sql.PreparedStatement dbstmt = null;
        java.sql.ResultSet rs = null;
        long sqlid=0;
        String sqlType=null;
        try {

            dbstmt = conn.prepareStatement(stmt);
            dbstmt.clearParameters();
            dbstmt.setString(1, "o2");
            rs = dbstmt.executeQuery();
            if(rs.next()) {
                sqlid = rs.getLong(1);
                sqlType=rs.getString(3);
                assertEquals(5,sqlid);
            }else{
                // unknown object
                fail();
            }
        } catch (java.sql.SQLException ex) {
            fail();
        } finally {
            if (rs != null) {
                rs.close();
                rs=null;
            }
            if (dbstmt != null) {
                dbstmt.close();
                dbstmt=null;
            }
            if (conn != null) {
                conn.close();
                conn=null;
            }
        }
    }
    @Test
    public void testSubQryByLong() throws Exception {
        importXtf();
        String stmt="SELECT T_Id,T_Ili_Tid,T_Type FROM (SELECT r1.T_Id, r1.T_Ili_Tid, 'classa1' T_Type FROM classa1 r1) r0 WHERE r0.T_Id=?";
        //String stmt="SELECT T_Id, T_Ili_Tid, 'kt_codelisten_kt_code' T_Type FROM kt_codelisten_kt_code WHERE T_Ili_Tid=?";
        Class driverClass = Class.forName(FgdbDriver.class.getName());
        Connection conn = DriverManager.getConnection(
                FgdbDriver.BASE_URL+FGDBFILENAME, null, null);
        
        java.sql.PreparedStatement dbstmt = null;
        java.sql.ResultSet rs = null;
        String tid=null;
        String sqlType=null;
        try {

            dbstmt = conn.prepareStatement(stmt);
            dbstmt.clearParameters();
            dbstmt.setLong(1, 5);
            rs = dbstmt.executeQuery();
            if(rs.next()) {
                tid = rs.getString(2);
                sqlType=rs.getString(3);
                assertEquals("o2",tid);
            }else{
                // unknown object
                fail();
            }
        } catch (java.sql.SQLException ex) {
            fail();
        } finally {
            if (rs != null) {
                rs.close();
                rs=null;
            }
            if (dbstmt != null) {
                dbstmt.close();
                dbstmt=null;
            }
            if (conn != null) {
                conn.close();
                conn=null;
            }
        }
    }
	
}