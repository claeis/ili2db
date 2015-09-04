package ch.ehi.ili2db.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import ch.ehi.basics.wizard.*;


public class FunctionChoosePanel extends JPanel {
 
    private javax.swing.ButtonGroup connectorGroup;
    private javax.swing.JRadioButton importRB;
    private javax.swing.JRadioButton exportRB;
    private javax.swing.JRadioButton importSchemaRB;
    javax.swing.JLabel blankSpace;
    private javax.swing.JPanel jPanel1;
    
    public FunctionChoosePanel() {
     
        super();
                
        connectorGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        importRB = new javax.swing.JRadioButton();
        exportRB = new javax.swing.JRadioButton();
        importSchemaRB = new javax.swing.JRadioButton();
        
        importRB.setActionCommand("import");
        exportRB.setActionCommand("export");
        importSchemaRB.setActionCommand("importSchema");
        
        importRB.setSelected(true);
        
        this.setLayout(new java.awt.BorderLayout());
        this.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        importRB.setText("Import Transferfile");
        connectorGroup.add(importRB);
        jPanel1.add(importRB);

        exportRB.setText("Export Transferfile");
        connectorGroup.add(exportRB);
        jPanel1.add(exportRB);

        importSchemaRB.setText("Import Model");
        connectorGroup.add(importSchemaRB);
        jPanel1.add(importSchemaRB);

        this.add(jPanel1, java.awt.BorderLayout.CENTER);
    }
        
    public String getRadioButtonSelected() {
        return connectorGroup.getSelection().getActionCommand();
    }
    public int getIli2dbFunction()
    {
    	if(exportRB.isSelected()){
    		return Config.FC_EXPORT;
    	}
    	if(importRB.isSelected()){
    		return Config.FC_IMPORT;
    	}
    	if(importSchemaRB.isSelected()){
    		return Config.FC_SCHEMAIMPORT;
    	}
    	throw new IllegalStateException();
    }
}
