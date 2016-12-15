package albuquerque.paulo.wlslogviewer.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import albuquerque.paulo.wlslogviewer.configuration.AppPreferences;

public class WLSLogViewerLoggingManager {

    static private FileHandler logFileOutput;
    static private Formatter logTextFormatter;

    public static void setUp() {
        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        try {

            // get the Parent Logger and set its level
            Logger parentLogger = Logger.getLogger(AppPreferences.getProperty("wlslogviewer.parent.logger"));
            parentLogger.setLevel(Level.parse(AppPreferences.getProperty("wlslogviewer.parent.log.level")));

            // create a File formatter
            logTextFormatter = new LogFileFormatter();

            // Create the File Handler and set its formatter
            logFileOutput = new FileHandler(AppPreferences.getProperty("wlslogviewer.logging.file"));
            logFileOutput.setFormatter(logTextFormatter);

            // Set the Parent Logger File Handler
            parentLogger.addHandler(logFileOutput);

            parentLogger.config("WLSLogViewer Logger Started, all messages are going to be logged to this file");

            // get the Trombone application loggers and set their levels
            /*
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.restservices.logger"))).setLevel(Level.parse(
             * TromboneConfig.getString("trombone.restservices.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.business.logger"))).setLevel(Level.parse(TromboneConfig
             * .getString("trombone.business.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.config.logger"))).setLevel(Level.parse(TromboneConfig.
             * getString("trombone.config.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString("trombone.dao.logger")
             * )).setLevel(Level.parse(TromboneConfig.getString(
             * "trombone.dao.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.entity.logger"))).setLevel(Level.parse(TromboneConfig.
             * getString("trombone.entity.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.restservices.logger"))).setLevel(Level.parse(
             * TromboneConfig.getString("trombone.restservices.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString(
             * "trombone.security.realm.logger"))).setLevel(Level.parse(
             * TromboneConfig.getString("trombone.security.realm.logger.level"))
             * ); (Logger.getLogger(TromboneConfig.getString(
             * "trombone.services.logger"))).setLevel(Level.parse(TromboneConfig
             * .getString("trombone.services.logger.level")));
             * (Logger.getLogger(TromboneConfig.getString("trombone.util.logger"
             * ))).setLevel(Level.parse(TromboneConfig.getString(
             * "trombone.util.logger.level")));
             */

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
