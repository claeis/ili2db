package ch.ehi.ili2db.mapping;

public class MultiPointMapping {

	private String bagOfPointsAttrName;
	private String pointAttrName;
	
	public MultiPointMapping(String bagOfPointsAttrName,
			String pointAttrName) {
		this.bagOfPointsAttrName = bagOfPointsAttrName;
		this.pointAttrName = pointAttrName;
	}

	public String getBagOfPointsAttrName() {
		return bagOfPointsAttrName;
	}

	public String getPointAttrName() {
		return pointAttrName;
	}

}
