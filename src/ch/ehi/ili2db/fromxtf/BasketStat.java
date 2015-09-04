package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;


public class BasketStat{
	public BasketStat(String file,
			String topic, String basketId,HashMap<String, ClassStat> objStat) {
		super();
		this.objStat = objStat;
		this.file = file;
		this.topic = topic;
		this.basketId = basketId;
	}
	private HashMap<String, ClassStat> objStat=null;
	private String file=null;
	private String topic=null;
	private String basketId=null;
	public HashMap<String, ClassStat> getObjStat() {
		return objStat;
	}
	public String getFile() {
		return file;
	}
	public String getTopic() {
		return topic;
	}
	public String getBasketId() {
		return basketId;
	}
	
}