package ch.ehi.ili2db;

import javax.swing.JPanel;

import ch.ehi.ili2db.gui.AbstractDbPanelDescriptor;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.gui.Ili2dbWizard;

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
