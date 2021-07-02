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

public class LevelProgressLabelPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;
    private final ExperienceManager experienceManager;

    @Inject public LevelProgressLabelPlaceholder(PlayerManager playerManager, ExperienceManager experienceManager) {
        this.playerManager = playerManager;
        this.experienceManager = experienceManager;
    }

    @Override
    public String getPlaceholderName() {
        return "level_progress_label";
    }

    @Override
    public String replace(Player player, String args) {
        IPlayer iPlayer = playerManager.getPlayer(player);
        if (!iPlayer.isAllDataLoaded()) {
            return CoreLang.LOADING.getMessage(player);
        }

        int level = experienceManager.getExperienceCalculator().expToLevelFloor(iPlayer.getExperience());
        int nextLevel = level + 1;
        long nextLevelExp = experienceManager.getExperienceCalculator().levelToExp(nextLevel);
        long currLevelExp = experienceManager.getExperienceCalculator().levelToExp(level);
        nextLevelExp -= currLevelExp;
        long exp = iPlayer.getExperience() - currLevelExp;

        String currExp = Utils.formatLong(exp);
        String nextExp = Utils.formatLong(nextLevelExp);

        return ChatColor.AQUA + currExp + ChatColor.GRAY + "/" + ChatColor.GREEN + nextExp;
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
