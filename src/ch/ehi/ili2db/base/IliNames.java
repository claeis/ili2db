package ch.ehi.ili2db.base;

public class IliNames {

	
	public static final String CHBASE1_GEOMETRYCHLV03 = "GeometryCHLV03_V1";
	public static final String CHBASE1_GEOMETRYCHLV95 = "GeometryCHLV95_V1";
	public static final String CHBASE1_GEOMETRY_LINESTRUCTURE = "LineStructure";
	public static final String CHBASE1_GEOMETRY_DIRECTEDLINESTRUCTURE = "DirectedLineStructure";
	public static final String CHBASE1_GEOMETRY_LINESTRUCTURE_LINE = "Line";
	public static final String CHBASE1_GEOMETRY_MULTILINE = "MultiLine";
	public static final String CHBASE1_GEOMETRY_MULTIDIRECTEDLINE = "MultiDirectedLine";
	public static final String CHBASE1_GEOMETRY_MULTILINE_LINES = "Lines";
	public static final String CHBASE1_GEOMETRY_SURFACESTRUCTURE = "SurfaceStructure";
	public static final String CHBASE1_GEOMETRY_SURFACESTRUCTURE_SURFACE = "Surface";
	public static final String CHBASE1_GEOMETRY_MULTISURFACE = "MultiSurface";
	public static final String CHBASE1_GEOMETRY_MULTISURFACE_SURFACES = "Surfaces";
	public static final String CHBASE1_CATALOGUEOBJECTS = "CatalogueObjects_V1"; // MODEL
	public static final String CHBASE1_CATALOGUEOBJECTS_CATALOGUES = CHBASE1_CATALOGUEOBJECTS+".Catalogues"; // TOPIC
	public static final String CHBASE1_CATALOGUEREFERENCE = "CatalogueReference"; // STRUCTURE
	public static final String CHBASE1_MANDATORYCATALOGUEREFERENCE = "MandatoryCatalogueReference"; // STRUCTURE
	public static final String CHBASE1_CATALOGUEREFERENCE_REFERENCE ="Reference"; // Attribute
	public static final String CHBASE1_ITEM = "Item"; // CLASS
	public static final String CHBASE1_CATALOGUES_ITEM = CHBASE1_CATALOGUEOBJECTS_CATALOGUES+"."+CHBASE1_ITEM; // CLASS
	public static final String CHBASE1_LOCALISATIONCH = "LocalisationCH_V1";
    public static final String CHBASE1_LOCALISEDMTEXT = "LocalisedMText";
    public static final String CHBASE1_LOCALISEDTEXT = "LocalisedText";
	public static final String CHBASE1_MULTILINGUALMTEXT = "MultilingualMText";
	public static final String CHBASE1_MULTILINGUALTEXT = "MultilingualText";
	public static final String CHBASE1_MULTILINFUALTEXT_LOCALISEDTEXT = "LocalisedText";
	public static final String CHBASE1_LOCALISEDTEXT_TEXT = "Text";
	public static final String CHBASE1_LOCALISEDTEXT_LANGUAGE = "Language";
	public static final String CHBASE1_LOCALISEDTEXT_LANG_DE = "de";
	public static final String CHBASE1_LOCALISEDTEXT_LANG_FR = "fr";
	public static final String CHBASE1_LOCALISEDTEXT_LANG_IT = "it";
	public static final String CHBASE1_LOCALISEDTEXT_LANG_RM = "rm";
	public static final String CHBASE1_LOCALISEDTEXT_LANG_EN = "en";
	public static final String CHBASE2_LOCALISATIONCH = "LocalisationCH_V2";
	public static final String CHBASE2_LOCALISEDURI = "LocalisedUri";
	public static final String CHBASE2_MULTILINGUALURI = "MultilingualUri";

	private IliNames() {
	};

}
