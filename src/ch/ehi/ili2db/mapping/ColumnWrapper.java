package ch.ehi.ili2db.mapping;

import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class ColumnWrapper {
    private ViewableTransferElement prop=null;
    private Integer epsgCode=null;
    public ColumnWrapper(ViewableTransferElement transferProperty) {
        prop=transferProperty;
    }
    public ColumnWrapper(ViewableTransferElement transferProperty, int epsgCode) {
        prop=transferProperty;
        this.epsgCode=epsgCode;
    }
    public ViewableTransferElement getViewableTransferElement() {
        return prop;
    }
    public Integer getEpsgCode() {
        return epsgCode;
    }
    @Override
    public String toString() {
        return ((Element)prop.obj).getScopedName() + (epsgCode==null ? "":":"+epsgCode);
    }
    
}
