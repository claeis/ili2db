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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import ch.ehi.basics.logging.EhiLogger;

import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.*;

import ch.ehi.ili2db.mapping.*;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.sqlgen.repository.DbTableName;

/** make names unique and conforming to the underlying database
 * @author ce
 * @version $Revision: 1.0 $ $Date: 04.04.2005 $
 */
public class Mapping {
	public static String SQL_T_ILI2DB_CLASSNAME="T_ILI2DB_CLASSNAME";
	public static String SQL_T_ILI2DB_ATTRNAME="T_ILI2DB_ATTRNAME";
	private static String SQL_IliName="IliName";
	private static String SQL_SqlName="SqlName";
	public static int DEFAULT_NAME_LENGTH=60;
	private int _maxSqlNameLength=DEFAULT_NAME_LENGTH;
	/** mapping from a qualified interlis viewable or attribute name to a sql table name.
	 * Maintained by addMapping().
	 */
	private HashMap<String,String> classNameIli2sql=new HashMap<String,String>();
	/** mapping from a sql table name to a qualified interlis viewable or attribute name.
	 * Maintained by addMApping().
	 */
	private HashMap<String,String> classNameSql2ili=new HashMap<String,String>();
	/** mapping from a qualified interlis attribute name to a sql column name.
	 * Maintained by addMapping().
	 */
	private HashMap<String,String> attrNameIli2sql=new HashMap<String,String>();
	/** mapping from a sql column name to a qualified interlis attribute name.
	 * Maintained by addMApping().
	 */
	private HashMap<String,String> attrNameSql2ili=new HashMap<String,String>();
	private HashMap deprecatedConfig=new HashMap();
	private Mapping(){};
	public Mapping(ch.ehi.ili2db.gui.Config config)
	{
		_maxSqlNameLength=Integer.parseInt(config.getMaxSqlNameLength());
	}
	/** @deprecated
	 */
	public void readDeprecatedConfig(String filename)
	throws Ili2dbException
	{
		java.io.InputStream inputFile=null;
		try{
			inputFile=new java.io.FileInputStream(new java.io.File (filename));
			XtfReader reader=new XtfReader(inputFile);
			IoxEvent event=reader.read();
			while(event!=null){
				if(event instanceof ObjectEvent){
					IomObject obj=((ObjectEvent)event).getIomObject();
					String tag=obj.getobjecttag();
					if(tag.equals("Ili2ora05.MappingConfig.ModelDef")){
						String iliName=obj.getattrvalue("iliName");
						String sqlName=obj.getattrvalue("sqlName");
						ModelDef model=new ModelDef();
						model.setIliName(iliName);
						if(sqlName!=null){
							model.setSqlName(sqlName);
						}
						deprecatedConfig.put(iliName,model);
						int topicc=obj.getattrvaluecount("definition");
						for(int topici=0;topici<topicc;topici++){
							handleModelMember(model,obj.getattrobj("definition",topici));
						}
					}else{
						throw new Ili2dbException("("+obj.getobjectline()+","+obj.getobjectcol()+"): unknown tag "+tag);
					}
				}
			}
		}catch(ch.interlis.iox.IoxException ex){
			throw new Ili2dbException(ex);
		}catch(java.io.IOException ex){
			throw new Ili2dbException(ex);
		}finally{
			if(inputFile!=null){
				try{
					inputFile.close();
				}catch(java.io.IOException ex){
					throw new Ili2dbException(ex);
				}
				inputFile=null;
			}
		}
				
	}
	private void handleModelMember(ModelDef model,IomObject topicObj){
		if(topicObj!=null){
			String tag=topicObj.getobjecttag();
			if(tag.equals("Ili2ora05.MappingConfig.TopicDef")){
				String iliName=topicObj.getattrvalue("iliName");
				String sqlName=topicObj.getattrvalue("sqlName");
				TopicDef topic=new TopicDef();
				topic.setIliName(iliName);
				if(sqlName!=null){
					topic.setSqlName(sqlName);
				}
				model.addDefinition(topic);
				int classc=topicObj.getattrvaluecount("definition");
				for(int memberi=0;memberi<classc;memberi++){
					handleTopicMember(topic,topicObj.getattrobj("definition",memberi));
				}
			}else if(tag.equals("Ili2ora05.MappingConfig.ClassDef")){
				String iliName=topicObj.getattrvalue("iliName");
				String sqlName=topicObj.getattrvalue("sqlName");
				ClassDef aclass=new ClassDef();
				aclass.setIliName(iliName);
				if(sqlName!=null){
					aclass.setSqlName(sqlName);
				}
				model.addDefinition(aclass);
				int attrc=topicObj.getattrvaluecount("attribute");
				for(int memberi=0;memberi<attrc;memberi++){
					handleAttribute(aclass,topicObj.getattrobj("attribute",memberi));
				}
			}else{
				EhiLogger.logError("("+topicObj.getobjectline()+","+topicObj.getobjectcol()+"): unknown tag "+tag);
			}
		}
	}
	private void handleTopicMember(TopicDef topic,IomObject classObj){
		if(classObj!=null){
			String tag=classObj.getobjecttag();
			if(tag.equals("Ili2ora05.MappingConfig.ClassDef")){
				String iliName=classObj.getattrvalue("iliName");
				String sqlName=classObj.getattrvalue("sqlName");
				ClassDef aclass=new ClassDef();
				aclass.setIliName(iliName);
				if(sqlName!=null){
					aclass.setSqlName(sqlName);
				}
				topic.addDefinition(aclass);
				int attrc=classObj.getattrvaluecount("attribute");
				for(int memberi=0;memberi<attrc;memberi++){
					handleAttribute(aclass,classObj.getattrobj("attribute",memberi));
				}
			}else{
				EhiLogger.logError("("+classObj.getobjectline()+","+classObj.getobjectcol()+"): unknown tag "+tag);
			}
		}
	}
	private void handleAttribute(ClassDef aclass,IomObject attrObj){
		if(attrObj!=null){
			String tag=attrObj.getobjecttag();
			if(tag.equals("Ili2ora05.MappingConfig.AttributeDef")){
				String iliName=attrObj.getattrvalue("iliName");
				String sqlName=attrObj.getattrvalue("sqlName");
				AttributeDef attr=new AttributeDef();
				attr.setIliName(iliName);
				if(sqlName!=null){
					attr.setSqlName(sqlName);
				}
				aclass.addAttribute(attr);
			}else{
				EhiLogger.logError("("+attrObj.getobjectline()+","+attrObj.getobjectcol()+"): unknown tag "+tag);
			}
		}
	}
	private String makeSqlTableName(String modelName,String topicName,String className,String attrName,int maxSqlNameLength)
	{
		StringBuffer ret=new StringBuffer();
		String modelSqlName=modelName;
		String topicSqlName=topicName;
		String classSqlName=className;
		String attrSqlName=attrName;
		ModelDef model=(ModelDef)deprecatedConfig.get(modelName);
		if(model!=null){
			String sqlName=model.getSqlName();
			if(sqlName!=null){
				modelSqlName=sqlName;
			}
			if(topicName!=null){
				TopicDef topic=(TopicDef)model.getDefinition(topicName);
				if(topic!=null){
					sqlName=topic.getSqlName();
					if(sqlName!=null){
						topicSqlName=sqlName;
					}
					ClassDef aclass=(ClassDef)topic.getDefinition(className);
					if(aclass!=null){
						sqlName=aclass.getSqlName();
						if(sqlName!=null){
							classSqlName=sqlName;
						}
					}
				}
			}else{
				ClassDef aclass=(ClassDef)model.getDefinition(className);
				if(aclass!=null){
					sqlName=aclass.getSqlName();
					if(sqlName!=null){
						classSqlName=sqlName;
					}
				}
			}
		}
		int maxClassNameLength=maxSqlNameLength;
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
		return ret.toString();
	}
	private String makeSqlAttrName(String modelName,String topicName,String className,String attrName,int maxSqlNameLength)
	{
		StringBuffer ret=new StringBuffer();
		String modelSqlName=modelName;
		String topicSqlName=topicName;
		String classSqlName=className;
		String attrSqlName=attrName;
		int maxClassNameLength=maxSqlNameLength-5;
		if(topicSqlName==null){
			ret.append(shortcutName(modelSqlName,maxClassNameLength/2-2));
		}else{
			ret.append(shortcutName(modelSqlName,maxClassNameLength/4-1));
			ret.append(shortcutName(topicSqlName,maxClassNameLength/4+2));
		}
		ret.append("_");
		ret.append(shortcutName(classSqlName,maxClassNameLength-ret.length()));
		ret.append("_");
		ret.append(shortcutName(attrSqlName,maxSqlNameLength-ret.length()));
		return ret.toString();
	}
	public String mapSqlTableName(String sqlname){
		return (String)classNameSql2ili.get(sqlname);
	}
	private void addTableNameMapping(String iliname,String sqlname)
	{
		classNameIli2sql.put(iliname,sqlname);
		classNameSql2ili.put(sqlname,iliname);
	}
	private void addAttrNameMapping(String iliname,String sqlname)
	{
		attrNameIli2sql.put(iliname,sqlname);
		attrNameSql2ili.put(sqlname,iliname);
	}
	public String defineTableNameMapping(String iliname,String sqlname1)
	{
		String sqlname=(String)classNameIli2sql.get(iliname);
		if(sqlname==null){
			sqlname=makeValidSqlName(shortcutName(sqlname1, getMaxSqlNameLength()));
			addTableNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String defineAttrNameMapping(String iliname,String sqlname1)
	{
		String sqlname=(String)attrNameIli2sql.get(iliname);
		if(sqlname==null){
			sqlname=makeValidSqlName(shortcutName(sqlname1, getMaxSqlNameLength()));
			addAttrNameMapping(iliname,sqlname);
		}
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
	public String mapItfLineTableAsTable(ch.interlis.ili2c.metamodel.AttributeDef def){
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
	public String mapIliAttributeDefQualified(ch.interlis.ili2c.metamodel.AttributeDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)attrNameIli2sql.get(iliname);
		if(sqlname==null){
			ch.interlis.ili2c.metamodel.Topic topic=(ch.interlis.ili2c.metamodel.Topic)def.getContainer(ch.interlis.ili2c.metamodel.Topic.class);
			ch.interlis.ili2c.metamodel.Model model=(ch.interlis.ili2c.metamodel.Model)def.getContainer(ch.interlis.ili2c.metamodel.Model.class);
			ch.interlis.ili2c.metamodel.Viewable aclass=(ch.interlis.ili2c.metamodel.Viewable)def.getContainer(ch.interlis.ili2c.metamodel.Viewable.class);
			sqlname=makeSqlAttrName(model.getName(),topic!=null ? topic.getName():null,aclass.getName(),def.getName(),getMaxSqlNameLength());
			addAttrNameMapping(iliname,sqlname);
		}
		return sqlname;
	}
	public String mapIliRoleDef(ch.interlis.ili2c.metamodel.RoleDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)attrNameIli2sql.get(iliname);
		if(sqlname==null){
			sqlname=shortcutName(def.getName(),getMaxSqlNameLength()-6);
			sqlname=makeValidSqlName(sqlname);
			if(!def.getName().equals(sqlname)){
				addAttrNameMapping(iliname,sqlname);
			}
		}
		return sqlname;
	}
	public String mapIliAttributeDef(ch.interlis.ili2c.metamodel.AttributeDef def){
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName();
		String sqlname=(String)attrNameIli2sql.get(iliname);
		if(sqlname==null){
			sqlname=shortcutName(def.getName(),getMaxSqlNameLength());
			sqlname=makeValidSqlName(sqlname);
			if(!def.getName().equals(sqlname)){
				addAttrNameMapping(iliname,sqlname);
			}
		}
		return sqlname;
	}
	public String mapIliAttrName(ch.interlis.ili2c.metamodel.AttributeDef def,String iliAttrName)
	{
		String iliname=def.getContainer().getScopedName(null)+"."+def.getName()+"."+iliAttrName;
		String sqlname=(String)attrNameIli2sql.get(iliname);
		if(sqlname==null){
			sqlname=shortcutName(iliAttrName,getMaxSqlNameLength());
			sqlname=makeValidSqlName(sqlname);
			if(!iliAttrName.equals(sqlname)){
				addAttrNameMapping(iliname,sqlname);
			}
		}
		return sqlname;
	}
	private String makeValidSqlName(String name)
	{
		if(name.equalsIgnoreCase("Date")){
		  return "adate";
		}
		if(name.equalsIgnoreCase("level")){
		  return "alevel";
		}
		if(name.equalsIgnoreCase("number")){
		  return "anumber";
		}
		// Postgresql
		if(name.equalsIgnoreCase("union")){
			  return "aunion";
			}
		if(name.equalsIgnoreCase("from")){
			  return "afrom";
			}
		if(name.equalsIgnoreCase("to")){
			  return "ato";
			}
		if(name.equalsIgnoreCase("with")){
			  return "awith";
			}
		// MS-ACCESS
		if(name.equalsIgnoreCase("value")){
		  return "avalue";
		}
		if(name.equalsIgnoreCase("text")){
			  return "atext";
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
	static public void addTableMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),SQL_T_ILI2DB_CLASSNAME));
		ch.ehi.sqlgen.repository.DbColVarchar iliClassName=new ch.ehi.sqlgen.repository.DbColVarchar();
		iliClassName.setName(SQL_IliName);
		iliClassName.setNotNull(true);
		iliClassName.setSize(1024);
		iliClassName.setPrimaryKey(true);
		tab.addColumn(iliClassName);
		ch.ehi.sqlgen.repository.DbColVarchar sqlTableName=new ch.ehi.sqlgen.repository.DbColVarchar();
		sqlTableName.setName(SQL_SqlName);
		sqlTableName.setNotNull(true);
		sqlTableName.setSize(1024);
		tab.addColumn(sqlTableName);
		schema.addTable(tab);
	}
	static public void addAttrMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
	{
		ch.ehi.sqlgen.repository.DbTable tab=new ch.ehi.sqlgen.repository.DbTable();
		tab.setName(new DbTableName(schema.getName(),SQL_T_ILI2DB_ATTRNAME));
		ch.ehi.sqlgen.repository.DbColVarchar iliClassName=new ch.ehi.sqlgen.repository.DbColVarchar();
		iliClassName.setName(SQL_IliName);
		iliClassName.setNotNull(true);
		iliClassName.setSize(1024);
		iliClassName.setPrimaryKey(true);
		tab.addColumn(iliClassName);
		ch.ehi.sqlgen.repository.DbColVarchar sqlTableName=new ch.ehi.sqlgen.repository.DbColVarchar();
		sqlTableName.setName(SQL_SqlName);
		sqlTableName.setNotNull(true);
		sqlTableName.setSize(1024);
		tab.addColumn(sqlTableName);
		schema.addTable(tab);
	}

	private static HashSet<String> readTableMappingTableEntries(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> ret=new HashSet<String>();
		String sqlName=SQL_T_ILI2DB_CLASSNAME;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
			exstStmt="SELECT "+SQL_IliName+" FROM "+sqlName;
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
		String mapTabName=SQL_T_ILI2DB_CLASSNAME;
		if(schema!=null){
			mapTabName=schema+"."+mapTabName;
		}
		// create table
		try{

			// insert mapping entries
			String stmt="INSERT INTO "+mapTabName+" ("+SQL_IliName+","+SQL_SqlName+") VALUES (?,?)";
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
		String mapTableName=SQL_T_ILI2DB_CLASSNAME;
		if(schema!=null){
			mapTableName=schema+"."+mapTableName;
		}
		// create table
		String stmt="SELECT "+SQL_IliName+", "+SQL_SqlName+" FROM "+mapTableName;
		java.sql.Statement dbstmt = null;
		try{
			
			dbstmt = conn.createStatement();
			java.sql.ResultSet rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String iliname=rs.getString(SQL_IliName);
				String sqlname=rs.getString(SQL_SqlName);
				//EhiLogger.debug("map: "+iliname+"->"+sqlname);
				if(classNameIli2sql.get(iliname)==null){
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
	private static HashSet<String> readAttrMappingTableEntries(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> ret=new HashSet<String>();
		String sqlName=SQL_T_ILI2DB_ATTRNAME;
		if(schema!=null){
			sqlName=schema+"."+sqlName;
		}
		try{
			String exstStmt=null;
			exstStmt="SELECT "+SQL_IliName+" FROM "+sqlName;
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
			throw new Ili2dbException("failed to read attr-mapping-table "+sqlName,ex);
		}
		return ret;
	}
	public void updateAttrMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		HashSet<String> exstEntries=readAttrMappingTableEntries(conn,schema);
		String mapTabName=SQL_T_ILI2DB_ATTRNAME;
		if(schema!=null){
			mapTabName=schema+"."+mapTabName;
		}
		// create table
		try{

			// insert mapping entries
			String stmt="INSERT INTO "+mapTabName+" ("+SQL_IliName+","+SQL_SqlName+") VALUES (?,?)";
			EhiLogger.traceBackendCmd(stmt);
			java.sql.PreparedStatement ps = conn.prepareStatement(stmt);
			String iliname=null;
			String sqlname=null;
			try{
				java.util.Iterator<String> entri=attrNameIli2sql.keySet().iterator();
				while(entri.hasNext()){
					iliname=entri.next();
					if(!exstEntries.contains(iliname)){
						sqlname=attrNameIli2sql.get(iliname);
						ps.setString(1, iliname);
						ps.setString(2, sqlname);
						ps.executeUpdate();
					}
				}
			}catch(java.sql.SQLException ex){
				throw new Ili2dbException("failed to insert attrname-mapping "+iliname,ex);
			}finally{
				ps.close();
			}
		}catch(java.sql.SQLException ex){		
			throw new Ili2dbException("failed to update mapping-table "+mapTabName,ex);
		}

	}
	public void readAttrMappingTable(java.sql.Connection conn,String schema)
	throws Ili2dbException
	{
		String mapTableName=SQL_T_ILI2DB_ATTRNAME;
		if(schema!=null){
			mapTableName=schema+"."+mapTableName;
		}
		// create table
		String stmt="SELECT "+SQL_IliName+", "+SQL_SqlName+" FROM "+mapTableName;
		java.sql.Statement dbstmt = null;
		try{
			
			dbstmt = conn.createStatement();
			java.sql.ResultSet rs=dbstmt.executeQuery(stmt);
			while(rs.next()){
				String iliname=rs.getString(SQL_IliName);
				String sqlname=rs.getString(SQL_SqlName);
				//EhiLogger.debug("map: "+iliname+"->"+sqlname);
				if(attrNameIli2sql.get(iliname)==null){
					addAttrNameMapping(iliname,sqlname);
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

	public int getMaxSqlNameLength()
	{
		return _maxSqlNameLength;
	}
}
