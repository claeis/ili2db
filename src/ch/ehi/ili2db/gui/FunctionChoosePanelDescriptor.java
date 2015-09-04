package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class FunctionChoosePanelDescriptor extends WizardPanelDescriptor {
    
    public void aboutToHidePanel() {
		int fc=panel.getIli2dbFunction();
		((Ili2dbWizard)getWizard()).setIli2dbFunction(fc);
		((Ili2dbWizard)getWizard()).getIli2dbConfig().setFunction(fc);
		super.aboutToHidePanel();
	}

	public static final String IDENTIFIER = "CONNECTOR_CHOOSE_PANEL";
    
    FunctionChoosePanel panel;
    
    public FunctionChoosePanelDescriptor() {
        
        panel = new FunctionChoosePanel();
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        
    }
	public String getPanelTitle() {
		return "Choose a function";
	}  
    
    public Object getNextPanelDescriptor() {
        return AbstractDbPanelDescriptor.IDENTIFIER;
    }
    
    public Object getBackPanelDescriptor() {
        return null;
    }
    
}
