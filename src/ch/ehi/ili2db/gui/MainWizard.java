package ch.ehi.ili2db.gui;


import ch.ehi.ili2db.base.DbUrlConverter;

public class MainWizard {
	public static void main(Config config,String appHome,String appName,AbstractDbPanelDescriptor dbPanelDescriptor,DbUrlConverter makeUrl) {
		Ili2dbWizard wizard = new Ili2dbWizard();
		wizard.setAppHome(appHome);
		wizard.setDbUrlConverter(makeUrl);
        wizard.getDialog().setTitle(appName);
        wizard.setIli2dbConfig(config);
        
        wizard.registerWizardPanel(FunctionChoosePanelDescriptor.IDENTIFIER, new FunctionChoosePanelDescriptor());
        wizard.registerWizardPanel(AbstractDbPanelDescriptor.IDENTIFIER, dbPanelDescriptor);
        wizard.registerWizardPanel(ImportPanelDescriptor.IDENTIFIER, new ImportPanelDescriptor());
        wizard.registerWizardPanel(ExportPanelDescriptor.IDENTIFIER, new ExportPanelDescriptor());
        wizard.registerWizardPanel(ImportSchemaPanelDescriptor.IDENTIFIER, new ImportSchemaPanelDescriptor());
        wizard.registerWizardPanel(WorkPanelDescriptor.IDENTIFIER, new WorkPanelDescriptor());
        wizard.setCurrentPanel(FunctionChoosePanelDescriptor.IDENTIFIER);
        wizard.showModalDialog();
		
	}

}
