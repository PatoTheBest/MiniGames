package me.patothebest.gamecore.menu.actions;

import me.patothebest.gamecore.menu.Action;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandAction implements Action {

    private String command;

    @Override
    public void load(Map<String, Object> map) {
        command = (String) map.get("command");
    }

    @Override
    public void execute(Player player) {
        player.chat(command);
    }
}
