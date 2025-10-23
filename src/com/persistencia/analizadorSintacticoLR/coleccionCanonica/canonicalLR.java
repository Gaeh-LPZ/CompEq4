package com.persistencia.analizadorSintacticoLR.coleccionCanonica;

import java.util.*;

public class canonicalLR {
    // ---------- cerradura ----------
    public static Set<itemLR0> closure(grammar g, Set<itemLR0> I) {
        Set<itemLR0> C = new LinkedHashSet<>(I);
        boolean changed = true;
        while (changed) {
            changed = false;
            List<itemLR0> snapshot = new ArrayList<>(C);
            for (itemLR0 it : snapshot) {
                String X = it.symbolAfterDot();
                if (X != null && g.N.contains(X)) {
                    for (production p : g.byLeft.getOrDefault(X, Collections.emptyList())) {
                        itemLR0 cand = new itemLR0(p, 0);
                        if (!C.contains(cand)) { C.add(cand); changed = true; }
                    }
                }
            }
        }
        return C;
    }

    // ---------- ir_a ----------
    public static Set<itemLR0> goTo(grammar g, Set<itemLR0> I, String X) {
        Set<itemLR0> J = new LinkedHashSet<>();
        for (itemLR0 it : I) {
            if (X.equals(it.symbolAfterDot())) J.add(it.advance());
        }
        return closure(g, J);
    }
        public static Set<itemLR0> go(grammar g, Set<itemLR0> I, String X) {
        Set<itemLR0> J = new LinkedHashSet<>();
        for (itemLR0 it : I) {
            if (X.equals(it.symbolAfterDot())) J.add(it.advance());
        }
        return J;
    }
    // ---------- colección canónica----------
    public static List<Set<itemLR0>> canonicalCollection(grammar g) {
        production startProd = g.byLeft.get(g.startPrime).get(0);
        Set<itemLR0> I0 = closure(g, new LinkedHashSet<>(Collections.singleton(new itemLR0(startProd, 0))));

        List<Set<itemLR0>> C = new ArrayList<>();
        C.add(I0);

        boolean changed = true;
        while (changed) {
            changed = false;
            List<Set<itemLR0>> snapshot = new ArrayList<>(C);
            for (Set<itemLR0> I : snapshot) {
                Set<String> symbols = new LinkedHashSet<>();
                for (itemLR0 it : I) {
                    String s = it.symbolAfterDot();
                    if (s != null) symbols.add(s);
                }
                for (String X : symbols) {
                    if ("$".equals(X)) continue; 
                    Set<itemLR0> J = goTo(g, I, X);
                    if (J.isEmpty()) continue;
                    boolean exists = false;
                    for (Set<itemLR0> K : C) if (K.equals(J)) { exists = true; break; }
                    if (!exists) { C.add(J); changed = true; }
                }
            }
        }
        return C;
    }

    private static List<String> orderedSymbols(grammar g, Set<itemLR0> I) {
        LinkedHashSet<String> s = new LinkedHashSet<>();
        for (itemLR0 it : I) {
            String x = it.symbolAfterDot();
            if (x != null) s.add(x);
        }
        List<String> out = new ArrayList<>();
        for (String A : g.N) if (s.contains(A)) out.add(A);
        for (String a : g.T) if (s.contains(a)) out.add(a);
        for (String x : s) if (!out.contains(x)) out.add(x);
        return out;
    }

    private static String itemsAsSet(Set<itemLR0> S) {
        List<String> lines = new ArrayList<>();
        for (itemLR0 it : S) lines.add(it.toString());
        return "{" + String.join("|", lines) + "}";
    }

    public static String formatReport(grammar g, List<Set<itemLR0>> C) {
        StringBuilder out = new StringBuilder();

        production startProd = g.byLeft.get(g.startPrime).get(0);
        itemLR0 startItem = new itemLR0(startProd, 0);

        out.append("cerradura({").append(startItem.toString()).append("})\n");
        out.append("I0=").append(itemsAsSet(C.get(0))).append("\n\n");

        // Mapa de índices
        Map<Set<itemLR0>, Integer> idx = new LinkedHashMap<>();
        for (int i = 0; i < C.size(); i++) idx.put(C.get(i), i);

        // Por cada estado, listar Ir_a ordenado (N luego T)
        for (int i = 0; i < C.size(); i++) {
            Set<itemLR0> I = C.get(i);
            List<String> symbols = orderedSymbols(g, I);

            for (String X : symbols) {
                Set<itemLR0> J = goTo(g, I, X);

                boolean isAccept = "$".equals(X);
                out.append("Ir_a(I").append(i).append(", ").append(X).append(")=");

                if (isAccept) {
                    out.append("Aceptación");
                } else {
                    out.append("cerradura(").append(itemsAsSet(go(g, I, X))).append(")= (").append(itemsAsSet(J)).append(")");
                    Integer j = idx.get(J);
                    if (j == null) {
                        for (int k = 0; k < C.size(); k++) if (C.get(k).equals(J)) { j = k; break; }
                    }
                    if (j != null) out.append(" =I").append(j);
                }
                out.append("\n");
            }
            out.append("\n");
        }
        return out.toString();
    }
}
