package ch.ehi.ili2db.base;

import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
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

}
