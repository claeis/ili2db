package ch.ehi.ili2mssql;

import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2db.gui.Config;

import java.text.ParseException;


public class MsSqlMain  extends ch.ehi.ili2db.AbstractMain {

	String dbinstance = "";
	boolean dbWindowsAuth = false;
	
	@Override
	public void initConfig(Config config) {
		super.initConfig(config);
		config.setGeometryConverter(ch.ehi.ili2mssql.converter.MsSqlColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.ili2mssql.sqlgen.GeneratorMsSql.class.getName());
		config.setJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		config.setIdGenerator(ch.ehi.ili2mssql.MsSqlSequenceBasedIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2mssql.MsSqlCustomStrategy.class.getName());
		config.setUuidDefaultValue("NEWID()");
		config.setValue(Config.MODELS_TAB_MODELNAME_COLSIZE, "400"); // change column size because 'SQL Server retains the 900-byte limit for the maximum total size of all index key columns'
	}
	
	@Override
    public DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				if(config.getDbdatabase()!=null){
					
                    String strDbHost = !isNullOrEmpty(config.getDbhost())? config.getDbhost():"localhost";
                    String strInstance = !isNullOrEmpty(dbinstance)?"\\"+dbinstance:"";
                    String strPort = !isNullOrEmpty(config.getDbport())?":"+config.getDbport():"";
					String strDbdatabase = ";databaseName="+config.getDbdatabase();
					String strWindowsAuth =  dbWindowsAuth?";integratedSecurity=true":"";
					
					return "jdbc:sqlserver://" + strDbHost + strInstance + strPort + strDbdatabase + strWindowsAuth;
				}
				return null;
			}
		};
	}
	
	@Override
	public AbstractDbPanelDescriptor getDbPanelDescriptor() {
		return null;
	}
	
    public static void main(String[] args){
        new MsSqlMain().domain(args);
	}
	
	@Override
	public String getAPP_NAME() {
		return "ili2mssql";
	}

	@Override
	public String getDB_PRODUCT_NAME() {
		return "SQL Server";
	}

	@Override
	public String getJAR_NAME() {
		return "ili2mssql.jar";
	}

	@Override
	protected void printConnectOptions() {
		System.err.println("--dbhost  host         The host name of the server. Defaults to localhost.");
		System.err.println("--dbport  port         The port number the server is listening on. Defaults to 1433.");
		System.err.println("--dbdatabase database  The database name.");
		System.err.println("--dbusr  username      User name to access database.");
		System.err.println("--dbpwd  password      Password of user used to access database.");
	}
	
	@Override
	protected void printSpecificOptions() {
		System.err.println("--dbinstance instance  The instance of the Database Engine");
		System.err.println("--dbschema  schema     The name of the schema in the database. Defaults to not set.");
		System.err.println("--dbwindowsauth        SQL Server validates a user's identity using only his Windows username and password");
	}

	@Override
	protected int doArgs(String[] args,int argi,Config config) throws ParseException
	{
		String arg=args[argi];
		if(arg.equals("--dbhost")){
			argi++;
			config.setDbhost(args[argi]);
			argi++;
		}else if(arg.equals("--dbport")){
			argi++;
			config.setDbport(args[argi]);
			argi++;
		}else if(arg.equals("--dbdatabase")){
			argi++;
			config.setDbdatabase(args[argi]);
			argi++;
		}else if(arg.equals("--dbusr")){
			argi++;
			config.setDbusr(args[argi]);
			argi++;
		}else if(arg.equals("--dbpwd")){
			argi++;
			config.setDbpwd(args[argi]);
			argi++;
		}else if(arg.equals("--dbschema")){
			argi++;
			config.setDbschema(args[argi]);
			argi++;
		}else if(arg.equals("--dbinstance")){
			argi++;
			dbinstance = args[argi];
			argi++;
		}else if(isOption(arg, "--dbwindowsauth")){
			argi++;
			dbWindowsAuth = parseBooleanArgument(arg);
		}
		return argi;
	}
    
    private boolean isNullOrEmpty(String str) {
        return str==null||str.isEmpty();
    }
}
