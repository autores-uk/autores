package uk.autores.processors;

import uk.autores.processing.Pkg;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

final class ResPath {

    private ResPath() {}

    static String massage(ProcessingEnvironment env, Element element, Pkg pkg, String resource) {
        if (resource == null || resource.isEmpty()) {
            String err = "Resource name cannot be null or empty";
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, err, element);
            return null;
        }

        boolean relative = pkg.isRelative();
        if (relative == (resource.charAt(0) == '/')) {
            String err = relative
                    ? "Relative resource names cannot begin with '/'"
                    : "Absolute resource names must begin with '/'";
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, err, element);
            return null;
        }

        return relative ? resource : resource.substring(1);
    }
}
