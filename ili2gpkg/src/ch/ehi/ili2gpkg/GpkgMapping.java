package ch.ehi.ili2gpkg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.AbstractJdbcMapping;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;

public class GpkgMapping extends AbstractJdbcMapping {

	private boolean isNewFile=false;
	@Override
	public void fromIliInit(Config config) {
	}

	@Override
	public void fromIliEnd(Config config) {
	}

	@Override
	public void fixupViewable(DbTable sqlTableDef, Viewable iliClassDef) {
	}

	@Override
	public void fixupAttribute(DbTable sqlTableDef, DbColumn sqlColDef,
			AttributeDef iliAttrDef) {
	}

	@Override
	public void fixupEmbeddedLink(DbTable dbTable, DbColumn dbColId,
			AssociationDef roleOwner, RoleDef role, DbTableName targetTable,
			String targetPk) {
	}

	@Override
	public void preConnect(String url, String dbusr, String dbpwd, Config config) {
		String fileName=config.getDbfile();
		if(new File(fileName).exists()){
			isNewFile=false;
		}else{
			isNewFile=true;
		}
	}

	@Override
	public void postConnect(Connection conn, Config config) {
		if(isNewFile){
			// exec init script
			java.io.LineNumberReader reader=null;
			try {
				String filename = ch.ehi.basics.i18n.ResourceBundle.class2packagePath(getClass())+"/init.sql";
				InputStream initsqlStream = getClass().getResourceAsStream(filename);
				if(initsqlStream==null){
					throw new IllegalStateException("Resource "+filename+" not found");
				}
				reader = new java.io.LineNumberReader(new java.io.InputStreamReader(initsqlStream, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
			try{
				String line=reader.readLine();
				while(line!=null){
					// exec sql
					line=line.trim();
					if(line.length()>0){
						Statement dbstmt = null;
						try{
							try{
								dbstmt = conn.createStatement();
								EhiLogger.traceBackendCmd(line);
								dbstmt.execute(line);
							}finally{
								dbstmt.close();
							}
						}catch(SQLException ex){
							throw new IllegalStateException(ex);
						}
						
					}
					// read next line
					line=reader.readLine();
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}finally{
				try {
					reader.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

}
