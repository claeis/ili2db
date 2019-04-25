package ch.ehi.ili2mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ehi.ili2db.AbstractTestSetup;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;

public class SimpleMysqlTest extends ch.ehi.ili2db.SimpleTest {
	
    String dburl=System.getProperty("dburl"); 
    String dbuser=System.getProperty("dbusr");
    String dbpwd=System.getProperty("dbpwd");
    @Override
    protected AbstractTestSetup createTestSetup() {
        return new MysqlTestSetup(dburl,dbuser,dbpwd);
    } 

    
	
}