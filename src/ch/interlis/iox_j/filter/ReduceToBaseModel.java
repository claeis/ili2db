package ch.interlis.iox_j.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.SurfaceType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ModelUtilities;
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
	private List<Model> baseModels=null;
	/** mappings from xml-tags to Viewable|AttributeDef
	 */
	private HashMap<String,Object> tag2class=null;
	private HashMap<String,Topic> tag2topic=new HashMap<String,Topic>();
	private HashMap<String,Object> srctag2destElement=new HashMap<String,Object>();
	
	public ReduceToBaseModel(List<Model> exportModels,TransferDescription td,Settings config)
	{
		this.td=td;
		this.baseModels=exportModels;
		tag2class=ch.interlis.ili2c.generator.XSDGenerator.getTagMap(td);
		for(Iterator<Model> models=td.iterator();models.hasNext();){
			Model model=models.next();
			for(Iterator<Element> topics=model.iterator();topics.hasNext();){
				Element topic=topics.next();
				if(topic instanceof Topic){
					tag2topic.put(topic.getScopedName(), (Topic)topic);
				}
			}
			
		}
	}
	private void setupTranslation(Topic srcTopic){
		Topic baseTopic=srcTopic;
		do{
			if(baseModels.contains(baseTopic.getContainer(Model.class))) {
				// setup mapping of topic content
				setupTopicTranslation(srcTopic,baseTopic);
				return;
			}
			baseTopic=(Topic) srcTopic.getExtending();
		}while(baseTopic!=null);
		return;
	}
	
	private void setupTopicTranslation(Topic srcTopic, Topic baseTopic) {
		srctag2destElement.put(srcTopic.getScopedName(), baseTopic);
		if(srcTopic==baseTopic) {
			// no translation required
			return;
		}
		// for each class of srcTopic, find base in baseTopic
		while(srcTopic!=null) {
			if(srcTopic.isExtending(baseTopic) && srcTopic!=baseTopic) {
				Iterator<Element> eleIt=srcTopic.iterator();
				while(eleIt.hasNext()) {
					Element srcEle=eleIt.next();
					if(srcEle instanceof AbstractClassDef) {
						AbstractClassDef baseEle=(AbstractClassDef) srcEle;
						while(baseEle!=null){
							if(baseEle.getContainer()==baseTopic) {
								break;
							}
							baseEle=(AbstractClassDef)baseEle.getExtending();
						}
						if(baseEle!=null) {
							srctag2destElement.put(srcEle.getScopedName(), baseEle);
							if(srcEle!=baseEle) {
								setupClassTranslation((AbstractClassDef) srcEle,baseEle);
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
						AbstractClassDef baseEle=(AbstractClassDef) srcEle;
						srctag2destElement.put(srcEle.getScopedName(), baseEle);
						// no object translation required
						// setupClassTranslation((AbstractClassDef) srcEle,baseEle);
					}
				}
			}
			srcTopic=(Topic)srcTopic.getExtending();
		}
	}
	private void setupClassTranslation(AbstractClassDef srcEle, AbstractClassDef baseEle) {
		HashMap<String,ViewableTransferElement> baseAttrs=new HashMap<String,ViewableTransferElement>();
		Iterator<ViewableTransferElement> basePropIt=baseEle.getAttributesAndRoles2();
		while(basePropIt.hasNext()){
			ViewableTransferElement baseProp=basePropIt.next();
			String basePropName = ((Element)baseProp.obj).getName();
			baseAttrs.put(basePropName,baseProp);
		}
		Iterator<ViewableTransferElement> srcPropIt=srcEle.getAttributesAndRoles2();
		while(srcPropIt.hasNext()) {
			ViewableTransferElement srcProp=srcPropIt.next();
			String srcPropName = ((Element)srcProp.obj).getName();
			if(baseAttrs.containsKey(srcPropName)) {
				// attribute exists in base class
				srctag2destElement.put(((Element)srcProp.obj).getScopedName(), baseAttrs.get(srcPropName));
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
		Viewable aclass=(Viewable)modelElement;
		Viewable destClass=(Viewable)destModelEle;
		String destName=destClass.getScopedName();
		iomObj.setobjecttag(destName);
		
		// handle attrs
		Iterator iter = aclass.getAttributesAndRoles2();
		while (iter.hasNext()) {
			ViewableTransferElement srcProp = (ViewableTransferElement)iter.next();
			ViewableTransferElement destProp=(ViewableTransferElement) srctag2destElement.get(((Element)srcProp.obj).getScopedName());
			if(destProp==null) {
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
		AttributeDef destAttr=(AttributeDef)((ViewableTransferElement)srctag2destElement.get(srcAttr.getScopedName())).obj;
		for(int attri=0;attri<attrc;attri++){
			String attrValue=iomObj.getattrprim(srcAttrName,attri);
			if(attrValue!=null){
				if(isEnumType){
					attrValue=translateEnumValue((String)attrValue,srcEnumType,(EnumerationType)destAttr.getDomainResolvingAliases());
					iomObj.setattrvalue(srcAttrName, (String)attrValue);
				}
			}else{
				IomObject structValue=iomObj.getattrobj(srcAttrName,attri);
				if(isCompType){
					translateObject(structValue);
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
		Map<String,String> src2dest=src2destEles.get(srcEnumType);
		if(src2dest==null){
			src2dest=new HashMap<String,String>(); 
			List<String> srcVals=new java.util.ArrayList<String>();
		      buildEnumList(src2dest,"",srcEnumType.getConsolidatedEnumeration(),destEnumType.getConsolidatedEnumeration(),null);
			src2destEles.put(srcEnumType,src2dest);
		}
		return src2dest;
	}
	  public static void buildEnumList(java.util.Map<String,String> accu,String prefix1,Enumeration srcEnumer,Enumeration destEnumer,String destEeName){
	      String prefix="";
	      if(prefix1.length()>0){
	        prefix=prefix1+".";
	      }
	      Iterator srcIter = srcEnumer.getElements();
	      Iterator destIter = null;
	      if(destEnumer!=null) {
		      destIter=destEnumer.getElements();
	      }
	      while (srcIter.hasNext()) {
	        Enumeration.Element srcEe=(Enumeration.Element) srcIter.next();
	        Enumeration srcSubEnum = srcEe.getSubEnumeration();
	        Enumeration destSubEnum=null;
		      if(destEnumer!=null) {
			        Enumeration.Element destEe=(Enumeration.Element) destIter.next();
			        destSubEnum = destEe.getSubEnumeration();
			        if(destSubEnum==null) {
			        	destEeName=prefix+destEe.getName();
			        }
		      }
	        if (srcSubEnum != null)
	        {
	          // ee is not leaf, add its name to prefix and add sub elements to accu
	          buildEnumList(accu,prefix+srcEe.getName(),srcSubEnum,destSubEnum,destEeName);
	        }else{
	          // ee is a leaf, add it to accu
	          accu.put(prefix+srcEe.getName(),destEeName);
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
