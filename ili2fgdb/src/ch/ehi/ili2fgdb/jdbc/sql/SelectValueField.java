package ch.ehi.ili2fgdb.jdbc.sql;

public class SelectValueField extends SelectValue {
	private String colName=null;
	public SelectValueField(SqlQname col) {
		colName=col.getLocalName();
	}
	@Override
	public String getColumnName(){
		return colName;
	}
}
