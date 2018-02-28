package ch.ehi.ili2ora.converter;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.vividsolutions.jts.io.ParseException;

import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.wkb.Iox2wkb;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class OracleSpatialColumnConverter extends AbstractWKBColumnConverter {
	@Override
	public String getInsertValueWrapperCoord(String wkfValue,int srid) {
		return "SDO_UTIL.FROM_WKBGEOMETRY("+wkfValue+")";
	}
	@Override
	public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
		return "SDO_UTIL.FROM_WKBGEOMETRY("+wkfValue+")";
	}
	@Override
	public String getInsertValueWrapperSurface(String wkfValue,int srid) {
		return "SDO_UTIL.FROM_WKBGEOMETRY("+wkfValue+")";
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
		return "SDO_UTIL.FROM_WKBGEOMETRY("+wkfValue+")";
	}
	
	
	//desde aquí
	@Override
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	
	public IomObject toIomSurface(
			Object geomobj,
			String sqlAttrName,
			boolean is3D)
			throws SQLException, ConverterException {
			//byte bv[]=(byte [])geomobj;
			Blob blob = (Blob) geomobj;
			
			int blobLength = (int) blob.length();  
			byte[] bv = blob.getBytes(1, blobLength);		
		
			com.vividsolutions.jts.geom.Geometry geom;
			try {
				geom = new com.vividsolutions.jts.io.WKBReader().read(bv);
			} catch (ParseException e) {
				throw new ConverterException(e);
			}
			return ch.interlis.iox_j.jts.Jts2iox.JTS2surface((com.vividsolutions.jts.geom.Polygon)geom);
		}
	
	@Override
	public IomObject toIomCoord(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws SQLException, ConverterException {
		Blob blob = (Blob) geomobj;
		
		int blobLength = (int) blob.length();  
		byte[] bv = blob.getBytes(1, blobLength);
		com.vividsolutions.jts.geom.Geometry geom;
		try {
			geom = new com.vividsolutions.jts.io.WKBReader().read(bv);
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
		return ch.interlis.iox_j.jts.Jts2iox.JTS2coord(geom.getCoordinate());
	}
	
	@Override
	public IomObject toIomPolyline(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws SQLException, ConverterException {
		Blob blob = (Blob) geomobj;
		
		int blobLength = (int) blob.length();  
		byte[] bv = blob.getBytes(1, blobLength);
		com.vividsolutions.jts.geom.Geometry geom;
		try {
			geom = new com.vividsolutions.jts.io.WKBReader().read(bv);
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
		return ch.interlis.iox_j.jts.Jts2iox.JTS2polyline((com.vividsolutions.jts.geom.LineString)geom);
	}
	
	@Override
	public IomObject toIomMultiSurface(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws SQLException, ConverterException {
		Blob blob = (Blob) geomobj;
		
		int blobLength = (int) blob.length();  
		byte[] bv = blob.getBytes(1, blobLength);
		Wkb2iox conv=new Wkb2iox();
		try {
			return conv.read(bv);
		} catch (ParseException e) {
			throw new ConverterException(e);
		}
	}
	
	@Override
	public java.lang.Object fromIomMultiSurface(
			IomObject value,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws SQLException, ConverterException {
		
			// TODO ajustar el parametro strokeArcs
			boolean strokeArcs=true;
		
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
	public Integer getSrsid(String crsAuthority, String crsCode,Connection conn) 
	throws ConverterException
	{
		int srsid;
		srsid=Integer.parseInt(crsCode);

		return srsid;
	}
}
