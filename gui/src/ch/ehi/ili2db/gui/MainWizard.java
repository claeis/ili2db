package ch.ehi.ili2db.gui;


import javax.swing.JOptionPane;

import ch.ehi.ili2db.base.DbUrlConverter;

public class MainWizard {
    public boolean showDialog() {
        JOptionPane.showMessageDialog(null, "ili2db doesn't support a GUI yet. Please use the command line.");
        return true;
    }
}
