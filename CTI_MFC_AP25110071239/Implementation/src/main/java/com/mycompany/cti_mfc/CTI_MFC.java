package com.mycompany.cti_mfc;

import javax.swing.*;
import java.util.List;

public class CTI_MFC extends JFrame {
    private final MalwareService service = new MalwareService();
    private final DashboardPanel ui = new DashboardPanel();
    private final MalwareClassifier.StaticClassifier staticModel = new MalwareClassifier.StaticClassifier();
    private final MalwareClassifier.BehavioralClassifier behavioralModel = new MalwareClassifier.BehavioralClassifier();

    public CTI_MFC() {
        setTitle("CTI Project 06: Malware Family Classification Using Behavioral Intelligence");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(ui);

        // Wire Button Events
        ui.addBtn.addActionListener(e -> onCreate());
        ui.updateBtn.addActionListener(e -> onUpdate());
        ui.deleteBtn.addActionListener(e -> onDelete());
        ui.searchBtn.addActionListener(e -> ui.updateTable(service.searchSamples(ui.searchField.getText())));
        ui.resetBtn.addActionListener(e -> ui.updateTable(service.getAllSamples()));
        ui.predictBtn.addActionListener(e -> onClassify());
        ui.evalBtn.addActionListener(e -> runEvaluationAsync());

        ui.updateTable(service.getAllSamples());
        runEvaluationAsync();
        pack();
        setLocationRelativeTo(null);
    }

    private void onCreate() {
        try {
            MalwareRecord r = new MalwareRecord(0, ui.hashField.getText().trim(), ui.familyField.getText().trim(),
                    20261112, Double.parseDouble(ui.entropyField.getText()),
                    Integer.parseInt(ui.writesField.getText()), Integer.parseInt(ui.netField.getText()));
            service.addSample(r);
            ui.updateTable(service.getAllSamples());
            JOptionPane.showMessageDialog(this, "Record Created!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int row = ui.table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first!"); return; }
        try {
            int id = (int) ui.tableModel.getValueAt(row, 0);
            String fam = JOptionPane.showInputDialog(this, "New Malware Family:");
            if (fam != null) {
                service.updateSampleFamily(id, fam);
                ui.updateTable(service.getAllSamples());
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void onDelete() {
        int row = ui.table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first!"); return; }
        try {
            int id = (int) ui.tableModel.getValueAt(row, 0);
            service.removeSample(id);
            ui.updateTable(service.getAllSamples());
            JOptionPane.showMessageDialog(this, "Record Deleted!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    private void onClassify() {
        try {
            MalwareRecord sample = new MalwareRecord(0, "LIVE", "?", 20261112,
                    Double.parseDouble(ui.entropyField.getText()),
                    Integer.parseInt(ui.writesField.getText()), Integer.parseInt(ui.netField.getText()));
            String pred = behavioralModel.predict(sample);
            ui.familyField.setText(pred);
            JOptionPane.showMessageDialog(this, "Predicted Family: " + pred);
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid numbers!"); }
    }

    // Multithreading via SwingWorker
    private void runEvaluationAsync() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                List<MalwareRecord> data = service.getAllSamples();
                int split = (int) (data.size() * 0.75);
                List<MalwareRecord> train = data.subList(0, split);
                List<MalwareRecord> test = data.subList(split, data.size());

                staticModel.train(train);
                behavioralModel.train(train);
                return service.evaluateModel(staticModel, test) + "\n" + service.evaluateModel(behavioralModel, test);
            }
            @Override
            protected void done() {
                try { ui.resultArea.setText(get()); } catch (Exception ignored) {}
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CTI_MFC().setVisible(true));
    }
}