package ch.ehi.ili2db.json;

import static org.junit.Assert.*;


import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;

public class Iox2jsonTest {
    @Test
    public void writeSimpleEmptyObject() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringWriter out=new java.io.StringWriter();
        JsonGenerator jg = jsonF.createJsonGenerator(out);
        
        IomObject iomObj=new Iom_jObject("Model.Topic.ClassA","1");
        Iox2jsonUtility.writeRaw(jg, iomObj,null);
        jg.flush();
        assertEquals("{\"@type\":\"Model.Topic.ClassA\",\"@id\":\"1\"}",out.toString());
    }
    @Test
    public void readSimpleEmptyObject() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringReader in=new java.io.StringReader("{\"@type\":\"Model.Topic.ClassA\",\"@id\":\"1\"}");
        JsonParser jg = jsonF.createJsonParser(in);
        
        IomObject iomObj=Iox2jsonUtility.readOneObject(jg);
        assertEquals("Model.Topic.ClassA oid 1 {}",iomObj.toString());
    }
    @Test
    public void writeSimpleStructEle() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringWriter out=new java.io.StringWriter();
        JsonGenerator jg = jsonF.createJsonGenerator(out);
        
        IomObject iomObj=new Iom_jObject("Model.Topic.StructA",null);
        iomObj.setattrvalue("attrA", "attrAvalue");
        Iox2jsonUtility.writeRaw(jg, iomObj,null);
        jg.flush();
        assertEquals("{\"@type\":\"Model.Topic.StructA\",\"attrA\":\"attrAvalue\"}",out.toString());
    }
    @Test
    public void readSimpleStructEle() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringReader in=new java.io.StringReader("{\"@type\":\"Model.Topic.StructA\",\"attrA\":\"attrAvalue\"}");
        JsonParser jg = jsonF.createJsonParser(in);
        
        IomObject iomObj=Iox2jsonUtility.readOneObject(jg);
        assertEquals("Model.Topic.StructA {attrA attrAvalue}",iomObj.toString());
    }
    @Test
    public void writeBag() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringWriter out=new java.io.StringWriter();
        JsonGenerator jg = jsonF.createJsonGenerator(out);
        
        IomObject iomObj=new Iom_jObject("Model.Topic.StructA",null);
        IomObject a1=iomObj.addattrobj("attrA", "Model.Topic.StructB");
        a1.setattrvalue("attrB", "b1");
        IomObject a2=iomObj.addattrobj("attrA", "Model.Topic.StructB");
        a2.setattrvalue("attrB", "b2");
        Iox2jsonUtility.writeRaw(jg, iomObj,null);
        jg.flush();
        assertEquals("{\"@type\":\"Model.Topic.StructA\",\"attrA\":[{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b1\"},{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b2\"}]}",out.toString());
    }
    @Test
    public void readBag() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringReader in=new java.io.StringReader("{\"attrA\":[{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b1\"},{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b2\"}],\"@type\":\"Model.Topic.StructA\"}");
        JsonParser jg = jsonF.createJsonParser(in);
        
        IomObject iomObj=Iox2jsonUtility.readOneObject(jg);
        assertEquals("Model.Topic.StructA {attrA [Model.Topic.StructB {attrB b1}, Model.Topic.StructB {attrB b2}]}",iomObj.toString());
        
    }
    @Test
    public void readArray() throws Exception
    {
        JsonFactory jsonF = new JsonFactory();
        java.io.StringReader in=new java.io.StringReader("[{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b1\"},{\"@type\":\"Model.Topic.StructB\",\"attrB\":\"b2\"}]");
        JsonParser jg = jsonF.createJsonParser(in);
        
        IomObject iomObj[]=Iox2jsonUtility.read(jg);
        assertEquals(2,iomObj.length);
        assertEquals("Model.Topic.StructB {attrB b1}",iomObj[0].toString());
        assertEquals("Model.Topic.StructB {attrB b2}",iomObj[1].toString());
        
    }

}
