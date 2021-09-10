/* This file is part of the iox-ili project.
 * For more information, please see <http://www.eisenhutinformatik.ch/iox-ili/>.
 *
 * Copyright (c) 2006 Eisenhut Informatik AG
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package ch.ehi.ili2fgdb;

import java.util.ArrayList;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.impl.jtsext.geom.ArcSegment;
import ch.interlis.iom_j.itf.impl.jtsext.geom.CompoundCurve;
import ch.interlis.iom_j.itf.impl.jtsext.geom.CompoundCurveRing;
import ch.interlis.iom_j.itf.impl.jtsext.geom.CurveSegment;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.jts.Iox2jts;
import ch.interlis.iox_j.jts.Iox2jtsException;
import ch.interlis.iox_j.jts.Iox2jtsext;
import ch.interlis.iox_j.wkb.ByteArrayOutputStream;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class Iox2fgdb {
	  private int outputDimension = 2;
	  private ByteArrayOutputStream os = null;
	  private static class Arc {
		  public Arc(int startPointIndex, double ip_x,
				double ip_y, int bits) {
			super();
			this.startPointIndex = startPointIndex;
			this.centerPoint_x = ip_x;
			this.centerPoint_y = ip_y;
			this.bits = bits;
		}
		public int startPointIndex;
		  public double centerPoint_x;
		  public double centerPoint_y;
		  public int bits;
	  }
	// utility, no instances
	private Iox2fgdb(){}
	  public Iox2fgdb(int outputDimension) {
		    this(outputDimension, java.nio.ByteOrder.LITTLE_ENDIAN);
	  }
	  public Iox2fgdb(int outputDimension, java.nio.ByteOrder byteOrder) {
		    this.outputDimension = outputDimension;
		    os = new ByteArrayOutputStream(byteOrder);
		    
		    if (outputDimension < 2 || outputDimension > 3)
		      throw new IllegalArgumentException("Output dimension must be 2 or 3");
		  }
	
	public byte[] coord2wkb(IomObject obj,int srsId) 
	throws Iox2wkbException 
	{
		if(obj==null){
			return null;
		}
    	os.reset();
    	os.writeInt(EsriShpConstants.ShapePoint);
		writeCoord(obj);
		return os.toByteArray();
	}
	public byte[] polyline2wkb(IomObject polylineObj,boolean isSurfaceOrArea,boolean asCompoundCurve,double p,int srsId)
	throws IoxException, Iox2jtsException
	{
		if(polylineObj==null){
			return null;
		}
		byte ret[]=null;
        LineString polyline = null;
		os.reset();
		if(asCompoundCurve){
	        polyline = Iox2jtsext.polyline2JTS(polylineObj,false, p);
			int shapeType=EsriShpConstants.ShapeGeneralPolyline;
			shapeType |= EsriShpConstants.shapeHasCurves;
			shapeType |= (outputDimension==3?EsriShpConstants.shapeHasZs:0);
			os.writeInt(shapeType);
		}else{
	        polyline = Iox2jts.polyline2JTSlineString(polylineObj,false, p);
			if(outputDimension==3){
				os.writeInt(EsriShpConstants.ShapePolylineZ);
			}else{
				os.writeInt(EsriShpConstants.ShapePolyline);
			}
		}
		
		// boundingBox
		Envelope env = polyline.getEnvelopeInternal();
		os.writeDouble(env.getMinX());
		os.writeDouble(env.getMinY());
		os.writeDouble(env.getMaxX());
		os.writeDouble(env.getMaxY());

		// cParts The number of lines in the multiline.
		// cPoints The total number of points for all parts.
		int cPart=1;
		int cPoints=getNumPoints(polyline);
		os.writeInt(cPart);
		os.writeInt(cPoints);
		
		int partStart=0;
		os.writeInt(partStart);
		
		java.util.ArrayList<Arc> arcs=null;
		if(asCompoundCurve){
			arcs=new java.util.ArrayList<Arc>(); 
		}
		java.util.ArrayList<Double> zv=null;
		if(outputDimension==3){
			zv=new java.util.ArrayList<Double>();
		}
		
		double zMin[]=new double[1];
		double zMax[]=new double[1];
		{
			Coordinate coord=polyline.getStartPoint().getCoordinate();
			if(outputDimension==3){
				zMin[0]=coord.z;
				zMax[0]=coord.z;
			}
		}

		int startPtIdx=0;
		writePoints(polyline, false,zv, zMin, zMax,startPtIdx,arcs);
		
		if(outputDimension==3){
			// zMin
			os.writeDouble(zMin[0]);
			// zMax
			os.writeDouble(zMax[0]);
			// Zs[cPoints]
			for(Double z:zv){
				os.writeDouble(z);
			}
		}
		if(asCompoundCurve){
			
			writeArcs(arcs);				
		}
		ret=os.toByteArray();
		return ret;
	}
	
	public byte[] multiline2wkb(IomObject obj,boolean asCompoundCurve,double p,int srsId)
	throws IoxException, Iox2jtsException
	{
		if(obj==null){
			return null;
		}
	    	os.reset();
			int polylinec=obj.getattrvaluecount(Wkb2iox.ATTR_POLYLINE);
			if(asCompoundCurve){
				int shapeType=EsriShpConstants.ShapeGeneralPolyline;
				shapeType |= EsriShpConstants.shapeHasCurves;
				shapeType |= (outputDimension==3?EsriShpConstants.shapeHasZs:0);
				os.writeInt(shapeType);
			}else{
				if(outputDimension==3){
					os.writeInt(EsriShpConstants.ShapePolylineZ);
				}else{
					os.writeInt(EsriShpConstants.ShapePolyline);
				}
			}
			java.util.ArrayList<LineString> curves=new java.util.ArrayList<LineString>(); 
			
			// boundingBox
	    	Envelope env=new Envelope();
			for(int polylinei=0;polylinei<polylinec;polylinei++){
				IomObject polyline=obj.getattrobj(Wkb2iox.ATTR_POLYLINE,polylinei);
                LineString curve = null;
				if(asCompoundCurve) {
	                curve = Iox2jtsext.polyline2JTS(polyline,false, p);
				}else {
	                curve = Iox2jts.polyline2JTSlineString(polyline,false, p);
				}
		    	curves.add(curve);
		    	env.expandToInclude(curve.getEnvelopeInternal());
	    	}
			os.writeDouble(env.getMinX());
			os.writeDouble(env.getMinY());
			os.writeDouble(env.getMaxX());
			os.writeDouble(env.getMaxY());

			// cParts The number of Lines in the Multiline.
			// cPoints The total number of points for all lines.
			int cPart=curves.size();
			int cPoints=0;
			for(LineString curve:curves){
				cPoints+=getNumPoints(curve);
			}
			os.writeInt(cPart);
			os.writeInt(cPoints);
			
			// parts[cParts] An array of length NumParts. Stores, for each Line, the index of its
			// first point in the points array. Array indexes are with respect to 0.
			int partStart=0;
			for(LineString curve:curves){
				os.writeInt(partStart);
				partStart+=getNumPoints(curve);
			}
			
			java.util.ArrayList<Arc> arcs=null;
			if(asCompoundCurve){
				arcs=new java.util.ArrayList<Arc>(); 
			}
			java.util.ArrayList<Double> zv=null;
			if(outputDimension==3){
				zv=new java.util.ArrayList<Double>();
			}
			
			double zMin[]=new double[1];
			double zMax[]=new double[1];
			{
				Coordinate coord=curves.get(0).getStartPoint().getCoordinate();
				if(outputDimension==3){
					zMin[0]=coord.z;
					zMax[0]=coord.z;
				}
			}


			int startPtIdx=0;
			for(LineString curve:curves){
				writePoints(curve, true,zv, zMin, zMax,startPtIdx,arcs);
				startPtIdx+=getNumPoints(curve);
			}
			
			
			if(outputDimension==3){
				// zMin
				os.writeDouble(zMin[0]);
				// zMax
				os.writeDouble(zMax[0]);
				// Zs[cPoints]
				for(Double z:zv){
					os.writeDouble(z);
				}
			}
			if(asCompoundCurve){
				writeArcs(arcs);				
			}

		return os.toByteArray();
	}
	private void writeArcs(java.util.ArrayList<Arc> arcs) {
		// cSegmentModifiers
		os.writeInt(arcs.size());
		
		// curvedSegments[cSegmentModifier]
		for(Arc arc:arcs){
			os.writeInt(arc.startPointIndex);
			os.writeInt(EsriShpConstants.segmentArc);
			os.writeDouble(arc.centerPoint_x);
			os.writeDouble(arc.centerPoint_y);
			os.writeInt(arc.bits); 
		}
	}
	/** Converts a SURFACE to a JTS Polygon.
	 * @param obj INTERLIS SURFACE structure
	 * @param strokeP maximum stroke to use when removing ARCs
	 * @return JTS Polygon
	 * @throws Iox2jtsException 
	 * @throws Iox2wkbException
	 */
	public byte[] surface2wkb(IomObject polygonObj,boolean asCurvePolygon,double strokeP,int srsId) //SurfaceOrAreaType type)
	throws IoxException, Iox2jtsException
	{
		if(polygonObj==null){
			return null;
		}
		byte ret[]=null;
        Polygon polygon = null;
		os.reset();
		if(asCurvePolygon){
	        polygon=Iox2jtsext.surface2JTS(polygonObj,strokeP);
			int shapeType=EsriShpConstants.ShapeGeneralPolygon;
			shapeType |= EsriShpConstants.shapeHasCurves;
			shapeType |= (outputDimension==3?EsriShpConstants.shapeHasZs:0);
			os.writeInt(shapeType);
		}else{
	        polygon=Iox2jts.surface2JTS(polygonObj,strokeP);
			if(outputDimension==3){
				os.writeInt(EsriShpConstants.ShapePolygonZ);
			}else{
				os.writeInt(EsriShpConstants.ShapePolygon);
			}
		}
		
		// boundingBox
		Envelope env = polygon.getEnvelopeInternal();
		os.writeDouble(env.getMinX());
		os.writeDouble(env.getMinY());
		os.writeDouble(env.getMaxX());
		os.writeDouble(env.getMaxY());

		// cParts The number of rings in the polygon.
		// cPoints The total number of points for all parts.
		int cPart=1;
		int cPoints=getNumPoints(polygon.getExteriorRing());
		int holec=polygon.getNumInteriorRing();
		cPart+=holec;
		for(int holei=0;holei<holec;holei++){
			cPoints+=getNumPoints(polygon.getInteriorRingN(holei));
		}
		os.writeInt(cPart);
		os.writeInt(cPoints);
		
		// parts[cParts] An array of length NumParts. Stores, for each Ring, the index of its
		// first point in the points array. Array indexes are with respect to 0.
		int partStart=0;
		os.writeInt(partStart);
		partStart+=getNumPoints(polygon.getExteriorRing());
		for(int holei=0;holei<holec;holei++){
			os.writeInt(partStart);
			partStart+=getNumPoints(polygon.getInteriorRingN(holei));
		}
		
		java.util.ArrayList<Arc> arcs=null;
		if(asCurvePolygon){
			arcs=new java.util.ArrayList<Arc>(); 
		}
		java.util.ArrayList<Double> zv=null;
		if(outputDimension==3){
			zv=new java.util.ArrayList<Double>();
		}
		
		double zMin[]=new double[1];
		double zMax[]=new double[1];
		{
			Coordinate coord=polygon.getExteriorRing().getStartPoint().getCoordinate();
			if(outputDimension==3){
				zMin[0]=coord.z;
				zMax[0]=coord.z;
			}
		}

		// shell is always in clockwise order
		// holes are in a counterclockwise direction 
		LineString polyline=polygon.getExteriorRing();
		int startPtIdx=0;
		polyline=asOneLine(polyline);
		if(CGAlgorithms.isCCW(polyline.getCoordinates())){
			polyline=(LineString)polyline.reverse();
		}
		writePoints(polyline, false,zv, zMin, zMax,startPtIdx,arcs);
		startPtIdx+=getNumPoints(polyline);
		for(int holei=0;holei<holec;holei++){
			polyline=polygon.getInteriorRingN(holei);
			polyline=asOneLine(polyline);
			if(!CGAlgorithms.isCCW(polyline.getCoordinates())){
				polyline=(LineString)polyline.reverse();
			}
			writePoints(polyline, true,zv, zMin, zMax,startPtIdx,arcs);
			startPtIdx+=getNumPoints(polyline);
		}
		
		
		if(outputDimension==3){
			// zMin
			os.writeDouble(zMin[0]);
			// zMax
			os.writeDouble(zMax[0]);
			// Zs[cPoints]
			for(Double z:zv){
				os.writeDouble(z);
			}
		}
		if(asCurvePolygon){
			writeArcs(arcs);				
		}
		ret=os.toByteArray();
		return ret;
	}
	private LineString asOneLine(LineString polyline) {
		if(!(polyline instanceof CompoundCurveRing)){
			return polyline;
		}
		ArrayList<CurveSegment> segments=new ArrayList<CurveSegment>();
		for(CompoundCurve curve:((CompoundCurveRing) polyline).getLines()){
			segments.addAll(curve.getSegments());
		}
		CompoundCurve line=new CompoundCurve(segments,polyline.getFactory());
		return line;
	}
	private int getNumPoints(LineString line) {
		if(line instanceof CompoundCurve){
			return ((CompoundCurve) line).getNumSegments()+1;
		}else if(line instanceof CompoundCurveRing){
			int ptc=1;
			for(CompoundCurve curve:((CompoundCurveRing) line).getLines()){
				ptc+=curve.getNumSegments();
			}
			return ptc;
		}
		return line.getNumPoints();
	}
	private void writePoints(LineString line,boolean isCCW,
			java.util.ArrayList<Double> zv, double[] zMin, double[] zMax,int startPtIdx,java.util.List<Arc> arcs) {
		if(line instanceof CompoundCurve){
			CompoundCurve polyline=(CompoundCurve)line;
			// start point
			{
				CurveSegment seg = polyline.getSegments().get(0);
				Coordinate coord=seg.getStartPoint();
				os.writeDouble(coord.x);
				os.writeDouble(coord.y);
				if(zv!=null){
					double z=coord.z;
					zv.add(z);
					if(z<zMin[0]){
						zMin[0]=z;
					}
					if(z>zMax[0]){
						zMax[0]=z;
					}
				}
			}

			int arcSegc=0;
			for(int i=0;i<polyline.getNumSegments();i++){
				CurveSegment seg = polyline.getSegments().get(i);
				if(seg instanceof ArcSegment){
					arcSegc++;
					ArcSegment arcSeg=(ArcSegment)seg;
					Coordinate ip=arcSeg.getMidPoint();
					int flags=0;
					//flags|=arcSeg.getSign()>0?0:EsriShpConstants.arcIsCCW;
					//flags|=EsriShpConstants.arcIsMinor; // arcSeg.getTheta()>Math.PI?0:EsriShpConstants.arcIsMinor;
					flags|=EsriShpConstants.arcDefinedIP;
					Arc arc=new Arc(startPtIdx+i,ip.x,ip.y,flags);
					arcs.add(arc);
				}
				Coordinate coord=seg.getEndPoint();
				os.writeDouble(coord.x);
				os.writeDouble(coord.y);
				if(zv!=null){
					double z=coord.z;
					zv.add(z);
					if(z<zMin[0]){
						zMin[0]=z;
					}
					if(z>zMax[0]){
						zMax[0]=z;
					}
				}
			}
			return;
		}else{
			for(Coordinate coord:line.getCoordinates()){
				os.writeDouble(coord.x);
				os.writeDouble(coord.y);
				if(zv!=null){
					double z=coord.z;
					zv.add(z);
					if(z<zMin[0]){
						zMin[0]=z;
					}
					if(z>zMax[0]){
						zMax[0]=z;
					}
				}
			}
			return;
		}
	}
	public byte[] multisurface2wkb(IomObject multisurfaceObj,boolean asCurvePolygon,double strokeP,int srsId) //SurfaceOrAreaType type)
	throws IoxException, Iox2jtsException
	{
		if(multisurfaceObj==null){
			return null;
		}
		byte ret[]=null;
		os.reset();
		if(asCurvePolygon){
			int shapeType=EsriShpConstants.ShapeGeneralPolygon;
			shapeType |= EsriShpConstants.shapeHasCurves;
			shapeType |= (outputDimension==3?EsriShpConstants.shapeHasZs:0);
			os.writeInt(shapeType);
		}else{
			if(outputDimension==3){
				os.writeInt(EsriShpConstants.ShapePolygonZ);
			}else{
				os.writeInt(EsriShpConstants.ShapePolygon);
			}
		}
		java.util.ArrayList<Polygon> polygons=new java.util.ArrayList<Polygon>(); 

		
		// boundingBox
    	Envelope env=new Envelope();
		int surfacec=multisurfaceObj.getattrvaluecount("surface");
		for(int surfacei=0;surfacei<surfacec;surfacei++){
			IomObject surface=multisurfaceObj.getattrobj("surface",surfacei);
			IomObject iomSurfaceClone=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
			iomSurfaceClone.addattrobj("surface",surface);
            Polygon polygon = null;
			if(asCurvePolygon) {
	            polygon = Iox2jtsext.surface2JTS(iomSurfaceClone,strokeP);
			}else {
	            polygon = Iox2jts.surface2JTS(iomSurfaceClone,strokeP);
			}
	    	polygons.add(polygon);
	    	env.expandToInclude(polygon.getEnvelopeInternal());
		}
		os.writeDouble(env.getMinX());
		os.writeDouble(env.getMinY());
		os.writeDouble(env.getMaxX());
		os.writeDouble(env.getMaxY());

		// cParts The number of rings in the multisurface.
		// cPoints The total number of points for all parts.
		int cPart=0;
		int cPoints=0;
		for(Polygon polygon:polygons){
			cPart+=1;
			cPoints+=getNumPoints(polygon.getExteriorRing());
			int holec=polygon.getNumInteriorRing();
			cPart+=holec;
			for(int holei=0;holei<holec;holei++){
				cPoints+=getNumPoints(polygon.getInteriorRingN(holei));
			}
		}
		os.writeInt(cPart);
		os.writeInt(cPoints);
		
		// parts[cParts] An array of length NumParts. Stores, for each Ring, the index of its
		// first point in the points array. Array indexes are with respect to 0.
		int partStart=0;
		for(Polygon polygon:polygons){
			os.writeInt(partStart);
			partStart+=getNumPoints(polygon.getExteriorRing());
			int holec=polygon.getNumInteriorRing();
			for(int holei=0;holei<holec;holei++){
				os.writeInt(partStart);
				partStart+=getNumPoints(polygon.getInteriorRingN(holei));
			}
		}
		
		java.util.ArrayList<Arc> arcs=null;
		if(asCurvePolygon){
			arcs=new java.util.ArrayList<Arc>(); 
		}
		java.util.ArrayList<Double> zv=null;
		if(outputDimension==3){
			zv=new java.util.ArrayList<Double>();
		}
		
		double zMin[]=new double[1];
		double zMax[]=new double[1];
		{
			Coordinate coord=polygons.get(0).getExteriorRing().getStartPoint().getCoordinate();
			if(outputDimension==3){
				zMin[0]=coord.z;
				zMax[0]=coord.z;
			}
		}

		int startPtIdx=0;
		for(Polygon polygon:polygons){
			// shell is always in clockwise order
			// holes are in a counterclockwise direction 
			LineString polyline=polygon.getExteriorRing();
			polyline=asOneLine(polyline);
			if(CGAlgorithms.isCCW(polyline.getCoordinates())){
				polyline=(LineString)polyline.reverse();
			}
			writePoints(polyline, false,zv, zMin, zMax,startPtIdx,arcs);
			startPtIdx+=getNumPoints(polyline);
			int holec=polygon.getNumInteriorRing();
			for(int holei=0;holei<holec;holei++){
				polyline=polygon.getInteriorRingN(holei);
				polyline=asOneLine(polyline);
				if(!CGAlgorithms.isCCW(polyline.getCoordinates())){
					polyline=(LineString)polyline.reverse();
				}
				writePoints(polyline, true,zv, zMin, zMax,startPtIdx,arcs);
				startPtIdx+=getNumPoints(polyline);
			}
		}
		
		
		if(outputDimension==3){
			// zMin
			os.writeDouble(zMin[0]);
			// zMax
			os.writeDouble(zMax[0]);
			// Zs[cPoints]
			for(Double z:zv){
				os.writeDouble(z);
			}
		}
		if(asCurvePolygon){
			
			writeArcs(arcs);				
		}
		ret=os.toByteArray();
		return ret;
	}
	private void writeCoord(IomObject value) throws Iox2wkbException {
		String c1=value.getattrvalue("C1");
		String c2=value.getattrvalue("C2");
		String c3=value.getattrvalue("C3");
		double xCoord;
		try{
			xCoord = Double.parseDouble(c1);
		}catch(Exception ex){
			throw new Iox2wkbException("failed to read C1 <"+c1+">",ex);
		}
		double yCoord;
		try{
			yCoord = Double.parseDouble(c2);
		}catch(Exception ex){
			throw new Iox2wkbException("failed to read C2 <"+c2+">",ex);
		}
		double zCoord=0.0;
		if(outputDimension==3){
			if(c3!=null){
				try{
					zCoord = Double.parseDouble(c3);
				}catch(Exception ex){
					throw new Iox2wkbException("failed to read C3 <"+c3+">",ex);
				}
			}else{
				throw new Iox2wkbException("missing C3");
			}
		}
		writeCoord(xCoord,yCoord,zCoord);
	}
	  private void writeCoord(double xCoord,double yCoord,double zCoord)
	  {
		    os.writeDouble(xCoord);
		    os.writeDouble(yCoord);
		    if(outputDimension==3){
			    os.writeDouble(zCoord);
		    }
	  }

}
