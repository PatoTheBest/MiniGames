package me.patothebest.gamecore.encouragement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.event.arena.CountdownStartEvent;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.modules.ModuleName;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.event.player.PlayerStateChangeEvent;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.modules.ActivableModule;
import me.patothebest.gamecore.modules.ListenerModule;
import me.patothebest.gamecore.modules.ReloadableModule;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.event.EventHandler;

@Singleton
@ModuleName("Encouragement Manager")
public class EncouragementManager implements ActivableModule, ReloadableModule, ListenerModule {

    private final CoreConfig coreConfig;
    private final PlayerManager playerManager;
    private boolean death;
    private boolean starting;

    @Inject
    private EncouragementManager(CoreConfig coreConfig, PlayerManager playerManager) {
        this.coreConfig = coreConfig;
        this.playerManager = playerManager;
    }

    @Override
    public void onEnable() {
        death = coreConfig.getBoolean("encouragement.death");
        starting = coreConfig.getBoolean("encouragement.starting");
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if (!death) {
            return;
        }

        if (event.getPlayerState() != PlayerStateChangeEvent.PlayerState.SPECTATOR) {
            return;
        }

        BaseComponent[] deathMessage = TextComponent.fromLegacyText(CoreLang.DEATH_MESSAGE.getMessage(event.getPlayer()));
        BaseComponent[] clickMessage = TextComponent.fromLegacyText(CoreLang.DEATH_MESSAGE_CLICK.getMessage(event.getPlayer()));
        BaseComponent[] hoverMessage = TextComponent.fromLegacyText(CoreLang.DEATH_MESSAGE_HOVER.getMessage(event.getPlayer()));
        for (BaseComponent baseComponent : clickMessage) {
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PluginConfig.BASE_COMMAND + " join -r"));
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
        }

        event.getPlayer().spigot().sendMessage(Utils.concatenateArray(deathMessage, clickMessage));
    }

    @EventHandler
    public void onStart(CountdownStartEvent event) {
        if (!starting) {
            return;
        }

        String arenaName = event.getArena().getName();
        String arenaWorldName = event.getArena().getWorldName();
        for (IPlayer player : playerManager.getPlayers()) {
            if (!player.isFullyJoined()) {
                continue;
            }

            if (player.isInArena()) {
                continue;
            }

            if (event.getArena().isPrivateArena()) {
                continue;
            }

            BaseComponent[] deathMessage = TextComponent.fromLegacyText(CoreLang.STARTING_MESSAGE.replace(player, arenaName));
            BaseComponent[] clickMessage = TextComponent.fromLegacyText(CoreLang.STARTING_MESSAGE_CLICK.getMessage(player));
            BaseComponent[] hoverMessage = TextComponent.fromLegacyText(CoreLang.STARTING_MESSAGE_HOVER.replace(player, arenaName));
            for (BaseComponent baseComponent : clickMessage) {
                baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + PluginConfig.BASE_COMMAND + " join " + arenaWorldName));
                baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
            }

            player.getPlayer().spigot().sendMessage(Utils.concatenateArray(deathMessage, clickMessage));
        }
    }

    @Override
    public void onReload() {
        onEnable();
    }

    @Override
    public String getReloadName() {
        return "encouragement";
    }
}
