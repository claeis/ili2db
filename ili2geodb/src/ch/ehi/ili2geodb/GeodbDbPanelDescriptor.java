package ch.ehi.ili2geodb;

import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ch.ehi.ili2db.gui.*;

public class GeodbDbPanelDescriptor extends AbstractDbPanelDescriptor {
    
    public JPanel createPanel() {
		return new GeodbDbPanel();
	}
	public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        GeodbDbPanel panel=(GeodbDbPanel)getPanelComponent();
    	panel.setDbfile(config.getDbfile());
    	panel.setDbusr(config.getDbusr());
    	panel.setDbpwd(config.getDbpwd());
    	panel.setDbUrlConverter(((Ili2dbWizard)getWizard()).getDbUrlConverter());
    	panel.setJdbcDriver(config.getJdbcDriver());
    	panel.setSettings(config.getAppSettings());
	}
	public void aboutToHidePanel() {
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        GeodbDbPanel panel=(GeodbDbPanel)getPanelComponent();
    	config.setDbfile(panel.getDbfile());
    	config.setDbusr(panel.getDbusr());
    	config.setDbpwd(panel.getDbpwd());
		super.aboutToHidePanel();
	}

    
    
}
