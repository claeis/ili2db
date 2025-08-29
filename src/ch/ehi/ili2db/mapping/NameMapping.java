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
package ch.ehi.ili2db.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.StatementExecutionHelper;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.metaattr.IliMetaAttrNames;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Enumeration;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.ili2c.metamodel.ViewableTransferElement;

/** make names unique and conforming to the underlying database
 * @author ce
 * @version $Revision: 1.0 $ $Date: 04.04.2005 $
 */
public class NameMapping {
	public static int DEFAULT_NAME_LENGTH=60;
	private int _maxSqlNameLength=DEFAULT_NAME_LENGTH;
	/** mapping from a qualified interlis viewable or attribute name to a sql table name.
	 * Maintained by addMapping().
	 */
	private HashMap<String,String> classNameIli2sql=new HashMap<String,String>();
	/** mapping from a sql table name to a qualified interlis viewable or attribute name.
	 * Maintained by addMapping().
	 */
	private HashMap<String,String> classNameSql2ili=new HashMap<String,String>();
	private ColumnNameMapping columnMapping=new ColumnNameMapping();
	private int nameing=UNQUALIFIED_NAMES;
	private static final int UNQUALIFIED_NAMES=0;
	private static final int TOPIC_QUALIFIED_NAMES=1;
	private static final int FULL_QUALIFIED_NAMES=2;
    private boolean removeUnderscoreFromEnumDispName=false;
	private boolean useEpsg=false;
    private boolean isVer3_export=false;
	private Integer batchSize = null;
	private String languagePath[]=null;
    private TranslationNameMapper translationNameMapper=null;
	private NameMapping(){};
	public NameMapping(TransferDescription td,ch.ehi.ili2db.gui.Config config)
	{
		_maxSqlNameLength=Integer.parseInt(config.getMaxSqlNameLength());
		if(config.NAME_OPTIMIZATION_DISABLE.equals(config.getNameOptimization())){
			nameing=FULL_QUALIFIED_NAMES;
		}else if(config.NAME_OPTIMIZATION_TOPIC.equals(config.getNameOptimization())){
			nameing=TOPIC_QUALIFIED_NAMES;
		}else{
			nameing=UNQUALIFIED_NAMES;
		}
        removeUnderscoreFromEnumDispName=Config.BEAUTIFY_ENUM_DISPNAME_UNDERSCORE.equals(config.getBeautifyEnumDispName());
		useEpsg=config.useEpsgInNames();
		isVer3_export=config.isVer3_export();
		batchSize = config.getBatchSize();
		String path=config.getNameLanguage();
		if(path!=null) {
		    languagePath=path.split(";");
		    translationNameMapper=new TranslationNameMapper(td,config);
		}
	}
	private String makeSqlTableName(String modelName,String topicName,String className,String attrName,int maxSqlNameLength)
	{
		StringBuffer ret=new StringBuffer();
		String modelSqlName=modelName;
		String topicSqlName=topicName;
		String classSqlName=className;
		String attrSqlName=attrName;
		int maxClassNameLength=maxSqlNameLength;
		if(attrName!=null){
			maxClassNameLength=maxClassNameLength-5;
		}
		if(nameing==UNQUALIFIED_NAMES){
		}else if(nameing==TOPIC_QUALIFIED_NAMES){
			if(topicSqlName==null){
			}else{
				ret.append(shortcutName(topicSqlName,maxClassNameLength/4+2));
				ret.append("_");
			}
		}else if(nameing==FULL_QUALIFIED_NAMES){
			if(topicSqlName==null){
				ret.append(shortcutName(modelSqlName,maxClassNameLength/2-2));
			}else{
				ret.append(shortcutName(modelSqlName,maxClassNameLength/4-1));
				ret.append(shortcutName(topicSqlName,maxClassNameLength/4+2));
			}
			ret.append("_");
		}else{
			throw new IllegalStateException("nameing=="+nameing);
		}
		ret.append(shortcutName(classSqlName,maxClassNameLength-ret.length()));
		if(attrSqlName!=null){
			ret.append("_");
			ret.append(shortcutName(attrSqlName,maxSqlNameLength-ret.length()));
		}
        String sqlTableName=makeValidSqlName(ret.toString());
		sqlTableName=normalizeSqlName(sqlTableName);
		if((nameing!=FULL_QUALIFIED_NAMES) && existsSqlTableName(sqlTableName)){
			// try full qualified name
			// reset ret to empty string
			ret.setLength(0);
			maxClassNameLength=maxSqlNameLength;
			if(attrName!=null){
				maxClassNameLength=maxClassNameLength-5;
			}
			if(topicSqlName==null){
				ret.append(shortcutName(modelSqlName,maxClassNameLength/2-2));
			}else{
				ret.append(shortcutName(modelSqlName,maxClassNameLength/4-1));
				ret.append(shortcutName(topicSqlName,maxClassNameLength/4+2));
			}
			ret.append("_");
			ret.append(shortcutName(classSqlName,maxClassNameLength-ret.length()));
			if(attrSqlName!=null){
				ret.append("_");
				ret.append(shortcutName(attrSqlName,maxSqlNameLength-ret.length()));
			}
			sqlTableName=normalizeSqlName(ret.toString());
		}
		if(existsSqlTableName(sqlTableName)){
			sqlTableName=makeSqlTableNameUnique(sqlTableName);
		}
		return sqlTableName;
	}
	public String mapSqlTableName(String sqlname){
		sqlname = normalizeSqlName(sqlname);
		return classNameSql2ili.get(sqlname);
	}
	private void addTableNameMapping(String iliname,String sqlname)
	{
		classNameIli2sql.put(iliname,sqlname);
		classNameSql2ili.put(sqlname,iliname);
	}
	private boolean existsSqlTableName(String sqlname)
	{
		sqlname = normalizeSqlName(sqlname);
		return classNameSql2ili.containsKey(sqlname);
	}
	private String normalizeSqlName(String sqlname) {
		if(sqlname.length()>getMaxSqlNameLength()){
			sqlname=sqlname.substring(0,getMaxSqlNameLength());
		}
		sqlname=sqlname.toLowerCase();
		return sqlname;
	}
	public String mapIliClassDef(ch.interlis.ili2c.metamodel.Viewable def)
	{
		String iliname=def.getScopedName(null);
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
	        def=(Viewable) getTranslatedElement(def);
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,def.getName(),null,getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapIliDomainDef(ch.interlis.ili2c.metamodel.Domain def)
	{
		String iliname=def.getScopedName(null);
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
	        def=(Domain) getTranslatedElement(def);
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,def.getName(),null,getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
    public String beautifyEnumDispName(String value) {
        if(removeUnderscoreFromEnumDispName){
            return value.replace('_', ' ');
        }
        return value;
    }
	public String mapIliEnumAttributeDefAsTable(ch.interlis.ili2c.metamodel.AttributeDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
	        def=(AttributeDef) getTranslatedElement(def);
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			ch.interlis.ili2c.metamodel.Viewable aclass=(ch.interlis.ili2c.metamodel.Viewable)def.getContainer(ch.interlis.ili2c.metamodel.Viewable.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapItfGeometryAsTable(Viewable aclass,ch.interlis.ili2c.metamodel.AttributeDef def,Integer epsgCode){
		String iliqname=aclass.getScopedName(null)+"_"+def.getName();
		String sqlname=null;
		if(useEpsg && epsgCode!=null) {
            String iliqname2 = iliqname+":"+epsgCode;
            sqlname=(String)classNameIli2sql.get(iliqname2);
            if(sqlname==null) {
                // pre 3.13.x
                sqlname=(String)classNameIli2sql.get(iliqname);
            }
            iliqname=iliqname2;
		}else {
	        sqlname=(String)classNameIli2sql.get(iliqname);
		}
		if(sqlname==null){
	        aclass=(Viewable) getTranslatedElement(aclass);
	        def=(AttributeDef) getTranslatedElement(def);
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)aclass.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)aclass.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			if(useEpsg && epsgCode!=null) {
                sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName()+"_"+epsgCode,getMaxSqlNameLength());
			}else {
	            sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
			}
			addTableNameMapping(iliqname,sqlname);
		}
		return sqlname;
	}
    public String mapAttributeAsTable(Viewable aclass,ch.interlis.ili2c.metamodel.AttributeDef def,Integer epsgCode){
        String iliqname=def.getContainer().getScopedName(null)+"."+def.getName();
        String sqlname=null;
        if(useEpsg && epsgCode!=null) {
            iliqname = iliqname+":"+epsgCode+"("+aclass.getScopedName()+")";
            sqlname=(String)classNameIli2sql.get(iliqname);
        }else {
            iliqname=iliqname+"("+aclass.getScopedName()+")";
            sqlname=(String)classNameIli2sql.get(iliqname);
        }
        if(sqlname==null){
            aclass=(Viewable) getTranslatedElement(aclass);
            def=(AttributeDef) getTranslatedElement(def);
            ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)aclass.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
            ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)aclass.getContainer(ch.interlis.ili2c.metamodel.Model.class);
            if(useEpsg && epsgCode!=null) {
                sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName()+"_"+epsgCode,getMaxSqlNameLength());
            }else {
                sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
            }
            addTableNameMapping(iliqname,sqlname);
        }
        return sqlname;
    }
	public String mapIliAttributeDefReverse(ch.interlis.ili2c.metamodel.AttributeDef def,String ownerSqlTablename,String targetSqlTablename){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)columnMapping.getSqlName(iliname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
	        def=(AttributeDef) getTranslatedElement(def);
			/*
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)targetTable.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)targetTable.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			sqlname=makeSqlAttrName(model.getName(),topic!=null ? topic.getName():null,targetTable.getName(),def.getName(),getMaxSqlNameLength());
			*/
			sqlname=shortcutName(targetSqlTablename,def.getName(),getMaxSqlNameLength()-6);
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename, sqlname);
			columnMapping.addAttrNameMapping(iliname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
	public String mapIliRoleDef(ch.interlis.ili2c.metamodel.RoleDef def,String ownerSqlTablename,String targetSqlTablename,boolean hasMultipleTargets){
		String iliqname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
	        def=(ch.interlis.ili2c.metamodel.RoleDef) getTranslatedElement(def);
			if(hasMultipleTargets){
				sqlname=shortcutName(def.getName(),targetSqlTablename,getMaxSqlNameLength()-6);
			}else{
				sqlname=shortcutName(def.getName(),getMaxSqlNameLength()-6);
			}
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename,sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
	public String mapIliRoleDef(ch.interlis.ili2c.metamodel.RoleDef def,String ownerSqlTablename,String targetSqlTablename){
		String iliqname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
	        def=(ch.interlis.ili2c.metamodel.RoleDef) getTranslatedElement(def);
			sqlname=shortcutName(def.getName(),getMaxSqlNameLength()-6);
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename,sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
	@Deprecated
    public String mapIliAttributeDef(ch.interlis.ili2c.metamodel.AttributeDef def,String ownerSqlTablename,String targetSqlTablename,boolean hasMultipleTargets){
        return mapIliAttributeDef(new StructAttrPath(new ViewableTransferElement(def)),ownerSqlTablename,targetSqlTablename,hasMultipleTargets);
    
    }
	public String mapIliAttributeDef(StructAttrPath def,String ownerSqlTablename,String targetSqlTablename,boolean hasMultipleTargets){
		String iliqname=def.getIliQName();
		String sqlname=columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
	        def=getTranslatedElement(def);
			if(hasMultipleTargets){
				sqlname=shortcutName(def.getName(),targetSqlTablename,getMaxSqlNameLength()-6);
			}else{
				sqlname=shortcutName(def.getName(),getMaxSqlNameLength());
			}
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename,sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
	@Deprecated
    public String mapIliAttributeDef(ch.interlis.ili2c.metamodel.AttributeDef def,Integer epsgCode,String ownerSqlTablename,String targetSqlTablename){
        return mapIliAttributeDef(new StructAttrPath(new ch.interlis.ili2c.metamodel.ViewableTransferElement(def)),epsgCode,ownerSqlTablename,targetSqlTablename);
    }
	public String mapIliAttributeDef(StructAttrPath def,Integer epsgCode,String ownerSqlTablename,String targetSqlTablename){
		String iliqname=def.getIliQName();
		String sqlname=null;
		if(useEpsg && epsgCode!=null) {
	        String iliqname2 = iliqname+":"+epsgCode;
            sqlname=columnMapping.getSqlName(iliqname2,ownerSqlTablename,targetSqlTablename);
	        if(sqlname==null) {
	            // pre 3.13.x
	            sqlname=columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
	        }
	        iliqname=iliqname2;
		}else {
	        sqlname=columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		}
		// not yet known attribute?
		if(sqlname==null){
	        def=getTranslatedElement(def);
		    if(useEpsg && epsgCode!=null) {
	            sqlname=shortcutName(def.getName()+"_"+epsgCode,getMaxSqlNameLength());
		    }else {
	            sqlname=shortcutName(def.getName(),getMaxSqlNameLength());
		    }
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename,sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
    private String mapIliAttrName(String ownerSqlTablename,ch.interlis.ili2c.metamodel.AttributeDef def,String suffix)
	{
		String iliqname=def.getContainer().getScopedName(null)+"."+def.getName()+"."+suffix;
		String sqlname=(String)columnMapping.getSqlName(iliqname,ownerSqlTablename,null);
		if(sqlname==null){
	        def=(AttributeDef) getTranslatedElement(def);
			sqlname=shortcutName(suffix,getMaxSqlNameLength());
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename, sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,null);
		}
		return sqlname;
	}
	public ch.interlis.ili2c.metamodel.Element getTranslatedElement(ch.interlis.ili2c.metamodel.Element def) {
        if(translationNameMapper!=null) {
            return translationNameMapper.translateElement(languagePath, def);
        }
        return def;
    }
    private StructAttrPath getTranslatedElement(StructAttrPath def) {
        StructAttrPath.PathEl srcPathv[]=def.getPath();
        StructAttrPath.PathEl pathv[]=new StructAttrPath.PathEl[srcPathv.length];
        for(int i=0;i<srcPathv.length;i++) {
            if(srcPathv[i] instanceof StructAttrPath.PathElAttr) {
                ch.interlis.ili2c.metamodel.Element srcEle=(ch.interlis.ili2c.metamodel.Element)((StructAttrPath.PathElAttr)srcPathv[i]).getAttr().obj;
                srcEle=getTranslatedElement(srcEle);
                pathv[i]=new StructAttrPath.PathElAttr(new ViewableTransferElement(srcEle),srcPathv[i].getIdx());
            }else {
                pathv[i]=srcPathv[i];
            }
        }
        return new StructAttrPath(pathv);
    }
    private String makeSqlColNameUnique(String ownerSqlTablename, String sqlname) {
		sqlname=normalizeSqlName(sqlname);
		String base=sqlname;
		int c=1;
		while(columnMapping.existsSqlName(ownerSqlTablename,sqlname)){
			sqlname=base+Integer.toString(c++);
		}
		return sqlname;
	}
	private String makeSqlTableNameUnique(String sqlname) {
		String base=sqlname;
		int c=1;
		while(existsSqlTableName(sqlname)){
			sqlname=base+Integer.toString(c++);
		}
		return sqlname;
	}
	private static Set<String> kws=null;
	private String makeValidSqlName(String name)
	{
		initReservedWordList();
		String ucname=name.toUpperCase();
		while(kws.contains(ucname)){
			  name= "a"+name;
			  ucname=name.toUpperCase();
		}
		return name;
	}
    public static boolean isValidSqlName(String name)
    {
        initReservedWordList();
        String ucname=name.toUpperCase();
        return !kws.contains(ucname);
    }
    private static void initReservedWordList() {
        if(kws==null){
			kws=getSqlKeywords();
		}
    }
    public static Set<String> getSqlKeywords() {
        Set<String> ret=new HashSet<String>();
        ret.addAll(Sql2003kw.getKeywords());			
        ret.addAll(PostgresqlKw.getKeywords());
        ret.addAll(SqliteKw.getKeywords());
        ret.addAll(MssqlKw.getKeywords());
        ret.addAll(MysqlKw.getKeywords());
        ret.addAll(OracleKw.getKeywords());
        ret.add("TEXT");
        ret.add("OBJECTID"); // ili2fgdb / common primary key column name in ESRI world
        return ret;
    }
	private static String shortcutName(String aname, int maxlen)
	{
		StringBuffer name=new StringBuffer(aname);
		// number of charcters to remove
	int stripc=name.length()-maxlen;
	if(stripc<=0)return aname;
		// remove vocals
	for(int i=name.length()-4;i>=3;i--){
		char c=name.charAt(i);
		if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u'
			|| c=='A' || c=='E' || c=='I' || c=='O' || c=='U'){
			name.deleteCharAt(i);
			stripc--;
			if(stripc==0)return name.toString();
		}
	}
		// still to long
		// remove from the middle of the name
		int start=(name.length()-stripc)/2;
	name.delete(start,start+stripc);
	// ASSERT(!name.IsEmpty());
	return name.toString();
	}
	private static String shortcutName(String modelName,String attrName,int maxSqlNameLength)
	{
		StringBuffer ret=new StringBuffer();
		String modelSqlName=modelName;
		String attrSqlName=attrName;
		int maxClassNameLength=maxSqlNameLength-5;
		ret.append(shortcutName(modelSqlName,maxClassNameLength/2-2));
		ret.append("_");
		ret.append(shortcutName(attrSqlName,maxSqlNameLength-ret.length()));
		return ret.toString();
	}
	private static HashSet<String> readTableMappingTableEntries(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> ret=new HashSet<String>();
		String sqlName=DbNames.CLASSNAME_TAB;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
			exstStmt="SELECT "+DbNames.CLASSNAME_TAB_ILINAME_COL+" FROM "+sqlName;
			EhiLogger.traceBackendCmd(exstStmt);
			java.sql.PreparedStatement exstPrepStmt = null;
            java.sql.ResultSet rs=null;
			try{
	            exstPrepStmt = conn.prepareStatement(exstStmt);
				rs=exstPrepStmt.executeQuery();
				while(rs.next()){
					String iliCode=rs.getString(1);
					ret.add(iliCode);
				}
			}finally{
			    if(rs!=null) {
			        rs.close();
			        rs=null;
			    }
			    if(exstPrepStmt!=null) {
	                exstPrepStmt.close();
	                exstPrepStmt=null;
			    }
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to read class-mapping-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateTableMappingTable(GeneratorJdbc gen, java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
        String mapTabName=DbNames.CLASSNAME_TAB;
        if(schema!=null){
            mapTabName=schema+"."+mapTabName;
        }
        
		if(conn!=null) {
	        HashSet<String> exstEntries=readTableMappingTableEntries(conn,schema);
	        try{

	            // insert mapping entries
	            String stmt="INSERT INTO "+mapTabName+" ("+DbNames.CLASSNAME_TAB_ILINAME_COL+","+DbNames.CLASSNAME_TAB_SQLNAME_COL+") VALUES (?,?)";
	            EhiLogger.traceBackendCmd(stmt);
	            java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
	            String iliname=null;
	            String sqlname=null;
				StatementExecutionHelper seHelper = new StatementExecutionHelper(batchSize);

	            try{
	                java.util.Iterator<String> entri=classNameIli2sql.keySet().iterator();
					long start = System.currentTimeMillis();
	                while(entri.hasNext()){
	                    iliname=entri.next();
	                    if(!exstEntries.contains(iliname)){
	                        sqlname=classNameIli2sql.get(iliname);
	                        ps.setString(1, iliname);
	                        ps.setString(2, sqlname);
	                        seHelper.write(ps);
	                    }

	                }

					seHelper.flush(ps);

	            }catch(java.sql.SQLException ex){
	                throw new Ili2dbException("failed to insert classname-mapping "+iliname,ex);
	            }finally{
	                ps.close();
	            }
	        }catch(java.sql.SQLException ex){       
	            throw new Ili2dbException("failed to update mapping-table "+mapTabName,ex);
	        }
		}
		if(gen!=null){
		    // create inserts
            java.util.Iterator<String> entri=classNameIli2sql.keySet().iterator();
            while(entri.hasNext()){
                String iliname=entri.next();
                String sqlname=classNameIli2sql.get(iliname);
                String stmt="INSERT INTO "+mapTabName+" ("+DbNames.CLASSNAME_TAB_ILINAME_COL+","+DbNames.CLASSNAME_TAB_SQLNAME_COL+") VALUES ("+Ili2db.quoteSqlStringValue(iliname)+","+Ili2db.quoteSqlStringValue(sqlname)+")";
                gen.addCreateLine(gen.new Stmt(stmt));
            }
		    
		}

	}
	public void readTableMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String mapTableName=DbNames.CLASSNAME_TAB;
		if(schema!=null){
			mapTableName=schema+"."+mapTableName;
		}
		// create table
		String stmt="SELECT "+DbNames.CLASSNAME_TAB_ILINAME_COL+", "+DbNames.CLASSNAME_TAB_SQLNAME_COL+" FROM "+mapTableName;
		java.sql.Statement dbstmt = null;
        java.sql.ResultSet rs=null;
		try{
			
			dbstmt = conn.createStatement();
			rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String iliname=rs.getString(DbNames.CLASSNAME_TAB_ILINAME_COL);
				String sqlname=rs.getString(DbNames.CLASSNAME_TAB_SQLNAME_COL);
				//EhiLogger.debug("map: "+iliname+"->"+sqlname);
				if(classNameIli2sql.get(iliname)==null){
					sqlname=normalizeSqlName(sqlname);
					addTableNameMapping(iliname,sqlname);
				}
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to query mapping-table "+mapTableName,ex);
		}finally{
            if(rs!=null){
                try{
                    rs.close();
                    rs=null;
                }catch(java.sql.SQLException ex){       
                    throw new Ili2dbException("failed to close query of "+mapTableName,ex);
                }
            }
			if(dbstmt!=null){
				try{
					dbstmt.close();
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to close query of "+mapTableName,ex);
				}
				dbstmt=null;
			}
		}

	}
	public void updateAttrMappingTable(GeneratorJdbc gen, java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		columnMapping.updateAttrMappingTable(gen,conn, schema);
	}
	public void readAttrMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		columnMapping.readAttrMappingTable(conn, schema,isVer3_export);
	}

	public int getMaxSqlNameLength()
	{
		return _maxSqlNameLength;
	}
	
	public String getSqlColNameItfLineTableRefAttr(ch.interlis.ili2c.metamodel.AttributeDef attr,String ownerSqlTablename)
	{
		return mapIliAttrName(ownerSqlTablename,attr,DbNames.ITF_LINETABLE_MAINTABLEREF_ILI_SUFFIX);
	}
	public String getSqlColNameItfLineTableGeomAttr(ch.interlis.ili2c.metamodel.AttributeDef attr,String ownerSqlTablename)
	{
		return mapIliAttrName(ownerSqlTablename,attr,DbNames.ITF_LINETABLE_GEOMATTR_ILI_SUFFIX);
	}
}
