package me.patothebest.gamecore.feature.features.gameoptions.weather;

import com.google.inject.Inject;
import me.patothebest.gamecore.event.player.ArenaLeaveEvent;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.permission.Permission;
import me.patothebest.gamecore.event.arena.ArenaPrePhaseChangeEvent;
import me.patothebest.gamecore.feature.AbstractFeature;
import me.patothebest.gamecore.feature.features.gameoptions.GameOption;
import me.patothebest.gamecore.feature.features.gameoptions.GameOptionsGUIFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class WeatherSelectorGameOption extends AbstractFeature implements GameOption {

    private final Map<Player, WeatherType> weatherVoting = new HashMap<>();
    private final GameOptionsGUIFactory gameOptionsGUIFactory;

    @Inject private WeatherSelectorGameOption(GameOptionsGUIFactory gameOptionsGUIFactory) {
        this.gameOptionsGUIFactory = gameOptionsGUIFactory;
    }

    @Override
    public void initializeFeature() {
        super.initializeFeature();
        weatherVoting.clear();
    }

    @EventHandler
    public void onArenaLeave(ArenaLeaveEvent event) {
        weatherVoting.remove(event.getPlayer());
    }

    @EventHandler
    public void onArenaPhasePreChangeEvent(ArenaPrePhaseChangeEvent event) {
        if(event.getNewPhase() != arena.getNextPhase()) {
            return;
        }

        if(event.getArena() != arena) {
            return;
        }

        Map.Entry<WeatherType, Integer> maxEntry = null;

        if(!weatherVoting.isEmpty()) {
            Map<WeatherType, Integer> voteMapResults = new HashMap<>();

            for (Map.Entry<Player, WeatherType> playerWeatherTypeEntry : weatherVoting.entrySet()) {
                voteMapResults.putIfAbsent(playerWeatherTypeEntry.getValue(), 0);
                Integer vote = voteMapResults.remove(playerWeatherTypeEntry.getValue());
                vote++;
                voteMapResults.put(playerWeatherTypeEntry.getValue(), vote);
            }

            for (Map.Entry<WeatherType, Integer> entry : voteMapResults.entrySet()) {
                if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
        }

        arena.getWorld().setStorm(maxEntry != null && maxEntry.getKey().isStorm());
        arena.getWorld().setWeatherDuration(Integer.MAX_VALUE);
    }

    @Override
    public SimpleButton getButton(Player player) {
        return new SimpleButton(new ItemStackBuilder().material(Material.LILY_PAD).name(player, CoreLang.GUI_WEATHER_ITEM)).action(() -> {
            if(!player.hasPermission(Permission.CHOOSE_WEATHER.getBukkitPermission())) {
                CoreLang.GUI_WEATHER_NO_PERMISSION.sendMessage(player);
                return;
            }

            gameOptionsGUIFactory.openWeatherUI(player, this);
        });
    }

    public Map<Player, WeatherType> getWeatherVoting() {
        return weatherVoting;
    }
}
