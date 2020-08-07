package me.patothebest.gamecore.feature.features.gameoptions.weather;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.entity.Player;

public class WeatherVoteUI extends GUIPage {

    private final WeatherSelectorGameOption weatherSelectorGameOption;

    @Inject private WeatherVoteUI(CorePlugin plugin, @Assisted Player player, @Assisted WeatherSelectorGameOption weatherSelectorGameOption) {
        super(plugin, player, CoreLang.GUI_WEATHER_VOTE_TITLE.getMessage(player), 9);
        this.weatherSelectorGameOption = weatherSelectorGameOption;
        build();
    }

    @Override
    protected void buildPage() {
        WeatherType selectedWeather = weatherSelectorGameOption.getWeatherVoting().get(getPlayer());

        for (WeatherType weatherType : WeatherType.values()) {
            addButton(new SimpleButton(new ItemStackBuilder(weatherType.getItem())
                    .name(weatherType.getLangMessage().getMessage(getPlayer()))
                    .lore(selectedWeather == weatherType ? CoreLang.GUI_SHOP_SELECTED.getMessage(getPlayer()) : CoreLang.GUI_SHOP_CLICK_TO_SELECT.getMessage(getPlayer()))
                    .glowing(selectedWeather == weatherType))
                    .action(() -> {
                        weatherSelectorGameOption.getWeatherVoting().remove(player);

                        CoreLang.GUI_WEATHER_VOTE_VOTED.replaceAndSend(player, weatherType.getLangMessage());
                        weatherSelectorGameOption.getWeatherVoting().put(player, weatherType);
                        refresh();
                    }), weatherType.getSlot());
        }
    }
}
