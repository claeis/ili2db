package ch.interlis.iox_j.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.NumericalType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
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
import ch.interlis.iom_j.Iom_jObject;
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

public class Rounder implements IoxFilter {

	private IoxLogging loggingHandler=null;
	private TransferDescription td=null;
	private Map<String, String> genericDomains = null;
	
	public Rounder(TransferDescription td,Settings config)
	{
		this.td=td;
	}

    @Override
    public IoxEvent filter(IoxEvent event) throws IoxException {
        if (event instanceof StartBasketEvent) {
            genericDomains = ((ch.interlis.iox_j.StartBasketEvent) event).getDomains();
        } else if (event instanceof EndBasketEvent) {
            genericDomains = null;
        } else if (event instanceof ObjectEvent) {
            IomObject iomObj = ((ObjectEvent) event).getIomObject();
            roundObject(iomObj);
        }
        return event;
    }

	private void roundObject(IomObject iomObj) {
		Element modelElement = td.getElement(iomObj.getobjecttag());
		if(modelElement==null) {
		    return; // unknown class; do not report here
		}
		Viewable aclass=(Viewable)modelElement;
		// handle attrs
		Iterator iter = aclass.getAttributesAndRoles2();
		while (iter.hasNext()) {
			ViewableTransferElement srcProp = (ViewableTransferElement)iter.next();
            if (srcProp.obj instanceof AttributeDef) {
                AttributeDef attr = (AttributeDef) srcProp.obj;
                if(!attr.isTransient()){
                    Type proxyType=attr.getDomain();
                    if(proxyType!=null && (proxyType instanceof ObjectType)){
                        // skip implicit particles (base-viewables) of views
                    }else{
                        roundAttrValue(iomObj,attr);
                    }
                }
            }else if(srcProp.embedded && srcProp.obj instanceof RoleDef) {
                IomObject ref=iomObj.getattrobj(((RoleDef)srcProp.obj).getName(), 0);
                if(ref!=null && ref.getattrcount()>0) {
                    roundObject(ref);
                }
            }
		}
	}

	
	private void roundAttrValue(IomObject iomObj, AttributeDef srcAttr) {
		String srcAttrName=srcAttr.getName();
		int attrc=iomObj.getattrvaluecount(srcAttrName);
		if(attrc==0){
			return;
		}
		Type type=srcAttr.getDomainResolvingAll();
		Model model = (Model) srcAttr.getContainer(Model.class);
		for(int attri=0;attri<attrc;attri++){
            if(type instanceof NumericType){
                String attrValue=iomObj.getattrprim(srcAttrName,attri);
                try {
                    BigDecimal value=roundNumber(attrValue,(NumericType)type);
                    ((Iom_jObject)iomObj).setattrvalue(srcAttrName, attri, value.toString());
                }catch(NumberFormatException ex) {
                    // ignore; keep value as it is
                }
            }else if(type instanceof CoordType) {
                IomObject attrValue=iomObj.getattrobj(srcAttrName,attri);
                CoordType coordType = (CoordType) type;
                if (coordType.isGeneric()) {
                    coordType = (CoordType) Ili2cUtility.resolveGenericCoordDomain(srcAttr, null, genericDomains).getType();
                }
                roundSegment(attrValue, coordType);
            }else if(type instanceof PolylineType) {
                IomObject attrValue=iomObj.getattrobj(srcAttrName,attri);
                roundLine(attrValue,(PolylineType)type, model);
            }else if(type instanceof SurfaceOrAreaType) {
                IomObject attrValue=iomObj.getattrobj(srcAttrName,attri);
                roundPolygon(attrValue,(SurfaceOrAreaType)type, model);
            }else if(srcAttr.getDomain() instanceof CompositionType){
                IomObject attrValue=iomObj.getattrobj(srcAttrName,attri);
                roundObject(attrValue);
            }
		}
	}

	private void roundPolygon(IomObject surfaceValue, SurfaceOrAreaType type, Model model) {
	    int surfacec=surfaceValue.getattrvaluecount("surface");
	    for(int surfacei=0;surfacei<surfacec;surfacei++) {
	        IomObject surface= surfaceValue.getattrobj("surface",surfacei);
	        int boundaryc=surface.getattrvaluecount("boundary");
	        for(int boundaryi=0;boundaryi<boundaryc;boundaryi++) {
	            IomObject boundary=surface.getattrobj("boundary",boundaryi);
	            int polylinec=boundary.getattrvaluecount("polyline");
	            for(int polylinei=0;polylinei<polylinec;polylinei++) {
	                IomObject polyline=boundary.getattrobj("polyline",polylinei);
	                roundLine(polyline, type, model);
	            }
	        }
	    }
    }
    private void roundLine(IomObject polylineValue, LineType type, Model model) {
        Domain coordDomain = type.getControlPointDomain();
        CoordType coordType = (CoordType) coordDomain.getType();
        if (coordType.isGeneric()) {
            coordType = (CoordType) Ili2cUtility.resolveGenericCoordDomain(model, coordDomain, null, genericDomains).getType();
        }
        int sequencec=polylineValue.getattrvaluecount("sequence");
        for(int sequencei=0;sequencei<sequencec;sequencei++) {
            IomObject sequence=polylineValue.getattrobj("sequence",sequencei);
            int segmentc=sequence.getattrvaluecount("segment");
            for(int segmenti=0;segmenti<segmentc;segmenti++) {
                IomObject segment=sequence.getattrobj("segment",segmenti);
                roundSegment(segment,coordType);
            }
            
        }
    }
    private void roundSegment(IomObject iomObj, CoordType coordType) {
	    NumericalType[] dims = coordType.getDimensions();
	    for(int i=0;i<dims.length;i++) {
	        NumericType dimType = (NumericType) dims[i];
	        for(String prop:new String[] {"C","A"}) {
	            String propName=prop+Integer.toString(i+1);
	            String attrValue=iomObj.getattrprim(propName,0);
	            if(attrValue!=null) {
	                try {
	                    BigDecimal value=roundNumber(attrValue,dimType);
	                    iomObj.setattrvalue(propName, value.toString());
	                }catch(NumberFormatException ex) {
	                    // ignore; keep value as it is
	                }
	            }
	        }
	    }
    }
    private BigDecimal roundNumber(String attrValue, NumericType type) {
        PrecisionDecimal minimum=((NumericType) type).getMinimum();
        int precision= minimum.getAccuracy();
        BigDecimal value=Validator.roundNumeric(precision, attrValue);
        return value;
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
