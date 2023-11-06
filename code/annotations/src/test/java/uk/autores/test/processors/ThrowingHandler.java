package uk.autores.test.processors;

import uk.autores.handling.Context;
import uk.autores.handling.Handler;

public class ThrowingHandler implements Handler {
    @Override
    public void handle(Context context) throws Exception {
        throw new RuntimeException("this is just a test");
    }
}
