package com.persistencia.analizadorSintacticoLR.coleccionCanonica;

import java.util.List;
import java.util.Objects;

public class production {
    public final String left;
    public final List<String> right;

    public production(String left, List<String> right) {
        this.left = left;
        this.right = right;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof production)) return false;
        production p = (production) o;
        return Objects.equals(left, p.left) && Objects.equals(right, p.right);
    }

    @Override public int hashCode() { return Objects.hash(left, right); }

    @Override public String toString() {
        return left + "->" + String.join(" ", right);
    }
}
