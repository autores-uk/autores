package uk.autores;

import uk.autores.processing.Context;
import uk.autores.processing.Pkg;

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
    private String resourceLoadMethod = null;

    JavaWriter(Object generator, Context ctxt, Writer w, String className, String comment) throws IOException {
        this.w = w;
        this.className = className;

        visibility = ctxt.option(ConfigDefs.VISIBILITY).isPresent() ? "public " : "";

        w.append("// GENERATED CODE: ").append(generator.getClass().getName()).append(NL);

        Pkg pkg = ctxt.pkg();
        if (!pkg.isUnnamed()) {
            w.append("package ").append(pkg.name()).append(";").append(NL).append(NL);
        }

        if (!comment.isEmpty()) {
            w.append("/** \"");
            StringLiterals.write(comment, w);
            w.append("\" */").append(NL);
        }
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
            if (resourceLoadMethod != null) {
                writeResourceLoadMethod();
            }

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

    public JavaWriter openResource(String resource) throws IOException {
        if (resourceLoadMethod == null) {
            resourceLoadMethod = "open$" + Integer.toString(className.hashCode(), 16) + "$resource";
        }

        return this.append(resourceLoadMethod)
                .append("(")
                .string(resource)
                .append(")");
    }

    private void writeResourceLoadMethod() throws IOException {
        this.nl();
        this.indent()
                .append("private static java.io.InputStream ")
                .append(resourceLoadMethod)
                .append("(java.lang.String resource) throws java.io.IOException ")
                .openBrace()
                .nl();
        this.indent()
                .append("java.io.InputStream in = ")
                .append(className)
                .append(".class.getResourceAsStream(resource);")
                .nl();
        this.indent()
                .append("if (in == null) { throw new java.io.IOException(")
                .string("Resource not found: ")
                .append("+resource); }")
                .nl();
        this.indent().append("return in;").nl();
        this.closeBrace().nl();
    }

    public JavaWriter throwOnModification(String predicate, String resource) throws IOException {
        String err = "Resource modified after compilation: ";

        this.indent().append("if (").append(predicate).append(") ").openBrace().nl();
        this.indent().append("throw new AssertionError(").string(err).append("+").string(resource).append(");").nl();
        this.closeBrace().nl();
        return this;
    }
}
