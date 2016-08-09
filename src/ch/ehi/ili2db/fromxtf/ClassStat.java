package ch.ehi.ili2db.fromxtf;

public class ClassStat{
	public ClassStat(String tag, long startid) {
		super();
		this.tag = tag;
		this.objcount = 1;
		this.startid = startid;
		this.endid = startid;
	}
	private String tag=null;
	private long objcount=0;
	private long startid=0;
	private long endid=0;
	public long getObjcount() {
		return objcount;
	}
	public long getEndid() {
		return endid;
	}
	public void addEndid(long endid) {
		this.endid = endid;
		this.objcount++;
	}
	public String getTag() {
		return tag;
	}
	public long getStartid() {
		return startid;
	}
}