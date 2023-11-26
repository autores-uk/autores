// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Utility type for analysing {@link MessageFormat} strings */
final class MessageParser {

    static final String STRING = String.class.getName();
    static final String NUMBER = Number.class.getName();
    static final String DATE = ZonedDateTime.class.getName();

    private MessageParser() {}

    static List<String> parse(String msg) {
        MessageFormat mf = new MessageFormat(msg);
        Format[] formats = mf.getFormatsByArgumentIndex();
        if (formats.length == 0) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>(formats.length);
        for (Format format : mf.getFormatsByArgumentIndex()) {
            if (format == null) {
                list.add(STRING);
            } else if (format instanceof NumberFormat) {
                list.add(NUMBER);
            } else if (format instanceof DateFormat) {
                list.add(DATE);
            } else {
                // unreachable unless MessageFormat adds new Formats
                throw new IllegalStateException("Cannot handle: " + format.getClass().getCanonicalName());
            }
        }
        return list;
    }

    static boolean needsLocale(List<String> vars) {
        for (String vt : vars) {
            if (vt.equals(NUMBER) || vt.equals(DATE)) {
                return true;
            }
        }
        return false;
    }

    static int firstDateIndex(List<String> vars) {
        for (int i = 0, len = vars.size(); i < len; i++) {
            String vt = vars.get(i);
            if (vt.equals(DATE)) {
                return i;
            }
        }
        return -1;
    }

}
