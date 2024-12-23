/* This file is part of the uml2db project.
 * For more information, please see <http://www.umleditor.org>.
 *
 * Copyright (c) 2006 Eisenhut Informatik AG
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package ch.ehi.ili2duckdb;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.generator.SqlConfiguration;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc.Stmt;
import ch.ehi.sqlgen.repository.*;

/**
 * @author sz
 */
public class GeneratorDuckDB extends GeneratorJdbc {

	private boolean createGeomIdx=false;
	private ArrayList<DbColumn> indexColumns=null;
	
	@Override
	public void visitSchemaBegin(Settings config, DbSchema schema)
			throws IOException {
	    super.visitSchemaBegin(config, schema);

        if ("True".equalsIgnoreCase(config.getValue(SqlConfiguration.CREATE_GEOM_INDEX))) {
            createGeomIdx = true;
        }
	}

	@Override
	public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
		String type="";
		String size="";
		String notSupported=null;
		boolean createColNow=true;
		if(column instanceof DbColBoolean){
			type="BOOLEAN";
		}else if(column instanceof DbColDateTime){
			type="TIMESTAMP";
		}else if(column instanceof DbColDate){
			type="DATE";
		}else if(column instanceof DbColTime){
			type="TIME";
		}else if(column instanceof DbColDecimal){
			DbColDecimal col=(DbColDecimal)column;
			type="DECIMAL("+Integer.toString(col.getSize())+","+Integer.toString(col.getPrecision())+")";
		}else if(column instanceof DbColGeometry){
		    DbColGeometry geo=(DbColGeometry)column;
		    if(!geo.getSrsAuth().equals("EPSG")) {
		        throw new IllegalArgumentException("unexpected SrsAuth <"+geo.getSrsAuth()+">");
		    }
            //type="geometry("+getH2gisType(geo.getType())+(geo.getDimension()==3?",Z":"")+","+geo.getSrsId()+")";
            type="GEOMETRY";
		}else if(column instanceof DbColId){
			type="BIGINT";
		}else if(column instanceof DbColUuid){
			type="UUID";
		}else if(column instanceof DbColNumber){
			DbColNumber col=(DbColNumber)column;
            if(col.getSize()>10 
                    || (col.getMaxValue()!=null && col.getMaxValue()>(long)Integer.MAX_VALUE)
                    || (col.getMinValue()!=null && col.getMinValue()<(long)Integer.MIN_VALUE)) {
                type="BIGINT";
            }else {
                type="INTEGER";
            }
		}else if(column instanceof DbColVarchar){
			int colsize=((DbColVarchar)column).getSize();
			if(colsize==DbColVarchar.UNLIMITED){
                type="VARCHAR";
			}else{
				type="VARCHAR("+Integer.toString(colsize)+")";
			}
		}else if(column instanceof DbColBlob){
			type="BLOB";
		}else if(column instanceof DbColXml){
			type="VARCHAR";
        }else if(column instanceof DbColJson){
            type="JSON";
		}else{
			type="VARCHAR";
		}
        if(column.getArraySize()!=DbColumn.NOT_AN_ARRAY && !(column instanceof DbColJson)) {
            String isNull=column.isNotNull()?"NOT NULL":"NULL";
            type="TEXT";
            String name=column.getName();
            out.write(getIndent()+colSep+name+" "+type+" "+isNull+newline());
            colSep=",";
        }else {
            String sep=" ";
            String defaultValue="";
            if(column.getDefaultValue()!=null){
                defaultValue=sep+"DEFAULT "+column.getDefaultValue();
            }
            String isNull=sep+(column.isNotNull()?"NOT NULL":"NULL");
            if(column.isPrimaryKey()){
                isNull=sep+"PRIMARY KEY";
            }            
            if(column.isIndex() || (createGeomIdx && column instanceof DbColGeometry)){
                // just collect it; process it later
                indexColumns.add(column);
            }
            if(createColNow){
                String name=column.getName();
                out.write(getIndent()+colSep+name+" "+type+defaultValue+isNull+newline());
                colSep=",";
            }
        }
	}
	
	@Override
	public void visit1TableBegin(DbTable tab) throws IOException {
		super.visit1TableBegin(tab);
		indexColumns=new ArrayList<DbColumn>();
	}
	
	@Override
	public void visitTableEndColumn(DbTable tab) throws IOException {
	}

	@Override
	public void visitIndex(DbIndex idx) throws IOException {
		if(idx.isUnique()){
			java.io.StringWriter out = new java.io.StringWriter();
			DbTable tab=idx.getTable();
			String tableName=tab.getName().getQName();
			String constraintName=idx.getName();
			if(constraintName==null){
				String colNames[]=new String[idx.sizeAttr()];
				int i=0;
				for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
					DbColumn attr=(DbColumn)attri.next();
					colNames[i++]=attr.getName();
				}
				constraintName=createConstraintName(tab,"key", colNames);
			}
			out.write(getIndent()+"CREATE UNIQUE INDEX "+constraintName+" ON "+tableName+" (");
			String sep="";
			for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
				DbColumn attr=(DbColumn)attri.next();

				if (attr instanceof DbColGeometry) {
					if (((DbColGeometry) attr).getType() == DbColGeometry.POINT) {
						// only for points?
					}			
					//out.write(sep+"ST_AsWKB("+attr.getName()+")");
				} else {
					out.write(sep+attr.getName());
				}				
				sep=",";
			}
			out.write(")"+newline());
			String stmt=out.toString();
			addCreateLine(new Stmt(stmt));
			out=null;
			if(conn!=null) {
	            if(createdTables.contains(tab.getName())){
	                Statement dbstmt = null;
	                try{
	                    try{
	                        dbstmt = conn.createStatement();
	                        EhiLogger.traceBackendCmd(stmt);
	                        dbstmt.executeUpdate(stmt);
	                    }finally{
	                        dbstmt.close();
	                    }
	                }catch(SQLException ex){
	                    IOException iox=new IOException("failed to add UNIQUE to table "+tab.getName());
	                    iox.initCause(ex);
	                    throw iox;
	                }
	            }
			}
		}
	}

    @Override
    protected String getTableEndOptions(DbTable tab) {
        return "";
    }
	@Override
	public void visit1TableEnd(DbTable tab) throws IOException {
		String sqlTabName=tab.getName().getName();
		if(tab.getName().getSchema()!=null){
			sqlTabName=tab.getName().getSchema()+"."+sqlTabName;
		}
		boolean tableExists=tableExists(conn,tab.getName());
		super.visit1TableEnd(tab);		
		{
	        String cmt = tab.getComment();
	        if (cmt != null) {
	            String cmtstmt="COMMENT ON TABLE " +sqlTabName+" IS '" + escapeString(cmt) + "';";
	            addCreateLine(new Stmt(cmtstmt));
	            
	            if(conn!=null) {
	                if(!tableExists){
	                    Statement dbstmt = null;
	                    try{
	                        try{
	                            dbstmt = conn.createStatement();
	                            EhiLogger.traceBackendCmd(cmtstmt);
	                            dbstmt.execute(cmtstmt);
	                        }finally{
	                            dbstmt.close();
	                        }
	                    }catch(SQLException ex){
	                        IOException iox=new IOException("failed to add comment on table "+tab.getName());
	                        iox.initCause(ex);
	                        throw iox;
	                    }
	                }
	            }
	        }
	        
            Iterator<DbColumn> coli = tab.iteratorColumn();
            while (coli.hasNext()) {
                DbColumn col = (DbColumn) coli.next();
                cmt = col.getComment();
                if (cmt != null) {
                    cmt = "COMMENT ON COLUMN " + sqlTabName + "." + col.getName() + " IS '" + escapeString(cmt) + "'";
                    addCreateLine(new Stmt(cmt));
                    if (conn != null) {
                        if (!tableExists) {
                            Statement dbstmt = null;
                            try {
                                try {
                                    dbstmt = conn.createStatement();
                                    EhiLogger.traceBackendCmd(cmt);
                                    dbstmt.execute(cmt);
                                } finally {
                                    dbstmt.close();
                                }
                            } catch (SQLException ex) {
                                IOException iox = new IOException("failed to add comment to table " + tab.getName());
                                iox.initCause(ex);
                                throw iox;
                            }
                        }
                    }
                }
            }
		}
		
		for(DbColumn idxcol:indexColumns){
			String idxstmt=null;
			String idxName=createConstraintName(tab,"idx",idxcol.getName().toLowerCase());
			if(idxcol instanceof DbColGeometry){
				idxstmt="CREATE INDEX "+idxName+" ON "+sqlTabName.toLowerCase()+" USING RTREE ( "+idxcol.getName().toLowerCase()+" )";
			}else{
				idxstmt="CREATE INDEX "+idxName+" ON "+sqlTabName.toLowerCase()+" ( "+idxcol.getName().toLowerCase()+" )";
			}
			addCreateLine(new Stmt(idxstmt));
			
            if(conn!=null) {
                if(!tableExists){
                    Statement dbstmt = null;
                    try{
                        try{
                            dbstmt = conn.createStatement();
                            EhiLogger.traceBackendCmd(idxstmt);                            
                            dbstmt.execute(idxstmt);
                        }finally{
                            dbstmt.close();
                        }
                    }catch(SQLException ex){
                        IOException iox=new IOException("failed to add index on column "+tab.getName()+"."+idxcol.getName());
                        iox.initCause(ex);
                        throw iox;
                    }
                }
            }
		}
		indexColumns=null;
	}

	static public String escapeString(String cmt)
	{
		StringBuilder ret=new StringBuilder((int)cmt.length());
		for(int i=0;i<cmt.length();i++){
			char c=cmt.charAt(i);
			ret.append(c);
			if(c=='\''){
				ret.append(c);
			}
		}
		return ret.toString();
	}

	@Override
	public void visitTableBeginConstraint(DbTable dbTab) throws IOException {
		super.visitTableBeginConstraint(dbTab);
		
		// TODO
		// DuckDB does not yet support "ADD CONSTRAINT". 
		String sqlTabName=dbTab.getName().getQName();
//		for(Iterator dbColi=dbTab.iteratorColumn();dbColi.hasNext();){
//			DbColumn dbCol=(DbColumn) dbColi.next();
//			if(false && dbCol.getReferencedTable()!=null){
//	            if(dbCol.getArraySize()==DbColumn.NOT_AN_ARRAY) {
//	                String createstmt=null;
//	                String action="";
//	                if(dbCol.getOnUpdateAction()!=null){
//	                    action=action+" ON UPDATE "+dbCol.getOnUpdateAction();
//	                }
//	                if(dbCol.getOnDeleteAction()!=null){
//	                    action=action+" ON DELETE "+dbCol.getOnDeleteAction();
//	                }
//	                String constraintName=createConstraintName(dbTab,"fkey",dbCol.getName());
//	                //  ALTER TABLE ce.classb1 ADD CONSTRAINT classb1_t_id_fkey FOREIGN KEY ( t_id ) REFERENCES ce.classa1;
//	                // MySQL checks foreign key constraints immediately; the check is not deferred to transaction commit. According to the SQL standard, the default behavior should be deferred checking.
//	                createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" FOREIGN KEY ( "+dbCol.getName()+" ) REFERENCES "+dbCol.getReferencedTable().getQName()
//	                        +" ( "+getPrimaryKeyColumn(dbTab.getSchema(),dbCol.getReferencedTable()).getName()+" )";
//	                
//	                //  ALTER TABLE ce.classb1 DROP CONSTRAINT classb1_t_id_fkey;
//	                String dropstmt=null;
//	                dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;
//
//	                addConstraint(dbTab, constraintName,createstmt, dropstmt);
//	            }
//			}
//			if(dbCol instanceof DbColNumber && (((DbColNumber)dbCol).getMinValue()!=null || ((DbColNumber)dbCol).getMaxValue()!=null)){
//	            if(dbCol.getArraySize()==DbColumn.NOT_AN_ARRAY) {
//	                DbColNumber dbColNum=(DbColNumber)dbCol;
//	                String createstmt=null;
//	                String action="";
//	                if(dbColNum.getMinValue()!=null || dbColNum.getMaxValue()!=null){
//	                    if(dbColNum.getMaxValue()==null){
//	                        action=">="+dbColNum.getMinValue();
//	                    }else if(dbColNum.getMinValue()==null){
//	                        action="<="+dbColNum.getMaxValue();
//	                    }else{
//	                        action="BETWEEN "+dbColNum.getMinValue()+" AND "+dbColNum.getMaxValue();
//	                    }
//	                }
//	                String constraintName=createConstraintName(dbTab,"check",dbCol.getName());
//	                //  ALTER TABLE ce.classb1 ADD CONSTRAINT classb1_attr_check CHECK (attr BETWEEN 0 AND 50);
//	                createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action+")";
//	                
//	                //  ALTER TABLE ce.classb1 DROP CONSTRAINT classb1_t_id_fkey;
//	                String dropstmt=null;
//	                dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;
//
//	                addConstraint(dbTab, constraintName,createstmt, dropstmt);
//	            }
//			}else if(dbCol instanceof DbColDecimal && (((DbColDecimal)dbCol).getMinValue()!=null || ((DbColDecimal)dbCol).getMaxValue()!=null)){
//	            if(dbCol.getArraySize()==DbColumn.NOT_AN_ARRAY) {
//	                DbColDecimal dbColNum=(DbColDecimal)dbCol;
//	                String createstmt=null;
//	                String action="";
//	                if(dbColNum.getMinValue()!=null || dbColNum.getMaxValue()!=null){
//	                    if(dbColNum.getMaxValue()==null){
//	                        action=">="+dbColNum.getMinValue();
//	                    }else if(dbColNum.getMinValue()==null){
//	                        action="<="+dbColNum.getMaxValue();
//	                    }else{
//	                        action="BETWEEN "+dbColNum.getMinValue()+" AND "+dbColNum.getMaxValue();
//	                    }
//	                }
//	                String constraintName=createConstraintName(dbTab,"check",dbCol.getName());
//	                //  ALTER TABLE ce.classb1 ADD CONSTRAINT classb1_attr_check CHECK (attr BETWEEN 0 AND 50);
//	                createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action+")";
//	                
//	                //  ALTER TABLE ce.classb1 DROP CONSTRAINT classb1_t_id_fkey;
//	                String dropstmt=null;
//	                dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;
//
//	                addConstraint(dbTab, constraintName,createstmt, dropstmt);
//	            }
//			}
//            if(dbCol instanceof DbColVarchar && ((DbColVarchar)dbCol).getValueRestriction()!=null){
//                if(dbCol.getArraySize()==DbColumn.NOT_AN_ARRAY) {
//                    DbColVarchar dbColTxt=(DbColVarchar)dbCol;
//                    String createstmt=null;
//                    StringBuffer action=new StringBuffer("IN (");
//                    String sep="";
//                    for(String restrictedValue:dbColTxt.getValueRestriction()){
//                        action.append(sep);
//                        action.append("'");
//                        action.append(escapeString(restrictedValue));
//                        action.append("'");
//                        sep=",";
//                    }
//                    action.append(")");
//                    String constraintName=createConstraintName(dbTab,"check",dbCol.getName());
//                    //  ALTER TABLE ce.classb1 ADD CONSTRAINT classb1_attr_check CHECK (attr IN ('a','b'));
//                    createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action.toString()+")";
//                    
//                    //  ALTER TABLE ce.classb1 DROP CONSTRAINT classb1_t_id_fkey;
//                    String dropstmt=null;
//                    dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;
//
//                    addConstraint(dbTab, constraintName,createstmt, dropstmt);
//                }
//            }
//		}
	}

    private DbColumn getPrimaryKeyColumn(DbSchema schema,DbTableName referencedTableName) {
        DbTable referencedTable=schema.findTable(referencedTableName);
        for(Iterator<DbColumn> dbColIt=referencedTable.iteratorColumn();dbColIt.hasNext();) {
            DbColumn dbCol=dbColIt.next();
            if(dbCol.isPrimaryKey()) {
                return dbCol;
            }
        }
        return null;
    }
    public static boolean tableExists(Connection conn,DbTableName tableName)
    {
        try{
            if(conn!=null) {
                java.sql.DatabaseMetaData meta=conn.getMetaData();
                String catalog=conn.getCatalog();
                String schema=tableName.getSchema();
                if(schema==null){
                    schema=meta.getUserName();
                    if(schema==null || schema.equals("")) {
                        schema="main";
                    }
                }
                java.sql.ResultSet rs=null;
                try{
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
                    if(rs!=null) {
                        rs.close();
                        rs=null;
                    }
                }
            }
        }catch(java.sql.SQLException ex){
            throw new IllegalStateException("failed to check if table "+tableName+" exists",ex);
        }
        return false;
    }

}
