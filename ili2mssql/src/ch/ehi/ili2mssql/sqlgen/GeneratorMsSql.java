package ch.ehi.ili2mssql.sqlgen;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.repository.DbColBoolean;
import ch.ehi.sqlgen.repository.DbColDate;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColDecimal;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import ch.ehi.sqlgen.repository.DbColTime;
import ch.ehi.sqlgen.repository.DbColUuid;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbIndex;
import ch.ehi.sqlgen.repository.DbTable;

public class GeneratorMsSql extends GeneratorJdbc {
	
	@Override
	public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
        String type=getMsSqlType(column);
		String isNull=column.isNotNull()?"NOT NULL":"NULL";
		if(column.isPrimaryKey()){
			isNull="PRIMARY KEY";
		}
		String defaultValue="";
		if(column.getDefaultValue()!=null){
            defaultValue=" DEFAULT ("+column.getDefaultValue()+")";
		}
		
		String name=column.getName();
		out.write(getIndent()+colSep+"["+name+"] "+type+" "+isNull+defaultValue+newline());
		colSep=",";
	}
	
	@Override
	public void visitTableEndColumn(DbTable tab) throws IOException {
        DbUtility.tableExists(conn,tab.getName());
	}
	

	@Override
	public void visitTableBeginConstraint(DbTable dbTab) throws IOException {
		super.visitTableBeginConstraint(dbTab);

		for(Iterator dbColi=dbTab.iteratorColumn();dbColi.hasNext();){
			DbColumn dbCol=(DbColumn) dbColi.next();
            if(dbCol.getReferencedTable()!=null){
                writeForeignKey(dbTab, dbCol);
            }
            writeValueRangeNumber(dbTab, dbCol);
		}
	}
	
	@Override
	public void visitIndex(DbIndex idx) throws IOException {
		if(idx.isUnique()){
            StringBuilder out = new StringBuilder();
			DbTable tab=idx.getTable();
			String tableName=tab.getName().getQName();
			String constraintName=idx.getName();
			if(constraintName==null){
                String[] colNames=new String[idx.sizeAttr()];
				int i=0;
				for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
					DbColumn attr=(DbColumn)attri.next();
					colNames[i++]=attr.getName();
				}
				constraintName=createConstraintName(tab,"key", colNames);
			}
            out.append(getIndent()+"CREATE UNIQUE INDEX "+constraintName+" ON "+tableName+" (");
			String sep="";
			
            StringBuilder condition = new StringBuilder(" ");
			String sepCondition = " ";
			
			for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
				DbColumn attr=(DbColumn)attri.next();
				
                out.append(sep+attr.getName());
                condition.append(sepCondition + attr.getName() + " is not null");
					
				sep=",";
				sepCondition = " AND ";
			}
            out.append(") WHERE"+condition.toString()+newline());
			String stmt=out.toString();
			addCreateLine(new Stmt(stmt));
			if(conn!=null&&createdTables.contains(tab.getName())){
                executeUpdateStatement(stmt,"Failed to add UNIQUE to table "+tab.getName());
			}
		}
	}

    private void writeForeignKey(DbTable dbTab, DbColumn dbCol) throws IOException {
        String createstmt=null;
        String action="";
        String sqlTabName=dbTab.getName().getQName();
        
        if(dbCol.getOnUpdateAction()!=null){
            action=action+" ON UPDATE "+dbCol.getOnUpdateAction();
        }
        if(dbCol.getOnDeleteAction()!=null){
            action=action+" ON DELETE "+dbCol.getOnDeleteAction();
        }
        String constraintName=createConstraintName(dbTab,"fkey",dbCol.getName());
        
        createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" FOREIGN KEY ( "+dbCol.getName()+" ) REFERENCES "+dbCol.getReferencedTable().getQName()+action;
        
        String dropstmt=null;
        dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;

        addConstraint(dbTab, constraintName,createstmt, dropstmt);
    }
    
    private void writeValueRangeNumber(DbTable dbTab, DbColumn dbCol) throws IOException {
        String min=getMinValue(dbCol);
        String max=getMaxValue(dbCol);
        if(min!=null||max!=null) {
            String sqlTabName=dbTab.getName().getQName();
            String action="";
            
            if(max==null){
                action=">="+min;
            }else if(min==null){
                action="<="+max;
            }else{
                action="BETWEEN "+min+" AND "+max;
            }
    
            String constraintName=createConstraintName(dbTab,"check",dbCol.getName());
            String createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action+")";
            String dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;
    
            addConstraint(dbTab, constraintName,createstmt, dropstmt);
        }
    }
    
    private String getMinValue(DbColumn dbCol) {
        String result=null;
        if(dbCol instanceof DbColNumber) {
            Long minVal=((DbColNumber)dbCol).getMinValue();
            result=minVal!=null?minVal.toString():null;
         } else if(dbCol instanceof DbColDecimal){
            Double minVal=((DbColDecimal)dbCol).getMinValue();
            result=minVal!=null?minVal.toString():null;
        }
        return result;
    }

    private String getMaxValue(DbColumn dbCol) {
        String result=null;
        if(dbCol instanceof DbColNumber) {
            Long maxVal=((DbColNumber)dbCol).getMaxValue();
            result=maxVal!=null?maxVal.toString():null;
         } else if(dbCol instanceof DbColDecimal){
            Double maxVal=((DbColDecimal)dbCol).getMaxValue();
            result=maxVal!=null?maxVal.toString():null;
        }
        return result;
    }
    
    private String getMsSqlType(DbColumn column) {
        String type;
        if(column instanceof DbColBoolean){
            type="BIT";
        }else if(column instanceof DbColDateTime){
            type="DATETIME";
        }else if(column instanceof DbColDate){
            type="DATE";
        }else if(column instanceof DbColTime){
            type="TIME";
        }else if(column instanceof DbColDecimal){
            DbColDecimal col=(DbColDecimal)column;
            type="DECIMAL("+Integer.toString(col.getSize())+","+Integer.toString(col.getPrecision())+")";
        }else if(column instanceof DbColGeometry){
            type="GEOMETRY";
        }else if(column instanceof DbColId){
            type="BIGINT";
        }else if(column instanceof DbColUuid){
            type="UNIQUEIDENTIFIER";
        }else if(column instanceof DbColNumber){
            DbColNumber col=(DbColNumber)column;
            type="NUMERIC("+Integer.toString(col.getSize())+")";
        }else if(column instanceof DbColVarchar){
            int colsize=((DbColVarchar)column).getSize();
            if(colsize!=DbColVarchar.UNLIMITED)
                type="VARCHAR("+Integer.toString(colsize)+")";
            else
                type="VARCHAR(MAX)";
        }else{
            type="VARCHAR(MAX)";
        }
        return type;
    }

    private void executeUpdateStatement(String stmt, String errorMessage) throws IOException  {
        Statement dbstmt = null;
        try{
            try{
                dbstmt = conn.createStatement();
                EhiLogger.traceBackendCmd(stmt);
                dbstmt.executeUpdate(stmt);
            }finally{
                if(dbstmt!=null) dbstmt.close();
            }
        }catch(SQLException ex){
            IOException iox=new IOException(errorMessage);
            iox.initCause(ex);
            throw iox;
        }
    }
}
