package ch.ehi.ili2duckdb;

import java.io.IOException;
import java.sql.Connection;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.DbUtility;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.repository.DbTableName;

public class DuckDBIdGen implements DbIdGen {

    public final static String SQL_ILI2DB_SEQ_NAME="t_ili2db_seq";
    java.sql.Connection conn=null;
    String dbusr=null;
    String schema=null;
    Long minValue=null;
    Long maxValue=null;
    @Override
    public void init(String schema,Config config) {
        this.schema=schema;
        minValue=config.getMinIdSeqValue();
        maxValue=config.getMaxIdSeqValue();
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

    @Override
    public void initDbDefs(ch.ehi.sqlgen.generator.Generator gen) {
        DbTableName sqlName=new DbTableName(schema,SQL_ILI2DB_SEQ_NAME);
        String stmt="CREATE SEQUENCE "+sqlName.getQName();
        if(minValue!=null){
            stmt=stmt+" MINVALUE "+minValue;
        }
        if(maxValue!=null){
            stmt=stmt+" MAXVALUE "+maxValue;
        }
        stmt=stmt+";";
        if(gen instanceof GeneratorJdbc){
            ((GeneratorJdbc) gen).addCreateLine(((GeneratorJdbc) gen).new Stmt(stmt));
            ((GeneratorJdbc) gen).addDropLine(((GeneratorJdbc) gen).new Stmt("DROP SEQUENCE "+sqlName.getQName()+";"));
        }
        if(conn!=null) {
            if(DbUtility.sequenceExists(conn,sqlName)){
                return;
            }
            EhiLogger.traceBackendCmd(stmt);
            java.sql.PreparedStatement updstmt = null;
            try{
                updstmt = conn.prepareStatement(stmt);
                updstmt.execute();
            }catch(java.sql.SQLException ex){
                EhiLogger.logError("failed to create sequence "+sqlName.getQName(),ex);
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
    }
    /** gets a new obj id.
     */
    long lastLocalId=0;
    @Override
    public long newObjSqlId(){
        lastLocalId=getSeqCount();
        return lastLocalId;
    }
    @Override
    public long getLastSqlId()
    {
        return lastLocalId;
    }
    private long getSeqCount()
    {
        String sqlName=SQL_ILI2DB_SEQ_NAME;
        if(schema!=null){
            sqlName=schema+"."+sqlName;
        }
        java.sql.PreparedStatement getstmt=null;
        java.sql.ResultSet res=null;
        try{
            String stmt="SELECT nextval('"+sqlName+"')";
            EhiLogger.traceBackendCmd(stmt);
            getstmt=conn.prepareStatement(stmt);
            res=getstmt.executeQuery();
            long ret=0;
            if(res.next()){
                ret=res.getLong(1);
                return ret;
            }
        }catch(java.sql.SQLException ex){
            EhiLogger.logError("failed to query "+sqlName,ex);
            throw new IllegalStateException(ex);
        }finally{
            if(res!=null){
                try{
                    res.close();
                }catch(java.sql.SQLException ex){
                    EhiLogger.logError(ex);
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