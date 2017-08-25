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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ehi.ili2fgdb.jdbc.sql.AbstractSelectStmt;
import ch.ehi.ili2fgdb.jdbc.sql.JoinStmt;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValue;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueField;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueNull;
import ch.ehi.ili2fgdb.jdbc.sql.SelectValueString;

public class JoinResultSet extends AbstractResultSet implements ResultSet {

	private ResultSet rsLeft=null;
	private List<ResultSet> rsRight=null;
	private String leftKeyCol=null;
	private Object rightRecord[][]=null;
	private Map<Object,Object[]> rightRecords[]=null;
	private int rightTableFieldCount[]=null;
	private int rightTableKeyIdx[]=null;
	private boolean lastGetWasNull=false;
	private JoinStmt stmt=null;
	private int rightc;
	public JoinResultSet(ResultSet rsLeft, List<ResultSet> rsRight, JoinStmt jstmt) throws SQLException {
		this.rsLeft=rsLeft;
		this.rsRight=rsRight;
		leftKeyCol=jstmt.getLeftKeyCol();
		rightc = rsRight.size();
		rightTableKeyIdx=new int[rightc]; // i based
		rightTableFieldCount=new int[rightc];
		rightRecords=new Map[rightc];
		for(int i=0;i<rightc;i++){
			rightTableKeyIdx[i]=rsRight.get(i).findColumn(jstmt.getRightKeyCol().get(i));
			rightTableFieldCount[i]=rsRight.get(i).getMetaData().getColumnCount();
			rightRecords[i]=new HashMap<Object,Object[]>();
		}
		stmt=jstmt;
	}

	@Override
	public boolean next() throws SQLException {
		if(!rsLeft.next()){
			return false;
		}
		Object key=rsLeft.getObject(leftKeyCol);
		rightRecord=findRightRecord(key);
		return true;
	}

	private Object[][] findRightRecord(Object key) throws SQLException {
		Object[][] ret=new Object[rightc][];
		for(int i=0;i<rightc;i++){
			ret[i]=findRightRecord(key,rightRecords[i],rsRight.get(i),rightTableFieldCount[i],rightTableKeyIdx[i]);
		}
		return ret;
	}
	private Object[] findRightRecord(Object key,Map<Object,Object[]> rightRecords,ResultSet rsRight, int rightTableFieldCount,int rightTableKeyIdx) throws SQLException {
		Object[] rec=rightRecords.get(key);
		if(rec!=null){
			return rec;
		}
		while(rsRight.next()){
			rec=new Object[rightTableFieldCount];
			for(int i=0;i<rightTableFieldCount;i++){
				rec[i]=rsRight.getObject(i+1);
			}
			Object recKey=rec[rightTableKeyIdx-1];
			rightRecords.put(recKey, rec);
			if(key.equals(recKey)){
				return rec;
			}
		}
		return null;
	}

	@Override
	public void close() throws SQLException {
		if(rsLeft!=null){
			rsLeft.close();rsLeft=null;
		}
		if(rsRight!=null){
			for(ResultSet rs:rsRight){
				rs.close();
			}
			rsRight=null;
		}
	}

	@Override
	public boolean wasNull() throws SQLException {
		return lastGetWasNull;
	}


	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		javax.sql.rowset.RowSetMetaDataImpl ret=new javax.sql.rowset.RowSetMetaDataImpl();
		ret.setColumnCount(stmt.getFields().size());
		return ret;
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		Object val=null;
		SelectValue selectValue=stmt.getFields().get(columnIndex-1);
		if(selectValue instanceof SelectValueNull){
			val=null;
		}else if(selectValue instanceof SelectValueString){
			val=((SelectValueString) selectValue).getLiteralValue();
		}else if(selectValue instanceof SelectValueField){
			String tabName=((SelectValueField) selectValue).getTableName();
			String colName=selectValue.getColumnName();
			int colIdxInLeftTab=0;
			int colIdxInRightTab=0;
			int rightTableIdx=-1;
			if(tabName!=null){
				String leftAlias=stmt.getLeftStmt().getTableAlias();
				if(leftAlias==null){
					leftAlias=stmt.getLeftStmt().getTableName();
				}
				if(leftAlias.equals(tabName)){
					// column in left table
					colIdxInLeftTab=stmt.getLeftStmt().findCol(colName);
				}else{
					// column in right table
					for(int i=0;i<rightc;i++){
						AbstractSelectStmt rightSelectStmt = stmt.getRightStmt().get(i);
						String rightAlias=rightSelectStmt.getTableAlias();
						if(rightAlias==null){
							rightAlias=rightSelectStmt.getTableName();
						}
						if(rightAlias.equals(tabName)){
							colIdxInRightTab=rightSelectStmt.findCol(colName);
							if(colIdxInRightTab>0){
								rightTableIdx=i;
								break;
							}
						}
					}
				}
			}else{
				colIdxInLeftTab=stmt.getLeftStmt().findCol(colName);
				if(colIdxInLeftTab==0){
					// column in right table
					for(int i=0;i<rightc;i++){
						colIdxInRightTab=stmt.getRightStmt().get(i).findCol(colName);
						if(colIdxInRightTab>0){
							rightTableIdx=i;
							break;
						}
					}
				}
			}
			if(colIdxInLeftTab>0){
				val=rsLeft.getObject(colIdxInLeftTab);
			}else if(rightRecord[rightTableIdx]==null){
				val=null;
			}else if(colIdxInRightTab>0){
				val=rightRecord[rightTableIdx][colIdxInRightTab-1];
			}else{
				val=null;
			}
		}
		if(val==null){
			lastGetWasNull=true;
		}else{
			lastGetWasNull=false;
		}
		return val;
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		int idx=stmt.findCol(columnLabel);
		if(idx==0){
			throw new SQLException("column <"+columnLabel+"> not found");
		}
		return idx;
	}

	@Override
	public boolean isClosed() throws SQLException {
		if(rsLeft==null && rsRight==null){
			return true;
		}
		return false;
	}

}
