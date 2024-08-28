package ch.ehi.ili2db.mapping;

import org.junit.Assert;
import org.junit.Test;

import ch.ehi.ili2db.base.DbNames;

public class SqlKwTest {

    @Test
    public void test_META_INFO()
    {
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_TABLENAME_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_TAG_COL));
        Assert.assertTrue(NameMapping.isValidSqlName(DbNames.META_INFO_TABLE_TAB_SETTING_COL));   
    }
}
