package ch.ehi.ili2db.gui;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public abstract class AbstractDbPanelDescriptor extends WizardPanelDescriptor {
    
	public static final String IDENTIFIER = "DB_CHOOSE_PANEL";
    
    public abstract JPanel createPanel(); 
    
    public AbstractDbPanelDescriptor() {
        
        JPanel panel = createPanel();
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel);
        
    }
	public String getPanelTitle() {
		return "set DB connection details";
	}  
    
    public Object getNextPanelDescriptor() {
        return ((Ili2dbWizard)getWizard()).getFileSelectPanelDescriptor();
    }
    
    public Object getBackPanelDescriptor() {
        return FunctionChoosePanelDescriptor.IDENTIFIER;
    }
    
}
