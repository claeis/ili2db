package ch.ehi.ili2pg;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
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

/*
 * jdbc:postgresql:database
 * jdbc:postgresql://host/database
 * jdbc:postgresql://host:port/database
 */
// -Ddburl=jdbc:postgresql:dbname -Ddbusr=usrname -Ddbpwd=1234
public class CatalogueObjectsTest {
	
	private static final String DBSCHEMA = "CatalogueObjects1";
	private static final String DATASETNAME = "Testset1";
	private static final String TEST_OUT="test/data/CatalogueObjects/";
	
	String dburl=System.getProperty("dburl"); 
	String dbuser=System.getProperty("dbusr");
	String dbpwd=System.getProperty("dbpwd"); 

	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);
		config.setDburl(dburl);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
        config.setSetupPgExt(true);
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setXtffile(xtfFilename);
		if(Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
	}

	@Test
	public void importIli() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"CatalogueObjects1.ili");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_SCHEMAIMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
				{
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"Localisation_V1.MultilingualText.LocalisedText", "multilingualtext_localisedtext", "localisedtext", "multilingualtext"},
                        {"CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference.Reference", "areference", "catalogues_mandatorycataloguereference", "catalogues_item"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm2_n", "topicc_nutzung_programm2_n", "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm_n", "topicc_nutzung_programm_n", "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2.Titel", "titel", "topica_katalog_programm2", null},
                        {"CatalogueObjects1.TopicA.Katalog_Programm.Programm", "topica_katalog_prgramm_programm", "multilingualtext",  "topica_katalog_programm"},
                        {"Localisation_V1.LocalisedText.Text", "atext", "localisedtext", null},
                        {"CatalogueObjects_V1.Catalogues.CatalogueReference.Reference", "areference", "catalogues_cataloguereference", "catalogues_item"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm.Code",    "acode", "topica_katalog_programm", null},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm_1",   "topicc_nutzung_programm_1", "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm2_1",  "topicc_nutzung_programm2_1",    "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "topicc_nutzung_ohneuuid_1", "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_1",  "topicc_nutzung_ohneuuid2_1",    "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_n",  "topicc_nutzung_ohneuuid2_n",    "catalogues_cataloguereference", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_n",   "topicc_nutzung_ohneuuid_n", "catalogues_cataloguereference", "topicc_nutzung"},
                        {"Localisation_V1.LocalisedText.Language",    "alanguage", "localisedtext", null},
                        
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
				}
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"CatalogueObjects1.TopicA.Katalog_Programm", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects_V1.Catalogues.Item", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Localisation_V1.MultilingualText", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicC.Nutzung", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2Ref", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicA.Katalog_ProgrammRef", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Localisation_V1.LocalisedText", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects_V1.Catalogues.CatalogueReference", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid2Ref", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"LocalisationCH_V1.LocalisedText", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"LocalisationCH_V1.MultilingualText", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuidRef", "ch.ehi.ili2db.inheritance", "newClass"}, 
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
	
    @Test
    public void importIliSmart1CoalesceCatalogRef() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"CatalogueObjects1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
                config.setDatasetName(DATASETNAME);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
                {
                    // t_ili2db_attrname
                    String [][] expectedValues=new String[][] {
                        {"CatalogueObjects1.TopicC.Nutzung.Programm_1", "programm_1", "topicc_nutzung", "topica_katalog_programm"},
                        {"Localisation_V1.MultilingualText.LocalisedText", "multilingualtext_localisedtext", "localisedtext", "multilingualtext"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm.Programm",    "programm",  "topica_katalog_programm", null},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm_n",   "topicc_nutzung_programm_n", "topica_katalog_programmref", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm2_n",  "topicc_nutzung_programm2_n", "topica_katalog_programmref", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2.Titel",  "titel", "topica_katalog_programm", null},
                        {"CatalogueObjects1.TopicB.OhneUuidRef.Reference",    "areference", "topicb_ohneuuidref", "topicb_ohneuuid"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "ohneuuid_1", "topicc_nutzung", "topicb_ohneuuid"},
                        {"Localisation_V1.LocalisedText.Text", "atext", "localisedtext", null},
                        {"CatalogueObjects1.TopicA.Katalog_Programm.Code",    "acode",  "topica_katalog_programm", null},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm2_1",  "programm2_1",   "topicc_nutzung",    "topica_katalog_programm"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_1",  "ohneuuid2_1",   "topicc_nutzung",    "topicb_ohneuuid"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_n",   "topicc_nutzung_ohneuuid_n", "topicb_ohneuuidref", "topicc_nutzung"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_n",  "topicc_nutzung_ohneuuid2_n",    "topicb_ohneuuidref", "topicc_nutzung"},
                        {"Localisation_V1.LocalisedText.Language", "alanguage", "localisedtext" , null},
                        {"CatalogueObjects1.TopicA.Katalog_ProgrammRef.Reference", "areference", "topica_katalog_programmref", "topica_katalog_programm"},
                        
                    };
                    Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
                {
                    // t_ili2db_trafo
                    String [][] expectedValues=new String[][] {
                        {"Localisation_V1.MultilingualText",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "ch.ehi.ili2db.catalogueRefTrafo", "coalesce"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2",    "ch.ehi.ili2db.inheritance", "superClass"},
                        {"CatalogueObjects1.TopicC.Nutzung",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm2_1",  "ch.ehi.ili2db.catalogueRefTrafo", "coalesce"},
                        {"CatalogueObjects_V1.Catalogues.CatalogueReference", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"CatalogueObjects1.TopicA.Katalog_ProgrammRef",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"Localisation_V1.LocalisedText", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuidRef",  "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid2Ref", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm", "ch.ehi.ili2db.inheritance", "newClass"},
                        {"LocalisationCH_V1.MultilingualText", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"LocalisationCH_V1.LocalisedText", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm.Programm", "ch.ehi.ili2db.multilingualTrafo",   "expand"},
                        {"CatalogueObjects1.TopicA.Katalog_Programm2Ref", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"CatalogueObjects_V1.Catalogues.Item", "ch.ehi.ili2db.inheritance", "subClass"},
                        {"CatalogueObjects1.TopicB.OhneUuid2", "ch.ehi.ili2db.inheritance", "superClass"},
                        {"CatalogueObjects1.TopicC.Nutzung.Programm_1", "ch.ehi.ili2db.catalogueRefTrafo",  "coalesce"},
                        {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_1",  "ch.ehi.ili2db.catalogueRefTrafo", "coalesce"},
                    };
                    Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
                }
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void importIliSmart2CoalesceCatalogRef() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(
                    dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"CatalogueObjects1.ili");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_SCHEMAIMPORT);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
                config.setDatasetName(DATASETNAME);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
            {
                // t_ili2db_attrname
                String [][] expectedValues=new String[][] {
                    {"CatalogueObjects1.TopicC.Nutzung.Programm_1",   "programm_1_topica_katalog_programm",    "topicc_nutzung", "topica_katalog_programm"},
                    {"Localisation_V1.MultilingualText.LocalisedText",    "multilingualtext_localisedtext", "localisedtext", "multilingualtext"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm.Programm",    "programm", "topica_katalog_programm", null},
                    {"CatalogueObjects1.TopicB.OhneUuid2Ref.Reference",   "areference", "topicb_ohneuuid2ref","topicb_ohneuuid2"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm2.Titel",  "titel", "topica_katalog_programm2",null},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm_n",   "topicc_nutzung_programm_n", "topica_katalog_programmref",    "topicc_nutzung"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm.Code",    "acode",  "topica_katalog_programm2",null},
                    {"CatalogueObjects1.TopicA.Katalog_Programm2Ref.Reference",   "areference", "topica_katalog_programm2ref",   "topica_katalog_programm2"},
                    {"CatalogueObjects1.TopicB.OhneUuidRef.Reference",    "reference_topicb_ohneuuid", "topicb_ohneuuidref",    "topicb_ohneuuid"},
                    {"CatalogueObjects1.TopicB.OhneUuidRef.Reference",    "reference_topicb_ohneuuid2",    "topicb_ohneuuidref", "topicb_ohneuuid2"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm.Code",    "acode" , "topica_katalog_programm", null},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_1",  "ohneuuid2_1",   "topicc_nutzung",    "topicb_ohneuuid2"},
                    {"LocalisationCH_V1.MultilingualText.LocalisedText",  "loclstnch_vmltlngltext_localisedtext",  "localisationch_v1_localisedtext", "localisationch_v1_multilingualtext"},
                    {"Localisation_V1.LocalisedText.Text",    "atext", "localisationch_v1_localisedtext", null},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm2_n",  "topicc_nutzung_programm2_n", "topica_katalog_programm2ref", "topicc_nutzung"},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "ohneuuid_1_topicb_ohneuuid", "topicc_nutzung", "topicb_ohneuuid"},
                    {"Localisation_V1.LocalisedText.Text",    "atext", "localisedtext", null },
                    {"CatalogueObjects1.TopicC.Nutzung.Programm_1",   "programm_1_topica_katalog_programm2",   "topicc_nutzung",    "topica_katalog_programm2"},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm2_1",  "programm2_1", "topicc_nutzung", "topica_katalog_programm2"},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "ohneuuid_1_topicb_ohneuuid2", "topicc_nutzung", "topicb_ohneuuid2"},
                    {"Localisation_V1.LocalisedText.Language",    "alanguage", "localisationch_v1_localisedtext", null},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_n",   "topicc_nutzung_ohneuuid_n", "topicb_ohneuuidref", "topicc_nutzung"},
                    {"CatalogueObjects1.TopicA.Katalog_ProgrammRef.Reference", "reference_topica_katalog_programm2", "topica_katalog_programmref", "topica_katalog_programm2"},
                    {"CatalogueObjects1.TopicA.Katalog_ProgrammRef.Reference", "reference_topica_katalog_programm" , "topica_katalog_programmref", "topica_katalog_programm"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm.Programm",    "programm", "topica_katalog_programm2", null},
                    {"Localisation_V1.LocalisedText.Language",    "alanguage", "localisedtext", null},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_n", "topicc_nutzung_ohneuuid2_n", "topicb_ohneuuid2ref","topicc_nutzung"},

                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_n","topicc_nutzung_ohneuuid_n","topicb_ohneuuid2ref","topicc_nutzung"},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm_n","topicc_nutzung_programm_n","topica_katalog_programm2ref","topicc_nutzung"},
                    {"Localisation_V1.MultilingualText.LocalisedText","multilingualtext_localisedtext","localisationch_v1_localisedtext","multilingualtext"}
                    
                };
                Ili2dbAssert.assertAttrNameTable(jdbcConnection,expectedValues, DBSCHEMA);
            }
            {
                // t_ili2db_trafo
                String [][] expectedValues=new String[][] {
                    {"Localisation_V1.MultilingualText",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid_1",   "ch.ehi.ili2db.catalogueRefTrafo",   "coalesce"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm2",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicC.Nutzung",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm2_1",  "ch.ehi.ili2db.catalogueRefTrafo",  "coalesce"},
                    {"CatalogueObjects_V1.Catalogues.CatalogueReference", "ch.ehi.ili2db.inheritance", "subClass"},
                    {"CatalogueObjects1.TopicA.Katalog_ProgrammRef",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"Localisation_V1.LocalisedText", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicB.OhneUuidRef",  "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicB.OhneUuid", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicB.OhneUuid2Ref", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"LocalisationCH_V1.MultilingualText",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"LocalisationCH_V1.LocalisedText",   "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm.Programm",    "ch.ehi.ili2db.multilingualTrafo", "expand"},
                    {"CatalogueObjects1.TopicA.Katalog_Programm2Ref", "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference",    "ch.ehi.ili2db.inheritance", "subClass"},
                    {"CatalogueObjects_V1.Catalogues.Item",   "ch.ehi.ili2db.inheritance", "subClass"},
                    {"CatalogueObjects1.TopicB.OhneUuid2",    "ch.ehi.ili2db.inheritance", "newAndSubClass"},
                    {"CatalogueObjects1.TopicC.Nutzung.Programm_1", "ch.ehi.ili2db.catalogueRefTrafo", "coalesce"},
                    {"CatalogueObjects1.TopicC.Nutzung.OhneUuid2_1",  "ch.ehi.ili2db.catalogueRefTrafo", "coalesce"},
                };
                Ili2dbAssert.assertTrafoTable(jdbcConnection,expectedValues, DBSCHEMA);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
	
    @Ignore("fails because of mixed TID type in base table")
	@Test
	public void importXtf() throws Exception
	{
		//EhiLogger.getInstance().setTraceFilter(false);
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
	        Statement stmt=jdbcConnection.createStatement();
	        stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
	        {
				File data=new File(TEST_OUT,"CatalogueObjects1a.xtf");
				Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
				Ili2db.setNoSmartMapping(config);
				config.setFunction(Config.FC_IMPORT);
				config.setCreateFk(Config.CREATE_FK_YES);
				config.setDatasetName(DATASETNAME);
				config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
				config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
				//config.setCreatescript(data.getPath()+".sql");
				//config.setValidation(false);
				Ili2db.readSettingsFromDb(config);
				Ili2db.run(config,null);
			}
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}	
	
    @Test
    public void importXtfSmart1CoalesceCatalogRef() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"CatalogueObjects1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setDatasetName(DATASETNAME);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                //config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void importXtfSmart2CoalesceCatalogRef() throws Exception
    {
        //EhiLogger.getInstance().setTraceFilter(false);
        Connection jdbcConnection=null;
        try{
             Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            Statement stmt=jdbcConnection.createStatement();
            stmt.execute("DROP SCHEMA IF EXISTS "+DBSCHEMA+" CASCADE");
            {
                File data=new File(TEST_OUT,"CatalogueObjects1a.xtf");
                Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
                Ili2db.setNoSmartMapping(config);
                config.setFunction(Config.FC_IMPORT);
                config.setDoImplicitSchemaImport(true);
                config.setCreateFk(Config.CREATE_FK_YES);
                config.setDatasetName(DATASETNAME);
                config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART2);
                config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
                config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
                config.setBasketHandling(Config.BASKET_HANDLING_READWRITE);
                config.setNameOptimization(Config.NAME_OPTIMIZATION_TOPIC);
                //config.setCreatescript(data.getPath()+".sql");
                //config.setValidation(false);
                Ili2db.readSettingsFromDb(config);
                Ili2db.run(config,null);
            }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }   
	
    @Ignore("see importXtf()")
	@Test
	public void exportXtf() throws Exception
	{
    	{
    		importXtf();
    	}
		Connection jdbcConnection=null;
		try{
			 Class driverClass = Class.forName("org.postgresql.Driver");
	        jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
			File data=new File(TEST_OUT,"CatalogueObjects1a-out.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setFunction(Config.FC_EXPORT);
			config.setDatasetName(DATASETNAME);
			config.setBaskets("CatalogueObjects1.TopicC.1");
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
			// read objects of db and write objectValue to HashMap
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
			 {
				 IomObject obj0 = objs.get("9");
				 Assert.assertNotNull(obj0);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Katalog_OhneUuid", obj0.getobjecttag());
			 }
			 {
				 IomObject obj1 = objs.get("10");
				 Assert.assertNotNull(obj1);
				 Assert.assertEquals("CatalogueObjects1.TopicB.Nutzung", obj1.getobjecttag());
			 }
		}finally{
			if(jdbcConnection!=null){
				jdbcConnection.close();
			}
		}
	}
    
    @Test
    public void exportXtfSmart1CoalesceCatalogRef() throws Exception
    {
    	{
    		importXtfSmart1CoalesceCatalogRef();
    	}
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File(TEST_OUT,"CatalogueObjects1a-smart1out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setDatasetName(DATASETNAME);
            config.setBaskets("CatalogueObjects1.TopicC.1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // read objects of db and write objectValue to HashMap
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
            IomObject nutzung=null;
            IomObject ohneUuid=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null){
                        objs.put(iomObj.getobjectoid(), iomObj);
                        if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicC.Nutzung")) {
                            nutzung=iomObj;
                        }else if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicB.OhneUuid")) {
                            ohneUuid=iomObj;
                        }
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertNotNull(ohneUuid);
             Assert.assertNotNull(nutzung);
             
            // ohneUuid
         	String objTag=ohneUuid.getobjecttag();
         	assertEquals("CatalogueObjects1.TopicB.OhneUuid",objTag);
         	String oid=ohneUuid.getobjectoid();
			assertEquals("6",oid);
             {
                // Programm_n
            	IomObject programm_n=nutzung.getattrobj("Programm_n", 0);
            	IomObject programm=programm_n.getattrobj("Reference", 0);
            	String refOid=programm.getobjectrefoid();
				assertEquals("5880375a-52cd-4b2d-af50-c3a6fc5c5352",refOid);
             }
             {
				// OhneUuid_n
				IomObject programm_n=nutzung.getattrobj("OhneUuid_n", 0);
            	IomObject programm=programm_n.getattrobj("Reference", 0);
            	String refOid=programm.getobjectrefoid();
				assertEquals(oid,refOid);
             }
             {
				// Programm_1
				IomObject programm_n=nutzung.getattrobj("Programm_1", 0);
            	IomObject programm=programm_n.getattrobj("Reference", 0);
            	String refOid=programm.getobjectrefoid();
				assertEquals("5880375a-52cd-4b2d-af50-c3a6fc5c5352",refOid);
             }
             {
				// OhneUuid_1
				IomObject programm_n=nutzung.getattrobj("OhneUuid_1", 0);
            	IomObject programm=programm_n.getattrobj("Reference", 0);
            	String refOid=programm.getobjectrefoid();
				assertEquals(oid,refOid);
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
    
    @Test
    public void exportXtfSmart2CoalesceCatalogRef() throws Exception
    {
    	{
    		importXtfSmart2CoalesceCatalogRef();
    	}
        Connection jdbcConnection=null;
        try{
            Class driverClass = Class.forName("org.postgresql.Driver");
            jdbcConnection = DriverManager.getConnection(dburl, dbuser, dbpwd);
            File data=new File(TEST_OUT,"CatalogueObjects1a-smart2out.xtf");
            Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
            config.setFunction(Config.FC_EXPORT);
            config.setDatasetName(DATASETNAME);
            config.setBaskets("CatalogueObjects1.TopicC.1");
            Ili2db.readSettingsFromDb(config);
            Ili2db.run(config,null);
            // read objects of db and write objectValue to HashMap
            HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
            XtfReader reader=new XtfReader(data);
            IoxEvent event=null;
            IomObject nutzung=null;
            IomObject ohneUuid=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null){
                        objs.put(iomObj.getobjectoid(), iomObj);
                        if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicC.Nutzung")) {
                            nutzung=iomObj;
                        }else if(iomObj.getobjecttag().equals("CatalogueObjects1.TopicB.OhneUuid")) {
                            ohneUuid=iomObj;
                        }
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
             Assert.assertNotNull(ohneUuid);
             Assert.assertNotNull(nutzung);
             {
            	// ohneUuid
              	String objTag=ohneUuid.getobjecttag();
              	assertEquals("CatalogueObjects1.TopicB.OhneUuid",objTag);
              	String oid=ohneUuid.getobjectoid();
     			assertEquals("6",oid);
                  {
                     // Programm_n
                 	IomObject programm_n=nutzung.getattrobj("Programm_n", 0);
                 	IomObject programm=programm_n.getattrobj("Reference", 0);
                 	String refOid=programm.getobjectrefoid();
     				assertEquals("5880375a-52cd-4b2d-af50-c3a6fc5c5352",refOid);
                  }
                  {
     				// OhneUuid_n
     				IomObject programm_n=nutzung.getattrobj("OhneUuid_n", 0);
                 	IomObject programm=programm_n.getattrobj("Reference", 0);
                 	String refOid=programm.getobjectrefoid();
     				assertEquals(oid,refOid);
                  }
                  {
     				// Programm_1
     				IomObject programm_n=nutzung.getattrobj("Programm_1", 0);
                 	IomObject programm=programm_n.getattrobj("Reference", 0);
                 	String refOid=programm.getobjectrefoid();
     				assertEquals("5880375a-52cd-4b2d-af50-c3a6fc5c5352",refOid);
                  }
                  {
     				// OhneUuid_1
     				IomObject programm_n=nutzung.getattrobj("OhneUuid_1", 0);
                 	IomObject programm=programm_n.getattrobj("Reference", 0);
                 	String refOid=programm.getobjectrefoid();
     				assertEquals(oid,refOid);
                  }
             }
        }finally{
            if(jdbcConnection!=null){
                jdbcConnection.close();
            }
        }
    }
}