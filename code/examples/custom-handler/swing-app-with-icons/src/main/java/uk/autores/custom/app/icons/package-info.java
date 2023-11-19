/** A package containing {@link javax.swing.ImageIcon}s. */
@ResourceFiles(
        value = {
                "Meow.png", // class MeowIcon
                "Woof.png", // class WoofIcon
        },
        handler = GenerateIconsFromFiles.class
)
package uk.autores.custom.app.icons;

import uk.autores.ResourceFiles;
import uk.autores.custom.handler.GenerateIconsFromFiles;
