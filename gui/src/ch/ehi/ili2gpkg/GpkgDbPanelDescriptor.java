package ch.ehi.ili2gpkg;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.wizard.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.*;

public class GpkgDbPanelDescriptor extends AbstractDbPanelDescriptor {
    
    public JPanel createPanel() {
		return new GpkgDbPanel();
	}
	public void aboutToDisplayPanel() {
		super.aboutToDisplayPanel();
    	Config config=((Ili2dbWizard)getWizard()).getIli2dbConfig();
        GpkgDbPanel panel=(GpkgDbPanel)getPanelComponent();
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
        GpkgDbPanel panel=(GpkgDbPanel)getPanelComponent();
    	config.setDbdatabase(panel.getDbname());
    	config.setDbhost(panel.getDbhost());
    	config.setDbport(panel.getDbport());
    	config.setDbusr(panel.getDbusr());
    	config.setDbpwd(panel.getDbpwd());
    	try {
			config.setDburl(panel.getDbUrlConverter().makeUrl(config));
			Ili2db.readSettingsFromDb(config);
		} catch (Ili2dbException e) {
			EhiLogger.logError(e);
		}
		super.aboutToHidePanel();
	}

    
    
}
