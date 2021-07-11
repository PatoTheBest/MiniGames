package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.experience.ExperienceManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LevelProgressBarPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;
    private final ExperienceManager experienceManager;

    @Inject public LevelProgressBarPlaceholder(PlayerManager playerManager, ExperienceManager experienceManager) {
        this.playerManager = playerManager;
        this.experienceManager = experienceManager;
    }

    @Override
    public String getPlaceholderName() {
        return "level_progress_bar";
    }

    @Override
    public String replace(Player player, String args) {
        IPlayer iPlayer = playerManager.getPlayer(player);
        if (!iPlayer.isAllDataLoaded()) {
            return CoreLang.LOADING.getMessage(player);
        }

        double progress = experienceManager.getExperienceCalculator().calculateLevelProgress(iPlayer.getExperience());
        return Utils.makeProgress(progress, 10, '■', ChatColor.AQUA, ChatColor.WHITE);
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
