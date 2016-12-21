package albuquerque.paulo.wlslogviewer.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
/**
 * Class that formats the WLSLogViewer own logs format.
 * Not to be confused with the WLS logs parsed by this
 * tool.
 * @author Paulo Albuquerque
 *
 */
public class LogFileFormatter extends Formatter {
    /**
     * Formats a log line and returns a formatted
     * string to be saved to the log file.
     * @param rec the log record to be formatted
     * @return the formatted string
     */
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer();
        //appends the new line token
        buf.append("#### ");
        buf.append("<" + rec.getSequenceNumber() + "> ");
        buf.append("<" + formatDate(rec.getMillis()) + "> ");
        buf.append("<" + rec.getLevel() + "> ");
        buf.append("<Thread-" + rec.getThreadID() + "> ");
        buf.append("<" + formatPackage(rec.getLoggerName()) + "> ");
        buf.append("<" + rec.getMessage() + ">");
        buf.append("\n");

        return buf.toString();
    }
    /**
     * Utility method to format date
     * @param millisecs the time in millis
     * @return the formated time
     */
    private String formatDate(long millisecs) {
        //instantiates SimpleDateFormat with the spec 'MMM, dd yyyy HH:mm:ss.SSS z'
        SimpleDateFormat date_format = new SimpleDateFormat("MMM, dd yyyy HH:mm:ss.SSS z");
        //instantiate a new Date object with the millis argument
        Date resultdate = new Date(millisecs);
        //returns the formatted date
        return date_format.format(resultdate);
    }
    /**
     * Utility method to remove the package name from the logger name
     * @param pkg full qualified class name
     * @return class name without 'package'
     */
    private String formatPackage(String pkg) {
        return pkg.replace("package ", "");
    }

}