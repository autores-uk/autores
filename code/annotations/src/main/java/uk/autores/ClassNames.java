package uk.autores;

import uk.autores.handling.Namer;

final class ClassNames {

    private static final Namer NAMER = new Namer();

    private ClassNames() {}

    static String generateClassName(Iterable<?> source) {
        StringBuilder buf = new StringBuilder();
        long hash = 0L;
        for (Object o : source) {
            String s = o.toString();
            hash = 31 * hash + s.hashCode();
            String simple = NAMER.simplifyResourceName(s);
            if (buf.length() < 20) {
                buf.append(simple);
            }
        }
        String name = String.format("AutoRes$%s$%x", buf, hash);
        return NAMER.nameType(name);
    }
}