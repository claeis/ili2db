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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.sqlgen.repository.DbTableName;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 27.05.2006 $
 */
public class DbUtility {
	private DbUtility(){
	}
	/** tests if a table with the given name exists
	 */
	public static boolean tableExists(Connection conn,DbTableName tableName)
	{
		try{
			boolean supportsMixedCase=conn.getMetaData().supportsMixedCaseIdentifiers();
			String catalog=conn.getCatalog();
			java.sql.DatabaseMetaData meta=conn.getMetaData();
			// on oracle getUserName() returns schemaname
			// on PostgreSQL "public" is the default default schema; this can be changed by "SET search_path TO newdefschema;" or "ALTER DATABASE mydb SET search_path = public, shared, project1, project2;"
			String schema=tableName.getSchema();
			if(schema==null){
				if(conn.getMetaData().getURL().startsWith("jdbc:postgresql:")){
					schema="public";
				}else{
					schema=meta.getUserName();
				}
			}
			java.sql.ResultSet rs=null;
			try{
				//rs=meta.getTables(catalog,schema,tableName.toUpperCase(),null);
				rs=meta.getTables(null,null,null,null);
				while(rs.next()){
					String db_catalogName=rs.getString("TABLE_CAT");
					String db_schemaName=rs.getString("TABLE_SCHEM");
					String db_tableName=rs.getString("TABLE_NAME");
					//EhiLogger.debug(db_catalogName+"."+db_schemaName+"."+db_tableName);
					if((db_schemaName==null || db_schemaName.equalsIgnoreCase(schema)) && db_tableName.equalsIgnoreCase(tableName.getName())){
						//EhiLogger.debug(db_catalogName+"."+db_schemaName+"."+db_tableName);
						// table exists
						return true;
					}
				}
			}finally{
				if(rs!=null)rs.close();
			}
		}catch(java.sql.SQLException ex){
			throw new IllegalStateException("failed to check if table "+tableName+" exists",ex);
		}
		return false;
	}
	/** tests if a schema with the given name exists
	 */
	public static boolean schemaExists(Connection conn,String schemaName)
	{
		try{
			java.sql.DatabaseMetaData meta=conn.getMetaData();
			java.sql.ResultSet rs=null;
			try{
				//rs=meta.getTables(catalog,schema,tableName.toUpperCase(),null);
				rs=meta.getSchemas();
				while(rs.next()){
					String db_catalogName=rs.getString("TABLE_CATALOG"); // maybe null
					String db_schemaName=rs.getString("TABLE_SCHEM");
					if(db_schemaName.equalsIgnoreCase(schemaName) ){
						//EhiLogger.debug(db_catalogName+"."+db_schemaName+"."+db_tableName);
						// table exists
						return true;
					}
				}
			}finally{
				if(rs!=null)rs.close();
			}
		}catch(java.sql.SQLException ex){
			throw new IllegalStateException("failed to check if schema "+schemaName+" exists",ex);
		}
		return false;
	}
	public static void createSchema(Connection conn,String schemaName)
	{
		try {
			java.sql.Statement stmt=conn.createStatement();
			String sql="CREATE SCHEMA "+schemaName;
			EhiLogger.traceBackendCmd(sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new IllegalStateException("failed to create schema "+schemaName,e);
		}
	}
	
}
