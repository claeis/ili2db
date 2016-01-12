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
package ch.ehi.ili2gpkg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ch.ehi.ili2db.base.IliNames;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.wkb.Wkb2iox;

import com.vividsolutions.jts.io.ByteOrderDataInStream;
import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.InStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.ByteOrderValues;


public class Gpkg2iox
{

  Wkb2iox helper=new Wkb2iox();

  public Gpkg2iox() {
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
		// GeoPackageBinaryHeader {
		// byte[2] magic = 0x4750;  // 'GP'
		// byte version; // 8-bit unsigned integer, 0 = version 1
		// byte flags; // see flags layout below
		// int32 srs_id;
		// double[] envelope;  // see flags envelope contents indicator code below
		//}
		// magic
		//os.write(0x47);
		//os.write(0x50);
	  int pos=0;
	  byte magic1=bytes[pos++];
	  if(magic1!=0x47){
		  throw new ParseException("unexpected magic1 "+magic1);
	  }
	  byte magic2=bytes[pos++];
	  if(magic2!=0x50){
		  throw new ParseException("unexpected magic2 "+magic2);
	  }
		// version		os.write(0);
		  byte version=bytes[pos++];
		  if(version!=0){
			  throw new ParseException("unexpected version "+version);
		  }
		// flags
		byte flags=bytes[pos++];
		int env=(flags & 0x0e)>>1;
		int envlen=0;
		if(env==0){
			envlen=0;
		}else if(env==1){
			envlen=32;
		}else if(env==2){
			envlen=48;
		}else if(env==3){
			envlen=48;
		}else if(env==4){
			envlen=64;
		}else{
			  throw new ParseException("unexpected envelope "+env);
		}
		
		// srs_id
		pos+=Integer.SIZE/8;
		// envelope
		pos+=envlen;
	  return helper.read(Arrays.copyOfRange(bytes, pos, bytes.length));
  }
}