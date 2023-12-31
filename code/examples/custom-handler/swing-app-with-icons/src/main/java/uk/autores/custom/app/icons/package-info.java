// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/** A package containing {@link javax.swing.ImageIcon}s. */
@ResourceFiles(
        value = {
                "Meow.png",
                "Woof.png",
        },
        handler = GenerateIconsFromFiles.class,
        config = @ResourceFiles.Cfg(key = VISIBILITY, value = Visibility.PUBLIC)
)
package uk.autores.custom.app.icons;

import uk.autores.ResourceFiles;
import uk.autores.cfg.Visibility;
import uk.autores.custom.handler.GenerateIconsFromFiles;

import static uk.autores.cfg.Visibility.VISIBILITY;
