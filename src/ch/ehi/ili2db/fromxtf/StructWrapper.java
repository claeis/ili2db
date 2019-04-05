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
import ch.interlis.iom.IomObject;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 05.04.2005 $
 */
public class StructWrapper extends AbstractStructWrapper {
	private long parentSqlId;
	private String parentSqlType;
	private IomObject struct;
	private int structi;
	private String parentSqlAttr;
	private AttributeDef parentAttr;
	public StructWrapper(long parentSqlId1,String parentSqlType1,String parentSqlAttr1,IomObject struct1,int structi1,AttributeDef parentAttr1){
		parentSqlId=parentSqlId1;
		parentSqlType=parentSqlType1;
		parentSqlAttr=parentSqlAttr1;
		parentAttr=parentAttr1;
		struct=struct1;
		structi=structi1;
	}
	public long getParentSqlId() {
		return parentSqlId;
	}
	/** gets the sql name of the parent class/structure. 
	 * @return table name or value of column t_type if parent is not mapped with a newClass strategy
	 */
	
	public String getParentSqlType() {
		return parentSqlType;
	}
	public String getParentSqlAttr() {
		return parentSqlAttr;
	}
	public AttributeDef getParentAttr() {
		return parentAttr;
	}
	@Override
	public IomObject getStruct() {
		return struct;
	}
	public int getStructi() {
		return structi;
	}

}
