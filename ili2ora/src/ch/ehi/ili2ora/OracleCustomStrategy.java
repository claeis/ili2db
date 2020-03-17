package ch.ehi.ili2ora;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.generator.TextFileUtility;

public class OracleCustomStrategy extends AbstractJdbcMapping {
    private static final String WRAPPER_FUNCTION = "ILI2ORA_SDO_GEOMETRY";
    private Connection conn = null;
    
    @Override
    public void fromIliInit(Config config) {
        String schema = config.getDbschema();
        String strWrapperFunction = WRAPPER_FUNCTION;
        TextFileUtility txtOut=new TextFileUtility();

        if(schema != null && !schema.isEmpty()) {
            strWrapperFunction = schema + "." + strWrapperFunction;
        }
        
        StringBuilder stmt = new StringBuilder();
        
        stmt.append(txtOut.getIndent() + "CREATE OR REPLACE FUNCTION " + strWrapperFunction);
        stmt.append("(geom_input BLOB, srid NUMBER)" + txtOut.newline());
        txtOut.inc_ind();
        stmt.append(txtOut.getIndent() + "RETURN MDSYS.SDO_GEOMETRY IS geom MDSYS.SDO_GEOMETRY;" + txtOut.newline());
        txtOut.dec_ind();
        stmt.append(txtOut.getIndent() + "BEGIN" + txtOut.newline());
        txtOut.inc_ind();
        stmt.append(txtOut.getIndent() + "geom := NULL;" + txtOut.newline());
        stmt.append(txtOut.getIndent() + "IF geom_input IS NOT NULL THEN" + txtOut.newline());
        txtOut.inc_ind();
        stmt.append(txtOut.getIndent() + "geom := SDO_GEOMETRY(geom_input, srid);" + txtOut.newline());
        txtOut.dec_ind();
        stmt.append(txtOut.getIndent() + "END IF;" + txtOut.newline());
        stmt.append(txtOut.getIndent() + "RETURN(geom);" + txtOut.newline());
        txtOut.dec_ind();
        stmt.append(txtOut.getIndent() + "END;");
        
        String strStmt=stmt.toString();
        if(conn != null) {
            try{
                Statement dbstmt = null;
                try{
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(strStmt);
                    dbstmt.execute(strStmt);
                } finally {
                    if(dbstmt!=null) dbstmt.close();
                }
            } catch(SQLException e){
                EhiLogger.logError(e);
                throw new IllegalStateException("Failed to add function " + strWrapperFunction);
            }
        }
    }
    
    @Override
    public void postConnect(Connection conn, Config config) {
        this.conn = conn;
    }
}
