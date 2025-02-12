package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.Ili2dbAssert;
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

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class Json24Test extends ch.ehi.ili2db.Json24Test {
	private static final String DBSCHEMA = "Json24";
	
	
    @Override
    protected AbstractTestSetup createTestSetup() {
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd"); 
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    }
	@Override
    protected void importXtf_doAsserts(java.sql.Statement stmt) throws SQLException {
        java.sql.ResultSet rs=null;
        try {
            rs=stmt.executeQuery("SELECT farben->0->>'@type',farben->0->'r' FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='1'");
            assertTrue(rs.next());
            assertEquals("Json24.TestA.Farbe",rs.getString(1));
            assertEquals(10,rs.getInt(2));
            rs=stmt.executeQuery("SELECT cast(farben as text) FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='2'");
            assertTrue(rs.next());
            rs.getString(1);
            assertEquals(true,rs.wasNull());

            rs=stmt.executeQuery("SELECT jsonb_typeof(farben),jsonb_typeof(farbe) FROM "+setup.prefixName("auto")+" WHERE t_ili_tid='3'");
            assertTrue(rs.next());
            assertEquals("array",rs.getString(1));
            assertEquals("object",rs.getString(2));
            
        }finally {
            if(rs!=null) {
                rs.close();
                rs=null;
            }
        }
    }
}