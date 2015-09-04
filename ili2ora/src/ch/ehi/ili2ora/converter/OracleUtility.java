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
package ch.ehi.ili2ora.converter;

import java.sql.ResultSet;
import oracle.spatial.geometry.JGeometry;
import ch.interlis.iom.*;
import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.ili2c.metamodel.CoordType;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 27.05.2006 $
 */
public class OracleUtility {

	private OracleUtility() {
	}
	public static String gtype2str(int gtype)
	{
		switch(gtype){
			case JGeometry.GTYPE_COLLECTION:
				return "GTYPE_COLLECTION";
			case JGeometry.GTYPE_CURVE:
				return "GTYPE_CURVE";
			case JGeometry.GTYPE_MULTICURVE:
				return "GTYPE_MULTICURVE";
			case JGeometry.GTYPE_MULTIPOINT:
				return "GTYPE_MULTIPOINT";
			case JGeometry.GTYPE_MULTIPOLYGON:
				return "GTYPE_MULTIPOLYGON";
			case JGeometry.GTYPE_POINT:
				return "GTYPE_POINT";
			case JGeometry.GTYPE_POLYGON:
				return "GTYPE_POINT";
			default:
				return Integer.toString(gtype);
		}
	}

}
