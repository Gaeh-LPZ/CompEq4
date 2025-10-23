package com.persistencia.analizadorSintacticoLR.tablaLR;

import com.persistencia.analizadorSintacticoLR.coleccionCanonica.grammar;
import com.persistencia.analizadorSintacticoLR.coleccionCanonica.production;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public final class lrParser {

    private lrParser() {}

    public static List<String> lex(String program, List<String> grammarTerminals) {
        Set<String> T = new LinkedHashSet<>(grammarTerminals);
        T.remove("$");

        List<String> keywords = new ArrayList<>();
        for (String t : T) {
            if (t.matches("[A-Za-z_][A-Za-z_0-9]*") && !t.equals("id")) keywords.add(t);
        }

        Set<String> twoChar = new LinkedHashSet<>();
        if (T.contains(":=")) twoChar.add(":=");

        Set<String> oneChar = new LinkedHashSet<>();
        for (String s : new String[]{"+","*","(",")",";",",",":","="}) {
            if (T.contains(s)) oneChar.add(s);
        }

        List<String> out = new ArrayList<>();
        int i = 0, n = program.length();
        while (i < n) {
            char ch = program.charAt(i);
            if (Character.isWhitespace(ch)) { i++; continue; }

            if (i + 1 < n) {
                String pair = program.substring(i, i+2);
                if (twoChar.contains(pair)) { out.add(pair); i += 2; continue; }
            }

            if (Character.isLetter(ch) || ch == '_') {
                int j = i + 1;
                while (j < n && (Character.isLetterOrDigit(program.charAt(j)) || program.charAt(j) == '_')) j++;
                String lex = program.substring(i, j);
                if (keywords.contains(lex)) out.add(lex);
                else if (T.contains("id")) out.add("id");
                else out.add(lex);
                i = j; continue;
            }
            if (Character.isDigit(ch)) {
                int j = i + 1;
                while (j < n && Character.isDigit(program.charAt(j))) j++;
                if (T.contains("num")) out.add("num");
                else if (T.contains("id")) out.add("id");
                else out.add(program.substring(i, j));
                i = j; continue;
            }

            String s = String.valueOf(ch);
            if (oneChar.contains(s)) { out.add(s); i++; continue; }
            if (T.contains(s))       { out.add(s); i++; continue; }

            i++;
        }

        out.add("$");
        return out;
    }

    /** Ejecuta el análisis LR y devuelve un modelo de tabla (PILA, ENTRADA, ACCIÓN). */
    public static DefaultTableModel runLRParse(String grammarPath, lr0Table.Result table, List<String> tokens) throws Exception {
        grammar gAug = grammar.parseAugmentedGrammar(grammarPath);
        List<production> rules = new ArrayList<>();
        for (String A : gAug.byLeft.keySet()) {
            if (A.endsWith("'")) continue;
            rules.addAll(gAug.byLeft.get(A));
        }

        String[] cols = {"PILA", "ENTRADA", "ACCIÓN"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };

        Deque<Object> stack = new ArrayDeque<>();
        stack.push(0);

        int ptr = 0;
        while (true) {
            int state = (int) stack.peek();
            String a = (ptr < tokens.size()) ? tokens.get(ptr) : "$";

            String act = table.action.getOrDefault(state, Map.of()).get(a);

            String pilaStr = renderStack(stack);
            String entradaStr = String.join(" ", tokens.subList(ptr, tokens.size()));

            if (act == null || act.isBlank()) {
                Set<String> esperados = new LinkedHashSet<>();
                table.action.getOrDefault(state, Map.of()).forEach((t,v)->{
                    if (v != null && !v.isBlank()) esperados.add(t);
                });
                String msg = "Error sintáctico: se esperaba " + (esperados.isEmpty()? "otro token" : String.join(" o ", esperados));
                model.addRow(new Object[]{pilaStr, entradaStr, msg});
                break;
            }

            if (act.startsWith("d")) {
                int j = Integer.parseInt(act.substring(1));
                model.addRow(new Object[]{pilaStr, entradaStr, "d" + j});
                stack.push(a);
                stack.push(j);
                ptr++;
                continue;
            }

            if (act.startsWith("r")) {
                int k = Integer.parseInt(act.substring(1));
                production p = rules.get(k - 1);

                int betaLen = effectiveBetaLength(p.right);
                for (int i=0;i<betaLen;i++) { stack.pop(); stack.pop(); }

                int j = (int) stack.peek();
                stack.push(p.left); 
                Integer s = table.gotoTable.getOrDefault(j, Map.of()).get(p.left);
                if (s == null)
                    throw new IllegalStateException("Ir_a["+j+","+p.left+"] no definido en reducción r"+k);
                stack.push(s);

                model.addRow(new Object[]{
                        pilaStr,
                        entradaStr,
                        "r"+k+"  "+p.left+"→"+String.join(" ", p.right)
                });
                continue;
            }

            if (act.equalsIgnoreCase("acep") || act.equalsIgnoreCase("aceptar")) {
                model.addRow(new Object[]{pilaStr, entradaStr, "Aceptar"});
                break;
            }

            model.addRow(new Object[]{pilaStr, entradaStr, act});
            break;
        }

        return model;
    }

    private static int effectiveBetaLength(List<String> right) {
        if (right == null || right.isEmpty()) return 0;
        if (right.size() == 1) {
            String s = right.get(0);
            if (s != null) {
                String t = s.trim();
                if (t.equals("ε") || t.equalsIgnoreCase("epsilon")) return 0;
            }
        }
        return right.size();
    }

    private static String renderStack(Deque<Object> stack) {
        List<Object> list = new ArrayList<>(stack);
        Collections.reverse(list);
        StringBuilder sb = new StringBuilder();
        for (Object o : list) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(o);
        }
        return sb.toString();
    }
}
