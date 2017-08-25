package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.List;

public class SqlQname {
	private String tableName=null;
   private String localName=null;
	public SqlQname(List<String> c) {
		localName=c.get(c.size()-1);
		if(c.size()>1){
			tableName=c.get(c.size()-2);
		}
	}

	public SqlQname(String name) {
		localName=name;
	}

	public String getLocalName() {
		return localName;
	}

	public String getTableName() {
		return tableName;
	}

}
