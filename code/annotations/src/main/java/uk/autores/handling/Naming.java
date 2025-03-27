package uk.autores.handling;

import uk.autores.naming.Namer;

final class Naming {
    private Naming() {}

    static String type(Context ctxt) {
        String name = ctxt.option(CfgName.DEF).orElse("");
        if (name.isEmpty()) {
            Namer namer = ctxt.namer();
            String segment = ctxt.pkg().lastSegment();
            name = namer.nameType(segment);
        }
        return name;
    }

    static String member(Context ctxt, Resource resource) {
        Namer namer = ctxt.namer();
        String simple = namer.simplifyResourceName(resource.toString());
        return namer.nameMember(simple);
    }

    static String type(Context ctxt, Resource res) {
        Namer namer = ctxt.namer();
        String simple = namer.simplifyResourceName(res.toString());
        return namer.nameType(simple);
    }
}
