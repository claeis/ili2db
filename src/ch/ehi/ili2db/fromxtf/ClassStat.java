package ch.ehi.ili2db.fromxtf;

public class ClassStat{
	public ClassStat(String tag, int startid) {
		super();
		this.tag = tag;
		this.objcount = 1;
		this.startid = startid;
		this.endid = startid;
	}
	private String tag=null;
	private int objcount=0;
	private int startid=0;
	private int endid=0;
	public int getObjcount() {
		return objcount;
	}
	public int getEndid() {
		return endid;
	}
	public void addEndid(int endid) {
		this.endid = endid;
		this.objcount++;
	}
	public String getTag() {
		return tag;
	}
	public int getStartid() {
		return startid;
	}
}