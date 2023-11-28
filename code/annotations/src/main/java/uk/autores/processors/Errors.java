// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processors;

final class Errors {

    private Errors() {}

    static String resourceErrorMessage(Exception e, CharSequence resource, CharSequence pkg) {
        boolean missing = e.getClass().getSimpleName().equals("ClientCodeException")
                && e.getCause() != null
                && NullPointerException.class == e.getCause().getClass();
        if (missing) {
            return resource + " not found in package '" + pkg + "'";
        }
        return "Could not process " + resource + " in package '" + pkg + "': " + e;
    }
}
