package me.patothebest.skywars.placeholder.placeholders.all;

import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.skywars.arena.Arena;
import me.patothebest.skywars.lang.Lang;
import me.patothebest.skywars.phase.SkyWarsPhase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class NextEventPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;

    @Inject public NextEventPlaceholder(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public String getPlaceholderName() {
        return "next_event";
    }

    @Override
    public String replace(Player player, String args) {
        return getTranslatedMessage(player, (Arena) playerManager.getPlayer(player).getCurrentArena());
    }

    @Override
    public String replace(AbstractArena arena) {
        return getTranslatedMessage(null, (Arena) arena);
    }

    private String getTranslatedMessage(@Nullable CommandSender sender, Arena arena) {
        Lang message;
        if (arena == null || arena.getPhase().getNextPhase() == null || !(arena.getPhase().getNextPhase() instanceof SkyWarsPhase)) {
            message = Lang.PHASE_NONE;
        } else {
            message = ((SkyWarsPhase) arena.getPhase().getNextPhase()).getPhaseType().getMessage();
        }

        return message.getMessage(sender);
    }
}
