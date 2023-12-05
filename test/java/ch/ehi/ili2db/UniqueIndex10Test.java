package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.generator.SqlConfiguration;

public abstract class UniqueIndex10Test {
    protected static final String TEST_DATA_DIR="test/data/UniqueIndex/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	
	@Test
	public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
			File data=new File(TEST_DATA_DIR,"UniqueIndex10.ili");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setValue(Config.UNIQUE_CONSTRAINTS,Config.UNIQUE_CONSTRAINTS_CREATE);
			config.setMaxSqlNameLength("20");
            config.setDefaultSrsAuthority("EPSG");
            config.setDefaultSrsCode("21781");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			{
	            jdbcConnection = setup.createConnection();
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        
                        {"UniqueIndex10.Topic.ClassA.attr1",    "attr1",   "classa",  null},
                        {"UniqueIndex10.Topic.ClassA.attr2",    "attr2",   "classa",  null},
                        {"UniqueIndex10.Topic.ClassB.attr1",    "attr1",   "classb",  null},
                        {"UniqueIndex10.Topic.ClassB.attr2",    "attr2",   "classb",  null},
                        {"UniqueIndex10.Topic.ClassC.attr1",    "attr1",   "classc",  null},
                        {"UniqueIndex10.Topic.ClassC.attr2",    "attr2",   "classc",  null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"UniqueIndex10.Topic.ClassA",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"UniqueIndex10.Topic.ClassB", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"UniqueIndex10.Topic.ClassC", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
                {
                    String[] idx=getUniqueIndexes(jdbcConnection,config,"classa");
                    Assert.assertEquals(1,idx.length);
                }
                {
                    String[] idx=getUniqueIndexes(jdbcConnection,config,"classb");
                    Assert.assertEquals(1,idx.length);
                }
                {
                    String[] idx=getUniqueIndexes(jdbcConnection,config,"classc");
                    Assert.assertEquals(1,idx.length);
                }
                
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}


    private String[] getUniqueIndexes(Connection jdbcConnection,Config config,String tableName) throws SQLException {
        DatabaseMetaData dbMetaData = jdbcConnection.getMetaData();
        ResultSet rs = dbMetaData.getIndexInfo(null, setup.getSchema(), tableName, true, false);
        String colT_ID=config.getColT_ID();
        if(colT_ID==null){
            colT_ID=DbNames.T_ID_COL;
        }
        List<String> idxs=new ArrayList<String>();
        while (rs.next()) {
            if(colT_ID.equalsIgnoreCase(rs.getString("COLUMN_NAME"))){
                
            }else {
                String name=rs.getString("INDEX_NAME");
                if(!idxs.contains(name)) {
                    idxs.add(name);
                }
            }
        }                
        return idxs.toArray(new String[idxs.size()]);
    }
}