package albuquerque.paulo.wlslogviewer.entity;

public class ParsedLog {
    private Object[][] data;
    private LogFileType type;

    public enum LogFileType {
        LOG_FILE, DIAGNOSTIC_FILE, OUT_FILE
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    public LogFileType getType() {
        return type;
    }

    public void setType(LogFileType type) {
        this.type = type;
    }
}