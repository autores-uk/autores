package uk.autores.test.processors;

import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnvironmentTest {
    @Test
    void checkCompiler() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
    }
}
