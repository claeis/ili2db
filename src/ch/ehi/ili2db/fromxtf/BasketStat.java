package ch.ehi.ili2db.fromxtf;

import java.util.HashMap;


public class BasketStat{
	public BasketStat(String datasource,
			String topic, String iliBasketId,HashMap<String, ClassStat> objStat) {
		super();
		this.objStat = objStat;
		this.datasource = datasource;
		this.topic = topic;
		this.basketId = iliBasketId;
	}
	private HashMap<String, ClassStat> objStat=null;
	private String datasource=null;
	private String topic=null;
	private String basketId=null;
	public HashMap<String, ClassStat> getObjStat() {
		return objStat;
	}
	public String getDatasource() {
		return datasource;
	}
	public String getTopic() {
		return topic;
	}
	public String getBasketId() {
		return basketId;
	}
	
}