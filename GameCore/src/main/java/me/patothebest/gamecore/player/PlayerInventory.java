package me.patothebest.gamecore.player;

import me.patothebest.gamecore.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class PlayerInventory {

    private final Player player;
    private ItemStack[] armor;
    private ItemStack[] inventory;
    private GameMode gameMode;
    private float xp;
    private int xpLevel;
    private int foodLevel;
    private double maxHealth;
    private double health;
    private boolean flight;
    private Collection<PotionEffect> potionEffects;

    PlayerInventory(Player player) {
        this.player = player;
    }

    public void savePlayer() {
        armor = player.getInventory().getArmorContents();
        inventory = player.getInventory().getContents();
        gameMode = player.getGameMode();
        xp = player.getExp();
        xpLevel = player.getLevel();
        foodLevel = player.getFoodLevel();
        maxHealth = player.getMaxHealth();
        health = player.getHealth();
        flight = player.getAllowFlight();
        potionEffects = player.getActivePotionEffects();

        clearPlayer();
    }

    public void restoreInventory() {
        clearPlayer();

        // TODO: Config option?
       /* player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(inventory);
        player.setGameMode(gameMode);
        player.setExp(xp);
        player.setLevel(xpLevel);
        player.setFoodLevel(foodLevel);
        player.setMaxHealth(maxHealth);
        player.setHealth(health);
        player.setAllowFlight(flight);
        player.addPotionEffects(potionEffects);*/
    }

    public void clearPlayer() {
        Utils.clearPlayer(player);
    }
}
