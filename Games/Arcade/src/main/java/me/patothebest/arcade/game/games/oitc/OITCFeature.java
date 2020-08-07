package me.patothebest.arcade.game.games.oitc;

import com.google.inject.Inject;
import me.patothebest.arcade.arena.Arena;
import me.patothebest.arcade.game.Game;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.combat.CombatManager;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class OITCFeature extends AbstractFeature {

    private final static ItemStack BOW = new ItemStackBuilder().material(Material.BOW).name(ChatColor.YELLOW + "OITC Bow").lore(ChatColor.WHITE + "One hit kill");
    private final static ItemStack ARROW = new ItemStackBuilder().material(Material.ARROW).name(ChatColor.YELLOW + "OITC Arrow");
    private final static ItemStack SWORD = new ItemStackBuilder().material(Material.IRON_SWORD);

    private final PlayerManager playerManager;
    private final CombatManager combatManager;
    private Game game;
    private int spawnIndex = 0;
    private int maxSpawns = 0;

    @Inject private OITCFeature(PlayerManager playerManager, CombatManager combatManager) {
        this.playerManager = playerManager;
        this.combatManager = combatManager;
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        this.game = ((Arena)arena).getCurrentGame();

        for (Player player : arena.getPlayers()) {
            player.getInventory().setItem(0, BOW);
            player.getInventory().setItem(1, SWORD);
            player.getInventory().setItem(8, ARROW);
        }

        maxSpawns = game.getSpawns().size();
        spawnIndex = arena.getPlayers().size();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Arrow damager = (Arrow) event.getDamager();

        if (!(damager.getShooter() instanceof Player)) {
            return;
        }

        Player damageSource = (Player) damager.getShooter();

        if (!isPlayingInArena(damaged) || !isPlayingInArena(damageSource)) {
            return;
        }

        giveArrow(damageSource);

        event.setCancelled(false);
        event.setDamage(Integer.MAX_VALUE);
    }

    @EventHandler
    public void onDeath(CombatDeathEvent event) {
        if (!isPlayingInArena(event)) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);
        event.getDrops().clear();
        playerManager.getPlayer(player).getPlayerInventory().clearPlayer();

        player.getInventory().setItem(0, BOW);
        player.getInventory().setItem(1, SWORD);
        player.getInventory().setItem(8, ARROW);
        player.teleport(game.getSpawns().get(spawnIndex++ % maxSpawns));

        if (event.getKillerPlayer() != null) {
            giveArrow(event.getKillerPlayer());
        }
    }

    private void giveArrow(Player player) {
        if (player.getInventory().contains(Material.ARROW.parseItem())) {
            player.getInventory().addItem(ARROW);
        } else {
            player.getInventory().setItem(8, ARROW);
        }
    }
}