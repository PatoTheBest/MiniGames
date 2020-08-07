package me.patothebest.gamecore.menu;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Action {

    void load(Map<String, Object> map);

    void execute(Player player);

}
