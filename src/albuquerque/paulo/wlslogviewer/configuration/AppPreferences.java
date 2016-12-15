package albuquerque.paulo.wlslogviewer.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppPreferences {

    private static Properties defaultProps = new Properties();

    static {
        File preferences = new File("config.properties");

        if (preferences.exists()) {
            loadPreferences();
        } else {
            createPreferences();
        }
    }

    private static void loadPreferences() {

        try {

            defaultProps.load(new FileInputStream("config.properties"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void createPreferences() {

        defaultProps.setProperty("wlslogviewer.logfile.begin.line", "####");
        defaultProps.setProperty("wlslogviewer.log.level", "DEBUG");
        defaultProps.setProperty("wlslogviewer.last.location", "");

        try {
            defaultProps.store(new FileOutputStream("config.properties"), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getProperty(String key) {
        return defaultProps.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        defaultProps.setProperty(key, value);

        try {
            defaultProps.store(new FileOutputStream("config.properties"), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
