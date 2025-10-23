package com.presentacion.tablaAnalisisSintacticoLR;

import com.persistencia.analizadorSintacticoLR.tablaLR.lr0Table;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResultadosTablaLRFrame extends JFrame {
    private final JTable table = new JTable();

    public ResultadosTablaLRFrame(lr0Table.Result r) {
        super("Tabla de Análisis Sintáctico LR(0)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationByPlatform(true);

        table.setModel(toMergedModel(r));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(50); // Edo
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(70);
        }

        table.getTableHeader().setReorderingAllowed(false);

        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private DefaultTableModel toMergedModel(lr0Table.Result r) {
        int tCount = r.terminals.size();
        int nCount = r.nonTerminals.size();

        String[] cols = new String[1 + tCount + nCount];
        cols[0] = "Edo";
        int idx = 1;
        for (String t : r.terminals) cols[idx++] = t;
        for (String nt : r.nonTerminals) cols[idx++] = nt;

        Object[][] data = new Object[r.states][cols.length];

        for (int i = 0; i < r.states; i++) {
            data[i][0] = i;

            Map<String, String> rowAction = r.action.getOrDefault(i, Map.of());
            for (int j = 0; j < tCount; j++) {
                String a = r.terminals.get(j);
                String v = rowAction.get(a);
                data[i][1 + j] = v == null ? "" : v;
            }

            Map<String, Integer> rowGoto = r.gotoTable.getOrDefault(i, Map.of());
            for (int j = 0; j < nCount; j++) {
                String A = r.nonTerminals.get(j);
                Integer v = rowGoto.get(A);
                data[i][1 + tCount + j] = v == null ? "" : v;
            }
        }

        return new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }
}
