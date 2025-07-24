// Copyright 2025 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.handlers.debug;

import uk.autores.handling.Context;
import uk.autores.handling.Handler;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Internal tool for reporting on {@link Filer#getResource(JavaFileManager.Location, CharSequence, CharSequence)}
 * implementations.
 */
public final class DebugReport implements Handler {
    private final AtomicBoolean done = new AtomicBoolean(false);

    @Override
    public void handle(Context context) throws Exception {
        if (done.getAndSet(true)) {
            return;
        }
        String report = document(context);
        write(context, report);
    }

    private String document(Context context) {
        StringBuilder buf = new StringBuilder();
        for (StandardLocation l : StandardLocation.values()) {
            location(context, l, buf);
        }
        return buf.toString();
    }

    private void location(Context context, JavaFileManager.Location l, StringBuilder buf) {
        if (context.resources().isEmpty()) {
            return;
        }
        CharSequence r = context.resources().get(0);
        Filer filer = context.env().getFiler();
        try {
            filer.getResource(l, context.pkg(), r);
            buf.append(l).append(" OK\n");
        } catch (Exception e) {
            buf.append(l).append(" ").append(e).append("\n");
        }
    }

    private void write(Context context, String results) {
        Filer filer = context.env().getFiler();
        try {
            String name = DebugReport.class.getSimpleName() + "-" + System.identityHashCode(this) + ".txt";
            FileObject r = filer.createResource(StandardLocation.CLASS_OUTPUT, "", name);
            try (OutputStream os = r.openOutputStream();
                 Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                w.write(results);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
