// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.env;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class TestProcessingEnvironment implements ProcessingEnvironment {

    private final TestFiler filer = new TestFiler();
    private final TestElements elements = new TestElements();
    private final TestMessager messager = new TestMessager();

    @Override
    public Map<String, String> getOptions() {
        return Collections.emptyMap();
    }

    @Override
    public TestMessager getMessager() {
        return messager;
    }

    @Override
    public TestFiler getFiler() {
        return filer;
    }

    @Override
    public TestElements getElementUtils() {
        return elements;
    }

    @Override
    public Types getTypeUtils() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SourceVersion getSourceVersion() {
        return SourceVersion.RELEASE_11;
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }
}
