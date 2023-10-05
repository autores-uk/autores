package uk.autores;

import uk.autores.internal.*;
import uk.autores.processing.*;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.*;

import static java.util.Arrays.asList;

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
 *     <li>
 *         A second method is generated if "format" is true and format expressions have been detected in the value.
 *         <ul>
 *             <li>{@link Locale} is the first argument if "localize" is true and localized files have been detected and number/choice/date us used.</li>
 *             <li>If any of the format expressions are of type <code>date</code> {@link TimeZone} is the next argument.</li>
 *             <li>
 *                 Format expressions form the remaining arguments in index order.
 *                 {@link MessageFormat} expressions are mapped as follows.
 *                 <ul>
 *                     <li>number or choice: {@link Number}</li>
 *                     <li>date: {@link java.time.Instant}</li>
 *                     <li>none: {@link String}</li>
 *                 </ul>
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 * <h2>Example</h2>
 * <p>Example file <code>Cosmic.properties</code>:</p>
 * <pre>planet-event=At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.</pre>
 * <p>This will generate a class <code>Cosmic</code> with the method signature:</p>
 * <pre>static String planet_event(Locale l, TimeZone tz, Number v0, Instant v1, String v2)</pre>
 * <p>Usage:</p>
 * <pre>Cosmic.planet_event(Locale.ENGLISH, TimeZone.getTimeZone("GMT"), 4, Instant.EPOCH, "an attack")</pre>
 * <p>
 *     This will return the string <code>"At 12:00:00 AM on Jan 1, 1970, there was an attack on planet 4."</code>.
 * </p>
 */
public final class GenerateMessagesFromProperties implements Handler {

    private static final String EXTENSION = ".properties";

    /**
     * <p>All configuration is optional.</p>
     * <p>"localize" is "true" by default.</p>
     * <p>"missing-key" is "error" by default.</p>
     * <p>"format" is "true" by default.</p>
     *
     * @return visibility, localize, missing-key
     * @see ConfigDefs#VISIBILITY
     * @see ConfigDefs#LOCALIZE
     * @see ConfigDefs#MISSING_KEY
     * @see ConfigDefs#FORMAT
     */
    @Override
    public Set<ConfigDef> config() {
        return ConfigDefs.set(ConfigDefs.VISIBILITY, ConfigDefs.LOCALIZE, ConfigDefs.MISSING_KEY, ConfigDefs.FORMAT);
    }

    @Override
    public void handle(Context context) throws IOException {
        Map<String, FileObject> resources = context.resources();

        boolean localize = !context.option(ConfigDefs.LOCALIZE)
                .filter("false"::equals)
                .isPresent();

        for (Map.Entry<String, FileObject> entry : resources.entrySet()) {
            String resource = entry.getKey();
            if (!resource.endsWith(EXTENSION)) {
                String msg = "Resource names must end in " + EXTENSION + " - got " + resource;
                context.printError(msg);
                continue;
            }

            Properties base = PropLoader.load(entry.getValue());
            List<Localized> localizations = localize
                    ? loadLocalizations(context, resource)
                    : Collections.emptyList();

            writeProperties(context, resource, base, localizations);
        }
    }

    private List<Localized> loadLocalizations(Context context, String name) throws IOException {
        Filer filer = context.env().getFiler();
        JavaFileManager.Location location = context.location();
        String resourcePackage = context.pkg().resourcePackage();

        List<Localized> localized = new ArrayList<>();

        int end = name.length() - EXTENSION.length();
        String base = name.substring(0, end);
        for (Map.Entry<String, Locale> entry : expandedLocales().entrySet()) {
            String pattern = entry.getKey();
            Locale locale = entry.getValue();
            if (locale.getLanguage().isEmpty()) {
                continue;
            }

            String props = base + pattern + EXTENSION;

            final FileObject file;
            try {
                file = filer.getResource(location, resourcePackage, props);
                try (InputStream in = file.openInputStream()) {
                    Objects.requireNonNull(in);
                }
            } catch (IOException e) {
                // probably doesn't exist
                // env.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
                continue;
            }

            Properties properties = PropLoader.load(file);
            localized.add(new Localized(pattern, properties));
        }

        return localized;
    }

    private void writeProperties(Context ctxt,
                                 String resource,
                                 Properties base,
                                 List<Localized> localizations) throws IOException {
        Namer namer = ctxt.namer();
        Pkg pkg = ctxt.pkg();
        Filer filer = ctxt.env().getFiler();

        SortedSet<String> keys = new TreeSet<>(base.stringPropertyNames());

        String simple = namer.simplifyResourceName(resource);
        String name = namer.nameClass(simple);
        if (!Namer.isJavaIdentifier(name)) {
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
                            List<Localized> localizations) throws IOException {
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
        for (Localized l : localizations) {
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
        String resource = msgs.resource;
        String method = ctxt.namer().nameMethod(key);
        if (!Namer.isJavaIdentifier(method)) {
            String msg = "Cannot transform key '" + key + "' in " + resource + " to method name";
            ctxt.printError(msg);
            return;
        }

        JavaWriter writer = msgs.writer;
        List<Localized> localizations = msgs.localizations;

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
            for (Localized l : localizations) {
                String value = l.properties.getProperty(key);
                if (value == null) {
                    String msg = resource + ": " + l.pattern + ": missing key " + key;
                    Reporting.reporter(ctxt, ConfigDefs.MISSING_KEY).accept(msg);
                    continue;
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

    private void writeFormat(Context ctxt,
                             Msgs msgs,
                             JavaWriter writer,
                             String key,
                             String baseValue,
                             String method) throws IOException {
        if (ctxt.option(ConfigDefs.FORMAT).filter("false"::equals).isPresent()) {
            return;
        }

        List<MessageParser.VarType> vars = MessageParser.parse(baseValue);
        if (vars.isEmpty()) {
            return;
        }

        String resource = msgs.resource;
        List<Localized> localizations = msgs.localizations;

        if (!localizedMessagesMatchBase(ctxt, resource, vars, localizations, key)) {
            return;
        }

        boolean needsTimeZone = MessageParser.needsTimeZone(vars);
        boolean needsLocaleForFormat = MessageParser.needsLocale(vars);
        boolean hasLocalizedMsg = !localizations.isEmpty();

        boolean comma = false;

        writer.nl().comment(key);
        writer.indent().staticMember("java.lang.String", method).append("(");
        if (needsLocaleForFormat || hasLocalizedMsg) {
            writer.append("java.util.Locale l");
            comma = true;
        }
        if (needsTimeZone) {
            if (comma) {
                writer.append(", ");
            }
            writer.append("java.util.TimeZone tz");
            comma = true;
        }
        for (int i = 0; i < vars.size(); i++) {
            if (comma) {
                writer.append(", ");
            }
            comma = true;

            MessageParser.VarType vt = vars.get(i);
            writer.append(vt.type).append(" v").append(Integer.toString(i));
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
        if (needsTimeZone) {
            writer.indent().append("java.lang.Object[] fmts = formatter.getFormats();").nl();
            writer.indent().append("for (int i = 0, len = fmts.length; i < len; i++) ").openBrace().nl();
            writer.indent().append("if (fmts[i] instanceof java.text.DateFormat) ").openBrace().nl();
            writer.indent().append("((java.text.DateFormat) fmts[i]).setTimeZone(tz);").nl();
            writer.closeBrace().nl();
            writer.closeBrace();
        }
        writer.indent().append("java.lang.Object[] args = ").openBrace().nl();
        for (int i = 0; i < vars.size(); i++) {
            boolean date = vars.get(i) == MessageParser.VarType.DATE;
            writer.indent();
            if (date) {
                writer.append("java.util.Date.from(");
            }
            writer.append("v");
            writer.append(Integer.toString(i));
            if (date) {
                writer.append(")");
            }
            writer.append(",").nl();
        }
        writer.closeBrace().append(";").nl();
        writer.indent().append("return formatter.format(args, new java.lang.StringBuffer(), null).toString();").nl();
        writer.closeBrace().nl();

        // TODO: efficiency
    }

    private boolean localizedMessagesMatchBase(Context ctxt,
                                               String resource,
                                               List<MessageParser.VarType> vars,
                                               List<Localized> localizations, String key) {
        boolean ok = true;
        for (Localized l : localizations) {
            String localizedValue = l.properties.getProperty(key);
            if (localizedValue == null) {
                continue;
            }
            List<MessageParser.VarType> locVars = MessageParser.parse(localizedValue);
            if (!locVars.equals(vars)) {
                String msg = "Differing message variables in localization " + resource + ": " + l.pattern + ": ";
                msg += "key=" + key + " have " + locVars + " need " + vars;
                ctxt.printError(msg);
                ok = false;
            }
        }
        return ok;
    }

    /**
     * These locales are used to search for localized properties files.
     * Default locales can vary by runtime implementation.
     *
     * @return the supported locales
     * @see Locale#getAvailableLocales()
     */
    private List<Locale> locales() {
        return asList(Locale.getAvailableLocales());
    }

    private SortedMap<String, Locale> expandedLocales() {
        ResourceBundle.Control ctrl = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

        SortedMap<String, Locale> map = new TreeMap<>();
        for (Locale base : locales()) {
            if (base.getLanguage().isEmpty()) {
                continue;
            }
            for (Locale locale : ctrl.getCandidateLocales("", base)) {
                String pattern = ctrl.toBundleName("", locale);
                map.put(pattern, locale);
            }
        }
        return map;
    }

    private static class Localized {
        final String pattern;
        final Properties properties;

        private Localized(String pattern, Properties properties) {
            this.pattern = pattern;
            this.properties = properties;
        }
    }

    private static class Msgs {
        private final String resource;
        private final String lookupName;
        private final List<Localized> localizations;
        private final JavaWriter writer;

        private Msgs(String resource, String lookupName, List<Localized> localizations, JavaWriter writer) {
            this.resource = resource;
            this.lookupName = lookupName;
            this.localizations = localizations;
            this.writer = writer;
        }
    }
}
