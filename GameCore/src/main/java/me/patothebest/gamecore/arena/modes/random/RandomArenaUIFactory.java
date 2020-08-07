package me.patothebest.gamecore.arena.modes.random;

import org.bukkit.entity.Player;

public interface RandomArenaUIFactory {

    ChooseGroup createChooseGroupUI(Player player);

    ChooseMap createChooseMapUI(Player player, RandomArenaGroup randomArenaGroup);

}
