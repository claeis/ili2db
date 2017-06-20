SET search_path = catalogueobjects1, pg_catalog;

INSERT INTO t_ili2db_dataset VALUES (1, 'Testset1');
INSERT INTO t_ili2db_basket VALUES (3, 1, 'CatalogueObjects1.TopicA', 'CatalogueObjects1.TopicA.1', 'CatalogueObjects1a.xtf-3');
INSERT INTO t_ili2db_basket VALUES (8, 1, 'CatalogueObjects1.TopicB', 'CatalogueObjects1.TopicB.1', 'CatalogueObjects1a.xtf-3');
INSERT INTO t_ili2db_attrname VALUES ('CatalogueObjects1.TopicA.Katalog_Programm.Code', 'code', 'topica_katalog_programm', NULL);
INSERT INTO t_ili2db_attrname VALUES ('Localisation_V1.LocalisedText.Text', 'atext', 'localisedtext', NULL);
INSERT INTO t_ili2db_attrname VALUES ('CatalogueObjects1.TopicB.Nutzung.Programm', 'topicb_nutzung_programm', 'topica_katalog_programmref', 'topicb_nutzung');
INSERT INTO t_ili2db_attrname VALUES ('CatalogueObjects_V1.Catalogues.CatalogueReference.Reference', 'reference_topica_katalog_programm', 'topica_katalog_programmref', 'topica_katalog_programm');
INSERT INTO t_ili2db_attrname VALUES ('CatalogueObjects1.TopicA.Katalog_Programm.Programm', 'programm', 'topica_katalog_programm', NULL);
INSERT INTO t_ili2db_attrname VALUES ('CatalogueObjects_V1.Catalogues.CatalogueReference.Reference', 'reference_topicb_katalog_ohneuuid', 'topica_katalog_programmref', 'topicb_katalog_ohneuuid');
INSERT INTO t_ili2db_attrname VALUES ('Localisation_V1.MultilingualText.LocalisedText', 'multilingualtext_localisedtext', 'localisedtext', 'multilingualtext');
INSERT INTO t_ili2db_attrname VALUES ('Localisation_V1.LocalisedText.Language', 'alanguage', 'localisedtext', NULL);
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects1.TopicA.Katalog_ProgrammRef', 'topica_katalog_programmref');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects_V1.Catalogues.Item', 'catalogues_item');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects_V1.Catalogues.CatalogueReference', 'catalogues_cataloguereference');
INSERT INTO t_ili2db_classname VALUES ('LocalisationCH_V1.MultilingualText', 'localisationch_v1_multilingualtext');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects1.TopicA.Katalog_Programm', 'topica_katalog_programm');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects1.TopicB.Nutzung', 'topicb_nutzung');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects1.TopicB.Katalog_OhneUuid', 'topicb_katalog_ohneuuid');
INSERT INTO t_ili2db_classname VALUES ('Localisation_V1.LocalisedText', 'localisedtext');
INSERT INTO t_ili2db_classname VALUES ('LocalisationCH_V1.LocalisedText', 'localisationch_v1_localisedtext');
INSERT INTO t_ili2db_classname VALUES ('Localisation_V1.MultilingualText', 'multilingualtext');
INSERT INTO t_ili2db_classname VALUES ('CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference', 'catalogues_mandatorycataloguereference');
INSERT INTO t_ili2db_import VALUES (2, 1, '2017-05-08 09:45:53.119', 'postgres', 'test\data\CatalogueObjects\CatalogueObjects1a.xtf');
INSERT INTO t_ili2db_import_basket VALUES (6, 2, 3, 2, 3, 5);
INSERT INTO t_ili2db_import_basket VALUES (13, 2, 8, 2, 8, 12);
INSERT INTO t_ili2db_import_object VALUES (7, 6, 'CatalogueObjects1.TopicA.Katalog_Programm', 2, 4, 5);
INSERT INTO t_ili2db_import_object VALUES (14, 13, 'CatalogueObjects1.TopicA.Katalog_ProgrammRef', 2, 11, 12);
INSERT INTO t_ili2db_import_object VALUES (15, 13, 'CatalogueObjects1.TopicB.Nutzung', 1, 10, 10);
INSERT INTO t_ili2db_import_object VALUES (16, 13, 'CatalogueObjects1.TopicB.Katalog_OhneUuid', 1, 9, 9);
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects1.TopicB.Nutzung', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('Localisation_V1.MultilingualText', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('Localisation_V1.LocalisedText', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects1.TopicA.Katalog_ProgrammRef', 'CatalogueObjects_V1.Catalogues.CatalogueReference');
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects1.TopicA.Katalog_Programm', 'CatalogueObjects_V1.Catalogues.Item');
INSERT INTO t_ili2db_inheritance VALUES ('LocalisationCH_V1.MultilingualText', 'Localisation_V1.MultilingualText');
INSERT INTO t_ili2db_inheritance VALUES ('LocalisationCH_V1.LocalisedText', 'Localisation_V1.LocalisedText');
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects1.TopicB.Katalog_OhneUuid', 'CatalogueObjects_V1.Catalogues.Item');
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects_V1.Catalogues.CatalogueReference', NULL);
INSERT INTO t_ili2db_inheritance VALUES ('CatalogueObjects_V1.Catalogues.Item', NULL);
INSERT INTO t_ili2db_model VALUES ('CatalogueObjects1.ili', '2.3', 'CatalogueObjects1{ LocalisationCH_V1 CatalogueObjects_V1}', 'INTERLIS 2.3;

MODEL CatalogueObjects1 (en) AT "mailto:ce@eisenhutinformatik.ch"
  VERSION "2017-04-21" =
  IMPORTS CatalogueObjects_V1,LocalisationCH_V1;

    TOPIC TopicA
    	EXTENDS CatalogueObjects_V1.Catalogues =
    	OID AS INTERLIS.UUIDOID;
    
	    CLASS Katalog_Programm
	    EXTENDS CatalogueObjects_V1.Catalogues.Item =
	      Code : MANDATORY TEXT*20;
	      Programm : MANDATORY LocalisationCH_V1.MultilingualText;
	    END Katalog_Programm;
	
	    STRUCTURE Katalog_ProgrammRef
	    EXTENDS CatalogueObjects_V1.Catalogues.CatalogueReference =
	      Reference (EXTENDED) : MANDATORY REFERENCE TO (EXTERNAL) Katalog_Programm;
	    END Katalog_ProgrammRef;
    END TopicA;
    
    TOPIC TopicB =
	    DEPENDS ON TopicA;
	
	    CLASS Katalog_OhneUuid
	    EXTENDS CatalogueObjects_V1.Catalogues.Item =
	    END Katalog_OhneUuid;
	    
	    CLASS Nutzung =
	      Programm : BAG {0..*} OF CatalogueObjects1.TopicA.Katalog_ProgrammRef;
	    END Nutzung;
	    
    END TopicB;

END CatalogueObjects1.
', '2017-05-08 09:45:53.103');
INSERT INTO t_ili2db_model VALUES ('CHBase_Part2_LOCALISATION_20110830.ili', '2.3', 'InternationalCodes_V1 Localisation_V1{ InternationalCodes_V1} LocalisationCH_V1{ InternationalCodes_V1 Localisation_V1} Dictionaries_V1{ InternationalCodes_V1} DictionariesCH_V1{ Dictionaries_V1 InternationalCodes_V1}', '/* ########################################################################
   CHBASE - BASE MODULES OF THE SWISS FEDERATION FOR MINIMAL GEODATA MODELS
   ======
   BASISMODULE DES BUNDES           MODULES DE BASE DE LA CONFEDERATION
   F�R MINIMALE GEODATENMODELLE     POUR LES MODELES DE GEODONNEES MINIMAUX
   
   PROVIDER: GKG/KOGIS - GCS/COSIG             CONTACT: models@geo.admin.ch
   PUBLISHED: 2011-08-30
   ########################################################################
*/

INTERLIS 2.3;

/* ########################################################################
   ########################################################################
   PART II -- LOCALISATION
   - Package InternationalCodes
   - Packages Localisation, LocalisationCH
   - Packages Dictionaries, DictionariesCH
*/

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
TYPE MODEL InternationalCodes_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  DOMAIN
    LanguageCode_ISO639_1 = (de,fr,it,rm,en,
      aa,ab,af,am,ar,as,ay,az,ba,be,bg,bh,bi,bn,bo,br,ca,co,cs,cy,da,dz,el,
      eo,es,et,eu,fa,fi,fj,fo,fy,ga,gd,gl,gn,gu,ha,he,hi,hr,hu,hy,ia,id,ie,
      ik,is,iu,ja,jw,ka,kk,kl,km,kn,ko,ks,ku,ky,la,ln,lo,lt,lv,mg,mi,mk,ml,
      mn,mo,mr,ms,mt,my,na,ne,nl,no,oc,om,or,pa,pl,ps,pt,qu,rn,ro,ru,rw,sa,
      sd,sg,sh,si,sk,sl,sm,sn,so,sq,sr,ss,st,su,sv,sw,ta,te,tg,th,ti,tk,tl,
      tn,to,tr,ts,tt,tw,ug,uk,ur,uz,vi,vo,wo,xh,yi,yo,za,zh,zu);

    CountryCode_ISO3166_1 = (CHE,
      ABW,AFG,AGO,AIA,ALA,ALB,AND_,ANT,ARE,ARG,ARM,ASM,ATA,ATF,ATG,AUS,
      AUT,AZE,BDI,BEL,BEN,BFA,BGD,BGR,BHR,BHS,BIH,BLR,BLZ,BMU,BOL,BRA,
      BRB,BRN,BTN,BVT,BWA,CAF,CAN,CCK,CHL,CHN,CIV,CMR,COD,COG,COK,COL,
      COM,CPV,CRI,CUB,CXR,CYM,CYP,CZE,DEU,DJI,DMA,DNK,DOM,DZA,ECU,EGY,
      ERI,ESH,ESP,EST,ETH,FIN,FJI,FLK,FRA,FRO,FSM,GAB,GBR,GEO,GGY,GHA,
      GIB,GIN,GLP,GMB,GNB,GNQ,GRC,GRD,GRL,GTM,GUF,GUM,GUY,HKG,HMD,HND,
      HRV,HTI,HUN,IDN,IMN,IND,IOT,IRL,IRN,IRQ,ISL,ISR,ITA,JAM,JEY,JOR,
      JPN,KAZ,KEN,KGZ,KHM,KIR,KNA,KOR,KWT,LAO,LBN,LBR,LBY,LCA,LIE,LKA,
      LSO,LTU,LUX,LVA,MAC,MAR,MCO,MDA,MDG,MDV,MEX,MHL,MKD,MLI,MLT,MMR,
      MNE,MNG,MNP,MOZ,MRT,MSR,MTQ,MUS,MWI,MYS,MYT,NAM,NCL,NER,NFK,NGA,
      NIC,NIU,NLD,NOR,NPL,NRU,NZL,OMN,PAK,PAN,PCN,PER,PHL,PLW,PNG,POL,
      PRI,PRK,PRT,PRY,PSE,PYF,QAT,REU,ROU,RUS,RWA,SAU,SDN,SEN,SGP,SGS,
      SHN,SJM,SLB,SLE,SLV,SMR,SOM,SPM,SRB,STP,SUR,SVK,SVN,SWE,SWZ,SYC,
      SYR,TCA,TCD,TGO,THA,TJK,TKL,TKM,TLS,TON,TTO,TUN,TUR,TUV,TWN,TZA,
      UGA,UKR,UMI,URY,USA,UZB,VAT,VCT,VEN,VGB,VIR,VNM,VUT,WLF,WSM,YEM,
      ZAF,ZMB,ZWE);

END InternationalCodes_V1.

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
TYPE MODEL Localisation_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED InternationalCodes_V1;

  STRUCTURE LocalisedText =
    Language: LanguageCode_ISO639_1;
    Text: MANDATORY TEXT;
  END LocalisedText;
  
  STRUCTURE LocalisedMText =
    Language: LanguageCode_ISO639_1;
    Text: MANDATORY MTEXT;
  END LocalisedMText;

  STRUCTURE MultilingualText =
    LocalisedText : BAG {1..*} OF LocalisedText;
    UNIQUE (LOCAL) LocalisedText:Language;
  END MultilingualText;  
  
  STRUCTURE MultilingualMText =
    LocalisedText : BAG {1..*} OF LocalisedMText;
    UNIQUE (LOCAL) LocalisedText:Language;
  END MultilingualMText;

END Localisation_V1.

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
TYPE MODEL LocalisationCH_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED InternationalCodes_V1;
  IMPORTS Localisation_V1;

  STRUCTURE LocalisedText EXTENDS Localisation_V1.LocalisedText =
  MANDATORY CONSTRAINT
    Language == #de OR
    Language == #fr OR
    Language == #it OR
    Language == #rm OR
    Language == #en;
  END LocalisedText;
  
  STRUCTURE LocalisedMText EXTENDS Localisation_V1.LocalisedMText =
  MANDATORY CONSTRAINT
    Language == #de OR
    Language == #fr OR
    Language == #it OR
    Language == #rm OR
    Language == #en;
  END LocalisedMText;

  STRUCTURE MultilingualText EXTENDS Localisation_V1.MultilingualText =
    LocalisedText(EXTENDED) : BAG {1..*} OF LocalisedText;
  END MultilingualText;  
  
  STRUCTURE MultilingualMText EXTENDS Localisation_V1.MultilingualMText =
    LocalisedText(EXTENDED) : BAG {1..*} OF LocalisedMText;
  END MultilingualMText;

END LocalisationCH_V1.

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
MODEL Dictionaries_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED InternationalCodes_V1;

  TOPIC Dictionaries (ABSTRACT) =

    STRUCTURE Entry (ABSTRACT) =
      Text: MANDATORY TEXT;
    END Entry;
      
    CLASS Dictionary =
      Language: MANDATORY LanguageCode_ISO639_1;
      Entries: LIST OF Entry;
    END Dictionary;

  END Dictionaries;

END Dictionaries_V1.

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
MODEL DictionariesCH_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED InternationalCodes_V1;
  IMPORTS Dictionaries_V1;

  TOPIC Dictionaries (ABSTRACT) EXTENDS Dictionaries_V1.Dictionaries =

    CLASS Dictionary (EXTENDED) =
    MANDATORY CONSTRAINT
      Language == #de OR
      Language == #fr OR
      Language == #it OR
      Language == #rm OR
      Language == #en;
    END Dictionary;

  END Dictionaries;

END DictionariesCH_V1.

!! ########################################################################
', '2017-05-08 09:45:53.103');
INSERT INTO t_ili2db_model VALUES ('CHBase_Part3_CATALOGUEOBJECTS_20110830.ili', '2.3', 'CatalogueObjects_V1{ INTERLIS} CatalogueObjectTrees_V1{ CatalogueObjects_V1 INTERLIS}', '/* ########################################################################
   CHBASE - BASE MODULES OF THE SWISS FEDERATION FOR MINIMAL GEODATA MODELS
   ======
   BASISMODULE DES BUNDES           MODULES DE BASE DE LA CONFEDERATION
   F�R MINIMALE GEODATENMODELLE     POUR LES MODELES DE GEODONNEES MINIMAUX
   
   PROVIDER: GKG/KOGIS - GCS/COSIG             CONTACT: models@geo.admin.ch
   PUBLISHED: 2011-08-30
   ########################################################################
*/

INTERLIS 2.3;

/* ########################################################################
   ########################################################################
   PART III -- CATALOGUE OBJECTS
   - Package CatalogueObjects
   - Package CatalogueObjectTrees
*/

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
MODEL CatalogueObjects_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED INTERLIS;

  TOPIC Catalogues (ABSTRACT) =

    CLASS Item (ABSTRACT) =
    END Item;

    STRUCTURE CatalogueReference (ABSTRACT) =
      Reference: REFERENCE TO (EXTERNAL) Item;
    END CatalogueReference;
 
    STRUCTURE MandatoryCatalogueReference (ABSTRACT) =
      Reference: MANDATORY REFERENCE TO (EXTERNAL) Item;
    END MandatoryCatalogueReference;

  END Catalogues;

END CatalogueObjects_V1.

!! ########################################################################
!!@technicalContact=models@geo.admin.ch
!!@furtherInformation=http://www.geo.admin.ch/internet/geoportal/de/home/topics/geobasedata/models.html
MODEL CatalogueObjectTrees_V1 (en)
  AT "http://www.geo.admin.ch" VERSION "2011-08-30" =

  IMPORTS UNQUALIFIED INTERLIS;
  IMPORTS CatalogueObjects_V1;

  TOPIC Catalogues (ABSTRACT) EXTENDS CatalogueObjects_V1.Catalogues =

    CLASS Item (ABSTRACT,EXTENDED) = 
      IsSuperItem: MANDATORY BOOLEAN;
      IsUseable: MANDATORY BOOLEAN;
    MANDATORY CONSTRAINT
      IsSuperItem OR IsUseable;
    END Item;

    ASSOCIATION EntriesTree =
      Parent -<#> Item;
      Child -- Item;
    MANDATORY CONSTRAINT
      Parent->IsSuperItem;
    END EntriesTree;

    STRUCTURE CatalogueReference (ABSTRACT,EXTENDED) =
      Reference(EXTENDED): REFERENCE TO (EXTERNAL) Item;
    MANDATORY CONSTRAINT
      Reference->IsUseable;
    END CatalogueReference;
 
    STRUCTURE MandatoryCatalogueReference (ABSTRACT,EXTENDED) =
      Reference(EXTENDED): MANDATORY REFERENCE TO (EXTERNAL) Item;
    MANDATORY CONSTRAINT
      Reference->IsUseable;
    END MandatoryCatalogueReference;

  END Catalogues;

END CatalogueObjectTrees_V1.

!! ########################################################################
', '2017-05-08 09:45:53.103');

INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.uuidDefaultValue', 'uuid_generate_v4()');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsAuthority', 'EPSG');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.BasketHandling', 'readWrite');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.nameOptimization', 'topic');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.catalogueRefTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.multiSurfaceTrafo', 'coalesce');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.defaultSrsCode', '21781');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.maxSqlNameLength', '60');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.sender', 'ili2pg-3.8.1-20170421');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.inheritanceTrafo', 'smart1');
INSERT INTO t_ili2db_settings VALUES ('ch.interlis.ili2c.ilidirs', '%ILI_FROM_DB;%XTF_DIR;http://models.interlis.ch/;%JAR_DIR');
INSERT INTO t_ili2db_settings VALUES ('ch.ehi.ili2db.createForeignKey', 'yes');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects1.TopicA.Katalog_Programm.Programm', 'ch.ehi.ili2db.multilingualTrafo', 'expand');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects1.TopicA.Katalog_ProgrammRef', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects_V1.Catalogues.Item', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects_V1.Catalogues.CatalogueReference', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO t_ili2db_trafo VALUES ('LocalisationCH_V1.MultilingualText', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects1.TopicA.Katalog_Programm', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects1.TopicB.Nutzung', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects1.TopicB.Katalog_OhneUuid', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('Localisation_V1.LocalisedText', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('LocalisationCH_V1.LocalisedText', 'ch.ehi.ili2db.inheritance', 'superClass');
INSERT INTO t_ili2db_trafo VALUES ('Localisation_V1.MultilingualText', 'ch.ehi.ili2db.inheritance', 'newClass');
INSERT INTO t_ili2db_trafo VALUES ('CatalogueObjects_V1.Catalogues.MandatoryCatalogueReference', 'ch.ehi.ili2db.inheritance', 'subClass');
INSERT INTO topica_katalog_programm VALUES (4, 3, '5880375a-52cd-4b2d-af50-c3a6fc5c5352', 'Bio', NULL, 'Bioproduktion', 'Production bio', NULL, 'Agricoltura biologica', NULL);
INSERT INTO topica_katalog_programm VALUES (5, 3, '2d9b613c-ecd1-47b9-b6b3-17116e16ffc7', 'Extenso', NULL, 'Extensoproduktion', 'Production extenso', NULL, 'Produzione extensiva', NULL);
INSERT INTO topicb_katalog_ohneuuid VALUES (9, 8);
INSERT INTO topicb_nutzung VALUES (10, 8);
INSERT INTO topica_katalog_programmref VALUES (11, 8, 0, 4, NULL, 10);
INSERT INTO topica_katalog_programmref VALUES (12, 8, 1, 5, NULL, 10);