package ch.ehi.ili2fgdb;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class FgdbDbPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ch.ehi.basics.logging.TextAreaListener errlog=null;
	private JTextField dbhostUi = null;
	private JLabel dbhostLabel = null;
	private JLabel dbusrLabel = null;
	private JLabel dbpwdLabel = null;
	private JButton testConnectionButton = null;
	private JTextField dbusrUi = null;
	private JPasswordField dbpwdUi = null;
	private JLabel dbnameLabel = null;
	private JLabel dbportLabel = null;
	private JTextField dbnameUi = null;
	private JTextField dbportUi = null;
	/**
	 * This is the default constructor
	 */
	public FgdbDbPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
		gridBagConstraints33.fill = GridBagConstraints.BOTH;
		gridBagConstraints33.gridy = 5;
		gridBagConstraints33.weightx = 1.0;
		gridBagConstraints33.weighty = 1.0;
		gridBagConstraints33.gridwidth = 2;
		gridBagConstraints33.gridx = 0;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints4.gridy = 2;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
		gridBagConstraints32.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints32.gridy = 0;
		gridBagConstraints32.weightx = 1.0;
		gridBagConstraints32.gridx = 1;
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 2;
		dbportLabel = new JLabel();
		dbportLabel.setText("dbport");
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.gridx = 0;
		gridBagConstraints13.gridy = 0;
		dbnameLabel = new JLabel();
		dbnameLabel.setText("dbname");
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints31.gridy = 4;
		gridBagConstraints31.weightx = 1.0;
		gridBagConstraints31.gridx = 1;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints12.gridy = 3;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints3.gridy = 0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 4;
		dbpwdLabel = new JLabel();
		dbpwdLabel.setText("dbpwd");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 3;
		dbusrLabel = new JLabel();
		dbusrLabel.setText("dbusr");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		dbhostLabel = new JLabel();
		dbhostLabel.setText("dbhost");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getDbhostUi(), gridBagConstraints);
		this.add(dbhostLabel, gridBagConstraints1);
		this.add(dbusrLabel, gridBagConstraints11);
		this.add(dbpwdLabel, gridBagConstraints2);
		this.add(getTestConnectionButton(), gridBagConstraints3);
		this.add(getDbusrUi(), gridBagConstraints12);
		this.add(getDbpwdUi(), gridBagConstraints31);
		this.add(dbnameLabel, gridBagConstraints13);
		this.add(dbportLabel, gridBagConstraints21);
		this.add(getDbnameUi(), gridBagConstraints32);
		this.add(getDbportUi(), gridBagConstraints4);
		this.add(getLogScrollPane(), gridBagConstraints33);
		if(errlog==null){
			errlog=new ch.ehi.basics.logging.TextAreaListener();
			errlog.setOutputArea(getLogUi());
			EhiLogger.getInstance().addListener(errlog);
		}
		
	}

	/**
	 * This method initializes dbhostUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbhostUi() {
		if (dbhostUi == null) {
			dbhostUi = new JTextField();
			dbhostUi.setColumns(40);
		}
		return dbhostUi;
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
			testConnectionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getLogUi().setText("");
					ch.ehi.ili2db.gui.Config config=new ch.ehi.ili2db.gui.Config();
			    	config.setDbdatabase(getDbname());
			    	config.setDbhost(getDbhost());
			    	config.setDbport(getDbport());
			    	String dbusr=getDbusr();
			    	String dbpwd=getDbpwd();
			    	String dburl=dbUrlConverter.makeUrl(config);
					try{
						Class.forName(jdbcDriver);
					}catch(Exception ex){
						EhiLogger.logError("failed to load JDBC driver",ex);
						return;
					}
					Connection conn=null;
					try{
					  conn = DriverManager.getConnection(dburl, dbusr, dbpwd);
					}catch(Exception ex){
						EhiLogger.logError(ex);
						return;
					}finally{
						if(conn!=null){
							try {
								conn.close();
							} catch (SQLException ex) {
								EhiLogger.logError(ex);
							}
						}
					}
					JOptionPane.showMessageDialog(null, "Successfully connected","test connection", JOptionPane.INFORMATION_MESSAGE); 
				}
			});
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
	public String getDbname(){
		return purgeString(getDbnameUi().getText());
	}
	public void setDbname(String dbname){
		getDbnameUi().setText(dbname);
	}
	public String getDbhost(){
		return purgeString(getDbhostUi().getText());
	}
	public void setDbhost(String dbhost){
		getDbhostUi().setText(dbhost);
	}
	public String getDbport(){
		return purgeString(getDbportUi().getText());
	}
	public void setDbport(String dbport){
		getDbportUi().setText(dbport);
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

	/**
	 * This method initializes dbnameUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbnameUi() {
		if (dbnameUi == null) {
			dbnameUi = new JTextField();
			dbnameUi.setColumns(40);
		}
		return dbnameUi;
	}

	/**
	 * This method initializes dbportUi	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDbportUi() {
		if (dbportUi == null) {
			dbportUi = new JTextField();
			dbportUi.setColumns(40);
		}
		return dbportUi;
	}
	private DbUrlConverter dbUrlConverter;
	public DbUrlConverter getDbUrlConverter() {
		return dbUrlConverter;
	}
	public void setDbUrlConverter(DbUrlConverter dbUrlConverter) {
		this.dbUrlConverter = dbUrlConverter;
	}
	private String jdbcDriver=null;
	private JScrollPane logScrollPane = null;
	private JTextArea logUi = null;
	public void setJdbcDriver(String driver)
	{
		jdbcDriver=driver;
	}

	/**
	 * This method initializes logScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getLogScrollPane() {
		if (logScrollPane == null) {
			logScrollPane = new JScrollPane();
			logScrollPane.setViewportView(getLogUi());
		}
		return logScrollPane;
	}

	/**
	 * This method initializes logUi	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getLogUi() {
		if (logUi == null) {
			logUi = new JTextArea();
			logUi.setEditable(false);
			logUi.setRows(10);
			logUi.setColumns(40);
		}
		return logUi;
	}

}
