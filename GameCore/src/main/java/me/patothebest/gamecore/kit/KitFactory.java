package me.patothebest.gamecore.kit;

import org.bukkit.inventory.PlayerInventory;

import java.sql.ResultSet;

public interface KitFactory {

    Kit createKit(ResultSet resultSet);

    Kit createKit(String kitName, PlayerInventory playerInventory);
}
