package me.patothebest.gamecore.kit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.modules.ReloadPriority;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.storage.Storage;
import me.patothebest.gamecore.arena.AbstractGameTeam;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Priority;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
@ReloadPriority(priority = Priority.HIGHEST)
@ModuleName("Kit Manager")
public class KitManager implements ActivableModule, ReloadableModule {

    private final CorePlugin plugin;
    private final PlayerManager playerManager;
    private final Map<String, Kit> kits;
    private final Provider<Storage> storageProvider;
    @InjectLogger private Logger logger;

    // TODO: multiple default kits with permission
    @Inject
    @Named("DefaultKit")
    private Kit defaultKit;

    @Inject private KitManager(CorePlugin plugin, PlayerManager playerManager, Provider<Storage> storageProvider) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.storageProvider = storageProvider;
        this.kits = new HashMap<>();
    }

    @Override
    public void onPostEnable() {
        logger.config(ChatColor.YELLOW + "Loading kits...");

        storageProvider.get().loadKits(kits);
        logger.info("Loaded " + kits.size() + " kits!");
    }

    public void applyKit(Player player, AbstractGameTeam team) {
        IPlayer corePlayer = playerManager.getPlayer(player);
        if(corePlayer.getKit() != null) {
            corePlayer.getKit().applyKit(corePlayer);
        } else {
            defaultKit.applyKit(corePlayer);
        }

        // Don't paint armor for solo arenas
        if(corePlayer.getCurrentArena().getTeams().size() > 1) {
            if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET.parseMaterial()) {
                player.getInventory().setHelmet(new ItemStackBuilder(player.getInventory().getHelmet()).color(team.getColor().getColor()));
            }

            if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE.parseMaterial()) {
                player.getInventory().setChestplate(new ItemStackBuilder(player.getInventory().getChestplate()).color(team.getColor().getColor()));
            }

            if (player.getInventory().getLeggings() != null && player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS.parseMaterial()) {
                player.getInventory().setLeggings(new ItemStackBuilder(player.getInventory().getLeggings()).color(team.getColor().getColor()));
            }

            if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS.parseMaterial()) {
                player.getInventory().setBoots(new ItemStackBuilder(player.getInventory().getBoots()).color(team.getColor().getColor()));
            }
        }
    }

    public void applyPotionEffects(Player player) {
        if(playerManager.getPlayer(player) == null) {
            return;
        }

        defaultKit.applyPotionEffects(player);

        if(playerManager.getPlayer(player).getKit() != null) {
            playerManager.getPlayer(player).getKit().applyPotionEffects(player);
        }
    }

    @Override
    public void onReload() {
        kits.clear();
        onPostEnable();
    }

    @Override
    public String getReloadName() {
        return "kits";
    }

    public boolean kitExists(String name) {
        return kits.containsKey(name);
    }

    public Kit getDefaultKit() {
        return defaultKit;
    }

    public Map<String, Kit> getKits() {
        return kits;
    }

    public List<Kit> getEnabledKits() {
        return kits.values().stream().filter(Kit::isEnabled).collect(Collectors.toList());
    }

    public Kit createKit(String name, PlayerInventory items) {
        Kit kit = storageProvider.get().createKit(name, items);
        kits.put(name, kit);
        return kit;
    }
}