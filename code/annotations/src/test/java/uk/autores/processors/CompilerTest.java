package uk.autores.processors;

import org.junit.jupiter.api.Test;
import uk.autores.env.TestElement;
import uk.autores.env.TestProcessingEnvironment;
import uk.autores.processing.Context;
import uk.autores.processing.Namer;
import uk.autores.processing.Pkg;

import javax.lang.model.SourceVersion;
import javax.tools.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {

    @Test
    void detectsMissingCompiler() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context ctxt = new Context(
                env,
                StandardLocation.CLASS_PATH,
                new Pkg(""),
                TestElement.INSTANCE,
                emptyList(),
                emptyList(),
                new Namer()
        );
        Compiler.detect(ctxt, () -> null);
        assertEquals(1, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
    }

    @Test
    void detectsCompiler() {
        TestProcessingEnvironment env = new TestProcessingEnvironment();
        Context ctxt = new Context(
                env,
                StandardLocation.CLASS_PATH,
                new Pkg(""),
                TestElement.INSTANCE,
                emptyList(),
                emptyList(),
                new Namer()
        );
        Compiler.detect(ctxt, () -> "");
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.WARNING).size());
        assertEquals(0, env.getMessager().messages.get(Diagnostic.Kind.ERROR).size());
    }


}