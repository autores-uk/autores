package uk.autores.processors;

final class Comparables {

    private Comparables() {}

    static <C extends Comparable<C>> C max(C a, C b) {
        int n = a.compareTo(b);
        return n > 0 ? a : b;
    }
}
