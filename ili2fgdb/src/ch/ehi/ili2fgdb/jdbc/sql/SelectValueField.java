package ch.ehi.ili2fgdb.jdbc.sql;

public class SelectValueField extends SelectValue {
	private String colName=null;
	private String tabName=null;
	public SelectValueField(SqlQname col) {
		colName=col.getLocalName();
		tabName=col.getTableName();
	}
	@Override
	public String getColumnName(){
		return colName;
	}
	public String getTableName(){
		return tabName;
	}
	@Override
	public String toString() {
		return "SelectValueField [colName=" + colName + ", tabName=" + tabName
				+ "]";
	}
	
}
