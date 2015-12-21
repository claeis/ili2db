/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package ch.interlis.iox_j.wkb;

import java.io.IOException;
import java.util.ArrayList;

import ch.ehi.ili2db.base.IliNames;
import ch.interlis.iom.IomObject;

import com.vividsolutions.jts.io.ByteOrderDataInStream;
import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.ByteOrderValues;


public class Wkb2iox
{
  /**
   * Converts a hexadecimal string to a byte array.
   *
   * @param hex a string containing hex digits
   */
  public static byte[] hexToBytes(String hex)
  {
    int byteLen = hex.length() / 2;
    byte[] bytes = new byte[byteLen];

    for (int i = 0; i < hex.length() / 2; i++) {
      int i2 = 2 * i;
      if (i2 + 1 > hex.length())
        throw new IllegalArgumentException("Hex string has odd length");

      int nib1 = hexToInt(hex.charAt(i2));
      int nib0 = hexToInt(hex.charAt(i2 + 1));
      byte b = (byte) ((nib1 << 4) + (byte) nib0);
      bytes[i] = b;
    }
    return bytes;
  }

  private static int hexToInt(char hex)
  {
    int nib = Character.digit(hex, 16);
    if (nib < 0)
      throw new IllegalArgumentException("Invalid hex digit");
    return nib;
  }

  private static final String INVALID_GEOM_TYPE_MSG
  = "Invalid geometry type encountered in ";

  // default dimension - will be set on read
  private int inputDimension = 2;
  private boolean hasSRID = false;
  private int SRID = 0;
  private ByteOrderDataInStream dis = new ByteOrderDataInStream();
  private double[] ordValues;

  public Wkb2iox() {
  }


  /**
   * Reads a single {@link Geometry} from a byte array.
   *
   * @param bytes the byte array to read from
   * @return the geometry read
   * @throws ParseException if a parse exception occurs
   */
  public IomObject read(byte[] bytes) throws ParseException
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
   */
  public IomObject read(InStream is)
  throws IOException, ParseException
  {
    dis.setInStream(is);
    IomObject g = readGeometry();
    setSRID(g);
    return g;
  }

  private IomObject readGeometry()
  throws IOException, ParseException
  {
    // determine byte order
    byte byteOrder = dis.readByte();
    // default is big endian
    if (byteOrder == WKBConstants.wkbNDR)
      dis.setOrder(ByteOrderValues.LITTLE_ENDIAN);

    int typeInt = dis.readInt();
    int geometryType = typeInt & 0xff;
    // determine if Z values are present
    boolean hasZ = (typeInt & WKBConstants.ewkbIncludesZ) != 0;
    inputDimension =  hasZ ? 3 : 2;
    // determine if SRIDs are present
    hasSRID = (typeInt & WKBConstants.ewkbIncludesSRID) != 0;

    if (hasSRID) {
      SRID = dis.readInt();
    }

    // only allocate ordValues buffer if necessary
    if (ordValues == null || ordValues.length < inputDimension)
      ordValues = new double[inputDimension];

    switch (geometryType) {
      case WKBConstants.wkbPoint :
        return readPoint();
      case WKBConstants.wkbLineString :
        return readLineString();
      case WKBConstants.wkbCompoundCurve :
          return readCompoundCurve();
      case WKBConstants.wkbPolygon :
        return readPolygon();
      case WKBConstants.wkbCurvePolygon :
          return readCurvePolygon();
      case WKBConstants.wkbMultiPolygon :
          return readMultiPolygon();
      case WKBConstants.wkbMultiSurface :
          return readMultiSurface();
    }
    throw new ParseException("Unknown WKB type " + geometryType);
    //return null;
  }

  /**
   * Sets the SRID, if it was specified in the WKB
   *
   * @param g the geometry to update
   * @return the geometry with an updated SRID value, if required
   */
  private IomObject setSRID(IomObject g)
  {
    if (SRID != 0){
      // TODO g.setSRID(SRID);
    }
    return g;
  }

  private IomObject readPoint() throws IOException
  {
    readCoordinate();
	IomObject ret=new ch.interlis.iom_j.Iom_jObject("COORD",null);
	ret.setattrvalue("C1", Double.toString(ordValues[0]));
	ret.setattrvalue("C2", Double.toString(ordValues[1]));
	if(inputDimension==3){
		ret.setattrvalue("C3", Double.toString(ordValues[2]));
	}
	return ret;
  }

  private IomObject readLineString() throws IOException
  {

//	  <linestring binary representation> ::=
//				<byte order> <wkblinestring> [ <num> <wkbpoint binary>... ]
//	  <wkblinearring> ::= <num> <wkbpoint binary>...

	    IomObject ret=new ch.interlis.iom_j.Iom_jObject("POLYLINE",null);
		IomObject sequence=new ch.interlis.iom_j.Iom_jObject("SEGMENTS",null);
		ret.addattrobj("sequence",sequence);
    int coordc = dis.readInt();
	for(int coordi=0;coordi<coordc;coordi++){
		sequence.addattrobj("segment", readPoint());
	}
	return ret;
  }
  private IomObject readCompoundCurve() throws IOException
  {
    int compc = dis.readInt();
	IomObject ret=new ch.interlis.iom_j.Iom_jObject("POLYLINE",null);
	IomObject sequence=new ch.interlis.iom_j.Iom_jObject("SEGMENTS",null);
	ret.addattrobj("sequence",sequence);
	for(int compi=0;compi<compc;compi++){
	    byte byteOrder = dis.readByte();
	    int typeInt = dis.readInt();
	    int geometryType = typeInt & 0xff;
	    switch (geometryType) {
	      case WKBConstants.wkbLineString :
	      {
	    	    int coordc = dis.readInt();
	    		for(int coordi=0;coordi<coordc;coordi++){
	    			// not first component and start point?
	    			if(compi>0 && coordi==0){
	    				// skip start point
		    			readPoint();
	    			}else{
		    			sequence.addattrobj("segment", readPoint());
	    			}
	    		}
	    		break;
	      }
	      case WKBConstants.wkbCircularString :
	      {
	    	    int coordc = dis.readInt();
	    		for(int coordi=0;coordi<coordc;coordi++){
	    			// first component and start point?
	    			if(compi==0 && coordi==0){
	    				// add start point
		    			sequence.addattrobj("segment", readPoint());
	    			}else if(compi>0 && coordi==0){
		    			// not first component and start point?
	    				// skip start point
		    			readPoint();
	    			}else{
		    			IomObject arcPt=readPoint();coordi++;
		    			if(coordi>=coordc){
		    				throw new IllegalStateException("missing coord");
		    			}
		    			IomObject endPt=readPoint();
		    			endPt.setobjecttag("ARC");
		    			endPt.setattrvalue("A1", arcPt.getattrvalue("C1"));
		    			endPt.setattrvalue("A2", arcPt.getattrvalue("C2"));
		    			sequence.addattrobj("segment", endPt);
	    			}
	    		}
	    		break;
	      }
	      default:
    	    throw new IllegalStateException("Unexpected WKB type " + geometryType);
	    }
		
	}
	return ret;
  }

  private IomObject readPolygon() throws IOException
  {
//	  <polygon binary representation> ::=
//		<byte order> <wkbpolygon> [ <num> <wkblinearring binary>... ]
//		| <triangle binary representation>
    int holec = dis.readInt();

	IomObject ret=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
	IomObject surface=new ch.interlis.iom_j.Iom_jObject("SURFACE",null);
	ret.addattrobj("surface",surface);

	for(int holei=0;holei<holec;holei++){
		IomObject boundary=new ch.interlis.iom_j.Iom_jObject("BOUNDARY",null);
		surface.addattrobj("boundary",boundary);
		boundary.addattrobj("polyline", readLineString());
	}
	
	return ret;
    
    
  }
  private IomObject readCurvePolygon() throws IOException, ParseException
  {
//	  <curvepolygon binary representation> ::=
//				<byte order> <wkbcurvepolygon> [ <num> <wkbring binary>... ]
//				| <polygon binary representation>

//	  <wkbring binary> ::=
//				<linestring binary representation>
//				| <circularstring binary representation>
//				| <compoundcurve binary representation>
	  
	  
    int ringc = dis.readInt();

	IomObject ret=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
	IomObject surface=new ch.interlis.iom_j.Iom_jObject("SURFACE",null);
	ret.addattrobj("surface",surface);

	for(int holei=0;holei<ringc;holei++){
		IomObject boundary=new ch.interlis.iom_j.Iom_jObject("BOUNDARY",null);
		surface.addattrobj("boundary",boundary);
		boundary.addattrobj("polyline", readGeometry());
	}
	
	return ret;
    
    
  }
  private IomObject readMultiPolygon() throws IOException, ParseException
  {
/*
	  <multipolygon binary representation> ::=
			  <byte order> <wkbmultipolygon>
			  [ <num> <polygon binary representation>... ]			  
			  
	  <polygon binary representation> ::=
			  <byte order> <wkbpolygon> [ <num> <wkblinearring binary>... ]
			  | <triangle binary representation>

*/
	  IomObject ret=null;
	    int polygonc = dis.readInt();
	    for(int polygoni=0;polygoni<polygonc;polygoni++){
	        byte byteOrder = dis.readByte();
	        int typeInt = dis.readInt();
	        int geometryType = typeInt & 0xff;
	        if(geometryType!=WKBConstants.wkbPolygon){
	    	    throw new IllegalStateException("Unexpected WKB type " + geometryType);
	        }
	        if(ret==null){
	        	ret=readPolygon();
	        }else{
	        	IomObject poly=readPolygon();
	        	IomObject surface=poly.getattrobj("surface", 0);
		    	ret.addattrobj("surface",surface);
	        }
	    }
	    return ret;
	  
  }
  private IomObject readMultiSurface() throws IOException, ParseException
  {
/*
	  <multisurface binary representation> ::=
			  <byte order> <wkbmultisurface>
			  [ <num> <surface binary representation>... ]
			  | <multipolygon binary representation>

	  <multipolygon binary representation> ::=
			  <byte order> <wkbmultipolygon>
			  [ <num> <polygon binary representation>... ]			  

	  <surface binary representation> ::=
			  <curvepolygon binary representation>
			  | <polyhedralsurface binary representation>					  
*/
	  IomObject ret=null;
	    int surfacec = dis.readInt();
	    for(int surfacei=0;surfacei<surfacec;surfacei++){
	        byte byteOrder = dis.readByte();
	        int typeInt = dis.readInt();
	        int geometryType = typeInt & 0xff;
	        if(geometryType!=WKBConstants.wkbCurvePolygon){
	    	    throw new IllegalStateException("Unexpected WKB type " + geometryType);
	        }
	        if(ret==null){
	        	ret=readCurvePolygon();
	        }else{
	        	IomObject poly=readCurvePolygon();
	        	IomObject surface=poly.getattrobj("surface", 0);
		    	ret.addattrobj("surface",surface);
	        }
	    }
	    return ret;
  }
  /**
   * Reads a coordinate value with the specified dimensionality.
   * Makes the X and Y ordinates precise according to the precision model
   * in use.
   */
  private void readCoordinate() throws IOException
  {
    for (int i = 0; i < inputDimension; i++) {
        ordValues[i] = dis.readDouble();
    }
  }

}