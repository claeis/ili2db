package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class AbstractSelectStmt extends SqlStmt {
	private String tableName=null;
	private List<SelectValue> fields=new ArrayList<SelectValue>();
	private List<java.util.Map.Entry<Value,Value>> conditions=new ArrayList<java.util.Map.Entry<Value,Value>>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<SelectValue> getFields() {
		return fields;
	}
	public void addField(SelectValue f) {
		fields.add(f);
	}		
	public void addCond(Value col, Value value) {
		conditions.add(new java.util.AbstractMap.SimpleEntry<Value,Value>(col,value));
	}
	public List<java.util.Map.Entry<Value,Value>> getConditions() {
		return conditions;
	}
	public void orderAsc() {
		// TODO Auto-generated method stub
		
	}
	public void orderBy(String string) {
		// TODO Auto-generated method stub
		
	}		

}
