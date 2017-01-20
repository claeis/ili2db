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

public class ExportPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel xtffileLbl = null;
	private javax.swing.ButtonGroup modelGroup = new javax.swing.ButtonGroup();
	private JLabel iliFileLbl = null;
	private JTextField xtfFileUi = null;
	private JTextField iliFileUi = null;
	private JButton xtfFileButton = null;
	private JButton iliFileButton = null;

	/**
	 * This is the default constructor
	 */
	public ExportPanel() {
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
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 2;
		gridBagConstraints6.gridy = 0;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints4.gridy = 0;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 3;
		iliFileLbl = new JLabel();
		iliFileLbl.setText("iliFile");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		xtffileLbl = new JLabel();
		xtffileLbl.setText("xtffile");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(xtffileLbl, gridBagConstraints);
		this.add(iliFileLbl, gridBagConstraints3);
		this.add(getXtfFileUi(), gridBagConstraints4);
		this.add(getIliFileUi(), gridBagConstraints5);
		this.add(getXtfFileButton(), gridBagConstraints6);
		this.add(getIliFileButton(), gridBagConstraints7);
	}

	/**
	 * This method initializes xtfFileUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getXtfFileUi() {
		if (xtfFileUi == null) {
			xtfFileUi = new JTextField();
			xtfFileUi.setColumns(40);
		}
		return xtfFileUi;
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
	 * This method initializes xtfFileButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getXtfFileButton() {
		if (xtfFileButton == null) {
			xtfFileButton = new JButton();
			xtfFileButton.setText("...");
			xtfFileButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FileChooser saveDialog =  new FileChooser();
					if(settings.getValue(Ili2db.SETTING_DIRUSED)!=null){
						saveDialog.setCurrentDirectory(new java.io.File(settings.getValue(Ili2db.SETTING_DIRUSED)));
					}
					saveDialog.addChoosableFileFilter(GenericFileFilter.createXmlFilter());
					saveDialog.addChoosableFileFilter(new GenericFileFilter("INTERLIS 2-Transfer (*.xtf)","xtf"));
					saveDialog.addChoosableFileFilter(new GenericFileFilter("INTERLIS 1-Transfer (*.itf)","itf"));
					if (saveDialog.showSaveDialog(ExportPanel.this) == FileChooser.APPROVE_OPTION) {
						settings.setValue(Ili2db.SETTING_DIRUSED,saveDialog.getCurrentDirectory().getAbsolutePath());
						setXtffile(saveDialog.getSelectedFile().getAbsolutePath());
					}				
				}
			});
		}
		return xtfFileButton;
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
					if (openDialog.showOpenDialog(ExportPanel.this) == FileChooser.APPROVE_OPTION) {
						settings.setValue(Ili2db.SETTING_DIRUSED,openDialog.getCurrentDirectory().getAbsolutePath());
						setModels(openDialog.getSelectedFile().getAbsolutePath());
					}				
				}
			});
		}
		return iliFileButton;
	}
	public String getXtffile(){
		return strip(getXtfFileUi().getText());
	}
	public void setXtffile(String file){
		getXtfFileUi().setText(file);
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
