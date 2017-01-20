package ch.ehi.ili2db.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.ehi.basics.view.FileChooser;
import ch.ehi.basics.view.GenericFileFilter;
import ch.ehi.ili2db.base.Ili2db;

public class ImportSchemaPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private javax.swing.ButtonGroup modelGroup = new javax.swing.ButtonGroup();
	private JLabel iliFileLbl = null;
	private JTextField iliFileUi = null;
	private JButton iliFileButton = null;

	/**
	 * This is the default constructor
	 */
	public ImportSchemaPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 2;
		gridBagConstraints7.gridy = 3;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 3;
		iliFileLbl = new JLabel();
		iliFileLbl.setText("iliFile");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(iliFileLbl, gridBagConstraints3);
		this.add(getIliFileUi(), gridBagConstraints5);
		this.add(getIliFileButton(), gridBagConstraints7);
	}

	/**
	 * This method initializes iliFileUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getIliFileUi() {
		if (iliFileUi == null) {
			iliFileUi = new JTextField();
			iliFileUi.setColumns(40);
			iliFileUi.setEnabled(true);
		}
		return iliFileUi;
	}

	/**
	 * This method initializes iliFileButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getIliFileButton() {
		if (iliFileButton == null) {
			iliFileButton = new JButton();
			iliFileButton.setText("...");
			iliFileButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FileChooser openDialog =  new FileChooser();
					if(settings.getValue(Ili2db.SETTING_DIRUSED)!=null){
						openDialog.setCurrentDirectory(new java.io.File(settings.getValue(Ili2db.SETTING_DIRUSED)));
					}
					openDialog.addChoosableFileFilter(new GenericFileFilter("INTERLIS-Model (*.ili)","ili"));
					if (openDialog.showOpenDialog(ImportSchemaPanel.this) == FileChooser.APPROVE_OPTION) {
						settings.setValue(Ili2db.SETTING_DIRUSED,openDialog.getCurrentDirectory().getAbsolutePath());
						setModels(openDialog.getSelectedFile().getAbsolutePath());
					}				
				}
			});
		}
		return iliFileButton;
	}
	public void setMappingconfig(String configfile){
		//getMappingconfigUi().setText(configfile);
	}
	public String getMappingconfig(){
		//return strip(getMappingconfigUi().getText());
		return null;
	}
	public String getModeldir(){
		//return strip(getModeldirUi().getText());
		return null;
	}
	public void setModeldir(String modeldir){
		//getModeldirUi().setText(modeldir);
	}
	public String getModels(){
		return strip(getIliFileUi().getText());
	}
	public void setModels(String models){
		if(models!=null && models.equals(ch.ehi.ili2db.base.Ili2db.XTF)){
			models="";
		}
		getIliFileUi().setText(models);
	}
	public String getDropscript(){
		//return strip(getDropscriptUi().getText());
		return null;
	}
	public void setDropscript(String dropscript){
		//getDropscriptUi().setText(dropscript);
	}
	public String getCreatescript(){
		//return strip(getCreatescriptUi().getText());
		return null;
	}
	public void setCreatescript(String createscript){
		//getCreatescriptUi().setText(createscript);
	}
	private static String strip(String value)
	{
		return ch.ehi.basics.tools.StringUtility.purge(value);
	}
	private ch.ehi.basics.settings.Settings settings=null;
	public ch.ehi.basics.settings.Settings getSettings() {
		return settings;
	}

	public void setSettings(ch.ehi.basics.settings.Settings settings) {
		this.settings = settings;
	}
}
