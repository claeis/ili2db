/* This file is part of the ili2ora project.
 * For more information, please see <http://www.interlis.ch>.
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
 */
package ch.ehi.ili2pg.converter;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.gui.Config;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Types;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.jts.Iox2jtsException;
import ch.interlis.iox_j.wkb.Iox2wkb;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class PostgisColumnConverter extends AbstractWKBColumnConverter {
	private boolean strokeArcs=true;
	@Override
	public void setup(Connection conn, Config config) {
		super.setup(conn,config);
		strokeArcs=Config.STROKE_ARCS_ENABLE.equals(config.getStrokeArcs());
	}
	
	@Override
	public String getInsertValueWrapperCoord(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperSurface(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	public Object fromIomUuid(String uuid) 
			throws java.sql.SQLException, ConverterException
	{
		 org.postgresql.util.PGobject toInsertUUID = new org.postgresql.util.PGobject();
		 toInsertUUID.setType("uuid");
		 toInsertUUID.setValue(uuid);	
		return toInsertUUID;
	}
	@Override
	public Object fromIomXml(String xml) 
			throws java.sql.SQLException, ConverterException
	{
		 org.postgresql.util.PGobject toInsertUUID = new org.postgresql.util.PGobject();
		 toInsertUUID.setType("xml");
		 toInsertUUID.setValue(xml);	
		return toInsertUUID;
	}
	@Override
	public Object fromIomBlob(String blob) 
			throws java.sql.SQLException, ConverterException
	{

	    byte[] bytearray;
		try {
			bytearray = new sun.misc.BASE64Decoder().decodeBuffer(blob);
		} catch (IOException e) {
			throw new ConverterException(e);
		}
		return bytearray;
	}
	@Override
	public java.lang.Object fromIomSurface(
			IomObject value,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws SQLException, ConverterException {
				if(value!=null){
					Iox2wkb conv=new Iox2wkb(is3D?3:2);
					//EhiLogger.debug("conv "+conv); // select st_asewkt(form) from tablea
					try {
						return conv.surface2wkb(value,!strokeArcs,p);
					} catch (Iox2wkbException ex) {
						throw new ConverterException(ex);
					}
				}
				return null;
		}
	@Override
	public java.lang.Object fromIomMultiSurface(
			IomObject value,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws SQLException, ConverterException {
				if(value!=null){
					Iox2wkb conv=new Iox2wkb(is3D?3:2);
					//EhiLogger.debug("conv "+conv); // select st_asewkt(form) from tablea
					try {
						return conv.multisurface2wkb(value,!strokeArcs,p);
					} catch (Iox2wkbException ex) {
						throw new ConverterException(ex);
					}
				}
				return null;
		}
		@Override
		public java.lang.Object fromIomCoord(IomObject value, int srid,boolean is3D)
			throws SQLException, ConverterException {
			if(value!=null){
				Iox2wkb conv=new Iox2wkb(is3D?3:2);
				try {
					return conv.coord2wkb(value);
				} catch (Iox2wkbException ex) {
					throw new ConverterException(ex);
				}
			}
			return null;
		}
		@Override
		public java.lang.Object fromIomPolyline(IomObject value, int srid,boolean is3D,double p)
			throws SQLException, ConverterException {
			if(value!=null){
				Iox2wkb conv=new Iox2wkb(is3D?3:2);
				try {
					return conv.polyline2wkb(value,false,!strokeArcs,p);
				} catch (Iox2wkbException ex) {
					throw new ConverterException(ex);
				}
			}
			return null;
		}
		@Override
		public IomObject toIomCoord(
				Object geomobj,
				String sqlAttrName,
				boolean is3D)
				throws SQLException, ConverterException {
				byte bv[]=(byte [])geomobj;
				Wkb2iox conv=new Wkb2iox();
				try {
					return conv.read(bv);
				} catch (ParseException e) {
					throw new ConverterException(e);
				}
			}
		@Override
			public IomObject toIomSurface(
				Object geomobj,
				String sqlAttrName,
				boolean is3D)
				throws SQLException, ConverterException {
				byte bv[]=(byte [])geomobj;
				Wkb2iox conv=new Wkb2iox();
				try {
					return conv.read(bv);
				} catch (ParseException e) {
					throw new ConverterException(e);
				}
			}
		@Override
		public IomObject toIomMultiSurface(
			Object geomobj,
			String sqlAttrName,
			boolean is3D)
			throws SQLException, ConverterException {
			byte bv[]=(byte [])geomobj;
			Wkb2iox conv=new Wkb2iox();
			try {
				return conv.read(bv);
			} catch (ParseException e) {
				throw new ConverterException(e);
			}
		}
		@Override
			public IomObject toIomPolyline(
				Object geomobj,
				String sqlAttrName,
				boolean is3D)
				throws SQLException, ConverterException {
				byte bv[]=(byte [])geomobj;
				//String v=((org.postgresql.util.PGobject)geomobj).getValue();
				//byte bv[]=WKBReader.hexToBytes(v);
				Wkb2iox conv=new Wkb2iox();
				try {
					return conv.read(bv);
				} catch (ParseException e) {
					throw new ConverterException(e);
				}
			}

	@Override
	public String toIomXml(Object obj) throws java.sql.SQLException,
			ConverterException {
		return ((java.sql.SQLXML)obj).getString();
	}

	@Override
	public String toIomBlob(Object obj) throws java.sql.SQLException,
			ConverterException {
	    String s = new sun.misc.BASE64Encoder().encode((byte[])obj);
	    return s;
	}

		@Override
		public void setUuidNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.OTHER,"uuid");
		}
		@Override
		public void setXmlNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.SQLXML);
		}
		@Override
		public void setBlobNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.BINARY);
		}
}
