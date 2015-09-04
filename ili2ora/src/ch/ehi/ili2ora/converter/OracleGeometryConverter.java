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
package ch.ehi.ili2ora.converter;

import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.Iom_jObject;

import ch.interlis.iom.IomConstants;
import java.util.ArrayList;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.converter.ConverterException;
import ch.ehi.ili2db.converter.SqlGeometryConverter;
import ch.ehi.ili2db.gui.Config;
import oracle.spatial.geometry.JGeometry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 10.02.2007 $
 */
public class OracleGeometryConverter implements SqlGeometryConverter {
	public void setAreaNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
		stmt.setNull(parameterIndex,java.sql.Types.STRUCT);
	}
	public void setCoordNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
		stmt.setNull(parameterIndex,java.sql.Types.STRUCT);
		
	}
	public void setDecimalNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
		stmt.setNull(parameterIndex,java.sql.Types.DECIMAL);
	}
	public void setPolylineNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
		stmt.setNull(parameterIndex,java.sql.Types.STRUCT);
	}
	public void setSurfaceNull(PreparedStatement stmt, int parameterIndex) throws SQLException {
		stmt.setNull(parameterIndex,java.sql.Types.STRUCT);
	}
	public void setBoolean(java.sql.PreparedStatement stmt,int parameterIndex,boolean value)
	throws java.sql.SQLException
	{
		if(value==true){
			stmt.setString(parameterIndex, "1");
		}else{
			stmt.setString(parameterIndex, "0");
		}
		
	}
	/** helper to build SDO input
	 */
	private void addElemInfo(ArrayList elemInfo,int sdoStartingOffset,int sdoEtype,int sdoInterpretation){
		elemInfo.add(new Integer(sdoStartingOffset+1));
		elemInfo.add(new Integer(sdoEtype));
		elemInfo.add(new Integer(sdoInterpretation));
	}
	public java.lang.Object fromIomSurface(IomObject obj,int srid,boolean hasLineAttr,boolean is3D,double p)
		throws java.sql.SQLException,ConverterException
	{
		is3D=false;
		if(obj!=null){
			ArrayList elemInfo=new ArrayList();
			ArrayList ordinates=new ArrayList();
			final int EXTERIOR_POLYGON_RING=1005; // (must be specified in counterclockwise order)
			final int INTERIOR_POLYGON_RING=2005; // (must be specified in clockwise order)


			boolean clipped=obj.getobjectconsistency()==IomConstants.IOM_INCOMPLETE;
			for(int surfacei=0;surfacei<obj.getattrvaluecount("surface");surfacei++){
				if(clipped){
					//out.startElement("CLIPPED",0,0);
				}else{
					// an unclipped surface should have only one surface element
					if(surfacei>0){
						EhiLogger.logError("unclipped surface with multi 'surface' elements");
						break;
					}
				}
				IomObject surface=obj.getattrobj("surface",surfacei);
				for(int boundaryi=0;boundaryi<surface.getattrvaluecount("boundary");boundaryi++){
					int eleInfoStartPos=elemInfo.size();
					if(boundaryi==0){
						addElemInfo(elemInfo,ordinates.size(),EXTERIOR_POLYGON_RING,0);
					}else{
						addElemInfo(elemInfo,ordinates.size(),INTERIOR_POLYGON_RING,0);
					}
					IomObject boundary=surface.getattrobj("boundary",boundaryi);
					for(int polylinei=0;polylinei<boundary.getattrvaluecount("polyline");polylinei++){
						IomObject polyline=boundary.getattrobj("polyline",polylinei);
						addPolyline(elemInfo,ordinates,polyline,true,is3D);
					}
					// patch counter in head triplet
					int elemInfoCount=(elemInfo.size()-eleInfoStartPos)/3-1;
					elemInfo.set(eleInfoStartPos+2,new Integer(elemInfoCount));
				}
				if(clipped){
					//out.endElement(/*CLIPPED*/);
				}
			}
			int elemInfov[]=new int[elemInfo.size()];
			for(int i=0;i<elemInfov.length;i++){
				elemInfov[i]=((Integer)elemInfo.get(i)).intValue();
			}
			double ordinatesv[]=new double[ordinates.size()];
			for(int i=0;i<ordinatesv.length;i++){
				ordinatesv[i]=((Double)ordinates.get(i)).doubleValue();
			}
			JGeometry geom=new JGeometry(JGeometry.GTYPE_POLYGON,0,elemInfov,ordinatesv);
			return JGeometry.store(geom,conn);
		}
		return null;
	}
	public java.lang.Object fromIomCoord(IomObject value,int srid,boolean is3D)
		throws java.sql.SQLException,ConverterException
	{
		if(value!=null){
			
			String c1=value.getattrvalue("C1");
			String c2=value.getattrvalue("C2");
			String c3=value.getattrvalue("C3");
			double ordArray[]={0,0,0};
			ordArray[0] = Double.parseDouble(c1);
			ordArray[1] = Double.parseDouble(c2);
			JGeometry geom=null;
			if(is3D){
				if(c3==null){
					throw new ConverterException("unexpected dimension");
				}else{
					ordArray[2] = Double.parseDouble(c3);
					geom = JGeometry.createPoint(ordArray, 3,0);
				}
			}else{
				geom = JGeometry.createPoint(ordArray, 2,0);
			}
			return JGeometry.store(geom,conn);
		}
		return null;
	}
	public java.lang.Object fromIomPolyline(IomObject obj,int srid,boolean is3D,double p)
		throws java.sql.SQLException,ConverterException
	{
		is3D=false;
		if(obj!=null){
			ArrayList elemInfo=new ArrayList();
			ArrayList ordinates=new ArrayList();
			final int COMPOUND_LINESTRING=4;
			int eleInfoStartPos=elemInfo.size();
			addElemInfo(elemInfo,ordinates.size(),COMPOUND_LINESTRING,0);
			
			addPolyline(elemInfo,ordinates,obj,false,is3D);
			
			// patch counter in head triplet
			int elemInfoCount=(elemInfo.size()-eleInfoStartPos)/3-1;
			elemInfo.set(eleInfoStartPos+2,new Integer(elemInfoCount));
			int elemInfov[]=new int[elemInfo.size()];
			for(int i=0;i<elemInfov.length;i++){
				elemInfov[i]=((Integer)elemInfo.get(i)).intValue();
			}
			double ordinatesv[]=new double[ordinates.size()];
			for(int i=0;i<ordinatesv.length;i++){
				ordinatesv[i]=((Double)ordinates.get(i)).doubleValue();
			}
			JGeometry geom=new JGeometry(JGeometry.GTYPE_MULTICURVE,0,elemInfov,ordinatesv);
			return JGeometry.store(geom,conn);
		}
		return null;
	}
	private void addPolyline(
		ArrayList elemInfo
		,ArrayList ordinates
		,IomObject obj
		,boolean hasLineAttr
		,boolean is3D
		)
		throws java.sql.SQLException,ConverterException
	{
		is3D=false;
		if(obj!=null){
			final int LINESTRING=2;
			final int STRAIGHT=1;
			final int ARC=2;
			int lastSegment=0;
			if(hasLineAttr){
				IomObject lineattr=obj.getattrobj("lineattr",0);
				if(lineattr!=null){
					//writeAttrs(out,lineattr);
					EhiLogger.logAdaption("Lineattributes not supported by Oracle; ignored");							
				}
			}
			boolean clipped=obj.getobjectconsistency()==IomConstants.IOM_INCOMPLETE;
			for(int sequencei=0;sequencei<obj.getattrvaluecount("sequence");sequencei++){
				if(clipped){
					//out.startElement(tags::get_CLIPPED(),0,0);
				}else{
					// an unclipped polyline should have only one sequence element
					if(sequencei>0){
						EhiLogger.logError("unclipped polyline with multi 'sequence' elements");
						break;
					}
				}
				IomObject sequence=obj.getattrobj("sequence",sequencei);
				for(int segmenti=0;segmenti<sequence.getattrvaluecount("segment");segmenti++){
					IomObject segment=sequence.getattrobj("segment",segmenti);
					if(segment.getobjecttag().equals("COORD")){
						// COORD
						if(lastSegment!=STRAIGHT){
							addElemInfo(elemInfo,ordinates.size(),LINESTRING,STRAIGHT);
						}
						String c1=segment.getattrvalue("C1");
						String c2=segment.getattrvalue("C2");
						String c3=segment.getattrvalue("C3");
						ordinates.add(Double.valueOf(c1));
						ordinates.add(Double.valueOf(c2));
						if(is3D){
							ordinates.add(Double.valueOf(c3));
						}

					}else if(segment.getobjecttag().equals("ARC")){
						// ARC
						if(lastSegment!=ARC){
							addElemInfo(elemInfo,ordinates.size(),LINESTRING,ARC);
						}
						String c1=segment.getattrvalue("C1");
						String c2=segment.getattrvalue("C2");
						String c3=segment.getattrvalue("C3");
						String a1=segment.getattrvalue("A1");
						String a2=segment.getattrvalue("A2");
						ordinates.add(Double.valueOf(a1));
						ordinates.add(Double.valueOf(a2));
						if(is3D){
							// TODO calculate a3
							throw new IllegalStateException("TODO: calculate a3");
							//ordinates.add(Double.valueOf(a3));
						}
						ordinates.add(Double.valueOf(c1));
						ordinates.add(Double.valueOf(c2));
						if(is3D){
							ordinates.add(Double.valueOf(c3));
						}
					}else{
						// custum line form
						EhiLogger.logAdaption("custom line form not supported by Oracle; ignored");
						//out.startElement(segment->getTag(),0,0);
						//writeAttrs(out,segment);
						//out.endElement(/*segment*/);
					}

				}
				if(clipped){
					//out.endElement(/*CLIPPED*/);
				}
			}
		}
	}
	public IomObject toIomCoord(Object geomobj,String sqlAttrName,boolean is3D)
		throws java.sql.SQLException,ConverterException
	{
		JGeometry geometry = JGeometry.load((oracle.sql.STRUCT)geomobj);
		if(geometry.getType()!=JGeometry.GTYPE_POINT){
			throw new ConverterException("unexpected GTYPE ("+OracleUtility.gtype2str(geometry.getType())+")");
		}else{
			int dim=geometry.getDimensions();
			boolean isCurrentValue3D=(dim==3);
			if(isCurrentValue3D!=is3D){
				throw new ConverterException("unexpected dimension ("+Integer.toString(dim)+")");
			}else{
				double[] valuev=geometry.getFirstPoint();
				IomObject coord=new Iom_jObject("COORD",null);
				coord.setattrvalue("C1",Double.toString(valuev[0]));
				coord.setattrvalue("C2",Double.toString(valuev[1]));
				if(dim==3){
					coord.setattrvalue("C3",Double.toString(valuev[2]));
				}
				return coord;
			}
									
		}
	}
	public IomObject toIomSurface(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws java.sql.SQLException,ConverterException {
		JGeometry geometry = JGeometry.load((oracle.sql.STRUCT)geomobj);
		if(geometry.getType()!=JGeometry.GTYPE_POLYGON){
			throw new ConverterException("unexpected GTYPE ("+OracleUtility.gtype2str(geometry.getType())+") in attribute "+sqlAttrName);
		}else{
			int dim=geometry.getDimensions();
			boolean isCurrentValue3D=(dim==3);
			if(is3D!=isCurrentValue3D){
				throw new ConverterException("unexpected dimension ("+Integer.toString(dim)+") in attribute "+sqlAttrName);
			}else{
				int elev[]=geometry.getElemInfo();
				double ordv[]=geometry.getOrdinatesArray();
				final int SDO_STARTING_OFFSET=0;
				final int SDO_ETYPE=1;
				final int SDO_INTERPRETATION=2;
				final int NEXT_TRIPLET=3;
				final int EXTERIOR_POLYGON_RING=1005; // (must be specified in counterclockwise order)
				final int INTERIOR_POLYGON_RING=2005; // (must be specified in clockwise order)
				final int LINESTRING=2;
				final int STRAIGHT=1;
				final int ARC=2;
				int elei=0;
				IomObject multisurface=new Iom_jObject("MULTISURFACE",null);
				IomObject surface=multisurface.addattrobj("surface","SURFACE");
				while(elei<elev.length){
					if(elev[elei+SDO_ETYPE]!=EXTERIOR_POLYGON_RING && elev[elei+SDO_ETYPE]!=INTERIOR_POLYGON_RING){
						throw new ConverterException("unexpected SDO_ETYPE ("+Integer.toString(elev[elei+SDO_ETYPE])+") in attribute "+sqlAttrName);
					}else{
						int nTriplet=elev[elei+SDO_INTERPRETATION];
						elei+=NEXT_TRIPLET;
						
						IomObject boundary=surface.addattrobj("boundary","BOUNDARY");
						IomObject polylineValue=boundary.addattrobj("polyline","POLYLINE");
						IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
						for(int iTriplet=0;iTriplet<nTriplet;iTriplet++){
							if(elev[elei+SDO_ETYPE]!=LINESTRING){
								throw new ConverterException("unexpected SDO_ETYPE ("+Integer.toString(elev[elei+SDO_ETYPE])+") in attribute "+sqlAttrName);
							}else{
								if(elev[elei+SDO_INTERPRETATION]==STRAIGHT){
									int start=elev[elei+SDO_STARTING_OFFSET]-1;
									int end;
									if(elei+NEXT_TRIPLET>=elev.length){
										end=ordv.length;
									}else{
										end=elev[elei+NEXT_TRIPLET+SDO_STARTING_OFFSET]-1;
									}
									for(int i=start;i<end;){
										// add control point
										IomObject coordValue=sequence.addattrobj("segment","COORD");
										coordValue.setattrvalue("C1",Double.toString(ordv[i]));
										coordValue.setattrvalue("C2",Double.toString(ordv[i+1]));
										if(isCurrentValue3D){
											coordValue.setattrvalue("C3",Double.toString(ordv[i+2]));
											i+=3;
										}else{
											i+=2;
										}
									}
								}else if(elev[elei+SDO_INTERPRETATION]==ARC){
									int start=elev[elei+SDO_STARTING_OFFSET]-1;
									int end;
									if(elei+NEXT_TRIPLET>=elev.length){
										end=ordv.length;
									}else{
										end=elev[elei+NEXT_TRIPLET+SDO_STARTING_OFFSET]-1;
									}
									for(int i=start;i<end;){
										// add control point
										IomObject coordValue=sequence.addattrobj("segment","ARC");
										coordValue.setattrvalue("A1",Double.toString(ordv[i]));
										coordValue.setattrvalue("A2",Double.toString(ordv[i+1]));
										if(isCurrentValue3D){
											// no A3 in XTF!
											i+=3;
										}else{
											i+=2;
										}
										coordValue.setattrvalue("C1",Double.toString(ordv[i]));
										coordValue.setattrvalue("C2",Double.toString(ordv[i+1]));
										if(isCurrentValue3D){
											coordValue.setattrvalue("C3",Double.toString(ordv[i+2]));
											i+=3;
										}else{
											i+=2;
										}
									}
								}else{
									throw new ConverterException("unexpected SDO_INTERPRETATION ("+Integer.toString(elev[elei+SDO_INTERPRETATION])+") in attribute "+sqlAttrName);
								}
							}
							elei+=NEXT_TRIPLET;
						}
					}
				}
				return multisurface;
			}
			
		}
	}
	public IomObject toIomPolyline(
		Object geomobj,
		String sqlAttrName,
		boolean is3D)
		throws java.sql.SQLException,ConverterException {
		JGeometry geometry = JGeometry.load((oracle.sql.STRUCT)geomobj);
		if(geometry.getType()!=JGeometry.GTYPE_MULTICURVE){
			throw new ConverterException("unexpected GTYPE ("+OracleUtility.gtype2str(geometry.getType())+") in attribute "+sqlAttrName);
		}else{
			int dim=geometry.getDimensions();
			boolean isCurrentValue3D=(dim==3);
			if(is3D!=isCurrentValue3D){
				throw new ConverterException("unexpected dimension ("+Integer.toString(dim)+") in attribute "+sqlAttrName);
			}else{
				int elev[]=geometry.getElemInfo();
				double ordv[]=geometry.getOrdinatesArray();
				final int SDO_STARTING_OFFSET=0;
				final int SDO_ETYPE=1;
				final int SDO_INTERPRETATION=2;
				final int NEXT_TRIPLET=3;
				final int COMPOUND_LINESTRING=4;
				final int LINESTRING=2;
				final int STRAIGHT=1;
				final int ARC=2;
				int elei=0;
				if(elev[elei+SDO_ETYPE]!=COMPOUND_LINESTRING){
					throw new ConverterException("unexpected SDO_ETYPE ("+Integer.toString(elev[elei+SDO_ETYPE])+") in attribute "+sqlAttrName);
				}else{
					elei+=NEXT_TRIPLET;
					IomObject polylineValue=new Iom_jObject("POLYLINE",null);
					// unclipped polyline, add one sequence
					IomObject sequence=polylineValue.addattrobj("sequence","SEGMENTS");
					while(elei<elev.length){
						if(elev[elei+SDO_ETYPE]!=LINESTRING){
							throw new ConverterException("unexpected SDO_ETYPE ("+Integer.toString(elev[elei+SDO_ETYPE])+") in attribute "+sqlAttrName);
						}else{
							if(elev[elei+SDO_INTERPRETATION]==STRAIGHT){
								int start=elev[elei+SDO_STARTING_OFFSET]-1;
								int end;
								if(elei+NEXT_TRIPLET>=elev.length){
									end=ordv.length;
								}else{
									end=elev[elei+NEXT_TRIPLET+SDO_STARTING_OFFSET]-1;
								}
								for(int i=start;i<end;){
									// add control point
									IomObject coordValue=sequence.addattrobj("segment","COORD");
									coordValue.setattrvalue("C1",Double.toString(ordv[i]));
									coordValue.setattrvalue("C2",Double.toString(ordv[i+1]));
									if(isCurrentValue3D){
										coordValue.setattrvalue("C3",Double.toString(ordv[i+2]));
										i+=3;
									}else{
										i+=2;
									}
								}
							}else if(elev[elei+SDO_INTERPRETATION]==ARC){
								int start=elev[elei+SDO_STARTING_OFFSET]-1;
								int end;
								if(elei+NEXT_TRIPLET>=elev.length){
									end=ordv.length;
								}else{
									end=elev[elei+NEXT_TRIPLET+SDO_STARTING_OFFSET]-1;
								}
								for(int i=start;i<end;){
									// add control point
									IomObject coordValue=sequence.addattrobj("segment","ARC");
									coordValue.setattrvalue("A1",Double.toString(ordv[i]));
									coordValue.setattrvalue("A2",Double.toString(ordv[i+1]));
									if(isCurrentValue3D){
										// no A3 in XTF!
										i+=3;
									}else{
										i+=2;
									}
									coordValue.setattrvalue("C1",Double.toString(ordv[i]));
									coordValue.setattrvalue("C2",Double.toString(ordv[i+1]));
									if(isCurrentValue3D){
										coordValue.setattrvalue("C3",Double.toString(ordv[i+2]));
										i+=3;
									}else{
										i+=2;
									}
								}
							}else{
								throw new ConverterException("unexpected SDO_INTERPRETATION ("+Integer.toString(elev[elei+SDO_INTERPRETATION])+") in attribute "+sqlAttrName);
							}
						}
						elei+=NEXT_TRIPLET;
					}
					return polylineValue;
				}
			}
			
		}
	}
	public int getSrsid(String crsAuthority, String crsCode,Connection conn) 
	throws ConverterException
	{
		int srsid=0;
		try{
			java.sql.Statement stmt=conn.createStatement();
			String qryStmt=null;
			if(crsAuthority==null){
				qryStmt="SELECT srid FROM MDSYS.CS_SRS WHERE AUTH_NAME IS NULL AND AUTH_SRID="+crsCode;
			}else{
				qryStmt="SELECT srid FROM MDSYS.CS_SRS WHERE AUTH_NAME=\'"+crsAuthority+"\' AND AUTH_SRID="+crsCode;
			}
			java.sql.ResultSet ret=stmt.executeQuery(qryStmt);
			ret.next();
			srsid=ret.getInt("srid");
		}catch(java.sql.SQLException ex){
			throw new ConverterException("failed to query srsid from database",ex);
		}
		return srsid;
	}
	public String getInsertValueWrapperCoord(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getInsertValueWrapperPolyline(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getInsertValueWrapperSurface(String wkfValue, int srid) {
		return wkfValue;
	}
	public String getSelectValueWrapperCoord(String dbNativeValue) {
		return dbNativeValue;
	}
	public String getSelectValueWrapperPolyline(String dbNativeValue) {
		return dbNativeValue;
	}
	public String getSelectValueWrapperSurface(String dbNativeValue) {
		return dbNativeValue;
	}
	private Connection conn=null;
	public void setup(Connection conn, Config config) {
		this.conn=conn;
	}

}
