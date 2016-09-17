package ch.ehi.ili2db.mapping;

public class MultiSurfaceMapping {

	private String bagOfSurfacesAttrName;
	private String surfaceAttrName;
	
	public MultiSurfaceMapping(String bagOfSurfacesAttrName,
			String surfaceAttrName) {
		this.bagOfSurfacesAttrName = bagOfSurfacesAttrName;
		this.surfaceAttrName = surfaceAttrName;
	}

	public String getBagOfSurfacesAttrName() {
		return bagOfSurfacesAttrName;
	}

	public String getSurfaceAttrName() {
		return surfaceAttrName;
	}

}
