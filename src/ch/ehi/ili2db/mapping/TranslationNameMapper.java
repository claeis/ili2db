package ch.ehi.ili2db.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.Ili2dbException;
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

public class TranslationNameMapper {

	private Map<Element,Map<String,Element>> mappings=new HashMap<Element,Map<String,Element>>();
	
	public TranslationNameMapper(TransferDescription td,Settings config)
	{
	    ArrayList<Model> models=new ArrayList<Model>(); 
		for(Iterator<Model> modelIt=td.iterator();modelIt.hasNext();){
			Model model=modelIt.next();
			models.add(0,model);
		}
		for(Model model:models) {
		    visitElement(model);
		}
	}
	public Element translateElement(String langPath[],Element src) {
        Map<String,Element> translations=mappings.get(src);
	    if(translations!=null) {
	        for(String lang:langPath) {
	            Element dest=translations.get(lang);
	            if(dest!=null) {
	                return dest;
	            }
	        }
	    }
        return src;
	}
	
    private void visitElement(Element el)
    {
        if(mappings.containsKey(el)) {
            return;
        }
        Map<String,Element> translations=new HashMap<String,Element>();
        ArrayList<Element> eles=new ArrayList<Element>();
        Element translatedEle=el;
        while(translatedEle!=null){
            eles.add(0,translatedEle);
            String lang=getLang(translatedEle);
            if(!translations.containsKey(lang)) {
                translations.put(lang, translatedEle);
            }
            translatedEle=translatedEle.getTranslationOf();
        }
        for(Element e:eles) {
            mappings.put(e,translations);
        }
        
        if(el instanceof Container){
            Container e = (Container) el;
            Iterator it = e.iterator();
            while(it.hasNext()){
                visitElement((Element)it.next());
            }
        }
    }

    private static String getLang(Element el) {
        Model model=null;
        if(el instanceof Model) {
            model=(Model) el;
        }else {
            model=(Model) el.getContainer(Model.class);
        }
        String lang=model.getLanguage();
        return lang;
    }
	
	
}
