// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.testing;

import uk.autores.handling.*;
import uk.autores.test.env.TestElement;
import uk.autores.test.env.TestFileObject;
import uk.autores.test.env.TestProcessingEnvironment;

import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HandlerTester {

    private static final Namer NAMER = new Namer();

    private final Handler handler;
    private List<Config> cfg = Collections.emptyList();
    private final TestProcessingEnvironment env = new TestProcessingEnvironment();
    private final List<Resource> resources = new ArrayList<>();
    private Pkg pkg = Pkg.named("");

    public HandlerTester(Handler handler) {
        this.handler = handler;
    }

    public HandlerTester withConfig(List<Config> cfg) {
        this.cfg = cfg;
        return this;
    }

    public HandlerTester withInfinitelyLargeFile() {
        List<Resource> added = ResourceSets.infinitelyLargeFile(env);
        resources.addAll(added);
        return this;
    }

    public HandlerTester withUnspecifiedFile(String filename, byte[] data) throws IOException {
        TestFileObject tfo = new TestFileObject(true);
        try(OutputStream out = tfo.openOutputStream()) {
            out.write(data);
        }
        ResourceSets.of(env, filename, tfo);
        return this;
    }

    public HandlerTester withResource(String filename, byte[] data) throws IOException {
        TestFileObject tfo = new TestFileObject(true);
        try(OutputStream out = tfo.openOutputStream()) {
            out.write(data);
        }
        List<Resource> added = ResourceSets.of(env, filename, tfo);
        resources.addAll(added);
        return this;
    }

    public HandlerTester withBadFilename(String filename) throws IOException {
        List<Resource> added = ResourceSets.junkWithBadFilename(env, filename);
        resources.addAll(added);
        return this;
    }

    public HandlerTester withLargeAndSmallTextFiles(int multiplier) throws IOException {
        List<Resource> added = ResourceSets.largeAndSmallTextFile(env, multiplier);
        resources.addAll(added);
        return this;
    }

    public HandlerTester withPkg(Pkg pkg) {
        this.pkg = pkg;
        return this;
    }

    public HandlerResults test() throws Exception {
        Context context = Context.builder()
                .setAnnotated(TestElement.INSTANCE)
                .setEnv(env)
                .setConfig(cfg)
                .setLocation(StandardLocation.CLASS_PATH)
                .setNamer(NAMER)
                .setPkg(pkg)
                .setResources(resources)
                .build();

        handler.handle(context);

        return new HandlerResults(env);
    }
}
