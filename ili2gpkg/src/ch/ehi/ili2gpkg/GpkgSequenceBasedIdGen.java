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
package ch.ehi.ili2gpkg;

import java.io.IOException;
import java.sql.Connection;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.sqlgen.repository.DbTableName;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 15.03.2007 $
 */
public class GpkgSequenceBasedIdGen implements DbIdGen {
	public final static String SQL_ILI2DB_SEQ_NAME="t_ili2db_seq";
	java.sql.Connection conn=null;
	String dbusr=null;
	String schema=null;
	@Override
	public void init(String schema) {
		this.schema=schema;
	}
	@Override
	public void initDb(Connection conn, String dbusr) {
		this.conn=conn;
		this.dbusr=dbusr;
	}
	@Override
	public void addMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
	}

	/** tests if a table with the given name exists
	 */
	public boolean sequenceExists(DbTableName tableName)
	throws IOException
	{
		try{
			boolean supportsMixedCase=conn.getMetaData().supportsMixedCaseIdentifiers();
			String catalog=conn.getCatalog();
			java.sql.DatabaseMetaData meta=conn.getMetaData();
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
			IOException iox=new IOException("failed to check if table "+tableName+" exists");
			iox.initCause(ex);
			throw iox;
		}
		return false;
	}
	
	@Override
	public void initDbDefs() {
		String sqlName=SQL_ILI2DB_SEQ_NAME;
		try {
			if(sequenceExists(new DbTableName(schema,sqlName))){
				return;
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		String stmt="CREATE SEQUENCE "+sqlName+";";
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement updstmt = null;
		try{
			updstmt = conn.prepareStatement(stmt);
			updstmt.execute();
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to create sequence "+sqlName,ex);
		}finally{
			if(updstmt!=null){
				try{
					updstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}		
	}
	/** gets a new obj id.
	 */
	int lastLocalId=0;
	@Override
	public int newObjSqlId(){
		lastLocalId=getSeqCount();
		return lastLocalId;
	}
	@Override
	public int getLastSqlId()
	{
		return lastLocalId;
	}
	private int getSeqCount()
	{
		String sqlName=SQL_ILI2DB_SEQ_NAME;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.PreparedStatement getstmt=null;
		try{
			String stmt="SELECT nextval('"+sqlName+"')";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			java.sql.ResultSet res=getstmt.executeQuery();
			int ret=0;
			if(res.next()){
				ret=res.getInt(1);
				return ret;
			}
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to query "+sqlName,ex);
			throw new IllegalStateException(ex);
		}finally{
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		throw new IllegalStateException("no nextval "+sqlName);
	}
	@Override
	public String getDefaultValueSql() {
		String sqlName=SQL_ILI2DB_SEQ_NAME;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		return "nextval('"+sqlName+"')";
	}

}
