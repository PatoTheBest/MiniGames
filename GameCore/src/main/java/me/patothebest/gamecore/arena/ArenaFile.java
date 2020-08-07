package me.patothebest.gamecore.arena;

import me.patothebest.gamecore.file.FlatFile;

import java.io.File;

public class ArenaFile extends FlatFile {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final AbstractArena arena;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    public ArenaFile(AbstractArena arena) {
        super("arenas" + File.separatorChar + arena.getName());
        this.header = "Arena " + arena.getName() + " data";
        this.arena = arena;

        // load the file to memory
        load();
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void save() {
        // save the arena
        set("data", arena.serialize());

        // actually save the file
        super.save();
    }

    @Override
    public String toString() {
        return "ArenaFile{" +
                "arena=" + arena.getName() +
                '}';
    }
}
