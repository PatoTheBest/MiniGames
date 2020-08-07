package me.patothebest.gamecore.arena;

import com.google.inject.assistedinject.Assisted;

public interface ArenaFactory {

    AbstractArena create(@Assisted("name") String name, @Assisted("worldName") String worldName);

}
