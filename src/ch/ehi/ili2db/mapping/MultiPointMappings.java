package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.Iterator;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class MultiPointMappings {
	private HashMap<AttributeDef,MultiPointMapping> mappings=new HashMap<AttributeDef,MultiPointMapping>();
	public void addMultiPointAttr(AttributeDef multiPointAttr) {
		String bagOfPointsAttrName=null;
		String pointAttrName=null;
		// validate structure
		// create mapping
		Type multiPointTypeo=multiPointAttr.getDomain();
		if(!(multiPointTypeo instanceof CompositionType)){
			throw new IllegalArgumentException("not a valid multipoint attribute "+multiPointAttr.getScopedName(null));
		}else{
			CompositionType multiPointType=(CompositionType)multiPointTypeo;
			Table multiPointStruct=multiPointType.getComponentType();
			Iterator<ViewableTransferElement> it=multiPointStruct.getAttributesAndRoles2();
			if(!it.hasNext()){
				throw new IllegalArgumentException("not a valid multipoint structure "+multiPointStruct.getScopedName(null));
			}
			ViewableTransferElement prop = it.next();
			if(!(prop.obj instanceof AttributeDef)){
				throw new IllegalArgumentException("not a valid multipoint structure "+multiPointStruct.getScopedName(null));
			}
			AttributeDef pointsAttr=(AttributeDef) prop.obj;
			bagOfPointsAttrName=pointsAttr.getName();
			
			Type pointsTypeo=pointsAttr.getDomain();
			if(!(pointsTypeo instanceof CompositionType)){
				throw new IllegalArgumentException("not a valid multipoint structure "+multiPointStruct.getScopedName(null));
			}else{
				CompositionType pointsType=(CompositionType)pointsTypeo;
				Table pointStruct=pointsType.getComponentType();
				Iterator<ViewableTransferElement> it2=pointStruct.getAttributesAndRoles2();
				if(!it2.hasNext()){
					throw new IllegalArgumentException("not a valid point structure "+pointStruct.getScopedName(null));
				}
				ViewableTransferElement prop2 = it2.next();
				if(!(prop2.obj instanceof AttributeDef)){
					throw new IllegalArgumentException("not a valid point structure "+pointStruct.getScopedName(null));
				}
				AttributeDef pointAttr=(AttributeDef) prop2.obj;
				Type pointType=pointAttr.getDomainResolvingAliases();
				if(!(pointType instanceof CoordType)){
					throw new IllegalArgumentException("not a valid point structure "+pointStruct.getScopedName(null));
				}
				pointAttrName=pointAttr.getName();
			}
		}
		MultiPointMapping mapping=new MultiPointMapping(bagOfPointsAttrName, pointAttrName);
		mappings.put(multiPointAttr, mapping);
	}

	public MultiPointMapping getMapping(AttributeDef attr) {
		return mappings.get(attr);
	}

    public AttributeDef getCoordAttr(AttributeDef attr) {
        MultiPointMapping attrMapping=getMapping(attr);
        AttributeDef coordAttr = (AttributeDef) ((CompositionType) ((AttributeDef) ((CompositionType) attr.getDomainResolvingAll()).getComponentType().getElement(AttributeDef.class, attrMapping.getBagOfPointsAttrName())).getDomain()).getComponentType().getElement(AttributeDef.class,attrMapping.getPointAttrName());
        return coordAttr;
    }

}
