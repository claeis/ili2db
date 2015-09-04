package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.*;
import ch.ehi.ili2db.base.DbUrlConverter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class WorkPanelDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "WORK_PANEL";
    
    WorkPanel panel;
    public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
    	panel.setConfig(config);
    	panel.setWorkBtn(((Ili2dbWizard)getWizard()).getWorkBtnTitle());
    	panel.setAppHome(((Ili2dbWizard)getWizard()).getAppHome());
    	panel.setDbUrlConverter(((Ili2dbWizard)getWizard()).getDbUrlConverter());

	}
	public void aboutToHidePanel() {
		super.aboutToHidePanel();
	}
    
    public WorkPanelDescriptor() {
        
        panel = new WorkPanel();
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        
    }
	public String getPanelTitle() {
		return ((Ili2dbWizard)getWizard()).getWorkPanelTitle();
	}  
    
    public Object getNextPanelDescriptor() {
        return FINISH;
    }
    
    public Object getBackPanelDescriptor() {
        return ((Ili2dbWizard)getWizard()).getFileSelectPanelDescriptor();
    }
    
}
