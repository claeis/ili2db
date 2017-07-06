package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class UpdateStmt extends SqlStmt {
	private String tableName=null;
	private List<java.util.Map.Entry<Value,Value>> settings=new ArrayList<java.util.Map.Entry<Value,Value>>();
	private List<java.util.Map.Entry<Value,Value>> conditions=new ArrayList<java.util.Map.Entry<Value,Value>>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void addSet(Value col, Value value) {
		settings.add(new java.util.AbstractMap.SimpleEntry<Value,Value>(col,value));
	}
	public List<java.util.Map.Entry<Value,Value>> getSettings() {
		return settings;
	}		
	public void addCond(Value col, Value value) {
		conditions.add(new java.util.AbstractMap.SimpleEntry<Value,Value>(col,value));
	}
	public List<java.util.Map.Entry<Value,Value>> getConditions() {
		return conditions;
	}		

}
