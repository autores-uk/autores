// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.testing.env;

import javax.lang.model.element.Name;
import java.util.Objects;

public class TestName implements Name {

    private final String value;

    public TestName(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean contentEquals(CharSequence cs) {
        return value.contentEquals(cs);
    }
}
