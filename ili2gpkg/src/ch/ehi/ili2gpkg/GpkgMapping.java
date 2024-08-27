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
import ch.ehi.ili2db.gui.Config;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.*;
import org.sqlite.Function;

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
			    String filename=null;
			    if(config.isOneGeomPerTable()) {
	                filename = ch.ehi.basics.i18n.ResourceBundle.class2packagePath(getClass())+"/init.sql";
			    }else {
	                filename = ch.ehi.basics.i18n.ResourceBundle.class2packagePath(getClass())+"/init-mg.sql";
			    }
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

		if(Config.TRUE.equalsIgnoreCase(config.getValue(Config.CREATE_GEOM_INDEX))) {
			try {

				final byte MASK_GEOMETRY_EMPTY=0x10;
				final byte MASK_HEADER_BYTE_ORDER=0x01;
				final byte MASK_HEADER_ENVELOPE_CODE=0x0e;

				final byte FLAG_GEOMETRY_EMPTY=0x10;
				final byte FLAG_LITTLE_ENDIAN=0x01;
				final byte FLAG_BIG_ENDIAN=0x00;
				final byte FLAG_HEADER_ENVELOPE_EMPTY=0x00;

				Function.create(conn, "ST_IsEmpty", new Function() {
					@Override
					protected void xFunc() throws SQLException {
						try {
							byte[] blobArgument = value_blob(0);
							InStream input = new ByteArrayInStream(blobArgument);
							byte[] bytes = new byte[4];
							input.read(bytes);
							byte flags = bytes[3];
							if ((flags&MASK_GEOMETRY_EMPTY)==FLAG_GEOMETRY_EMPTY) {
								result(1);
							} else {
								result(0);
							}
						} catch (IOException e) {
							EhiLogger.logError("The implementation of SQL function ST_IsEmpty needed for a rtree spatial index failed. There was an error during the execution of the function.");
							throw new IllegalStateException(e);
						}
					}
				});

				Function.create(conn, "ST_MinX", new Function() {
					@Override
					protected void xFunc() throws SQLException {
						try {
							byte[] blobArgument = value_blob(0);
							InStream input = new ByteArrayInStream(blobArgument);
							byte[] bytes = new byte[4];
							input.read(bytes);
							byte flags = bytes[3];
							ByteOrderDataInStream din = new ByteOrderDataInStream(input);
							if((flags&MASK_HEADER_BYTE_ORDER)==FLAG_LITTLE_ENDIAN) {
								din.setOrder(ByteOrderValues.LITTLE_ENDIAN);
							} else {
								din.setOrder(ByteOrderValues.BIG_ENDIAN);
							}
							int srsId = din.readInt();
							if ((flags&MASK_HEADER_ENVELOPE_CODE)!=FLAG_HEADER_ENVELOPE_EMPTY) {
								double minX = din.readDouble();
								double maxX = din.readDouble();
								double minY = din.readDouble();
								double maxY = din.readDouble();
								result(minX);
							} else {
								WKBReader wkbReader = new WKBReader(new GeometryFactory());
								Geometry geom = wkbReader.read(input);
								geom.setSRID(srsId);
								result(geom.getEnvelopeInternal().getMinX());
							}
						} catch (Exception e) {
							EhiLogger.logError("The implementation of SQL function ST_MinX needed for a rtree spatial index failed. There was an error during the execution of the function.");
							throw new IllegalStateException(e);
						}
					}
				});

				Function.create(conn, "ST_MaxX", new Function() {
					@Override
					protected void xFunc() throws SQLException {
						try {
							byte[] blobArgument = value_blob(0);
							InStream input = new ByteArrayInStream(blobArgument);
							byte[] bytes = new byte[4];
							input.read(bytes);
							byte flags = bytes[3];
							ByteOrderDataInStream din = new ByteOrderDataInStream(input);
							if((flags&MASK_HEADER_BYTE_ORDER)==FLAG_LITTLE_ENDIAN) {
								din.setOrder(ByteOrderValues.LITTLE_ENDIAN);
							} else {
								din.setOrder(ByteOrderValues.BIG_ENDIAN);
							}
							int srsId = din.readInt();
							if ((flags&MASK_HEADER_ENVELOPE_CODE)!=FLAG_HEADER_ENVELOPE_EMPTY) {
								double minX = din.readDouble();
								double maxX = din.readDouble();
								double minY = din.readDouble();
								double maxY = din.readDouble();
								result(maxX);
							} else {
								WKBReader wkbReader = new WKBReader(new GeometryFactory());
								Geometry geom = wkbReader.read(input);
								geom.setSRID(srsId);
								result(geom.getEnvelopeInternal().getMaxX());
							}
						} catch (Exception e) {
							EhiLogger.logError("The implementation of SQL function ST_MaxX needed for a rtree spatial index failed. There was an error during the execution of the function.");
							throw new IllegalStateException(e);
						}
					}
				});

				Function.create(conn, "ST_MinY", new Function() {
					@Override
					protected void xFunc() throws SQLException {
						try {
							byte[] blobArgument = value_blob(0);
							InStream input = new ByteArrayInStream(blobArgument);
							byte[] bytes = new byte[4];
							input.read(bytes);
							byte flags = bytes[3];
							ByteOrderDataInStream din = new ByteOrderDataInStream(input);
							if((flags&MASK_HEADER_BYTE_ORDER)==FLAG_LITTLE_ENDIAN) {
								din.setOrder(ByteOrderValues.LITTLE_ENDIAN);
							} else {
								din.setOrder(ByteOrderValues.BIG_ENDIAN);
							}
							int srsId = din.readInt();
							if ((flags&MASK_HEADER_ENVELOPE_CODE)!=FLAG_HEADER_ENVELOPE_EMPTY) {
								double minX = din.readDouble();
								double maxX = din.readDouble();
								double minY = din.readDouble();
								double maxY = din.readDouble();
								result(minY);
							} else {
								WKBReader wkbReader = new WKBReader(new GeometryFactory());
								Geometry geom = wkbReader.read(input);
								geom.setSRID(srsId);
								result(geom.getEnvelopeInternal().getMinY());
							}
						} catch (Exception e) {
							EhiLogger.logError("The implementation of SQL function ST_MinY needed for a rtree spatial index failed. There was an error during the execution of the function.");
							throw new IllegalStateException(e);
						}
					}
				});

				Function.create(conn, "ST_MaxY", new Function() {
					@Override
					protected void xFunc() throws SQLException {
						try {
							byte[] blobArgument = value_blob(0);
							InStream input = new ByteArrayInStream(blobArgument);
							byte[] bytes = new byte[4];
							input.read(bytes);
							byte flags = bytes[3];
							ByteOrderDataInStream din = new ByteOrderDataInStream(input);
							if((flags&MASK_HEADER_BYTE_ORDER)==FLAG_LITTLE_ENDIAN) {
								din.setOrder(ByteOrderValues.LITTLE_ENDIAN);
							} else {
								din.setOrder(ByteOrderValues.BIG_ENDIAN);
							}
							int srsId = din.readInt();
							if ((flags&MASK_HEADER_ENVELOPE_CODE)!=FLAG_HEADER_ENVELOPE_EMPTY) {
								double minX = din.readDouble();
								double maxX = din.readDouble();
								double minY = din.readDouble();
								double maxY = din.readDouble();
								result(maxY);
							} else {
								WKBReader wkbReader = new WKBReader(new GeometryFactory());
								Geometry geom = wkbReader.read(input);
								geom.setSRID(srsId);
								result(geom.getEnvelopeInternal().getMaxY());
							}
						} catch (Exception e) {
							EhiLogger.logError("The implementation of SQL function ST_MaxY needed for a rtree spatial index failed. There was an error during the execution of the function.");
							throw new IllegalStateException(e);
						}
					}
				});
			} catch (SQLException e) {
				EhiLogger.logError("Failed to create SQL functions ST_MinX, ST_MaxX, ST_MinY, ST_MaxY needed for a rtree spatial index.");
				throw new IllegalStateException(e);
			}
		}
	}

}
