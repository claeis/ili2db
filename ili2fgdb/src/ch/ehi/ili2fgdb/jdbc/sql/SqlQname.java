package ch.ehi.ili2fgdb.jdbc.sql;

import java.util.List;

public class SqlQname {
   private String localName=null;
	public SqlQname(List<String> c) {
		localName=c.get(c.size()-1);
	}

	public SqlQname(String name) {
		localName=name;
	}

	public String getLocalName() {
		return localName;
	}

}
