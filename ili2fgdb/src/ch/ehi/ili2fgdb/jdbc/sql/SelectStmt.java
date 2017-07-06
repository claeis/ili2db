package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class SelectStmt extends SqlStmt {
	private String tableName=null;
	private List<String> fields=new ArrayList<String>();
	private List<java.util.Map.Entry<Value,Value>> conditions=new ArrayList<java.util.Map.Entry<Value,Value>>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<String> getFields() {
		return fields;
	}
	public void addField(String field) {
		fields.add(field);
	}		
	public void addCond(Value col, Value value) {
		conditions.add(new java.util.AbstractMap.SimpleEntry<Value,Value>(col,value));
	}
	public List<java.util.Map.Entry<Value,Value>> getConditions() {
		return conditions;
	}		

}
