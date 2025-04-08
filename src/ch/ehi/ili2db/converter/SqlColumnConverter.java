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
package ch.ehi.ili2db.converter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import ch.ehi.basics.settings.Settings;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.EnumCodeMapper;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 12.02.2007 $
 */
public interface SqlColumnConverter {
	public void setup(Connection conn,Settings config);
	public abstract Integer getSrsid(String crsAuthority,String crsCode,Connection conn) throws ConverterException;
	/*
	public abstract String getCoordSqlUDT();
	public abstract String getPolylineSqlUDT();
	public abstract String getSurfaceSqlUDT();
	public abstract String getAreaSqlUDT();
	*/
	public abstract String getInsertValueWrapperCoord(String wkfValue,int srid);
	public abstract String getInsertValueWrapperMultiCoord(String wkfValue,int srid);
	public abstract String getInsertValueWrapperPolyline(String wkfValue,int srid);
	public abstract String getInsertValueWrapperMultiPolyline(String wkfValue,int srid);
	public abstract String getInsertValueWrapperSurface(String wkfValue,int srid);
	public abstract String getInsertValueWrapperMultiSurface(String wkfValue,int srid);
	public abstract String getSelectValueWrapperDate(String sqlColName);
	public abstract String getSelectValueWrapperTime(String sqlColName);
	public abstract String getSelectValueWrapperDateTime(String sqlColName);
	public abstract String getSelectValueWrapperCoord(String dbNativeValue);
	public abstract String getSelectValueWrapperMultiCoord(String dbNativeValue);
	public abstract String getSelectValueWrapperPolyline(String dbNativeValue);
	public abstract String getSelectValueWrapperMultiPolyline(String dbNativeValue);
	public abstract String getSelectValueWrapperSurface(String dbNativeValue);
	public abstract String getSelectValueWrapperMultiSurface(String dbColName);
	public abstract String getSelectValueWrapperArray(String makeColumnRef);
	public abstract String getInsertValueWrapperArray(String sqlColName);
	public abstract void setCoordNull(java.sql.PreparedStatement stmt,int parameterIndex) throws java.sql.SQLException;
	public abstract void setPolylineNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setSurfaceNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setDecimalNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setUuidNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setBlobNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setXmlNull(java.sql.PreparedStatement stmt,int parameterIndex)throws java.sql.SQLException;
	public abstract void setArrayNull(PreparedStatement ps, int parameterIndex)throws java.sql.SQLException;
	public abstract void setBoolean(java.sql.PreparedStatement stmt,int parameterIndex,boolean value)throws java.sql.SQLException;
	public abstract void setTimestamp(PreparedStatement ps, int valuei,Timestamp datetime) throws SQLException;
	public abstract void setDate(PreparedStatement ps, int valuei, Date date) throws SQLException;
	public abstract void setTime(PreparedStatement ps, int valuei, Time time) throws SQLException;
	public abstract Object fromIomUuid(String uuid)
			throws java.sql.SQLException, ConverterException;
	public abstract Object fromIomBlob(String uuid)
			throws java.sql.SQLException, ConverterException;
	public abstract Object fromIomXml(String uuid)
			throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomSurface(
		IomObject obj,
		int srid,
		boolean hasLineAttr,
		boolean is3D,double p)
		throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomMultiSurface(
			IomObject obj,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomCoord(IomObject value,int srid, boolean is3D)
		throws java.sql.SQLException, ConverterException;
	public Object fromIomMultiCoord(IomObject iomMultiline, int srsid, boolean is3d)
		throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomPolyline(
		IomObject obj,
		int srid,
		boolean is3D,double p)
		throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomMultiPolyline(
			IomObject obj,
			int srid,
			boolean is3D,double p)
			throws java.sql.SQLException, ConverterException;
	public abstract java.lang.Object fromIomArray(ch.interlis.ili2c.metamodel.AttributeDef iliEleAttr,String iomValues[],Class<? extends DbColumn> dbTypeHint) throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomCoord(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomMultiCoord(
			Object geomobj,
			String sqlAttrName,
			boolean is3D)
			throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomSurface(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomMultiSurface(
			Object geomobj,
			String sqlAttrName,
			boolean is3D)
			throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomPolyline(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws java.sql.SQLException, ConverterException;
	public abstract IomObject toIomMultiPolyline(
			Object geomobj,
			String sqlAttrName,
			boolean is3D)
			throws java.sql.SQLException, ConverterException;
	public String toIomXml(Object obj)
		throws java.sql.SQLException, ConverterException;
	public String toIomBlob(Object obj)
			throws java.sql.SQLException, ConverterException;
	public String[] toIomArray(ch.interlis.ili2c.metamodel.AttributeDef iliEleAttr,Object sqlArray,Class<? extends DbColumn> dbTypeHint) throws java.sql.SQLException, ConverterException;
    public void setJsonNull(PreparedStatement ps, int valuei) throws SQLException;
    public String getInsertValueWrapperJson(String sqlColName);
    public String getSelectValueWrapperJson(String sqlColName);
    public Object fromIomStructureToJson(AttributeDef iliEleAttr, IomObject[] iomValues)
            throws SQLException, ConverterException;
    public Object fromIomStructureToJsonArray(AttributeDef iliEleAttr, IomObject[] iomValues)
            throws SQLException, ConverterException;
    public IomObject[] toIomStructureFromJson(AttributeDef iliEleAttr, Object sqlArray)
            throws SQLException, ConverterException;
    public Object fromIomValueArrayToJson(AttributeDef iliEleAttr, String[] iomValues, boolean isEnumInt)
            throws SQLException, ConverterException;
    public String[] toIomValueArrayFromJson(AttributeDef iliEleAttr, Object sqlArray, boolean isEnumInt)
            throws SQLException, ConverterException;
}