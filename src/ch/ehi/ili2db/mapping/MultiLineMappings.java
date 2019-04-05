package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.Iterator;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class MultiLineMappings {
	private HashMap<AttributeDef,MultiLineMapping> mappings=new HashMap<AttributeDef,MultiLineMapping>();
	public void addMultiLineAttr(AttributeDef multiLineAttr) {
		String bagOfLinesAttrName=null;
		String lineAttrName=null;
		// validate structure
		// create mapping
		Type multiLineTypeo=multiLineAttr.getDomain();
		if(!(multiLineTypeo instanceof CompositionType)){
			throw new IllegalArgumentException("not a valid multiline attribute "+multiLineAttr.getScopedName(null));
		}else{
			CompositionType multiLineType=(CompositionType)multiLineTypeo;
			Table multiLineStruct=multiLineType.getComponentType();
			Iterator<ViewableTransferElement> it=multiLineStruct.getAttributesAndRoles2();
			if(!it.hasNext()){
				throw new IllegalArgumentException("not a valid multiline structure "+multiLineStruct.getScopedName(null));
			}
			ViewableTransferElement prop = it.next();
			if(!(prop.obj instanceof AttributeDef)){
				throw new IllegalArgumentException("not a valid multiline structure "+multiLineStruct.getScopedName(null));
			}
			AttributeDef linesAttr=(AttributeDef) prop.obj;
			bagOfLinesAttrName=linesAttr.getName();
			
			Type linesTypeo=linesAttr.getDomain();
			if(!(linesTypeo instanceof CompositionType)){
				throw new IllegalArgumentException("not a valid multiline structure "+multiLineStruct.getScopedName(null));
			}else{
				CompositionType linesType=(CompositionType)linesTypeo;
				Table lineStruct=linesType.getComponentType();
				Iterator<ViewableTransferElement> it2=lineStruct.getAttributesAndRoles2();
				if(!it2.hasNext()){
					throw new IllegalArgumentException("not a valid line structure "+lineStruct.getScopedName(null));
				}
				ViewableTransferElement prop2 = it2.next();
				if(!(prop2.obj instanceof AttributeDef)){
					throw new IllegalArgumentException("not a valid line structure "+lineStruct.getScopedName(null));
				}
				AttributeDef lineAttr=(AttributeDef) prop2.obj;
				Type lineType=lineAttr.getDomainResolvingAliases();
				if(!(lineType instanceof PolylineType)){
					throw new IllegalArgumentException("not a valid line structure "+lineStruct.getScopedName(null));
				}
				lineAttrName=lineAttr.getName();
			}
		}
		MultiLineMapping mapping=new MultiLineMapping(bagOfLinesAttrName, lineAttrName);
		mappings.put(multiLineAttr, mapping);
	}

	public MultiLineMapping getMapping(AttributeDef attr) {
		return mappings.get(attr);
	}
    public AttributeDef getPolylineAttr(AttributeDef attr) {
        MultiLineMapping attrMapping=getMapping(attr);
        AttributeDef polylineAttr = (AttributeDef) ((CompositionType) ((AttributeDef) ((CompositionType) attr.getDomainResolvingAll()).getComponentType().getElement(AttributeDef.class, attrMapping.getBagOfLinesAttrName())).getDomain()).getComponentType().getElement(AttributeDef.class,attrMapping.getLineAttrName());
        return polylineAttr;
    }

}
