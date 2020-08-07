package me.patothebest.arcade.game.components;

import me.patothebest.arcade.game.Game;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.command.CommandSender;

import java.util.Map;

public interface GameComponent extends SerializableObject {

    void parse(Game game, Map<String, Object> data);

    boolean canBeEnabled(CommandSender commandSender);
}
