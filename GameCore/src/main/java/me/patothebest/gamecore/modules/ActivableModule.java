package me.patothebest.gamecore.modules;

import me.patothebest.gamecore.injector.Activatable;

public interface ActivableModule extends Module, Activatable {

    default void onPreEnable() {}

    default void onPostEnable() {}

}
