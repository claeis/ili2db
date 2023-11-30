package ch.ehi.ili2db.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom.IomConstants;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;

public class Iox2jsonUtility {
    public static final String REF = "@ref";
    public static final String REFBID = "@refbid";
    public static final String ORDERPOS = "@orderpos";
    public static final String CONSISTENCY = "@consistency";
    public static final String OPERATION = "@operation";
    public static final String TID = "@id";
    public static final String TYPE = "@type";
    public static final String BID = "@bid";
    public static final String TOPIC = "@topic";
    public static final String CONSISTENCY_ADAPTED = "ADAPTED";
    public static final String CONSISTENCY_INCOMPLETE = "INCOMPLETE";
    public static final String CONSISTENCY_INCONSISTENT = "INCONSISTENT";
    public static final String OPERATION_UPDATE = "UPDATE";
    public static final String OPERATION_DELETE = "DELETE";
    public static void write(JsonGenerator jg,ch.interlis.iom.IomObject objs[],TransferDescription td) throws IOException
    {
        if(objs.length==1) {
            writeRaw(jg,objs[0],td);
        }else {
            jg.writeStartArray();
            for(IomObject obj:objs) {
                writeRaw(jg,obj,td);
            }
            jg.writeEndArray();
        }
    }
    public static void writeArray(JsonGenerator jg,ch.interlis.iom.IomObject objs[],TransferDescription td) throws IOException
    {
        jg.writeStartArray();
        for(IomObject obj:objs) {
            writeRaw(jg,obj,td);
        }
        jg.writeEndArray();
    }
    public static void writeRaw(JsonGenerator jg,ch.interlis.iom.IomObject objs[]) throws IOException
    {
        if(objs.length==1) {
            writeRaw(jg,objs[0],null);
        }else {
            jg.writeStartArray();
            for(IomObject obj:objs) {
                writeRaw(jg,obj,null);
            }
            jg.writeEndArray();
        }
    }
    public static void writeRaw(JsonGenerator jg,ch.interlis.iom.IomObject obj,TransferDescription td) throws IOException
    {
        jg.writeStartObject();
        String className=obj.getobjecttag();
        jg.writeStringField(TYPE,className);
        String oid=obj.getobjectoid();
        if(oid!=null){
            jg.writeStringField(TID,oid);
        }
        int op = obj.getobjectoperation();
        if(op==IomConstants.IOM_OP_DELETE) {
            jg.writeStringField(OPERATION,OPERATION_DELETE);
        }else if(op==IomConstants.IOM_OP_UPDATE) {
            jg.writeStringField(OPERATION,OPERATION_UPDATE);
        }
        int consistency = obj.getobjectconsistency();
        if(consistency==IomConstants.IOM_INCONSISTENT) {
            jg.writeStringField(CONSISTENCY,CONSISTENCY_INCONSISTENT);
        }else if(consistency==IomConstants.IOM_INCOMPLETE) {
            jg.writeStringField(CONSISTENCY,CONSISTENCY_INCOMPLETE);
        }else if(consistency==IomConstants.IOM_ADAPTED) {
            jg.writeStringField(CONSISTENCY,CONSISTENCY_ADAPTED);
        }
        long orderpos = obj.getobjectreforderpos();
        if(orderpos!=0) {
            jg.writeNumberField(ORDERPOS,orderpos);
        }
        String refbid = obj.getobjectrefbid();
        if(refbid!=null){
            jg.writeStringField(REFBID,refbid);
        }
        String refoid = obj.getobjectrefoid();
        if(refoid!=null){
            jg.writeStringField(REF,refoid);
        }
        
        Viewable aclass=null;
        if(td!=null) {
            aclass=(Viewable) td.getElement(className);
        }
        if(aclass!=null) {
            Iterator attri=aclass.getAttributesAndRoles2();
            while(attri.hasNext()) {
                ViewableTransferElement propDef = (ViewableTransferElement) attri.next();
                if (propDef.obj instanceof AttributeDef) {
                    AttributeDef attr = (AttributeDef) propDef.obj;
                    if(!attr.isTransient()){
                        Type proxyType=attr.getDomain();
                        if(proxyType!=null && (proxyType instanceof ObjectType)){
                            // skip implicit particles (base-viewables) of views
                        }else{
                            String propName=attr.getName();
                            if(attr.isDomainBoolean()) {
                                String value=obj.getattrprim(propName,0);
                                if(value!=null){
                                    if(value.equals("true")) {
                                        jg.writeBooleanField(propName,true);
                                    }else {
                                        jg.writeBooleanField(propName,false);
                                    }
                                }
                            }else{
                                Type type=attr.getDomainResolvingAll();
                                if(type instanceof CompositionType) {
                                    int propc=obj.getattrvaluecount(propName);
                                    if(propc>0){
                                        jg.writeFieldName(propName);
                                        if(propc>1){
                                            jg.writeStartArray();
                                        }
                                        for(int propi=0;propi<propc;propi++){
                                            IomObject structvalue=obj.getattrobj(propName,propi);
                                            writeRaw(jg,structvalue,td);
                                        }
                                        if(propc>1){
                                            jg.writeEndArray();
                                        }
                                    }
                                    
                                }else if(type instanceof NumericType) {
                                    String value=obj.getattrprim(propName,0);
                                    if(value!=null){
                                        jg.writeFieldName(propName);
                                        jg.writeNumber(value);
                                    }
                                }else if(type instanceof CoordType) {
                                    IomObject structvalue=obj.getattrobj(propName,0);
                                    if(structvalue!=null) {
                                        jg.writeFieldName(propName);
                                        writeRaw(jg,structvalue,td);
                                    }
                                }else if(type instanceof LineType) {
                                    IomObject structvalue=obj.getattrobj(propName,0);
                                    if(structvalue!=null) {
                                        jg.writeFieldName(propName);
                                        writeRaw(jg,structvalue,td);
                                    }
                                }else {
                                    String value=obj.getattrprim(propName,0);
                                    if(value!=null){
                                        jg.writeStringField(propName,value);
                                    }
                                }
                            }
                        }
                    }
                }else if(propDef.obj instanceof RoleDef){
                    RoleDef role = (RoleDef) propDef.obj;
                    AssociationDef roleOwner = (AssociationDef) role.getContainer();
                    if (roleOwner.getDerivedFrom() == null) {
                        String propName=role.getName();
                        IomObject structvalue=obj.getattrobj(propName,0);
                        if(structvalue!=null) {
                            jg.writeFieldName(propName);
                            writeRaw(jg,structvalue,td);
                        }
                    }
                }else {
                    throw new IllegalStateException("unexpected property "+propDef.obj);
                }
            }
        }else {
            boolean isNumeric=false;
            if(className.equals("COORD") || className.equals("ARC")) {
                isNumeric=true;
            }
            int attrc = obj.getattrcount();
            String propNames[]=new String[attrc];
            for(int i=0;i<attrc;i++){
                   propNames[i]=obj.getattrname(i);
            }
            java.util.Arrays.sort(propNames);
            for(int i=0;i<attrc;i++){
               String propName=propNames[i];
                int propc=obj.getattrvaluecount(propName);
                if(propc>0){
                    jg.writeFieldName(propName);
                    if(propc>1){
                        jg.writeStartArray();
                    }
                    for(int propi=0;propi<propc;propi++){
                        String value=obj.getattrprim(propName,propi);
                        if(value!=null){
                            if(isNumeric) {
                                jg.writeNumber(value);
                            }else {
                                jg.writeString(value);
                            }
                        }else{
                            IomObject structvalue=obj.getattrobj(propName,propi);
                            writeRaw(jg,structvalue,td);
                        }
                    }
                    if(propc>1){
                        jg.writeEndArray();
                    }
                }
            }
            
        }
        jg.writeEndObject();
    }
    public static void writeArray(JsonGenerator jg, String[] iomValues, AttributeDef attr,boolean isEnumInt) throws IOException {
        jg.writeStartArray();
        for(String value:iomValues) {
            if(value==null) {
                jg.writeNull();
            }else {
                if(attr.isDomainBoolean()) {
                    if(value.equals("true")) {
                        jg.writeBoolean(true);
                    }else {
                        jg.writeBoolean(false);
                    }
                }else{
                    Type type=attr.getDomainResolvingAll();
                    if(type instanceof NumericType) {
                        jg.writeNumber(value);
                    }else if((type instanceof EnumerationType) && isEnumInt) {
                        jg.writeNumber(value);
                    }else {
                        jg.writeString(value);
                    }
                }
            }
        }
        jg.writeEndArray();
    }
    public static ch.interlis.iom.IomObject[] read(JsonParser jg) throws IOException
    {
        JsonToken current = jg.currentToken();
        // before any tokens have been read?
        if(current==null) {
            current = jg.nextToken();
            // end of input?
            if(current==null) {
                return null;
            }
        }
        IomObject ret[]=null;
        if(current==JsonToken.START_OBJECT) {
            IomObject structEle=readOneObject(jg);
            ret=new IomObject[1];
            ret[0]=structEle;
        }else if(current==JsonToken.START_ARRAY) {
            ArrayList<IomObject> objs=new ArrayList<IomObject>();
            current = jg.nextToken();
            while(current==JsonToken.START_OBJECT) {
                IomObject structEle=readOneObject(jg);
                objs.add(structEle);
                current = jg.nextToken();
            }
            if(current!=JsonToken.END_ARRAY) {
                throw new IOException("unexpected json token "+jg.currentToken().toString()+"; ']' expected");
            }
            current = jg.nextToken();
            ret=objs.toArray(new IomObject[objs.size()]);
        }else {
            throw new IOException("unexpected json token "+current.toString()+"; '{' or '[' expected");
        }
        return ret;
    }
    public static ch.interlis.iom.IomObject readOneObject(JsonParser jg) throws IOException
    {
        JsonToken current = jg.currentToken();
        // before any tokens have been read?
        if(current==null) {
            current = jg.nextToken();
            // end of input?
            if(current==null) {
                return null;
            }
        }
        if(current!=JsonToken.START_OBJECT) {
            throw new IOException("unexpected json token "+current.toString()+"; '{' expected");
        }
        current = jg.nextToken();
        IomObject ret=new Iom_jObject("TAG",null);
        while(current==JsonToken.FIELD_NAME) {
            String propName=jg.getCurrentName();
            current = jg.nextToken();
            String propValue=null;
            if(current==JsonToken.VALUE_TRUE) {
                propValue= "true";
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_FALSE) {
                propValue= "false";
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_NULL) {
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_NUMBER_FLOAT || current==JsonToken.VALUE_NUMBER_INT || current==JsonToken.VALUE_STRING) {
                propValue=jg.getValueAsString();
                current = jg.nextToken();
            }else if(current==JsonToken.START_OBJECT) {
                IomObject structEle=readOneObject(jg);
                ret.addattrobj(propName, structEle);
            }else if(current==JsonToken.START_ARRAY) {
                current = jg.nextToken();
                while(current==JsonToken.START_OBJECT) {
                    IomObject structEle=readOneObject(jg);
                    ret.addattrobj(propName, structEle);
                    current = jg.nextToken();
                }
                if(current!=JsonToken.END_ARRAY) {
                    throw new IOException("unexpected json token "+jg.currentToken().toString()+"; ']' expected");
                }
                current = jg.nextToken();
            }else {
                throw new IOException("unexpected json token "+jg.currentToken().toString());
            }
            if(propValue!=null) {
                
                if (propName.equals(CONSISTENCY)) {
                    if(propValue.equals(CONSISTENCY_ADAPTED)) {
                        ret.setobjectconsistency(IomConstants.IOM_ADAPTED);
                    }else if(propValue.equals(CONSISTENCY_INCOMPLETE)) {
                        ret.setobjectconsistency(IomConstants.IOM_INCOMPLETE);
                    }else if(propValue.equals(CONSISTENCY_INCONSISTENT)) {
                        ret.setobjectconsistency(IomConstants.IOM_INCONSISTENT);
                    }else {
                        throw new IOException("unexpected consistency value "+propValue);
                    }
                } else if (propName.equals(OPERATION)) {
                    if(propValue.equals(OPERATION_DELETE)) {
                        ret.setobjectoperation(IomConstants.IOM_OP_DELETE);
                    }else if(propValue.equals(OPERATION_UPDATE)) {
                        ret.setobjectoperation(IomConstants.IOM_OP_UPDATE);
                    }else {
                        throw new IOException("unexpected operation value "+propValue);
                    }
                } else if (propName.equals(ORDERPOS)) {
                    ret.setobjectreforderpos(Long.parseLong(propValue));
                } else if (propName.equals(REF)) {
                    ret.setobjectrefoid(propValue);
                } else if (propName.equals(REFBID)) {
                    ret.setobjectrefbid(propValue);
                } else if (propName.equals(TID)) {
                    ret.setobjectoid(propValue);
                } else if (propName.equals(TYPE)) {
                    ret.setobjecttag(propValue);
                }else {
                    ret.setattrvalue(propName, propValue);
                }
            }
        }
        if(current!=JsonToken.END_OBJECT) {
            throw new IOException("unexpected json token "+jg.currentToken().toString()+"; '}' expected");
        }
        return ret;
    }
    public static String[] readArray(JsonParser jg) throws IOException {
        JsonToken current = jg.currentToken();
        // before any tokens have been read?
        if(current==null) {
            current = jg.nextToken();
            // end of input?
            if(current==null) {
                return null;
            }
        }
        if(current!=JsonToken.START_ARRAY) {
            throw new IOException("unexpected json token "+jg.currentToken().toString()+"; '[' expected");
        }
        ArrayList<String> values=new ArrayList<String>();
        current = jg.nextToken();
        while(current!=null && current!=JsonToken.END_ARRAY) {
            if(current==JsonToken.VALUE_TRUE) {
                values.add("true");
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_FALSE) {
                values.add("false");
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_NULL) {
                values.add(null);
                current = jg.nextToken();
            }else if(current==JsonToken.VALUE_NUMBER_FLOAT || current==JsonToken.VALUE_NUMBER_INT || current==JsonToken.VALUE_STRING) {
                values.add(jg.getValueAsString());
                current = jg.nextToken();
            }else {
                throw new IOException("unexpected json token "+jg.currentToken().toString());
            }
        }
        return values.toArray(new String[values.size()]);
    }
    
}
