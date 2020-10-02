package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.Wizard;
import ch.ehi.ili2db.base.DbUrlConverter;

public class Ili2dbWizard extends Wizard {
	
	private String workPanelTitle=null;
	private String workBtn=null;
	private int function;
	private Config config;
	private Object fileSelectPanelDescriptor;
	void setIli2dbFunction(int fc)
	{
		if(fc==Config.FC_IMPORT){
			workPanelTitle="Import transfer file to database";
			workBtn="Import";
			fileSelectPanelDescriptor=ImportPanelDescriptor.IDENTIFIER;
		}else if(fc==Config.FC_EXPORT){
			workPanelTitle="Export from database to transfer file";
			workBtn="Export";
			fileSelectPanelDescriptor=ExportPanelDescriptor.IDENTIFIER;
		}else if(fc==Config.FC_SCHEMAIMPORT){
			workPanelTitle="Convert model to database schema";
			workBtn="Convert";
			fileSelectPanelDescriptor=ImportSchemaPanelDescriptor.IDENTIFIER;
		}else{
			throw new IllegalArgumentException();
		}
		function=fc;
	}
	public Config getIli2dbConfig()
	{
		if(config==null){
			config=new Config();
		}
		return config;
	}
	public void setIli2dbConfig(Config config)
	{
		this.config=config;
	}

	public String getWorkPanelTitle() {
		return workPanelTitle;
	}
	public String getWorkBtnTitle() {
		return workBtn;
	}
	public Object getFileSelectPanelDescriptor()
	{
		return fileSelectPanelDescriptor;
	}
	private String appHome=null;
	public String getAppHome() {
		return appHome;
	}
	public void setAppHome(String appHome) {
		this.appHome = appHome;
	}
	private DbUrlConverter dbUrlConverter;
	public DbUrlConverter getDbUrlConverter() {
		return dbUrlConverter;
	}
	public void setDbUrlConverter(DbUrlConverter dbUrlConverter) {
		this.dbUrlConverter = dbUrlConverter;
	}

	
}
