package me.patothebest.gamecore.menu;

import me.patothebest.gamecore.file.FlatFile;

import java.io.File;

public class MenuFile extends FlatFile {

    protected MenuFile(String menuName) {
        super(new File(MenuManager.MENUS_DIRECTORY, menuName + ".yml"));

        load();
    }
}
