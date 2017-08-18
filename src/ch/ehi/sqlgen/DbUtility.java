/* This file is part of the ehisqlgen project.
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
package ch.ehi.sqlgen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			java.sql.DatabaseMetaData meta=conn.getMetaData();
			if(meta.getURL().startsWith("jdbc:postgresql:")){
				return pgTableExists(conn,tableName);
			}
			if(meta.getURL().startsWith("jdbc:ili2fgdb:")){
				return fgdbTableExists(conn, tableName);
			}
			String catalog=conn.getCatalog();
			// on oracle getUserName() returns schemaname
			// on PostgreSQL "public" is the default default schema; this can be changed by "SET search_path TO newdefschema;" or "ALTER DATABASE mydb SET search_path = public, shared, project1, project2;"
			String schema=tableName.getSchema();
			if(schema==null){
				schema=meta.getUserName();
				if(conn.getMetaData().getURL().startsWith("jdbc:postgresql:")){
					schema="public";
				}else{
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
	private static boolean fgdbTableExists(Connection conn,
			DbTableName tableName) throws SQLException {
		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt=conn.createStatement();
			rs=stmt.executeQuery("SELECT NULL AS dy FROM "+tableName.getName()+"");
		} catch (SQLException e) {
			if(e.getErrorCode()==-2147220655){
				return false;
			}
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
				rs=null;
			}
			if(stmt!=null){
				stmt.close();
				stmt=null;
			}
		}
		return true;
	}
	private static boolean pgTableExists(Connection conn, DbTableName tableName) {
		String schema=tableName.getSchema();
		if(schema==null){
			schema="public";
		}else{
			schema=schema.toLowerCase();
		}
		String table=tableName.getName().toLowerCase();
		try{
			java.sql.PreparedStatement stmt=null;
			ResultSet rs=null;
			try{
				stmt=conn.prepareStatement("SELECT 1 FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace  WHERE  n.nspname = ? AND c.relname = ? "); //  AND    c.relkind = 'r'    -- only tables
				stmt.setString(1, schema);
				stmt.setString(2, table);
				rs=stmt.executeQuery();
				if(rs.next()){
					return true;
				}
				return false;
			}finally{
				if(rs!=null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
			}
		}catch(SQLException ex){
			throw new IllegalStateException("failed to check if table "+tableName+" exists",ex);
		}
	}
	/** tests if a table with the given name exists
	 */
	static public boolean sequenceExists(Connection conn,DbTableName tableName)
	{
		try{
			java.sql.DatabaseMetaData meta=conn.getMetaData();
			if(meta.getURL().startsWith("jdbc:postgresql:")){
				return pgTableExists(conn,tableName);
			}
			// on oracle getUserName() returns schemaname
			// on PostgreSQL "public" is the defualt schema
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
			throw new IllegalStateException("failed to check if sequence "+tableName+" exists");
		}
		return false;
	}
	/** 
	 * tests if a schema with the given name exists
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

	public static void executeSqlScript(Connection conn,java.io.InputStreamReader script)
	{
		java.io.PushbackReader reader=null;
		reader = new java.io.PushbackReader(script);
		try{
			String line = readSqlStmt(reader);
			while(line!=null){
				// exec sql
				line=line.trim();
				if(line.length()>0){
					Statement dbstmt = null;
					try{
						try{
							dbstmt = conn.createStatement();
							EhiLogger.traceBackendCmd(line);
							dbstmt.execute(line);
						}finally{
							dbstmt.close();
						}
					}catch(SQLException ex){
						throw new IllegalStateException(ex);
					}
					
				}
				// read next line
				line=readSqlStmt(reader);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	public static String readSqlStmt(java.io.PushbackReader reader)
			throws IOException {
		StringBuffer stmt=new StringBuffer();
		int c=reader.read();
		while(c!=-1){
			if(c=='-'){
				c=reader.read();
				if(c==-1){
					stmt.append('-');
					break;
				}else if(c=='-'){
					c=reader.read();
					while(c!=-1){
						if(c=='\n'){
							c=reader.read();
							if(c!=-1 && c!='\r'){
								reader.unread(c);
							}
							break;
						}else if(c=='\r'){
							c=reader.read();
							if(c!=-1 && c!='\n'){
								reader.unread(c);
							}
							break;
						}
						c=reader.read();
					}
				}else{
					stmt.append('-');
					stmt.append((char)c);
				}
			}else if(c=='\''){ 
				stmt.append((char)c);
				while(true){
					c=reader.read();
					if(c==-1){
						break;
					}else if(c=='\''){ 
						c=reader.read();
						if(c==-1){
							// eof
							break;
						}else if(c=='\''){
							stmt.append('\'');
							stmt.append('\'');
						}else{
							reader.unread(c);
							break;
						}
					}else{
						stmt.append((char)c);
					}
				}
				stmt.append('\'');
			}else if(c==';'){
				stmt.append((char)c);
				// skip end of line
				c=reader.read();
				if(c=='\n'){
					c=reader.read();
					if(c!=-1 && c!='\r'){
						reader.unread(c);
					}
				}else if(c=='\r'){
					c=reader.read();
					if(c!=-1 && c!='\n'){
						reader.unread(c);
					}
				}else{
					if(c!=-1){
						reader.unread(c);
						
					}
				}
				break;
			}else if(c=='\n'){
				c=reader.read();
				if(c!=-1 && c!='\r'){
					reader.unread(c);
				}
			}else if(c=='\r'){
				c=reader.read();
				if(c!=-1 && c!='\n'){
					reader.unread(c);
				}
			}else{
				stmt.append((char)c);
			}
			c=reader.read();
		}
		if(stmt.length()==0){
			return null;
		}
		return stmt.toString();
	}
}
