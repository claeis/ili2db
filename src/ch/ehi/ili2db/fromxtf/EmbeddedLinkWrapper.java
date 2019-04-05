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
package ch.ehi.ili2db.fromxtf;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.iom.IomObject;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 05.04.2005 $
 */
public class EmbeddedLinkWrapper extends AbstractStructWrapper {
    private String parentXtfId;
	private String parentSqlType;
	private IomObject struct;
	private RoleDef targetRole;
	public EmbeddedLinkWrapper(String parentXtfId1, String parentSqlType1, IomObject struct1, RoleDef targetRole1) {
        parentXtfId=parentXtfId1;
        parentSqlType=parentSqlType1;
        targetRole=targetRole1;
        struct=struct1;
    }
	/** gets the sql name of the parent class/structure. 
	 * @return table name or value of column t_type if parent is not mapped with a newClass strategy
	 */
	
	public String getParentSqlType() {
		return parentSqlType;
	}
    @Override
	public IomObject getStruct() {
		return struct;
	}
    public String getParentXtfId() {
        return parentXtfId;
    }
    public RoleDef getTargetRole() {
        return targetRole;
    }
}
