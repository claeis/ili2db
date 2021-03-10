package ch.ehi.ili2ora.sqlgen;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static final String GENERAL_TABLESPACE = "generalTablespace";
    public static final String INDEX_TABLESPACE="indexTablespace";
    public static final String LOB_TABLESPACE="lobTableSpace";
    private static final int MAJOR_VERSION_SUPPORT_SEQ_AS_DEFAULT = 12;
    private static final int MINOR_VERSION_SUPPORT_SEQ_AS_DEFAULT = 1;

    private String generalTableSpace;
    private String indexTablespace;
    private String lobTablespace;
    private boolean useTriggerToSetTId = true;

    private DbColumn primaryKeyDefaultValue=null;
    private DbColumn primaryKeyCol=null;
    
    private List<DbColumn> lobCols;

    @Override
    public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
        String type=getOraType(column);
        String isNull=column.isNotNull()?"NOT NULL":"NULL";
        
        if(column instanceof DbColId && ((DbColId)column).isPrimaryKey()){
            primaryKeyCol=column;
        }
        String defaultValue="";
        if(column.getDefaultValue()!=null){
            defaultValue=" DEFAULT " + column.getDefaultValue();
            
            if(column.isPrimaryKey() && useTriggerToSetTId) {
                defaultValue = "";
                primaryKeyDefaultValue = column;
            }
        }

        String name=column.getName();
        
        if(name.equals(DbNames.MODELS_TAB_FILENAME_COL_VER3) && dbTab.getName().getName().equals(DbNames.MODELS_TAB)) {
            name = "\"" + name + "\"";
        }
        
        out.write(getIndent()+colSep+name+" "+type+defaultValue+" "+isNull+newline());
        colSep=",";
    }

    @Override
    public void visitSchemaBegin(Settings config, DbSchema schema) throws IOException {
        super.visitSchemaBegin(config, schema);

        generalTableSpace = config.getValue(GENERAL_TABLESPACE);
        indexTablespace=config.getValue(INDEX_TABLESPACE);
        lobTablespace=config.getValue(LOB_TABLESPACE);
        lobCols=new ArrayList<DbColumn>();
        DatabaseMetaData meta;
        
        if(conn!=null) {
            try {
                meta = conn.getMetaData();
                useTriggerToSetTId = true;
                int majorVersion = meta.getDatabaseMajorVersion();
                int minorVersion = meta.getDatabaseMinorVersion();
                useTriggerToSetTId = 
                        (majorVersion < MAJOR_VERSION_SUPPORT_SEQ_AS_DEFAULT) ||
                        (majorVersion == MAJOR_VERSION_SUPPORT_SEQ_AS_DEFAULT && minorVersion < MINOR_VERSION_SUPPORT_SEQ_AS_DEFAULT);
                
            } catch (SQLException e) {
                IOException iox=new IOException("It was not possible to get the Oracle version.");
                iox.initCause(e);
                throw iox;
            }
        }
    }

    @Override
    public void visitIndex(DbIndex idx) throws IOException {
        if(!idx.isPrimary()&&idx.isUnique()){
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
            out.append(getIndent()+"ALTER TABLE "+tableName+" ADD CONSTRAINT "+constraintName+" UNIQUE (");
            String sep="";
            for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
                DbColumn attr=(DbColumn)attri.next();
                out.append(sep+attr.getName());
                sep=",";
            }
            String tableSpace="";
            if(indexTablespace!=null) {
                tableSpace=" USING INDEX TABLESPACE "+indexTablespace;
            } else if(generalTableSpace!=null) {
                tableSpace=" USING INDEX TABLESPACE "+generalTableSpace;
            }
            out.append(")"+tableSpace);
            String stmt=out.toString();
            addCreateLine(new Stmt(stmt));
            if(conn!=null&&createdTables.contains(tab.getName())){
                executeUpdateStatement(stmt,"failed to add UNIQUE to table "+tab.getName());
            }
        }
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
    public void visit1TableEnd(DbTable tab) throws IOException {
        
        boolean tableExists=DbUtility.tableExists(conn,tab.getName());
        executeCreateTable(tab);
        writePrimaryKey(tab);

        if(primaryKeyDefaultValue != null) {
            writeDefaultValueForPrimaryKey(tab);
            primaryKeyDefaultValue = null;
        }

        String sqlTabName=tab.getName().toString();
        String cmt=tab.getComment();
        if(cmt!=null){
            cmt="COMMENT ON TABLE "+sqlTabName+" IS '"+escapeString(cmt)+"'";
            addCreateLine(new Stmt(cmt));
            if(conn!=null&&!tableExists){
                executeStatement(cmt, "Failed to add comment to table "+tab.getName());
            }
        }

        java.util.Iterator coli=tab.iteratorColumn();
        while(coli.hasNext()){
            DbColumn col=(DbColumn)coli.next();
            cmt=col.getComment();
            if(cmt!=null){
                cmt="COMMENT ON COLUMN "+sqlTabName+"."+col.getName()+" IS '"+escapeString(cmt)+"'";
                addCreateLine(new Stmt(cmt));
                if(conn!=null&&!tableExists){
                    executeStatement(cmt, "Failed to add comment to column "+tab.getName());
                }
            }
        }
    }

    @Override
    protected String getTableEndOptions(DbTable dbTab) {
        String generalTablespacePart="";
        StringBuilder lobTablespacePart=new StringBuilder();
        if(generalTableSpace!=null) {
            generalTablespacePart=newline()+"TABLESPACE "+generalTableSpace;
        }
        if(lobTablespace!=null&&!lobCols.isEmpty()) {
            for(DbColumn lobColi:lobCols) {
                lobTablespacePart.append(newline()+"LOB ("+lobColi.getName()+") STORE AS (TABLESPACE "+lobTablespace+")");
            }
        }
        return generalTablespacePart+lobTablespacePart;
    }

    private void executeCreateTable(DbTable tab) throws IOException {
        dec_ind();
        String cmt=getTableEndOptions(tab);
        lobCols=new ArrayList<DbColumn>();
        out.write(getIndent()+")"+cmt);
        // execute stmt
        String stmt=out.toString();
        addCreateLine(new Stmt(stmt));
        addDropLine(new Stmt("DROP TABLE "+tab.getName()));
        out=null;
        if(conn!=null) {
            if(DbUtility.tableExists(conn,tab.getName())){
                if(tab.isDeleteDataIfTableExists()){
                    String delStmt="DELETE FROM "+tab.getName();
                    executeUpdateStatement(delStmt, "Failed to delete data from table "+tab.getName());
                }
            }else{
                executeUpdateStatement(stmt, "Failed to create table "+tab.getName());
                createdTables.add(tab.getName());
            }
        }
    }

    private void writePrimaryKey(DbTable tab) throws IOException {
        String[] constraintCols=null;
        
        // get cols of the primary key when pk is combined
        for(Iterator idxi=tab.iteratorIndex();idxi.hasNext();){
            DbIndex idx=(DbIndex)idxi.next();
            if(idx.isPrimary()){
                constraintCols=new String[idx.sizeAttr()];
                int coli=0;
                for(Iterator attri=idx.iteratorAttr();attri.hasNext();){
                    DbColumn attr=(DbColumn)attri.next();
                    constraintCols[coli++]=attr.getName();
                }
            }
        }
        // get col of the primary key
        if(primaryKeyCol!=null) {
            constraintCols=new String[] { primaryKeyCol.getName() };
            primaryKeyCol = null;
        }
        
        if(constraintCols!=null) {
            String constraintName=createConstraintName(tab,"pkey",constraintCols);
            String createstmt="ALTER TABLE "+tab.getName()+" ADD CONSTRAINT "+constraintName+" PRIMARY KEY("+stringJoin(",", constraintCols)+")";
            if(indexTablespace!=null) {
                createstmt+=" USING INDEX TABLESPACE "+indexTablespace;
            } else if(generalTableSpace!=null) {
                createstmt+=" USING INDEX TABLESPACE "+generalTableSpace;
            }
            String dropstmt="ALTER TABLE "+tab.getName()+" DROP CONSTRAINT "+constraintName;
            
            this.addConstraint(tab, constraintName, createstmt, dropstmt);
        }
    }
    
    private void writeDefaultValueForPrimaryKey(DbTable tab) throws IOException {
        String sqlTabName=tab.getName().toString();
        String fieldName = primaryKeyDefaultValue.getName();
        String triggerName="trg_" + tab.getName().getName() +"_"+ fieldName;
        if(tab.getName().getSchema()!=null){
            triggerName=tab.getName().getSchema()+"."+triggerName;
        }
        StringBuilder trgQuery = new StringBuilder();
        trgQuery.append(getIndent() + "CREATE OR REPLACE TRIGGER "+ triggerName + newline());
        trgQuery.append(getIndent() + "BEFORE INSERT ON " + sqlTabName + newline()); 
        trgQuery.append(getIndent() + "FOR EACH ROW" + newline());
        trgQuery.append(getIndent() + "BEGIN" + newline());
        inc_ind();
        trgQuery.append(getIndent() + "IF (:NEW."+ fieldName +" is NULL) THEN" + newline());
        inc_ind();
        trgQuery.append(getIndent() + ":NEW." + fieldName +" := " + primaryKeyDefaultValue.getDefaultValue() + ";" + newline());
        dec_ind();
        trgQuery.append(getIndent() + "END IF;" + newline());
        dec_ind();
        trgQuery.append(getIndent() + "END;");
        
        String strTrgQuery = trgQuery.toString();
        addCreateLine(new Stmt(strTrgQuery));
        
        if(conn!=null) {
            executeStatement(strTrgQuery, "Failed to add default value to "+tab.getName() + "." + fieldName);
        }
    }
    
    private String stringJoin(String sep, String[] eles) {
        StringBuilder ret=new StringBuilder();
        ret.append(eles[0]);
        for(int i=1;i<eles.length;i++) {
            ret.append(sep);
            ret.append(eles[i]);
        }
        return ret.toString();
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
        createstmt+=" INITIALLY DEFERRED";
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

    private String getOraType(DbColumn column) {
        String type;
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
                lobCols.add(column);
            }
        }else{
            type="VARCHAR2(20)";
        }
        return type;
    }

    private void executeStatement(String cmt, String errorMessage) throws IOException {
        Statement dbstmt = null;
        try{
            try{
                dbstmt = conn.createStatement();
                EhiLogger.traceBackendCmd(cmt);
                dbstmt.execute(cmt);
            }finally{
                if(dbstmt!=null) dbstmt.close();
            }
        }catch(SQLException ex){
            IOException iox=new IOException(errorMessage);
            iox.initCause(ex);
            throw iox;
        }
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

    public static String escapeString(String cmt)
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
