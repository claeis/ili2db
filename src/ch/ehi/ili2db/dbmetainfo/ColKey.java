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
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ColKey other = (ColKey) obj;
        if (column == null) {
            if (other.column != null)
                return false;
        } else if (!column.equals(other.column))
            return false;
        if (subtype == null) {
            if (other.subtype != null)
                return false;
        } else if (!subtype.equals(other.subtype))
            return false;
        if (table == null) {
            if (other.table != null)
                return false;
        } else if (!table.equals(other.table))
            return false;
        return true;
    }
	
}
