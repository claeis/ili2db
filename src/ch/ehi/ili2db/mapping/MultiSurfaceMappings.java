package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.Iterator;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class MultiSurfaceMappings {
	private HashMap<AttributeDef,MultiSurfaceMapping> mappings=new HashMap<AttributeDef,MultiSurfaceMapping>();
	public void addMultiSurfaceAttr(AttributeDef multiSurfaceAttr) {
		String bagOfSurfacesAttrName=null;
		String surfaceAttrName=null;
		// validate structure
		// create mapping
		Type multiSurfaceTypeo=multiSurfaceAttr.getDomain();
		if(!(multiSurfaceTypeo instanceof CompositionType)){
			throw new IllegalArgumentException("not a valid multisurface attribute "+multiSurfaceAttr.getScopedName(null));
		}else{
			CompositionType multiSurfaceType=(CompositionType)multiSurfaceTypeo;
			Table multiSurfaceStruct=multiSurfaceType.getComponentType();
			Iterator<ViewableTransferElement> it=multiSurfaceStruct.getAttributesAndRoles2();
			if(!it.hasNext()){
				throw new IllegalArgumentException("not a valid multisurface structure "+multiSurfaceStruct.getScopedName(null));
			}
			ViewableTransferElement prop = it.next();
			if(!(prop.obj instanceof AttributeDef)){
				throw new IllegalArgumentException("not a valid multisurface structure "+multiSurfaceStruct.getScopedName(null));
			}
			AttributeDef surfacesAttr=(AttributeDef) prop.obj;
			bagOfSurfacesAttrName=surfacesAttr.getName();
			
			Type surfacesTypeo=surfacesAttr.getDomain();
			if(!(surfacesTypeo instanceof CompositionType)){
				throw new IllegalArgumentException("not a valid multisurface structure "+multiSurfaceStruct.getScopedName(null));
			}else{
				CompositionType surfacesType=(CompositionType)surfacesTypeo;
				Table surfaceStruct=surfacesType.getComponentType();
				Iterator<ViewableTransferElement> it2=surfaceStruct.getAttributesAndRoles2();
				if(!it2.hasNext()){
					throw new IllegalArgumentException("not a valid surface structure "+surfaceStruct.getScopedName(null));
				}
				ViewableTransferElement prop2 = it2.next();
				if(!(prop2.obj instanceof AttributeDef)){
					throw new IllegalArgumentException("not a valid surface structure "+surfaceStruct.getScopedName(null));
				}
				AttributeDef surfaceAttr=(AttributeDef) prop2.obj;
				Type surfaceType=surfaceAttr.getDomainResolvingAliases();
				if(!(surfaceType instanceof SurfaceType)){
					throw new IllegalArgumentException("not a valid surface structure "+surfaceStruct.getScopedName(null));
				}
				surfaceAttrName=surfaceAttr.getName();
			}
		}
		MultiSurfaceMapping mapping=new MultiSurfaceMapping(bagOfSurfacesAttrName, surfaceAttrName);
		mappings.put(multiSurfaceAttr, mapping);
	}

	public MultiSurfaceMapping getMapping(AttributeDef attr) {
		return mappings.get(attr);
	}
    public AttributeDef getSurfaceAttr(AttributeDef attr) {
        MultiSurfaceMapping attrMapping=getMapping(attr);
        return (AttributeDef) ((CompositionType) ((AttributeDef) ((CompositionType) attr.getDomainResolvingAll()).getComponentType().getElement(AttributeDef.class, attrMapping.getBagOfSurfacesAttrName())).getDomain()).getComponentType().getElement(AttributeDef.class,attrMapping.getSurfaceAttrName());
    }

}
