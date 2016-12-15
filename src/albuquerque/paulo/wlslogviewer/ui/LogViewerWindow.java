package albuquerque.paulo.wlslogviewer.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import albuquerque.paulo.wlslogviewer.entity.ParsedLog;
import albuquerque.paulo.wlslogviewer.parser.Parser;

/**
 * 
 * @author PAPEREIR
 * 
 *         Main Log Viewer Window. Consists of 3 sections: Buttons, Table with
 *         log lines and Details Panel.
 *
 */
public class LogViewerWindow implements TableColumnModelListener, ClipboardOwner {

    /**
     * Parsed Log Object
     */
    private ParsedLog logObj;

    /**
     * Window Frame
     */
    private JFrame frame;
    /**
     * Open Log Window
     */
    private OpenLogWindow openLogWindow;

    /**
     * Buttons Panel
     */
    private JPanel buttonsPanel;
    private JButton btnFilterLog;
    private JButton btnReset;
    private JButton btnOpenLog;
    private JButton btnCopy;

    /**
     * Filters and Table Panel
     */
    private JPanel tablePanel;
    private JPanel panelFilter;
    private JTextField jtxt0, jtxt1, jtxt2, jtxt3, jtxt4, jtxt5, jtxt6, jtxt7, jtxt8, jtxt9, jtxt10, jtxt11;
    private JPanel panelRows;
    private JTable tableLines;

    /**
     * Details Panel
     */
    private JPanel detailPanel;
    private JScrollPane scrollPaneDetails;
    private JTextPane textPaneDetails;

    /**
     * Sorter and Filters
     */
    private TableRowSorter<TableModel> sorter = null;
    private List<RowFilter<Object, Object>> filters = null;

    /**
     * UI Labels
     */
    private final String wndtitle = "WLS Log Viewer";

    // 12 columns
    private static String[] logColumnNames = { "Time Stamp", "Severity", "Subsystem", "Machine", "Server", "Thread ID",
            "User ID", "Transaction", "Diagnostic Ctx", "Raw Time", "Message ID", "Message Text" };
    // 10 columns
    private static String[] diagnosticColumnNames = { "Time Stamp", "Server", "Severity", "Instance", "Component",
            "Thread ID", "User ID", "ECID", "Compl. Attributes", "Message Text" };
    // 5 columns
    // private static String[] outColumnNames = {"Time Stamp", "Severity",
    // "Component", "Message ID", "Message Text"};

    private String logFile;

    /**
     * Run this Windows for UI testing purposes.
     */
    /*
     * public static void main(String[] args) { EventQueue.invokeLater(new
     * Runnable() { public void run() { try { LogViewerWindow main = new
     * LogViewerWindow(); main.frame.setVisible(true); } catch (Exception e) {
     * e.printStackTrace(); } } }); }
     */

    /**
     * Create the application. Private Constructor for Window Builder GUI
     */
    /*
     * private LogViewerWindow() { initialize(); }
     */

    /**
     * Create the application.
     */
    public LogViewerWindow(String log, OpenLogWindow wnd) {
        logFile = log;
        openLogWindow = wnd;

        openFile(logFile);

        initialize();

        frame.setTitle(wndtitle + ": " + log);
        frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        // Initialize the Frame Window
        frame = new JFrame();
        frame.setBounds(100, 100, 1440, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new MigLayout("", "[1425.00px]", "[41px][209.00px][604.00px]"));

        // Initialize the Buttons Panel and Buttons
        buttonsPanel = new JPanel();
        buttonsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.getContentPane().add(buttonsPanel, "cell 0 0,grow");

        btnReset = new JButton("Reset Filter");
        btnReset.addActionListener(resetTable);

        btnOpenLog = new JButton("Open File");
        btnOpenLog.addActionListener(openFile);
        buttonsPanel.setLayout(new MigLayout("", "[352px][352px][352px][352px]", "[36px]"));
        buttonsPanel.add(btnOpenLog, "cell 0 0,grow");

        btnFilterLog = new JButton("Filter Column");
        btnFilterLog.addActionListener(filterFields);
        buttonsPanel.add(btnFilterLog, "cell 1 0,grow");
        buttonsPanel.add(btnReset, "cell 2 0,grow");

        btnCopy = new JButton("Copy to Clipboard");
        btnCopy.addActionListener(copyToClipboard);
        buttonsPanel.add(btnCopy, "cell 3 0,grow");

        // Initialize the Table Panel, Filters and Table
        tablePanel = new JPanel();
        tablePanel.setAlignmentY(Component.TOP_ALIGNMENT);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.getContentPane().add(tablePanel, "cell 0 1,grow");
        tablePanel.setLayout(new MigLayout("", "[1415.00px]", "[22.00px][174.00px]"));

        panelFilter = new JPanel();
        panelFilter.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        panelFilter.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        panelFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.add(panelFilter, "cell 0 0,grow");
        panelFilter.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        if (logObj.getType().equals(ParsedLog.LogFileType.LOG_FILE)) {
            initGUIForLogFile();
            DefaultTableModel mdl = new DefaultTableModel(logObj.getData(), logColumnNames);
            tableLines.setModel(mdl);
            sorter = new TableRowSorter<TableModel>(tableLines.getModel());
            tableLines.setRowSorter(sorter);
        }

        if (logObj.getType().equals(ParsedLog.LogFileType.DIAGNOSTIC_FILE)) {
            initGUIForDiagnosticFile();
            DefaultTableModel mdl = new DefaultTableModel(logObj.getData(), diagnosticColumnNames);
            tableLines.setModel(mdl);
            sorter = new TableRowSorter<TableModel>(tableLines.getModel());
            tableLines.setRowSorter(sorter);
        }
    }

    private void initGUIForDiagnosticFile() {

        jtxt0 = new JTextField();
        jtxt0.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        jtxt0.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelFilter.add(jtxt0);

        jtxt1 = new JTextField();
        panelFilter.add(jtxt1);

        jtxt2 = new JTextField();
        panelFilter.add(jtxt2);

        jtxt3 = new JTextField();
        panelFilter.add(jtxt3);

        jtxt4 = new JTextField();
        panelFilter.add(jtxt4);

        jtxt5 = new JTextField();
        panelFilter.add(jtxt5);

        jtxt6 = new JTextField();
        panelFilter.add(jtxt6);

        jtxt7 = new JTextField();
        panelFilter.add(jtxt7);

        jtxt8 = new JTextField();
        panelFilter.add(jtxt8);

        jtxt9 = new JTextField();
        panelFilter.add(jtxt9);

        panelRows = new JPanel();
        tablePanel.add(panelRows, "cell 0 1,grow");
        panelRows.setLayout(new GridLayout(0, 1, 0, 0));

        JScrollPane scrollPaneTable = new JScrollPane();
        panelRows.add(scrollPaneTable);

        Object[][] data = { { "", "", "", "", "", "", "", "", "", "" } };

        tableLines = new JTable(data, diagnosticColumnNames);

        sorter = new TableRowSorter<TableModel>(tableLines.getModel());
        filters = new ArrayList<RowFilter<Object, Object>>();
        tableLines.setRowSorter(sorter);

        tableLines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SelectionListener listener = new SelectionListener();

        tableLines.getSelectionModel().addListSelectionListener(listener);
        tableLines.getColumnModel().getSelectionModel().addListSelectionListener(listener);

        tableLines.getColumnModel().addColumnModelListener(this);

        scrollPaneTable.setViewportView(tableLines);

        sorter = new TableRowSorter<TableModel>(tableLines.getModel());
        tableLines.setRowSorter(sorter);

        tableLines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableLines.getColumnModel().addColumnModelListener(this);

        // Initializes the Details Panel and Details TextPane
        detailPanel = new JPanel();
        detailPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        detailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.getContentPane().add(detailPanel, "cell 0 2,grow");
        detailPanel.setLayout(new MigLayout("", "[1410px]", "[599px]"));

        scrollPaneDetails = new JScrollPane();
        detailPanel.add(scrollPaneDetails, "cell 0 0,grow");

        textPaneDetails = new JTextPane();
        scrollPaneDetails.setViewportView(textPaneDetails);

        columnMarginChanged(new ChangeEvent(tableLines.getColumnModel()));

        frame.setTitle("WLS Log Viewer");

    }

    private void initGUIForLogFile() {

        jtxt0 = new JTextField();
        jtxt0.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        jtxt0.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelFilter.add(jtxt0);

        jtxt1 = new JTextField();
        panelFilter.add(jtxt1);

        jtxt2 = new JTextField();
        panelFilter.add(jtxt2);

        jtxt3 = new JTextField();
        panelFilter.add(jtxt3);

        jtxt4 = new JTextField();
        panelFilter.add(jtxt4);

        jtxt5 = new JTextField();
        panelFilter.add(jtxt5);

        jtxt6 = new JTextField();
        panelFilter.add(jtxt6);

        jtxt7 = new JTextField();
        panelFilter.add(jtxt7);

        jtxt8 = new JTextField();
        panelFilter.add(jtxt8);

        jtxt9 = new JTextField();
        panelFilter.add(jtxt9);

        jtxt10 = new JTextField();
        panelFilter.add(jtxt10);

        jtxt11 = new JTextField();
        panelFilter.add(jtxt11);

        panelRows = new JPanel();
        tablePanel.add(panelRows, "cell 0 1,grow");
        panelRows.setLayout(new GridLayout(0, 1, 0, 0));

        JScrollPane scrollPaneTable = new JScrollPane();
        panelRows.add(scrollPaneTable);

        Object[][] data = { { "", "", "", "", "", "", "", "", "", "", "", "" } };

        tableLines = new JTable(data, logColumnNames);

        sorter = new TableRowSorter<TableModel>(tableLines.getModel());
        filters = new ArrayList<RowFilter<Object, Object>>();
        tableLines.setRowSorter(sorter);

        tableLines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SelectionListener listener = new SelectionListener();

        tableLines.getSelectionModel().addListSelectionListener(listener);
        tableLines.getColumnModel().getSelectionModel().addListSelectionListener(listener);

        tableLines.getColumnModel().addColumnModelListener(this);

        scrollPaneTable.setViewportView(tableLines);

        sorter = new TableRowSorter<TableModel>(tableLines.getModel());
        tableLines.setRowSorter(sorter);

        tableLines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableLines.getColumnModel().addColumnModelListener(this);

        // Initializes the Details Panel and Details TextPane
        detailPanel = new JPanel();
        detailPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        detailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.getContentPane().add(detailPanel, "cell 0 2,grow");
        detailPanel.setLayout(new MigLayout("", "[1410px]", "[599px]"));

        scrollPaneDetails = new JScrollPane();
        detailPanel.add(scrollPaneDetails, "cell 0 0,grow");

        textPaneDetails = new JTextPane();
        scrollPaneDetails.setViewportView(textPaneDetails);

        columnMarginChanged(new ChangeEvent(tableLines.getColumnModel()));

        frame.setTitle("WLS Log Viewer");
    }

    /**
     * Copy the contents of details TextPane to the System Clipboard
     */
    public void setClipboardContents() {

        String fullLineText = "";

        int modelRow = tableLines.convertRowIndexToModel(tableLines.getSelectedRow());

        if (logObj.getType().equals(ParsedLog.LogFileType.LOG_FILE)) {
            fullLineText = "####";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 0) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 1) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 2) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 3) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 4) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 5) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 6) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 7) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 8) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 9) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 10) + "> ";
            fullLineText += "<" + (String) tableLines.getModel().getValueAt(modelRow, 11) + "> ";
        }

        if (logObj.getType().equals(ParsedLog.LogFileType.DIAGNOSTIC_FILE)) {
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 0) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 1) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 2) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 3) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 4) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 5) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 6) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 7) + "] ";
            fullLineText += "[" + (String) tableLines.getModel().getValueAt(modelRow, 8) + "] ";
            fullLineText += (String) tableLines.getModel().getValueAt(modelRow, 9);
            ;
        }

        StringSelection stringSelection = new StringSelection(fullLineText);

        // StringSelection stringSelection = new
        // StringSelection(textPaneDetails.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    /**
     * Sorts the table by column with Regex AND Filter
     */
    private void sortTable() {

        filters.clear();

        if (jtxt0 != null && jtxt0.getText() != null && !jtxt0.getText().equals("")) {
            String text = jtxt0.getText();
            filters.add(RowFilter.regexFilter(text, 0));
        }
        if (jtxt1 != null && jtxt1.getText() != null && !jtxt1.getText().equals("")) {
            String text = jtxt1.getText();
            filters.add(RowFilter.regexFilter(text, 1));
        }
        if (jtxt2 != null && jtxt2.getText() != null && !jtxt2.getText().equals("")) {
            String text = jtxt2.getText();
            filters.add(RowFilter.regexFilter(text, 2));
        }
        if (jtxt3 != null && jtxt3.getText() != null && !jtxt3.getText().equals("")) {
            String text = jtxt3.getText();
            filters.add(RowFilter.regexFilter(text, 3));
        }
        if (jtxt4 != null && jtxt4.getText() != null && !jtxt4.getText().equals("")) {
            String text = jtxt4.getText();
            filters.add(RowFilter.regexFilter(text, 4));
        }
        if (jtxt5 != null && jtxt5.getText() != null && !jtxt5.getText().equals("")) {
            String text = jtxt5.getText();
            filters.add(RowFilter.regexFilter(text, 5));
        }
        if (jtxt6 != null && jtxt6.getText() != null && !jtxt6.getText().equals("")) {
            String text = jtxt6.getText();
            filters.add(RowFilter.regexFilter(text, 6));
        }
        if (jtxt7 != null && jtxt7.getText() != null && !jtxt7.getText().equals("")) {
            String text = jtxt7.getText();
            filters.add(RowFilter.regexFilter(text, 7));
        }
        if (jtxt8 != null && jtxt8.getText() != null && !jtxt8.getText().equals("")) {
            String text = jtxt8.getText();
            filters.add(RowFilter.regexFilter(text, 8));
        }
        if (jtxt9 != null && jtxt9.getText() != null && !jtxt9.getText().equals("")) {
            String text = jtxt9.getText();
            filters.add(RowFilter.regexFilter(text, 9));
        }
        if (jtxt10 != null && jtxt10.getText() != null && !jtxt10.getText().equals("")) {
            String text = jtxt10.getText();
            filters.add(RowFilter.regexFilter(text, 10));
        }
        if (jtxt11 != null && jtxt11.getText() != null && !jtxt11.getText().equals("")) {
            String text = jtxt11.getText();
            filters.add(RowFilter.regexFilter(text, 11));
        }

        if (logObj.getType().equals(ParsedLog.LogFileType.LOG_FILE)) {
            if (jtxt0.getText().equals("") && jtxt1.getText().equals("") && jtxt2.getText().equals("")
                    && jtxt3.getText().equals("") && jtxt4.getText().equals("") && jtxt5.getText().equals("")
                    && jtxt6.getText().equals("") && jtxt7.getText().equals("") && jtxt8.getText().equals("")
                    && jtxt9.getText().equals("") && jtxt10.getText().equals("") && jtxt11.getText().equals("")) {

                sorter.setRowFilter(RowFilter.regexFilter(""));
            }
        }
        if (logObj.getType().equals(ParsedLog.LogFileType.DIAGNOSTIC_FILE)) {
            if (jtxt0.getText().equals("") && jtxt1.getText().equals("") && jtxt2.getText().equals("")
                    && jtxt3.getText().equals("") && jtxt4.getText().equals("") && jtxt5.getText().equals("")
                    && jtxt6.getText().equals("") && jtxt7.getText().equals("") && jtxt8.getText().equals("")
                    && jtxt9.getText().equals("")) {

                sorter.setRowFilter(RowFilter.regexFilter(""));
            }
        }

        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    /**
     * Clears the Filter fields and clears the Filters
     */
    private void clearFilter() {
        if (jtxt1 != null) {
            jtxt1.setText("");
        }
        if (jtxt2 != null) {
            jtxt2.setText("");
        }
        if (jtxt3 != null) {
            jtxt3.setText("");
        }
        if (jtxt4 != null) {
            jtxt4.setText("");
        }
        if (jtxt5 != null) {
            jtxt5.setText("");
        }
        if (jtxt6 != null) {
            jtxt6.setText("");
        }
        if (jtxt7 != null) {
            jtxt7.setText("");
        }
        if (jtxt8 != null) {
            jtxt8.setText("");
        }
        if (jtxt9 != null) {
            jtxt9.setText("");
        }
        if (jtxt10 != null) {
            jtxt10.setText("");
        }
        if (jtxt11 != null) {
            jtxt11.setText("");
        }
        if (sorter != null) {
            sorter.setRowFilter(RowFilter.regexFilter(""));
        }
        if (filters != null) {
            filters.clear();
        }
    }

    /**
     * Open the log file and sets the new TableModel
     * 
     * @param log
     *            The log file to open
     */
    public void openFile(String log) {

        clearFilter();

        logObj = Parser.getTableModel(log);
    }

    /**
     * Opens the File Chooser Window
     */
    private void openFileChooser() {
        openLogWindow.setMain(null);
        openLogWindow.setVisible(true);
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * Action Listeners for the UI
     */
    ActionListener filterFields = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            sortTable();
        }
    };

    ActionListener resetTable = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            clearFilter();
        }
    };

    ActionListener openFile = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            openFileChooser();
        }
    };

    ActionListener copyToClipboard = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            setClipboardContents();
        }
    };

    /**
     * Overriden Actions to keep the Filter TextFields equal to Table Column
     * width
     */
    @Override
    public void columnMoved(TableColumnModelEvent e) {
        Component moved = panelFilter.getComponent(e.getFromIndex());
        panelFilter.remove(e.getFromIndex());
        panelFilter.add(moved, e.getToIndex());
        panelFilter.validate();
    }

    @Override
    public void columnMarginChanged(ChangeEvent e) {

        TableColumnModel tcm = tableLines.getColumnModel();
        int columns = tcm.getColumnCount();

        for (int i = 0; i < columns; i++) {
            JTextField textField = (JTextField) panelFilter.getComponent(i);
            Dimension d = textField.getPreferredSize();
            d.width = tcm.getColumn(i).getWidth();
            textField.setPreferredSize(d);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                panelFilter.revalidate();
            }
        });

    }

    @Override
    public void columnAdded(TableColumnModelEvent e) {
    }

    @Override
    public void columnRemoved(TableColumnModelEvent e) {
    }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    /**
     * 
     * @author Paulo Pereira
     * 
     *         SelectionListener for the table row selection and to display only
     *         the line message in the Details Panel
     *
     */
    private class SelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {

            if (e.getValueIsAdjusting())
                return;

            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.isSelectionEmpty()) {
                textPaneDetails.setText("No rows are selected.");
            } else {
                int selectedRow = lsm.getMinSelectionIndex();
                int modelRow = tableLines.convertRowIndexToModel(selectedRow);

                if (logObj.getType().equals(ParsedLog.LogFileType.LOG_FILE)) {
                    textPaneDetails.setText((String) tableLines.getModel().getValueAt(modelRow, 11));
                }

                if (logObj.getType().equals(ParsedLog.LogFileType.DIAGNOSTIC_FILE)) {
                    textPaneDetails.setText((String) tableLines.getModel().getValueAt(modelRow, 9));
                }

            }

            textPaneDetails.setCaretPosition(0);
        }
    }
}
