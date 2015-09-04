package ch.ehi.ili2geodb.sqlgen;

public class GeodbLink {
	private String ds;
	private String relClassName;
	private String originClass;
	private String destinationClass;
	private String forwardLabel;
	private String backwardLabel;
	private int cardinality;
	private int notification;
	private boolean isComposite;
	private String originPrimaryKey;
	private String originForeignKey;
  public GeodbLink(
			String ds
			,String relClassName
			,String originClass
			,String destinationClass
			,String forwardLabel
			,String backwardLabel
			, int cardinality
			, int notification
			, boolean isComposite
			, String originPrimaryKey
			, String originForeignKey
		  )
  {
	  this.ds=ds;
		this.relClassName=relClassName;
		this.originClass=originClass;
		this.destinationClass=destinationClass;
		this.forwardLabel=forwardLabel;
		this.backwardLabel=backwardLabel;
		this.cardinality=cardinality;
		this.notification=notification;
		this.isComposite=isComposite;
		this.originPrimaryKey=originPrimaryKey;
		this.originForeignKey=originForeignKey;
  }
public String getBackwardLabel() {
	return backwardLabel;
}
public int getCardinality() {
	return cardinality;
}
public String getDestinationClass() {
	return destinationClass;
}
public String getDs() {
	return ds;
}
public String getForwardLabel() {
	return forwardLabel;
}
public boolean isComposite() {
	return isComposite;
}
public int getNotification() {
	return notification;
}
public String getOriginClass() {
	return originClass;
}
public String getOriginForeignKey() {
	return originForeignKey;
}
public String getOriginPrimaryKey() {
	return originPrimaryKey;
}
public String getRelClassName() {
	return relClassName;
}
  
}
