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

import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbTableName;


/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 15.03.2007 $
 */
public class TableBasedIdGen implements DbIdGen {
	public final static String SQL_T_KEY_OBJECT="T_KEY_OBJECT";
	public final static String SQL_T_KEY="T_Key";
	public final static String SQL_T_LASTUNIQUEID="T_LastUniqueId";
	java.sql.Connection conn=null;
	String dbusr=null;
	String schema=null;
	@Override
	public void init(String schema,Config config)
	{
		this.schema=schema;
	}
	@Override
	public void initDb(java.sql.Connection conn,String dbusr)
	{
		this.conn=conn;
		this.dbusr=dbusr;
	}
//	CREATE TABLE T_Key_Object (
//		   T_Key                Text(30) NOT NULL,
//		   T_LastUniqueId       LONG NOT NULL,
//		   T_CreateDate         DATE NOT NULL,
//		   T_LastChange         DATE NOT NULL,
//		   T_User               TEXT(30) NOT NULL,
//		   CONSTRAINT PK PRIMARY KEY (T_Key)
//	);
	 
	@Override
	public void addMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),SQL_T_KEY_OBJECT));
		ch.ehi.sqlgen.repository.DbColVarchar t_key=new ch.ehi.sqlgen.repository.DbColVarchar();
		t_key.setName(SQL_T_KEY);
		t_key.setNotNull(true);
		t_key.setPrimaryKey(true);
		t_key.setSize(30);
		tab.addColumn(t_key);
		ch.ehi.sqlgen.repository.DbColNumber t_lastuniqueid=new ch.ehi.sqlgen.repository.DbColNumber();
		t_lastuniqueid.setName(SQL_T_LASTUNIQUEID);
		t_lastuniqueid.setNotNull(true);
		tab.addColumn(t_lastuniqueid);
		ch.ehi.ili2db.converter.AbstractRecordConverter.addStdCol(tab);
		schema.addTable(tab);
	}
	public long getCount(String key)
	{
		String sqlName=SQL_T_KEY_OBJECT;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.PreparedStatement getstmt=null;
		java.sql.ResultSet res=null;
		try{
			String stmt="SELECT "+SQL_T_LASTUNIQUEID+" FROM "+sqlName+" WHERE "+SQL_T_KEY+"= ?";
			EhiLogger.traceBackendCmd(stmt);
			getstmt=conn.prepareStatement(stmt);
			getstmt.setString(1,key);
			res=getstmt.executeQuery();
			long ret=0;
			if(res.next()){
				ret=res.getLong(1);
				return ret;
			}
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to query "+sqlName,ex);
		}finally{
            if(res!=null) {
                try {
                    res.close();
                } catch (SQLException e) {
                    EhiLogger.logError(e);
                }
                res=null;
            }
			if(getstmt!=null){
				try{
					getstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
				getstmt=null;
			}
		}
			
		// INSERT
		long ret=0;
		java.sql.Timestamp today=new java.sql.Timestamp(System.currentTimeMillis());
		String stmt="INSERT INTO "+sqlName+" ("+SQL_T_KEY+","+SQL_T_LASTUNIQUEID+",T_LastChange,T_CreateDate,T_User) VALUES (?,?,?,?,?)";
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement updstmt=null;
		try{
			updstmt = conn.prepareStatement(stmt);
			updstmt.setString(1, key);
			updstmt.setLong(2, ret);
			updstmt.setTimestamp(3, today);
			updstmt.setTimestamp(4, today);
			updstmt.setString(5, dbusr);
			updstmt.executeUpdate();
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to insert "+sqlName,ex);
		}finally{
			if(updstmt!=null){
				try{
					updstmt.close();
				}catch(java.sql.SQLException ex){
					EhiLogger.logError(ex);
				}
			}
		}
		return ret;
	}
	public void setCount(long newId,String key)
	{
		String sqlName=SQL_T_KEY_OBJECT;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		// update entry
		java.sql.Timestamp today=new java.sql.Timestamp(System.currentTimeMillis());
		String stmt="UPDATE "+sqlName+" SET "+SQL_T_LASTUNIQUEID+"=?,T_LastChange=?,T_User=? WHERE "+SQL_T_KEY+"=?";
		EhiLogger.traceBackendCmd(stmt);
		java.sql.PreparedStatement updstmt = null;
		try{
			updstmt = conn.prepareStatement(stmt);
			updstmt.setLong(1, newId);
			updstmt.setTimestamp(2, today);
			updstmt.setString(3, dbusr);
			updstmt.setString(4, key);
			updstmt.executeUpdate();
		}catch(java.sql.SQLException ex){
			EhiLogger.logError("failed to update "+sqlName,ex);
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

	@Override
	public void initDbDefs(ch.ehi.sqlgen.generator.Generator gen) {
	}
	/** gets a new obj id.
	 */
	long idBlockStart=0;
	long lastLocalId=0;
	@Override
	public long newObjSqlId(){
		long ret;
		final long BLOCK_SIZE=20;
		if(lastLocalId!=0 && lastLocalId<BLOCK_SIZE){
			lastLocalId++;
			ret=idBlockStart+lastLocalId;
		}else{
			lastLocalId=1;
			ret=idBlockStart=getCount("T_Id")+lastLocalId;
			setCount(idBlockStart+BLOCK_SIZE,"T_Id");
		}
		return ret;
	}
	@Override
	public long getLastSqlId()
	{
		return idBlockStart+lastLocalId;
	}

	@Override
	public String getDefaultValueSql() {
		return null;
	}

}
