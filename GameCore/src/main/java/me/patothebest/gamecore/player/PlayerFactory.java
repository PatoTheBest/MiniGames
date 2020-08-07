package me.patothebest.gamecore.player;

import me.patothebest.gamecore.lang.Locale;

public interface PlayerFactory {

    CorePlayer create(Locale defaultLocale);

}
