package me.patothebest.gamecore.cosmetics.victoryeffects;

import me.patothebest.gamecore.player.IPlayer;
import org.bukkit.event.Listener;

public interface IVictoryEffect extends Listener {

    void display(IPlayer player);
}
