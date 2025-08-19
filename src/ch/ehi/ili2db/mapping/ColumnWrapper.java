package ch.ehi.ili2db.mapping;

import ch.interlis.ili2c.metamodel.Element;
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
    public ViewableTransferElement getViewableTransferElement() {
        return prop.getLast().getAttr();
    }
    public StructAttrPath getStructAttrPath() {
        return prop;
    }
    public Integer getEpsgCode() {
        return epsgCode;
    }
    @Override
    public String toString() {
        return ((Element)getViewableTransferElement().obj).getScopedName() + (epsgCode==null ? "":":"+epsgCode);
    }
    
}
