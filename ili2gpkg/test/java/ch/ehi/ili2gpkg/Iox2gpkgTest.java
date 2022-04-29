package ch.ehi.ili2gpkg;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2gpkg.Iox2gpkg;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class Iox2gpkgTest {

    @Test
    public void coord() throws Exception {  

        IomObject coordValue=new Iom_jObject("COORD",null);
        coordValue.setattrvalue("C1","480000.0");
        coordValue.setattrvalue("C2","310000.0");
        
        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = {71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0};
        byte wkb[]=obj1.coord2wkb(coordValue, 0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
    
    @Test
    public void multicoord() throws Exception {  
        IomObject multiCoordValue=new Iom_jObject(Wkb2iox.OBJ_MULTICOORD,null);
        {
            IomObject coordValue=new Iom_jObject("COORD",null);
            coordValue.setattrvalue("C1","480000.0");
            coordValue.setattrvalue("C2","310000.0");
            multiCoordValue.addattrobj(Wkb2iox.ATTR_COORD, coordValue);
        }
        {
            IomObject coordValue=new Iom_jObject("COORD",null);
            coordValue.setattrvalue("C1","480000.0");
            coordValue.setattrvalue("C2","310000.0");            
            multiCoordValue.addattrobj(Wkb2iox.ATTR_COORD, coordValue);
        }

        
        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = {71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0};
        byte wkb[]=(byte[]) obj1.multicoord2wkb(multiCoordValue, 0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
    
    @Test
    public void polyline() throws Exception {        
        IomObject polylineValue=new Iom_jObject("POLYLINE",null);
        IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
        IomObject coordValue=sequence.addattrobj("segment","COORD");
        coordValue.setattrvalue("C1","480000.0");
        coordValue.setattrvalue("C2","310000.0");

        IomObject coordValueArc=sequence.addattrobj("segment","COORD");
        coordValueArc.setattrvalue("C1","500000.0");
        coordValueArc.setattrvalue("C2","310000.0");
        
        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = { 71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0,
                0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, 
                -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0 };
        byte wkb[]=obj1.polyline2wkb(polylineValue, false, true, 0, 0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
    
    @Test
    public void multipolyline() throws Exception {        
        IomObject multipolylineValue=new Iom_jObject(Wkb2iox.OBJ_MULTIPOLYLINE,null);
        {
            IomObject polylineValue=new Iom_jObject("POLYLINE",null);
            IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
            IomObject coordValue=sequence.addattrobj("segment","COORD");
            coordValue.setattrvalue("C1","480000.0");
            coordValue.setattrvalue("C2","310000.0");

            IomObject coordValueArc=sequence.addattrobj("segment","COORD");
            coordValueArc.setattrvalue("C1","500000.0");
            coordValueArc.setattrvalue("C2","310000.0");

            multipolylineValue.addattrobj(Wkb2iox.ATTR_POLYLINE, polylineValue);
        }
        {
            IomObject polylineValue=new Iom_jObject("POLYLINE",null);
            IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
            IomObject coordValue=sequence.addattrobj("segment","COORD");
            coordValue.setattrvalue("C1","480000.0");
            coordValue.setattrvalue("C2","310000.0");

            IomObject coordValueArc=sequence.addattrobj("segment","COORD");
            coordValueArc.setattrvalue("C1","500000.0");
            coordValueArc.setattrvalue("C2","310000.0");

            multipolylineValue.addattrobj(Wkb2iox.ATTR_POLYLINE, polylineValue);
        }
        
        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = { 71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0 };
        byte wkb[]=obj1.multiline2wkb(multipolylineValue, false, 0, 0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
    
    @Test
    public void surface() throws Exception {        
        IomObject surfaceValue=new Iom_jObject("MULTISURFACE",null);
        {
            IomObject surface=surfaceValue.addattrobj("surface","SURFACE");
            IomObject boundary=surface.addattrobj("boundary","BOUNDARY");
            IomObject polylineValue=boundary.addattrobj("polyline","POLYLINE");
            IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
            
            IomObject coordValue1 = sequence.addattrobj("segment","COORD");
            coordValue1.setattrvalue("C1","500000.0");
            coordValue1.setattrvalue("C2","180000.0");
            
            IomObject coordValue2 = sequence.addattrobj("segment","COORD");
            coordValue2.setattrvalue("C1","700000.0");
            coordValue2.setattrvalue("C2","310000.0");
            
            IomObject coordValue3 = sequence.addattrobj("segment","COORD");
            coordValue3.setattrvalue("C1","800000.0");
            coordValue3.setattrvalue("C2","310000.0");
            
            IomObject coordValue4 = sequence.addattrobj("segment","COORD");
            coordValue4.setattrvalue("C1","500000.0");
            coordValue4.setattrvalue("C2","180000.0");
            
        }

        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = {71, 80, 0, 2, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65,
                18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, 
                -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0};
        byte wkb[]=obj1.surface2wkb(surfaceValue, false, 0, false,0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
    @Test
    public void multisurface() throws Exception {        
        IomObject surfaceValue=new Iom_jObject("MULTISURFACE",null);
        {
            IomObject surface=surfaceValue.addattrobj("surface","SURFACE");
            IomObject boundary=surface.addattrobj("boundary","BOUNDARY");
            IomObject polylineValue=boundary.addattrobj("polyline","POLYLINE");
            IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
            
            IomObject coordValue1 = sequence.addattrobj("segment","COORD");
            coordValue1.setattrvalue("C1","500000.0");
            coordValue1.setattrvalue("C2","180000.0");
            
            IomObject coordValue2 = sequence.addattrobj("segment","COORD");
            coordValue2.setattrvalue("C1","700000.0");
            coordValue2.setattrvalue("C2","310000.0");
            
            IomObject coordValue3 = sequence.addattrobj("segment","COORD");
            coordValue3.setattrvalue("C1","800000.0");
            coordValue3.setattrvalue("C2","310000.0");
            
            IomObject coordValue4 = sequence.addattrobj("segment","COORD");
            coordValue4.setattrvalue("C1","500000.0");
            coordValue4.setattrvalue("C2","180000.0");
            
        }
        {
            IomObject surface=surfaceValue.addattrobj("surface","SURFACE");
            IomObject boundary=surface.addattrobj("boundary","BOUNDARY");
            IomObject polylineValue=boundary.addattrobj("polyline","POLYLINE");
            IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
            
            IomObject coordValue1 = sequence.addattrobj("segment","COORD");
            coordValue1.setattrvalue("C1","500000.0");
            coordValue1.setattrvalue("C2","180000.0");
            
            IomObject coordValue2 = sequence.addattrobj("segment","COORD");
            coordValue2.setattrvalue("C1","700000.0");
            coordValue2.setattrvalue("C2","310000.0");
            
            IomObject coordValue3 = sequence.addattrobj("segment","COORD");
            coordValue3.setattrvalue("C1","800000.0");
            coordValue3.setattrvalue("C2","310000.0");
            
            IomObject coordValue4 = sequence.addattrobj("segment","COORD");
            coordValue4.setattrvalue("C1","500000.0");
            coordValue4.setattrvalue("C2","180000.0");
            
        }

        Iox2gpkg obj1 = new Iox2gpkg(2);
        byte expectedByte[] = {71, 80, 0, 2, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0};
        byte wkb[]=obj1.multisurface2wkb(surfaceValue, false, 0, false,0);
//        byte wkb[]=obj1.surface2wkb(surfaceValue, false, 0, 0);
        Assert.assertEquals(true, Arrays.equals(wkb, expectedByte));
    }
}