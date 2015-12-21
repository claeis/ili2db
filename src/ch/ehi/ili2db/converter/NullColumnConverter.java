package ch.ehi.ili2db.converter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;

public class NullColumnConverter implements SqlColumnConverter {

	@Override
	public Object fromIomCoord(IomObject value, int srid, boolean is3D)
			throws SQLException, ConverterException {
		return null;
	}

	@Override
	public Object fromIomPolyline(IomObject obj, int srid, boolean is3D,double p)
			throws SQLException, ConverterException {
		return null;
	}

	@Override
	public Object fromIomSurface(IomObject obj, int srid, boolean hasLineAttr,
			boolean is3D,double p) throws SQLException, ConverterException {
		return null;
	}
	@Override
	public Object fromIomMultiSurface(IomObject obj, int srid, boolean hasLineAttr,
			boolean is3D,double p) throws SQLException, ConverterException {
		return null;
	}

	@Override
	public String getInsertValueWrapperCoord(String wkfValue, int srid) {
		return null;
	}

	@Override
	public String getInsertValueWrapperPolyline(String wkfValue, int srid) {
		return null;
	}

	@Override
	public String getInsertValueWrapperSurface(String wkfValue, int srid) {
		return null;
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue, int srid) {
		return null;
	}

	@Override
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return null;
	}

	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return null;
	}

	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return null;
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return null;
	}

	@Override
	public int getSrsid(String crsAuthority, String crsCode, Connection conn)
			throws ConverterException {
		return 0;
	}

	@Override
	public void setBoolean(PreparedStatement stmt, int parameterIndex,
			boolean value) throws SQLException {
	}

	@Override
	public void setCoordNull(PreparedStatement stmt, int parameterIndex)
			throws SQLException {
	}

	@Override
	public void setDecimalNull(PreparedStatement stmt, int parameterIndex)
			throws SQLException {
	}

	@Override
	public void setPolylineNull(PreparedStatement stmt, int parameterIndex)
			throws SQLException {
	}

	@Override
	public void setSurfaceNull(PreparedStatement stmt, int parameterIndex)
			throws SQLException {
	}

	@Override
	public void setup(Connection conn, Config config) {
	}

	@Override
	public IomObject toIomCoord(Object geomobj, String sqlAttrName, boolean is3D)
			throws SQLException, ConverterException {
		return null;
	}

	@Override
	public IomObject toIomPolyline(Object geomobj, String sqlAttrName,
			boolean is3D) throws SQLException, ConverterException {
		return null;
	}

	@Override
	public IomObject toIomSurface(Object geomobj, String sqlAttrName,
			boolean is3D) throws SQLException, ConverterException {
		return null;
	}
	@Override
	public IomObject toIomMultiSurface(Object geomobj, String sqlAttrName,
			boolean is3D) throws SQLException, ConverterException {
		return null;
	}

}
