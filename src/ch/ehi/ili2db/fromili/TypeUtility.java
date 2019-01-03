package ch.ehi.ili2db.fromili;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.base.DbNames;
import ch.ehi.ili2db.dbmetainfo.DbExtMetaInfo;
import ch.ehi.ili2db.mapping.NameMapping;


import ch.interlis.ili2c.metamodel.Viewable;

public class TypeUtility {

    public static String[] getViewableExtensions(Viewable v, NameMapping mapping){
        /**
         * Returns a string array with the extensions as sqlnames
         * of the passed viewable.
         */

        ArrayList<String> extensions = new ArrayList<String>();
        for (Object o : v.getExtensions()){
            extensions.add(mapping.mapIliClassDef((Viewable) o));
        }

        String[] result = new String[extensions.size()];
        return extensions.toArray(result);
    }

    public static void writeColumnProps(Connection conn, String dbSchema, NameMapping mapping)
        throws Ili2dbException {
        /**
         * Writes in the meta-info table the possible values of the t_type
         * column, comma separated.
         */

        ArrayList<String> tables = getTablesWithTypeSetting(conn, dbSchema);
        for (String table : tables) {
            writeColumnProp(conn, dbSchema, table, getSubClasses(conn, dbSchema, mapping.mapSqlTableName(table)));
        }
    }

    private static void writeColumnProp(Connection conn, String dbSchema, String table, ArrayList<String> subClasses)
        throws Ili2dbException {

        String classesString = "";
        for (String s: subClasses){
            classesString += s+",";
        }
        //Remove last comma
        classesString = classesString.substring(0, classesString.length() - 1);

        try{
            String stmt = "UPDATE  "+dbSchema+"."+DbNames.META_INFO_COLUMN_TAB+
                " SET "+DbNames.META_INFO_TABLE_TAB_SETTING_COL+" = ? WHERE "
                +DbNames.META_INFO_TABLE_TAB_TABLENAME_COL+" = ? AND "
                +DbNames.META_INFO_TABLE_TAB_TAG_COL+" = '"+DbExtMetaInfo.TAG_COL_TYPES+"';";

            PreparedStatement prepStmt = conn.prepareStatement(stmt);
            prepStmt.setString(1, classesString);
            prepStmt.setString(2, table);
            prepStmt.execute();
        }catch(SQLException ex){
			throw new Ili2dbException("Failed to add type column property",ex);
        }
    }

    public static void addTypeConstraints(Connection conn, String dbSchema, NameMapping mapping)
        throws Ili2dbException {
        /**
         * Adds a check constraint on the t_type column.
         */

        ArrayList<String> tables = getTablesWithTypeSetting(conn, dbSchema);
        for (String table : tables) {
            addTypeConstraint(conn, dbSchema, table, getSubClasses(conn, dbSchema, mapping.mapSqlTableName(table)));
        }
    }

    private static void addTypeConstraint(Connection conn, String dbSchema,  String table, ArrayList<String> subClasses)
        throws Ili2dbException {

        String classesString = "";
        for (String s: subClasses){
            classesString += "'"+s+"', ";
        }
        //Remove last comma and space
        classesString = classesString.substring(0, classesString.length() - 2);

        try{
            String stmt = "ALTER TABLE "+dbSchema+"."+table+
                " ADD CONSTRAINT type_constraint CHECK ("+DbNames.T_TYPE_COL+
                " IN ("+classesString+"));";

            PreparedStatement prepStmt = conn.prepareStatement(stmt);
            prepStmt.execute();
        }catch(SQLException ex){
			throw new Ili2dbException("Failed to add type constraint ",ex);
        }
    }

    private static ArrayList<String> getSubClasses(Connection conn, String dbSchema, String baseClass)
        throws Ili2dbException {

        ArrayList<String> result = new ArrayList<String>();
        result.add(getSqlNameFromIliname(conn, dbSchema, baseClass));
        try{
			String stmt= "SELECT "+DbNames.INHERIT_TAB_THIS_COL+" FROM "
                +dbSchema+"."+DbNames.INHERIT_TAB+" WHERE "
                +DbNames.INHERIT_TAB_BASE_COL+"= ?";

			PreparedStatement prepStmt = conn.prepareStatement(stmt);
            prepStmt.setString(1, baseClass);
            ResultSet rs = prepStmt.executeQuery();
			try{
				while(rs.next()){
					result.add(getSqlNameFromIliname(conn, dbSchema, rs.getString(1)));
				}
			}catch(SQLException ex){
				throw ex;
			}finally{
			    rs.close();
				prepStmt.close();
			}
		}catch(SQLException ex){
			throw new Ili2dbException("Failed to read inheritance table ",ex);
		}
    return result;
    }

    private static ArrayList<String> getTablesWithTypeSetting(Connection conn, String dbSchema)
        throws Ili2dbException {

        ArrayList<String> result = new ArrayList<String>();

        try{
			String stmt= "SELECT "+DbNames.META_INFO_COLUMN_TAB_TABLENAME_COL+
                " FROM "+dbSchema+"."+DbNames.META_INFO_COLUMN_TAB+" WHERE "+DbNames.META_INFO_TABLE_TAB_TAG_COL+"='"+
                DbExtMetaInfo.TAG_COL_TYPES+"';";
			PreparedStatement prepStmt = conn.prepareStatement(stmt);

            ResultSet rs = prepStmt.executeQuery();
			try{
				while(rs.next()){
					result.add(rs.getString(1));
				}
			}catch(SQLException ex){
				throw ex;
			}finally{
			    rs.close();
				prepStmt.close();
			}
		}catch(SQLException ex){
			throw new Ili2dbException("Failed to read meta info table ",ex);
		}
        return result;
    }

    private static String getSqlNameFromIliname(Connection conn, String dbSchema, String iliName)
        throws Ili2dbException {

        try{
            String stmt = "SELECT "+DbNames.CLASSNAME_TAB_SQLNAME_COL+" FROM "+dbSchema+"."+DbNames.CLASSNAME_TAB+
                " WHERE "+DbNames.CLASSNAME_TAB_ILINAME_COL+" = ? ;";

            PreparedStatement prepStmt = conn.prepareStatement(stmt);
            prepStmt.setString(1, iliName);
            ResultSet rs = prepStmt.executeQuery();

            try{
                while(rs.next()){
                    return rs.getString(1);
                }
            }catch(SQLException ex){
				throw ex;
			}finally{
			    rs.close();
				prepStmt.close();
			}
            return null;

        }catch(SQLException ex){
			throw new Ili2dbException("Failed to read classname table",ex);
        }
    }
}
