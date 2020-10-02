package ch.ehi.ili2db;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GenericDbPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField dburlUi = null;
	private JLabel dburlLabel = null;
	private JLabel dbusrLabel = null;
	private JLabel dbpwdLabel = null;
	private JButton testConnectionButton = null;
	private JTextField dbusrUi = null;
	private JPasswordField dbpwdUi = null;
	/**
	 * This is the default constructor
	 */
	public GenericDbPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints31.gridy = 2;
		gridBagConstraints31.weightx = 1.0;
		gridBagConstraints31.gridx = 1;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints3.gridy = 0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		dbpwdLabel = new JLabel();
		dbpwdLabel.setText("dbpwd");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 1;
		dbusrLabel = new JLabel();
		dbusrLabel.setText("dbusr");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		dburlLabel = new JLabel();
		dburlLabel.setText("dburl");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getDburlUi(), gridBagConstraints);
		this.add(dburlLabel, gridBagConstraints1);
		this.add(dbusrLabel, gridBagConstraints11);
		this.add(dbpwdLabel, gridBagConstraints2);
		this.add(getTestConnectionButton(), gridBagConstraints3);
		this.add(getDbusrUi(), gridBagConstraints12);
		this.add(getDbpwdUi(), gridBagConstraints31);
	}

	/**
	 * This method initializes dburlUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDburlUi() {
		if (dburlUi == null) {
			dburlUi = new JTextField();
			dburlUi.setColumns(40);
		}
		return dburlUi;
	}

	/**
	 * This method initializes testConnectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getTestConnectionButton() {
		if (testConnectionButton == null) {
			testConnectionButton = new JButton();
			testConnectionButton.setText("test connection");
		}
		return testConnectionButton;
	}

	/**
	 * This method initializes dbusrUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbusrUi() {
		if (dbusrUi == null) {
			dbusrUi = new JTextField();
			dbusrUi.setColumns(40);
		}
		return dbusrUi;
	}

	/**
	 * This method initializes dbpwdUi	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getDbpwdUi() {
		if (dbpwdUi == null) {
			dbpwdUi = new JPasswordField();
			dbpwdUi.setColumns(40);
		}
		return dbpwdUi;
	}
	public String getDburl(){
		return purgeString(getDburlUi().getText());
	}
	public void setDburl(String dbhost){
		getDburlUi().setText(dbhost);
	}
	public String getDbusr(){
		return purgeString(getDbusrUi().getText());
	}
	public void setDbusr(String dbusr){
		getDbusrUi().setText(dbusr);
	}
	public String getDbpwd(){
		return purgeString(getDbpwdUi().getText());
	}
	public void setDbpwd(String dbpwd){
		getDbpwdUi().setText(dbpwd);
	}
	/** removes leading and trailing white space and returns null if nothing left.
	 */
	private static String purgeString(String value){
		return ch.ehi.basics.tools.StringUtility.purge(value);
	}

}
