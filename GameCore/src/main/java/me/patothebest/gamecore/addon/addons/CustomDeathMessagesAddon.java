package me.patothebest.gamecore.addon.addons;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.combat.CombatDeathEvent;
import me.patothebest.gamecore.combat.CombatEntry;
import me.patothebest.gamecore.combat.DamageOption;
import me.patothebest.gamecore.file.DeathMessagesFile;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.addon.Addon;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Singleton
public class CustomDeathMessagesAddon extends Addon {

    private final PlayerManager playerManager;
    private final DeathMessagesFile config;
    private boolean setColor = false;

    @Inject private CustomDeathMessagesAddon(PlayerManager playerManager, DeathMessagesFile config) {
        this.playerManager = playerManager;
        this.config = config;
    }

    @Override
    public void configure(ConfigurationSection addonConfigSection) {
        config.load();
        setColor = config.getBoolean("set-team-color");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(CombatDeathEvent event) {
        CombatEntry reason = event.getLastDamage();
        String messagePath = event.getDeathCause().name().toLowerCase();
        Entity killer = event.getKiller();
        if (killer != null) {
            if (reason.hasOption(DamageOption.WHILE_ESCAPING_OPTIONAL) ||
                reason.hasOption(DamageOption.ENTITY_NAME_OPTIONAL)) {
                messagePath += "-player";
            }

            if (event.getItemKilledWith() != null && reason.hasOption(DamageOption.ITEM_OPTIONAL)) {
                messagePath += "-item";
            }
        }

        String deathMessage = config.getString(messagePath);
        if(deathMessage == null) {
            Utils.printError("No death message displayed for " + messagePath);
            return;
        }

        ChatColor playerColor = null;
        boolean setColorInternal = setColor;
        if (setColor) {
            IPlayer player = playerManager.getPlayer(event.getPlayer());
            AbstractGameTeam gameTeam = player.getGameTeam();
            // set only color to team arenas
            if (gameTeam != null && player.getCurrentArena() != null && player.getCurrentArena().getTeams().size() > 1) {
                playerColor = Utils.getColorFromDye(gameTeam.getColor());
                setColorInternal = true;
            } else {
                setColorInternal = false;
            }
        }

        deathMessage = deathMessage
                .replace("%player%", (playerColor != null ? playerColor : "") + event.getPlayer().getName())
                .replace("%player_display_name%", (playerColor != null ? playerColor : "") + event.getPlayer().getDisplayName())
                .replace("%world", event.getPlayer().getWorld().getName());

        deathMessage = ChatColor.translateAlternateColorCodes('&', deathMessage);

        if (killer != null) {
            ItemStack itemInHand = event.getItemKilledWith();
            String materialName = "";
            String itemDisplayName = "";

            if (itemInHand != null && itemInHand.getType() != Material.AIR.parseMaterial()) {
                materialName = WordUtils.capitalizeFully(itemInHand.getType().toString().replace("_", " "));
                final ItemMeta im = itemInHand.getItemMeta();
                if (im != null && im.hasDisplayName()) {
                    itemDisplayName = im.getDisplayName();
                }
            }

            if (itemDisplayName.isEmpty()) {
                itemDisplayName = materialName;
            }

            ChatColor killerTeamColor = null;
            if (setColorInternal && event.getKillerPlayer() != null) {
                AbstractGameTeam gameTeam = playerManager.getPlayer(event.getKillerPlayer()).getGameTeam();
                if (gameTeam != null) {
                    killerTeamColor = Utils.getColorFromDye(gameTeam.getColor());
                }
            }
            deathMessage = deathMessage
                    .replace("%killer_display_name%", (killerTeamColor != null ? killerTeamColor : "") + (killer.getCustomName() == null ? killer.getName() : killer.getCustomName()))
                    .replace("%killer%", (killerTeamColor != null ? killerTeamColor : "") + killer.getName())
                    .replace("%mob_type%", (killerTeamColor != null ? killerTeamColor : "") + WordUtils.capitalizeFully(killer.getType().toString()))
                    .replace("%weapon_material%", materialName)
                    .replace("%weapon%", itemDisplayName);
        }

        event.setDeathMessage(deathMessage);
    }

    @Override
    public String getConfigPath() {
        return "death-messages";
    }
}
