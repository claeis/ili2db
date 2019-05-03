package ch.ehi.ili2mysql;

import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.base.TableBasedIdGen;
import ch.ehi.sqlgen.repository.DbColumn;
import ch.ehi.sqlgen.repository.DbTableName;

public class MySqlIdGen extends TableBasedIdGen {

    @Override
    public void addMappingTable(ch.ehi.sqlgen.repository.DbSchema schema)
    {
        super.addMappingTable(schema);
        ch.ehi.sqlgen.repository.DbTable tab=schema.findTable(new DbTableName(schema.getName(),SQL_T_KEY_OBJECT));
        DbColumn createDate=tab.getColumn(DbNames.T_CREATE_DATE_COL);
        createDate.setDefaultValue("NOW()");
        DbColumn lastChange=tab.getColumn(DbNames.T_LAST_CHANGE_COL);
        lastChange.setDefaultValue("NOW()");
    }
}
