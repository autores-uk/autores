// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/** A package containing {@link javax.swing.ImageIcon}s. */
@ResourceFiles(
        value = {
                "Meow.png",
                "Woof.png",
        },
        handler = GenerateIconsFromFiles.class,
        config = @ResourceFiles.Cfg(key = VISIBILITY, value = CfgVisibility.PUBLIC),
        processing = @Processing(
                namer = IdiomaticNamer.class
        )
)
package uk.autores.custom.app.icons;

import uk.autores.Processing;
import uk.autores.custom.handler.GenerateIconsFromFiles;
import uk.autores.handling.CfgVisibility;
import uk.autores.ResourceFiles;
import uk.autores.naming.IdiomaticNamer;

import static uk.autores.handling.CfgVisibility.VISIBILITY;
