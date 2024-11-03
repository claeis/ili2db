package ch.ehi.ili2db.mapping;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.DbNames;

public class SqlKwTest {

    @Test
    public void test_META_INFO_TAB()
    {
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_TABLENAME_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_TAG_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_SETTING_COL));   
    }
    @Test
    public void test_NLS_TAB()
    {
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_ILIELEMENT_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_LANG_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_UIVARIANT_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_LABEL_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_MNEMONIC_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_TOOLTIP_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_DESCRIPTION_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.NLS_TAB_SYMBOL_COL));
    }
}
