package ch.ehi.ili2db.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class Viewable2TableMapper {

	private Config config=null;
	private String sqlSchemaname=null;
	private TrafoConfig trafoConfig=null;
	private NameMapping nameMapping=null;	
	private boolean singleGeom=false;
	private boolean coalesceMultiSurface=false;
	private boolean coalesceMultiLine=false;
	private boolean coalesceMultiPoint=false;
	private boolean coalesceArray=false;
	private boolean createItfLineTables=false;
    private String defaultCrsAuthority=null;
    private String defaultCrsCode=null;
    private String srsModelAssignment=null;
	private TransferDescription td=null;
	private Viewable2TableMapper(Config config1,
			TrafoConfig trafoConfig1, NameMapping nameMapping1) {	
		config=config1;
		trafoConfig=trafoConfig1;
		nameMapping=nameMapping1;
		sqlSchemaname=config.getDbschema();
        defaultCrsAuthority=config.getDefaultSrsAuthority();
        defaultCrsCode=config.getDefaultSrsCode();
        srsModelAssignment=config.getSrsModelAssignment();
	}

	public static Viewable2TableMapping getClass2TableMapping(Config config,
			TrafoConfig trafoConfig, List<Element> eles,NameMapping nameMapping) {
		Viewable2TableMapper mapper=new Viewable2TableMapper(config, trafoConfig, nameMapping);
		mapper.singleGeom=config.isOneGeomPerTable();
		mapper.coalesceMultiSurface=Config.MULTISURFACE_TRAFO_COALESCE.equals(config.getMultiSurfaceTrafo());
		mapper.coalesceMultiLine=Config.MULTILINE_TRAFO_COALESCE.equals(config.getMultiLineTrafo());
		mapper.coalesceMultiPoint=Config.MULTIPOINT_TRAFO_COALESCE.equals(config.getMultiPointTrafo());
		mapper.coalesceArray=Config.ARRAY_TRAFO_COALESCE.equals(config.getArrayTrafo());
		mapper.createItfLineTables=config.getDoItfLineTables();
		return mapper.doit(eles);
	}
	private Viewable2TableMapping doit(List<Element> eles) {
		// 
		// setup/update TrafoConfig (mapping strategy per class)
		//
		if(Config.INHERITANCE_TRAFO_SMART1.equals(config.getInheritanceTrafo())){
			doSmart1(eles);
		}else if(Config.INHERITANCE_TRAFO_SMART2.equals(config.getInheritanceTrafo())){
			doSmart2(eles);
		}else{
			doSmartOff(eles);
		}
		//
		// create ViewableWrappers for all NewClass or NewAndSubClass tagged viewables
		//
		Viewable2TableMapping ret=new Viewable2TableMapping();
		for(Element ele:eles){
			if(!(ele instanceof Viewable)){
				// not a Viewable; skip it
			}else{
				// a Viewable
				Viewable aclass=(Viewable) ele;
				String inheritanceStrategy = trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO);
				if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(inheritanceStrategy) || TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(inheritanceStrategy)){
					String sqlTablename=nameMapping.mapIliClassDef(aclass);
					ViewableWrapper wrapper=new ViewableWrapper(sqlSchemaname,sqlTablename,aclass);
					List<ColumnWrapper> props=new java.util.ArrayList<ColumnWrapper>();
					// defined attrs
					{
						addProps(wrapper,props,aclass.getDefinedAttributesAndRoles2());
					}
					// defined attrs of bases with subclass or newAndSubclass strategy
					{
						Viewable base=(Viewable) aclass.getExtending();
						while(base!=null){
							String baseInheritanceStrategy = trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO);
							if(!TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(baseInheritanceStrategy) 
									&& !TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(baseInheritanceStrategy)){
								break;
							}
							addProps(wrapper,props,base.getDefinedAttributesAndRoles2());
							base=(Viewable) base.getExtending();
						}
					}
					// add attrs of extensions with superclass strategy while visiting extensions
					
					wrapper.setAttrv(props);
					// link to base ViewableWrapper
					{
						Viewable base=(Viewable) aclass.getExtending();
						while(base!=null){
							String baseInheritanceStrategy = trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO);
							if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(baseInheritanceStrategy)){ // but NOT INHERITANCE_TRAFO_NEWANDSUBCLASS! 
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
					// includes more than one type
					if(wrapper.getExtending()!=null){
						wrapper.setMultipleTypes(false); // base contains type typediscriminator
					}else{
						// if a concrete base
						if(hasAnyConcreteBaseWithSubClass(trafoConfig,aclass)){
							wrapper.setMultipleTypes(true);
						}else if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(inheritanceStrategy) && hasAnyConreteExtension(aclass)){
							// newClass and any concrete extensions
							wrapper.setMultipleTypes(true);
						}else if(TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(inheritanceStrategy) && hasAnyConreteExtensionWithoutNewClass(trafoConfig,aclass)){
							// newAndSubClass and any concrete extensions without newClass or newAndSubClass
							wrapper.setMultipleTypes(true);
						}else{
							wrapper.setMultipleTypes(false);
						}
						aclass.getDirectExtensions();
					}
					ret.add(aclass, wrapper);
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_SUPERCLASS.equals(inheritanceStrategy)){
					// add props of extensions with superclass strategy to base-class
					// find base
					Viewable base=(Viewable) aclass.getExtending();
					while(base!=null){
						String baseInheritanceStrategy = trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO);
						if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(baseInheritanceStrategy)
								|| TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(baseInheritanceStrategy)){
							break;
						}
						base=(Viewable) base.getExtending();
					}
					ViewableWrapper wrapper=ret.get(base);
					List<ColumnWrapper> props=wrapper.getAttrv();
					// add props of extension
					addProps(wrapper,props,aclass.getDefinedAttributesAndRoles2());
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

	private void doSmartOff(List<Element> eles) {
		/*
		 * Alle Klassen mit NewClass-Strategie abbilden
		 */
		for(Element ele:eles){
			if(!(ele instanceof Viewable)){
				// not a Viewable; skip it
			}else{
				// a Viewable
				Viewable aclass=(Viewable) ele;
				if(trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)==null){
					// newClass
					trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS);
				}
				String sqlTablename=nameMapping.mapIliClassDef(aclass);
				EhiLogger.traceState("viewable "+aclass.getScopedName(null)+" "+trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)+", "+sqlTablename);
			}
		}
	}
	private TransferDescription getTransferDescription(Element e)
	{
		if(td==null){
			td=(TransferDescription) e.getContainer(TransferDescription.class);
		}
		return td;
	}
	private void doSmart1(List<Element> eles) {
		/*
		 * Fuer Klassen, die referenziert werden und deren Basisklassen nicht mit
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
				}
				String sqlTablename=nameMapping.mapIliClassDef(aclass);
				EhiLogger.traceState("viewable "+aclass.getScopedName(null)+" "+trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)+", "+sqlTablename);
			}
		}
	}
	private void doSmart2(List<Element> eles) {
		/*
		 * Alle abstrakten Klassen werden mit einer
		 * SubClass-Strategie abgebildet. 
		 * Alle konkreten Klassen werden mit einer NewAndSubClass-Strategie 
		 * abgebildet.
		 */
		for(Element ele:eles){
			if(!(ele instanceof Viewable)){
				// not a Viewable; skip it
			}else{
				// a Viewable
				Viewable aclass=(Viewable) ele;
				if(trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)==null){
					if(aclass.isAbstract()){
						// subClass
						trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS);
					}else{
						// newAndSubClass
						trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS);
					}
				}
				String sqlTablename=nameMapping.mapIliClassDef(aclass);
				EhiLogger.traceState("viewable "+aclass.getScopedName(null)+" "+trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)+", "+sqlTablename);
			}
		}
	}

	private void addProps(ViewableWrapper viewable,List<ColumnWrapper> attrv,
		Iterator<ViewableTransferElement> iter) {
        Viewable iliclass=viewable.getViewable();
		boolean hasGeometry=false;
		// only one geometry column per table?
		if(singleGeom){
			for(ColumnWrapper propE:attrv){
			    ViewableTransferElement attrE=propE.getViewableTransferElement();
				if(attrE.obj instanceof AttributeDef){
					AttributeDef attr=(AttributeDef) attrE.obj;
					ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAliases();
					if(type instanceof ch.interlis.ili2c.metamodel.CoordType || type instanceof ch.interlis.ili2c.metamodel.LineType
							|| (Ili2cUtility.isMultiSurfaceAttr(getTransferDescription(attr), attr) && (coalesceMultiSurface 
									|| TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTISURFACE_TRAFO))))
							|| (Ili2cUtility.isMultiLineAttr(getTransferDescription(attr), attr) && (coalesceMultiLine 
									|| TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTILINE_TRAFO))))
							|| (Ili2cUtility.isMultiPointAttr(getTransferDescription(attr), attr) && (coalesceMultiPoint 
									|| TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTIPOINT_TRAFO))))
					){
						hasGeometry=true;
						break;
					}
				}
			}
		}
		while (iter.hasNext()) {
			ViewableTransferElement viewableTransferElement = iter.next();
			if (viewableTransferElement.obj instanceof AttributeDef) {
				AttributeDef attr=(AttributeDef) viewableTransferElement.obj;
				attr=getBaseAttr(iliclass,attr); // get the most general attribute definition, but only up to the class of the current table
				viewableTransferElement.obj=attr;
				for(Integer epsgCode:getEpsgCodes(attr)) {
	                String sqlname=trafoConfig.getAttrConfig(iliclass,attr, epsgCode,TrafoConfigNames.SECONDARY_TABLE);
	                if(sqlname==null) {
	                    // pre ili2db 3.13.x
	                    sqlname=trafoConfig.getAttrConfig(iliclass,attr, TrafoConfigNames.SECONDARY_TABLE);
	                }
	                // attribute configured to be in a secondary table?
	                if(sqlname!=null){
	                    // add attribute to given secondary table
	                    ViewableWrapper attrWrapper=viewable.getSecondaryTable(sqlname);
	                    if(attrWrapper==null){
	                        attrWrapper=viewable.createSecondaryTable(sqlname);
	                    }
	                    List<ColumnWrapper> attrProps=new java.util.ArrayList<ColumnWrapper>();
	                    addColumn(attrProps,new ColumnWrapper(viewableTransferElement,epsgCode));
	                    attrWrapper.setAttrv(attrProps);
	                }else{
                        ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAliases();
                        if(type instanceof ch.interlis.ili2c.metamodel.CoordType || type instanceof ch.interlis.ili2c.metamodel.LineType
                            || (Ili2cUtility.isMultiSurfaceAttr(getTransferDescription(attr), attr) && (coalesceMultiSurface 
                                || TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTISURFACE_TRAFO))))
                            || (Ili2cUtility.isMultiLineAttr(getTransferDescription(attr), attr) && (coalesceMultiLine 
                                    || TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTILINE_TRAFO))))
                            || (Ili2cUtility.isMultiPointAttr(getTransferDescription(attr), attr) && (coalesceMultiPoint 
                                    || TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTIPOINT_TRAFO))))
                        ){                      
                            if(createItfLineTables && type instanceof ch.interlis.ili2c.metamodel.SurfaceType){
                                // no attribute in maintable required
                            }else{
                                // table already has a geometry column?
                                if(singleGeom && hasGeometry){
                                    // create a new secondary table
                                    sqlname=nameMapping.mapAttributeAsTable(iliclass,attr,epsgCode);
                                    ViewableWrapper attrWrapper=viewable.getSecondaryTable(sqlname);
                                    if(attrWrapper==null){
                                        attrWrapper=viewable.createSecondaryTable(sqlname);
                                    }
                                    // add attribute to new secondary table
                                    List<ColumnWrapper> attrProps=new java.util.ArrayList<ColumnWrapper>();
                                    addColumn(attrProps,new ColumnWrapper(viewableTransferElement,epsgCode));
                                    attrWrapper.setAttrv(attrProps);
                                    trafoConfig.setAttrConfig(iliclass,attr, epsgCode,TrafoConfigNames.SECONDARY_TABLE, sqlname);
                                }else{
                                    // table has not yet a geometry column
                                    // add it
                                    hasGeometry=true;
                                    addColumn(attrv,new ColumnWrapper(viewableTransferElement,epsgCode));
                                }
                            }
                        }else{
                            // not a Geom type
                            addColumn(attrv,new ColumnWrapper(viewableTransferElement));
                        }
	                }
				    
				}
			}
			if(viewableTransferElement.obj instanceof RoleDef){
				RoleDef role = (RoleDef) viewableTransferElement.obj;
				AssociationDef roleOwner = (AssociationDef) role.getContainer();
				// not an embedded role and roledef not defined in a lightweight association?
				if (!viewableTransferElement.embedded && !roleOwner.isLightweight()){
					addColumn(attrv,new ColumnWrapper(viewableTransferElement));
				}
				// a role of an embedded association?
				if(viewableTransferElement.embedded){
					if(roleOwner.getDerivedFrom()==null){
						addColumn(attrv,new ColumnWrapper(viewableTransferElement));
					}
				}
			}
		}
		
	}

	private void addColumn(List<ColumnWrapper> attrProps, ColumnWrapper columnWrapper) {
        for(ColumnWrapper exst:attrProps) {
            if(exst.getViewableTransferElement().obj==columnWrapper.getViewableTransferElement().obj && exst.getEpsgCode()==columnWrapper.getEpsgCode()) {
                // already added
                return;
            }
        }
        attrProps.add(columnWrapper);
    }

    private static AttributeDef getBaseAttr(Viewable iliclass, AttributeDef attr) {
        AttributeDef baseAttr=attr;
        while(true){
            AttributeDef baseAttr1=(AttributeDef)baseAttr.getExtending();
            if(baseAttr1==null){
                break;
            }
            if(!((Viewable)baseAttr1.getContainer()).isExtending(iliclass)){
                break;
            }
            baseAttr=baseAttr1;
        }
        return baseAttr;
    }

    private static boolean noBaseIsNewClass(TrafoConfig trafoConfig,Viewable aclass) {
		Viewable base=(Viewable) aclass.getExtending();
		while(base!=null){
			String baseInheritanceStrategy = trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO);
			if(TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(baseInheritanceStrategy)){
				return false;
			}
			base=(Viewable) base.getExtending();
		}
		return true;
	}
	private static boolean hasAnyConcreteBaseWithSubClass(TrafoConfig trafoConfig,Viewable aclass) {
		Viewable base=(Viewable) aclass.getExtending();
		while(base!=null){
			if(!base.isAbstract()){
				String baseInheritanceStrategy = trafoConfig.getViewableConfig(base, TrafoConfigNames.INHERITANCE_TRAFO);
				if(TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(baseInheritanceStrategy)){
					return true;
				}
			}
			base=(Viewable) base.getExtending();
		}
		return false;
	}
	private static boolean hasAnyConreteExtensionWithoutNewClass(TrafoConfig trafoConfig,Viewable aclass) {
		for(Viewable ext: (Set<Viewable>)aclass.getExtensions()){
			if(ext==aclass){
				continue;
			}
			if(!ext.isAbstract()){
				
				String extInheritanceStrategy = trafoConfig.getViewableConfig(ext, TrafoConfigNames.INHERITANCE_TRAFO);
				if(!TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS.equals(extInheritanceStrategy)
						&& !TrafoConfigNames.INHERITANCE_TRAFO_NEWANDSUBCLASS.equals(extInheritanceStrategy)){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean hasAnyConreteExtension(Viewable aclass) {
		for(Viewable ext: (Set<Viewable>)aclass.getExtensions()){
			if(ext==aclass){
				continue;
			}
			if(!ext.isAbstract()){
				return true;
			}
		}
		return false;
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
    private Integer[] getEpsgCodes(AttributeDef attr) {
        TransferDescription td=getTransferDescription(attr);
        if(Ili2cUtility.isMultiSurfaceAttr(td,attr)) {
            MultiSurfaceMappings multiSurfaceAttrs=new MultiSurfaceMappings();
            multiSurfaceAttrs.addMultiSurfaceAttr(attr);
            AttributeDef surfaceAttr = multiSurfaceAttrs.getSurfaceAttr(attr);
            attr=surfaceAttr;
        }else if(Ili2cUtility.isMultiLineAttr(td, attr)) {
            MultiLineMappings multiLineAttrs=new MultiLineMappings();
            multiLineAttrs.addMultiLineAttr(attr);
            AttributeDef polylineAttr=multiLineAttrs.getPolylineAttr(attr);
            attr=polylineAttr;
        }else if(Ili2cUtility.isMultiPointAttr(td, attr)) {
            MultiPointMappings multiPointAttrs=new MultiPointMappings();
            multiPointAttrs.addMultiPointAttr(attr);
            AttributeDef multipointAttr=multiPointAttrs.getCoordAttr(attr);
            attr=multipointAttr;
        }
        int epsgCodes[]= TransferFromIli.getEpsgCodes(attr,srsModelAssignment,Integer.parseInt(defaultCrsCode));
        if(epsgCodes==null) {
            // not a geometry attribute
            return new Integer[]{null};
        }
        Integer ret[]=new Integer[epsgCodes.length];
        for(int i=0;i<epsgCodes.length;i++) {
            ret[i]=epsgCodes[i];
        }
        return ret;
    }

}
