package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class InsertStmt extends SqlStmt {
	private String tableName=null;
	private List<String> fields=new ArrayList<String>();
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

}
