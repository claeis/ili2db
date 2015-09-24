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
package ch.ehi.ili2db.fromili;

import ch.ehi.basics.logging.EhiLogger;
import ch.interlis.ili2c.ModelScan;
import ch.interlis.ili2c.metamodel.*;
import ch.interlis.ilirepository.IliFiles;
import ch.interlis.iom_j.itf.ModelUtilities;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import ch.ehi.sqlgen.repository.*;
import ch.ehi.ili2db.base.DbUtility;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.Mapping;

/**
 * @author ce
 * @version $Revision: 1.0 $ $Date: 07.02.2005 $
 */
public class TransferFromIli {
	public static final String T_SEQ = "T_Seq";
	public static final String T_PARENT_ATTR = "T_ParentAttr";
	public static final String T_PARENT_TYPE = "T_ParentType";
	public static final String T_PARENT_ID = "T_ParentId";
	public static final String T_ID = "T_Id";
	public static final String T_BASKET="T_basket";
	public static final String T_ILI_TID = "T_Ili_Tid";
	public static final String T_TYPE = "T_Type";
	public static final String T_USER = "T_User";
	public static final String T_CREATE_DATE = "T_CreateDate";
	public static final String T_LAST_CHANGE = "T_LastChange";
	public static final String ENUM_TXT_COL_SUFFIX="_txt";
	public static final String ITF_MAINTABLE_GEOTABLEREF_SUFFIX="_ref";
	public static final String ITF_LINETABLE_MAINTABLEREF="_ref";
	public static final String ITF_LINETABLE_GEOMATTR="_geom";
	private DbSchema schema=null;
	private HashSet visitedElements=null;
	private HashSet visitedEnums=null;
	private TransferDescription td=null;
	private ch.ehi.ili2db.mapping.Mapping ili2sqlName=null;
	private String defaultCrsAuthority=null;
	private String defaultSrcCode=null;
	private String createEnumTable=null;
	private boolean createStdCols=false;
	private boolean createEnumTxtCol=false;
	private boolean createEnumColAsItfCode=false;
	private boolean createIliTidCol=false;
	private boolean createTypeDiscriminator=false;
	private boolean createGenericStructRef=false;
	private boolean sqlEnableNull=true;
	private boolean strokeArcs=true;
	private boolean createBasketCol=false;
	private CustomMapping customMapping=null;
	private boolean createItfLineTables=false;
	private boolean createItfAreaRef=false;
	private boolean createFk=false;
	private boolean isIli1Model=false;
	private boolean deleteExistingData=false;
	private String colT_ID=null;
	private String nl=System.getProperty("line.separator");

	public DbSchema doit(TransferDescription td1,java.util.List<Element> modelEles,ch.ehi.ili2db.mapping.Mapping ili2sqlName,ch.ehi.ili2db.gui.Config config)
	throws Ili2dbException
	{
		this.defaultCrsAuthority=config.getDefaultSrsAuthority();
		this.defaultSrcCode=config.getDefaultSrsCode();
		this.ili2sqlName=ili2sqlName;
		createEnumTable=config.getCreateEnumDefs();
		createEnumColAsItfCode=config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getCreateEnumColAsItfCode());
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createEnumTxtCol=config.CREATE_ENUM_TXT_COL.equals(config.getCreateEnumCols());
		createFk=config.CREATE_FK_YES.equals(config.getCreateFk());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=T_ID;
		}
		deleteExistingData=config.DELETE_DATA.equals(config.getDeleteMode());
		
		createTypeDiscriminator=config.CREATE_TYPE_DISCRIMINATOR_ALWAYS.equals(config.getCreateTypeDiscriminator());
		createGenericStructRef=config.STRUCT_MAPPING_GENERICREF.equals(config.getStructMapping());
		sqlEnableNull=config.SQL_NULL_ENABLE.equals(config.getSqlNull());
		strokeArcs=config.STROKE_ARCS_ENABLE.equals(config.getStrokeArcs());
		createIliTidCol=config.TID_HANDLING_PROPERTY.equals(config.getTidHandling());
		createBasketCol=config.BASKET_HANDLING_READWRITE.equals(config.getBasketHandling());
		
		isIli1Model=td1.getIli1Format()!=null;
		createItfLineTables=isIli1Model && config.getDoItfLineTables();
		createItfAreaRef=isIli1Model && config.AREA_REF_KEEP.equals(config.getAreaRef());
		
		customMapping=getCustomMappingStrategy(config);
		customMapping.init(config);

		schema=new DbSchema();
		schema.setName(config.getDbschema());
		visitedElements=new HashSet();
		visitedEnums=new HashSet();
		td=td1;
		Iterator modeli=modelEles.iterator();
		while(modeli.hasNext()){
			Object modelo=modeli.next();
			if(modelo instanceof Model){
				Model model=(Model)modelo;
				//generateModel(model);
			}else if (modelo instanceof Topic){
				//generateTopic((Topic)modelo);
			}else if (modelo instanceof Domain){
				generateDomain((Domain)modelo);
			}else if (modelo instanceof Viewable){
				if(modelo instanceof Table && ((Table)modelo).isIli1LineAttrStruct()){
					// skip it
				}else{
					try{
						generateViewable((Viewable)modelo);
					}catch(Ili2dbException ex){
						throw new Ili2dbException("mapping of "+((Viewable)modelo).getScopedName(null)+" failed",ex);
					}
				}
			}else if (modelo instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)modelo;
				if(attr.getDomainResolvingAll() instanceof SurfaceOrAreaType){
					generateItfLineTable(attr);
				}else if(attr.getDomainResolvingAll() instanceof EnumerationType){
					visitedEnums.add(attr);
				}else{
					// skip it
				}
			}else{
				// skip it
			}
		}
		// sys_interlisnames
		// interlis LONGVARCHAR(767)
		// db VARCHAR(30)
		
		customMapping.end(config);
		return schema;		

	}
	private CustomMapping getCustomMappingStrategy(ch.ehi.ili2db.gui.Config config)
	throws Ili2dbException
	{
		String mappingClassName=config.getIli2dbCustomStrategy();
		if(mappingClassName==null){
			return new CustomMappingNull();
		}
		CustomMapping mapping=null;
		try{
			mapping=(CustomMapping)Class.forName(mappingClassName).newInstance();
		}catch(Exception ex){
			throw new Ili2dbException("failed to load/create custom mapping strategy",ex);
		}
		return mapping;
	}

	private void generateDomain(Domain def)
	throws Ili2dbException
	{
		if(def.getType() instanceof EnumerationType){
			visitedEnums.add(def);
		}
	}
	private void generateViewable(Viewable def)
	throws Ili2dbException
	{
		if(def instanceof AssociationDef){
			AssociationDef assoc=(AssociationDef)def;
			if(assoc.getDerivedFrom()!=null){
				return;
			}
			if(assoc.isLightweight() 
				&& !assoc.getAttributes().hasNext()
				&& !assoc.getLightweightAssociations().iterator().hasNext()) {
				customMapping.fixupViewable(null,def);
				return;
			}
		}
		//EhiLogger.debug("viewable "+def);
		DbTableName sqlName=getSqlTableName(def);
		Viewable base=(Viewable)def.getExtending();
		DbTable dbTable=new DbTable();
		dbTable.setName(sqlName);
		dbTable.setIliName(def.getScopedName(null));
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(def.getDocumentation()!=null){
			cmt.append(cmtSep+def.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+def.getScopedName(null));
		cmtSep=nl;
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
		}
		
		if(deleteExistingData){
			dbTable.setDeleteDataIfTableExists(true);
		}
		if(base==null){
		  dbTable.setRequiresSequence(true);
		}
		String baseRef="";
		DbColId dbColId=addKeyCol(dbTable);
		if(base!=null){
		  dbColId.setScriptComment("REFERENCES "+base.getScopedName(null));
		  if(createFk){
			  dbColId.setReferencedTable(getSqlTableName(base));
		  }
		}
		  if(createBasketCol){
			  // add basketCol
				DbColId t_basket=new DbColId();
				t_basket.setName(T_BASKET);
				t_basket.setNotNull(true);
				t_basket.setScriptComment("REFERENCES "+BASKETS_TAB);
				if(createFk){
					t_basket.setReferencedTable(new DbTableName(schema.getName(),BASKETS_TAB));
				}
				dbTable.addColumn(t_basket);
		  }
		DbColumn dbCol;
		if(base==null){
			if(createTypeDiscriminator || Ili2cUtility.isViewableWithExtension(def)){
				  dbCol=createSqlTypeCol(T_TYPE);
				  dbTable.addColumn(dbCol);
			}
			// if CLASS
			  if((def instanceof Table) && ((Table)def).isIdentifiable()){
				  if(createIliTidCol){
						addIliTidCol(dbTable);
				  }
			  }
		  // if STRUCTURE, add ref to parent
		  if((def instanceof Table) && !((Table)def).isIdentifiable()){
			  if(createGenericStructRef){
				  // add parentid
					DbColId dbParentId=new DbColId();
					dbParentId.setName(T_PARENT_ID);
					dbParentId.setNotNull(true);
					dbParentId.setPrimaryKey(false);
					dbTable.addColumn(dbParentId);
					  // add parent_type
					dbCol=createSqlTypeCol(T_PARENT_TYPE);
					dbTable.addColumn(dbCol);
					// add parent_attr
					dbCol=createSqlTypeCol(T_PARENT_ATTR);
					dbTable.addColumn(dbCol);
			  }else{
				  // add reference to parent for each structAttr when generating structAttr
			  }
			// add seqeunce attr
			DbColId dbSeq=new DbColId();
			dbSeq.setName(T_SEQ);
			dbSeq.setNotNull(true);
			dbSeq.setPrimaryKey(false);
			dbTable.addColumn(dbSeq);
		  }
		}

		// body
		ArrayList<AttributeDef> surfaceAttrs=new ArrayList<AttributeDef>(); 
		Iterator iter=null;
		if(isIli1Model){
			iter=ModelUtilities.getIli1AttrList((AbstractClassDef)def).iterator();
		}else{
			iter=def.getDefinedAttributesAndRoles2();
		}
		  while (iter.hasNext()) {
			  ViewableTransferElement obj = (ViewableTransferElement)iter.next();
			  if (obj.obj instanceof AttributeDef) {
				  AttributeDef attr = (AttributeDef) obj.obj;
				  if(attr.getExtending()==null){
					try{
						if(createItfLineTables && attr.getDomainResolvingAll() instanceof SurfaceOrAreaType){
							surfaceAttrs.add(attr);
						}
						if(!attr.isTransient()){
							Type proxyType=attr.getDomain();
							if(proxyType!=null && (proxyType instanceof ObjectType)){
								// skip implicit particles (base-viewables) of views
							}else{
								generateAttr(dbTable,attr);
							}
						}
					}catch(Exception ex){
						throw new Ili2dbException(attr.getContainer().getScopedName(null)+"."+attr.getName(),ex);
					}
				  }else{
					  if(isBoolean(td,attr)){
						  
					  }else if(createEnumColAsItfCode && attr.getDomainResolvingAll() instanceof EnumerationType){
						  throw new Ili2dbException("EXTENDED attributes with type enumeration not supported");
					  }
				  }
			  }
			  if(obj.obj instanceof RoleDef){
				  RoleDef role = (RoleDef) obj.obj;
					if(role.getExtending()==null){
						// not an embedded role and roledef not defined in a lightweight association?
						if (!obj.embedded && !((AssociationDef)def).isLightweight()){
						  dbColId=new DbColId();
						  dbColId.setName(getSqlRoleName(role));
						  dbColId.setNotNull(true);
						  dbColId.setPrimaryKey(false);
						  if(createFk){
							  dbColId.setReferencedTable(getSqlTableName(role.getDestination()));
						  }
						  dbTable.addColumn(dbColId);
						  // handle ordered
						  if(role.isOrdered()){
								// add seqeunce attr
								DbColId dbSeq=new DbColId();
								dbSeq.setName(getSqlRoleName(role)+"_"+T_SEQ);
								dbSeq.setNotNull(true);
								dbSeq.setPrimaryKey(false);
								dbTable.addColumn(dbSeq);
						  }
						}
						// a role of an embedded association?
						if(obj.embedded){
							AssociationDef roleOwner = (AssociationDef) role.getContainer();
							if(roleOwner.getDerivedFrom()==null){
								// role is oppend;
							  dbColId=new DbColId();
							  String fkName=getSqlRoleName(role);
							  dbColId.setName(fkName);
							  boolean notNull=false;
							  dbColId.setNotNull(notNull);
							  dbColId.setPrimaryKey(false);
							  if(createFk){
								  dbColId.setReferencedTable(getSqlTableName(role.getDestination()));
							  }
							  customMapping.fixupEmbeddedLink(dbTable,dbColId,roleOwner,role,getSqlTableName(role.getDestination()),colT_ID);
							  dbTable.addColumn(dbColId);
							  // handle ordered
							  if(role.getOppEnd().isOrdered()){
									// add seqeunce attr
									DbColId dbSeq=new DbColId();
									dbSeq.setName(getSqlRoleName(role)+"_"+T_SEQ);
									dbSeq.setNotNull(notNull);
									dbSeq.setPrimaryKey(false);
									dbTable.addColumn(dbSeq);
							  }
							}
						}
					}
			  }
		}
		  if(createStdCols){
				addStdCol(dbTable);
		  }
		  customMapping.fixupViewable(dbTable,def);
	  	schema.addTable(dbTable);
	  	
	  	if(createItfLineTables && surfaceAttrs.size()>0){
	  		for(AttributeDef attr : surfaceAttrs){
	  			generateItfLineTable(attr);
	  		}
	  	}
	}
	private void generateItfLineTable(AttributeDef attr)
	throws Ili2dbException
	{
		DbTableName sqlName=getSqlTableNameItfLineTable(attr);
		DbTable dbTable=new DbTable();
		dbTable.setName(sqlName);
		dbTable.setIliName(attr.getContainer().getScopedName(null)+"."+attr.getName());
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+attr.getContainer().getScopedName(null)+"."+attr.getName());
		cmtSep=nl;
		if(cmt.length()>0){
			dbTable.setComment(cmt.toString());
		}
		if(deleteExistingData){
			dbTable.setDeleteDataIfTableExists(true);
		}
		  dbTable.setRequiresSequence(true);
		DbColId dbColId=addKeyCol(dbTable);
		  if(createIliTidCol){
				addIliTidCol(dbTable);
		  }
		  if(createBasketCol){
			  // add basketCol
				DbColId t_basket=new DbColId();
				t_basket.setName(T_BASKET);
				t_basket.setNotNull(true);
				t_basket.setScriptComment("REFERENCES "+BASKETS_TAB);
				if(createFk){
					t_basket.setReferencedTable(new DbTableName(schema.getName(),BASKETS_TAB));
				}
				dbTable.addColumn(t_basket);
		  }
			SurfaceOrAreaType type = (SurfaceOrAreaType)attr.getDomainResolvingAll();
			
			DbColGeometry dbCol = generatePolylineType(type, attr.getContainer().getScopedName(null)+"."+attr.getName());
			  dbCol.setName(getSqlColNameItfLineTableGeomAttr(attr,ITF_LINETABLE_GEOMATTR));
			  dbCol.setNotNull(true);
			  dbTable.addColumn(dbCol);
			
			if(type instanceof SurfaceType){
				  dbColId=new DbColId();
				  dbColId.setName(getSqlColNameItfLineTableRefAttr(attr,ITF_LINETABLE_MAINTABLEREF));
				  dbColId.setNotNull(true);
				  dbColId.setPrimaryKey(false);
				  dbColId.setScriptComment("REFERENCES "+getSqlTableName((Viewable)attr.getContainer()));
				  if(createFk){
					  dbColId.setReferencedTable(getSqlTableName((Viewable)attr.getContainer()));
				  }
				  dbTable.addColumn(dbColId);
			}
			
			Table lineAttrTable=type.getLineAttributeStructure();
			if(lineAttrTable!=null){
			    Iterator attri = lineAttrTable.getAttributes ();
			    while(attri.hasNext()){
			    	AttributeDef lineattr=(AttributeDef)attri.next();
			    	generateAttr(dbTable,lineattr);
			    }
			}
		
			  if(createStdCols){
					addStdCol(dbTable);
			  }
		  	schema.addTable(dbTable);
	}
	private void addIliTidCol(DbTable dbTable) {
		DbColVarchar dbColIliTid=new DbColVarchar();
		dbColIliTid.setName(T_ILI_TID);
		dbColIliTid.setNotNull(false); // enable later inserts without TID
		dbColIliTid.setSize(200);
		dbTable.addColumn(dbColIliTid);
	}
	static public boolean isBoolean(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias && isBoolean(td,attr.getDomain())) {
			return true;
		}
		return false;
		
	}
	static public boolean isIliUuid(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias){
			Type type=attr.getDomain();
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.UUIDOID) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		return false;
	}
	static public boolean isIli1Date(TransferDescription td,AttributeDef attr){
		if (attr.getDomain() instanceof TypeAlias){
			Type type=attr.getDomain();
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.INTERLIS_1_DATE) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		return false;
	}
	static public boolean isIli2Date(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlDate) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlDate){
				return true;
			}
		}
		return false;
	}
	static public boolean isIli2Time(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlTime) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlTime){
				return true;
			}
		}
		return false;
	}
	static public boolean isIli2DateTime(TransferDescription td,AttributeDef attr){
		Type type=attr.getDomain();
		if (type instanceof TypeAlias){
			while(type instanceof TypeAlias) {
				if (((TypeAlias) type).getAliasing() == td.INTERLIS.XmlDateTime) {
					return true;
				}
				type=((TypeAlias) type).getAliasing().getType();
			}
		}
		if(type instanceof FormattedType){
			FormattedType ft=(FormattedType)type;
			if(ft.getDefinedBaseDomain()== td.INTERLIS.XmlDateTime){
				return true;
			}
		}
		return false;
	}
	static public boolean isBoolean(TransferDescription td,Type type){
		while(type instanceof TypeAlias) {
			if (((TypeAlias) type).getAliasing() == td.INTERLIS.BOOLEAN) {
				return true;
			}
			type=((TypeAlias) type).getAliasing().getType();
		}
		
		return false;
	}
	private void generateAttr(DbTable dbTable,AttributeDef attr)
	throws Ili2dbException
	{
		if(attr.getDomain() instanceof EnumerationType){
			visitedEnums.add(attr);
		}
		DbColumn dbCol=null;
		DbColumn dbCol_georef=null;
		Type type = attr.getDomainResolvingAll();
		if (isBoolean(td,attr)) {
			dbCol= new DbColBoolean();
		}else if (isIli1Date(td,attr)) {
			dbCol= new DbColDate();
		}else if (isIliUuid(td,attr)) {
			dbCol= new DbColUuid();
			// CREATE EXTENSION "uuid-ossp";
			// dbCol.setDefaultValue("uuid_generate_v4()");
		}else if (isIli2Date(td,attr)) {
			dbCol= new DbColDate();
		}else if (isIli2DateTime(td,attr)) {
			dbCol= new DbColDateTime();
		}else if (isIli2Time(td,attr)) {
			dbCol= new DbColTime();
		}else if (type instanceof PolylineType){
			String attrName=attr.getContainer().getScopedName(null)+"."+attr.getName();
			DbColGeometry ret = generatePolylineType((PolylineType)type, attrName);
			dbCol=ret;
		}else if (type instanceof SurfaceOrAreaType){
			if(createItfLineTables){
				dbCol=null;
			}else{
				DbColGeometry ret=new DbColGeometry();
				boolean curvePolygon=false;
				if(!strokeArcs){
					curvePolygon=true;
				}
				ret.setType(curvePolygon ? DbColGeometry.CURVEPOLYGON : DbColGeometry.POLYGON);
				// TODO get crs from ili
				ret.setSrsAuth(defaultCrsAuthority);
				ret.setSrsId(defaultSrcCode);
				CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
				ret.setDimension(coord.getDimensions().length);
				setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
				dbCol=ret;
			}
			if(createItfAreaRef){
				if(type instanceof AreaType){
					DbColGeometry ret=new DbColGeometry();
					ret.setType(DbColGeometry.POINT);
					// TODO get crs from ili
					ret.setSrsAuth(defaultCrsAuthority);
					ret.setSrsId(defaultSrcCode);
					ret.setDimension(2); // always 2 (even if defined as 3d in ili)
					CoordType coord=(CoordType)((SurfaceOrAreaType)type).getControlPointDomain().getType();
					setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
					dbCol_georef=ret;
				}
			}
		}else if (type instanceof CoordType){
			DbColGeometry ret=new DbColGeometry();
			ret.setType(DbColGeometry.POINT);
			// TODO get crs from ili
			ret.setSrsAuth(defaultCrsAuthority);
			ret.setSrsId(defaultSrcCode);
			CoordType coord=(CoordType)type;
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attr.getContainer().getScopedName(null)+"."+attr.getName());
			dbCol=ret;
		}else if (type instanceof CompositionType){
			// skip it
			if(!createGenericStructRef){
				// add reference to struct table
				addParentRef(attr);
			}
			dbCol=null;
		}else if (type instanceof ReferenceType){
			DbColId ret=new DbColId();
			ret.setNotNull(false);
			ret.setPrimaryKey(false);
			if(createFk){
				ret.setReferencedTable(getSqlTableName(((ReferenceType)type).getReferred()));
			}
			dbCol=ret;
		}else if (type instanceof BasketType){
			// skip it; type no longer exists in ili 2.3
			dbCol=null;
		}else if(type instanceof EnumerationType){
			if(createEnumColAsItfCode){
				DbColId ret=new DbColId();
				dbCol=ret;
			}else{
				DbColVarchar ret=new DbColVarchar();
				ret.setSize(255);
				dbCol=ret;				
			}
		}else if(type instanceof NumericType){
			if(type.isAbstract()){
			}else{
				PrecisionDecimal min=((NumericType)type).getMinimum();
				PrecisionDecimal max=((NumericType)type).getMaximum();
				if(min.getAccuracy()>0){
					DbColDecimal ret=new DbColDecimal();
					int size=Math.max(min.toString().length(),max.toString().length());
					int precision=min.getAccuracy();
					//EhiLogger.debug("attr "+ attr.getName()+", maxStr <"+maxStr+">, size "+Integer.toString(size)+", precision "+Integer.toString(precision));
					ret.setSize(size);
					ret.setPrecision(precision);
					dbCol=ret;
				}else{
					DbColNumber ret=new DbColNumber();
					int size=Math.max(min.toString().length(),max.toString().length());
					ret.setSize(size);
					dbCol=ret;
				}
			}
		}else if(type instanceof TextType){
			DbColVarchar ret=new DbColVarchar();
			if(((TextType)type).getMaxLength()>0){
				ret.setSize(((TextType)type).getMaxLength());
			}else{
				ret.setSize(255);
			}
			dbCol=ret;
		}else{
			DbColVarchar ret=new DbColVarchar();
			ret.setSize(255);
			dbCol=ret;
		}

		if (dbCol != null) {
			String sqlName=getSqlAttrName(attr);
			setAttrDbColProps(attr, dbCol, sqlName);
			customMapping.fixupAttribute(dbTable, dbCol, attr);
			dbTable.addColumn(dbCol);
			if (createEnumTxtCol && type instanceof EnumerationType) {
				DbColVarchar ret = new DbColVarchar();
				ret.setSize(255);
				dbCol=ret;
				dbCol.setName(sqlName+ENUM_TXT_COL_SUFFIX);
				if (sqlEnableNull) {
					dbCol.setNotNull(false);
				} else {
					if (attr.getDomain().isMandatoryConsideringAliases()) {
						dbCol.setNotNull(true);
					}
				}
				customMapping.fixupAttribute(dbTable, dbCol, attr);
				dbTable.addColumn(dbCol);
			}
		} else {
			customMapping.fixupAttribute(dbTable, null, attr);
		}
		if (dbCol_georef != null) {
			String sqlName=getSqlAttrName(attr)+ITF_MAINTABLE_GEOTABLEREF_SUFFIX;
			setAttrDbColProps(attr, dbCol_georef, sqlName);
			//customMapping.fixupAttribute(dbTable, dbCol_georef, attr);
			dbTable.addColumn(dbCol_georef);
		}
	}
	private void setAttrDbColProps(AttributeDef attr, DbColumn dbCol,
			String sqlName) {
		dbCol.setName(sqlName);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		if(sqlName!=attr.getName()){
			cmt.append(cmtSep+"@iliname "+attr.getName());
			cmtSep=nl;
		}
		if(cmt.length()>0){
			dbCol.setComment(cmt.toString());
		}
		if (sqlEnableNull) {
			dbCol.setNotNull(false);
		} else {
			Type type=attr.getDomain();
			if(type==null){
				Evaluable[] ev = (((LocalAttribute)attr).getBasePaths());
				type=((ObjectPath)ev[0]).getType();
			}
			if (type.isMandatoryConsideringAliases()) {
				dbCol.setNotNull(true);
			}
		}
	}

	private DbColGeometry generatePolylineType(LineType type, String attrName) {
		DbColGeometry ret=new DbColGeometry();
		boolean compoundCurve=false;
		if(!strokeArcs){
			compoundCurve=true;
		}
		ret.setType(compoundCurve ? DbColGeometry.COMPOUNDCURVE : DbColGeometry.LINESTRING);
		// TODO get crs from ili
		ret.setSrsAuth(defaultCrsAuthority);
		ret.setSrsId(defaultSrcCode);
		Domain coordDomain=type.getControlPointDomain();
		if(coordDomain!=null){
			CoordType coord=(CoordType)coordDomain.getType();
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attrName);
		}
		return ret;
	}
	private void addParentRef(AttributeDef attr){
		CompositionType type = (CompositionType)attr.getDomainResolvingAll();
		Table structClass=type.getComponentType();
		Table root=(Table)structClass.getRootExtending();
		if(root!=null){
			structClass=root;
		}
		DbTableName structClassSqlName=getSqlTableName(structClass);
		
		// find struct table
		DbTable dbTable=findTable(structClassSqlName);
		
		// add ref attr
		String refAttrSqlName=ili2sqlName.mapIliAttributeDefQualified(attr);
		DbColId dbParentId=new DbColId();
		dbParentId.setName(refAttrSqlName);
		dbParentId.setNotNull(false); // values of other struct attrs will have NULL
		dbParentId.setPrimaryKey(false);
		StringBuffer cmt=new StringBuffer();
		String cmtSep="";
		if(attr.getDocumentation()!=null){
			cmt.append(cmtSep+attr.getDocumentation());
			cmtSep=nl;
		}
		cmt.append(cmtSep+"@iliname "+attr.getContainer().getScopedName(null)+"."+attr.getName());
		cmtSep=nl;
		if(cmt.length()>0){
			dbParentId.setComment(cmt.toString());
		}
		if(createFk){
			dbParentId.setReferencedTable(getSqlTableName((Viewable)attr.getContainer()));
		}
		dbTable.addColumn(dbParentId);
	}
	private DbTable findTable(DbTableName structClassSqlName) {
		Iterator tabi=schema.iteratorTable();
		while(tabi.hasNext()){
			DbTable tab=(DbTable)tabi.next();
			if(tab.getName().equals(structClassSqlName)){
				return tab;
			}
		}
		return null;
	}
	private void setBB(DbColGeometry ret, CoordType coord,String scopedAttrName) {
		NumericalType dimv[]=coord.getDimensions();
		if(!(dimv[0] instanceof NumericType) || !(dimv[1] instanceof NumericType)){
			EhiLogger.logError("Attribute "+scopedAttrName+": COORD type not supported ("+dimv[0].getClass().getName()+")");
			return;
		}
		if(((NumericType)dimv[0]).getMinimum()!=null){
			ret.setMin1(((NumericType)dimv[0]).getMinimum().doubleValue());
			ret.setMax1(((NumericType)dimv[0]).getMaximum().doubleValue());
			ret.setMin2(((NumericType)dimv[1]).getMinimum().doubleValue());
			ret.setMax2(((NumericType)dimv[1]).getMaximum().doubleValue());
			if(dimv.length==3){
				ret.setMin3(((NumericType)dimv[2]).getMinimum().doubleValue());
				ret.setMax3(((NumericType)dimv[2]).getMaximum().doubleValue());
			}
		}
	}
	/** setup a mapping from a qualified model or topic name
	 * to the corresponding java object.
	 */
	private HashMap setupTopicTagMap(TransferDescription td){
		HashMap ret=new HashMap();
		Iterator modeli = td.iterator ();
		while (modeli.hasNext ())
		{
		  Object mObj = modeli.next ();
		  if(mObj instanceof Model){
			Model model=(Model)mObj;
			ret.put(model.getScopedName(null),model);
			Iterator topici=model.iterator();
			while(topici.hasNext()){
				Object tObj=topici.next();
				if (tObj instanceof Topic){
				  	Topic topic=(Topic)tObj;
   					ret.put(topic.getScopedName(null),topic);
				}
			}
		  }
		}
		return ret;
	}
	private String getSqlColNameItfLineTableRefAttr(AttributeDef attr,String ioxName)
	{
		return ili2sqlName.mapIliAttrName(attr,ioxName);
	}
	private String getSqlColNameItfLineTableGeomAttr(AttributeDef attr,String ioxName)
	{
		return ili2sqlName.mapIliAttrName(attr,ioxName);
	}
	private String getSqlRoleName(RoleDef def){
		return ili2sqlName.mapIliRoleDef(def);
	}
	private String getSqlAttrName(AttributeDef def){
		return ili2sqlName.mapIliAttributeDef(def);
	}
	private DbTableName getSqlTableName(Viewable def){
		String sqlname=ili2sqlName.mapIliClassDef(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbTableName getSqlTableName(Domain def){
		String sqlname=ili2sqlName.mapIliDomainDef(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbTableName getSqlTableNameEnum(AttributeDef def){
		String sqlname=ili2sqlName.mapIliEnumAttributeDefAsTable(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbTableName getSqlTableNameItfLineTable(AttributeDef def){
		String sqlname=ili2sqlName.mapItfLineTableAsTable(def);
		return new DbTableName(schema.getName(),sqlname);
	}
	private DbColumn createSqlTypeCol(String name){
	  DbColVarchar dbCol=new DbColVarchar();
	  dbCol.setName(name);
	  dbCol.setNotNull(true);
	  dbCol.setSize(ili2sqlName.getMaxSqlNameLength());
	  return dbCol;
	}
	private DbColId addKeyCol(DbTable table) {
		  DbColId dbColId=new DbColId();
		  dbColId.setName(colT_ID);
		  dbColId.setNotNull(true);
		  dbColId.setPrimaryKey(true);
		  table.addColumn(dbColId);
		  return dbColId;
	}
	public static void addStdCol(DbTable table) {
		DbColumn dbCol=new DbColDateTime();
		dbCol.setName(T_LAST_CHANGE);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		dbCol=new DbColDateTime();
		dbCol.setName(T_CREATE_DATE);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		DbColVarchar dbColUsr=new DbColVarchar();
		dbColUsr.setName(T_USER);
		dbColUsr.setNotNull(true);
		dbColUsr.setSize(40);
		table.addColumn(dbColUsr);
	}
	public static final String MODELS_TAB="T_ILI2DB_MODEL";
	public static final String MODELS_TAB_FILE="file";
	public static final String MODELS_TAB_ILIVERSION="iliversion";
	public static final String MODELS_TAB_MODELNAME="modelName";
	public static final String MODELS_TAB_CONTENT="content";
	public static final String MODELS_TAB_IMPORTDATE="importDate";
	static public void addModelsTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),MODELS_TAB));
		DbColVarchar fileCol=new DbColVarchar();
		fileCol.setName(MODELS_TAB_FILE);
		fileCol.setNotNull(true);
		fileCol.setSize(250);
		tab.addColumn(fileCol);
		DbColVarchar iliversionCol=new DbColVarchar();
		iliversionCol.setName(MODELS_TAB_ILIVERSION);
		iliversionCol.setNotNull(true);
		iliversionCol.setSize(3);
		tab.addColumn(iliversionCol);
		DbColVarchar importsCol=new DbColVarchar();
		importsCol.setName(MODELS_TAB_MODELNAME);
		importsCol.setNotNull(true);
		importsCol.setSize(-1);
		tab.addColumn(importsCol);
		DbColVarchar contentCol=new DbColVarchar();
		contentCol.setName(MODELS_TAB_CONTENT);
		contentCol.setNotNull(true);
		contentCol.setSize(-1);
		tab.addColumn(contentCol);
		DbColDateTime importDateCol=new DbColDateTime();
		importDateCol.setName(MODELS_TAB_IMPORTDATE);
		importDateCol.setNotNull(true);
		tab.addColumn(importDateCol);
		DbIndex pk=new DbIndex();
		pk.setPrimary(true);
		pk.addAttr(importsCol);
		pk.addAttr(iliversionCol);
		tab.addIndex(pk);
		schema.addTable(tab);
	}
	public static ch.interlis.ilirepository.IliFiles readIliFiles(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String sqlName=MODELS_TAB;
		if(!DbUtility.tableExists(conn,new DbTableName(schema,sqlName))){
			return null;
		}
		ch.interlis.ilirepository.IliFiles ret=new ch.interlis.ilirepository.IliFiles();
		try{
			String reposUri=conn.getMetaData().getURL();
			if(schema!=null){
				sqlName=schema+"."+sqlName;
				reposUri=reposUri+"/"+schema;
			}
			// select entries
			String insStmt="SELECT "+MODELS_TAB_FILE+","+MODELS_TAB_ILIVERSION+","+MODELS_TAB_MODELNAME+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			try{
				java.sql.ResultSet rs=insPrepStmt.executeQuery();
				while(rs.next()){
					String file=rs.getString(1);
					double iliversion=Double.parseDouble(rs.getString(2));
					String imports=rs.getString(3);
					ch.interlis.ili2c.modelscan.IliFile iliFile=IliImportsUtility.parseIliImports(iliversion, imports);
					iliFile.setPath(file);
					iliFile.setRepositoryUri(reposUri);
					ret.addFile(iliFile);
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read IliFiles from db",ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return ret;
	}
	public static String readIliFile(java.sql.Connection conn,String schema,String filename)
	throws Ili2dbException
	{
		String sqlName=MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			// select entries
			String selStmt="SELECT "+MODELS_TAB_CONTENT+" FROM "+sqlName+" WHERE "+MODELS_TAB_FILE+"=?";
			EhiLogger.traceBackendCmd(selStmt);
			java.sql.PreparedStatement selPrepStmt = conn.prepareStatement(selStmt);
			try{
				selPrepStmt.clearParameters();
				selPrepStmt.setString(1, filename);
				java.sql.ResultSet rs=selPrepStmt.executeQuery();
				while(rs.next()){
					String file=rs.getString(1);
					return file;
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read ili-file <"+filename+"> from db",ex);
			}finally{
				selPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read models-table "+sqlName,ex);
		}
		return null;
	}
	public static void addModels(java.sql.Connection conn,TransferDescription td,String schema)
	throws Ili2dbException
	{
		// read existing models from db
		IliFiles iliModelsInDb = TransferFromIli.readIliFiles(conn,schema);

		String sqlName=MODELS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		java.sql.Timestamp today=new java.sql.Timestamp(System.currentTimeMillis());

		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+MODELS_TAB_FILE+","+MODELS_TAB_ILIVERSION+","+MODELS_TAB_MODELNAME+","+MODELS_TAB_CONTENT+","+MODELS_TAB_IMPORTDATE+") VALUES (?,?,?,?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			java.util.Iterator entri=td.iterator();
			HashMap<java.io.File,ch.interlis.ili2c.modelscan.IliFile> ilifiles=new HashMap<java.io.File,ch.interlis.ili2c.modelscan.IliFile>();
			while(entri.hasNext()){
				Object entro=entri.next();
				if(entro instanceof ch.interlis.ili2c.metamodel.Model){
					if(entro instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
						continue;
					}
					ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)entro;
					java.io.File file=new java.io.File(model.getFileName());
					ch.interlis.ili2c.modelscan.IliFile ilifile=null;
					if(ilifiles.containsKey(file)){
						ilifile=ilifiles.get(file);
					}else{
						ilifile=new ch.interlis.ili2c.modelscan.IliFile();
						ilifile.setFilename(file);
						ilifiles.put(file,ilifile);
					}
					ch.interlis.ili2c.modelscan.IliModel ilimodel=new ch.interlis.ili2c.modelscan.IliModel();
					ilimodel.setIliVersion(Double.parseDouble(model.getIliVersion()));
					ilimodel.setName(model.getName());
					Model imports[]=model.getImporting();
					for(Model importm : imports){
						ilimodel.addDepenedency(importm.getName());
					}
					ilifile.addModel(ilimodel);
				}
			}
			try{
				for(ch.interlis.ili2c.modelscan.IliFile ilifile: ilifiles.values()){
					ch.interlis.ili2c.modelscan.IliModel ilimodel=(ch.interlis.ili2c.modelscan.IliModel)ilifile.iteratorModel().next();
					if(iliModelsInDb==null || iliModelsInDb.getFileWithModel(ilimodel.getName(), ilimodel.getIliVersion())==null){
						insPrepStmt.clearParameters();
						insPrepStmt.setString(1, ilifile.getFilename().getName());
						insPrepStmt.setString(2, Double.toString(ilifile.getIliVersion()));
						insPrepStmt.setString(3, IliImportsUtility.getIliImports(ilifile));
						insPrepStmt.setString(4, readFileAsString(ilifile.getFilename()));
						insPrepStmt.setTimestamp(5, today);
						
						insPrepStmt.executeUpdate();
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert model",ex);
			} catch (IOException e) {
				throw new Ili2dbException("failed to update models-table "+sqlName,e);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update models-table "+sqlName,ex);
		}

	}
	public static String readFileAsString(java.io.File filePath) throws java.io.IOException {
	    java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(filePath));
	    try {
	        long len = filePath.length();
	        if (len > Integer.MAX_VALUE) throw new java.io.IOException("File "+filePath+" too large, was "+len+" bytes.");
	        byte[] bytes = new byte[(int) len];
	        dis.read(bytes);
	        return new String(bytes, "UTF-8");
	    } finally {
	        dis.close();
	    }
	}

	public static final String SETTINGS_TAB="T_ILI2DB_SETTINGS";
	public static final String SETTINGS_TAB_TAG="tag";
	public static final String SETTINGS_TAB_SETTING="setting";
	static public void addSettingsTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),SETTINGS_TAB));
		DbColVarchar tagCol=new DbColVarchar();
		tagCol.setName(SETTINGS_TAB_TAG);
		tagCol.setNotNull(true);
		tagCol.setPrimaryKey(true);
		tagCol.setSize(60);
		tab.addColumn(tagCol);
		DbColVarchar settingCol=new DbColVarchar();
		settingCol.setName(SETTINGS_TAB_SETTING);
		settingCol.setNotNull(false);
		settingCol.setSize(60);
		tab.addColumn(settingCol);
		schema.addTable(tab);
	}
	public static void readSettings(java.sql.Connection conn,Config settings,String schema)
	throws Ili2dbException
	{
		String sqlName=SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		if(DbUtility.tableExists(conn,new DbTableName(schema,SETTINGS_TAB))){
			try{
				// select entries
				String insStmt="SELECT "+SETTINGS_TAB_TAG+","+SETTINGS_TAB_SETTING+" FROM "+sqlName;
				EhiLogger.traceBackendCmd(insStmt);
				java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
				boolean settingsExists=false;
				try{
					java.sql.ResultSet rs=insPrepStmt.executeQuery();
					while(rs.next()){
						String tag=rs.getString(1);
						String value=rs.getString(2);
						if(tag.equals(Config.SENDER))continue;
						settings.setValue(tag,value);
						settingsExists=true;
					}
					if(settingsExists){
						settings.setConfigReadFromDb(true);
					}
				}catch(java.sql.SQLException ex){
					throw new Ili2dbException("failed to read setting",ex);
				}finally{
					insPrepStmt.close();
				}
			}catch(java.sql.SQLException ex){		
				throw new Ili2dbException("failed to read settings-table "+sqlName,ex);
			}
		}
	}
	public static void updateSettings(java.sql.Connection conn,Config settings,String schema)
	throws Ili2dbException
	{

		String sqlName=SETTINGS_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+SETTINGS_TAB_TAG+","+SETTINGS_TAB_SETTING+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			try{
				java.util.Iterator entri=settings.getValues().iterator();
				while(entri.hasNext()){
					String tag=(String)entri.next();
					insPrepStmt.clearParameters();
					insPrepStmt.setString(1, tag);
					insPrepStmt.setString(2, settings.getValue(tag));
					insPrepStmt.executeUpdate();
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert setting",ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update settings-table "+sqlName,ex);
		}

	}
	
	public static final String INHERIT_TAB="T_ILI2DB_INHERITANCE";
	public static final String INHERIT_TAB_THIS="thisClass";
	public static final String INHERIT_TAB_BASE="baseClass";
	static public void addInheritanceTable(DbSchema schema,int sqlNameSize)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(),INHERIT_TAB));
		DbColVarchar thisClass=new DbColVarchar();
		thisClass.setName(INHERIT_TAB_THIS);
		thisClass.setNotNull(true);
		thisClass.setPrimaryKey(true);
		thisClass.setSize(sqlNameSize);
		tab.addColumn(thisClass);
		DbColVarchar baseClass=new DbColVarchar();
		baseClass.setName(INHERIT_TAB_BASE);
		baseClass.setNotNull(false);
		baseClass.setSize(sqlNameSize);
		tab.addColumn(baseClass);
		schema.addTable(tab);
	}
	public static final String BASKETS_TAB="T_ILI2DB_BASKET";
	public static final String DATASETS_TAB="T_ILI2DB_DATASET";
	public static final String BASKETS_TAB_TOPIC="topic";
	public static final String BASKETS_TAB_DATASET="dataset";
	public static final String BASKETS_TAB_ATTACHMENT_KEY="attachmentKey";
	//public static final String BASKETS_TAB_DISPNAME="dispName"; // name of basket z.B. "Projekt 35" 
	//public static final String BASKETS_TAB_DESC="desc"; // description of basket z.B. "Bodenprofile Sursee"
	public void addBasketsTable(DbSchema schema)
	{
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),BASKETS_TAB));
			
			// primary key
			addKeyCol(tab);
			
			// optional reference to dataset table
			DbColId dbColDataset=new DbColId();
			dbColDataset.setName(BASKETS_TAB_DATASET);
			dbColDataset.setNotNull(false);
			dbColDataset.setPrimaryKey(false);
			if(createFk){
				dbColDataset.setReferencedTable(new DbTableName(schema.getName(),DATASETS_TAB));
			}
			tab.addColumn(dbColDataset);
			
			// qualified name of ili topic
			DbColVarchar thisClass=new DbColVarchar();
			thisClass.setName(BASKETS_TAB_TOPIC);
			thisClass.setNotNull(true);
			thisClass.setSize(200);
			tab.addColumn(thisClass);

			// basket id as read from xtf
			addIliTidCol(tab); 
			
			// name of subdirectory in attachments folder
			DbColVarchar attkey=new DbColVarchar();
			attkey.setName(BASKETS_TAB_ATTACHMENT_KEY);
			attkey.setNotNull(true);
			attkey.setSize(200);
			tab.addColumn(attkey);
			
			schema.addTable(tab);
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),DATASETS_TAB));
			
			// primary key
			addKeyCol(tab);
			
			schema.addTable(tab);
			
		}
	}
	public static final String IMPORTS_TAB="T_ILI2DB_IMPORT";
	public static final String IMPORTS_BASKETS_TAB="T_ILI2DB_IMPORT_BASKET";
	public static final String IMPORTS_OBJECTS_TAB="T_ILI2DB_IMPORT_OBJECT";
	public static final String IMPORTS_TAB_IMPORTDATE="importDate";
	public static final String IMPORTS_TAB_IMPORTUSER="importUser";
	public static final String IMPORTS_TAB_IMPORTFILE="importFile";
	public static final String IMPORTS_TAB_DATASET="dataset";
	public static final String IMPORTS_BASKETS_TAB_IMPORT="import";
	public static final String IMPORTS_BASKETS_TAB_BASKET="basket";
	public static final String IMPORTS_TAB_OBJECTCOUNT="objectCount";
	public static final String IMPORTS_TAB_STARTTID="start_t_id";
	public static final String IMPORTS_TAB_ENDTID="end_t_id";
	public static final String IMPORTS_OBJECTS_TAB_CLASS="class";
	public static final String IMPORTS_OBJECTS_TAB_IMPORT="import_basket";
	public void addImportsTable(DbSchema schema)
	{
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),IMPORTS_TAB));
			
			addKeyCol(tab);
			
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(IMPORTS_TAB_DATASET);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+DATASETS_TAB);
			if(createFk){
				dbColBasket.setReferencedTable(new DbTableName(schema.getName(),DATASETS_TAB));
			}
			tab.addColumn(dbColBasket);
			
			DbColDateTime dbColImpDate=new DbColDateTime();
			dbColImpDate.setName(IMPORTS_TAB_IMPORTDATE);
			dbColImpDate.setNotNull(true);
			tab.addColumn(dbColImpDate);
		
			DbColVarchar dbColUsr=new DbColVarchar();
			dbColUsr.setName(IMPORTS_TAB_IMPORTUSER);
			dbColUsr.setNotNull(true);
			dbColUsr.setSize(40);
			tab.addColumn(dbColUsr);
			
			DbColVarchar dbColFile=new DbColVarchar();
			dbColFile.setName(IMPORTS_TAB_IMPORTFILE);
			dbColFile.setNotNull(true);
			dbColFile.setSize(200);
			tab.addColumn(dbColFile);
			
			schema.addTable(tab);
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),IMPORTS_BASKETS_TAB));
			
			addKeyCol(tab);
			
			DbColId dbColImport=new DbColId();
			dbColImport.setName(IMPORTS_BASKETS_TAB_IMPORT);
			dbColImport.setNotNull(true);
			dbColImport.setScriptComment("REFERENCES "+IMPORTS_TAB);
			if(createFk){
				dbColImport.setReferencedTable(new DbTableName(schema.getName(),IMPORTS_TAB));
			}
			tab.addColumn(dbColImport);
			
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(IMPORTS_BASKETS_TAB_BASKET);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+BASKETS_TAB);
			if(createFk){
				dbColBasket.setReferencedTable(new DbTableName(schema.getName(),BASKETS_TAB));
			}
			tab.addColumn(dbColBasket);
						
			DbColNumber dbColObjc=new DbColNumber();
			dbColObjc.setName(IMPORTS_TAB_OBJECTCOUNT);
			dbColObjc.setNotNull(false);
			tab.addColumn(dbColObjc);

			DbColNumber dbColStartId=new DbColNumber();
			dbColStartId.setName(IMPORTS_TAB_STARTTID);
			dbColStartId.setNotNull(false);
			tab.addColumn(dbColStartId);

			DbColNumber dbColEndId=new DbColNumber();
			dbColEndId.setName(IMPORTS_TAB_ENDTID);
			dbColEndId.setNotNull(false);
			tab.addColumn(dbColEndId);

			schema.addTable(tab);
			
		}
		{
			DbTable tab=new DbTable();
			tab.setName(new DbTableName(schema.getName(),IMPORTS_OBJECTS_TAB));
			addKeyCol(tab);
			DbColId dbColBasket=new DbColId();
			dbColBasket.setName(IMPORTS_OBJECTS_TAB_IMPORT);
			dbColBasket.setNotNull(true);
			dbColBasket.setScriptComment("REFERENCES "+IMPORTS_BASKETS_TAB);
			tab.addColumn(dbColBasket);
					
			// qualified name of ili class
			DbColVarchar dbColClass=new DbColVarchar();
			dbColClass.setName(IMPORTS_OBJECTS_TAB_CLASS);
			dbColClass.setNotNull(true);
			dbColClass.setSize(200);
			tab.addColumn(dbColClass);
						
			DbColNumber dbColObjc=new DbColNumber();
			dbColObjc.setName(IMPORTS_TAB_OBJECTCOUNT);
			dbColObjc.setNotNull(false);
			tab.addColumn(dbColObjc);

			DbColNumber dbColStartId=new DbColNumber();
			dbColStartId.setName(IMPORTS_TAB_STARTTID);
			dbColStartId.setNotNull(false);
			tab.addColumn(dbColStartId);

			DbColNumber dbColEndId=new DbColNumber();
			dbColEndId.setName(IMPORTS_TAB_ENDTID);
			dbColEndId.setNotNull(false);
			tab.addColumn(dbColEndId);

			schema.addTable(tab);
		}
	}
	public static final String ENUM_TAB="T_ILI2DB_ENUM";
	public static final String ENUM_TAB_THIS="thisClass";
	public static final String ENUM_TAB_BASE="baseClass";
	public static final String ENUM_TAB_SEQ="seq";
	public static final String ENUM_TAB_ILICODE="iliCode";
	public static final String ENUM_TAB_ITFCODE="itfCode";
	public static final String ENUM_TAB_DISPNAME="dispName";
	public void addEnumTable(DbSchema schema)
	{
		if(Config.CREATE_ENUM_DEFS_SINGLE.equals(createEnumTable)){
			DbTable tab=new DbTable();
			DbColVarchar thisClass=new DbColVarchar();
			thisClass.setName(ENUM_TAB_THIS);
			thisClass.setNotNull(true);
			thisClass.setSize(1024);
			tab.addColumn(thisClass);
			DbColVarchar baseClass=new DbColVarchar();
			baseClass.setName(ENUM_TAB_BASE);
			baseClass.setNotNull(false);
			baseClass.setSize(1024);
			tab.addColumn(baseClass);
			DbColNumber seq=new DbColNumber();
			seq.setName(ENUM_TAB_SEQ);
			seq.setNotNull(false);
			seq.setSize(4);
			tab.addColumn(seq);
			DbColVarchar iliCode=new DbColVarchar();
			iliCode.setName(ENUM_TAB_ILICODE);
			iliCode.setNotNull(true);
			iliCode.setSize(1024);
			tab.addColumn(iliCode);
			tab.setName(new DbTableName(schema.getName(),ENUM_TAB));
			DbColNumber itfCode=new DbColNumber();
			itfCode.setName(ENUM_TAB_ITFCODE);
			itfCode.setNotNull(true);
			itfCode.setSize(4);
			tab.addColumn(itfCode);
			DbColVarchar dispName=new DbColVarchar();
			dispName.setName(ENUM_TAB_DISPNAME);
			dispName.setNotNull(true);
			dispName.setSize(250);
			tab.addColumn(dispName);
			schema.addTable(tab);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			java.util.Iterator entri=visitedEnums.iterator();
			while(entri.hasNext()){
				Object entro=entri.next();
				DbTableName thisSqlName=null;
				if(entro instanceof AttributeDef){
					AttributeDef attr=(AttributeDef)entro;
					EnumerationType type=(EnumerationType)attr.getDomain();
					
					thisSqlName=getSqlTableNameEnum(attr);
					
				}else if(entro instanceof Domain){
					Domain domain=(Domain)entro;
					EnumerationType type=(EnumerationType)domain.getType();
					
					thisSqlName=getSqlTableName(domain);
				}
				if(thisSqlName!=null){
					DbTable tab=new DbTable();
					tab.setName(thisSqlName);
					DbColNumber itfCode=new DbColNumber();
					itfCode.setName(ENUM_TAB_ITFCODE);
					itfCode.setNotNull(true);
					itfCode.setSize(4);
					itfCode.setPrimaryKey(true);
					tab.addColumn(itfCode);
					DbColVarchar iliCode=new DbColVarchar();
					iliCode.setName(ENUM_TAB_ILICODE);
					iliCode.setNotNull(true);
					iliCode.setSize(1024);
					tab.addColumn(iliCode);
					DbColNumber seq=new DbColNumber();
					seq.setName(ENUM_TAB_SEQ);
					seq.setNotNull(false);
					seq.setSize(4);
					tab.addColumn(seq);
					DbColVarchar dispName=new DbColVarchar();
					dispName.setName(ENUM_TAB_DISPNAME);
					dispName.setNotNull(true);
					dispName.setSize(250);
					tab.addColumn(dispName);
					schema.addTable(tab);
				}
			}
			
		}
	}
	private static HashSet readInheritanceTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet ret=new HashSet();
		String sqlName=INHERIT_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
			exstStmt="SELECT "+INHERIT_TAB_THIS+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
			try{
				java.sql.ResultSet rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliCode=rs.getString(1);
					ret.add(iliCode);
				}
			}finally{
				exstPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read inheritance-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateInheritanceTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String sqlName=INHERIT_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		//String stmt="CREATE TABLE "+tabname+" ("+thisClassCol+" VARCHAR2(30) NOT NULL,"+baseClassCol+" VARCHAR2(30) NULL)";
		HashSet exstEntries=readInheritanceTable(conn,schema);
		try{

			// insert entries
			String stmt="INSERT INTO "+sqlName+" ("+INHERIT_TAB_THIS+","+INHERIT_TAB_BASE+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
			DbTableName thisClass=null;
			try{
				java.util.Iterator entri=visitedElements.iterator();
				while(entri.hasNext()){
					Object entro=entri.next();
					if(entro instanceof Viewable){
						Viewable aclass=(Viewable)entro;
						thisClass=getSqlTableName(aclass);
						if(!exstEntries.contains(thisClass)){
							Viewable base=(Viewable)aclass.getExtending();
							ps.setString(1, thisClass.getName());
							if(base!=null){
								DbTableName baseClass=getSqlTableName(base);
								ps.setString(2, baseClass.getName());
							}else{
								ps.setNull(2,java.sql.Types.VARCHAR);
							}
							ps.executeUpdate();
						}
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert inheritance-relation for class "+thisClass,ex);
			}finally{
				ps.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update inheritance-table "+sqlName,ex);
		}

	}
	public void updateEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{
		if(Config.CREATE_ENUM_DEFS_SINGLE.equals(createEnumTable)){
			updateSingleEnumTable(conn);
		}else if(Config.CREATE_ENUM_DEFS_MULTI.equals(createEnumTable)){
			updateMultiEnumTable(conn);
		}
	}
	private static HashSet readEnumTable(java.sql.Connection conn,boolean singleTable,String qualifiedIliName,DbTableName sqlDbName)
	throws Ili2dbException
	{
		HashSet ret=new HashSet();
		String sqlName=sqlDbName.getName();
		if(sqlDbName.getSchema()!=null){
			sqlName=sqlDbName.getSchema()+"."+sqlName;
		}
		try{
			String exstStmt=null;
			if(!singleTable){
				exstStmt="SELECT "+ENUM_TAB_ILICODE+" FROM "+sqlName;
			}else{
				exstStmt="SELECT "+ENUM_TAB_ILICODE+" FROM "+sqlName+" WHERE "+ENUM_TAB_THIS+" = '"+qualifiedIliName+"'";
			}
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = conn.prepareStatement(exstStmt);
			try{
				java.sql.ResultSet rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliCode=rs.getString(1);
					ret.add(iliCode);
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to read enum values for type "+qualifiedIliName,ex);
			}finally{
				exstPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read enum-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateSingleEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{
		DbTableName tabName=new DbTableName(schema.getName(),ENUM_TAB);
		String sqlName=tabName.getName();
		if(tabName.getSchema()!=null){
			sqlName=tabName.getSchema()+"."+sqlName;
		}
		try{

			// insert entries
			String insStmt="INSERT INTO "+sqlName+" ("+ENUM_TAB_SEQ+","+ENUM_TAB_ILICODE+","+ENUM_TAB_ITFCODE+","+ENUM_TAB_DISPNAME+","+ENUM_TAB_THIS+","+ENUM_TAB_BASE+") VALUES (?,?,?,?,?,?)";
			EhiLogger.traceBackendCmd(insStmt);
			java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
			String thisClass=null;
			try{
				java.util.Iterator entri=visitedEnums.iterator();
				while(entri.hasNext()){
					Object entro=entri.next();
					if(entro instanceof AttributeDef){
						AttributeDef attr=(AttributeDef)entro;
						EnumerationType type=(EnumerationType)attr.getDomainResolvingAll();
						
						thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
						AttributeDef base=(AttributeDef)attr.getExtending();
						String baseClass=null;
						if(base!=null){
							baseClass=base.getContainer().getScopedName(null)+"."+base.getName();
						}
						HashSet exstEntries=readEnumTable(conn,true,thisClass,tabName);
						updateEnumEntries(exstEntries,insPrepStmt, type, thisClass, baseClass);
					}else if(entro instanceof Domain){
						Domain domain=(Domain)entro;
						EnumerationType type=(EnumerationType)domain.getType();
						
						thisClass=domain.getScopedName(null);
						Domain base=(Domain)domain.getExtending();
						String baseClass=null;
						if(base!=null){
							baseClass=base.getScopedName(null);
						}
						HashSet exstEntries=readEnumTable(conn,true,thisClass,tabName);
						updateEnumEntries(exstEntries,insPrepStmt, type, thisClass, baseClass);
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
			}finally{
				insPrepStmt.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update enum-table "+sqlName,ex);
		}

	}
	public void updateMultiEnumTable(java.sql.Connection conn)
	throws Ili2dbException
	{

		java.util.Iterator entri=visitedEnums.iterator();
		while(entri.hasNext()){
			Object entro=entri.next();
			if(entro instanceof AttributeDef){
				AttributeDef attr=(AttributeDef)entro;
				EnumerationType type=(EnumerationType)attr.getDomainResolvingAll();
				String thisClass=attr.getContainer().getScopedName(null)+"."+attr.getName();
				DbTableName thisSqlName=getSqlTableNameEnum(attr);
				HashSet exstEntries=readEnumTable(conn,false,thisClass,thisSqlName);
				try{

					// insert entries
					String stmt="INSERT INTO "+thisSqlName+" ("+ENUM_TAB_SEQ+","+ENUM_TAB_ILICODE+","+ENUM_TAB_ITFCODE+","+ENUM_TAB_DISPNAME+") VALUES (?,?,?,?)";
					EhiLogger.traceBackendCmd(stmt);
					java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
					try{
						updateEnumEntries(exstEntries,ps, type, null, null);
					}catch(java.sql.SQLException ex){
						throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
					}finally{
						ps.close();
					}
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
				}
				
			}else if(entro instanceof Domain){
				Domain domain=(Domain)entro;
				EnumerationType type=(EnumerationType)domain.getType();
				
				String thisClass=domain.getScopedName(null);
				DbTableName thisSqlName=getSqlTableName(domain);
				HashSet exstEntries=readEnumTable(conn,false,thisClass,thisSqlName);
				try{

					// insert entries
					String stmt="INSERT INTO "+thisSqlName+" ("+ENUM_TAB_SEQ+","+ENUM_TAB_ILICODE+","+ENUM_TAB_ITFCODE+","+ENUM_TAB_DISPNAME+") VALUES (?,?,?,?)";
					EhiLogger.traceBackendCmd(stmt);
					java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
					try{
						updateEnumEntries(exstEntries,ps, type, null, null);
					}catch(java.sql.SQLException ex){
						throw new Ili2dbException("failed to insert enum values for type "+thisClass,ex);
					}finally{
						ps.close();
					}
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to update enum-table "+thisSqlName,ex);
				}
			}
		}
		
		

	}
	private void updateEnumEntries(HashSet exstEntires,java.sql.PreparedStatement ps, EnumerationType type, String thisClass, String baseClass) 
	throws SQLException 
	{
		java.util.ArrayList ev=new java.util.ArrayList();
		ch.interlis.iom_j.itf.ModelUtilities.buildEnumList(ev,"",type.getConsolidatedEnumeration());
		boolean isOrdered=type.isOrdered();
		int itfCode=0;
		int seq=0;
		Iterator evi=ev.iterator();
		while(evi.hasNext()){
			String ele=(String)evi.next();
			// entry exists already?
			if(!exstEntires.contains(ele)){
				// insert only non-existing entries
				if(isOrdered){
					ps.setInt(1, seq);
				}else{
					ps.setNull(1,java.sql.Types.NUMERIC);
				}
				ps.setString(2, ele);
				ps.setInt(3, itfCode);
				ps.setString(4, ele); // dispName
				// single table for all enums?
				if(thisClass!=null){
					ps.setString(5, thisClass);
					if(baseClass!=null){
						ps.setString(6, baseClass);
					}else{
						ps.setNull(6,java.sql.Types.VARCHAR);
					}
				}
				ps.executeUpdate();
			}
			itfCode++;
			seq++;
		}
	}
}
