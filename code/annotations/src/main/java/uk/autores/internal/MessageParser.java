package uk.autores.internal;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Utility type for analysing {@link MessageFormat} strings */
public final class MessageParser {

    private MessageParser() {}

    public static List<VarType> parse(String msg) {
        MessageFormat mf = new MessageFormat(msg);
        Format[] formats = mf.getFormatsByArgumentIndex();
        if (formats.length == 0) {
            return Collections.emptyList();
        }

        List<VarType> list = new ArrayList<>();
        for (Format format : mf.getFormatsByArgumentIndex()) {
            if (format == null) {
                list.add(VarType.STRING);
            } else if (format instanceof NumberFormat) {
                list.add(VarType.NUMBER);
            } else if (format instanceof DateFormat) {
                list.add(VarType.DATE);
            } else {
                // unreachable unless MessageFormat adds new Formats
                throw new IllegalStateException("Cannot handle: " + format.getClass().getCanonicalName());
            }
        }
        return list;
    }

    public static boolean needsLocale(List<VarType> vars) {
        return vars.stream().anyMatch(v -> v == VarType.NUMBER || v == VarType.DATE);
    }

    public static boolean needsTimeZone(List<VarType> vars) {
        return vars.stream().anyMatch(VarType.DATE::equals);
    }

    public enum VarType {

        STRING("java.lang.String"),
        NUMBER("java.lang.Number"),
        DATE("java.time.Instant");

        public final String type;

        VarType(String type) {
            this.type = type;
        }
    }

}
