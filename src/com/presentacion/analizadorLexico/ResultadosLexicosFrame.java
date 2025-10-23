package com.presentacion.analizadorLexico;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResultadosLexicosFrame extends JFrame {
    private JTable tblTokens, tblSimbolos, tblErrores;

    public ResultadosLexicosFrame(
            Object[][] tokens, 
            Object[][] simbolos, 
            Object[][] errores) {

        super("Resultados del Análisis Léxico");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        // --- Tira de Tokens ---
        String[] colTokens = {"#línea", "Lexema", "Token"};
        tblTokens = new JTable(new DefaultTableModel(tokens, colTokens));
        tabs.addTab("Tira de Tokens", new JScrollPane(tblTokens));

        // --- Tabla de Símbolos ---
        String[] colSimbolos = {"#Id", "Nombre del identificador"};
        tblSimbolos = new JTable(new DefaultTableModel(simbolos, colSimbolos));
        tabs.addTab("Tabla de Símbolos", new JScrollPane(tblSimbolos));

        // --- Errores ---
        String[] colErrores = {"#línea", "Descripción del error"};
        tblErrores = new JTable(new DefaultTableModel(errores, colErrores));
        tabs.addTab("Errores Léxicos", new JScrollPane(tblErrores));

        getContentPane().add(tabs, BorderLayout.CENTER);
    }
}
