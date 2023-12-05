package ch.ehi.ili2db;

import java.io.File;
import java.sql.Connection;
import org.junit.Test;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.generator.SqlConfiguration;

public abstract class GeomIndex10Test {
    private static final String TEST_DATA_DIR="test/data/GeomIndex/";
    protected AbstractTestSetup setup=createTestSetup();
    protected abstract AbstractTestSetup createTestSetup() ;
    
	
	@Test
	public void importIli() throws Exception
	{
		Connection jdbcConnection=null;
		try{
            setup.resetDb();
	        jdbcConnection = setup.createConnection();
			File data=new File(TEST_DATA_DIR,"GeomIndex10.ili");
			Config config=setup.initConfig(data.getPath(),data.getPath()+".log");
            Ili2db.setNoSmartMapping(config);
			config.setFunction(Config.FC_SCHEMAIMPORT);
			config.setCreateFk(Config.CREATE_FK_YES);
			config.setTidHandling(Config.TID_HANDLING_PROPERTY);
			config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
			config.setValue(SqlConfiguration.CREATE_GEOM_INDEX,Config.TRUE);
			config.setMaxSqlNameLength("20");
            Ili2db.setSkipPolygonBuilding(config);
            config.setDefaultSrsAuthority("EPSG");
            config.setDefaultSrsCode("21781");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			{
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"GeomIndex10.Topic.FlaechenTable2.surface._geom",    "_geom", "flaechentable2_suace",null},
                        {"GeomIndex10.Topic.FlaechenTable2.surface._ref", "_ref",  "flaechentable2_suace",null},
                        {"GeomIndex10.Topic.FlaechenTable.surface._geom", "_geom", "flaechentable_surace",null},
                        {"GeomIndex10.Topic.FlaechenTable.surface._ref",  "_ref",  "flaechentable_surace",null},
                        {"GeomIndex10.Topic.flaace__geom_idx.dy", "dy",    "flaace__geom_idx",null},
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, setup.getSchema());
                    
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"GeomIndex10.Topic.FlaechenTable2",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"GeomIndex10.Topic.FlaechenTable", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"GeomIndex10.Topic.flaace__geom_idx", "ch.ehi.ili2db.inheritance", "newClass"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, setup.getSchema());
                }
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
}