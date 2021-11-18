package ch.ehi.ili2pg;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.MetaInfo23Test;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.metaattr.MetaAttrUtility;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

//-Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class MetaInfo23PgTest extends MetaInfo23Test {
	private static final String DBSCHEMA = "MetaInfo23";
	
    @Override
    protected AbstractTestSetup createTestSetup() {
        
        
        String dburl=System.getProperty("dburl"); 
        String dbuser=System.getProperty("dbusr");
        String dbpwd=System.getProperty("dbpwd");
        
        return new PgTestSetup(dburl,dbuser,dbpwd,DBSCHEMA);
    } 
	
    @Test
    public void importIliT_TypeConstraint() throws Exception {
        Connection jdbcConnection = null;
        Statement stmt = null;

        try{
            setup.resetDb();

            File data = new File(TEST_DATA_DIR, "T_Type23.ili");
            Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            config.setFunction(Config.FC_SCHEMAIMPORT);
            config.setCreateTypeConstraint(true);

            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config, null);

            String query = "SELECT pg_catalog.pg_get_constraintdef(r.oid, true) as condef FROM pg_catalog.pg_constraint r WHERE r.conrelid = '"+DBSCHEMA.toLowerCase()+".classa1'::regclass AND r.contype = 'c';";

            jdbcConnection=setup.createConnection();
            stmt=jdbcConnection.createStatement();
            Assert.assertTrue(stmt.execute(query));
            ResultSet rs = stmt.getResultSet();

            Assert.assertTrue(rs.next());
            String constraint = rs.getString(1);
            Assert.assertTrue(constraint.contains("classa1"));
            Assert.assertTrue(constraint.contains("classa1b"));
            Assert.assertTrue(constraint.contains("classa1c"));
            Assert.assertTrue(constraint.contains("classa1d"));

        }finally{
            if(jdbcConnection != null){
                jdbcConnection.close();
            }
        }
    }
	
}