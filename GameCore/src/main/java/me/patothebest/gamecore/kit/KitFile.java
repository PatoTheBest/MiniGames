package me.patothebest.gamecore.kit;

import me.patothebest.gamecore.file.FlatFile;

import java.io.File;
import java.io.IOException;

public class KitFile extends FlatFile {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final Kit kit;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public KitFile(Kit kit) {
        super("kits" + File.separatorChar + kit.getKitName());
        this.header = "Kit" + kit.getKitName() + " data";
        this.kit = kit;
        load();
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void save() {
        set("data", kit.serialize());

        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "KitFile{" +
                "kit=" + kit.getKitName() +
                '}';
    }
}
