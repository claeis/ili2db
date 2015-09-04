package ch.ehi.basics.wizard;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class implements a basic wizard dialog, where the programmer can
 * insert one or more Components to act as panels. These panels can be navigated
 * through arbitrarily using the 'Next' or 'Back' buttons, or the dialog itself
 * can be closed using the 'Cancel' button. Note that even though the dialog
 * uses a CardLayout manager, the order of the panels is not linear. Each panel
 * determines at runtime what its next and previous panel will be.
 */
public class Wizard extends WindowAdapter implements PropertyChangeListener {

    /**
     * Indicates that the 'Finish' button was pressed to close the dialog.
     */    
    public static final int FINISH_RETURN_CODE = 0;
    /**
     * Indicates that the 'Cancel' button was pressed to close the dialog, or
     * the user pressed the close box in the corner of the window.
     */    
    public static final int CANCEL_RETURN_CODE = 1;
    /**
     * Indicates that the dialog closed due to an internal error.
     */    
    public static final int ERROR_RETURN_CODE = 2;
        
    /**
     * The String-based action command for the 'Next' button.
     */    
    public static final String NEXT_BUTTON_ACTION_COMMAND = "NextButtonActionCommand";
    /**
     * The String-based action command for the 'Back' button.
     */    
    public static final String BACK_BUTTON_ACTION_COMMAND = "BackButtonActionCommand";
    /**
     * The String-based action command for the 'Cancel' button.
     */    
    public static final String CANCEL_BUTTON_ACTION_COMMAND = "CancelButtonActionCommand";
        

    // The i18n text used for the buttons. Loaded from a property resource file.    
    
    static String BACK_TEXT;
    static String NEXT_TEXT;
    static String FINISH_TEXT;
    static String CANCEL_TEXT;
        
    private WizardData wizardData;
    private WizardController wizardController;
    private JDialog wizardDialog;
        
    private JPanel cardPanel;
    private CardLayout cardLayout;            
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    private JLabel panelTitleLabel;
    private JLabel panelIconLabel;
    
    private int returnCode;

    
    
    /**
     * Default constructor. This method creates a new WizardModel object and passes it
     * into the overloaded constructor.
     */    
    public Wizard() {
        this((Frame)null);
    }
    
    /**
     * This method accepts a java.awt.Dialog object as the javax.swing.JDialog's
     * parent.
     * @param owner The java.awt.Dialog object that is the owner of this dialog.
     */    
    public Wizard(Dialog owner) {
        wizardData = new WizardData();
        wizardDialog = new JDialog(owner);         
        initComponents();
    }
 
    /**
     * This method accepts a java.awt.Frame object as the javax.swing.JDialog's
     * parent.
     * @param owner The java.awt.Frame object that is the owner of the javax.swing.JDialog.
     */    
    public Wizard(Frame owner) {
        wizardData = new WizardData();
        wizardDialog = new JDialog(owner);         
        initComponents();
    }
    
    /**
     * Returns an instance of the JDialog that this class created. This is useful in
     * the event that you want to change any of the JDialog parameters manually.
     * @return The JDialog instance that this class created.
     */    
    public JDialog getDialog() {
        return wizardDialog;
    }
    
    /**
     * Returns the owner of the generated javax.swing.JDialog.
     * @return The owner (java.awt.Frame or java.awt.Dialog) of the javax.swing.JDialog generated
     * by this class.
     */    
    public Component getOwner() {
        return wizardDialog.getOwner();
    }
    
    /**
     * Sets the title of the generated javax.swing.JDialog.
     * @param s The title of the dialog.
     */    
    public void setTitle(String s) {
        wizardDialog.setTitle(s);
    }
    
    /**
     * Returns the current title of the generated dialog.
     * @return The String-based title of the generated dialog.
     */    
    public String getTitle() {
        return wizardDialog.getTitle();
    }
    
    /**
     * Sets the modality of the generated javax.swing.JDialog.
     * @param b the modality of the dialog
     */    
    public void setModal(boolean b) {
        wizardDialog.setModal(b);
    }
    
    /**
     * Returns the modality of the dialog.
     * @return A boolean indicating whether or not the generated javax.swing.JDialog is modal.
     */    
    public boolean isModal() {
        return wizardDialog.isModal();
    }
    
    /**
     * Convienence method that displays a modal wizard dialog and blocks until the dialog
     * has completed.
     * @return Indicates how the dialog was closed. Compare this value against the RETURN_CODE
     * constants at the beginning of the class.
     */    
    public int showModalDialog() {
        
        wizardDialog.setModal(true);
        wizardDialog.pack();
        wizardDialog.show();
        
        return returnCode;
    }
    
    /**
     * Returns the current model of the wizard dialog.
     * @return A WizardModel instance, which serves as the model for the wizard dialog.
     */    
    public WizardData getWizardData() {
        return wizardData;
    }
    
    /**
     * Add a Component as a panel for the wizard dialog by registering its
     * WizardPanelDescriptor object. Each panel is identified by a unique Object-based
     * identifier (often a String), which can be used by the setCurrentPanel()
     * method to display the panel at runtime.
     * @param id An Object-based identifier used to identify the WizardPanelDescriptor object.
     * @param panel The WizardPanelDescriptor object which contains helpful information about the panel.
     */    
    public void registerWizardPanel(Object id, WizardPanelDescriptor panel) {
        
        //  Add the incoming panel to our JPanel display that is managed by
        //  the CardLayout layout manager.
        
        cardPanel.add(panel.getPanelComponent(), id);
        
        //  Set a callback to the current wizard.
        
        panel.setWizard(this);
        
        //  Place a reference to it in the model. 
        
        wizardData.registerPanel(id, panel);
        
    }  
    
    /**
     * Displays the panel identified by the object passed in. This is the same Object-based
     * identified used when registering the panel.
     * @param id The Object-based identifier of the panel to be displayed.
     */    
    public void setCurrentPanel(Object id) {

        //  Get the hashtable reference to the panel that should
        //  be displayed. If the identifier passed in is null, then close
        //  the dialog.
        
        if (id == null)
            close(ERROR_RETURN_CODE);
        
        WizardPanelDescriptor oldPanelDescriptor = wizardData.getCurrentPanelDescriptor();
        if (oldPanelDescriptor != null)
            oldPanelDescriptor.aboutToHidePanel();
        
        wizardData.setCurrentPanel(id);
        wizardData.getCurrentPanelDescriptor().aboutToDisplayPanel();
        
        //  Show the panel in the dialog.
        panelTitleLabel.setText(wizardData.getCurrentPanelDescriptor().getPanelTitle());
        ImageIcon icon = wizardData.getCurrentPanelDescriptor().getPanelTitleIcon();
        panelTitleLabel.setIcon(icon);
        
        cardLayout.show(cardPanel, id.toString());
        wizardData.getCurrentPanelDescriptor().displayingPanel();        
        
        
    }
    
    /**
     * Method used to listen for property change events from the model and update the
     * dialog's graphical components as necessary.
     * @param evt PropertyChangeEvent passed from the model to signal that one of its properties has changed value.
     */    
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (evt.getPropertyName().equals(WizardData.CURRENT_PANEL_DESCRIPTOR_PROPERTY)) {
            wizardController.resetButtonsToPanelRules(); 
        } else if (evt.getPropertyName().equals(WizardData.NEXT_FINISH_BUTTON_TEXT_PROPERTY)) {            
            nextButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardData.BACK_BUTTON_TEXT_PROPERTY)) {            
            backButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardData.CANCEL_BUTTON_TEXT_PROPERTY)) {            
            cancelButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardData.NEXT_FINISH_BUTTON_ENABLED_PROPERTY)) {            
            nextButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardData.BACK_BUTTON_ENABLED_PROPERTY)) {            
            backButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardData.CANCEL_BUTTON_ENABLED_PROPERTY)) {            
            cancelButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardData.NEXT_FINISH_BUTTON_ICON_PROPERTY)) {            
            nextButton.setIcon((Icon)evt.getNewValue());
        } else if (evt.getPropertyName().equals(WizardData.BACK_BUTTON_ICON_PROPERTY)) {            
            backButton.setIcon((Icon)evt.getNewValue());
        } else if (evt.getPropertyName().equals(WizardData.CANCEL_BUTTON_ICON_PROPERTY)) {            
            cancelButton.setIcon((Icon)evt.getNewValue());
        }
        
    }
    
    /**
     * Retrieves the last return code set by the dialog.
     * @return An integer that identifies how the dialog was closed. See the *_RETURN_CODE
     * constants of this class for possible values.
     */    
    public int getReturnCode() {
        return returnCode;
    }
    
   /**
     * Mirrors the WizardModel method of the same name.
     * @return A boolean indicating if the button is enabled.
     */  
    public boolean getBackButtonEnabled() {
        return wizardData.getBackButtonEnabled().booleanValue();
    }

   /**
     * Mirrors the WizardModel method of the same name.
     * @param boolean newValue The new enabled status of the button.
     */ 
    public void setBackButtonEnabled(boolean newValue) {
        wizardData.setBackButtonEnabled(new Boolean(newValue));
    }

   /**
     * Mirrors the WizardModel method of the same name.
     * @return A boolean indicating if the button is enabled.
     */ 
    public boolean getNextFinishButtonEnabled() {
        return wizardData.getNextFinishButtonEnabled().booleanValue();
    }

   /**
     * Mirrors the WizardModel method of the same name.
     * @param boolean newValue The new enabled status of the button.
     */ 
    public void setNextFinishButtonEnabled(boolean newValue) {
        wizardData.setNextFinishButtonEnabled(new Boolean(newValue));
    }
 
   /**
     * Mirrors the WizardModel method of the same name.
     * @return A boolean indicating if the button is enabled.
     */ 
    public boolean getCancelButtonEnabled() {
        return wizardData.getCancelButtonEnabled().booleanValue();
    }

    /**
     * Mirrors the WizardModel method of the same name.
     * @param boolean newValue The new enabled status of the button.
     */ 
    public void setCancelButtonEnabled(boolean newValue) {
        wizardData.setCancelButtonEnabled(new Boolean(newValue));
    }
    
    /**
     * Closes the dialog and sets the return code to the integer parameter.
     * @param code The return code.
     */    
    void close(int code) {
        returnCode = code;
        wizardDialog.dispose();
    }
    
    /**
     * This method initializes the components for the wizard dialog: it creates a JDialog
     * as a CardLayout panel surrounded by a small amount of space on each side, as well
     * as three buttons at the bottom.
     */
    
    private void initComponents() {

        wizardData.addPropertyChangeListener(this);       
        wizardController = new WizardController(this);       

        wizardDialog.getContentPane().setLayout(new BorderLayout());
        wizardDialog.addWindowListener(this);
                
        /////
        
        JPanel titlePanel = new javax.swing.JPanel();
        panelTitleLabel = new javax.swing.JLabel();
        panelIconLabel = new javax.swing.JLabel();

        titlePanel.setLayout(new java.awt.BorderLayout());
        titlePanel.setBackground(Color.gray);
        
        panelTitleLabel.setBackground(Color.gray);
        panelTitleLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 14));
        panelTitleLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        panelTitleLabel.setOpaque(true);

        panelIconLabel.setBackground(Color.gray);
        
        titlePanel.add(panelTitleLabel, BorderLayout.CENTER);
        titlePanel.add(panelIconLabel, BorderLayout.EAST);
        titlePanel.add(new javax.swing.JSeparator(), BorderLayout.SOUTH);
        
        /////
        
        
        //  Create the outer wizard panel, which is responsible for three buttons:
        //  Next, Back, and Cancel. It is also responsible a JPanel above them that
        //  uses a CardLayout layout manager to display multiple panels in the 
        //  same spot.
        
        JPanel buttonPanel = new JPanel();
        JSeparator separator = new JSeparator();
        Box buttonBox = new Box(BoxLayout.X_AXIS);

        cardPanel = new JPanel();
        cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       

        cardLayout = new CardLayout(); 
        cardPanel.setLayout(cardLayout);
        
        backButton = new JButton();
        nextButton = new JButton();
        cancelButton = new JButton();
        
        backButton.setActionCommand(BACK_BUTTON_ACTION_COMMAND);
        nextButton.setActionCommand(NEXT_BUTTON_ACTION_COMMAND);
        cancelButton.setActionCommand(CANCEL_BUTTON_ACTION_COMMAND);

        backButton.addActionListener(wizardController);
        nextButton.addActionListener(wizardController);
        cancelButton.addActionListener(wizardController);
        
        //  Create the buttons with a separator above them, then place them
        //  on the east side of the panel with a small amount of space between
        //  the back and the next button, and a larger amount of space between
        //  the next button and the cancel button.
        
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(separator, BorderLayout.NORTH);

        buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(cancelButton);
        
        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        
        wizardDialog.getContentPane().add(titlePanel, java.awt.BorderLayout.NORTH);
        wizardDialog.getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
        wizardDialog.getContentPane().add(cardPanel, java.awt.BorderLayout.CENTER);

    }
    
   /**
     * If the user presses the close box on the dialog's window, treat it
     * as a cancel.
     * @param WindowEvent The event passed in from AWT.
     */ 
    
    public void windowClosing(WindowEvent e) {
        returnCode = CANCEL_RETURN_CODE;
    }
    
    
    
    static {

        try { 
            
            PropertyResourceBundle resources = (PropertyResourceBundle)
                ResourceBundle.getBundle("ch.ehi.basics.wizard.wizard");
            
            BACK_TEXT = (String)(resources.getObject("backButtonText"));
            NEXT_TEXT = (String)(resources.getObject("nextButtonText"));
            CANCEL_TEXT = (String)(resources.getObject("cancelButtonText"));
            FINISH_TEXT = (String)(resources.getObject("finishButtonText"));
            
        } catch (MissingResourceException mre) {
            System.out.println(mre);
            System.exit(1);
        }
    }

}
