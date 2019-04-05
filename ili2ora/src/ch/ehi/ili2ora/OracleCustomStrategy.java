package ch.ehi.ili2ora;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.ehi.sqlgen.generator.TextFileUtility;

public class OracleCustomStrategy extends AbstractJdbcMapping {
    private final String wrapperFunction = "ILI2ORA_SDO_GEOMETRY";
    private Connection conn = null;
    
	@Override
	public void fromIliInit(Config config) {
        String schema = config.getDbschema();
        String strWrapperFunction = wrapperFunction;
        TextFileUtility txtOut=new TextFileUtility();

        if(schema != null && !schema.isEmpty()) {
            strWrapperFunction = schema + "." + strWrapperFunction;
        }
        
        String stmt = "";
        
        stmt += txtOut.getIndent() + "CREATE OR REPLACE FUNCTION " + strWrapperFunction;
        stmt += "(geom_input BLOB, srid NUMBER)" + txtOut.newline();
        txtOut.inc_ind();
        stmt += txtOut.getIndent() + "RETURN MDSYS.SDO_GEOMETRY IS geom MDSYS.SDO_GEOMETRY;" + txtOut.newline();
        txtOut.dec_ind();
        stmt += txtOut.getIndent() + "BEGIN" + txtOut.newline();
        txtOut.inc_ind();
        stmt += txtOut.getIndent() + "geom := NULL;" + txtOut.newline();
        stmt += txtOut.getIndent() + "IF geom_input IS NOT NULL THEN" + txtOut.newline();
        txtOut.inc_ind();
        stmt += txtOut.getIndent() + "geom := SDO_GEOMETRY(geom_input, srid);" + txtOut.newline();
        txtOut.dec_ind();
        stmt += txtOut.getIndent() + "END IF;" + txtOut.newline();
        stmt += txtOut.getIndent() + "RETURN(geom);" + txtOut.newline();
        txtOut.dec_ind();
        stmt += txtOut.getIndent() + "END;";
        
        Statement dbstmt = null;
        if(conn != null) {
            try{
                try{
                    dbstmt = conn.createStatement();
                    EhiLogger.traceBackendCmd(stmt);
                    dbstmt.execute(stmt);
                }catch(SQLException e){
                    throw new IllegalStateException("failed to add function " + strWrapperFunction);
                }
            }finally{
                if(dbstmt!=null){
                    try {
                        dbstmt.close();
                        dbstmt=null;
                    } catch (SQLException e) {
                        EhiLogger.logError(e);
                    }
                }
            }
        }
    }

	@Override
	public void fromIliEnd(Config config) {
	}

	@Override
	public void fixupViewable(DbTable sqlTableDef, Viewable iliClassDef) {
	}

	@Override
	public void fixupAttribute(DbTable sqlTableDef, DbColumn sqlColDef, AttributeDef iliAttrDef) {
	}

	@Override
	public void fixupEmbeddedLink(DbTable dbTable, DbColumn dbColId, AssociationDef roleOwner, RoleDef role,
			DbTableName targetTable, String targetPk) {
	}

	@Override
	public void preConnect(String url, String dbusr, String dbpwd, Config config) {
	}

	@Override
	public void postConnect(Connection conn, Config config) {
		this.conn = conn;
	}

}
