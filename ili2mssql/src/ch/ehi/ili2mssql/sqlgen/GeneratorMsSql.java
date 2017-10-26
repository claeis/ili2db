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
		String type="";
		
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
			type="VARCHAR(36)";
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
		String isNull=column.isNotNull()?"NOT NULL":"NULL";
		if(column.isPrimaryKey()){
			isNull="PRIMARY KEY";
		}
		String sep=" ";
		String defaultValue="";
		if(column.getDefaultValue()!=null){
			defaultValue=sep+"DEFAULT ("+column.getDefaultValue()+")";
			sep=" ";
		}
		
		String name=column.getName();
		out.write(getIndent()+colSep+"["+name+"] "+type+" "+isNull+defaultValue+newline());
		colSep=",";
	}

	
	@Override
	public void visit1TableEnd(DbTable tab) throws IOException {
		super.visit1TableEnd(tab);
	}
	
	@Override
	public void visitTableEndColumn(DbTable tab) throws IOException {
		boolean tableExists = DbUtility.tableExists(conn,tab.getName());
	}
	

	@Override
	public void visitTableBeginConstraint(DbTable dbTab) throws IOException {
		super.visitTableBeginConstraint(dbTab);
		
		String sqlTabName=dbTab.getName().getQName();
		for(Iterator dbColi=dbTab.iteratorColumn();dbColi.hasNext();){
			DbColumn dbCol=(DbColumn) dbColi.next();
			if(dbCol.getReferencedTable()!=null){
				String createstmt=null;
				String action="";
				if(dbCol.getOnUpdateAction()!=null){
					action=action+" ON UPDATE "+dbCol.getOnUpdateAction();
				}
				if(dbCol.getOnDeleteAction()!=null){
					action=action+" ON DELETE "+dbCol.getOnDeleteAction();
				}
				String constraintName=createConstraintName(dbTab,"fkey",dbCol.getName());
				
				createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" FOREIGN KEY ( "+dbCol.getName()+" ) REFERENCES "+dbCol.getReferencedTable().getQName();//+action+" DEFERRABLE INITIALLY DEFERRED";
				
				String dropstmt=null;
				dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;

				addConstraint(dbTab, constraintName,createstmt, dropstmt);
				
			}
			if(dbCol instanceof DbColNumber && (((DbColNumber)dbCol).getMinValue()!=null || ((DbColNumber)dbCol).getMaxValue()!=null)){
				DbColNumber dbColNum=(DbColNumber)dbCol;
				String createstmt=null;
				String action="";
				if(dbColNum.getMinValue()!=null || dbColNum.getMaxValue()!=null){
					if(dbColNum.getMaxValue()==null){
						action=">="+dbColNum.getMinValue();
					}else if(dbColNum.getMinValue()==null){
						action="<="+dbColNum.getMaxValue();
					}else{
						action="BETWEEN "+dbColNum.getMinValue()+" AND "+dbColNum.getMaxValue();
					}
				}
				String constraintName=createConstraintName(dbTab,"check",dbCol.getName());

				createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action+")";
				
				String dropstmt=null;
				dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;

				addConstraint(dbTab, constraintName,createstmt, dropstmt);
				
			} else if(dbCol instanceof DbColDecimal && (((DbColDecimal)dbCol).getMinValue()!=null || ((DbColDecimal)dbCol).getMaxValue()!=null)){
				DbColDecimal dbColNum=(DbColDecimal)dbCol;
				String createstmt=null;
				String action="";
				if(dbColNum.getMinValue()!=null || dbColNum.getMaxValue()!=null){
					if(dbColNum.getMaxValue()==null){
						action=">="+dbColNum.getMinValue();
					}else if(dbColNum.getMinValue()==null){
						action="<="+dbColNum.getMaxValue();
					}else{
						action="BETWEEN "+dbColNum.getMinValue()+" AND "+dbColNum.getMaxValue();
					}
				}
				String constraintName=createConstraintName(dbTab,"check",dbCol.getName());
				
				createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" CHECK( "+dbCol.getName()+" "+action+")";
				
				String dropstmt=null;
				dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;

				addConstraint(dbTab, constraintName,createstmt, dropstmt);
			}
		}
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
			
			String condition = " ";
			String sepCondition = " ";
			
			for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
				DbColumn attr=(DbColumn)attri.next();
				
					out.write(sep+attr.getName());
					condition += sepCondition + attr.getName() + " is not null";
					
				sep=",";
				sepCondition = " AND ";
			}
			out.write(") WHERE"+condition+newline());
			String stmt=out.toString();
			addCreateLine(new Stmt(stmt));
			out=null;
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
