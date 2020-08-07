package me.patothebest.gamecore.treasure.reward.rewards;

import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.treasure.reward.Reward;
import me.patothebest.gamecore.treasure.reward.RewardData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class CommandReward extends Reward {

    private final List<String> commands = new ArrayList<>();

    public CommandReward() {
        displayItem = new ItemStackBuilder(Material.COMMAND_BLOCK);
    }

    @Override
    public boolean parse(ConfigurationSection configurationSection) {
        boolean parsedSuper = super.parse(configurationSection);
        boolean parsed = false;

        if (configurationSection.isSet("command")) {
            commands.add(configurationSection.getString("command"));
            parsed = true;
        }

        if (configurationSection.isSet("commands")) {
            commands.addAll(configurationSection.getStringList("commands"));
            parsed = true;
        }

        if (!parsed) {
            System.err.println("Reward " + configurationSection.getCurrentPath() + " is missing either a command or a command list!");
        }

        return parsed && parsedSuper;
    }

    @Override
    public RewardData give(IPlayer player) {
        for (String command : commands) {
            String parsed = command.replace("%player%", player.getName());

            if (command.startsWith("{msg}")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', parsed));
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            }
        }

        return new RewardData(hologramMessage, displayItem);
    }
}