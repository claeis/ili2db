package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

public class AbstractSelectStmt extends SqlStmt {
	private String tableName=null;
	private String tableAlias=null;
	private List<SelectValue> fields=new ArrayList<SelectValue>();
	private List<java.util.Map.Entry<Value,Value>> conditions=new ArrayList<java.util.Map.Entry<Value,Value>>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/** evtl. mehr Spalten vorhanden, als in der Tabelle vorhanden sind
	 */
	public List<SelectValue> getFields() {
		return fields;
	}
	public void addField(SelectValue f) {
		fields.add(f);
	}		
	public static void addField(AbstractSelectStmt stmt,SelectValue f) {
		String tabName=stmt.getTableAlias();
		if(tabName==null){
			tabName=stmt.getTableName();
		}
		if(f instanceof SelectValueField){
			String tabRef=((SelectValueField) f).getTableName();
			if(tabRef!=null){
				if(!tabRef.equals(tabName)){
					// do not add it
					return;
				}
			}
		}
		stmt.fields.add(f);
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
	public String getTableAlias() {
		return tableAlias;
	}
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
	/** find column with given name
	 * @return idx (1 based) or 0 if not found
	 */
	public int findCol(String colName) {
		int idx=1;
		for(SelectValue field:fields){
			if(field.getColumnName().equals(colName)){
				return idx;
			}
			idx++;
		}
		// not found
		return 0;
	}
	@Override
	public String toString() {
		return "AbstractSelectStmt [tableName=" + tableName + ", tableAlias="
				+ tableAlias + ", fields=" + fields + ", conditions="
				+ conditions + "]";
	}		

}
