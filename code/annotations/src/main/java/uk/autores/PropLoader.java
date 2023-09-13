package uk.autores;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class PropLoader {

    private PropLoader() {}

    static Properties load(FileObject file) throws IOException {
        Properties props = new Properties();
        try (InputStream in = file.openInputStream()) {
            props.load(in);
        }
        return props;
    }
}
