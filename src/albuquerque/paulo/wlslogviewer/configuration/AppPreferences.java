package albuquerque.paulo.wlslogviewer.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Creates and keep track of the application preferences.
 * Checks if the file wlslogviewer.properties is present
 * in the user home directory. If not, it creates it with
 * the basic preferences
 * 
 * @author Paulo Albuquerque
 *
 */
public class AppPreferences {
    //Default properties
    private static Properties defaultProps = new Properties();
    //properties file name
    private static String preferencesFileName = "wlslogviewer.properties";

    /**
     * static block to check if the properties file exist in the user home folder
     */
    static {
        //the properties file
        File preferences = new File(System.getProperty("user.home") + "/" + preferencesFileName);
        //if the file exists and can be read loads the saved preferences
        if (preferences.exists() && preferences.canRead()) {
            loadPreferences();
        } else {
          //if the file does not exist, it creates a basic prefenreces file
            createPreferences(System.getProperty("user.home") + "/" + preferencesFileName);
        }
    }

    /**
     * Loads the preferences file from the user home folder
     */
    private static void loadPreferences() {
        try {
            //loads the property file from the user home directory
            defaultProps.load(new FileInputStream(System.getProperty("user.home") + "/" + preferencesFileName));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * create the default property file
     * @param file_path the full path to the property file
     */
    private static void createPreferences(String file_path) {
        //diagnostic file line start token
        defaultProps.setProperty("wlslogviewer.diagnostic.begin.line", "[");
        //log file line start token
        defaultProps.setProperty("wlslogviewer.logfile.begin.line", "####");
        //out file line start tokne
        defaultProps.setProperty("wlslogviewer.outfile.begin.line", "<");
        //log level
        defaultProps.setProperty("wlslogviewer.log.level", "INFO");
        //last opened file location
        defaultProps.setProperty("wlslogviewer.last.location", "");
        //log file path
        defaultProps.setProperty("wlslogviewer.logging.file", System.getProperty("user.home") + "/wlslogviewer.log" );
       
        try {
            //save the property file
            defaultProps.store(new FileOutputStream(file_path), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Get the property by key
     * @param key the property key
     * @return the property value
     */
    public static String getProperty(String key) {
        return defaultProps.getProperty(key);
    }
    
    /**
     * Adds a new property to the property file
     * @param key the property key
     * @param value the property valeu
     */
    public static void setProperty(String key, String value) {
        defaultProps.setProperty(key, value);

        try {
            defaultProps.store(new FileOutputStream(System.getProperty("user.home") + "/" + preferencesFileName), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
