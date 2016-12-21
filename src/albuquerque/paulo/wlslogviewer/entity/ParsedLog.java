package albuquerque.paulo.wlslogviewer.entity;
/**
 * Class that represent a parsed log
 * @author Paulo Albuquerque
 *
 */
public class ParsedLog {
    //The file data
    private Object[][] data;
    //log type
    private LogFileType type;
    //types of log files
    public enum LogFileType {
        LOG_FILE, DIAGNOSTIC_FILE, OUT_FILE
    }
    /**
     * Returns the data array
     * @return the log file data
     */
    public Object[][] getData() {
        return data;
    }
    /**
     * Sets the log file data array
     * @param data the log file data array
     */
    public void setData(Object[][] data) {
        this.data = data;
    }
    /**
     * Returns the log file type
     * @return the log file type
     */
    public LogFileType getType() {
        return type;
    }
    /**
     * Sets the log file type
     * @param type the log file type
     */
    public void setType(LogFileType type) {
        this.type = type;
    }
}