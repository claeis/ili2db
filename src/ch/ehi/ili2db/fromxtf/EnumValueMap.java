package ch.ehi.ili2db.fromxtf;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.metaattr.IliMetaAttrNames;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractEnumerationType;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.EnumTreeValueType;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.iom_j.itf.ModelUtilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EnumValueMap {
    private EnumValueMap() {};
    private HashMap<Long,String> id2xtf=new HashMap<Long,String>();
    private HashMap<String,Long> xtf2id=new HashMap<String,Long>();
    private HashMap<String,Integer> xtf2itfCode=new HashMap<String,Integer>();
    private HashMap<String,Integer> xtf2seq=new HashMap<String,Integer>();
    private HashMap<String,String> xtf2displayName=new HashMap<String,String>();
    private HashMap<String,String> xtf2doc=new HashMap<String,String>();
    public long mapXtfValue(String xtfvalue) {
        return xtf2id.get(xtfvalue);
    }
    public String mapIdValue(long value) {
        return id2xtf.get(value);
    }
    public String mapXtfValueToDisplayName(String xtfvalue) {
        return xtf2displayName.get(xtfvalue);
    }
    public int mapXtfValueToItfCode(String xtfvalue) {
        return xtf2itfCode.get(xtfvalue);
    }
    public int mapXtfValueToSeq(String xtfvalue) {
        return xtf2seq.get(xtfvalue);
    }
    public String mapXtfValueToDoc(String xtfvalue) {
        return xtf2doc.get(xtfvalue);
    }

    public Set<String> getXtfCodes() {
        HashSet<String> ret=new HashSet<String>();
        ret.addAll(xtf2id.keySet());
        return ret;
    }
    public static Set<String> readIliCodesFromDb(java.sql.Connection conn,String tidColumnName,boolean hasThisClassColumn,String qualifiedIliName,DbTableName sqlDbName)
    throws Ili2dbException
    {
        try {
            EnumValueMap map;
            map = readEnumValueMapFromDb(conn,tidColumnName,hasThisClassColumn,qualifiedIliName,sqlDbName);
            return map.getXtfCodes();
        } catch (SQLException ex) {
            throw new Ili2dbException("failed to read enum-table "+sqlDbName,ex);
        }
    }
    public static EnumValueMap readEnumValueMapFromDb(java.sql.Connection conn,String tidColumnName,boolean hasThisClassColumn,String qualifiedIliName,DbTableName sqlDbName) throws SQLException
    {
        EnumValueMap ret=new EnumValueMap();
    	String sqlName=sqlDbName.getName();
    	if(sqlDbName.getSchema()!=null){
    		sqlName=sqlDbName.getSchema()+"."+sqlName;
    	}
    		String exstStmt=null;
    		if(!hasThisClassColumn){
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+" FROM "+sqlName;
    		}else{
    			exstStmt="SELECT "+DbNames.ENUM_TAB_ILICODE_COL+(tidColumnName!=null?","+tidColumnName:"")+","+DbNames.ENUM_TAB_DISPNAME_COL+","+DbNames.ENUM_TAB_DESCRIPTION_COL+","+DbNames.ENUM_TAB_ITFCODE_COL+" FROM "+sqlName+" WHERE "+DbNames.ENUM_TAB_THIS_COL+" = '"+qualifiedIliName+"'";
    		}
    		EhiLogger.traceBackendCmd(exstStmt);
    		java.sql.PreparedStatement exstPrepStmt = null;
            java.sql.ResultSet rs=null;
    		try{
                exstPrepStmt = conn.prepareStatement(exstStmt);
    			rs=exstPrepStmt.executeQuery();
                Long id=0L;
                int seq=0;
    			while(rs.next()){
    			    int col=1;
    				String iliCode=rs.getString(col++);
                    if(tidColumnName!=null) {
                        id=rs.getLong(col++);
                    }else {
                        id++;
                    }
                    String displayName=rs.getString(col++);
                    String desc=rs.getString(col++);
                    int itfCode=rs.getInt(col++);
    				ret.addValue(id,iliCode,itfCode,displayName,desc,seq++);
    			}
    		}finally{
    		    if(rs!=null) {
    		        rs.close();
    		        rs=null;
    		    }
    		    if(exstPrepStmt!=null) {
                    exstPrepStmt.close();
                    exstPrepStmt=null;
    		    }
    		}
    	return ret;
    }

    void addValue(long id, String xtfCode, int itfCode,String displayName,String doc,int seq) {
        id2xtf.put(id,xtfCode);
        xtf2id.put(xtfCode,id);
        xtf2displayName.put(xtfCode, displayName);
        xtf2itfCode.put(xtfCode, itfCode);
        xtf2seq.put(xtfCode, seq);
        xtf2doc.put(xtfCode, doc);
    }
    public static EnumValueMap createEnumValueMap(Element attrOrDomain,ch.ehi.ili2db.mapping.NameMapping ili2sqlName) {
        Element attrOrDomain_tr=ili2sqlName.getTranslatedElement(attrOrDomain);
        String lang_tr=((Model)attrOrDomain_tr.getContainer(Model.class)).getLanguage();
        AbstractEnumerationType type=null;
        AbstractEnumerationType type_tr=null;
        if(attrOrDomain instanceof AttributeDef) {
            type=(AbstractEnumerationType)((AttributeDef)attrOrDomain).getDomainResolvingAll();
            type_tr=(AbstractEnumerationType)((AttributeDef)attrOrDomain_tr).getDomainResolvingAll();
        }else if(attrOrDomain instanceof Domain) {
            type=(AbstractEnumerationType)((Domain)attrOrDomain).getType();
            type_tr=(AbstractEnumerationType)((Domain)attrOrDomain_tr).getType();
        }else {
            throw new IllegalArgumentException("unexpected element "+attrOrDomain);
        }
        EnumValueMap ret=new EnumValueMap();
        java.util.List<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>> ev=new java.util.ArrayList<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>>();
        java.util.List<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>> ev_tr=new java.util.ArrayList<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>>();
        if(type instanceof EnumTreeValueType) {
            ch.interlis.iom_j.itf.ModelUtilities.buildEnumElementListAll(ev,"",type.getConsolidatedEnumeration());
            ch.interlis.iom_j.itf.ModelUtilities.buildEnumElementListAll(ev_tr,"",type_tr.getConsolidatedEnumeration());
        }else {
            ch.interlis.iom_j.itf.ModelUtilities.buildEnumElementList(ev,"",type.getConsolidatedEnumeration());
            ch.interlis.iom_j.itf.ModelUtilities.buildEnumElementList(ev_tr,"",type_tr.getConsolidatedEnumeration());
        }
        boolean isOrdered=type.isOrdered();
        int itfCode=0;
        int seq=0;
        Iterator<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>> evi=ev.iterator();
        Iterator<java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element>> evi_tr=ev_tr.iterator();
        while(evi.hasNext()){
            java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element> ele=evi.next();
            java.util.Map.Entry<String,ch.interlis.ili2c.metamodel.Enumeration.Element> ele_tr=evi_tr.next();
            String eleName=ele.getKey();
            String eleName_tr=ele_tr.getKey();
            Enumeration.Element eleElement=ele.getValue();
            Enumeration.Element eleElement_tr=ele_tr.getValue();
            String dispName=null;
            if(lang_tr!=null) {
                dispName = eleElement_tr.getMetaValues().getValue(IliMetaAttrNames.METAATTR_DISPNAME+"_"+lang_tr);
            }
            if (dispName==null){
                dispName = eleElement_tr.getMetaValues().getValue(IliMetaAttrNames.METAATTR_DISPNAME);
            }
            if (dispName==null){
                dispName = eleElement.getMetaValues().getValue(IliMetaAttrNames.METAATTR_DISPNAME);
            }
            if (dispName==null){
                dispName=ili2sqlName.beautifyEnumDispName(eleName_tr);
            }
            String doc=eleElement_tr.getDocumentation();
            if(doc==null) {
                doc=eleElement.getDocumentation();
            }
            ret.addValue(seq,eleName,itfCode,dispName,doc,seq);
            itfCode++;
            seq++;
            
        }        
        return ret;
    }
}
