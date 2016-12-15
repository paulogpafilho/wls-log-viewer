package albuquerque.paulo.wlslogviewer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import albuquerque.paulo.wlslogviewer.configuration.AppPreferences;
import albuquerque.paulo.wlslogviewer.entity.ParsedLog;

public class DiagnosticParser {

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

        String[][] ret = new String[logLines.size()][10];

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
        if (l != null && l.startsWith(AppPreferences.getProperty("wlslogviewer.diagnostic.begin.line"))) {
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

        if (line == null || line.length() < 2) {
            return;
        }

        String[] ln = new String[10];

        String wk = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

        ln[0] = wk;

        line = line.substring(line.indexOf(']') + 1, line.length());

        ln[1] = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

        line = line.substring(line.indexOf(']') + 1, line.length());

        ln[2] = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

        line = line.substring(line.indexOf(']') + 1, line.length());

        ln[3] = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

        line = line.substring(line.indexOf(']') + 1, line.length());

        ln[4] = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

        line = line.substring(line.indexOf(']') + 1, line.length());

        ln[5] = line.substring(line.indexOf("[tid:") + 1, line.indexOf("] ["));

        line = line.substring(line.indexOf("] [") + 1, line.length());

        ln[6] = line.substring(line.indexOf("[userId:") + 1, line.indexOf("] ["));

        line = line.substring(line.indexOf("] [") + 1, line.length());

        ln[7] = line.substring(line.indexOf("[ecid:") + 1, line.indexOf(']'));

        line = line.substring(line.indexOf(']') + 1, line.length());

        if (line.indexOf("[APP:") < 0) {
            ln[8] = "";
            ln[9] = line;
        } else {
            ln[8] = line.substring(line.indexOf("[APP:") + 1, line.indexOf(']'));
            ln[9] = line.substring(line.indexOf(']') + 1, line.length());
        }

        logLines.add(ln);
    }

    /*
     * private static String removeArrow(String l){ String h = l.trim();
     * if(h.endsWith("]]")){ return h.substring(0, (h.length()-2)); }else
     * if(h.trim().endsWith("]")) { return h.substring(0, (h.length()-1)); }
     * return h; }
     */

}
