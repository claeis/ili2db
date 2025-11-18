package ch.ehi.ili2db.nls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Evaluable;
import ch.interlis.ili2c.metamodel.Ili2cMetaAttrs;
import ch.interlis.ili2c.metamodel.LocalAttribute;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.ObjectPath;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.metamodel.Type;
import ch.interlis.ili2c.metamodel.Viewable;
import ch.interlis.iox_j.inifile.IniFileReader;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.ili2c.generator.nls.Ili2TranslationXml;
import ch.interlis.ili2c.generator.nls.ModelElements;
import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Cardinality;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.Domain;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.ehi.basics.tools.NameUtility;
import ch.ehi.basics.tools.StringUtility;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.mapping.NameMapping;
import ch.ehi.ili2db.metaattr.IliMetaAttrNames;
import ch.ehi.sqlgen.repository.DbSchema;
import ch.ehi.sqlgen.repository.DbTable;
import ch.ehi.sqlgen.repository.DbTableName;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc;
import ch.ehi.sqlgen.generator_impl.jdbc.GeneratorJdbc.Stmt;
import ch.ehi.sqlgen.repository.DbColBlob;
import ch.ehi.sqlgen.repository.DbColVarchar;

public class NlsUtility{
	
	private static final String UIVARIANT_ILI = "ili";
    // create NLS table
	public static void addNlsTable(DbSchema schema)
	{
		DbTable tab=new DbTable();
		tab.setName(new DbTableName(schema.getName(), DbNames.NLS_TAB));
		{
    		DbColVarchar ilielementCol=new DbColVarchar();
    		ilielementCol.setName(DbNames.NLS_TAB_ILIELEMENT_COL);
    		ilielementCol.setNotNull(true);
    		ilielementCol.setSize(255);
    		tab.addColumn(ilielementCol);
		}
		{
        DbColVarchar langCol=new DbColVarchar();
        langCol.setName(DbNames.NLS_TAB_LANG_COL);
        langCol.setNotNull(false);
        langCol.setSize(20);
        tab.addColumn(langCol);
		}
		{
        DbColVarchar uivariantCol=new DbColVarchar();
        uivariantCol.setName(DbNames.NLS_TAB_UIVARIANT_COL);
        uivariantCol.setNotNull(false);
        uivariantCol.setSize(30);
        tab.addColumn(uivariantCol);
		}
		{
        DbColVarchar labelCol=new DbColVarchar();
        labelCol.setName(DbNames.NLS_TAB_LABEL_COL);
        labelCol.setNotNull(false);
        labelCol.setSize(DbNames.NLS_TAB_LABEL_COL_SIZE);
        tab.addColumn(labelCol);
		}
		{
        DbColVarchar mnemonicCol=new DbColVarchar();
        mnemonicCol.setName(DbNames.NLS_TAB_MNEMONIC_COL);
        mnemonicCol.setNotNull(false);
        mnemonicCol.setSize(20);
        tab.addColumn(mnemonicCol);
		}
		{
        DbColVarchar tooltipCol=new DbColVarchar();
        tooltipCol.setName(DbNames.NLS_TAB_TOOLTIP_COL);
        tooltipCol.setNotNull(false);
        tooltipCol.setSize(200);
        tab.addColumn(tooltipCol);
		}
		{
            DbColVarchar descriptionCol=new DbColVarchar();
            descriptionCol.setName(DbNames.NLS_TAB_DESCRIPTION_COL);
            descriptionCol.setNotNull(false);
            descriptionCol.setSize(DbColVarchar.UNLIMITED);
            tab.addColumn(descriptionCol);
		}
		{
            DbColBlob symbolCol=new DbColBlob();
            symbolCol.setName(DbNames.NLS_TAB_SYMBOL_COL);
            symbolCol.setNotNull(false);
            tab.addColumn(symbolCol);
		}
		
		schema.addTable(tab);
	}

    public static class NlsKey
    {
        public NlsKey(String ilielementCol, String langCol, String uivariantCol) {
            super();
            this.ilielementCol = ilielementCol;
            this.langCol = langCol;
            this.uivariantCol = uivariantCol;
        }
        public String ilielementCol=null;
        public String langCol=null;
        public String uivariantCol=null;
        @Override
        public int hashCode() {
            return Objects.hash(ilielementCol, langCol, uivariantCol);
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NlsKey other = (NlsKey) obj;
            return Objects.equals(ilielementCol, other.ilielementCol) && Objects.equals(langCol, other.langCol)
                    && Objects.equals(uivariantCol, other.uivariantCol);
        }
    }
	public static class NlsEntry
	{
        public String ilielementCol=null;
        public String langCol=null;
        public String uivariantCol=null;
        public String labelCol=null;
        public String mnemonicCol=null;
        public String tooltipCol=null;
        public String descriptionCol=null;
        public byte[] symbolCol=null;
	}
    public static void updateNlsTable(GeneratorJdbc gen, java.sql.Connection conn, String schema, TransferDescription td, NameMapping class2wrapper) 
    throws Ili2dbException
    {
        Map<NlsKey,NlsEntry> entries=new HashMap<NlsKey,NlsEntry>();
        Iterator<Model> transIter = td.iterator();
        List<Model> models=new ArrayList<Model>();
        while(transIter.hasNext()){
            Model transElem=transIter.next();
            if(transElem instanceof ch.interlis.ili2c.metamodel.PredefinedModel){
                continue;
            }else if(transElem instanceof Model){
                models.add(0,transElem);
            }
        }
        Ili2TranslationXml ili2cHelper=new Ili2TranslationXml();
        List<ch.interlis.ili2c.generator.nls.NlsModelElement> modelEles = ili2cHelper.convertModels(models.toArray(new Model[models.size()]));
        for(ch.interlis.ili2c.generator.nls.NlsModelElement modelEle:modelEles) {
            for(String lang:modelEle.getLanguages()) {
                // map entry
                NlsEntry entry=new NlsEntry();
                entry.ilielementCol=modelEle.getScopedName();
                entry.langCol=lang;
                entry.uivariantCol=UIVARIANT_ILI;
                entry.labelCol=modelEle.getName(lang);
                String dispName=getDispName(modelEle,entry.langCol);
                if(dispName!=null) {
                    entry.labelCol=dispName;
                }
                if(entry.labelCol!=null && entry.labelCol.length()>DbNames.NLS_TAB_LABEL_COL_SIZE) {
                    entry.labelCol=NameUtility.shortcutName(entry.labelCol,DbNames.NLS_TAB_LABEL_COL_SIZE);
                }
                entry.mnemonicCol=null;
                entry.tooltipCol=null;
                entry.descriptionCol=modelEle.getDocumentation(lang);
                entry.symbolCol=null;
                entries.put(new NlsKey(entry.ilielementCol, entry.langCol, entry.uivariantCol),entry);
            }
        }
        
        saveTableTab(gen,conn,schema,entries);
    }

    private static String getDispName(ch.interlis.ili2c.generator.nls.NlsModelElement modelEle, String lang) {
        String value=null;
        lang=StringUtility.purge(lang);
        if(lang!=null) {
            value=StringUtility.purge(modelEle.getMetaAttr(IliMetaAttrNames.METAATTR_DISPNAME+"_"+lang));
        }
        if(value==null) {
            value=StringUtility.purge(modelEle.getMetaAttr(IliMetaAttrNames.METAATTR_DISPNAME));
        }
        if(value!=null) {
            return value;
        }
        return null;
    }

    private static void saveTableTab(GeneratorJdbc gen, Connection conn,String schemaName,Map<NlsKey,NlsEntry> tabInfo)
    throws Ili2dbException
    {
        DbTableName tabName=new DbTableName(schemaName,DbNames.NLS_TAB);
        String sqlName=tabName.getQName();
        if(conn!=null) {
            Map<NlsKey,NlsEntry> exstEntries=readTableTab(conn,sqlName);
            try{

                // insert entries
                String insStmt="INSERT INTO "+sqlName+" ("+
                        DbNames.NLS_TAB_ILIELEMENT_COL+
                        ","+DbNames.NLS_TAB_LANG_COL+
                        ","+DbNames.NLS_TAB_UIVARIANT_COL+
                        ","+DbNames.NLS_TAB_LABEL_COL+
                        ","+DbNames.NLS_TAB_MNEMONIC_COL+
                        ","+DbNames.NLS_TAB_TOOLTIP_COL+
                        ","+DbNames.NLS_TAB_DESCRIPTION_COL+
                        ","+DbNames.NLS_TAB_SYMBOL_COL+
                        ") VALUES (?,?,?,?,?,?,?,?)";
                EhiLogger.traceBackendCmd(insStmt);
                java.sql.PreparedStatement insPrepStmt = conn.prepareStatement(insStmt);
                try{
                    for(Map.Entry<NlsKey,NlsEntry> entry:tabInfo.entrySet()){
                        if(!exstEntries.containsKey(entry.getKey())){
                            insPrepStmt.setString(1, entry.getValue().ilielementCol);
                            insPrepStmt.setString(2, entry.getValue().langCol);
                            insPrepStmt.setString(3, entry.getValue().uivariantCol);
                            insPrepStmt.setString(4, entry.getValue().labelCol);
                            insPrepStmt.setString(5, entry.getValue().mnemonicCol);
                            insPrepStmt.setString(6, entry.getValue().tooltipCol);
                            insPrepStmt.setString(7, entry.getValue().descriptionCol);
                            insPrepStmt.setBytes(8, entry.getValue().symbolCol);
                            insPrepStmt.executeUpdate();
                        }
                    }
                }catch(java.sql.SQLException ex){
                    throw new Ili2dbException("failed to insert meta info values to "+sqlName,ex);
                }finally{
                    insPrepStmt.close();
                }
            }catch(java.sql.SQLException ex){       
                throw new Ili2dbException("failed to update meta-info table "+sqlName,ex);
            }
        }
        if(gen!=null){
            for(Map.Entry<NlsKey,NlsEntry> entry:tabInfo.entrySet()){
                    String insStmt="INSERT INTO "+sqlName+" ("+
                            DbNames.NLS_TAB_ILIELEMENT_COL+
                            ","+DbNames.NLS_TAB_LANG_COL+
                            ","+DbNames.NLS_TAB_UIVARIANT_COL+
                            ","+DbNames.NLS_TAB_LABEL_COL+
                            ","+DbNames.NLS_TAB_MNEMONIC_COL+
                            ","+DbNames.NLS_TAB_TOOLTIP_COL+
                            ","+DbNames.NLS_TAB_DESCRIPTION_COL+
                            // ","+DbNames.NLS_TAB_SYMBOL_COL+
                            ") VALUES ("+
                            quoteSqlValue(entry.getValue().ilielementCol)+","+
                            quoteSqlValue(entry.getValue().langCol)+","+
                            quoteSqlValue(entry.getValue().uivariantCol)+","+
                            quoteSqlValue(entry.getValue().labelCol)+","+
                            quoteSqlValue(entry.getValue().mnemonicCol)+","+
                            quoteSqlValue(entry.getValue().tooltipCol)+","+
                            quoteSqlValue(entry.getValue().descriptionCol)+
                            // ","+quoteSqlValue(entry.getValue().symbolCol)+
                            ")";
                    gen.addCreateLine(gen.new Stmt(insStmt));
            }
        }
        
    }
    private static String quoteSqlValue(String val) {
        return Ili2db.quoteSqlStringValue(val);
    }
    private static Map<NlsKey,NlsEntry> readTableTab(
            Connection conn, String sqlName) throws Ili2dbException {
        Map<NlsKey,NlsEntry> exstEntries=new HashMap<NlsKey,NlsEntry>();
        try{

            // select entries
            String selStmt="SELECT "+
                    DbNames.NLS_TAB_ILIELEMENT_COL+
                    ","+DbNames.NLS_TAB_LANG_COL+
                    ","+DbNames.NLS_TAB_UIVARIANT_COL+
                    ","+DbNames.NLS_TAB_LABEL_COL+
                    ","+DbNames.NLS_TAB_MNEMONIC_COL+
                    ","+DbNames.NLS_TAB_TOOLTIP_COL+
                    ","+DbNames.NLS_TAB_DESCRIPTION_COL+
                    ","+DbNames.NLS_TAB_SYMBOL_COL+
                    " FROM "+sqlName;
            EhiLogger.traceBackendCmd(selStmt);
            java.sql.PreparedStatement selPrepStmt = null;
            ResultSet rs = null;
            try{
                selPrepStmt = conn.prepareStatement(selStmt);
                rs = selPrepStmt.executeQuery();
                while(rs.next()){
                    NlsEntry entry=new NlsEntry();
                    entry.ilielementCol=rs.getString(1);
                    entry.langCol=rs.getString(2);
                    entry.uivariantCol=rs.getString(3);
                    entry.labelCol=rs.getString(4);
                    entry.mnemonicCol=rs.getString(5);
                    entry.tooltipCol=rs.getString(6);
                    entry.descriptionCol=rs.getString(7);
                    entry.symbolCol=rs.getBytes(8);
                    NlsKey key=new NlsKey(entry.ilielementCol,entry.langCol,entry.uivariantCol);
                    exstEntries.put(key, entry);
                }
            }catch(java.sql.SQLException ex){
                throw new Ili2dbException("failed to read nls info values from "+sqlName,ex);
            }finally{
                if(rs!=null) {
                    rs.close();
                }
                if(selPrepStmt!=null) {
                    selPrepStmt.close();
                    selPrepStmt=null;
                }
            }
        }catch(java.sql.SQLException ex){       
            throw new Ili2dbException("failed to read nls-info table "+sqlName,ex);
        }
        
        return exstEntries;
    }
	
}
