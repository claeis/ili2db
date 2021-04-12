package ch.ehi.ili2db.gui;


import javax.swing.JOptionPane;

import ch.ehi.ili2db.base.DbUrlConverter;

public class MainWizard {
	public static void main(Config config,String appHome,String appName,AbstractDbPanelDescriptor dbPanelDescriptor,DbUrlConverter makeUrl) {
	    JOptionPane.showMessageDialog(null,
	            appName+" doesn't support a GUI yet. Please use the command line.");	}

}
