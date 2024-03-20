// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.processors;

import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.autores.*;
import uk.autores.handling.ResourceFiles;
import uk.autores.processors.ResourceFilesProcessor;
import uk.autores.repeat.*;
import uk.autores.test.testing.env.TestProcessingEnvironment;

import javax.lang.model.SourceVersion;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// https://blog.jooq.org/how-to-unit-test-your-annotation-processor-using-joor/
class ResourceFilesProcessorTest {

  private DecoratedProcessor processor;

  @BeforeEach
  void before() {
    processor = new DecoratedProcessor(new ResourceFilesProcessor());
  }

  @Test
  void metadata() {
    processor.init(new TestProcessingEnvironment());
    assertEquals(SourceVersion.RELEASE_8, processor.getSupportedSourceVersion());
    Class<?>[] annotations = {
            ResourceFiles.class, RepeatableResourceFiles.class,
            ByteArrayResources.class, RepeatableByteArrayResources.class,
            InputStreamResources.class, RepeatableInputStreamResources.class,
            KeyedResources.class, RepeatableKeyedResources.class,
            MessageResources.class, RepeatableMessageResources.class,
            StringResources.class, RepeatableStringResources.class,
    };
    Set<String> expected = Stream.of(annotations)
            .map(Class::getName)
            .collect(Collectors.toSet());
    assertEquals(expected, processor.getSupportedAnnotationTypes());
  }

  @Test
  void correctlyAnnotatedResourcesCompile() throws IOException {
    TestSources.Source src = TestSources.load(this, "ClasspathResource_OK.java");
    Reflect.compile(
            src.className,
            src.sourceCode,
            new CompileOptions().processors(processor)
    ).create().get();
    assertTrue(processor.invoked);
  }

  @Test
  void correctlyAnnotatedRepeatersCompile() throws IOException {
    TestSources.Source src = TestSources.load(this, "ClasspathResourceRepeated_OK.java");
    Reflect.compile(
            src.className,
            src.sourceCode,
            new CompileOptions().processors(processor)
    ).create().get();
    assertTrue(processor.invoked);
  }

  @Test
  void missingResourcesFailCompilation() throws IOException {
    TestSources.Source src = TestSources.load(this, "ClasspathResource_ERR_NOT_EXIST.java");
    try {
      Reflect.compile(
              src.className,
              src.sourceCode,
              new CompileOptions().processors(processor)
      ).create().get();
      fail();
    } catch (ReflectException e) {
      assertTrue(processor.invoked);
    }
  }

  @Test
  void missingHandlerClassFailsCompilation() throws IOException {
    TestSources.Source src = TestSources.load(this, "BadHandler.java");
    try {
      Reflect.compile(
              src.className,
              src.sourceCode,
              new CompileOptions().processors(processor)
      ).create().get();
      fail();
    } catch (ReflectException e) {
      assertTrue(processor.invoked);
    }
  }

  @Test
  void badConfigFailsCompilation() throws IOException {
    TestSources.Source src = TestSources.load(this, "BadConfig.java");
    try {
      Reflect.compile(
              src.className,
              src.sourceCode,
              new CompileOptions().processors(processor)
      ).create().get();
      fail();
    } catch (ReflectException e) {
      assertTrue(processor.invoked);
    }
  }

  @Test
  void throwingHandlerFailsCompilation() throws IOException {
    TestSources.Source src = TestSources.load(this, "ThrowingHandlerTest.java");
    try {
      Reflect.compile(
              src.className,
              src.sourceCode,
              new CompileOptions().processors(processor)
      ).create().get();
      fail();
    } catch (ReflectException e) {
      assertTrue(processor.invoked);
    }
  }

  @Test
  void emptyResourceFailsCompilation() throws IOException {
    TestSources.Source src = TestSources.load(this, "EmptyResource.java");
    try {
      Reflect.compile(
              src.className,
              src.sourceCode,
              new CompileOptions().processors(processor)
      ).create().get();
      fail();
    } catch (ReflectException e) {
      assertTrue(processor.invoked);
    }
  }

  @Test
  void resourcesAnnotationsProcessed() throws IOException {
    TestSources.Source src = TestSources.load(this, "SpecificResources_OK.java");
    Reflect.compile(
            src.className,
            src.sourceCode,
            new CompileOptions().processors(processor)
    ).create().get();
    assertTrue(processor.invoked);
  }

  @Test
  void repeatedResourceAnnotationsProcessed() throws IOException {
    TestSources.Source src = TestSources.load(this, "SpecificResourcesRepeated_OK.java");
    Reflect.compile(
            src.className,
            src.sourceCode,
            new CompileOptions().processors(processor)
    ).create().get();
    assertTrue(processor.invoked);
  }
}
