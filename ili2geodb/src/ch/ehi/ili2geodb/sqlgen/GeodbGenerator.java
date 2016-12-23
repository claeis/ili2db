package ch.ehi.ili2geodb.sqlgen;


import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.IOException;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.Fields;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geodatabase.GeometryDef;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.geodatabase.esriFeatureType;
import com.esri.arcgis.geometry.ISpatialReferenceFactory;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.IProjection;
import com.esri.arcgis.system.ISet;

import ch.ehi.ili2geodb.jdbc.GeodbConnection;
import java.sql.Statement;
import java.sql.SQLException;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.sqlgen.generator.Generator;
import ch.ehi.sqlgen.generator.TextFileUtility;
import ch.ehi.sqlgen.generator.SqlConfiguration;
import ch.ehi.sqlgen.repository.*;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.Type;

/**
 * @author ce
 * @version $Revision: 1.1 $ $Date: 2006/05/30 07:40:33 $
 */
public class GeodbGenerator implements Generator {
	private Workspace wksp=null;
	private HashMap geodbDomains=null;
	private HashSet geodbLinks=null;
	private static final String PREFIX="ch.ehi.ili2geodb.sqlgen";
	public static final String GEODB_DOMAINS=PREFIX+".geodbDomains";
	public static final String GEODB_LINKS=PREFIX+".geodbLinks";
	public static final String DATASET_NAME=PREFIX+".datasetname";
	public static final String DOMAIN_NAME=PREFIX+".domainname";

	public GeodbGenerator(){
	}
	public void visitSchemaBegin(ch.ehi.basics.settings.Settings config, DbSchema schema)
		throws IOException {
		GeodbConnection conn=(GeodbConnection)config.getTransientObject(SqlConfiguration.JDBC_CONNECTION);
		if(conn==null){
			throw new IllegalArgumentException("config.getConnection()==null");
		}
		wksp=new Workspace(conn.getGeodbWorkspace());
		geodbDomains=(HashMap)config.getTransientObject(GEODB_DOMAINS);
		geodbLinks=(HashSet)config.getTransientObject(GEODB_LINKS);
	}

	public void visitSchemaEnd(DbSchema schema) throws IOException {
	}

	public void visit1Begin() throws IOException {
		Iterator domainNamei=geodbDomains.keySet().iterator();
		while(domainNamei.hasNext()){
			String domainName=(String)domainNamei.next();
			Object ilidomaino=geodbDomains.get(domainName);
			ch.interlis.ili2c.metamodel.Type type=null;
			if(ilidomaino instanceof ch.interlis.ili2c.metamodel.Domain){
				type=((ch.interlis.ili2c.metamodel.Domain)ilidomaino).getType();
			}else if(ilidomaino instanceof ch.interlis.ili2c.metamodel.AttributeDef){
				type=((ch.interlis.ili2c.metamodel.AttributeDef)ilidomaino).getDomain();
			}else{
				throw new IllegalArgumentException("domain!=AttributeDef && domain!=DomainDef");
			}
			if(type instanceof ch.interlis.ili2c.metamodel.EnumerationType){
				com.esri.arcgis.geodatabase.CodedValueDomain domain = new com.esri.arcgis.geodatabase.CodedValueDomain();
				domain.setFieldType(esriFieldType.esriFieldTypeInteger);

  	          java.util.ArrayList ev=new java.util.ArrayList();
	          buildEnumList(ev,"",((EnumerationType)type).getConsolidatedEnumeration());
	  		int itfCode=0;
	          Iterator iter=ev.iterator();
	          while(iter.hasNext()){
	            String value=(String)iter.next();
				domain.addCode(/*code*/itfCode, /*name*/value);
				itfCode++;
	          }
				
				domain.setName(domainName);
				domain.setSplitPolicy(com.esri.arcgis.geodatabase.esriSplitPolicyType.esriSPTDuplicate);
				domain.setMergePolicy(com.esri.arcgis.geodatabase.esriMergePolicyType.esriMPTAreaWeighted);
				wksp.addDomain(domain);
			}else if(type instanceof ch.interlis.ili2c.metamodel.NumericType){
				PrecisionDecimal min=((NumericType)type).getMinimum();
				PrecisionDecimal max=((NumericType)type).getMaximum();
				com.esri.arcgis.geodatabase.RangeDomain domain = new com.esri.arcgis.geodatabase.RangeDomain();
				if(min.getAccuracy()>0){
					// double
					domain.setMinValue(new Double(min.doubleValue()));
					domain.setMaxValue(new Double(max.doubleValue()));
					domain.setFieldType(esriFieldType.esriFieldTypeDouble);
				}else{
					// int
					domain.setMinValue(new Integer((int)min.doubleValue()));
					domain.setMaxValue(new Integer((int)max.doubleValue()));
					domain.setFieldType(esriFieldType.esriFieldTypeInteger);
				}
				domain.setName(domainName);
				domain.setSplitPolicy(com.esri.arcgis.geodatabase.esriSplitPolicyType.esriSPTDuplicate);
				domain.setMergePolicy(com.esri.arcgis.geodatabase.esriMergePolicyType.esriMPTAreaWeighted);
				wksp.addDomain(domain);
			}
		}
	}

	public void visit1End() throws IOException {
	}

	public void visit2Begin() throws IOException {
	}

	public void visit2End() throws IOException {
		Iterator linki=geodbLinks.iterator();
		while(linki.hasNext()){
			GeodbLink link=(GeodbLink)linki.next();
			com.esri.arcgis.geodatabase.IObjectClass originClass=new com.esri.arcgis.geodatabase.ObjectClass(wksp.openTable(link.getOriginClass()));
			com.esri.arcgis.geodatabase.IObjectClass destinationClass=new com.esri.arcgis.geodatabase.ObjectClass(wksp.openTable(link.getDestinationClass()));
			String dsName=link.getDs();
			com.esri.arcgis.geodatabase.IRelationshipClass relClass=wksp.createRelationshipClass(
						link.getRelClassName()
						,originClass
						,destinationClass
						,link.getForwardLabel()
						, link.getBackwardLabel()
						, link.getCardinality()
						, link.getNotification()
						, link.isComposite()
						, /*isAttributed*/false
						, /*relAttrFields*/null
						, link.getOriginPrimaryKey()
						, "" // destPrimaryKey
						, link.getOriginForeignKey()
						, "" // destForeignKey
						);
				
			//com.esri.arcgis.geodatabase.IDataset ds=wksp.openFeatureDataset(dsName);
		}
		
	}
	
	private Fields fieldv = null;
	private String geomFieldName=null;
	public void visit1TableBegin(DbTable tab) throws IOException {
		if(!tableExists(tab.getName().getName())){
			fieldv = new Fields();
		}else{
			fieldv=null;
		}
		geomFieldName=null;
	}

	public void visit1TableEnd(DbTable tab) throws IOException {
		if(fieldv!=null){
			String dsName=tab.getCustomValue(DATASET_NAME);
			if(dsName==null){
				if(geomFieldName==null){
					com.esri.arcgis.system.UID objectType= new com.esri.arcgis.system.UID();
					objectType.setValue("esriGeoDatabase.Object");
					wksp.createTable(tab.getName().getName(), fieldv, objectType, null, "");
				}else{
					wksp.createFeatureClass(tab.getName().getName(), fieldv, null, null,
							esriFeatureType.esriFTSimple, geomFieldName, "");
				}
			}else{
				if(geomFieldName==null){
					geomFieldName="dummy";
					Field field = new Field();
					GeometryDef geomDef = new GeometryDef();
					geomDef.setGeometryType(esriGeometryType.esriGeometryPoint);
					//geomDef.setSpatialReferenceByRef(crs);
					field.setType(esriFieldType.esriFieldTypeGeometry);
					field.setGeometryDefByRef(geomDef);
					field.setIsNullable(true);
					field.setName(geomFieldName);
					fieldv.addField(field);
				}
				com.esri.arcgis.geodatabase.IFeatureDataset ds=null;
				if(datasetExists(dsName)){
					ds=wksp.openFeatureDataset(dsName);
				}else{
			    	SpatialReferenceEnvironment spatialReferenceFactory = new SpatialReferenceEnvironment();
			    	com.esri.arcgis.geometry.IProjectedCoordinateSystem crs = spatialReferenceFactory.createProjectedCoordinateSystem(com.esri.arcgis.geometry.esriSRProjCS2Type.esriSRProjCS_CH1903_LV03);
					crs.setDomain(480000.0, 850000.0,60000.0,  320000.0); // min/max
					ds=wksp.createFeatureDataset(dsName,crs);
				}
				ds.createFeatureClass(tab.getName().getName(), fieldv, null, null,
						esriFeatureType.esriFTSimple, geomFieldName, "");
			}
		}
	}

	public void visit2TableBegin(DbTable arg0) throws IOException {
	}
	public void visit2TableEnd(DbTable arg0) throws IOException {
	}
	public void visitColumn(DbTable dbTab,DbColumn column) throws IOException {
		// skip this table?
		if(fieldv==null){
			// ignore cols
			return;
		}
		//EhiLogger.debug("col "+column.getName());
		boolean isOid=false;
		Field field = new Field();
		if(column instanceof DbColBoolean){
			field.setType(esriFieldType.esriFieldTypeSmallInteger);
		}else if(column instanceof DbColDateTime){
			field.setType(esriFieldType.esriFieldTypeDate);
		}else if(column instanceof DbColDecimal){
			DbColDecimal col=(DbColDecimal)column;
			field.setType(esriFieldType.esriFieldTypeDouble);
			field.setLength(col.getSize());
			field.setPrecision(col.getPrecision());
		}else if(column instanceof DbColGeometry){
			DbColGeometry col=(DbColGeometry)column;
			if(geomFieldName==null){
		    	SpatialReferenceEnvironment spatialReferenceFactory = new SpatialReferenceEnvironment();
		    	com.esri.arcgis.geometry.IProjectedCoordinateSystem crs = spatialReferenceFactory.createProjectedCoordinateSystem(com.esri.arcgis.geometry.esriSRProjCS2Type.esriSRProjCS_CH1903_LV03);
				crs.setDomain(col.getMin1(), col.getMin2(), col.getMax1(), col.getMax2()); // min/max
				GeometryDef geomDef = new GeometryDef();
				if(col.getType()==DbColGeometry.POINT){
					geomDef.setGeometryType(esriGeometryType.esriGeometryPoint);
				}else if(col.getType()==DbColGeometry.COMPOUNDCURVE || col.getType()==DbColGeometry.CIRCULARSTRING || col.getType()==DbColGeometry.LINESTRING){
					geomDef.setGeometryType(esriGeometryType.esriGeometryPolyline);
				}else if(col.getType()==DbColGeometry.CURVEPOLYGON || col.getType()==DbColGeometry.POLYGON){
					geomDef.setGeometryType(esriGeometryType.esriGeometryPolygon);
				}
				geomDef.setSpatialReferenceByRef(crs);
				field.setType(esriFieldType.esriFieldTypeGeometry);
				field.setGeometryDefByRef(geomDef);
				geomFieldName=col.getName();
			}
		}else if(column instanceof DbColId){
			DbColId col=(DbColId)column;
			if(col.isPrimaryKey()){
				field.setType(esriFieldType.esriFieldTypeOID);
				isOid=true;
			}else{
				field.setType(esriFieldType.esriFieldTypeInteger);
			}
		}else if(column instanceof DbColNumber){
			DbColNumber col=(DbColNumber)column;
			field.setType(esriFieldType.esriFieldTypeInteger);
			field.setLength(col.getSize());
		}else if(column instanceof DbColVarchar){
			int colsize=((DbColVarchar)column).getSize();
			field.setLength(colsize);
			field.setType(esriFieldType.esriFieldTypeString);
		}else{
			field.setLength(20);
			field.setType(esriFieldType.esriFieldTypeString);
		}
		if(!isOid){
			field.setIsNullable(!column.isNotNull());
		}
		field.setName(column.getName());
		String domainName=column.getCustomValue(DOMAIN_NAME);
		if(domainName!=null){
			field.setDomainByRef(wksp.getDomainByName(domainName));
		}
		fieldv.addField(field);
	}
	public void visitTableBeginColumn(DbTable arg0) throws IOException {
	}
	public void visitTableEndColumn(DbTable arg0) throws IOException {
	}
	public void visitIndex(DbIndex arg0) throws IOException {
	}
	public void visitTableBeginIndex(DbTable arg0) throws IOException {
	}
	public void visitTableEndIndex(DbTable arg0) throws IOException {
	}
	public void visitConstraint(DbConstraint arg0) throws IOException {
	}
	public void visitTableBeginConstraint(DbTable arg0) throws IOException {
	}
	public void visitTableEndConstraint(DbTable arg0) throws IOException {
	}
	public void visitEnumEle(DbEnumEle arg0) throws IOException {
	}
	public void visitTableBeginEnumEle(DbTable arg0) throws IOException {
	}
	public void visitTableEndEnumEle(DbTable arg0) throws IOException {
	}
	// utilities
	/** tests if a table with the given name exists
	 */
	public boolean tableExists(String tableName)
	throws IOException
	{
		EhiLogger.debug("tabname <"+tableName+">");
		if (wksp.isNameExists(com.esri.arcgis.geodatabase.esriDatasetType.esriDTFeatureClass, tableName)){
			return true;
		}
		if (wksp.isNameExists(com.esri.arcgis.geodatabase.esriDatasetType.esriDTTable, tableName)){
			return true;
		}
		if (wksp.isNameExists(com.esri.arcgis.geodatabase.esriDatasetType.esriDTRelationshipClass, tableName)){
			return true;
		}

		return false;
	}
	/** tests if a dataset with the given name exists
	 */
	public boolean datasetExists(String tableName)
	throws IOException
	{
		EhiLogger.debug("dataset <"+tableName+">");
		if (wksp.isNameExists(com.esri.arcgis.geodatabase.esriDatasetType.esriDTFeatureDataset, tableName)){
			return true;
		}

		return false;
	}
	public static boolean supportsGeodbDomain(Type iliType) {
		if(iliType instanceof ch.interlis.ili2c.metamodel.NumericType){
			if(((ch.interlis.ili2c.metamodel.NumericType)iliType).isAbstract()){
				return false;
			}
			return true;
		}else if(iliType instanceof ch.interlis.ili2c.metamodel.EnumerationType){
			return true;
		}
		return false;
	}
	  private void buildEnumList(java.util.List accu,String prefix1,ch.interlis.ili2c.metamodel.Enumeration enumer){
	      Iterator iter = enumer.getElements();
	      String prefix="";
	      if(prefix1.length()>0){
	        prefix=prefix1+".";
	      }
	      while (iter.hasNext()) {
	        ch.interlis.ili2c.metamodel.Enumeration.Element ee=(ch.interlis.ili2c.metamodel.Enumeration.Element) iter.next();
	        ch.interlis.ili2c.metamodel.Enumeration subEnum = ee.getSubEnumeration();
	        if (subEnum != null)
	        {
	          // ee is not leaf, add its name to prefix and add sub elements to accu
	          buildEnumList(accu,prefix+ee.getName(),subEnum);
	        }else{
	          // ee is a leaf, add it to accu
	          accu.add(prefix+ee.getName());
	        }
	      }
	  }

}
