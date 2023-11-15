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
        return "Could not process " + resource + " in package '" + pkg + "': " + e.toString();
    }
}
