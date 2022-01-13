package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.HashSet;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.StatementExecutionHelper;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;

public class ColumnNameMapping {
	/** mapping from a qualified interlis attribute name to a sql column name.
	 * Maintained by addMapping().
	 */
	private HashMap<AttrMappingKey,String> attrNameIli2sql=new HashMap<AttrMappingKey,String>();
	private HashMap<String,HashSet<String>> tables=new HashMap<String,HashSet<String>>();
	private Integer batchSize = null;

	public ColumnNameMapping() {
	}

	public ColumnNameMapping(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void addAttrNameMapping(String iliname,String sqlname,String ownerSqlTablename,String targetSqlTablename)
	{
		if(ownerSqlTablename==null){
			throw new IllegalArgumentException("ownerSqlTablename==null");
		}

		attrNameIli2sql.put(new AttrMappingKey(iliname,ownerSqlTablename,targetSqlTablename),sqlname);
		
		HashSet<String> colNames=null;
		if(tables.containsKey(ownerSqlTablename)){
			colNames=tables.get(ownerSqlTablename);
		}else{
			colNames=new HashSet<String>();
			tables.put(ownerSqlTablename, colNames);
		}
		if(colNames.contains(sqlname)){
			throw new IllegalArgumentException("duplicate column "+sqlname+" in table "+ownerSqlTablename);
		}
		colNames.add(sqlname);
	}
	public String getSqlName(String iliAttrqname,String ownerSqlTablename,String targetSqlTablename)
	{
		if(ownerSqlTablename==null){
			throw new IllegalArgumentException("ownerSqlTablename==null");
		}
		return attrNameIli2sql.get(new AttrMappingKey(iliAttrqname,ownerSqlTablename,targetSqlTablename));
	}
	private static HashSet<AttrMappingKey> readAttrMappingTableEntries(java.sql.Connection conn,String schema,boolean isVer3_export)
	throws Ili2dbException
	{
		HashSet<AttrMappingKey> ret=new HashSet<AttrMappingKey>();
		String sqlName=DbNames.ATTRNAME_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
            exstStmt="SELECT "+DbNames.ATTRNAME_TAB_ILINAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+","+DbNames.ATTRNAME_TAB_TARGET_COL+" FROM "+sqlName;
			if(isVer3_export) {
	            exstStmt="SELECT "+DbNames.ATTRNAME_TAB_ILINAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL_VER3+","+DbNames.ATTRNAME_TAB_TARGET_COL+" FROM "+sqlName;
			}
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
            java.sql.ResultSet rs=null;
			try{
				rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliCode=rs.getString(1);
					String owner=rs.getString(2);
					String target=rs.getString(3);
					ret.add(new AttrMappingKey(iliCode,owner,target));
				}
			}finally{
			    if(rs!=null) {
	                rs.close();
	                rs=null;
			    }
			    if(exstPrepStmt!=null) {
	                exstPrepStmt.close();
	                exstPrepStmt=null;
			    }
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read attr-mapping-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateAttrMappingTable(GeneratorJdbc gen, java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String mapTabName=DbNames.ATTRNAME_TAB;
		if(schema!=null){
			mapTabName=schema+"."+mapTabName;
		}
		if(conn!=null) {
	        HashSet<AttrMappingKey> exstEntries=readAttrMappingTableEntries(conn,schema,false);
	        // create table
	        try{

	            // insert mapping entries
	            String stmt="INSERT INTO "+mapTabName+" ("+DbNames.ATTRNAME_TAB_ILINAME_COL+","+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+","+DbNames.ATTRNAME_TAB_TARGET_COL+") VALUES (?,?,?,?)";
	            EhiLogger.traceBackendCmd(stmt);
	            java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
	            AttrMappingKey entry1=null;
				StatementExecutionHelper seHelper = new StatementExecutionHelper(batchSize);
	            try{
					long start = System.currentTimeMillis();
	                for(AttrMappingKey entry:attrNameIli2sql.keySet()){
	                    if(!exstEntries.contains(entry)){
	                        entry1=entry;
	                        String sqlname=attrNameIli2sql.get(entry);
	                        ps.setString(1, entry.getIliname());
	                        ps.setString(2, sqlname);
	                        ps.setString(3, entry.getOwner());
	                        ps.setString(4, entry.getTarget());
	                        seHelper.write(ps);
	                    }

	                }

	                seHelper.flush(ps);
	            }catch(java.sql.SQLException ex){
	                throw new Ili2dbException("failed to insert attrname-mapping "+entry1,ex);
	            }finally{
	                ps.close();
	            }
	        }catch(java.sql.SQLException ex){       
	            throw new Ili2dbException("failed to update mapping-table "+mapTabName,ex);
	        }
		}
		if(gen!=null){
            // create inserts of mapping entries
            for(AttrMappingKey entry:attrNameIli2sql.keySet()){
                String sqlname=attrNameIli2sql.get(entry);
                String target=entry.getTarget();
                String stmt="INSERT INTO "+mapTabName+" ("+DbNames.ATTRNAME_TAB_ILINAME_COL+","+DbNames.ATTRNAME_TAB_SQLNAME_COL+","+DbNames.ATTRNAME_TAB_COLOWNER_COL+","+DbNames.ATTRNAME_TAB_TARGET_COL
                        +") VALUES ("+Ili2db.quoteSqlStringValue(entry.getIliname())+","+Ili2db.quoteSqlStringValue(sqlname)+","+Ili2db.quoteSqlStringValue(entry.getOwner())+","+Ili2db.quoteSqlStringValue(target)+")";
                gen.addCreateLine(gen.new Stmt(stmt));
            }
		    
		}

	}
	public void readAttrMappingTable(java.sql.Connection conn,String schema,boolean isVer3_export)
	throws Ili2dbException
	{
		String mapTableName=DbNames.ATTRNAME_TAB;
		if(schema!=null){
			mapTableName=schema+"."+mapTableName;
		}
		// create table
		String ownerCol=DbNames.ATTRNAME_TAB_COLOWNER_COL;
        if(isVer3_export) {
            ownerCol=DbNames.ATTRNAME_TAB_COLOWNER_COL_VER3;
        }
		String stmt="SELECT "+DbNames.ATTRNAME_TAB_ILINAME_COL+", "+DbNames.ATTRNAME_TAB_SQLNAME_COL+", "+ownerCol+", "+DbNames.ATTRNAME_TAB_TARGET_COL+" FROM "+mapTableName;
		java.sql.Statement dbstmt = null;
        java.sql.ResultSet rs=null;
		try{
			
			dbstmt = conn.createStatement();
			rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String iliname=rs.getString(DbNames.ATTRNAME_TAB_ILINAME_COL);
				String sqlname=rs.getString(DbNames.ATTRNAME_TAB_SQLNAME_COL);
				String owner=rs.getString(ownerCol);
				String target=rs.getString(DbNames.ATTRNAME_TAB_TARGET_COL);
				//EhiLogger.debug("map: "+iliname+"->"+sqlname);
				addAttrNameMapping(iliname,sqlname,owner,target);
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to query mapping-table "+mapTableName,ex);
		}finally{
            if(rs!=null){
                try{
                    rs.close();
                }catch(java.sql.SQLException ex){       
                    throw new Ili2dbException("failed to close query of "+mapTableName,ex);
                }
                rs=null;
            }
			if(dbstmt!=null){
				try{
					dbstmt.close();
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to close query of "+mapTableName,ex);
				}
				dbstmt=null;
			}
		}

	}
	public boolean existsSqlName(String ownerSqlTablename, String sqlname) {
		if(ownerSqlTablename==null){
			throw new IllegalArgumentException("ownerSqlTablename==null");
		}
		if(tables.containsKey(ownerSqlTablename)){
			HashSet<String> colNames=tables.get(ownerSqlTablename);
			if(colNames.contains(sqlname)){
				return true;
			}
		}
		return false;
	}

}
