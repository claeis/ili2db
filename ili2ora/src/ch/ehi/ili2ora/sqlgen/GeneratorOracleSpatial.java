package ch.ehi.ili2ora.sqlgen;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.DbNames;
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
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;

public class GeneratorOracleSpatial extends GeneratorJdbc {

	@Override
	public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
		String type="";
		String size="";
		String notSupported=null;
		
		if(column instanceof DbColBoolean){
			type="NUMBER(1)";
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
			type="MDSYS.SDO_GEOMETRY";
		}else if(column instanceof DbColId){
			type="NUMBER(9)";
		}else if(column instanceof DbColUuid){
			type="VARCHAR2(36)";
		}else if(column instanceof DbColNumber){
			DbColNumber col=(DbColNumber)column;
			type="NUMBER("+Integer.toString(col.getSize())+")";
		}else if(column instanceof DbColVarchar){
			int colsize=((DbColVarchar)column).getSize();
			
			if(colsize!=DbColVarchar.UNLIMITED) {
				type="VARCHAR2("+Integer.toString(colsize)+")";
			}else {
				type="CLOB";
			}
		}else{
			type="VARCHAR2(20)";
		}
		String isNull=column.isNotNull()?"NOT NULL":"NULL";
		if(column instanceof DbColId){
			if(((DbColId)column).isPrimaryKey()){
				isNull="PRIMARY KEY";
			}
		}
		String name=column.getName();
		
		if(name.equals(DbNames.MODELS_TAB_FILE_COL) && dbTab.getName().getName().equals(DbNames.MODELS_TAB)) {
			name = "\"" + name + "\"";
		}
		
		out.write(getIndent()+colSep+name+" "+type+" "+isNull+newline());
		colSep=",";
	}

	@Override
	public void visitIndex(DbIndex idx) throws IOException {
		if(!idx.isPrimary())
			super.visitIndex(idx);
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

                createstmt="ALTER TABLE "+sqlTabName+" ADD CONSTRAINT "+constraintName+" FOREIGN KEY ( "+dbCol.getName()+" ) REFERENCES "+dbCol.getReferencedTable().getQName();

                String dropstmt=null;
                dropstmt="ALTER TABLE "+sqlTabName+" DROP CONSTRAINT "+constraintName;

                addConstraint(dbTab, constraintName,createstmt, dropstmt);
            }
        }
    }

   @Override
    public void visit1TableEnd(DbTable tab) throws IOException {

        String sqlTabName=tab.getName().toString();
        
        boolean tableExists=DbUtility.tableExists(conn,tab.getName());
        super.visit1TableEnd(tab);

        String cmt=tab.getComment();
        if(cmt!=null){
            cmt="COMMENT ON TABLE "+sqlTabName+" IS '"+escapeString(cmt)+"'";
            addCreateLine(new Stmt(cmt));
            if(!tableExists){
                Statement dbstmt = null;
                try{
                    try{
                        dbstmt = conn.createStatement();
                        EhiLogger.traceBackendCmd(cmt);
                        dbstmt.execute(cmt);
                    }finally{
                        dbstmt.close();
                    }
                }catch(SQLException ex){
                    IOException iox=new IOException("failed to add comment to table "+tab.getName());
                    iox.initCause(ex);
                    throw iox;
                }
            }
        }

        java.util.Iterator coli=tab.iteratorColumn();
        while(coli.hasNext()){
            DbColumn col=(DbColumn)coli.next();
            cmt=col.getComment();
            if(cmt!=null){
                cmt="COMMENT ON COLUMN "+sqlTabName+"."+col.getName()+" IS '"+escapeString(cmt)+"'";
                addCreateLine(new Stmt(cmt));
                if(!tableExists){
                    Statement dbstmt = null;
                    try{
                        try{
                            dbstmt = conn.createStatement();
                            EhiLogger.traceBackendCmd(cmt);
                            dbstmt.execute(cmt);
                        }finally{
                            dbstmt.close();
                        }
                    }catch(SQLException ex){
                        IOException iox=new IOException("failed to add comment to table "+tab.getName());
                        iox.initCause(ex);
                        throw iox;
                    }
                }
            }
        }
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
}
