package ch.ehi.ili2db.dbmetainfo;

public class ColKey {
	private String table=null;
	private String subtype=null;
	private String column=null;
	public ColKey(String table, String subtype, String column) {
		super();
		this.table = table;
		this.subtype = subtype;
		this.column = column;
	}
	public String getTable() {
		return table;
	}
	public String getSubtype() {
		return subtype;
	}
	public String getColumn() {
		return column;
	}
	
}
