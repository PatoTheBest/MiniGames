package me.patothebest.gamecore.placeholder.placeholders.player;

import com.google.inject.Inject;
import me.patothebest.gamecore.arena.AbstractArena;
import me.patothebest.gamecore.experience.ExperienceManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.placeholder.PlaceHolder;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;

public class LevelColorPlaceholder implements PlaceHolder {

    private final PlayerManager playerManager;
    private final ExperienceManager experienceManager;

    @Inject public LevelColorPlaceholder(PlayerManager playerManager, ExperienceManager experienceManager) {
        this.playerManager = playerManager;
        this.experienceManager = experienceManager;
    }

    @Override
    public String getPlaceholderName() {
        return "level_color";
    }

    @Override
    public String replace(Player player, String args) {
        IPlayer iPlayer = playerManager.getPlayer(player);
        if (!iPlayer.isAllDataLoaded()) {
            return CoreLang.LOADING.getMessage(player);
        }

        int level = experienceManager.getExperienceCalculator().expToLevelFloor(iPlayer.getExperience());
        return experienceManager.getColor(level);
    }

    @Override
    public String replace(AbstractArena arena) {
        throw new IllegalArgumentException("Not supported");
    }
}
