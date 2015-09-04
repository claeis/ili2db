package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ImportPanelDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "IMPORT_PANEL";
    
    ImportPanel panel;
    
    public ImportPanelDescriptor() {
        
        panel = new ImportPanel();
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        
    }
	public String getPanelTitle() {
		return "select data file to import";
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
    	panel.setXtffile(config.getXtffile());
    	panel.setModels(config.getModels());
    	panel.setSettings(config.getAppSettings());
	}
	public void aboutToHidePanel() {
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
    	config.setXtffile(panel.getXtffile());
    	config.setModels(panel.getModels());
		super.aboutToHidePanel();
	}
    
}
