package ch.ehi.ili2db.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.swing.SwingWorker;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;

public class WorkPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton workButton = null;
	private ch.ehi.basics.logging.TextAreaListener errlog=null;
	
	/**
	 * This is the default constructor
	 */
	public WorkPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints2.gridy = 2;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints11.gridy = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.gridheight = 2;
		gridBagConstraints1.gridx = 0;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJScrollPane(), gridBagConstraints1);
		this.add(getWorkButton(), gridBagConstraints11);
		this.add(getClearlogButton(), gridBagConstraints2);
		if(errlog==null){
			errlog=new ch.ehi.basics.logging.TextAreaListener();
			errlog.setOutputArea(getLogUi());
			EhiLogger.getInstance().addListener(errlog);
		}
	}

	/**
	 * This method initializes workButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getWorkButton() {
		if (workButton == null) {
			workButton = new JButton();
			workButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingWorker worker = new SwingWorker() {
						public Object construct() {
							try {
								config.setDburl(getDbUrlConverter().makeUrl(config));
								if(config.getXtffile()!=null && Ili2db.isItfFilename(config.getXtffile())){
									config.setItfTransferfile(true);
								}
								Ili2db.run(config,getAppHome());
							}
							catch (Exception ex) {
								EhiLogger.logError("failed",ex);
							}
							return null;
						}
					};
					worker.start();
				}
			});
		}
		return workButton;
	}
	public void setWorkBtn(String text){
		getWorkButton().setText(text);
	}
	private String appHome=null;
	private Config config=null;
	private JScrollPane jScrollPane = null;
	private JTextArea logUi = null;
	public void setConfig(Config config)
	{
		this.config=config;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getLogUi());
		}
		return jScrollPane;
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
		}
		return logUi;
	}

	public String getAppHome() {
		return appHome;
	}

	public void setAppHome(String appHome) {
		this.appHome = appHome;
	}
	private DbUrlConverter dbUrlConverter;
	private JButton clearlogButton = null;
	public DbUrlConverter getDbUrlConverter() {
		return dbUrlConverter;
	}
	public void setDbUrlConverter(DbUrlConverter dbUrlConverter) {
		this.dbUrlConverter = dbUrlConverter;
	}

	/**
	 * This method initializes clearlogButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearlogButton() {
		if (clearlogButton == null) {
			clearlogButton = new JButton();
			clearlogButton.setText("clear log");
			clearlogButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getLogUi().setText("");
				}
			});
		}
		return clearlogButton;
	}
}
