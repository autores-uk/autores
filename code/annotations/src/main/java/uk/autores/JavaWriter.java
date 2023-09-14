package uk.autores;

import uk.autores.processing.Context;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Convenience type for writing Java files.
 */
final class JavaWriter extends Writer {

    private static final String NL = System.lineSeparator();
    private final Writer w;
    private final String visibility;
    private boolean closed = false;
    private int indentation = 2;

    JavaWriter(Object generator, Context ctxt, Writer w, String className, String comment) throws IOException {
        this.w = w;

        visibility = ctxt.option(ConfigDefs.VISIBILITY.name()).isPresent() ? "public " : "";

        if (!ctxt.pkg.isUnnamed()) {
            w.append("package ").append(ctxt.pkg.name).append(";").append(NL).append(NL);
        }

        if (!comment.isEmpty()) {
            w.append("/** \"");
            StringLiterals.write(comment, w);
            w.append("\" */").append(NL);
        }
        w.append("@javax.annotation.Generated(\"").append(generator.getClass().getName()).append("\")").append(NL);
        w.append(visibility).append("final class ").append(className).append(" {").append(NL);
        w.append("\n  private ").append(className).append("() {}").append(NL);
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

    public JavaWriter string(String value) throws IOException {
        w.append('"');
        StringLiterals.write(value, w);
        w.append('"');
        return this;
    }

    public JavaWriter string(Reader reader) throws IOException {
        w.append('"');
        StringLiterals.write(reader, w);
        w.append('"');
        return this;
    }

    public JavaWriter comment(String comment) throws IOException {
        return this.append("  /** ").string(comment).append(" */").append(NL);
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
}