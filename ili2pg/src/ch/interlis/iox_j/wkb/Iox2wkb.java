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
package ch.interlis.iox_j.wkb;

import java.io.IOException;
import java.util.Iterator;



import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.iom.IomConstants;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.jts.Iox2jtsException;

/** Utility to convert from INTERLIS to JTS geometry types.
 * @author ceis
 * 
 * 
<point binary representation> ::=
	<byte order> <wkbpoint> [ <wkbpoint binary> ]

<linestring binary representation> ::=
	<byte order> <wkblinestring> [ <num> <wkbpoint binary>... ]

<circularstring binary representation> ::=
	<byte order> <wkbcircularstring> [ <num> <wkbpoint binary>... ]

<compoundcurve binary representation> ::=
	<byte order> <wkbcompoundcurve> [ <num> <wkbcurve binary>... ]

<curvepolygon binary representation> ::=
	<byte order> <wkbcurvepolygon> [ <num> <wkbring binary>... ]
	| <polygon binary representation>
	
<polygon binary representation> ::=
	<byte order> <wkbpolygon> [ <num> <wkblinearring binary>... ]
	| <triangle binary representation>

<triangle binary representation> ::=
	<byte order> <wkbtriangle>
	[ <wkbpoint binary> <wkbpoint binary> <wkbpoint binary> ]	

<wkbring binary> ::=
	<linestring binary representation>
	| <circularstring binary representation>
	| <compoundcurve binary representation>

<wkblinearring> ::= <num> <wkbpoint binary>...

<wkbcurve binary> ::=
	<linestring binary representation>
	| <circularstring binary representation>

<wkbpoint binary> ::= <wkbx> <wkby>	
 */
public class Iox2wkb {
	  private int outputDimension = 2;
	  private ByteArrayOutputStream os = null;
	// utility, no instances
	private Iox2wkb(){}
	  public Iox2wkb(int outputDimension) {
		    this(outputDimension, java.nio.ByteOrder.BIG_ENDIAN);
	  }
	  public Iox2wkb(int outputDimension, java.nio.ByteOrder byteOrder) {
		    this.outputDimension = outputDimension;
		    os = new ByteArrayOutputStream(byteOrder);
		    
		    if (outputDimension < 2 || outputDimension > 3)
		      throw new IllegalArgumentException("Output dimension must be 2 or 3");
		  }
	private static double sqr(double x)
	{
		return x*x;
	}
	private static double dist(double re1,double ho1,double re2,double ho2)
	{
		double ret;
		ret=Math.sqrt(sqr(re2-re1)+sqr(ho2-ho1));
		return ret;
	}
	  public static String bytesToHex(byte[] bytes)
	  {
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < bytes.length; i++) {
	      byte b = bytes[i];
	      buf.append(toHexDigit((b >> 4) & 0x0F));
	      buf.append(toHexDigit(b & 0x0F));
	    }
	    return buf.toString();
	  }

	  private static char toHexDigit(int n)
	  {
	    if (n < 0 || n > 15)
	      throw new IllegalArgumentException("Nibble value out of range: " + n);
	    if (n <= 9)
	      return (char) ('0' + n);
	    return (char) ('A' + (n - 10));
	  }
	
	/** Converts a COORD to a JTS Coordinate.
	 * @param value INTERLIS COORD structure.
	 * @return JTS Coordinate.
	 * @throws Iox2wkbException
	 */
	public byte[] coord2wkb(IomObject value) 
	throws Iox2wkbException 
	{
		if(value==null){
			return null;
		}
	    try {
	        os.reset();
	        writeByteOrder();
	        writeGeometryType(WKBConstants.wkbPoint);
			writeCoord(value);
	      }
	      catch (IOException ex) {
	        throw new RuntimeException("Unexpected IO exception: " + ex.getMessage());
	      }
	      return os.toByteArray();
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
	private static void arc2JTS(com.vividsolutions.jts.geom.CoordinateList ret,IomObject value,double p)
	throws Iox2wkbException
	{
		if(value!=null){
			String c1=value.getattrvalue("C1");
			String c2=value.getattrvalue("C2");
			String c3=value.getattrvalue("C3");
			String a1=value.getattrvalue("A1");
			String a2=value.getattrvalue("A2");
			double pt2_re;
			try{
				pt2_re = Double.parseDouble(c1);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C1 <"+c1+">",ex);
			}
			double pt2_ho;
			try{
				pt2_ho = Double.parseDouble(c2);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C2 <"+c2+">",ex);
			}
			double arcPt_re;
			try{
				arcPt_re = Double.parseDouble(a1);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read A1 <"+a1+">",ex);
			}
			double arcPt_ho;
			try{
				arcPt_ho = Double.parseDouble(a2);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read A2 <"+a2+">",ex);
			}
			if(p==0.0){
				ret.add(new com.vividsolutions.jts.geom.Coordinate(arcPt_re, arcPt_ho));
				ret.add(new com.vividsolutions.jts.geom.Coordinate(pt2_re, pt2_ho));
				return;
			}
			int lastCoord=ret.size();
			com.vividsolutions.jts.geom.Coordinate p1=null;
			p1=ret.getCoordinate(lastCoord-1);
			double pt1_re=p1.x;
			double pt1_ho=p1.y;
			//EhiLogger.debug("pt1 "+pt1_re+", "+pt1_ho);
			//EhiLogger.debug("arc "+arcPt_re+", "+arcPt_ho);
			//EhiLogger.debug("pt2 "+pt2_re+", "+pt2_ho);
			/*
			if(c3==null){
				ret.setDimension(IFMEFeature.FME_TWO_D);
				ret.add2DCoordinate(p2_x, p2_y);
			}else{
				double zCoord = Double.parseDouble(c3);
				ret.setDimension(IFMEFeature.FME_THREE_D);
				ret.add3DCoordinate(p2_x, p2_y, zCoord);
			}
			*/
			// letzter Punkt ein Bogenzwischenpunkt?
		
			// Zwischenpunkte erzeugen

			// Distanz zwischen Bogenanfanspunkt und Zwischenpunkt
			double a=dist(pt1_re,pt1_ho,arcPt_re,arcPt_ho);
			// Distanz zwischen Zwischenpunkt und Bogenendpunkt 
			double b=dist(arcPt_re,arcPt_ho,pt2_re,pt2_ho);

			// Zwischenpunkte erzeugen, so dass maximale Pfeilhöhe nicht 
			// überschritten wird
			// Distanz zwischen Bogenanfanspunkt und Bogenendpunkt 
			double c=dist(pt1_re,pt1_ho,pt2_re,pt2_ho);
			// Radius bestimmen
			double s=(a+b+c)/2.0;
			double ds=Math.atan2(pt2_re-arcPt_re,pt2_ho-arcPt_ho)-Math.atan2(pt1_re-arcPt_re,pt1_ho-arcPt_ho);
			double rSign=(Math.sin(ds)>0.0)?-1.0:1.0;
			double r=a*b*c/4.0/Math.sqrt(s*(s-a)*(s-b)*(s-c))*rSign;
			// Kreismittelpunkt
			double thetaM=Math.atan2(arcPt_re-pt1_re,arcPt_ho-pt1_ho)+Math.acos(a/2.0/r);
			double reM=pt1_re+r*Math.sin(thetaM);
			double hoM=pt1_ho+r*Math.cos(thetaM);

			// mindest Winkelschrittweite
			double theta=2*Math.acos(1-p/Math.abs(r));

			if(a>2*p){
				// Zentriwinkel zwischen pt1 und arcPt
				double alpha=2.0*Math.asin(a/2.0/Math.abs(r));
				// anzahl Schritte
				int alphan=(int)Math.ceil(alpha/theta);
				// Winkelschrittweite
				double alphai=alpha/(alphan*(r>0.0?1:-1));
				double ri=Math.atan2(pt1_re-reM,pt1_ho-hoM);
				for(int i=1;i<alphan;i++){
					ri += alphai;
					double pti_re=reM + Math.abs(r) * Math.sin(ri);
					double pti_ho=hoM + Math.abs(r) * Math.cos(ri);
					ret.add(new com.vividsolutions.jts.geom.Coordinate(pti_re, pti_ho));
				}
			}

			ret.add(new com.vividsolutions.jts.geom.Coordinate(arcPt_re, arcPt_ho));

			if(b>2*p){
				// Zentriwinkel zwischen arcPt und pt2
				double beta=2.0*Math.asin(b/2.0/Math.abs(r));
				// anzahl Schritte
				int betan=(int)Math.ceil((beta/theta));
				// Winkelschrittweite
				double betai=beta/(betan*(r>0.0?1:-1));
				double ri=Math.atan2(arcPt_re-reM,arcPt_ho-hoM);
				for(int i=1;i<betan;i++){
					ri += betai;
					double pti_re=reM + Math.abs(r) * Math.sin(ri);
					double pti_ho=hoM + Math.abs(r) * Math.cos(ri);
					ret.add(new com.vividsolutions.jts.geom.Coordinate(pti_re, pti_ho));
				}
			}
			ret.add(new com.vividsolutions.jts.geom.Coordinate(pt2_re, pt2_ho));
		}
	}
	private int arc2JTS(IomObject startPt,IomObject value,double p)
	throws Iox2wkbException
	{
		if(value==null){
			return 0;
		}
			int pointc=0;
			String c1=value.getattrvalue("C1");
			String c2=value.getattrvalue("C2");
			String c3=value.getattrvalue("C3");
			String a1=value.getattrvalue("A1");
			String a2=value.getattrvalue("A2");
			double pt2_re;
			try{
				pt2_re = Double.parseDouble(c1);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C1 <"+c1+">",ex);
			}
			double pt2_ho;
			try{
				pt2_ho = Double.parseDouble(c2);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C2 <"+c2+">",ex);
			}
			double pt2_z=0.0;
			if(c3!=null){
				try{
					pt2_z = Double.parseDouble(c3);
				}catch(Exception ex){
					throw new Iox2wkbException("failed to read C3 <"+c3+">",ex);
				}
			}
			double arcPt_re;
			try{
				arcPt_re = Double.parseDouble(a1);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read A1 <"+a1+">",ex);
			}
			double arcPt_ho;
			try{
				arcPt_ho = Double.parseDouble(a2);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read A2 <"+a2+">",ex);
			}
			if(p==0.0){
				throw new Iox2wkbException("illegal p");
			}

			String p1c1=startPt.getattrvalue("C1");
			String p1c2=startPt.getattrvalue("C2");
			String p1c3=startPt.getattrvalue("C3");
			double pt1_re;
			try{
				pt1_re = Double.parseDouble(p1c1);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C1 <"+p1c1+">",ex);
			}
			double pt1_ho;
			try{
				pt1_ho = Double.parseDouble(p1c2);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C2 <"+p1c2+">",ex);
			}
			double pt1_z=0.0;
			if(p1c3!=null){
				try{
					pt1_z = Double.parseDouble(p1c3);
				}catch(Exception ex){
					throw new Iox2wkbException("failed to read C3 <"+p1c3+">",ex);
				}
			}
			
			//EhiLogger.debug("pt1 "+pt1_re+", "+pt1_ho);
			//EhiLogger.debug("arc "+arcPt_re+", "+arcPt_ho);
			//EhiLogger.debug("pt2 "+pt2_re+", "+pt2_ho);
			// letzter Punkt ein Bogenzwischenpunkt?
		
			// Zwischenpunkte erzeugen

			// Distanz zwischen Bogenanfanspunkt und Zwischenpunkt
			double a=dist(pt1_re,pt1_ho,arcPt_re,arcPt_ho);
			// Distanz zwischen Zwischenpunkt und Bogenendpunkt 
			double b=dist(arcPt_re,arcPt_ho,pt2_re,pt2_ho);

			// Zwischenpunkte erzeugen, so dass maximale Pfeilhöhe nicht 
			// überschritten wird
			// Distanz zwischen Bogenanfanspunkt und Bogenendpunkt 
			double c=dist(pt1_re,pt1_ho,pt2_re,pt2_ho);
			// Radius bestimmen
			double s=(a+b+c)/2.0;
			double ds=Math.atan2(pt2_re-arcPt_re,pt2_ho-arcPt_ho)-Math.atan2(pt1_re-arcPt_re,pt1_ho-arcPt_ho);
			double rSign=(Math.sin(ds)>0.0)?-1.0:1.0;
			double r=a*b*c/4.0/Math.sqrt(s*(s-a)*(s-b)*(s-c))*rSign;
			// Kreismittelpunkt
			double thetaM=Math.atan2(arcPt_re-pt1_re,arcPt_ho-pt1_ho)+Math.acos(a/2.0/r);
			double reM=pt1_re+r*Math.sin(thetaM);
			double hoM=pt1_ho+r*Math.cos(thetaM);

			// mindest Winkelschrittweite
			double theta=2*Math.acos(1-p/Math.abs(r));

			if(a>2*p){
				// Zentriwinkel zwischen pt1 und arcPt
				double alpha=2.0*Math.asin(a/2.0/Math.abs(r));
				// anzahl Schritte
				int alphan=(int)Math.ceil(alpha/theta);
				// Winkelschrittweite
				double alphai=alpha/(alphan*(r>0.0?1:-1));
				double ri=Math.atan2(pt1_re-reM,pt1_ho-hoM);
				for(int i=1;i<alphan;i++){
					ri += alphai;
					double pti_re=reM + Math.abs(r) * Math.sin(ri);
					double pti_ho=hoM + Math.abs(r) * Math.cos(ri);
					writeCoord(pti_re, pti_ho,0.0);
					pointc++;
				}
			}

			writeCoord(arcPt_re, arcPt_ho,0.0);
			pointc++;

			if(b>2*p){
				// Zentriwinkel zwischen arcPt und pt2
				double beta=2.0*Math.asin(b/2.0/Math.abs(r));
				// anzahl Schritte
				int betan=(int)Math.ceil((beta/theta));
				// Winkelschrittweite
				double betai=beta/(betan*(r>0.0?1:-1));
				double ri=Math.atan2(arcPt_re-reM,arcPt_ho-hoM);
				for(int i=1;i<betan;i++){
					ri += betai;
					double pti_re=reM + Math.abs(r) * Math.sin(ri);
					double pti_ho=hoM + Math.abs(r) * Math.cos(ri);
					writeCoord(pti_re, pti_ho,0.0);
					pointc++;
				}
			}
			writeCoord(pt2_re, pt2_ho,pt2_z);
			pointc++;
			return pointc;
	}
	/** Converts a COORD to a JTS Coordinate.
	 * @param value INTERLIS COORD structure.
	 * @return JTS Coordinate.
	 * @throws Iox2jtsException
	 */
	private static com.vividsolutions.jts.geom.Coordinate coord2JTS(IomObject value) 
	throws Iox2wkbException 
	{
		if(value==null){
			return null;
		}
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
		com.vividsolutions.jts.geom.Coordinate coord=null;
		if(c3==null){
			coord=new com.vividsolutions.jts.geom.Coordinate(xCoord, yCoord);
		}else{
			double zCoord;
			try{
				zCoord = Double.parseDouble(c3);
			}catch(Exception ex){
				throw new Iox2wkbException("failed to read C3 <"+c3+">",ex);
			}
			coord=new com.vividsolutions.jts.geom.Coordinate(xCoord, yCoord,zCoord);
		}
		return coord;
	}
	/** Converts a POLYLINE to a JTS CoordinateList.
	 * @param polylineObj INTERLIS POLYLINE structure
	 * @param isSurfaceOrArea true if called as part of a SURFACE conversion.
	 * @param p maximum stroke to use when removing ARCs
	 * @return JTS CoordinateList
	 * @throws Iox2wkbException
	 */
	public byte[] polyline2wkb(IomObject polylineObj,boolean isSurfaceOrArea,boolean asCompoundCurve,double p)
	throws Iox2wkbException
	{
		if(polylineObj==null){
			return null;
		}
		byte ret[]=null;
        try {
			os.reset();
			writeByteOrder();
			if(asCompoundCurve){
				writeGeometryType(WKBConstants.wkbCompoundCurve);
			}else{
				writeGeometryType(WKBConstants.wkbLineString);
			}
			
			// remember position of size
			int sizePos=os.size();
			int size=0;
			// write dummy size
			os.writeInt(0);
			
			java.util.ArrayList<Integer> patches=new java.util.ArrayList<Integer>();
			
			// is POLYLINE?
			if(isSurfaceOrArea){
				IomObject lineattr=polylineObj.getattrobj("lineattr",0);
				if(lineattr!=null){
					//writeAttrs(out,lineattr);
					throw new Iox2wkbException("Lineattributes not supported");							
				}
			}
			boolean clipped=polylineObj.getobjectconsistency()==IomConstants.IOM_INCOMPLETE;
			if(clipped){
				throw new Iox2wkbException("clipped polyline not supported");
			}
			for(int sequencei=0;sequencei<polylineObj.getattrvaluecount("sequence");sequencei++){
				if(clipped){
					//out.startElement(tags::get_CLIPPED(),0,0);
				}else{
					// an unclipped polyline should have only one sequence element
					if(sequencei>0){
						throw new Iox2wkbException("unclipped polyline with multi 'sequence' elements");
					}
				}
				int coordc=0;
				int coordcPos=0;
				int currentComponent=0;
				IomObject arcStartPt=null;
				IomObject sequence=polylineObj.getattrobj("sequence",sequencei);
				int segmentc=sequence.getattrvaluecount("segment");
				for(int segmenti=0;segmenti<segmentc;segmenti++){
					IomObject segment=sequence.getattrobj("segment",segmenti);
					//EhiLogger.debug("segmenttag "+segment.getobjecttag());
					if(segment.getobjecttag().equals("COORD")){
						// COORD
						if(asCompoundCurve){
							if(segmenti==0 && segmentc>=2 && sequence.getattrobj("segment",segmenti+1).getobjecttag().equals("ARC")){
								// us it as startpoint of first arc
							}else{
								if(currentComponent!=WKBConstants.wkbLineString){
									if(currentComponent!=0){
										// finish last component
										patches.add(coordcPos);
										patches.add(coordc);
									}
									// start LineString
									writeByteOrder();
									writeGeometryType(WKBConstants.wkbLineString);
									// remember position of size
									coordcPos=os.size();
									coordc=0;
									// write dummy size
									os.writeInt(0);
									// new component
									currentComponent=WKBConstants.wkbLineString;
									size++;
									// not first segment
									if(arcStartPt!=null){
										// write start point
										writeCoord(arcStartPt);
										coordc++;
									}
								}
								writeCoord(segment);
								coordc++;
							}
						}else{
							writeCoord(segment);
							coordc++;
						}
					}else if(segment.getobjecttag().equals("ARC")){
						// ARC
						if(asCompoundCurve){
							if(currentComponent!=WKBConstants.wkbCircularString){
								if(currentComponent!=0){
									// finish last component
									patches.add(coordcPos);
									patches.add(coordc);
								}
								// start CircularString
								writeByteOrder();
								writeGeometryType(WKBConstants.wkbCircularString);
								// remember position of size
								coordcPos=os.size();
								coordc=0;
								// write dummy size
								os.writeInt(0);
								// new component
								currentComponent=WKBConstants.wkbCircularString;
								size++;
								// write start point
								writeCoord(arcStartPt);
								coordc++;
							}
							// write intermediate point
							String a1=segment.getattrvalue("A1");
							String a2=segment.getattrvalue("A2");
							double arcPt_re;
							try{
								arcPt_re = Double.parseDouble(a1);
							}catch(Exception ex){
								throw new Iox2wkbException("failed to read A1 <"+a1+">",ex);
							}
							double arcPt_ho;
							try{
								arcPt_ho = Double.parseDouble(a2);
							}catch(Exception ex){
								throw new Iox2wkbException("failed to read A2 <"+a2+">",ex);
							}
							writeCoord(arcPt_re,arcPt_ho,0.0);
							coordc++;
							// write end point
							writeCoord(segment);
							coordc++;
						}else{
							int pointc=arc2JTS(arcStartPt,segment,p);
							coordc+=pointc;
						}
					}else{
						// custum line form
						throw new Iox2wkbException("custom line form not supported");
						//out.startElement(segment->getTag(),0,0);
						//writeAttrs(out,segment);
						//out.endElement(/*segment*/);
					}
					arcStartPt=segment;
				}
				if(clipped){
					//out.endElement(/*CLIPPED*/);
				}
				if(asCompoundCurve){
					// finish last component
					patches.add(coordcPos);
					patches.add(coordc);
				}else{
					size+=coordc;
				}
			}
			ret=os.toByteArray();
			// fix size of components
			Iterator<Integer> patchi=patches.iterator();
			while(patchi.hasNext()){
				int patchPos=patchi.next();
				int value=patchi.next();
				patchInt(ret,patchPos,value);
			}
			// fix number of components (or size if simple linestring)
			patchInt(ret,sizePos,size);
		} catch (IOException e) {
	        throw new RuntimeException("Unexpected IO exception: " + e.getMessage());
		}
		return ret;
	}
	void patchInt(byte ret[],int patchPos,int patchValue)
	{
		java.nio.ByteBuffer buf=java.nio.ByteBuffer.allocate(4);
		buf.order(os.order());
		buf.rewind();
		buf.putInt(patchValue);
		System.arraycopy(buf.array(),0,ret,patchPos, buf.position());
	}

	private static com.vividsolutions.jts.geom.CoordinateList polyline2coordlist(IomObject polylineObj,boolean isSurfaceOrArea,double p)
	throws Iox2wkbException
	{
		if(polylineObj==null){
			return null;
		}
		com.vividsolutions.jts.geom.CoordinateList ret=new com.vividsolutions.jts.geom.CoordinateList();
		// is POLYLINE?
		if(isSurfaceOrArea){
			IomObject lineattr=polylineObj.getattrobj("lineattr",0);
			if(lineattr!=null){
				//writeAttrs(out,lineattr);
				throw new Iox2wkbException("Lineattributes not supported");							
			}
		}
		boolean clipped=polylineObj.getobjectconsistency()==IomConstants.IOM_INCOMPLETE;
		if(clipped){
			throw new Iox2wkbException("clipped polyline not supported");
		}
		for(int sequencei=0;sequencei<polylineObj.getattrvaluecount("sequence");sequencei++){
			if(clipped){
				//out.startElement(tags::get_CLIPPED(),0,0);
			}else{
				// an unclipped polyline should have only one sequence element
				if(sequencei>0){
					throw new Iox2wkbException("unclipped polyline with multi 'sequence' elements");
				}
			}
			IomObject sequence=polylineObj.getattrobj("sequence",sequencei);
			for(int segmenti=0;segmenti<sequence.getattrvaluecount("segment");segmenti++){
				IomObject segment=sequence.getattrobj("segment",segmenti);
				//EhiLogger.debug("segmenttag "+segment.getobjecttag());
				if(segment.getobjecttag().equals("COORD")){
					// COORD
					ret.add(coord2JTS(segment));
				}else if(segment.getobjecttag().equals("ARC")){
					// ARC
					arc2JTS(ret,segment,p);
				}else{
					// custum line form
					throw new Iox2wkbException("custom line form not supported");
					//out.startElement(segment->getTag(),0,0);
					//writeAttrs(out,segment);
					//out.endElement(/*segment*/);
				}

			}
			if(clipped){
				//out.endElement(/*CLIPPED*/);
			}
		}
		return ret;
	}
	/** Converts a SURFACE to a JTS Polygon.
	 * @param obj INTERLIS SURFACE structure
	 * @param strokeP maximum stroke to use when removing ARCs
	 * @return JTS Polygon
	 * @throws Iox2wkbException
	 */
	public byte[] surface2wkb(IomObject obj,boolean asCurvePolygon,double strokeP) //SurfaceOrAreaType type)
	throws Iox2wkbException
	{
		if(obj==null){
			return null;
		}
	    try {
			writeByteOrder();
			if(asCurvePolygon){
				writeGeometryType(WKBConstants.wkbCurvePolygon);
			}else{
				writeGeometryType(WKBConstants.wkbPolygon);
			}

			//IFMEFeatureVector bndries=session.createFeatureVector();
			boolean clipped=obj.getobjectconsistency()==IomConstants.IOM_INCOMPLETE;
			if(clipped){
				throw new Iox2wkbException("clipped surface not supported");
			}
			for(int surfacei=0;surfacei<obj.getattrvaluecount("surface");surfacei++){
				if(clipped){
					//out.startElement("CLIPPED",0,0);
				}else{
					// an unclipped surface should have only one surface element
					if(surfacei>0){
						throw new Iox2wkbException("unclipped surface with multi 'surface' elements");
					}
				}
				IomObject surface=obj.getattrobj("surface",surfacei);

				int boundaryc=surface.getattrvaluecount("boundary");
				
			    os.writeInt(boundaryc);
			    
				for(int boundaryi=0;boundaryi<boundaryc;boundaryi++){
					IomObject boundary=surface.getattrobj("boundary",boundaryi);
					if(asCurvePolygon){
						Iox2wkb helper=new Iox2wkb(outputDimension,os.order());
						for(int polylinei=0;polylinei<boundary.getattrvaluecount("polyline");polylinei++){
							IomObject polyline=boundary.getattrobj("polyline",polylinei);
							os.write(helper.polyline2wkb(polyline,true,asCurvePolygon,strokeP));
						}
					}else{
						//IFMEFeature fmeLine=session.createFeature();
						com.vividsolutions.jts.geom.CoordinateList jtsLine=new com.vividsolutions.jts.geom.CoordinateList();
						for(int polylinei=0;polylinei<boundary.getattrvaluecount("polyline");polylinei++){
							IomObject polyline=boundary.getattrobj("polyline",polylinei);
							jtsLine.addAll(polyline2coordlist(polyline,true,strokeP));
						}
						jtsLine.closeRing();
						os.writeInt(jtsLine.size());
						for(Iterator coordi=jtsLine.iterator();coordi.hasNext();){
							com.vividsolutions.jts.geom.Coordinate coord=(com.vividsolutions.jts.geom.Coordinate)coordi.next();
						    os.writeDouble(coord.x);
						    os.writeDouble(coord.y);
						    if (outputDimension==3) {
						      os.writeDouble(coord.z);
						    }
						}
					}

				}
				if(clipped){
					//out.endElement(/*CLIPPED*/);
				}
			}
		} catch (IOException e) {
	        throw new RuntimeException("Unexpected IO exception: " + e.getMessage());
		}
		return os.toByteArray();
	}
	public byte[] multisurface2wkb(IomObject obj,boolean asCurvePolygon,double strokeP) //SurfaceOrAreaType type)
	throws Iox2wkbException
	{
		if(obj==null){
			return null;
		}
	    try {
			writeByteOrder();
			if(asCurvePolygon){
				writeGeometryType(WKBConstants.wkbMultiSurface);
			}else{
				writeGeometryType(WKBConstants.wkbMultiPolygon);
			}
			int surfacec=obj.getattrvaluecount("surface");
			os.writeInt(surfacec);

			for(int surfacei=0;surfacei<surfacec;surfacei++){
				IomObject surface=obj.getattrobj("surface",surfacei);
				IomObject iomSurfaceClone=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
				iomSurfaceClone.addattrobj("surface",surface);
				Iox2wkb helper=new Iox2wkb(outputDimension,os.order());
				os.write(helper.surface2wkb(iomSurfaceClone,asCurvePolygon,strokeP));
			}
		} catch (IOException e) {
	        throw new RuntimeException("Unexpected IO exception: " + e.getMessage());
		}
		return os.toByteArray();
	}

	  private void writeByteOrder() throws IOException
	  {
	    if (os.order().equals(java.nio.ByteOrder.LITTLE_ENDIAN)){
	      os.write(WKBConstants.wkbNDR);
	    }else{
	      os.write(WKBConstants.wkbXDR);
	    }
	  }

	  private void writeGeometryType(int geometryType)
	      throws IOException
	  {
	    int flag3D = (outputDimension == 3) ? WKBConstants.ewkbIncludesZ : 0;
	    int typeInt = geometryType | flag3D;
	    os.writeInt(typeInt);
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
