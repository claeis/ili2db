package ch.interlis.iox_j.wkb;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.io.ParseException;

import ch.interlis.iom.IomObject;

public class Iox2wkbTest {
	private void addArc(IomObject polyline,double xa, double ya,double x, double y){
		IomObject sequence=polyline.getattrobj("sequence",0);
		IomObject ret=new ch.interlis.iom_j.Iom_jObject("ARC",null);
		ret.setattrvalue("C1", Double.toString(x));
		ret.setattrvalue("C2", Double.toString(y));
		ret.setattrvalue("A1", Double.toString(xa));
		ret.setattrvalue("A2", Double.toString(ya));
		sequence.addattrobj("segment",ret);
	}
	private void addCoord(IomObject polyline,double x, double y){
		IomObject sequence=polyline.getattrobj("sequence",0);
		IomObject ret=new ch.interlis.iom_j.Iom_jObject("COORD",null);
		ret.setattrvalue("C1", Double.toString(x));
		ret.setattrvalue("C2", Double.toString(y));
		sequence.addattrobj("segment",ret);
	}
	private IomObject newPolyline()
	{
		IomObject ret=new ch.interlis.iom_j.Iom_jObject("POLYLINE",null);
		IomObject sequence=new ch.interlis.iom_j.Iom_jObject("SEGMENTS",null);
		ret.addattrobj("sequence",sequence);
		return ret;
	}
	private IomObject createPolgon(IomObject polyline) {
		// MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline
		IomObject ret=new ch.interlis.iom_j.Iom_jObject("MULTISURFACE",null);
		IomObject surface=new ch.interlis.iom_j.Iom_jObject("SURFACE",null);
		IomObject boundary=new ch.interlis.iom_j.Iom_jObject("BOUNDARY",null);
		boundary.addattrobj("polyline", polyline);
		surface.addattrobj("boundary",boundary);
		ret.addattrobj("surface",surface);
		return ret;
	}

	@Test
	public void testSurface2wkb() throws Iox2wkbException, ParseException {
		Iox2wkb conv=new Iox2wkb(2);
		String value="MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 614899.152, C2 226049.103}, ARC {A2 226054.966, A1 614901.943, C1 614908.417, C2 226055.465}, ARC {A2 226044.618, A1 614909.049, C1 614899.152, C2 226049.103}]}}}}}";
		String v=    "MULTISURFACE {surface SURFACE {boundary BOUNDARY {polyline POLYLINE {sequence SEGMENTS {segment [COORD {C1 614899.152, C2 226049.103}, ARC {A2 226054.966, A1 614901.943, C1 614908.417, C2 226055.465}, ARC {A2 226044.618, A1 614909.049, C1 614899.152, C2 226049.103}]}}}}}";
		IomObject polyline=newPolyline();
		addCoord(polyline,614899.152, 226049.103);
		addArc(polyline, 614901.943, 226054.966, 614908.417, 226055.465); 
		addArc(polyline, 614909.049, 226044.618, 614899.152, 226049.103); 
		IomObject polygon=createPolgon(polyline);
		System.out.println(polygon);
		assertEquals(value,polygon.toString());
		byte wkb[]=conv.surface2wkb(polygon,false,0.001);
		Wkb2iox backConv=new Wkb2iox();
		IomObject result=backConv.read(wkb);
		System.out.println(result);
		
	}

}
