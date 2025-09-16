package ch.ehi.ili2db.mapping;

import ch.ehi.ili2db.base.DbNames;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

public class StructAttrPath {
    public abstract static class PathEl {
        public Integer getIdx() {
            return null;
        }
        public abstract String getName();
        public abstract String getIliName();
        
    }
    public static class PathElType extends PathEl {
        public String getName()
        {
            return DbNames.T_TYPE_COL;
        }
        public String getIliName()
        {
            return "_type";
        }

    }
    private PathEl  path[];
    public static class PathElAttr extends PathEl {
        private ViewableTransferElement attr;
        private Integer idx;
        public PathElAttr(ViewableTransferElement attr, Integer idx) {
            super();
            this.attr = attr;
            this.idx = idx;
        }
        public PathElAttr(ViewableTransferElement attr) {
            super();
            this.attr = attr;
            this.idx = null;
        }
        public ViewableTransferElement getAttr() {
            return attr;
        }
        @Override
        public String getName()
        {
            return getIliName();
        }
        @Override
        public String getIliName()
        {
            return ((ch.interlis.ili2c.metamodel.Element)getAttr().obj).getName();
        }
        @Override
        public Integer getIdx() {
            return idx;
        }
    }
    public StructAttrPath(ViewableTransferElement viewableTransferElement) {
        path=new PathEl[] {new PathElAttr(viewableTransferElement)};
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
        StructAttrPath.PathElAttr pathEl=(StructAttrPath.PathElAttr)pathv[0];
        iliqnameBuf.append(((ch.interlis.ili2c.metamodel.Element)pathEl.getAttr().obj).getContainer().getScopedName(null));
        for(StructAttrPath.PathEl path:pathv) {
            iliqnameBuf.append(".");
            iliqnameBuf.append(path.getIliName());
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
            nameBuf.append(path.getName());
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
