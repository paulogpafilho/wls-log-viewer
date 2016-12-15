package albuquerque.paulo.wlslogviewer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import albuquerque.paulo.wlslogviewer.configuration.AppPreferences;
import albuquerque.paulo.wlslogviewer.entity.ParsedLog;
import albuquerque.paulo.wlslogviewer.entity.ParsedLog.LogFileType;

public class Parser {

    public static ParsedLog getTableModel(String log) {

        ParsedLog logObj = new ParsedLog();

        logObj.setType(identifyFileType(log));

        if (logObj.getType().equals(ParsedLog.LogFileType.LOG_FILE)) {
            return LogParser.parse(log, logObj);
        }

        if (logObj.getType().equals(ParsedLog.LogFileType.DIAGNOSTIC_FILE)) {
            // Object[][] data = (DiagnosticParser.parse(log,
            // logObj)).getData();
            return DiagnosticParser.parse(log, logObj);
        }

        if (logObj.getType().equals(ParsedLog.LogFileType.OUT_FILE)) {

        }
        return logObj;
    }

    private static LogFileType identifyFileType(String log) {

        File logFile = new File(log);

        BufferedReader input;

        int logFileCount = 0;
        int diagnosticFileCount = 0;
        int outFileCount = 0;

        try {

            input = new BufferedReader(new FileReader(logFile));

            String line = null;

            while ((line = input.readLine()) != null) {

                if (line != null && line.startsWith(AppPreferences.getProperty("wlslogviewer.logfile.begin.line"))) {
                    logFileCount++;
                }
                if (line != null && line.startsWith(AppPreferences.getProperty("wlslogviewer.diagnostic.begin.line"))) {
                    diagnosticFileCount++;
                }
                if (line != null && line.startsWith(AppPreferences.getProperty("wlslogviewer.outfile.begin.line"))) {
                    outFileCount++;
                }
            }
            input.close();

            if (logFileCount > diagnosticFileCount && logFileCount > outFileCount) {
                return ParsedLog.LogFileType.LOG_FILE;
            } else if (diagnosticFileCount > logFileCount && diagnosticFileCount > outFileCount) {
                return ParsedLog.LogFileType.DIAGNOSTIC_FILE;
            } else if (outFileCount > logFileCount && outFileCount > diagnosticFileCount) {
                return ParsedLog.LogFileType.OUT_FILE;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
