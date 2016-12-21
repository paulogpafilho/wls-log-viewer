package albuquerque.paulo.wlslogviewer.ui;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import albuquerque.paulo.wlslogviewer.configuration.AppPreferences;
import albuquerque.paulo.wlslogviewer.logging.WLSLogViewerLoggingManager;

/**
 * The entry point for WLS Log Viewer
 * @author Paulo Albuquerque
 * File Chooser UI to select Log File to open
 *
 */
public class OpenLogWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    //this logger name
    private static final Logger log = Logger.getLogger("albuquerque.paulo.wlslogviewer");
    //Content Pane
    private JPanel jContentPane = null;
    //File name Text Field
    private JTextField jTextField = null;
    //Choose file button
    private JButton jButton = null;
    //Open file button
    private JButton jButton1 = null;
    //File Chooser
    private JFileChooser jFileChooser = null;
    //Window
    private static OpenLogWindow openLogWindow;
    //Reference to the log viewer window
    private LogViewerWindow main;

    // public LogViewerWindow getMain() {
    //     return main;
    // }

    //Sets the reference to the Log Viewer Window
    public void setMain(LogViewerWindow main) {
        this.main = main;
    }

    //The main method. entry point for the application
    public static void main(String[] args) {
        WLSLogViewerLoggingManager.setUp();
        if (openLogWindow == null) {
            openLogWindow = new OpenLogWindow();
            openLogWindow.setResizable(false);
            openLogWindow.setVisible(true);
        }
    }

    ActionListener chooseFileAction = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            openFileChooser();
        }
    };

    ActionListener openFileAction = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            openFile();
        }
    };

    private void openFile() {
        log.info("Opening a new File");
        if (main == null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {

                    String log = jTextField.getText();

                    try {

                        String lastLocation = log.substring(0, log.lastIndexOf("\\"));

                        AppPreferences.setProperty("wlslogviewer.last.location", lastLocation);

                        main = new LogViewerWindow(log, openLogWindow);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            main.openFile(jTextField.getText());
        }
        openLogWindow.setVisible(false);
    }

    private void openFileChooser() {
        jFileChooser.setCurrentDirectory(new File(AppPreferences.getProperty("wlslogviewer.last.location")));
        int returnVal = jFileChooser.showOpenDialog(OpenLogWindow.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                jTextField.setText(file.getCanonicalPath().toString());
            } catch (Exception e) {
                log.severe("Error: " + e.getMessage());
            }
            log.info("Opening: " + file.getName() + ".");
        } else {
            log.info("Open command cancelled by user.");
        }
    }

    /**
     * This is the default constructor
     */
    public OpenLogWindow() {
        super();
        initialize();
    }

    /**
     * initializes the GUI components
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(352, 237);
        this.setContentPane(getJContentPane());
        this.setTitle("WLS Log Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJTextField(), null);
            jContentPane.add(getJButton(), null);
            jContentPane.add(getJButton1(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new JTextField();
            jTextField.setBounds(new Rectangle(30, 15, 197, 31));
        }
        return jTextField;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setBounds(new Rectangle(239, 15, 78, 30));
            jButton.setText("Choose");
            jButton.addActionListener(chooseFileAction);
        }
        return jButton;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setBounds(new Rectangle(31, 62, 287, 111));
            jButton1.setText("Open File");
            jButton1.addActionListener(openFileAction);
        }
        return jButton1;
    }

}
