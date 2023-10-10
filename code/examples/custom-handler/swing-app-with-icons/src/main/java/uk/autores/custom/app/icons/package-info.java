/** A package containing {@link javax.swing.ImageIcon}s. */
@ClasspathResource(
        value = {
                "Meow.png",
                "Woof.png",
        },
        handler = GenerateIconsFromFiles.class
)
package uk.autores.custom.app.icons;

import uk.autores.ClasspathResource;
import uk.autores.custom.handler.GenerateIconsFromFiles;
