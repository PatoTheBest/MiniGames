package me.patothebest.gamecore.sign;

import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.gamecore.modules.Module;

import javax.inject.Inject;

public class SignFile extends FlatFile implements Module {

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    @Inject private SignFile() {
        super("signs");
        this.header = "Signs File";

        load();
    }
}
