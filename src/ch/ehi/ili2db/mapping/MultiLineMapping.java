package ch.ehi.ili2db.mapping;

public class MultiLineMapping {

	private String bagOfLinesAttrName;
	private String lineAttrName;
	
	public MultiLineMapping(String bagOfLinesAttrName,
			String lineAttrName) {
		this.bagOfLinesAttrName = bagOfLinesAttrName;
		this.lineAttrName = lineAttrName;
	}

	public String getBagOfLinesAttrName() {
		return bagOfLinesAttrName;
	}

	public String getLineAttrName() {
		return lineAttrName;
	}

}
