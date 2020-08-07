package me.patothebest.arcade.game.goal;

import me.patothebest.arcade.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.LinkedList;

public interface Goal extends Listener {

    LinkedList<Player> getPlacement();

    void setGame(Game game);

    void reset();
}
