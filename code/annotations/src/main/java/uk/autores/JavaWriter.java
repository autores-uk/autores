// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import uk.autores.cfg.Visibility;
import uk.autores.handling.Context;
import uk.autores.handling.Pkg;

import java.io.IOException;
import java.io.Writer;

/**
 * Convenience type for writing Java files.
 */
final class JavaWriter extends Writer {

    private static final String NL = System.lineSeparator();
    private final Writer w;
    private final String visibility;
    private final String className;
    private boolean closed = false;
    private int indentation = 2;

    JavaWriter(Object generator, Context ctxt, Writer w, String className, CharSequence comment) throws IOException {
        this.w = w;
        this.className = className;

        visibility = ctxt.option(Visibility.DEF).isPresent() ? "public " : "";

        w.append("// GENERATED CODE: ").append(generator.getClass().getName()).append(NL);

        Pkg pkg = ctxt.pkg();
        if (!pkg.isUnnamed()) {
            w.append("package ").append(pkg).append(";").append(NL).append(NL);
        }

        if (comment.length() != 0) {
            w.append("/** \"");
            StringLiterals.write(comment, w);
            w.append("\" */").append(NL);
        }
        w.append(visibility).append("final class ").append(className).append(" {").append(NL).append(NL);
        w.append("  private ").append(className).append("() {}").append(NL);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        w.write(cbuf, off, len);
    }

    @Override
    public JavaWriter append(CharSequence csq) throws IOException {
        w.append(csq);
        return this;
    }

    @Override
    public JavaWriter append(char c) throws IOException {
        w.append(c);
        return this;
    }

    @Override
    public void flush() throws IOException {
        w.flush();
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        closed = true;
        try {
            w.append("}").append(NL);
        } finally {
            w.close();
        }
    }

    public JavaWriter nl() throws IOException {
        w.append(NL);
        return this;
    }

    public JavaWriter staticFinal(String type, String name) throws IOException {
        w.append(visibility).append("static final ").append(type).append(" ").append(name).append(" = ");
        return this;
    }

    public JavaWriter staticMember(String type, String name) throws IOException {
        w.append(visibility).append("static ").append(type).append(" ").append(name);
        return this;
    }

    public JavaWriter string(CharSequence value) throws IOException {
        w.append('"');
        StringLiterals.write(value, w);
        w.append('"');
        return this;
    }

    public JavaWriter comment(String comment) throws IOException {
        return this.indent().append("/** ").string(comment).append(" */").append(NL);
    }

    public JavaWriter openBrace() throws IOException {
        w.append('{');
        this.indentation += 2;
        return this;
    }

    public JavaWriter closeBrace() throws IOException {
        this.indentation -= 2;
        return this.indent().append('}');
    }

    public JavaWriter indent() throws IOException {
        for (int i = 0; i < indentation; i++) {
            w.append(' ');
        }
        return this;
    }

    public JavaWriter openResource(CharSequence resource) throws IOException {
        return openResource(resource, true);
    }

    public JavaWriter openResource(CharSequence resource, boolean literal) throws IOException {
        append("java.util.Objects.requireNonNull(");
        append(className);
        append(".class.getResourceAsStream(");
        if (literal) {
            string(resource);
        } else {
            append(resource);
        }
        append("), ");
        if (literal) {
            string(resource);
        } else {
            append(resource);
        }
        append(")");
        return this;
    }

}
