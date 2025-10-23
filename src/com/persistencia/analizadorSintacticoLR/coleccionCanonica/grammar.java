package com.persistencia.analizadorSintacticoLR.coleccionCanonica;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class grammar {
    public static final String EPS = "ε";
    private static final Set<String> EPS_FORMS = new HashSet<>(Arrays.asList("ε","eps","epsilon"));

    public final List<String> N;                 
    public final List<String> T;                 
    public final String startPrime;              
    public final Map<String, List<production>> byLeft; 
    public final List<production> all;        

    public grammar(List<String> n, List<String> t, String sp,
                   Map<String, List<production>> map, List<production> all) {
        this.N = n; this.T = t; this.startPrime = sp; this.byLeft = map; this.all = all;
    }

    public static String readWholeFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public static grammar parseAugmentedGrammar(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String raw : Files.readAllLines(Paths.get(path))) {
            String ln = raw.trim();
            if (!ln.isEmpty() && !ln.startsWith("#")) lines.add(ln);
        }
        if (lines.size() < 4) {
            throw new IllegalArgumentException("Archivo inválido: se esperan N, T y producciones (incluye S'→S$).");
        }

        List<String> N = new ArrayList<>(Arrays.asList(lines.get(0).split("\\s+")));
        List<String> T = new ArrayList<>(Arrays.asList(lines.get(1).split("\\s+")));

        List<String> vocab = new ArrayList<>();
        vocab.addAll(N); vocab.addAll(T);
        vocab.sort((a,b) -> Integer.compare(b.length(), a.length()));

        Map<String, List<production>> byLeft = new LinkedHashMap<>();
        for (String A : N) byLeft.put(A, new ArrayList<>());
        List<production> all = new ArrayList<>();
        String startPrime = null;

        for (int i = 2; i < lines.size(); i++) {
            String ln = lines.get(i);
            String left, right;
            if (ln.contains("->")) { String[] sp = ln.split("->",2); left=sp[0].trim(); right=sp[1].trim(); }
            else if (ln.contains("→")) { String[] sp = ln.split("→",2); left=sp[0].trim(); right=sp[1].trim(); }
            else throw new IllegalArgumentException("Producción inválida: " + ln);

            if (!byLeft.containsKey(left)) { byLeft.put(left, new ArrayList<>()); if (!N.contains(left)) N.add(0, left); }

            for (String alt : right.split("\\|")) {
                List<String> rhs = tokenize(alt.trim(), vocab);
                production p = new production(left, rhs);
                byLeft.get(left).add(p);
                all.add(p);
                if (left.endsWith("'")) startPrime = left;
            }
        }

        if (startPrime == null)
            throw new IllegalArgumentException("No se encontró producción inicial aumentada (por ej. S'→S$).");

        return new grammar(N, T, startPrime, byLeft, all);
    }

    private static String norm(String s) { return EPS_FORMS.contains(s) ? EPS : s; }

    private static List<String> tokenize(String rhs, List<String> vocab) {
        if (rhs.isEmpty()) return Collections.singletonList(EPS);
        if (rhs.contains(" ")) {
            String[] parts = rhs.split("\\s+");
            List<String> toks = new ArrayList<>();
            for (String p : parts) toks.add(norm(p));
            return toks;
        }
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < rhs.length()) {
            boolean matched = false;
            for (String sym : vocab) {
                if (i + sym.length() <= rhs.length() && rhs.startsWith(sym, i)) {
                    out.add(sym); i += sym.length(); matched = true; break;
                }
            }
            if (!matched) { out.add(String.valueOf(rhs.charAt(i))); i++; }
        }
        if (out.isEmpty()) out.add(EPS);
        return out;
    }
}
