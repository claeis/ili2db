package ch.ehi.ili2fgdb;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.itf.impl.jtsext.geom.ArcSegment;
import ch.interlis.iom_j.itf.impl.jtsext.geom.CurvePolygon;
import ch.interlis.iom_j.itf.impl.jtsext.geom.CurveSegment;
import ch.interlis.iom_j.itf.impl.jtsext.geom.JtsextGeometryFactory;
import ch.interlis.iom_j.itf.impl.jtsext.geom.StraightSegment;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.jts.Iox2jtsException;
import ch.interlis.iox_j.jts.Jtsext2iox;
import ch.interlis.iox_j.wkb.WKBConstants;
import ch.interlis.iox_j.wkb.Wkb2iox;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ByteOrderDataInStream;
import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.ByteOrderValues;


public class Fgdb2iox
{
	  private static class Arc {
		  public Arc(int startPointIndex, double centerPoint_x,
				double centerPoint_y, int bits) {
			super();
			this.startPointIndex = startPointIndex;
			this.centerPoint_x = centerPoint_x;
			this.centerPoint_y = centerPoint_y;
			this.bits = bits;
		}
		public int startPointIndex;
		  public double centerPoint_x;
		  public double centerPoint_y;
		  public int bits;
	  }

	@Deprecated
	private int inputDimension = 2;
	private boolean hasZ = false;
	private ByteOrderDataInStream dis = new ByteOrderDataInStream();
	
	@Deprecated
	private double[] ordValues;

  public Fgdb2iox() {
  }


  /**
   * Reads a single {@link Geometry} from a byte array.
   *
   * @param bytes the byte array to read from
   * @return the geometry read
   * @throws ParseException if a parse exception occurs
 * @throws IoxException 
   */
  public IomObject read(byte[] bytes) throws ParseException, IoxException
  {
    // possibly reuse the ByteArrayInStream?
    // don't throw IOExceptions, since we are not doing any I/O
    try {
      return read(new ByteArrayInStream(bytes));
    }
    catch (IOException ex) {
      throw new RuntimeException("Unexpected IOException caught: " + ex.getMessage());
    }
  }

  /**
   * Reads a {@link Geometry} from an {@link InStream).
   *
   * @param is the stream to read from
   * @return the Geometry read
   * @throws IOException
   * @throws ParseException
 * @throws IoxException 
   */
  public IomObject read(InStream is)
  throws IOException, ParseException, IoxException
  {
    dis.setInStream(is);
    IomObject g = readGeometry();
    return g;
  }

  private IomObject readGeometry()
  throws IOException, ParseException,IoxException
  {
    dis.setOrder(ByteOrderValues.LITTLE_ENDIAN);

    int typeInt = dis.readInt();
    int geometryType = typeInt & EsriShpConstants.shapeBasicTypeMask;
    if(geometryType==EsriShpConstants.ShapeNull){
    	return null;
    }
    // determine if Z values are present
	hasZ = geometryType == EsriShpConstants.ShapePointZM
				|| geometryType == EsriShpConstants.ShapePointZ
				|| geometryType == EsriShpConstants.ShapeMultiPointZM
				|| geometryType == EsriShpConstants.ShapeMultiPointZ
				|| geometryType == EsriShpConstants.ShapePolylineZM
				|| geometryType == EsriShpConstants.ShapePolylineZ
				|| geometryType == EsriShpConstants.ShapePolygonZM
				|| geometryType == EsriShpConstants.ShapePolygonZ
				|| (geometryType == EsriShpConstants.ShapeGeneralPoint
							|| geometryType == EsriShpConstants.ShapeGeneralPolyline
							|| geometryType == EsriShpConstants.ShapeGeneralPolygon
							|| geometryType == EsriShpConstants.ShapeGeneralMultiPoint 
							|| geometryType == EsriShpConstants.ShapeGeneralMultiPatch)
						&& ((typeInt & EsriShpConstants.shapeHasZs) != 0);
	boolean hasM = geometryType == EsriShpConstants.ShapePointZM
			|| geometryType == EsriShpConstants.ShapePointM
			|| geometryType == EsriShpConstants.ShapeMultiPointZM
			|| geometryType == EsriShpConstants.ShapeMultiPointM
			|| geometryType == EsriShpConstants.ShapePolylineZM
			|| geometryType == EsriShpConstants.ShapePolylineM
			|| geometryType == EsriShpConstants.ShapePolygonZM
			|| geometryType == EsriShpConstants.ShapePolygonM
			|| (geometryType == EsriShpConstants.ShapeGeneralPoint
						|| geometryType == EsriShpConstants.ShapeGeneralPolyline
						|| geometryType == EsriShpConstants.ShapeGeneralPolygon
						|| geometryType == EsriShpConstants.ShapeGeneralMultiPoint 
						|| geometryType == EsriShpConstants.ShapeGeneralMultiPatch)
					&& ((typeInt & EsriShpConstants.shapeHasMs) != 0);
	boolean hasCurves = (geometryType == EsriShpConstants.ShapeGeneralPolyline
						|| geometryType == EsriShpConstants.ShapeGeneralPolygon)
					&& (typeInt & EsriShpConstants.shapeNonBasicModifierMask) != 0
					|| (typeInt & EsriShpConstants.shapeHasCurves) != 0;
    inputDimension =  hasZ ? 3 : 2;


    
    // only allocate ordValues buffer if necessary
    if (ordValues == null || ordValues.length < inputDimension)
      ordValues = new double[inputDimension];
	if( geometryType == EsriShpConstants.ShapePoint
			|| geometryType == EsriShpConstants.ShapePointZM
			|| geometryType == EsriShpConstants.ShapePointZ
			|| geometryType == EsriShpConstants.ShapeGeneralPoint){
        double x = dis.readDouble();
        double y = dis.readDouble();
		IomObject ret=new ch.interlis.iom_j.Iom_jObject("COORD",null);
		ret.setattrvalue("C1", Double.toString(x));
		ret.setattrvalue("C2", Double.toString(y));
		if(hasZ){
	        double z = dis.readDouble();
			ret.setattrvalue("C3", Double.toString(z));
		}
		return ret;
	}
	if( geometryType == EsriShpConstants.ShapeGeneralMultiPatch){
		throw new IoxException("unexpected geometryType "+geometryType);
	}
	// boundingBox
    double min_x = dis.readDouble();
    double min_y = dis.readDouble();
    double max_x = dis.readDouble();
    double max_y = dis.readDouble();
    
    // cParts
    int cParts=0;
    int partStart[]=null;
	if( geometryType == EsriShpConstants.ShapeMultiPoint
			|| geometryType == EsriShpConstants.ShapeMultiPointZM
			|| geometryType == EsriShpConstants.ShapeMultiPointZ
			|| geometryType == EsriShpConstants.ShapeGeneralMultiPoint){
	}else{
	    cParts=dis.readInt();
	    partStart=new int[cParts];
	}
    
	// cPoints
    int cPoints=dis.readInt();
    
    // parts[cParts]
    if(cParts>0){
    	for(int i=0;i<cParts;i++){
    		partStart[i]=dis.readInt();
    	}
    }
    
	// points[cPoints]
    Coordinate points[]=new Coordinate[cPoints];
	for(int i=0;i<cPoints;i++){
		points[i]=new Coordinate();
		points[i].x=dis.readDouble();
		points[i].y=dis.readDouble();
	}
	
	if( geometryType == EsriShpConstants.ShapeMultiPoint
			|| geometryType == EsriShpConstants.ShapeMultiPointZM
			|| geometryType == EsriShpConstants.ShapeMultiPointZ
			|| geometryType == EsriShpConstants.ShapeGeneralMultiPoint){
		throw new IoxException("unexpected geometryType "+geometryType);
	}else if( geometryType == EsriShpConstants.ShapePolyline
			|| geometryType == EsriShpConstants.ShapePolylineZM
			|| geometryType == EsriShpConstants.ShapePolylineZ
			|| geometryType == EsriShpConstants.ShapeGeneralPolyline){
	}else if( geometryType == EsriShpConstants.ShapePolygon
			|| geometryType == EsriShpConstants.ShapePolygonZM
			|| geometryType == EsriShpConstants.ShapePolygonZ
			|| geometryType == EsriShpConstants.ShapeGeneralPolygon){
	}else{
		throw new IoxException("unexpected geometryType "+geometryType);
	}
	
	if(hasZ){
	    double min_z = dis.readDouble();
	    double max_z = dis.readDouble();
		// Zs[cPoints]
		for(int i=0;i<cPoints;i++){
			points[i].z=dis.readDouble();
		}
	}
	if(hasM){
	    double min_m = dis.readDouble();
	    double max_m = dis.readDouble();
		// Ms[cPoints]
		for(int i=0;i<cPoints;i++){
			dis.readDouble(); // ignore
		}
	}
	java.util.Map<Integer,Arc> arcs=null;
	if(hasCurves){
		int cSegmentModifiers=dis.readInt();
		arcs=new java.util.HashMap<Integer,Arc>();
		for(int i=0;i<cSegmentModifiers;i++){
			int startPointIndex=dis.readInt();
			int segmentType=dis.readInt();
			if(segmentType==EsriShpConstants.segmentArc){
				double v1=dis.readDouble();
				double v2=dis.readDouble();
				int bits=dis.readInt();
				//int skip1=dis.readInt();
				if((bits & EsriShpConstants.arcIsEmpty) != 0){
					// skip it
				}else if((bits & EsriShpConstants.arcIsLine) != 0){
					// straight line, skip it
				}else if((bits & EsriShpConstants.arcIsPoint) != 0){
					throw new IoxException("not supported SegmentArc.Bits "+bits);
				}else if((bits & EsriShpConstants.arcDefinedIP) != 0){
					//throw new IoxException("not supported SegmentArc.Bits "+bits);
				}else{
					if((bits & EsriShpConstants.arcIsCCW) != 0){
						// counterclockwise
					}else{
						// clockwise
					}
				}
				
				//double skip1=dis.readDouble();
				//double skip2=dis.readDouble();
				//double skip3=dis.readDouble();
				arcs.put(startPointIndex, new Arc(startPointIndex, v1, v2, bits));
			}else if(segmentType==EsriShpConstants.segmentLine){
				// will never appear; should be ignored
			}else if(segmentType==EsriShpConstants.segmentSpiral){
			}else if(segmentType==EsriShpConstants.segmentBezier3Curve){
				// two middle control points
				double skip1=dis.readDouble();
				double skip2=dis.readDouble();
				double skip3=dis.readDouble();
				double skip4=dis.readDouble();
			}else if(segmentType==EsriShpConstants.segmentEllipticArc){
				// center
				double skip1=dis.readDouble();
				double skip2=dis.readDouble();
				// rotation or fromV
				double skip3=dis.readDouble();
				// semiMajor
				double skip4=dis.readDouble();
				// minorMajorRatio or deltaV
				double skip5=dis.readDouble();
				// bits
				int skip6=dis.readInt();
			}else if(segmentType==0){
				break;
			}else{
				throw new IoxException("unexpected segmentType "+segmentType);
				//EhiLogger.traceState(("unexpected segmentType "+segmentType));
				//continue;
			}
			
		}
	}
	JtsextGeometryFactory fact=new JtsextGeometryFactory();
	if( geometryType == EsriShpConstants.ShapePolyline
			|| geometryType == EsriShpConstants.ShapePolylineZM
			|| geometryType == EsriShpConstants.ShapePolylineZ
			|| geometryType == EsriShpConstants.ShapeGeneralPolyline){
		if(cParts==1){
			LineString line=getPolyline(fact,0,points,partStart,arcs,false);
			IomObject ret;
			try {
				ret = Jtsext2iox.JTS2polyline(line);
			} catch (Iox2jtsException e) {
				throw new IoxException(e);
			}
			return ret;
		}
		IomObject ret=new Iom_jObject(Wkb2iox.OBJ_MULTIPOLYLINE,null);
		for(int i=0;i<cParts;i++){
			LineString line=getPolyline(fact,i,points,partStart,arcs,false);
			try {
				IomObject lineObj = Jtsext2iox.JTS2polyline(line);
				ret.addattrobj(Wkb2iox.ATTR_POLYLINE, lineObj);
			} catch (Iox2jtsException e) {
				throw new IoxException(e);
			}
		}
		return ret;
	}else if( geometryType == EsriShpConstants.ShapePolygon
			|| geometryType == EsriShpConstants.ShapePolygonZM
			|| geometryType == EsriShpConstants.ShapePolygonZ
			|| geometryType == EsriShpConstants.ShapeGeneralPolygon){
		if(cParts==1){
			LineString line=getPolyline(fact,0,points,partStart,arcs,true);
			if(line.getCoordinateSequence().size()<=3) {
				throw new IoxException("Not a Ring");
			}
			Polygon polygon=fact.createCurvePolygon(fact.createRing(line));
			IomObject ret;
			try {
				ret = Jtsext2iox.JTS2surface(polygon);
			} catch (Iox2jtsException e) {
				throw new IoxException(e);
			}
			return ret;
		}
		ArrayList<LineString> shells=new ArrayList<LineString>();
		ArrayList<LineString> holes=new ArrayList<LineString>();
		for(int i=0;i<cParts;i++){
			LineString line=getPolyline(fact,i,points,partStart,arcs,true);
			if(line.getCoordinateSequence().size()<=3) {
				throw new IoxException("Not a Ring");
			}
			if(CGAlgorithms.isCCW(line.getCoordinates())){
				holes.add(line);
			}else{
				shells.add(line);
			}
		}
		if(shells.size()==0){
			throw new IoxException("polygon without shell");
		}else if(shells.size()==1){
		    LineString shell=shells.get(0);
			return createIoxSurface(fact, shell, holes);
		}
        IomObject iomMultiSurface=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
        for(LineString shell:shells) {
            ArrayList<LineString> holesPerShell=new ArrayList<LineString>();
            for(int holei=holes.size()-1;holei>=0;holei--) {
                LineString hole=holes.get(holei);
                if(shell.contains(hole)) {
                    holesPerShell.add(hole);
                    holes.remove(holei);
                }
            }
            IomObject iomSingleSurface=createIoxSurface(fact, shell, holesPerShell);
            IomObject iomSurface=iomSingleSurface.getattrobj("surface",0);
            iomMultiSurface.addattrobj("surface", iomSurface);
        }
        return iomMultiSurface;
	}else{
		throw new IoxException("unexpected geometryType "+geometryType);
	}
  }


private IomObject createIoxSurface(JtsextGeometryFactory fact, LineString shellx, ArrayList<LineString> holes)
        throws IoxException {
    LinearRing shell=fact.createRing(shellx);
    Polygon polygon = null;
    if(holes.size()==0){
    	polygon=fact.createPolygon(shell);
    }else{
    	LinearRing hole[]=new LinearRing[holes.size()];
    	int i=0;
    	for(LineString line:holes){
    		hole[i]=fact.createRing(line);
    		i++;
    	}
    	polygon=fact.createPolygon(shell,hole);
    }
    IomObject ret=null;
    try {
    	ret = Jtsext2iox.JTS2surface(polygon);
    } catch (Iox2jtsException e) {
    	throw new IoxException(e);
    }
    return ret;
}


  private LineString getPolyline(JtsextGeometryFactory fact,int part, Coordinate[] points, int[] partsStart,
		java.util.Map<Integer,Arc> arcs,boolean closeIt) {
	  int from=partsStart[part];
	  int to=points.length;
	  if(part<partsStart.length-1){
		  to=partsStart[part+1];
	  }
	  // LineString?
	  if(arcs==null || arcs.size()==0){
		  Coordinate coords[]=null;
		  if(closeIt && !points[from].equals2D(points[to-1])) {
			  coords=Arrays.copyOfRange(points, from, to+1);
			  coords[to]=coords[0];
		  }else {
			  coords=Arrays.copyOfRange(points, from, to);
		  }
		  return fact.createLineString(coords);
	  }
	  
	  // ASSERT: CompoundCurve
	ArrayList<CurveSegment> segs=new ArrayList<CurveSegment>();
	Coordinate start=points[from];
	for(int i=from+1;i<to;i++){
		Coordinate end=points[i];
		Arc arc=arcs.get(i-1);
		CurveSegment seg=null;
		if(arc!=null){
			if(((arc.bits&EsriShpConstants.arcDefinedIP)!=0)){
				Coordinate midpt=new Coordinate(arc.centerPoint_x,arc.centerPoint_y);
				seg=new ArcSegment(start,midpt,end);
			}else{
				Coordinate center=new Coordinate(arc.centerPoint_x,arc.centerPoint_y);
				double radius=ArcSegment.dist(start, center);
				double sign=0.0;
				boolean isMinor=((arc.bits&EsriShpConstants.arcIsMinor)!=0);
				if(CGAlgorithms.computeOrientation(start,end,center)<0){
					sign=isMinor?1.0:-1.0;
				}else{
					sign=isMinor?-1.0:1.0;
				}
				Coordinate midpt=ArcSegment.calcArcPt(start, end, center, radius, sign);
				seg=new ArcSegment(start,midpt,end);
			}
		}else{
			seg=new StraightSegment(start,end);
		}
		segs.add(seg);
		start=end;
	}
	  if(closeIt && !points[from].equals2D(points[to-1])) {
			segs.add(new StraightSegment(points[to-1],points[from]));
	}
	return fact.createCompoundCurve(segs);
}

}