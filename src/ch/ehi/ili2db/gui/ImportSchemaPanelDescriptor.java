package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ImportSchemaPanelDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "IMPORTSCHEMA_PANEL";
    
    ImportSchemaPanel panel;
    
    public ImportSchemaPanelDescriptor() {
        
        panel = new ImportSchemaPanel();
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        
    }
	public String getPanelTitle() {
		return "select model file to convert";
	}  
    
    public Object getNextPanelDescriptor() {
        return WorkPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
        return AbstractDbPanelDescriptor.IDENTIFIER;
    }
    public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
    	panel.setModels(config.getXtffile());
    	panel.setSettings(config.getAppSettings());
	}
	public void aboutToHidePanel() {
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
    	config.setXtffile(panel.getModels());
		super.aboutToHidePanel();
	}
    
}
