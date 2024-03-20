// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.naming.Namer;

final class ClassNames {

    private ClassNames() {}

    static String generateClassName(Iterable<?> source) {
        Namer namer = new Namer();
        StringBuilder buf = new StringBuilder();
        long hash = 0L;
        for (Object o : source) {
            String s = o.toString();
            hash = 31 * hash + s.hashCode();
            String simple = namer.simplifyResourceName(s);
            if (buf.length() < 20) {
                buf.append(simple);
            }
        }
        String name = String.format("AutoRes$%s$%x", buf, hash);
        return namer.nameType(name);
    }
}
