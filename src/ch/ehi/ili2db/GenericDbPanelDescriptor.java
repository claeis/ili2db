package ch.ehi.ili2db;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ch.ehi.ili2db.gui.*;

public class GenericDbPanelDescriptor extends AbstractDbPanelDescriptor {
    
    public JPanel createPanel() {
		return new GenericDbPanel();
	}
	public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        GenericDbPanel panel=(GenericDbPanel)getPanelComponent();
    	panel.setDburl(config.getDburl());
    	panel.setDbusr(config.getDbusr());
    	panel.setDbpwd(config.getDbpwd());
	}
	public void aboutToHidePanel() {
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        GenericDbPanel panel=(GenericDbPanel)getPanelComponent();
    	config.setDburl(panel.getDburl());
    	config.setDbusr(panel.getDbusr());
    	config.setDbpwd(panel.getDbpwd());
		super.aboutToHidePanel();
	}

    
    
}
