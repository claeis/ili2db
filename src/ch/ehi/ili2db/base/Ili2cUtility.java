package ch.ehi.ili2db.base;

import ch.ehi.ili2db.mapping.IliMetaAttrNames;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.TypeAlias;
import ch.interlis.ili2c.metamodel.Viewable;

/** functions that should be moved to ili2c.
 */
public class Ili2cUtility {

	/** tests if a viewable has no (known) extensions.
	 * @param def viewable to test
	 * @return true if no subtypes known; true if subtypes known.
	 */
	public static boolean isViewableWithExtension(Viewable def){
		return def.getExtensions().size()>1;
	}

	static public boolean isBoolean(TransferDescription td,Type type){
		while(type instanceof TypeAlias) {
			if (((TypeAlias) type).getAliasing() == td.INTERLIS.BOOLEAN) {
				return true;
			}
			type=((TypeAlias) type).getAliasing().getType();
		}
		
		return false;
	}
	static public boolean isUuidOid(TransferDescription td,Type type){
		while(type instanceof TypeAlias) {
			if (((TypeAlias) type).getAliasing() == td.INTERLIS.UUIDOID) {
				return true;
			}
			type=((TypeAlias) type).getAliasing().getType();
		}
		
		return false;
	}

	public static boolean isViewableWithOid(Viewable def) {
		if(!(def instanceof AbstractClassDef)){
			return false;
		}
		if((def instanceof Table) && !((Table)def).isIdentifiable()){
			return false;
		}
		AbstractClassDef aclass=(AbstractClassDef) def;
		if(aclass.getOid()!=null){
			return true;
		}
		for(Object exto : aclass.getExtensions()){
			AbstractClassDef ext=(AbstractClassDef) exto;
			if(ext.getOid()!=null){
				return true;
			}
		}
		return false;
	}
	public static Viewable getRootViewable(Viewable def) {
		Viewable root=(Viewable) def.getRootExtending();
		if(root==null){
			root=def;
		}
		return root;
	}

	public static boolean isChbaseCatalogueItem(TransferDescription td,Viewable aclass) {
		Viewable root=getRootViewable(aclass);
		return root.getScopedName().equals(IliNames.CHBASE1_CATALOGUES_ITEM);
	}
	public static boolean isPureChbaseCatalogueRef(TransferDescription td,AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)typeo;
			Table struct=type.getComponentType();
			Table root=(Table) struct.getRootExtending();
			if(root==null){
				root=struct;
			}
			if(root.getContainer().getScopedName(null).equals(IliNames.CHBASE1_CATALOGUEOBJECTS_CATALOGUES)){
				if(root.getName().equals(IliNames.CHBASE1_CATALOGUEREFERENCE) || root.getName().equals(IliNames.CHBASE1_MANDATORYCATALOGUEREFERENCE)){
					java.util.Iterator it=struct.getAttributesAndRoles2();
					int c=0;
					while(it.hasNext()){
						it.next();
						c++;
					}
					if(c==1){
						// only one attribute
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean isPureChbaseMultiSuface(TransferDescription td,AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)typeo;
			Table struct=type.getComponentType();
			Table root=(Table) struct.getRootExtending();
			if(root==null){
				root=struct;
			}
			String containerQName=root.getContainer().getScopedName(null);
			if(containerQName.equals(IliNames.CHBASE1_GEOMETRYCHLV03) || containerQName.equals(IliNames.CHBASE1_GEOMETRYCHLV95)){
				if(root.getName().equals(IliNames.CHBASE1_GEOMETRY_MULTISURFACE)){
					java.util.Iterator it=struct.getAttributesAndRoles2();
					int c=0;
					while(it.hasNext()){
						it.next();
						c++;
					}
					if(c==1){
						// only one attribute
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean isPureChbaseMultiLine(TransferDescription td,AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)typeo;
			Table struct=type.getComponentType();
			Table root=(Table) struct.getRootExtending();
			if(root==null){
				root=struct;
			}
			String containerQName=root.getContainer().getScopedName(null);
			if(containerQName.equals(IliNames.CHBASE1_GEOMETRYCHLV03) || containerQName.equals(IliNames.CHBASE1_GEOMETRYCHLV95)){
				if(root.getName().equals(IliNames.CHBASE1_GEOMETRY_MULTILINE) || root.getName().equals(IliNames.CHBASE1_GEOMETRY_MULTIDIRECTEDLINE)){
					java.util.Iterator it=struct.getAttributesAndRoles2();
					int c=0;
					while(it.hasNext()){
						it.next();
						c++;
					}
					if(c==1){
						// only one attribute
						return true;
					}
				}
			}
		}
		return false;
	}

    public static boolean isPureChbaseLocalisedMText(TransferDescription td,
            AttributeDef attr) {
        return isPureChbaseMultilingualText(td, attr, IliNames.CHBASE1_MULTILINGUALMTEXT);
    }
    public static boolean isPureChbaseLocalisedText(TransferDescription td,
                AttributeDef attr) {
        return isPureChbaseLocalisedText(td, attr, IliNames.CHBASE1_LOCALISEDTEXT);
    }
	public static boolean isPureChbaseMultilingualMText(TransferDescription td,
			AttributeDef attr) {
		return isPureChbaseLocalisedText(td, attr, IliNames.CHBASE1_LOCALISEDMTEXT);
	}
    private static boolean isPureChbaseLocalisedText(TransferDescription td,
            AttributeDef attr,String textType) {
        Type typeo=attr.getDomain();
        if(typeo instanceof CompositionType){
            CompositionType type=(CompositionType)typeo;
            Table struct=type.getComponentType();
            Table base=null;
            if(struct.getContainer().getScopedName(null).equals(IliNames.CHBASE1_LOCALISATIONCH)){
                base=struct;
            }else{
                base=(Table) struct.getExtending();
                if(base==null){
                    base=struct;
                }
                while(base!=null && !base.getContainer().getScopedName(null).equals(IliNames.CHBASE1_LOCALISATIONCH)){
                    base=(Table) base.getExtending();
                }
                
            }
            if(base==null){
                return false;
            }
            // ASSERT: base.getContainer().getScopedName(null).equals("LocalisationCH_V1"))
                if(base.getName().equals(textType)){
                    java.util.Iterator it=struct.getAttributesAndRoles2();
                    int c=0;
                    while(it.hasNext()){
                        it.next();
                        c++;
                    }
                    if(c==2){
                        // only two attributes text + language
                        return true;
                    }
                }
        }
        return false;
    }
	public static boolean isPureChbaseMultilingualText(TransferDescription td,
				AttributeDef attr) {
		return isPureChbaseMultilingualText(td, attr, IliNames.CHBASE1_MULTILINGUALTEXT);
	}
	private static boolean isPureChbaseMultilingualText(TransferDescription td,
			AttributeDef attr,String textType) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)typeo;
			Table struct=type.getComponentType();
			Table base=null;
			if(struct.getContainer().getScopedName(null).equals(IliNames.CHBASE1_LOCALISATIONCH)){
				base=struct;
			}else{
				base=(Table) struct.getExtending();
				if(base==null){
					base=struct;
				}
				while(base!=null && !base.getContainer().getScopedName(null).equals(IliNames.CHBASE1_LOCALISATIONCH)){
					base=(Table) base.getExtending();
				}
				
			}
			if(base==null){
				return false;
			}
			// ASSERT: base.getContainer().getScopedName(null).equals("LocalisationCH_V1"))
				if(base.getName().equals(textType)){
					java.util.Iterator it=struct.getAttributesAndRoles2();
					int c=0;
					while(it.hasNext()){
						it.next();
						c++;
					}
					if(c==1){
						// only one attribute LocalisedText
						return true;
					}
				}
		}
		return false;
	}

	public static boolean isMultiSurfaceAttr(TransferDescription td,
			AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)attr.getDomain();
			if(type.getCardinality().getMaximum()==1){
				if(isPureChbaseMultiSuface(td, attr)){
					return true;
				}
				Table struct=type.getComponentType();
				if(IliMetaAttrNames.METAATTR_MAPPING_MULTISURFACE.equals(struct.getMetaValue(IliMetaAttrNames.METAATTR_MAPPING))){
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isMultiLineAttr(TransferDescription td,
			AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)attr.getDomain();
			if(type.getCardinality().getMaximum()==1){
				if(isPureChbaseMultiLine(td, attr)){
					return true;
				}
				Table struct=type.getComponentType();
				if(IliMetaAttrNames.METAATTR_MAPPING_MULTILINE.equals(struct.getMetaValue(IliMetaAttrNames.METAATTR_MAPPING))){
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isMultiPointAttr(TransferDescription td,
			AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			CompositionType type=(CompositionType)attr.getDomain();
			if(type.getCardinality().getMaximum()==1){
				Table struct=type.getComponentType();
				if(IliMetaAttrNames.METAATTR_MAPPING_MULTIPOINT.equals(struct.getMetaValue(IliMetaAttrNames.METAATTR_MAPPING))){
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isArrayAttr(TransferDescription td,
			AttributeDef attr) {
		Type typeo=attr.getDomain();
		if(typeo instanceof CompositionType){
			if(IliMetaAttrNames.METAATTR_MAPPING_ARRAY.equals(attr.getMetaValue(IliMetaAttrNames.METAATTR_MAPPING))){
				return true;
			}
		}
		return false;
	}
    public static boolean isJsonAttr(TransferDescription td,
            AttributeDef attr) {
        Type typeo=attr.getDomain();
        if(typeo instanceof CompositionType){
            if(IliMetaAttrNames.METAATTR_MAPPING_JSON.equals(attr.getMetaValue(IliMetaAttrNames.METAATTR_MAPPING))){
                return true;
            }
        }
        return false;
    }

    public static Domain getRootBaseDomain(Domain domain) {
        Domain base=domain.getExtending();
        while(base!=null) {
            domain=base;
            base=domain.getExtending();
        }
        return domain;
    }

    public static AttributeDef getRootBaseAttr(AttributeDef attr) {
        AttributeDef base=(AttributeDef) attr.getExtending();
        while(base!=null) {
            attr=base;
            base=(AttributeDef) attr.getExtending();
        }
        return attr;
    }
    public static RoleDef getRootBaseRole(RoleDef role) {
        RoleDef base=(RoleDef) role.getExtending();
        while(base!=null){
            role=base;
            base=(RoleDef) role.getExtending();
        }
        return role;
    }

}
