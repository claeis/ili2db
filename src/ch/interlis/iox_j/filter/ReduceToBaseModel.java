package ch.interlis.iox_j.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxValidationDataPool;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.validator.Validator;

public class ReduceToBaseModel implements IoxFilter {

	private IoxLogging loggingHandler=null;
	private TransferDescription td=null;
    private boolean doItfLineTables=false;
	private List<Model> destModels=null;
	/** mappings from xml-tags to Viewable|AttributeDef
	 */
	private HashMap<String,Object> tag2class=null;
	private HashMap<String,Topic> tag2topic=new HashMap<String,Topic>();
	private HashMap<String,Object> srctag2destElement=new HashMap<String,Object>();
    private HashMap<Topic,ArrayList<Topic>> topicTranslatedBy=new HashMap<Topic,ArrayList<Topic>>();
	
	public ReduceToBaseModel(List<Model> exportModels,TransferDescription td,Settings config)
	{
		this.td=td;
		this.destModels=exportModels;
        this.doItfLineTables = Validator.CONFIG_DO_ITF_LINETABLES_DO.equals(config.getValue(Validator.CONFIG_DO_ITF_LINETABLES));
        if(doItfLineTables){
            tag2class=ch.interlis.iom_j.itf.ModelUtilities.getTagMap(td);
        }else{
            tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
        }
		for(Iterator<Model> models=td.iterator();models.hasNext();){
			Model model=models.next();
			for(Iterator<Element> topics=model.iterator();topics.hasNext();){
				Element topic=topics.next();
				if(topic instanceof Topic){
					tag2topic.put(topic.getScopedName(), (Topic)topic);
					ArrayList<Topic> translatedBy=topicTranslatedBy.get(topic);
					if(translatedBy==null) {
					    translatedBy=new ArrayList<Topic>();
					    topicTranslatedBy.put((Topic) topic,translatedBy);
					}
					Topic originTopic=(Topic) topic.getTranslationOf();
					if(originTopic!=null) {
					    translatedBy=topicTranslatedBy.get(originTopic);
					    translatedBy.add((Topic) topic);
					}
				}
			}
			
		}
	}
	private void setupTranslation(Topic srcTopic){
		Topic destTopic=srcTopic;
		do{
			if(destModels.contains(destTopic.getContainer(Model.class))) {
				// setup mapping of topic content
				setupTopicTranslation(srcTopic,destTopic);
				return;
			}
			// find dest topic
			// translated topics, origin topics
			Set<Topic> destTopics=getTranslationsOf(destTopic);
			for(Topic d:destTopics) {
	            if(destModels.contains(d.getContainer(Model.class))) {
	                // setup mapping of topic content
	                setupTopicTranslation(srcTopic,d);
	                return;
	            }
			}
            // base topic
			destTopic=(Topic) destTopic.getExtending();
		}while(destTopic!=null);
		return;
	}
	
	private Set<Topic> getTranslationsOf(Topic start) {
	    Set<Topic> ret=new HashSet<Topic>();
	    ArrayList<Topic> queue=new ArrayList<Topic>();
	    Topic topic=(Topic) start.getTranslationOfOrSame();
	    queue.add(topic);
        ret.add(topic);
	    while(queue.size()>0){
            topic=queue.remove(0);
	        ArrayList<Topic> translatedBy=topicTranslatedBy.get(topic);
	        ret.addAll(translatedBy);
	        queue.addAll(translatedBy);
	    }
	    ret.remove(start);
        return ret;
    }
    private void setupTopicTranslation(Topic srcTopic, Topic destTopic) {
		srctag2destElement.put(srcTopic.getScopedName(), destTopic);
		if(srcTopic==destTopic) {
			// no translation required
			return;
		}
		Topic originDestTopic=(Topic) destTopic.getTranslationOfOrSame();
		// for each class of srcTopic, find dest in destTopic
		while(srcTopic!=null) {
			if((srcTopic.isExtending(destTopic) || srcTopic.getTranslationOfOrSame()==originDestTopic) && srcTopic!=destTopic) {
				Iterator<Element> eleIt=srcTopic.iterator();
				while(eleIt.hasNext()) {
					Element srcEle=eleIt.next();
					if(srcEle instanceof AbstractClassDef) {
                        AbstractClassDef originSrcEle=(AbstractClassDef) srcEle.getTranslationOfOrSame();
					    // find equivalent class in dest topic
						AbstractClassDef originDestEle=originSrcEle;
						while(originDestEle!=null){
							if(originDestEle.getContainer()==originDestTopic) {
								break;
							}
							originDestEle=(AbstractClassDef)originDestEle.getExtending();
						}
                        // found destEle in original language?
                        AbstractClassDef destEle=null;
						if(originDestEle!=null && !originDestEle.isAbstract()) {
						    destEle=findTranslatedEle(originDestEle,destTopic);
						    if(destEle==null) {
						        throw new IllegalStateException("destEle "+originDestEle.getScopedName()+" in "+destTopic.getScopedName()+"not found");
						    }
							srctag2destElement.put(srcEle.getScopedName(), destEle);
							if(srcEle!=destEle) {
								setupClassTranslation((AbstractClassDef) srcEle,destEle);
							}else {
								// no object translation required
								// setupClassTranslation((AbstractClassDef) srcEle,baseEle);
							}
						}else {
							// delete object from export
							srctag2destElement.put(srcEle.getScopedName(), null);
						}
					}
				}
			}else {
				// keep elements, as they are (no translation required)
				Iterator<Element> eleIt=srcTopic.iterator();
				while(eleIt.hasNext()) {
					Element srcEle=eleIt.next();
					if(srcEle instanceof AbstractClassDef) {
						AbstractClassDef destEle=(AbstractClassDef) srcEle;
						srctag2destElement.put(srcEle.getScopedName(), destEle);
						// no object translation required
						// setupClassTranslation((AbstractClassDef) srcEle,baseEle);
					}
				}
			}
			srcTopic=(Topic)srcTopic.getExtending();
		}
	}
	private AbstractClassDef findTranslatedEle(AbstractClassDef originDestEle, Topic destTopic) {
        Iterator<Element> eleIt=destTopic.iterator();
        while(eleIt.hasNext()) {
            Element destEle=eleIt.next();
            if(destEle instanceof AbstractClassDef) {
                AbstractClassDef originEle=(AbstractClassDef) destEle.getTranslationOfOrSame();
                if(originEle==originDestEle) {
                    return (AbstractClassDef)destEle;
                }
            }
        }
        return null;
    }
    private void setupClassTranslation(AbstractClassDef srcEle, AbstractClassDef destEle) {
		HashMap<String,ViewableTransferElement> destAttrs=new HashMap<String,ViewableTransferElement>();
		Iterator<ViewableTransferElement> destPropIt=destEle.getAttributesAndRoles2();
		while(destPropIt.hasNext()){
			ViewableTransferElement destProp=destPropIt.next();
			String destOriginPropName = ((Element)destProp.obj).getTranslationOfOrSame().getName();
			destAttrs.put(destOriginPropName,destProp);
		}
		Iterator<ViewableTransferElement> srcPropIt=srcEle.getAttributesAndRoles2();
		while(srcPropIt.hasNext()) {
			ViewableTransferElement srcProp=srcPropIt.next();
			String srcOriginPropName = ((Element)srcProp.obj).getTranslationOfOrSame().getName();
			if(destAttrs.containsKey(srcOriginPropName)) {
				// attribute exists in base class
				srctag2destElement.put(((Element)srcProp.obj).getScopedName(), destAttrs.get(srcOriginPropName).obj);
			}else {
				// attribute doesn't exist in base class
				// remove it from export
				srctag2destElement.put(((Element)srcProp.obj).getScopedName(), null);
			}
		}
	}
	@Override
	public IoxEvent filter(IoxEvent event) throws IoxException {
		 if(event instanceof StartTransferEvent){
		}else if(event instanceof StartBasketEvent){
			String srcTopicName=((StartBasketEvent) event).getType();
			srcTopic = tag2topic.get(srcTopicName);
			srctag2destElement=new HashMap<String,Object>();			
			setupTranslation(srcTopic);
			destTopic = (Topic)getTranslatedElement(srcTopic);
			if(destTopic==srcTopic) {
				// no translation required
			}else if(destTopic==null) {
				// TOPIC doesn't exist in base model (it only exists in the extension model)
				// export as it is; no translation required
			}else {
				((ch.interlis.iox_j.StartBasketEvent) event).setType(destTopic.getScopedName());
			}
		}else if(event instanceof ObjectEvent){
			if(destTopic==srcTopic) {
				// no translation required
			}else if(destTopic==null) {
				// TOPIC doesn't exist in base model (it only exists in the extenison model)
				// export object as it is; no translation required
			}else {
				IomObject iomObj = ((ObjectEvent) event).getIomObject();
				if(srctag2destElement.get(iomObj.getobjecttag())==null) {
					return null;
				}
				translateObject(iomObj);
			}
		}else if(event instanceof EndBasketEvent){
		}else if(event instanceof EndTransferEvent){
		}
		return event;
	}

	private void translateObject(IomObject iomObj) {
		Element modelElement = (Element)tag2class.get(iomObj.getobjecttag());
		Element destModelEle=getTranslatedElement(modelElement);
		if(destModelEle==modelElement){
			// no translation required
			return;
		}
		if(isForeignElement(modelElement)) {
		    // STRUCTURE of a other model (such as a Catalog.Item)
            // no translation required
            return;
		}
		if(doItfLineTables && modelElement instanceof AttributeDef){
			// TODO see TranslateToOrigin
		}

		Viewable aclass=(Viewable)modelElement;
		Viewable destClass=(Viewable)destModelEle;
		String destName=destClass.getScopedName();
		iomObj.setobjecttag(destName);
		
		// handle attrs
		Iterator iter = aclass.getAttributesAndRoles2();
		while (iter.hasNext()) {
			ViewableTransferElement srcProp = (ViewableTransferElement)iter.next();
			Element destEle=(Element) srctag2destElement.get(((Element)srcProp.obj).getScopedName());
			if(destEle==null) {
				iomObj.setattrundefined(((Element)srcProp.obj).getName());
			}else {
				if (srcProp.obj instanceof AttributeDef) {
					AttributeDef attr = (AttributeDef) srcProp.obj;
					if(!attr.isTransient()){
						Type proxyType=attr.getDomain();
						if(proxyType!=null && (proxyType instanceof ObjectType)){
							// skip implicit particles (base-viewables) of views
						}else{
							translateAttrValue(iomObj,attr);
						}
					}
				}else if (srcProp.obj instanceof RoleDef) {
                    RoleDef srcRole = (RoleDef) srcProp.obj;
                    RoleDef destRole=(RoleDef)getTranslatedElement(srcRole);
                    String srcRoleName = srcRole.getName();
                    if(iomObj.getattrvaluecount(srcRoleName)>0){
                        IomObject structvalue = iomObj.getattrobj(srcRoleName, 0);
                        iomObj.deleteattrobj(srcRoleName, 0);
                        iomObj.addattrobj(destRole.getName(), structvalue);
                    }
				}
			}
		}
	}

	
	private Element getTranslatedElement(Element modelElement) {
		Element destEle=(Element)srctag2destElement.get(modelElement.getScopedName());
		return destEle;
	}
    private boolean isForeignElement(Element modelElement) {
        // a mapping from a scopedName to null means: remove element
        // therefore test if map contains scopedName as key to check if modelElement is from the current topic
        return !srctag2destElement.containsKey(modelElement.getScopedName());
    }

	private void translateAttrValue(IomObject iomObj, AttributeDef srcAttr) {
		String srcAttrName=srcAttr.getName();
		int attrc=iomObj.getattrvaluecount(srcAttrName);
		if(attrc==0){
			return;
		}
		boolean isCompType=srcAttr.getDomain() instanceof CompositionType ? true :false;
		boolean isEnumType=srcAttr.getDomainResolvingAliases() instanceof EnumerationType ? true : false;
		EnumerationType srcEnumType=null;
		if(isEnumType){
			srcEnumType=(EnumerationType)srcAttr.getDomainResolvingAliases();
		}
		AttributeDef destAttr=(AttributeDef)srctag2destElement.get(srcAttr.getScopedName());
        String destAttrName=destAttr.getName();
		
		ArrayList<Object> attrValues=new ArrayList<Object>();
		for(int attri=0;attri<attrc;attri++){
			String attrValue=iomObj.getattrprim(srcAttrName,attri);
			if(attrValue!=null){
				attrValues.add(attrValue);
			}else{
				IomObject structValue=iomObj.getattrobj(srcAttrName,attri);
				attrValues.add(structValue);
			}
		}
		iomObj.setattrundefined(srcAttrName);
		for(int attri=0;attri<attrc;attri++){
			Object attrValue=attrValues.get(attri);
			if(attrValue!=null){
				if(attrValue instanceof String){
					if(isEnumType){
						attrValue=translateEnumValue((String)attrValue,srcEnumType,(EnumerationType)destAttr.getDomainResolvingAliases());
					}
					iomObj.setattrvalue(destAttrName, (String)attrValue);
				}else{
					IomObject structValue=(IomObject)attrValue;
					if(isCompType){
						translateObject(structValue);
					}
					iomObj.addattrobj(destAttrName,structValue);
				}
			}
		}
	}

	private String translateEnumValue(String attrValue, EnumerationType srcEnumType,EnumerationType destEnumType) {
		Map<String,String> src2dest=getEnumMapping(srcEnumType,destEnumType);
		String destValue=src2dest.get(attrValue);
		return destValue;
	}


	Map<EnumerationType,Map<String,String>> src2destEles=new HashMap<EnumerationType,Map<String,String>>();
	private Topic srcTopic;
	private Topic destTopic;
	private Map<String, String> getEnumMapping(
			EnumerationType srcEnumType,EnumerationType destEnumType) {
        Map<String, String> src2dest = src2destEles.get(srcEnumType);
        if (src2dest == null) {
            src2dest = new HashMap<String, String>();
            buildEnumList(src2dest, "", srcEnumType.getConsolidatedEnumeration(), "",
                    destEnumType.getConsolidatedEnumeration(), null);
            src2destEles.put(srcEnumType, src2dest);
        }
        return src2dest;
	}
	  public static void buildEnumList(java.util.Map<String,String> accu,String srcPrefix1,Enumeration srcEnumer,String destPrefix1,Enumeration destEnumer,String destEeName){
        String srcPrefix = "";
        String destPrefix = "";
        if (srcPrefix1.length() > 0) {
            srcPrefix = srcPrefix1 + ".";
        }
        if (destPrefix1!=null && destPrefix1.length() > 0) {
            destPrefix = destPrefix1 + ".";
        }
        Iterator srcIter = srcEnumer.getElements();
        Iterator destIter = null;
        if (destEnumer != null) {
            destIter = destEnumer.getElements();
        }
        while (srcIter.hasNext()) {
            Enumeration.Element srcEe = (Enumeration.Element) srcIter.next();
            Enumeration srcSubEnum = srcEe.getSubEnumeration();
            Enumeration destSubEnum = null;
            Enumeration.Element destEe = null;
            if (destIter != null) {
                destEe = (Enumeration.Element) destIter.next();
                destSubEnum = destEe.getSubEnumeration();
                if (destSubEnum == null) {
                    destEeName = destPrefix + destEe.getName();
                }
            }
            if (srcSubEnum != null) {
                if(destSubEnum!=null) {
                    // ee is not leaf, add its name to prefix and add sub elements to accu
                    buildEnumList(accu, srcPrefix + srcEe.getName(), srcSubEnum, destPrefix+destEe.getName(), destSubEnum, null);
                }else {
                    // ee is not leaf, add its name to prefix and add sub elements to accu
                    buildEnumList(accu, srcPrefix + srcEe.getName(), srcSubEnum, null, null, destEeName);
                }
            } else {
                // ee is a leaf, add it to accu
                accu.put(srcPrefix + srcEe.getName(), destEeName);
            }
        }
	  }
	

	@Override
	public void close() {
		loggingHandler=null;
	}

	@Override
	public IoxLogging getLoggingHandler() {
		return loggingHandler;
	}

	@Override
	public void setLoggingHandler(IoxLogging errs) {
		loggingHandler=errs;
	}

	@Override
	public IoxValidationDataPool getDataPool() {
		return null;
	}
}
