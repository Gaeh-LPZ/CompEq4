package com.persistencia.analizadorSintacticoLR.primerosYSiguientes;

import java.util.*;

public final class FirstFollow {

    private FirstFollow() {}

    public static Map<String, Set<String>> computeFirst(grammar g) {
        Map<String, Set<String>> first = new LinkedHashMap<>();
        for (String nt : g.nonTerminals) {
            first.put(nt, new LinkedHashSet<>());
        }
        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<List<String>>> e : g.prods.entrySet()) {
                String A = e.getKey();
                for (List<String> alpha : e.getValue()) {
                    if (alpha.size() == 1 && grammar.isEpsilon(alpha.get(0))) {
                        if (first.get(A).add(grammar.EPS)) changed = true;
                        continue;
                    }
                    boolean allNullable = true;
                    for (String X : alpha) {
                        Set<String> toAdd = new LinkedHashSet<>();
                        if (g.terminals.contains(X)) {
                            toAdd.add(X);
                        } else if (grammar.isEpsilon(X)) {
                            toAdd.add(grammar.EPS);
                        } else {
                            toAdd.addAll(first.getOrDefault(X, Collections.emptySet()));
                        }
                        boolean localChanged = false;
                        for (String s : toAdd) {
                            if (!s.equals(grammar.EPS)) {
                                if (first.get(A).add(s)) localChanged = true;
                            }
                        }
                        if (localChanged) changed = true;
                        if (!toAdd.contains(grammar.EPS)) {
                            allNullable = false;
                            break;
                        }
                    }
                    if (allNullable) {
                        if (first.get(A).add(grammar.EPS)) changed = true;
                    }
                }
            }
        } while (changed);
        return first;
    }

    public static Map<String, Set<String>> computeFollow(grammar g, Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new LinkedHashMap<>();
        for (String nt : g.nonTerminals) {
            follow.put(nt, new LinkedHashSet<>());
        }
        if (g.start == null) throw new IllegalStateException("No se identificó símbolo inicial");
        follow.get(g.start).add("$");

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, List<List<String>>> e : g.prods.entrySet()) {
                String A = e.getKey();
                for (List<String> alpha : e.getValue()) {
                    for (int i = 0; i < alpha.size(); i++) {
                        String B = alpha.get(i);
                        if (!g.nonTerminals.contains(B)) continue;

                        List<String> beta = (i + 1 < alpha.size()) ? alpha.subList(i + 1, alpha.size())
                                                                    : Collections.emptyList();
                        if (!beta.isEmpty()) {
                            Set<String> firstBeta = firstOfSequence(beta, g, first);
                            boolean localChanged = false;
                            for (String s : firstBeta) {
                                if (!s.equals(grammar.EPS)) {
                                    if (follow.get(B).add(s)) localChanged = true;
                                }
                            }
                            if (localChanged) changed = true;
                            if (firstBeta.contains(grammar.EPS)) {
                                if (follow.get(B).addAll(follow.get(A))) changed = true;
                            }
                        } else {
                            if (follow.get(B).addAll(follow.get(A))) changed = true;
                        }
                    }
                }
            }
        } while (changed);
        return follow;
    }

    private static Set<String> firstOfSequence(List<String> seq, grammar g, Map<String, Set<String>> first) {
        Set<String> result = new LinkedHashSet<>();
        boolean allNullable = true;
        for (String X : seq) {
            Set<String> fx = new LinkedHashSet<>();
            if (g.terminals.contains(X)) {
                fx.add(X);
            } else if (grammar.isEpsilon(X)) {
                fx.add(grammar.EPS);
            } else {
                fx.addAll(first.getOrDefault(X, Collections.emptySet()));
            }
            for (String s : fx) {
                if (!s.equals(grammar.EPS)) result.add(s);
            }
            if (!fx.contains(grammar.EPS)) {
                allNullable = false;
                break;
            }
        }
        if (allNullable) result.add(grammar.EPS);
        return result;
    }
}

