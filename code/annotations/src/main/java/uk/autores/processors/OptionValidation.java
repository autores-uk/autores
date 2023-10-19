package uk.autores.processors;

import uk.autores.processing.Config;
import uk.autores.processing.ConfigDef;
import uk.autores.processing.Context;
import uk.autores.processing.Handler;

import java.util.Optional;

import static java.lang.String.format;

final class OptionValidation {

    private OptionValidation() {}

    static boolean areValid(Handler handler, Context context) {
        if (!handler.validateConfig()) {
            return true;
        }

        String err = "";

        for (Config option : context.config()) {
            long c = context.config().stream().filter(o -> option.key().equals(o.key())).count();
            Optional<ConfigDef> def = handler.config().stream().filter(o -> o.key().equals(option.key())).findFirst();
            if (def.isPresent()) {
                ConfigDef od = def.get();
                if (c > 1) {
                    err = format("%sDuplicate configuration option %s. ", err, option.key());
                }
                if (!od.isValid(option.value())) {
                    err = format("%sInvalid value %s=%s. ", err, option.key(), option.value());
                }
            } else {
                err = format("%sUnknown configuration option %s. ", err, option.key());
            }
        }

        if (err.isEmpty()) {
            return true;
        }

        context.printError(err);

        return false;
    }
}
