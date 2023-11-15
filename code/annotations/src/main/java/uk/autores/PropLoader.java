package uk.autores;

import uk.autores.handling.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Utility type for loading {@link Properties} files. */
final class PropLoader {

    private PropLoader() {}

    static Properties load(Resource file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = file.open()) {
            props.load(in);
        }
        return props;
    }
}
