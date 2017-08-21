package ch.ehi.ili2fgdb.jdbc.sql;

public class ColRef extends Value {
	private String colName=null;
	public ColRef(String colName) {
		this.colName=colName;
	}

	public String getName() {
		return colName;
	}

}
