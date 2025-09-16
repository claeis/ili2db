package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.Iterator;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class ArrayMappings {
    public static int MAX_ARRAY_EXPAND=5;
	private HashMap<AttributeDef,ArrayMapping> mappings=new HashMap<AttributeDef,ArrayMapping>();
	public void addArrayAttr(AttributeDef arrayAttr) {
		// validate structure
		// create mapping
		Type multiPointTypeo=arrayAttr.getDomain();
		if(!(multiPointTypeo instanceof CompositionType)){
            ArrayMapping mapping=new ArrayMapping(arrayAttr);
            mappings.put(arrayAttr, mapping);
		}else {
	        CompositionType multiPointType=(CompositionType)multiPointTypeo;
	        Table arrayEleStruct=multiPointType.getComponentType();
	        Iterator<ViewableTransferElement> it=arrayEleStruct.getAttributesAndRoles2();
	        if(!it.hasNext()){
	            throw new IllegalArgumentException("not a valid array structure "+arrayEleStruct.getScopedName(null));
	        }
	        ViewableTransferElement prop = it.next();
	        if(!(prop.obj instanceof AttributeDef)){
	            throw new IllegalArgumentException("not a valid array structure "+arrayEleStruct.getScopedName(null));
	        }
	        AttributeDef valueAttr=(AttributeDef) prop.obj;
	        ArrayMapping mapping=new ArrayMapping(valueAttr);
	        mappings.put(arrayAttr, mapping);
		}
	}

	public ArrayMapping getMapping(AttributeDef attr) {
		return mappings.get(attr);
	}

}
