// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.testing.env;

import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import java.util.Objects;

public class TestPackageElement extends TestElement implements PackageElement {

    public static final TestPackageElement INSTANCE = new TestPackageElement("foo.bar");

    private final String name;

    public TestPackageElement(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public Name getQualifiedName() {
        return new TestName(name);
    }

    @Override
    public boolean isUnnamed() {
        return "".equals(name);
    }
}
