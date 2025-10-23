package com.presentacion.analizadorSintacticoLR;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ResultadoAnalisisLRFrame extends JFrame {

    public ResultadoAnalisisLRFrame(DefaultTableModel model) {
        super("Corrida del Analizador Sintáctico LR");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (comp instanceof JTextArea ta) {
                    ta.setSize(getColumnModel().getColumn(column).getWidth(), Integer.MAX_VALUE);
                    int h = ta.getPreferredSize().height;
                    if (getRowHeight(row) != h) setRowHeight(row, h);
                }
                return comp;
            }
        };

        TableCellRenderer wrapRenderer = new TextAreaWrapRenderer(table);
        for (int c = 0; c < table.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(wrapRenderer);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(980, 560));
        getContentPane().add(sp, BorderLayout.CENTER);

        pack();
        if (getWidth() < 900 || getHeight() < 500) {
            setSize(Math.max(900, getWidth()), Math.max(500, getHeight()));
        }
    }

    static class TextAreaWrapRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaWrapRenderer(JTable table) {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(table.getFont());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setSize(table.getColumnModel().getColumn(column).getWidth(), Integer.MAX_VALUE);
            int preferred = getPreferredSize().height;
            if (table.getRowHeight(row) != preferred) {
                table.setRowHeight(row, preferred);
            }
            return this;
        }
    }
}
