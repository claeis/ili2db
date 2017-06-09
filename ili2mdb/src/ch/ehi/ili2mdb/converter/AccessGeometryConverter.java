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
package ch.ehi.ili2mdb.converter;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Connection;

import ch.interlis.iom.IomObject;
import ch.interlis.iom.IomConstants;

public class AccessGeometryConverter extends AbstractWKBColumnConverter {
	public void setDecimalNull(PreparedStatement stmt, int parameterIndex) 
	throws java.sql.SQLException
	{
		stmt.setNull(parameterIndex,java.sql.Types.DOUBLE);
	}
	public void setBoolean(java.sql.PreparedStatement stmt,int parameterIndex,boolean value)
	throws java.sql.SQLException
	{
		if(value==true){
			stmt.setString(parameterIndex, "1");
		}else{
			stmt.setString(parameterIndex, "0");
		}
		
	}
	public String getInsertValueWrapperCoord(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getInsertValueWrapperPolyline(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getInsertValueWrapperSurface(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return dbNativeValue;
	}
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return dbNativeValue;
	}
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return dbNativeValue;
	}
	public Integer getSrsid(String crsAuthority, String crsCode, Connection conn) throws ConverterException {
		return -1;
	}

}
