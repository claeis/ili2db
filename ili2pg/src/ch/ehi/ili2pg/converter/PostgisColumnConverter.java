/* This file is part of the ili2ora project.
 * For more information, please see <http://www.interlis.ch>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.ehi.ili2pg.converter;

import ch.ehi.basics.settings.Settings;
import ch.ehi.ili2db.converter.AbstractWKBColumnConverter;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.json.Iox2jsonUtility;
import ch.ehi.ili2db.toxtf.ToXtfRecordConverter;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Types;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.vividsolutions.jts.io.ParseException;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BlackboxType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.TextType;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.wkb.Iox2wkb;
import ch.interlis.iox_j.wkb.Iox2wkbException;
import ch.interlis.iox_j.wkb.Wkb2iox;
import net.iharder.Base64;

public class PostgisColumnConverter extends AbstractWKBColumnConverter {
	private boolean strokeArcs=true;
	private boolean repairTouchingLines;
    private TransferDescription td=null;
	@Override
	public void setup(Connection conn, Settings config) {
		super.setup(conn,config);
		strokeArcs=Config.STROKE_ARCS_ENABLE.equals(Config.getStrokeArcs(config));
		repairTouchingLines = ((Config)config).getRepairTouchingLines();
	}
	
	@Override
	public String getInsertValueWrapperCoord(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperMultiCoord(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperPolyline(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperMultiPolyline(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperSurface(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperMultiSurface(String wkfValue,int srid) {
		return "ST_GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
		//return "GeomFromWKB("+wkfValue+(srid==-1?"":","+srid)+")";
	}
	@Override
	public String getInsertValueWrapperArray(String sqlColName) {
		return sqlColName;
	}

	@Override
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperMultiCoord(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperMultiPolyline(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperMultiSurface(String dbNativeValue) {
		return "ST_AsEWKB("+dbNativeValue+")";
		//return "AsBinary("+dbNativeValue+")";
	}
	@Override
	public String getSelectValueWrapperArray(String dbColName) {
		return dbColName;
	}
	public Object fromIomUuid(String uuid) 
			throws java.sql.SQLException, ConverterException
	{
		 org.postgresql.util.PGobject toInsertUUID = new org.postgresql.util.PGobject();
		 toInsertUUID.setType("uuid");
		 toInsertUUID.setValue(uuid);	
		return toInsertUUID;
	}
	@Override
	public Object fromIomXml(String xml) 
			throws java.sql.SQLException, ConverterException
	{
		 org.postgresql.util.PGobject toInsertUUID = new org.postgresql.util.PGobject();
		 toInsertUUID.setType("xml");
		 toInsertUUID.setValue(xml);	
		return toInsertUUID;
	}
	@Override
	public Object fromIomBlob(String blob) 
			throws java.sql.SQLException, ConverterException
	{

	    byte[] bytearray;
		try {
			bytearray = Base64.decode(blob);
		} catch (IOException e) {
			throw new ConverterException(e);
		}
		return bytearray;
	}
	@Override
	public java.lang.Object fromIomSurface(
			IomObject value,
			int srid,
			boolean hasLineAttr,
			boolean is3D,double p)
			throws SQLException, ConverterException {
				if(value!=null){
					Iox2wkb conv=new Iox2wkb(is3D?3:2);
					//EhiLogger.debug("conv "+conv); // select st_asewkt(form) from tablea
					try {
						return conv.surface2wkb(value,!strokeArcs,p,repairTouchingLines);
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
				if(value!=null){
					Iox2wkb conv=new Iox2wkb(is3D?3:2);
					//EhiLogger.debug("conv "+conv); // select st_asewkt(form) from tablea
					try {
						return conv.multisurface2wkb(value,!strokeArcs,p,repairTouchingLines);
					} catch (Iox2wkbException ex) {
						throw new ConverterException(ex);
					}
				}
				return null;
		}
		@Override
		public java.lang.Object fromIomCoord(IomObject value, int srid,boolean is3D)
			throws SQLException, ConverterException {
			if(value!=null){
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
			if(value!=null){
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
			if(value!=null){
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
			if(value!=null){
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
		public Object fromIomArray(ch.interlis.ili2c.metamodel.AttributeDef attr,String[] iomValues,Class<? extends DbColumn> dbColHint) throws SQLException, ConverterException {
			java.sql.Array array=null;
			ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAliases();
			if (attr.isDomainBoolean()) {
				Boolean values[]=new Boolean[iomValues.length];
				for(int i=0;i<values.length;i++) {
					if(iomValues[i].equals("true")){
						values[i]=true;
					}else {
						values[i]=false;
					}
				}
				array=conn.createArrayOf("bool", values);
			}else if (attr.isDomainIli1Date()) {
				java.sql.Date values[]=new java.sql.Date[iomValues.length];
				for(int i=0;i<values.length;i++) {
					String iomValue=iomValues[i];
					GregorianCalendar gdate=new GregorianCalendar(Integer.parseInt(iomValue.substring(0,4)),Integer.parseInt(iomValue.substring(4,6))-1,Integer.parseInt(iomValue.substring(6,8)));
					values[i]=new java.sql.Date(gdate.getTimeInMillis());
				}
				array=conn.createArrayOf("DATE", values);
			}else if (attr.isDomainIliUuid()) {
				Object values[]=new Object[iomValues.length];
				for(int i=0;i<values.length;i++) {
					String iomValue=iomValues[i];
					values[i]=fromIomUuid(iomValue);
				}
				array=conn.createArrayOf("uuid", values);
			}else if (attr.isDomainIli2Date()) {
				java.sql.Date values[]=new java.sql.Date[iomValues.length];
				for(int i=0;i<values.length;i++) {
					String iomValue=iomValues[i];
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(iomValue);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					values[i]=new java.sql.Date(xmldate.toGregorianCalendar().getTimeInMillis());
				}
				array=conn.createArrayOf("DATE", values);
			}else if (attr.isDomainIli2DateTime()) {
				java.sql.Timestamp values[]=new java.sql.Timestamp[iomValues.length];
				for(int i=0;i<values.length;i++) {
					String iomValue=iomValues[i];
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(iomValue);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					values[i]=new java.sql.Timestamp(xmldate.toGregorianCalendar().getTimeInMillis());
				}
				array=conn.createArrayOf("TIMESTAMP", values);
			}else if (attr.isDomainIli2Time()) {
				java.sql.Time values[]=new java.sql.Time[iomValues.length];
				for(int i=0;i<values.length;i++) {
					String iomValue=iomValues[i];
					XMLGregorianCalendar xmldate;
					try {
						xmldate = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(iomValue);
					} catch (DatatypeConfigurationException e) {
						throw new ConverterException(e);
					}
					values[i]=new java.sql.Time(xmldate.toGregorianCalendar().getTimeInMillis());
				}
				array=conn.createArrayOf("TIME", values);
			}else if(type instanceof EnumerationType){
				if(DbColNumber.class.equals(dbColHint)){
					Long values[]=new Long[iomValues.length];
					for(int i=0;i<values.length;i++) {
						String iomValue=iomValues[i];
						long itfCode=Long.parseLong(iomValue);
						values[i]=itfCode;
					}
					array=conn.createArrayOf("BIGINT", values);
				}else {
					array=conn.createArrayOf("VARCHAR", iomValues);
				}
			}else if(type instanceof NumericType){
				if(type.isAbstract()){
				}else{
					PrecisionDecimal min=((NumericType)type).getMinimum();
					PrecisionDecimal max=((NumericType)type).getMaximum();
					if(min.getAccuracy()>0){
						Double values[]=new Double[iomValues.length];
						for(int i=0;i<values.length;i++) {
							String iomValue=iomValues[i];
							double value=Double.parseDouble(iomValue);
							values[i]=value;
						}
						array=conn.createArrayOf("DECIMAL", values);
					}else{
						Integer values[]=new Integer[iomValues.length];
						for(int i=0;i<values.length;i++) {
							String iomValue=iomValues[i];
							int value=(int)Math.round(Double.parseDouble(iomValue));
							values[i]=value;
						}
						array=conn.createArrayOf("INTEGER", values);
					}
				}
			}else if(type instanceof TextType){
				array=conn.createArrayOf("VARCHAR", iomValues);
			}else if(type instanceof BlackboxType){
				if(((BlackboxType)type).getKind()==BlackboxType.eXML){
					Object values[]=new Object[iomValues.length];
					for(int i=0;i<values.length;i++) {
						String iomValue=iomValues[i];
						values[i]=fromIomXml(iomValue);
					}
					array=conn.createArrayOf("xml", values);
				}else{
					Object values[]=new Object[iomValues.length];
					for(int i=0;i<values.length;i++) {
						String iomValue=iomValues[i];
						values[i]=fromIomBlob(iomValue);
					}
					array=conn.createArrayOf("bytea", values);
				}
            }else if(type instanceof ReferenceType){
                if(DbColId.class.equals(dbColHint)){
                    Long values[]=new Long[iomValues.length];
                    for(int i=0;i<values.length;i++) {
                        String iomValue=iomValues[i];
                        long itfCode=Long.parseLong(iomValue);
                        values[i]=itfCode;
                    }
                    array=conn.createArrayOf("BIGINT", values);
                }else {
                    array=conn.createArrayOf("VARCHAR", iomValues);
                }
			}else{
				throw new IllegalArgumentException(attr.getScopedName());
			}
			
			return array;
		}
		@Override
		public IomObject toIomCoord(
				Object geomobj,
				String sqlAttrName,
				boolean is3D)
				throws SQLException, ConverterException {
				byte bv[]=(byte [])geomobj;
				Wkb2iox conv=new Wkb2iox();
				try {
					return conv.read(bv);
				} catch (ParseException e) {
					throw new ConverterException(e);
				}
			}
		@Override
		public IomObject toIomMultiCoord(
				Object geomobj,
				String sqlAttrName,
				boolean is3D)
				throws SQLException, ConverterException {
				byte bv[]=(byte [])geomobj;
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
				byte bv[]=(byte [])geomobj;
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
			byte bv[]=(byte [])geomobj;
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
				byte bv[]=(byte [])geomobj;
				//String v=((org.postgresql.util.PGobject)geomobj).getValue();
				//byte bv[]=WKBReader.hexToBytes(v);
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
			byte bv[]=(byte [])geomobj;
			//String v=((org.postgresql.util.PGobject)geomobj).getValue();
			//byte bv[]=WKBReader.hexToBytes(v);
			Wkb2iox conv=new Wkb2iox();
			try {
				return conv.read(bv);
			} catch (ParseException e) {
				throw new ConverterException(e);
			}
		}

	@Override
	public String toIomXml(Object obj) throws java.sql.SQLException,
			ConverterException {
		return ((java.sql.SQLXML)obj).getString();
	}

	@Override
	public String toIomBlob(Object obj) throws java.sql.SQLException,
			ConverterException {
	    String s = Base64.encodeBytes((byte[])obj);
	    return s;
	}
	@Override
	public String[] toIomArray(ch.interlis.ili2c.metamodel.AttributeDef attr,Object sqlArray,Class<? extends DbColumn> dbColHint) throws SQLException, ConverterException {
		java.sql.Array array=(java.sql.Array)sqlArray;
		String[] ret=null;
		ch.interlis.ili2c.metamodel.Type type=attr.getDomainResolvingAliases();
		if (attr.isDomainBoolean()) {
			Boolean values[]=(Boolean[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				if(values[i]){
					ret[i]="true";
				}else {
					ret[i]="false";
				}
			}
		}else if (attr.isDomainIli1Date()) {
			java.sql.Date values[]=(java.sql.Date[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyyMMdd");
				GregorianCalendar date=new GregorianCalendar();
				date.setGregorianChange(ToXtfRecordConverter.PURE_GREGORIAN_CALENDAR);
				fmt.setCalendar(date);
				ret[i]=fmt.format(values[i]);
			}
		}else if (attr.isDomainIliUuid()) {
			java.util.UUID values[]=(java.util.UUID[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				ret[i]=values[i].toString();
			}
		}else if (attr.isDomainIli2Date()) {
			java.sql.Date values[]=(java.sql.Date[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd");
				GregorianCalendar date=new GregorianCalendar();
				date.setGregorianChange(ToXtfRecordConverter.PURE_GREGORIAN_CALENDAR);
				fmt.setCalendar(date);
				ret[i]=fmt.format(values[i]);
			}
		}else if (attr.isDomainIli2DateTime()) {
			java.sql.Timestamp values[]=(java.sql.Timestamp[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // with timezone: yyyy-MM-dd'T'HH:mm:ss.SSSZ 
				GregorianCalendar date=new GregorianCalendar();
				date.setGregorianChange(ToXtfRecordConverter.PURE_GREGORIAN_CALENDAR);
				fmt.setCalendar(date);
				ret[i]=fmt.format(values[i]);
			}
		}else if (attr.isDomainIli2Time()) {
			java.sql.Time values[]=(java.sql.Time[])array.getArray();
			ret=new String[values.length];
			for(int i=0;i<values.length;i++) {
				java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("HH:mm:ss.SSS");
				ret[i]=fmt.format(values[i]);
			}
		}else if(type instanceof EnumerationType){
			if(DbColNumber.class.equals(dbColHint)){
				Long values[]=(Long[])array.getArray();
				ret=new String[values.length];
				for(int i=0;i<values.length;i++) {
					ret[i]=values[i].toString();
				}
			}else {
				ret=((String[])array.getArray());			}
		}else if(type instanceof NumericType){
			if(type.isAbstract()){
			}else{
				PrecisionDecimal min=((NumericType)type).getMinimum();
				PrecisionDecimal max=((NumericType)type).getMaximum();
				if(min.getAccuracy()>0){
					BigDecimal values[]=(BigDecimal[])array.getArray();
					ret=new String[values.length];
					for(int i=0;i<values.length;i++) {
						ret[i]=values[i].toString();
					}
				}else{
					Integer values[]=(Integer[])array.getArray();
					ret=new String[values.length];
					for(int i=0;i<values.length;i++) {
						ret[i]=values[i].toString();
					}
				}
			}
		}else if(type instanceof TextType){
			ret=((String[])array.getArray());			
		}else if(type instanceof BlackboxType){
			if(((BlackboxType)type).getKind()==BlackboxType.eXML){
				Object values[]=(Object[])array.getArray();
				ret=new String[values.length];
				for(int i=0;i<values.length;i++) {
					ret[i]=toIomXml(values[i]);
				}
			}else{
				Object values[]=(Object[])array.getArray();
				ret=new String[values.length];
				for(int i=0;i<values.length;i++) {
					ret[i]=toIomBlob(values[i]);
				}
			}
        }else if(type instanceof ReferenceType){
            if(DbColId.class.equals(dbColHint)){
                Long values[]=(Long[])array.getArray();
                ret=new String[values.length];
                for(int i=0;i<values.length;i++) {
                    ret[i]=values[i].toString();
                }
            }else {
                ret=((String[])array.getArray());           
            }
		}else{
			throw new IllegalArgumentException(attr.getScopedName());
		}
		
		return ret;
	}

		@Override
		public void setUuidNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.OTHER,"uuid");
		}
		@Override
		public void setXmlNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.SQLXML);
		}
		@Override
		public void setBlobNull(PreparedStatement stmt, int parameterIndex)
				throws SQLException {
			 stmt.setNull(parameterIndex, Types.BINARY);
		}
		@Override
		public void setArrayNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
			 stmt.setNull(parameterIndex, Types.ARRAY);
		}
        @Override
        public String getSelectValueWrapperJson(String sqlColName) {
            //return "from_jsonb("+sqlColName+")";
            return "cast("+sqlColName+" as text)";
        }
        @Override
        public String getInsertValueWrapperJson(String sqlColName) {
            //return "to_jsonb("+sqlColName+")";
            return "cast("+sqlColName+" as jsonb)";
        }
        @Override
        public void setJsonNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
            stmt.setNull(parameterIndex, Types.VARCHAR);
        }
        @Override
        public Object fromIomStructureToJson(AttributeDef iliEleAttr, IomObject[] iomObjects)
                throws SQLException, ConverterException {
            JsonFactory jsonF = new JsonFactory();
            java.io.StringWriter out=new java.io.StringWriter();
            JsonGenerator jg=null;
            try {
                if(jg==null) {
                    jg = jsonF.createJsonGenerator(out);
                    td=(TransferDescription) iliEleAttr.getContainer(TransferDescription.class);
                }
                Iox2jsonUtility.write(jg, iomObjects,td);
                jg.flush();
            } catch (IOException e) {
                throw new ConverterException(e);
            }
            return out.toString();
        }
        @Override
        public IomObject[] toIomStructureFromJson(AttributeDef iliEleAttr, Object sqlArray)
                throws SQLException, ConverterException {
            JsonFactory jsonF = new JsonFactory();
            java.io.StringReader in=new java.io.StringReader(sqlArray.toString());
            IomObject iomObj[]=null;
            try {
                JsonParser jg = jsonF.createJsonParser(in);
                
                iomObj=Iox2jsonUtility.read(jg);
            }catch(IOException ex) {
                throw new ConverterException(ex);
            }
            return iomObj;
        }

}
