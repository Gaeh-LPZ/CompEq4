package com.presentacion.primerosSiguientes;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Ventana para mostrar los conjuntos de PRIMEROS y SIGUIENTES en pestañas. */
public class ResultadosPrimerosSiguientesFrame extends JFrame {

    public ResultadosPrimerosSiguientesFrame(Object[][] datosPrimeros,
                                             Object[][] datosSiguientes) {
        super("Resultados: PRIMEROS y SIGUIENTES");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Tabla PRIMEROS
        JTable tblFirst = makeTable(datosPrimeros, new String[]{"No Terminal", "PRIMEROS"});
        JScrollPane spFirst = new JScrollPane(tblFirst);
        tabs.addTab("PRIMEROS", spFirst);

        // Tabla SIGUIENTES
        JTable tblFollow = makeTable(datosSiguientes, new String[]{"No Terminal", "SIGUIENTES"});
        JScrollPane spFollow = new JScrollPane(tblFollow);
        tabs.addTab("SIGUIENTES", spFollow);

        add(tabs, BorderLayout.CENTER);
    }

    private JTable makeTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        return table;
    }
}
