package uk.autores.internal;

import uk.autores.processing.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Utility type for loading {@link Properties} files. */
public final class PropLoader {

    private PropLoader() {}

    public static Properties load(Resource file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = file.open()) {
            props.load(in);
        }
        return props;
    }
}
