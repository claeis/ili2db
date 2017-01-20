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
package ch.ehi.ili2db.base;

import ch.ehi.ili2db.gui.Config;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 15.03.2007 $
 */
public interface DbIdGen {
	public void init(String schema,Config config);
	public void initDb(java.sql.Connection conn,String dbusr);
	 
	public void initDbDefs(ch.ehi.sqlgen.generator.Generator gen);
	public void addMappingTable(ch.ehi.sqlgen.repository.DbSchema schema);
	
	/** gets a new obj id.
	 */
	public long newObjSqlId();
	public long getLastSqlId();

	public String getDefaultValueSql();
}
