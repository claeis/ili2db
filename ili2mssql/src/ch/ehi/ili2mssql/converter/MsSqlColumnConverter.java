package ch.ehi.ili2mssql.converter;

import java.sql.Connection;
import java.sql.SQLException;

import com.vividsolutions.jts.io.ParseException;

import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.wkb.Iox2wkb;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class MsSqlColumnConverter extends AbstractWKBColumnConverter {
	
	private boolean strokeArcs=true;
	
	@Override
	public void setup(Connection conn, Settings config) {
		super.setup(conn,config);
		strokeArcs=Config.STROKE_ARCS_ENABLE.equals(Config.getStrokeArcs(config));
	}
	
	@Override
	public String getInsertValueWrapperCoord(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+","+srid+")";
	}
	@Override
	public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+","+srid+")";
	}
	@Override
	public String getInsertValueWrapperSurface(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+","+srid+")";
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+","+srid+")";
	}
	
	@Override
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return dbNativeValue+".STAsBinary()";
	}
	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return dbNativeValue+".STAsBinary()";
	}
	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return dbNativeValue+".STAsBinary()";
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return dbNativeValue+".STAsBinary()";
	}
	
	@Override
	public Integer getSrsid(String crsAuthority, String crsCode,Connection conn) 
		throws ConverterException{
		
		int srsid;
		srsid=Integer.parseInt(crsCode);

		return srsid;
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
	public IomObject toIomPolyline(
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
	public java.lang.Object fromIomMultiSurface(
			IomObject value,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws SQLException, ConverterException {
		
				if(value!=null){
					Iox2wkb conv=new Iox2wkb(is3D?3:2);
					
					try {
						return conv.multisurface2wkb(value,!strokeArcs,p);
					} catch (Iox2wkbException ex) {
						throw new ConverterException(ex);
					}
				}
				return null;
		}
	

}
