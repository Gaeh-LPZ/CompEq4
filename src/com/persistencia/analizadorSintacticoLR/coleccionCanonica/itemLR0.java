package com.persistencia.analizadorSintacticoLR.coleccionCanonica;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class itemLR0 {
    public final production p;
    public final int dot;

    public itemLR0(production p, int dot) { this.p = p; this.dot = dot; }

    public String symbolAfterDot() {
        return dot < p.right.size() ? p.right.get(dot) : null;
    }

    public itemLR0 advance() { return new itemLR0(p, dot + 1); }

    @Override public boolean equals(Object o) {
        if (!(o instanceof itemLR0)) return false;
        itemLR0 x = (itemLR0) o;
        return dot == x.dot && p.equals(x.p);
    }

    @Override public int hashCode() { return Objects.hash(p, dot); }

    @Override public String toString() {
        List<String> out = new ArrayList<>(p.right);
        out.add(dot, "•");
        return "[" + p.left + "→" + String.join("", out) + "]";
    }
}
