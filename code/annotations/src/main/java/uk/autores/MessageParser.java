package uk.autores;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Utility type for analysing {@link MessageFormat} strings */
final class MessageParser {

    private MessageParser() {}

    static List<VarType> parse(String msg) {
        MessageFormat mf = new MessageFormat(msg);
        Format[] formats = mf.getFormatsByArgumentIndex();
        if (formats.length == 0) {
            return Collections.emptyList();
        }

        List<VarType> list = new ArrayList<>(formats.length);
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

    static boolean needsLocale(List<VarType> vars) {
        for (VarType vt : vars) {
            if (vt == VarType.NUMBER || vt == VarType.DATE) {
                return true;
            }
        }
        return false;
    }

    static boolean needsTimeZone(List<VarType> vars) {
        return vars.contains(VarType.DATE);
    }

    enum VarType {

        STRING("java.lang.String"),
        NUMBER("java.lang.Number"),
        DATE("java.time.Instant");

        final String type;

        VarType(String type) {
            this.type = type;
        }
    }

}
