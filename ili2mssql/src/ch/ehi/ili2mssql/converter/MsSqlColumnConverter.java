package ch.ehi.ili2mssql.converter;

import java.sql.Connection;

import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.iox.adddefval.Converter;

public class MsSqlColumnConverter extends AbstractWKBColumnConverter {
	final String defaultSrid = "3116";
	
	@Override
	public String getInsertValueWrapperCoord(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+(srid==-1?","+defaultSrid:","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+(srid==-1?","+defaultSrid:","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperSurface(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+(srid==-1?","+defaultSrid:","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
		return "geometry::STGeomFromWKB("+wkfValue+(srid==-1?","+defaultSrid:","+srid)+")";
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

}
