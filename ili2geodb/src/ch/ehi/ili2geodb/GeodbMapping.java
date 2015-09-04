package ch.ehi.ili2geodb;

import ch.ehi.ili2db.fromili.CustomMapping;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.interlis.ili2c.metamodel.*;
import ch.ehi.ili2geodb.sqlgen.GeodbGenerator;

public class GeodbMapping implements CustomMapping {
	private java.util.HashMap geodbDomains=new java.util.HashMap(); // map<String geodbDomainName,AttributeDef | DomainDef>
	private java.util.HashSet geodbLinks=new java.util.HashSet(); // map<GeodbLink>
	@Override
	public void init(ch.ehi.ili2db.gui.Config config)
	{
		
	}
	
	@Override
	public void end(ch.ehi.ili2db.gui.Config config)
	{
		config.setTransientObject(ch.ehi.ili2geodb.sqlgen.GeodbGenerator.GEODB_DOMAINS,geodbDomains);
		config.setTransientObject(ch.ehi.ili2geodb.sqlgen.GeodbGenerator.GEODB_LINKS,geodbLinks);
	}

	@Override
	public void fixupViewable(DbTable sqlTableDef, Viewable iliClassDef) {
		if(sqlTableDef==null){
			return;
		}
		ch.interlis.ili2c.metamodel.Container container=iliClassDef.getContainer();
		//if(container instanceof ch.interlis.ili2c.metamodel.Topic){
		//	// set dataset name
		//	String topicName=container.getName();
		//	sqlTableDef.setCustomValue(GeodbGenerator.DATASET_NAME, topicName);
		//}
	}
	@Override
	public void fixupAttribute(DbTable sqlTableDef, DbColumn sqlColDef, AttributeDef iliAttrDef) {
		if(sqlColDef==null){
			return;
		}
		Type iliType=iliAttrDef.getDomain();
		String domainName=null;
		if(iliType instanceof TypeAlias){
			Domain iliDomain=((TypeAlias)iliType).getAliasing();
			iliType=iliDomain.getType();
			if(!GeodbGenerator.supportsGeodbDomain(iliType)){
				// skip it; no geodb domain
				return;
			}
			domainName=iliDomain.getScopedName(null).replace('.', ' ');
			geodbDomains.put(domainName, iliDomain);
		}else{
			if(!GeodbGenerator.supportsGeodbDomain(iliType)){
				// skip it; no geodb domain
				return;
			}
			domainName=iliAttrDef.getContainer().getScopedName(null).replace('.', ' ')+" "+iliAttrDef.getName();
			geodbDomains.put(domainName, iliAttrDef);
		}
		sqlColDef.setCustomValue(GeodbGenerator.DOMAIN_NAME, domainName);
	}
	@Override
	public void fixupEmbeddedLink(DbTable dbTable,DbColumn dbFk,AssociationDef assoc,RoleDef target,DbTableName targetName,String targetPk)
	{
		ch.interlis.ili2c.metamodel.Container container=assoc.getContainer();
		String topicName=null;
		if(container instanceof ch.interlis.ili2c.metamodel.Topic){
			// set dataset name
			topicName=container.getName();
		}
		String relName="_"+assoc.getScopedName(null).replace('.', '_');
			geodbLinks.add(new ch.ehi.ili2geodb.sqlgen.GeodbLink(
					topicName
					,relName // relClassName
					, targetName.getName() // OriginClass
					, dbTable.getName().getName() // DestinationClass
					, target.getOppEnd().getName() // forwardLabel
					, target.getName() // backwardLabel
					, com.esri.arcgis.geodatabase.esriRelCardinality.esriRelCardinalityOneToMany // Cardinality
					, com.esri.arcgis.geodatabase.esriRelNotification.esriRelNotificationNone // Notification
					, /*isComposite*/false
					, targetPk // OriginPrimaryKey
					, dbFk.getName() // OriginForeignKey
					));
	}

}
