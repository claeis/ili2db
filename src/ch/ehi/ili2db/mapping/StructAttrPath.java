package ch.ehi.ili2db.mapping;

import java.util.Arrays;
import java.util.Objects;

import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class StructAttrPath {
    private PathEl  path[];
    public static class PathEl {
        private ViewableTransferElement attr;
        private Integer idx;
        public PathEl(ViewableTransferElement attr, Integer idx) {
            super();
            this.attr = attr;
            this.idx = idx;
        }
        public PathEl(ViewableTransferElement attr) {
            super();
            this.attr = attr;
            this.idx = null;
        }
        public ViewableTransferElement getAttr() {
            return attr;
        }
        public Integer getIdx() {
            return idx;
        }
    }
    public StructAttrPath(ViewableTransferElement viewableTransferElement) {
        path=new PathEl[] {new PathEl(viewableTransferElement)};
    }
    public StructAttrPath(PathEl[] pathv) {
        path=pathv.clone();
    }
    public PathEl[] getPath()
    {
        return path;
    }
    public PathEl getLast()
    {
        return path[path.length-1];
    }
    public String getIliQName() {
        StructAttrPath.PathEl pathv[]=getPath();
        StringBuffer iliqnameBuf=new StringBuffer();
        iliqnameBuf.append(((ch.interlis.ili2c.metamodel.Element)pathv[0].getAttr().obj).getContainer().getScopedName(null));
        for(StructAttrPath.PathEl path:pathv) {
            iliqnameBuf.append(".");
            iliqnameBuf.append(((ch.interlis.ili2c.metamodel.Element)path.getAttr().obj).getName());
            Integer idx=path.getIdx();
            if(idx!=null) {
                iliqnameBuf.append("[");
                iliqnameBuf.append(idx.toString());
                iliqnameBuf.append("]");
            }
        }
        return iliqnameBuf.toString();
    }
    public String getName() {
        StructAttrPath.PathEl pathv[]=getPath();
        StringBuffer nameBuf=new StringBuffer();
        String sep="";
        for(StructAttrPath.PathEl path:pathv) {
            nameBuf.append(sep);
            nameBuf.append(((ch.interlis.ili2c.metamodel.Element)path.getAttr().obj).getName());
            Integer idx=path.getIdx();
            if(idx!=null) {
                nameBuf.append(idx.toString());
            }
            sep="_";
        }
        return nameBuf.toString();
    }
    @Override
    public String toString() {
        return "StructAttrPath [" + getIliQName() + "]";
    }
    
}
