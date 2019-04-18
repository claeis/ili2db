package ch.ehi.ili2fgdb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.ehi.ili2fgdb.jdbc.sql.ColRef;
import ch.ehi.ili2fgdb.jdbc.sql.IntConst;
import ch.ehi.ili2fgdb.jdbc.sql.IsNull;
import ch.ehi.ili2fgdb.jdbc.sql.StringConst;
import ch.ehi.ili2fgdb.jdbc.sql.Value;

public class MemResultSet implements ResultSet {

	private ResultSet subResultSet=null;
	private List<java.util.Map.Entry<Value,Value>> conditions=null;
	private List<Object> params=null;

	public MemResultSet(ResultSet subResultSet,List<java.util.Map.Entry<Value,Value>> conditions,List<Object> params) {
		this.subResultSet=subResultSet;
		this.conditions=conditions;
		this.params=params;
	}
	public boolean absolute(int row) throws SQLException {
		return subResultSet.absolute(row);
	}
	public void afterLast() throws SQLException {
		subResultSet.afterLast();
	}
	public void beforeFirst() throws SQLException {
		subResultSet.beforeFirst();
	}
	public void cancelRowUpdates() throws SQLException {
		subResultSet.cancelRowUpdates();
	}
	public void clearWarnings() throws SQLException {
		subResultSet.clearWarnings();
	}
	public void close() throws SQLException {
		subResultSet.close();
	}
	public void deleteRow() throws SQLException {
		subResultSet.deleteRow();
	}
	public int findColumn(String columnLabel) throws SQLException {
		return subResultSet.findColumn(columnLabel);
	}
	public boolean first() throws SQLException {
		return subResultSet.first();
	}
	public Array getArray(int columnIndex) throws SQLException {
		return subResultSet.getArray(columnIndex);
	}
	public Array getArray(String columnLabel) throws SQLException {
		return subResultSet.getArray(columnLabel);
	}
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return subResultSet.getAsciiStream(columnIndex);
	}
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return subResultSet.getAsciiStream(columnLabel);
	}
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		return subResultSet.getBigDecimal(columnIndex, scale);
	}
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return subResultSet.getBigDecimal(columnIndex);
	}
	public BigDecimal getBigDecimal(String columnLabel, int scale)
			throws SQLException {
		return subResultSet.getBigDecimal(columnLabel, scale);
	}
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return subResultSet.getBigDecimal(columnLabel);
	}
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return subResultSet.getBinaryStream(columnIndex);
	}
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return subResultSet.getBinaryStream(columnLabel);
	}
	public Blob getBlob(int columnIndex) throws SQLException {
		return subResultSet.getBlob(columnIndex);
	}
	public Blob getBlob(String columnLabel) throws SQLException {
		return subResultSet.getBlob(columnLabel);
	}
	public boolean getBoolean(int columnIndex) throws SQLException {
		return subResultSet.getBoolean(columnIndex);
	}
	public boolean getBoolean(String columnLabel) throws SQLException {
		return subResultSet.getBoolean(columnLabel);
	}
	public byte getByte(int columnIndex) throws SQLException {
		return subResultSet.getByte(columnIndex);
	}
	public byte getByte(String columnLabel) throws SQLException {
		return subResultSet.getByte(columnLabel);
	}
	public byte[] getBytes(int columnIndex) throws SQLException {
		return subResultSet.getBytes(columnIndex);
	}
	public byte[] getBytes(String columnLabel) throws SQLException {
		return subResultSet.getBytes(columnLabel);
	}
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return subResultSet.getCharacterStream(columnIndex);
	}
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return subResultSet.getCharacterStream(columnLabel);
	}
	public Clob getClob(int columnIndex) throws SQLException {
		return subResultSet.getClob(columnIndex);
	}
	public Clob getClob(String columnLabel) throws SQLException {
		return subResultSet.getClob(columnLabel);
	}
	public int getConcurrency() throws SQLException {
		return subResultSet.getConcurrency();
	}
	public String getCursorName() throws SQLException {
		return subResultSet.getCursorName();
	}
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return subResultSet.getDate(columnIndex, cal);
	}
	public Date getDate(int columnIndex) throws SQLException {
		return subResultSet.getDate(columnIndex);
	}
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return subResultSet.getDate(columnLabel, cal);
	}
	public Date getDate(String columnLabel) throws SQLException {
		return subResultSet.getDate(columnLabel);
	}
	public double getDouble(int columnIndex) throws SQLException {
		return subResultSet.getDouble(columnIndex);
	}
	public double getDouble(String columnLabel) throws SQLException {
		return subResultSet.getDouble(columnLabel);
	}
	public int getFetchDirection() throws SQLException {
		return subResultSet.getFetchDirection();
	}
	public int getFetchSize() throws SQLException {
		return subResultSet.getFetchSize();
	}
	public float getFloat(int columnIndex) throws SQLException {
		return subResultSet.getFloat(columnIndex);
	}
	public float getFloat(String columnLabel) throws SQLException {
		return subResultSet.getFloat(columnLabel);
	}
	public int getHoldability() throws SQLException {
		return subResultSet.getHoldability();
	}
	public int getInt(int columnIndex) throws SQLException {
		return subResultSet.getInt(columnIndex);
	}
	public int getInt(String columnLabel) throws SQLException {
		return subResultSet.getInt(columnLabel);
	}
	public long getLong(int columnIndex) throws SQLException {
		return subResultSet.getLong(columnIndex);
	}
	public long getLong(String columnLabel) throws SQLException {
		return subResultSet.getLong(columnLabel);
	}
	public ResultSetMetaData getMetaData() throws SQLException {
		return subResultSet.getMetaData();
	}
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return subResultSet.getNCharacterStream(columnIndex);
	}
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return subResultSet.getNCharacterStream(columnLabel);
	}
	public NClob getNClob(int columnIndex) throws SQLException {
		return subResultSet.getNClob(columnIndex);
	}
	public NClob getNClob(String columnLabel) throws SQLException {
		return subResultSet.getNClob(columnLabel);
	}
	public String getNString(int columnIndex) throws SQLException {
		return subResultSet.getNString(columnIndex);
	}
	public String getNString(String columnLabel) throws SQLException {
		return subResultSet.getNString(columnLabel);
	}
	public Object getObject(int columnIndex, Map<String, Class<?>> map)
			throws SQLException {
		return subResultSet.getObject(columnIndex, map);
	}
	public Object getObject(int columnIndex) throws SQLException {
		return subResultSet.getObject(columnIndex);
	}
	public Object getObject(String columnLabel, Map<String, Class<?>> map)
			throws SQLException {
		return subResultSet.getObject(columnLabel, map);
	}
	public Object getObject(String columnLabel) throws SQLException {
		return subResultSet.getObject(columnLabel);
	}
	public Ref getRef(int columnIndex) throws SQLException {
		return subResultSet.getRef(columnIndex);
	}
	public Ref getRef(String columnLabel) throws SQLException {
		return subResultSet.getRef(columnLabel);
	}
	public int getRow() throws SQLException {
		return subResultSet.getRow();
	}
	public RowId getRowId(int columnIndex) throws SQLException {
		return subResultSet.getRowId(columnIndex);
	}
	public RowId getRowId(String columnLabel) throws SQLException {
		return subResultSet.getRowId(columnLabel);
	}
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return subResultSet.getSQLXML(columnIndex);
	}
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return subResultSet.getSQLXML(columnLabel);
	}
	public short getShort(int columnIndex) throws SQLException {
		return subResultSet.getShort(columnIndex);
	}
	public short getShort(String columnLabel) throws SQLException {
		return subResultSet.getShort(columnLabel);
	}
	public Statement getStatement() throws SQLException {
		return subResultSet.getStatement();
	}
	public String getString(int columnIndex) throws SQLException {
		return subResultSet.getString(columnIndex);
	}
	public String getString(String columnLabel) throws SQLException {
		return subResultSet.getString(columnLabel);
	}
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return subResultSet.getTime(columnIndex, cal);
	}
	public Time getTime(int columnIndex) throws SQLException {
		return subResultSet.getTime(columnIndex);
	}
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return subResultSet.getTime(columnLabel, cal);
	}
	public Time getTime(String columnLabel) throws SQLException {
		return subResultSet.getTime(columnLabel);
	}
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		return subResultSet.getTimestamp(columnIndex, cal);
	}
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return subResultSet.getTimestamp(columnIndex);
	}
	public Timestamp getTimestamp(String columnLabel, Calendar cal)
			throws SQLException {
		return subResultSet.getTimestamp(columnLabel, cal);
	}
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return subResultSet.getTimestamp(columnLabel);
	}
	public int getType() throws SQLException {
		return subResultSet.getType();
	}
	public URL getURL(int columnIndex) throws SQLException {
		return subResultSet.getURL(columnIndex);
	}
	public URL getURL(String columnLabel) throws SQLException {
		return subResultSet.getURL(columnLabel);
	}
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return subResultSet.getUnicodeStream(columnIndex);
	}
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return subResultSet.getUnicodeStream(columnLabel);
	}
	public SQLWarning getWarnings() throws SQLException {
		return subResultSet.getWarnings();
	}
	public void insertRow() throws SQLException {
		subResultSet.insertRow();
	}
	public boolean isAfterLast() throws SQLException {
		return subResultSet.isAfterLast();
	}
	public boolean isBeforeFirst() throws SQLException {
		return subResultSet.isBeforeFirst();
	}
	public boolean isClosed() throws SQLException {
		return subResultSet.isClosed();
	}
	public boolean isFirst() throws SQLException {
		return subResultSet.isFirst();
	}
	public boolean isLast() throws SQLException {
		return subResultSet.isLast();
	}
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return subResultSet.isWrapperFor(iface);
	}
	public boolean last() throws SQLException {
		return subResultSet.last();
	}
	public void moveToCurrentRow() throws SQLException {
		subResultSet.moveToCurrentRow();
	}
	public void moveToInsertRow() throws SQLException {
		subResultSet.moveToInsertRow();
	}
	public boolean next() throws SQLException {
		boolean hasNext=subResultSet.next();
		if(conditions!=null) {
		    while(hasNext) {
	            if(condtionsTrue()) {
	                return true;
	            }
	            hasNext=subResultSet.next();
		    }
		}
		return hasNext;
	}
	private boolean condtionsTrue() throws SQLException {
            int paramIdx=0;
            for(java.util.Map.Entry<Value,Value> set:conditions){
                ColRef colref=(ColRef)set.getKey();
                Object leftValue=subResultSet.getObject(colref.getName());
                Value rh=set.getValue();
                  if(rh instanceof IntConst){
                      if(leftValue==null || !leftValue.equals(((IntConst)rh).getValue())) {
                          return false;
                      }
                  }else if(rh instanceof IsNull){
                      if(leftValue!=null) {
                          return false;
                      }
                  }else if(rh instanceof StringConst){
                      if(leftValue==null || !leftValue.equals(((StringConst)rh).getValue())) {
                          return false;
                      }
                  }else{
                        Object param=params.get(paramIdx++);
                        if(param==null) {
                            if(leftValue!=null) {
                                return false;
                            }
                        }else {
                            if(leftValue==null) {
                                return false;
                            }
                            if((leftValue instanceof Short || leftValue instanceof Integer || leftValue instanceof Long) && (param instanceof Short || param instanceof Integer || param instanceof Long)) {
                                if(((Number) leftValue).longValue()==((Number) param).longValue()) {
                                    return true;
                                }
                                return false;
                            }
                            if(!leftValue.equals(param)) {
                                return false;
                            }
                        }
                  }
            }
        return true;
    }
    public boolean previous() throws SQLException {
		return subResultSet.previous();
	}
	public void refreshRow() throws SQLException {
		subResultSet.refreshRow();
	}
	public boolean relative(int rows) throws SQLException {
		return subResultSet.relative(rows);
	}
	public boolean rowDeleted() throws SQLException {
		return subResultSet.rowDeleted();
	}
	public boolean rowInserted() throws SQLException {
		return subResultSet.rowInserted();
	}
	public boolean rowUpdated() throws SQLException {
		return subResultSet.rowUpdated();
	}
	public void setFetchDirection(int direction) throws SQLException {
		subResultSet.setFetchDirection(direction);
	}
	public void setFetchSize(int rows) throws SQLException {
		subResultSet.setFetchSize(rows);
	}
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return subResultSet.unwrap(iface);
	}
	public void updateArray(int columnIndex, Array x) throws SQLException {
		subResultSet.updateArray(columnIndex, x);
	}
	public void updateArray(String columnLabel, Array x) throws SQLException {
		subResultSet.updateArray(columnLabel, x);
	}
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		subResultSet.updateAsciiStream(columnIndex, x, length);
	}
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		subResultSet.updateAsciiStream(columnIndex, x, length);
	}
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		subResultSet.updateAsciiStream(columnIndex, x);
	}
	public void updateAsciiStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		subResultSet.updateAsciiStream(columnLabel, x, length);
	}
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		subResultSet.updateAsciiStream(columnLabel, x, length);
	}
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		subResultSet.updateAsciiStream(columnLabel, x);
	}
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		subResultSet.updateBigDecimal(columnIndex, x);
	}
	public void updateBigDecimal(String columnLabel, BigDecimal x)
			throws SQLException {
		subResultSet.updateBigDecimal(columnLabel, x);
	}
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		subResultSet.updateBinaryStream(columnIndex, x, length);
	}
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		subResultSet.updateBinaryStream(columnIndex, x, length);
	}
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		subResultSet.updateBinaryStream(columnIndex, x);
	}
	public void updateBinaryStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		subResultSet.updateBinaryStream(columnLabel, x, length);
	}
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		subResultSet.updateBinaryStream(columnLabel, x, length);
	}
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		subResultSet.updateBinaryStream(columnLabel, x);
	}
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		subResultSet.updateBlob(columnIndex, x);
	}
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		subResultSet.updateBlob(columnIndex, inputStream, length);
	}
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		subResultSet.updateBlob(columnIndex, inputStream);
	}
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		subResultSet.updateBlob(columnLabel, x);
	}
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		subResultSet.updateBlob(columnLabel, inputStream, length);
	}
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		subResultSet.updateBlob(columnLabel, inputStream);
	}
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		subResultSet.updateBoolean(columnIndex, x);
	}
	public void updateBoolean(String columnLabel, boolean x)
			throws SQLException {
		subResultSet.updateBoolean(columnLabel, x);
	}
	public void updateByte(int columnIndex, byte x) throws SQLException {
		subResultSet.updateByte(columnIndex, x);
	}
	public void updateByte(String columnLabel, byte x) throws SQLException {
		subResultSet.updateByte(columnLabel, x);
	}
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		subResultSet.updateBytes(columnIndex, x);
	}
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		subResultSet.updateBytes(columnLabel, x);
	}
	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		subResultSet.updateCharacterStream(columnIndex, x, length);
	}
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		subResultSet.updateCharacterStream(columnIndex, x, length);
	}
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		subResultSet.updateCharacterStream(columnIndex, x);
	}
	public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		subResultSet.updateCharacterStream(columnLabel, reader, length);
	}
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		subResultSet.updateCharacterStream(columnLabel, reader, length);
	}
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		subResultSet.updateCharacterStream(columnLabel, reader);
	}
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		subResultSet.updateClob(columnIndex, x);
	}
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		subResultSet.updateClob(columnIndex, reader, length);
	}
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		subResultSet.updateClob(columnIndex, reader);
	}
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		subResultSet.updateClob(columnLabel, x);
	}
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		subResultSet.updateClob(columnLabel, reader, length);
	}
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		subResultSet.updateClob(columnLabel, reader);
	}
	public void updateDate(int columnIndex, Date x) throws SQLException {
		subResultSet.updateDate(columnIndex, x);
	}
	public void updateDate(String columnLabel, Date x) throws SQLException {
		subResultSet.updateDate(columnLabel, x);
	}
	public void updateDouble(int columnIndex, double x) throws SQLException {
		subResultSet.updateDouble(columnIndex, x);
	}
	public void updateDouble(String columnLabel, double x) throws SQLException {
		subResultSet.updateDouble(columnLabel, x);
	}
	public void updateFloat(int columnIndex, float x) throws SQLException {
		subResultSet.updateFloat(columnIndex, x);
	}
	public void updateFloat(String columnLabel, float x) throws SQLException {
		subResultSet.updateFloat(columnLabel, x);
	}
	public void updateInt(int columnIndex, int x) throws SQLException {
		subResultSet.updateInt(columnIndex, x);
	}
	public void updateInt(String columnLabel, int x) throws SQLException {
		subResultSet.updateInt(columnLabel, x);
	}
	public void updateLong(int columnIndex, long x) throws SQLException {
		subResultSet.updateLong(columnIndex, x);
	}
	public void updateLong(String columnLabel, long x) throws SQLException {
		subResultSet.updateLong(columnLabel, x);
	}
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		subResultSet.updateNCharacterStream(columnIndex, x, length);
	}
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		subResultSet.updateNCharacterStream(columnIndex, x);
	}
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		subResultSet.updateNCharacterStream(columnLabel, reader, length);
	}
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		subResultSet.updateNCharacterStream(columnLabel, reader);
	}
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		subResultSet.updateNClob(columnIndex, nClob);
	}
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		subResultSet.updateNClob(columnIndex, reader, length);
	}
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		subResultSet.updateNClob(columnIndex, reader);
	}
	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		subResultSet.updateNClob(columnLabel, nClob);
	}
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		subResultSet.updateNClob(columnLabel, reader, length);
	}
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		subResultSet.updateNClob(columnLabel, reader);
	}
	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		subResultSet.updateNString(columnIndex, nString);
	}
	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		subResultSet.updateNString(columnLabel, nString);
	}
	public void updateNull(int columnIndex) throws SQLException {
		subResultSet.updateNull(columnIndex);
	}
	public void updateNull(String columnLabel) throws SQLException {
		subResultSet.updateNull(columnLabel);
	}
	public void updateObject(int columnIndex, Object x, int scaleOrLength)
			throws SQLException {
		subResultSet.updateObject(columnIndex, x, scaleOrLength);
	}
	public void updateObject(int columnIndex, Object x) throws SQLException {
		subResultSet.updateObject(columnIndex, x);
	}
	public void updateObject(String columnLabel, Object x, int scaleOrLength)
			throws SQLException {
		subResultSet.updateObject(columnLabel, x, scaleOrLength);
	}
	public void updateObject(String columnLabel, Object x) throws SQLException {
		subResultSet.updateObject(columnLabel, x);
	}
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		subResultSet.updateRef(columnIndex, x);
	}
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		subResultSet.updateRef(columnLabel, x);
	}
	public void updateRow() throws SQLException {
		subResultSet.updateRow();
	}
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		subResultSet.updateRowId(columnIndex, x);
	}
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		subResultSet.updateRowId(columnLabel, x);
	}
	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		subResultSet.updateSQLXML(columnIndex, xmlObject);
	}
	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		subResultSet.updateSQLXML(columnLabel, xmlObject);
	}
	public void updateShort(int columnIndex, short x) throws SQLException {
		subResultSet.updateShort(columnIndex, x);
	}
	public void updateShort(String columnLabel, short x) throws SQLException {
		subResultSet.updateShort(columnLabel, x);
	}
	public void updateString(int columnIndex, String x) throws SQLException {
		subResultSet.updateString(columnIndex, x);
	}
	public void updateString(String columnLabel, String x) throws SQLException {
		subResultSet.updateString(columnLabel, x);
	}
	public void updateTime(int columnIndex, Time x) throws SQLException {
		subResultSet.updateTime(columnIndex, x);
	}
	public void updateTime(String columnLabel, Time x) throws SQLException {
		subResultSet.updateTime(columnLabel, x);
	}
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		subResultSet.updateTimestamp(columnIndex, x);
	}
	public void updateTimestamp(String columnLabel, Timestamp x)
			throws SQLException {
		subResultSet.updateTimestamp(columnLabel, x);
	}
	public boolean wasNull() throws SQLException {
		return subResultSet.wasNull();
	}
	// since jre 1.7

	//@Override
	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		//return subResultSet.getObject(arg0, arg1);
		return null;
	}

	//@Override
	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		//return subResultSet.getObject(arg0, arg1);
		return null;
	}
}
