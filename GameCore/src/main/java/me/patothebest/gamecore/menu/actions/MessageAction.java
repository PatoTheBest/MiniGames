package me.patothebest.gamecore.menu.actions;

import me.patothebest.gamecore.menu.Action;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageAction implements Action {

    private String message;

    @Override
    public void load(Map<String, Object> map) {
        message = (String) map.get("message");
    }

    @Override
    public void execute(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
