package com.mycompany.cti_mfc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    public final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Sample Hash", "Family", "Timestamp", "Entropy", "File Writes", "Net Calls"}, 0);
    public final JTable table = new JTable(tableModel);

    public final JTextField hashField = new JTextField(9);
    public final JTextField familyField = new JTextField(9);
    public final JTextField entropyField = new JTextField("7.4", 4);
    public final JTextField writesField = new JTextField("310", 4);
    public final JTextField netField = new JTextField("12", 4);
    public final JTextField searchField = new JTextField(10);
    public final JTextArea resultArea = new JTextArea(5, 55);

    public final JButton addBtn = new JButton("Create");
    public final JButton updateBtn = new JButton("Update");
    public final JButton deleteBtn = new JButton("Delete");
    public final JButton predictBtn = new JButton("Classify Trace");
    public final JButton searchBtn = new JButton("Search");
    public final JButton resetBtn = new JButton("Show All");
    public final JButton evalBtn = new JButton("Run CTI Evaluation");

    public DashboardPanel() {
        setLayout(new BorderLayout(8, 8));

        // Top Form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Malware Sample CRUD Form"));
        form.add(new JLabel("Hash:")); form.add(hashField);
        form.add(new JLabel("Family:")); form.add(familyField);
        form.add(new JLabel("Entropy:")); form.add(entropyField);
        form.add(new JLabel("Writes:")); form.add(writesField);
        form.add(new JLabel("Net:")); form.add(netField);
        form.add(addBtn); form.add(updateBtn); form.add(deleteBtn); form.add(predictBtn);

        // Center Table & Search
        JPanel center = new JPanel(new BorderLayout());
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(new JLabel("Filter:")); searchBar.add(searchField);
        searchBar.add(searchBtn); searchBar.add(resetBtn); searchBar.add(evalBtn);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Console
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane console = new JScrollPane(resultArea);
        console.setBorder(BorderFactory.createTitledBorder("Static vs Behavioral Intelligence Metrics"));

        add(form, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(console, BorderLayout.SOUTH);
    }

    public void updateTable(List<MalwareRecord> records) {
        tableModel.setRowCount(0);
        for (MalwareRecord r : records) {
            tableModel.addRow(new Object[]{
                r.getId(), r.getSampleHash(), r.getFamilyName(), r.getTimestamp(),
                String.format("%.2f", r.getEntropy()), r.getFileWrites(), r.getNetworkCalls()
            });
        }
    }
}