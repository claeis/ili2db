package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class Viewable2TableMapper {

	private Viewable2TableMapper() {	}

	public static Viewable2TableMapping getClass2TableMapping(Config config,
			TrafoConfig trafoConfig, List<Element> eles) {
		boolean smartInheritance=Config.INHERITANCE_TRAFO_SMART.equals(config.getInheritanceTrafo());
		// setup/update TrafoConfig
		/*
		 * Für Klassen, die referenziert werden und deren Basisklassen nicht mit
		 * einer NewClass-Strategie abgebildet werden, wird die
		 * NewClass-Strategie verwendet. Abstrakte Klassen werden mit einer
		 * SubClass-Strategie abgebildet. Konkrete Klassen, ohne Basisklasse
		 * oder deren direkte Basisklassen mit einer SubClass-Strategie
		 * abgebildet werden, werden mit einer NewClass-Strategie abgebildet.
		 * Alle anderen Klassen werden mit einer SuperClass-Strategie
		 * abgebildet.
		 */
		for(Element ele:eles){
			if(!(ele instanceof Viewable)){
				// not a Viewable; skip it
			}else{
				// a Viewable
				Viewable aclass=(Viewable) ele;
				if(trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)==null){
					if(smartInheritance){
						if(isReferenced(aclass) && noBaseIsNewClass(trafoConfig,aclass)){
							// newClass
							trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS);
						}else if(aclass.isAbstract()){
							// subClass
							trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS);
						}else{
							Viewable base=(Viewable) aclass.getExtending();
							if(base==null || TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO))){
								// newClass
								trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS);
							}else{
								// superClass
								trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_SUPERCLASS);
							}
						}
					}else{
						// newClass
						trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS);
					}
				}
			}
		}
		Viewable2TableMapping ret=new Viewable2TableMapping();
		// create ViewableWrappers for all NewClass viewables
		for(Element ele:eles){
			if(!(ele instanceof Viewable)){
				// not a Viewable; skip it
			}else{
				// a Viewable
				Viewable aclass=(Viewable) ele;
				String inheritanceStrategy = trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO);
				if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(inheritanceStrategy)){
					ViewableWrapper wrapper=new ViewableWrapper(aclass);
					List<ViewableTransferElement> props=new java.util.ArrayList<ViewableTransferElement>();
					// defined attrs
					{
						addProps(props,aclass.getDefinedAttributesAndRoles2());
					}
					// defined attrs of bases with subclass strategy
					{
						Viewable base=(Viewable) aclass.getExtending();
						while(base!=null){
							if(!TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO))){
								break;
							}
							ret.add(base, wrapper);
							addProps(props,base.getDefinedAttributesAndRoles2());
							base=(Viewable) base.getExtending();
						}
					}
					// add attrs of extensions with superclass strategy while visiting extensions
					
					wrapper.setAttrv(props);
					// get base ViewableWrapper
					{
						Viewable base=(Viewable) aclass.getExtending();
						while(base!=null){
							if(!TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO))){
								break;
							}
							base=(Viewable) base.getExtending();
						}
						if(base!=null){
							ViewableWrapper baseWrapper=ret.get(base);
							if(baseWrapper!=wrapper){
								wrapper.setExtending(baseWrapper);
							}
						}
					}
					ret.add(aclass, wrapper);
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_SUPERCLASS.equals(inheritanceStrategy)){
					// add props of extensions with superclass strategy to base-class
					// find base
					Viewable base=(Viewable) aclass.getExtending();
					while(base!=null){
						if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO))){
							break;
						}
						base=(Viewable) base.getExtending();
					}
					ViewableWrapper wrapper=ret.get(base);
					List<ViewableTransferElement> props=wrapper.getAttrv();
					// add props of extension
					addProps(props,aclass.getDefinedAttributesAndRoles2());
					wrapper.setAttrv(props);
					ret.add(aclass, wrapper);
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(inheritanceStrategy)){
					// skip it; props already added when visiting subclass
				}else{
					throw new IllegalStateException("unexpected inheritance config <"+inheritanceStrategy+">");
				}
			}
		}
		return ret;
	}

	private static void addProps(List<ViewableTransferElement> attrv,
		Iterator<ViewableTransferElement> iter) {
		while (iter.hasNext()) {
			ViewableTransferElement obj = iter.next();
			if (obj.obj instanceof AttributeDef) {
				attrv.add(obj);
			}
			if(obj.obj instanceof RoleDef){
				RoleDef role = (RoleDef) obj.obj;
				AssociationDef roleOwner = (AssociationDef) role.getContainer();
				// not an embedded role and roledef not defined in a lightweight association?
				if (!obj.embedded && !roleOwner.isLightweight()){
					attrv.add(obj);
				}
				// a role of an embedded association?
				if(obj.embedded){
					if(roleOwner.getDerivedFrom()==null){
						attrv.add(obj);
					}
				}
			}
		}
		
	}

	private static boolean noBaseIsNewClass(TrafoConfig trafoConfig,Viewable aclass) {
		Viewable base=(Viewable) aclass.getExtending();
		while(base!=null){
			if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO))){
				return false;
			}
		}
		return true;
	}

	private static boolean isReferenced(Viewable viewable) {
		if(viewable instanceof AbstractClassDef){
			AbstractClassDef aclass=(AbstractClassDef) viewable;
			Iterator<RoleDef> rolei=aclass.getDefinedTargetForRoles();
			while(rolei.hasNext()){
				RoleDef role=rolei.next();
				AssociationDef assoc=(AssociationDef) role.getContainer();
				if(!assoc.isLightweight()){
					return true;
				}
				if(!role.isAssociationEmbedded()){
					// opposide role is embedded into opposide class, so opposide class references this viewable
					return true;
				}
			}
		}
		return false;
	}

}
