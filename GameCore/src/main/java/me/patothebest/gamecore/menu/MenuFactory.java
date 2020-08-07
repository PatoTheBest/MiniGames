package me.patothebest.gamecore.menu;

import com.google.inject.assistedinject.Assisted;

public interface MenuFactory {

    Menu createMenu(@Assisted String menuName);

}
