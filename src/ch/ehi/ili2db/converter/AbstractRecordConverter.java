package ch.ehi.ili2db.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbIdGen;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2cUtility;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.ili2db.mapping.TrafoConfig;
import ch.ehi.sqlgen.repository.DbColBoolean;
import ch.ehi.sqlgen.repository.DbColDate;
import ch.ehi.sqlgen.repository.DbColDateTime;
import ch.ehi.sqlgen.repository.DbColDecimal;
import ch.ehi.sqlgen.repository.DbColGeometry;
import ch.ehi.sqlgen.repository.DbColId;
import ch.ehi.sqlgen.repository.DbColNumber;
import ch.ehi.sqlgen.repository.DbColTime;
import ch.ehi.sqlgen.repository.DbColUuid;
import ch.ehi.sqlgen.repository.DbColVarchar;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AbstractLeafElement;
import ch.interlis.ili2c.metamodel.AreaType;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.BasketType;
import ch.interlis.ili2c.metamodel.CompositionType;
import ch.interlis.ili2c.metamodel.CoordType;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.EnumerationType;
import ch.interlis.ili2c.metamodel.Evaluable;
import ch.interlis.ili2c.metamodel.LineType;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.NumericType;
import ch.interlis.ili2c.metamodel.NumericalType;
import ch.interlis.ili2c.metamodel.ObjectPath;
import ch.interlis.ili2c.metamodel.ObjectType;
import ch.interlis.ili2c.metamodel.PolylineType;
import ch.interlis.ili2c.metamodel.PrecisionDecimal;
import ch.interlis.ili2c.metamodel.ReferenceType;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.SurfaceOrAreaType;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.TextType;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.View;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;
import ch.interlis.iom_j.itf.EnumCodeMapper;
import ch.interlis.iom_j.itf.ModelUtilities;

public class AbstractRecordConverter {
	private EnumCodeMapper enumTypes=new EnumCodeMapper();
	protected TransferDescription td=null;
	protected ch.ehi.ili2db.mapping.NameMapping ili2sqlName=null;
	private String schemaName=null;
	protected String defaultCrsAuthority=null;
	protected String defaultCrsCode=null;
	private String createEnumTable=null;
	protected boolean createStdCols=false;
	protected boolean createEnumTxtCol=false;
	protected boolean createEnumColAsItfCode=false;
	protected boolean createIliTidCol=false;
	protected boolean createTypeDiscriminator=false;
	protected boolean createGenericStructRef=false;
	protected boolean sqlEnableNull=true;
	protected boolean strokeArcs=true;
	protected boolean createBasketCol=false;
	protected boolean createItfLineTables=false;
	protected boolean createItfAreaRef=false;
	protected boolean createFk=false;
	protected boolean createFkIdx=false;
	protected boolean isIli1Model=false;
	protected boolean deleteExistingData=false;
	protected String colT_ID=null;
	private String uuid_default_value=null;
	private DbIdGen idGen=null;
	protected TrafoConfig trafoConfig=null;
	public AbstractRecordConverter(TransferDescription td1,ch.ehi.ili2db.mapping.NameMapping ili2sqlName,ch.ehi.ili2db.gui.Config config,DbIdGen idGen1, TrafoConfig trafoConfig1){
		td=td1;
		this.defaultCrsAuthority=config.getDefaultSrsAuthority();
		this.defaultCrsCode=config.getDefaultSrsCode();
		this.ili2sqlName=ili2sqlName;
		trafoConfig=trafoConfig1;
		createEnumTable=config.getCreateEnumDefs();
		createEnumColAsItfCode=config.CREATE_ENUMCOL_AS_ITFCODE_YES.equals(config.getCreateEnumColAsItfCode());
		createStdCols=config.CREATE_STD_COLS_ALL.equals(config.getCreateStdCols());
		createEnumTxtCol=config.CREATE_ENUM_TXT_COL.equals(config.getCreateEnumCols());
		createFk=config.CREATE_FK_YES.equals(config.getCreateFk());
		createFkIdx=config.CREATE_FKIDX_YES.equals(config.getCreateFkIdx());
		colT_ID=config.getColT_ID();
		if(colT_ID==null){
			colT_ID=DbNames.T_ID_COL;
		}
		uuid_default_value=config.getUuidDefaultValue();
		this.idGen=idGen1;
		schemaName=config.getDbschema();

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
		
	}
	public DbColGeometry generatePolylineType(LineType type, String attrName) {
		DbColGeometry ret=new DbColGeometry();
		boolean compoundCurve=false;
		if(!strokeArcs){
			compoundCurve=true;
		}
		ret.setType(compoundCurve ? DbColGeometry.COMPOUNDCURVE : DbColGeometry.LINESTRING);
		// TODO get crs from ili
		ret.setSrsAuth(defaultCrsAuthority);
		ret.setSrsId(defaultCrsCode);
		Domain coordDomain=type.getControlPointDomain();
		if(coordDomain!=null){
			CoordType coord=(CoordType)coordDomain.getType();
			ret.setDimension(coord.getDimensions().length);
			setBB(ret, coord,attrName);
		}
		return ret;
	}
		public DbColId addKeyCol(DbTable table) {
			  DbColId dbColId=new DbColId();
			  dbColId.setName(colT_ID);
			  dbColId.setNotNull(true);
			  dbColId.setPrimaryKey(true);
			  dbColId.setDefaultValue(idGen.getDefaultValueSql());
			  table.addColumn(dbColId);
			  return dbColId;
		}
		public void addIliTidCol(DbTable dbTable,Viewable aclass) {
			if(isUuidOid(td,aclass)){
				DbColUuid dbColIliTid= new DbColUuid();
				dbColIliTid.setName(DbNames.T_ILI_TID_COL);
				// CREATE EXTENSION "uuid-ossp";
				dbColIliTid.setDefaultValue(uuid_default_value);
				dbTable.addColumn(dbColIliTid);
			}else{
				DbColVarchar dbColIliTid=new DbColVarchar();
				dbColIliTid.setName(DbNames.T_ILI_TID_COL);
				dbColIliTid.setSize(200);
				dbTable.addColumn(dbColIliTid);
			}
		}
		
	protected String getSqlRoleName(RoleDef def){
		return ili2sqlName.mapIliRoleDef(def);
	}
	protected String getSqlAttrName(AttributeDef def){
		return ili2sqlName.mapIliAttributeDef(def);
	}
	public DbTableName getSqlTableName(Viewable def){
		String sqlname=ili2sqlName.mapIliClassDef(def);
		return new DbTableName(schemaName,sqlname);
	}
	public static boolean isUuidOid(TransferDescription td,Viewable aclass) {
		if(aclass instanceof AbstractClassDef){
			Domain oid=((AbstractClassDef<AbstractLeafElement>) aclass).getOid();
			if(oid==td.INTERLIS.UUIDOID){
				return true;
			}
		}
		return false;
	}
	public static void addStdCol(DbTable table) {
		DbColumn dbCol=new DbColDateTime();
		dbCol.setName(DbNames.T_LAST_CHANGE_COL);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		dbCol=new DbColDateTime();
		dbCol.setName(DbNames.T_CREATE_DATE_COL);
		dbCol.setNotNull(true);
		table.addColumn(dbCol);
	
		DbColVarchar dbColUsr=new DbColVarchar();
		dbColUsr.setName(DbNames.T_USER_COL);
		dbColUsr.setNotNull(true);
		dbColUsr.setSize(40);
		table.addColumn(dbColUsr);
	}
	protected void setBB(DbColGeometry ret, CoordType coord,String scopedAttrName) {
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
	protected int mapXtfCode2ItfCode(EnumerationType type,String xtfCode)
	{
		return Integer.parseInt(enumTypes.mapXtfCode2ItfCode(type, xtfCode));
	}	
	protected String mapItfCode2XtfCode(EnumerationType type,int itfCode)
	{
		return enumTypes.mapItfCode2XtfCode(type, Integer.toString(itfCode));
	}	
	
}
