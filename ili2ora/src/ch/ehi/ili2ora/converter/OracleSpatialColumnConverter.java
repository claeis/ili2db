package ch.ehi.ili2ora.converter;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import com.vividsolutions.jts.io.ParseException;
import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.gui.Config;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.wkb.Iox2wkb;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;

public class OracleSpatialColumnConverter extends AbstractWKBColumnConverter {
	
	private boolean strokeArcs=true;
	
	@Override
	public void setup(Connection conn, Settings config) {
		super.setup(conn,config);
		strokeArcs=Config.STROKE_ARCS_ENABLE.equals(Config.getStrokeArcs(config));
	}

    @Override
    public String getInsertValueWrapperCoord(String wkfValue,int srid) {
        return "ILI2ORA_SDO_GEOMETRY(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
        return "ILI2ORA_SDO_GEOMETRY(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperSurface(String wkfValue,int srid) {
        return "ILI2ORA_SDO_GEOMETRY(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
        return "ILI2ORA_SDO_GEOMETRY(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperMultiPolyline(String wkfValue,int srid) {
        return "ILI2ORA_SDO_GEOMETRY(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getSelectValueWrapperCoord(String dbNativeValue) {
        return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
    }
	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
	}
    @Override
    public String getSelectValueWrapperMultiPolyline(String dbNativeValue) {
        return "SDO_UTIL.TO_WKBGEOMETRY(" + dbNativeValue + ")";
    }

    @Override
    public java.lang.Object fromIomSurface(
        IomObject value,
        int srid,
        boolean hasLineAttr,
        boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);

            try {
                return conv.surface2wkb(value,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomMultiSurface(
        IomObject value,
        int srid,
        boolean hasLineAttr,
        boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);

            try {
                return conv.multisurface2wkb(value,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomCoord(IomObject value, int srid,boolean is3D)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            try {
                return conv.coord2wkb(value);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomMultiCoord(IomObject value, int srid,boolean is3D)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            try {
                return conv.multicoord2wkb(value);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomPolyline(IomObject value, int srid,boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            try {
                return conv.polyline2wkb(value,false,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomMultiPolyline(IomObject value, int srid,boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            try {
                return conv.multiline2wkb(value,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
        }
        return null;
    }

    @Override
    public IomObject toIomMultiCoord(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);
        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
    @Override
    public IomObject toIomSurface(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);
        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
    @Override
    public IomObject toIomMultiSurface(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);
        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public IomObject toIomCoord(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {

        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);

        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public IomObject toIomPolyline(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);

        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
    @Override
    public IomObject toIomMultiPolyline(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        Blob geom = (Blob) geomobj;
        int blobLenght = (int) geom.length();
        byte bv[]= geom.getBytes(1, blobLenght);

        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }

	@Override
	public Integer getSrsid(String crsAuthority, String crsCode,Connection conn) 
	throws ConverterException
	{
		int srsid;
		srsid=Integer.parseInt(crsCode);

		return srsid;
	}
}
