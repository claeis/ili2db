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
import ch.ehi.ili2db.base.Ili2dbException;

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
	private NameMapping(){};
	public NameMapping(ch.ehi.ili2db.gui.Config config)
	{
		_maxSqlNameLength=Integer.parseInt(config.getMaxSqlNameLength());
		if(config.NAME_OPTIMIZATION_DISABLE.equals(config.getNameOptimization())){
			nameing=FULL_QUALIFIED_NAMES;
		}else if(config.NAME_OPTIMIZATION_TOPIC.equals(config.getNameOptimization())){
			nameing=TOPIC_QUALIFIED_NAMES;
		}else{
			nameing=UNQUALIFIED_NAMES;
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
		String sqlTableName=normalizeSqlName(ret.toString());
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
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,def.getName(),null,getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapIliEnumAttributeDefAsTable(ch.interlis.ili2c.metamodel.AttributeDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			ch.interlis.ili2c.metamodel.Viewable aclass=(ch.interlis.ili2c.metamodel.Viewable)def.getContainer(ch.interlis.ili2c.metamodel.Viewable.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapGeometryAsTable(ch.interlis.ili2c.metamodel.AttributeDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			ch.interlis.ili2c.metamodel.Viewable aclass=(ch.interlis.ili2c.metamodel.Viewable)def.getContainer(ch.interlis.ili2c.metamodel.Viewable.class);
			sqlname=makeSqlTableName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapIliAttributeDefReverse(ch.interlis.ili2c.metamodel.AttributeDef def,String ownerSqlTablename,String targetSqlTablename){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)columnMapping.getSqlName(iliname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
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
			sqlname=shortcutName(def.getName(),getMaxSqlNameLength()-6);
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename,sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,targetSqlTablename);
		}
		return sqlname;
	}
	public String mapIliAttributeDef(ch.interlis.ili2c.metamodel.AttributeDef def,String ownerSqlTablename,String targetSqlTablename,boolean hasMultipleTargets){
		String iliqname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
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
	public String mapIliAttributeDef(ch.interlis.ili2c.metamodel.AttributeDef def,String ownerSqlTablename,String targetSqlTablename){
		String iliqname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=columnMapping.getSqlName(iliqname,ownerSqlTablename,targetSqlTablename);
		if(sqlname==null){
			sqlname=shortcutName(def.getName(),getMaxSqlNameLength());
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
			sqlname=shortcutName(suffix,getMaxSqlNameLength());
			sqlname=makeValidSqlName(sqlname);
			sqlname=makeSqlColNameUnique(ownerSqlTablename, sqlname);
			columnMapping.addAttrNameMapping(iliqname,sqlname,ownerSqlTablename,null);
		}
		return sqlname;
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
	private Set<String> kws=null;
	private String makeValidSqlName(String name)
	{
		if(kws==null){
			kws=Sql2003kw.getKeywords();			
			kws.add("TEXT");
		}
		String ucname=name.toUpperCase();
		while(kws.contains(ucname)){
			  name= "a"+name;
			  ucname=name.toUpperCase();
		}
		return name;
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
			throw new Ili2dbException("failed to read class-mapping-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateTableMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> exstEntries=readTableMappingTableEntries(conn,schema);
		String mapTabName=DbNames.CLASSNAME_TAB;
		if(schema!=null){
			mapTabName=schema+"."+mapTabName;
		}
		// create table
		try{

			// insert mapping entries
			String stmt="INSERT INTO "+mapTabName+" ("+DbNames.CLASSNAME_TAB_ILINAME_COL+","+DbNames.CLASSNAME_TAB_SQLNAME_COL+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
			String iliname=null;
			String sqlname=null;
			try{
				java.util.Iterator<String> entri=classNameIli2sql.keySet().iterator();
				while(entri.hasNext()){
					iliname=entri.next();
					if(!exstEntries.contains(iliname)){
						sqlname=classNameIli2sql.get(iliname);
						ps.setString(1, iliname);
						ps.setString(2, sqlname);
						ps.executeUpdate();
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert classname-mapping "+iliname,ex);
			}finally{
				ps.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update mapping-table "+mapTabName,ex);
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
		try{
			
			dbstmt = conn.createStatement();
			java.sql.ResultSet rs=dbstmt.executeQuery(stmt);
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
			if(dbstmt!=null){
				try{
					dbstmt.close();
				}catch(java.sql.SQLException ex){		
					throw new Ili2dbException("failed to close query of "+mapTableName,ex);
				}
			}
		}

	}
	public void updateAttrMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		columnMapping.updateAttrMappingTable(conn, schema);
	}
	public void readAttrMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		columnMapping.readAttrMappingTable(conn, schema);
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
