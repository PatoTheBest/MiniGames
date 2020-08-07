package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.VaultHook;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class MoneyPlaceholder implements PlaceHolder {

    private final PluginHookManager pluginHookManager;
    private final Provider<Economy> economyProvider;

    @Inject private MoneyPlaceholder(PluginHookManager pluginHookManager, Provider<Economy> economyProvider) {
        this.pluginHookManager = pluginHookManager;
        this.economyProvider = economyProvider;
    }

    @Override
    public String getPlaceholderName() {
        return "money";
    }

    @Override
    public String replace(Player player, String args) {
        if(!pluginHookManager.isHookLoaded(VaultHook.class)) {
            return "NO VAULT";
        }

        if(economyProvider.get() == null) {
            return "NO ECONOMY";
        }

        return economyProvider.get().getBalance(player) + "";
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
