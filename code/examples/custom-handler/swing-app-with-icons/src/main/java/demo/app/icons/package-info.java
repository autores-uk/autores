/** A package containing {@link javax.swing.ImageIcon}s. */
@ClasspathResource(
        value = {
                "Meow.png",
                "Woof.png",
        },
        handler = GenerateIconsFromFiles.class,
        config = @ClasspathResource.Cfg(key = "strategy", value = "inline")
)
package demo.app.icons;

import demo.custom.handler.GenerateIconsFromFiles;
import uk.autores.ClasspathResource;