// Copyright 2024 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.format.*;

import java.io.IOException;
import java.util.List;

final class GenerateMessages {
    private GenerateMessages() {}

    public static void write(JavaWriter w, List<FormatSegment> expression) throws IOException {
        int argCount = Formatting.argumentCount(expression);
        int est = Formatting.estimateLength(expression);
        w.indent().append("java.lang.StringBuffer buf = new java.lang.StringBuffer(").append(est).append(");").nl();
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatLiteral) {
                FormatLiteral lit = (FormatLiteral) segment;
                w.indent().append("buf.append(").string(lit.processed()).append(");").nl();
            } else {
                add(w, argCount, (FormatVariable) segment);
            }
        }
        w.indent().append("return buf.toString();").nl();

    }

    private static void add(JavaWriter writer, int argCount, FormatVariable v) throws IOException {
        switch (v.type()) {
            case NONE:
                none(writer, v);
                break;
            case NUMBER:
                number(writer, v);
                break;
            case CHOICE:
                choice(writer, argCount, v);
                break;
            default:
                temporal(writer, v);
        }
    }

    private static void none(JavaWriter w, FormatVariable v) throws IOException {
        int i = v.index();
        w.indent().append("buf.append(arg").append(i).append(");").nl();
    }

    private static void number(JavaWriter w, FormatVariable v) throws IOException {
        int i = v.index();

        w.indent();
        switch (v.style()) {
            case INTEGER:
                w.append("java.text.NumberFormat.getIntegerInstance(l)");
                break;
            case CURRENCY:
                w.append("java.text.NumberFormat.getCurrencyInstance(l)");
                break;
            case PERCENT:
                w.append("java.text.NumberFormat.getPercentInstance(l)");
                break;
            case SUBFORMAT:
                String symbols = "java.text.DecimalFormatSymbols.getInstance(l)";
                String sf = v.subformat();
                w.append("new java.text.DecimalFormat(").string(sf).append(", ").append(symbols).append(")");
                break;
            default:
                w.append("java.text.NumberFormat.getInstance(l)");
        }
        w.append(".format(arg").append(i).append(", buf, new java.text.FieldPosition(0));").nl();
    }

    private static void choice(JavaWriter w, int argCount, FormatVariable v) throws IOException {
        final String cf = "java.text.ChoiceFormat";
        final String nfp = "new java.text.FieldPosition(0)";
        int i = v.index();

        w.indent().openBrace().nl();

        w.indent().append(cf).append(" format = new ").append(cf).append("(").string(v.subformat()).append(");").nl();
        w.indent().append("java.lang.String result = format.format(arg").append(i).append(");").nl();
        w.indent().append("if (result.indexOf('{') >= 0) ").openBrace().nl();

        w.indent().append("java.lang.Object[] args = {");
        String delim = "";
        for (int a = 0; a < argCount; a++) {
            w.append(delim).append("arg").append(a);
            delim = ", ";
        }
        w.append("};").nl();

        w.indent().append("new java.text.MessageFormat(result, l).format(args, buf, ").append(nfp).append(");").nl();
        w.closeBrace().append(" else ").openBrace().nl();
        w.indent().append("buf.append(result);").nl();
        w.closeBrace().nl();

        w.closeBrace().nl();
    }

    public static void temporal(JavaWriter w, FormatVariable v) throws IOException {
        int i = v.index();
        w.indent().append("java.time.format.DateTimeFormatter.");

        if (v.style() == FmtStyle.SUBFORMAT) {
            w.append("ofPattern(").string(v.subformat()).append(", l)");
            w.append(".formatTo(arg").append(i).append(", buf);").nl();
            return;
        }

        String fs = "java.time.format.FormatStyle.";
        String style = (v.style() == FmtStyle.NONE)
                ? "MEDIUM"
                : v.style().toString();
        switch (v.type()) {
            case DTF_DATETIME:
                w.append("ofLocalizedDateTime(").append(fs).append(style).append(").withLocale(l)");
                break;
            case DATE:
            case DTF_DATE:
                w.append("ofLocalizedDate(").append(fs).append(style).append(").withLocale(l)");
                break;
            case TIME:
            case DTF_TIME:
                w.append("ofLocalizedTime(").append(fs).append(style).append(").withLocale(l)");
                break;
            default:
                String standard = v.type().toString();
                w.append(standard);
        }
        w.append(".formatTo(arg").append(i).append(", buf);").nl();
    }
}
