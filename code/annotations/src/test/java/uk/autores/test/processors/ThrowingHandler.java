// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.test.processors;

import uk.autores.handling.Context;
import uk.autores.handling.Handler;

public class ThrowingHandler implements Handler {
    @Override
    public void handle(Context context) throws Exception {
        throw new RuntimeException("this is just a test");
    }
}
