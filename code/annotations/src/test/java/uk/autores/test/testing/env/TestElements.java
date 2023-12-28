// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.testing.env;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class TestElements implements Elements {

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDocComment(Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeprecated(Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageElement getPackageOf(Element type) {
        return TestPackageElement.INSTANCE;
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConstantExpression(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printElements(Writer w, Element... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getName(CharSequence cs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        throw new UnsupportedOperationException();
    }
}
