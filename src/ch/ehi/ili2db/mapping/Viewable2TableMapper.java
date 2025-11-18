package ch.ehi.ili2db.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.fromili.TransferFromIli;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Cardinality;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
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
    private boolean coalesceJson=false;
    private boolean expandStruct=false;
	private boolean createItfLineTables=false;
    private Integer defaultCrsCode=null;
    private String srsModelAssignment=null;
	private TransferDescription td=null;
	private Viewable2TableMapper(Config config1,
			TrafoConfig trafoConfig1, NameMapping nameMapping1) {	
		config=config1;
		trafoConfig=trafoConfig1;
		nameMapping=nameMapping1;
		sqlSchemaname=config.getDbschema();
        if(config.getDefaultSrsCode()!=null) {
            defaultCrsCode=Integer.parseInt(config.getDefaultSrsCode());
        }
        srsModelAssignment=config.getSrsModelAssignment();
	}

	public static Viewable2TableMapping getClass2TableMapping(boolean isIli1, Config config,
			TrafoConfig trafoConfig, List<Element> eles,NameMapping nameMapping) throws Ili2dbException {
		Viewable2TableMapper mapper=new Viewable2TableMapper(config, trafoConfig, nameMapping);
		mapper.singleGeom=config.isOneGeomPerTable();
		mapper.coalesceMultiSurface=Config.MULTISURFACE_TRAFO_COALESCE.equals(config.getMultiSurfaceTrafo());
		mapper.coalesceMultiLine=Config.MULTILINE_TRAFO_COALESCE.equals(config.getMultiLineTrafo());
		mapper.coalesceMultiPoint=Config.MULTIPOINT_TRAFO_COALESCE.equals(config.getMultiPointTrafo());
		mapper.coalesceArray=Config.ARRAY_TRAFO_COALESCE.equals(config.getArrayTrafo());
        mapper.coalesceJson=Config.JSON_TRAFO_COALESCE.equals(config.getJsonTrafo());
        mapper.expandStruct=Config.STRUCT_TRAFO_EXPAND.equals(config.getStructTrafo());
		mapper.createItfLineTables=isIli1 && config.getDoItfLineTables();
		return mapper.doit(eles);
	}
	private Viewable2TableMapping doit(List<Element> eles) throws Ili2dbException {
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
					
                    // add columns
					{
	                    List<ColumnWrapper> props=new java.util.ArrayList<ColumnWrapper>();
					    
	                    // defined attrs
	                    {
	                        if(aclass instanceof AssociationDef) {
	                            addProps(wrapper,props,wrapper.getViewable(),getRoles((AssociationDef)aclass),null);
	                        }
	                        addProps(wrapper,props,wrapper.getViewable(),aclass.getDefinedAttributesAndRoles2(),null);
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
	                            if(base instanceof AssociationDef) {
	                                addProps(wrapper,props,wrapper.getViewable(),getRoles((AssociationDef)base),null);
	                            }
	                            addProps(wrapper,props,wrapper.getViewable(),base.getDefinedAttributesAndRoles2(),null);
	                            base=(Viewable) base.getExtending();
	                        }
	                    }
	                    
	                    // will add attrs of extensions with superclass strategy while visiting extensions
	                    
	                    wrapper.setAttrv(props);
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
					// add props of this extension
					addProps(wrapper,props,wrapper.getViewable(),aclass.getDefinedAttributesAndRoles2(),null);
					wrapper.setAttrv(props);
					ret.add(aclass, wrapper);
				}else if(TrafoConfigNames.INHERITANCE_TRAFO_SUBCLASS.equals(inheritanceStrategy)){
					// skip it; props already added when visiting subclass
                }else if(TrafoConfigNames.INHERITANCE_TRAFO_EMBEDDED.equals(inheritanceStrategy)){
                    // skip it; props already added when visiting container class
				}else{
					throw new IllegalStateException("unexpected inheritance config <"+inheritanceStrategy+">");
				}
			}
		}
		return ret;
	}

    private Iterator<ViewableTransferElement> getRoles(AssociationDef aclass) {
        ArrayList<ViewableTransferElement> roles=new ArrayList<ViewableTransferElement>();
        for(Iterator<ViewableTransferElement> roleIt=((AssociationDef)aclass).getAttributesAndRoles2();roleIt.hasNext();) {
            ViewableTransferElement prop=roleIt.next();
            if(prop.obj instanceof RoleDef && prop.embedded==false){
                roles.add(new ViewableTransferElement(prop.obj,false));
            }
        }
        return roles.iterator();
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
				    if(aclass instanceof AssociationDef && TransferFromIli.isLightweightAssociation((AssociationDef)aclass)) {
                        trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_EMBEDDED);
				    }else {
	                    // newClass
	                    trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_NEWCLASS);
				    }
				}
				String sqlTablename=nameMapping.mapIliClassDef(aclass);
				EhiLogger.traceState("viewable "+aclass.getScopedName(null)+" "+trafoConfig.getViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO)+", "+sqlTablename);
			}
		}
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
                    if(aclass instanceof AssociationDef && TransferFromIli.isLightweightAssociation((AssociationDef)aclass)) {
                        trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_EMBEDDED);
                    }else if(isReferenced(aclass) && noBaseIsNewClass(trafoConfig,aclass)){
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
                    if(aclass instanceof AssociationDef && TransferFromIli.isLightweightAssociation((AssociationDef)aclass)) {
                        trafoConfig.setViewableConfig(aclass, TrafoConfigNames.INHERITANCE_TRAFO, TrafoConfigNames.INHERITANCE_TRAFO_EMBEDDED);
                    }else if(aclass.isAbstract()){
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

    private TransferDescription getTransferDescription(Element e)
    {
        if(td==null){
            td=(TransferDescription) e.getContainer(TransferDescription.class);
        }
        return td;
    }
	private void addProps(ViewableWrapper table,List<ColumnWrapper> existingColumns,
	        Viewable iliclassOfAttrs,
	        Iterator<ViewableTransferElement> additionalAttrs, StructAttrPath.PathEl structAttrPrefix[]) throws Ili2dbException {
		boolean hasGeometry=false;
		// only one geometry column per table?
		if(singleGeom){
			for(ColumnWrapper propE:existingColumns){
				if(propE.isIliAttr()){
					AttributeDef attr=(AttributeDef) propE.getViewableTransferElement().obj;
					ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAliases();
					if(type instanceof ch.interlis.ili2c.metamodel.AbstractCoordType || type instanceof ch.interlis.ili2c.metamodel.LineType
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
        if(structAttrPrefix==null) {
            structAttrPrefix=new StructAttrPath.PathEl[1]; 
        }else {
            structAttrPrefix=java.util.Arrays.copyOf(structAttrPrefix,structAttrPrefix.length+1);
        }
		while (additionalAttrs.hasNext()) {
			ViewableTransferElement viewableTransferElement = additionalAttrs.next();
			if (viewableTransferElement.obj instanceof AttributeDef) {
				AttributeDef attr=(AttributeDef) viewableTransferElement.obj;
                if(!attr.isTransient()){
                    Type proxyType=attr.getDomain();
                    if(proxyType!=null && (proxyType instanceof ObjectType)){
                        // skip implicit particles (base-viewables) of views
                    }else{
                        attr=getBaseAttr(iliclassOfAttrs,attr); // get the most general attribute definition, but only up to the class of the current table
                        viewableTransferElement.obj=attr;
                        for(Integer epsgCode:getEpsgCodes(attr)) {
                            String sqlSecondaryTableName=trafoConfig.getAttrConfig(iliclassOfAttrs,attr, epsgCode,TrafoConfigNames.SECONDARY_TABLE);
                            if(sqlSecondaryTableName==null) {
                                // pre ili2db 3.13.x
                                sqlSecondaryTableName=trafoConfig.getAttrConfig(iliclassOfAttrs,attr, TrafoConfigNames.SECONDARY_TABLE);
                            }
                            // attribute configured to be in a secondary table?
                            if(sqlSecondaryTableName!=null){
                                // add attribute to given secondary table
                                ViewableWrapper attrWrapperSecondaryTable=table.getSecondaryTable(sqlSecondaryTableName);
                                if(attrWrapperSecondaryTable==null){
                                    attrWrapperSecondaryTable=table.createSecondaryTable(sqlSecondaryTableName);
                                }
                                List<ColumnWrapper> attrPropsSecondaryTable=new java.util.ArrayList<ColumnWrapper>();
                                final ColumnWrapper newProp = new ColumnWrapper(new StructAttrPath(viewableTransferElement),epsgCode);
                                addColumn(table,attrPropsSecondaryTable,newProp);
                                attrWrapperSecondaryTable.setAttrv(attrPropsSecondaryTable);
                            }else{
                                Cardinality cardinality = attr.getDomainOrDerivedDomain().getCardinality();
                                ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAll();
                                if(type instanceof ch.interlis.ili2c.metamodel.AbstractCoordType || type instanceof ch.interlis.ili2c.metamodel.LineType
                                    || (Ili2cUtility.isMultiSurfaceAttr(getTransferDescription(attr), attr) && (coalesceMultiSurface 
                                        || TrafoConfigNames.MULTISURFACE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTISURFACE_TRAFO))))
                                    || (Ili2cUtility.isMultiLineAttr(getTransferDescription(attr), attr) && (coalesceMultiLine 
                                            || TrafoConfigNames.MULTILINE_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTILINE_TRAFO))))
                                    || (Ili2cUtility.isMultiPointAttr(getTransferDescription(attr), attr) && (coalesceMultiPoint 
                                            || TrafoConfigNames.MULTIPOINT_TRAFO_COALESCE.equals(trafoConfig.getAttrConfig(attr,TrafoConfigNames.MULTIPOINT_TRAFO))))
                                ){
                                    if(epsgCode==null) {
                                        throw new Ili2dbException("no CRS for attribute "+attr.getScopedName());
                                    }
                                    if(createItfLineTables && type instanceof ch.interlis.ili2c.metamodel.SurfaceType){
                                        // no attribute in maintable required
                                    }else{
                                        // table already has a geometry column?
                                        if(singleGeom && hasGeometry){
                                            // attribute not yet mapped?
                                            final ColumnWrapper newProp = new ColumnWrapper(new StructAttrPath(viewableTransferElement),epsgCode);
                                            if(!columnAlreadyAdded(table,existingColumns,newProp)){
                                                // create a new secondary table
                                                sqlSecondaryTableName=nameMapping.mapAttributeAsTable(iliclassOfAttrs,attr,epsgCode);
                                                ViewableWrapper attrWrapperSecondaryTable=table.getSecondaryTable(sqlSecondaryTableName);
                                                if(attrWrapperSecondaryTable==null){
                                                    attrWrapperSecondaryTable=table.createSecondaryTable(sqlSecondaryTableName);
                                                }
                                                // add attribute to new secondary table
                                                List<ColumnWrapper> attrPropsSecondaryTable=new java.util.ArrayList<ColumnWrapper>();
                                                addColumn(table,attrPropsSecondaryTable,newProp);
                                                attrWrapperSecondaryTable.setAttrv(attrPropsSecondaryTable);
                                                trafoConfig.setAttrConfig(iliclassOfAttrs,attr, epsgCode,TrafoConfigNames.SECONDARY_TABLE, sqlSecondaryTableName);
                                            }
                                        }else{
                                            // table has not yet a geometry column
                                            // add it
                                            hasGeometry=true;
                                            structAttrPrefix[structAttrPrefix.length-1]=new StructAttrPath.PathElAttr(viewableTransferElement); 
                                            StructAttrPath structAttrPath=new StructAttrPath(structAttrPrefix);
                                            addColumn(table,existingColumns,new ColumnWrapper(structAttrPath,epsgCode));
                                        }
                                    }
                                } else if (cardinality.getMaximum() > 1 && 
                                        !(type instanceof CompositionType) && 
                                        !(Ili2cUtility.isJsonMapping(attr) && coalesceJson) && 
                                        !(Ili2cUtility.isArrayAttr(td,attr) && coalesceArray) &&
                                        !(Ili2cUtility.isExpandMapping(attr) && expandStruct)
                                        ) {
                                    // create a new secondary table for attribute with cardinality greater than one
                                    sqlSecondaryTableName=nameMapping.mapAttributeAsTable(table.getViewable(), attr, epsgCode);
                                    ViewableWrapper secondaryTable = table.createSecondaryTable(sqlSecondaryTableName);
                                    // add attribute to new secondary table
                                    addColumn(table, secondaryTable.getAttrv(), new ColumnWrapper(new StructAttrPath(viewableTransferElement)));
                                    trafoConfig.setAttrConfig(table.getViewable(),attr, epsgCode,TrafoConfigNames.SECONDARY_TABLE, sqlSecondaryTableName);
                                } else if (cardinality.getMaximum() <= ArrayMappings.MAX_ARRAY_EXPAND &&
                                        (Ili2cUtility.isExpandMapping(attr) && expandStruct)
                                        ) {
                                    if(type instanceof CompositionType) {
                                        trafoConfig.setAttrConfig(attr, TrafoConfigNames.STRUCT_TRAFO, TrafoConfigNames.STRUCT_TRAFO_EXPAND);
                                        long maxIdx=cardinality.getMaximum();
                                        for(int idx=0;idx<maxIdx;idx++) {
                                            structAttrPrefix[structAttrPrefix.length-1]=new StructAttrPath.PathElAttr(viewableTransferElement,idx); 
                                            Viewable struct=((CompositionType)type).getComponentType();
                                            {
                                                StructAttrPath.PathEl structAttrPrefixProps[]=java.util.Arrays.copyOf(structAttrPrefix,structAttrPrefix.length+1);
                                                structAttrPrefixProps[structAttrPrefixProps.length-1]=new StructAttrPath.PathElType(); 
                                                StructAttrPath structAttrPath=new StructAttrPath(structAttrPrefixProps);
                                                addColumn(table,existingColumns,new ColumnWrapper(structAttrPath));
                                            }
                                            addProps(table,existingColumns,
                                                    struct,
                                                    struct.getDefinedAttributesAndRoles2(),structAttrPrefix);
                                        }
                                    }else {
                                        trafoConfig.setAttrConfig(attr, TrafoConfigNames.ARRAY_TRAFO, TrafoConfigNames.ARRAY_TRAFO_EXPAND);
                                        long maxIdx=cardinality.getMaximum();
                                        for(int idx=0;idx<maxIdx;idx++) {
                                            structAttrPrefix[structAttrPrefix.length-1]=new StructAttrPath.PathElAttr(viewableTransferElement,idx); 
                                            StructAttrPath structAttrPath=new StructAttrPath(structAttrPrefix);
                                            addColumn(table,existingColumns,new ColumnWrapper(structAttrPath));
                                        }
                                    }
                                }else{
                                    // normal 1:1 column/attribute
                                    structAttrPrefix[structAttrPrefix.length-1]=new StructAttrPath.PathElAttr(viewableTransferElement); 
                                    StructAttrPath structAttrPath=new StructAttrPath(structAttrPrefix);
                                    addColumn(table,existingColumns,new ColumnWrapper(structAttrPath));
                                }
                            }
                            
                        }
                    }
                }
				
			}else if(viewableTransferElement.obj instanceof RoleDef){
				RoleDef role = (RoleDef) viewableTransferElement.obj;
				AssociationDef roleOwner = (AssociationDef) role.getContainer();
                // a role of an embedded association?
				if (viewableTransferElement.embedded) {
				    if(!TransferFromIli.isLightweightAssociation(roleOwner)){
				        ; // skip it; is added when visiting AssociationDef
				    }else {
	                    if(roleOwner.getDerivedFrom()==null){
	                        addColumn(table,existingColumns,new ColumnWrapper(new StructAttrPath(viewableTransferElement)));
	                    }
				    }
				}else {
                    addColumn(table,existingColumns,new ColumnWrapper(new StructAttrPath(viewableTransferElement)));
				}
			}
		}
		
	}

	private void addColumn(ViewableWrapper current,List<ColumnWrapper> existingProps, ColumnWrapper additionalProp) {
	    if(columnAlreadyAdded(current,existingProps,additionalProp)) {
	        return;
	    }
        existingProps.add(additionalProp);
    }
    private boolean columnAlreadyAdded(ViewableWrapper current,List<ColumnWrapper> existingProps, ColumnWrapper additionalProp) {
        for(ColumnWrapper exst:existingProps) {
            if(columnWrapperEquals(additionalProp, exst)) {
                // already added
                return true;
            }
        }
        // check also secondaries
        for(ViewableWrapper secondary:current.getSecondaryTables()){
            for(ColumnWrapper exst:secondary.getAttrv()) {
                if(columnWrapperEquals(additionalProp, exst)) {
                    // already added
                    return true;
                }
            }
        }
        ViewableWrapper base=current.getExtending();
        while(base!=null) {
            for(ColumnWrapper exst:base.getAttrv()) {
                if(columnWrapperEquals(additionalProp, exst)) {
                    // already added
                    return true;
                }
            }
            // check also secondaries
            for(ViewableWrapper secondary:base.getSecondaryTables()){
                for(ColumnWrapper exst:secondary.getAttrv()) {
                    if(columnWrapperEquals(additionalProp, exst)) {
                        // already added
                        return true;
                    }
                }
            }
            base=base.getExtending();
        }
        return false;
    }

    private boolean columnWrapperEquals(ColumnWrapper col1, ColumnWrapper col2) {
        return structAttrPathEquals(col2.getStructAttrPath(),col1.getStructAttrPath()) && epsgCodeEqual(col2.getEpsgCode(),col1.getEpsgCode());
    }

    private boolean structAttrPathEquals(StructAttrPath path1, StructAttrPath path2) {
        if(path1==null && path2==null) {
            return true;
        }
        if(path1!=null && path2!=null) {
            if(path1.getPath().length!=path2.getPath().length) {
                return false;
            }
            for(int i=0;i<path1.getPath().length;i++) {
                StructAttrPath.PathEl el1=path1.getPath()[i];
                StructAttrPath.PathEl el2=path2.getPath()[i];
                if(el1.getIdx()!=el2.getIdx()) {
                    return false;
                }
                if(!el1.getClass().equals(el2.getClass())) {
                    return false;
                }
                if(el1 instanceof StructAttrPath.PathElAttr) {
                    if(getRootProp(((StructAttrPath.PathElAttr) el1).getAttr().obj)!=getRootProp(((StructAttrPath.PathElAttr) el2).getAttr().obj)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean epsgCodeEqual(Integer epsgCode1, Integer epsgCode2) {
        if(epsgCode1==null && epsgCode2==null) {
            return true;
        }
        if(epsgCode1!=null && epsgCode2!=null && epsgCode1.equals(epsgCode2)) {
            return true;
        }
        return false;
    }

    private Object getRootProp(Object obj) {
        if(obj instanceof AttributeDef) {
            return Ili2cUtility.getRootBaseAttr((AttributeDef)obj);
        }else if(obj instanceof RoleDef) {
            return Ili2cUtility.getRootBaseRole((RoleDef)obj);
        }
        throw new IllegalArgumentException("unexpected "+obj);
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
        int epsgCodes[]= TransferFromIli.getEpsgCodes(attr,srsModelAssignment,defaultCrsCode);
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
