package ch.ehi.ili2ora;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ch.ehi.ili2db.gui.*;

public class OraDbPanelDescriptor extends AbstractDbPanelDescriptor {
    
    public JPanel createPanel() {
		return new OraDbPanel();
	}
	public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        OraDbPanel panel=(OraDbPanel)getPanelComponent();
    	panel.setDbname(config.getDbdatabase());
    	panel.setDbhost(config.getDbhost());
    	panel.setDbport(config.getDbport());
    	panel.setDbusr(config.getDbusr());
    	panel.setDbpwd(config.getDbpwd());
    	panel.setDbUrlConverter(((Ili2dbWizard)getWizard()).getDbUrlConverter());
    	panel.setJdbcDriver(config.getJdbcDriver());
	}
	public void aboutToHidePanel() {
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        OraDbPanel panel=(OraDbPanel)getPanelComponent();
    	config.setDbdatabase(panel.getDbname());
    	config.setDbhost(panel.getDbhost());
    	config.setDbport(panel.getDbport());
    	config.setDbusr(panel.getDbusr());
    	config.setDbpwd(panel.getDbpwd());
		super.aboutToHidePanel();
	}

    
    
}
