package albuquerque.paulo.wlslogviewer.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFileFormatter extends Formatter {

    public String format(LogRecord rec) {

        StringBuffer buf = new StringBuffer();

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

    private String formatDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM, dd yyyy HH:mm:ss.SSS z");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    private String formatPackage(String pkg) {
        return pkg.replace("package ", "");
    }

}