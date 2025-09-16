package ch.ehi.ili2db.mapping;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class ColumnWrapper {
    private StructAttrPath prop=null;
    private Integer epsgCode=null;
    public ColumnWrapper(StructAttrPath transferProperty) {
        prop=transferProperty;
    }
    public ColumnWrapper(StructAttrPath transferProperty, Integer epsgCode) {
        prop=transferProperty;
        this.epsgCode=epsgCode;
    }
    public boolean isTypeCol() {
        if(prop.getLast() instanceof StructAttrPath.PathElType) {
            return true;
        }
        return false;
    }
    public boolean isIliAttr() {
        if(prop.getLast() instanceof StructAttrPath.PathElAttr) {
            return getViewableTransferElement().obj instanceof AttributeDef;
        }
        return false;
    }
    public boolean isIliRole() {
        if(prop.getLast() instanceof StructAttrPath.PathElAttr) {
            return getViewableTransferElement().obj instanceof RoleDef;
        }
        return false;
    }
    public boolean isIliElement() {
        if(prop.getLast() instanceof StructAttrPath.PathElAttr) {
            return true;
        }
        return false;
    }
    public ViewableTransferElement getViewableTransferElement() {
        return ((StructAttrPath.PathElAttr)prop.getLast()).getAttr();
    }
    public StructAttrPath getStructAttrPath() {
        return prop;
    }
    public Integer getEpsgCode() {
        return epsgCode;
    }
    @Override
    public String toString() {
        return getStructAttrPath().getIliQName() + (epsgCode==null ? "":":"+epsgCode);
    }
    
}
