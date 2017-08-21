package ch.ehi.ili2db.mapping;

class AttrMappingKey {
	private String iliname;
	private String owner;
	private String target;
	public AttrMappingKey(String iliname, String owner, String target) {
		this.iliname = iliname;
		this.owner = owner;
		if(target!=null && target.trim().length()==0){
			target=null;
		}
		this.target = target;
	}
	public String getIliname() {
		return iliname;
	}
	public String getOwner() {
		return owner;
	}
	public String getTarget() {
		return target;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((iliname == null) ? 0 : iliname.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((target == null) ? 0 : target.hashCode());
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
		AttrMappingKey other = (AttrMappingKey) obj;
		if (iliname == null) {
			if (other.iliname != null)
				return false;
		} else if (!iliname.equals(other.iliname))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AttrMappingKey [iliname=" + iliname + ", owner=" + owner
				+ ", target=" + target + "]";
	}
}