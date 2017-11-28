package ch.ehi.ili2db.mapping;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Type;

public class ArrayMapping {

	private AttributeDef valueAttr;
	
	public ArrayMapping(AttributeDef pointAttrName) {
		this.valueAttr = pointAttrName;
	}

	public AttributeDef getValueAttr() {
		return valueAttr;
	}

}
