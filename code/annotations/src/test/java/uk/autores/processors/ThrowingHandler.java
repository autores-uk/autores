package uk.autores.processors;

import uk.autores.processing.Context;
import uk.autores.processing.Handler;

public class ThrowingHandler implements Handler {
    @Override
    public void handle(Context context) throws Exception {
        throw new RuntimeException("this is just a test");
    }
}
