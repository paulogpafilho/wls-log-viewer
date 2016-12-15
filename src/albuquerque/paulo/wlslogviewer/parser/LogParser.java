package albuquerque.paulo.wlslogviewer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import albuquerque.paulo.wlslogviewer.configuration.AppPreferences;
import albuquerque.paulo.wlslogviewer.entity.ParsedLog;

public class LogParser {

    private static Collection<String[]> logLines;

    public static ParsedLog parse(String log, ParsedLog logObj) {

        File logFile = new File(log);

        BufferedReader input;
        logLines = new ArrayList<String[]>();

        try {

            input = new BufferedReader(new FileReader(logFile));

            String line = null;

            StringBuilder sb = new StringBuilder();

            while ((line = input.readLine()) != null) {
                if (isNewLine(line)) {
                    flushToOutput(sb);
                    sb.append(line + "\n");
                } else {
                    sb.append(line + "\n");
                }
            }

            flushToOutput(sb);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[][] ret = new String[logLines.size()][12];

        Iterator<String[]> it = logLines.iterator();

        int i = 0;

        while (it.hasNext()) {
            ret[i] = it.next();
            i++;
        }

        logObj.setData(ret);

        return logObj;
    }

    private static boolean isNewLine(String l) {
        if (l != null && l.startsWith(AppPreferences.getProperty("wlslogviewer.logfile.begin.line"))) {
            return true;
        }
        return false;
    }

    private static void flushToOutput(StringBuilder sb) throws Exception {
        String s = sb.toString();
        sb.setLength(0);
        parseLine(s);
    }

    private static void parseLine(String line) throws Exception {

        if (line == null || line.length() < 4) {
            return;
        }

        String s = line.substring(4, line.length());

        StringTokenizer st = new StringTokenizer(s, "<");

        String[] ln = new String[12];

        int i = 0;

        while (st.hasMoreTokens()) {

            String v = removeArrow(st.nextToken());

            try {
                ln[i] = v;
            } catch (ArrayIndexOutOfBoundsException aio) {
                ln[11] = ln[11] + v;
            }

            i++;
        }
        logLines.add(ln);
    }

    private static String removeArrow(String l) {
        String h = l.trim();
        if (h.endsWith(">>")) {
            return h.substring(0, (h.length() - 2));
        } else if (h.trim().endsWith(">")) {
            return h.substring(0, (h.length() - 1));
        }
        return h;
    }

}
