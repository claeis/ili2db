package ch.ehi.ili2db.base;

import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.FormattedType;
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

	static public boolean isIli1Date(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias){
			Type type=attr.getDomain();
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.INTERLIS_1_DATE) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		return false;
	}

	static public boolean isIli2Date(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlDate) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlDate){
				return true;
			}
		}
		return false;
	}

	static public boolean isIli2Time(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlTime) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlTime){
				return true;
			}
		}
		return false;
	}

	static public boolean isIli2DateTime(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlDateTime) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlDateTime){
				return true;
			}
		}
		return false;
	}

	static public boolean isIliUuid(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias){
			Type type=attr.getDomain();
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.UUIDOID) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		return false;
	}

	static public boolean isBoolean(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias && Ili2cUtility.isBoolean(td,attr.getDomain())) {
			return true;
		}
		return false;
		
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
	public static boolean isUuidOid(TransferDescription td, AttributeDef attr) {
		if (attr.getDomain() instanceof TypeAlias && Ili2cUtility.isUuidOid(td,attr.getDomain())) {
			return true;
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

	public static boolean isPureChbaseMultilingualMText(TransferDescription td,
			AttributeDef attr) {
		return isPureChbaseMultilingualText(td, attr, IliNames.CHBASE1_MULTILINGUALMTEXT);
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


}
