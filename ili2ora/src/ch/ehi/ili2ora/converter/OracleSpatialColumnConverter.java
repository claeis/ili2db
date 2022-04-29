package ch.ehi.ili2ora.converter;

import java.io.IOException;
import java.io.OutputStream;
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
    private boolean repairTouchingLines;
    private String geomFromWkbFunction;
    private static final String GEOM_FROM_WKB_FUNCTION="ILI2ORA_SDO_GEOMETRY";
    private static final String GEOM_TO_WKB_FUNCTION="SDO_UTIL.TO_WKBGEOMETRY";

    @Override
    public void setup(Connection conn, Settings config) {
        super.setup(conn,config);
        strokeArcs=Config.STROKE_ARCS_ENABLE.equals(Config.getStrokeArcs(config));
        repairTouchingLines = ((Config)config).getRepairTouchingLines();
        geomFromWkbFunction=GEOM_FROM_WKB_FUNCTION;
        if(config instanceof Config) {
            String dbschema=((Config)config).getDbschema();
            if(dbschema!=null) {
                geomFromWkbFunction=dbschema+"."+geomFromWkbFunction;
            }
        }
    }

    @Override
    public String getInsertValueWrapperCoord(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperSurface(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperMultiPolyline(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getInsertValueWrapperMultiCoord(String wkfValue,int srid) {
        return geomFromWkbFunction+"(" + wkfValue + ", "+ Integer.toString(srid)  + ")";
    }
    @Override
    public String getSelectValueWrapperCoord(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
    }
    @Override
    public String getSelectValueWrapperPolyline(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
    }
    @Override
    public String getSelectValueWrapperSurface(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
    }
    @Override
    public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
    }
    @Override
    public String getSelectValueWrapperMultiPolyline(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
    }
    @Override
    public String getSelectValueWrapperMultiCoord(String dbNativeValue) {
        return GEOM_TO_WKB_FUNCTION+ "(" + dbNativeValue + ")";
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
            byte[] geomObj;
            try {
                geomObj = conv.surface2wkb(value,!strokeArcs,p,repairTouchingLines);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
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
            byte[] geomObj;
            try {
                geomObj =conv.multisurface2wkb(value,!strokeArcs,p,repairTouchingLines);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomCoord(IomObject value, int srid,boolean is3D)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            byte[] geomObj;
            try {
                geomObj =  conv.coord2wkb(value);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomMultiCoord(IomObject value, int srid,boolean is3D)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            byte[] geomObj;
            try {
                geomObj = conv.multicoord2wkb(value);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomPolyline(IomObject value, int srid,boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            byte[] geomObj;
            try {
                geomObj =  conv.polyline2wkb(value,false,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
        }
        return null;
    }
    @Override
    public java.lang.Object fromIomMultiPolyline(IomObject value, int srid,boolean is3D,double p)
    throws SQLException, ConverterException {
        if (value!=null) {
            Iox2wkb conv=new Iox2wkb(is3D?3:2);
            byte[] geomObj;
            try {
                geomObj =  conv.multiline2wkb(value,!strokeArcs,p);
            } catch (Iox2wkbException ex) {
                throw new ConverterException(ex);
            }
            return createBlobFromBytes(geomObj);
        }
        return null;
    }

    @Override
    public IomObject toIomMultiCoord(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }
    @Override
    public IomObject toIomSurface(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }
    @Override
    public IomObject toIomMultiSurface(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }

    @Override
    public IomObject toIomCoord(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }

    @Override
    public IomObject toIomPolyline(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }
    @Override
    public IomObject toIomMultiPolyline(
        Object geomobj,
        String sqlAttrName,
        boolean is3D)
    throws SQLException, ConverterException {
        return toIomGeometry((Blob)geomobj);
    }

    @Override
    public Integer getSrsid(String crsAuthority, String crsCode,Connection conn) 
    throws ConverterException
    {
        int srsid;
        srsid=Integer.parseInt(crsCode);

        return srsid;
    }
    
    private Blob createBlobFromBytes(byte[] geomObject) throws SQLException, ConverterException {
        Blob data = conn.createBlob();
        OutputStream stream =  data.setBinaryStream(1);
        try {
            stream.write(geomObject);
            stream.flush();
        } catch (IOException ex) {
            throw new ConverterException(ex);
        }
        
        return data;
    }
    
    private IomObject toIomGeometry(Blob geom) throws SQLException, ConverterException {
        int blobLenght = (int) geom.length();
        byte[] bv= geom.getBytes(1, blobLenght);
        Wkb2iox conv=new Wkb2iox();
        try {
            return conv.read(bv);
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}
