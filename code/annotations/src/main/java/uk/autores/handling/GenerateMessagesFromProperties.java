// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.handling;

import uk.autores.naming.Namer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.*;

/**
 * <p>
 * {@link Handler} that generates message classes from {@link Properties} files using {@link Locale}s to match localized
 * strings and {@link java.text.MessageFormat} to create typed method signatures.
 * </p>
 * <p>
 *     The properties file name is used to name the class.
 *     The keys are used to name the methods.
 * </p>
 * <p>
 *     {@link Locale#getAvailableLocales()} and
 *     {@link ResourceBundle.Control#toBundleName(String, Locale)}
 *     are used to discover localized properties.
 * </p>
 * <p>
 *     Generated method signatures will vary based on need.
 * </p>
 * <ul>
 *     <li>A method to return the raw value is always generated.
 *     <ul>
 *         <li>If "localize" is true and localized files have been detected the method requires a {@link Locale} argument.</li>
 *     </ul>
 *     </li>
 *     <li>q
 *         A second method is generated if "format" is true and format expressions have been detected in the value.
 *         <ul>
 *             <li>{@link Locale} is the first argument if "localize" is true and localized files have been detected and number/choice/date us used.</li>
 *             <li>
 *                 Format expressions form the remaining arguments in index order.
 *                 {@link MessageFormat} expressions are mapped as follows.
 *                 <ul>
 *                     <li>number or choice: {@link Number}</li>
 *                     <li>date or time: {@link java.time.ZonedDateTime}</li>
 *                     <li>none: {@link String}</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 * <h2>Example</h2>
 * <p>
 *     Example file <code>Cosmic.properties</code>:
 * </p>
 *
 * <pre>planet-event=At {1,time} on {2,date}, there was {3} on planet {0,number,integer}.</pre>
 *
 * <p>
 *     This will generate a class <code>Cosmic</code> with the method signature:
 * </p>
 *
 * <pre>static String planet_event(Locale l, Number v0, ZonedDateTime v1, ZonedDateTime v2, String v3)</pre>
 *
 * <p>
 *     Usage:
 * </p>
 *
 * <pre>
 *     var now = ZonedDateTime.now();
 *     Cosmic.planet_event(Locale.US, 4, now, "an attack")
 * </pre>
 *
 * <p>
 *     Generally variables can be reused in {@link MessageFormat} patterns but this is unsupported
 *     when dates are present due to implementation limitations in time zone handling.
 * </p>
 */
public final class GenerateMessagesFromProperties implements Handler {

    private static final String EXTENSION = ".properties";

    private final LocalePatterns locales = new LocalePatterns();

    /** Ctor */
    public GenerateMessagesFromProperties() {}

    /**
     * <p>All configuration is optional.</p>
     * <p>"localize" is "true" by default.</p>
     * <p>"missing-key" is "error" by default.</p>
     * <p>"format" is "true" by default.</p>
     * <p>
     *     Use "visibility" to make the generated classes public.
     * </p>
     *
     * @return visibility, localize, missing-key
     * @see CfgVisibility
     * @see CfgLocalize
     * @see CfgMissingKey
     * @see CfgFormat
     */
    @Override
    public Set<ConfigDef> config() {
        return Sets.of(CfgVisibility.DEF, CfgLocalize.DEF, CfgMissingKey.DEF, CfgFormat.DEF);
    }

    @Override
    public void handle(Context context) throws IOException {
        List<Resource> resources = context.resources();

        boolean localize = !context.option(CfgLocalize.DEF)
                .filter("false"::equals)
                .isPresent();

        for (Resource res : resources) {
            if (!res.toString().endsWith(EXTENSION)) {
                String msg = "Resource names must end in " + EXTENSION + " - got " + res;
                context.printError(msg);
                continue;
            }

            Properties base = PropLoader.load(res);
            List<Localization> localizations = localize
                    ? loadLocalizations(context, res)
                    : Collections.emptyList();

            writeProperties(context, res, base, localizations);
        }
    }

    private List<Localization> loadLocalizations(Context context, Resource resource) throws IOException {
        Filer filer = context.env().getFiler();
        List<JavaFileManager.Location> locations = context.locations();

        CharSequence resourcePackage = ResourceFiling.pkg(context.pkg(), resource);
        CharSequence name = ResourceFiling.relativeName(resource);

        List<Localization> localized = new ArrayList<>();

        int end = name.length() - EXTENSION.length();
        CharSequence base = name.subSequence(0, end);
        StringBuilder props = new StringBuilder(base.length() + EXTENSION.length() + 6);
        props.append(base);

        for (String pattern : locales.patterns()) {
            props.setLength(base.length());
            props.append(pattern).append(EXTENSION);
            final FileObject file;
            try {
                file = getResource(filer, locations, resourcePackage, props);
            } catch (IOException e) {
                // probably doesn't exist
                // env.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
                continue;
            }

            Resource res = new Resource(file::openInputStream, props.toString());
            Properties properties = PropLoader.load(res);
            localized.add(new Localization(pattern, properties));
        }

        return localized;
    }

    private FileObject getResource(Filer filer, List<JavaFileManager.Location> locations, CharSequence pkg, CharSequence value) throws IOException {
        IOException first = null;
        FileObject fo = null;
        for (JavaFileManager.Location location : locations) {
            try {
                fo = getResource(filer, location, pkg, value);
                try (InputStream is = fo.openInputStream()) {
                    // NOOP; if file can be opened it exists
                    assert is != null;
                }
                return fo;
            } catch (IOException e) {
                if (first == null) {
                    first = e;
                } else {
                    first.addSuppressed(e);
                }
            }
        }
        if (first != null) {
            throw first;
        }
        return fo;
    }

    private FileObject getResource(Filer filer, JavaFileManager.Location location, CharSequence pkg, CharSequence value) throws IOException {
        try {
            return filer.getResource(location, pkg, value);
        } catch (IllegalArgumentException e) {
            // Eclipse compiler doesn't like CLASS_PATH as a location
            throw new IOException(e);
        }
    }

    private void writeProperties(Context ctxt,
                                 Resource resource,
                                 Properties base,
                                 List<Localization> localizations) throws IOException {
        Namer namer = ctxt.namer();
        Pkg pkg = ctxt.pkg();
        Filer filer = ctxt.env().getFiler();

        SortedSet<String> keys = new TreeSet<>(base.stringPropertyNames());

        String simple = namer.simplifyResourceName(resource.toString());
        String name = namer.nameType(simple);
        if (!Namer.isIdentifier(name)) {
            String msg = "Cannot transform resource '" + resource + "' into class name";
            ctxt.printError(msg);
            return;
        }
        String qualified = pkg.qualifiedClassName(name);

        String lookupName = String.format("pattern$%s$%x", name, name.hashCode());

        JavaFileObject jfo = filer.createSourceFile(qualified, ctxt.annotated());
        try (Writer out = jfo.openWriter();
             Writer escaper = new UnicodeEscapeWriter(out);
             JavaWriter writer = new JavaWriter(this, ctxt, escaper, name, resource)) {

            Msgs msgs = new Msgs(resource, lookupName, localizations, writer);

            if (!localizations.isEmpty()) {
                writeCache(writer, name, lookupName, localizations);
            }

            for (String key : keys) {
                writeProperty(ctxt, msgs, key, base.getProperty(key));
            }
        }
    }

    private void writeCache(JavaWriter writer,
                            String name,
                            String lookupName,
                            List<Localization> localizations) throws IOException {
        String cacheName = "CACHE$" + lookupName.toUpperCase(Locale.ENGLISH);
        String computeName = "compute$" + lookupName;

        writer.nl();
        writer.indent()
                .append("private static final java.util.Map<java.util.Locale, java.lang.String> ")
                .append(cacheName)
                .append(" = new java.util.concurrent.ConcurrentHashMap<>();").nl();

        writer.nl();
        writer.indent()
                .append("private static java.lang.String ")
                .append(computeName)
                .append("(java.util.Locale l) ")
                .openBrace()
                .nl();
        writer.indent().append("java.util.ResourceBundle.Control ctrl = java.util.ResourceBundle.Control.getControl(java.util.ResourceBundle.Control.FORMAT_PROPERTIES);").nl();
        writer.indent().append("for (java.util.Locale candidate : ctrl.getCandidateLocales(\"\", l)) ").openBrace().nl();
        writer.indent().append("if (candidate.getLanguage().isEmpty()) ").openBrace().nl();
        writer.indent().append("continue;").nl();
        writer.closeBrace().nl();
        writer.indent().append("java.lang.String pattern = ctrl.toBundleName(\"\", candidate).substring(1);").nl();
        writer.indent().append("switch (pattern) ").openBrace().nl();
        for (Localization l : localizations) {
            String p = l.pattern.substring(1);
            writer.indent().append("case \"")
                    .append(p)
                    .append("\": return \"")
                    .append(p)
                    .append("\";")
                    .nl();
        }
        writer.closeBrace().nl();
        writer.closeBrace().nl();
        writer.indent().append("return \"\";").nl();
        writer.closeBrace().nl();

        writer.nl();
        writer.indent()
                .append("private static java.lang.String ")
                .append(lookupName)
                .append("(java.util.Locale l) ")
                .openBrace()
                .nl();
        writer.indent()
                .append("return ")
                .append(cacheName)
                .append(".computeIfAbsent(l, ")
                .append(name)
                .append("::")
                .append(computeName)
                .append(");")
                .nl();
        writer.closeBrace().nl();
    }

    private void writeProperty(Context ctxt,
                               Msgs msgs,
                               String key,
                               String baseValue) throws IOException {
        Resource resource = msgs.resource;
        String method = ctxt.namer().nameMember(key);
        if (!Namer.isIdentifier(method)) {
            String msg = "Cannot transform key '" + key + "' in " + resource + " to method name";
            ctxt.printError(msg);
            return;
        }

        JavaWriter writer = msgs.writer;
        List<Localization> localizations = msgs.localizations;

        writer.nl().comment(key);
        if (localizations.isEmpty()) {
            writer.indent().staticMember("java.lang.String", method).append("() ").openBrace().nl();
        } else {
            String lookupName = msgs.lookupName;

            writer.indent()
                    .staticMember("java.lang.String", method)
                    .append("(java.util.Locale l) ")
                    .openBrace()
                    .nl();
            writer.indent().append("java.lang.String pattern = ").append(lookupName).append("(l);").nl();
            writer.indent().append("switch (pattern) ").openBrace().nl();
            for (Localization l : localizations) {
                String value = l.properties.getProperty(key);
                if (value == null) {
                    String msg = resource + ": " + l.pattern + ": missing key " + key;
                    Reporting.reporter(ctxt, CfgMissingKey.DEF).accept(msg);
                    value = substituteMissingValue(msgs, l.pattern, key, baseValue);
                }
                String pattern = l.pattern.substring(1);
                writer.indent().append("case ").string(pattern).append(": return ").string(value).append(";").nl();
            }
            writer.closeBrace().nl();
        }
        writer.indent().append("return ").string(baseValue).append(";").nl();
        writer.closeBrace().nl();

        writeFormat(ctxt, msgs, writer, key, baseValue, method);
    }

    private String substituteMissingValue(Msgs msgs, String pattern, String key, String baseValue) {
        List<Localization> candidates = locales.findCandidatesFor(pattern, l18n -> l18n.pattern, msgs.localizations);
        for (Localization candidate : candidates) {
            String result = candidate.properties.getProperty(key);
            if (result != null) {
                return result;
            }
        }
        return baseValue;
    }

    private void writeFormat(Context ctxt,
                             Msgs msgs,
                             JavaWriter writer,
                             String key,
                             String baseValue,
                             String method) throws IOException {
        if (ctxt.option(CfgFormat.DEF).filter(CfgFormat.FALSE::equals).isPresent()) {
            return;
        }

        List<String> vars = MessageParser.parse(baseValue);
        if (vars.isEmpty()) {
            return;
        }

        Resource resource = msgs.resource;
        List<Localization> localizations = msgs.localizations;

        if (!localizedMessagesMatchBase(ctxt, resource, vars, localizations, key)) {
            return;
        }

        int firstDateIndex = MessageParser.firstDateIndex(vars);
        boolean needsLocaleForFormat = MessageParser.needsLocale(vars);
        boolean hasLocalizedMsg = !localizations.isEmpty();

        boolean comma = false;

        writer.nl().comment(key);
        writer.indent().staticMember("java.lang.String", method).append("(");
        if (needsLocaleForFormat || hasLocalizedMsg) {
            writer.append("java.util.Locale l");
            comma = true;
        }
        for (int i = 0; i < vars.size(); i++) {
            if (comma) {
                writer.append(", ");
            }
            comma = true;

            String vt = vars.get(i);
            writer.append(vt).append(" v").append(Integer.toString(i));
        }
        writer.append(") ").openBrace().nl();
        writer.indent().append("java.lang.String msg = ").append(method);
        if (hasLocalizedMsg) {
            writer.append("(l);").nl();
        } else {
            writer.append("();").nl();
        }
        if (needsLocaleForFormat || hasLocalizedMsg) {
            writer.indent().append("java.text.MessageFormat formatter = new java.text.MessageFormat(msg, l);").nl();
        } else {
            writer.indent().append("java.text.MessageFormat formatter = new java.text.MessageFormat(msg);").nl();
        }
        if (firstDateIndex >= 0) {
            if (MessageParser.variableReuse(baseValue)) {
                reportVariableReuseError(ctxt, resource.toString(), baseValue);
            } else {
                printTimeZoneFormat(writer, vars);
            }
        }
        writer.indent().append("java.lang.Object[] args = ").openBrace().nl();
        for (int i = 0; i < vars.size(); i++) {
            boolean date = vars.get(i).equals(MessageParser.DATE);
            writer.indent();
            if (date) {
                writer.append("java.util.Date.from(");
            }
            writer.append("v");
            writer.append(Integer.toString(i));
            if (date) {
                writer.append(".toInstant())");
            }
            writer.append(",").nl();
        }
        writer.closeBrace().append(";").nl();
        // capacity is just a guess
        int capacity = baseValue.length() * 2 + vars.size() * 8;
        writer.indent()
                .append("return formatter.format(args, new java.lang.StringBuffer(")
                .append(capacity)
                .append("), null).toString();").nl();
        writer.closeBrace().nl();
    }

    private void printTimeZoneFormat(JavaWriter writer, List<String> patternVariables) throws IOException {
        writer.indent().append("java.util.TimeZone tz;").nl();
        writer.indent().append("java.text.Format[] fmts = formatter.getFormatsByArgumentIndex();").nl();
        for (int i = 0; i < patternVariables.size(); i++) {
            if (MessageParser.DATE.equals(patternVariables.get(i))) {
                writer.indent()
                        .append("tz = java.util.TimeZone.getTimeZone(v")
                        .append(i)
                        .append(".getZone());")
                        .nl();
                writer.indent()
                        .append("((java.text.DateFormat) fmts[")
                        .append(i)
                        .append("]).setTimeZone(tz);")
                        .nl();
            }
        }
    }

    private void reportVariableReuseError(Context ctxt, String resource, String pattern) {
        String msg = "Unsupported variable reuse in " + resource + ": " + pattern + ": ";
        msg += "variable reuse is not supported when dates are present";
        ctxt.printError(msg);
    }

    private boolean localizedMessagesMatchBase(Context ctxt,
                                               Resource resource,
                                               List<String> vars,
                                               List<Localization> localizations, String key) {
        boolean ok = true;
        for (Localization l : localizations) {
            String localizedValue = l.properties.getProperty(key);
            if (localizedValue == null) {
                continue;
            }
            List<String> locVars = MessageParser.parse(localizedValue);
            if (!locVars.equals(vars)) {
                String msg = "Differing message variables in localization " + resource + ": " + l.pattern + ": ";
                msg += "key=" + key + " have " + locVars + " need " + vars;
                ctxt.printError(msg);
                ok = false;
            }
            if (MessageParser.firstDateIndex(locVars) >=0 && MessageParser.variableReuse(localizedValue)) {
                reportVariableReuseError(ctxt, resource + " " + l.pattern, localizedValue);
                ok = false;
            }
        }
        return ok;
    }

    private static final class Localization {

        final String pattern;
        final Properties properties;

        Localization(String pattern, Properties properties) {
            this.pattern = pattern;
            this.properties = properties;
        }
    }

    private static class Msgs {
        private final Resource resource;
        private final String lookupName;
        private final List<Localization> localizations;
        private final JavaWriter writer;

        private Msgs(Resource resource, String lookupName, List<Localization> localizations, JavaWriter writer) {
            this.resource = resource;
            this.lookupName = lookupName;
            this.localizations = localizations;
            this.writer = writer;
        }
    }
}
