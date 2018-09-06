package ch.ehi.ili2fgdb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ch.ehi.fgdb4j.jni.ByteArray;
import ch.ehi.fgdb4j.jni.EnumRows;
import ch.ehi.fgdb4j.jni.FieldDefs;
import ch.ehi.fgdb4j.jni.FieldInfo;
import ch.ehi.fgdb4j.jni.FieldType;
import ch.ehi.fgdb4j.jni.MultiPartShapeBuffer;
import ch.ehi.fgdb4j.jni.Row;
import ch.ehi.fgdb4j.jni.ShapeBuffer;
import ch.ehi.fgdb4j.jni.Table;
import ch.ehi.fgdb4j.jni.ce_time;
import ch.ehi.fgdb4j.jni.fgbd4j;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValue;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueField;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueNull;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueString;
import ch.ehi.ili2fgdb.jdbc.sql.SqlQname;

public abstract class AbstractResultSet implements ResultSet {

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean absolute(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void afterLast() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeFirst() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean first() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Array getArray(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array getArray(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getAsciiStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getAsciiStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getBinaryStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getBinaryStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob getBlob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob getBlob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBoolean(int col) throws SQLException {
		Object ret=getObject(col);
		if(ret==null){
			return false;
		}
		if(ret instanceof Integer){
			int v=(Integer)ret;
			return v==0?false:true;
		}else if(ret instanceof Short){
			int v=(Short)ret;
			return v==0?false:true;
		}
		throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public boolean getBoolean(String col) throws SQLException {
		return getBoolean(findColumn(col));
	}

	@Override
	public byte getByte(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getByte(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getBytes(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getBytes(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob getClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob getClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate(int col) throws SQLException {
		Object ret=getObject(col);
		if(ret==null){
			return null;
		}
		if(ret instanceof Date){
			return (Date) ret;
		}else if(ret instanceof Timestamp){
			return new java.sql.Date(((Timestamp) ret).getTime());
		}
		throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public Date getDate(String col) throws SQLException {
		return getDate(findColumn(col));
	}

	@Override
	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDouble(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDouble(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloat(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloat(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(int colIdx) throws SQLException {
        Object ret=getObject(colIdx);
        if(ret==null){
            return 0;
        }
        if(ret instanceof Integer){
            return (Integer) ret;
        }
        throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public int getInt(String colName) throws SQLException {
        return getInt(findColumn(colName));
	}

	@Override
	public long getLong(int colIdx) throws SQLException {
		Object ret=getObject(colIdx);
		if(ret==null){
			return 0;
		}
		if(ret instanceof Integer){
			return (Integer) ret;
		}
		throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public long getLong(String colName) throws SQLException {
		return getLong(findColumn(colName));
	}

	@Override
	public Reader getNCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getNCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob getNClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob getNClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNString(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(String colName) throws SQLException {
		int colIdx=findColumn(colName);
		return getObject(colIdx);
	}

	@Override
	public Object getObject(int arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject(String arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ref getRef(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ref getRef(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RowId getRowId(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RowId getRowId(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML getSQLXML(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML getSQLXML(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getShort(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getShort(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(int colIdx) throws SQLException {
		Object value=getObject(colIdx);
		if(value==null){
			return null;
		}
		return value.toString();
	}

	@Override
	public String getString(String colName) throws SQLException {
		Object value=getObject(colName);
		if(value==null){
			return null;
		}
		return value.toString();
	}

	@Override
	public Time getTime(int col) throws SQLException {
		Object ret=getObject(col);
		if(ret==null){
			return null;
		}
		if(ret instanceof Time){
			return (Time) ret;
		}
		throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public Time getTime(String col) throws SQLException {
		return getTime(findColumn(col));
	}

	@Override
	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(int col) throws SQLException {
		Object ret=getObject(col);
		if(ret==null){
			return null;
		}
		if(ret instanceof Timestamp){
			return (Timestamp) ret;
		}else if(ret instanceof Date){
            return new Timestamp( ((Date) ret).getTime() );
        }
		throw new SQLException("unexpected type "+ret.getClass().getName());
	}

	@Override
	public Timestamp getTimestamp(String col) throws SQLException {
		return getTimestamp(findColumn(col));
	}

	@Override
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0, Calendar arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public URL getURL(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean relative(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArray(int arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateArray(String arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBigDecimal(String arg0, BigDecimal arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(int arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateByte(String arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(int arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDate(String arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(int arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDouble(String arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(int arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFloat(String arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateInt(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(int arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLong(String arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(int arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNull(String arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(int arg0, Object arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateObject(String arg0, Object arg1, int arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(int arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRef(String arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(int arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateShort(String arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(int arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTime(String arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTimestamp(String arg0, Timestamp arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	// since jre 1.7

	//@Override
	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
