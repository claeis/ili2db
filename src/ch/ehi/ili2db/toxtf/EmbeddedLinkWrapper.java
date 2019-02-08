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
package ch.ehi.ili2db.toxtf;

import ch.ehi.ili2db.mapping.ViewableWrapper;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;

public class EmbeddedLinkWrapper extends AbstractStructWrapper {

    private long parentSqlId;
    private RoleDef targetRole;
    private Iom_jObject parent;
    private ViewableWrapper parentTable;
    public EmbeddedLinkWrapper(long parentSqlId1, RoleDef targetRole1, Iom_jObject parent1, ViewableWrapper parentTable1) {
        parentSqlId=parentSqlId1;
        targetRole=targetRole1;
        parent=parent1;
        parentTable=parentTable1;
    }

    @Override
    public IomObject getParent() {
        return parent;
    }

    public RoleDef getRole() {
        return targetRole;
    }

    public long getParentSqlId() {
        return parentSqlId;
    }

    public ViewableWrapper getParentTable() {
        return parentTable;
    }
}
