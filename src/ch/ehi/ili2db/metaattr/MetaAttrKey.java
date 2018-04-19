package ch.ehi.ili2db.metaattr;

public class MetaAttrKey {
    public MetaAttrKey(String iliElement, String attrName) {
        super();
        this.iliElement = iliElement;
        this.attrName = attrName;
    }
    private String iliElement=null;
    private String attrName=null;
    public String getIliElement() {
        return iliElement;
    }
    public String getAttrName() {
        return attrName;
    }

}
