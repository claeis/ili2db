package ch.ehi.ili2gpkg;

import org.junit.Assert;
import org.junit.Test;
import ch.ehi.ili2gpkg.Gpkg2iox;
import ch.interlis.iom.IomObject;

public class Gpkg2ioxTest {
    
    @Test
    public void coord() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] {71, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0};
        IomObject iomObj = obj1.read(wkb);
        String getattrvalueC1 = iomObj.getattrvalue("C1");
        String getattrvalueC2 = iomObj.getattrvalue("C2");

        Assert.assertEquals("COORD", iomObj.getobjecttag());
        Assert.assertEquals("480000.0", getattrvalueC1);
        Assert.assertEquals("310000.0", getattrvalueC2);
    }
    
    @Test
    public void multicoord() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] {71, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 1, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0};
        IomObject iomObj = obj1.read(wkb);
        {
            IomObject sequenceobj = iomObj.getattrobj("coord", 0);
            String getattrvalueC1 = sequenceobj.getattrvalue("C1");
            String getattrvalueC2 = sequenceobj.getattrvalue("C2");

            Assert.assertEquals("480000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);            
        }
        {
            IomObject sequenceobj = iomObj.getattrobj("coord", 1);
            String getattrvalueC1 = sequenceobj.getattrvalue("C1");
            String getattrvalueC2 = sequenceobj.getattrvalue("C2");

            Assert.assertEquals("480000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
        }

    }
    
    @Test
    public void polyline() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] {71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0,
                0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 1, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, 
                -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0};
        IomObject iomObj = obj1.read(wkb);
        Assert.assertEquals("POLYLINE", iomObj.getobjecttag());
        IomObject sequenceobj = iomObj.getattrobj("sequence", 0);
        IomObject segmentobj = sequenceobj.getattrobj("segment", 0);
        String getattrvalueC1 = segmentobj.getattrvalue("C1");
        String getattrvalueC2 = segmentobj.getattrvalue("C2");

        Assert.assertEquals("480000.0", getattrvalueC1);
        Assert.assertEquals("310000.0", getattrvalueC2);
        
        segmentobj = sequenceobj.getattrobj("segment", 1);
        getattrvalueC1 = segmentobj.getattrvalue("C1");
        getattrvalueC2 = segmentobj.getattrvalue("C2");
        
        Assert.assertEquals("500000.0", getattrvalueC1);
        Assert.assertEquals("310000.0", getattrvalueC2);        
    }
    
    @Test
    public void multiPolyline() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] { 71, 80, 0, 2, 0, 0, 0, 0, 65, 29, 76, 0, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 65, 29, 76, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0 };
        IomObject iomObj = obj1.read(wkb);
        Assert.assertEquals("MULTIPOLYLINE", iomObj.getobjecttag());
        {
            IomObject iompolyline = iomObj.getattrobj("polyline", 0);
            IomObject sequenceobj = iompolyline.getattrobj("sequence", 0);
            IomObject segmentobj = sequenceobj.getattrobj("segment", 0);
            String getattrvalueC1 = segmentobj.getattrvalue("C1");
            String getattrvalueC2 = segmentobj.getattrvalue("C2");

            Assert.assertEquals("480000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentobj = sequenceobj.getattrobj("segment", 1);
            getattrvalueC1 = segmentobj.getattrvalue("C1");
            getattrvalueC2 = segmentobj.getattrvalue("C2");
            
            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);             
        }
        {
            IomObject iompolyline = iomObj.getattrobj("polyline", 1);
            IomObject sequenceobj = iompolyline.getattrobj("sequence", 0);
            IomObject segmentobj = sequenceobj.getattrobj("segment", 0);
            String getattrvalueC1 = segmentobj.getattrvalue("C1");
            String getattrvalueC2 = segmentobj.getattrvalue("C2");

            Assert.assertEquals("480000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentobj = sequenceobj.getattrobj("segment", 1);
            getattrvalueC1 = segmentobj.getattrvalue("C1");
            getattrvalueC2 = segmentobj.getattrvalue("C2");
            
            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2); 
        }
       
    }
    
    @Test
    public void surface() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] {71, 80, 0, 2, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65,
                                18, -21, -64, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, 
                                -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0};
        IomObject iomObj = obj1.read(wkb);
        Assert.assertEquals("MULTISURFACE", iomObj.getobjecttag());
        IomObject surfaceObj = iomObj.getattrobj("surface", 0);
        IomObject boundaryObj = surfaceObj.getattrobj("boundary", 0);
        IomObject polylineObj = boundaryObj.getattrobj("polyline", 0);
        IomObject sequenceObj = polylineObj.getattrobj("sequence", 0);
        
        IomObject segmentObj = sequenceObj.getattrobj("segment", 0);
        String getattrvalueC1 = segmentObj.getattrvalue("C1");
        String getattrvalueC2 = segmentObj.getattrvalue("C2");

        Assert.assertEquals("500000.0", getattrvalueC1);
        Assert.assertEquals("180000.0", getattrvalueC2);
        
        segmentObj = sequenceObj.getattrobj("segment", 1);
        getattrvalueC1 = segmentObj.getattrvalue("C1");
        getattrvalueC2 = segmentObj.getattrvalue("C2");

        Assert.assertEquals("700000.0", getattrvalueC1);
        Assert.assertEquals("310000.0", getattrvalueC2);
        
        segmentObj = sequenceObj.getattrobj("segment", 2);
        getattrvalueC1 = segmentObj.getattrvalue("C1");
        getattrvalueC2 = segmentObj.getattrvalue("C2");

        Assert.assertEquals("800000.0", getattrvalueC1);
        Assert.assertEquals("310000.0", getattrvalueC2);
        
        segmentObj = sequenceObj.getattrobj("segment", 3);
        getattrvalueC1 = segmentObj.getattrvalue("C1");
        getattrvalueC2 = segmentObj.getattrvalue("C2");

        Assert.assertEquals("500000.0", getattrvalueC1);
        Assert.assertEquals("180000.0", getattrvalueC2);
    }
    
    @Test
    public void multiSurface() throws Exception {
        Gpkg2iox obj1 = new Gpkg2iox();
        byte wkb[]=new byte[] {71, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0, 65, 37, 92, -64, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 40, 106, 0, 0, 0, 0, 0, 65, 18, -21, -64, 0, 0, 0, 0, 65, 30, -124, -128, 0, 0, 0, 0, 65, 5, -7, 0, 0, 0, 0, 0};
        IomObject iomObj = obj1.read(wkb);
        Assert.assertEquals("MULTISURFACE", iomObj.getobjecttag());
        {
            IomObject surfaceObj = iomObj.getattrobj("surface", 0);
            IomObject boundaryObj = surfaceObj.getattrobj("boundary", 0);
            IomObject polylineObj = boundaryObj.getattrobj("polyline", 0);
            IomObject sequenceObj = polylineObj.getattrobj("sequence", 0);
            
            IomObject segmentObj = sequenceObj.getattrobj("segment", 0);
            String getattrvalueC1 = segmentObj.getattrvalue("C1");
            String getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("180000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 1);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("700000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 2);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("800000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 3);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("180000.0", getattrvalueC2);   
        }
        {
            IomObject surfaceObj = iomObj.getattrobj("surface", 0);
            IomObject boundaryObj = surfaceObj.getattrobj("boundary", 0);
            IomObject polylineObj = boundaryObj.getattrobj("polyline", 0);
            IomObject sequenceObj = polylineObj.getattrobj("sequence", 0);
            
            IomObject segmentObj = sequenceObj.getattrobj("segment", 0);
            String getattrvalueC1 = segmentObj.getattrvalue("C1");
            String getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("180000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 1);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("700000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 2);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("800000.0", getattrvalueC1);
            Assert.assertEquals("310000.0", getattrvalueC2);
            
            segmentObj = sequenceObj.getattrobj("segment", 3);
            getattrvalueC1 = segmentObj.getattrvalue("C1");
            getattrvalueC2 = segmentObj.getattrvalue("C2");

            Assert.assertEquals("500000.0", getattrvalueC1);
            Assert.assertEquals("180000.0", getattrvalueC2);
        }
    }
 
}