package ch.ehi.ili2fgdb;

/**
 * Constant values used by the ESRI Extended Shape Buffer format
 */
public interface EsriShpConstants {
	public static final int ShapeNull = 0;
	public static final int ShapePoint = 1;
	public static final int ShapePointM = 21;
	public static final int ShapePointZM = 11;
	public static final int ShapePointZ = 9;
	public static final int ShapeMultiPoint = 8;
	public static final int ShapeMultiPointM = 28;
	public static final int ShapeMultiPointZM = 18;
	public static final int ShapeMultiPointZ = 20;
	public static final int ShapePolyline = 3;
	public static final int ShapePolylineM = 23;
	public static final int ShapePolylineZM = 13;
	public static final int ShapePolylineZ = 10;
	public static final int ShapePolygon = 5;
	public static final int ShapePolygonM = 25;
	public static final int ShapePolygonZM = 15;
	public static final int ShapePolygonZ = 19;
	public static final int ShapeMultiPatchM = 31;
	public static final int ShapeMultiPatch = 32;
	public static final int ShapeGeneralPolyline = 50;
	public static final int ShapeGeneralPolygon = 51;
	public static final int ShapeGeneralPoint = 52;
	public static final int ShapeGeneralMultiPoint = 53;
	public static final int ShapeGeneralMultiPatch = 54;

	public static final int   shapeHasZs=((-2147483647-1));
	public static final int   shapeHasMs=(1073741824);
	public static final int   shapeHasCurves=(536870912);
	public static final int   shapeHasIDs=(268435456);
	public static final int   shapeHasNormals=(134217728);
	public static final int   shapeHasTextures=(67108864);
	public static final int   shapeHasPartIDs=(33554432);
	public static final int   shapeHasMaterials=(16777216);
	public static final int   shapeIsCompressed=(8388608);
	public static final int   shapeModifierMask=(-16777216);
	public static final int   shapeMultiPatchModifierMask=(15728640);
	public static final int   shapeBasicTypeMask=(255);
	public static final int   shapeBasicModifierMask=(-1073741824);
	public static final int   shapeNonBasicModifierMask=(1056964608);
	public static final int   shapeExtendedModifierMask=(-587202560);

	public static final int  segmentArc=1;
	public static final int  segmentLine=2;
	public static final int  segmentSpiral=3;
	public static final int  segmentBezier3Curve=4;
	public static final int  segmentEllipticArc=5;
	
	public static final int arcIsEmpty=1;
	public static final int arcReserved1=2;
	public static final int arcReserved2=4;
	public static final int arcIsCCW=8;
	public static final int arcIsMinor=16;
	public static final int arcIsLine=32;
	public static final int arcIsPoint=64;
	public static final int arcDefinedIP=128;

}
